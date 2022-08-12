#
# EditPage.pm - This widget populates a proposal page with the data from 
#               a proposal and provides edit access to the data
#
# Purpose: Provides a convenient interface for updating information for 
#          proposals
#          
# Copyright (c) 2005-2022
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package EditPage;

use strict;
use warnings;

use Carp;
use config;
use Data::Dumper;
use base qw/ Tk::Frame /;
use Tk::Balloon;
use Regexp::Common qw/number/;

require Tk::Dialog;
require Tk::Pane;
require Tk::LabEntry;
require Tk::ViewTable;

Construct Tk::Widget 'EditPage';


use vars qw(@Pprops @Tprops @Jprops @TooProps @AltProps
	    $groups $lock_state $verbose ) ;
# Description of global variables:
#    @Pprops - The array of proposal properties contained in the panel object
#    @Tprop - The array of target properties contained in the panel object
#    @Jprops - The array of joint properties contained in the panel object
#    @TooProps - The array of TOO properties contained in the panel object
#    @AltProps - The array of alternate target group properties contained in
#                the panel object
#    $groups - array ref of groups available to the proposal
#    $lock_state - flag indicating the panel is locked for edits

## Class Method ##
# Name: Populate
# Purpose: populate the notebook page with proposal data and widgets for
#          editing the data
# Parameters: the notebook page
#             -panel - a panel object
#             -prop_id - proposal id
#             -verbose - level of verbosity
# Returns: nothing
sub Populate {
  my ($w, $args) = @_;

  croak( "must specify panel" )
    unless exists $args->{-panel};

  my $panel = $args->{-panel};
  my $prop_id = $args->{-prop_id};
  $verbose = $args->{-verbose};

  my $obj = $panel->proposals->proposal($prop_id);
  my $Pprop_ref = $obj->PROPOSAL_PROPERTIES;
  @Pprops = @$Pprop_ref;
  my $Tprop_ref = $obj->TARGET_PROPERTIES;
  @Tprops = @$Tprop_ref;
  my $Jprop_ref = $obj->JOINT_PROPERTIES;
  @Jprops = @$Jprop_ref;
  my $TooProp_ref = $obj->TOO_PROPERTIES;
  @TooProps = @$TooProp_ref;
  my $AltProp_ref = $obj->ALT_GRP_PROPERTIES;
  @AltProps = @$AltProp_ref;
  $groups = $panel->groups;
  $lock_state = 'normal';
  $lock_state = 'disabled' if $panel->locked =~ /Y/;
  croak( "-panel must be a Panel object" )
      unless UNIVERSAL::isa( $panel, 'Panel' );
  $w->ConfigSpecs( -panel => [ 'PASSIVE' ],
		   -prop_id => [ 'PASSIVE' ],
		   -verbose => [ 'PASSIVE' ],
		   -savecmd => [ 'CALLBACK' ],
		   -closecmd => [ 'CALLBACK' ],
		   -changecmd => [ 'CALLBACK' ],
		   -switchcmd => [ 'CALLBACK' ],
		   );

  $w->SUPER::Populate( $args );

  my $pData = $w->privateData;
  $pData->{chgData} = {};
#print STDERR "main set switched to 0\n";
  $pData->{switched} = 0;
  my $data = $pData->{propData} = {};
  $w->reset( $obj );


  print "EditPage::Populate - populating widget\n" if $verbose;
  # Put a scrollbar at the top of the page
  my $SFrame = $w->Frame();
  my $label = '';
  $label = 'This proposal has been turned-off' if ($obj->type !~ /ARC|THE/ and
						   $obj->prop_status =~ /N/
						   and $obj->rank);
  $label = 'Panel is locked' if $panel->locked =~ /Y/;
  my $scroll_pane = $SFrame->Scrolled('Pane',
				      -label => $label,
				      -scrollbars => 'onoe',
				      -gridded => 'xy',
				      -sticky => 'ns',
				      -width => 1000,
				      -height => 420
				     );

  # This is the general information section.  It spans column 0 in
  # the grid.

  my $prop_info = $w->prop_info( $obj, $data, $scroll_pane );
  $prop_info->grid(-column => 0, -row => 0, -sticky => 'n');

  # These objects are the title and abstract of the proposal (column 3 of
  # the grid)
  my $abstract = $w->abstract( $obj, $scroll_pane );
  $abstract->grid(-column => 0, -row => 1, -sticky => 'n' );

  # These objects go in the middle portion of the page.  It contains
  # target information, grades, and comments. (column 1 of the grid)

  my $tg_frame = $scroll_pane->Frame()->grid(-column => 1, -row => 0,
					     -rowspan => 2, -sticky => 'n' );;

  my $targets = $w->targets( $obj, $data, $tg_frame );
  $targets->pack(-side => 'top', -pady => '15');

  # These are the grades
  my $grades = $w->grades( $obj, $data, $tg_frame );
  $grades->pack(-side => 'top', -pady => '15');
  my $g_cmts = $w->grade_cmt( $obj, $data,  $tg_frame );
  $g_cmts->pack(-side => 'top', -pady => '0');
  $pData->{g_cmts} = $g_cmts;

  if ($obj->panel_id == 99) {
    my $bppstd = $w->bpp_stddev( $obj, $tg_frame );
    $bppstd->pack(-side => 'top', -pady => '15');
  }

  # no longer used as of cycle 14
  # This sets whether triaged grades should be lowered
  #my $lower = $w->lower_triage( $obj, $data, $tg_frame );
  #$lower->pack(-side => 'top', -pady => '15');

  # This sets whether the proposal is infeasible
  my $infeasible = $w->infeasible_prop( $obj, $data, $tg_frame );
  $infeasible->pack(-side => 'top', -pady => '15');

  my $altglbl = "Other Grades";
  my $altg_state = $lock_state;
  $altg_state = 'disabled' if $obj->triage =~ /Y|A/ ;

  if ($obj->hasAlternates() > 0) {
    $altglbl .= "*";
  }

  # These are the comments
  my ($comments,$txtfr) = $w->comments( $obj, $data, $tg_frame );
  $comments->pack(-side => 'top', -pady => '15');
  $pData->{cmtw} = $txtfr;

  $scroll_pane->pack( -expand => 1, -fill => 'both' );

  # These are the buttons at the bottom of the page (last row of the grid)
  my $BFrame = $w->Frame();
  $BFrame->configure( -relief => 'raised', -bd => 1 );
  $BFrame->Button( -text => 'Save' ,
		   -takefocus => 1,
		   -state => $lock_state,
		   -command => [ save  => $w ]
		   )->grid( -row => 0, -column => 0, -ipadx => 2);
  $BFrame->Button( -text => 'Close',
		   -takefocus => 1,
		   -command => [close => $w ]
		 )->grid( -row => 0,
			  -column => 1, -ipadx => 2);
  $BFrame->Button( -text => 'Reset' ,
		   -takefocus => 1,
		   -state => $lock_state,
		   -command => [ reset => $w ]
		   )->grid( -row => 0, -column => 2, -ipadx => 2);
  $BFrame->Button( -text => $altglbl ,
		   -takefocus => 1,
		   -state => $altg_state,
		   -command => [ switchgrades  => $w ]
		 )->grid( -row => 0,
			  -column => 3, -ipadx => 2);

  $SFrame->pack( -expand => 1, -fill => 'both' );
  $BFrame->pack( -side => 'bottom', -fill => 'x' );

}


## Internal Utility ##
# Name: prop_info
# Purpose: populates the proposal information section of the page
# Parameters: proposal object
#             data that can be edited
#             top level frame of page
sub prop_info {
  my ( $w, $obj, $data, $top ) = @_;
  my $prop_frame = $top->Frame;

  my $row = 0;

  # GroupId here

  my %group = init_field('group_id', 'proposal');

  $prop_frame->Label(-text => 'Group Id' )->grid(-row => $row,
						 -column => 0,
						 -sticky => 'e');

  # Group Info - on for all proposals
  my $group_pane = $prop_frame->Scrolled('Pane',
					 -scrollbars => 'oe',
					 -relief => 'ridge',
					 -borderwidth => 2,
					 -height => 70,
					 -sticky => 'nsew'
					)->grid(-row => $row,
						-column => 1,
						-sticky => 'w');

  foreach my $idx (sort {$a <=> $b} keys %$groups) {
    $group_pane->Radiobutton(-text => $groups->{$idx},
			     -value => $groups->{$idx},
			     -state => $lock_state,
			     -variable => \$data->{group_id},
			     -command => sub{
			       track_changes($w,
					     \%group,
					     $data->{group_id},
					     $obj)},
			     -anchor => 'w'
			    )->pack(-side => 'top',
				    -expand => 1,
				    -fill => 'both');
  }
  $row++;

  # Proposal information - Same for all proposals except for
  #    - proposal status: no edit for triaged proposals (archive/theory)
  #    - joint observations: no edit based on status+triage
  my $prop_label = $prop_frame->Label(-text => "Proposal Information",
				      -font => "Helvetica 14"
				      )->grid(-row => $row,
					      -columnspan => 2);
  $row++;
  $row = $w->fill_rl_grid($prop_frame, 20, 15, 'blue', $row, 0,
			  ['PI:' => $obj->last_name()],
			  ['Proposal Type:' => $obj->type()],
			  ['SubType:' => $obj->big_proj()],
			  ['Conflict:' => $obj->conflict],
			  ['Proposal Constraints:' => $obj->tc_prop],
			  ['Reverse TOO:' => $obj->rev_too],
			  ['Triage:' => $obj->triage],
			  ['Joint Flag:' => $obj->joint_flag],
			  );

   if ($obj->mcop eq 'N') {
    $row = $w->fill_rl_grid($prop_frame, 20, 15, 'blue', $row, 0,
			    ['# Obs Req:' => $obj->num_obs_req]
			   );
    $prop_frame->Label(-text => '# Obs App:',
		       -anchor => 'e',
		       -foreground => 'black',
		       -relief => 'flat',
		       -width => 20,
		      )->grid
			($prop_frame->Label(-textvariable =>
					    $obj->get_ref('num_obs_app'),
					    -anchor => 'w',
					    -width => 15,
					    -foreground => 'red',)
			)->grid(-column => 0,
				-row => $row);
    $row++;

    if ($obj->link_id) {
      $row = $w->fill_rl_grid($prop_frame, 20, 15, 'blue', $row, 0,
			      ['Linked Proposal:' => $obj->link_id()]
			     );
    }

    # Proposal information that does not apply to archive/theory
    if ($obj->type() !~ /ARC|THE/) {
      if ($obj->type() =~ /TOO/) {
        $prop_frame->Label(-text => 'TOO 0-5:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('vf_app'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column => 0,
				  -row => $row);
        $row++;
        $prop_frame->Label(-text => 'TOO 5-20:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('f_app'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column => 0,
				  -row => $row);
        $row++;
        $prop_frame->Label(-text => 'TOO 20-40:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('s_app'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column => 0,
				  -row => $row);
        $row++;
        $prop_frame->Label(-text => 'TOO >=40:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('vs_app'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column => 0,
				  -row => $row);
        $row++;
      } else {
        $prop_frame->Label(-text => 'RC Total :',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('rc_score_app'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column => 0,
				  -row => $row);
        $row++;
      }

      $prop_frame->Label(-text => 'Total Tax:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('tax_tot'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column=> 0,
				  -row => $row);
      $row++;

      $row = $w->fill_rl_grid($prop_frame, 20, 15, 'blue', $row, 0,
			      ['Req Time:' => $obj->total_req_time]);
      $prop_frame->Label(-text => 'App Time:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('total_app_time'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column=> 0,
				  -row => $row);
      $row++;

		$row = $w->fill_rl_grid($prop_frame, 20, 15, 'blue', $row, 0,
			['HEL Req Time:' => $obj->total_req_hel]);
		$prop_frame->Label(-text => 'HEL App Time:',
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
			-width => 20,
		)->grid
			($prop_frame->Label(-textvariable =>
				$obj->get_ref('total_app_hel'),
				-anchor => 'w',
				-width => 15,
				-foreground => 'red',)
			)->grid(-column=> 0,
			-row => $row);
		$row++;

      $row = $w->fill_rl_grid($prop_frame, 20, 15, 'blue', $row, 0,
			      ['# Targets Req:' => $obj->num_targ_req()]);
      $prop_frame->Label(-text => '# Targets App:',
			 -anchor => 'e',
			 -foreground => 'black',
			 -relief => 'flat',
			 -width => 20,
			)->grid
			  ($prop_frame->Label(-textvariable =>
					      $obj->get_ref('num_targ_app'),
					      -anchor => 'w',
					      -width => 15,
					      -foreground => 'red',)
			  )->grid(-column=> 0,
				  -row => $row);
      $row++;
    }
    else {
      my $amt = "\$" . $obj->total_req_time() . 'k';
      $row = $w->fill_rl_grid($prop_frame, 20, 15, 'blue', $row, 0,
			      ['Amt Req:' => $amt],
			     );

      # Allow the amount of arc/the to be edited if the -if flag was given
      if ($obj->open_edit eq 'Y') {
	$prop_frame->Label(-text => 'Amt Approved:',
			   -anchor => 'e',
			   -foreground => 'black',
			   -relief => 'flat',
			   -width => 20,
			  )->grid
			    (myEntry($prop_frame, $obj,
				     -textvariable => \$data->{total_app_time},
				     -width => 5,
				     -validatecommand =>
				     [ \&validate,
				       'real', ['Amt Approved', 0.001,
						$obj->total_req_time],
				       [$w, {field => 'total_app_time',
					     object => 'proposal',
					     index => 0,
					     datatype => 'real'},
					$data->{total_app_time},
					$obj]],
				    )

			    )->grid(-column => 0,
				    -row => $row);
	$row++;
      }


      # Proposal approval for ARC/THE here - no edit for triaged proposals
      my %status = init_field('prop_status', 'proposal');
      $prop_frame->Label( -text => 'Proposal Status:'
			)->grid( -row => $row,
				 -column => 0,
				 -sticky => 'e',
			       );
      my $stat_frame = $prop_frame->Frame()->grid(-row => $row,
						  -column => 1,
						  -sticky => 'w');
      my $prop_state = $lock_state;
      $prop_state = 'disabled' if $obj->triage =~ /Y|A/ or
	($obj->prop_status =~ /N/ and $obj->type !~ /ARCHIVE|THEORY/);
      foreach my $ans (qw(Y N)) {
	$stat_frame->Radiobutton(-text => $ans,
				 -value => $ans,
				 -state => $prop_state,
				 -variable => \$data->{prop_status},
				 -command => sub{
				   track_changes($w,
						 \%status,
						 $data->{prop_status},
						 $obj)}
				)->pack(-side=>'left');
      }
      $row++;
    }
  }
  else {
    # Display the multicycle info in a table
    my $mc_lable = $prop_frame->Label(-text=>"Multicyle Information",
				      -font => "Helvetica 14"
				     )->grid(-columnspan => 2,
					     -row => $row);
    $row++;
    my $mc_frame = $prop_frame->Frame()->grid(-columnspan => 2,
					      -row => $row);

    # Row 1 are the labels
    my $ao0 = $AO;
    my $ao1 = $AO + 1;
    my $ao2 = $AO + 2;
    my @labels = ($mc_frame->Label(-text => $ao0,
				   -borderwidth => 2,
				   -relief => 'groove' ),
		  $mc_frame->Label(-text => $ao1,
				   -borderwidth => 2,
				   -relief => 'groove'),
		  $mc_frame->Label(-text => $ao2,
				   -relief => 'groove',
				   -borderwidth => 2,),
		 );
    $mc_frame->Label( -text => '',
		      -borderwidth => 2,
		      -relief => 'groove'
		    )->grid( @labels,
			     -sticky => 'nsew');
    $row++;

    my @obs_req = ($mc_frame->Label(-text => $obj->get('num_obs_req'),
				    -foreground => 'blue',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('num_obs_req_1'),
				    -foreground => 'blue',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('num_obs_req_2'),
				    -foreground => 'blue',
				    -borderwidth => 2,
				    -relief => 'groove'),
		  );
    $mc_frame->Label(-text => '# Obs Req',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@obs_req,
			    -sticky => 'nsew' );
    $row++;

    my @obs_app = ($mc_frame->Label(-textvariable =>
				    $obj->get_ref('num_obs_app'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('num_obs_app_1'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('num_obs_app_2'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		  );
    $mc_frame->Label(-text => '# Obs App',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@obs_app,
			    -sticky => 'nsew' );
    $row++;

    my @rc_score_app = ($mc_frame->Label(-textvariable =>
				    $obj->get_ref('rc_score_app'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('rc_score_app_1'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('rc_score_app_2'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		  );
    $mc_frame->Label(-text => 'RC Total ',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@rc_score_app,
			    -sticky => 'nsew' );
    $row++;

    my @tax_app = ($mc_frame->Label(-textvariable =>
				    $obj->get_ref('tax_tot'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('tax_tot_1'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('tax_tot_2'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		  );
    $mc_frame->Label(-text => 'Total Tax',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@tax_app,
			    -sticky => 'nsew' );
    $row++;

    my @time_req = ($mc_frame->Label(-text => $obj->get('total_req_time'),
				    -foreground => 'blue',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('total_req_time_1'),
				    -foreground => 'blue',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('total_req_time_2'),
				    -foreground => 'blue',
				    -borderwidth => 2,
				    -relief => 'groove'),
		  );
    $mc_frame->Label(-text => 'Req Time',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@time_req,
			    -sticky => 'nsew' );
    $row++;

    my @time_app = ($mc_frame->Label(-textvariable =>
				    $obj->get_ref('total_app_time'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('total_app_time_1'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('total_app_time_2'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		  );
    $mc_frame->Label(-text => 'App Time',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@time_app,
			    -sticky => 'nsew' );
    $row++;
	   my @hel_req = ($mc_frame->Label(-text => $obj->get('total_req_hel'),
		   -foreground => 'blue',
		   -borderwidth => 2,
		   -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('total_req_hel_1'),
			   -foreground => 'blue',
			   -borderwidth => 2,
			   -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('total_req_hel_2'),
			   -foreground => 'blue',
			   -borderwidth => 2,
			   -relief => 'groove'),
	   );
	   $mc_frame->Label(-text => 'HEL Req Time',
		   -borderwidth => 2,
		   -relief => 'groove'
	   )->grid(@hel_req,
		   -sticky => 'nsew' );
	   $row++;

	   my @hel_app = ($mc_frame->Label(-textvariable =>
		   $obj->get_ref('total_app_hel'),
		   -foreground => 'red',
		   -borderwidth => 2,
		   -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
			   $obj->get_ref('total_app_hel_1'),
			   -foreground => 'red',
			   -borderwidth => 2,
			   -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
			   $obj->get_ref('total_app_hel_2'),
			   -foreground => 'red',
			   -borderwidth => 2,
			   -relief => 'groove'),
	   );
	   $mc_frame->Label(-text => 'HEL App Time',
		   -borderwidth => 2,
		   -relief => 'groove'
	   )->grid(@hel_app,
		   -sticky => 'nsew' );
	   $row++;

	   my @targ_req = ($mc_frame->Label(-text => $obj->get('num_targ_req'),
		   -foreground => 'blue',
		   -borderwidth => 2,
		   -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('num_targ_req_1'),
			   -foreground => 'blue',
			   -borderwidth => 2,
			   -relief => 'groove'),
		   $mc_frame->Label(-text =>$obj->get('num_targ_req_2'),
			   -foreground => 'blue',
			   -borderwidth => 2,
			   -relief => 'groove'),
	   );
    $mc_frame->Label(-text => '# Targets Req',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@targ_req,
			    -sticky => 'nsew' );
    $row++;

    my @targ_app = ($mc_frame->Label(-textvariable =>
				    $obj->get_ref('num_targ_app'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('num_targ_app_1'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		   $mc_frame->Label(-textvariable =>
				    $obj->get_ref('num_targ_app_2'),
				    -foreground => 'red',
				    -borderwidth => 2,
				    -relief => 'groove'),
		  );
    $mc_frame->Label(-text => '# Targets App',
		     -borderwidth => 2,
		     -relief => 'groove'
		    )->grid(@targ_app,
			    -sticky => 'nsew' );
    $row++;

  }

  $row++;

  # The joint time goes here if the joint flag is not "None"
  #     - List only observatories requested
  #     - no edit based on status and triage
  #     - exclude any joint contingent
  # If this is a big project, then just display the amount
  # requested.
  if ($obj->joint_flag() !~ /None/) {
   my @obs = split /\+/, $obj->joint_flag();
   if ($obj->joint_flag() !~ /-c/ || $#obs > 0) {

    my $joint_label = $prop_frame->Label(-text => "Joint Observations",
					 -font => "Helvetica 14"
					)->grid(-columnspan => 2,
						-row => $row);
    $row++;
    my $joint_frame = $prop_frame->Frame()->grid(-columnspan => 2,
						 -row => $row);
    # Row 1 are the labels
    my @labels = ($joint_frame->Label(-text => 'Units',
				       -borderwidth => 2,
				       -relief => 'groove' ),
		   $joint_frame->Label(-text => 'Datatype',
				       -borderwidth => 2,
				       -relief => 'groove'),
		   $joint_frame->Label(-text => 'Req Time',
				       -relief => 'groove',
				       -borderwidth => 2,),
		  );
    push @labels, $joint_frame->Label(-text => 'App time',
				       -relief => 'groove',
				       -borderwidth => 2,) if
					 $obj->prop_status !~ /$STAT_BPP/;

    $joint_frame->Label( -text => 'Obs',
			 -borderwidth => 2,
			 -relief => 'groove'
		       )->grid( @labels,
				-sticky => 'nsew');
    # One row per observatory
    my $row = 0;
    foreach my $obs (@obs) {
      if ($obs !~ /-c/ ) {
        $row++;
        my $lcobs = lc $obs;
        my %jnt_time = init_field("app_time", 'joint', $obs,
				$obj->get("$lcobs\_dtype"));

        my @joint_data = ($joint_frame->Label(-text =>
					    $obj->
					    get("$lcobs\_units"),
					    -borderwidth => 2,
					    -relief => 'groove'),
			$joint_frame->Label(-text =>
					    $obj->
					    get("$lcobs\_dtype"),
					    -borderwidth => 2,
					    -relief => 'groove'),
			$joint_frame->Label(-text =>
					    $obj->
					    get("$lcobs\_req"),
					    -borderwidth => 2,
					    -relief => 'groove'),
		       );
        $joint_frame->Label(-text => $obs,
			  -borderwidth => 2,
			  -relief => 'groove'
			 )->grid(@joint_data,
				 -sticky => 'nsew' );

        # if the proposal is a BPP, then there is no approval
        if ($obj->prop_status !~ /$STAT_BPP/) {
	  myEntry($joint_frame, $obj,
		-textvariable =>
		\$data->{"$lcobs\_app"},
		-width => 5,
		-validatecommand =>
		[ \&validate,
		  $jnt_time{datatype} =>
		  ["$obs joint time", 0,
		   $data->{"$lcobs\_req"}],
		  [$w, \%jnt_time,
		   $data->{"$lcobs\_app"},
		   $obj]]
	       )->grid(-row => $row,
		       -column => 4,
		       -sticky => 'nsew');
        }
      }
    }
   }
  }

  $prop_frame->pack(-expand => 1,
		    -fill => 'both');

}

## Internal Utility ##
# Name: targets
# Purpose: populates the target information section of the page
# Parameters: proposal object
#             data that can be edited
#             top level frame of page
# Returns: nothing
sub targets {
  my ( $w, $obj, $data, $top ) = @_;

  # If there are targets put the target information in a scrollable table

  my $frame = $top->Frame();
  if ($obj->num_targets() > 0) {
    # Raster targets have extra fields, which only get shown if there is one
    # in the proposal
    my $raster = $obj->dbh->prepare(qq(select count(*) from target where
                                       raster_scan = 'Y' and 
                                       panel_id = ? and prop_id = ?));
    $raster->execute($obj->panel_id, $obj->prop_id);
    my ($num_raster) = $raster->fetchrow_array;
    $raster->finish;


    # Too's must be broken up into separate components, so they are displayed
    # in a separate type of table
    my $table;
    if ($obj->type !~ /TOO/) {
      # Are there non-monitor targets in the proposal?
      if ($obj->num_targets > $obj->num_monitor) {
	my $target_label = $frame->Label(-text => "Target Information",
					 -font => "Helvetica 14"
					)->pack(-side => 'top');
	if ($obj->num_targets() > 10) {
	  $table = $frame->Table(-rows => 6,
				 -columns => 18,
				 -fixedrows => 2,
				 -scrollbars => 'w',
				 -takefocus => 0,
				)->pack(-side => 'top');
	}
	else {
	  $table = $frame->Table(-rows => 100,
				 -columns => 18,
				 -scrollbars => '',
				 -takefocus => 0,
				)->pack(-side => 'top');
	}

	my $col = 1;
	$table->put(1,$col++,'AO');
	$table->put(1,$col++,"#");
	$table->put(1,$col++,"Name");
	$table->put(1,$col++,"Instr");
	$table->put(1, $col++, "Grating");
	$table->put(1,$col++,"Req_Time");
	$table->put(1,$col++,"App_Time") if ($obj->prop_status !~ /$STAT_BPP/);
    $table->put(1,$col++,"HEL");
	$table->put(1,$col++,"Tax");
	$table->put(1,$col++,"RC");
	$table->put(1,$col++,"Time_Crit");
	$table->put(1,$col++,"Status");
	$table->put(1,$col++,"Rev_TOO");
	$table->put(1,$col++,"Grid");

	# There are additional columns if this is a raster
	if ($num_raster) {
	  $table->put(1,$col++,"NumObsReq");
	  $table->put(1,$col++,"NumObsApp") if ($obj->prop_status !~ /$STAT_BPP/);
	  $table->put(1,$col++,"TimeObsReq");
	}

	my $trow = 1;

        my $maxDisplay=$obj->num_targets;
        if ($maxDisplay > $MAX_TGTS) {$maxDisplay=$MAX_TGTS;}
	for (my $i = 1; $i <= $maxDisplay; $i++) {
	  next if $obj->target($i)->monitor =~ /Y|P/;

	  $col = 1;
	  $trow++;
	  $table->put($trow,$col++, $AO);
	  $table->put($trow,$col++, $obj->target($i)->targ_num());
	  $table->put($trow,$col++, $obj->target($i)->targ_name());
	  $table->put($trow,$col++, $obj->target($i)->detector());
	  $table->put($trow,$col++, $obj->target($i)->grating());
	  $table->put($trow,$col++, $obj->target($i)->req_time());

	  # approved_time is not editable for LPs if prop_status is B
	  if ($obj->prop_status !~ /$STAT_BPP/) {
	    # approved time is calculated for raster targets
	    if ( $obj->target($i)->raster_scan eq 'Y') {
	      if ($obj->triage !~ /Y|A/) {
		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{app_time},
					 ));
	      }
	      else {
		$table->put($trow, $col++, $obj->target($i)->app_time());
	      }
	    }
	    else {
	      my (%app_time) = init_field('app_time', 'targets', $i,
					  'real');
	      # the minimum approved_time is set to .001 so it can't be set
	      # to zero as a way of turning off a target
	      $table->put( $trow, $col++,
			   myEntry($table, $obj,
				   -textvariable =>
				   \$data->{targets}[$i]{app_time},
				   -width => 5,
				   -validatecommand => [ \&validate,
							 real =>
							 ["Approved time for target $i",
							  .001,
							  $data->{targets}[$i]{req_time}
							 ],
							 [$w,
							  \%app_time,
							  $data->{targets}[$i]{app_time},
							  $obj]]
				  )
			 );
	    }

	  }
	  $table->put($trow,$col++, $obj->target($i)->at_hel);
	  $table->put($trow,$col++,
		      $table->Label(-textvariable =>
				    $obj->target($i)->get_ref('tax'),
				   ));

	  $table->put($trow,$col++,
		      $table->Label(-textvariable =>
				    $obj->target($i)->get_ref('rc_score'),
				   ));

	  $table->put($trow,$col++,
		      $table->Label(-textvariable =>
				    \$data->{targets}[$i]{time_crit}));

	  # targ_status is not editable if prop_status is P
	  if ($obj->prop_status !~ /$STAT_BPP/ ) {
	    my (%status) = init_field('targ_status', 'targets', $i);
	    $table->put($trow, $col++,
			mkTargRadio($table,
				    $data,
				    \%status, $obj, $w)
		       );

	  }
	  else {
	    $table->put($trow, $col++,
			$obj->target($i)->targ_status());
	  }

	  $table->put($trow,$col++,
		      $obj->target($i)->rev_too());
	  $table->put($trow,$col++,
		      $obj->target($i)->raster_scan());

	  # This stuff only applies to raster observations
	  if ($obj->target($i)->raster_scan eq 'Y') {
	    $table->put($trow,$col++,$obj->target($i)->num_obs_req());

	    # num_obs_app is not editable for LPs with status P
	    if ($obj->prop_status !~ /$STAT_BPP/ ) {
	      my (%num_obs_app) = init_field('num_obs_app', 'targets', $i,
					     'integer');
	      $table->put( $trow, $col++,
			   myEntry($table, $obj,
				   -textvariable =>
				   \$data->{targets}[$i]{num_obs_app},
				   -width => 5,
				   -validatecommand => [ \&validate,
							 int =>
							 ["Number of observations for target $i",
							  1,
							  $data->{targets}[$i]{num_obs_req}
							 ],
							 [$w,
							  \%num_obs_app,
							  $data->{targets}[$i]{num_obs_app},
							  $obj]]
				  )
			 );
	    }

	    $table->put($trow,$col++,$obj->target($i)->time_obs_req()) if
	      $obj->target($i)->raster_scan eq 'Y';
	  }

	  # Add AO+1 if multicycle targets in AO+1
	  if ($obj->target($i)->req_time_1) {
	    $col = 1;
	    $trow++;
	    $table->put($trow, $col++, $AO + 1);
	    $col += 4;
	    $table->put($trow,$col++, $obj->target($i)->req_time_1());

	    # approved_time is not editable for LPs if prop_status is P
	    if ($obj->prop_status !~ /$STAT_BPP/) {
	      # approved time is calculated for raster targets
	      if ($obj->target($i)->raster_scan eq 'Y') {
		if ($obj->triage !~ /Y|A/) {
		  $table->put($trow,$col++,
			      $table->Label(-textvariable =>
					    \$data->{targets}[$i]{app_time_1},
					   ));
		}
		else {
		  $table->put($trow, $col++, $obj->target($i)->app_time_1());
		}
	      }
	      else {
		my (%app_time_1) = init_field('app_time_1', 'targets', $i,
					      'real');
		# the minimum approved_time is set to .001 so it can't be set
		# to zero as a way of turning off a target
		$table->put( $trow, $col++,
			     myEntry($table, $obj,
				     -textvariable =>
				     \$data->{targets}[$i]{app_time_1},
				     -width => 5,
				     -validatecommand => [ \&validate,
							   real =>
							   ["Approved time for target $i ($AO+1)",
							    .001,
							    $data->{targets}[$i]{req_time_1}
							   ],
							   [$w,
							    \%app_time_1,
							    $data->{targets}[$i]{app_time_1},
							    $obj]]
				    )
			   );
	      }

	    }
        $table->put($trow,$col++, $obj->target($i)->at_hel);

	    $table->put($trow,$col++,
			$table->Label(-textvariable =>
				      $obj->target($i)->get_ref('tax_1'),
				     ));

	    $table->put($trow,$col++,
			$table->Label(-textvariable =>
				      $obj->target($i)->get_ref('rc_score_1'),
				     ));

	    $col++;

	    # targ_status is not editable if prop_status is P
	    if ($obj->prop_status !~ /$STAT_BPP/ ) {
	      my (%status_1) = init_field('targ_status_1', 'targets', $i);
	      $table->put($trow, $col++,
			  mkTargRadio($table,
				      $data,
				      \%status_1, $obj, $w)
			 );

	    }
	    else {
	      $table->put($trow, $col++,
			  $obj->target($i)->targ_status_1());
	    }

	    $col += 2;

	    # This stuff only applies to raster observations
	    if ($obj->target($i)->raster_scan eq 'Y') {
	      $table->put($trow,$col++,$obj->target($i)->num_obs_req_1());

	      # num_obs_app is not editable for LPs with status P
	      if ($obj->prop_status !~ /$STAT_BPP/ ) {
		my (%num_obs_app_1) = init_field('num_obs_app_1', 'targets', $i,
						 'integer');
		$table->put( $trow, $col++,
			     myEntry($table, $obj,
				     -textvariable =>
				     \$data->{targets}[$i]{num_obs_app_1},
				     -width => 5,
				     -validatecommand => [ \&validate,
							   int =>
							   ["Number of observations for target $i ($AO+1)",
							    1,
							    $data->{targets}[$i]{num_obs_req_1}
							   ],
							   [$w,
							    \%num_obs_app_1,
							    $data->{targets}[$i]{num_obs_app_1},
							    $obj]]
				    )
			   );
	      }

	      $table->put($trow,$col++,$obj->target($i)->time_obs_req_1()) if
		$obj->target($i)->raster_scan eq 'Y';
	    }
	  }
	  # AO+2
	  if ($obj->target($i)->req_time_2) {
	    $col = 1;
	    $trow++;
	    $table->put($trow,$col++, $AO + 2);
	    $col += 4;
	    $table->put($trow,$col++, $obj->target($i)->req_time_2());

	    # approved_time is not editable for LPs if prop_status is P
	    if ($obj->prop_status !~ /$STAT_BPP/) {
	      # approved time is calculated for raster targets
	      if ($obj->target($i)->raster_scan eq 'Y') {
		if ($obj->triage !~ /Y|A/) {
		  $table->put($trow,$col++,
			      $table->Label(-textvariable =>
					    \$data->{targets}[$i]{app_time_2},
					   ));
		}
		else {
		  $table->put($trow, $col++, $obj->target($i)->app_time_2());
		}
	      }
	      else {
		my (%app_time_2) = init_field('app_time_2', 'targets', $i,
					      'real');
		# the minimum approved_time is set to .001 so it can't be set
		# to zero as a way of turning off a target
		$table->put( $trow, $col++,
			     myEntry($table, $obj,
				     -textvariable =>
				     \$data->{targets}[$i]{app_time_2},
				     -width => 5,
				     -validatecommand => [ \&validate,
							   real =>
							   ["Approved time for target $i ($AO+2)",
							    .001,
							    $data->{targets}[$i]{req_time_2}
							   ],
							   [$w,
							    \%app_time_2,
							    $data->{targets}[$i]{app_time_2},
							    $obj]]
				    )
			   );
	      }

	    }
        $table->put($trow,$col++, $obj->target($i)->at_hel);
	    $table->put($trow,$col++,
			$table->Label(-textvariable =>
				      $obj->target($i)->get_ref('tax_2'),
				     ));

	    $table->put($trow,$col++,
			$table->Label(-textvariable =>
				      $obj->target($i)->get_ref('rc_score_2'),
				     ));

	    $col++;

	    # targ_status is not editable if prop_status is P
	    if ($obj->prop_status !~ /$STAT_BPP/ ) {
	      my (%status_2) = init_field('targ_status_2', 'targets', $i);
	      $table->put($trow, $col++,
			  mkTargRadio($table,
				      $data,
				      \%status_2, $obj, $w)
			 );

	    }
	    else {
	      $table->put($trow, $col++,
			  $obj->target($i)->targ_status_2());
	    }

	    $col += 2;

	    # This stuff only applies to raster observations
	    if ($obj->target($i)->raster_scan eq 'Y') {
	      $table->put($trow,$col++,$obj->target($i)->num_obs_req_2());

	      # num_obs_app is not editable for LPs with status P
	      if ($obj->prop_status !~ /$STAT_BPP/ ) {
		my (%num_obs_app_2) = init_field('num_obs_app_2', 'targets', $i,
						 'integer');
		$table->put( $trow, $col++,
			     myEntry($table, $obj,
				     -textvariable =>
				     \$data->{targets}[$i]{num_obs_app_2},
				     -width => 5,
				     -validatecommand => [ \&validate,
							   int =>
							   ["Number of observations for target $i ($AO+2)",
							    1,
							    $data->{targets}[$i]{num_obs_req_2}
							   ],
							   [$w,
							    \%num_obs_app_2,
							    $data->{targets}[$i]{num_obs_app_2},
							    $obj]]
				    )
			   );
	      }

	      $table->put($trow,$col++,$obj->target($i)->time_obs_req_2()) if
		$obj->target($i)->raster_scan eq 'Y';
	    }
	  }
	}
      }
      if ($obj->num_monitor) {
	# This is where the monitors start
	my $monitor_fr = $frame->Frame()->pack();
	my $text = "Target Information for Monitoring Targets";
	my $mon_label = $monitor_fr->Label(-text => $text,
					   -font => "Helvetica 14"
					  )->pack(-side => 'top');

	my $get_tot_seg = $obj->dbh->prepare(qq(select sum(num_obs_req +
                                                num_obs_req_1 + num_obs_req_2)
					      from target where prop_id = ?
					      and panel_id = ? and 
                                              monitor in ('Y','P')));
	$get_tot_seg->execute($obj->prop_id(), $obj->panel_id());
	my ($tot_seg) = $get_tot_seg->fetchrow_array;
	$get_tot_seg->finish;
	if ($tot_seg > 5) {
	  $table = $monitor_fr->Table(-rows => 10,
				 -columns => 22,
				 -fixedrows => 2,
				 -scrollbars => 'w',
				)->pack(-side => 'top');
	}
	else {
	  $table = $monitor_fr->Table(-rows => 14,
				 -columns => 22,
				 -scrollbars => '',
				)->pack(-side => 'top');
	}

	my $col = 1;
	$table->put(1, $col++, "AO");
	$table->put(1,$col++,"#");
	$table->put(1,$col++,"Name");
	$table->put(1,$col++,"Rev_TOO");
	$table->put(1,$col++,"Instr");
	$table->put(1, $col++, "Grating");
	$table->put(1,$col++,"Req_Time");
	$table->put(1,$col++,"App_Time") if ($obj->prop_status !~ /$STAT_BPP/);
  	$table->put(1,$col++,"HEL");
  	$table->put(1,$col++,"Tax");
	$table->put(1,$col++,"RC");
	$table->put(1,$col++,"Time_Crit");
	$table->put(1,$col++,"Status");

	my $trow = 1;
	my $cur_ao = -1;
	for (my $i = 1; $i <= $obj->num_targets; $i++) {
	  next if $obj->target($i)->monitor eq 'N';
	  my $reps = $obj->target($i)->num_obs_req() +
	    $obj->target($i)->num_obs_req_1() +
	      $obj->target($i)->num_obs_req_2();
	  for (my $j = 1; $j <= $reps; $j++) {
	    $col = 1;
	    $trow++;

	    if ($obj->target($i)->too($j)->ao != $cur_ao) {
	      $cur_ao = $obj->target($i)->too($j)->ao;
	      $col++;
	      $col++;
	      $table->put($trow,$col++, $obj->target($i)->targ_name());
	      $table->put($trow,$col++, $obj->target($i)->rev_too());
	      $table->put($trow,$col++, $obj->target($i)->detector());
	      $table->put($trow,$col++, $obj->target($i)->grating());
              if ($obj->prop_status !~ /$STAT_BPP/)  {
                $col +=3;
              } else {
                $col +=2;
              }
	      if ($cur_ao == 0) {
		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{tax},
					 ));

		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{rc_score},
				       ));
	      }
	      elsif ($cur_ao == 1) {
		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{tax_1},
					 ));

		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{rc_score_1},
				       ));
	      }
	      elsif ($cur_ao == 2) {
		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{tax_2},
					 ));

		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{rc_score_2},
				       ));
	      }
	      $table->put($trow,$col++, $obj->target($i)->time_crit());
	      $trow++;
	    }

	    $col = 1;
	    my $ao = $AO + $obj->target($i)->too($j)->ao();
	    $table->put($trow,$col++, $ao);
	    my $number = $obj->target($i)->targ_num() . '-' .
	      $obj->target($i)->too($j)->ordr;
	    $table->put($trow,$col++,$number);
	    $col += 4;
	    $table->put($trow,$col++,
			$obj->target($i)->too($j)->req_obs_time());

	    # approved_time is not editable for LPs
	    if ($obj->prop_status() !~ /$STAT_BPP/) {
	      my (%obs_time) = init_field('app_obs_time',
					  "targets\ttoos",
					  "$i\t$j", 'real');
	      # the minimum approved_time is set to .001 so it can't be set
	      # to zero as a way of turning off a target
	      $table->put( $trow, $col++,
			   myEntry($table, $obj,
				   -textvariable =>
				   \$data->{targets}[$i]{toos}[$j]{app_obs_time},
				   -width => 5,
				   -validatecommand =>
				   [\&validate,
				    real =>
				    ["Approved time for target $i, pointing $j",
				     .001,
				     $data->{targets}[$i]{toos}[$j]{req_obs_time}
				    ],
				    [$w, \%obs_time,
				     $data->{targets}[$i]{toos}[$j]{app_obs_time},
				     $obj]]
				  )
			 );
	    }
        $table->put($trow,$col++,
            $table->Label(-textvariable =>
                \$data->{targets}[$i]{at_hel},
            ));
	    $col +=3;

	    if ($obj->target($i)->targ_status() !~ /$STAT_BPP/ ) {
	      my @choices = ('Y', 'N');
	      my (%status) = init_field('obs_status',
					"targets\ttoos",
					"$i\t$j");
	      $table->put($trow,$col++,
			  mkTooRadio($table,
				     $data,
				     \%status,
				     $obj, $w));
	    }
	    else {
	      $table->put($trow, $col++,
			  $obj->target($i)->too($j)->obs_status());
	    }
	  }
	$cur_ao = -1;
	}
      }
    }
    else {
      # This is where TOOs start
      #================================================================
      # First display targets that are not in an alternate target group
      #================================================================

	my $target_label = $frame->Label(-text => "Target Information for TOOs",
					 -font => "Helvetica 14"
					)->pack(-side => 'top');

      # Find out how many total pointings there are for the proposal
      # to determine if there should be scrollbars on the table
      # Only take targets that are not part of alternate target groups

      my $get_tot_pnt = $obj->dbh->prepare(qq(select sum(num_obs_req)
					      from target where prop_id = ?
					      and panel_id = ? and 
                                              alt_id is null));
      $get_tot_pnt->execute($obj->prop_id(), $obj->panel_id());
      my ($tot_pnt) = $get_tot_pnt->fetchrow_array;
      $get_tot_pnt->finish;


      if ($tot_pnt) {
	if ($tot_pnt > 5) {
	  $table = $frame->Table(-rows => 10,
				 -columns => 22,
				 -fixedrows => 2,
				 -scrollbars => 'w',
				)->pack(-side => 'top');
	}
	else {
	  $table = $frame->Table(-rows => 14,
				 -columns => 22,
				 -scrollbars => '',
				)->pack(-side => 'top');
	}

	my $col = 1;
	$table->put(1,$col++,"AO");
	$table->put(1,$col++,"#");
	$table->put(1,$col++,"TF");
	$table->put(1,$col++,"Name");
	$table->put(1,$col++,"Instr");
	$table->put(1,$col++,"Grating");
	$table->put(1,$col++, "Req_Time");
	$table->put(1,$col++,"App_Time") if ($obj->prop_status !~ /$STAT_BPP/);
    $table->put(1,$col++, "HEL");
  	$table->put(1,$col++,"Tax");
	$table->put(1,$col++,"Time_Crit");
	$table->put(1,$col++,"Resp_Time") ;
	$table->put(1,$col++,"Status");
	$table->put(1,$col++,"TOO_Prob");

	my $trow = 1;

	for (my $i = 1; $i <= $obj->num_targets; $i++) {
          #skip target if part of alternate group for now
	  next if defined $obj->target($i)->alt_id;
	  my $reps = $obj->target($i)->num_obs_req() +
	    $obj->target($i)->num_obs_req_1() +
	      $obj->target($i)->num_obs_req_2();
	  my $cur_ao =  -1;
	  for (my $j = 1; $j <= $reps; $j++) {
	    $trow++;

	    if ($obj->target($i)->too($j)->ao != $cur_ao) {
	      $cur_ao = $obj->target($i)->too($j)->ao;
	      $col = 4;
	      $table->put($trow,$col++, $obj->target($i)->targ_name());
	      $table->put($trow,$col++, $obj->target($i)->detector());
	      $table->put($trow,$col++, $obj->target($i)->grating());
              # skip over req/app time, HEL columns
              $col++;
	      $col++ if ($obj->prop_status() !~ /$STAT_BPP/);
		  $col++;
	      if ($cur_ao == 0) {
		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{tax},
					 ));

	      }
	      elsif ($cur_ao == 1) {
		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{tax_1},
					 ));

	      }
	      elsif ($cur_ao == 2) {
		$table->put($trow,$col++,
			    $table->Label(-textvariable =>
					  \$data->{targets}[$i]{tax_2},
					 ));

	      }
	      $table->put($trow,$col++, $obj->target($i)->time_crit());
	      $table->put($trow,$col++, $obj->target($i)->response_time());
	      $col++; #skip status column
	      # too_prob is not editable for LPs
	      if ($obj->prop_status !~ /$STAT_BPP/ and $cur_ao == 0) {
		my (%too_prob) = init_field('too_prob_app',
					    "targets",
					    "$i", 'real');
		my $var = $data->{targets}[$i]{too_prob_app};
		$table->put( $trow, $col++,
			     myEntry($table, $obj,
				     -textvariable =>
				     \$data->{targets}[$i]{too_prob_app},
				     -width => 5,
				     -validatecommand =>
				     [\&validate,
				      real =>
				      ["Too probability for target $i",
				       0.1, 1],
				      [$w, \%too_prob,
				       $data->{targets}[$i]{too_prob_app},
				       $obj]]
				    )
			   );
	      } elsif ($obj->prop_status =~ /$STAT_BPP/ and $cur_ao == 0) {
	        $table->put($trow, $col++, $data->{targets}[$i]{too_prob_app});
              }
	      $trow++;
	    }

	    $col = 1;

	    my $ao = $AO + $obj->target($i)->too($j)->ao();
	    $table->put($trow,$col++, $ao);
	    my $number = $obj->target($i)->targ_num() . '-' .
	      $obj->target($i)->too($j)->ordr;
	    $table->put($trow,$col++,$number);

	    $table->put($trow,$col++, $obj->target($i)->too($j)->trigflag);

            # skip name,instr,grating
	    $col += 3;
	    $table->put($trow,$col++,
			$obj->target($i)->too($j)->req_obs_time());

	    # approved_time is not editable for LPs
	    if ($obj->prop_status() !~ /$STAT_BPP/) {
	      my (%obs_time) = init_field('app_obs_time',
					  "targets\ttoos",
					  "$i\t$j", 'real');

	      # the minimum approved_time is set to .001 so it can't be set
	      # to zero as a way of turning off a target
	      $table->put( $trow, $col++,
			   myEntry($table, $obj,
				   -textvariable =>
				   \$data->{targets}[$i]{toos}[$j]{app_obs_time},
				   -width => 5,
				   -validatecommand =>
				   [\&validate,
				    real =>
				    ["Approved time for target $i, pointing $j",
				     .001,
				     $data->{targets}[$i]{toos}[$j]{req_obs_time}
				    ],
				    [$w, \%obs_time,
				     $data->{targets}[$i]{toos}[$j]{app_obs_time},
				     $obj]]
				  )
			 );

	    }
		  $table->put($trow,$col++,
              $obj->target($i)->at_hel);
            # skip tax,time_crit,response
	    $col +=3;
	    if ($obj->target($i)->targ_status() !~ /$STAT_BPP/ ) {
	      my @choices = ('Y', 'N');
	      my (%status) = init_field('obs_status',
					"targets\ttoos",
					"$i\t$j");
	      $table->put($trow,$col++,
			  mkTooRadio($table,
				     $data,
				     \%status,
				     $obj, $w));
	    }
	    else {
	      $table->put($trow, $col++,
			  $obj->target($i)->too($j)->obs_status());
	    }

            # too prob column
	    $col++;

	  }
	}
	# horrible hack.  must force creation of scrollbars so we can turn
	# off focus to them
	$table->Layout;
	defined $table->{$_} && $table->{$_}->configure( -takefocus => 0 )
	  foreach (qw/ xsb ysb /);
      }


      #=============================================================
      # Second display targets that are in an alternate target group
      #=============================================================
      my $alt_grp = $obj->dbh->prepare(qq(select distinct a.alt_id,
                                          alt_grp_name from 
                                          alternate_target_group a, 
                                          target t where a.alt_id = t.alt_id 
                                          and a.prop_id = ? and a.panel_id = ?
                                          order by a.alt_id));
     $alt_grp->execute($obj->prop_id, $obj->panel_id);
     while (my($alt_id, $alt_grp_name) = $alt_grp->fetchrow_array) {
       print "Need new display code for alternate targets in MCOP\n" if $obj->mcop eq 'Y';
       die if $obj->mcop eq 'Y';
       my $alt_grp_fr = $frame->Frame()->pack();
       my $text = "Target Information for Alternate Target Group";
       my $alt_label = $alt_grp_fr->Label(-text => $text,
					  -font => "Helvetica 14"
					 )->pack(-side => 'top');

       my $alt_fr = $alt_grp_fr->Frame()->pack(-expand => 1,
					       -fill => 'both');
       $text = "Alternate Group Name: " .
	 $obj->alt_grp($alt_id)->get('alt_grp_name');
       $alt_fr->Label(-text => $text
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');
       $text = 'Num Targs Req: ' . $obj->alt_grp($alt_id)->get('req_cnt');
       if ($obj->prop_status !~ /$STAT_BPP/) {
         $alt_fr->Label(-text => $text
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');
         $alt_fr->Label(-text => 'Num Targs App:'
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');

         my (%app_cnt) = init_field('app_cnt',
				  "alt_grps",
				  "$alt_id", 'integer');
         myEntry($alt_fr, $obj,
	       -textvariable =>
	       \$data->{alt_grps}{$alt_id}{app_cnt},
	       -width => 5,
	       -validatecommand =>
	       [\&validate,
		integer =>
		["Number of approved observations for group",
		 0,
		 $data->{alt_grps}{$alt_id}{req_cnt}
		],
		[$w, \%app_cnt,
		 $data->{alt_grps}{$alt_id}{app_cnt},
		 $obj]]
	      )->pack(-side => 'left');
       }

       # Too's must be broken up into separate components
       my $alttable;

       # Find out how many total pointings there are for the proposal
       # to determine if there should be scrollbars on the table
       # Only take targets that are not part of alternate target groups
       my $get_tot_pnt = $obj->dbh->prepare(qq(select sum(num_obs_req)
					        from target where prop_id = ?
					        and panel_id = ? and 
                                                alt_id = ?));
       $get_tot_pnt->execute($obj->prop_id(), $obj->panel_id(), $alt_id);
       my ($tot_pnt) = $get_tot_pnt->fetchrow_array;
       $get_tot_pnt->finish;

       if ($tot_pnt > 5) {
	 $alttable = $alt_grp_fr->Table(-rows => 6,
					-columns => 22,
					-fixedrows => 2,
					-scrollbars => 'w',
				       )->pack(-side => 'top');
       }
       else {
	 $alttable = $alt_grp_fr->Table(-rows => 6,
					-columns => 22,
					-scrollbars => '',
				       )->pack(-side => 'top');
       }

       my $col = 1;
       $alttable->put(1,$col++,"#");
       $alttable->put(1,$col++,"TF");
       $alttable->put(1,$col++,"Name");
       $alttable->put(1,$col++,"Instr");
       $alttable->put(1,$col++,"Grating");
       $alttable->put(1,$col++, "Req_Time");
       $alttable->put(1,$col++,"App_Time") if ($obj->prop_status() !~ /$STAT_BPP/);
	   $alttable->put(1,$col++, "HEL");
       $alttable->put(1,$col++,"Tax") if ($obj->prop_status !~ /$STAT_BPP/);
       $alttable->put(1,$col++,"Time_Crit");
       $alttable->put(1,$col++,"Resp_Time") ;
       $alttable->put(1,$col++,"Status");
       $alttable->put(1,$col++,"TOO_Prob");

       my $trow = 1;

       for (my $i = 1; $i <= $obj->num_targets; $i++) {
	 next if (!defined $obj->target($i)->alt_id or
		  $obj->target($i)->alt_id != $alt_id);
	 my $reps = $obj->target($i)->num_obs_req() + $obj->target($i)->num_obs_req_1() + $obj->target($i)->num_obs_req_2();
	 for (my $j = 1; $j <= $reps; $j++) {
	   $col = 1;
	   $trow++;

	   my $number = $obj->target($i)->targ_num() . '-' .
	     $obj->target($i)->too($j)->ordr;
	   $alttable->put($trow,$col++,$number);

	   $alttable->put($trow,$col++, $obj->target($i)->too($j)->trigflag);

	   if ($j == 1) {
	     $alttable->put($trow,$col++, $obj->target($i)->targ_name());
	     $alttable->put($trow,$col++, $obj->target($i)->detector());
	     $alttable->put($trow,$col++, $obj->target($i)->grating());
	   }
	   else { $col += 3;}

	   $alttable->put($trow,$col++,
			  $obj->target($i)->too($j)->req_obs_time());

	   # approved_time is not editable for LPs
	   if ($obj->prop_status() !~ /$STAT_BPP/) {
	     my (%obs_time) = init_field('app_obs_time',
					 "targets\ttoos",
					 "$i\t$j", 'real');

	     # the minimum approved_time is set to .001 so it can't be set
	     # to zero as a way of turning off a target
	     $alttable->put( $trow, $col++,
			     myEntry($alttable, $obj,
				     -textvariable =>
				     \$data->{targets}[$i]{toos}[$j]{app_obs_time},
				     -width => 5,
				     -validatecommand =>
				     [\&validate,
				      real =>
				      ["Approved time for target $i, pointing $j",
				       .001,
				       $data->{targets}[$i]{toos}[$j]{req_obs_time}
				      ],
				      [$w, \%obs_time,
				       $data->{targets}[$i]{toos}[$j]{app_obs_time},
				       $obj]]
				    )
			   );
	     if ($j == 1) {
		 $alttable->put($trow,$col++,
			 $obj->target($i)->at_hel);
	     $alttable->put($trow,$col++,
			    $alttable->Label(-textvariable =>
					     \$data->{targets}[$i]{tax},
					    ));
	    }
	    else {$col += 1;}
	   }

	   if ($j == 1) {
	     $alttable->put($trow,$col++,
			    $alttable->Label(-textvariable => \$data->{targets}[$i]{time_crit}));
	     $alttable->put($trow,$col++,$obj->target($i)->response_time());
	   }
	   else {$col += 2;}

	   if ($obj->target($i)->targ_status() !~ /$STAT_BPP/ ) {
	     my @choices = ('Y', 'N');
	     my (%status) = init_field('obs_status',
				       "targets\ttoos",
				       "$i\t$j");
	     $alttable->put($trow,$col++,
			    mkTooRadio($alttable,
				       $data,
				       \%status,
				       $obj, $w));
	   }
	   else {
	     $alttable->put($trow, $col++,
			    $obj->target($i)->too($j)->obs_status());
	   }

	   # too_prob is not editable for LPs
           if ($j == 1) {
	     my (%too_prob) = init_field('too_prob_app',
					 "targets",
					 "$i", 'real');
	     my $var = $data->{targets}[$i]{too_prob_app};
	     if ($obj->prop_status !~ /$STAT_BPP/ ) {
	       $alttable->put( $trow, $col++,
			     myEntry($alttable, $obj,
				     -textvariable =>
				     \$data->{targets}[$i]{too_prob_app},
				     -width => 5,
				     -validatecommand =>
				     [\&validate,
				      real =>
				      ["Too probability for target $i",
				       0.1, 1],
				      [$w, \%too_prob,
				       $data->{targets}[$i]{too_prob_app},
				       $obj]]
				    )
			   );
	     }
             else {
	        $alttable->put($trow, $col++, $var);
             }
           }
	 }
       }
       # horrible hack.  must force creation of scrollbars so we can turn
       # off focus to them
       $alttable->Layout;
       defined $table->{$_} && $alttable->{$_}->configure( -takefocus => 0 )
       foreach (qw/ xsb ysb /);
     }
     $alt_grp->finish;
    }
  }
  $frame;
}

## Internal Utility ##
# Name: display_monitor
# Purpose: populates the target information section of the page for a too or
#          monitor
# Parameters: proposal object
#             data that can be edited
#             top level frame of page
# Returns: nothing
sub display_monitor {
}

## Internal Utility ##
# Name: abstract
# Purpose: populates the abstract section of the page
# Parameters: proposal object
#             top level frame of page
# Returns: nothing
sub abstract {
  my ( $w, $obj, $top ) = @_;

  my $frame = $top->Frame();
  my $label = $frame->Label(-text => "Abstract",
			    -font => "Helvetica 14"
			   )->pack(-side => 'top');

  my $abs = $frame->Scrolled("ROText",
			     -takefocus => 0,
			     -wrap => "word",
			     -width => 40,
			     -height => 15,
			     -scrollbars => "w",
			    )->pack(-side => 'left');
  $abs->insert('0.0', $obj->title());
  $abs->insert('end', "\n\n");
  $abs->insert('end', $obj->abstract());

  $frame;
}

## Internal Utility ##
# Name: comments
# Purpose: populates the comments section of the page
# Parameters: proposal object
#             the top level of the page
# Returns: nothing
sub comments {
  my ( $w, $obj, $data, $top ) = @_;

  my $frame = $top->Frame;

  my $label = $frame->Label(-text => "Comments",
			    -font => "Helvetica 14"
			   )->pack(-side => 'top');

  my %cmts = init_field('comments', 'proposal');
  my $comments = $frame->Scrolled("Text",
				  -takefocus => 0,
				  -wrap => "word",
				  -width => 60,
				  -height => 10,
				  -scrollbars => "w",
				 )->pack(-side => 'top');
  $comments->insert('0.0', $data->{comments});
  $comments->insert('end', "\n\n");
  #$frame->Button(-text => 'Edit Comments',
		 #-state => $lock_state,
		 #-command => sub {
		   #editComments($frame, $obj);
		   #$comments->delete('1.0', 'end');
		   #$comments->insert('0.0', $obj->comments());
		   #$comments->insert('end', "\n\n");
		 #}
		#)->pack();
  $frame,$comments;
}
## Internal Utility ##
# Name: bpp_stddev
# Purpose: populates the grades section of the page
# Parameters: proposal object
#             the top level of the page
# Returns: nothing
sub bpp_stddev {

  my ( $w, $obj, $top ) = @_;

  my $bppstr="";
  if ($obj->panel_id == 99) {
    my($xprop) = $obj->prop_id;
    my $xpg_query = $obj->dbh->prepare(qq(select panel_id,fg_avg,fg_norm,fg_stdev
			from bpp_panel_grades where prop_id = $xprop
			and panel_id != 99));
    $xpg_query->execute();
    while (my($xpid, $xavg,$xnorm,$xstdev) = $xpg_query->fetchrow_array) {
        if (!$xstdev) { $xstdev = "0"; }
        if ($xpid == 98) {
          $bppstr .=  "Panel XVP: $xavg/$xstdev   ";
        } else {
          $bppstr .=  "Panel $xpid: $xnorm/$xstdev   ";
        }
    }
  }
  my $frame = $top->Frame();
  $frame->Label(-text => "BPP Preliminary Grades and StdDev: "
	       )->pack(-side => 'left');
  $frame->Label(-text => $bppstr
	       )->pack(-side => 'left');

  return $frame;
}
## Internal Utility ##
# Name: grade_cmt
# Purpose: populates the grade comment field
# Parameters: proposal object
#             the top level of the page
# Returns: nothing
sub grade_cmt {

  my ( $w, $obj, $data, $top ) = @_;
  my %g_cmt = init_field('g_cmt', 'proposal');

  my $grstr="";
  my $frame = $top->Frame();
  $frame->LabEntry(-label => "Grade Comment: ",
	-textvariable => \$data->{g_cmt},
	-width => 60,
        -validate => 'focusout',
        -validatecommand =>  sub {
		  track_changes($w,
			\%g_cmt,
			$data->{g_cmt}, $obj);
		  return 1;
		},
	-labelPack => [-side => 'left'],
	       )->pack();
  return $frame;
}

## Internal Utility ##
# Name: grades
# Purpose: populates the grades section of the page
# Parameters: proposal object
#             the editable data
#             the top level of the page
# Returns: nothing
sub grades {

  my ( $w, $obj, $data, $top ) = @_;

  # These are the grades
  my $label = $top->Label(-text => "Grades",
					-font => "Helvetica 14"
					)->pack(-side => 'top');
  if (defined $obj->rank && $obj->rank > 0) {
     my $label = $top->Label(-text => "Grades should not be edited once the Rank has been set!",
					-foreground => "red"
					)->pack(-side => 'top');
  }

  my $frame = $top->Frame();

  $frame->Label(-text => ''
	       )->grid(-row => 0,
		       -column => 0,
		       -sticky => 'nsew'
		      );

  $frame->Label(-text => 'Prelim Grades',
		-anchor => 'e')->grid(-row => 1,
				      -column => 0,
				      -sticky => 'nsew');
  $frame->Label(-text => 'Final Grades',
		-anchor => 'e')->grid(-row => 2,
				      -column => 0,
				      -sticky => 'nsew');
  my $col = 0;
  my @revs;
  # this is for use by CDO which wants to tracks statistics
  # display the name when hovering over grade to help facilitator
  # enter grades by reviewer in same column each time
  my $sth = $obj->dbh->prepare(qq(select last_name from panel_member
	where panel_id = ? order by lower(last_name)));
  $sth->execute($obj->panel_id);
  push(@revs,"");
  while (my($rname) = $sth->fetchrow_array) {
    push(@revs,$rname)
  }
  for (my $i = 1; $i <= $num_grades; $i++) {
      $col = $i;
      my (%final_grades) = init_field('final_grade', 'proposal', $i, 'real');
      my ($lx) = $frame->Label(-text => $i)->grid(-row => 0,
				       -column => $i,
				       -sticky => 'nsew');
      my $revname = $revs[$i];
      my $ba = $top->Balloon(-background=>'LemonChiffon');
      $ba->attach($lx,-initwait=>0,-balloonmsg=>$revname);


      if ($obj->panel_id != 99) {
	  $frame->Label(-text => $obj->prelim_grade($i),
			-anchor => 'e',
			-borderwidth => 2,
			-relief => 'groove')->grid(-row => 1,
						   -column => $i,
						   -sticky => 'nsew');
      }
      else {
	  if ($i < 5) {
	      $frame->Label(-text => $obj->prelim_grade($i),
			    -anchor => 'e',
			    -borderwidth => 2,
			    -relief => 'groove')->grid(-row => 1,
						       -column => $i,
						       -sticky => 'nsew');
	  }
	  else {
	      $frame->Label(-text => '',
			    -anchor => 'e',
			    -borderwidth => 2,
			    -relief => 'groove')->grid(-row => 1,
						       -column => $i,
						       -sticky => 'nsew');
	  }
      }

      my $gw = myEntry($frame, $obj,
	      -textvariable => \$data->{"fg$i"},
	      -width => 4,
	      -validatecommand =>
	      [\&validate,
	       real => ["Grade $i", 0.0, 5],
	       [$w, \%final_grades, $data->{"fg$i"}, $obj]]
	      )->grid(-row => 2,
		      -column => $i,
		      -sticky => 'nsew');
      $ba->attach($gw,-initwait=>0,-balloonmsg=>$revname);
  }

  $col++;

  # Grade average
  $frame->Label(-text => 'Average')->grid(-row => 0,
					  -column => $col,
					  -sticky => 'nsew');

  $frame->Label(-text => $obj->pg_avg,
		-anchor => 'e',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 1,
					   -column => $col,
					   -sticky => 'nsew');
  $frame->Label(-textvariable => $obj->get_ref('fg_avg'),
		-anchor => 'e',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 2,
					   -column => $col,
					   -sticky => 'nsew');

  # Grade median
  $col++;
  $frame->Label(-text => 'Median')->grid(-row => 0,
					 -column => $col,
					 -sticky => 'nsew');

  $frame->Label(-text => $obj->pg_med,
		-anchor => 'e',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 1,
					   -column => $col,
					   -sticky => 'nsew');
  $frame->Label(-textvariable => $obj->get_ref('fg_med'),
		-anchor => 'e',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 2,
					   -column => $col,
					   -sticky => 'nsew');

  # Grade standard deviation
  $col++;
  $frame->Label(-text => 'StdDev')->grid(-row => 0,
					  -column => $col,
					  -sticky => 'nsew');

  $frame->Label(-text => $obj->pg_stdev,
		-anchor => 'e',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 1,
					   -column => $col,
					   -sticky => 'nsew');
  $frame->Label(-textvariable => $obj->get_ref('fg_stdev'),
		-anchor => 'e',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 2,
					   -column => $col,
					   -sticky => 'nsew');
  # Grade normalized
  $col++;
  $frame->Label(-text => 'Norm')->grid(-row => 0,
				       -column => $col,
				       -sticky => 'nsew');

  $frame->Label(-text => '-',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 1,
					   -column => $col,
					   -sticky => 'nsew');
  $frame->Label(-textvariable => $obj->get_ref('fg_norm'),
		-anchor => 'e',
		-borderwidth => 2,
		-relief => 'groove')->grid(-row => 2,
					   -column => $col,
					   -sticky => 'nsew');

  return $frame;
}

## Internal Utility ##
# Name: lower_triage
# Purpose: populates the suppress triage choice
# Parameters: proposal object
#             the editable data
#             the top level of the page
# Returns: nothing
sub lower_triage {

  my ( $w, $obj, $data, $top ) = @_;

  # State of editing widgets
  my $prop_state = $lock_state;
  $prop_state = 'disabled' if $obj->triage =~ /Y|A/ or
    ($obj->prop_status =~ /N/);

  my $frame = $top->Frame();
  $frame->Label(-text => "Suppress Triaged Grades",
	       )->pack(-side => 'left');
  my %suptr = init_field('sup_triage', 'proposal');
  foreach my $val (qw/Y N/) {
    $frame->Radiobutton(-text => $val,
			-value => $val,
			-state => $prop_state,
			-variable => \$data->{sup_triage},
			-command => sub{
			  track_changes($w,
					\%suptr,
					$data->{sup_triage}, $obj) },
       		        -anchor => 'w'
		       )->pack(-side => 'left',
			       -expand => 1,
			       -fill => 'both');
  }

  $frame->Label(-text => qq/'Y' means that the grades for triaged proposals will be lowered during the Finalize stage if the average grade of this proposal is less than the highest triaged grade./,
		-wraplength => 400,
		-justify => 'left'
		)->pack(-side => 'right',
			-expand => 1,
			-fill => 'both');

  $frame;
}

## Internal Utility ##
# Name: infeasible_prop
# Purpose: populates the infeasible proposal section
# Parameters: proposal object
#             the editable data
#             the top level of the page
# Returns: nothing
sub infeasible_prop {

  my ( $w, $obj, $data, $top ) = @_;

  my $frame = $top->Frame();
  $frame->Label(-text => "Infeasible Proposal",
	       )->pack(-side => 'left');
  my %infeasible = init_field('infeasible', 'proposal');
  my %status = init_field('prop_status', 'proposal');
  foreach my $val (qw/Y N/) {
    $frame->Radiobutton(-text => $val,
			-value => $val,
			-variable => \$data->{infeasible},
			-command => sub{
			  track_changes($w,
					\%infeasible,
					$data->{infeasible},
					$obj);
			},
			-anchor => 'w'
		       )->pack(-side => 'left',
			       -expand => 1,
			       -fill => 'both');
  }
  $frame->Label(-text => qq/'Y' means that the proposal is infeasible as proposed or that the proposal did not contain any science justification./,
		-wraplength => 400,
		-justify => 'left'
		)->pack(-side => 'right',
			-expand => 1,
			-fill => 'both');

  $frame;
}

## Internal Utility ##
# Name: editComments
# Purpose: provide edit capability to comments
# Parameters: the proposal data
# Returns: nothing
sub editComments {
    my ($w, $obj) = @_;

    my $edit_box = $w->DialogBox(-title => 'PR: Edit Comments',
				 -buttons => ['Save', 'Cancel'],
			         -default_button => 'ignore'
				 );
    my $edit_frame = $edit_box->add("Frame"
				    )->pack(-fill => 'both',
					    -expand => 1);
    my $edit = $edit_frame->Scrolled("Text",
				     -wrap => 'word',
				     )->pack(-fill => 'both',
					     -expand => 1);
    $edit->insert('end', $obj->comments());
    my $answer = $edit_box->Show();

    if ($answer eq 'Save') {
	my $new_comment = $edit->get('1.0', 'end');
	$new_comment =~ s/^\s+//;
	$new_comment =~ s/\s+$//;
	$obj->save_member('comments', $new_comment);
    }

}

## Internal Utility ##
# Name: fill_rl_grid
# Purpose: populate a grid with the first column right justified and the 
#          second column left justified
# Parameters: gridded frame
#             width of column 1
#             width of column 2
#             foreground color
#             row in grid
#             column in grid
#             data
# Returns: returns row number
sub fill_rl_grid {
    my $w = shift;
    my $frame = shift;
    my $w1 = shift;
    my $w2 = shift;
    my $fgd = shift;
    my $trow = shift;
    my $col = shift;
    foreach my $row (@_) {
	$frame->Label(-text => $row->[0],
		      -anchor => 'e',
		      -foreground => 'black',
		      -relief => 'flat',
		      -width => $w1,
		  )->grid
		      ($frame->Label(-text => $row->[1],
				     -anchor => 'w',
				     -width => $w2,
				     -foreground => $fgd,)
		       )->grid(-column=>$col,
			       -row => $trow);
	$trow++;
    }
    return $trow;
}

## Internal Utility ##
# Name: init_field
# Purpose: populate the field hash to describe a field
# Parameters: field name
#             type of field
#             field index
#             datatype
# Returns: hash
sub init_field
{
    my ($field, $object, $index, $datatype) = @_;
    my %field = (field => $field,
		 object => $object,
		 index => $index,
		 datatype => $datatype);
    return %field;
}

## Internal Utility ##
# Name: myEntry
# Purpose: check validity of an entry widget and keep focus on the 
#          entry until valid
# Parameters: frame containing the entry widget
#             options for the entry widget
# Returns: nothing
sub myEntry  {
  # This is a complicated scheme to check the validity of an entry
  # and keep focus on the entry until the entry is valid.  A
  # messageBox displays a detailed error message when the focus leaves
  # the entry and is not valid

  # You pass in all of the options you want for the entry widget except
  # for -validate and -invalidcommand

  my $top = shift;
  my $obj = shift;

  my $widget = $top->Entry( @_ );
  if ($lock_state =~ /disabled/ or $obj->triage =~ /Y|A/ or
      ($obj->prop_status =~ /N/)) {
    $widget->configure(-state => 'disabled');
  }
  else {
    my $error;
    my $validatecommand = $widget->cget('-validatecommand');
    push @$validatecommand, \$error;

    $widget->configure(
		       -validate => 'focusout',
		       -validatecommand => $validatecommand,
		       -invalidcommand => sub {
			 $top->messageBox(-title => 'PR: Data Entry Error',
					  -type => 'OK',
					  -message => $error);
			 $widget->focus } );
  }
  return $widget;
}

## Internal Utility ##
# Name: validate
# Purpose: checks the validity of the data in an entry widget
# Parameters: datatype
#             limits of data
#             arguments for calling track_changes
#             error message for myEntry
#             data being validated
#             the last character entered?
#             the index of the cursor
#             the action which causes validation
# Returns: true if valid, false if invalid
sub validate
{
  my ( $type, $limits, $track, $error,
       $new, $chars, $current, $index, $action ) = @_;
  # $type is either real or int
  # $limits is an anonymous array with the following elements:
  #       string which describes what's in the entry
  #       min for entry
  #       max for entry
  # $track is an anonymous array containing the arguments to call
  #    track_changes
  #
  # The rest are taken care of in myEntry.  They are the values passed to the
  # validatecommand in an entry widget

  # truncate type from integer to int for the regular expression below
  $type = "int" if $type =~ /int/;

  my ( $field, $min, $max ) = @$limits;

  my $art = 'a';
  $art .= 'n' if $type =~ /int/;

  my $data;
  $data = 'integer' if $type =~ /int/;
  $data = 'real number' if $type =~ /real/;
  if ($data =~ /int/ || $data =~ /real/) {
    $new =~ s/ //g;
  }
  # The entry must be blank or of the correct datatype
  $$error = "$field must be $art $data" and return 0 unless
    ($new =~ /^$RE{num}{$type}$/ or !$new);

  # The entry must be within the min and max if they exist
  if (defined ($new) and $new ne '') {
      if ( defined $min && $new < $min )
      {
	  $$error = "$field must be greater than $min";
	  return 0;
      }

      if ( defined $max && $new > $max )
      {
	  $$error = "$field must be less than $max";
	  return 0;
      }
  }

  # the track array comes in with the old value,
  # so we need to set it to the new value if
  # validation is okay
  $track->[2] = $new;
  track_changes(@$track);
  1;
}

## Internal Utility ##
# Name: mkFrame
# Purpose: create a frame
# Parameters: fraem to put new frame into
# Returns: the new frame
sub mkFrame
{
    my ($w) = @_;
    my $frame = $w->Frame->pack;
    return $frame;
}

## Internal Utility ##
# Name: mkCheckButton
# Purpose: creates a check button
# Parameters: frame to put check button into
#             data for check button
#             reference to field information
#             proposal object
#             tracking information
# Returns: returns the button
sub mkCheckButton
{
  my ( $w, $data, $field_ref, $obj, $track) = @_;
  my %field = %$field_ref;

  my $state = $lock_state;
  $state = 'disabled' if $obj->triage =~ /Y|A/ or
    ($obj->prop_status =~ /N/ and $obj->type !~ /ARCHIVE|THEORY/);

  my $button = $w->Checkbutton(-onvalue => 'Y',
			       -offvalue => 'N',
			       -state => $state,
			       -variable => \$data->{$field{field}},
 			       );
  $button->bind( '<Button>', [ $track, 'track_changes', $field_ref,
			       $data->{$field{field}}, $obj ] );

  $button;
}

## Internal Utility ##
# Name: mkTargButton
# Purpose: creates a check button for turning targets on and off
# Parameters: frame to put check button into
#             data for check button
#             reference to field information
#             proposal object
#             tracking information
# Returns: returns the button
sub mkTargButton
{
  my ( $w, $data, $field_ref, $obj, $track) = @_;
  my %field = %$field_ref;
  my $i = $field{index};
  my $var = $field{field};
  my $value = $data->{targets}[$i]{$var};
  my $state = $lock_state;
  $state = 'disabled' if $obj->triage =~ /Y|A/ or
    ($obj->prop_status =~ /N/ and $obj->type !~ /ARCHIVE|THEORY/);

  my $button = $w->Checkbutton(-text => 'Y',
			       -onvalue => 'Y',
			       -offvalue => 'N',
			       -state => $state,
			       -variable => \$value,
			       -command => sub {
				   track_changes($track, $field_ref,
						 $value,
						 $obj)}
 			       );
  $button;
}

## Internal Utility ##
# Name: mkTargRadio
# Purpose: creates a radio button for turning targets on and off
# Parameters: frame to put check button into
#             data for check button
#             reference to field information
#             proposal object
#             tracking information
# Returns: returns the frame with buttons
sub mkTargRadio
{
  my ( $w, $data, $field_ref, $obj, $track) = @_;
  my %field = %$field_ref;
  my $i = $field{index};
  my $var = $field{field};
  my $value = $data->{targets}[$i]{$var};
  my $frame = $w->Frame()->pack(-expand => 1,
				-fill => 'both');
  my $state = $lock_state;
  $state = 'disabled' if $obj->triage =~ /Y|A/ or
    ($obj->prop_status =~ /N/ and $obj->type !~ /ARCHIVE|THEORY/);

  my @status = ('Y', 'N');
  push @status, 'G' if $obj->rank;
  foreach my $val (@status) {
    $frame->Radiobutton(-text => $val,
			-value => $val,
			-variable => \$value,
			-state => $state,
			-command => sub {
			  track_changes($track, $field_ref,
					$value,
					$obj)}
		       )->pack(-side => 'left');
  }
  $frame
}

## Internal Utility ##
# Name: mkTooButton
# Purpose: creates a check button for turning too targets on and off
# Parameters: frame to put radio button into
#             data for check button
#             reference to field information
#             proposal object
#             tracking information
# Returns: returns the button
sub mkTooButton
{
  my ( $w, $data, $field_ref, $obj, $track) = @_;
  my %field = %$field_ref;
  my ($i, $j) = split /\t/, $field{index};
  my $var = $field{field};
  my $value = $data->{targets}[$i]{toos}[$j]{$var};
  my $state = $lock_state;
  $state = 'disabled' if $obj->triage =~ /Y|A/ or
    ($obj->prop_status =~ /N/ and $obj->type !~ /ARCHIVE|THEORY/);

  my $button = $w->Checkbutton(-text => 'Y',
			       -onvalue => 'Y',
			       -offvalue => 'N',
			       -state => $state,
			       -variable => \$value,
			       -command => sub {
				   track_changes($track, $field_ref,
						 $value,
						 $obj)}
 			       );
  $button;
}

## Internal Utility ##
# Name: mkTooRadio
# Purpose: creates a radio button for turning too targets on and off
# Parameters: frame to put radio buttons into
#             data for check button
#             reference to field information
#             proposal object
#             tracking information
# Returns: returns the button
sub mkTooRadio
{
  my ( $w, $data, $field_ref, $obj, $track) = @_;
  my %field = %$field_ref;
  my ($i, $j) = split /\t/, $field{index};
  my $var = $field{field};
  my $value = $data->{targets}[$i]{toos}[$j]{$var};
  my $state = $lock_state;
  $state = 'disabled' if $obj->triage =~ /Y|A/ or
    ($obj->prop_status =~ /N/ and $obj->type !~ /ARCHIVE|THEORY/);

  my $frame = $w->Frame()->pack(-expand => 1,
				-fill => 'both');
  my @status = ('Y', 'N');
  push @status, 'G' if $obj->rank;
  foreach my $val (@status) {
    $frame->Radiobutton(-text => $val,
			-value => $val,
			-variable => \$value,
			-state => $state,
			-command => sub {
			  track_changes($track, $field_ref,
					$value,
					$obj)}
		       )->pack(-side => 'left');
  }
  $frame;
}

## Internal Utility ##
# Name: track_changes
# Purpose: track if changes are made to the data
# Parameters: reference to field information
#             data
#             proposal object
# Returns: nothing
sub track_changes
{
  my ( $w, $field_ref, $data, $obj) = @_;
  $data = "" if !$data;
  my %field = %$field_ref;
  my $pData = $w->privateData;
  my %pData = %$pData;
  if ($field{object} =~ /targets/) {
    if ($field{object} =~ /toos/) {
      my $member = $field{field};
      my ($i, $j) = split /\t/, $field{index};
      my $value = $obj->target($i)->too($j)->$member;
      $value = '' if !defined($value);
      $pData->{chgData}{targets}[$i]{toos}[$j]{$member} =
	$data ne $value;
      # Need to set the private data value for status because the
      # checkbutton is not properly doing it
      if ($member =~ /status/ and $member !~ /bck/ and
	  $pData->{chgData}{targets}[$i]{toos}[$j]{$member}) {
	$pData->{propData}{targets}[$i]{toos}[$j]{$member} = $data;
      }
    }
    else {
      my ($i, $j) = split /\t/, $field{index};
      my $member = $field{field};
      my $value = $obj->target($i)->$member;
      $value = '' if !defined($value);
      $pData->{chgData}{targets}[$i]{$member} = $data ne $value;
      # Need to set the private data value for status because the
      # checkbutton is not properly doing it
      if ($member =~ /status/ and $member !~ /bck/ and
	  $pData->{chgData}{targets}[$i]{$member}) {
	$pData->{propData}{targets}[$i]{$member} = $data;
      }
    }

    # Need to propagate changes to time and num_obs of
    # raster scan targets to the total approved time of the target
    my ($i, $j) = split /\t/, $field{index};
    if ($pData->{propData}{targets}[$i]{raster_scan} eq 'Y') {
      if  (($pData->{chgData}{targets}[$i]{num_obs_app} or
	    $pData->{chgData}{targets}[$i]{time_obs_app})
	  ) {
	$pData->{chgData}{targets}[$i]{app_time} = 1;
	my $numobs = $pData->{propData}{targets}[$i]{num_obs_app};
	my $timeobs = $pData->{propData}{targets}[$i]{time_obs_req};
	$pData->{propData}{targets}[$i]{app_time} = $numobs * $timeobs;

	if ($pData->{propData}{type} =~ /TOO/ or
	    $pData->{propData}{targets}[$i]{monitor} =~ /Y|P/) {
	  $pData->{chgData}{targets}[$i]{toos}[$j]{app_obs_time} = 1;
	  $pData->{propData}{targets}[$i]{toos}[$j]{app_obs_time} =
	    $numobs * $timeobs;
	}
      }
      elsif  (($pData->{chgData}{targets}[$i]{num_obs_app_1} or
	       $pData->{chgData}{targets}[$i]{time_obs_app_1})) {
	$pData->{chgData}{targets}[$i]{app_time_1} = 1;
	my $numobs = $pData->{propData}{targets}[$i]{num_obs_app_1};
	my $timeobs = $pData->{propData}{targets}[$i]{time_obs_req_1};
	$pData->{propData}{targets}[$i]{app_time_1} = $numobs * $timeobs;

	if ($pData->{propData}{type} =~ /TOO/ or
	    $pData->{propData}{targets}[$i]{monitor} =~ /Y|P/) {
	  $pData->{chgData}{targets}[$i]{toos}[$j]{app_obs_time} = 1;
	  $pData->{propData}{targets}[$i]{toos}[$j]{app_obs_time} =
	    $numobs * $timeobs;
	}
      }
      elsif  (($pData->{chgData}{targets}[$i]{num_obs_app_2} or
	       $pData->{chgData}{targets}[$i]{time_obs_app_2})) {
	$pData->{chgData}{targets}[$i]{app_time_2} = 1;
	my $numobs = $pData->{propData}{targets}[$i]{num_obs_app_2};
	my $timeobs = $pData->{propData}{targets}[$i]{time_obs_req_2};
	$pData->{propData}{targets}[$i]{app_time_2} = $numobs * $timeobs;

	if ($pData->{propData}{type} =~ /TOO/ or
	    $pData->{propData}{targets}[$i]{monitor} =~ /Y|P/ ) {
	  $pData->{chgData}{targets}[$i]{toos}[$j]{app_obs_time} = 1;
	  $pData->{propData}{targets}[$i]{toos}[$j]{app_obs_time} =
	    $numobs * $timeobs;
	}
      }
    }
  }
  elsif ($field{field} =~ /grade/) {
    my $i = $field{index};
    my $value;
    $value = $obj->get("g$i");

    $value = '' if !defined($value);

    $pData->{chgData}{"fg$i"} = $data ne $value;
  }
  elsif ($field{object} =~ /joint/) {
    my $obs = $field{index};
    my $member;
    $member = "$obs\_app" if $field{field} =~ /app_time/;
    $member = "$obs\_req" if $field{field} =~ /req_time/;
    $member = "$obs\_dtype" if $field{field} =~ /datatype/;
    $member = "$obs\_units" if $field{field} =~ /units/;

    $member = lc($member);
    my $value = $obj->get($member);
    $value = '' if !defined($value);
    $pData->{chgData}{$member} = $data ne $value;
  }
  elsif ($field{object} =~ /alt_grps/) {
    my $value = $obj->alt_grp($field{index}, $field{field});
    $value = '' if !defined($value);
    $pData->{chgData}{$field{object}}{$field{index}}{$field{field}} = $data ne
				       $value;
  }
  else {
    my $value = $obj->get($field{field});
    $value = '' if !defined($value);
#print "$field{object} -- $field{field} -- $value\n";

    $pData->{chgData}{$field{field}} = $data ne $value;
  }
  $w->Callback( '-changecmd', $w->changed_data )
}


## Internal Utility ##
# Name: reset
# Purpose: reset the value in the widget to the original value
# Parameters: proposal object
# Returns: nothing
sub reset
{
  my $w = shift;
  my $obj = shift;
  if (!$obj) {
    my $panel = $w->cget('-panel');
    my $prop_id = $w->cget('-prop_id');
    $obj = $panel->proposals->proposal($prop_id);
  }

  my $pData = $w->privateData;
  my $data = $pData->{propData};
  my $chg = $pData->{chgData};

  foreach my $prop (@Pprops){
    $data->{$prop} = $obj->get($prop);
    $chg->{$prop} = 0;
  }
  #print STDERR "reset g_cmt to  " . $obj->get("g_cmt") . "------\n";
  $data->{g_cmt} = $obj->get("g_cmt");
  $data->{comments} = $obj->get("comments");
  if ($pData->{cmtw}) {
    $pData->{cmtw}->delete('1.0','end');
    $pData->{cmtw}->insert('0.0', $obj->get("comments"));
    $pData->{cmtw}->insert('end', "\n\n");
  }

  if ($obj->joint_flag !~ /None/) {
    my @obs = split /\+/, $obj->joint_flag;
    foreach my $obs (@obs) {
      $obs = lc $obs;
      foreach my $prop (qw(_app _req _dtype _units)) {
	$data->{"$obs$prop"} = $obj->get("$obs$prop");
	$chg->{"$obs$prop"} = 0;
      }
    }
  }


  # Prelim Grades:
  for (my $i = 1; $i <= 11; $i++) {
    $data->{"pg$i"} = $obj->get("pg$i");
    $chg->{"pg$i"} = 0;
  }

  # Final Grades:
  for (my $i = 1; $i <= 25; $i++) {
    $data->{"fg$i"} = $obj->get("g$i");
    $chg->{"fg$i"} = 0;
  }

  # Alternate Target Groups
  if ($obj->alt_grps) {
    my $alts = $obj->alt_grps;
    foreach my $alt_id (keys %$alts) {
      $data->{alt_grps}{$alt_id} ||= {};
      $chg->{alt_grps}{$alt_id} ||= {};
      foreach my $alt_prop (@AltProps) {
	$data->{alt_grps}{$alt_id}{$alt_prop} =
	  $obj->alt_grp($alt_id)->get($alt_prop);
	$chg->{alt_grps}{$alt_id}{$alt_prop} = 0;
      }
    }
  }

  # Targets
  for (my $i = 1; $i <= $obj->num_targets; $i++) {
    $data->{targets}[$i] ||= {};
    $chg->{targets}[$i] ||= {};
    foreach my $prop (@Tprops) {
      $data->{targets}[$i]{$prop} = $obj->target($i)->get($prop);
      $chg->{targets}[$i]{$prop} = 0;
    }

    if ($obj->type =~ /TOO/ or $obj->target($i)->monitor =~ /Y|P/) {
      my $reps = $obj->target($i)->num_obs_req +
                 $obj->target($i)->num_obs_req_1 +
                 $obj->target($i)->num_obs_req_2;
      for (my $j = 1; $j <= $reps; $j++ ) {
	foreach my $too_prop (@TooProps) {

	  $data->{targets}[$i]{toos}[$j]{$too_prop} =
	    $obj->target($i)->too($j)->get($too_prop);
	  $chg->{targets}[$i]{toos}[$j]{$too_prop} = 0;
	}
      }
    }
  }

  $w->Callback( '-changecmd', $w->changed_data );
}
## Internal Utility ##
# Name: switchgrades
# Purpose: switch grades
# Parameters: none
# Returns: nothing
sub switchgrades
{
  my $w = shift;
  my $stat = $w->validate_last();
  return if !$stat;

  # data have been changed; does the user want to save, discard, or cancel?
  if ( $w->changed_data && $w->changed_data != 9)
  {
    my $dialog = $w->Dialog( -text => 'There are unsaved changes.',
			     -bitmap => 'warning',
			     -default_button => 'Save',
			     -buttons => [ qw/ Save Discard Cancel / ] );
    my $answer = $dialog->Show;

    'Cancel' eq $answer && return;

    # save data and fall through to close
    'Save' eq $answer && $w->save;

    'Discard' eq $answer && $w->reset;
  }

  $w->switchcmd;

}

sub switchcmd
{
  my $w=shift;

  my $panel = $w->cget('-panel');
  my $prop_id = $w->cget('-prop_id');
  my $obj = $panel->proposals->proposal($prop_id);
  $obj->switch_grades();

  # Propagate average grade to normalized grade
  $panel->set_grades();
  $panel->set_lp_grades() if $panel->panel_id==99;
  $panel->set_vlp_grades() if $panel->panel_id==99;
  $panel->set_xvp_grades() if $panel->panel_id==99;

  my $pData = $w->privateData;
  #if ($pData->{switched} && $pData->{switched} == 9) {
    #$pData->{switched} = 0;
  #} else {
    #$pData->{switched} = 9;
  #}
  $pData->{switched} = 9;
  #cause all the other data has already been saved or canceled
  $w->reset();

}

## Internal Utility ##
# Name: save
# Purpose: save all changes to proposal object and database
# Parameters: none
# Returns: nothing
sub save
{
  my $w = shift;
  my $stat = $w->validate_last();
  return if !$stat;

  my $joint_warn = 0;  # This flag indicates that there was a save to the
                       # approved time and that a change to the joint time
                       # may be necessary

  my $panel = $w->cget('-panel');
  my $prop_id = $w->cget('-prop_id');
  my $obj = $panel->proposals->proposal($prop_id);
  my $pData = $w->privateData;
  my $data = $pData->{propData};
  my $chg = $pData->{chgData};
  if ($obj && $data && $obj->{g_cmt} ne $data->{g_cmt}) {
     #print STDERR "$prop_id: save g_cmt " . $data->{g_cmt} . "\n";
     $obj->save_member("g_cmt",$data->{g_cmt});
  }

  # for the other grades, if they hit save it's ok
  #print STDERR "saved set switched to 0\n";
  $pData->{switched} = 0;

  my $cmtw = $pData->{cmtw};
  if ($cmtw) {
    my $new_comment = $cmtw->get('1.0', 'end');
    $new_comment =~ s/^\s+//;
    $new_comment =~ s/\s+$//;
    if ($obj && $obj->{comments} ne $new_comment) {
      #print STDERR "$prop_id: save comments----" . $new_comment . "-----\n";
      $obj->save_member("comments",$new_comment);
    }
  }
  # Save the changed values for the proposal
  foreach my $member (@Pprops){
    if ($chg->{$member} > 0) {
      $obj->save_member($member, $data->{$member});
    }
  }

  # Save alternate group data
  my $alt = $chg->{alt_grps};
  foreach my $id (keys %$alt) {
    foreach my $member (@AltProps) {
      if ($chg->{alt_grps}{$id}{$member} > 0) {
	$obj->alt_grp($id)->
	  save_member($member, $data->{alt_grps}{$id}{$member});
	$obj->alt_grp($id)->save2database;
      }
    }
  }

  # Save joint changes
  if ($obj->joint_flag !~ /None/) {
    my @obs = split /\+/, $obj->joint_flag;
    foreach my $obs (@obs) {
      $obs = lc $obs;
      foreach my $prop (qw(_app _req _dtype _units)) {
	if ($chg->{"$obs$prop"} > 0) {
	  $obj->save_member("$obs$prop", $data->{"$obs$prop"});
	}
      }
    }
  }

  # Save grade changes
  my $grade_update = 0;  # This variable lets me know if the average grade
                         # will be changing for this proposal
  for (my $i = 1; $i <= $num_grades; $i++) {
    if ($chg->{"fg$i"}) {
      $grade_update++;
      if ($data->{"fg$i"} and $data->{"fg$i"} ne '') {
	$data->{"fg$i"} = sprintf "%3.2f", $data->{"fg$i"};
      }
      else {
	$data->{"fg$i"} = undef;
      }
      $obj->save_member("g$i", $data->{"fg$i"});
    }
  }


  # Save TOO changes
  my $too_warn = 0;
  if (($obj->type =~ /TOO/ or $obj->num_monitor) and
      $obj->prop_status !~ /$STAT_BPP/) {
    for (my $i = 1; $i <= $obj->num_targets; $i++) {
      my $at_hel = $obj->target($i)->at_hel;
      my $reps = $obj->target($i)->num_obs_req +
	$obj->target($i)->num_obs_req_1 +
	  $obj->target($i)->num_obs_req_2;
      $reps = 1 if $obj->target($i)->raster_scan eq 'Y';
      for (my $j = 1; $j <= $reps; $j++) {
	my $chg_flg = 0;
        if (defined $chg->{targets}[$i]{toos} ) {
	foreach my $too_prop (@TooProps) {
	  if ($chg->{targets}[$i]{toos}[$j]{$too_prop} > 0) {
	    $joint_warn++ if $too_prop =~ /status|time/;

	    $chg_flg++;
	    $obj->target($i)->too($j)->
	      save_member($too_prop,
			  $data->{targets}[$i]{toos}[$j]{$too_prop} );
	    $obj->target($i)->too($j)->save2database();
	  }
	}
	}
        else {
          #print STDERR "$i: no toos $prop_id\n";
        }

	# If there were changes made, we need to propagate the app_obs_time,
	# targ_status, and num_obs_app up to target
	if ($chg_flg) {
	  if ($obj->target($i)->too($j)->get('ao') == 0) {
	    my $status = $obj->target($i)->calc_status;
	    my $time = $obj->target($i)->num_time;
	    $obj->target($i)->set('targ_status', $status );
	    $obj->target($i)->set('app_time', $time );
	    if (defined $at_hel and  $at_hel eq 'Y') {
		  $obj->target($i)->set('app_hel', $time);
	    }
	    $obj->target($i)->set('num_obs_app',
				  $obj->target($i)->num_followups(0) + 1) if
				    $obj->target($i)->num_followups(0) +
				      $obj->target($i)->num_followups(1) +
					$obj->target($i)->num_followups(2);
	    $obj->target($i)->save2database();
	  }
	  elsif ($obj->target($i)->too($j)->get('ao') == 1) {
	    my $status = $obj->target($i)->calc_status_1;
	    my $time = $obj->target($i)->num_time_1;
	    $obj->target($i)->set('targ_status_1', $status );
		  if (defined $at_hel and  $at_hel eq 'Y') {
			  $obj->target($i)->set('app_time_1', $time);
		  }
	    $obj->target($i)->set('num_obs_app_1',
				  $obj->target($i)->num_followups(1)) if
				    $obj->target($i)->num_followups(1);
	    $obj->target($i)->save2database();
	  }
	  elsif ($obj->target($i)->too($j)->get('ao') == 2) {
	    my $status = $obj->target($i)->calc_status_2;
	    my $time = $obj->target($i)->num_time_2;
	    $obj->target($i)->set('targ_status_2', $status );
		  if (defined $at_hel and  $at_hel eq 'Y') {
			  $obj->target($i)->set('app_time_2', $time);
		  }
	    $obj->target($i)->set('num_obs_app_2',
				  $obj->target($i)->num_followups(2)) if
				    $obj->target($i)->num_followups(2);
	    $obj->target($i)->save2database();
	  }
	}
      }
    }
  }

  # Save changes to target properties
  foreach my $prop (@Tprops) {
    for (my $i = 1; $i <= $obj->num_targets; $i++) {
      my $chg_flg = 0;
      if ($chg->{targets}[$i]{$prop} > 0) {
	$joint_warn++ if $prop =~ /time|status/;
	if ($prop eq 'time_obs_app' or $prop eq 'time_obs_app_1' or $prop eq 'time_obs_app_2') {
	  $data->{targets}[$i]{$prop} = sprintf "%6.2f",
	    $data->{targets}[$i]{$prop};
	}
	$obj->target($i)->save_member($prop,
				      $data->{targets}[$i]{$prop});
	$obj->target($i)->save2database();
      }
    }
  }


  # Do all the bookkeeping calculations in target
  #   tc_group_app
  #   tc_grade_app
  #   tax
  #   tc
  #   num_pnt_app
  for (my $i = 1; $i <= $obj->num_targets; $i++) {
    if ($obj->prop_status !~ /$STAT_BPP/) {
      $obj->target($i)->set('tc_monitor',
			    $obj->target($i)->calc_tc_too(0)) if
			      $obj->target($i)->monitor =~ /Y/;
      $obj->target($i)->set('tc_monitor_1',
			    $obj->target($i)->calc_tc_too(1)) if
			      $obj->target($i)->monitor =~ /Y/ and
				$obj->target($i)->req_time_1;
      $obj->target($i)->set('tc_monitor_2',
			    $obj->target($i)->calc_tc_too(2)) if
			      $obj->target($i)->monitor =~ /Y/ and
				$obj->target($i)->req_time_2;
      $obj->target($i)->set('tc_group_app',
			    $obj->target($i)->calc_tc_group) if
			      $obj->target($i)->group_obs =~ /Y/;
      $obj->target($i)->set('tc_group_app_1',
			    $obj->target($i)->calc_tc_group_1) if
			      $obj->target($i)->group_interval_1;
      $obj->target($i)->set('tc_group_app_2',
			    $obj->target($i)->calc_tc_group_2) if
			      $obj->target($i)->group_interval_2;
      $obj->target($i)->set('tc_too',
			    $obj->target($i)->calc_tc_too(0)) if
			      defined $obj->target($i)->response_time;
      $obj->target($i)->set('tc_too_1',
			    $obj->target($i)->calc_tc_too(1)) if
			      defined $obj->target($i)->response_time and
				$obj->target($i)->req_time_1;
      $obj->target($i)->set('tc_too_2',
			    $obj->target($i)->calc_tc_too(2)) if
			      defined $obj->target($i)->response_time and
				$obj->target($i)->req_time_2;
      $obj->target($i)->set('tc_grade_app',
			    $obj->target($i)->calc_tc_grade) if
			      $obj->target($i)->time_crit =~ /Y/;
      $obj->target($i)->set('tc_grade_app_1',
			    $obj->target($i)->calc_tc_grade_1) if
			      $obj->target($i)->time_crit =~ /Y/;
      $obj->target($i)->set('tc_grade_app_2',
			    $obj->target($i)->calc_tc_grade_2) if
			      $obj->target($i)->time_crit =~ /Y/;
      $obj->target($i)->set('tax', $obj->target($i)->calc_tax);
      $obj->target($i)->set('tax_1', $obj->target($i)->calc_tax_1) if
	$obj->target($i)->req_time_1;
      $obj->target($i)->set('tax_2', $obj->target($i)->calc_tax_2) if
	$obj->target($i)->req_time_2;
      $obj->target($i)->set('tc', $obj->target($i)->calc_tc);
      $obj->target($i)->set('tc_1', $obj->target($i)->calc_tc_1)if
	$obj->target($i)->req_time_1;
      $obj->target($i)->set('tc_2', $obj->target($i)->calc_tc_2) if
	$obj->target($i)->req_time_2;
      $obj->target($i)->set('num_pnt_app', $obj->target($i)->num_pnt);
      $obj->target($i)->set('num_pnt_app_1', $obj->target($i)->num_pnt_1) if
	$obj->target($i)->req_time_1;
      $obj->target($i)->set('num_pnt_app_2', $obj->target($i)->num_pnt_2) if
	$obj->target($i)->req_time_2;

      $obj->target($i)->save2database();
    }
  }

  # Do all the bookkeeping calculations in proposal
  #   total_app_time
  #   prop_app_time
  #   prop_status
  #   num_obs_app
  #   num_targ_app
  #   tax_tot
  #   tc_(e|a|d)_app
  #   (vf|f|s|vs)_app
  if (($obj->prop_status !~ /$STAT_BPP/) && ($obj->triage !~ /Y|A/)) {
    $obj->set('prop_status', $obj->calc_status);
  }
  if ($obj->type !~ /ARC|THE/ and $obj->prop_status !~ /$STAT_BPP/) {

    $obj->set('total_app_time', fmt("%7.2f",$obj->tot_time));
    $obj->set('total_app_time_1', fmt("%7.2f",$obj->tot_time_1));
    $obj->set('total_app_time_2', fmt("%7.2f",$obj->tot_time_2));

    $obj->set('total_app_hel', fmt("%7.2f",$obj->tot_hel));
    $obj->set('total_app_hel_1', fmt("%7.2f",$obj->tot_hel_1));
    $obj->set('total_app_hel_2', fmt("%7.2f",$obj->tot_hel_2));


    $obj->set('prop_app_time', fmt("%7.2f",$obj->prop_time));
    $obj->set('prop_app_time_1', fmt("%7.2f",$obj->prop_time_1));
    $obj->set('prop_app_time_2', fmt("%7.2f",$obj->prop_time_2));

    # use tot_hel to not include slew
    $obj->set('prop_app_hel', fmt("%7.2f",$obj->tot_hel));
    $obj->set('prop_app_hel_1', fmt("%7.2f",$obj->tot_hel_1));
    $obj->set('prop_app_hel_2', fmt("%7.2f",$obj->tot_hel_2));

    $obj->set('num_obs_app', fmt("%3d",$obj->num_pnt));
    $obj->set('num_obs_app_1',fmt("%3d", $obj->num_pnt_1));
    $obj->set('num_obs_app_2', fmt("%3d",$obj->num_pnt_2));

    $obj->set('num_targ_app', fmt("%3d",$obj->num_targs));
    $obj->set('num_targ_app_1', fmt("%3d",$obj->num_targs_1));
    $obj->set('num_targ_app_2',fmt("%3d", $obj->num_targs_2));

    $obj->set('tax_tot', fmt("%7.2f",$obj->num_tax));
    $obj->set('tax_tot_1', fmt("%7.2f",$obj->num_tax_1));
    $obj->set('tax_tot_2',fmt("%7.2f", $obj->num_tax_2));

    if ($obj->type !~ /TOO/ and $obj->prop_status !~ /$STAT_BPP/) {
      $obj->set('rc_score_app', fmt("%8.2f",$obj->num_rc_score()));
      $obj->set('rc_score_app_1',fmt("%7.2f", $obj->num_rc_score_1()));
      $obj->set('rc_score_app_2',fmt("%7.2f", $obj->num_rc_score_2()));
    }

    $obj->set('tc_e_app', fmt("%7.2f",$obj->num_tc('e')));
    $obj->set('tc_e_app_1',fmt("%7.2f", $obj->num_tc_1('e')));
    $obj->set('tc_e_app_2',fmt("%7.2f", $obj->num_tc_2('e')));

    $obj->set('tc_a_app', fmt("%7.2f",$obj->num_tc('a')));
    $obj->set('tc_a_app_1', fmt("%7.2f",$obj->num_tc_1('a')));
    $obj->set('tc_a_app_2', fmt("%7.2f",$obj->num_tc_2('a')));

    $obj->set('tc_d_app', fmt("%7.2f",$obj->num_tc('d')));
    $obj->set('tc_d_app_1', fmt("%7.2f",$obj->num_tc_1('d')));
    $obj->set('tc_d_app_2', fmt("%7.2f",$obj->num_tc_2('d')));

    my ($vf_too,$f_too,$s_too,$vs_too) = $obj->num_too_new();
    $obj->set("vf_app", fmt("%7.2f",$vf_too)) if (defined $vf_too);
    $obj->set("f_app", fmt("%7.2f",$f_too)) if (defined $f_too);
    $obj->set("s_app", fmt("%7.2f",$s_too)) if (defined $s_too);
    $obj->set("vs_app", fmt("%7.2f",$vs_too)) if (defined $vs_too);

    $obj->save2database();
  }

  # Check if the average grade is lower than the highest triaged grade.  If it
  # is, we pop up a requestor asking if they want to lower the triaged grades
  # by the difference.
  #  y -> set sup_triage to Y
  #  n -> set sup_triage to N
  # don't suppress as of cycle 14
  my $NOSUPPRESS = 0;
  if ($NOSUPPRESS) {
  if ($grade_update and $obj->prop_status !~ /$STAT_BPP/) {
    my $gr_query = $obj->dbh->prepare(qq(select max(fg_avg) from proposal
                                         where triage in ('Y', 'A') and 
                                         panel_id = ?));
    $gr_query->execute($obj->panel_id);
    my ($tr_avg) = $gr_query->fetchrow_array;
    $gr_query->finish;

    $tr_avg = 0 if not $tr_avg;

    if ($tr_avg > $obj->fg_avg ) {
      my $diff = $tr_avg - $obj->fg_avg;
      my $text = qq/The average grade for this proposal is lower than the highest average grade for the triaged proposals.  Do you wish to lower the average grades of the triaged proposals by $diff points?/;
      my $dialog = $w->Dialog(-title => 'Please Reply',
			      -text => $text,
			      -buttons => ['Y', 'N'],
			      -default_button => 'N',
			     );

      my $answer = $dialog->Show();

      $obj->save_member('sup_triage', $answer);
      $obj->save2database;
    }
  }
  }

  # Give a warning if there were any joint updates.
  if ($joint_warn and $obj->joint_flag !~ /None/) {
    my $text = qq(The status and/or time of one or more targets in the proposal has changed.  Please check that the joint time has also been changed if needed.);
    my $dialog = $w->Dialog(-title => 'Warning',
			    -text => $text,
			    -buttons => ['Okay'],
			    -default_button => 'Okay',
			   );
    $dialog->Show();
  }

  # Give a warning if a trigger was turned off.
  if ($obj->type =~ /TOO/) {
    my $too_warn = 0;
    for (my $i = 1; $i <= $obj->num_targets; $i++) {
      my $follow = $obj->target($i)->num_obs_req + $obj->target($i)->num_obs_req_1 + $obj->target($i)->num_obs_req_2 - 1;
      $follow = 0 if $obj->target($i)->raster_scan eq 'Y';

      if ($obj->target($i)->targ_status =~ /N/ and $follow) {
	for (my $j = 2; $j <= $follow +1; $j++) {
	  if ($obj->target($i)->too($j)->obs_status =~ /Y/) {
	    $too_warn++;
	    my $text = 'The trigger for target ' .
	      $obj->target($i)->targ_num() .
		" has been turned off.  Please turn off all of the followups for that target";

	    my $dialog = $w->Dialog(-title => 'Warning',
				    -text => $text,
				    -buttons => ['Okay'],
				    -default_button => 'Okay',
				   );
	    $dialog->Show();
	  }
	  last if $too_warn;
	}
      }
    }
  }


  # Propagate average grade to normalized grade
  $panel->set_grades();
  $panel->set_lp_grades() if $panel->panel_id==99;
  $panel->set_vlp_grades() if $panel->panel_id==99;
  $panel->set_xvp_grades() if $panel->panel_id==99;

  # Propagate cummulative totals to panel
  $panel->set_running_totals();
  $w->reset;    # reset the private data and change values
  $w->Callback( '-changecmd', $w->changed_data );
  $w->Callback( '-savecmd');
}

## Internal Utility ##
# Name: changed_data
# Purpose: checks if any data has been changed
# Parameters: none
# Returns: true if any data have been changed
sub changed_data
{
  my $w = shift;
  my $pData = $w->privateData;
  my $data = $pData->{propData};
  my $chg = $pData->{chgData};
  my $changed = 0;
  my $panel = $w->cget('-panel');
  my $prop_id = $w->cget('-prop_id');
  my $obj="";
  if ($panel) {
    $obj = $panel->proposals->proposal($prop_id);
  }

  foreach my $prop (@Pprops) {
    $changed += $chg->{$prop};
  }
  if ($obj && $data && $obj->{g_cmt} ne $data->{g_cmt})  {
      $changed += 1;
  }
  my $cmtw = $pData->{cmtw};
  if ($cmtw) {
    my $new_comment = $cmtw->get('1.0', 'end');
    $new_comment =~ s/^\s+//;
    $new_comment =~ s/\s+$//;
    if ($obj && $obj->{comments} ne $new_comment) {
      $changed += 1;
    }
  }

  if ($data->{joint_flag} !~ /None/) {
    my @obs = split /\+/, $data->{joint_flag};
    foreach my $obs (@obs) {
      $obs = lc $obs;
      foreach my $prop (qw(_app _req _dtype _units)) {
	$changed += $chg->{"$obs$prop"};
      }
    }
  }

  for (my $i = 1; $i <= 11; $i++) {
    $changed += $chg->{"pg$i"};
  }
  for (my $i = 1; $i <= 25; $i++) {
    $changed += $chg->{"fg$i"};
  }
  for (my $i = 1; $i <= $data->{num_targets}; $i++) {
    foreach my $prop (@Tprops) {
      $changed += $chg->{targets}[$i]{$prop};

      if ($data->{type} =~ /TOO/ or $data->{targets}[$i]{monitor} =~/Y|P/) {
	my $reps = $data->{targets}[$i]{num_obs_req} + $data->{targets}[$i]{num_obs_req_1} + $data->{targets}[$i]{num_obs_req_2};
	for (my $j = 1; $j <= $reps; $j++) {
	  foreach my $too_prop (@TooProps) {
	    $changed += $chg->{targets}[$i]{toos}[$j]{$too_prop};
	  }
	}
      }
    }
  }

  if ($changed > 0) {
     $changed = 1;
  } else {
    if ($pData->{switched} && $pData->{switched} == 9) {
     $changed = 9;
    }
  }

  return $changed;
}

## Internal Utility ##
# Name: close
# Purpose: call back to destroy page
# Parameters: none
# Returns: nothing
sub close
{
  my $w = shift;

  return unless $w->validate_last;

  # data have been changed; does the user want to save, discard, or cancel?
  if ( $w->changed_data  == 1)
  {
    my $dialog = $w->Dialog( -text => 'There are unsaved changes.
Please verify grades before closing.',
			     -bitmap => 'warning',
			     -default_button => 'Save',
			     -buttons => [ qw/ Save Discard Cancel / ] );
    my $answer = $dialog->Show;

    'Cancel' eq $answer && return;

    # save data and fall through to close
    'Save' eq $answer && $w->save;
  } elsif ( $w->changed_data  == 9)  {
    my $dialog = $w->Dialog( -text => 'Please verify the correct set of grades is displayed.',
			     -bitmap => 'warning',
			     -default_button => 'Ok',
			     -buttons => [ qw/ Ok Cancel / ] );
    my $answer = $dialog->Show;

    'Cancel' eq $answer && return;
   }

  defined $w->cget( '-closecmd' ) ? $w->Callback( '-closecmd' ) : $w->destroy;
}
sub fmt {
  my($fmt,$val)=@_;

  #local $SIG{__WARN__} = sub { print( Carp::longmess (shift) ); };
  if (defined $val) {
    $val = sprintf($fmt,$val);
  }
  return $val;
}


## Internal Utility ##
# Name: validate_last
# Purpose: validate the last widget before closing or saving
# Parameters: none
# Returns: true if validation succeeded
sub validate_last {
  my $w = shift;
  # We need to validate the last widget to have focus before closing or
  # saving  because the entry
  # widgets validate only when you leave the widget and pressing the
  # button does not suffice for changing the focus.

  my $last_focus = $w->focusCurrent;

  $last_focus->can('validate') ? $last_focus->validate : 1;
}

1;

__END__

=head1 NAME

EditPage - This widget populates a proposal page with the data from a proposal
and provides edit acces to the data.

=head1 VERSION

$Revision: 1.83 $

=head1 SYNOPSIS

   require EditPage;
   my $self = EditNotebook->new( $mw, $quit );
   my $page = $self->nb->add($prop_id, -label => "$prop_id\n$pi");

   $page->EditPage( -panel	=> $panel,
 		    -prop_id	=> $prop_id,
                    -verbose    => $verbosity,
	 	    -savecmd	=> [savePage  => ( $self, $prop_id, $panel )],
		    -closecmd	=> [ delPage  => ( $self, $prop_id ) ],
		    -changecmd	=> [ updLabel => ( $self, $prop_id ) ],
		    -switchcmd	=> [ switchcmd => ( $self, $prop_id, $panel ) ],
		   )->pack(-expand => 1,  -fill => 'both');

=head1 DESCRIPTION

EditPage - Provides a convenient interface for updating information for
proposals.

=head1 PUBLIC METHODS

=head2 Populate($w, $args)

Populates the notebook page with proposal data and widgets for editing the data

=over 

=item $w - the notebook page

=item $args - reference to hash of arguments

   -panel: a panel object
   -prop_id: proposal id
   -verbosity: level of verbosity

=back

=head1 PRIVATE METHODS

=head2 prop_info

Populates the general proposal information section of the page.

=head2 targets

Populates the target information section of the page.

=head2 abstract

Populates the abstract section of the page.

=head2 comments

Populates the comments section of the page.

=head2 grades

Populates the grades section of the page.

=head2 editComments

Provides edit capabilities to comments section.

=head2 fill_rl_grid

Populates a grid with the first column right justified and the second column
left justified

=head2 init_field

Populated field hash to describe a field

=head2 myEntry

Entry widget retains focus until the data passes validation

=head2 validate

Checks the validity of data in a myEntry widget

=head2 mkFrame

Returns a frame

=head2 mkCheckButton

Returns a check button

=head2 mkTargButton

Returns a check button for turning targets on and off

=head2 mkTooButton

Returns a check button for turning too targets on and off

=head2 track_changes

Tracks if changes have been made to the proposal object

=head2 reset

Resets the values in the widget to the original proposal object values

=head2 save

Saves all changes to the proposal object and the database

=head2 changed_data

Returns true if any data have been changed

=head2 close

Call back to destroy page

=head2 validate_last

Returns true if validation succeeded

=head1 DEPENDENCIES

This module uses TK::Dialog, Tk::Pane, Tk::LabEntry,
and Regexp::Common (available from CPAN)

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
