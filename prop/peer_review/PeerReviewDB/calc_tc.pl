#!/usr/bin/perl

#*****************************************************************************
# calc_tc.pl
#
# Calculates the TC value for roll, phase and time constraints from
# their values in the rollreq, phasereq and timereq tables and updates the
# target table with those totals.
#*****************************************************************************

use strict;
use DBI;
use vars qw($pwdProp %param $VERSION);
$VERSION = '$Id: calc_tc.pl,v 1.10 2012/02/14 14:40:41 wink Exp $';

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
  
  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  my $dbh1 = DBI->connect($dsn1, "", "", {
					  PrintError => 1,
					  RaiseError => 0});
  
  # Database connection 2
  my $dsn2 = "dbi:Pg:dbname=$param{U}";
  my $dbh2 = DBI->connect($dsn2, "", "", {
					  PrintError => 1,
					  RaiseError => 0});
  
  # Database connection 3
  my $dsn3 = "dbi:Pg:dbname=$param{U}";
  my $dbh3 = DBI->connect($dsn3, "", "", {
					  PrintError => 1,
					  RaiseError => 0});

  my $fh;  
  open ($fh, ">$param{log}") || die "Can't open $param{log}: $!\n";

  #========================================================================
  # Set the tc_roll and tc_phase for each with the constraint
  #========================================================================
  print $fh "=====\n";
  print $fh "Setting the TC scores for each roll constraint\n";
  print $fh "=====\n";
  my $get_roll = $dbh1->prepare(qq(select targ_id, sum(tc_roll) from rollreq 
                                   where roll_constraint = 'Y' and panel_id = ?
                                   group by targ_id));
  my $update_roll = $dbh2->prepare(qq(update target set tc_roll = ? where 
                                      targ_id = ? and panel_id = ?));

  $get_roll->execute($param{panel});
  while (my ($targ_id, $tc_roll) = $get_roll->fetchrow_array) {
    $update_roll->execute($tc_roll, $targ_id, $param{panel});
    $update_roll->finish;
  }
  $get_roll->finish;


  print $fh "=====\n";
  print $fh "Setting the TC scores for each phase constraint\n";
  print $fh "=====\n";
  my $get_phase = $dbh1->prepare(qq(select targ_id, tc_phase from phasereq 
                                    where phase_constraint = 'Y' and 
                                    panel_id = ?));
  my $update_phase = $dbh2->prepare(qq(update target set tc_phase = ? where 
                                       targ_id = ? and panel_id = ?));
  $get_phase->execute($param{panel});
  while (my ($targ_id, $tc_phase) = $get_phase->fetchrow_array) {
    $update_phase->execute($tc_phase, $targ_id, $param{panel});
    $update_phase->finish;
  }
  $get_phase->finish;

  #========================================================================
  # Set the tc_time constraint; calculate for each AO
  #========================================================================
  print $fh "=====\n";
  print $fh "Setting the TC scores for each time constraint\n";
  print $fh "=====\n";
  my $get_time = $dbh1->prepare(qq(select targ_id, ao, sum(tc_time) from timereq where time_constraint = 'Y' and panel_id = ? group by targ_id, ao));
  my $update_time = $dbh2->prepare(qq(update target set tc_time = ? where 
                                      targ_id = ? and panel_id = ?));
  my $update_time_1 = $dbh2->prepare(qq(update target set tc_time_1 = ? where 
                                      targ_id = ? and panel_id = ?));
  my $update_time_2 = $dbh2->prepare(qq(update target set tc_time_2 = ? where 
                                      targ_id = ? and panel_id = ?));
  $get_time->execute($param{panel});
  while (my ($targ_id, $ao, $tc_time) = $get_time->fetchrow_array) {
    if ($ao == 0) {
      $update_time->execute($tc_time, $targ_id, $param{panel});
      $update_time->finish;
    }
    elsif ($ao == 1) {
      $update_time_1->execute($tc_time, $targ_id, $param{panel});
      $update_time_1->finish;
    }
    if ($ao == 2) {
      $update_time_2->execute($tc_time, $targ_id, $param{panel});
      $update_time_2->finish;
    }
  }
  $get_time->finish;
  
  print $fh "=====\n";
  print $fh "Setting the TC scores for each monitor or TOO constraint\n";
  print $fh "=====\n";
  #========================================================================
  # Set the tc_mon and tc_too constraints; calculate for each AO
  #========================================================================
  # Determine the tc_mon value if this is a monitoring target
  # or tc_too for TOOs with followup
  # The dimensionless parameter for the monitoring interval constraint 
  # is: 
  # (min(Imax)) * (FRACTIONAL TOLERANCE)
  # -------------------------------------------
  #            max(OBS_TIME)
  #
  # FRACTIONAL TOLERANCE = (Imax - Imin)/(Imax + Imin)
  #
  # Imin, Imax, OBS_TIME are in units of days

  my $get_mon_targets = $dbh1->prepare(qq(select distinct prop_id, targ_id, req_time, req_time_1, req_time_2, monitor from target where targ_id in (select distinct targ_id from too where panel_id = ?)));
  my $get_count = $dbh1->prepare(qq(select max(ordr) from too where targ_id = ?));
  my $monitor_query1 = $dbh1->prepare(qq(select ordr, req_obs_time / 86.4, pre_min_lead, pre_max_lead, fractol from too where targ_id = ? and ao = ? and panel_id=? order by pre_max_lead, fractol, ordr ));
  my $monitor_query2 = $dbh1->prepare(qq(select max(req_obs_time / 86.4) from too where targ_id = ? and ao = ? and ordr != 1 and panel_id=? ));

  my $update_tc_mon = $dbh1->prepare(qq(update target set tc_monitor = ?, tc_monitor_req = ? where targ_id = ? and panel_id=?));
  my $update_tc_mon_1 = $dbh1->prepare(qq(update target set tc_monitor_1 = ?, tc_monitor_req_1 = ? where targ_id = ? and panel_id=?));
  my $update_tc_mon_2 = $dbh1->prepare(qq(update target set tc_monitor_2 = ?, tc_monitor_req_2 = ? where targ_id = ? and panel_id=?));

  my $update_tc_too = $dbh1->prepare(qq(update target set tc_too = ?, tc_too_req = ? where targ_id = ? and panel_id=?));
  my $update_tc_too_1 = $dbh1->prepare(qq(update target set tc_too_1 = ?, tc_too_req_1 = ? where targ_id = ? and panel_id=?));
  my $update_tc_too_2 = $dbh1->prepare(qq(update target set tc_too_2 = ?, tc_too_req_2 = ? where targ_id = ? and panel_id=?));

  $get_mon_targets->execute($param{panel});
  while (my ($prop_id, $targ_id, $req_time, $req_time_1, 
	     $req_time_2, $monitor) = $get_mon_targets->fetchrow_array) {
    # The constraint only applies if there is more than one segment
    $get_count->execute($targ_id);
    my ($count) = $get_count->fetchrow_array;
    $get_count->finish;
    next unless $count > 1; # We don't do anything for TOOs without followups
    next if $monitor =~ /P/; # We don't do anything for monitor preferences

    print $fh "Target is a Monitor\n" if $monitor eq 'Y';
    print $fh "Target is a TOO with follow-ups\n" if $monitor eq 'N';

    foreach my $ao (0, 1, 2){
      next if $ao == 1 and !$req_time_1;
      next if $ao == 2 and !$req_time_2;

      print $fh "AO: $ao\n=====\n" ;

      $monitor_query2->execute($targ_id, $ao,$param{panel});
      my ($max_time) = $monitor_query2->fetchrow_array;
      $monitor_query2->finish;

      my $mon_score = 'null';
      $monitor_query1->execute($targ_id, $ao,$param{panel});
      while (my ($order, $obs_time, $Imin, $Imax, 
		 $fractol) = $monitor_query1->fetchrow_array) {
	# we need to take the smallest fractol which is the first in the
	# list
	if ($mon_score eq 'null' && $max_time > 0) {
	  $mon_score = ($Imax * $fractol) / ($max_time);
	  $mon_score = ($Imax * 0.001) / ($max_time) if !$fractol;
	  if ($monitor =~ /Y/) {
	    my $update_query = $update_tc_mon;
	    $update_query = $update_tc_mon_1 if $ao == 1;
	    $update_query = $update_tc_mon_2 if $ao == 2;

	    $update_query->execute($mon_score, $mon_score, $targ_id,$param{panel});
	    $update_query->finish;
	  }
	  else {
	    my $update_query = $update_tc_too;
	    $update_query = $update_tc_too_1 if $ao == 1;
	    $update_query = $update_tc_too_2 if $ao == 2;

	    $update_query->execute($mon_score, $mon_score, $targ_id,$param{panel});
	    $update_query->finish;
	  }
	  print $fh "Proposal\ttarget\tscore\n";
	  print $fh "$prop_id\t$targ_id\t$mon_score\n";
	  print $fh "\torder\ttime (days)\t\tImin\tImax\tfractol\n";
	  print $fh "\t$order\t$obs_time\t$Imin\t$Imax\t$fractol";
	  print $fh " (using 0.001)" if !$fractol;
	  print $fh "\n";
	}
      }
      
    }

  }
  $get_mon_targets->finish;

#   if ($monitor eq 'Y' or ($type eq 'TOO' and $too_follow and 
# 			  $trig_target = 'Y')) {
#    my $max_time_q = $dbhProp3->prepare($monitor_query2);
# 	  $max_time_q->execute($targid);
# 	  my ($max_time) = $max_time_q->fetchrow_array;
# 	  $max_time_q->finish;
    
#     my $monitor_info = $dbhProp3->prepare(qq($monitor_query1));
#     $monitor_info->execute($targid);
#     while (my($order, $obs_time, $Imin, 
# 	      $Imax, $fractol) = $monitor_info->fetchrow_array) {
#       # we need to take the smallest fractol which is the first in the
#       # list
#       if ($mon_score eq 'null') {
# 	$mon_score = ($Imax * $fractol) / ($max_time);
# 	$mon_score = ($Imax * 0.001) / ($max_time) if !$fractol;
# 	if ($monitor =~ /Y/) {
# 	  $tc_mon = $mon_score;
# 	  print MON "Proposal\ttarget\tscore\n";
# 	  print MON "$prop_id\t$targid\t$tc_mon\n";
# 	  print MON "\torder\ttime (days)\t\tImin\tImax\tfractol\n";
# 	}
# 	else {
# 	  $tc_too = $mon_score;
# 	  print TOO "Proposal\ttarget\tscore\n";
# 	  print TOO "$prop_id\t$targid\t$tc_too\n";
# 	  print TOO "\torder\ttime (days)\t\tImin\tImax\tfractol\n";
# 	}
#       }
#       if ($monitor =~ /Y/) {
# 	print MON "\t$order\t$obs_time\t$Imin\t$Imax\t$fractol";
# 	print MON " (using 0.001)" if !$fractol;
# 	print MON "\n";
#       }
#       else {
# 	print TOO "\t$order\t$obs_time\t$Imin\t$Imax\t$fractol";
# 	print TOO " (using 0.001)" if !$fractol;
# 	print TOO "\n";
#       }
#     }
#     $monitor_info->finish;
    
#     print OUT qq(update target set tc_too = $tc_too where prop_id = $prop_id and targ_id = $targid and panel_id = $panel_id;), "\n";
#   }

  print $fh "=====\n";
  print $fh "Setting the TC scores for each group constraint\n";
  print $fh "=====\n";
  #=====================================================================
  # Set the tc_group constraint
  #=====================================================================
  # Determine the tc_group value if this is a grouped target
  # The dimensionless parameter for Group Observations is: 
  #       (TIME INTERVAL FOR THE GROUP)
  # -----------------------------------------
  # (TOTAL DURATION OF OBSERVATIONS IN GROUP)
  my $group_query = $dbh1->prepare(qq(select distinct prop_id, targ_id, group_name, group_interval, group_interval_1, group_interval_2  from target where group_obs = 'Y' and panel_id=? ));
  my $group_time = $dbh1->prepare(qq(select sum(req_time) from target where group_name = ? and prop_id = ? and panel_id=? ));
  my $update_group = $dbh1->prepare(qq(update target set tc_group_req = ?, tc_group_app = ? where targ_id = ? and panel_id=?));
  my $update_group_1 = $dbh1->prepare(qq(update target set tc_group_req_1 = ?, tc_group_app_1 = ? where targ_id = ? and panel_id=?));
  my $update_group_2 = $dbh1->prepare(qq(update target set tc_group_req_2 = ?, tc_group_app_2 = ? where targ_id = ? and panel_id=?));

  my %processed_group;

  #print STDERR ("GROUP for : $param{panel}\n");
  $group_query->execute($param{panel});
  while (my($prop_id, $targid, $group_name, 
	    $group_interval, $group_interval_1, $group_interval_2 )
	     = $group_query->fetchrow_array) {
    print $fh "Proposal\tGroup\n";
    print $fh "$prop_id\t$group_name\n";
    #print "GROUP processing $prop_id  $group_name\n";

    foreach my $ao (0, 1, 2){
      next if $ao == 1 and !$group_interval_1;
      next if $ao == 2 and !$group_interval_2;

      print $fh "AO: $ao\n=====\n" ;

      #if ($processed_group{"$prop_id $group_name $group_interval"}) {
	#die "Group $group_name in proposal $prop_id has more than one group interval\n";
      #}
      #else 
      {
	$processed_group{"$prop_id $group_name $group_interval"} = 1;
	
	$group_time->execute($group_name, $prop_id,$param{panel});
	my ($tot_gr_time) = $group_time->fetchrow_array;
	$group_time->finish;
      
      #print STDERR "group: $prop_id, $group_name, $tot_gr_time\n";
	my $tc_group = $group_interval / ($tot_gr_time / 86.4);
	print $fh "T ksec\tT days\Score\n";
	print $fh $tot_gr_time/86.4, "\t$tot_gr_time\t$tc_group\n";

#	my $update_query = $update_group;
#	$update_query = $update_group_1 if $ao == 1;
#	$update_query = $update_group_2 if $ao == 2;
	if ($ao == 0) {
	  $update_group->execute($tc_group, $tc_group, $targid,$param{panel});
	  $update_group->finish;
	}
	elsif ($ao == 1) {
	  $update_group_1->execute($tc_group, $tc_group, $targid,$param{panel});
	  $update_group_1->finish;
	}
	elsif ($ao == 2) {
	  $update_group_2->execute($tc_group, $tc_group, $targid,$param{panel});
	  $update_group_2->finish;
	}
      }
    }
  }
  $group_query->finish;

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
              "verbose",
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

calc_tc.pl [options]

=head1 OPTIONS

B<calc_tc.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name on postgresql server

=item B<-log>

Name of log file

=item B<-panel>

Panel to operate on

=item B<-help>

displays documentation for B<calc_tc.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script calculates the TC value for roll, phase and time constraints from
their values in the rollreq, phasereq and timereq tables and updates the
target table with those totals.

=head1 AUTHOR

Sherry L. Winkelman
