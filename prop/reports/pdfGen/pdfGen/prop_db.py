#_PYTHON_INSERT_SAO_COPYRIGHT_HERE_(2018-2020)_
import sybpydb
import sys
import os
from subprocess import Popen, PIPE


# -----------------------------------------------------------
def getlobdata(lob):
    """ process large object data from database  """
    outarr = bytearray()
    chunk = bytearray(1024)
    while True:
        try:
            len = lob.readinto(chunk)
            # print len
            if (len == None):
                break
            outarr.extend(chunk[:len])
        except sybpydb.ProgrammingError:
            break
    return outarr

# -----------------------------------------------------------
def douploadquery(cur,stmt,pno,type):
    """ retrieve PDF blob from database
        return 1 field as bytearray  """
    cur.execute(stmt,[pno,type])
    row = cur.fetchone()
    if (row == None):
        return None

    column_names = [desc[0] for desc in cur.description]
    for col in range (len(row)):
        # print row[col]
        arr1 = getlobdata(row[col])
    return arr1

# -----------------------------------------------------------
def dosubquery(tcur,stmt,myid):
    """ retrieve supporting proposal table entries from database
        return python dictionary  of column name:value   """
    myarr = []
    tcur.execute(stmt,[myid])
    rows = tcur.fetchall()
    tcolumn_names = [desc[0] for desc in tcur.description]
    for row in rows:
        mydict = {}
        for col in range (len(row)):
            mydict[tcolumn_names[col]] = row[col]
            #print "%s=%s" % (tcolumn_names[col],row[col])
        myarr.append(mydict)
    return myarr


# -----------------------------------------------------------
def getProposalsByCycle(user,pwd,srvr,ao,vrb=0):
    """ get ALL proposals for specified cycle
        return list of proposal numbers  """

    conn = sybConErrs(vrb, conf=sybpydb.connect, user=user, password=pwd,
                      servername=srvr)
    cur = conn.cursor()
    stmt = "select proposal_number from proposal where status not in ('INCOMPLETE','WITHDRAWN','HOLD') and ao_str = ?"
    cur.execute("use proposal")
    cur.execute(stmt,[ao])
    rows = cur.fetchall()
    pnoList = []
    for row in rows:
        pnoList += [row[0]]
    return pnoList

# -----------------------------------------------------------
def getProposalData(user,pwd,srvr,proposalNumber,vrb=0):
    """ get ALL proposal data for cycle except the associated files
        return dictionary of proposal data  """

    conn = sybConErrs(vrb, conf=sybpydb.connect, user=user, password=pwd,
                      servername=srvr)
    cur = conn.cursor()

    # basic proposal data
    stmt = "select proposal_id, proposal_number, submission_date,  title, category_descrip 'subject category', type, request_extra_flag 'proprietary_rights', coi_contact, coi_phone, total_time 'time_or_budget', num_targets, ao_str, joint, multi_cycle, linked_proposal, linked_propnum, science_keywords, cost_pi_coin, first 'pi_first', 'pi_middle',last 'pi_last', email 'pi_email', phone, department, institution, street, mailstop, city, state, zip, country,orcid, hst_time, hst_instruments, noao_time, noao_instruments,nrao_time, nrao_instruments, xmm_time, swift_time, nustar_time, submitter_id, (select sum(cnt) from view_too_trigger_cnt vtc where vtc.proposal_id =view_proposal_cps.proposal_id ) trigger_cnt, abstract  from view_proposal_cps where proposal_number = ?"

    # all Co-investigator data
    coistmt = "select proposal_number,coin_number,ps.last,ps.first,ps.email,ps.institution,ps.country from axafusers..person_short ps,proposal vpc,coin where vpc.proposal_id = ?  and vpc.proposal_id = coin.proposal_id and coin.pers_id =   ps.pers_id order by coin_number"

    # all basic Target data for the proposal
    tgtstmt = "select targid, targ_num, targname, targ_position_flag, ss_object, photometry_flag, vmagnitude, num_observations, time_critical, coord_epoch, ra, dec, est_cnt_rate, forder_cnt_rate, y_det_offset, z_det_offset, pointing_constraint, raster_scan, prop_exposure_time, est_time_cycle_n0, est_time_cycle_n1, est_time_cycle_n2, uninterrupt, monitor_flag, proposal_id,type,proposal_number, grating_name, instrument_name, total_fld_cnt_rate, extended_src, sim_trans_offset, description, multitelescope, observatories,multitelescope_interval, constr_in_remarks, group_obs,group_id,group_interval, atg_group_name, atg_req_count, exp_mode, bep_pack, ccdi0_on, ccdi1_on, ccdi2_on, ccdi3_on, ccds0_on, ccds1_on, ccds2_on, ccds3_on, ccds4_on, ccds5_on, most_efficient,frame_time, subarray, subarray_start_row, subarray_row_count, subarray_frame_time, duty_cycle, secondary_exp_count, primary_exp_time, eventfilter, eventfilter_lower, eventfilter_range, spwindow, spectra_max_count,multiple_spectral_lines, chip_confirm, hrc_zero_block,timing_mode, phase_constraint_flag, phase_period, phase_epoch, phase_start, phase_end, phase_start_margin, phase_end_margin, phase_unique, tootype, split_interval, start, stop, followup,time, probability ,response_time, trigger_target, too_cancel, grid_name, grid_num_pointings, grid_max_radius, remarks,trig,tooremarks from view_target_cps where proposal_id = ? order by targ_num"

    # ddt proposal data
    ddtstmt = "select contact_info, rights_justification , target_justification, response_time , xmm_ddt ,xmm_status, prev_request, prev_cycles , next_cfp, transient_behavior, response_justification from ddt_proposal where proposal_id=?"

    # roll, window, monitor/followup and acis window entries for a target
    rollstmt = "select targid, ordr, roll_constraint, roll_180, roll, roll_tolerance    from rollreq where targid=? order by ordr"

    windowstmt = "select targid, ordr, time_constraint, tstart, tstop from timereq where targid=? order by ordr"

    obsstmt = "select targid, ordr, obs_time, pre_min_lead, pre_max_lead, split_interval, status, targ_num, charge_ao_str, req_obs_time from observation where targid=? order by ordr"

    aciswinstmt = "select aciswin.aciswin_id, chip, include_flag, start_row, start_column, width, height, lower_threshold, pha_range, sample, ordr from target_aciswin, aciswin where targid = ?  and target_aciswin.aciswin_id = aciswin.aciswin_id order by chip, ordr"

    # get a single proposal at a time
    cur.execute("use proposal")
    cur.execute(stmt,[proposalNumber])
    row = cur.fetchone()
    if not row:  # bad proposal / is empty
        return None
    tdict = {}

    column_names = []
    column_names = [desc[0] for desc in cur.description]
    for col in range (len(row)):
        if column_names[col] == "abstract":
            arr1= getlobdata(row[col])
            tdict[column_names[col]] = arr1
            #print "%s=%s" % (column_names[col],arr1)
        else:
            #print "%s=%s" % (column_names[col],row[col])
            tdict[column_names[col]] = row[col]

    pid = row[0]  # proposal id

    # Get DDT data into tdict
    cur.execute(ddtstmt,[pid])
    row = cur.fetchone()
    if row:
        column_names = [desc[0] for desc in cur.description]
        ddt_lobs = ["next_cfp", "transient_behavior", "response_justification"]
        for col in range (len(row)):
            cname = column_names[col]
            if cname in ddt_lobs:
                arr1 = getlobdata(row[col])
                tdict[column_names[col]] = arr1
                #print "%s=%s" % (column_names[col],arr1)
            else:
                #print "%s=%s" % (column_names[col],row[col])
                tdict[column_names[col]] = row[col]

    coiarr = dosubquery(cur,coistmt,pid)
    key = "COI"  # _ + str(pid) #don'think I need this
    tdict[key] = coiarr

    #  get  the Targets
    tgtmatrix = []
    tidmatrix = []

    cur.execute(tgtstmt,[pid])
    column_names = [desc[0] for desc in cur.description]
    while True:
        row = cur.fetchone()
        if row == None:
            break
        else:
            tid = row[0]
        tidmatrix.append(tid)
        tgtdict = {}
        for col in range (len(row)):
            cname = column_names[col]
            if cname == "remarks" or cname=="tooremarks" or cname=="trig" :
                  arr1=getlobdata(row[col])
                  tgtdict[column_names[col]] = arr1
                  #print "%s=%s" % (column_names[col],arr1)
            else:
                  tgtdict[column_names[col]] = row[col]
                  #print "%s=%s" % (column_names[col],row[col])
        tgtmatrix.append(tgtdict)

    key = "TARGETS"
    tdict["TARGETS"] = tgtmatrix

    for ind, t in enumerate(tgtmatrix):
        tid = t.get("targid")
        key = "OBS"
        myarr = dosubquery(cur,obsstmt,tid)
        tgtmatrix[ind][key] = myarr
        key = "ROLL"
        myarr= dosubquery(cur,rollstmt,tid)
        tgtmatrix[ind][key] = myarr
        key = "WINDOW"
        myarr= dosubquery(cur,windowstmt,tid)
        tgtmatrix[ind][key] = myarr
        key = "ACISWIN"
        myarr= dosubquery(cur,aciswinstmt,tid)
        tgtmatrix[ind][key] = myarr

    cur.close()
    conn.close()

    return tdict


def sybConErrs(vrb, conf=sybpydb.connect, **kwargs):
    """Process user/pw/srvr errors when connecting to database

    Parameters
    ------------
    vrb: Verbosity level
    conf: Connection function
    kwargs: kwargs to pass to connection function
    Returns
    -----------
    conn: Connection to database

    """
    try:
        conn = conf(**kwargs)
    except sybpydb.ProgrammingError:
        errMsg = "Invalid user name or password. PDF Generation halted.\n"
        if vrb > 1:
            print(errMsg)
            raise
        else:
            sys.exit(errMsg)
    except sybpydb.DatabaseError:
        errMsg = "Server name not found. PDF Generation halted.\n"
        if vrb > 1:
            print(errMsg)
            raise
        else:
            sys.exit(errMsg)
    except Exception:
        raise

    return conn

def ex_alt_time(propid, user, pwd, srvr, vrb):
    """Call exclude_alt_time_mcop stored procedure to get list of alt times to
        exclude for each cycle. Should only call for TOO proposals

    Parameters
    ------------
    propid: proposal #
    user: db user name
    pwd: User's password
    srvr: Name of server
    vrb: verbosity level

    Return
    ------------
    atime: list of excluded alternate times for each cycle

    """
    conn = sybConErrs(vrb, conf=sybpydb.connect, user=user, password=pwd,
                      servername=srvr)
    cur = conn.cursor()
    conn.commit()
    alt_out = sybpydb.OutParam(float())
    cur.execute("set chained off")
    atimes = cur.callproc('proposal..exclude_alt_time_mcop', (propid, alt_out))
    cont = True
    while cont:
        get_times = cur.fetchone()
        cont = cur.nextset()
    atimes = get_times
    return atimes


def prop_resources(propid, user, pwd, srvr, verbose, out_log):
    """
    Run prop_resources.pl to get RC score. 
    Note: Validation of the input args not performed here since the database
          connections are already vetted in genPDF.py before this function
          is called

    Parameters
    ------------
    propid: proposal #
    user: db user name
    pwd: User's password
    srvr: Name of server
    verbose: verbosity level
    out_log: Directory for log outputs

    Return
    ------------
    rc: String of form 
        {rows: [{'data': [prop_num, targnum, targname, RC], 'id': prop_id }]}
        obtained from prop_resources.pl

    """

    my_env = os.environ.copy()
    asc_bin = my_env.get("ASCDS_BIN")
    if "sqldev" in srvr:
        #  sqldev doesn't have a default log space.
        prop_logs = out_log
    else:
        prop_logs = my_env.get("ASCDS_PROP_LOGS")
    cmd = "{}/prop_resources.pl -U {} -S {} -p {} -t {}".format(asc_bin, user,
                                                                srvr,
                                                                propid,
                                                                prop_logs)
    pp = Popen([cmd], shell=True, stdin=PIPE, stdout=PIPE, stderr=PIPE,
               universal_newlines=True, env=my_env)
    
    # input password
    pp.stdin.write(pwd)
    pp.stdin.flush()
    sout, serr = pp.communicate()
    if verbose > 1:
        print("Output from prop_resources.pl: {} {} ", sout, serr)
    rc = sout

    return rc
