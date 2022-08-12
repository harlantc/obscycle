#----------------------------------
# Copyright (c) 1995, Smithsonian Astrophysical Observatory
# You may not do anything you like with this file. 
#
# FILE NAME:  op_config.pl
# DEVELOPMENT: 
#
# NOTES:       Setup environment variables for the apstat
#             
# INPUT:      
#
# REVISION HISTORY:
#
# Date: 
#---------------------------------------------------------
use Sys::Hostname;


# debug files , set to 1 is you're debugging
$dbg = 1;

$VV_URL_DIR    = "vv";
$db            = "axafapstat";
if ($ENV{'REMOTE_ADDR'}) {
  $tmp_host      = $ENV{'REMOTE_ADDR'};
}
else {
  $tmp_host = "";
}
if ( $ENV{'HTTP_HOST'}) {
  $host          = $ENV{'HTTP_HOST'};
} elsif ($ENV{'SERVER_NAME'}) {
  $host  = $ENV{'SERVER_NAME'};
}
else  {
  $host = "CMDLINE";
}


if ($ENV{'WEB_BIN'} ) {
  $web_bin      = $ENV{'WEB_BIN'};
  $web_base =  $web_bin . "/..";
  $web_bin .= "/op";
  $CGI_BIN       = "$web_base/cgi-bin";
} 
else {
  $web_bin   = ".";
  $web_base  = ".";
  $CGI_BIN   = "";
}

if ($ENV{'OTS_BIN'}) {
  $otsdir        = "$ENV{'OTS_BIN'}"; 
}
else {
  $otsdir = "/home/ascds/DS.release/ots/bin";
}
$rdbrepair     = "$otsdir/repair";
$ST            = "/cgi-bin/op/op_status_table.cgi";

$soft_dir = "/proj/web-icxc/htdocs/soft/R4";
$soft_url = "/soft/R4";

if ($web_base eq ".") {
  $opdir = $web_base;
}
else {
  if ( "$ENV{'OBSCYCLE_DATA_PATH'}") {
    $opdir = "$ENV{'OBSCYCLE_DATA_PATH'}";
    $opdir .= "/op";
  } else {
    $opdir = "/data/rpc/op";
  }
}

#print STDERR "OP $opdir";
open PFILE,"< $opdir/.htop";
while ($stmp = <PFILE>) {
   if ($stmp =~ /pass/i) {
        chop($stmp);
	$pwd = (split(/=/,$stmp))[1];
	$pwd =~ s/\"//g;
   }
   elsif ($stmp =~ /connect/ ) {
	chomp($stmp);
	$srv = (split(/ /,$stmp))[2];
	$uid = (split(/ /,$stmp))[4];
   }
}
close PFILE;


my($lname) = hostname;
$tmp_dir = $opdir . "/tmp/";
$tmp_dir .= $lname if ($lname);
if (!-e $tmp_dir) {
   mkdir("$tmp_dir",02775);
   chmod(02775,"$tmp_dir");
}
$tmp_err   = "$tmp_dir/op_serror.html";
$dbg_file  = "$tmp_dir/op\_$tmp_host\_debug.txt";
$ISSUE_TMP = "$tmp_dir";

$lname= "" if (!$lname); 
$log_file  = "$opdir/$lname" . "_op_access.log";

my(@dtmp) = localtime();
$tmp_date = sprintf("%02.2d%02.2d%02.2d", $dtmp[2],$dtmp[1],$dtmp[0]);


#maximum entries for 'in' SQL clause 
$MAX_IN = 200;

$sql_date_fmt = "102";
$sql_time_fmt = "108";


$sp_char = "___";
