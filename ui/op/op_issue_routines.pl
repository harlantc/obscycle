#----------------------------------
# Copyright (c) 1995, Smithsonian Astrophysical Observatory
# You may not do anything you like with this file. 
#
# FILE NAME:  op_issue_routines.pl
# DEVELOPMENT: 
#
# NOTES:       
#             
# INPUT:       user input
#
# REVISION HISTORY:
#
# Date: 
#---------------------------------------------------------
$issue_routines = 1;

#-------------------------------------------------
# Getting issueIds for retrieved ObsIds
# 
# results are list of issueids, obsid w/open issues,
# obsids w/resolved issues
# and a hash table of all the issues for an obsid
#
# $open_obsid_list
# $resolved_obsid_list
# $issue_list
# %obs_issue_list 
#-------------------------------------------------
sub get_issueids {

  local($olist) = @_;
  local(@obs_issue_dat);
  my($sth,$sql_stmt,$tstr,$ostr);
  local($issobs,$issid,$issdate,$issobi);


  $sql_stmt = qq(select obsid,obi,issueid,date_resolved from obsid_issue);
  if (length($olist) > 0) {
    $ostr = qq( obsid in ($olist) );
  }
  if ($issue_where =~ /Open/ && !$issue_where =~ /Resolved/) {
     $tstr = "date_resolved is NULL";
  }
  if (!$issue_where =~ /Open/ && $issue_where =~ /Resolved/) {
     $tstr = "date_resolved is not NULL";
  }
  
  if ($ostr && !$tstr) {
    $sql_stmt .= " where $ostr ";
  }
  elsif (!$ostr && $tstr) {
    $sql_stmt .= " where $tstr ";
  }
  elsif ($ostr && $tstr) {
    $sql_stmt .= " where $ostr and $tstr ";
  }
  
  $sql_stmt .= " order by obsid";

  &debug("ISSUES: $sql_stmt\n") if $dbg;
  $sth = $dbh->prepare($sql_stmt);
  $sth->execute(); 

  while (@obs_issue_dat = $sth->fetchrow_array)           
  {
    ($issobs,$issobi,$issid,$issdate) = @obs_issue_dat;
    $tstr = join('^',@obs_issue_dat);
    push (@issue_id_list,$tstr);

    # add to array of issues by obsid
    $tstr = $obs_issue_list{$issobs};
    if ($tstr !~ /$issid/) {
      if ($tstr) { 
        $tstr .= ","; 
      }
      if ($issdate) {
        $tstr .= "($issid)";
      }
      else {
        $tstr .= "$issid";
      }
      $obs_issue_list{$issobs} = $tstr;
    }
      
    #add to array of obsids with resolved issues
    #resolved issues have a date
    if ($issdate)  {
      if ($resolved_obsid_list !~ /$issobs/) {
        if ($resolved_obsid_list) {
          $resolved_obsid_list .= ","; 
        }
        $resolved_obsid_list .= "$issobs"; 
      }
    }
    else {
      #add to array of obsids with open issues
      if ($open_obsid_list !~ /$issobs/) {
        if ($open_obsid_list) {
          $open_obsid_list .= ","; 
        }
        $open_obsid_list .= "$issobs"; 
      }
    }
    #add to list of issues
    if ($issue_list !~ /$issid/) {
      if ($issue_list) {
        $issue_list .= ","; 
      }
      $issue_list .= "$issid"
    }
  }

  &debug( "Issues: $issue_list\n") if $dbg;
  &debug( "Open:   $open_obsid_list\n") if $dbg;
  &debug( "Reslv:  $resolved_obsid_list\n") if $dbg;
}



#----------------------------------------------
# return all issueids for a given obsid
#----------------------------------------------
sub get_issueids_for_obsid 
{

  local ($myobs) = @_;
  local ($tstr);

  $tstr = $obs_issue_list{$myobs};

  return $tstr;
}
  
#----------------------------------------------
# return all issueids for a given obsid/obi
#----------------------------------------------
sub get_issueids_for_obi 
{

  local ($myobs,$myobi) = @_;
  local ($tstr,$hh);
  local($issobs,$issid,$issdate,$issobi);

  foreach $hh (@issue_id_list) {
    ($issobs,$issobi,$issid,$issdate) = split(/\^/,$hh);
    &debug("$myobs ** $myobi ** $issobs ** $issobi ** \n") if $dbg;
    if (($issobs == $myobs) && ($issobi == $myobi)) {
    &debug("found $myobs ** $myobi ** $issid \n") if $dbg;
      if ($tstr) {
        $tstr .= ",";
      }
      if ($issdate) {
        $tstr .= "($issid)";
      } else {
        $tstr .= "$issid";
      }
    }
  }

  return $tstr;
}
  
  

  


#---------------------------------------------------------------
# build issues files
#
# Currently it builds one file for each issue and returns the
# last file built.  This could easily be changed to build
# one big file of all issues past in.
#---------------------------------------------------------------
sub build_issue_files {

  local ($issue_ids) = @_;
  local (@issue_names,$sql_stmt,$sth,$iopen,$iresolved);
  local ($itmp_file,$hstr);
  local ($sth_open);
  local ($sth_resolved);

  $hstr = cxc_header();

  @issue_names = (
	"IssueId", "Title", "Description", "BugId",
	"Status", "Date Fixed","Date Resolved" );

  $sql_stmt = qq(select obsid from obsid_issue where 
        date_resolved is NULL and issueid = ?
	order by obsid); 
  $sth_open = $dbh->prepare($sql_stmt);
  $sql_stmt = qq(select obsid from obsid_issue where 
        date_resolved is not NULL and issueid = ?
	order by obsid); 
  $sth_resolved = $dbh->prepare($sql_stmt);
  

  $sql_stmt = qq(select issueid, title, description, bugid,
	status, date_fixed,date_resolved from process_issue
	where issueid in ($issue_ids));

  $sth =$dbh->prepare($sql_stmt);
  $sth->execute;

  while(@issue_dat = $sth->fetchrow_array) {

    # open the file 
    $itmp_file = "$tmp_dir/op\_" .$tmp_host."_"."$issue_dat[0]".".html";
    open(ITMP, ">$itmp_file") ||  die "Can't open temp file\n";

    print ITMP <<header;
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>issue:$issue_dat[0]</title>
</head>
$hstr
header

    # get the resolved/open obsids for issue
    $iopen = "";
    $iresolved = "";
    $sth_open->bind_param(1,$issue_dat[0]);
    $sth_open->execute;
    while (@row = $sth_open->fetchrow_array) {
      if ($iopen) {
        $iopen .= ",";
      } 
      $iopen .= $row[0];
    }
    $sth_resolved->bind_param(1,$issue_dat[0]);
    $sth_resolved->execute;
    while (@row = $sth_resolved->fetchrow_array) {
      if ($iresolved) {
        $iresolved .= ",";
      } 
      $iresolved .= $row[0];
    }
        
    for($ii=0; $ii<=$#issue_dat ; $ii++) {
      $issue_line = $issue_names[$ii].":  ".$issue_dat[$ii]."<br>\n";
      print ITMP "$issue_line<br>\n";
    }

    print ITMP "\nResolved Obsids: $iresolved <br>\n";
    print ITMP "<br>Open Obsids: $iopen <p>\n";

    print ITMP <<explan;
\n<hr><br><br>
<ul>
<li><font size=-1> Status OPEN: the issue has been found and await resolution</font>
<li><font size=-1>Status FIXED: there is a software or operational fix to 
the issue, that can be applied in custom data processing</font>
<li><font size=-1>Status RESOLVED: the fix has been applied to all data 
and will be applied to all future data; the issue is closed.</font>
</ul>

<font size=-1>
NOTE: an issue may have OPEN or FIXED status, while a given obsid with the
same issue may have RESOLVED status. This happens if the obsid has been
reprocessed, while other obsid with the same issue may still be awaiting reprocessing.</font>
explan


    print ITMP footer_no_date();
    print ITMP qq(</body></html>\n);

    close(ITMP);
  }
  chmod(0777,"$itmp_file");

  return ($itmp_file);

  
}




