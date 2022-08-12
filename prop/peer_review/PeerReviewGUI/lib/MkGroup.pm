
# MkGroup.pm - This is a widget for assigning proposals to groups
#
# Purpose: Provides a convenient interface for putting proposals into
#          groups for discussion
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package MkGroup;

use strict;
use Carp;
use Data::Dumper;
use base qw/ Tk::Frame /;
require Tk::Dialog;
require Tk::Pane;
require Tk::ViewTable;
require Tk::Optionmenu;

Construct Tk::Widget 'MkGroup';

use vars qw($view $tally $lists $verbose
	    @sorted_props %search_cols $search_col $search_str
		$searchGrpWidget $searchGrpBG $searchFound);

## Class Method ##
# Name: Populate
# Purpose: populate the widget with panel information and widgets
#          for assigning proposals to groups
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
  
  $w->ConfigSpecs(-panel => [ 'PASSIVE' ],
		  -verbose => [ 'PASSIVE' ],
		  -searchFound => [ 'PASSIVE' ],
		  -closecmd => [ 'CALLBACK' ],
		  -changecmd => [ 'CALLBACK' ],
		 );
  
  $w->SUPER::Populate( $args );
  
  my $pData = $w->privateData;
  $pData->{chgData} = {};
  my $data = $pData->{propData} = {};
  my $grp_tally = $pData->{grp_tally} = {};
  my $grp_lists = $pData->{grp_lists} = {};
  my $groups = $pData->{groups} = $obj->groups;
  $w->reset( $obj );

  print "MkGroup::Populate - populating widget\n" if $verbose > 2; 
  # These are the buttons to Save/Close/Reset
  my $button_frame = $w->Frame(-relief => 'groove',
			       -borderwidth => 3,
			      )->pack;
  my $save = $button_frame->Button( -text => 'Save'  , 
				    -command => [ save  => $w  ] 
				  )->pack(-side => 'left',
					  -expand => 1);
  my $close = $button_frame->Button( -text => 'Close',  
				     -command => [ close => $w ] 
				   )->pack(-side => 'left',
					   -expand => 1);
  my $reset = $button_frame->Button( -text => 'Reset' , 
				     -command => sub {
				       if ($w->changed_data) {
					 my $dialog = 
					   $w->Dialog(-text => 'Do you really want to reset?',
						      -bitmap => 'question',
						      -default_button => 'Yes',
						      -buttons => [qw /Yes No/ ]
						     );
					 my $answer = $dialog->Show;
					 'No' eq $answer && return;
					 'Yes' eq $answer && $w->reset;
				       }
				       else {
					 $w->reset;
				       }
				     }
				   )->pack(-side => 'left',
					   -expand => 1);

  # This frame holds the new group and tally sections
  my $grp_tally_fr = $w->Frame()->pack(-side => 'top',
				       -expand => 1,
				       -fill => 'both');

  # This is the "make a new group" section in the grp/tally section
  my $grp_frame = $grp_tally_fr->Frame(-relief => 'groove',
				       -borderwidth => 3
				      )->pack(-side => 'left',
					      -expand => 1,
					      -fill => 'both');

  $grp_frame->Button(-text => 'Create Group',
		     -command => [ create_grp => ($w, $obj, $grp_tally_fr) ]
		    )->pack(-side => 'top',
			    -expand => 1,
			    -fill => 'both');

  my $grp_lb = $grp_frame->Scrolled('Listbox',
				    -scrollbars => 'oe',
				    -height => 5,
				    -relief => 'ridge',
				    -borderwidth => 2,
				    -activestyle => 'none',
				   )->pack(-side => 'top',
					   -expand => 1,
					   -fill => 'both');
  $grp_lb->insert('end', $groups->{$_}) foreach sort keys %$groups;
  $pData->{grp_lb} = $grp_lb;
  $grp_frame->Button(-text => 'Edit Groups',
		     -command => [ edit_grp_list => ($w, $obj) ]
		    )->pack(-side => 'top',
			    -expand => 1,
			    -fill => 'both');

  # This is the tally portion of the group/tally section
  my $tally_fr = $grp_tally_fr->Frame(-label => 'Assignment Summaries',
				      -relief => 'groove',
				      -borderwidth => 3,
				     )->pack(-side => 'left',
					     -expand => 1,
					     -fill => 'both');


  $w->popTally($tally_fr);
  $w->popLists($obj, $tally_fr);

  $w->mkView($obj);
}

## Internal Utility ##
# Name: popTally
# Purpose: populates the tally frame
# Parameters: tally_frame, panel object
# Returns: nothing
sub popTally {
  my $w = shift;
  my $parent = shift;
  my $obj = $w->cget('-panel');
  my $pData = $w->privateData;
  my $data = $pData->{propData};
  my $grp_tally = $pData->{grp_tally};
  my $groups = $pData->{groups};
  if (Tk::Exists($tally)) {
    $parent = $tally->parent;
    $tally->destroy;
  }

  $tally = $parent->Frame(-relief => 'ridge',
			 -borderwidth => 2,
			)->pack(-side => 'left');
  $tally->Label(-text => 'Group Tally')->pack;
  my $tally_tbl = new BuildTable($verbose, $tally, 
				 ['Group', 'Tally'],
				 2,'w',
				 7);
  my $trow = 1;
  $tally_tbl->print_headers($trow, $obj, \&popTally, $w);
  foreach my $grp (sort keys %$grp_tally) {
    $trow++;
    $tally_tbl->print_field($trow, 1, $grp, 'w');
    my $var_label = $tally_tbl->table()->
      Label(-textvariable => \$grp_tally->{$grp},
	    -relief => 'ridge',
	    -borderwidth => 2
	   )->pack();
    $tally_tbl->print_object($trow, 2, $var_label, 'w');
  }

  $tally_tbl->pack(-fill => 'both', -expand => 1);
  $tally_tbl->update;

  $tally->pack(-expand => 1,
	       -fill => 'both');
  $tally->update;
}

## Internal Utility ##
# Name: popLists
# Purpose: populates the lists frame
# Parameters: panel object
# Returns: nothing
sub popLists {
  my $w = shift;
  my $obj = shift;
  my $parent = shift;
  my $pData = $w->privateData;
  my $data = $pData->{propData};
  my $grp_lists = $pData->{grp_lists};
  my $groups = $pData->{groups};
  if (Tk::Exists($lists)) {
    $parent = $lists->parent;
    $lists->destroy;
  }

  $lists = $parent->Frame(-relief => 'ridge',
			  -borderwidth => 2,
			 )->pack(-side => 'left');
  $lists->Label(-text => 'Group Assignments') ->pack;
  my $grp_tbl = new BuildTable($verbose, $lists, ['Group', 'PropId'], 2,'w', 7);
  my $grow = 1;
  $grp_tbl->print_headers($grow, $obj, \&popLists, $w);
  foreach my $grp (sort keys %$grp_lists) {
    my $props = $grp_lists->{$grp};
    foreach my $prop (sort keys %$props) {
      $grow++;
      $grp_tbl->print_field($grow, 1, $grp, 'w');
      $grp_tbl->print_field($grow, 2, $prop, 'w');
    }
  }
  $grp_tbl->pack(-fill => 'both', -expand => 1);
  $grp_tbl->update;

  $lists->pack(-expand => 1,
	       -fill => 'both');
  $lists->update;
}

## Internal Utility ##
# Name: mkView
# Purpose: creates a table with proposal information and group selection 
#          widgets
# Parameters: panel object
# Returns: nothing
sub mkView {
  my $w = shift;
  my $obj = shift;
  my $pData = $w->privateData;
  my $data = $pData->{propData};
  my $groups = $pData->{groups};
  $view->destroy if Tk::Exists($view);
  $view = $w->Frame();


  
  # This is the "search the table" section
  my $find_frame = $view->Frame(-label => 'Search For',
				-relief => 'ridge',
				-borderwidth => 2
			       )->pack(-side => 'top');


  # Header Row
  my $row = 1;
  my @headers = ('Prop#', 'Group', 'Cat', 'Type', 'PI', 'Triage');
  
  my $table = new BuildTable($verbose, $view, \@headers, 2,'w', 10);
  $table->print_headers($row, $obj, \&mkView, $w);
  
  my %gslist = $obj->get_group_list('','prop_id', 'N', 'N', 'N');
  foreach my $group (sort keys %gslist) {
    my $sorted_list = $gslist{$group};
    my %sorted_list = %$sorted_list;
    foreach my $prop_order (sort {$a <=> $b} keys %$sorted_list) {
      my $prop_id = $sorted_list->{$prop_order};
      # this will allow us to skip to the row if a find is done
      push @sorted_props, $prop_id;

      $row++;

      # It seems like Optionmenu would be nicer here than the Radiobuttons,
      # but I can't figure out how to get the Optionmenu to display the 
      # current value of the menu.  Implementing a native_optionmenu seems
      # like a possibility for later.

      my $group_pane = $table->table()->
	Scrolled('Pane',
		 -scrollbars => 'oe',
		 -relief => 'ridge',
		 -borderwidth => 2,
		 -height => 70,
		 -sticky => 'nsew',
		)->pack(-side => 'top',
			-expand => 1,
			-fill => 'both');
      foreach my $idx (sort {$a <=> $b} keys %$groups) {
	$group_pane->Radiobutton(-text => $groups->{$idx},
				  -value => $idx,
				  -variable => \$data->{$prop_id},
				  -anchor => 'w',
				  -command => sub {
				    $w->track_changes($obj, $prop_id);
				    return (1);
				  }
				 )->pack(-side => 'top',
					 -expand => 1,
					 -fill => 'both');
      }
						   
      $table->print_field($row, 1, 
			  $obj->proposals->proposal($prop_id)->prop_id,
			  'w');
      $table->print_object($row, 2, $group_pane);
      $table->print_field($row, 3, 
			  $obj->proposals->proposal($prop_id)->category,
			  'w',);
      $table->print_field($row, 4, 
			  $obj->proposals->proposal($prop_id)->type,
			  'w',);
      $table->print_field($row, 5, 
			  $obj->proposals->proposal($prop_id)->last_name,
			  'w',);
      $table->print_field($row, 6, 
			  $obj->proposals->proposal($prop_id)->triage,
			  'w',);
    }
  }
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

  my %search_cols = ('Prop#' => {idx => 1,
			       name => 'Prop#',
			       att => 'prop_id'},
		     Group => {idx => 2,
			       name => 'Group',
			       att => 'group_id'},
		     Cat => {idx => 3,
			     name => 'Cat',
			     att => 'category'},
		     Type => {idx => 4,
			      name => 'Type',
			      att => 'type'},
		     PI => {idx => 5,
			    name => 'PI',
			    att => 'last_name'},
		     Triage => {idx => 6,
				name => 'Triage',
				att => 'triage'},
		    );
  my @cols = ('Prop#', 'Cat', 'Type', 'PI', 'Triage');
  $search_col = $cols[0];
  $searchGrpWidget=0;
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
                 if ($searchGrpWidget > 0) {
                      $searchGrpWidget->configure(-background=> $searchGrpBG);
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
                      $searchGrpWidget = $kid;
                      $searchGrpBG = $kid->cget(-background);
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
# Name: create_grp
# Purpose: callback to add a group to the group list
# Parameters: none
# Returns: nothing
sub create_grp {
  my $w = shift;
  my $obj = shift;
  my $grp_tally_fr = shift;
  my $pData = $w->privateData;
  my $groups = $pData->{groups};
  my $grp_tally = $pData->{grp_tally};
  my $grp_lists = $pData->{grp_lists};
  my $grp_lb = $pData->{grp_lb};

  my $entry;
  my $d = $w->DialogBox(-title => 'PR: Create a Group',
			-buttons => ["Add Group", "Cancel"]);
  $d->add( 'LabEntry',
	   -label => 'New Group',
	   -textvariable => \$entry,
	   -width => 15,
	   -labelPack => [-side => 'left'],
	   )->pack;

  my $answer = $d->Show();

  if ($answer eq 'Add Group') {
    my $exists = 0;
    foreach my $num (keys %$groups) {
      $exists = 1 if $groups->{$num} eq $entry;
    }
    if ($exists) {
      my $message = "Group $entry already exists";
      $w->messageBox(-title => 'PR: Warning',
		     -message => $message,
		     -type => 'OK',
		     -icon => 'warning');
    } elsif ($entry eq ""){
      my $message = "Group name cannot be null";
      $w->messageBox(-title => 'PR: Warning',
		     -message => $message,
		     -type => 'OK',
		     -icon => 'warning');
    }
    else {
      $entry = sprintf "%0.50s", $entry;
      my @nums = sort {$a <=> $b} keys %$groups;
      my $max = pop @nums;
      # Insert the new group into the groups table
      $obj->new_group($entry);
      $pData->{groups} = $obj->groups;
      $w->reset($obj);

      $grp_tally->{$entry} = 0;
      $grp_lists->{$entry} = {};
      $grp_lb->insert('end', $entry);

      $w->popTally($obj);
      $w->popLists();
      $w->mkView($obj);
    }	   
  }
}

## Internal Utility ##
# Name: edit_grp_list
# Purpose: callback to edit the group list
# Parameters: panel object
# Returns: nothing
sub edit_grp_list {
  my $w = shift;
  my $obj = shift;
  my $pData = $w->privateData;
  my $groups = $pData->{groups};
  my $grp_lb = $pData->{grp_lb};

  my $d = $w->DialogBox(-title => 'PR: Edit Groups',
			-buttons => ["OK", "Cancel"]);

  foreach my $idx (sort {$a <=> $b} keys %$groups) {
    $d->add('Button',
	    -textvariable => \$groups->{$idx},
	    -command => sub {
	      $w->edit_grp($d, $obj, $idx);
	    },
	   )->pack if $groups->{$idx} !~ /unassigned/;
  }
  my $answer = $d->Show;
  if ($answer eq 'OK') {
    $w->mkView($obj);
    $w->track_changes($obj);
  }
}


## Internal Utility ##
# Name: edit_grp
# Purpose: callback to edit a group
# Parameters: group index
#             button frame
# Returns: nothing
sub edit_grp {
  my $w = shift;
  my $dialog = shift;
  my $obj = shift;
  my $idx = shift;
  my $pData = $w->privateData;
  my $groups = $pData->{groups};
  my $grp_lb = $pData->{grp_lb};
  my $grp_tally = $pData->{grp_tally};
  my $grp_lists = $pData->{grp_lists};

  
  my $title = "PR: Edit " . $groups->{$idx};

  my $ed = $w->DialogBox(-title => $title,
			 -takefocus => 1,
			 -buttons => ['Delete', 'Save', 'Cancel'],
			 -default_button => 'Cancel');
  my $name = $groups->{$idx};
  $ed->add('Entry',
	   -width => 15,
	   -textvariable => \$name,
	   -takefocus => 1,
	   -validate => 'all'
	  )->pack;
  $ed->focusForce;  # I don't know why I need to force focus here, but it
                    # seems to be the only way I can get focus into the 
                    # entry widget without first moving the mouse out of the
                    # DialogBox
  my $answer = $ed->Show;

  if ($answer eq 'Delete') {
    # only delete from hash if no proposals have been assigned to the group
    if (!$w->props_in_group($idx)) {
      $obj->delete_group($name);
      $pData->{groups} = $obj->groups;
      $groups = $pData->{groups};
      $w->reset($obj);

      $w->mkView($obj);
      $w->track_changes($obj);
      $dialog->destroy if Tk::Exists($dialog);
    }
    else {
      my $message = 'Cannot delete group.  There are proposals assigned to ' .
	$groups->{$idx};
      $w->messageBox(-title => 'PR: Warning',
		     -message => $message,
		     -type => 'OK',
		     -icon => 'warning');
    }
  }
  if ($answer eq 'Save') {
    $name = sprintf "%0.50s", $name;
    # don't allow a save if the same group with proposals assigned to it
    # already exists
    my $exists = 0;
    foreach my $group (values %$groups) {
      $exists++ if $group eq $name;
    }
    if ($exists) {
      my $message = "Group $name already exists.";
      $w->messageBox(-title => 'PR: Warning',
		     -message => $message,
		     -type => 'OK',
		     -icon => 'warning');
    } elsif ($name eq ""){
      my $message = "Group name cannot be null";
      $w->messageBox(-title => 'PR: Warning',
		     -message => $message,
		     -type => 'OK',
		     -icon => 'warning');
    }
    else {
      my $old_name = $groups->{$idx};
      my $count = $grp_tally->{$old_name};
      my $list = $grp_lists->{$old_name};

      $obj->update_group($old_name, $name);
      $pData->{groups} = $obj->groups;
      $groups = $pData->{groups};
      $w->reset($obj);

      $grp_tally->{$name} = $count;
      delete $grp_tally->{$old_name};
      $grp_lists->{$name} = $list;
      delete $grp_lists->{$old_name};
      $w->popTally();
      $w->popLists();
      $w->mkView($obj);
      $w->track_changes($obj);
      $dialog->destroy if Tk::Exists($dialog);
    }
  }
  $grp_lb->delete(0, 'end');
  $grp_lb->insert('end', $groups->{$_}) foreach sort {$a <=> $b} keys %$groups;
}

## Internal Utility ##
# Name: props_in_group
# Purpose: checks if any proposals are in the group
# Parameters: group index
# Returns: true if there are proposals in the group
sub props_in_group {
  my $w = shift;
  my $idx = shift;
  my $pData = $w->privateData;
  my $groups = $pData->{groups};
  my $data = $pData->{propData};
  my $count = 0;
  foreach my $propid (keys %$data) {
    $count = 1 if $data->{$propid} == $idx;
  }
  return $count;
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
    my $groups = $pData->{groups};
    my $grp_tally = $pData->{grp_tally};
    my $grp_lists = $pData->{grp_lists};

    foreach my $group (values %$groups) {
      $grp_tally->{$group} = 0;
      $grp_lists->{$group} = {};
    }
    
    my $props = $obj->proposals->list;
    foreach my $prop_id (keys %$props){
	my $proposal = $$props{$prop_id};
	my $idx = 0;
	my $grp;
	foreach my $grp_idx (keys %$groups) {
	  $grp = $groups->{$grp_idx};
	  $idx = $grp_idx if $groups->{$grp_idx} eq $proposal->group_id();
	  last if $idx;
	}

        if ($idx == 0) { $grp="";}

	$data->{$prop_id} = $idx;
	$grp_tally->{$grp} += 1;
	$grp_lists->{$grp}->{$prop_id} = 1;
	$chg->{$prop_id} = 0;
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
    foreach my $prop_id (keys %$data){
	$changed += $chg->{$prop_id};
    }
    $changed = 1 if $changed > 0;
    return $changed;
}

## Internal Utility ##
# Name: track_changes
# Purpose: track if changes are made to the data
# Parameters: none
# Returns: nothing
sub track_changes
{
  my ( $w, $obj, $prop_id) = @_;

  my $pData = $w->privateData;
  my $propData = $pData->{propData};
  my $chg = $pData->{chgData};
  my $groups = $pData->{groups};
  my $grp_tally = $pData->{grp_tally};
  my $grp_lists = $pData->{grp_lists};

  if ($prop_id) {  # This is used by the radio buttons
    my $data = $groups->{$propData->{$prop_id}};
    $data = '' if !$data;
    my $value = $obj->proposals->proposal($prop_id)->get('group_id');
    $value = '' if !$value;
    $pData->{chgData}{$prop_id} = $data ne $value;

    # put the proposal in the proper group list 
    foreach my $grp (keys %$grp_lists) {
      delete $grp_lists->{$grp}->{$prop_id};
    }
    $grp_lists->{$data}->{$prop_id} = 1;

    foreach my $grp (keys %$grp_tally) {
      my $list = $grp_lists->{$grp};
      $grp_tally->{$grp} = scalar keys %$list;
    }
    $w->popLists($obj);
  }
  else {  # This is used by the edit group list
    foreach my $prop_id(keys %$propData) {
      my $data = $groups->{$propData->{$prop_id}};
      $data = '' if !$data;
      my $value = $obj->proposals->proposal($prop_id)->get('group_id');
      $value = '' if !$value;
      $pData->{chgData}{$prop_id} = $data ne $value;
    }
  }
  $w->Callback( '-changecmd', $w->changed_data )
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
    my $groups = $pData->{groups};

    foreach my $prop_id (keys %$chg) {
      if ($chg->{$prop_id} > 0) {
	if ($data->{$prop_id} > 0) {
	  my $value = $groups->{$data->{$prop_id}};
	  print "MkGroup::save - saving $value to group_id for $prop_id\n" if 
	    $verbose > 1;
	  $obj->proposals->proposal($prop_id)->
	    save_member('group_id', $value);
	}
	else{
	  $w->Dialog(-title => 'Data Entry Error',
		     -text => 
		     "GroupId for $prop_id  must be > 0",
		     -buttons => 
		     ['Okay'],
		    )->Show();
	}
      }
    }
    $w->reset;    # reset the private data and change values
    $w->Callback( '-changecmd', $w->changed_data );
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

MkGroup - This is a widget for assigning proposals to groups

=head1 VERSION

$Revision: 1.16 $

=head1 SYNOPSIS

   use MkGroup;
   my $add_group = $mw->Toplevel();
   $add_group->MkGroup(-panel => $panel,
                       -verbose => $verbosity,
	               -closecmd => [ \&closeGroup => ($panel) ],
		       -changecmd => [ \&updGroup => ($panel) ],
		       )->pack(-expand => 1,
			       -fill => 'both');

=head1 DESCRIPTION

Provides a convenient interface for putting proposals into groups for 
discussion

=head1 PUBLIC METHODS

=head2 Populate($w, $args)

Populates the widget with panel information and widgets for assigning
proposals to groups

=over 

=item $w - toplevel widget

=item $args - reference to hash of arguments

   -panel: a panel object

   -verbose: level of verbosity

=back

=head1 PRIVATE METHODS

=head2 mkView

Creates a table with proposal information and group selection widgets

=head2 pop_findframe

Populates the frame which allows 'Find'

=head2 get_str_posn

Finds the row and column of a search string and moves scrollbars there
during the validation of a search string.

=head2 create_grp

Callback to add a group to the group list

=head2 edit_grp_list

Callback to edit the group list

=head2 edit_grp

Callback to edit a group

=head2 props_in_group

Returns true if there are proposals in the group

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
