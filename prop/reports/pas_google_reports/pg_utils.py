"""
Common utilities for working with Google APIs and PAS Review Reports
"""

import os
import re
import pickle
import logging
import collections
import csv
import configparser
import httplib2
from argparse import ArgumentParser
from pathlib import Path
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2 import service_account, credentials
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError, UnknownApiNameOrVersion
from google_auth_httplib2 import AuthorizedHttp

# Setup logger in arg_parse so log file contains title
logger = ""

# globals for getting info out of callback
gresponse = []
gexception = []

try:
    # PAS .htproperties has reportDataPath, which already ends with webreports
    # dir, ASCDS_PROP_DIR set from DS doesn't
    CONFIG_FILE = Path(os.environ["ASCDS_PROP_DIR"])
    HTTP_ENV = os.environ.get("HTTP_PROXY")
    HTTPS_ENV = os.environ.get("HTTPS_PROXY")
    if CONFIG_FILE.stem != "webreports":
        CONFIG_FILE = CONFIG_FILE / "webreports"
    CONFIG_FILE = CONFIG_FILE / "bin" / "pas_google_reports" / "pgr_config.ini"
    CONFIG = configparser.ConfigParser(
        interpolation=configparser.ExtendedInterpolation()
    )
    if CONFIG.read(str(CONFIG_FILE)):
        PAS_DIR = Path(CONFIG.get("paths", "pas_dir"))
        USER_GMAILS = Path(CONFIG.get("paths", "user_gmails"))
        CDO_GMAILS = Path(CONFIG.get("paths", "cdo_gmails"))
        REV_EMAILS = Path(CONFIG.get("paths", "rev_emails"))
        CHAIR_EMAILS = Path(CONFIG.get("paths", "chair_emails"))
        REPORT_FOLDER_ID = CONFIG.get("paths", "report_folder_id")
        DRIVE_ID = CONFIG.get("paths", "drive_id")
        DRIVE_VERSION = CONFIG.get("versions", "drive_version")
        DOCS_VERSION = CONFIG.get("versions", "docs_version")
        DRIVE_AUTH_TOKEN = Path(CONFIG.get("authentication", "drive_auth_token"))
        DRIVE_AUTH_CREDS = Path(CONFIG.get("authentication", "drive_auth_creds"))
        SCOPES = [CONFIG.get("authentication", "scopes")]
        TEMPLATE_TITLE = CONFIG.get("template", "template_title")
        TEMPLATE_TEXT = CONFIG.get("template", "template_text")
        TEMPLATE_PROPOSAL_NUM = CONFIG.get("template", "template_proposal_num")
        TEMPLATE_PANEL_NUM = CONFIG.get("template", "template_panel_num")
        TEMPLATE_PROP_TITLE = CONFIG.get("template", "template_proposal_title")
        PROXY_HOST = CONFIG.get("proxy", "proxy_host")
        PROXY_PORT = int(CONFIG.get("proxy", "proxy_port"))
except configparser.Error:
    CONFIG = "Config file error {}".format(os.environ["ASCDS_PROP_DIR"])
except KeyError:
    CONFIG = "Config file missing key or ASCDS_PROP_DIR env var not set: {}".format(
        os.environ["ASCDS_PROP_DIR"]
    )

# For parsing report text. Note the space before new line
COMMENTS_START = "Comments = \n"
COMMENTS_END = "End of Comments\n"
RECS_START = "If accepted, enter specific recommendations concerning targets, time, observing conditions = \n"
RECS_END = "End of Recommendations\n"
PROPOSAL_TITLE_START = "Proposal Title      = "
PROPOSAL_TITLE_END = "Type                = "


def parse_args() -> tuple:
    """
    Parse input arguments
    Role & emails needed for create & permissions
    For updating text, just need title
    Returns:
        tuple of args
    """

    parser = ArgumentParser()
    parser.add_argument(
        "-a",
        "--action",
        help="Select action to perform",
        required=True,
        choices=["create", "fileid", "update", "perms"],
    )
    parser.add_argument(
        "-t",
        "--title",
        help="Google Doc title of the form"
        " panelNum_proposalNum. To create all peer "
        "proposals enter 'peer'. To generate all bpp "
        "proposals, enter 'bpp'",
        type=str,
        required=True,
    )
    parser.add_argument(
        "-r",
        "--roles",
        help="New role for user access to Doc. Single value"
        " will apply to all email addresses, multiple"
        " values will apply to the individual corresponding"
        " emails.",
        choices=["commenter", "reader", "writer"],
        nargs="+",
        type=str,
        required=False,
    )
    parser.add_argument(
        "-e",
        "--emails",
        help="List of emails addresses to create permissions for",
        nargs="+",
        type=str,
        required=False,
    )
    parser.add_argument(
        "-g",
        "--gmail",
        help="Assume input emails are gmail addresses and don't" " parse gmail_list.",
        required=False,
        action="store_true",
    )
    parser.add_argument(
        "-d",
        "--dev",
        help="Dev flag is used to get auth token first time"
        " running the script. Only CXCDS dev should run"
        " this!",
        required=False,
        action="store_true",
    )

    args = parser.parse_args()
    title = args.title
    roles = args.roles
    emails = args.emails
    action = args.action
    gmail = args.gmail
    dev = args.dev

    # title for bpp props sometimes comes as LP_propNum but really want bpp_propNum.LP
    if title.startswith("LP"):
        title = title.replace("LP", "bpp") + ".LP"
    set_logger(title)

    enmasse = ("peer", "bpp")

    if title in enmasse and action not in ("create", "update"):
        raise AssertionError(
            "title: {} only applies to create and update" " actions".format(title)
        )

    # create & perms require emails and roles (except en masse creation)
    # update & file_id just need title
    if (
        action in ["create", "perms"]
        and not (emails and roles)
        and title not in enmasse
    ):
        raise AssertionError(
            "Creating Docs or updating permissions requires"
            " both user email addresses and permission roles."
        )

    if emails:
        if not gmail:
            emails = get_gmails(emails)

        if len(roles) > 1 and len(roles) != len(emails):
            raise AssertionError(
                "If input number of roles > 1, needs to match"
                " number of input email addresses."
            )

        if len(roles) == 1:
            roles = roles * len(emails)

    return (title, roles, emails, action, dev)


def set_logger(prepend: str) -> None:
    """
    Create log directory if it doesn't exist and instantiate logger with
    different filename for each prepend (ie. panel/proposal)
    Args:
        prepend: Identifying text to prepend to log file

    Returns: None
    """

    # better way to have the logger accessible by all files AND set title?
    global logger
    try:
        log_dir = PAS_DIR / "bin" / "pas_google_logs"
        log_dir.mkdir(parents=True, exist_ok=True)
        log_file = log_dir / "{}_pgr.log".format(prepend)
        logger = logging.getLogger(__name__)
        # TODO I think setting name here means it will always be pg_utils, can
        #  name be reset in each file so we know where the info is coming from
        logging.basicConfig(
            format="%(levelname)s: %(name)s: %(asctime)s: %(message)s",
            datefmt="%d-%b-%y %H:%M:%S",
            filename=str(log_file),
            level=logging.DEBUG,
        )
        # Suppress output from google api logger. Not sure this propagates...
        logging.getLogger("googleapiclient.discovery").setLevel(logging.ERROR)
    except Exception as err:
        log_file = PAS_DIR / "logger_error.log"
        err_msg = "Initializing Logger failed:" " {0}: {1}\n".format(prepend, str(err))
        print(err_msg)
        with open(str(log_file), "w") as logf:
            logf.write(err_msg)


def sort_dict_on_list(dct: dict, lst: list) -> dict:
    """
    Sort dictionary keys on list.
    If list element not in dict, will just be skipped
    Args:
        dct: Input dict
        lst: Input list

    Returns:
        dct sorted on lst
    """

    return {key: dct[key] for key in lst if key in dct}


def oauth_creds(scopes: list, auth: Path, dev: bool = False) -> credentials.Credentials:
    """
    Authenticate using oath2.0 credentials.
    The file, token.pickle, stores the user's access and refresh tokens, and is
    created automatically when the authorization flow completes for the first
    time (via consent page pop-up). If there are no (valid) credentials
     available, let the user (ie. the person/account running this code) login.
    This will hang in PAS since it tries to bring up a browser on the machine
    running the script, so login auth only permitted with dev flag.
if

    Args:
        scopes: List Google Drive/Doc API authorization scopes
        auth: Path to authentication auth token file
        dev: Only dev should have option for login authentication
            to create token

    Returns:
        Credentials object
    """

    creds = None
    if auth.is_file():
        with open(str(auth), "rb") as token:
            creds = pickle.load(token)

    if dev:
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            # TODO refresh tokens are invalidated after 6 months of no use.
            # TODO need to add a try/except here here to go into this else if the above
            else:
                flow = InstalledAppFlow.from_client_secrets_file(
                    str(DRIVE_AUTH_CREDS), scopes
                )
                creds = flow.run_local_server(port=0)
            # Save the credentials for the next run
            with open(str(auth), "wb") as token:
                pickle.dump(creds, token)

    return creds


def service_account_creds(sa_file: Path, scopes: list) -> service_account.Credentials:
    """
    Authenticate using Service Account credentials.
    NOT CURRENTLY USING THIS.
    Args:
        sa_file: Service Account auth file
        scopes: Google Docs/Drive API scopes
    Returns:
        Service Account Credentials object
    """

    creds = None
    if sa_file.is_file():
        creds = service_account.Credentials.from_service_account_file(
            str(sa_file), scopes=scopes
        )
    return creds


def callback(request_id: str, response: dict, exception: HttpError) -> None:
    """
    Function that gets called by new_batch_http_request for each response.
    Without callback, there's no indication of whether new_batch_http_request
    was successful or not.
    See:
        https://googleapis.github.io/google-api-python-client/docs/batch.html
        https://developers.google.com/drive/api/v3/handle-errors#exponential-backoff
    Args:
        request_id: ID of request made
        response: dict of returned response from request
        exception: HttpError error object if there was and error in request
    Return: None
    """

    # Since callback is a function that gets passed into new_batch_http_request,
    # It can't handle returns, so it's not straightforward to retrieve info
    # from the response or exception (aside from logging and printing). It feels
    # a bit icky to use a global to get the info out, but not sure there's a
    # better way :\

    global gresponse, gexception

    if exception:
        # Handle error
        # Transient errors are related to server issues/ too many requests.
        #   googleapiclient.discovery.build will retry  4 times, no need to
        #   retry here.
        #   429 error: Too many requests
        #   403 error: Rate limit exceeded OR User rate limit exceeded
        #   500, 502, 503, 504: Backend server issues
        # 404 if file_id or permission doesn't exist
        try_again = ["limit exceeded", "429", "500", "502", "503", "504"]
        gexception.append(exception)
        if "404" in str(exception):
            logger.error(
                "404 error in request. See pgr logs for details: {}".format(exception)
            )
        elif any(err in str(exception) for err in try_again):
            logger.error(
                "Error in request. See pgr logs for details: {}".format(exception)
            )
    else:
        logger.info(
            "No errors returned during API request! Response is:" " {}".format(response)
        )
        gresponse.append(response)


def get_service(scopes: list, api: str, dev: bool = False) -> build:
    """
    Get the Drive/Doc build service object for making requests
    Args:
        scopes: List Google Drive/Doc API authorization scopes
        api: drive or docs
        dev: Only dev should have option for login authentication
            to create token

    Returns:
            build object
    """
    # logger.debug("env: {}".format(os.environ))
    auth_token = DRIVE_AUTH_TOKEN
    # Only using drive or docs api
    if api == "drive":
        api_version = DRIVE_VERSION
    elif api == "docs":
        api_version = DOCS_VERSION
    else:
        raise UnknownApiNameOrVersion

    creds = oauth_creds(scopes, auth_token, dev)

    proxy = httplib2.ProxyInfo(
        proxy_type=httplib2.socks.PROXY_TYPE_HTTP,
        proxy_host=PROXY_HOST,
        proxy_port=PROXY_PORT,
    )
    http = httplib2.Http(proxy_info=proxy)
    logger.debug("PAS_DIR {}".format(PAS_DIR))
    if str(PAS_DIR).startswith("/proj/cxcds") or dev:
        authorized_http = None
    else:
        authorized_http = AuthorizedHttp(creds, http=http)
        creds = None
    logger.debug("http: {}, creds: {}".format(http, creds))
    logger.debug("host: {}, port: {}".format(PROXY_HOST, PROXY_PORT))
    # cache_discovery = False removes logging warnings old warning was:
    # WARNING: googleapiclient.discovery_cache: file_cache is unavailable
    # when using oauth2client >= 4.0.0 or google-auth
    # so it looks like we can't cache the discovery doc anyway
    return build(
        api,
        api_version,
        credentials=creds,
        http=authorized_http,
        cache_discovery=False,
        num_retries=2,
    )


def get_file_ids(title: str, drive_service: build, query_operator: str = "=") -> list:
    """
    Call Drive API to get file_id for Document title.
    If no matching Doc is found, file_id is empty
    Args:
        title: Title of Document to query for
        drive_service:
        query_operator: operator to use when querying file titles.
         '=' will match exact title of doc, 'contains' will match from
             beginning of word.
         See https://developers.google.com/drive/api/v3/ref-search-terms

    Returns: list of objects (including file_ids)
    """

    query_string = "name {} '{}' and mimeType = 'application/vnd.google-apps.document'".format(
        query_operator, title
    )

    response = (
        drive_service.files()
        .list(
            corpora="drive",
            q=query_string,
            spaces="drive",
            driveId=DRIVE_ID,
            fields="nextPageToken, files(id, name)",
            includeItemsFromAllDrives=True,
            supportsAllDrives=True,
        )
        .execute()
    )
    file_list = response.get("files", [])

    return file_list


def get_report_path(title: str) -> Path:
    """
     Obtain the appropriate file PAS report file since the files
      have different suffix before/after Completion by reviewer/CDO.
    Args:
        title: Document title

    Returns: Path to report file
    """

    base = PAS_DIR / title
    # base.suffix will keep suffix LP for bpp
    suffix = base.suffix if base.suffix == ".LP" else ".peer"
    report_path = base.with_suffix(suffix)
    finalize_path = base.with_suffix(suffix + ".CDO")
    completed_path = base.with_suffix(suffix + ".Reviewer")
    checkedoff_path = base.with_suffix(suffix + ".Panel")

    # After CDO completion
    if finalize_path.is_file():
        report_path = finalize_path
    # After chair completion
    elif checkedoff_path.is_file():
        report_path = checkedoff_path
    # After primary completion
    elif completed_path.is_file():
        report_path = completed_path

    logger.info("\nReport file is: {}".format(report_path))

    return report_path


def doc_exists(title: str, drive_service: build) -> list:
    """
    Titles in drive are not unique (file_id is) so need to ensure file with
    given title doesn't exist before creating new one. doc_exists checks if
    there's at least one file with title.

    Args:
        title: Doc title
        drive_service: API build request object

    Returns:
        file_list: should be empty if file doesn't exist

    """

    logger.info("Check if {} exists".format(title))
    file_list = get_file_ids(title, drive_service)

    return file_list


def get_prop_panel_email() -> list:
    """
    Get panel number, proposal number, and email addresses
    for all proposals on all panels from previously generated
    REV_EMAILS (prop_num\tpanel_num\tprim_email\tsec_email)
    and CHAIR_EMAILS (email\tpanel_num)files.

    Returns:
        List of {panNum_propNum:[emails]}
    """

    prop_panel_email = collections.defaultdict(dict)
    empty = (None, "", " ")
    # get reviewers (including pundits)
    try:
        with open(str(REV_EMAILS)) as ef:
            rd = csv.reader(ef, delimiter="\t", quotechar='"')
            for row in rd:
                # only add if prim or sec email exists
                prim_email = row[2].strip()
                sec_email = row[3].strip()
                has_prim = prim_email if prim_email not in empty else None
                has_sec = sec_email if sec_email not in empty else None
                if has_prim or has_sec:
                    if row[1].strip() == "LP":
                        panel = "bpp"
                    else:
                        panel = row[1].strip()
                    proposal = row[0].strip()
                    title = panel + "_" + proposal
                    emails = [prim_email, sec_email]
                    # [title, prim_email, sec_email]. prune None
                    prop_panel_email[panel][title] = [
                        email for email in emails if email
                    ]

        # get chairs/deputies
        with open(str(CHAIR_EMAILS)) as ef:
            rd = csv.reader(ef, delimiter="\t", quotechar='"')
            for row in rd:
                if "LP" in row[1]:
                    panel = "bpp"
                else:
                    panel = row[1].strip()
                chair_email = row[0].strip()
                rev_pan = prop_panel_email.get(panel)
                new_pan = {
                    title: emails + [chair_email] for title, emails in rev_pan.items()
                }
                prop_panel_email[panel] = new_pan
    except FileNotFoundError:
        logger.exception("Can't find {} or {}".format(REV_EMAILS, CHAIR_EMAILS))

    return list(prop_panel_email.values())


def get_gmails(pas_emails: list) -> list:
    """
    Input list of panelist email addresses stored in person..person_short table,
     get corresponding gmail address from tsv file of gmail \t email.
    Args:
        pas_emails: Input list of reviewer email addresses

    Returns:
        gmails: List of corresponding gmail addresses
    """

    gmails = []
    with open(str(USER_GMAILS)) as ef:
        rd = csv.reader(ef, delimiter="\t", quotechar='"')
        # Skip header
        next(rd, None)
        for row in rd:
            if row[1] in pas_emails:
                gmails.append(row[0])
    return gmails


def replace_common_utf(text: str) -> str:
    """
    Taken from PAS updateReportServlet.java: getascii.
    Replaces UTF characters that are commonly entered into PAS with ascii
    equivalents.
    Args:
        text: In put text string to replace

    Returns:
        Ascii replaced string.
    """
    logger.debug("text before replace: {}".format(text))
    text = re.sub(u"\u0085", "...", text)
    text = re.sub(u"\u0091", "'", text)
    text = re.sub(u"\u0092", "'", text)
    text = re.sub(u"\u0093", '"', text)
    text = re.sub(u"\u0094", '"', text)
    text = re.sub(u"\u0095", "o ", text)
    text = re.sub(u"\u0096", "-", text)
    text = re.sub(u"\u0097", "-", text)
    text = re.sub(u"\u0098", "~", text)
    text = re.sub(u"\u00b0", "deg", text)
    text = re.sub(u"\u00b2", "^2", text)
    text = re.sub(u"\u00b3", "^3", text)
    text = re.sub(u"\u00b9", "^1", text)
    text = re.sub(u"\u00bc", "1/4", text)
    text = re.sub(u"\u00bd", "1/2", text)
    text = re.sub(u"\u00be", "3/4", text)
    text = re.sub(u"\u00b1", "+/-", text)
    text = re.sub(u"\u00f7", "/", text)
    text = re.sub(u"\u2018", "'", text)
    text = re.sub(u"\u2019", "'", text)
    text = re.sub(u"\u2070", "^0", text)
    text = re.sub(u"\u2071", "^1", text)
    text = re.sub(u"\u2072", "^2", text)
    text = re.sub(u"\u2073", "^3", text)
    text = re.sub(u"\u2074", "^4", text)
    text = re.sub(u"\u2075", "^5", text)
    text = re.sub(u"\u2076", "^6", text)
    text = re.sub(u"\u2077", "^7", text)
    text = re.sub(u"\u2078", "^8", text)
    text = re.sub(u"\u2079", "^9", text)
    text = re.sub("\\^(\\d)\\^(\\d)", "^$1$2", text)
    # This one not in PAS
    text = re.sub(u"[\u201c\u201d]", '"', text)
    logger.debug("text after replace: {}".format(text))
    return text
