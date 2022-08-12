#
# Monitor.pm - This module populates the monitor window
#
# Purpose: Provide up-to-date panel statistics. 
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Monitor;

require Exporter;
use Data::Dumper;
use config;
use Print;
use strict;
use Carp;

our @ISA = qw(Exporter);
our @EXPORT = qw(fill_monitor set_color );

use vars qw($tc $tc_1 $tc_2
            $vf $f $s $vs 
            $time_slew_prob $time_slew_prob_1 $time_slew_prob_2
            $hel_slew_prob $hel_slew_prob_1 $hel_slew_prob_2
            $arc $the
	    $hst $xmm $noao $nrao $spitzer $swift $nustar
	    $lp_time_slew_prob $lp_time_slew_prob_1 $lp_time_slew_prob_2 
            $vlp_time_slew_prob $vlp_time_slew_prob_1 $vlp_time_slew_prob_2
            $xvp_time_slew_prob $xvp_time_slew_prob_1 $xvp_time_slew_prob_2
            $verbose );

## Class Method ##
# Name: fill_monitor
# Purpose: populate the monitor window with panel statistics
# Parameters: monitor top level window
#             panel object
#             verbosity
# Returns: nothing
sub fill_monitor {
    my ($mon, $panel, $vb) = @_;
    $verbose = $vb;

    print "Monitor::fill_monitor - populating widget\n" if $verbose > 2;

    $mon->Button(-text => 'Print',
		 -command => sub {printStatus($panel, 'panel');}
		)->pack(-side => 'top');

    my $monitor = $mon->Scrolled('Pane', 
				 -scrollbars => 'onow',
				 -width => 1000,
				 -height => 1000,
				 );

    grade_summary($monitor, $panel) if ($panel->panel_id != 99);
    lp_grade_summary($monitor, $panel) if ($panel->panel_id == 99);
    time_summary($monitor, $panel) if $panel->panel_id != 99;
    lp_time_summary($monitor, $panel) if $panel->panel_id == 99;
    if ($isVLP) {
      vlp_time_summary($monitor, $panel) if $panel->panel_id == 99;
    }
    if ($isXVP) {
      xvp_time_summary($monitor, $panel) if $panel->panel_id == 99;
    }
    money_summary($monitor, $panel) if $panel->panel_id != 99;
    tc_too_summary($monitor, $panel);
    joint_summary($monitor, $panel);
    status_summary($monitor, $panel);
    fixed_totals($monitor, $panel);

    $monitor->pack();
    set_color($panel);
}

## Class Method ##
# Name: set_color
# Purpose: sets the color of fields based on their value relative to allotted
#          values for the panel
# Parameters: panel object
# Returns: nothing
sub set_color {
  my $panel = shift;
  set_foreground($tc, 'red') if $panel->rc_score_bal < 0;
  set_foreground($tc_1, 'red') if $panel->rc_score_bal_1 < 0;
  set_foreground($tc_2, 'red') if $panel->rc_score_bal_2 < 0;

  set_foreground($tc, 'blue') if $panel->rc_score_bal >= 0;
  set_foreground($tc_1, 'blue') if $panel->rc_score_bal_1 >= 0;
  set_foreground($tc_2, 'blue') if $panel->rc_score_bal_2 >= 0;

  set_foreground($tc, 'black') if $panel->rc_score_bal =~ /N/;
  set_foreground($tc_1, 'black') if $panel->rc_score_bal_1 =~ /N/;
  set_foreground($tc_2, 'black') if $panel->rc_score_bal_2 =~ /N/;

  set_foreground($vf, 'red') if $panel->vf_bal < 0;
  set_foreground($f, 'red') if $panel->f_bal < 0;
  set_foreground($s, 'red') if $panel->s_bal < 0;
  set_foreground($vs, 'red') if $panel->vs_bal < 0;
  set_foreground($vf, 'blue') if $panel->vf_bal >= 0;
  set_foreground($f, 'blue') if $panel->f_bal >= 0;
  set_foreground($s, 'blue') if $panel->s_bal >= 0;
  set_foreground($vs, 'blue') if $panel->vs_bal >= 0;
  set_foreground($vf, 'black') if $panel->vf_bal =~ /N/;
  set_foreground($f, 'black') if $panel->f_bal =~ /N/;
  set_foreground($s, 'black') if $panel->s_bal =~ /N/;
  set_foreground($vs, 'black') if $panel->vs_bal =~ /N/;

  set_foreground($hst, 'red') if $panel->hst_bal < 0;
  set_foreground($xmm, 'red') if $panel->xmm_bal < 0;
  set_foreground($noao, 'red') if $panel->noao_bal < 0;
  set_foreground($nrao, 'red') if $panel->nrao_bal < 0;
  #set_foreground($spitzer, 'red') if $panel->spitzer_bal < 0;
  set_foreground($swift, 'red') if $panel->swift_bal < 0;
  set_foreground($nustar, 'red') if $panel->nustar_bal < 0;
  set_foreground($hst, 'blue') if $panel->hst_bal >= 0;
  set_foreground($xmm, 'blue') if $panel->xmm_bal >= 0;
  set_foreground($noao, 'blue') if $panel->noao_bal >= 0;
  set_foreground($nrao, 'blue') if $panel->nrao_bal >= 0;
  #set_foreground($spitzer, 'blue') if $panel->spitzer_bal >= 0;
  set_foreground($swift, 'blue') if $panel->swift_bal >= 0;
  set_foreground($nustar, 'blue') if $panel->nustar_bal >= 0;
  set_foreground($hst, 'black') if $panel->hst_bal =~ /N/;
  set_foreground($xmm, 'black') if $panel->xmm_bal =~ /N/;
  set_foreground($noao, 'black') if $panel->noao_bal =~ /N/;
  set_foreground($nrao, 'black') if $panel->nrao_bal =~ /N/;
  #set_foreground($spitzer, 'black') if $panel->spitzer_bal =~ /N/;
  set_foreground($swift, 'black') if $panel->swift_bal =~ /N/;
  set_foreground($nustar, 'black') if $panel->nustar_bal =~ /N/;

  set_foreground($time_slew_prob, 'red') if $panel->time_slew_prob_bal < 0;
  set_foreground($time_slew_prob_1, 'red') if $panel->time_slew_prob_bal_1 < 0;
  set_foreground($time_slew_prob_2, 'red') if $panel->time_slew_prob_bal_2 < 0;
  set_foreground($time_slew_prob, 'blue') if $panel->time_slew_prob_bal >= 0;
  set_foreground($time_slew_prob_1, 'blue') 
    if $panel->time_slew_prob_bal_1 >= 0;
  set_foreground($time_slew_prob_2, 'blue') 
    if $panel->time_slew_prob_bal_2 >= 0;
  set_foreground($time_slew_prob_1, 'black') if $panel->time_slew_prob_bal_1 =~ /N/;
  set_foreground($time_slew_prob_2, 'black') if $panel->time_slew_prob_bal_2 =~ /N/;

  set_foreground($hel_slew_prob, 'red') if $panel->hel_slew_prob_bal < 0;
  set_foreground($hel_slew_prob_1, 'red') if $panel->hel_slew_prob_bal_1 < 0;
  set_foreground($hel_slew_prob_2, 'red') if $panel->hel_slew_prob_bal_2 < 0;
  set_foreground($hel_slew_prob, 'blue') if $panel->hel_slew_prob_bal >= 0;
  set_foreground($hel_slew_prob_1, 'blue') if $panel->hel_slew_prob_bal_1 >= 0;
  set_foreground($hel_slew_prob_2, 'blue') if $panel->hel_slew_prob_bal_2 >= 0;
  set_foreground($hel_slew_prob_1, 'black') if $panel->hel_slew_prob_bal_1 =~ /N/;
  set_foreground($hel_slew_prob_2, 'black') if $panel->hel_slew_prob_bal_2 =~ /N/;

  set_foreground($lp_time_slew_prob, 'red') 
      if $panel->lp_time_slew_prob_bal < 0 and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob_1, 'red') 
      if $panel->lp_time_slew_prob_bal_1 < 0 and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob_2, 'red') 
      if $panel->lp_time_slew_prob_bal_2 < 0 and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob, 'blue') 
      if $panel->lp_time_slew_prob_bal >= 0 and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob_1, 'blue') 
      if $panel->lp_time_slew_prob_bal_1 >= 0 and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob_2, 'blue') 
      if $panel->lp_time_slew_prob_bal_2 >= 0 and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob, 'black') 
      if $panel->lp_time_slew_prob_bal =~ /N/ and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob_1, 'black') 
      if $panel->lp_time_slew_prob_bal_1 =~ /N/ and $panel->panel_id == 99;
  set_foreground($lp_time_slew_prob_2, 'black') 
      if $panel->lp_time_slew_prob_bal_2 =~ /N/ and $panel->panel_id == 99;

  set_foreground($vlp_time_slew_prob, 'red') 
      if $panel->vlp_time_slew_prob_bal < 0 and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob_1, 'red') 
      if $panel->vlp_time_slew_prob_bal_1 < 0 and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob_2, 'red') 
      if $panel->vlp_time_slew_prob_bal_2 < 0 and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob, 'blue') 
      if $panel->vlp_time_slew_prob_bal >= 0 and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob_1, 'blue') 
      if $panel->vlp_time_slew_prob_bal_1 >= 0 and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob_2, 'blue') 
      if $panel->vlp_time_slew_prob_bal_2 >= 0 and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob, 'black') 
      if $panel->vlp_time_slew_prob_bal =~ /N/ and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob_1, 'black') 
      if $panel->vlp_time_slew_prob_bal_1 =~ /N/ and $panel->panel_id == 99;
  set_foreground($vlp_time_slew_prob_2, 'black') 
      if $panel->vlp_time_slew_prob_bal_2 =~ /N/ and $panel->panel_id == 99;

  set_foreground($xvp_time_slew_prob, 'red') 
      if $panel->xvp_time_slew_prob_bal < 0 and $panel->panel_id == 99;
  set_foreground($xvp_time_slew_prob_1, 'red') 
      if $panel->xvp_time_slew_prob_bal_1 < 0 and $panel->panel_id == 99;
  set_foreground($xvp_time_slew_prob_2, 'red') 
      if $panel->xvp_time_slew_prob_bal_2 < 0 and $panel->panel_id == 99;

  set_foreground($xvp_time_slew_prob, 'blue') 
      if $panel->xvp_time_slew_prob_bal >= 0 and $panel->panel_id == 99;
  set_foreground($xvp_time_slew_prob_1, 'blue') 
      if $panel->xvp_time_slew_prob_bal_1 >= 0 and $panel->panel_id == 99;
  set_foreground($xvp_time_slew_prob_2, 'blue') 
      if $panel->xvp_time_slew_prob_bal_2 >= 0 and $panel->panel_id == 99;

  set_foreground($xvp_time_slew_prob, 'black') 
      if $panel->xvp_time_slew_prob_bal =~ /N/ and $panel->panel_id == 99;
  set_foreground($xvp_time_slew_prob_1, 'black') 
      if $panel->xvp_time_slew_prob_bal_1 =~ /N/ and $panel->panel_id == 99;
  set_foreground($xvp_time_slew_prob_2, 'black') 
      if $panel->xvp_time_slew_prob_bal_2 =~ /N/ and $panel->panel_id == 99;

  set_foreground($arc, 'red') if $panel->arc_bal < 0 and 
    $panel->panel_id != 99;
  set_foreground($the, 'red') if $panel->the_bal < 0 and 
    $panel->panel_id != 99;
  set_foreground($arc, 'blue') if $panel->arc_bal >= 0 and 
    $panel->panel_id != 99;
  set_foreground($the, 'blue') if $panel->the_bal >= 0 and 
    $panel->panel_id != 99;
}

## Internal Utility ##
# Name: fixed_totals
# Purpose: displays the fixed totals for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub fixed_totals {
    my $monitor = shift;
    my $panel = shift;

    # Fixed totals
    my $fixed_totals = $monitor->LabFrame(-label => 'Fixed Totals',
					  -labelside => 'acrosstop',
					  -relief => 'groove',
					  )->pack(-side => 'top');
    $fixed_totals->Label(-text => 'Current AO',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 0,
				 -columnspan => 4);
    $fixed_totals->Label(-text => 'AO + 1',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 4,
				 -columnspan => 4);
    $fixed_totals->Label(-text => 'AO + 2',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 8,
				 -columnspan => 4);

    $fixed_totals->Label(-text => 'GO Prop:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -width => 10
			 )->grid
			     ($fixed_totals->Label(-text => $panel->std_prop,
						   -anchor => 'e',
						   -foreground => 'blue',
						   -width => 4),
			      $fixed_totals->Label(-text => 'GO Targ:',
						   -anchor => 'e',
						   -foreground => 'black',
						   -width => 8),
			      $fixed_totals->Label(-text => $panel->std_targ,
						   -anchor => 'e',
						   -foreground => 'blue',
						   -width => 4),
			      $fixed_totals->Label(-text => 'GO Prop:',
						   -anchor => 'e',
						   -foreground => 'black',
						   -width => 12),
			      $fixed_totals->Label(-text => $panel->std_prop_1,
						   -anchor => 'e',
						   -foreground => 'blue',
						   -width => 4),
			      $fixed_totals->Label(-text => 'GO Targ:',
						   -anchor => 'e',
						   -foreground => 'black',
						   -width => 8),
			      $fixed_totals->Label(-text => $panel->std_targ_1,
						   -anchor => 'e',
						   -foreground => 'blue',
						   -width => 4),
			      $fixed_totals->Label(-text => 'GO Prop:',
						   -anchor => 'e',
						   -foreground => 'black',
						   -width => 12),
			      $fixed_totals->Label(-text => $panel->std_prop_2,
						   -anchor => 'e',
						   -foreground => 'blue',
						   -width => 4),
			      $fixed_totals->Label(-text => 'GO Targ:',
						   -anchor => 'e',
						   -foreground => 'black',
						   -width => 8),
			      $fixed_totals->Label(-text => $panel->std_targ_2,
						   -anchor => 'e',
						   -foreground => 'blue',
						   -width => 4),
			      -sticky => 'nsew'
			      );
    $fixed_totals->Label(-text => ' LP Prop:',
			 -anchor => 'e',
			 -foreground => 'black',
			 )->grid
			     ($fixed_totals->Label(-text => $panel->lp_prop,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => ' LP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->lp_targ,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => ' LP Prop:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->lp_prop_1,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => ' LP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->lp_targ_1,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => ' LP Prop:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->lp_prop_2,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => ' LP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->lp_targ_2,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      -sticky => 'nsew'
			      );
    if ($isVLP) {
    $fixed_totals->Label(-text => 'VLP Prop:',
			 -anchor => 'e',
			 -foreground => 'black',
			 )->grid
			     ($fixed_totals->Label(-text => $panel->vlp_prop,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'VLP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->vlp_targ,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'VLP Prop:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->vlp_prop_1,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'VLP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->vlp_targ_1,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'VLP Prop:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->vlp_prop_2,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'VLP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->vlp_targ_2,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      -sticky => 'nsew'
			      );
     }
    if ($isXVP) {
    $fixed_totals->Label(-text => 'XVP Prop:',
			 -anchor => 'e',
			 -foreground => 'black',
			 )->grid
			     ($fixed_totals->Label(-text => $panel->xvp_prop,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'XVP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->xvp_targ,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'XVP Prop:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->xvp_prop_1,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'XVP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->xvp_targ_1,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'XVP Prop:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->xvp_prop_2,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      $fixed_totals->Label(-text => 'XVP Targ:',
						   -anchor => 'e',
						   -foreground => 'black'),
			      $fixed_totals->Label(-text => $panel->xvp_targ_2,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      -sticky => 'nsew'
			      );
     }
    $fixed_totals->Label(-text => 'TOO Prop:',
			 -anchor => 'e',
			 -foreground => 'black',
			 )->grid
			($fixed_totals->Label(-text => $panel->too_prop,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'TOO Targ:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->too_targ,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'TOO Prop:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->too_prop_1,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'TOO Targ:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->too_targ_1,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'TOO Prop:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->too_prop_2,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'TOO Targ:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->too_targ_2,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 -sticky => 'nsew'
			 );
    $fixed_totals->Label(-text => 'ARC Prop:',
			 -anchor => 'e',
			 -foreground => 'black',
			 )->grid
			     ($fixed_totals->Label(-text => $panel->arc_prop,
						   -anchor => 'e',
						   -foreground => 'blue'),
			      -sticky => 'nsew');
    $fixed_totals->Label(-text => 'THE Prop:',
			 -anchor => 'e',
			 -foreground => 'black'
			 )->grid
			 ($fixed_totals->Label(-text => $panel->the_prop,
					       -anchor => 'e',
					       -foreground => 'blue'),
			  -sticky => 'nsew'
			  );
    $fixed_totals->Label(-text => 'Total Prop:',
			 -anchor => 'e',
			 -foreground => 'black',
			 )->grid
			($fixed_totals->Label(-text => $panel->num_props,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'Total Targ:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->num_targs,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'Total Prop:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->num_props_1,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'Total Targ:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->num_targs_1,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'Total Prop:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->num_props_2,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 $fixed_totals->Label(-text => 'Total Targ:',
					      -anchor => 'e',
					      -foreground => 'black'),
			 $fixed_totals->Label(-text => $panel->num_targs_2,
					      -anchor => 'e',
					      -foreground => 'blue'),
			 -sticky => 'nsew'
			 );
}

## Internal Utility ##
# Name: tc_too_summary
# Purpose: displays summary of tc/too allocations for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub tc_too_summary {
    my $monitor = shift;
    my $panel = shift;

    # RC and TOO's
    my $tc_too = $monitor->LabFrame(-label => 'RC, HEL, & TOOs',
				    -labelside => 'acrosstop',
				    -relief =>'groove'
				    )->pack(-side => 'top');
    $tc_too->Label(-text => 'Resource Cost',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 1,
				 -columnspan => 3);
	$tc_too->Label(-text => 'High Ecliptic Latitude',
		-foreground => 'black',
		)->grid (-row => 0,
			-column => 4,
			-columnspan => 3);
    $tc_too->Label(-text => 'TOO Triggers',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 7,
				 -columnspan => 4);

    $tc_too->Label(-text => '',
		   )->grid
		       ($tc_too->Label(-text => 'AO', -anchor => 'center'),
		        $tc_too->Label(-text => 'AO+1', -anchor => 'center'),
		        $tc_too->Label(-text => 'AO+2', -anchor => 'center'),
			    $tc_too->Label(-text => 'AO', -anchor => 'center'),
			    $tc_too->Label(-text => 'AO+1', -anchor => 'center'),
			    $tc_too->Label(-text => 'AO+2', -anchor => 'center'),
			$tc_too->Label(-text => '<5d', -anchor => 'center'),
			$tc_too->Label(-text => '5-20d', -anchor => 'center'),
			$tc_too->Label(-text => '20-40d', -anchor => 'center'),
			$tc_too->Label(-text => '>=40d', -anchor => 'center'),
			-sticky => 'swen'
			);
    $tc_too->Label(-text => 'Requested',
		   -anchor => 'e')->grid
		       ($tc_too->Label(-text => $panel->rc_score_req,
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-text => $panel->rc_score_req_1,
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-text => $panel->rc_score_req_2,
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
		   $tc_too->Label(-text => $panel->hel_slew_prob_req,
			   -relief => 'sunken',
			   -anchor => 'e',
			   -foreground => 'blue'),
		   $tc_too->Label(-text => $panel->hel_slew_prob_req_1,
			   -relief => 'sunken',
			   -anchor => 'e',
			   -foreground => 'blue'),
		   $tc_too->Label(-text => $panel->hel_slew_prob_req_2,
			   -relief => 'sunken',
			   -anchor => 'e',
			   -foreground => 'blue'),
			$tc_too->Label(-text => $panel->vf_req,
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-text => $panel->f_req,
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-text => $panel->s_req,
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-text => $panel->vs_req,
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			-sticky => 'swen'
			);
    $tc_too->Label(-text => 'Allotted',
		   -anchor => 'e')->grid
		       ($tc_too->Label(-text => $panel->rc_score_allot, 
				       -relief => 'sunken',
				       -anchor => 'e'),
			$tc_too->Label(-text => $panel->rc_score_allot_1, 
				       -relief => 'sunken',
				       -anchor => 'e'),
			$tc_too->Label(-text => $panel->rc_score_allot_2, 
				       -relief => 'sunken',
				       -anchor => 'e'),
		   $tc_too->Label(-text => $panel->hel_slew_prob_allot,
			   -relief => 'sunken',
			   -anchor => 'e'),
		   $tc_too->Label(-text => $panel->hel_slew_prob_allot_1,
			   -relief => 'sunken',
			   -anchor => 'e'),
		   $tc_too->Label(-text => $panel->hel_slew_prob_allot_2,
			   -relief => 'sunken',
			   -anchor => 'e'),
			$tc_too->Label(-text => $panel->vf_allot, 
				       -relief => 'sunken',
				       -anchor => 'e'),
			$tc_too->Label(-text => $panel->f_allot, 
				       -relief => 'sunken',
				       -anchor => 'e'),
			$tc_too->Label(-text => $panel->s_allot, 
				       -relief => 'sunken',
				       -anchor => 'e'),
			$tc_too->Label(-text => $panel->vs_allot, 
				       -relief => 'sunken',
				       -anchor => 'e'),
			-sticky => 'nsew'
			);
    
    $tc_too->Label(-text => 'Current',
		   -anchor => 'e')->grid
		       ($tc_too->Label(-textvariable => 
				       $panel->get_ref('rc_score_cur'),
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-textvariable => 
				       $panel->get_ref('rc_score_cur_1'),
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-textvariable => 
				       $panel->get_ref('rc_score_cur_2'),
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
		   $tc_too->Label(-textvariable =>
			   $panel->get_ref('hel_slew_prob_cur'),
			   -relief => 'sunken',
			   -anchor => 'e',
			   -foreground => 'blue'),
		   $tc_too->Label(-textvariable =>
			   $panel->get_ref('hel_slew_prob_cur_1'),
			   -relief => 'sunken',
			   -anchor => 'e',
			   -foreground => 'blue'),
		   $tc_too->Label(-textvariable =>
			   $panel->get_ref('hel_slew_prob_cur_2'),
			   -relief => 'sunken',
			   -anchor => 'e',
			   -foreground => 'blue'),
			$tc_too->Label(-textvariable => 
				       $panel->get_ref('vf_cur'),
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-textvariable => 
				       $panel->get_ref('f_cur'),
				       -anchor => 'e',
				       -relief => 'sunken',
				       -foreground => 'blue'),
			$tc_too->Label(-textvariable => 
				       $panel->get_ref('s_cur'),
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			$tc_too->Label(-textvariable => 
				       $panel->get_ref('vs_cur'),
				       -relief => 'sunken',
				       -anchor => 'e',
				       -foreground => 'blue'),
			-sticky => 'swen'
			);
    
    $tc = $tc_too->Label(-textvariable => $panel->get_ref('rc_score_bal'),
			   -relief => 'sunken',
			   -anchor => 'e',
			   -foreground => 'blue');
    $tc_1 = $tc_too->Label(-textvariable => $panel->get_ref('rc_score_bal_1'),
			     -relief => 'sunken',
			     -anchor => 'e',
			     -foreground => 'blue');
    $tc_2 = $tc_too->Label(-textvariable => $panel->get_ref('rc_score_bal_2'),
			     -relief => 'sunken',
			     -anchor => 'e',
			     -foreground => 'blue');
	$hel_slew_prob = $tc_too->Label(-textvariable => $panel->get_ref('hel_slew_prob_bal'),
		-relief => 'sunken',
		-anchor => 'e',
		-foreground => 'blue');
	$hel_slew_prob_1 = $tc_too->Label(-textvariable => $panel->get_ref('hel_slew_prob_bal_1'),
		-relief => 'sunken',
		-anchor => 'e',
		-foreground => 'blue');
	$hel_slew_prob_2 = $tc_too->Label(-textvariable => $panel->get_ref('hel_slew_prob_bal_2'),
		-relief => 'sunken',
		-anchor => 'e',
		-foreground => 'blue');
    $vf = $tc_too->Label(-textvariable => $panel->get_ref('vf_bal'),
			 -relief => 'sunken',
			 -anchor => 'e',
			 -foreground => 'blue');
    $f = $tc_too->Label(-textvariable => $panel->get_ref('f_bal'),
			-relief => 'sunken',
			-anchor => 'e',
			-foreground => 'blue');
    $s = $tc_too->Label(-textvariable => $panel->get_ref('s_bal'),
			-relief => 'sunken',
			-anchor => 'e',
			-foreground => 'blue');
    $vs = $tc_too->Label(-textvariable => $panel->get_ref('vs_bal'),
			 -relief => 'sunken',
			 -anchor => 'e',
			 -foreground => 'blue');
    
    $tc_too->Label(-text => '    Balance',
		   -anchor => 'e')->grid
		       ($tc,  
			    $tc_1,
			    $tc_2,
		        $hel_slew_prob,
			    $hel_slew_prob_1,
			    $hel_slew_prob_2,
			$vf, $f, $s, $vs,
			-sticky => 'nsew');
}

## Internal Utility ##
# Name: joint_summary
# Purpose: displays of joint allocations for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub joint_summary {
    my $monitor = shift;    
    my $panel = shift;

    # Joint Summary
    my $joint_summary = $monitor->LabFrame(-label => 'Joint Summary',
					   -labelside => 'acrosstop',
					   -relief => 'groove',
					   );
    $joint_summary->Label(-text => '',
			  )->grid
			      ($joint_summary->Label(-text => 'HST',
						     -anchor => 'center'),
			       $joint_summary->Label(-text => 'XMM',
						     -anchor => 'center'),
			       $joint_summary->Label(-text => 'NOAO',
						     -anchor => 'center'),
			       $joint_summary->Label(-text => 'NRAO',
						     -anchor => 'center'),
			       #$joint_summary->Label(-text => 'Spitzer',
						     #-anchor => 'center'),
			       $joint_summary->Label(-text => 'Swift',
						     -anchor => 'center'),
			       $joint_summary->Label(-text => 'NuSTAR',
						     -anchor => 'center'),
			       -sticky => 'nsew'
			       );
    $joint_summary->Label(-text => 'Requested',
			  -anchor => 'e')->grid
			      ($joint_summary->Label(-text => $panel->hst_req,
						     -relief => 'sunken',
						     -anchor => 'e',
						     -foreground => 'blue'),
			       $joint_summary->Label(-text => $panel->xmm_req,
						     -anchor => 'e',
						     -relief => 'sunken',
						     -foreground => 'blue'),
			       $joint_summary->Label(-text => $panel->noao_req,
						     -anchor => 'e',
						     -relief => 'sunken',
						     -foreground => 'blue'),
			       $joint_summary->Label(-text => $panel->nrao_req,
						     -anchor => 'e',
						     -relief => 'sunken',
						     -foreground => 'blue'),
			       #$joint_summary->Label(-text => $panel->spitzer_req,
						     #-anchor => 'e',
						     #-relief => 'sunken',
						     #-foreground => 'blue'),
			       $joint_summary->Label(-text => $panel->swift_req,
						     -anchor => 'e',
						     -relief => 'sunken',
						     -foreground => 'blue'),
			       $joint_summary->Label(-text => $panel->nustar_req,
						     -anchor => 'e',
						     -relief => 'sunken',
						     -foreground => 'blue'),
			       -sticky => 'nsew'
			       );
    $joint_summary->Label(-text => 'Allotted',
			  -anchor => 'e')->grid
			      ($joint_summary->Label(-text => 
						     $panel->hst_allot,
						     -relief => 'sunken',
						     -anchor => 'e'),
			       $joint_summary->Label(-text => $
						     panel->xmm_allot,
						     -relief => 'sunken',
						     -anchor => 'e'),
			       $joint_summary->Label(-text => 
						     $panel->noao_allot,
						     -relief => 'sunken',
						     -anchor => 'e'),
			       $joint_summary->Label(-text => 
						     $panel->nrao_allot,
						     -relief => 'sunken',
						     -anchor => 'e'),
			       #$joint_summary->Label(-text => 
						     #$panel->spitzer_allot,
						     #-relief => 'sunken',
						     #-anchor => 'e'),
			       $joint_summary->Label(-text => 
						     $panel->swift_allot,
						     -relief => 'sunken',
						     -anchor => 'e'),
			       $joint_summary->Label(-text => 
						     $panel->nustar_allot,
						     -relief => 'sunken',
						     -anchor => 'e'),
			       -sticky => 'nsew'
			       );
    
    $joint_summary->Label(-text => 'Current',
			  -anchor => 'e')->grid
			      ($joint_summary->Label(-textvariable => 
						    $panel->get_ref('hst_cur'),
						     -relief => 'sunken',
						     -anchor => 'e',
						     -foreground => 'blue'),
			       $joint_summary->Label(-textvariable => 
						    $panel->get_ref('xmm_cur'),
						     -relief => 'sunken',
						     -anchor => 'e',
						     -foreground => 'blue'),
			       $joint_summary->Label(-textvariable => 
						   $panel->get_ref('noao_cur'),
						     -relief => 'sunken',
						     -anchor => 'e',
						     -foreground => 'blue'),
			       $joint_summary->Label(-textvariable => 
						   $panel->get_ref('nrao_cur'),
						     -relief => 'sunken',
						     -anchor => 'e',
						     -foreground => 'blue'),
			       #$joint_summary->Label(-textvariable => 
						   #$panel->get_ref('spitzer_cur'),
						     #-relief => 'sunken',
						     #-anchor => 'e',
						     #-foreground => 'blue'),
			       $joint_summary->Label(-textvariable => 
						   $panel->get_ref('swift_cur'),
						     -relief => 'sunken',
						     -anchor => 'e',
						     -foreground => 'blue'),
			       $joint_summary->Label(-textvariable => 
						  $panel->get_ref('nustar_cur'),
						     -relief => 'sunken',
						     -anchor => 'e',
						     -foreground => 'blue'),
			       -sticky => 'nsew'
			       );
    
    $hst = $joint_summary->Label(-textvariable => 
				 $panel->get_ref('hst_bal'),
				 -relief => 'sunken',
				 -anchor => 'e',
				 -foreground => 'blue');
    $xmm = $joint_summary->Label(-textvariable => 
				 $panel->get_ref('xmm_bal'),
				 -relief => 'sunken',
				 -anchor => 'e',
				 -foreground => 'blue');
    $noao = $joint_summary->Label(-textvariable => 
				  $panel->get_ref('noao_bal'),
				  -relief => 'sunken',
				  -anchor => 'e',
				  -foreground => 'blue');
    $nrao = $joint_summary->Label(-textvariable => 
				 $panel->get_ref('nrao_bal'),
				 -relief => 'sunken',
				 -anchor => 'e',
				 -foreground => 'blue');
    #$spitzer = $joint_summary->Label(-textvariable => 
				 #$panel->get_ref('spitzer_bal'),
				 #-relief => 'sunken',
				 #-anchor => 'e',
				 #-foreground => 'blue');
    $swift = $joint_summary->Label(-textvariable => 
				 $panel->get_ref('swift_bal'),
				 -relief => 'sunken',
				 -anchor => 'e',
				 -foreground => 'blue');
    $nustar = $joint_summary->Label(-textvariable => 
				 $panel->get_ref('nustar_bal'),
				 -relief => 'sunken',
				 -anchor => 'e',
				 -foreground => 'blue');
    $joint_summary->Label(-text => '    Balance',
			  -anchor => 'e')->grid
			      ($hst, $xmm,  
			       $noao, $nrao, $swift,$nustar,
			       -sticky => 'nsew');
			       #$noao, $nrao, $spitzer,$swift,$nustar,
    $joint_summary->pack(-side => 'top');
}

## Internal Utility ##
# Name: time_summary
# Purpose: displays summary of time allocations for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub time_summary {
    my $monitor = shift;
    my $panel = shift;

    # Exposure Time
    my $time_summary = $monitor->LabFrame(-label => 'Exposure Time Summary',
					  -labelside => 'acrosstop',
					  -relief => 'groove',
					  )->pack(-side => 'top');
    $time_summary->Label(-text => 'Current AO',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 1,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+1',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 3,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+2',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 5,
				 -columnspan => 1);
    #$time_summary->Label(-text => '',
			 #-width => 10,
			 #)->grid(
			      #$time_summary->Label(-text => 'Slew+Prob',
						   #-anchor => 'center', 
						   #-width => 10),
			      #$time_summary->Label(-text => ''),
			      #$time_summary->Label(-text => 'Slew+Prob',
						   #-anchor => 'center', 
						   #-width => 10),
			      #$time_summary->Label(-text => ''),
			      #$time_summary->Label(-text => 'Slew+Prob',
						   #-anchor => 'center', 
						   #-width => 10),
			      #-sticky => 'nsew',
			      #);
    $time_summary->Label(-text => 'Requested',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						   $panel->time_slew_prob_req,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->time_slew_prob_req_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->time_slew_prob_req_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    $time_summary->Label(-text => '   Allotted',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						 $panel->time_slew_prob_allot,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->time_slew_prob_allot_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->time_slew_prob_allot_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			       );
    
    $time_summary->Label(-text => '     Current',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('time_slew_prob_cur'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('time_slew_prob_cur_1'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('time_slew_prob_cur_2'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    
    my $space = $time_summary->Label(-text => '');
    my $space_1 = $time_summary->Label(-text => '');
    my $space_2 = $time_summary->Label(-text => '');

    $time_slew_prob = $time_summary->Label(-textvariable => 
					 $panel->get_ref('time_slew_prob_bal'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $time_slew_prob_1 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('time_slew_prob_bal_1'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $time_slew_prob_2 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('time_slew_prob_bal_2'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);

    $time_summary->Label(-text => '    Balance',
			 -anchor => 'e',
			 -width => 10)->grid
			     ($time_slew_prob, $space_1, 
			      $time_slew_prob_1, $space_2,
			      $time_slew_prob_2,
			      -sticky => 'nesw');
}

## Internal Utility ##
# Name: lp_time_summary
# Purpose: displays summary of large project time allocations for BPP
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub lp_time_summary {
    my $monitor = shift;
    my $panel = shift;

    # Exposure Time
    my $time_summary = $monitor->LabFrame(-label => 'LP Exposure Time Summary',
					  -labelside => 'acrosstop',
					  -relief => 'groove',
					  )->pack(-side => 'top');
    $time_summary->Label(-text => 'Current AO',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 1,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+1',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 3,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+2',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 5,
				 -columnspan => 1);
    $time_summary->Label(-text => 'Requested',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						   $panel->lp_time_slew_prob_req,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->lp_time_slew_prob_req_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->lp_time_slew_prob_req_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    $time_summary->Label(-text => '   Allotted',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						 $panel->lp_time_slew_prob_allot,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->lp_time_slew_prob_allot_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->lp_time_slew_prob_allot_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			       );
    
    $time_summary->Label(-text => '     Current',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('lp_time_slew_prob_cur'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('lp_time_slew_prob_cur_1'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('lp_time_slew_prob_cur_2'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    
    my $space = $time_summary->Label(-text => '');
    my $space_1 = $time_summary->Label(-text => '');
    my $space_2 = $time_summary->Label(-text => '');

    $lp_time_slew_prob = $time_summary->Label(-textvariable => 
					 $panel->get_ref('lp_time_slew_prob_bal'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $lp_time_slew_prob_1 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('lp_time_slew_prob_bal_1'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $lp_time_slew_prob_2 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('lp_time_slew_prob_bal_2'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);

    $time_summary->Label(-text => '    Balance',
			 -anchor => 'e',
			 -width => 10)->grid
			     ( $lp_time_slew_prob, $space_1, 
			      $lp_time_slew_prob_1, $space_2,
			       $lp_time_slew_prob_2,
			      -sticky => 'nesw');
}

## Internal Utility ##
# Name: vlp_time_summary
# Purpose: displays summary of very large project allocations for BPP
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub vlp_time_summary {
    my $monitor = shift;
    my $panel = shift;

    # Exposure Time
    my $time_summary = $monitor->LabFrame(-label => 'VLP Exposure Time Summary',
					  -labelside => 'acrosstop',
					  -relief => 'groove',
					  )->pack(-side => 'top');
    $time_summary->Label(-text => 'Current AO',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 1,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+1',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 3,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+2',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 5,
				 -columnspan => 1);
    $time_summary->Label(-text => 'Requested',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						   $panel->vlp_time_slew_prob_req,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->vlp_time_slew_prob_req_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->vlp_time_slew_prob_req_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    $time_summary->Label(-text => '   Allotted',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						 $panel->vlp_time_slew_prob_allot,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->vlp_time_slew_prob_allot_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->vlp_time_slew_prob_allot_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			       );
    
    $time_summary->Label(-text => '     Current',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('vlp_time_slew_prob_cur'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('vlp_time_slew_prob_cur_1'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('vlp_time_slew_prob_cur_2'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    
    my $space = $time_summary->Label(-text => '');
    my $space_1 = $time_summary->Label(-text => '');
    my $space_2 = $time_summary->Label(-text => '');

    $vlp_time_slew_prob = $time_summary->Label(-textvariable => 
					 $panel->get_ref('vlp_time_slew_prob_bal'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $vlp_time_slew_prob_1 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('vlp_time_slew_prob_bal_1'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $vlp_time_slew_prob_2 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('vlp_time_slew_prob_bal_2'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);

    $time_summary->Label(-text => '    Balance',
			 -anchor => 'e',
			 -width => 10)->grid
			     ($vlp_time_slew_prob, $space_1, 
			      $vlp_time_slew_prob_1, $space_2,
			      $vlp_time_slew_prob_2,
			      -sticky => 'nesw');
}

## Internal Utility ##
# Name: xvp_time_summary
# Purpose: displays summary of x-ray visionary program allocations for BPP
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub xvp_time_summary {
    my $monitor = shift;
    my $panel = shift;

    # Exposure Time
    my $time_summary = $monitor->LabFrame(-label => 'XVP Exposure Time Summary',
					  -labelside => 'acrosstop',
					  -relief => 'groove',
					  )->pack(-side => 'top');
    $time_summary->Label(-text => 'Current AO',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 1,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+1',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 3,
				 -columnspan => 1);
    $time_summary->Label(-text => 'AO+2',
			 -foreground => 'black',
			)->grid (-row => 0,
				 -column => 5,
				 -columnspan => 1);
    $time_summary->Label(-text => '',
			 -width => 10,
			 )->grid(
			      $time_summary->Label(-text => 'w/slew+prob',
						   -anchor => 'center', 
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 'w/slew+prob',
						   -anchor => 'center', 
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 'w/slew+prob',
						   -anchor => 'center', 
						   -width => 10),
			      -sticky => 'nsew',
			      );
    $time_summary->Label(-text => 'Requested',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						   $panel->xvp_time_slew_prob_req,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->xvp_time_slew_prob_req_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						   $panel->xvp_time_slew_prob_req_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    $time_summary->Label(-text => '   Allotted',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-text => 
						 $panel->xvp_time_slew_prob_allot,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->xvp_time_slew_prob_allot_1,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-text => 
						 $panel->xvp_time_slew_prob_allot_2,
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			       );
    
    $time_summary->Label(-text => '     Current',
			 -anchor => 'e',
			 -width => 10)->grid(
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('xvp_time_slew_prob_cur'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('xvp_time_slew_prob_cur_1'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      $time_summary->Label(-text => ''),
			      $time_summary->Label(-textvariable => 
					 $panel->get_ref('xvp_time_slew_prob_cur_2'),
						    -foreground => 'blue',
						   -relief => 'sunken',
						   -anchor => 'e',
						   -width => 10),
			      -sticky => 'nesw'
			       );
    
    my $space = $time_summary->Label(-text => '');
    my $space_1 = $time_summary->Label(-text => '');
    my $space_2 = $time_summary->Label(-text => '');

    $xvp_time_slew_prob = $time_summary->Label(-textvariable => 
					 $panel->get_ref('xvp_time_slew_prob_bal'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $xvp_time_slew_prob_1 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('xvp_time_slew_prob_bal_1'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);
    $xvp_time_slew_prob_2 = $time_summary->Label(-textvariable => 
					 $panel->get_ref('xvp_time_slew_prob_bal_2'),
					      -foreground => 'blue',
					      -relief => 'sunken',
					      -anchor => 'e',
					      -width => 10);

    $time_summary->Label(-text => '    Balance',
			 -anchor => 'e',
			 -width => 10)->grid
			     ($xvp_time_slew_prob, $space_1, 
			      $xvp_time_slew_prob_1, $space_2,
			      $xvp_time_slew_prob_2,
			      -sticky => 'nesw');
}

## Internal Utility ##
# Name: money_summary
# Purpose: displays summary for archive and theory allocations for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub money_summary {
    my $monitor = shift;
    my $panel = shift;

    # $ Allotment (Arc+The)
    my $money_summary = $monitor->LabFrame(-label => 'Money Allotment Summary',
					  -labelside => 'acrosstop',
					  -relief => 'groove',
					  )->pack(-side => 'top');
    $money_summary->Label(-text => '',
			 )->grid
			     ($money_summary->Label(-text => 'Archive',
						    -anchor => 'center'),
			      $money_summary->Label(-text => 'Theory',
						    -anchor => 'center'),
			      -sticky => 'nsew'
			       );
    $money_summary->Label(-text => '# Approved',
			 -anchor => 'e')->grid
			     ($money_summary->Label(-textvariable => 
						    $panel->get_ref('arc_num'),
						    -anchor => 'e',
						    -relief => 'sunken'),
			      $money_summary->Label(-textvariable => 
						    $panel->get_ref('the_num'),
						    -anchor => 'e',
						    -relief => 'sunken'),
			      -sticky => 'nsew'
			      );
    $money_summary->Label()->grid;
    $money_summary->Label(-text => 'Requested',
			  -anchor => 'e')->grid
			      ($money_summary->Label(-text => $panel->arc_req,
						    -anchor => 'e',
						    -relief => 'sunken'),
			       $money_summary->Label(-text => $panel->the_req,
						    -anchor => 'e',
						    -relief => 'sunken'),
			       -sticky => 'nsew'
			       );
    $money_summary->Label(-text => '   Allotted',
			  -anchor => 'e')->grid
			      ($money_summary->Label(-text => 
						     $panel->arc_allot,
						    -anchor => 'e',
						    -relief => 'sunken'),
			       $money_summary->Label(-text => 
						     $panel->the_allot,
						    -anchor => 'e',
						    -relief => 'sunken'),
			       -sticky => 'nsew'
			       );
    
    $money_summary->Label(-text => '     Current',
			  -anchor => 'e')->grid
			      ($money_summary->Label(-textvariable => 
						    $panel->get_ref('arc_cur'),
						     -foreground => 'blue',
						    -anchor => 'e',
						    -relief => 'sunken'),
			       $money_summary->Label(-textvariable => 
						    $panel->get_ref('the_cur'),
						     -foreground => 'blue',
						    -anchor => 'e',
						    -relief => 'sunken'),
			       -sticky => 'nsew'
			       );
    
    $arc = $money_summary->Label(-textvariable => 
				    $panel->get_ref('arc_bal'),
				    -foreground => 'blue',
				    -anchor => 'e',
				    -relief => 'sunken');
    $the = $money_summary->Label(-textvariable => 
				    $panel->get_ref('the_bal'),
				    -foreground => 'blue',
				    -anchor => 'e',
				    -relief => 'sunken');

    $money_summary->Label(-text => '    Balance',
			  -anchor => 'e')->grid
			      ($arc, $the,
			       -sticky => 'nsew');
}
## Internal Utility ##
# Name: grades_summary
# Purpose: displays summary of grading progress for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub grade_summary {
    my $monitor = shift;
    my $panel = shift;

    # Grade Summary
    my $grades = $monitor->LabFrame(-label => 'Grade Summary',
                                    -labelside => 'acrosstop',
                                    -relief => 'groove',
                                    )->pack(-side => 'top');

    # Number graded
    my $graded = $grades->LabFrame(-label => '# Graded',
                                   -labelside => 'acrosstop',
                                   -relief => 'groove'
                                   )->pack(-side => 'left');
    $graded->Label(-text => '# Graded',
                   -anchor => 'w',
                   -width => 12
                   )->grid
                       ($graded->Label(-textvariable =>
                                             $panel->get_ref('num_graded'),
                                             -relief => 'sunken',
                                             -anchor => 'e',
                                             -width => 4),
                        -sticky => 'nesw'
                        );
    $graded->Label(-text => '# Not Graded',
                         -anchor => 'w',
                         -width => 12)->grid
                             ($graded->Label(-textvariable =>
                                               $panel->get_ref('num_ungraded'),
                                             -relief => 'sunken',
                                             -anchor => 'e',
                                             -width => 4,
                                             -foreground => 'red'),
                              -sticky => 'nesw'
                              );
    # Grade Status
    my $grade_summary = $grades->LabFrame(-label => 'High - Low Grades',
                                          -labelside => 'acrosstop',
                                          -relief => 'groove',
                                          )->pack(-side => 'left');
    $grade_summary->Label(-text => '',
                         -anchor => 'center',
                         -width => 6)->grid
                             ($grade_summary->Label(-text => 'NormG',
                                                    -anchor => 'center',
                                                    -width => 6,),
                             $grade_summary->Label(-text => 'AvgG',
                                                    -anchor => 'center',
                                                    -width => 6,),
                              -sticky => 'nesw'
                              );
    $grade_summary->Label(-text => 'High',
                         -anchor => 'w',
                         -width => 6)->grid
                             ($grade_summary->Label(-textvariable =>
                                                  $panel->get_ref('norm_high'),
                                                    -relief => 'sunken',
                                                    -anchor => 'center',
                                                    -width => 6,),
                             $grade_summary->Label(-textvariable =>
                                                 $panel->get_ref('grade_high'),
                                                    -relief => 'sunken',
                                                    -anchor => 'center',
                                                    -width => 6,),
                              -sticky => 'nesw'
                              );
    $grade_summary->Label(-text => 'Pass',
                         -anchor => 'w',
                         -width => 6)->grid
                             ($grade_summary->Label(-text => $panel->norm_pass,
                                                    -relief => 'sunken',
                                                    -anchor => 'center',
                                                    -width => 6,),
                             $grade_summary->Label(-textvariable =>
                                                 $panel->get_ref('grade_pass'),
                                                    -relief => 'sunken',
                                                    -anchor => 'center',
                                                    -width => 6,),
                              -sticky => 'nesw'
                              );
    $grade_summary->Label(-text => 'Low',
                         -anchor => 'w',
                         -width => 6)->grid
                             ($grade_summary->Label(-textvariable =>
                                                  $panel->get_ref('norm_low'),
                                                    -relief => 'sunken',
                                                    -anchor => 'center',
                                                    -width => 6,),
                             $grade_summary->Label(-textvariable =>
                                                 $panel->get_ref('grade_low'),
                                                    -relief => 'sunken',
                                                    -anchor => 'center',
                                                    -width => 6,),
                              -sticky => 'nesw'
                              );

}



## Internal Utility ##
# Name: lp_grade_summary
# Purpose: displays summary of grading progress for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub lp_grade_summary {
    my $monitor = shift;
    my $panel = shift;

    # Grade Summary
    my $grades = $monitor->LabFrame(-label => 'Grade Summary',
				    -labelside => 'acrosstop',
				    -relief => 'groove',
				    )->pack(-side => 'top');

    # Number graded
    my $graded = $grades->LabFrame(-label => '# Graded',
				   -labelside => 'acrosstop',
				   -relief => 'groove'
				   )->pack(-side => 'left');
    $graded->Label(-text => '',
                         -anchor => 'center',
                         -width => 6)->grid(
                             #$graded->Label(-text => 'All',
                                                    #-anchor => 'center',
                                                    #-width => 3,),
                              $graded->Label(-text => 'LP',
                                                    -anchor => 'center',
                                                    -width => 3,),
                              $graded->Label(-text => 'VLP',
                                                    -anchor => 'center',
                                                    -width => 3,),
			-sticky => 'nesw'
			);
	
    $graded->Label(-text => '# Graded',
		   -anchor => 'w',
		   -width => 12
		   )->grid(
		       #$graded->Label(-textvariable => 
					     #$panel->get_ref('num_graded'),
					     #-relief => 'sunken',
					     #-anchor => 'e',
					     #-width => 4),
		        $graded->Label(-textvariable => 
					     $panel->get_ref('lp_num_graded'),
					     -relief => 'sunken',
					     -anchor => 'e',
					     -width => 4),
		        $graded->Label(-textvariable => 
					     $panel->get_ref('vlp_num_graded'),
					     -relief => 'sunken',
					     -anchor => 'e',
					     -width => 4),
			-sticky => 'nesw'
			);
    $graded->Label(-text => '# Not Graded',
			 -anchor => 'w',
			 -width => 12)->grid(
			     #$graded->Label(-textvariable => 
					       #$panel->get_ref('num_ungraded'),
					     #-relief => 'sunken',
					     #-anchor => 'e',
					     #-width => 4,
					     #-foreground => 'red'),
			      $graded->Label(-textvariable => 
					       $panel->get_ref('lp_num_ungraded'),
					     -relief => 'sunken',
					     -anchor => 'e',
					     -width => 4,
					     -foreground => 'red'),
			      $graded->Label(-textvariable => 
					       $panel->get_ref('vlp_num_ungraded'),
					     -relief => 'sunken',
					     -anchor => 'e',
					     -width => 4,
					     -foreground => 'red'),
			      -sticky => 'nesw'
			      );



    # Grade Status
    my $space = $grades->LabFrame(-label => '  ',
					  -labelside => 'acrosstop',
					  -relief => 'flat',
					  )->pack(-side => 'left');
    my $grade_summary = $grades->LabFrame(-label => 'High - Low Grades',
					  -labelside => 'acrosstop',
					  -relief => 'groove',
					  )->pack(-side => 'left');
    $grade_summary->Label(-text => '',
			 -anchor => 'center',
			 -width => 6)->grid(
			     #$grade_summary->Label(-text => 'All
#NormG',
						    #-anchor => 'center',
						    #-width => 6,),
			     #$grade_summary->Label(-text => 'All
#AvgG',
						    #-anchor => 'center',
						    #-width => 6,),
			     $grade_summary->Label(-text => 'LP
NormG',
						    -anchor => 'center',
						    -width => 6,)->pack,
			     $grade_summary->Label(-text => 'LP  
AvgG',
						    -anchor => 'center',
						    -width => 6,)->pack,
			     $grade_summary->Label(-text => 'VLP 
NormG',
						    -anchor => 'center',
						    -width => 6,)->pack,
			     $grade_summary->Label(-text => 'VLP 
AvgG',
						    -anchor => 'center',
						    -width => 6,)->pack,
			      -sticky => 'nesw'
			      );
    $grade_summary->Label(-text => 'High',
			 -anchor => 'w',
			 -width => 6)->grid(
			     #$grade_summary->Label(-textvariable => 
					  #$panel->get_ref('norm_high'),
						    #-relief => 'sunken',
						    #-anchor => 'center',
						    #-width => 6,),
			     #$grade_summary->Label(-textvariable =>
					 #$panel->get_ref('grade_high'),
						    #-relief => 'sunken',
						    #-anchor => 'center',
						    #-width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('lp_norm_high'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('lp_grade_high'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('vlp_norm_high'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('vlp_grade_high'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			      -sticky => 'nesw'
			      );
    $grade_summary->Label(-text => 'Pass',
			 -anchor => 'w',
			 -width => 6)->grid(
			     #$grade_summary->Label(-text => $panel->norm_pass,
						    #-relief => 'sunken',
						    #-anchor => 'center',
						    #-width => 6,),
			     #$grade_summary->Label(-textvariable =>
					 #$panel->get_ref('grade_pass'),
						    #-relief => 'sunken',
						    #-anchor => 'center',
						    #-width => 6,),
			     $grade_summary->Label(-text => 
					$panel->lp_norm_pass,
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('lp_grade_pass'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-text => 
					$panel->vlp_norm_pass,
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('vlp_grade_pass'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			      -sticky => 'nesw'
			      );
    $grade_summary->Label(-text => 'Low',
			 -anchor => 'w',
			 -width => 6)->grid(
			     #$grade_summary->Label(-textvariable => 
						  #$panel->get_ref('norm_low'),
						    #-relief => 'sunken',
						    #-anchor => 'center',
						    #-width => 6,),
			     #$grade_summary->Label(-textvariable =>
						 #$panel->get_ref('grade_low'),
						    #-relief => 'sunken',
						    #-anchor => 'center',
						    #-width => 6,),
			     $grade_summary->Label(-textvariable => 
					  $panel->get_ref('lp_norm_low'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('lp_grade_low'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable => 
					  $panel->get_ref('vlp_norm_low'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			     $grade_summary->Label(-textvariable =>
					 $panel->get_ref('vlp_grade_low'),
						    -relief => 'sunken',
						    -anchor => 'center',
						    -width => 6,),
			      -sticky => 'nesw'
			      );

}

## Internal Utility ##
# Name: status_summary
# Purpose: displays summary of proposal and target statuses for a panel
# Parameters: top level monitor window
#             panel object
# Returns: nothing
sub status_summary {
    my $monitor = shift;
    my $panel = shift;
    my $bpplbl = "LP";
    $bpplbl = "LP+XVP" if ($isXVP) ; 
    $bpplbl = "LP+VLP" if ($isVLP) ; 

    # Status Breakdown
    my $summary = $monitor->LabFrame(-label => 'Status Summary',
				     -labelside => 'acrosstop',
				     -relief => 'groove',
				     )->pack(-side => 'top');
    $summary->Label()->grid
			($summary->Label(-text => $STAT_YES,
					      -anchor => 'center',
					      -width => 4),
			 $summary->Label(-text => $STAT_NO,
					      -anchor => 'center',
					      -width => 4),
			 $summary->Label(-text => $STAT_GRAY,
					      -anchor => 'center',
					      -width => 4),
			 $summary->Label(-text => $STAT_BPP,
					      -anchor => 'center', 
					      -width => 4),
			 $summary->Label(-text => 'All',
					      -anchor => 'center', 
					      -width => 4),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => 'GO Props',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable => 
					 $panel->get_ref('std_prop_y'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('std_prop_n'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('std_prop_g'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('std_prop_p'),
					 -relief => 'sunken',
					 -anchor => 'e'),
			 $summary->Label(-text => $panel->std_prop,
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => 'GO Targets',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable => 
					 $panel->get_ref('std_targ_y'),
					 -relief => 'sunken',
					 -anchor => 'e'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('std_targ_n'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('std_targ_g'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('std_targ_p'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-text => $panel->std_targ,
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => 'TOO Props',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable => 
					 $panel->get_ref('too_prop_y'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('too_prop_n'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('too_prop_g'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('too_prop_p'),
					 -relief => 'sunken',
					 -anchor => 'e'),
			 $summary->Label(-text => $panel->too_prop,
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => 'TOO Targets',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable => 
					 $panel->get_ref('too_targ_y'),
					 -relief => 'sunken',
					 -anchor => 'e'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('too_targ_n'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('too_targ_g'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('too_targ_p'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-text => $panel->too_targ,
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => $bpplbl.' Props',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable =>
					 $panel->get_ref('bpp_prop_y'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('bpp_prop_n'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('bpp_prop_g'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable => 
					 $panel->get_ref('bpp_prop_p'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-text =>
					 ($panel->lp_prop + $panel->xvp_prop + $panel->vlp_prop),
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => $bpplbl .' Targs',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable => 
					 $panel->get_ref('bpp_targ_y'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('bpp_targ_n'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('bpp_targ_g'),
					 -anchor => 'e',
					 -relief => 'sunken'),
			 $summary->Label(-textvariable => 
					 $panel->get_ref('bpp_targ_p'),
					 -relief => 'sunken',
					 -anchor => 'e'),
			 $summary->Label(-text =>
					 ($panel->lp_targ + $panel->vlp_targ + $panel->xvp_targ),
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => 'Archive',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable => 
					 $panel->get_ref('arc_y'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('arc_n'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-relief => 'sunken'),
			 $summary->Label(-relief => 'sunken'),
			 $summary->Label(-text => $panel->arc_prop,
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
    $summary->Label(-text => 'Theory',
		    -anchor => 'e'
		    )->grid
			($summary->Label(-textvariable => 
					 $panel->get_ref('the_y'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-textvariable =>
					 $panel->get_ref('the_n'),
					 -relief => 'sunken',
					 -anchor => 'e',),
			 $summary->Label(-relief => 'sunken'),
			 $summary->Label(-relief => 'sunken'),
			 $summary->Label(-text => $panel->the_prop,
					 -relief => 'sunken',
					 -anchor => 'e'),
			 -sticky => 'nsew',
			 );
}

## Internal Utility ##
# Name: set_foreground
# Purpose: configures the foreground color of a widget
# Parameters: widget
#             color
# Returns: nothing
sub set_foreground {
  my ($widget, $color) = @_;
  $widget->configure(-foreground => $color) if defined $widget;
}

1;


__END__

=head1 NAME

Monitor - This module populates the monitor window

=head1 VERSION

$Revision: 1.22 $

=head1 SYNOPSIS

    use Panel;
    use Tk;

    my $panel = new Panel($dbh1, $panel_Id, $if, $verbosity);
    my $monitor = $mw->Toplevel();
    fill_monitor($monitor, $panel, $verbosity);

=head1 DESCRIPTION

Provide up-to-date panel statistics.

=head1 PUBLIC METHODS

=head2 fill_monitor($monitor, $panel, $verbosity)

Populate the monitor top level with panel statistics

=over 

=item $monitor - top level widget

=item $panel - Panel object

=item $verbosity - level of verbosity

=back

=head2 set_color($panel)

Sets the colors of fields based on their value relative to allotted values for
the panel.

=over 

=item $panel - Panel object

=back

=head1 PRIVATE METHODS

=head2 fixed_totals

Displays the fixed totals for a panel.

=head2 tc_too_summary

Displays a summary of tc/too allocations for a panel

=head2 joint_summary

Displays a summary of joint allocations for a panel

=head2 time_summary

Displays a summary of time allocations for a panel

=head2 lp_time_summary

Displays a summary of large project time allocations for BPP

=head2 vlp_time_summary

Displays a summary of very large project time allocations for BPP

=head2 xvp_time_summary

Displays a summary of X-ray Visionary Projects time allocations for BPP

=head2 money_summary

Displays summary for archive and theory allocations for a panel

=head2 grades_summary

Displays summary of grading progress for a panel

=head2 status_summary

Displays summary of status for a panel

=head2 set_foreground

Configures the foreground color of a widget

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
