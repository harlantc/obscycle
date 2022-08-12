#
# BuildTable.pm - This is an OO-interface for displaying data in a 
#                 is several formats within a Tk::Table widget
#
# Purpose: Provides a convenient interface for displaying proposals
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package BuildTable;


use strict;
use warnings;

use Carp;
use Data::Dumper;
use Monitor;
use config;
use Tk 800.000;
use Tk::Table;
use EditNotebook;

use base qw(Class::Accessor);
BuildTable->mk_accessors(qw(mw table headers cols id verbose));

## Class Method ##
# Name: new
# Purpose: create a new BuildTable object
# Parameters: level of verbosity
#             GUI main window
#             columns headers for table
#             fixed rows
#             scrollbar location
#             number of rows
#             number of tables
# Returns: BuildTable object
sub new {
    my $self = {};
    bless $self,shift;
    $self->_init(@_);
    print "BuildTable::new - creating new object\n" if $self->verbose > 2;
    return $self;
}

## Internal Utility ##
# Name: _init
# Purpose: initializes a new BuildTable object
# Parameters: level of verbosity
#             GUI main window
#             array ref of column headers for table
#             fixed rows
#             scrollbar location
#             number of rows
#             number of tables
# Returns: BuildTable object
sub _init {
    my ($self, $verbose, $mw, $headers, $fixedrows,$scrollbars, $rows, $cols) = @_;
    my (%init) = %$self;
    $self->{'mw'} = $mw;
    $self->{verbose} = $verbose;
    $cols = int(scalar @$headers) * 2 - 1 if !defined($cols);
    $self->{'cols'} = $cols;
    my @headers = @$headers;
    $self->{'headers'} = \@headers;

  
    # Add one to the -columns because PropNum is really two columns
    $self->{'table'} = $mw->Table(-rows => $rows,
				  -columns => $cols,
				  -fixedrows => $fixedrows,
				  -fixedcolumns => 2,
				  -scrollbars => $scrollbars,
				  -takefocus => 0,
				);

}


## Class Method ##
# Name: bind_scrollbars
# Purpose: 
# Parameters: GUI main window
#             quit filemenu widget
# Returns: EditNotebook object
#sub bind_scrollbars {
#  my $self = shift;
#  $_->bind('<Any-Button>', sub{ $self->table->focus}) 
#    foreach $self->table->children;
#}

## Class Method ##
# Name: pack
# Purpose: pack a table
# Parameters: none
# Returns: nothing
sub pack { shift()->table->pack( @_ ); }

## Class Method ##
# Name: update
# Purpose: update a table
# Parameters: none
# Returns: nothing
sub update { shift()->table->update( @_ ); }

## Class Method ##
# Name: mk_pack_frame
# Purpose: create a frame with pack geometry
# Parameters: row and column to place frame into BuildTable object
# Returns: frame
sub mk_pack_frame {
    my ($self, $row, $col) = @_;
    my $frame = $self->table()->Frame(-relief => 'groove',
				      -borderwidth => 2,
				      )->pack(-side => 'top',
					      -fill => 'both',
					      -expand => 1);
    $self->table()->put($row, $col, $frame) if ($row and $col);
    return $frame;
}

## Class Method ##
# Name: mk_grid_frame
# Purpose: create a frame with grid geometry
# Parameters: row and column to place frame into BuildTable object
# Returns: frame
#sub mk_grid_frame {
#    my ($self, $row, $col) = @_;
#    my $frame = $self->table()->Frame(-relief => 'groove',
#				      -borderwidth => 2,
#				      )->grid(-sticky => 'nswe');
#    $self->table()->put($row, $col, $frame) if ($row and $col);
#    return $frame;
#}

## Class Method ##
# Name: print_headers
# Purpose: put the headers of a table into the header row
# Parameters: row that headers go into
#             panel object
#             mkView subroutine
#             window that View goes into
#             string indicating which BPP group the sort applies to
# Returns: nothing
sub print_headers {
  my ($self, $row, $panel, $mkView, $view, $BPP,$finalflg) = @_;
  my $headers = $self->headers();

  if (! defined $finalflg) {
    $finalflg=0;
  }

  if ($panel and $mkView) {
    my $query = $panel->dbh->prepare(qq(select col_name, attribute,
                                        c.sortby, s.sortby from 
                                        columns c, sorts s where
                                        col_name = ? and c.panel_id = ? and 
					col_name = sort_name and 
					s.panel_id = c.panel_id 
			order by object ));      
    for (my $i = 1; $i <= scalar @$headers; $i++) {
      my (@subhead) = split /\t/, $$headers[$i-1];
      my ($bf);
      if ($finalflg != 1) {
        $bf = $self->table->Frame();
      }
      my $lf = $self->table->Frame();
      my $side = 'left';
      foreach my $subhead (@subhead) {
	$query->execute($subhead, $panel->panel_id);
	my ($sortname, $att, $sortby, $sort) = $query->fetchrow_array;
	$subhead =~ s/\s+/\n/g;
	$sortby = 'N' if !$sortby;
	$query->finish;
	if ($subhead =~ /PropNum/) {
	  $lf->Label(-text => 'Proposal',
		     -anchor => 'c',
		     -width => 1,
		     -relief => 'groove',
		    )->pack(-side => 'left',
			    -expand => 1,
			    -fill => 'both');
          if ($finalflg != 1) {
	    $bf->Button(-text => 'A',
		      -width => 2,
		      -command => sub {
			$panel->set_sort($view, '', 'prop_id', 'PropNum');
			&$mkView($view, undef, $BPP);
		      }
		     )->pack(-side => 'left');
	    $bf->Button(-text => 'D',
		      -width => 2,
		      -command => sub {
			$panel->set_sort($view, '', 'prop_id DESC', 'PropNum');
			&$mkView($view, undef, $BPP);
		      }
		     )->pack(-side => 'left');
           }

	  $lf->Label(-text => 'PI',
		     -anchor => 'c',
		     -relief => 'groove',
		    )->pack(-side => 'left',
			    -expand => 1,
			    -fill => 'both');
          if ($finalflg != 1) {
	    $bf->Button(-text => 'A',
		      -command => sub {
			$panel->set_sort($view,'', 'last_name', 'PI');
			&$mkView($view, undef, $BPP);
		      }
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');
	    $bf->Button(-text => 'D',
		      -command => sub {
			$panel->set_sort($view, '', 'last_name DESC', 'PI');
			&$mkView($view, undef, $BPP);
		      }
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');
          }
          if ($subhead =~ /Rank/) {
	    $lf->Label(-text => 'Rank',
		       -anchor => 'c',
		       -relief => 'groove',
		      )->pack(-side => 'left',
			      -expand => 1,
			      -fill => 'both');
            if ($finalflg != 1) {
	      $bf->Button(-text => 'A',
			-command => sub {
			  $panel->set_sort($view,'', 'rank', 'Rank');
			  &$mkView($view, undef, $BPP);
			}
		       )->pack(-side => 'left',
			       -expand => 1,
			       -fill => 'both');
	      $bf->Button(-text => 'D',
			-command => sub {
			  $panel->set_sort($view, '', 'Rank DESC', 'Rank');
			  &$mkView($view, undef, $BPP);
			}
		       )->pack(-side => 'left',
			       -expand => 1,
			       -fill => 'both');	  
	    }
	  }
	}
	elsif ($sortby eq 'Y') {
	    # ASC and DESC sort buttons
	    my $asc_sort = $sort;
	    my $desc_sort = $sort . " DESC" ;

	    $lf->Label(-text => $subhead,
		       -anchor => 'c',
		       -relief => 'groove',
		      )->pack(-side => 'top', -expand => 1, -fill => 'both');
            if ($finalflg != 1) {
	      $bf->Button(-text => 'A',
			-command => sub {
			  $panel->
			    set_sort($view, '', $asc_sort, $sortname);
			  &$mkView($view, undef, $BPP);
			}
		       )->pack(-side => $side,
			       -expand => 1,
			       -fill => 'both');

	      $bf->Button(-text => 'D',
			-command => sub {
			  $panel->
			    set_sort($view, '', $desc_sort, $sortname);
			  &$mkView($view, undef, $BPP);
			}
		       )->pack(-side => $side,
			       -expand => 1,
			       -fill => 'both');
	    }
	}
	else {
	  $lf->Label(-text => $subhead,
		     -anchor => 'c',
		     -relief => 'groove',
		    )->pack(-expand => 1, -fill => 'both');
	}
	  $lf->pack(-expand => 1,
		  -fill => 'both');
          if ($finalflg != 1) {
	    $bf->pack(-expand => 1,
		  -fill => 'both');
          }
	
	$self->table->put($row, $i, $lf);
        if ($finalflg != 1) {
	  $self->table->put($row + 1, $i, $bf);
        }
      }
    }
  }
  else {
    for (my $i = 1; $i <= scalar @$headers; $i++) {
      my $bf = $self->table->Frame();
      my $heading = $$headers[$i-1];
      $heading =~ s/\s+/\n/g;
      $bf->Label(-text => $heading,
		 -anchor => 'c',
		 -relief => 'groove'
		)->pack(-expand => 1, -fill => 'both');
      $bf->pack(-expand => 1,
		-fill => 'both');
      
      $self->table->put($row, $i, $bf);
    }
  }
}


## Class Method ##
# Name: prop_buttons
# Purpose: tie a notebook page to a proposal button and puts the button into 
#          the table
# Parameters: row of table that button goes into
#             col of table that button goes into
#             proposal id
#             quit menu item
#             monitor window
#             panel object
# Returns: nothing
sub prop_buttons {
  my ($self, $row, $col, $prop_id, $quit, $monitor, $panel, $show_button,
      $show_rank) = @_;
  my $button_frame = $self->mk_pack_frame();
  my $prop_frame = $button_frame->Frame()->pack(-expand => 1,
						-fill => 'both');
  
  my $proposal = $panel->proposals->proposal($prop_id);
  
  my $nb = EditNotebook->instance;
  
  my $color;
  if ($proposal->big_proj =~ /GTO/ && $proposal->panel_id == 99) {
    $color = '#9999CC';
    #$color = '#6666FF';
    #$color = '#CCCCFF';
  }
  elsif ($proposal->prop_status =~ /$STAT_BPP/) {
    $color = '#99CCFF';
  }
  elsif ($proposal->type =~ /ARCHIVE|THEORY/) {
    $color = '#99FF99';
  }
  elsif ($proposal->prop_status =~ /$STAT_NO/) {
    $color = '#CCCCCC';
  }
  
  if ($color) {
    if ($show_button) {
      $prop_frame->Button(-text => $prop_id,
			  -anchor => 'w',
			  -background => $color,
			  -width => 8,
			  -command => sub {
			    $nb->edit_prop( $prop_id, $panel);},
			 )->pack(-side=>'left');
    }
    else {
      $prop_frame->Label(-text => $prop_id,
			 -anchor => 'w',
			 -background => $color,
			)->pack(-side=>'left');
    }
    $prop_frame->Label(-text => $panel->proposals->proposal($prop_id)->last_name,
		       -anchor => 'w',
		       -background => $color,
		      )->pack(-side=>'left', -expand => 1, -fill => 'both');
    if ($show_rank) {
      $prop_frame->Label(-text => $panel->proposals->proposal($prop_id)->rank,
			 -anchor => 'e',
			 -background => $color,
			)->pack(-side=>'left', -expand => 1, -fill => 'both');
    }
  }
  else {
    if ($show_button) {
      $prop_frame->Button(-text => $prop_id,
			  -anchor => 'w',
			  -width => 8,
			  -command => sub {
			    $nb->edit_prop( $prop_id, $panel);},
			 )->pack(-side => 'left');
    }
    else {
      $prop_frame->Label(-text => $prop_id,
			 -anchor => 'w',
			)->pack(-side=>'left');
    }
    $prop_frame->Label(-text => $panel->proposals->proposal($prop_id)->last_name,
		       -anchor => 'w',
		      )->pack(-side=>'left', -expand => 1, -fill => 'both');
    if ($show_rank) {
      $prop_frame->Label(-text => $panel->proposals->proposal($prop_id)->rank,
			 -anchor => 'e',
			)->pack(-side=>'left');
    }
  }
    
  if ($proposal->link_id) {
    my $link_id = $proposal->link_id;
    #print STDERR "link id= $link_id";
    my $lproposal = $panel->proposals->proposal($link_id);
    my($ltxt) = "Linked to " . $link_id . " ";
    if ($lproposal) {
        $ltxt .= $lproposal->last_name unless !$lproposal->last_name;
    }
    my $link_frame = $button_frame->Label(-text => $ltxt,

	-font => "small"
					 )->pack(-expand => 1,
						 -fill => 'both');
  }
  $self->table()->put($row, $col, $button_frame);
}

## Class Method ##
# Name: prop_info_new
# Purpose: populate a cell using the proposal information layout
# Paramters: which display (lcd or fac)
#            BPP type
#            row of table cell
#            column of table cell
#            hash ref of fields to put in cell
#            proposal object
#            width of value column (this is no longer necessary, but needs
#                                   to be cleaned up in PR
# Returns: nothing
sub prop_info_new {
  my ($self, $view, $bpp, $row, $col, $fields, $prop, $label_width) = @_;
  my $frame = $self->mk_pack_frame();
  my %tables;
  my $items = 0;
  my $label_frame;
  # Put the label displays into the label_frame and add the table
  # columns to @tables to be created after the label_frame
  foreach my $col_num (sort { $a <=> $b } keys %$fields) {
    if ($$fields{$col_num}{display} =~ /label/ || 
        $$fields{$col_num}{display} =~ /entry/) {
	my $name = $$fields{$col_num}{name};
	$name =~ s/\n//g;
	$name =~ s/\s+//g;
      # make a frame if first label display
      $label_frame = $frame->Frame()->pack(-anchor => 'w', 
					   -side => 'top') 
	if $items == 0;
      $items++;
      my $attribute = $$fields{$col_num}{attribute};
      my $vv = $view;
      if ($bpp && $bpp =~ /lp/i) { $vv = $view ."_" . lc($bpp); }
      $attribute =~ s/cum_/cum_${vv}_/ if $attribute =~ /cum/;
      if ($$fields{$col_num}{edit_flag} =~ /Y/) {
#	my $color = 'red';
	my $color = 'black';
#	$color = 'dark green' if $attribute =~ /cum/;
	my $label =  $label_frame->Label(-textvariable => 
					 $prop->get_ref($attribute),
					 -anchor => 'w',
					 -foreground => $color,
					 -relief => 'flat',
					);
	$label_frame->Label(-text => $name,
			    -anchor => 'e',
			    -foreground => 'black',
			    -relief => 'flat',
			   )->grid
			     ($label,
			      -sticky => 'nsew');
      }
      else {
#	my $color = 'blue';
	my $color = 'black';
#	$color = 'dark green' if $attribute =~ /cum/;
	$label_frame->Label(#-text => $$fields{$col_num}{name},
			    -text => $name,
			    -anchor => 'e',
			    -foreground => 'black',
			    -relief => 'flat',
			   )->grid
			     ($frame->Label(-text => $prop->$attribute,
					    -anchor => 'w',
					    -foreground => $color),
			      -sticky => 'nsew',
			     );
      }
    }
    else {
      my @rows;
      if (!$tables{$$fields{$col_num}{table_id}}){
	my @rows = $$fields{$col_num};
	$tables{$$fields{$col_num}{table_id}} = \@rows;
      }
      else {
	my $rows = $tables{$$fields{$col_num}{table_id}};
	my @rows = @$rows;
	push @rows, $$fields{$col_num};
	$tables{$$fields{$col_num}{table_id}} = \@rows;
      }
    }
  }
  
  # Create any table displays
  foreach my $table_id (sort keys %tables) {
    #print STDERR "processing $table_id\n";
    my $table_frame = $frame->Frame()->pack();
    $table_frame = column_table($table_frame, $tables{$table_id}, $prop, 
				$table_id, $fields, $tables{name});
  }
  $self->table()->put($row, $col, $frame);
}


## Class Method ##
# Name: text
# Purpose: place a ROText widget into table cell
# Parameters: row of cell
#             column of cell
#             title of text
#             width of text frame
#             height of text frame
# Returns: nothing
sub text {
    my ($self, $row, $col, $title, $width, $height) = @_;
    my $frame = $self->mk_pack_frame();
    my $title_text = $frame->ROText( -wrap => "word",
				     -width => $width,
				     -height => $height,
				     -relief => 'flat',
				     )->pack(-side => 'top');
    $title_text->insert('end', $title);
    $self->table()->put($row, $col, $frame);
}

## Class Method ##
# Name: column_table
# Purpose: populate a frame with data in a gridded format
# Parameters: frame
#             number of rows
#             proposal object
#             table_id
#             hash of fields
#             name of table
# Returns: frame
sub column_table {
    my ($frame, $rows, $prop, $table_id, $fields, $table_name) = @_;
    my $spreadsheet =  $table_name ? 1 : 0;

    my $table_name_query = $prop->dbh->prepare(qq(select distinct table_name 
						  from table_columns where 
						  table_id = ? and 
						  panel_id = ?));
    my $col_query = $prop->dbh->prepare(qq(select col_name, col_width from 
					   table_columns 
					   where table_id = ? and panel_id = ?
					   order by col_order));
    
    $table_name_query->execute($table_id, $prop->panel_id());
    ($table_name) = $table_name_query->fetchrow_array;
    $table_name_query->finish;
    
    my @table_headers;
    push @table_headers, "" if $spreadsheet != 1;

    my $table_frame = $frame->Frame()->pack(-side => 'top');

    $col_query->execute($table_id, $prop->panel_id());
    my %cols;
    while (my($col_name, $col_width) = $col_query->fetchrow_array) {
	push @table_headers, $col_name;
	$cols{$col_name} = $col_width;
    }
    $col_query->finish;
    my $table_row = 1;
    my $colspan = scalar @table_headers;
    if ($spreadsheet != 1 and $table_name !~ /Grades/) {
	my $table_label = $table_frame->Label(-text => $table_name,
					      )->grid(-row => $table_row,
						      -column => 1,
						      -columnspan => $colspan,
						      -sticky => 'nsew');
	
	$table_row++;
    }
    my $ngrade = $num_grades;
    my $row = @$rows[0];
    if ($$row{name} =~ /PrelimGrades/ ) {
      $ngrade = $num_pgrades;
    }
    #print STDERR "\nDBG: $$row{name} --  ngrade=$ngrade\n";

    # having numbers for headers is really repetitive, so don't do it
    if ($$row{name} !~ /grade/i) {
      foreach (my $i = 0; $i < scalar @table_headers ; $i++) {
	$table_frame->Label(-text => $table_headers[$i],
			    -anchor => 'e',
			    -foreground => 'black',
			    -relief => 'sunken',
			    )->grid(-column => $i+1,
				    -row => $table_row,
				    -sticky => 'nsew');
      }
    }
    
    foreach my $row (@$rows) {
	$table_row++;
	my $table_col = 1;

	foreach my $col (@table_headers) {
	    my $index = $table_col - 1;
    	    $index++ if $spreadsheet == 1;
   	    if (($$row{attribute} =~ /prelim_grades/ ||
   	         $$row{attribute} =~ /final_grades/  ) && $index > $ngrade) {
	       $table_col++;
              #print STDERR "dbg: skipping $index...$ngrade";
               next;
            }
              #print STDERR "dbg: $index - $$row{attribute}--  $table_name\n";

	    if ($table_col == 1 and $spreadsheet != 1) { 
              if ($$row{name} !~ /PrelimGrades/ && $$row{name} !~/FinalGrades/) {
		$table_frame->Label(-text => $$row{name},
				    -anchor => 'e',
				    -foreground => 'black',
				    -relief => 'sunken',
				    )->grid(-column => $table_col,
					    -row => $table_row,
					    -sticky => 'nsew');
	      }
	    }
	    else {
		my $index = $table_col - 1;
		$index++ if $spreadsheet == 1;
		if ($$row{edit_flag} =~ /Y/) {
		    my $name = $$row{name} . $index;
		    
		    if ($$row{attribute} =~ /prelim_grades/  ) {
                        
			if ($index < $num_grades ) {
		           my($pp) = $prop->prelim_grade($index);
                           if ($pp && $pp > 0) { $pp=sprintf("%4.2f",$pp); }
			    	$table_frame->Label(-text => $pp,
						-anchor => 'e',
						-foreground => 'black',
						-relief => 'sunken',
						-width => $cols{$col}
						)->grid(-column =>$table_col,
							-row => $table_row,
							-sticky => 'nsew');
			}
			else {
				$table_frame->Label(-text => '',
					    -anchor => 'e',
					    -foreground => 'black',
					    -relief => 'sunken',
					    -width => $cols{$col}
					    )->grid(-column =>
						    $table_col,
						    -row => $table_row,
						    -sticky => 'nsew');
			}

		    }
		    elsif ($$row{attribute} =~ /final_grades/ ) {
			$table_frame->Label(-textvariable =>  
				$prop->final_grade_ref($index),
					    -anchor => 'e',
#					    -foreground => 'red',
					    -foreground => 'black',
					    -relief => 'sunken',
					    -width => $cols{$col}
					    )->grid(-column =>$table_col,
						    -row => $table_row,
						    -sticky => 'nsew');
		    }
		    elsif ($$row{attribute} =~ /joint/) {
			my $obs = lc $table_headers[$index];
			$obs = lc $table_headers[$index-1] if $spreadsheet;
			$row->{name} =~ /(app|req)/i;
			my $member = lc "${obs}_$1";
			$table_frame->Label(-textvariable => 
					    $prop->get_ref($member),
					    -anchor => 'e',
#					    -foreground => 'red',
					    -foreground => 'black',
					    -relief => 'sunken',
					    -width => $cols{$col}
					    )->grid(-column =>$table_col,
						    -row => $table_row,
						    -sticky => 'nsew');
		    }
		    
		}
		else {
		    my $text = "";
		    if ($$row{attribute} =~ /prelim_grades/  && $index <= $num_pgrades) {
			$text = sprintf("%4.2f",$prop->prelim_grade($index)) if 
			    $prop->prelim_grade($index);
		    }
		    elsif ($$row{attribute} =~ /final_grades/ && $index <= $num_grades) {
			$text = sprintf("%4.2f",$prop->final_grade($index)) if 
			    $prop->final_grade($index);
		    }
		    elsif ($$row{attribute} =~ /joint/) {
			my $obs = lc $col;
			$row->{name} =~ /(app|req)/i;
			$text = $prop->get( lc "${obs}_$1" );
		    }
		    $table_frame->Label(-text => $text,
					-anchor => 'e',
					-foreground => 'black',
					-relief => 'sunken',
					-width => $cols{$col}
					)->grid(-column => $table_col,
						-row => $table_row,
						-sticky => 'nsew');
		}
		
	    }
	    $table_col++;
	}
    }
    return $frame;
}

## Class Method ##
# Name: target_table
# Purpose: put a target table into a table cell
# Parameters:row of cell
#            column of cell
#            proposal object
#            number of rows before making scrollable
#            section_id
#            view_id
# Returns: nothing
sub target_table {
    my ($self, $row, $col, $prop, $size, $section_id, $view_id) = @_;
    my $frame = $self->mk_pack_frame($row, $col);
    # Only make a target info table if there are targets
    if ($prop->num_targets > 0) {
	my $get_col_info = $prop->dbh->prepare(qq(select col_order, attribute, 
						  col_name, edit_flag, display,
						  table_id, col_width from 
						  columns c, 
						  section_columns s where 
						  c.col_id = s.col_id and 
						  c.panel_id = s.panel_id and
						  section_id = ? and 
						  s.panel_id = ? and
						  s.view_id = ? and 
						  object = 'target' order by 
						  col_order));
	$get_col_info->execute($section_id, $prop->panel_id(), $view_id);
	my %target_cols;
	my @target_headers;
	while (my($col_order, $attribute, 
		  $col_name, $edit_flag, 
		  $display, $table_id, 
		  $col_width) = $get_col_info->fetchrow_array) {
	    push @target_headers, $col_name;
	    my %col = (attribute => $attribute,
		       name => $col_name,
		       edit_flag => $edit_flag,
		       display => $display,
		       table_id => $table_id,
		       width => $col_width);
	    $target_cols{$col_order} = \%col;
	}
	$get_col_info->finish;
	my $target_row = 1;
	my $target_table;
	if ($prop->num_targets() > $size) {
	    $target_table = new BuildTable($self->verbose, $frame, 
					   \@target_headers,2, 'e', $size);
	}
	else {
	    $target_table = new BuildTable($self->verbose, $frame, 
					   \@target_headers,2, '', $size+1);
	}
	
    	$target_table->print_headers($target_row);
	for (my $i = 1; $i <= $prop->num_targets(); $i++) {
	    $target_row++;
	    my $target_col = 0;
	    foreach my $col_num (sort {$a <=> $b} keys %target_cols) {
		if ($target_cols{$col_num}{display} =~ /label/) {
		    $target_col++;
		    if ($target_cols{$col_num}{edit_flag} =~ /Y/) {
			my $member = $target_cols{$col_num}{attribute};
			my $var_label = 
			    $target_table->table()->Label(-textvariable => 
					  $prop->target($i)->get_ref($member),
							  -anchor => 'w',
#							  -foreground => 'red',
							  -foreground => 'black',
							  -relief => 'sunken',
);

			$target_table->
			    fill_l_table($target_row,
					 [$target_col, $var_label] 
					 );
		    }
		    else {
			my $attribute = $target_cols{$col_num}{attribute};
			my $label = 
			    $target_table->table()->Label(-text => 
						 $prop->target($i)->$attribute,
							  -anchor => 'w',
							  -foreground => 
							  'black',
							  -relief => 'sunken',
							  );
			$target_table->
			    fill_l_table($target_row,
					 [$target_col, $label]);
		    }
		}
		else {
		    print "Can't deal with tables in target_tables yet\n";
		}
	    }
	}
    $target_table->pack(-fill => 'both', -expand => 1);
    $target_table->update;
    }
}

## Class Method ##
# Name: spreadsheet
# Purpose: display a set of fields in spreadsheet form in a cell
# Parameters: intended display (lcd or fac)
#             row of cell
#             column of cell
#             hash of fields
#             proposal object
# Returns: last column populated
sub spreadsheet {
  my ($self, $view, $bpp, $row, $col, $fields, $prop) = @_;
  foreach my $col_num (sort {$a <=> $b} keys %$fields) {
    my $frame = $self->mk_pack_frame;
    if ($$fields{$col_num}{display} =~ /label/) {
      my $attribute = $$fields{$col_num}{attribute};
      my $vv = $view;
      if ($bpp && $bpp =~ /lp/i) { $vv = $view . "_" . lc($bpp); }
      $attribute =~ s/cum_/cum_${vv}_/ if $attribute =~ /cum/;
      if ($$fields{$col_num}{edit_flag} =~ /Y/) {
#	my $color = 'red';
	my $color = 'black';
#	$color = 'dark green' if $attribute =~ /cum/;
	my $label = $frame->Label(-textvariable => 
				  $prop->get_ref($attribute),
				  -anchor => 'e',
				  -foreground => $color,
				  -relief => 'flat',
				 )->pack(-side => 'left',
					 -expand => 1,
					 -fill => 'both');
      }
      else {
	if ($attribute =~ /cum/) {
	  my $label = $frame->Label(-textvariable => 
				    $prop->get_ref($attribute),
				    -anchor => 'e',
#				    -foreground => 'dark green',
				    -foreground => 'black',
				    -relief => 'flat',
				   )->pack(-side =>'left',
					   -expand => 1,
					   -fill => 'both');
	}
	else {
	  my $label = $frame->Label(-text => $prop->get($attribute),
				    -anchor => 'e',
				    -foreground => 'black',
				    -relief => 'flat',
				   )->pack(-side =>'left',
					   -expand => 1,
					   -fill => 'both');
	}
      }
    }
    elsif ($$fields{$col_num}{display} =~ /combo/) {
      my $attribute1 = $$fields{$col_num}{attribute};
      my $attribute2 = $attribute1;
      $attribute2 =~ s/app/req/;
      $attribute2 =~ s/stdev/avg/;
      if ((defined $prop->get($attribute1) && $prop->get($attribute1) >0) ||
          (defined $prop->get($attribute2) && $prop->get($attribute2) >0) ) {

      if ($$fields{$col_num}{edit_flag} =~ /Y/) {
	$frame->Label(-textvariable => 
		      $prop->get_ref($attribute1),
		      -anchor => 'e',
#		      -foreground => 'red',
		      -foreground => 'black',
		      -relief => 'flat',
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');
      }
      else {
	$frame->Label(-text => $prop->get($attribute1),
		      -anchor => 'e',
		      -foreground => 'black',
		      -relief => 'flat',
		     )->pack(-side => 'left',
			     -fill => 'both');
      }
      if ($prop->get($attribute1)) {
	$frame->Label(-text => '/' ,
		      -anchor => 'e',
		      -foreground => 'black',
		      -relief => 'flat',
		     )->pack(-side => 'left',
			     -fill => 'both');
	$frame->Label(-text => $prop->get($attribute2),
		      -anchor => 'e',
		      -foreground => 'black',
		      -relief => 'flat',
		     )->pack(-side => 'left',
			     -fill => 'both');
      }
      }
    }
    elsif ($$fields{$col_num}{display} =~ /multi/) {
      my $attribute1 = $$fields{$col_num}{attribute};
      my $attribute2 = $attribute1 . '_1';
      my $attribute3 = $attribute1 . '_2';


      if ($$fields{$col_num}{edit_flag} =~ /Y/) {
	$frame->Label(-textvariable => 
		      $prop->get_ref($attribute1),
		      -anchor => 'e',
#		      -foreground => 'red',
		      -foreground => 'black',
		      -relief => 'flat',
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');
	if ($prop->get($attribute1) and $prop->get('mcop') eq 'Y') {
	  $frame->Label(-text => '/' ,
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	  $frame->Label(-textvariable => $prop->get_ref($attribute2),
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	  $frame->Label(-text => '/' ,
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	  $frame->Label(-textvariable => $prop->get_ref($attribute3),
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	}
      }
      else {
	$frame->Label(-text => $prop->get($attribute1),
		      -anchor => 'e',
		      -foreground => 'black',
		      -relief => 'flat',
		     )->pack(-side => 'left',
			     -expand => 1,
			     -fill => 'both');
	if ($prop->get($attribute1) and $prop->get('mcop') eq 'Y') {
	  $frame->Label(-text => '/' ,
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	  $frame->Label(-text => $prop->get($attribute2),
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	  $frame->Label(-text => '/' ,
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	  $frame->Label(-text => $prop->get($attribute3),
			-anchor => 'e',
			-foreground => 'black',
			-relief => 'flat',
		       )->pack(-side => 'left',
			       -fill => 'both');
	}
      }
    }
    else {
      my $table_frame = $frame->Frame()->pack();
      my @table_rows = ($$fields{$col_num});
      $table_frame = column_table($table_frame,
				  \@table_rows, $prop, 
				  $$fields{$col_num}{table_id},
				  $fields,
				  $$fields{$col_num}{name});
    }
    $self->table()->put($row, $col, $frame);
    $col++;
  }
  # Subtract off last column added to return the position of the last column
  # in the section
  $col--;
  return $col;
}

## Class Method ##
# Name: print_field
# Purpose: place data into a table cell
# Parameters: row of cell
#             column of cell
#             data
#             justify value
#             relief value
# Returns: nothing
sub print_field {
    my ($self, $row, $col, $field, $justify, $relief) = @_;
    if (!defined($relief)) {
	my $frame = $self->mk_pack_frame();
	$frame->Label(-text => $field,
		      -anchor => $justify,
		      )->pack(-side => 'top',
			      -expand => 1,
			      -fill => 'both');
	$self->table()->put($row, $col, $frame);
    }
    else {
	my $field_text = $self->table()->Label(-text => $field,
					       -anchor => $justify,
					       -relief => $relief,
					       )->pack(-side => 'top',
						       -expand => 1,
						       -fill => 'both');
	$self->table()->put($row, $col, $field_text);
    }
}

## Class Method ##
# Name: print_object
# Purpose: put a widget into a table cell
# Parameters: row of cell
#             column of cell
#             widget
# Returns: nothing
sub print_object {
    my ($self, $row, $col, $obj) = @_;
    $self->table()->put($row, $col, $obj);
}

## Class Method ##
# Name: fill_l_table
# Purpose: put data in a cell with the data being left justified
# Parameters: row of cell
#             array: column of cell and data
# Returns: nothing
sub fill_l_table {
    my $self = shift;
    my $rown = shift;
    foreach my $row (@_) {
	$self->table()->put($rown, $row->[0],$row->[1]);
    }
}

#******************************************************************************
# Subroutine to fill a table with right/left justified columns
#******************************************************************************
## Class Method ##
# Name: fill_rl_table
# Purpose: 
#sub fill_rl_table {
#    my $self = shift;
#    my $rown = shift;
#
#    foreach my $row (@_) {
#	$self->table()->put($rown, 1, mk_rt_label($self->table(), $row->[0]));
#	$self->table()->put($rown, 2, mk_lt_label($self->table(), $row->[1]));
#	$rown++;
#    }
#    $rown;
#}

1;

__END__

=head1 NAME

BuildTable - This is an OO-interface for displaying data in several formats
within a Tk::Table widget

=head1 VERSION

$Revision: 1.31 $

=head1 SYNOPSIS

    use BuildTable;
    my $table = new BuildTable($verbosity,$View, \@headers,'nw',$num_rows,3);

=head1 DESCRIPTION

Provides a convenient interface for displaying proposals in a Tk::Table widget.

=head1 PUBLIC METHODS

=head2 new($verbosity, $mw, \@headers, $scrollbar, $rows, $cols)

Creates a new BuildTable object.

=over 

=item $verbosity - level of verbosity

=item $mw - main window that table goes into

=item \@headers - headers of columns in table

=item $scrollbar - location of scrollbar

=item $rows - number of rows in table

=item $cols - number of columns in table

=back

=head2 pack

packs the table

=head2 update

updates the table

=head2 mk_pack_frame($row, $col)

Returns a frame with pack geometry and puts the frame in position $row, $col
of table

=head2 print_headers($row, $panel, $mkView, $w, $BPP)

Puts the headers of a table into the header row; if one optional parameter is
used, they must all be used

=over 

=item $row - row that headers go into (required)

=item $panel - panel object 

=item $mkView - reference to subroutine to refresh view

=item $w - frame that View goes into

=item $BPP - flag indicating which BPP group the sort applies to

=back

=head2 prop_buttons($row, $col, $prop_id, $quit, $monitor, $panel)

Ties a notebook page to a proposal button and puts the button into the table

=over 

=item $row - row that button goes into

=item $col - column that button goes into

=item $prop_id - proposal id

=item $quit - quit file menu widget

=item $monitor - monitor widget

=item $panel - panel object

=back

=head2 prop_info_new($row, $col, $fields, $prop, $label_width)

Populates a cell using the proposal information layout

=over 

=item $row - row of table cell

=item $col - column of table cell

=item $fields - hash reference of fields to put into cell

=item $prop - proposal object

=item $label_width - width of value column (this is no longer necessary)

=back 

=head2 text($row, $col, $title, $width, $height)

Place a ROText widget into a table cell

=over

=item $row - row of table cell

=item $col - column of table cell

=item $title - title of ROText widget

=item $width - width of ROText widget

=item $height - height of ROText widget

=back

=head2 column_table($frame, $rows, $prop, $table_id, $fields, $table_name)

Puts a data into a gridded layout and returns the frame

=over 

=item $frame - frame to put data into

=item $rows - number of rows

=item $prop - proposal object

=item $table_id - table id in database

=item $fields - hash of fields

=item $table_name - name of table

=back 

=head2 target_table($row, $col, $prop, $size, $section_id, $view_id)

Puts a target table into a table cell

=over 

=item $row - row of table cell

=item $col - column of table cell

=item $prop - proposal object

=item $size - number of rows before making target table scrollable

=item $section_id - section id in database

=item $view_id - view id in database

=back 

=head2 spreadsheet($row, $col, $fields, $prop)

Display a set of fields in spreadsheet format; returns next column to 
populate

=over 

=item $row - row of table cell

=item $col - column of table cell

=item $fields - hash of fields

=item $prop - proposal object

=back

=head2 print_field($row, $col, $field, $justify, $relief)

Places data into a table cell

=over

=item $row - row of table cell

=item $col - column of table cell

=item $field - data

=item $justify - value for anchor parameter

=item $relief - value for relief parameter (optional)

=back

=head2 print_object($row, $col, $obj)

Puts a widget into a table cell

=over

=item $row - row of table cell

=item $col - column of table cell

=item $obj - widget

=back

=head2 fill_l_table($row, [$col, $data])

Puts the data into a cell with the data being left justified

=head1 PRIVATE METHODS

=head2 _init

Initializes a new BuildTable object.

=head1 DEPENDENCIES

This module uses TK::LabEntry and Tk::Notebook (available from CPAN)

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
