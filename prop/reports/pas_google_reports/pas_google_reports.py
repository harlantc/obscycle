#!/usr/bin/env /proj/cm/Release/ots.install.DS10.11/google-python/miniconda3/envs/google-auth/bin/python

"""
Google Review Reports:
Wrapper for running the Google Doc/Drive interfaces to PAS
"""

import os
import pg_utils as pgu
from googleapiclient.errors import HttpError, UnknownApiNameOrVersion
import configparser


def main() -> None:
    """
    Parse args and run the appropriate actions.
    """
    if isinstance(pgu.CONFIG, configparser.ConfigParser):
        try:
            title, roles, emails, action, dev = pgu.parse_args()
            logger = pgu.logger
            logger.info("Action is {}, for {} ".format(action, title))
            # import after logger to make sure logger is passed along
            import update_comments as uc
            import create_doc as cd
            import modify_permissions as mp

            # all actions need drive. create/update need docs
            drive_service = pgu.get_service(pgu.SCOPES, "drive", dev)

            # Can currently create and update proposals en masses
            if action in ("create", "update"):
                docs_service = pgu.get_service(pgu.SCOPES, "docs", dev)
                prelim = False  # for create
                enmasse = ("peer", "bpp")
                if title in enmasse:
                    title_emails = pgu.get_prop_panel_email()
                    prelim = True
                else:
                    title_emails = [{title: emails}]

            if action == "create":
                # TODO rewrite create_doc to do 100 Doc batches
                for panel in title_emails:
                    for pan_prop, prop_emails in panel.items():
                        # for bpp action only make bpp, for peer don't make bpp
                        if (title == "bpp" and "bpp" not in pan_prop) or (
                            title == "peer" and "bpp" in pan_prop
                        ):
                            continue
                        if title in enmasse:
                            gmails = pgu.get_gmails(prop_emails)
                            roles = ["writer"] * len(gmails)
                        else:
                            gmails = prop_emails
                        logger.info("Creating new Doc titled {}".format(pan_prop))
                        file_list, existed = cd.create_doc(pan_prop, drive_service)
                        file_id = file_list[0].get("id")
                        if title in enmasse:
                            print(
                                "Creating new Doc titled: {}, file_id: "
                                "https://docs.google.com/document/d/{}".format(
                                    pan_prop, file_id
                                )
                            )
                        else:
                            # Return just id to PAS
                            print(file_id)
                        if not existed:
                            logger.info(
                                "Initializing permissions: emails: {}"
                                " roles{}".format(gmails, roles)
                            )
                            cd.create_perms(
                                pan_prop, roles, gmails, drive_service, file_list
                            )
                            logger.info("Populating {} from template.".format(pan_prop))
                            cd.populate_doc(pan_prop, file_list, docs_service, prelim)
            elif action == "fileid":
                file_list = pgu.get_file_ids(title, drive_service)
                try:
                    file_id = file_list[0].get("id")
                except IndexError:
                    logger.exception("No file id found for {} ".format(title))
                else:
                    print(file_id)
            elif action == "update":
                logger.info(
                    "Updating PAS Report file from Doc for" " {}.".format(title)
                )
                for panel in title_emails:
                    # Only need pan_prop for update, not emails
                    for pan_prop in panel.keys():
                        if title in enmasse:
                            print("Updating comments for: {}".format(pan_prop))
                        uc.update_comments(pan_prop, drive_service, docs_service)
            else:  # perms
                mp.modify_perms(title, drive_service, roles, emails)
        except AssertionError:
            logger.exception("Bad input in argparse:")
        except UnknownApiNameOrVersion:
            logger.exception("Bad Drive or Doc version supplied.")
        except HttpError:
            logger.exception("Error in Drive or Doc request.")
        except Exception:
            logger.exception("An unexpected error occurred.")
    else:

        config_err_file = "/tmp/config_err.log"
        with open(config_err_file, "w") as logf:
            logf.write(
                "Configuration failed: "
                " {0}\n{1}".format(str(pgu.CONFIG), os.environ["ASCDS_PROP_DIR"])
            )


if __name__ == "__main__":
    main()
