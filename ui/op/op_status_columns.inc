#----------------------------------
# Copyright (c) 1995, Smithsonian Astrophysical Observatory
# You may not do anything you like with this file. 
#
# FILE NAME:  op_status_columns.inc
# DEVELOPMENT:
#
# NOTES:       defines used to build the different status report types
#             
#
# INPUT:       user input
#
# REVISION HISTORY:
#
# Date: 
#---------------------------------------------------------


# ------------------------------------------------------
# Formats for printing etc
# ------------------------------------------------------
$obsid_fmt     = "%-8.8s";
$alt_exp_fmt   = "%-9.9s";
$seqnbr_fmt    = "%-7.7s";
$object_fmt    = "%-20.20s";
$version_fmt   = "%-8.8s";
$ap_cp_fmt     = "%-8.8s";
$ap_date_fmt   = "%-16.16s";
$ap_stat_fmt   = "%-18.18s";
$charge_time_fmt = "%6.6s";
$good_time_fmt = "%6.6s";
$release_fmt   = "%-15.15s";
$caldb_fmt     = "%-14.14s";
$vv_date_fmt   = "%-16.16s";
$vv_status_fmt = "%-16.16s";
$cdo_review_fmt = "%-10.10s";
$vv_version_fmt= "%-11.11s";
$dd_date_fmt   = "%-16.16s";
$mail_date_fmt = "%-16.16s";
$ocat_status_fmt = "%-24.24s";
$ingested_fmt  = "%-8.8s";
$pipe_config_fmt = "%-15.15s";
$tgt_ra_fmt    = "%-10.10s";
$tgt_dec_fmt   = "%-10.10s";
$nom_roll_fmt  = "%6.6s";
$sim_z_fmt     = "%7.7s";
$readout_fmt   = "%-10.10s";
$grating_fmt   = "%-8.8s";
$datamode_fmt  = "%-15.15s";
$readmode_fmt  = "%-10.10s";
$app_time_fmt  = "%6.6s";
$type_fmt      = "%-4.4s";
$rights_fmt    = "%-11.11s";
$pub_date_fmt  = "%-16.16s";
$ap_dir_fmt    = "%-50.50s";
$cp_comment_fmt = "%s";
$cp_dist_fmt   = "%-16.16s";
$ap_dist_fmt   = "%-16.16s";
$obsdate_fmt   = "%-16.16s";
$sched_time_fmt = "%8.8s";
$ontime_fmt    = "%8.8s";
$obs_interval_fmt = "%-10.10s";
$delta_fmt     = "%6.6s";
$pifirst_fmt   = "%-12.12s";
$pilast_fmt    = "%-16.16s";
$propno_fmt    = "%-8.8s";
$issues_fmt    = "%s";


# ------------------------------------------------------
# Fixed report column headers  and formats
# ------------------------------------------------------
@long_hdr    =   (
    "ObsId",
    "SeqNum",
    "Object",
    "RA",
    "Dec",
    "Roll",
    "SIM-Z",
    "OCat Status",
    "Proc Status",
    "V&V Status",
    "Instrument",
    "Grating",
    "ReadMode",
    "DataMode",
    "Pipe. Config.",
    "Proc Type",
    "Proc Date",
    "V&V Date",
    "Data Distr Date",
    "Type",
    "Apprvd Time(ks)",
    "Good Time",
    "Charge Time",
    "Data Rights",
    "Public Rel Date",
    "Release",
    "CALDB Version",
    "Proc Version",
    "V&V Version",
    "Data Mail Date",
    "Issues"
  );

@long_fmt = (
        $obsid_fmt ,
	$seqnbr_fmt ,
	$object_fmt ,
	$tgt_ra_fmt ,
	$tgt_dec_fmt ,
	$nom_roll_fmt ,
	$sim_z_fmt ,
	$ocat_status_fmt ,
	$ap_stat_fmt,
	$vv_status_fmt,
	$readout_fmt ,
	$grating_fmt ,
	$readmode_fmt,
	$datamode_fmt ,
	$pipe_config_fmt ,
	$ap_cp_fmt,
	$ap_date_fmt ,
	$vv_date_fmt ,
	$dd_date_fmt ,
	$type_fmt ,
	$app_time_fmt ,
	$good_time_fmt ,
	$charge_time_fmt ,
	$rights_fmt ,
	$pub_date_fmt,
	$release_fmt ,
	$caldb_fmt ,
	$version_fmt ,
	$vv_version_fmt,
	$mail_date_fmt ,
	$issues_fmt 
	);

@short_hdr   =  (
    "ObsId",
    "SeqNum",
    "Object",
    "RA",
    "Dec",
    "Type",
    "Instrument",
    "Grating",
    "Proc Type",
    "Proc Status",
    "V&V Status",
    "OCat Status",
    "Obs Date",
    "V&V Date",
    "Data Distr Date",
    "Public Rel Date",
    "Issues",
  );

@short_fmt = (
        $obsid_fmt ,
	$seqnbr_fmt ,
	$object_fmt ,
	$tgt_ra_fmt ,
	$tgt_dec_fmt ,
	$type_fmt,
	$readout_fmt ,
	$grating_fmt ,
	$ap_cp_fmt,
	$ap_stat_fmt,
	$vv_status_fmt ,
	$ocat_status_fmt ,
	$obsdate_fmt ,
	$vv_date_fmt ,
	$dd_date_fmt ,
	$pub_date_fmt ,
	$issues_fmt 
	);

@dates_hdr   =  (
    "ObsId",
    "Object",
    "Type",
    "Proc Type",
    "Proc Status",
    "Proc Date",
    "Proc Version",
    "Obs Date",
    "Apprvd Time (ks)",
    "Sched Time (ks)",
    "On Target Time (ks)",
    "Good Time (ks)",
    "Charge Time (ks)",
    "V&V Status",
    "V&V Date",
    "V&V Version",
    "Data Distr Date",
    "Data Mailed Date",
    "Issues",
  );


    #"CP_Distr._Date",
    #"AP_Distr._Date",
@dates_fmt = (
        $obsid_fmt ,
	$object_fmt ,
	$type_fmt ,
	$ap_cp_fmt,
	$ap_stat_fmt,
	$ap_date_fmt ,
	$version_fmt ,
	$obsdate_fmt,
	$app_time_fmt ,
     	$sched_time_fmt,
	$ontime_fmt,
	$good_time_fmt ,
	$charge_time_fmt ,
	$vv_status_fmt ,
	$vv_date_fmt ,
	$vv_version_fmt,
	$dd_date_fmt ,
	$mail_date_fmt ,
	$issues_fmt 
	);

	#$cp_dist_fmt,
	#$ap_dist_fmt,
@summary_hdr = (
    "PI First",
    "Last Name",
    "Proposal",
    "Object",
    "ObsId",
    "SeqNum",
    "Apprvd Time (ks)",
    "Good Time (ks)",
    "Type",
    "Obs Date",
    "Data Distr Date",
    "Public Rel Date",
    "Issues",
  );

@summary_fmt = (
        $pifirst_fmt ,
        $pilast_fmt ,
        $propno_fmt ,
	$object_fmt ,
        $obsid_fmt ,
	$seqnbr_fmt ,
	$app_time_fmt ,
	$good_time_fmt ,
	$type_fmt ,
	$obsdate_fmt ,
	$dd_date_fmt ,
	$pub_date_fmt ,
	$issues_fmt
	);



