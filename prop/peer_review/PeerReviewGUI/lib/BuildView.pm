#
# BuildView.pm - This is a widget for creating a new view
#
# Purpose: Provides a convenient interface for creating views.
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package BuildView;

use strict;
use warnings;

use Carp;
use Data::Dumper;
use base qw/ Tk::Frame /;
use Tk::widgets qw( LabEntry );
require Tk::Dialog;
use Tk::Pane;
use Tk::Table;
use Tk::EnumList;

use BuildTable;
use BVNotebook;

Construct Tk::Widget 'BuildView';

use vars qw($sections %section_types $section_count $table $section_frame
	    $dbh $panel_id $sec_lb $sec_ids $sec_names $sec_idx $sec_rb
	    $sec_convert @headers $verbose);


# The number preceding proposal and spreadsheet are there to prevent the
# radiobutton in the "Add Section" dialog from highlighting more than one 
# button at a time.  This leading digit must be removed before using the
# value elswhere in the application.
%section_types = ('1Proposal Columns Displayed as List' => "proposal\tproposal",
		  '2Proposal Columns Displayed as Spreadsheet' => 
		     "spreadsheet\tproposal",
		  '3Target Columns' => "target\ttarget",
		  '4All Grades' => "proposal\tgrades",
		  '4Prelim Grades' => "proposal\tprelimgrades",
		  '4Final Grades' => "proposal\tfinalgrades",
		  '5Cumulative Totals' => "spreadsheet\trun_tot",
		  '6Combination Columns' => "combo_cols\tcombo_cols",
		  '7Multi-Cycle Columns' => "multi_cols\tmulti_cols",
      '8Proposal Title' => "title\t",
      '9Proposal Short Title' => "short_ttl\t",
		  '10Proposal Abstract' => "abstract\t",
		 );

## Class Method ##
# Name: Populate
# Purpose: populate the widget
# Parameters: top level widget
#             -dbh - database handle
#             -panel_id - panel id
#             -verbose - level of verbosity
#             -previewcmd - call back for previewing the new view
#             -viewmenucmd - call back for adding new view to view menu
#             -closecmd - call back for closing widget
# Returns: nothing
sub Populate {
  my ($w, $args) = @_;
  
  $panel_id = $args->{-panel_id};
  $dbh = $args->{-dbh};
  $verbose = $args->{-verbose};
  $w->ConfigSpecs( -dbh => [ 'PASSIVE'],
		   -panel_id => [ 'PASSIVE' ],
		   -verbose => [ 'PASSIVE' ],
		   -previewcmd => [ 'CALLBACK' ],
		   -lcdviewmenucmd => [ 'CALLBACK' ],
		   -facviewmenucmd => [ 'CALLBACK' ],
		   -closecmd => [ 'CALLBACK' ],
		 );
  
  $w->SUPER::Populate( $args );
  
  # Initialize some variables
  $sections = {};
  $sec_ids = [];
  $sec_names = [];
  $sec_idx = [];
  $section_count = 1;
  $sec_rb = '';
  $sec_convert = '';

  print "BuildView::Populate - populating widget\n" if $verbose > 2;
  # All Views must have a prop_id section as the first section
  my %section = (section_name => "PropNum PI",
		 section_label => 'Proposal Button plus PI',
		 section_type => 'prop_id',
		 cols => { col_ids => [], 
			   col_idx => [],
			   col_names => []},
		 object => undef,
	       );
  $sections->{1} = \%section;
  $w->chooseCols(1);

  $section_frame = $w->Scrolled('Frame',
				-label => 'Sections in View',
				-scrollbars => 'ose',
				-takefocus => 0,);

  # This is the frame containing a table of sections
  
  # @headers is an ordered list of headers
  @headers = ('Id', 'Name', 'Type', 'Columns');
  
  $table = new BuildTable($verbose, $section_frame, \@headers,3, 'w', 5);
  BVNotebook->new($w, $sections, $table, $dbh, $panel_id, $verbose);

  my $row = 1;
  $table->print_headers($row);
  foreach my $sec (sort {$a <=> $b} keys %$sections) {
    $row++;
    $w->fillTable($row);
  }

  my $load_frame = $section_frame->Frame()->pack();
  $load_frame->Button(-text => 'Load Existing View',
		      -command => sub {
			my $dialog = $w->DialogBox(-title => 
						   'PR: Load Existing View',
						   -default_button => 'Load',
						   -buttons => 
						   [qw/Load/]);
			$w->popLoad($dialog);
		      },
		     )->pack(-side => 'left');
  $load_frame->Button(-text => 'Clear View',
		      -command => sub {
			$w->_init();

			# Clear the table
			$table->table()->clear;

			# Repopulate the table
			$row = 1;
			$section_count = 1;
			$table->print_headers($row);
			foreach my $sec (sort {$a <=> $b} keys %$sections) {
			  $row++;
			  $w->fillTable($row);
			}

			# Clear the sections list
			$sec_lb->delete(0, 'end');
		      }
		     )->pack(-side => 'left');

  # Here is where the final sorting and selecting of sections occurs
  my $sort_frame = $section_frame->Frame(-relief => 'groove',
					 -borderwidth => 2);
  my $sec_frame = $sort_frame->Frame()->grid(-row => 1,
					     -rowspan => 3,
					     -column => 2);
  $sec_frame->Label(-text => "Final Section List")->pack();

  $sec_lb = $sec_frame->Scrolled('Listbox',
				 -scrollbars => 'oe',
				 -takefocus => 0,
				 -activestyle => 'none',
				)->pack(-expand => 1,
					-fill => 'both',
					-side => 'top');
  $sec_frame->Button(-text => 'Edit',
		     -command => sub {
		       $w->editSections($sort_frame, $sec_lb);
		     }
		    )->pack();

  # This frame holds the widgets to add a new section to the table
  my $add_frame = $section_frame->Frame()->pack(-side => 'top');
  $add_frame->Button(-text => 'Add Section',
		     -command => sub {
		       my $add = 1;
		       my $d = $w->DialogBox( -title => 'PR: Select Section Type',
					      -buttons => ["OK", "Cancel"] );
		       foreach my $section (sort keys %section_types) {
			 my $sect_name = $section;
			 $sect_name =~ s/^\d+//;
			 $d->add( 'Radiobutton', 
				  -text => $sect_name,
				  -value => $section,
				  -variable => \$sec_rb,
				  -anchor => 'w',
				)->pack(-expand => 1, -fill => 'both');
		       }

		       my $answer = $d->Show;
		       if ($answer eq 'OK' and $sec_rb) {
			 my $sect_name = $sec_rb;
			 $sect_name =~ s/^\d+//;
			 my $idx = $section_count;
			 my ($section_type,
			     $obj)= split /\t/, 
			       $section_types{$sec_rb};
			 my %section = (section_name => '',
					section_label => $sect_name,
					section_type => $section_type,
					cols => { col_ids => [], 
						  col_idx => [],
						  col_names => []},
					object => $obj
				       );
			 $sections->{$idx} = \%section;
			 $w->chooseCols($idx);
			 push @$sec_idx, $section_count-2;
			 push @$sec_ids, $section_count;
			 push @$sec_names, 
			   $section_count . " " .
			     $sections->{$idx}->{section_name};
			 $w->fill_lb();
			 $w->fillTable($table->table->totalRows) if $add;
			 $sec_rb ='';
		       }
		       elsif ($answer eq 'Cancel') {
			 $sec_rb = '';
		       }
		     },
		    )->pack(-side => 'left');
  $add_frame->Button(-text => 'Convert Section Type',
		     -command => sub {
		       my $d = $w->DialogBox(-title => 
					         'PR: Select Section to Convert',
					     -buttons => ["OK", "Cancel"]);
		       my $count = 0;
                       $sec_convert = '';
		       foreach my $idx (sort{$a <=> $b} keys %$sections) {
			 if ($sections->{$idx}->{section_label} =~ 
			     /Proposal Columns Displayed/) {
			   $count++;
			   my $text = "$idx: Convert List to Spreadsheet";
			   $text = "$idx: Convert Spreadsheet to List" if 
			     $sections->{$idx}->{section_label} =~ 
			       /Spreadsheet/;
			   my $value = "$idx\tspreadsheet";
			   $value = "$idx\tproposal" if 
			     $sections->{$idx}->{section_label} =~ 
			       /Spreadsheet/;
			   $d->add('Radiobutton',
				   -text => $text,
				   -value => $value,
				   -variable => \$sec_convert,
				   -anchor => 'w',
				  )->pack(-expand => 1, -fill => 'both');
			 }
		       }
		       $d->add('Label', 
			       -text => 'No proposal sections to convert'
			      )->pack(-expand => 1, 
				      -fill => 'both') if !$count;
		       my $answer = $d->Show;
		       if ($answer eq 'OK' and $sec_convert) {
			 my ($section_id, $type) = split /\t/, $sec_convert;
			 my $label = 'Proposal Columns Displayed as List';
			 $label =~ s/List/Spreadsheet/ if 
			   $type =~ /spreadsheet/;

			 $sections->{$section_id}->{section_type} = $type;
			 $sections->{$section_id}->{section_label} = $label;

			 # Clear the table
			 $table->table()->clear;
			 # Repopulate the table
			 my $row = 1;
			 $section_count = 1;
			 $table->print_headers($row);
			 foreach my $sec (sort {$a <=> $b} keys %$sections) {
			   $row++;
			   $w->fillTable($row);
			 }
			 $sec_convert = 'foo';
		       }
		       elsif ($answer eq 'Cancel') {
			 $sec_rb = 'foo';
		       }
		     },
		    )->pack(-side => 'left');


  # These are the buttons to Save/Close/Preview
  my $button_frame = $w->Frame();
  my $save = $button_frame->Button(-text => 'Save' , 
				   -command => [ save  => $w  ] 
				  )->pack(-side => 'left',
					  -expand => 1);
  my $close = $button_frame->Button(-text => 'Close',  
				    -command => sub {
				      my @list = $sec_lb->get(0, 'end');
				      if (scalar @list) {
					my $text = 'You have an unsaved view, do you really want to close?';
					my $dialog = 
					  $w->Dialog(-text => $text,
						     -bitmap => 'question',
						     -default_button => 'Yes',
						     -buttons => [qw /Yes No/ ]
						    );
					my $answer = $dialog->Show;
					'No' eq $answer && return;
					'Yes' eq $answer && $w->close;
				      }
				      else {
					$w->close;
				      }
				    } 
				   )->pack(-side => 'left',
					   -expand => 1);
  my $preview = $button_frame->Button(-text => 'Preview' , 
				      -command => [ preview => $w ] 
				     )->pack(-side => 'left',
					     -expand => 1);
  $button_frame->pack(-side => 'bottom');
  $table->pack(-fill => 'both', -expand => 1);
  $table->update;
  $sec_frame->pack( -expand => 1, -fill => 'both' );
  $sort_frame->pack( -expand => 1, -fill => 'both');
  $section_frame->pack(-side => 'top',
		       -expand => 1,
		       -fill => 'both');
}

## Internal Utility
# Name: _init
# Purpose: initialize for a new view
# Parameters: none
# Returns: nothing
sub _init {
  my $w = shift;
  $sec_ids = [];
  $sec_names = [];
  $sec_idx = [];
  $section_count = 1;
  $sec_rb = '';

  # remove all but the prop_id section from %sections
  foreach my $sec (keys %$sections) {
    delete $sections->{$sec} if $sec > 1;
  }
 
  # Remove pages from notebook
  my $bv = BVNotebook->instance;
  $bv->clean_nb();
}

## Internal Utility
# Name: fill_lb
# Purpose: fill the section order list box
# Parameters: list box
# Returns: nothing
sub fill_lb {
  my $w = shift;

  $sec_lb->delete(0, 'end');
  $sec_lb->insert( 'end',$sec_names->[$_]) foreach @$sec_idx;
}

## Internal Utility ##
# Name: popLoad
# Purpose: populate the load view DialogBox
# Parameters: dialog box
# Returns: nothing
sub popLoad {
  my $w = shift;
  my $db = shift;


 

  $db->add('Label',
	   -text => 'Select View to Load'
	  )->pack;

  my $view_q = $dbh->prepare(qq(select view_id, view_name from views where 
                                panel_id = ? and pub = 'Y' order by view_name));

  # The first section is always a proposal section (even if there is not one 
  # in the database)
  $w->_init();
  
  my $ld_view = undef;
  #my $button_fr = $db->add('Frame')->pack;
  my $button_fr  = $db->Scrolled('Pane', -scrollbars => 'oe')->pack(
       -expand => 1,
       -fill => 'both',
      );
  $view_q->execute($panel_id);
  my ($view_id, $view_name);
  while (($view_id, $view_name) = $view_q->fetchrow_array) {
    $button_fr->Radiobutton(-text => $view_name,
			    -anchor => 'w',
			    -value => $view_id,
			    -variable => \$ld_view,
			   )->pack(-expand => 1,
				   -fill => 'both');
  }

  my $answer = $db->Show;
  if ('Cancel' eq $answer) {
    $sections = {};
    $sec_ids = [];
    $sec_names = [];
    $sec_idx = [];
    $section_count = 2;
    $sec_rb = '';

    # All Views must have a prop_id section as the first section
    my %section = (section_name => "PropNum PI",
		   section_label => 'Proposal Button plus PI',
		   section_type => 'prop_id',
		   cols => { col_ids => [], 
			     col_idx => [],
			     col_names => []},
		   object => undef,
		  );
    $sections->{1} = \%section;
    $w->chooseCols(1);
    return;
  }
  'Load' eq $answer && $w->loadView($ld_view);
}

## Internal Utility ##
# Name: loadView
# Purpose: loads a view from the database into %sections
# Parameters: view_id
# Returns: nothing
sub loadView {
  my $w = shift;
  my $view_id = shift;

  # Clear the table
  $table->table()->clear;
  
  my $sect_q = $dbh->prepare(qq(select section_id, section_name, section_type 
                                from sections where view_id = ? and 
                                panel_id = ? and section_type != 'prop_id' 
                                order by section_order));
  my $col_q = $dbh->prepare(qq(select s.col_id, col_name from columns c, 
                               section_columns s where s.col_id = c.col_id
                               and s.panel_id = c.panel_id and
                               section_id = ? and view_id = ? and 
                               s.panel_id = ? order by col_order));  

  $sect_q->execute($view_id, $panel_id);
  my @sections;
  my $count = 1;
  while (my($section_id, $section_name, 
	    $section_type) = $sect_q->fetchrow_array) {
    $count++;
    push @sections, $section_id;
    $sections->{$count}->{section_name} = $section_name;
    $sections->{$count}->{section_type} = $section_type;

    if ($section_type eq 'proposal') {
      $sections->{$count}->{object} = 'proposal';
      $sections->{$count}->{object} = 'prelimgrades' if $section_name eq 'Prelim Grades';
      $sections->{$count}->{object} = 'finalgrades' if $section_name eq 'Final Grades';
      $sections->{$count}->{object} = 'grades' if $section_name eq 'All Grades';
    }
    elsif ($section_type eq 'spreadsheet') {
      $sections->{$count}->{object} = 'proposal';
      $sections->{$count}->{object} = 'run_tot' if 
	$section_name eq 'Running Totals';
    }
    elsif ($section_type eq 'target') {
      $sections->{$count}->{object} = 'target';
    }
    elsif ($section_type eq 'combo_cols') {
      $sections->{$count}->{object} = 'combo_cols';
    }
    elsif ($section_type eq 'multi_cols') {
      $sections->{$count}->{object} = 'multi_cols';
    }
    else {
      $sections->{$count}->{object} = undef;
    }
    foreach my $label (sort keys %section_types) {
      my $test = "$section_type\t";
      $test .= $sections->{$count}->{object} if $sections->{$count}->{object};
      if ($section_types{$label} eq $test) {
	$label =~ s/^\d+//;
	$sections->{$count}->{section_label} = $label;
      }
    }
    push @$sec_idx, $count-2;
    push @$sec_ids, $count;
    push @$sec_names, $count . " " . $sections->{$count}->{section_name};
  }
  $sect_q->finish;

  $count = 1;
  foreach my $section_id (@sections) {
    $count++;
    $w->chooseCols($count);
    if ($sections->{$count}->{object} and 
	$sections->{$count}->{object} !~ /grades/) {
      $col_q->execute($section_id, $view_id, $panel_id);
      my $col = 0;
      while (my($col_id, $col_name) = $col_q->fetchrow_array) {
	my $col_ids = $sections->{$count}->{cols}->{col_ids};
	my $col_idx = $sections->{$count}->{cols}->{col_idx};
	for (my $i = 0; $i < scalar @$col_ids; $i++) {
	   if ($col_id == $col_ids->[$i]) {
	     push @$col_idx, $i;
	}
	}
	$sections->{$count}->{cols}->{col_idx} = $col_idx;
      }
      $col_q->finish;
    }
  }

  # Repopulate the table
  my $row = 1;
  $section_count = 1;
  $table->print_headers($row);
  foreach my $sec (sort {$a <=> $b} keys %$sections) {
    $row++;
    $w->fillTable($row);
  }
  
  # Populate the list box
  $w->fill_lb();
}

## Internal Utility ##
# Name: fillTable
# Purpose: display the sections of a view
# Parameters: table object
# Returns: nothing
sub fillTable {
  my $w = shift;
  my $row = shift;

  if ($sections->{$section_count}->{section_type} =~ 
      /prop_id|abstract|title|short_ttl/ or
     $sections->{$section_count}->{object} =~ /grades/) {
    # Load the @cols with the prop_id column
    $table->print_field($row, 1, $section_count, 'w');
  }
  else {
    my $b_frame = $table->mk_pack_frame($row, 1);
    my $nb = BVNotebook->instance;

    $b_frame->Button(-text => $section_count, 
		     -anchor => 'w',
		     -command => sub {
		       $nb->build_section($row-1, $sec_lb, 
					  $sec_names, $sec_idx);
		     }
		    )->pack(-side => 'top', 
			    -expand => 1,
			    -fill => 'both');
  }

  my $name_frame = $table->mk_pack_frame($row, 2);
  $name_frame->Label(-textvariable => 
		         \$sections->{$row-1}->{section_name},
		     -anchor => 'w')->pack(-side => 'top',
					   -expand => 1,
					   -fill => 'both');

  $table->print_field($row, 3, $sections->{$row-1}->{section_label}, 
		      'w');

  my $col_ref = $sections->{$row-1}->{cols}->{col_idx};
  if (scalar @$col_ref) {
    my $col_frame = $table->mk_pack_frame($row, 4);
    foreach my $idx (@$col_ref) {
      my $label = $idx+1 . ": " . 
	$sections->{$row-1}->{cols}->{col_names}->[$idx];
      $col_frame->Label(-text => $label,
			-anchor => 'w')->pack(-side => 'top',
					      -expand => 1,
					      -fill => 'both');
    }
  }
  else {
    $table->print_field($row, 4, 'No Columns Selected', 'w');
  }

  $section_count++;
}

## Internal Utility ##
# Name: chooseCols
# Purpose: populates the col_ids available for the section_type/object
#          definition
# Parameters: section number
# Returns: nothing
sub chooseCols {
  my ($w, $sec_num) = @_;
  my $sec_type = $sections->{$sec_num}->{section_type};
  my $object = $sections->{$sec_num}->{object};
  
  my $get_col_id = $dbh->prepare(qq(select col_id, col_name from columns 
				      where panel_id = ? and attribute = ?
				      and pub = 'Y'));
  if ($sec_type =~ /prop_id|title|short_ttl|abstract/) {
    $get_col_id->execute($panel_id, $sec_type);
    my ($col_id, $col_name) = $get_col_id->fetchrow_array;
    $get_col_id->finish;
    
    $sections->{$sec_num}->{section_name} = $col_name;
    
    my $col_ids = $sections->{$sec_num}->{cols}->{col_ids};
    my $col_idx = $sections->{$sec_num}->{cols}->{col_idx};
    my $col_names = $sections->{$sec_num}->{cols}->{col_names};    
    push @{$col_ids}, $col_id;
    push @{$col_names}, $col_name;
    push @{$col_idx}, @{$col_ids}-1;
    $sections->{$sec_num}->{cols} = { col_ids => $col_ids, 
				      col_idx => $col_idx,
				      col_names => $col_names};
  }
  elsif ($object =~ /finalgrades/ ) {
    my @att = ('final_gradesx');
    my $col_ids = $sections->{$sec_num}->{cols}->{col_ids};
    my $col_idx = $sections->{$sec_num}->{cols}->{col_idx};
    my $col_names = $sections->{$sec_num}->{cols}->{col_names};
    
    foreach my $att (@att) {
      $get_col_id->execute($panel_id, $att);
      my ($col_id, $col_name) = $get_col_id->fetchrow_array;
      $get_col_id->finish;
      
      $sections->{$sec_num}->{section_name} = 'FinalGrades';
      
      
      push @{$col_ids}, $col_id;
      push @{$col_names}, $col_name;
      push @{$col_idx}, @{$col_ids}-1;
    }
    $sections->{$sec_num}->{cols} = { col_ids => $col_ids, 
				      col_idx => $col_idx,
				      col_names => $col_names};
  }
  elsif ($object =~ /prelimgrades/ ) {
    my @att = ('prelim_gradesx');
    my $col_ids = $sections->{$sec_num}->{cols}->{col_ids};
    my $col_idx = $sections->{$sec_num}->{cols}->{col_idx};
    my $col_names = $sections->{$sec_num}->{cols}->{col_names};
    
    foreach my $att (@att) {
      $get_col_id->execute($panel_id, $att);
      my ($col_id, $col_name) = $get_col_id->fetchrow_array;
      $get_col_id->finish;
      
      $sections->{$sec_num}->{section_name} = 'PreliminaryGrades';
      
      
      push @{$col_ids}, $col_id;
      push @{$col_names}, $col_name;
      push @{$col_idx}, @{$col_ids}-1;
    }
    $sections->{$sec_num}->{cols} = { col_ids => $col_ids, 
				      col_idx => $col_idx,
				      col_names => $col_names};
  }
  elsif ($object =~ /grades/ ) {
    my @att = ('prelim_grades', 'final_grades');

    my $col_ids = $sections->{$sec_num}->{cols}->{col_ids};
    my $col_idx = $sections->{$sec_num}->{cols}->{col_idx};
    my $col_names = $sections->{$sec_num}->{cols}->{col_names};
    
    foreach my $att (@att) {
      $get_col_id->execute($panel_id, $att);
      my ($col_id, $col_name) = $get_col_id->fetchrow_array;
      $get_col_id->finish;
      
      $sections->{$sec_num}->{section_name} = 'AllGrades';
      
      
      push @{$col_ids}, $col_id;
      push @{$col_names}, $col_name;
      push @{$col_idx}, @{$col_ids}-1;
    }
    $sections->{$sec_num}->{cols} = { col_ids => $col_ids, 
				      col_idx => $col_idx,
				      col_names => $col_names};
  }
  elsif ($object =~ /run_tot/) {
    $sections->{$sec_num}->{section_name} = 'Running Totals';
    my $query = $dbh->prepare(qq(select col_id, col_name from columns 
				 where panel_id = ? and 
			         attribute like 'cum%' and pub = 'Y'
			         order by col_id));
    $query->execute($panel_id);
    
    # column names and ids from the database; filtered out
    # those dealt with elsewhere
    my ( @col_names,  @col_ids);
    
    # this will contain the ordered list of selected column indices
    # into the @col_names and @col_ids arrays.
    my @col_idx;
    
    while (my($col_id, $col_name) = $query->fetchrow_array) {
      $col_name =~ s/\s+//g;
      
      push @col_names, $col_name;
      push @col_ids, $col_id;
    }
    $query->finish;
    
    $sections->{$sec_num}->{cols} = { col_ids => \@col_ids, 
				      col_idx => \@col_idx,
				      col_names => \@col_names};
  }
  elsif ($object =~ /combo_cols/) {
    my $query = $dbh->prepare(qq(select col_id, col_name from columns
                                 where panel_id = ? and display = 'combo'
                                 and pub = 'Y'
                                 order by col_id));
    $query->execute($panel_id);

    # column names and ids from the database; filtered out
    # those dealt with elsewhere
    my ( @col_names,  @col_ids);
    
    # this will contain the ordered list of selected column indices
    # into the @col_names and @col_ids arrays.
    my @col_idx;
    
    while (my($col_id, $col_name) = $query->fetchrow_array) {
      $col_name =~ s/\s+//g;
      
      push @col_names, $col_name;
      push @col_ids, $col_id;
    }
    $query->finish;
    
    $sections->{$sec_num}->{cols} = { col_ids => \@col_ids, 
				      col_idx => \@col_idx,
				      col_names => \@col_names};
  }
  elsif ($object =~ /multi_cols/) {
    my $query = $dbh->prepare(qq(select col_id, col_name from columns
                                 where panel_id = ? and display = 'multi'
                                 and pub = 'Y'
                                 order by col_id));
    $query->execute($panel_id);

    # column names and ids from the database; filtered out
    # those dealt with elsewhere
    my ( @col_names,  @col_ids);
    
    # this will contain the ordered list of selected column indices
    # into the @col_names and @col_ids arrays.
    my @col_idx;
    
    while (my($col_id, $col_name) = $query->fetchrow_array) {
      $col_name =~ s/\s+//g;
      
      push @col_names, $col_name;
      push @col_ids, $col_id;
    }
    $query->finish;
    
    $sections->{$sec_num}->{cols} = { col_ids => \@col_ids, 
				      col_idx => \@col_idx,
				      col_names => \@col_names};
  }
  else { 
    my $query = $dbh->prepare(qq(select col_id, col_name from columns 
		         	 where panel_id = ? and object = ? 
                                 and pub = 'Y' and 
                                 display not in ('combo', 'multi')
			         order by col_id));
    $query->execute($panel_id, $object);
    
    # column names and ids from the database; filtered out
    # those dealt with elsewhere
    my ( @col_names,  @col_ids);
    
    # this will contain the ordered list of selected column indices
    # into the @col_names and @col_ids arrays.
    my @col_idx;
    
    while (my($col_id, $col_name) = $query->fetchrow_array) {
      $col_name =~ s/\s+//g;
      next if $col_name =~ /PropNum|Title|Short Title|Abstract|Grades|Run/ or 
	$col_name eq 'PI';
      
      push @col_names, $col_name;
      push @col_ids, $col_id;
    }
    $query->finish;
    
    $sections->{$sec_num}->{cols} = { col_ids => \@col_ids, 
				      col_idx => \@col_idx,
				      col_names => \@col_names};
    
  }
}

## Internal Utility ##
# Name: editSections
# Purpose: Choose final order and selection of sections
# Parameters: frame
#             listbox
# Returns: nothing
sub editSections {
  my ( $w, $frame, $lb ) = @_;

  # edit Sections
  if (scalar @$sec_names > 0) {
    my $d = $frame->DialogBox( -title => 'PR: Defined Sections',
			     -buttons => ["OK"] );

    $d->add( 'EnumList', 
	   -orderedlist => $sec_idx, 
	   -choicelist => $sec_names,
	   -duplicate => 0 )->pack;
	   
    $d->Show;
  
    # now show columns in list box
    $lb->delete(0,'end');
    $lb->insert( 'end', $sec_names->[$_] ) foreach @$sec_idx;
  } else {
    my $dialog = $w->Dialog(-title => 'Warning',
                            -text => "No sections available",
                            -buttons => ['Okay'],
                            -default_button => 'Okay',
                           );
    $dialog->Show();

  }

}

## Internal Utility ##
# Name: save
# Purpose: widget for saving the new view to the database
# Parameters: none
# Returns: nothing
sub save
{
  my $w = shift;
  my $panel_id  = $w->cget('-panel_id');
  my $dbh = $w->cget('-dbh');
  
  # Get the next view_id for the panel
  my $get_next_view = $dbh->prepare(qq(select max(view_id)+1 from views 
					 where panel_id = ?));
  $get_next_view->execute($panel_id);
  my ($view_id) = $get_next_view->fetchrow_array;
  $get_next_view->finish;
  
  # Request a view name
  my $view_name;
  my $save_box = $w->DialogBox(-title => 'PR: Save View',
			       -buttons => ['Save', 'Cancel'],
			       -default_button => 'Save'
			      );
  $save_box->add('LabEntry',
		 -textvariable => \$view_name,
		 -width => 30,
		 -label => 'View Name',
		 -labelPack => [-side => 'left']
		)->pack();
  
  my $answer = $save_box->Show();
  
  if ($answer eq 'Save') {
    if ($view_name) {
      # Does this view name exist?
      my $view_exist = $dbh->prepare(qq(select count(*) from views where 
					view_name = ? and panel_id = ?));
      $view_exist->execute($view_name, $panel_id);
      my ($count) = $view_exist->fetchrow_array;
      $view_exist->finish;
      
      if ($count > 0) {
	$w->messageBox(-title => 'PR: Data Entry Error',
		       -message => "A view with this name already exists in the database.  Please choose a different name."
		      );
      }
      else{
	# Insert view
	my $insert_view = $dbh->prepare(qq(insert into views values 
					   (?, ?, ?, ?)));
	$insert_view->execute($view_id, $panel_id, $view_name, 'Y');
	$insert_view->finish;
	
	# Insert sections
	my $section_order = 1;
	$w->insert_sections($view_id, 1, $section_order++);  # proposal button

	$w->insert_sections($view_id, $sec_ids->[$_],
			    $section_order++) foreach (@$sec_idx);
	
	# Update Menus
	$w->Callback( '-lcdviewmenucmd', $view_id );
	$w->Callback( '-facviewmenucmd', $view_id );
	
	# Close
	defined $w->cget( '-closecmd' ) ? $w->Callback( '-closecmd' ) : 
	  $w->destroy;
      }
    }
  }
}

## Internal Utility ##
# Name: close
# Purpose: call back to destroy window
# Parameters: none
# Returns: nothing
sub close
{
    my $w = shift;
    defined $w->cget( '-closecmd' ) ? $w->Callback( '-closecmd' ) : 
	$w->destroy;
}

## Internal Utility ##
# Name: preview
# Purpose: Preview the new view before saving
# Parameters: none
# Returns: nothing
sub preview
{
    my $w = shift;
    # First we need to delete the last preview from the sections and
    # section_column tables
    my $del_section_columns = $dbh->prepare(qq(delete from section_columns 
					       where view_id = ? and 
					       panel_id = ? and
					       section_id in 
					       (select section_id from 
						sections where view_id = ? and 
						panel_id = ?)));
    my $del_sections = $dbh->prepare(qq(delete from sections where 
					view_id = ? and panel_id = ?));

    $del_section_columns->execute(-1, $panel_id, -1, $panel_id);
    $del_section_columns->finish;
    $del_sections->execute(-1, $panel_id);
    $del_sections->finish;

    # Insert sections
    my $section_order = 1;
    $w->insert_sections(-1, 1, $section_order++);  # proposal button
    
    $w->insert_sections(-1, $sec_ids->[$_],
			$section_order++) foreach (@$sec_idx);
    $w->Callback( '-previewcmd', -1 );
}

## Internal Utility ##
# Name: insert_sections
# Purpose: insert sections into database
# Parameters: view_id
#             database handle
#             panel id
# Returns: nothing
sub insert_sections {
  my ($w, $view_id, $sec_num, $section_order) = @_;
  my $get_section_id = $dbh->prepare(qq(select max(section_id) from sections 
					where view_id = ? and panel_id = ?));
  my $insert_section = $dbh->prepare(qq(insert into sections values 
					(?, ?, ?, ?, ?, ?, ?)));
  my $update_section = $dbh->prepare(qq(update sections set 
					section_name = 'PropNum PI' where
					panel_id = ? and view_id = ? and
                                        section_type = 'prop_id'));
  my $insert_sec_col = $dbh->prepare(qq(insert into section_columns values 
					  (?, ?, ?, ?, ?)));
  # Get section information and insert into table
  $get_section_id->execute($view_id, $panel_id);
  my ($section_id) = $get_section_id->fetchrow_array;
  $get_section_id->finish;
  $section_id++;

  my $section_name = $sections->{$sec_num}->{section_name};
  $section_name = "NONE" if !$section_name;
  
  my $section_type = $sections->{$sec_num}->{section_type};
  
  my $section_width;
  $section_width = 20 if $section_type =~ /title|short_ttl/;
  $section_width = 40 if $section_type =~ /abstract/;

  $insert_section->execute($section_id, $view_id, $panel_id, 
			   $section_name, $section_order, 
			   $section_type, $section_width);
  $insert_section->finish;
      
  # For each column, insert into section_columns table
  my $col_ids = $sections->{$sec_num}->{cols}->{col_ids};
  my $col_idx = $sections->{$sec_num}->{cols}->{col_idx};

  # Get the column information and insert into section_columns
  my $col_order = 0;
  foreach my $idx ( @$col_idx ) {
    $col_order++;
    my $col_id = $col_ids->[$idx];
    $insert_sec_col->execute($section_id, $view_id, $panel_id, 
			     $col_id, $col_order);
    $insert_sec_col->finish;
  }
      
  if ($section_type =~ /proposal/) {
    my $get_sec_width = $dbh->prepare(qq(select max(col_width) 
					 from columns where col_id in
					 (select col_id from section_columns 
                                          where view_id = ? and section_id = ? 
                                          and panel_id = ?)));
    my $update_sec_width = $dbh->prepare(qq(update sections set 
                                            section_width = ? where
					    section_id = ? and panel_id = ?));
	
    $get_sec_width->execute($view_id, $section_id, $panel_id);
    my ($section_width) = $get_sec_width->fetchrow_array;
    $get_sec_width->finish;
    
    $update_sec_width->execute($section_width, $section_id, 
			       $panel_id);
    $update_sec_width->finish;
  }
  $update_section->execute($panel_id, $view_id);
  $update_section->finish;
}

1;

__END__

=head1 NAME

BuildView - This is a widget for creating views

=head1 VERSION

$Revision: 1.27 $

=head1 SYNOPSIS

   use BuildView;
   my $build_view = $mw->Toplevel();
   $build_view->BuildView( -dbh => $dbh1,
			   -panel_id => $panel_id,
                           -verbose => $verbosity,
			   -previewcmd => [\&previewView => ($self)],
			   -lcdviewmenucmd => [\&addLCDViewMenu => ($self) ],
			   -facviewmenucmd => [\&addFacViewMenu => ($self) ],
			   -closecmd => [ \&closeBuildView => ($self) ],
			  )->pack(-expand => 1,
				  -fill => 'both');

=head1 DESCRIPTION

Provides a convenient interface for creating views

=head1 PUBLIC METHODS

=head2 Populate($w, $args)

Populates the widget

=over  

=item $w - toplevel widget

=item $args - reference to hash of arguments

   -dbh - database handle

   -panel_id - panel id

   -verbose - level of verbosity

   -closecmd - call back for closing widget

   -previewcmd - call back for previewing the new view

   -viewmenucmd - call back for adding new view to view menu

=back 

=head1 PRIVATE METHODS

=head2 _init

Initialize after a clear.

=head2 fill_lb

Put the selected sections into the section order list box

=head2 popLoad

Populates the "Load Existing View" dialog.

=head2 loadView

Loads the selected view into the widget.

=head2 fillTable

Display the sections of a view in a table

=head2 chooseCols

Populates the col_ids available for the section_type/object definition

=head2 editSections

Choose final order and selection of sections

=head2 save

Widget for saving new view

=head2 close

Call back to destroy page

=head2 preview

Preview the view before saving

=head2 insert_sections

Inserts sections into database

=head1 DEPENDENCIES

This module uses Tk::Pane, Tk::Dialog, and Tk::Table (available from CPAN)

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
