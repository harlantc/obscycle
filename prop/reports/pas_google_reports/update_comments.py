"""
Obtain Google Doc for given proposal and insert Doc text into PAS report.
"""

from googleapiclient.discovery import build
import pg_utils as pgu

logger = pgu.logger


def read_paragraph_element(element) -> str:
    """ Get text in the given ParagraphElement.
        Args:
            element: a ParagraphElement from a Google Doc.
        Returns:
            String of paragraph text
    """
    text_run = element.get("textRun")
    if not text_run:
        return ""
    return text_run.get("content")


def read_structural_elements(elements: list) -> str:
    """Recurse through a list of Structural Elements to read a document's text
        where text may be in nested elements.

        Args:
            elements: List of Structural Elements.
        Returns:
            Text string for entire document
    """

    text = ""
    for value in elements:
        if "paragraph" in value:
            elements = value.get("paragraph").get("elements")
            for elem in elements:
                text += read_paragraph_element(elem)
        # Don't read in tables
        # elif 'table' in value:
        #     # Text in table cells is in nested Structural Elements
        #     # and tables may be nested.
        #     table = value.get('table')
        #     for row in table.get('tableRows'):
        #         cells = row.get('tableCells')
        #         for cell in cells:
        #             text  += read_structural_elements(cell.get('content'))
        elif "tableOfContents" in value:
            # The text in the TOC is also in a Structural Element.
            toc = value.get("tableOfContents")
            text += read_structural_elements(toc.get("content"))

    text = pgu.replace_common_utf(text)

    return text


def replace_report_comments(doc_text: str, report_path) -> None:
    """
    Read in PAS report file, find line to replace report comments with
    Google Doc text, insert and write back to report file.

    Args:
        doc_text: Text from Google Doc to insert in report file
        report_path: Path to report file to insert text into.

    Returns: None
    """

    try:
        # str needed for < py3.6
        with open(str(report_path), "r+", encoding="utf-8") as rpt_file:
            rep_text = rpt_file.readlines()
            start_ind = rep_text.index(pgu.COMMENTS_START)
            end_ind = rep_text.index(pgu.COMMENTS_END)

            # Excise previous comments between comments_start and comments_end
            # insert text from doc in it's place
            rep_text = rep_text[: start_ind + 1] + rep_text[end_ind:]
            rep_text.insert(start_ind + 1, doc_text)

            # Reset to beginning of file and write all
            rpt_file.seek(0)
            rpt_file.writelines(rep_text)
            rpt_file.truncate()
    except FileNotFoundError:
        logger.exception("{} doesn't exist".format(report_path))


def update_comments(title: str, drive_service: build, docs_service: build) -> None:
    """
    Get text from Google Doc, find appropriate PAS report file, and update
        that file with text from the Doc in the comments location.
    Args:
        title: Doc title
        drive_service: Drive build request object
        docs_service: Doc build request object

    Returns: None

    """

    file_list = pgu.get_file_ids(title, drive_service)
    if not file_list:
        warn = "No documents obtained by query for {}!".format(title)
        logger.warning(warn)
    # TODO Consider using batch?
    # batch = drive_service.new_batch_http_request(callback=pgu.callback)
    for doc in file_list:
        # Get text from Doc
        response = docs_service.documents().get(documentId=doc["id"]).execute()  # dict
        logger.info(
            "\nThe title of the document being updated is: "
            "{}\n".format(response.get("title"))
        )
        doc_content = response.get("body").get("content")
        doc_text = read_structural_elements(doc_content)

        # Replace text from Doc in report file
        report_path = pgu.get_report_path(title)
        replace_report_comments(doc_text, report_path)
