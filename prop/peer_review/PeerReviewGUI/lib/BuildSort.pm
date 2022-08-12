#
# BuildSort.pm - This is a widget for creating a new sort
#
# Purpose: Provides a convenient interface for creating sorts with or without
#          grouping.
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package BuildSort;

use strict;
use Carp;
use Data::Dumper;
use base qw/ Tk::Frame /;
use Tk::widgets qw( LabEntry );
require Tk::Dialog;
use Tk::Pane;
require Tk::EnumList;

Construct Tk::Widget 'BuildSort';

use vars qw(@sort_cols $sortby $sort_name $groupby $groupbyLabel $set
	    $sort_cols $group_cols $verbose);

## Class Method ##
# Name: Populate
# Purpose: populate the widget
# Parameters: top level widget
#             -dbh - database handle
#             -panel_id - panel id
#             -verbose - level of verbosity
#             -previewcmd - call back for previewing the new sort
#             -sortmenucmd - call back for adding new sort to sort menu
#             -closecmd - call back for closing widget
# Returns: nothing
sub Populate {
    my ($w, $args) = @_;

    @sort_cols = ();
    $sortby = undef;
    $sort_name = undef;
    $groupby = undef;
    $groupbyLabel = undef;
    $set = undef;

    my $panel_id = $args->{-panel_id};
    my $dbh = $args->{-dbh};
    $verbose = $args->{-verbose};

    $w->ConfigSpecs( -dbh => [ 'PASSIVE'],
		     -panel_id => [ 'PASSIVE' ],
		     -verbose => [ 'PASSIVE' ],
		     -previewcmd => [ 'CALLBACK' ],
		     -lcdsortmenucmd => [ 'CALLBACK' ],
		     -facsortmenucmd => [ 'CALLBACK' ],
		     -closecmd => [ 'CALLBACK' ],
		     );

    $w->SUPER::Populate( $args );
    $sort_cols = $w->getCols($dbh, $panel_id, 'sortby');
    $group_cols = $w->getCols($dbh, $panel_id, 'groupby');

    print "BuildSort::Populate - populating the widget\n" if $verbose > 2;
    # This is the frame where the sort is constructed
    my $build_frame = $w->Scrolled('Frame',
				   -scrollbars => 'ose',
				   -takefocus => 0,
				  )->pack(-side => 'top',
					  -expand => 1,
					  -fill => 'both');

    my $sort_frame = $build_frame->Frame(-relief => 'groove',
					 -borderwidth => 2,
					)->pack( -expand => 1, 
						 -fill => 'both',
						 -side => 'left');

    my $scol_frame = $sort_frame->Frame()->grid(-row => 1,
						-rowspan => 3,
						-column => 2);

    $scol_frame->Label(-text => "Sort by:")->pack();

    my $sort_lb = $scol_frame->Scrolled('Listbox',
					-width => 35,
					-scrollbars => 'oe',
					-takefocus => 0,
					-activestyle => 'none',
				       )->pack(-expand => 1,
					       -fill => 'both',
					       -side => 'top');
    $scol_frame->Button(-text => 'Edit',
			-command => sub {
			  $w->editCols($sort_frame, $sort_lb);
			}
		      )->pack();

    my $group_frame = $build_frame->Frame(-relief => 'groove',
					  -borderwidth => 2
					  )->pack(-side => 'left',
						  -expand => 1,
						  -fill => 'both');
    $group_frame->Label(-text => "Group by:")->pack();

    my $count = 0;
    my $grp_col = $group_cols->{col_names};
    foreach my $col (@$grp_col) {
	$group_frame->Radiobutton(-text => $group_cols->{col_names}->[$count],
				  -value => $group_cols->{col_att}->[$count],
				  -variable => \$groupby,
				  -anchor => 'w',
				  -command => sub {
				    if ($groupby) {
				      my $cnt = 0;
				      my $grps = $group_cols->{col_att};
				      foreach my $grp (@$grps){
					last if $grp eq $groupby;
					$cnt++;
				      }
				      $groupbyLabel = 'GroupedBy' .
					  $group_cols->{col_names}->[$cnt];
				    }
				  }
				 )->pack(-side => 'top',
					 -fill => 'both',
					 -expand => 1,
					);
      $count++;
    }
    $group_frame->Button(-text => 'No Group',
			 -command => sub {
			   $groupby = undef;
			   $groupbyLabel = undef;
			 }
			)->pack(-side => 'top');
    
    # These are the buttons to Save/Close/Preview
    my $button_frame = $w->Frame()->pack(-side => 'bottom');
    my $save = $button_frame->Button(-text => 'Save' , 
				     -command => [ save  => $w  ] 
				     )->pack(-side => 'left',
					      -expand => 1);
    my $close = $button_frame->Button(-text => 'Close',  
				      -command => sub {
				      my @list = $sort_lb->get(0, 'end');
				      if (scalar @list) {
					my $text = 'You have an unsaved sort, do you really want to close?';
					my $dialog = 
					  $w->Dialog(-text => $text,
						     -bitmap => 'question',
						     -default_button => 'Yes',
						     -buttons => [qw /Yes No/ ]
						    );
					my $answer = $dialog->Show;
					'No' eq $answer && return;
					'Yes' eq $answer && $w->close;
				      } else {
					$w->close;
				      }
				    }
				     
				      )->pack(-side => 'left',
					       -expand => 1);
    my $preview = $button_frame->Button(-text => 'Preview' , 
					-command => [ preview => $w ] 
					)->pack(-side => 'left',
						 -expand => 1);
}

## Internal Utility ##
# Name: getCols
# Purpose: retrieves the columns available for sorting by
# Parameters: database handle
#             panel_id
#             sort type
# Returns: array reference to columns
sub getCols {
  my ($w, $dbh, $panel_id, $sort_type) = @_;
  
  my $get_cols_query;
  if ($sort_type !~ /group/) {
    $get_cols_query = qq(select col_id, col_name, attribute from columns 
                          where $sort_type = 'Y' and panel_id = ? and 
                          (pub = 'Y' or col_name='Rank' ) order by col_id);
  } else  {
    $get_cols_query = qq(select col_id, col_name, attribute from columns 
                          where $sort_type = 'Y' and panel_id = ? and 
                          pub = 'Y' order by col_id);
  }
  
  my $get_cols = $dbh->prepare($get_cols_query);
  $get_cols->execute($panel_id);
  my (@col_names, @col_ids, @col_idx, @col_att,@sort_dir);
  
  while (my ($col_id, $col_name, $attribute) = $get_cols->fetchrow_array) {
    $col_name =~ s/\s+//g;
    push @col_names, $col_name;
    push @col_att, $attribute;
    push @col_ids, $col_id;
  }
  $get_cols->finish;
  
  my $cols = {col_names => \@col_names,
	      col_att => \@col_att,
	      col_ids => \@col_ids,
	      col_idx => \@col_idx,
	      sort_dir => \@sort_dir
	     };
  return $cols;
}

## Internal Utility ##
# Name: editCols
# Purpose: Choose the order of sort columns
# Parameters: frame
#             listbox
# Returns: nothing
sub editCols {
  my ($w, $frame, $lb) = @_;

  # edit Sort
  my $d = $frame->DialogBox(-title => 'PR: Columns to Sort On',
			    -buttons => ["OK"]);

  $d->add( 'EnumList',
	   -orderedlist => $sort_cols->{col_idx},
	   -choicelist => $sort_cols->{col_names},
	   -sortbtn => 1,
	   -sortdirection => $sort_cols->{sort_dir},
	   -duplicate => 0)->pack;
  $d->Show;

  # now show selected columns in list box
  $lb->delete(0,'end');
  my $col_idx = $sort_cols->{col_idx};
  my $sort_dir = $sort_cols->{sort_dir};
  #$lb->insert('end', $sort_cols->{col_names}->[$_]) foreach @$col_idx;
  foreach (@$col_idx) {
    my($ii) = $_;
    my($lbl) = $sort_cols->{col_names}->[$_]; 
    foreach (@$sort_dir) {
      if ($_ == $ii) {
        if ($lbl !~ / DESCENDING$/ ) {
         $lbl .= "   DESCENDING";
        }
      }
    }
    $lb->insert('end', $lbl);
  }
}

## Internal Utility ##
# Name: setSort
# Purpose: Composes the sort name and sortby string
# Parameters: none
# Returns: nothing
sub setSort {
    # Compose sort name and sort by list
    $sort_name = '';
    $sortby = '';

    my $cols = $sort_cols->{col_idx};
    my $sortdir = $sort_cols->{sort_dir};
    if (!scalar @$cols) {
	$sort_name = 'Prop#';
	$sortby = 'prop_id';
    }

    # Put cols with same order into a string
    foreach my $idx (@$cols) {
      # grade columns have descending sorts    
      my $sort_col = $sort_cols->{col_att}->[$idx];
      $sort_col .= '_sort' if $sort_col =~ /type|big_proj|triage/;

      my ($sort_order) = "";
      foreach my $sidx  (@$sortdir) {
         if ($sidx == $idx) {
           if ($sort_col !~ / DESC$/ ) {
             $sort_col .= ' DESC' ;
             $sort_order = '_d' ;
           }
         }
      }

      #$sort_col .= ' DESC' if $sort_col =~ /avg|norm/;
   
      # these columns use a sort column to have a special sort
      $sort_name .= $sort_cols->{col_names}->[$idx] . $sort_order . '+';
      $sortby .= $sort_col . ',';
      
    }

    $sortby =~ s/,$//;

    if ($sortby !~ /prop_id$/) {
	$sortby .= ',prop_id';
    }

    # Add groupby Label
    if ($groupbyLabel) {
	$sort_name .= $groupbyLabel;
    }
    else {
	$sort_name =~ s/\+$//;
    }
}

## Internal Utility ##
# Name: save
# Purpose: saves the new sort to the database
# Parameters: none
# Returns: nothing
sub save
{
    my $w = shift;
    my $panel_id  = $w->cget('-panel_id');
    my $dbh = $w->cget('-dbh');

    # Get the next sort_id for the panel
    my $get_next_sort = $dbh->prepare(qq(select max(sort_id)+1 from sorts 
					 where panel_id = ?));
    $get_next_sort->execute($panel_id);
    my ($sort_id) = $get_next_sort->fetchrow_array;
    $get_next_sort->finish;

    # Set sort name and sort by
    setSort();

    # Check that the sort doesn't already exist
    my $count;
    if (!$groupby) {
	my $query = $dbh->prepare(qq(select count(*) from sorts where 
				     panel_id = ? and sortby = ?));
	$query->execute($panel_id, $sortby);
	($count) = $query->fetchrow_array;
	$query->finish;
    }
    else {
	my $query = $dbh->prepare(qq(select count(*) from sorts where 
				     panel_id = ? and sortby = ? and 
				     groupby = ?));
	$query->execute($panel_id, $sortby, $groupby);
	($count) = $query->fetchrow_array;
	$query->finish;
    }
    if ($count > 0) {
	$w->messageBox(-title => 'PR: Data Entry Error',
		       -message => "This sort already exists in database"
		       );
    }
    else {
	# Insert sort
	my $insert_sort = $dbh->prepare(qq(insert into sorts values 
					   (?, ?, ?, ?, ?)));
        #print STDERR ("BuildSort: $sort_id, $panel_id, $groupby, $sortby, $sort_name\n");
	$insert_sort->execute($sort_id, $panel_id, $groupby, $sortby, 
			      $sort_name);
	$insert_sort->finish;
	
	# Update Menus
	$w->Callback( '-lcdsortmenucmd', $sort_name, $sortby, $groupby );
	$w->Callback( '-facsortmenucmd', $sort_name, $sortby, $groupby );
	
	# Close
	defined $w->cget( '-closecmd' ) ? $w->Callback( '-closecmd' ) : 
	    $w->destroy;
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
# Purpose: Preview the new sort before saving
# Parameters: none
# Returns: nothing
sub preview
{
    my $w = shift;
    setSort();
    $w->Callback( '-previewcmd', $sortby, $groupby );
}

1;

__END__

=head1 NAME

BuildSort - This is a widget for creating sorts

=head1 VERSION

$Revision: 1.10 $

=head1 SYNOPSIS

   use BuildSort;
   my $buildsort = $mw->Toplevel();
   $buildsort->BuildSort(-dbh => $dbh1,
			  -panel_id => $panel_id,
			  -previewcmd => [\&previewSort => ($self)],
			  -sortmenucmd => [\&addSortMenu => ($self) ],
			  -closecmd => [ \&closeBuildSort => ($self) ],
			 )->pack(-expand => 1,
				 -fill => 'both');

=head1 DESCRIPTION

Provides a convenient interface for creating sorts with or without grouping

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

   -previewcmd - call back for previewing the new sort

   -sortmenucmd - call back for adding new sort to sort menu

=back

=head1 PRIVATE METHODS

=head2 getCols

Retrieves from database columns available for sorting by

=head2 setSort

Composes sort name and sortby list based on columns selected

=head2 save

Saves new sort to database

=head2 close

Call back to destroy page

=head2 preview

Preview the sort before saving

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
