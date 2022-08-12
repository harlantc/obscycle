"""
Create new Google Doc with permissions set for users and populate Doc with
initial text from template.
"""

from googleapiclient.discovery import build
import pg_utils as pgu
from googleapiclient.errors import HttpError

logger = pgu.logger


def create_doc(new_title: str, drive_service: build) -> (list, bool):
    """
    Use Drive API to copy doc from template. Will not copy comments from
    template. Will not try to create a new doc if one with new_title already
    exists. Another way avoid create two docs with the same title (file_id is
    unique, not title) might be to use file.generateId, pass those into
    file.create, and key off file_id rather than title, but then we'd have to
    store a hash of proposal: file_id, to disk.

    Args:
        new_title: Title of new Doc
        drive_service: build request object

    Returns: tuple of (file_id, existed)
    """

    template_list = pgu.get_file_ids(pgu.TEMPLATE_TITLE, drive_service)
    try:
        template_id = template_list[0].get("id")
        file_list = pgu.doc_exists(new_title, drive_service)
    except IndexError:
        logger.exception(
            "No template file id found for" " {} ".format(pgu.TEMPLATE_TITLE)
        )
        file_list = [{"id": ""}]
        existed = False
    else:
        if file_list:
            logger.info("{} exists already! File creation" " halted".format(new_title))
            existed = True
            logger.info("Existing file_id is {}: ".format(file_list))
        else:
            existed = False
            logger.info("{} does not exist yet. Let there be" " Doc!".format(new_title))
            # TODO batch here?
            # batch = drive_service.new_batch_http_request(pgu.callback)
            # for role, perm_id in zip(roles, perm_ids): # one file at a time
            doc_info = {"name": new_title, "parents": [pgu.REPORT_FOLDER_ID]}
            file_list = (
                drive_service.files()
                .copy(
                    fileId=template_id,
                    body=doc_info,
                    fields="id",
                    supportsAllDrives=True,
                )
                .execute()
            )
            logger.info("Created file_id is {}: ", file_list)
    # if doing batch, response would be list of dicts, else
    # dict with fields as keys.
    if not isinstance(file_list, list):
        file_list = [file_list]

    return file_list, existed


def create_perms(
    title: str, roles: list, emails: list, drive_service: build, file_list: list
) -> None:
    """
    Create permissions for a new user of a document.
    Will not update permissions only create them the first time around.
    If permission already exists for some reason, this will have no affect on
    existing permissions and will not fail.
    Args:
        title: Doc title
        roles: New Permission roles
        emails: gmail addresses to modify create for
        drive_service: build request object
        file_list: list of dicts of {id: file_id}

    Returns: None
    """

    # Should only return one, uniquely named document since there's one title
    try:
        file_id = file_list[0].get("id")
    except IndexError:
        logger.exception("No file id found for {} ".format(title))
    else:
        batch = drive_service.new_batch_http_request(callback=pgu.callback)

        # Add CDO emails with writer permission
        gmails = []
        try:
            with open(str(pgu.CDO_GMAILS)) as gfile:
                gmails = [line.rstrip() for line in gfile]
            logger.debug("CDO gmails {}".format(gmails))
            emails += gmails
        except FileNotFoundError:
            logger.exception("{} doesn't exist".format(pgu.CDO_GMAILS))
        if len(roles) > 1:
            groles = ["writer"] * len(gmails)
            roles += groles

        for role, email in zip(roles, emails):
            user_permission = {"type": "user", "role": role, "emailAddress": email}
            batch.add(
                drive_service.permissions().create(
                    fileId=file_id,
                    body=user_permission,
                    fields="id",
                    sendNotificationEmail=False,
                    supportsAllDrives=True,
                )
            )
        batch.execute()


def get_peer_text(title: str, suffix: str = "") -> (str, str):
    """
    Get preliminary PAS review report text as string.
    Remove recommendations from peer_report since they will be in
     comments now.
    Args:
        suffix:
        title: Doc title

    Returns:
        Tuple of string Comments and Recommendations from peer report and proposal title
    """

    coms = ""
    if not suffix:
        peer_report = pgu.get_report_path(title)
    else:
        base = pgu.PAS_DIR / title
        peer_report = base.with_suffix(suffix)

    logger.debug("Open report file: {}".format(peer_report))

    # If .pri or .sec doesn't exist, there will be no title
    prop_title = ""
    try:
        with open(str(peer_report), "r+") as rpt_file:  # str needed for < py3.6
            rep_text = rpt_file.readlines()
            comm_start_ind = rep_text.index(pgu.COMMENTS_START)
            comm_end_ind = rep_text.index(pgu.COMMENTS_END)
            recs_start_ind = rep_text.index(pgu.RECS_START)
            recs_end_ind = rep_text.index(pgu.RECS_END)
            prop_title_start_ind = [
                rep_text.index(line)
                for line in rep_text
                if line.startswith(pgu.PROPOSAL_TITLE_START)
            ][0]
            prop_title_end_ind = [
                rep_text.index(line)
                for line in rep_text
                if line.startswith(pgu.PROPOSAL_TITLE_END)
            ][0]

            # Get proposal title
            prop_title = rep_text[prop_title_start_ind:prop_title_end_ind][0]
            prop_title = prop_title.replace(pgu.PROPOSAL_TITLE_START, "").rstrip()
            # Get comments and recs then combine. Will be ['\n'] if no coms/recs
            coms = rep_text[comm_start_ind + 1 : comm_end_ind]
            # Add text to distinguish prim/sec comments/recs
            # coms = ["Primary Review :\n"] + coms + ["\n"]
            coms += ["\n\n"]
            recs = rep_text[recs_start_ind + 1 : recs_end_ind]
            # recs = ["Primary Recommendations :\n"] + recs + ["\n"]
            recs = [
                line.replace("Secondary Review", "Secondary Recommendations")
                for line in recs
            ]
            if suffix == ".sec":
                coms = ["Secondary Review :\n"] + coms + ["\n"]
                recs = ["Secondary Recommendations :\n"] + recs + ["\n"]
            coms.extend(recs)
            # Remove recs from .peer, but not pri/sec
            if suffix not in (".pri", ".sec"):
                rep_text = rep_text[: recs_start_ind + 1] + rep_text[recs_end_ind:]

                # Reset to beginning of file and write all
                rpt_file.seek(0)
                rpt_file.writelines(rep_text)
                rpt_file.truncate()
    except FileNotFoundError:
        logger.exception("{} doesn't exist".format(peer_report))

    return "".join(coms), prop_title


def populate_doc(
    title: str, file_list: list, docs_service: build, prelim: bool = False
) -> None:
    """
    Insert text from primary and secondary prelim reports
    into Google doc.
    Add comments on initial text?
    Args:
        title: Doc Title
        docs_service: build request object
        file_list: list of dicts of {id: file_id}
        prelim: Whether to populate Doc from preliminary (.pri/.sec) reports
            directly, rather than from .peer. This should be true if creating
            Docs enmasse, False for from within PAS

    Returns: None
    """

    try:
        file_id = file_list[0].get("id")
    except IndexError:
        logger.exception("No file id found for {} ".format(title))
    else:
        # This is different than what's done for update_comments.
        # There, start with something user writes in doc and insert into
        # webreports file. Here, I need to read the initial report file, and
        # insert into doc.

        # title is like panelNum_proposalNum
        panel_num, proposal_num = title.split("_")
        if prelim:
            # If there's no .pri or .sec file, then we have no way to get the title so it will be blank
            pri_text, prop_title_pri = get_peer_text(title, ".pri")
            sec_text, prop_title_sec = get_peer_text(title, ".sec")

            # If one exists go with that since they'll both be the same. If neither exists they'll both be empty.
            prop_title = prop_title_pri if prop_title_pri else prop_title_sec
            report_text = pri_text + "\n" + sec_text
        else:
            report_text, prop_title = get_peer_text(title)
        requests = [
            {
                "replaceAllText": {
                    "containsText": {"text": pgu.TEMPLATE_TEXT, "matchCase": "true"},
                    "replaceText": report_text,
                }
            },
            {
                "replaceAllText": {
                    "containsText": {
                        "text": pgu.TEMPLATE_PROP_TITLE,
                        "matchCase": "true",
                    },
                    "replaceText": prop_title,
                }
            },
            {
                "replaceAllText": {
                    "containsText": {
                        "text": pgu.TEMPLATE_PANEL_NUM,
                        "matchCase": "true",
                    },
                    "replaceText": panel_num,
                }
            },
            {
                "replaceAllText": {
                    "containsText": {
                        "text": pgu.TEMPLATE_PROPOSAL_NUM,
                        "matchCase": "true",
                    },
                    "replaceText": proposal_num,
                },
            },
        ]

        try:
            response = (
                docs_service.documents()
                .batchUpdate(documentId=file_id, body={"requests": requests})
                .execute()
            )
        except HttpError:
            logger.exception(
                "HTTPError for {}. Preliminary text" " not inserted! ".format(title)
            )
        except Exception:
            logger.exception("Unexpected error occurred in %(funcName)s")
