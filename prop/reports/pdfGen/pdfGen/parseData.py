#_PYTHON_INSERT_SAO_COPYRIGHT_HERE_(2018-2022)_
"""Includes several helper functions for parsing, manipulating, and organizing
   the data to suit reportlab.
"""

import os
import re
import json
import warnings
import html
import pandas as pd
import numpy as np
from tempfile import NamedTemporaryFile
from pdfGen.format import deg2dec, deg2ra
from subprocess import Popen, PIPE
from collections import OrderedDict
from pdfGen.prop_db import getProposalsByCycle, getProposalData, ex_alt_time,\
                           prop_resources

pd.options.mode.chained_assignment = None


def parse_json(infile):
    """Reads in config file for arranging each block

    Parameters
    ------------
    infile: Path to input json file

    Returns
    -----------
    dat: Nested dict

    """

    dat = json.load(open(infile), object_pairs_hook=OrderedDict)

    return dat


def get_name(data, form):
    """Combine first, middle, last name.
       Doesn't delve into nested dicts. Should it?

    Parameters
    ------------
    data: Data dict
    form: First name initial if "inits"

    Returns
    ------------
    first, mid, last: First, Middle, Last names

    """

    first = data.get("pi_first")
    if not first:
        first = "n"
    if "grant" not in form:
        first = first[0] + "."
    mid = data.get("pi_middle")
    last = data.get("pi_last")
    last = "unknown" if not last else last

    return first, mid, last


def mk_tot_time_tab(val_dict, rname, drop_rows=None):
    """Make Total Time and Budget Table

    Parameters
    ------------
    val_dict: dictionary to make right table
    drop_rows: list of rows to drop
    rname: dict of {oldName: newName}

    Return
    ----------
    tab: Formatted time/budget table

    """

    if drop_rows is None:
        drop_rows = []
    tab = pd.DataFrame(val_dict).drop(drop_rows).transpose()
    tab.reset_index(level=0, inplace=True)
    tab['index'][0] = rname

    return tab.values.tolist()


def mk_req_summ_tab(totals, mcop, rcs):
    """Combine the old total requested time, rc, and multi-cycle time tables.
       Format should look like:
       |          | Cycle N | Cycle N+1 | Cycle N+2| totals
       | Req Time |    t    |    t1     |    t2    |  t sum    |
       |  RC      |    RC   |    RC1    |    RC2   |  RC sum   |
       | # Targs  |    N    |    -      |     -    |    -      |

    Parameters
    ------------
    totals: Reportlab-formatted total time/targets table from mk_tot_time_tab
    mcop: Reportlab-formatted multi-cycle exposure time table 
    rcs: List of total RC for cycle N, N+1, N+2
    
    Return
    ------------
    tab: Formatted Requested Summary table

    """

    tab = mcop
    # add header col
    tab[0].append('Totals')
    tab[0].insert(0, (' '))
    
    # totals
    tab.append(totals[1] + ['-']+['-']+['-'])  # -> insert total targets

    # RC or TOO
    if rcs:
        tab.append(rcs)
    else:
        tab.append(totals[-1] + ['-']+['-']+['-'])  # -> insert TOO triggers
    
    # append/prepend to the original exposure mcop 
    tab[1].append(totals[0][1])  # -> insert "req time (ks)
    tab[1].insert(0, totals[0][0])  # -> insert total time val

    return tab


def mk_jointrq_tab(all_data, obs):
    """Make formatted Joint Requests Table. Pulls appropriate cols from
       all_data and orders them.

    Parameters
    ------------
    all_data: Input list of dicts
    obs: String of observatories

    Return
    ------------
    tab: The formatted joint req  table

    """

    # Get rid of obs that have no obs_time field
    obs = obs.replace("XMM-c", "XMM").replace("HST-c", "HST").split("+")
    obs_units = {"hst": "orbits", "noao": "nights", "nrao": "hours",
                 "nustar": "ksec", "swift": "ksec", "xmm": "ksec"}
    tab = [["Observatory", "Request", "Units", "Telescope / Instruments"]]

    for ob in obs:
        try:
            inst = all_data.get(ob.lower() + "_instruments")
            tm = all_data.get(ob.lower() + "_time")
            unit = obs_units[ob.lower()]
        except KeyError:
            inst = "N/A"
            tm = ""
            unit = ""
        if not inst:
            inst = "N/A"
        tab.append([ob, tm, unit, inst])
    return tab


def mk_gen_tab(data, flds, rname, rnd=None, fill=False, sm=None, ret_lst=True,
               drp_emp=None, atime=0.0, form="gen", **kwargs):
    """
    Make a formatted general Table.

    Parameters
    ------------
    data: list of data dicts
    flds: Ordered list of names of data fields to include
    rname: Corresponding names for table header
    rnd: # of digits to round to if not False
    fill: Fill None/NaN with value 0 if not False
    sm: List of row names to sum over if not False
    ret_lst: If true return list of lists, else the DataFrame
    drp_emp: List of cols to drop if col in table is empty
    atime: exclude alternate time
    form: output form type
    **kwargs: kwargs for  df_to_list

    Return
    ------------
    tab: The formatted table if ret_list, else the DataFrame

    """

    if sm is None:
        sm = []
    assert len(flds) == len(rname), ("{} and {} must be the same length".format
                                     (flds, rname))
    tab = pd.DataFrame(data)
    if sm:
        if not atime == 0.0 and "Cycle" in rname[0]:
            # Can't round Series it's dtype is object, convert to float.
            tab = (tab[sm].sum() - atime).astype(dtype=float).round(decimals=2)
        else:
            tab = (tab[sm].sum()).astype(dtype=float).round(decimals=2)
    if "tstart" in tab:
        tab.tstart = tab.tstart.dt.strftime("%Y-%m-%d-%X")
    if "tstop" in tab:
        tab.tstop = tab.tstop.dt.strftime("%Y-%m-%d-%X")
    if "first" in tab and "last" in tab:
        if "grant" not in form:
            tab["first"] = tab["first"].astype(str).str[0] + ". " + tab["last"]
        else:
            tab["first"] = tab["first"] + " " + tab["last"]
    if kwargs.get("idx_head"):
        tab.rename(index=list2dict(flds, rname), inplace=True)
    else:
        tab = rename_cols(tab, flds, rname)
    tab = tab[rname]
    if rnd:
        tab = tab.round(rnd)
    if drp_emp:
        for drp in drp_emp:
            if tab[drp].dropna(how="all").empty:
                tab.drop(drp, axis=1, inplace=True)
    if fill:
        if 'Grating' in tab:
            drpCols = ['Grating']
            kpCols = tab.drop(drpCols, axis=1)
            tab[kpCols.columns] = kpCols.fillna("")
            tab[drpCols] = tab[drpCols].fillna("None")
        elif 'Split\nInterval' in tab:
            tab.fillna("", inplace=True)
        else:
            tab.fillna(0, inplace=True)
    if ret_lst:
        tab = df_to_list(tab, kwargs.get("idx_head"))

    return tab


def mk_too_tab(targs, flds, rname):
    """Create formatted TOO table

    Parameters
    ------------
    targs: data for all targets
    flds: Ordered list of names of data fields to include
    rname: Corresponding names for table header

    Return
    ------------
    tab: Formatted TOO DataFrame

    """

    tab = pd.DataFrame(targs, copy=True)
    # Get all the OBS dicts (multi-index), convert to DataFrame, add to tab
    obs = pd.concat([pd.DataFrame(ob) for ob in tab.OBS])
    tab.set_index("targid", inplace=True)
    if not obs.empty:
        obs.rename(columns={"targ_num": "params"}, inplace=True)
        obs.index += 1
        obs.index.name = "obs"
        obs.reset_index(inplace=True)
        obs.set_index(["targid", "obs"], inplace=True)
        tab["obs"] = 0
        tab.set_index(["obs"], append=True, inplace=True)
        tab.drop("split_interval", axis=1, inplace=True)
        tab = tab.join(obs, how="outer")
        # Better way than resetting the multiindex in place?
        tab.reset_index(level=1, inplace=True)
        tab['obs'] = range(1, len(tab['obs']) + 1)
        tab.set_index(["obs"], append=True, inplace=True)
    # Add obs col names to tab in case obs is empty
    for fld in flds:
        if fld not in tab:
            tab[fld] = None
    # Modify Observation (status)
    trig_mask = (tab.trigger_target == "Y") & (tab.atg_group_name.notnull())
    tab.loc[trig_mask, "status"] = "alt trigger"

    trig_mask = (tab.trigger_target == "Y") & (tab.atg_group_name.isnull())
    tab.loc[trig_mask, "status"] = "trigger"

    trig_mask = (tab.trigger_target != "Y")
    tab.dropna(how="all", inplace=True)
    if not obs.empty:
        tab.loc[trig_mask, "status"] = "    followup "
        # append obs to followup
        tab.loc[trig_mask, "status"] = tab.status.str.cat(
                                       tab.ordr.astype(str).str.replace(".0",
                                       "", regex=False))
        tab.loc[tab["status"].str.endswith("nan"), "status"] = ""
    # Replace req_obs_time with time for primary targets
    tab.req_obs_time.where(~tab.req_obs_time.isna(), tab.time, inplace=True)
    tab = rename_cols(tab, flds, rname)
    tab = tab[rname]
    tab.fillna("", inplace=True)
    # add empty row below col names
    if isinstance(tab.index, pd.MultiIndex):
        tab.loc[(0, 0), :] = tab.columns
        # Get right exposure time for Exp\nTime col for Multiindex
        # Replace ExpTime when there's no followup with prop_exp_time
        cts = tab.reset_index(level=1).groupby("targid").count()['obs']
        cts = tab.join(cts)
        cts.rename(columns={'obs': 'cts'}, inplace=True)
        cts = tab['Exp\nTime'].where(cts.cts != 1, tab["Exposure\nTime"])
        # Fix 2nd head col that gets overwritten
        cts[(0, 0)] = 'Exp\nTime'
        tab["Exp\nTime"] = cts
    else:
        tab.loc[0] = tab.columns
        tab['Exp\nTime'] = tab['Exposure\nTime']
    tab.sort_index(level=0, inplace=True)

    return tab


def mk_acis_tab(targs, flds, rname, lay=True):
    """Make ACIS chips tables

    Parameters
    ------------
    targs: Individual target data
    flds: Ordered list of names of data fields to include
    rname: Corresponding names for table header
    lay: True for layout, False for selection

    Return
    ------------
    rtab: Formatted chip table

    """

    tab = pd.DataFrame(targs)
    tab = rename_cols(tab, flds, rname)

    # Sometimes s* chips is None instead of N. Replace with N
    tab.replace(np.nan, "N", inplace=True)
    tab = tab[rname]
    shade_dict = {}
    if lay:
        chips = list(tab)
    else:
        chips = tab.loc[0].tolist()

    top = [["", "", chips[0], chips[1], "", ""]]
    mid = [["", "", chips[2], chips[3], "", ""]]
    bot = [[chips[4], chips[5], chips[6], chips[7], chips[8], chips[9]]]
    rtab = top + mid + bot

    # Convert back to DataFrame to make it easier to get indices for cell bkg
    df = pd.DataFrame(rtab)
    for col in df.columns:
        r1, c1 = get_inds(df, col, "Y")
        r2, c1 = get_inds(df, col, "O")
        if r1 or r2:
            shade_dict[c1] = [r1] + [r2]
    return rtab, shade_dict


def list2dict(keys, vals):
    """Convert two lists into a dict

    Parameters
    ------------
    keys: list of keys
    vals: list of vals

    Return
    ------------
    d: dict(keys:vals)
    """
    dic = {kk: vv for kk, vv in zip(keys, vals)}
    return dic


def get_cycle_list(targ):
    """
    Make list of est_time_cycle_*

    Parameters
    ------------
    targ: Individual target dict

    Return
    ------------
    cyc_list: List of est_time_cycle_*
    """

    tab = pd.DataFrame(targ)
    cyc_list = [xx for xx in list(tab) if xx.startswith("est_time_cycle_")]

    return cyc_list


def mod_cycle(targs, ao):
    """Change est_time_cycle_n* to est_time_cycle_n*+ao
       Add est_time_cycle_nao

        Parameters
        ------------
        targs:  targets dict
        ao: cycle #

        Return
        ------------
        tab: modified targets dict

    """

    ao = int(ao)
    if isinstance(targs, dict):
        targs = [targs]
    fld = get_cycle_list(targs)
    tab = pd.DataFrame(targs)

    # added est_time_cycle0 to prop_db so don't need this anymore
    # # add col
    # newCyc = "est_time_cycle_" + str(ao)
    # newCycTime = tab.prop_exposure_time.values - tab[fld[0]] - tab[fld[1]]
    # if newCyc not in tab.columns.tolist():
    #     tab.insert(0, newCyc, newCycTime)

    # change  name of  cols in df
    rnam = ["est_time_cycle_" + str(ao + int(re.findall("\d+", xx)[0]))
            for xx in fld]
    tab = rename_cols(tab, fld, rnam)

    return tab


def mod_solar_name(targs):
    """Modify name of target to solar system object name if target has no name

        Parameters
        ------------
        targs:  DataFrame


        Return
        ------------
        targs: modified targets dict

    """

    if not isinstance(targs, pd.DataFrame):
        targs = pd.DataFrame(targs)

    # only replace empty str targname if any ss_object not null
    if targs.ss_object.notnull().any():
        targs.targname.replace({'': None}, inplace=True)
    mask = targs.targname.isnull()
    targs.loc[mask, "targname"] = targs.loc[mask, "ss_object"]

    return targs


def get_blk(blk_name, config, data, form='gen', verbose=0):
    """Replace all the primary vals for an individual block from the config
       file with the appropriate values from the data while keeping structure
       of config.


    Parameters
    ------------
    blk_name: Name of block to get
    config: Configuration dict
    data: dict of all the data
    form: Proposal format
    verbose: verbosity level

    Returns
    -----------
    blk: OrderedDict of blocks with each key in the block being a list of
        [data_value, alignment]

    """

    blk = OrderedDict()
    for kc, vc in config[blk_name].items():
        if isinstance(vc, dict):  # for side-by-side tables in config
            blk[kc] = OrderedDict()
            for ki, vi in vc.items():
                blk[kc][ki] = [data[vi[0]], vi[1]]
        elif isinstance(vc, list):
            if isinstance(vc[0], list):
                # for fields like [ra, dec], align
                blk[kc] = [(data[vc[0][0]], data[vc[0][1]]), vc[1]]
            elif vc[0] == "pi_first":
                name = get_name(data, form)
                name = " ".join(filter(None, name))
                blk[kc] = [name, vc[1]]
            else:
                try:
                    blk[kc] = [data[vc[0]], vc[1]]
                except KeyError as err:
                    if verbose > 1:
                        warnings.warn(("KeyError: {}.".format(err) +
                                   " Skipping DDT keys for non-DDT proposals."))
        elif isinstance(data[vc[0]], list):  # For fields that are list(dict())
            blk[kc] = [data[vc[0]], vc[1]]

    return blk


def combine_props(ao_list=None, props=None, **kwargs):
    """Gets all proposals by cycle and combines with individual proposal list
    Parameters:
    -----------------
    usr: db user name
    pw: User's password
    ao_list: List of Cycle #'s. If None, use props.
    props: List of proposal #'s.
    srvr: Name of server

    Return:
    -----------------
    props: Extended set of proposals
    warns: list of ao's missing

    """

    if props is None:
        props = []
    if ao_list is None:
        ao_list = []
    clist = []
    warns = []
    if ao_list:
        for ao in ao_list:
            ao_props = getProposalsByCycle(ao=ao, **kwargs)
            if ao_props:
                for kk in ao_props:
                    clist.append(str(kk))
            else:
                err = "Cycle {} has no proposals or is not in ".format(ao) + \
                      "database. Skipping this cycle."
                warnings.warn(err)
                warns.append(ao)
        props.extend(clist)

    return set(props), warns


def query_DB(prop, **kwargs):
    """Get data for individual proposal from the database.

    Parameters:
    -----------------
    prop: Individual proposal #'s.
    **kwargs: verbosity kwarg

    Return:
    -----------------
    data: dict containing data for individual proposal.

    """

    data = getProposalData(proposalNumber=str(prop), **kwargs)
    try:
        data = to_ascii(data)
    except AttributeError:  # Bad proposal num
        return None

    return data


def getSup(usr, pw, prop, odir, verbose=0):
    """Get supplementary PDF files (CV, PC, SJ) from the archive server for
        an individual proposal. Use subprocess to run arc4gl on the command
        in a new process.
        arc4gl -U usr -i cmdF
    Parameters:
    -----------------
    usr: User name for arc4gl
    pw: password for arc4gl
    cmdsPth: path to command arguments for arc4gl
    Return:
    -----------------
    supList: List of paths to [CV, PC, SJ]
    """

    hm = os.getcwd()
    os.chdir(odir)  # arc4gl outputs to pwd
    cmdF = genCmds(prop)
    cmd = "arc4gl -U {} -i {} -v".format(usr, cmdF.name)
    my_env = os.environ.copy()  # not sure I needs this but just in case
    p = Popen([cmd], shell=True, stdin=PIPE, stdout=PIPE, stderr=PIPE,
              universal_newlines=True, env=my_env)

    # include the password
    p.stdin.write(pw)
    p.stdin.flush()
    sout, serr = p.communicate()
    cmdF.close()
    os.chdir(hm)

    if serr:
        err = "Something went wrong in accessing arc4gl for" +\
            " proposal # {}: {}".format(prop, serr)
        warnings.warn(err)
        return []
    if "0 file(s)" in sout:
        if verbose > 1:
            err = "No supplementary files retrieved from archive" + \
                " for proposal #: {}".format(prop)
            warnings.warn(err)
        return []

    # look for strings that end in cv, pc, sj
    supList = [os.path.join(odir, xx) for xx in sout.split("\n")
               if (("_sj.pdf" in xx) or ("_cv.pdf" in xx) or ("_pc.pdf" in xx))]
    # # make sure they're in the order cv, pc, sj
    # supList = sorted(supList, key=lambda x: x[-6])  # sort on c, p s

    return supList


def genCmds(prop):
    """Create a tempfile containing the commands file to feed to arc4gl
    Parameters:
    -----------------
    prop: proposal number to get
    Return:
    -----------------
        f: tempfile containing the commands
    """

    f = NamedTemporaryFile(mode='w')
    f.write("operation=retrieve\n")
    f.write("dataset=proposal\n")
    f.write("detector=supporting_files\n")
    f.write("propnum={}\n".format(prop))
    f.write("version=last\n")
    f.write("go\n")
    f.write("cr")
    f.seek(0)
    return f


def get_keys(data_dict, key_set=None):
    """Get all unique keys from a possibly nested dictionary.

    Note: Only used this to get a list of all the keys for inspection. Not
    used by any other functions

    Parameters:
    -----------------
    data_dict: Possibly nested dictionary
    key_set: set of all unique keys to be added too

    Returns:
    -----------------
    key_set: set of all unique keys

    """

    if key_set is None:
        key_set = set()
    for kk, vv in data_dict.items():
        # know I have either strings(bytearrays), None,  or list of dicts
        if isinstance(vv, list):
            for ll in vv:
                if isinstance(ll, dict):
                    get_keys(ll, key_set)
                else:
                    key_set.add(ll)
        elif isinstance(kk, str):
            key_set.add(kk)

    return key_set


def to_ascii(data):
    """Convert bytearray or unicode values to ascii and html escape strings.
       The unicode shouldn't be a problem, but convert anyway.

    Parameters:
    -----------------
    data: Possibly nested dictionary

    Returns:
    -----------------
    data: Same as input but with bytearrays --> strings

    """

    for kk, vv in data.items():
        if isinstance(vv, list):
            for ll in vv:
                if isinstance(ll, dict):
                    to_ascii(ll)
                elif isinstance(vv, bytearray):
                    vv = vv.decode().strip()
                    ll[ll] = None if vv == "None" else html.escape(str(vv))
        elif isinstance(vv, bytearray):
            vv = vv.decode().strip()
            data[kk] = None if vv in ("None", "NONE", "none") else html.escape(str(vv))
        elif isinstance(vv, str):
            data[kk] = None if vv in ("None", "NONE", "none") else html.escape(vv)
    return data


def get_inds(df, col, cond, tolist=True):
    """Get row/col index of pandas DataFrame matching some condition in given
       column.

    Parameters
    ------------------
    df: Input DataFrame
    col: Column name string
    cond: Condition to check for elements in col to equate to
    tolist: Return row_num as list if True

    Return
    ------------------
    row_num: List of all rows matching cond if not Multiindex, else Multiindex
             rows
    col_num: Column number of col

    """

    col_num = df.columns.get_loc(col)
    if isinstance(cond, str):
        # If proposal is incomplete, df[col] might not be string. Cast to str
        row_num = df.index[df[col].apply(str).str.startswith(cond)]
        if tolist:
            row_num = row_num.tolist()
    else:
        row_num = df.index[df[col] == cond]
    return row_num, col_num


def flat_index(df, row_num):
    """Translate multiindex tuple to list of single level row numbers

    Parameters
    ------------------
    df: Input DataFrame
    row_num: Multiindex row tuples

    Return
    ------------------
    flat_rows: flattened row numbers list

    """

    flat_rows = df.index.get_indexer(row_num).tolist()
    return flat_rows


def replace_val(df, ind, col, val):
    """Insert value into element of DataFrame (Works for multiiindex)

    Parameters
    ------------------
    df: Input DataFrame
    ind: Index at which to replace (tuple if Multiindex df)
    col: Column name at which to replace
    val: Value to replace with

    Return
    ------------------
    df: Modified DataFrame

    """

    df.loc[ind, col] = val
    return df


def get_rows_span(df, ind_grp="targid", ind_count="obs", verbose=0,
                  renum=False):
    """Get list of start/stop row nums for spanning across a column in a table

    Parameters
    ------------------
    df: DataFrame
    ind_grp: Index to groupby
    ind_count: Index to count # of for each ind_grp
    verbose: Print caught exceptions messages if True
    renum: Whether to re-number ind_count

    Return
    ------------------
    rows: list of tuples [(row_start, row_stop)]
    """

    # Count # of ind_counts for each ind_grp

    warnings.formatwarning = newWarn
    try:
        if renum:
            # fill with anything but ""
            df[ind_grp].fillna("blah", inplace=True)
            # cache
            oldCol = df[ind_count]
            if isinstance(df.index, pd.MultiIndex):
                old2ndHd = df[ind_grp].loc[(0, 0)]
                # count this as a blank
                df[ind_grp].loc[(0, 0)] = ""
            else:
                old2ndHd = df[ind_grp].loc[0]
                df[ind_grp].loc[0] = ""
            df[ind_count] = range(1, len(df) + 1)
            cts = df.reset_index().groupby(ind_grp)[ind_count
                                                    ].nunique().tolist()
            # reset
            df[ind_count] = oldCol
            df[ind_grp].loc[(0, 0)] = old2ndHd
            replace_col(df, "blah", np.nan, ind_grp)
        else:
            cts = df.reset_index().groupby(ind_grp)[ind_count].count().tolist()
    except KeyError as err:
        if verbose > 1:
            warnings.warn("KeyError: {}. Skipping span for {}, {} ".format(
                          err, ind_grp, ind_count))
            return []

    rows = []
    start = 0
    for ct in cts:
        stop = start + ct - 1
        span = (start + 1, start + ct)
        if span[1] - span[0] >= 30:
            if verbose > 1:
                warnings.warn("Too many targets. Can't span column across " +
                            "pages. Skipping {}, {}".format(ind_grp, ind_count))
            return []
        if span[0] != span[1]:  # don't add rows that aren't spanning
            rows.append(span)
        start = stop + 1
    return rows


def replace_col(df, old, new, col, regex=True):
    """Replace all occurrences of old in col of df with new

    Parameters
    ------------------
    df: DataFrame
    old: Values to replace
    new: Values to reaplace by
    col: col to do replacement in
    regex: arg for DataFrame.replace. Whether to interpret as regex

    Return
    ------------------
    df: Modified DataFrame

    """
    if old == "nan":
        old = np.nan
    df[col].replace(old, new, inplace=True, regex=regex)

    return df


def cond_pad(df, col_fill, cond_col, cond):
    """Conditionally pad empty string with above value in column.
       (Generalize to replace any common value vs. just empty string?)

    Parameters
    -------------------
    df: DataFrame
    col_fill: Column to pad values into
    cond_col: Column to test condition in
    cond: Condition to test. For now just statswith.

    Return
    ------------------
    df: Modified DataFrame

    """

    df[col_fill].replace("", np.nan, inplace=True)  # Need NaN for fillna
    df[col_fill].where(~df[cond_col].str.startswith(cond),
                       df[col_fill].fillna(method="pad"), inplace=True)
    # also fill if Observation is blank.
    df[col_fill].where(df[cond_col] != "", df[col_fill].fillna(method="pad"),
                       inplace=True)
    # If first targid row is empty, will fill with "Grp\Name", since there's an
    # extra row added with the column names for spanning. Replace those with ""
    df[col_fill].replace(col_fill, "", inplace=True)
    df[col_fill][0] = col_fill

    return df


def df_to_list(df, idx_head=False):
    """Convert DataFrame to list of lists for reportlab compatibility.

    Parameters
    ------------------
    df: DataFrame
    idx_head: Use index for header row if true, use header of table if False

    Return
    ------------------
    rtab: list of lists

    """

    if idx_head:
        rtab = [list(df.index)] + [df.values.tolist()]
    else:
        rtab = [list(df)] + df.values.tolist()
    return rtab


def prepend_level(df, lev_name=""):
    """Prepend a level to a Multiindex DataFrame

    Parameters
    ------------------
    df: DataFrame
    lev_name: Name of level (default is empty string)

    Return
    ------------------
    df: DataFrame with prepended level

    NB: Decided not to use this to add empty row

    """

    df.columns = pd.MultiIndex.from_product([df.columns, [lev_name]])

    return df


def rename_cols(df, flds, rname):
    """Rename given column names

    Parameters
    ------------------
    df: DataFrame
    flds: Ordered list of names of data fields to include
    rname: dict of {old_name:new_name}
    Return
    ------------------
    df: DataFrame with rename columns

    """

    df.rename(columns=list2dict(flds, rname), inplace=True)

    return df


def sum_grp_replace(df, grp, col_sum, col_repl):
    """Group DataFrame by grp, sum values in col, insert summed values from
       one col into empty rows of another col.

    Parameters
    ------------------
    df: DataFrame
    grp: Field to groupby
    col_sum: Column to sum over
    col_repl: Column to insert sums into (if they're empty)

    Return
    ------------------
    df: DataFrame with rename columns

    """

    # Replace empty values by NaN for now
    df[col_sum][df[col_sum] == ''] = np.nan
    grp_sum = df.groupby([grp])[[col_sum]].sum()
    df[col_repl].where((df[col_repl] == ""), grp_sum[col_sum], inplace=True)
    # Get rid of NaN
    df[col_sum].fillna("", inplace=True)

    return df


def newWarn(message, *args):
    """For overriding warnings.showwarning so it doesn't show the src
       code line
    Parameters
    ------------------
    message: warning message
    *args: ignore all other args in showwarning

    Return
    ------------------
    message
    """

    return str(message) + "\n"


def convert_ra_dec(radec):
    """ Use format.py to convert decimal degrees to sexagesimal for
         tuple(ra, dec) or a DataFrame containing ra/dec

    Parameters
    ------------------
    radec: tuple(ra, dec), DataFrame
    Return
    ------------------
    df: Modified DataFrame
    """

    if isinstance(radec, tuple):
        radec = (deg2ra(radec[0], format=":"), deg2dec(radec[1], format=":"))
    else:
        mask_ra = (pd.notnull(radec.ra))
        mask_dec = (pd.notnull(radec.dec))
        radec.loc[mask_ra, 'ra'] = radec.loc[mask_ra, 'ra'].apply(deg2ra,
                                                                  format=":")
        radec.loc[mask_dec, 'dec'] = radec.loc[mask_dec, 'dec'].apply(deg2dec,
                                                                      format=":")

    return radec


def float2string(mdf, cols):
    """Pandas converts dtype of a col with NaN in it to float. Convert just
       those floats back to ints.

    Parameters
    ------------------
    mdf: DataFrame
    cols: List of cols to swap

    Return
    ------------------
    df: Modified DataFrame
    """

    for col in cols:
        # Only select numeric values
        mdf[col].fillna(value=np.nan, inplace=True)
        mask = mdf[col].apply(np.isreal)
        mdf[col].loc[mask] = mdf[col].loc[mask].astype(int)
        # this doesn't work for mon table, still get floats
        # Maybe because dtype is int64 and not int?
        # force it to be a string
        mdf[col].loc[mask] = mdf[col].loc[mask].astype(str)
    return mdf


def get_rc(propid, user, pwd, srvr, verbose, out_log):
    """Parse RC string returned from prop_resources to obtain just target names
     and rc values for all targets in proposal. Calculate total RC.

    Parameters
    ------------
    propid: proposal #
    user: db user name
    pwd: User's password
    srvr: Name of server
    verbose: verbosity level
    out_log: Directory for log outputs

    Return
    ------------------
    rc: Dict of {targid:[rc0, rc1,rc2}
    rc_tot: List of total [RC1, RC2, RC3] for each cycle
    """

    # rc comes from prop_resources as a string looking like
    # {rows: [{'data':
    #         [prop_num, targnum, targname, RC0, RC1, RC2], 'id': prop_id }],}
    rcs = prop_resources(propid, user, pwd, srvr, verbose, out_log)
    
    # remove extra comma at the end that's troublesome for json
    com_ind = rcs.rfind(',')
    if verbose > 1:
        print("rc for {}: {}".format(propid, rcs))
    rcs = rcs[:com_ind] + rcs[com_ind+1:]

    try:
        rcs = json.loads(rcs)
        # Just want targname,RC0,RC1,RC2. Strip extra white space from RC strings.
        rcs = rcs.get('rows')
        rc = {data.get('id'): [data.get('data')[3].strip(),
                               data.get('data')[4].strip(),
                               data.get('data')[5].strip()] for data in rcs}
    except (ValueError, IndexError) as err:
        warnings.warn(("Problem decoding string returned from"
                       " prop_resources.pl. RC will not be display."
                       " {}".format(err)))
        if verbose > 1:
            print("Bad RC: {} ".format(rcs))
        return {"bah": None}, [0, 0, 0]
    # Get sum for totals. Skip infesible / incalcuable
    rc_tot = []
    for rclist in zip(*rc.values()):
        rclistn = []
        for rcl in rclist:
            try:
                # Convert RC from string for summing.
                rclistn.append(float(rcl))
            except (ValueError, TypeError) as err:
                if verbose > 1:
                    warnings.warn(("ValueError: {}.".format(err) +
                                   " Bad RC for prop:{} ").format(propid))
                # Write non-convertible vals to None for summing
                rclistn.insert(0, None)
        rc_tot.append(round(sum(filter(None, rclistn)), 2))  # ignore None on sum

    return rc, rc_tot


def mk_fld_list(flds, col_wrap=3):
    """Take input OrderedDict, output as list of lists for reportlab table
       format

    Parameters
    ------------
    flds: OrderedDict fields read in from file like targetsDict.json
    col_wrap: wrap to next row after col_wrap cols

    Return
    ------------------
    tab: List of lists representing reportlab table.
    """

    # If field name contains Blank put empty space in cell
    # Format is "Field: value" for each cel in the table

    tab = []
    for fld, val in flds.items():

        fl = "" if "Blank" in fld else fld + ": "
        if "Blank" in fld:
            vl = ""
        else:
            if isinstance(val[0], float):
                vl = "{:.3g}".format(val[0])
            else:
                vl = val[0]
        tab.append("{}{}".format(fl, vl))
    tab = [tab[row:row+col_wrap] for row in range(0, len(tab), col_wrap)]

    return tab
