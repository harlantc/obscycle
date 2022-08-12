#!/usr/bin/perl 

#******************************************************************************
# calc_num_obs.pl
#
# This script calculates the number of observations requested for each
# proposal.  The number of observations for a proposal is the sum of pointings
# necessary to observe the proposal.  Each target and follow-up is a pointing.
# In addition, for pointings greater than 80 ksec, we count each 80 ksec
# as a separate pointing.
#******************************************************************************

use strict;
use DBI;
use FindBin qw ($Bin);
use lib "$Bin/../PeerReviewGUI/lib";
use Panel;
use Data::Dumper;
use vars qw($pwdProp %param $VERSION);

$VERSION = '$Id: calc_num_obs.pl,v 1.34 2012/02/14 15:37:37 wink Exp $';

{
  use Getopt::Long;
  parse_opts();
  
  if ($param{version})
    {
      print $VERSION, "\n";
      exit( 0 );
    }
  
  if ($param{help}) 
    { 
      usage(0);
    }
  
#  DBI->trace(1);
  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  my $dbh1 = DBI->connect($dsn1, "", "", {
					  PrintError => 1,
					  RaiseError => 0});
  
  # Load the panel
  my $panel = new Panel($dbh1, $param{panel}, 'N', $param{verbose});
  
  my $proposals = $panel->proposals()->list();
  my %prop = %$proposals;

  my $fh;  
  open ($fh, ">$param{log}") || die "Can't open $param{log}: $!\n";

  #==================================================
  # Set the number of pointings for TOOs and Monitors
  #==================================================
  print $fh "=====\n";
  print $fh "Setting the number of pointings for TOOs and Monitors\n";
  print $fh "=====\n";
  if ($param{test}) {
    print "=====\n";
    print "Setting the number of pointings for TOOs and Monitors\n";
    print "=====\n";
  }

  my $too_list = $dbh1->prepare(qq(select distinct prop_id from too where panel_id = ?));
  $too_list->execute($param{panel});
  my @too_props;
  while (my($prop_id) = $too_list->fetchrow_array) {
    push @too_props, $prop_id;
  }
  $too_list->finish;
  
  foreach my $prop_id (@too_props){
    print STDERR "Processing TOOs/Monitors for $prop_id\n" if $param{verbose};
    my $proposal = $prop{$prop_id};
    my $targ_list = $proposal->targets;
    for (my $i = 1; $i < scalar @$targ_list; $i++) {
      print $fh "Panel_id ", $proposal->panel_id, ", Proposal ",
	$proposal->prop_id, ", target ", $$targ_list[$i]->targ_id, "\n";
      print "Panel_id ", $proposal->panel_id, ", Proposal ",
	$proposal->prop_id, ", target ", $$targ_list[$i]->targ_id, "\n" if
	  $param{test};
      my $too_list = $$targ_list[$i]->toos;
      my $num_obs = 0;
      my $num_obs_1 = 0;
      my $num_obs_2 = 0;
      foreach (my $j = 1; $j < scalar @$too_list; $j++) {
	$num_obs++ if $$too_list[$j]->ao == 0;
	$num_obs_1++ if $$too_list[$j]->ao == 1;
	$num_obs_2++ if $$too_list[$j]->ao == 2;

	my $segments = $$too_list[$j]->num_pnt;    
	print $fh "Update for num_too_pt: $segments segment for too $j\n";
	print "Update for num_too_pt: $segments segment for too $j\n" if 
	  $param{test};
	$$too_list[$j]->set('num_too_pt', $segments);
	$$too_list[$j]->save2database;
      }
      $$targ_list[$i]->set('num_obs_req', $num_obs);
      $$targ_list[$i]->set('num_obs_app', $num_obs);
      $$targ_list[$i]->set('num_obs_req_1', $num_obs_1);
      $$targ_list[$i]->set('num_obs_app_1', $num_obs_1);
      $$targ_list[$i]->set('num_obs_req_2', $num_obs_2);
      $$targ_list[$i]->set('num_obs_app_2', $num_obs_2);
      $$targ_list[$i]->save2database;
    }
  }
  
  #========================================================
  # Set the number of pointings, tax and grades for Targets
  #========================================================
  print $fh "=====\n";
  print $fh "Setting the number of pointings, tax, and grades for Targets\n";
  print $fh "=====\n";
  if ($param{test}) {
    print "=====\n";
    print "Setting the number of pointings, tax, and grades for Targets\n";
    print "=====\n";
  }
  
  foreach my $prop_id (keys %prop){
    my $proposal = $prop{$prop_id};
    print STDERR "Processing $prop_id\n" if $param{verbose};
    next if $proposal->type =~ /ARC|THE/;
    my $targ_list = $proposal->targets;
    for (my $i = 1; $i < scalar @$targ_list; $i++) {
      print $fh "Panel_id ", $proposal->panel_id, ", Proposal ", 
	$proposal->prop_id, ", target ", 
	  $$targ_list[$i]->targ_id, ":\n\ttype: ", $proposal->type, " ",$proposal->big_proj,
	    "\n\treq_time: ", $$targ_list[$i]->req_time,
          "\n\tat_hel: ", $$targ_list[$i]->at_hel,
	      "\n\ttime_crit: ", $$targ_list[$i]->time_crit,
	       "\n\tmcop: ", $$targ_list[$i]->mcop,
		"\n\tmonitor: ", $$targ_list[$i]->monitor, 
		  "\n\tnum_obs: ", $$targ_list[$i]->num_obs_req, 
		    "\n\ttoo_prob: ", $$targ_list[$i]->too_prob_req,
		      " \n\traster_scan: ", 
			$$targ_list[$i]->raster_scan, "\n";
      print $fh "\tresp_time: ", $$targ_list[$i]->response_time, "\n" if 
	$$targ_list[$i]->response_time;
      if ($param{test}) {
	print "Panel_id ", $proposal->panel_id, ", Proposal ", 
	  $proposal->prop_id, ", target ", 
	    $$targ_list[$i]->targ_id, ":\n\ttype: ", $proposal->type, 
	      "\n\treq_time: ", $$targ_list[$i]->req_time,
        "\n\tat_hel: ", $$targ_list[$i]->at_hel,
		"\n\ttime_crit: ", $$targ_list[$i]->time_crit,
	         "\n\tmcop: ", $$targ_list[$i]->mcop,
		  "\n\tmonitor: ", $$targ_list[$i]->monitor, 
		    "\n\tnum_obs: ", $$targ_list[$i]->num_obs_req, 
		      "\n\ttoo_prob: ", $$targ_list[$i]->too_prob_req,
			" \n\traster_scan: ", 
			  $$targ_list[$i]->raster_scan, "\n";
	print "\tresp_time: ", $$targ_list[$i]->response_time, "\n" if 
	  $$targ_list[$i]->response_time;
      }

      my $segments = $$targ_list[$i]->num_pnt(1);
      my $tax = $$targ_list[$i]->calc_tax($fh);
      my $tc_cnt = $$targ_list[$i]->calc_tc(1);
      
      my $grade = $$targ_list[$i]->calc_tc_grade(1);
    
      print $$targ_list[$i]->targ_id, "\n" if !defined $segments;
      print $fh "Update for tax and tc tallies:\n\tpointings: $segments\n\ttax: $tax\n\ttc_tally: $tc_cnt\n";
      print $fh "\ttc_grade: $grade\n" if $$targ_list[$i]->time_crit =~ /Y/;
      if ($param{test}) {
	print "Update for tax and tc tallies:\n\tpointings: $segments\n\ttax: $tax\n\ttc_tally: $tc_cnt\n";
	print "\ttc_grade: $grade\n" if $$targ_list[$i]->time_crit =~ /Y/;
      }

      $$targ_list[$i]->set('num_pnt_req', $segments);
      $$targ_list[$i]->set('num_pnt_app', $segments);
      $$targ_list[$i]->set('tax', $tax);
      $$targ_list[$i]->set('tax_req', $tax);
      $$targ_list[$i]->set('tc', $tc_cnt);
      $$targ_list[$i]->set('tc_req', $tc_cnt);
      $$targ_list[$i]->set('tc_grade_req', $grade);
      $$targ_list[$i]->set('tc_grade_app', $grade);
      if ($$targ_list[$i]->at_hel eq 'Y') {
          my $targ_hel = $$targ_list[$i]->req_time;
          $$targ_list[$i]->set('req_hel', $targ_hel);
          $$targ_list[$i]->set('app_hel', $targ_hel);
        }
      $$targ_list[$i]->save2database;

      # Do the calculations for future AOs if target is an MCOP
      if ($$targ_list[$i]->mcop eq 'Y') {
	if ($$targ_list[$i]->req_time_1) {
	  print $fh "  AO 1\n  ====\treq_time: ", $$targ_list[$i]->req_time_1,
	    "\n\tnum_obs: ", $$targ_list[$i]->num_obs_req_1, "\n";
	  print "  AO 1\n  ====\treq_time: ", $$targ_list[$i]->req_time_1,
	    "\n\tnum_obs: ", $$targ_list[$i]->num_obs_req_1, "\n" if $param{test};

	  my $segments_1 = $$targ_list[$i]->num_pnt_1(1);
	  my $tax_1 = $$targ_list[$i]->calc_tax_1($fh);
	  my $tc_cnt_1 = $$targ_list[$i]->calc_tc_1(1);
	  
	  my $grade_1 = $$targ_list[$i]->calc_tc_grade_1(1);
	  
	  print $fh "Update for tax and tc tallies:\n\tpointings: $segments_1\n\ttax: $tax_1\n\ttc_tally: $tc_cnt_1\n";
	  print $fh "\ttc_grade: $grade_1\n" if $$targ_list[$i]->time_crit =~ /Y/;
	  if ($param{test}) {
	    print "Update for tax and tc tallies:\n\tpointings: $segments_1\n\ttax: $tax_1\n\ttc_tally: $tc_cnt_1\n";
	    print "\ttc_grade: $grade_1\n" if $$targ_list[$i]->time_crit =~ /Y/;
	  }

	  $$targ_list[$i]->set('num_pnt_req_1', $segments_1);
	  $$targ_list[$i]->set('num_pnt_app_1', $segments_1);
	  $$targ_list[$i]->set('tax_1', $tax_1);
	  $$targ_list[$i]->set('tax_req_1', $tax_1);
	  $$targ_list[$i]->set('tc_1', $tc_cnt_1);
	  $$targ_list[$i]->set('tc_req_1', $tc_cnt_1);
	  $$targ_list[$i]->set('tc_grade_req_1', $grade_1);
	  $$targ_list[$i]->set('tc_grade_app_1', $grade_1);
      if ($$targ_list[$i]->at_hel eq 'Y') {
        my $targ_hel_1 = $$targ_list[$i]->req_time_1;
        $$targ_list[$i]->set('req_hel_1', $targ_hel_1);
        $$targ_list[$i]->set('app_hel_1', $targ_hel_1);
    }
	  $$targ_list[$i]->save2database;
	}
	if ($$targ_list[$i]->req_time_2) {
	  print $fh "  AO 2\n  ====\treq_time: ", $$targ_list[$i]->req_time_2,
	    "\n\tnum_obs: ", $$targ_list[$i]->num_obs_req_2, "\n";
	  print "  AO 2\n  ====\treq_time: ", $$targ_list[$i]->req_time_2,
	    "\n\tnum_obs: ", $$targ_list[$i]->num_obs_req_2, "\n" if $param{test};

	  my $segments_2 = $$targ_list[$i]->num_pnt_2(1);
	  my $tax_2 = $$targ_list[$i]->calc_tax_2($fh);
	  my $tc_cnt_2 = $$targ_list[$i]->calc_tc_2(1);
	  
	  my $grade_2 = $$targ_list[$i]->calc_tc_grade_2(1);
	  
	  print $fh "Update for tax and tc tallies:\n\tpointings: $segments_2\n\ttax: $tax_2\n\ttc_tally: $tc_cnt_2\n";
	  print $fh "\ttc_grade: $grade_2\n" if $$targ_list[$i]->time_crit =~ /Y/;
	  if ($param{test}) {
	    print "Update for tax and tc tallies:\n\tpointings: $segments_2\n\ttax: $tax_2\n\ttc_tally: $tc_cnt_2\n";
	    print "\ttc_grade: $grade_2\n" if $$targ_list[$i]->time_crit =~ /Y/;
	  }

	  $$targ_list[$i]->set('num_pnt_req_2', $segments_2);
	  $$targ_list[$i]->set('num_pnt_app_2', $segments_2);
	  $$targ_list[$i]->set('tax_2', $tax_2);
	  $$targ_list[$i]->set('tax_req_2', $tax_2);
	  $$targ_list[$i]->set('tc_2', $tc_cnt_2);
	  $$targ_list[$i]->set('tc_req_2', $tc_cnt_2);
	  $$targ_list[$i]->set('tc_grade_req_2', $grade_2);
	  $$targ_list[$i]->set('tc_grade_app_2', $grade_2);
      if ($$targ_list[$i]->at_hel eq 'Y') {
        my $targ_hel_2 = $$targ_list[$i]->req_time_2;
        $$targ_list[$i]->set('req_hel2', $targ_hel_2);
        $$targ_list[$i]->set('app_hel2', $targ_hel_2);
    }
	  $$targ_list[$i]->save2database;
	}
      }
    }
  }
  

  #===================
  # Set the prop_times and HEL times
  #===================
  print $fh "=====\n";
  print $fh "Setting prop_times for Proposals\n";
  print $fh "=====\n";
  if ($param{test}) {
    print "=====\n";
    print "Setting prop_times for Proposals\n";
    print "=====\n";
  }
  foreach my $prop_id (keys %prop){
    my $proposal = $prop{$prop_id};
    next if $proposal->type =~ /ARC|THE/;
    my $req_time = $proposal->tot_time(1);
    my $prop_time = $proposal->prop_time(1);
    # Set HEL times
    my $req_hel = $proposal->tot_hel(1);
    my $prop_hel = $proposal->prop_hel(1);

    # Now make the update
    print $fh "Update for prop_time for ", $proposal->prop_id, 
      " in panel ", $proposal->panel_id, ": $prop_time\n";
    print "Update for prop_time for ", $proposal->prop_id, 
      " in panel ", $proposal->panel_id, ": $prop_time\n" if $param{test};
    print $fh "Update for req_time for ", $proposal->prop_id, 
      " in panel ", $proposal->panel_id, ": $req_time\n";
    print "Update for req_time for ", $proposal->prop_id, 
      " in panel ", $proposal->panel_id, ": $req_time\n" if $param{test};
    print $fh "Update for prop_hel for ", $proposal->prop_id,
      " in panel ", $proposal->panel_id, ": $prop_hel\n";
    print "Update for prop_hel for ", $proposal->prop_id,
      " in panel ", $proposal->panel_id, ": $prop_hel\n" if $param{test};
    print $fh "Update for req_hel for ", $proposal->prop_id,
      " in panel ", $proposal->panel_id, ": $req_hel\n";
    print "Update for req_hel for ", $proposal->prop_id,
      " in panel ", $proposal->panel_id, ": $req_hel\n" if $param{test};
    $proposal->set('total_req_time', $req_time);
    $proposal->set('total_app_time', $req_time);
    $proposal->set('prop_req_time', $prop_time);
    $proposal->set('prop_app_time', $prop_time);
    $proposal->set('total_req_hel', $req_hel);
    $proposal->set('total_app_hel', $req_hel);
    # No slew for  HEL
    $proposal->set('prop_req_hel', $req_hel);
    $proposal->set('prop_app_hel', $req_hel);
      $proposal->save2database;

    # Times for future cycles
    if ($proposal->mcop eq 'Y') {
      my $req_time_1 = $proposal->tot_time_1(1);
      my $prop_time_1 = $proposal->prop_time_1(1);
      my $req_time_2 = $proposal->tot_time_2(1);
      my $prop_time_2 = $proposal->prop_time_2(1);
      my $req_hel_1 = $proposal->tot_hel_1(1);
      my $prop_hel_1 = $proposal->prop_hel_1(1);
      my $req_hel_2 = $proposal->tot_hel_2(1);
      my $prop_hel_2 = $proposal->prop_hel_2(1);
      print $fh "Update for prop_time_1: $prop_time_1\n";
      print "Update for prop_time_1: $prop_time_1\n" if $param{test};
      print $fh "Update for prop_time_2: $prop_time_2\n";
      print "Update for prop_time_2: $prop_time_1\n" if $param{test};
      print $fh "Update for req_time_1: $req_time_1\n";
      print "Update for req_time_1: $req_time_1\n" if $param{test};
      print $fh "Update for req_time_2: $req_time_2\n";
      print "Update for req_time_2: $req_time_1\n" if $param{test};
      print $fh "Update for prop_hel_1: $prop_hel_1\n";
      print "Update for prop_hel_1: $prop_hel_1\n" if $param{test};
      print $fh "Update for prop_hel_2: $prop_hel_2\n";
      print "Update for prop_hel_2: $prop_hel_1\n" if $param{test};
      print $fh "Update for req_hel_1: $req_hel_1\n";
      print "Update for req_hel_1: $req_hel_1\n" if $param{test};
      print $fh "Update for req_hel_2: $req_hel_2\n";
      print "Update for req_hel_2: $req_hel_1\n" if $param{test};
      $proposal->set('total_req_time_1', $req_time_1);
      $proposal->set('total_app_time_1', $req_time_1);
      $proposal->set('prop_req_time_1', $prop_time_1);
      $proposal->set('prop_app_time_1', $prop_time_1);   
      $proposal->set('total_req_time_2', $req_time_2);
      $proposal->set('total_app_time_2', $req_time_2);
      $proposal->set('prop_req_time_2', $prop_time_2);
      $proposal->set('prop_app_time_2', $prop_time_2);
      $proposal->set('total_req_hel_1', $req_hel_1);
      $proposal->set('total_app_hel_1', $req_hel_1);
      $proposal->set('prop_req_hel_1', $prop_hel_1);
      $proposal->set('prop_app_hel_1', $prop_hel_1);
      $proposal->set('total_req_hel_2', $req_hel_2);
      $proposal->set('total_app_hel_2', $req_hel_2);
      $proposal->set('prop_req_hel_2', $prop_hel_2);
      $proposal->set('prop_app_hel_2', $prop_hel_2);
        $proposal->save2database;
    }
  }

  #==========================================
  # Set the number of pointings for Proposals
  #==========================================
  print $fh "=====\n";
  print $fh "Setting the number of pointings for Proposals\n";
  print $fh "=====\n";
  if ($param{test}) {
    print "=====\n";
    print "Setting the number of pointings for Proposals\n";
    print "=====\n";
  }
  foreach my $prop_id (keys %prop){
    my $proposal = $prop{$prop_id};
    next if $proposal->type =~ /ARC|THE/;
    print $fh "Panel_id ", $proposal->panel_id, ", Proposal ", 
      $proposal->prop_id, ": ", $proposal->type, "\n",
	"\tmcop: ", $proposal->mcop, "\n";
    print "Panel_id ", $proposal->panel_id, ", Proposal ", 
      $proposal->prop_id, ": ", $proposal->type, "\n",
	"\tmcop: ", $proposal->mcop, "\n" if $param{test};
    
    my $segments = $proposal->num_pnt(1);
    my $tax = $proposal->num_tax(1);

    # Now make the update
    print $fh "   Update for tax and tc tallies for ", $proposal->prop_id, 
      " in panel ", $proposal->panel_id, ":\n\tpointings: $segments\n\ttot_tax: $tax\n";
    print "   Update for tax and tc tallies for ", $proposal->prop_id, 
      " in panel ", $proposal->panel_id, ":\n\tpointings: $segments\n\ttot_tax: $tax\n" if $param{test};
    $proposal->set('num_obs_req', $segments);
    $proposal->set('num_obs_app', $segments);
    $proposal->set('tax_tot', $tax);
    $proposal->set('tax_req', $tax);
    $proposal->save2database;

    if ($proposal->total_req_time_1 && $proposal->mcop eq 'Y') {
      print $fh "   AO 1 for " . $proposal->total_req_time_1 . "\n   ====\n";
      print "   AO 1\n   ====\n" if $param{test};
      my $segments_1 = $proposal->num_pnt_1(1);
      my $tax_1 = $proposal->num_tax_1(1);
      my $targets_1 = $proposal->num_targs_1(1);
      # Now make the update
      print $fh "\tpointings: $segments_1\n\ttot_tax: $tax_1\n\tnum_targs: $targets_1\n";
      print "\tpointings: $segments_1\n\ttot_tax: $tax_1\n\tnum_targs: $targets_1\n" if $param{test};
      $proposal->set('num_obs_req_1', $segments_1);
      $proposal->set('num_obs_app_1', $segments_1);
      $proposal->set('tax_tot_1', $tax_1);
      $proposal->set('tax_req_1', $tax_1);
      $proposal->set('num_targ_req_1', $targets_1);
      $proposal->set('num_targ_app_1', $targets_1);
      $proposal->save2database;
    }

    if ($proposal->total_req_time_2 && $proposal->mcop eq 'Y') {
      print $fh "   AO 2\n   ====\n";
      print "   AO 2\n   ====\n" if $param{test};
      my $segments_2 = $proposal->num_pnt_2(1);
      my $tax_2 = $proposal->num_tax_2(1);
      my $targets_2 = $proposal->num_targs_2(1);
      
      # Now make the update
      print $fh "\tpointings: $segments_2\n\ttot_tax: $tax_2\n\tnum_targs: $targets_2\n";
      print "\tpointings: $segments_2\n\ttot_tax: $tax_2\n\tnum_targs: $targets_2\n" if $param{test};
      $proposal->set('num_obs_req_2', $segments_2);
      $proposal->set('num_obs_app_2', $segments_2);
      $proposal->set('tax_tot_2', $tax_2);
      $proposal->set('tax_req_2', $tax_2);
      $proposal->set('num_targ_req_2', $targets_2);
      $proposal->set('num_targ_app_2', $targets_2);
      $proposal->save2database;
    }
  }

  #===================
  # Set the TOO tallys
  #===================
  print $fh "=====\n";
  print $fh "Setting the number of TOOs for Proposals\n";
  print $fh "=====\n";
  if ($param{test}) {
    print "=====\n";
    print "Setting the number of TOOs for Proposals\n";
    print "=====\n";
  }
  foreach my $prop_id (keys %prop){
    my $proposal = $prop{$prop_id};

    # Need to get the trigger for reverse TOOs
    if ($proposal->rev_too =~ /Y/) {
       print "Haven't handled reverse TOO in ages $proposal->prop_id\n";
    }

    # Skip other nonTOOs
    next unless $proposal->type =~ /TOO/;
   
    # The TOOs
      my ($vf_too,$f_too,$s_too,$vs_too) = $proposal->num_too_new(1);
      print $fh "Update for TOO for ", $proposal->prop_id, 
	" in panel ", $proposal->panel_id, ": $vf_too,$f_too,$s_too,$vs_too\n";
      print "Update for TOO for ", $proposal->prop_id, 
	" in panel ", $proposal->panel_id, ": $vf_too,$f_too,$s_too,$vs_too\n" if $param{test};

      # Now make the update
      $proposal->set("vf_req", $vf_too);
      $proposal->set("f_req", $f_too);
      $proposal->set("s_req", $s_too);
      $proposal->set("vs_req", $vs_too);
      $proposal->set("vf_app", $vf_too);
      $proposal->set("f_app", $f_too);
      $proposal->set("s_app", $s_too);
      $proposal->set("vs_app", $vs_too);
      $proposal->save2database;

  }

  #==================
  # Set the TC tallys
  #==================
  print $fh "=====\n";
  print $fh "Setting the number of TCs for Proposals\n";
  print $fh "=====\n";
  if ($param{test}) {
    print "=====\n";
    print "Setting the number of TCs for Proposals\n";
    print "=====\n";
  }
  foreach my $prop_id (keys %prop){
    my $proposal = $prop{$prop_id};
    
    # Set RC current cycle
    my $count = $proposal->num_rc_score( 1);
    print $fh "Setting RC=$count for $prop_id\n";
    $proposal->set("rc_score_app", $count);
    $proposal->set("rc_score_req", $count);
    $proposal->save2database;

    # Set RC cycle n+1
    my $count = $proposal->num_rc_score_1( 1);
    print $fh "Setting RC_1=$count for $prop_id\n";
    $proposal->set("rc_score_app_1", $count);
    $proposal->set("rc_score_req_1", $count);
    $proposal->save2database;

    # Set RC cycle n+2
    my $count = $proposal->num_rc_score_2( 1);
    print $fh "Setting RC_2=$count for $prop_id\n";
    $proposal->set("rc_score_app_2", $count);
    $proposal->set("rc_score_req_2", $count);
    $proposal->save2database;

    next unless $proposal->tc_prop =~ /Y/;

    foreach my $grade (qw/e a d/) {
      my $count = $proposal->num_tc($grade, 1);
      $count = 0 if !$count;
      # Now make the update
      print $fh "Update for TC grade $grade for ", $proposal->prop_id, 
	" in panel ", $proposal->panel_id, ": $count\n";
      print "Update for TC grade $grade for ", $proposal->prop_id, 
	" in panel ", $proposal->panel_id, ": $count\n" if $param{test};
      $proposal->set("tc_${grade}_req", $count);
      $proposal->set("tc_${grade}_app", $count);
      $proposal->save2database;
    }

    # Tallys for future cycles
    if ($proposal->mcop eq 'Y') {
      print $fh "AO 1\n";
      print "AO 1\n" if $param{test}; 
      foreach my $grade (qw/e a d/) {
	my $count_1 = $proposal->num_tc_1($grade, 1);
	$count_1 = 0 if !$count_1;
	# Now make the update
	print $fh "Update for TC grade $grade for ", $proposal->prop_id, 
	  " in panel ", $proposal->panel_id, ": $count_1\n";
	print "Update for TC grade $grade for ", $proposal->prop_id, 
	  " in panel ", $proposal->panel_id, ": $count_1\n" if $param{test};
	$proposal->set("tc_${grade}_req_1", $count_1);
	$proposal->set("tc_${grade}_app_1", $count_1);
	$proposal->save2database;
      }

      print $fh "AO 2\n";
      print "AO 2\n" if $param{test}; 
      foreach my $grade (qw/e a d/) {
	my $count_2 = $proposal->num_tc_2($grade, 1);
	$count_2 = 0 if !$count_2;
	# Now make the update
	print $fh "Update for TC grade $grade: $count_2\n";
	print "Update for TC grade $grade: $count_2\n" if $param{test};
	$proposal->set("tc_${grade}_req_2", $count_2);
	$proposal->set("tc_${grade}_app_2", $count_2);
	$proposal->save2database;
      }
    }
  }

  close $fh;
}


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    log => undef,
	    panel => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "log=s",
	      "panel=i",
	      "test",
	      "all",
              "verbose=i",
              "version",
              "help"
            ) or exit(1);

  return if $param{help} or $param{version};


  my $err = 0;
  while ( my ( $par, $val ) = each ( %param ) )
  {
    next if defined $val;
    warn("parameter `$par' not set\n");
    $err++;
  }

  exit(1) if $err;

}

#******************************************************************************
# Subroutine for usage statements
#******************************************************************************
sub usage
{
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

calc_num_obs.pl [options]

=head1 OPTIONS

B<calc_num_obs.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name on postgresql server

=item B<-panel>

Panel to run calculations on

=item B<-all>

Flag indicating that all targets should be done, not just those with 
time_crit of 'Y'

=item B<-log>

Name of log file

=item B<-test>

Run in test mode

=item B<-help>

displays documentation for B<calc_num_obs.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script calculates the number of observations requested for each
proposal.  The number of observations for a proposal is the sum of pointings
necessary to observe the proposal.  Each target and follow-up is a pointing.
In addition, for pointings greater than 80 ksec, we count each 80 ksec
as a separate pointing.

=head1 AUTHOR

Sherry L. Winkelman
