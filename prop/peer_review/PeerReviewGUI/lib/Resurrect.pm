#
# Resurrect.pm - This is a widget for resurrecting triaged proposals
#
# Purpose: Provides a convenient interface for adding traiged proposals back
#          into the panel for discussion
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Resurrect;

use strict;
use warnings;

use strict;
use Carp;
use config;
use Data::Dumper;
use base qw/ Tk::Frame /;
require Tk::Dialog;
require Tk::Pane;

Construct Tk::Widget 'Resurrect';

use vars qw($view @sorted_props %search_cols $search_col $search_str $verbose
	$searchResWidget $searchResBG $searchFound);

## Class Method ##
# Name: Populate
# Purpose: populate the widget with panel information
# Parameters: top level widget
#             -panel - a panel object
#             -verbose - level of verbosity
# Returns: nothing
sub Populate {
  my ($w, $args) = @_;
  
  croak( "must specify panel" )
    unless exists $args->{-panel};
  
  my $obj = $args->{-panel};
  $verbose = $args->{-verbose};
  $searchFound = $args->{-searchFound};
  croak ( "-panel must be a Panel object" )
    unless UNIVERSAL::isa( $obj, 'Panel');
  
  $w->ConfigSpecs( -panel => [ 'PASSIVE' ],
		   -verbose => [ 'PASSIVE' ],
		   -searchFound => [ 'PASSIVE' ],
		   -closecmd => [ 'CALLBACK' ],
  		   -updviewmenucmd => [ 'CALLBACK' ],
		   -changecmd => [ 'CALLBACK' ],
		 );
  
  $w->SUPER::Populate( $args );
  
  my $pData = $w->privateData;
  $pData->{chgData} = {};
  my $data = $pData->{propData} = {};
  my $action = $pData->{actData} = {};
  $w->reset( $obj );
  
  print "Resurrect::Populate - populating widget\n" if $verbose > 2;  
  # These are the buttons to Save/Close/Reset
  my $button_frame = $w->Frame()->pack;
  my $save = $button_frame->Button( -text => 'Save'  , 
				    -command => [ save  => $w  ] 
				  )->pack(-side => 'left',
					  -expand => 1);
  my $close = $button_frame->Button( -text => 'Close',  
				     -command => [ close => $w ] 
				   )->pack(-side => 'left',
					   -expand => 1);
  my $reset = $button_frame->Button( -text => 'Reset' , 
				     -command => [ reset => $w ] 
				   )->pack(-side => 'left',
					   -expand => 1);
  
  $w->mkView($obj);
}

## Internal Utility ##
# Name: mkView
# Purpose: creates a table with proposal information and checkboxes
# Parameters: panel object
# Returns: nothing
sub mkView {
  my $w = shift;
  my $obj = shift;
  my $pData = $w->privateData;
  my $chg = $pData->{chgData};
  my $data = $pData->{propData};
  my $action = $pData->{actData};

  $view->destroy if Tk::Exists($view);
  $view = $w->Frame();

  # This is the "search the table" section
  my $find_frame = $view->Frame(-label => 'Search For',
				-relief => 'ridge',
				-borderwidth => 2
			       )->pack(-side => 'top');
  # Header Row
  my $row = 1;
  my @headers = ('Action', 'Prop#', 'Triage','Type', 'SubType',
		 'PGrade Avg', 'PGrade Stdev', 'PI');
  
  my $table = new BuildTable($verbose, $view, \@headers,2, 'sw', 20);
  $table->print_headers($row, $obj, \&mkView, $w);

  my %gslist = $obj->get_group_list('', 'prop_id');
  foreach my $group (sort keys %gslist) {
    my $sorted_list = $gslist{$group};
    my %sorted_list = %$sorted_list;
    foreach my $prop_order (sort {$a <=> $b} keys %$sorted_list) {
      my $prop_id = $sorted_list->{$prop_order};
      
      # this will allow us to skip to the row if a find is done
      push @sorted_props, $prop_id;

      $row++;
      
      my $label = $table->mk_pack_frame();
      $label->Label(-textvariable => $obj->proposals->proposal($prop_id)->get_ref('triage'),
		    -anchor => 'w'
		   )->pack(-side => 'top');


      my $big_proj = $obj->proposals->proposal($prop_id)->big_proj;
      my $pstatus =  $obj->proposals->proposal($prop_id)->prop_status;
      # Don't need a button for big projects - they can't be triaged (unless
      # it is panel 99, the BPP)
      # $cb will be a label for big projects and a check button for the rest
      my $cb = $table->mk_pack_frame();
      if ($pstatus =~ /$STAT_BPP/) {
	$cb->Label(-textvariable =>\$action->{$prop_id},
		   -anchor => 'w'
		  )->pack(-side => 'top');
	
      } else {
	$cb->Checkbutton(-textvariable => \$action->{$prop_id},
			 -variable => \$data->{$prop_id},
			 -anchor => 'w',
			 -command => sub {
			   $chg->{$prop_id} = 
			     $data->{$prop_id};
			 }
			)->pack(-expand => 1,
			        -fill => 'x');
      }
      
      $table->print_object($row, 1, $cb);
      $table->print_field($row, 2, 
			  $obj->proposals->proposal($prop_id)->prop_id,
			  'w');
      $table->print_object($row, 3, $label);
      $table->print_field($row, 4, 
			  $obj->proposals->proposal($prop_id)->type,
			  'w');
      $table->print_field($row, 5, $big_proj,'center');
      $table->print_field($row, 6, 
			  $obj->proposals->proposal($prop_id)->pg_avg,
			  'w');
      $table->print_field($row, 7, 
			  $obj->proposals->proposal($prop_id)->pg_stdev,
			  'w');
      $table->print_field($row, 8, 
			  $obj->proposals->proposal($prop_id)->last_name,
			  'w');
    }
  }
  $table->table->configure(-takefocus => 1);
  $table->table->bind( '<Enter>' => 'focus' );
  $table->pack(-fill => 'both', -expand => 1);
  $table->update;

  $w->pop_findframe($obj, $find_frame, $table);

  $view->pack(-expand => 1,
	      -fill => 'both');
  $view->update;
}


## Internal Utility ##
# Name: pop_findframe
# Purpose: populate the frame which allows 'Find'
# Usage: pop_findframe($find_frame)
# Returns: nothing
sub pop_findframe {
  my $w = shift;
  my $obj = shift;
  my $frame = shift;
  my $table = shift;

  my %search_cols = ('Prop#' => {idx => 2,
				 name => 'Prop#',
				 att => 'prop_id'},
		     Triage => {idx => 3,
				name => 'Triage',
				att => 'triage'},
		     'Type' => {idx => 4,
				name => 'Type',
				att => 'type'},
		     'SubType' => {idx => 5,
				  name => 'SubType',
				  att => 'big_proj'},
		     PI => {idx => 8,
			     name => 'PI',
			     att => 'last_name'},
		    );
  my @cols = ('Prop#', 'Triage', 'SubType','PI');
  $search_col = $cols[0];
  $searchResWidget=0;

  $frame->Optionmenu(-textvariable => \$search_col,
		     -options => [@cols],
		     -command => [sub {$search_str = '';}],
		    )->pack(-side => 'left');
  $frame->Entry(-textvariable => \$search_str,
		-width => 15,
		-validate => 'key',
		-validatecommand => sub {
		  my ($row, $col) = $w->get_str_psn($_[0], \%search_cols, 
						    $search_col, 
						    \@sorted_props, $obj);
                 if ($searchResWidget > 0) {
                      $searchResWidget->configure(-background=> $searchResBG);
                  }
                  my($xx) = $row;
                  if ($xx < 8) {$xx=4; }
                  $table->table->see($xx, $col);

                  if ($row > 1) {
                    my($duh) = $table->table->get(($row),$col);
                    my @kids = $duh->children;
                    while ($kids[0] =~ /Frame/) {
                      @kids = $kids[0]->children;
                    }
                    for (my $kk=0; $kk<=0;$kk++) {
                      my($kid) = $kids[$kk];
                      #print STDERR "searching kid $kid\n";
                      $searchResWidget = $kid;
                      $searchResBG = $kid->cget(-background);
                      $kid->configure(-background => $searchFound);
                    }
                  }
		  1;
		  },
	       )->pack(-side => 'left');
}

## Internal Utility ##
# Name: get_str_psn
# Purpose: Call back to find row and column of search string and move
#          scrollbars there during the validation of a search string
# Returns: true
sub get_str_psn {
  my $w = shift;
  my $string = shift;
  my $col_def = shift;
  my $col_name = shift;
  my $list = shift;
  my $obj = shift;

  my $row = 1;
  my $found = 0;
  foreach my $prop_id (@$list)  {
    $row++;
    my $compare = $obj->proposals->
      proposal($prop_id)->proposal($$col_def{$col_name}{att});

      my $lc_str = lc($string);
      my $lc_substr = lc($compare); 
      $found = 1 if $lc_substr =~ /$lc_str/;
      last if $lc_substr =~ /$lc_str/;
  }

  if (!$found) {
    $w->messageBox(-title => 'PR: Warning',
		   -message => "$string not found in column $col_name",
		   -type => 'OK',
		   -icon => 'warning') if $string;
    $row = 1;
  }

  my $col = $$col_def{$col_name}{idx};
  return $row, $col;
}

## Internal Utility ##
# Name: reset
# Purpose: reset the values in the widget to the original values
# Parameters: panel object
# Returns: nothing
sub reset
{
    my $w = shift;
    
    my $obj = shift || $w->cget('-panel');
    my $pData = $w->privateData;
    my $data = $pData->{propData};
    my $chg = $pData->{chgData};
    my $action = $pData->{actData};
    
    my $props = $obj->proposals->list;
    
    # We're setting the data to N because we want to start with the 
    # resurrect checkbuttons turned off
    foreach my $prop_id (keys %$props){
	my $proposal = $$props{$prop_id};
	    $data->{$prop_id} = 0;
	    $chg->{$prop_id} = 0;
	my $big_proj = $obj->proposals->proposal($prop_id)->big_proj;
        my $pstatus =  $obj->proposals->proposal($prop_id)->prop_status;
	
	# Can't triage large projects (unless it is panel 99, the BPP)
        if ($pstatus =~ /$STAT_BPP/) {
	  $action->{$prop_id} = 'N/A';
	} else {
	  $action->{$prop_id} = 'Triage' if $proposal->triage =~ /R/;
	  $action->{$prop_id} = 'Resurrect' if $proposal->triage =~ /Y/;
	  $action->{$prop_id} = 'Triage' if $proposal->triage =~ /N/;
	  $action->{$prop_id} = 'Resurrect' if $proposal->triage =~ /A/;
	}
    }

   $w->Callback( '-changecmd', $w->changed_data );

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
    my $props = $data;
    foreach my $prop_id (keys %$props){
	$changed += $chg->{$prop_id};
    }
    $changed = 1 if $changed > 0;
    return $changed;
}

## Internal Utility ##
# Name: save
# Purpose: save all changes to panel object and database
# Parameters: none
# Returns: nothing
sub save
{
  my $w = shift;
  
  my $obj = $w->cget('-panel');
  my $pData = $w->privateData;
  my $data = $pData->{propData};
  my $chg = $pData->{chgData};
  my $action = $pData->{actData};
  
  foreach my $prop_id (keys %$chg) {
    if ($chg->{$prop_id} > 0) {
      my $triage = $obj->proposals->proposal($prop_id)->triage;
      my $type = $obj->proposals->proposal($prop_id)->type;
      my $big_proj = $obj->proposals->proposal($prop_id)->big_proj;
      my $numTargs = $obj->proposals->proposal($prop_id)->num_targets;
      my $status;
      
      if ($triage =~ /Y/) {
	# This is a resurrection
	# triage is set to R; all statuses are set to Y;
	# final grades are nulled and fg_avg and
	# normalized grade set to 0
	$obj->proposals->proposal($prop_id)->save_member('triage', 'R');
	$obj->proposals->proposal($prop_id)->save_member('triage_sort', 1);
	$action->{$prop_id} = 'Triage';
	$obj->proposals->proposal($prop_id)->clear_grades();
      } 
      elsif ($triage =~ /R/) {
	# This a triage
	# triage is set Y; all statuses are set to N;
	# fg_avg, fg_1 are set to pg_avg
	$obj->proposals->proposal($prop_id)->save_member('triage', 'Y');
	$obj->proposals->proposal($prop_id)->save_member('triage_sort', 2);
	$action->{$prop_id} = 'Resurrect';
	$obj->proposals->proposal($prop_id)->clear_grades();
	$obj->proposals->proposal($prop_id)->
	  save_member('g1', $obj->proposals->proposal($prop_id)->pg_avg);
	$obj->proposals->proposal($prop_id)->
	  save_member('fg_avg', $obj->proposals->proposal($prop_id)->pg_avg);
      } 
      elsif ($triage =~ /N/){
	# This a triage of a proposal not in the bottom quartile
	# triage is set to A; all statuses are set to N;
	# fg_avg, fg_1 are set to pg_avg
	$obj->proposals->proposal($prop_id)->save_member('triage', 'A');
	$obj->proposals->proposal($prop_id)->save_member('triage_sort', 2);
	$action->{$prop_id} = 'Resurrect';
	$obj->proposals->proposal($prop_id)->clear_grades();
	$obj->proposals->proposal($prop_id)->
	    save_member('g1', $obj->proposals->proposal($prop_id)->pg_avg);
	$obj->proposals->proposal($prop_id)->
	    save_member('fg_avg', $obj->proposals->proposal($prop_id)->pg_avg);
      } 
      elsif ($triage =~ /A/){
	# This is a resurrection of a proposal not in the bottom quartile
	# triage is set to N; all statuses are set to Y;
	# final grades are nulled and fg_avg and
	# normalized grade set to 0
	$obj->proposals->proposal($prop_id)->save_member('triage', 'N');
	$obj->proposals->proposal($prop_id)->save_member('triage_sort', 1);
	$action->{$prop_id} = 'Triage';
	$obj->proposals->proposal($prop_id)->clear_grades();
      }

      $status = 'Y' if $triage =~ /Y/;
      $status = 'Y' if $triage =~ /A/;
      $status = 'N' if $triage =~ /R/;
      $status = 'N' if $triage =~ /N/;
      $obj->proposals->proposal($prop_id)->save_member('prop_status', $status);
    }
  }
  $obj->set_grades;
  $obj->set_lp_grades if ($obj->panel_id == 99);
  $obj->set_vlp_grades if ($obj->panel_id == 99);
  $obj->set_xvp_grades if ($obj->panel_id == 99);
  $obj->update_stats;
  $obj->set_running_totals;
  $w->reset;    # reset the private data and change values
  $w->Callback( '-changecmd', $w->changed_data );
  $w->Callback( '-updviewmenucmd');
}


## Internal Utility ##
# Name: close
# Purpose: call back to destroy window
# Parameters: none
# Returns: nothing
sub close
{
    my $w = shift;
    
    # data have been changed; does the user want to save, discard, or cancel?
    if ( $w->changed_data )
    {
	my $dialog = $w->Dialog( -text => 'There are unsaved changes',
				 -bitmap => 'warning',
				 -default_button => 'Save',
				 -buttons => [ qw/ Save Discard Cancel / ] );
	my $answer = $dialog->Show;
	
	'Cancel' eq $answer && return;
	
	# save data and fall through to close
	'Save' eq $answer && $w->save;
    }
    
    defined $w->cget( '-closecmd' ) ? $w->Callback( '-closecmd' ) : $w->destroy;
}

1;

__END__

=head1 NAME

Resurrect - This is a widget for resurrecting triaged proposals

=head1 VERSION

$Revision: 1.18 $

=head1 SYNOPSIS

   use Resurrect;
   my $resurrect = $mw->Toplevel();
   $resurrect->Resurrect(-panel => $panel,
                         -verbose => $verbosity,
	                 -closecmd => [ \&closeGroup => ($panel) ],
		         -changecmd => [ \&updGroup => ($panel) ],
		         )->pack(-expand => 1,
			         -fill => 'both');

=head1 DESCRIPTION

Provides a convenient interface for resurrecting triaged proposals for 
discussion in the panel

=head1 PUBLIC METHODS

=head2 Populate($w, $args)

Populates the widget

=over 

=item $w - toplevel widget

=item $args - reference to hash of arguments

   -panel: a panel object

   -verbose: level of verbosity

=back

=head1 PRIVATE METHODS

-head2 mkView

Creates a table with proposal information and checkboxes

=head2 pop_findframe

Populates the frame which allows 'Find'

=head2 get_str_posn

Finds the row and column of a search string and moves scrollbars there
during the validation of a search string.

=head2 reset

Resets the values in the widget to the original proposal object values

=head2 save

Saves all changes to the proposal object and the database

=head2 changed_data

Returns true if any data have been changed

=head2 close

Call back to destroy page

=head1 DEPENDENCIES

This module uses TK::Dialog, Tk::Pane (available from CPAN)

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
