"""
Modify Google Doc permissions for users.
"""

import pg_utils as pgu
from googleapiclient.discovery import build

logger = pgu.logger


def get_perm_ids(file_id: str, emails: list, drive_service: build) -> list:
    """
    Call Drive API to get file_id for Document title.
    If no matching Doc is found, file_id is empty
    Args:
        file_id: File ID corresponding to title of Doc
        emails: List of email addresses to get permission ids for
        drive_service:

    Returns:
        List of perm_ids matching match role / email order
    """
    # response will look like
    # {'permissions': [{'id': 'idnum', 'emailAddress': 'jcohen@head.cfa.harvard.edu'},...]

    response = (
        drive_service.permissions()
        .list(
            fileId=file_id,
            fields="permissions(id, emailAddress)",
            supportsAllDrives=True,
        )
        .execute()
    )

    perm_ids = {
        perm.get("emailAddress"): perm.get("id")
        for perm in response.get("permissions")
        if perm.get("emailAddress") in emails
    }

    # sort on emails so it's in the right order
    perm_ids = pgu.sort_dict_on_list(perm_ids, emails)
    logger.debug("Permission IDs from email {}: ".format(perm_ids))

    return list(perm_ids.values())


def modify_perms(title: str, drive_service: build, roles: list, emails: list) -> None:
    """
    Update Google Doc permissions for multiple users on a single Doc.

    Args:
        title: Title of Doc
        drive_service: build request object
        roles: New Permission roles
        emails: gmail addresses to modify permissions for

    Returns: None
    """

    file_list = pgu.get_file_ids(title, drive_service)

    # Should only return one, uniquely named document since there's one title
    try:
        file_id = file_list[0].get("id")
    except IndexError:
        logger.exception("No file id found for {} ".format(title))
    else:
        logger.debug("File id for {}: {} ".format(title, file_id))
        perm_ids = get_perm_ids(file_id, emails, drive_service)
        # Errors raised in new_batch_http_request -> callback. Don't catch here
        batch = drive_service.new_batch_http_request(pgu.callback)
        for role, perm_id in zip(roles, perm_ids):
            user_permission = {"role": role}
            batch.add(
                drive_service.permissions().update(
                    fileId=file_id,
                    permissionId=perm_id,
                    body=user_permission,
                    fields="id",
                    supportsAllDrives=True,
                )
            )
        batch.execute()
