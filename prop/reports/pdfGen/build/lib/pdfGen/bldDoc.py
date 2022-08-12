#_PYTHON_INSERT_SAO_COPYRIGHT_HERE_(2018,2022)_
"""Interface to Reportlab to create documents"""

# from __future__ import division, unicode_literals
import copy
import itertools
from reportlab.lib.pagesizes import letter
from reportlab.lib.units import inch, cm
from reportlab.platypus import (PageTemplate,
                                Frame,
                                Paragraph,
                                Spacer,
                                BaseDocTemplate,
                                PageBreak,
                                FrameBreak,
                                Table,
                                LongTable,
                                TableStyle,
                                flowables,
                                )
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.enums import TA_LEFT, TA_CENTER, TA_RIGHT
from reportlab.lib import colors
from reportlab.lib.colors import black, white, lightgrey


class ProposalDoc(object):
    """
    Setup, define, and build a document with Reportlab
    """

    def __init__(self,
                 htextL,
                 htextC,
                 htextR,
                 ftextL,
                 logo,
                 title,
                 pi,
                 filename,
                 date_str):
        """

        Parameters
        --------------------
        htextL: Head text left
        htextC: Head text center
        htextR: Head text right
        ftextL: Foot text left
        logo: Image logo for header left corner
        title: Title of prop
        pi: PI's initial LastName
        filename: Name of output file

        """
        self.width, self.height = letter
        self.htextL = htextL
        self.htextC = htextC
        self.htextR = htextR
        self.ftextL = ftextL
        self.logo = logo
        self.title = title
        self.pi = pi
        self.date_str = date_str
        self.marg = 0.5 * inch
        self.filename = filename
        self.doc = BaseDocTemplate(self.filename,
                                   leftMargin=self.marg,
                                   rightMargin=self.marg,
                                   topMargin=self.marg,
                                   bottomMargin=self.marg,
                                   pagesize=letter)
        self.indent = inch * (1 / 16)
        self.hd_spac = inch * 0.16
        self.ft_spac = inch * 0.14
        self.xOrig = self.doc.leftMargin + self.indent
        self.yOrig = self.doc.bottomMargin + self.indent
        self.eff_width = self.doc.width - 2 * self.indent
        self.eff_height = self.doc.height - self.indent

        # dicts for all the style definitions for each section in the document
        self.style_sheets = {"default": ParagraphStyle("default",
                                                       fontName="Helvetica",
                                                       fontSize=10,
                                                       leading=0,
                                                       leftIndent=0,
                                                       rightIndent=0,
                                                       firstLineIndent=0,
                                                       alignment=TA_LEFT,
                                                       spaceBefore=0,
                                                       spaceAfter=0,
                                                       bulletFontName="Helvetica",
                                                       bulletFontSize=10,
                                                       bulletIndent=0,
                                                       textColor=black,
                                                       backColor=None,
                                                       wordWrap="LTR",
                                                       borderWidth=0,
                                                       borderPadding=0,
                                                       borderColor=None,
                                                       borderRadius=None,
                                                       allowWidows=1,
                                                       allowOrphans=0,
                                                       textTransform=None,
                                                       endDots=None,
                                                       splitLongWords=1,
                                                       )}

        self.style_sheets["header_left"] = ParagraphStyle(
            "header_left",
            parent=self.style_sheets["default"],
            fontName="Helvetica",
            fontSize=11,
            alignment=TA_LEFT,
            leading=14.4
        )

        self.style_sheets["header_right"] = ParagraphStyle(
            "header_right",
            parent=self.style_sheets["header_left"],
            alignment=TA_RIGHT,
            leading=14.4
        )

        self.style_sheets["header_center"] = ParagraphStyle(
            "header_center",
            parent=self.style_sheets["header_left"],
            alignment=TA_CENTER,
            leading=14.4
        )
        self.style_sheets["header_center_bottom"] = ParagraphStyle(
            "header_center_bottom",
            fontName="Helvetica-Bold",
            fontSize=11,
            parent=self.style_sheets["header_center"],
            alignment=TA_CENTER
        )
        self.style_sheets["footer_left"] = ParagraphStyle(
            "footer_left",
            parent=self.style_sheets["default"],
            fontSize=11,
            alignment=TA_LEFT,

        )
        self.style_sheets["footer_center"] = ParagraphStyle(
            "footer_center",
            parent=self.style_sheets["footer_left"],
            fontName="Helvetica",
            alignment=TA_CENTER,
        )
        self.style_sheets["footer_center_bottom"] = ParagraphStyle(
            "footer_center_bottom",
            parent=self.style_sheets["footer_left"],
            fontSize=11,
            alignment=TA_CENTER,
        )
        self.style_sheets["footer_center_lead"] = ParagraphStyle(
            "footer_center",
            parent=self.style_sheets["footer_left"],
            leading=14.4,
            alignment=TA_CENTER,
        )
        self.style_sheets["footer_right"] = ParagraphStyle(
            "footer_right",
            parent=self.style_sheets["footer_left"],
            spaceBefore=self.indent,
            alignment=TA_RIGHT,
        )
        self.style_sheets["left"] = ParagraphStyle(
            "left",
            parent=self.style_sheets["default"],
            alignment=TA_LEFT,
        )      
        self.style_sheets["left_bld"] = ParagraphStyle(
            "left",
            parent=self.style_sheets["default"],
            alignment=TA_LEFT,
            fontName="Helvetica-Bold",
            )
        self.style_sheets["left_bld_new"] = ParagraphStyle(
            "left",
            parent=self.style_sheets["default"],
            alignment=TA_LEFT,
            fontName="Helvetica-Bold",
            leading=14.4,
            )
        self.style_sheets["center"] = ParagraphStyle(
            "center",
            parent=self.style_sheets["default"],
            alignment=TA_CENTER
        ) 
        self.style_sheets["center_bld"] = ParagraphStyle(
            "center",
            parent=self.style_sheets["default"],
            alignment=TA_CENTER,
            fontName="Helvetica-Bold",
        )
        self.style_sheets["center_bld_new"] = ParagraphStyle(
            "center",
            parent=self.style_sheets["default"],
            alignment=TA_CENTER,
            fontName="Helvetica-Bold",
            leading=14.4,
        )
        self.style_sheets["left_new"] = ParagraphStyle(
            "left",
            parent=self.style_sheets["default"],
            leading=14.4,
            alignment=TA_LEFT,
        )
        self.style_sheets["center_new"] = ParagraphStyle(
            "center",
            parent=self.style_sheets["default"],
            leading=14.4,
            alignment=TA_CENTER
        )

        self.style_sheets["center_new_smallfnt"] = ParagraphStyle(
            "center",
            parent=self.style_sheets["center_new"],
            fontSize=7
        )
        self.style_sheets["title"] = ParagraphStyle(
            "title",
            parent=self.style_sheets["center_new"],
            alignment=TA_CENTER,
            fontSize=10,
            fontName="Helvetica-Bold",

        )
        # section headers
        self.style_sheets["left_sec"] = ParagraphStyle(
            "sec_left",
            parent=self.style_sheets["left_new"],
            fontSize=12,
            leading=14.4,
            fontName="Helvetica-Bold",

        )
        # left section header same line
        self.style_sheets["left_sec_smln"] = ParagraphStyle(
            "sec_left",
            parent=self.style_sheets["left"],
            fontSize=12,
            leading=14.4,

        )

        self.style_sheets["center_sec"] = ParagraphStyle(
            "sec_center",
            parent=self.style_sheets["center_new"],
            fontSize=12,
            leading=14.4

        )
        # right aligned always gets new line
        self.style_sheets["right"] = ParagraphStyle(
            "right",
            parent=self.style_sheets["default"],
            leading=14.4,
            alignment=TA_RIGHT
        )

        self.style_sheets["rindent"] = ParagraphStyle(
            "indent",
            parent=self.style_sheets["default"],
            leading=14.4,
            alignment=TA_LEFT,
            leftIndent=00
        )

        # Table self.styles
        self.style_sheets["table"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, 0), "Helvetica"),
             ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("INNERGRID", (0, 0), (-1, -1), 1.0, black),
             ("BOX", (0, 0), (-1, -1), 1.0, black),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
             ("NOSPLIT", (0, 35), (-1, -1))
            ]
        )
        # First row/col bold
        self.style_sheets["table_req"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, -1), "Helvetica-Bold"),
             ("FONTNAME", (1, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("INNERGRID", (0, 0), (-1, -1), 1.0, black),
             ("BOX", (0, 0), (-1, -1), 1.0, black),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
             ("NOSPLIT", (0, 35), (-1, -1))
            ]
        )
        self.style_sheets["table_left"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, 0), "Helvetica"),
             ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("INNERGRID", (0, 0), (-1, -1), 1.0, black),
             ("BOX", (0, 0), (-1, -1), 1.0, black),
             ("ALIGN", (0, 0), (-1, -1), "LEFT"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
             ("LEFTPADDING", (0, 0), (-1, -1), 0),
             ("NOSPLIT", (0, 35), (-1, -1))
             ]
        )
        self.style_sheets["table_summ"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
             ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("INNERGRID", (0, 0), (-1, -1), 1.0, black),
             ("BOX", (0, 0), (-1, -1), 1.0, black),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ]
        )
        self.style_sheets["table_no_grid"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, 0), "Helvetica"),
             ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
             ("LEFTPADDING", (0, 0), (-1, -1), 0),
             ("NOSPLIT", (0, 35), (-1, -1))
            ]
        )

        self.style_sheets["table_no_grid_left"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, 0), "Helvetica"),
             ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("ALIGN", (0, 0), (-1, -1), "LEFT"),
             ("VALIGN", (0, 0), (-1, -1), "BOTTOM"),
             ("LEFTPADDING", (0, 0), (-1, -1), 0),
             ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
             ("NOSPLIT", (0, 35), (-1, -1))
            ]
        )
        self.style_sheets["table_no_grid_lbold"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, -1), "Helvetica-Bold"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("NOSPLIT", (0, 35), (-1, -1)),
             ("ALIGN", (0, 0), (-1, -1), "LEFT"),
             ("LEFTPADDING", (0, 0), (-1, -1), 0),
             ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
             ("VALIGN", (0, 0), (-1, -1), "BOTTOM")
            ]
        )

        self.style_sheets["table_no_grid_cbold"] = TableStyle(
            [("FONTNAME", (0, 0), (0, -1), "Helvetica-Bold"),
             # ("FONTNAME", (1, 0), (1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 11),
             ("NOSPLIT", (0, 35), (-1, -1)),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             ("LEFTPADDING", (0, 0), (-1, -1), 0),
             ("VALIGN", (0, 0), (-1, -1), "BOTTOM")
            ]
        )
        self.style_sheets["table_zebra"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
             ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("ROWBACKGROUNDS", (0, 1), (-1, -1), [lightgrey, white]),
             ("INNERGRID", (0, 0), (-1, -1), 1.0, black),
             ("BOX", (0, 0), (-1, -1), 1.0, black),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
             ("NOSPLIT", (0, 35), (-1, -1))
            ]
        )
        self.style_sheets["acis"] = TableStyle(
            [("GRID", (2, 0), (3, 2), 1.0, black),  # center rect
             ("GRID", (0, -1), (-1, -1), 1.0, black),  # lower rect
             ("SPAN", (0, 0), (1, 1)),
             ("SPAN", (-2, 0), (-1, 1)),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
            ]
        )
        self.style_sheets["too"] = TableStyle(
            [("FONTNAME", (0, 0), (-1, 2), "Helvetica-Bold"),
             ("FONTNAME", (0, 1), (-1, 1), "Helvetica-Bold"),
             ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
             ('FONTSIZE', (0, 0), (-1, -1), 10),
             ("INNERGRID", (0, 0), (-1, -1), 1.0, black),
             ("BOX", (0, 0), (-1, -1), 1.0, black),
             ("ALIGN", (0, 0), (-1, -1), "CENTER"),
             # ("ALIGN", (0, 2), (0, -1), "RIGHT"),
             # ("ALIGN", (2, 2), (4, -1), "RIGHT"),
             # ("ALIGN", (6, 2), (10, -1), "RIGHT"),
             # ("ALIGN", (-1, 2), (-1, -1), "RIGHT"),
             ("ALIGN", (5, 2), (5, -1), "LEFT"),
             ("VALIGN", (0, 0), (-1, -1), "TOP"),
             ("SPAN", (1, 0), (3, 0)),  # Response Window
             ("SPAN", (5, 0), (9, 0)),  # Observations
             ("SPAN", (-2, 0), (-1, 0)),  # Alternates
             ("SPAN", (0, 0), (0, 1)),  # Targ No
             ("SPAN", (4, 0), (4, 1)),  # Probability
             ("SPAN", (-3, 0), (-3, 1)),  # Exposure Time
            ]
        )

    def shade_or_span(self, row, col, style, color="lightgrey", shade=True,
                      hd_rws=0):
        """Modify TableStyle cells by altering the background color or spanning
           across a row or col. Takes list of rows, but individual columns.

        Parameters:
        --------------------
        row: Single row number for shading background or spanning row,
         2 element list for spanning col
        col: Single column number for shading background or spanning col,
         tuple spanning row
        color: Background color
        style: Style name
        shade: Shade if true, else span
        hd_rws: Number of header rows to skip

        Return:
        --------------------
        stlye: Modified style sheet

        """

        if not isinstance(style, TableStyle):
            style = copy.deepcopy(self.style_sheets.get(style))
        for rr in row:
            if isinstance(col, list):
                col0 = col[0]
                col1 = col[1]
            else:
                col0 = col
                col1 = col

            if isinstance(rr, tuple):
                row0 = rr[0]
                row1 = rr[1]
            else:
                row0 = rr
                row1 = rr

            row0 += hd_rws
            row1 += hd_rws
            if shade:
                style.add('BACKGROUND', (col0, row0), (col1, row1),
                          getattr(colors, color))
            else:
                style.add('SPAN', (col0, row0), (col1, row1))

        return style

    def mk_head_foot_cover(self, canvas, doc):
        """Create the header and footer to appear on cover page

        Parameters:
        --------------------
        canavs: canvas object to drawOn
        doc: the Document
            PageTemplate onPage argument expects a function
            that contains (only?) canvas and doc

        """

        canvas.saveState()
        headY = doc.height + doc.topMargin - self.hd_spac
        footY = doc.bottomMargin + self.marg - 2 * self.ft_spac

        # hl
        header = Paragraph(self.htextL, self.style_sheets.get("header_left"))
        w, h = header.wrap(self.eff_width + self.indent, doc.topMargin)
        header.drawOn(canvas, 2 * self.xOrig, headY)

        # add logo
        canvas.drawImage(self.logo, self.marg, headY, self.marg, self.marg)

        # hc top
        header = Paragraph(self.htextC, self.style_sheets.get("header_center"))
        w, h = header.wrap(self.eff_width + self.indent, doc.topMargin)
        header.drawOn(canvas, doc.leftMargin, headY)

        # hr
        header = Paragraph(self.htextR, self.style_sheets.get("header_right"))
        w, h = header.wrap(self.eff_width + self.indent, doc.topMargin)
        header.drawOn(canvas, doc.rightMargin, headY)

        # Line below header
        line = flowables.HRFlowable(width=self.eff_width, color=black)
        w, h = line.wrap(self.eff_width + self.indent, doc.topMargin)
        line.drawOn(canvas, self.xOrig, headY)

        # fl
        footer = Paragraph(self.ftextL,
                           self.style_sheets.get("footer_left"))
        w, h = footer.wrap(self.eff_width + self.indent, doc.bottomMargin)
        footer.drawOn(canvas, self.xOrig, footY)

        # fr
        footer = Paragraph(self.date_str,
                           self.style_sheets.get("footer_right"))
        w, h = footer.wrap(self.eff_width + self.indent, doc.bottomMargin)
        footer.drawOn(canvas, doc.leftMargin, footY)

        # fc bottom
        footer = Paragraph(str(canvas._pageNumber),
                           self.style_sheets.get("footer_center"))
        w, h = footer.wrap(self.eff_width + self.indent, doc.bottomMargin)
        footer.drawOn(canvas, doc.leftMargin, footY / 2.)

        canvas.restoreState()

    def mk_head_foot_subsq(self, canvas, doc):
        """Create the header and footer to appear on subsequent pages

        Parameters:
        --------------------
        canavs: canvas object to drawOn
        doc: the Document
            PageTemplate onPage argument expects a function
            that contains (only?) canvas and doc

        """

        canvas.saveState()
        headY = doc.height + doc.topMargin - self.hd_spac
        footY = doc.bottomMargin + self.marg - 2 * self.ft_spac

        # hl
        header = Paragraph(self.htextL, self.style_sheets.get("header_left"))
        w, h = header.wrap(self.eff_width + self.indent, doc.topMargin)
        header.drawOn(canvas, 2 * self.xOrig, headY)

        # add logo
        canvas.drawImage(self.logo, self.marg, headY, self.marg, self.marg)

        # hc top
        header = Paragraph(self.htextC, self.style_sheets.get("header_center"))
        w, h = header.wrap(self.eff_width + self.indent, doc.topMargin)
        header.drawOn(canvas, doc.leftMargin, headY)

        # hr
        header = Paragraph(self.htextR, self.style_sheets.get("header_right"))
        w, h = header.wrap(self.eff_width + self.indent, doc.topMargin)
        header.drawOn(canvas, doc.rightMargin, headY)

        # Line below header
        line = flowables.HRFlowable(width=self.eff_width, color=black)
        w, h = line.wrap(self.eff_width + self.indent, doc.topMargin)
        line.drawOn(canvas, self.xOrig, headY)

        # fl
        footer = Paragraph(self.ftextL,
                           self.style_sheets.get("footer_left"))
        w, h = footer.wrap(self.eff_width + self.indent, doc.bottomMargin)
        footer.drawOn(canvas, self.xOrig, footY)

        # fr
        footer = Paragraph(self.date_str,
                           self.style_sheets.get("footer_right"))
        w, h = footer.wrap(self.eff_width + self.indent, doc.bottomMargin)
        footer.drawOn(canvas, doc.leftMargin - self.indent, footY)

        # fc bottom
        footer = Paragraph(str(canvas._pageNumber),
                           self.style_sheets.get("footer_center_bottom"))
        w, h = footer.wrap(self.eff_width + self.indent, doc.bottomMargin)
        footer.drawOn(canvas, doc.leftMargin, footY / 2.)

        # fc top
        footer = Paragraph(self.pi, self.style_sheets.get("footer_center"))
        w, h = footer.wrap(self.eff_width + self.indent, doc.bottomMargin)
        footer.drawOn(canvas, doc.leftMargin, footY)
        
        # hc bottom
        header = Paragraph(self.title,
                           self.style_sheets.get("header_center_bottom"))
        w, h = header.wrap(self.eff_width + self.indent, doc.topMargin)
        header.drawOn(canvas, doc.leftMargin, headY - self.marg/1.2)

        canvas.restoreState()

    def table_cont(self, data, styles=None, sep=None, widths=None):
        """Arrange multiple tables side-by-side by creating a container Table
           consisting of the multiple individual tables.

        Parameters:
        --------------------
        data: List of data for each table
        style: List of style types for each table
        sep: Separation btwn container tables
        width: List of column widths in cm for each Table

        Returns:
        --------------------
        Table: Container table for the tables

        """

        # same sep between each table
        halign = None
        if sep is None:
            sep = [2.7]
        elif sep == "full":
            data_len = len(data)  # number of cols
            sep = [self.eff_width / data_len]
            halign = "LEFT"
        else:
            sep *= len(data)
            sep = [ss * inch for ss in sep]

        if styles is None:
            styles = []
        if widths is None:
            widths = []

        cont = []
        for dat, style, width in itertools.zip_longest(data, styles, widths):
            if width:
                width = [ww * cm for ww in width]
            if style is None:
                style = "table"
            if not isinstance(style, TableStyle):
                style = self.style_sheets.get(style)
            tab = Table(dat, style=style, colWidths=width)
            cont.append(tab)
        cont = [cont]

        return cont, sep, halign

    def bld_flowables(self,
                      txt=None,
                      w_next=False,
                      style=None,
                      flow_type="par",
                      flow_list=None,
                      spc_w=1,
                      spc_h=0.62 * cm,
                      colw=None,
                      halign=None):
        """
        Choose type of flowable and append to list

        Parameters:
        --------------------
            txt: data text that goes into each flowable
            w_next: Keep Flowable with next Flowable
            style: style type
            flow_type: par: Paragraph,
                       line: Draw line * add arg for width
                       tab_left: Table, left aligned
                       er: Table, center aligned
                       cont: Container Table
                       space: Spacer,
                       frm_brk: FrameBreak
                       pg_brk: 1PageBreak
            flow_list: list of previous flowables
            spc_w, spc_h: Spacer width, height
            colw: list of colWidths.
            halign: Horizontal alignment within table


        Returns:
        --------------------
        flow_list: List of flowables
        """

        if halign is None:
            halign = "LEFT"
        if flow_list is None:
            flow_list = []
        if style and not isinstance(style, TableStyle):
            # If I pass in a string vs. TableStyle
            style = self.style_sheets.get(style)
        if colw == "pg":
            colw = self.eff_width
        if flow_type == "par":
            flow = Paragraph(txt, style)
        elif flow_type == "line":
            flow = flowables.HRFlowable(width="100%", color=black,
                                        vAlign='CENTER', spaceAfter=3)
        elif flow_type == "abs":
            txt = Paragraph(txt, self.style_sheets.get("left_new"))
            flow = Table([[txt]], style=style, spaceBefore=3,
                         hAlign="LEFT", repeatRows=1, colWidths=colw)
        elif flow_type == "tab_left":
            flow = Table(txt, style=style, spaceBefore=3, hAlign="LEFT",
                         repeatRows=1, colWidths=colw)
        elif flow_type == "tab_center":
            flow = LongTable(txt, style=style, spaceBefore=3, hAlign="CENTER",
                             repeatRows=1, colWidths=colw, splitByRow=True)
        elif flow_type == "tab_full":
            col_width = [self.eff_width / (len(txt[0]))]
            flow = Table(txt, style=style, hAlign="LEFT", rowHeights=15,
                         colWidths=col_width, splitByRow=True)
        elif flow_type == "tab_too":
            flow = LongTable(txt, style=style, spaceBefore=3, hAlign="CENTER",
                             repeatRows=2, colWidths=colw, splitByRow=True)
        elif flow_type == "cont":
            flow = Table(txt, style=style, spaceAfter=3, spaceBefore=3,
                         hAlign=halign, colWidths=colw)
        elif flow_type == "spac":
            flow = Spacer(spc_w, spc_h)
        elif flow_type == "frm_brk":
            flow = FrameBreak()
        elif flow_type == "pg_brk":
            flow = PageBreak()

        if w_next:
            flow.keepWithNext = w_next

        flow_list.append(flow)

        return flow_list

    def bld_frames(self, frame_list=None, ncols=1, h=None, yO=None, pg="cover"):
        """Create, append to, and return list of frames

        Parameters:
        --------------------
        frame_list: List of existing frames to append to
        ncols: number of cols to use in frame
        h: height of Frame
        y0: y origin of Frame
        pg: Use cover or subsq frame height (subsq is lower to accomadate
            potentially long titles)

        NB: Currently genPDF only uses one_col.

        Return:
        --------------------
        frame_list: List of Frames

        """

        if frame_list is None:
            frame_list = []
        if not h:
            h = self.doc.height
        if not yO:
            yO = self.yOrig
        if pg == "subsq":
            h -= self.marg

        col_gap = 0.5 * cm  # different col gap for more columns?

        # Single frame spanning width of page
        if ncols == 1:
            one_col = Frame(x1=self.xOrig,
                            y1=yO,
                            width=self.eff_width,
                            height=h,
                            showBoundary=0,
                            leftPadding=0,
                            bottomPadding=1 * cm,
                            rightPadding=0,
                            topPadding=1 * cm)
            new_frame = [one_col]

        # Two and three cols are not currently used!
        # Two columns, each half width of page
        elif ncols == 2:
            two_col_left = Frame(x1=self.xOrig,
                                 y1=yO,
                                 width=self.eff_width / 2,
                                 height=h,
                                 showBoundary=0,
                                 leftPadding=0,
                                 bottomPadding=1 * cm,
                                 rightPadding=col_gap,
                                 topPadding=1 * cm)

            two_col_right = Frame(x1=self.xOrig + self.eff_width / 2,
                                  y1=yO,
                                  width=self.doc.width / 2,
                                  height=h,
                                  showBoundary=0,
                                  leftPadding=col_gap,
                                  bottomPadding=1 * cm,
                                  rightPadding=0,
                                  topPadding=1 * cm)

            new_frame = [two_col_left, two_col_right]

        # Three columns, each third width of page
        else:
            three_col_left = Frame(x1=self.xOrig,
                                   y1=yO,
                                   width=self.eff_width / 3,
                                   height=h,
                                   showBoundary=0,
                                   leftPadding=0,
                                   bottomPadding=1 * cm,
                                   rightPadding=col_gap,
                                   topPadding=1 * cm)

            three_col_center = Frame(x1=self.xOrig + self.eff_width / 3,
                                     y1=yO,
                                     width=self.eff_width / 3,
                                     height=h,
                                     showBoundary=0,
                                     leftPadding=col_gap,
                                     bottomPadding=1 * cm,
                                     rightPadding=col_gap,
                                     topPadding=1 * cm)

            three_col_right = Frame(x1=self.xOrig + 2 * self.eff_width / 3,
                                    y1=yO,
                                    width=self.eff_width / 3,
                                    height=h,
                                    showBoundary=0,
                                    leftPadding=col_gap,
                                    bottomPadding=1 * cm,
                                    rightPadding=0,
                                    topPadding=1 * cm)

            new_frame = [three_col_left, three_col_center, three_col_right]

        frame_list.extend(new_frame)

        return frame_list

    def par_in_table(self, table, sty="center_new"):
        """Wrap most cells in a table in Paragraph

        Parameters:
        --------------------
        table: list of list for Table
        sty: string style name for style_sheets

        Return:
        --------------------
        par: Formatted tab

        """

        par = []
        for ind, row in enumerate(table):
            tab = []
            if ind == 0:
                # skip header
                tab = [rr for rr in row]
            else:
                # Only wrap strings
                for r_ind, rr in enumerate(row):
                    nsty = sty
                    # want ra/dec for target summ to have smaller font
                    # par_in_table is currently only used for that table.
                    # if that changes, this needs to be modified.
                    if r_ind in [2, 3]:
                        nsty = "center_new_smallfnt"
                    if isinstance(rr, str):
                        tab.append(Paragraph(str(rr),
                                   style=self.style_sheets.get(nsty)))
                    else:
                        tab.append(rr)
            par.append(tab)

        return par

    def bld_templates(self, frames_cover, frames_subsq, temp_list=None):
        """ Setup page templates and build the document. Uses two different
            page templates for cover and subsequent pages.

        Parameters:
        --------------------
        frames_cover: list for frames for cover page for PageTemplate
        frames_subsq: list for frames for subsequent pages for PageTemplate
        temp_list: list of previous templates (not used by genPDF.py)

        Return:
        --------------------
        temp_list: List of PageTemplates

        """

        # Cover page with different head/foot on subsequent pages
        if temp_list is None:
            temp_list = []
        coverPage = PageTemplate(id="cover", frames=frames_cover,
                                 onPage=self.mk_head_foot_cover)

        nextPages = PageTemplate(id="subsq", frames=frames_subsq,
                                 onPage=self.mk_head_foot_subsq)
        temp_list.extend([coverPage, nextPages])

        return temp_list
