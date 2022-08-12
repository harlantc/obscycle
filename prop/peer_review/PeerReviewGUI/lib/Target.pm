#
# Target.pm - This object contains data associated with a target,
#             too objects when necessary
#
# Purpose: Provides a single place to access elements of a target
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Target;
use strict;
use Carp;
use Data::Dumper;
use Too;
use config;

use base qw(Class::Accessor::Ref);
Target->mk_accessors(qw(prop_id targ_name at_hel detector grating targ_id
            targ_num alt_id time_crit rev_too response_time too_prob_req
			too_prob_app too_start
                        req_time req_time_1 req_time_2
                        app_time app_time_1 app_time_2
                        req_hel req_hel_1 req_hel_2
                        app_hel app_hel_1 app_hel_2
			targ_status targ_status_1 targ_status_2
                        num_obs_req num_obs_req_1 num_obs_req_2
                        num_obs_app num_obs_app_1 num_obs_app_2
			num_pnt_req num_pnt_req_1 num_pnt_req_2
                        num_pnt_app num_pnt_app_1 num_pnt_app_2
                        tax tax_1 tax_2 tax_req tax_req_1 tax_req_2
                        tc tc_1 tc_2 tc_req tc_req_1 tc_req_2
			rc_score rc_score_1 rc_score_2
			tc_coor tc_roll
                        tc_const_rem tc_const_rem_1 tc_const_rem_2
                        tc_phase tc_phase_1 tc_phase_2
                        tc_time tc_time_1 tc_time_2
			tc_group_app tc_group_app_1 tc_group_app_2
                        tc_group_req tc_group_req_1 tc_group_req_2
                        tc_too tc_too_1 tc_too_2
                        tc_too_req tc_too_req_1 tc_too_req_2
                        tc_monitor tc_monitor_1 tc_monitor_2
                        tc_monitor_req tc_monitor_req_1 tc_monitor_req_2
			tc_override tc_override_1 tc_override_2
			tc_grade_app tc_grade_app_1 tc_grade_app_2
                        tc_grade_req tc_grade_req_1 tc_grade_req_2
                        monitor raster_scan grid_name max_radius uninterrupt 
			group_obs group_name 
                        group_interval group_interval_1 group_interval_2
                        multitelescope 
			constr_in_remarks panel_id toos
			alt_grp_name alt_req_cnt alt_app_cnt 
			time_obs_req time_obs_req_1 time_obs_req_2
                        time_obs_app time_obs_app_1 time_obs_app_2
                        changed mcop
			TARGET_PROPERTIES TOO_PROPERTIES dbh 
			verbose taxrate segtime  
		       ));
Target->mk_refaccessors(qw(at_hel too_prob_app alt_app_cnt
                           app_time app_time_1 app_time_2
                           targ_status targ_status_1 targ_status_2
			   num_obs_app num_obs_app_1 num_obs_app_2
                           time_obs_app time_obs_app_1 time_obs_app_2
                           tax tax_1 tax_2 tc tc_1 tc_2
                           tc_grade_app tc_grade_app_1 tc_grade_app_2
			   tc_grade_req tc_grade_req_1 tc_grade_req_2
                           tc_group_app tc_group_app_1 tc_group_app_2
                           tax_req tax_req_1 tax_req_2
                           tc_req tc_req_1 tc_req_2
			   tc_too tc_too_1 tc_too_1
			   rc_score rc_score_1 rc_score_2
			   tc_too_req tc_too_req_1 tc_too_req_1
			   tc_monitor tc_monitor_1 tc_monitor_1
			   tc_monitor_req tc_monitor_req_1 tc_monitor_req_1
                           tc_coor tc_roll 
                           tc_const_rem tc_const_rem_1 tc_const_rem_2
                           tc_phase tc_phase_1 tc_phase_2
			   tc_time tc_time_1 tc_time_2
			  ));

## Class Method ##
# Name: new
# Purpose: create a new Target object
# Parameters: database handle
#             proposal id
#             target index
#             target properties
#             too properties
#             panel id
#             verbosity
# Returns: Target object
sub new {
    my $self = {};
    bless $self,shift;
    $self->_init(@_);
    print "Target::new - creating new object\n" if $self->verbose > 2;
    return $self;
}


## Internal Utility ##
# Name: _init
# Purpose: initializes a new Target object
# Parameters: database handle
#             proposal id
#             target index
#             target properties
#             too properties
#             panel id
#             verbosity
# Returns: Target object
sub _init {
    my ($self, $dbh, $taxrate, $segtime, $prop_id, $target_index,
	$TargProps, $TooProps, $panel_id, $verbose) = @_;
    my %init = %$self;
    my %changed = ();
    $init{changed} = \%changed;
    $init{dbh} = $dbh;
    $init{taxrate} = $taxrate;
    $init{segtime} = $segtime;
    $init{panel_id} = $panel_id;
    $init{verbose} = $verbose;

    $init{TARGET_PROPERTIES} = $TargProps;
    $init{TOO_PROPERTIES} = $TooProps;

    my $target_query = $dbh->prepare(qq(select *, 
                                        req_time/num_obs_req as time_obs_req
                                        from target where prop_id=? and 
					req_time > 0  and
                                        targ_num = ? and panel_id = ?
                                        UNION
                                        select *,0 as time_obs_req
                                        from target where prop_id = ? and 
                                        (req_time = 0 or req_time is NULL) and
					targ_num = ? and panel_id = ?));
    $target_query->execute($prop_id, $target_index, $panel_id,$prop_id,$target_index,$panel_id);
    my $targ_ref = $target_query->fetchrow_hashref('NAME_lc');
    $target_query->finish;
    my @TargProps = @$TargProps;
    foreach my $xx (@TargProps) {
      if ($xx =~ /time/i && $xx !~ /response/ && $xx !~ /time_crit/i) {
        my $val = $targ_ref->{$xx};
        if (defined($targ_ref->{$xx})) {
          $val = sprintf("%7.2f",$val );
          $init{$xx} = $val;
        } else {
          $init{$xx} = $targ_ref->{$xx} ;
        }
      } else {
        $init{$xx} = $targ_ref->{$xx} ;
      }
    }

#    $init{$_} = $targ_ref->{$_} foreach @TargProps;

    my $target_1_query = $dbh->prepare(qq(select req_time_1/num_obs_req_1 from target where num_obs_req_1 > 0 and prop_id = ? and targ_num = ? and panel_id = ?));
    $target_1_query->execute($prop_id, $target_index, $panel_id);
    my ($time_obs_req_1) = $target_1_query->fetchrow_array;
    $target_1_query->finish;
    $init{time_obs_req_1} = $time_obs_req_1;

    my $target_2_query = $dbh->prepare(qq(select req_time_2/num_obs_req_2 from target where num_obs_req_2 > 0 and prop_id = ? and targ_num = ? and panel_id = ?));
    $target_2_query->execute($prop_id, $target_index, $panel_id);
    my ($time_obs_req_2) = $target_2_query->fetchrow_array;
    $target_2_query->finish;
    $init{time_obs_req_2} = $time_obs_req_2;

    # The target name for grids is the targ_name + grid_name
    if ($init{raster_scan} =~ /Y/) {
      $init{targ_name} .= "\n(" . $init{grid_name} . ")" if uc($init{targ_name}) ne uc($init{grid_name});
    }

    # Need to calculate time_obs_app independently in case num_obs_app is 0
    if (!$init{num_obs_app}) {
      $init{time_obs_app} = $init{time_obs_req};
    }
    else {
      $init{time_obs_app} = $init{app_time} / $init{num_obs_app};
    }
    if (!$init{num_obs_app_1}) {
      $init{time_obs_app_1} = $init{time_obs_req_1};
    }
    else {
      $init{time_obs_app_1} = $init{app_time_1} / $init{num_obs_app_1};
    }
    if (!$init{num_obs_app_2}) {
      $init{time_obs_app_2} = $init{time_obs_req_2};
    }
    else {
      $init{time_obs_app_2} = $init{app_time_2} / $init{num_obs_app_2};
    }

    # init truncates to 2 decimal, so be consistent
    $init{time_obs_req} = sprintf "%6.2f", $init{time_obs_req};
    $init{time_obs_app} = sprintf "%6.2f", $init{time_obs_app};
    $init{time_obs_req_1} = sprintf "%6.2f", $init{time_obs_req_1};
    $init{time_obs_app_1} = sprintf "%6.2f", $init{time_obs_app_1};
    $init{time_obs_req_2} = sprintf "%6.2f", $init{time_obs_req_2};
    $init{time_obs_app_2} = sprintf "%6.2f", $init{time_obs_app_2};

    my $alt_targ_query = $dbh->prepare(qq(select alt_grp_name, req_cnt, app_cnt
                                          from alternate_target_group where
                                          prop_id = ? and alt_id = ? and 
                                          panel_id = ?));    
    if ($init{alt_id}) {
      $alt_targ_query->execute($prop_id, $init{alt_id}, $panel_id);
      my ($alt_grp_name, $alt_req_cnt, 
	  $alt_app_cnt) = $alt_targ_query->fetchrow_array;
      $alt_targ_query->finish;
      $init{alt_grp_name} = $alt_grp_name;
      $init{alt_req_cnt} = $alt_req_cnt;
      $init{alt_app_cnt} = $alt_app_cnt;
    }

    # TOO and Monitors look the same
    my @too_list;
    my $reps = $targ_ref->{num_obs_req} + $targ_ref->{num_obs_req_1} + 
      $targ_ref->{num_obs_req_2};
    for (my $i = 1; $i <= $reps; $i++) {
	my $too = new Too($dbh, $segtime, $prop_id, $targ_ref->{targ_id}, 
			  $i, $TooProps, $panel_id, $verbose);
	$too_list[$i] = $too;
    }
    $init{toos} = \@too_list;
    %$self = %init;
}

## Class Method ##
# Name: dump
# Purpose: does a data dump of the target object
# Parameters: none
# Returns: nothing
sub dump {
    my $self = shift;
    print Dumper($self);
}

## Class Method ##
# Name: num_pnt
# Purpose:return number of approved pointings for target (or all pointings, if
#         status is B
# Parameters: initialize flag
# Returns: integer
sub num_pnt {
  my $self = shift;
  my $init = shift;
  # Assumptions:
  #   - grid TOOs only have a trigger observation
  # - number of pointings in a monitor is equal to the number of observations
  my $segments;
  if ($self->targ_status =~ /Y/ or $init) {
    if ($self->raster_scan =~ /Y/) {
      # total number of pointings is the number of pointings per observation
      # multiplied by the number of observations in the monitoring sequence
      $segments = int(($self->app_time / $self->num_obs_app) / $self->segtime);
      $segments++ if 
	(($self->app_time / $self->num_obs_app) % $self->segtime) > 0.0;

      $segments *= $self->num_obs_app;
    }
    elsif (defined $self->response_time or $self->monitor =~ /Y|P/) {
      # total number of pointings is the sum of the pointings for the trigger
      # and followup observations
      my $toos = $self->toos;
      my @too_list = @$toos;
      for (my $i = 1; $i < scalar @too_list; $i++) {
	$segments += $too_list[$i]->num_pnt if 
	  $too_list[$i]->ao == 0 and ($too_list[$i]->obs_status =~ /Y/ or 
				      $init);
      }
    }
    else {
      $segments = int($self->app_time / $self->segtime);
      $segments++ if ($self->app_time % $self->segtime) > 0.0;
    }
  }
  elsif ($self->targ_status =~ /$STAT_BPP/) {
    $segments = $self->num_obs_req;
  }
  else {
    $segments = 0;
  }
  $segments = 0 if !$segments;
  return sprintf("%3d",$segments);
}

## Class Method ##
# Name: num_pnt_1
# Purpose:return number of approved pointings for target (or all pointings, if
#         status is B for cycle n+1
# Parameters: initialize flag
# Returns: integer
sub num_pnt_1 {
  my $self = shift;
  my $init = shift;
  # Assumptions:
  #   - grid TOOs only have a trigger observation
  # - number of pointings in a monitor is equal to the number of observations
  my $segments;

  if ($self->targ_status_1 =~ /Y/ or $init) {
    if ($self->raster_scan =~ /Y/ and $self->num_obs_app_1) {
      # total number of pointings is the number of pointings per observation
      # multiplied by the number of observations in the monitoring sequence
      $segments = int(($self->app_time_1 / $self->num_obs_app_1) / $self->segtime);
      $segments++ if 
	(($self->app_time_1 / $self->num_obs_app_1) % $self->segtime) > 0.0;
      $segments *= $self->num_obs_app_1;
    }
    elsif (defined $self->response_time or $self->monitor =~ /Y|P/) {
      # total number of pointings is the sum of the pointings for the trigger
      # and followup observations
      my $toos = $self->toos;
      my @too_list = @$toos;
      for (my $i = 1; $i < scalar @too_list; $i++) {
	$segments += $too_list[$i]->num_pnt if 
	  $too_list[$i]->ao == 1 and ($too_list[$i]->obs_status =~ /Y/ or 
				      $init);
      }
    }
    else {
      $segments = int($self->app_time_1 / $self->segtime);
      $segments++ if ($self->app_time_1 % $self->segtime) > 0.0;
    }
  }
  elsif ($self->targ_status_1 =~ /$STAT_BPP/) {
    $segments = $self->num_obs_req_1;
  }
  else {
    $segments = 0;
  }
  $segments = 0 if !$segments;
  return $segments;
}

## Class Method ##
# Name: num_pnt_2
# Purpose:return number of approved pointings for target (or all pointings, if
#         status is B for cycle n+2
# Parameters: initialize flag
# Returns: integer
sub num_pnt_2 {
  my $self = shift;
  my $init = shift;
  # Assumptions:
  #   - grid TOOs only have a trigger observation
  # - number of pointings in a monitor is equal to the number of observations
  my $segments;

  if ($self->targ_status_2 =~ /Y/ or $init) {
    if ($self->raster_scan =~ /Y/ and $self->num_obs_app_2) {
      # total number of pointings is the number of pointings per observation
      # multiplied by the number of observations in the monitoring sequence
      $segments = int(($self->app_time_2 / $self->num_obs_app_2) / $self->segtime);
      $segments++ if 
	(($self->app_time_2 / $self->num_obs_app_2) % $self->segtime) > 0.0;
      $segments *= $self->num_obs_app_2;
    }
    elsif (defined $self->response_time or $self->monitor =~ /Y|P/) {
      # total number of pointings is the sum of the pointings for the trigger
      # and followup observations
      my $toos = $self->toos;
      my @too_list = @$toos;
      for (my $i = 1; $i < scalar @too_list; $i++) {
	$segments += $too_list[$i]->num_pnt if 
	  $too_list[$i]->ao == 2 and ($too_list[$i]->obs_status =~ /Y/ or 
				      $init);
      }
    }
    else {
      $segments = int($self->app_time_2 / $self->segtime);
      $segments++ if ($self->app_time_2 % $self->segtime) > 0.0;
    }
  }
  elsif ($self->targ_status_2 =~ /$STAT_BPP/) {
    $segments = $self->num_obs_req_2;
  }
  else {
    $segments = 0;
  }
  $segments = 0 if !$segments;
  return $segments;
}

sub num_time {
  my $self = shift;
  my $init = shift;
  
  # Assumptions:
  #   - monitoring TOOs only have a trigger observation (no followups) in too
  #   - grid TOOs only have a trigger observation

 
  my $time = 0;

  if ($self->targ_status !~ /$STAT_BPP/ or $init) {
    if (defined $self->response_time or $self->monitor eq 'Y' or $self->monitor eq 'P') {
      # total target time is the sum of the times for the trigger
      # and followup observations
      my $toos = $self->toos;
      my @too_list = @$toos;
      for (my $i = 1; $i < scalar @too_list; $i++) {
#print "Target 359: order is $i\n";;
	$time += $too_list[$i]->app_obs_time if 
	  $too_list[$i]->ao == 0 and 
	    ($too_list[$i]->obs_status =~ /Y/ or $init);
#	my $obstatus = $too_list[$i]->obs_status;
      }
    }
    else {
      $time = $self->app_time;
    }
  }
  elsif ($self->targ_status =~ /$STAT_BPP/) {
    $time = $self->req_time;
  }
  else {
    $time = 0;
  }

  return sprintf("%7.2f",$time);
}

sub num_time_1 {
  my $self = shift;
  my $init = shift;
  
  # Assumptions:
  #   - monitoring TOOs only have a trigger observation (no followups) in too
  #   - grid TOOs only have a trigger observation

 
  my $time = 0;

  if ($self->targ_status_1 !~ /$STAT_BPP/ or $init) {
    if (defined $self->response_time or $self->monitor eq 'Y' or $self->monitor eq 'P') {
      # total target time is the sum of the times for the trigger
      # and followup observations
      my $toos = $self->toos;
      my @too_list = @$toos;
      for (my $i = 1; $i < scalar @too_list; $i++) {
	$time += $too_list[$i]->app_obs_time if 
	  $too_list[$i]->ao == 1 and 
	    ($too_list[$i]->obs_status =~ /Y/ or $init);
      }
    }
    else {
      $time = $self->app_time_1;
    }
  }
  elsif ($self->targ_status_1 =~ /$STAT_BPP/) {
    $time = $self->req_time_1;
  }
  else {
    $time = 0;
  }

  return sprintf("%7.2f",$time);
}

sub num_time_2 {
  my $self = shift;
  my $init = shift;
  
  # Assumptions:
  #   - monitoring TOOs only have a trigger observation (no followups) in too
  #   - grid TOOs only have a trigger observation

 
  my $time = 0;

  if ($self->targ_status_2 !~ /$STAT_BPP/ or $init) {
    if (defined $self->response_time or $self->monitor eq 'Y' or $self->monitor eq 'P') {
      # total target time is the sum of the times for the trigger
      # and followup observations
      my $toos = $self->toos;
      my @too_list = @$toos;
      for (my $i = 1; $i < scalar @too_list; $i++) {
	$time += $too_list[$i]->app_obs_time if 
	  $too_list[$i]->ao == 2 and 
	    ($too_list[$i]->obs_status =~ /Y/ or $init);
      }
    }
    else {
      $time = $self->app_time_2;
    }
  }
  elsif ($self->targ_status_2 =~ /$STAT_BPP/) {
    $time = $self->req_time_2;
  }
  else {
    $time = 0;
  }
  return sprintf("%7.2f",$time);
}

## Class Method ##
# Name: num_followups
# Purpose: return the number of approved followups in a cycle
# Parameters: ao (0-2)
# Returns: integer
sub num_followups {
  my $self = shift;
  my $ao = shift;

  die "num_followups requires an ao (0-2)\n" if !defined $ao;
  my $count = 0;
  my $toos = $self->toos;
  for (my $i = 0; $i < scalar @$toos; $i++) {
    if (defined $$toos[$i]){
      $count++ if $$toos[$i]->obs_status =~ /Y/ and 
	$$toos[$i]->trigflag =~ /F/ and $$toos[$i]->ao == $ao;
    }
  }
  return $count;
}

## Class Method ##
# Name: num_tc
# Purpose: return 1 if there are non-monitoring constraints on the target
# Parameters: none
# Returns: 0 or 1
sub num_tc {
  my $self = shift;
  my $total = 0;
  my $grade = $self->calc_tc_grade;
  $total = 1 if $grade =~ /\w+/;
  return $total;
}

## Class Method ##
# Name: num_tc_1
# Purpose: return 1 if there are non-monitoring constraints on the target
# Parameters: none
# Returns: 0 or 1
sub num_tc_1 {
  my $self = shift;
  my $total = 0;
  my $grade = $self->calc_tc_grade_1;
  $total = 1 if $grade =~ /\w+/;
  return $total;
}

## Class Method ##
# Name: num_tc_2
# Purpose: return 1 if there are non-monitoring constraints on the target
# Parameters: none
# Returns: 0 or 1
sub num_tc_2 {
  my $self = shift;
  my $total = 0;
  my $grade = $self->calc_tc_grade_2;
  $total = 1 if $grade =~ /\w+/;
  return $total;
}

## Internal Utility ##
# Name: calc_tax
# Purpose: calculate the tax for a target
# Parameters: none
# Returns: tax
# Note: the raster part is lifted from Paul Green's code
sub calc_tax {
  my $self = shift;
  my $fh = shift;
  my $tax = 0;

  if ($self->raster_scan =~ /N/) {
    $tax = $self->num_pnt * $self->taxrate;
  }
  else {
    ($tax, my $tc) = $self->grid_tax($self->taxrate, 0) if !$fh;
    ($tax, my $tc) = $self->grid_tax($self->taxrate, 0, $fh) if $fh;
  }
  $tax = 0 if !$tax;
  if ($self->monitor eq 'N' || $self->monitor eq 'P') {
    # only use probability on non-monitor for slew tax
    $tax = $tax * $self->too_prob_app;
  }
  return $tax;
}

## Internal Utility ##
# Name: calc_tax_1
# Purpose: calculate the tax for a target for cycle n+1
# Parameters: none
# Returns: tax
# Note: the raster part is lifted from Paul Green's code
sub calc_tax_1 {
  my $self = shift;
  my $fh = shift;
  my $tax = 0;

  if ($self->raster_scan =~ /N/) {
    $tax = $self->num_pnt_1 * $self->taxrate;
  }
  else {
    ($tax, my $tc) = $self->grid_tax($self->taxrate, 1) if !$fh;
    ($tax, my $tc) = $self->grid_tax($self->taxrate, 1, $fh) if $fh;
  }
  $tax = 0 if !$tax;
  if ($self->monitor eq 'N' || $self->monitor eq 'P') {
    # only use probability on non-monitor for slew tax
    $tax = $tax * $self->too_prob_app;
  }
  return $tax;
}

## Internal Utility ##
# Name: calc_tax_2
# Purpose: calculate the tax for a target for cycle n+2
# Parameters: none
# Returns: tax
# Note: the raster part is lifted from Paul Green's code
sub calc_tax_2 {
  my $self = shift;
  my $fh = shift;
  my $tax = 0;

  if ($self->raster_scan =~ /N/) {
    $tax = $self->num_pnt_2 * $self->taxrate;
  }
  else {
    ($tax, my $tc) = $self->grid_tax($self->taxrate, 2) if !$fh;
    ($tax, my $tc) = $self->grid_tax($self->taxrate, 2, $fh) if $fh;
  }
  $tax = 0 if !$tax;
  if ($self->monitor eq 'N' || $self->monitor eq 'P') {
    # only use probability on non-monitor for slew tax
    $tax = $tax * $self->too_prob_app;
  }
  return $tax ;
}

## Internal Utility ##
# Name: calc_tc
# Purpose: calculate the tc for a target
# Parameters: none
# Returns: tc
sub calc_tc {
  my $self = shift;
  my $init = shift;
  my $tc = 0;

  # calculate the tally only if there is a grade
  #  - this ensures that the tally is zero if the constraints
  #    have been removed due to edits of other parameters
  my $grade = $self->calc_tc_grade($init);
  if ($grade =~ /\w+/){
    if ($self->raster_scan =~ /N/) {
      $tc = $self->num_pnt($init);

      # don't count 1st segment for monitor or TOO and no other constraints
      if ($self->tc_monitor > 0 || $self->tc_too > 0) {
        if ($self->tc_time <= 0 && 
            $self->tc_phase <= 0   && 
            $self->tc_roll <= 0  && 
            $self->uninterrupt !~ /Y/i && 
            $self->tc_const_rem <= 0 && 
            $self->multitelescope !~ /Y/i &&
            $self->tc_override <= 0 ) {
          $tc--;
        }
      }
    }
    else {
      (my $tax, $tc) = $self->grid_tax($self->taxrate, 0);
    }
  }

  return sprintf "%4.2f", $tc * $self->too_prob_app;
}

## Internal Utility ##
# Name: calc_tc_1
# Purpose: calculate the tc for a target for cycle n+1
# Parameters: none
# Returns: tc
sub calc_tc_1 {
  my $self = shift;
  my $init = shift;
  my $tc = 0;

  # calculate the tally only if there is a grade
  #  - this ensures that the tally is zero if the constraints
  #    have been removed due to edits of other parameters
  my $grade = $self->calc_tc_grade_1($init);
  if ($grade =~ /\w+/){
    if ($self->raster_scan =~ /N/) {
      $tc = $self->num_pnt_1($init);
      if ($self->tc_monitor_1 > 0 || $self->tc_too_1 > 0) {
        if ($self->tc_time_1 <= 0 && 
            $self->tc_phase_1 <= 0 && 
            $self->tc_roll <= 0 && 
            $self->uninterrupt !~ /Y/i && 
            $self->tc_const_rem_1 <= 0 && 
            $self->multitelescope !~ /Y/i &&
            $self->tc_override_1 <= 0 ) {
          # should only subtract 1 if this ao is first target in sequence
          $tc-- if ($self->num_obs_req == 0) ;
        }
      }
    }
    else {
      (my $tax, $tc) = $self->grid_tax($self->taxrate, 1);
    }

  }

  return sprintf "%4.2f", $tc * $self->too_prob_app;
}

## Internal Utility ##
# Name: calc_tc_2
# Purpose: calculate the tc for a target for cycle n+2
# Parameters: none
# Returns: tc
sub calc_tc_2 {
  my $self = shift;
  my $init = shift;
  my $tc = 0;

  # calculate the tally only if there is a grade
  #  - this ensures that the tally is zero if the constraints
  #    have been removed due to edits of other parameters
  my $grade = $self->calc_tc_grade_2($init);
  if ($grade =~ /\w+/){
    if ($self->raster_scan =~ /N/) {
      $tc = $self->num_pnt_2($init);
      if ($self->tc_monitor_2 > 0 || $self->tc_too_2 > 0) {
        if ($self->tc_time_2 <= 0 && 
            $self->tc_phase_2 <= 0 && 
            $self->tc_roll <= 0 && 
            $self->uninterrupt !~ /Y/i && 
            $self->tc_const_rem_2 <= 0 && 
            $self->multitelescope !~ /Y/i &&
            $self->tc_override_2 <= 0 ) {
          # should only subtract 1 if this ao is first target in sequence
          if ($self->num_obs_req == 0 && $self->num_obs_req_1 == 0) {
            $tc--;
          }
        }
      }
    }
    else {
      (my $tax, $tc) = $self->grid_tax($self->taxrate, 2);
    }


  }

  return sprintf "%4.2f", $tc * $self->too_prob_app;
}

## Class Method ##
# Name: grid_tax
# Purpose: determines the grid tax 
# Parameters: tax_base
#             ao = 0-2
#             fh: prints statements to file handle (optional)
# Returns: tax, num_grps
# Note: the raster part is lifted from Paul Green's code
sub grid_tax {
  my $self = shift;
  my $tax_base = shift;
  my $ao = shift;
  my $fh = shift;
  my $verbose = $self->verbose;

  die "grid_tax requires an ao (0-2)\n" if !defined $ao;
  #my $max_pnt_time = 45;
  #my $max_grp_time = 90;
  my $max_pnt_time = 30;
  my $max_grp_time = 30;

  my $grid_tax = 0;
  my ($Npt, $ti);

  if ($ao == 0) {
    $Npt = $self->num_obs_app;
    $ti = $self->app_time;
  }
  elsif ($ao == 1) {
    $Npt = $self->num_obs_app_1;
    $ti = $self->app_time_1;
  }
  elsif ($ao == 2) {
    $Npt = $self->num_obs_app_2;
    $ti = $self->app_time_2;
  }
  if ($ti > 0) {
    $ti /= $Npt;
  } else {
    printf STDERR "Error: time is 0 for grid_tax calculation for %d\n",$self->prop_id;
  }
  
  my $newgp=1;
  my $ngp=0;
  my $nexp=0;
  my $exptot=0;
  my $tgp=0;
  my $gptax=0;
  
  if ($ti < $max_pnt_time){
    if ($fh) {
      print $fh "Calculations for grid\n";
      print $fh "newgp nexp tgp   gptax  exptot \n";
    }
    if ($verbose) {
      print "Calculations for grid\n";
      print "newgp nexp tgp   gptax  exptot \n";
    }

    for ($nexp = 1; $nexp <= $Npt; $nexp++){
      if ($newgp == 1) {
	$gptax = 0;
	$ngp = $ngp + 1; 
	$exptot = $exptot + $ti; 
	$tgp = $tgp + $ti + $tax_base;
	$gptax = $gptax + $tax_base; 
	$grid_tax = $grid_tax + $tax_base;
	print $fh sprintf "%3d %3d %6.1f %6.1f %6.1f \n", 
	  $newgp,$nexp,$tgp,$gptax,$exptot if $fh;
	print sprintf "%3d %3d %6.1f %6.1f %6.1f \n", 
	  $newgp,$nexp,$tgp,$gptax,$exptot if $verbose;
	$newgp = 0
      }
      else {
	if($newgp == 0 && ($tgp + $ti + ($tax_base / 3 )) <= $max_grp_time) {
	  $exptot = $exptot + $ti;
	  $tgp = $tgp + $ti + ($tax_base / 3); 
	  $gptax = $gptax + ($tax_base / 3); 
	  $grid_tax = $grid_tax + ($tax_base / 3);
	} else {
	  $exptot = $exptot + $ti;
	  $gptax = $gptax + $tax_base; 
	  $grid_tax = $grid_tax + $tax_base ;
        }
	printf $fh sprintf "%3d %3d %6.1f %6.1f %6.1f \n", 
	  $newgp,$nexp,$tgp,$gptax,$exptot if $fh;
	print sprintf "%3d %3d %6.1f %6.1f %6.1f \n", 
	  $newgp,$nexp,$tgp,$gptax,$exptot if $verbose;

	if( ($tgp + $ti + ($tax_base / 3)) > $max_grp_time) {
	  $tgp = 0; 
	  $newgp = 1;
	  print $fh "\n" if $fh;
	  print "\n" if $verbose;
	}
      }
    }
  }
  else  {
    $Npt *= int(($ti +$max_pnt_time-1)/$max_pnt_time);
    $ngp = $Npt; 
    $exptot = $Npt * $ti; 
    $grid_tax = $tax_base * $Npt;
  }

  my $cxotime= $exptot + $grid_tax;   
  if ($fh) {
    print $fh " \n Number of pointings $Npt \n";
    print $fh " Expo per pointing $ti    \n";
    print $fh " Number of groups $ngp    \n";
    print $fh " Total exposure $exptot  \n";
    print $fh " Total slew tax $grid_tax  \n";
    print $fh " Total exposure + tax $cxotime  \n\n";
  }
  if ($verbose) {
    print " \n Number of pointings $Npt \n";
    print " Expo per pointing $ti    \n";
    print " Number of groups $ngp    \n";
    print " Total exposure $exptot  \n";
    print " Total slew tax $grid_tax  \n";
    print " Total exposure + tax $cxotime  \n\n";
  }

  return $grid_tax, $ngp;
}

## Class Method ##
# Name: calc_tc_group
# Purpose: determines the tc_group score 
# Parameters: none
# Returns: real
sub calc_tc_group {
  my $self = shift;

  # Determine the tc_group value if this is a grouped target
  # The dimensionless parameter for Group Observations is: 
  #       (TIME INTERVAL FOR THE GROUP)
  # -----------------------------------------
  # (TOTAL DURATION OF OBSERVATIONS IN GROUP)
  my $tc_group;
  if ($self->group_obs =~ /Y/ and $self->targ_status !~ /N|G/) {
    my $group_time_query = qq(select sum(app_time) from target where 
                              targ_status not in ('N', 'G') and
                              group_name = ? and prop_id = ? and panel_id = ?);
    my $group_time = $self->dbh->prepare($group_time_query);
    $group_time->execute($self->group_name, $self->prop_id, $self->panel_id);
    my ($tot_gr_time) = $group_time->fetchrow_array;
    $group_time->finish;
    $tc_group = $self->group_interval / ($tot_gr_time / 86.4);
  }

  return $tc_group;
}

## Class Method ##
# Name: calc_tc_group_1
# Purpose: determines the tc_group score for cycle n+1
# Parameters: none
# Returns: real
sub calc_tc_group_1 {
  my $self = shift;

  # Determine the tc_group value if this is a grouped target
  # The dimensionless parameter for Group Observations is: 
  #       (TIME INTERVAL FOR THE GROUP)
  # -----------------------------------------
  # (TOTAL DURATION OF OBSERVATIONS IN GROUP)
  my $tc_group;
  if ($self->group_obs =~ /Y/ and $self->targ_status_1 !~ /N|G/ and 
      $self->mcop eq 'Y') {
#print $self->targ_id, "\tgroup name: ", $self->group_name, "\n";
    my $group_time_query = qq(select sum(app_time_1) from target where 
                              targ_status not in ('N', 'G') and
                              targ_status_1 not in ('N', 'G') and
                              group_name = ? and prop_id = ? and panel_id = ?);
    my $group_time = $self->dbh->prepare($group_time_query);
    $group_time->execute($self->group_name, $self->prop_id, $self->panel_id);
    my ($tot_gr_time) = $group_time->fetchrow_array;
    $group_time->finish;
    $tc_group = $self->group_interval_1 / ($tot_gr_time / 86.4);
  }

  return $tc_group;
}

## Class Method ##
# Name: calc_tc_group_2
# Purpose: determines the tc_group score for cycle n+2
# Parameters: none
# Returns: real
sub calc_tc_group_2 {
  my $self = shift;
  my $tc_group = undef;

  # Determine the tc_group value if this is a grouped target
  # The dimensionless parameter for Group Observations is: 
  #       (TIME INTERVAL FOR THE GROUP)
  # -----------------------------------------
  # (TOTAL DURATION OF OBSERVATIONS IN GROUP)
  my $tc_group;
  if ($self->group_obs =~ /Y/ and $self->targ_status_2 !~ /N|G/ and 
      $self->mcop eq 'Y') {
    my $group_time_query = qq(select sum(app_time_2) from target where 
                              targ_status not in ('N', 'G') and
                              targ_status_1 not in ('N', 'G') and
                              group_name = ? and prop_id = ? and panel_id = ?);
    my $group_time = $self->dbh->prepare($group_time_query);
    $group_time->execute($self->group_name, $self->prop_id, $self->panel_id);
    my ($tot_gr_time) = $group_time->fetchrow_array;
    $group_time->finish;
    $tc_group = $self->group_interval_2 / ($tot_gr_time / 86.4);
  }

  return $tc_group;
}

## Class Method ##
# Name: calc_tc_too
# Purpose: determines the tc_too or tc_mon score for an ao
# Parameters: ao
# Returns: real
sub calc_tc_too {
  my $self = shift;
  my $ao = shift;
  my $tc_too = undef;
  die "calc_tc_too requires an ao (0-2)\n" if !defined $ao;

  if (defined $self->response_time or $self->monitor =~ /Y/ and 
      $self->targ_status !~ /N|$STAT_BPP/) {
    my $toos = $self->toos;

    # wrong - fixed for cycle 15
    # Find which segment has the smallest fractol for the ao and use the
    # pre-min/max-lead values from that segment
    # right:
    # Find which segment has the smallest Imax for the ao and use the fractol 
    # from that segment
    #
    # Also need to get the max segment time from the followup
    my $imax = 0.0;
    my $fractol = 1.0;
    my $max_time = 0;
    my $index = 0;
    # Start counting from 2 because the trigger does not have a fractol
    for (my $i = 2; $i < scalar @$toos; $i++) {
      next unless $$toos[$i]{ao} == $ao and $$toos[$i]{obs_status} eq 'Y';
      if ($imax == 0 || $$toos[$i]{pre_max_lead} < $imax) {
	$imax = $$toos[$i]{pre_max_lead};
	$fractol = $$toos[$i]{fractol};
	$index = $i;
      } elsif ($$toos[$i]{pre_max_lead} == $imax &&
	       $$toos[$i]{fractol} < $fractol ) {
	$imax = $$toos[$i]{pre_max_lead};
	$fractol = $$toos[$i]{fractol};
	$index = $i;
      }
 

      $max_time = $$toos[$i]->app_obs_time / 86.4 if 
	$$toos[$i]->app_obs_time / 86.4 > $max_time;
    }

    $tc_too = $$toos[$index]->pre_max_lead * $$toos[$index]->fractol / $max_time if $index;
  }
#print STDERR "calc_tc toos: $ao  $tc_too\n";
  return $tc_too;
}

## Class Method ##
# Name: calc_tc_grade
# Purpose: determines the tc_variable grade 
# Parameters: none
# Returns: grade (e|a|d)
sub calc_tc_grade {
  my $self = shift;
  my $init = shift;

  print "**** Proposal ", $self->prop_id, "; Target ", 
    $self->targ_id, " ****\n" if $self->verbose > 1;
  # The following TC grades contribute to the final tc_grade:
  #     tc_monitor
  #     tc_group
  #     tc_too
  #     uninterrupt
  #     tc_coor
  #     tc_const_rem
  #     tc_roll
  #     tc_time
  #     tc_phase
  #     tc_override

  my $grade = '';
  return undef if $self->time_crit =~ /N/ and $self->rev_too =~ /N/;

  # Override: E(1)  A(2)  D(3)
  # If tc_override is set, that is the grade and skip the rest
  if ($self->tc_override) {
    $grade = '' if $self->tc_override == 0;
    $grade = 'e' if $self->tc_override == 1;
    $grade = 'a' if $self->tc_override == 2;
    $grade = 'd' if $self->tc_override == 3;
    print "grade from override is $grade\n" if $self->verbose > 1;
    return $grade;
  }

  # Monitor:  E(>5)  A(2-5)  D(<2)
  if ($self->num_obs_app + $self->num_obs_app_1 + $self->num_obs_app_2 > 1 and 
      $self->monitor =~ /Y/) {
    $grade .= 'e' if $self->tc_monitor > 5;
    $grade .= 'a' if $self->tc_monitor >= 2 and $self->tc_monitor <= 5;
    $grade .= 'd' if $self->tc_monitor < 2 and $self->tc_monitor;
  }
  print "grade after monitoring: $grade\n" if $self->verbose > 1;

  # TOO:  E(>5)  A(2-5)  D(<2)
  print "number of followups: ", 
    $self->num_followups(0) + $self->num_followups(1) + 
      $self->num_followups(2), "\n" if $self->verbose > 1;
  if ($self->num_followups(0) + $self->num_followups(1) + 
      $self->num_followups(2) >= 1) {
    $grade .= 'e' if $self->tc_too > 5;
    $grade .= 'a' if $self->tc_too >= 2 and $self->tc_too <= 5;
    $grade .= 'd' if $self->tc_too < 2 and $self->tc_too;
  }
  print "grade after too: $grade\n" if $self->verbose > 1;
  
  # Group:  E(>10)  A(4-10)  D(<4)
  $grade .= 'e' if $self->tc_group_app > 10;
  $grade .= 'a' if $self->tc_group_app >= 4 and $self->tc_group_app <= 10;
  $grade .= 'd' if $self->tc_group_app < 4 and $self->tc_group_app;
  print "grade after group: $grade\n" if $self->verbose > 1;
  
  # Uninterrupted:  E(<30)  A(30-40)  D(>40)
  if ($self->uninterrupt eq 'Y') {
    my $uninterrupt = $self->app_time;
    if ($self->monitor =~ /Y/ or defined $self->response_time) {
      # Need get the longest segment in the ao
      my $toos = $self->toos;

      my $max_time = 0;
      for (my $i = 1; $i < scalar @$toos; $i++) {
	next unless $$toos[$i]->ao == 0 and $$toos[$i]->obs_status eq 'Y';
	$max_time = $$toos[$i]->app_obs_time if 
	  $$toos[$i]->app_obs_time > $max_time;
      }
      $uninterrupt = $max_time;
    }
    $grade .= 'e' if $uninterrupt < 30;
    $grade .= 'a' if $uninterrupt >= 30 and $uninterrupt <= 40;
    $grade .= 'd' if $uninterrupt > 40;
  }
  print "grade after uninterrupt: $grade\n" if $self->verbose > 1;

  # Coordinated:  E(-)  A(>=3)  D(<3)
  if ($self->multitelescope eq 'Y' ) {
    $grade .= 'a' if $self->tc_coor >= 3;
    $grade .= 'd' if $self->tc_coor < 3 ;
  }
  print "grade after coordinated: $grade\n" if $self->verbose > 1;

  # Roll:  E(>21)  A(3-21)  D(<3)
  $grade .= 'e' if $self->tc_roll > 21;
  $grade .= 'a' if $self->tc_roll >= 3 and $self->tc_roll <= 21;
  $grade .= 'd' if $self->tc_roll < 3 and $self->tc_roll;
  print "grade after roll: $grade\n" if $self->verbose > 1;

  # Time:  E(>21)  A(3-21)  D(<3)
  $grade .= 'e' if $self->tc_time > 21;
  $grade .= 'a' if $self->tc_time >= 3 and $self->tc_time <= 21;
  $grade .= 'd' if $self->tc_time < 3 and $self->tc_time;
  print "grade after time: $grade\n" if $self->verbose > 1;

  # Phase:  E(<20)  A(20-60)  D(>60)
  $grade .= 'e' if $self->tc_phase < 20 and $self->tc_phase;
  $grade .= 'a' if $self->tc_phase >= 20 and $self->tc_phase <= 60;
  $grade .= 'd' if $self->tc_phase > 60;
  print "grade after phase: $grade\n" if $self->verbose > 1;

  # Constraint in Remarks:  ?
  # Waiting to hear what the grading scheme is for this constraint
  if ($self->constr_in_remarks eq 'Y') {
    $grade .= 'e' if $self->tc_const_rem == 1;
    $grade .= 'a' if $self->tc_const_rem == 2;
    $grade .= 'd' if $self->tc_const_rem == 3;
  }
  print "grade after constraint in remarks: $grade\n" if $self->verbose > 1;

  my $tc_grade;
  if ($grade =~ /d/) {
    $tc_grade = 'd';
  }
  elsif ($grade =~ /a/) {
    $tc_grade = 'a';
  }
  elsif ($grade =~ /e/) {
    $tc_grade = 'e';
  }
  else {
    $tc_grade = undef;
  }
  print "final grade is $tc_grade\n" if $self->verbose > 1;
  return $tc_grade;
}

## Class Method ##
# Name: calc_tc_grade_1
# Purpose: determines the tc_variable grade for cycle n+1
# Parameters: none
# Returns: grade (e|a|d)
sub calc_tc_grade_1 {
  my $self = shift;
  my $init = shift;

  print "AO 1\n**** Proposal ", $self->prop_id, "; Target ", 
    $self->targ_id, " ****\n" if $self->verbose > 1;
  # The following TC grades contribute to the final tc_grade:
  #     tc_monitor
  #     tc_group
  #     tc_too
  #     uninterrupt
  #     tc_coor
  #     tc_const_rem
  #     tc_roll
  #     tc_time
  #     tc_phase
  #     tc_override

  my $grade = '';
  return undef if $self->time_crit =~ /N/ and $self->rev_too =~ /N/;

  return undef if !$self->app_time_1;

  # Override: E(1)  A(2)  D(3)
  # If tc_override is set, that is the grade and skip the rest
  if ($self->tc_override_1) {
    $grade = '' if $self->tc_override_1 == 0;
    $grade = 'e' if $self->tc_override_1 == 1;
    $grade = 'a' if $self->tc_override_1 == 2;
    $grade = 'd' if $self->tc_override_1 == 3;
    print "grade_1 from override is $grade\n" if $self->verbose > 1;
    return $grade;
  }

  # Monitor:  E(>5)  A(2-5)  D(<2)
  if ($self->num_obs_app_1 and $self->monitor =~ /Y/) {
    $grade .= 'e' if $self->tc_monitor_1 > 5;
    $grade .= 'a' if $self->tc_monitor_1 >= 2 and $self->tc_monitor_1 <= 5;
    $grade .= 'd' if $self->tc_monitor_1 < 2 and $self->tc_monitor_1;
  }
  print "grade_1 after monitoring: $grade\n" if $self->verbose > 1;

  # TOO:  E(>5)  A(2-5)  D(<2)
  print "number of followups_1: ", $self->num_followups(1), "\n" 
    if $self->verbose > 1;
  if ($self->num_followups(1)) {
    $grade .= 'e' if $self->tc_too_1 > 5;
    $grade .= 'a' if $self->tc_too_1 >= 2 and $self->tc_too_1 <= 5;
    $grade .= 'd' if $self->tc_too_1 < 2 and $self->tc_too_1;
  }
  print "grade_1 after too: $grade\n" if $self->verbose > 1;
  
  # Group:  E(>10)  A(4-10)  D(<4)
  $grade .= 'e' if $self->tc_group_app_1 > 10;
  $grade .= 'a' if $self->tc_group_app_1 >= 4 and $self->tc_group_app_1 <= 10;
  $grade .= 'd' if $self->tc_group_app_1 < 4 and $self->tc_group_app_1;
  print "grade_1 after group: $grade\n" if $self->verbose > 1;
  
  # Uninterrupted:  E(<30)  A(30-40)  D(>40)
  if ($self->uninterrupt eq 'Y') {
    my $uninterrupt = $self->app_time_1;
    if ($self->monitor =~ /Y/ or defined $self->response_time) {
      # Need get the longest segment in the ao
      my $toos = $self->toos;

      my $max_time = 0;
      for (my $i = 1; $i < scalar @$toos; $i++) {
	next unless $$toos[$i]->ao == 1 and $$toos[$i]->obs_status eq 'Y';
	$max_time = $$toos[$i]->app_obs_time if 
	  $$toos[$i]->app_obs_time > $max_time;
      }
      $uninterrupt = $max_time;
    }
    $grade .= 'e' if $uninterrupt < 30;
    $grade .= 'a' if $uninterrupt >= 30 and $uninterrupt <= 40;
    $grade .= 'd' if $uninterrupt > 40;
  }
  print "grade_1 after uninterrupt: $grade\n" if $self->verbose > 1;

  # Roll:  E(>21)  A(3-21)  D(<3)
  $grade .= 'e' if $self->tc_roll > 21;
  $grade .= 'a' if $self->tc_roll >= 3 and $self->tc_roll <= 21;
  $grade .= 'd' if $self->tc_roll < 3 and $self->tc_roll;
  print "grade_1 after roll: $grade\n" if $self->verbose > 1;

  # Time:  E(>21)  A(3-21)  D(<3)
  $grade .= 'e' if $self->tc_time_1 > 21;
  $grade .= 'a' if $self->tc_time_1 >= 3 and $self->tc_time_1 <= 21;
  $grade .= 'd' if $self->tc_time_1 < 3 and $self->tc_time_1;
  print "grade_1 after time: $grade\n" if $self->verbose > 1;

  # Phase:  E(<20)  A(20-60)  D(>60)
  $grade .= 'e' if $self->tc_phase_1 < 20 and $self->tc_phase_1;
  $grade .= 'a' if $self->tc_phase_1 >= 20 and $self->tc_phase_1 <= 60;
  $grade .= 'd' if $self->tc_phase_1 > 60;
  print "grade_1 after phase: $grade\n" if $self->verbose > 1;

  # Constraint in Remarks:  ?
  if ($self->constr_in_remarks eq 'Y') {
    $grade .= 'e' if $self->tc_const_rem_1 == 1;
    $grade .= 'a' if $self->tc_const_rem_1 == 2;
    $grade .= 'd' if $self->tc_const_rem_1 == 3;
  }
  print "grade_1 after constraint in remarks: $grade\n" if $self->verbose > 1;

  my $tc_grade;
  if ($grade =~ /d/) {
    $tc_grade = 'd';
  }
  elsif ($grade =~ /a/) {
    $tc_grade = 'a';
  }
  elsif ($grade =~ /e/) {
    $tc_grade = 'e';
  }
  else {
    $tc_grade = undef;
  }
  print "final grade_1 is $tc_grade\n" if $self->verbose > 1;
  return $tc_grade;
}

## Class Method ##
# Name: calc_tc_grade_2
# Purpose: determines the tc_variable grade for cycle n+2
# Parameters: none
# Returns: grade (e|a|d)
sub calc_tc_grade_2 {
  my $self = shift;
  my $init = shift;

  print "AO 2\n**** Proposal ", $self->prop_id, "; Target ", 
    $self->targ_id, " ****\n" if $self->verbose > 1;
  # The following TC grades contribute to the final tc_grade:
  #     tc_monitor
  #     tc_group
  #     tc_too
  #     uninterrupt
  #     tc_coor
  #     tc_const_rem
  #     tc_roll
  #     tc_time
  #     tc_phase
  #     tc_override

  my $grade = '';
  return undef if $self->time_crit =~ /N/ and $self->rev_too =~ /N/;

  return undef if !$self->app_time_2;

  # Override: E(1)  A(2)  D(3)
  # If tc_override is set, that is the grade and skip the rest
  if ($self->tc_override_2) {
    $grade = '' if $self->tc_override_2 == 0;
    $grade = 'e' if $self->tc_override_2 == 1;
    $grade = 'a' if $self->tc_override_2 == 2;
    $grade = 'd' if $self->tc_override_2 == 3;
    print "grade_2 from override is $grade\n" if $self->verbose > 1;
    return $grade;
  }

  # Monitor:  E(>5)  A(2-5)  D(<2)
  if ($self->num_obs_app_2 and $self->monitor =~ /Y/) {
    $grade .= 'e' if $self->tc_monitor_2 > 5;
    $grade .= 'a' if $self->tc_monitor_2 >= 2 and $self->tc_monitor_2 <= 5;
    $grade .= 'd' if $self->tc_monitor_2 < 2 and $self->tc_monitor_2;
  }
  print "grade_2 after monitoring: $grade\n" if $self->verbose > 1;

  # TOO:  E(>5)  A(2-5)  D(<2)
  print "number of followups_2: ", $self->num_followups(2), "\n" 
    if $self->verbose > 1;
  if ($self->num_followups(2)) {
    $grade .= 'e' if $self->tc_too_2 > 5;
    $grade .= 'a' if $self->tc_too_2 >= 2 and $self->tc_too_2 <= 5;
    $grade .= 'd' if $self->tc_too_2 < 2 and $self->tc_too_2;
  }
  print "grade_2 after too: $grade\n" if $self->verbose > 1;
  
  # Group:  E(>10)  A(4-10)  D(<4)
  $grade .= 'e' if $self->tc_group_app_2 > 10;
  $grade .= 'a' if $self->tc_group_app_2 >= 4 and $self->tc_group_app_2 <= 10;
  $grade .= 'd' if $self->tc_group_app_2 < 4 and $self->tc_group_app_2;
  print "grade_2 after group: $grade\n" if $self->verbose > 1;
  
  # Uninterrupted:  E(<30)  A(30-40)  D(>40)
  if ($self->uninterrupt eq 'Y') {
    my $uninterrupt = $self->app_time_2;
    if ($self->monitor =~ /Y/ or defined $self->response_time) {
      # Need get the longest segment in the ao
      my $toos = $self->toos;

      my $max_time = 0;
      for (my $i = 1; $i < scalar @$toos; $i++) {
	next unless $$toos[$i]->ao == 2 and $$toos[$i]->obs_status eq 'Y';
	$max_time = $$toos[$i]->app_obs_time if 
	  $$toos[$i]->app_obs_time > $max_time;
      }
      $uninterrupt = $max_time;
    }
    $grade .= 'e' if $uninterrupt < 30;
    $grade .= 'a' if $uninterrupt >= 30 and $uninterrupt <= 40;
    $grade .= 'd' if $uninterrupt > 40;
  }
  print "grade_2 after uninterrupt: $grade\n" if $self->verbose > 1;

  # Roll:  E(>21)  A(3-21)  D(<3)
  $grade .= 'e' if $self->tc_roll > 21;
  $grade .= 'a' if $self->tc_roll >= 3 and $self->tc_roll <= 21;
  $grade .= 'd' if $self->tc_roll < 3 and $self->tc_roll;
  print "grade_2 after roll: $grade\n" if $self->verbose > 1;

  # Time:  E(>21)  A(3-21)  D(<3)
  $grade .= 'e' if $self->tc_time_2 > 21;
  $grade .= 'a' if $self->tc_time_2 >= 3 and $self->tc_time_2 <= 21;
  $grade .= 'd' if $self->tc_time_2 < 3 and $self->tc_time_2;
  print "grade_2 after time: $grade\n" if $self->verbose > 1;

  # Phase:  E(<20)  A(20-60)  D(>60)
  $grade .= 'e' if $self->tc_phase_2 < 20 and $self->tc_phase_2;
  $grade .= 'a' if $self->tc_phase_2 >= 20 and $self->tc_phase_2 <= 60;
  $grade .= 'd' if $self->tc_phase_2 > 60;
  print "grade_2 after phase: $grade\n" if $self->verbose > 1;

  # Constraint in Remarks:  ?
  if ($self->constr_in_remarks eq 'Y') {
    $grade .= 'e' if $self->tc_const_rem_2 == 1;
    $grade .= 'a' if $self->tc_const_rem_2 == 2;
    $grade .= 'd' if $self->tc_const_rem_2 == 3;
  }
  print "grade_2 after constraint in remarks: $grade\n" if $self->verbose > 1;

  my $tc_grade;
  if ($grade =~ /d/) {
    $tc_grade = 'd';
  }
  elsif ($grade =~ /a/) {
    $tc_grade = 'a';
  }
  elsif ($grade =~ /e/) {
    $tc_grade = 'e';
  }
  else {
    $tc_grade = undef;
  }
  print "final grade_2 is $tc_grade\n" if $self->verbose > 1;
  return $tc_grade;
}

## Class Method ##
# Name: calc_status
# Purpose: determines the status of the target 
# Parameters: none
# Returns: status
sub calc_status {
  my $self = shift;
  my $status;
  return $self->targ_status if $self->targ_status =~ /$STAT_BPP/;

  # For TOOs, the status is the result of the status of the trigger, which is
  # always the first in the list
  if (defined $self->response_time or $self->monitor eq 'Y' or $self->monitor eq 'P') {
    my $toos = $self->toos;
    my $trigger = $$toos[1];
    return $trigger->obs_status;
  }
  else {
    return $self->targ_status;
  }
}

## Class Method ##
# Name: calc_status_1
# Purpose: determines the status of the target for cycle n+1
# Parameters: none
# Returns: status
sub calc_status_1 {
  my $self = shift;
  my $status;

  # Future cycles cannot be approved if the current cycle was not approved
  return $self->targ_status if $self->targ_status =~/N|G|$STAT_BPP/;

  # For TOOs and monitors, the status is the result having at least one 
  # segment approved for ao 1
  my $status = 'N';
  if (defined $self->response_time or $self->monitor eq 'Y' or $self->monitor eq 'P') {
    my $toos = $self->toos;
    for (my $i = 1; $i < scalar @$toos; $i++) {
      next unless $$toos[$i]{ao} == 1;
      $status = 'Y' if $$toos[$i]->obs_status eq 'Y';
      $status = 'G' if $status ne 'Y' and $$toos[$i]->obs_status eq 'G';
      $status = 'N' if $status !~ /Y|G/ and $$toos[$i]->obs_status eq 'N';
    }
    return $status;
  }
  else {
    return $self->targ_status_1;
  }
}

## Class Method ##
# Name: calc_status_2
# Purpose: determines the status of the target for cycle n+2
# Parameters: none
# Returns: status
sub calc_status_2 {
  my $self = shift;
  my $status;

  # Future cycles cannot be approved if the current cycle was not approved
  return $self->targ_status if $self->targ_status =~/N|G|$STAT_BPP/;

  # For TOOs and monitors, the status is the result having at least one 
  # segment approved for ao 2
  my $status = 'N';
  if (defined $self->response_time or $self->monitor eq 'Y' or $self->monitor eq 'P') {
    my $toos = $self->toos;
    for (my $i = 1; $i < scalar @$toos; $i++) {
      next unless $$toos[$i]{ao} == 2;
      $status = 'Y' if $$toos[$i]->obs_status eq 'Y';
      $status = 'G' if $status ne 'Y' and $$toos[$i]->obs_status eq 'G';
      $status = 'N' if $status !~ /Y|G/ and $$toos[$i]->obs_status eq 'N';
    }
    return $status;
  }
  else {
    return $self->targ_status_2;
  }
}

## Class Method ##
# Name: set
# Purpose: overloads the set accessor from Class::Accessor to update %changed
#          to reflect that there are unsaved changes in the object
# Parameters: field to access
#             array of values
# Returns: nothing
sub set {
    my($self, $key, @values) = @_;
    if ($key !~ /changed/) {
	my $changed = $self->changed;
	my %changed = %$changed;
	$changed{$key} = 1;
	$self->changed(\%changed);
    }
    
    $self->SUPER::set($key, @values);
}
 
## Class Method ##
# Name: too
# Purpose: provide general get/set accessor to TOO elements of a target
# Parameters: target index
#             field
#             value if used as a setter
# Returns: value of field if value not supplied
sub too {
    my ($self, $index, $method, $value) = @_;
    my $toos = $self->toos();
    my @toos = @$toos;
    if ($method =~ /app_obs_time/) {
	$toos[$index]->app_obs_time($value) if $value;
	return $toos[$index]->app_obs_time() if !$value;
    }
    elsif ($method =~ /obs_status/) {
      $toos[$index]->obs_status($value) if $value;
      return $toos[$index]->obs_status() if !$value;
    }
    else {
	return $toos[$index];
    }
}

## Class Method ##
# Name: save_member
# Purpose: saves the new value of a field to the target object
# Parameters: field
#             value
#             flag to override targ_status of 'P'
# Returns: nothing
sub save_member {
  my ($self, $member, $value, $override) = @_;
  # There should be no updates of LP/VLP targets unless BPP
  return if $self->targ_status =~ /$STAT_BPP/ and !$override;

  # This routine does not handle the back-up columns
  return if $member =~ /bck/;
  # These section handles targ_status and app_time
  my $app_time_query = $self->dbh->prepare(qq(select ao, sum(app_obs_time) from 
					      too where panel_id = ? 
					      prop_id = ? and targ_id = ?
					      and obs_status = 'Y' group by ao order by ao));
  my $targ_status_query = $self->dbh->prepare(qq(select ao, count(*) from too 
						 where obs_status = 'Y' and
						 panel_id = ? and prop_id = ?
						 and targ_id = ? group by ao 
                                                 order by ao));
  my $too_query = $self->dbh->prepare(qq(select count(*) from too where 
					   panel_id = ? and prop_id = ? and 
					   targ_id = ?));
  
  # Is the target a too or monitor?
  $too_query->execute($self->panel_id, $self->prop_id, 
			 $self->targ_id);
  my ($num_toos) = $too_query->fetchrow_array;
  $too_query->finish;
  
  my $num_segs;
  if ($num_toos > 0) {# and $self->monitor ne 'Y') {
    # Target with toos need to look at the too + follow-ups
    if ($member =~ /app_time/) {
      $value = 0;
      $app_time_query->execute($self->panel_id, $self->prop_id, 
			       $self->targ_id);
      while (my ($ao, $total) = $app_time_query->fetchrow_array) {
	$app_time_query->finish;
	$total = 0 if !$total;

	$value = $total if $ao == 0 and $member eq 'app_time';
	$value = $total if $ao == 1 and $member eq 'app_time_1';
	$value = $total if $ao == 2 and $member eq 'app_time_2';
      }
      $app_time_query->finish;
    }
    elsif ($member =~ /targ_status/) {
      $value = 'N';
      $targ_status_query->execute($self->panel_id, $self->prop_id, 
				  $self->targ_id);
      while (my ($ao, $count) = $targ_status_query->fetchrow_array) {
	$value = 'Y' if $ao == 0 and $count > 0 and $member eq 'targ_status';
	$value = 'Y' if $ao == 1 and $count > 0 and $member eq 'targ_status_1';
	$value = 'Y' if $ao == 2 and $count > 0 and $member eq 'targ_status_2';
      }
      $targ_status_query->finish;
    }
  }
  
  # Now do the saves
  $self->app_time($value) if $member eq 'app_time';
  $self->app_time_1($value) if $member eq 'app_time_1';
  $self->app_time_2($value) if $member eq 'app_time_2';

  $self->targ_status($value) if $member eq 'targ_status';
  $self->targ_status_1($value) if $member eq 'targ_status_1';
  $self->targ_status_2($value) if $member eq 'targ_status_2';

  $self->num_pnt_app($value) if $member eq 'num_pnt_app';
  $self->num_pnt_app_1($value) if $member eq 'num_pnt_app_1';
  $self->num_pnt_app_2($value) if $member eq 'num_pnt_app_2';

  $self->num_obs_app($value) if $member eq 'num_obs_app';
  $self->num_obs_app_1($value) if $member eq 'num_obs_app_1';
  $self->num_obs_app_2($value) if $member eq 'num_obs_app_2';

  $self->time_obs_app($value) if $member eq 'time_obs_app';
  $self->time_obs_app_1($value) if $member eq 'time_obs_app_1';
  $self->time_obs_app_2($value) if $member eq 'time_obs_app_2';

  $self->too_prob_app($value) if $member eq 'too_prob_app';
  $self->time_crit($value) if $member eq 'time_crit';

  $self->tax($value) if $member eq 'tax';
  $self->tax_1($value) if $member eq 'tax_1';
  $self->tax_2($value) if $member eq 'tax_2';

  $self->tc($value) if $member eq 'tc';
  $self->tc_1($value) if $member eq 'tc_1';
  $self->tc_2($value) if $member eq 'tc_2';
}

## Class Method ##
# Name: save2database
# Purpose: saves the target object to the database
# Parameters:none
# Returns: nothing
sub save2database {
    my ($self) = @_;
    my $changed = $self->changed;
    my %changed = %$changed;
    my $dbFields = $self->TARGET_PROPERTIES;

    foreach my $member (keys %changed) {
	my @matching = grep {$_ =~ /^$member$/} @$dbFields;
	# The property time_obs_app is a calculated quantity which is not in
	# the database
	if (scalar @matching > 0 and $member !~ /time_obs_app/) {
#print "update target set $member = ", $self->get($member), " where panel_id = ", $self->panel_id, " and prop_id = ", $self->prop_id, " and targ_id = ", $self->targ_id, "\n";
	    my $update = $self->dbh->prepare(qq(update target set $member = ? 
						where panel_id = ? and 
						prop_id = ? and targ_id = ?));
	    $update->execute($self->get($member), $self->panel_id, 
			     $self->prop_id, $self->targ_id);
	    $update->finish;
	    print "Target::save2database\n\tSaving $member in target ", 
	      $self->targ_id, "(",$self->get($member), ")\n" if $self->verbose;
	}
    }

    # Reset the changed hash
    %changed = ();
    $self->changed(\%changed);

    # Save to database any changes in toos
    if ($self->toos) {
	my $toos = $self->toos;
	my @too_list = @$toos;
	my $reps = $self->num_obs_req;
	$reps = 1 if $self->monitor eq 'Y' or $self->monitor eq 'P';
	for (my $i = 1; $i <= $reps; $i++) {
	    $too_list[$i]->save2database();
	}
    }
}

1;

__END__

=head1 NAME

Target - This object contains data associated with a target,
including too objects when necessary.

=head1 VERSION

$Revision: 1.70 $

=head1 SYNOPSIS

    use Target;
    my $target = new Target($dbh, $prop_id, $target_index, $targ_props, $too_props, $verbosity);

=head1 DESCRIPTION

Provides a single place to access elements of a target

=head1 PUBLIC METHODS

=head2 new($dbh, $prop_id, $targ_id, $targ_props, $too_props, $verbosity)

Creates a new Target object

=over 

=item $dbh - database handle

=item $prop_id - proposal id

=item $targ_id - index of target

=item $targ_props - list of target properties

=item $too_props - list of too properties

=item $verbose - level of verbosity

=back


=head2 set($field, $value)

Overloads the set accessor from Class::Accessor to update %changed to
reflect that there are unsaved changes in the object.

=head2 dump

Prints a data dump of the target object

=head2 num_tc

Boolean representing whether there are phase, roll or time constraints
for a target

=head2 num_tc_1

Boolean representing whether there are phase, roll or time constraints
for a target for cycle n+1

=head2 num_tc_2

Boolean representing whether there are phase, roll or time constraints
for a target for cycle n+2

=head2 num_pnt

Calculates the number of approved pointings required for the target

=head2 num_pnt_1

Calculates the number of approved pointings required for the target for cycle
n+1

=head2 num_pnt_2

Calculates the number of approved pointings required for the target for cycle
n+2

=head2 num_followups($ao)

Returns the number of approved followups for the target

=head2 calc_status

Calculates the status of the target after first checking on the status of any
TOO triggers

=head2 calc_status_1

Calculates the status of the target after first checking on the status of any
TOO triggers for cycle n+1

=head2 calc_status_2

Calculates the status of the target after first checking on the status of any
TOO triggers for cycle n+2

=head2 calc_tax

Calculates the tax of the target

=head2 calc_tax_1

Calculates the tax of the target for cycle n+1

=head2 calc_tax_2

Calculates the tax of the target for cycle n+2

=head2 calc_tc_mon

Calculates the tc_mon score of the target

=head2 calc_tc_group

Calculates the tc_group score of the target

=head2 calc_tc_group_1

Calculates the tc_group score of the target for cycle n+1

=head2 calc_tc_group_2

Calculates the tc_group score of the target for cycle n+2

=head2 calc_tc_too

Calculates the tc_too score of the target

=head2 calc_tc_grade

Calculates the tc grade of the target

=head2 calc_tc_grade_1

Calculates the tc grade of the target for cycle n+1

=head2 calc_tc_grade_2

Calculates the tc grade of the target for cycle n+2

=head2 too($field, $value)

Provides a general get/set accessor to all elements of a target.

=head2 save_member($field, $value)

Saves the new value of a field to the target object

=head2 save2database

Saves the target object to the database.

=head1 PRIVATE METHODS

=head2 _init

Initializes new Target object.

=head1 DEPENDENCIES

This module has no dependencies

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
