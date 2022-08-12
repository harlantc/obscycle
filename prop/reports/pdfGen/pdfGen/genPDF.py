#_PYTHON_INSERT_SAO_COPYRIGHT_HERE_(2018-2021)_
"""Run the data acquisition and doc building tools to generate PDFs"""
import os
import re
import warnings
from pdfGen.bldDoc import ProposalDoc
import pdfGen.parseData as prs
import pkg_resources as pkg
from PyPDF2 import PdfFileWriter, PdfFileReader, PdfFileMerger
from datetime import datetime
import time


warnings.formatwarning = prs.newWarn


def mk_pdf(ao, prop_list, usr, pw, srvr, form, out_dir, verbose, hide):
    """Interface to bldDoc and parseData to generate PDFs from proposal data
       using layout dicts to arrange data fields in the document.

    Parameters:
    --------------------
    ao: Cycle number. Get all proposals in cycle if given.
    prop_list: List of proposals
    usr: Database user name
    pw: Database password
    srvr: Name of Database server

    form: Output proposal format (gen, split, grant)
    out_dir: Optional directory of output PDF(s).
    verbose: Output progress statement.
    hide: If True, hide PI/CoI info

    Return
    --------------------
    pdf: dictionary of
    prop_warns: List of input props missing
    ao_warns: List of input aos missing
    prop_type: Proposal type

    """

    # Get logo
    img = pkg.resource_filename("pdfGen", "config/cxc-logo.jpg")
    coverJson = pkg.resource_filename("pdfGen", "config/coverDict.json")
    # Get config and proposal data
    conf = prs.parse_json(coverJson)
    prop_warns = []
    prop_list, ao_warns = prs.combine_props(user=usr, pwd=pw, ao_list=ao,
                                            props=prop_list, srvr=srvr,
                                            vrb=verbose)

    # Loop over each proposal
    pdf_dict = {}
    prop_type = ""
    for p in prop_list:
        d = prs.query_DB(p, user=usr, pwd=pw, srvr=srvr, vrb=verbose)
        if form == "merge" or form == "grant" or form == "split":
            # put support files in subdir to avoid conflicts
            sup_dir = os.path.join(out_dir, p)
            if not os.path.isdir(sup_dir):
                os.mkdir(sup_dir)
            sup_pdfs = prs.getSup(usr, pw, p, sup_dir)
        else:
            sup_pdfs = []
        if not d:
            err = "Proposal {} is empty or not in database. ".format(p) + \
                  "Skipping this proposal."
            warnings.warn(err)
            prop_warns.append(p)
            continue
        
        # Get values for header/footer
        f_nam, m_nam, l_nam = prs.get_name(d, form)
        # No longer want full name in header
        # pi = " ".join(filter(None, (f_nam, m_nam, l_nam)))
        init = f_nam[0] + ". " + l_nam  # F. Last
        if "grant" in form or not hide:
            pi = init
        else:
            pi = ""
        prop_type = str_title(d.get("type"))
        trigcnt = d.get("trigger_cnt")
        trigcnt = 0 if not trigcnt else trigcnt
        isDDT = True if "ddt" in prop_type.lower() else False
        if "too" in prop_type.lower():  # TOO and GTO/TOO
            isTOO = True
        # DDT, except non-transient
        elif isDDT and trigcnt > 0:
            isTOO = True
        else:
            isTOO = False

        if prop_type not in ["Theory", "Archive"]:
            if prop_type not in ["Cct","Cecs"]:
                prop_type = "Observing"
            else:
                prop_type = "Chandra Cool Target"

        # Get RC and total RC for all targets in proposal
        rc_tot = []
        rc = {}
        if prop_type == "Observing" and not isTOO:
            rc, rc_tot = prs.get_rc(propid=p, user=usr, pwd=pw, srvr=srvr,
                                    verbose=verbose, out_log=out_dir)
        ao_str = d.get("ao_str")
        cyc = "Cycle {}".format(ao_str)
        prop_num = d.get("proposal_number")
        propid = d.get("proposal_id")
        chandra = "<i>Chandra X-Ray Center</i>"
        title = d.get("title")
        if not title:
            title = "No title"
        # submission_date is already datetime object or None. Due to sybpydb?
        sub_date = d.get("submission_date")
        if "grant" in form and isinstance(sub_date, datetime):
            sub_date = "Submission Date: " + sub_date.strftime("%x")
        else:
            sub_date = "PDF Date: " + time.strftime("%x")

        if verbose > 1:
            print("Retrieving {} {}".format(prop_num, prop_type))

        # Useful to have targets separate
        targets = d.get("TARGETS")
        if prop_type == "Observing":
            # modify certain targets
            targets = prs.mod_cycle(targets, ao_str)
            targets = prs.mod_solar_name(targets)
            targets = prs.convert_ra_dec(targets)

        # Get output pdf name
        pdf = out_name(pi, prop_num, out_dir, form)
        pdf_dict[pdf] = sup_pdfs
        if verbose > 0:
            print("Generating PDF for proposal #: {}, cycle {}, {}"
                  .format(prop_num, ao_str, pdf))

        prop = ProposalDoc(htextL=chandra,
                           htextC=prop_type + " Proposal",
                           htextR=cyc,
                           ftextL="Proposal Number: " + prop_num,
                           logo=img,
                           title=title,
                           pi=pi,
                           filename=pdf,
                           date_str=sub_date)

        # Setup Frames and Templates
        frames_cov = prop.bld_frames(frame_list=[], ncols=1)
        frames_sub = prop.bld_frames(frame_list=[], ncols=1, pg="subsq")

        temps = prop.bld_templates(frames_cover=frames_cov,
                                   frames_subsq=frames_sub, temp_list=[])
        prop.doc.addPageTemplates(temps)

        # Get all blocks and pass them into bld_flowables
        flows = []
        pgBreak = False  # Flag for setting a page break
        doneThr = False  # Flag for breaking out for theory/archive
        colw = None      # colWidths for Table
        halign = None    # hAlign for table_cont
        for c in conf:
            if c == "Proposal Overview" and not hide:
                flows = prop.bld_flowables(flow_type="line", flow_list=flows)
            if c == "COI" and not hide:
                flows = title_above(prop, key="Co-Investigators",
                                    flows=flows, align="center_sec",
                                    ftype="par", spaceb4=False, sectitle=True)
            if c == "DDT Proposal Information":
                if isDDT:
                    flows = title_above(prop, key=c,
                                        flows=flows, align="center_sec",
                                        ftype="par", spaceb4=False,
                                        sectitle=True)
                    # flows = prop.bld_flowables(flow_type="spac",
                    #                            flow_list=flows)
                else:
                    continue
            blk = prs.get_blk(c, conf, d, form, verbose)
            for datFld, datVal in blk.items():
                par = "par"
                if not datFld.endswith("Dict"):
                    txt = "{}: {}".format(datFld, datVal[0])
                if datFld in ["Department", "Street"]:
                    if not hide:
                        txt = "{}".format(datVal[0])
                    else:
                        continue
                if datFld in ["Email", "Phone", "ORCID"] and hide:
                    continue
                if datFld in ["Country"]:
                    if not hide:
                        txt = "{}: {}".format(datFld, datVal[0])
                    else:
                        continue
                if datFld == "Mail":
                    if not datVal[0]:
                        continue
                    else:
                        if not hide:
                            txt = "{}".format(datVal[0])
                        else:
                            continue
                if datFld == "City":
                    if not hide:
                        state = d.get("state")
                        zipc = d.get("zip")
                        txt = "{}, {}, {}".format(datVal[0], state, zipc)
                    else:
                        continue
                if datFld in ["Principal Investigator", "Primary Institute"]:
                    if not hide:
                        datVal[1] = "left_bld_new"
                    else:
                        continue
                if datFld == "Linked Prop":
                    datVal[0] = "N" if not datVal[0] else datVal[0]
                    txt = "{}: {}".format(datFld, datVal[0])
                if datFld == "Requested Time_Dict":
                    if prop_type == "Observing":
                        time_txt = "<b>Request Summary</b>"
                        flows = title_above(prop, time_txt, flows,
                                            align="left_new", ul=False)
                        flds = prs.get_cycle_list(targets)
                        rname = ["Cycle " + re.findall("\d+", xx)[0]
                                 for xx in flds]
                        atime = 0.0  # get alternate target time to exclude
                        if isTOO:
                            atime = prs.ex_alt_time(propid, usr, pw, srvr,
                                                    verbose)
                        tab_mcop = prs.mk_gen_tab(targets, flds, rname, fill=True,
                                              sm=flds, atime=atime,
                                              idx_head=True)
                        rname_tot = "Requested Time (ks)"
                        if isTOO:
                            # do this here instead of TOO summ since I need
                            # the total triggers
                            rname_too = ["Targ\nNo", "Type", "Start", "Stop",
                                         "Proba\n-bility", "Observ\n-ation",
                                         "Exp\nTime", "Min\nInt", "Max\nInt",
                                         "Params from\nTarget No.",
                                         "Exposure\nTime", "Grp\nName",
                                         "# Req"]

                            flds_too = ["targ_num", "tootype", "start", "stop",
                                        "probability", "status",
                                        "req_obs_time", "pre_min_lead",
                                        "pre_max_lead", "params",
                                        "prop_exposure_time",
                                        "atg_group_name", "atg_req_count"]
                            dfTOO = prs.mk_too_tab(targets, flds_too,
                                                   rname_too)
                            rc_tab = None
                        else:
                            datVal.pop("# TOO Triggers")
                            # Create total RC
                            rc_tab = ['Resource Cost', *rc_tot, round(sum(rc_tot), 2)]
                        tab_total = prs.mk_tot_time_tab(datVal, rname_tot,
                                                   drop_rows=[1])
                        stylel = prop.style_sheets.get("table_req")
                        
                        req_tab = prs.mk_req_summ_tab(tab_total, tab_mcop, rc_tab)
                        stylel.add("ALIGN", (0, 0), (-1, -1), "RIGHT")
                        txt, colw, _ = prop.table_cont(data=[req_tab],
                                styles=[stylel],sep=[2.5])
                        par = "cont"
                        datVal["Total Time/Budg"][1] = "table_no_grid_left"
                        datVal = datVal["Total Time/Budg"]
                    else:
                        ttxt = "Total Requested Budget"
                        budg = datVal["Total Time/Budg"][0]
                        txt = "<b>{}: </b> ${}k".format(ttxt, budg)
                        datVal = datVal["Total Time/Budg"]
                elif datFld == "Joint Requests":
                    if datVal[0]:
                        if "-" in datVal[0]:
                            txt = "{}: {}".format(datFld, datVal[0])
                            par = "par"
                        else:
                            flows = title_above(prop, datFld, flows,
                                                align="left_bld_new", ul=False)
                            txt = prs.mk_jointrq_tab(d, datVal[0])
                            par = "tab_left"
                            style = prop.style_sheets.get("table")
                            style.add("ALIGN", (1, 1), (1, -1), "RIGHT")
                            datVal[1] = style
                    else:
                        txt = "{}: {}".format(datFld, "N/A")
                        par = "par"
                elif datFld == "Science Keywords":
                    if not datVal[0]:
                        datVal[1] = "right"
                    dv = datVal[0]  # str_title(datVal[0])
                    txt = "{}: {}".format(datFld, dv)
                elif datFld == "Subject Category":
                    if not datVal[0]:
                        datVal[1] = "right"
                    dv = datVal[0]  # str_title(datVal[0])
                    txt = "{}: {}".format(datFld, dv)
                elif datFld == "Abstract":
                    flows = title_above(prop, datFld, flows, sectitle=True)
                    txt = datVal[0]
                    par = "abs"
                    pgBreak = True
                    colw = "pg"
                    # Not sure why this doesn't happen automatically
                    prop.doc.handle_nextPageTemplate("subsq")
                elif datFld == "CO-Investigators" and datVal[0]:
                    # if last non-Observational data field, break
                    if prop_type not in "Observing":
                        doneThr = True
                    # pgBreak = True
                    if not hide:
                        flds = ["first", "email", "institution", "country"]
                        rname = ["Co-I Name", "Email", "Institute", "Country"]
                    else:
                        continue
                    txt = prs.mk_gen_tab(datVal[0], flds, rname, form=form)
                    par = "tab_center"
                elif datFld == "CO-Investigators" and not datVal[0]:
                    # Sometimes no CoIs
                    # Add space btwn non-table CoI and DDT
                    flows = prop.bld_flowables(flow_type="spac",
                                               flow_list=flows)
                    if prop_type not in "Observing":
                        doneThr = True
                    continue
                elif (datFld == "Is first Co-I responsible for the " +
                      "observation rather than the PI?"):
                    if not hide:
                        flows = prop.bld_flowables(flow_type="spac",
                                                   flow_list=flows)
                    else:
                        continue
                elif datFld == "Observing Co-I Phone":
                    if hide:
                        continue
                elif (datFld == "If PI is not based in USA and proposal has " +
                      "USA CoIs, which Co-I will be the Cost PI?"):
                    if not hide:
                        pgBreak = short_break(targets)
                    else:
                        continue
                elif (datFld == "Justification of Request for Proprietary " + 
                      "Rights"):
                    if isDDT:
                        txt = "{}: {}".format(datFld, datVal[0])
                elif datFld == "Cycle(s), PI name, status":
                    txt = "{}: {}".format(datFld, datVal[0])
                    pgBreak = True
                elif datFld == "Target Summary":
                    flows = title_above(prop, key=datFld,
                                        flows=flows, align="center_sec",
                                        ftype="par", spaceb4=False,
                                        sectitle=True)
                    cycs = prs.get_cycle_list(targets)
                    flds = ["targ_num", "targname", "ra", "dec",
                            "instrument_name", "grating_name",
                            "trigger_target"] + cycs + ["time_critical"]
                    cyc_rname = ["Time (ks)\n Cycle " + re.findall("\d+", xx)[0]
                                 for xx in cycs]
                    rname = ["Targ \nNo.", "Targ. Name", "Ra", "Dec", "Inst",
                             "Grating", "trigger_target"
                             ] + cyc_rname + ["Time \nConstraints"]
                    df = prs.mk_gen_tab(targets, flds, rname, rnd=2,
                                        fill=True, ret_lst=False)
                    # get param only targets using TOO table
                    col_name = "trigger_target"
                    rown, coln = prs.get_inds(df, col=col_name, cond="N")
                    for row in rown:
                        # replace first element of span
                        # First col of left hand span
                        df = prs.replace_val(df, row, "Targ. Name",
                                             val="followup param only")
                        # First col of right hand span
                        df = prs.replace_val(df, row, cyc_rname[0],
                                             val="")

                    df.drop(col_name, axis=1, inplace=True)
                    rown = prs.flat_index(df, rown)
                    style = datVal[1]
                    coln = [1, 3]
                    style = prop.shade_or_span(rown, coln, shade=False,
                                               style=style, hd_rws=1)
                    coln = [6, -1]
                    # for row in rown:
                        # replace first element of span
                        # df = prs.replace_val(df, row, "Grating", val="")
                    style = prop.shade_or_span(rown, coln, shade=False,
                                               style=style, hd_rws=1)

                    # Shade. Call get_inds once for each condition
                    rown, coln = prs.get_inds(df, col="Time \nConstraints",
                                              cond="Y")
                    style = prop.shade_or_span(rown, coln, color="darkgray",
                                               style=style, hd_rws=1)
                    rown, coln = prs.get_inds(df, col="Time \nConstraints",
                                              cond="P")
                    style = prop.shade_or_span(rown, coln, color="lightgrey",
                                               style=style, hd_rws=1)
                    style.add("ALIGN", (0, 1), (0, -1), "RIGHT")
                    style.add("ALIGN", (2, 1), (3, -1), "RIGHT")
                    style.add("ALIGN", (6, 0), (8, -1), "RIGHT")
                    txt = prs.df_to_list(df)
                    txt = prop.par_in_table(txt, sty="center_new")
                    datVal[1] = style
                    par = "tab_center"
                    pgBreak = short_break(targets) if isTOO else True
                elif (datFld == "TOO Target Summary") and isTOO:
                    # df obtained in Req. Time table
                    flows = title_above(prop, key=datFld,
                                        flows=flows, align="center_sec",
                                        ftype="par", spaceb4=False,
                                        sectitle=True)

                    # row span for param only.
                    col_name = "Exposure\nTime"
                    rown, coln = prs.get_inds(dfTOO, col=col_name, cond=0)
                    for row in rown:
                        # replace first element of span
                        dfTOO = prs.replace_val(dfTOO, row, "Type",
                                                val="param only")
                    coln = [1, 10]  # targ_num and Exposure\nTime
                    rown = prs.flat_index(dfTOO, rown)
                    style = datVal[1]
                    style = prop.shade_or_span(rown, coln, shade=False,
                                               style=style, hd_rws=1)

                    # sum over Exposure time for followups.
                    dfTOO = prs.sum_grp_replace(dfTOO, grp="targid",
                                                col_sum="Exp\nTime",
                                                col_repl="Exposure\nTime")
                    # col span for followups
                    if len(dfTOO.index.names) > 1:
                        rown = prs.get_rows_span(dfTOO, ind_grp="targid",
                                            ind_count="Params from\nTarget No.",
                                            verbose=verbose)
                    else:
                        rown = None
                    if rown:
                        # 1 col at a time (col order won't change so hard code)
                        cols = [0, 1, 2, 3, 4, 10]
                        for col in cols:
                            style = prop.shade_or_span(rown, col, shade=False,
                                                       style=style)

                    # col span for Grp
                    # Spanning over Alt Grps wasn't working right because
                    # sometimes the associated targets are not consecutive
                    # Removed the span over Alt Grps altogether for now. 
                    # if not (dfTOO["Grp\nName"][1:] == "").all():  # Skip hdr2
                    #     # Make sure followups have the same Grp as target
                    #     dfTOO = prs.cond_pad(dfTOO, col_fill="Grp\nName",
                    #                   cond_col="Observ\n-ation",
                    #                   cond="    followup")
                    #     rown = prs.get_rows_span(dfTOO, ind_grp="Grp\nName",
                    #                             ind_count="Targ\nNo",
                    #                             verbose=verbose, renum=True)
                    #     dfTOO = prs.replace_col(dfTOO, old="nan", new="",
                    #                             col="Grp\nName", regex=True)
                    #     cols = [11, 12]
                    #     for col in cols:
                    #         style = prop.shade_or_span(rown, col, shade=False,
                    #                                    style=style)
                    # add named top row spans
                    rname = ["Response Window", "Observations", "Alternates"]
                    flds = ["Type", "Observ\n-ation", "Grp\nName"]
                    dfTOO = prs.rename_cols(dfTOO, flds, rname)
                    par = "tab_too"
                    datVal[1] = style
                    dfTOO = prs.float2string(dfTOO, cols=["Targ\nNo",
                                             "Params from\nTarget No.",
                                             "# Req"])
                    txt = prs.df_to_list(dfTOO)
                elif (datFld == "If this TOO is a resubmission of a proposal" +
                        " approved in the previous Cycle, should this TOO " +
                        "be canceled if the previous Cycle TOO is " +
                        "triggered?"):
                    pgBreak = True
                    if not isTOO:
                        continue
                    datVal[0] = "N" if not datVal[0][0].get("too_cancel") else\
                        datVal[0][0].get("too_cancel")
                    txt = "{}: {}".format(datFld, datVal[0])
                    par = "par"
                elif (datFld == "TOO Target Summary") and not isTOO:
                    continue
                elif datFld == "TARGETS":
                    flows = bld_targets(prop, datVal[0], flows, isTOO,
                                        ao_str, rc)
                    # Continue since I build all the flows in bld_Targets
                    continue
                if verbose > 3:
                    print(("\nDEBUG: Flow being processed:  \ndatVal: " +
                           "{}\nflows{} ".format(datVal, flows)))
                flows = prop.bld_flowables(txt, style=datVal[1],
                                           flow_type=par, flow_list=flows,
                                           colw=colw, halign=halign)
                colw = None
                if pgBreak:
                    flows = prop.bld_flowables(flow_type="pg_brk",
                                               flow_list=flows)
                    pgBreak = False  # Reset
            if doneThr:
                break  # Works because conf is OrderedDict

        prop.doc.build(flows)

    return pdf_dict, prop_warns, ao_warns, prop_type.lower()


def bld_targets(prop, targ_data, flows, isTOO, ao, rc):
    """Loop over TARGETS block adding each data field to the flowables list,
       building all the Target specific sections

    Parameters:
    --------------------
    prop: ProposalDoc object
    targ_data: list of dicts for each target
    flows: Previous list of flows
    isTOO: Is the proposal a TOO
    ao: cycle #
    rc: RC dict

    Return:
    --------------------
    flows: list of flowables including Target blocl

    """

    inner_break = False
    outer_break = True
    targetsJson = pkg.resource_filename("pdfGen", "config/targetsDict.json")
    targ_conf = prs.parse_json(targetsJson)
    acs = "ACIS Parameters"
    conspc = "Constraint Specification"
    targrmk = "Target Remarks"
    toospc = "TOO Specifications"
    # Target Summary for all Targets
    for t in targ_data:
        for c in targ_conf:
            if c == acs:
                if not t.get("ccdi0_on"):
                    continue
                else:
                    flows = prop.bld_flowables(flow_type="line",
                                               flow_list=flows)
                    flows = title_above(prop, key=c,
                                        flows=flows, align="center_sec",
                                        ftype="par", spaceb4=False,
                                        sectitle=True)

            if c == toospc:
                if isTOO:
                    flows = prop.bld_flowables(flow_type="line",
                                               flow_list=flows)
                    flows = title_above(prop, key=c,
                                        flows=flows, align="center_sec",
                                        ftype="par", spaceb4=False,
                                        sectitle=True)
                else:
                    break
            elif not c == "Individual Target Details" and not (c ==
                          toospc) and not (c == acs):
                flows = prop.bld_flowables(flow_type="line", flow_list=flows)
            if c == conspc or c == targrmk:
                flows = title_above(prop, key=c,
                                    flows=flows, align="center_sec",
                                    ftype="par", spaceb4=False, sectitle=True)

            blk = prs.get_blk(c, targ_conf, t)
            for datFld, datVal in blk.items():
                colw = None
                halign = None
                par = "par"
                if not datFld.endswith("Dict"):
                    txt = "{}: {}".format(datFld, datVal[0])
                if datFld == "Target No.":
                    flows = title_above(prop, key="Individual Target Details",
                                        flows=flows,
                                        ftype="par", spaceb4=False,
                                        sectitle=True)
                elif datFld == "Target Name":
                    sol = prs.mod_solar_name([t])    
                    txt = "{}: {}".format(datFld, sol.targname[0])
                elif not isTOO and datFld == "Resource Cost":
                    datVal[0] = rc.get(t.get("targid"))
                    if not datVal[0]:
                        # If there's a problem getting RC
                        datVal[0] = [None, None, None]
                    flows = title_above(prop, key=datFld,
                                        flows=flows, align="left_new",
                                        ftype="par", spaceb4=False, ul=False)
                    cyct = prs.mod_cycle([t], ao)
                    flds = prs.get_cycle_list(cyct)
                    rname = ["Cycle " + re.findall("\d+", xx)[0] for xx in flds]
                    txt = [rname,datVal[0]]
                    par = "tab_left"
                elif isTOO and datFld == "Resource Cost":
                    # Don't display RC for TOO
                    continue
                elif datFld == "RA Dec":
                    ra = datFld.split(" ")[0]
                    dec = datFld.split(" ")[1]
                    ra_val = datVal[0][0]
                    dec_val = datVal[0][1]
                    # check for empty ra/dec. 0 is valid though
                    if ra_val is not None and dec_val is not None:
                        ra_val, dec_val = prs.convert_ra_dec((ra_val, dec_val))
                    if isinstance(ra_val, str) and isinstance(dec_val, str):
                        txt = "{:s}: {:s} {:s}:  {:s}\
                        ".format(ra, ra_val, dec, dec_val)
                    else:
                        txt = "{}:  {} {}:  {}".format(
                              ra, datVal[0][0], dec, datVal[0][1])
                elif datFld == "Grid_Dict":
                    flows = title_above(prop, key="Grid",
                                        flows=flows, align="left_new",
                                        ftype="par", ul=False, spaceb4=False)
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal["Grid Name"]
                elif datFld == "Detector_Dict":
                    flows = title_above(prop, key="Instruments",
                                        flows=flows, align="left_new",
                                        ftype="par", ul=False, spaceb4=False)
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal["Detector"]
                elif datFld == "Zero_Dict":
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal["Src/Zero-order count rate (cts/s)"]
                elif datFld == "Offsets_Dict":
                    flows = title_above(prop, key="Offsets",
                                        flows=flows, align="left_new",
                                        ftype="par", ul=False, spaceb4=False)
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal["Ydet"]
                elif datFld == conspc:
                    flows = title_above(prop, key=datFld,
                                        flows=flows, align="center_sec",
                                        ftype="par", spaceb4=False)
                    txt = datVal[0]
                elif datFld == targrmk:
                    if datVal[0]:
                        txt = datVal[0]
                    else:
                        txt = "N/A"
                elif datFld == toospc and not isTOO:
                    break
                elif datFld == "Exposure_Dict":
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal["Exposure Mode"]
                elif datFld == "ACIS_Chips_Dict":
                    # Don't display chip layout value
                    datVal["Chip Layout"][0] = ""

                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal.get('Chip Layout')
                    flows = prop.bld_flowables(txt, style=datVal[1],
                                               flow_type=par, flow_list=flows,
                                               colw=colw)
                    colw = [0.8]
                    flds = ["ccdi0_on", "ccdi1_on", "ccdi2_on",
                            "ccdi3_on", "ccds0_on", "ccds1_on",
                            "ccds2_on", "ccds3_on", "ccds4_on",
                            "ccds5_on"]
                    rname = ["IO", "I1", "I2", "I3", "S0", "S1", "S2",
                             "S3", "S4", "S5"]
                    tabl, _ = prs.mk_acis_tab([t], flds, rname)
                    tabr, shade_dict = prs.mk_acis_tab([t], flds, rname,
                                                       lay=False)
                    styler = "acis"
                    for coln, rown in shade_dict.items():
                        styler = prop.shade_or_span(rown[0], coln,
                                                    color="darkgray",
                                                    style=styler)
                        styler = prop.shade_or_span(rown[1], coln,
                                                    color="lightgrey",
                                                    style=styler)
                    txt, colw, halign = prop.table_cont(data=[tabl, tabr, [""]],
                                                        styles=[styler, styler,
                                                                "table_no_grid"],
                                                        widths=[colw, colw],
                                                        sep='full')
                    par = "cont"
                elif datFld == "Subarray_Dict":
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal["Subarray Type"]
                elif datFld == "Spatial Windows":
                    if datVal[0]:
                        flows = title_above(prop, key=datFld,
                                            flows=flows, align="left_new",
                                            ftype="par", spaceb4=False)
                        flds = ["ordr", "chip", "sample", "start_column",
                                "width", "start_row", "height",
                                "lower_threshold", "pha_range"]
                        rname = ["Order", "Chip ID", "Sampling\nFreq",
                                 "Start\nCol", "Width", "Start\nRow",
                                 "Height", "Lower\nThresh", "Energy\nRange"]
                        txt = prs.mk_gen_tab(datVal[0], flds, rname)
                        style = prop.style_sheets.get(datVal[1])
                        style.add("ALIGN", (0, 1), (-1, -1), "RIGHT")
                        datVal[1] = style
                        par = "tab_left"
                    else:
                        txt = "{}: {}".format(datFld, "N/A")
                        par = "par"
                        datVal[1] = "left_new"
                    inner_break = True
                elif datFld == "Uninterrupted_Dict":
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal["Uninterrupted"]
                elif datFld == "Win/Roll_Dict":
                    txt = []
                    for win_name, win_vals in datVal.items():
                        txt.append(win_name +
                                              ": N/A" if not win_vals[0]
                                              else win_name + ":")
                    # Add empty center cell
                    txt.insert(1, "")
                    txt = [txt]
                    par = "tab_full"

                    # Add win/roll header
                    winVal = datVal.get('Window Constraints')
                    flows = prop.bld_flowables(txt, style=winVal[1],
                                               flow_type=par, flow_list=flows,
                                               colw=colw)
                    for kk, vv in datVal.items():
                        if kk == "Window Constraints":
                            constl = "N/A" if not vv[0] else ""
                            if vv[0]:
                                flds = ["time_constraint", "tstart", "tstop"]
                                rname = ["Time \n Constr.", "Start Time",
                                         "Stop Time"]
                                tabl = prs.mk_gen_tab(vv[0], flds, rname)
                                stylel = prop.style_sheets.get("table")
                                stylel.add("ALIGN", (0, 1), (-1, -1), "RIGHT")
                            else:
                                tabl = [["", ""], ["", ""]]
                                stylel = "table_no_grid"

                        else:
                            constr = "N/A" if not vv[0] else ""
                            if vv[0]:
                                flds = ["roll_constraint", "roll_180", "roll",
                                        "roll_tolerance"]
                                rname = ["Roll \n Constr.", "180?", "Angle",
                                         "Tolerance"]
                                tabr = prs.mk_gen_tab(vv[0], flds, rname)
                                styler = prop.style_sheets.get("table")
                                styler.add("ALIGN", (0, 1), (-1, -1), "RIGHT")
                            else:
                                tabr = [["", ""], ["", ""]]
                                styler = "table_no_grid"
                    if constl.startswith("N/A") and constr.startswith("N/A"):
                        continue
                    else:
                        txt, colw, _ = prop.table_cont(data=[tabl, tabr],
                                                       styles=[stylel, styler],
                                                       sep=[4.93]*2)
                        par = "cont"
                        datVal["Window Constraints"][1] = "table_no_grid_left"
                        datVal = datVal["Window Constraints"]
                elif datFld == "Obs Time":
                    flows = title_above(prop, key=datFld + " (ks)",
                                        flows=flows, align="left_new",
                                        ftype="par", spaceb4=False, ul=False)
                    cyct = prs.mod_cycle([t], ao)
                    flds = prs.get_cycle_list(cyct)
                    rname = ["Cycle " + re.findall("\d+", xx)[0] for xx in flds]
                    txt = prs.mk_gen_tab(cyct, flds, rname, fill=True, sm=flds,
                                         idx_head=True)
                    par = "tab_left"
                elif datFld == "Phase/Group_Dict":
                    # Replace names with N/A if appropriate
                    for phs_name, phs_vals in datVal.items():
                        datVal[phs_name][0] = "N/A" if (phs_vals[0] == "N"
                                                        or not phs_vals[0])\
                                                        else str(phs_vals[0])
                    txt = prs.mk_fld_list(datVal)
                    par = "tab_full"
                    datVal = datVal.get('Phase Dependent')
                    # Add phase info
                    flows = prop.bld_flowables(txt, style=datVal[1],
                                               flow_type=par, flow_list=flows,
                                               colw=colw)
                    # Possibly make phase table
                    if datVal[0] in "N/A":
                        continue
                    else:
                        flds = ["phase_epoch", "phase_period",
                                "phase_start", "phase_start_margin",
                                "phase_end", "phase_end_margin", "phase_unique"]
                        rname = ["Epoch\n(mjd)", "Period\n(days)",
                                 "Min\nPhase", "Min\nErr",
                                 "Max\nPhase", "Max\nErr", "Unique\nPhase"]
                        txt = prs.mk_gen_tab([t], flds, rname, rnd=3)
                        style = prop.style_sheets.get("table")
                        datVal[1] = style
                        par = "tab_left"
                        # flows = prop.bld_flowables(flow_type="spac",
                        #                            flow_list=flows)
                elif datFld == "Mon/Const_Rmrks_Dict":
                    for kk, vv in datVal.items():
                        if kk == "Monitors":
                            mflag = t.get("monitor_flag")
                            constl = "N/A" if not mflag else mflag
                            if isTOO:
                                kk = "TOO Followups"
                                constl = " "
                            alg = "left_new"
                            # if vv[0]:
                            #     alg = "left_new"
                            # else:
                            #     alg = "left"
                            flows = title_above(prop, key=kk,
                                                val=" " + constl,
                                                flows=flows, align=alg,
                                                ftype="par", spaceb4=True,
                                                ul=False)
                            if vv[0]:
                                flds = ["ordr", "req_obs_time", "pre_min_lead",
                                        "pre_max_lead", "targ_num",
                                        "split_interval"]
                                rname = ["Order\nNo", "Exptime\n(ksec)",
                                         "Min\nInterval\n(days)",
                                         "Max\nInterval\n(days)",
                                         "Params\nfrom\nTarget",
                                         "Split\nInterval"]
                                tabl = prs.mk_gen_tab(vv[0], flds, rname,
                                                drp_emp=("Params\nfrom\nTarget",
                                                         "Split\nInterval"),
                                                ret_lst=False, fill=True)
                                if isTOO and "Params\nfrom\nTarget" in tabl:
                                    strcols = ["Params\nfrom\nTarget",
                                               "Order\nNo"]
                                else:
                                    strcols = ["Order\nNo"]
                                tabl = prs.float2string(tabl, cols=strcols)
                                tabl = prs.df_to_list(tabl)
                                stylel = prop.style_sheets.get("table")
                                stylel.add("ALIGN", (0, 1), (-1, -1), "RIGHT")
                            else:
                                tabl = [["", ""], ["", ""]]
                                stylel = "table_no_grid"
                        else:
                            if stylel == "table_no_grid":
                                constr = "N/A" if (vv[0] == "N" or not vv[0]) \
                                         else vv[0]
                                flows = title_above(prop, key=kk,
                                                    val=" " + constr,
                                                    flows=flows,
                                                    align="left_new",
                                                    ftype="par", spaceb4=False,
                                                    ul=False)
                            else:
                                flds = ["constr_in_remarks"]
                                rname = [kk+":"]
                                # tabr = prs.mk_tot_time_tab(flds, rname)
                                tabr = prs.mk_gen_tab([t], flds, rname,
                                                      ret_lst=True)
                                # transpose the elements
                                tabr = [list(ii) for ii in zip(*tabr)]
                                # kludgey way to insert empty rows so tabr is
                                # adjacent to the bottom of the neighboring tab
                                # for ii in range(len(tabl)):
                                #     tabr.insert(0, [None, None])
                                styler = "table_no_grid_cbold"
                    # If no followup/mon table
                    if stylel == "table_no_grid":
                        continue
                    # if too many followups, use single table
                    if len(tabl) <= 26:
                        txt, colw, _ = prop.table_cont(data=[tabl, [""], tabr],
                                                       styles=[stylel,
                                                               styler,
                                                               "table_no_grid"],
                                                       sep="full")
                        # if isTOO:  # Fix col sep for too wide table
                        #     colw = [col * 1.5 for col in colw]
                        par = "cont"
                        datVal["Monitors"][1] = "table_no_grid_left"
                    else:
                        txt = tabl
                        par = "tab_left"
                        datVal["Monitors"][1] = "table_left"
                    datVal = datVal["Monitors"]

                flows = prop.bld_flowables(txt, style=datVal[1],
                                           flow_type=par, flow_list=flows,
                                           colw=colw, halign=halign)

                if inner_break:  # for extra break in a blk
                    # flows = prop.bld_flowables(flow_type="spac",
                    #                           flow_list=flows)
                    # flows = prop.bld_flowables(flow_type="pg_brk",
                    #                           flow_list=flows)
                    inner_break = False
            flows = prop.bld_flowables(flow_type="spac", flow_list=flows)

        # page break after each target
        # changed page break to double line and extra space
        flows = prop.bld_flowables(flow_type="line", flow_list=flows)
        flows = prop.bld_flowables(flow_type="line", flow_list=flows)
        if outer_break:
            flows = prop.bld_flowables(flow_type="spac", flow_list=flows)
            # flows = prop.bld_flowables(flow_type="frm_brk", flow_list=flows)
        else:
            outer_break = True

    return flows


def out_name(lname, prop, path, form="gen"):
    """Get name of output pdf from data in form

    Parameters:
    --------------------
    lname: last name of PI
    prop: Proposal #
    path: Output directory path to save pdf in
    form: if form not gen, change suf to "prop"

    Return:
    --------------------
    pdf: output file name

    """

    # prune non-alpha chars
    lname = "".join([ii for ii in lname if ii.isalpha()])
    # ignore unicode chars and convert from byte back to string.
    lname = lname.encode('ascii', 'ignore').decode()
    lname = lname + "_" if lname else lname
    suf = {"gen": "f", "grant": "prop", "split": "f", "merge": "merged"}
    pdf = "{}_{}{}.pdf".format(prop, lname.lower(), suf[form])

    return os.path.join(path, pdf)


def title_above(prop, key, flows, val="", align="center_sec",
                ftype="par", ul=False, spaceb4=True, w_next=True,
                sectitle=False):
    """Place key above value (text or table)

   Parameters:
   --------------------
   key: Title text
   flows: list of existing flowables
   align: Title alignment
   ftype: Flow Type
   ul: Underline
   spaceb4: Add Spacer before title
   sectitle: Section title, do not follow with :

   Return
   --------------------
   flows: List of flowables with title added above

    """

    if ul:
        # txt = "<u><b>{} </b>{}</u>".format(key, val)
        txt = "{} {}".format(key, val)
    elif sectitle:
        txt = "<b>{}</b> {}".format(key, val)
    else:
        txt = "<b>{}: </b>{}".format(key, val)
        # txt = "{} {}".format(key, val)
    if spaceb4:
        flows = prop.bld_flowables(flow_type="spac",
                                   flow_list=flows)
    flows = prop.bld_flowables(txt, w_next, style=align,
                               flow_type="par",
                               flow_list=flows)

    return flows


def save_pdf(pdf, sup, vrb):
    """Save supporting PDFs in separate files. As of cycle 23, cv replaced by
       te in CPS. Files still saved in archive as CV though, so rename them te
       when saving anew.

    Parameters:
    --------------------
    pdf: Path to input pdf
    sup: List of paths to CV, SJ, PC
    vrb: verbosity level

    """

    ext = "unknown.pdf"
    for ss in sup:
        if "_cv.pdf" in ss:
            ext = "te.pdf"  # cv -> te
        elif "_pc.pdf" in ss:
            ext = "pc.pdf"
        elif "_sj.pdf" in ss:
            ext = "sj.pdf"
            
        df = pdf.replace("f.pdf", ext)
        if vrb > 1:
            print("Saving {}".format(df))
        os.rename(ss, df)


def merge_pdf(pdf, sup, vrb, pg_lim, pg=2):
    """Append sup data to generated PDF

    Parameters:
    --------------------
    pdf: Path to input pdf
    sup: List of paths to CV, SJ, PC
    vrb: verbosity level
    pg_lim: Page limit to warn on for SJ
    pg: Page number to merge SJ on

    """

    prop_num = os.path.basename(pdf).split('_')[0]
    lim_out = os.path.join(os.path.dirname(pdf), "pg_lim.txt")
    pf = open(pdf, 'rb')
    pdff = PdfFileReader(pf, 'rb')
    cvf = pcf = sjf = None
    try:
        for ss in sup:
            if "_cv.pdf" in ss:
                cf = open(ss, 'rb')
                cvf = PdfFileReader(cf, 'rb')
                cv_pg_num = cvf.getNumPages()
            elif "_pc.pdf" in ss:
                pf = open(ss, 'rb')
                pcf = PdfFileReader(pf, 'rb')
            else:
                sf = open(ss, 'rb')
                sjf = PdfFileReader(sf, 'rb')
                sj_pg_num = sjf.getNumPages()
    except IOError:
        # Will they all be in the same CPS dir? Change if not
        rdir = os.path.dirname(ss)
        err = "User does not have read permission in {}.".format(rdir) + \
              " Skip merge"
        warnings.warn(err)
        return

    merger = PdfFileMerger(strict=False)

    # add the entire base pdf
    merger.append(pdff)

    # add the SJ after pg
    if sjf:
        # write page limit info to file
        save_pg_info('SJ', prop_num, lim_out, sj_pg_num, pg_lim)
        merger.merge(position=pg, fileobj=sjf)
        sf.close()
    elif vrb > 0:
        print("No SJ data associated with {}".format(pdf))

    # append PC then CV
    if pcf:
        merger.append(pcf)
        pf.close()
    elif vrb > 0:
        print("No PC data associated with {}".format(pdf))
    if cvf:
        save_pg_info('CV', prop_num, lim_out, cv_pg_num)  # page limit 4 for cv
        merger.append(cvf)
        cf.close()
    elif vrb > 0:
        print("No CV data associated with {}".format(pdf))

    tmp = os.path.join(os.path.dirname(pdf), "tmp.pdf")
    out_pdf = open(tmp, "wb")
    merger.write(out_pdf)

    os.rename(tmp, pdf)
    out_pdf.close()
    pf.close()


def save_pg_info(sup_type, prop_num, out, pg_num, pg_lim=4):
    """Write page limit info to file

    Parameters:
    --------------------
    sup_type: Supplemental file type: CV or SJ
    prop_num: Proposal #
    out: Path to output file
    pg_num: Number of pages in pdf
    pg_lim: Max # of pages for proposal

    """

    wrn = " *EXCEEDS LIMIT!*" if pg_num > pg_lim else ""
    msg = "{} {}:\n\t# of pages = {}{}.\n".format(prop_num, sup_type, pg_num,
                                                  wrn)
    with open(out, 'a+') as f:
        f.write(msg)


def str_title(val):
    """Safer method to check if a value is a string and make it upper case
       if so.
    
    Parameters:
    --------------------
    val: Input value to make upper case if string.
    
    Return
    --------------------

    """    
    
    return val.title() if isinstance(val, str) else val


def short_break(targs, thresh=16):
    """ Only pgBreak if < thresh targets.

        If table is about length of page an extra pgBreak is auto-added.
        Max rows depends on default items on page (e.g. header, footer) and
        font size, but is ~ 20-25. Strings in a cell that get wrapped to the
        following line will decrease that number. To avoid double pgBreak in
        long tables, only add this break if "many" targets.
    
    Parameters:
    targs: List of target dicts
    thresh: break if num targets < thresh
    --------------------
    Return
    pgBreak: Whether to add page break

    """

    pgBreak = True if len(targs) < thresh else False
    
    return pgBreak
