#
# BVPage.pm - This widget populates a build view page
#
# Purpose: Provides a convenient interface for building a section in a view
#          
# Copyright (c) 2006 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package BVPage;

use strict;
use warnings;

use Carp;
use Data::Dumper;
use base qw/ Tk::Frame /;
use Regexp::Common qw/number/;

require Tk::Dialog;
require Tk::Pane;
require Tk::EnumList;

Construct Tk::Widget 'BVPage';
use vars qw($verbose);
# Description of global variables:

## Class Method ##
# Name: Populate
# Purpose: populate the notebook page 
# Parameters: the notebook page
#             -id - page id
#             -sections
#             -table
#             -panel_id
#             -dbh
#             -verbose
# Returns: nothing
sub Populate {
  my ($w, $args) = @_;
  my $id = $args->{-id};
  my $table = $args->{-table};
  my $sections = $args->{-sections};
  my $panel_id = $args->{-panel_id};
  my $section = $$sections{$id};
  my $dbh = $args->{-dbh};
  $verbose = $args->{-verbose};

  $w->ConfigSpecs( -id => [ 'PASSIVE' ],
		   -sections => [ 'PASSIVE' ],
		   -table => [ 'PASSIVE' ],
		   -panel_id => [ 'PASSIVE' ],
		   -dbh => [ 'PASSIVE'],
		   -verbose => [ 'PASSIVE' ],
		   -savecmd	=> [ 'CALLBACK' ],
		   -closecmd => [ 'CALLBACK' ],
		   -changecmd	=> [ 'CALLBACK' ]
		   );

  $w->SUPER::Populate( $args );
  my $pData = $w->privateData;
  my $chg = $pData->{chgData} = {};
  my $data = $pData->{secData} = {};
  $w->reset( $section );

  print "BVPage::Populate - populating the page\n" if $verbose > 2;
  # Put a scrollbar at the top of the page
  my $SFrame = $w->Frame();
  $SFrame->Label(-text => $sections->{$id}->{section_label},
		 )->pack(-side => 'top');

  my $frame = $SFrame->Frame(-relief => 'groove',
			     -borderwidth => 2);

  my $sel_cols = $data->{cols}->{col_idx};
  my @sel_cols = @$sel_cols;
  my $col_names = $data->{cols}->{col_names};
  my @col_names = @$col_names;

  $frame->LabEntry(-label => 'Section Name (< 50 char)',
		   -labelPack => [qw/-side left -anchor w/],
		   -textvariable => \$sections->{$id}->{section_name},
		   -width => 20,
		   -validate => 'key',
		   -validatecommand => sub {
		     # Check character being typed in for quotes
		     if ($_[1]) { return $_[1] !~ /\'|\"/; }
		     # This allows the string to be re-editted
		     else {return;}
		   },
		   -invalidcommand => sub {$SFrame->bell}
		  )->grid(-row => 3,
			  -column => 1);
  
  my $col_frame = $frame->Frame()->grid(-row => 1,
					-rowspan => 3,
					-column => 2);
  my $text = ucfirst($sections->{$id}->{section_type}) . " Columns";
  $text = 'Running Total Columns' if $sections->{$id}->{object} =~ /run_tot/;
  $text = 'Combination Columns' if $sections->{$id}->{object} =~ /combo_cols/;
  $col_frame->Label(-text => $text)->pack();
  my $col_lb = $col_frame->Scrolled('Listbox', 
				    -scrollbars => 'oe',
				    -takefocus => 0,
				    -activestyle => 'none',
				   )->pack( -expand => 1, 
					    -fill => 'both',
					    -side => 'top' );
  $col_lb->insert( 'end', $col_names->[$_] ) foreach @sel_cols;
  
  $col_frame->Button ( -text => 'Edit',
		       -command => sub { 
			 $w->editColumns( $frame, $col_lb,
					  \@sel_cols, \@col_names
					);
		       }
		     )->pack();

  # These are the buttons at the bottom of the page (last row of the grid)
  my $BFrame = $w->Frame();
  $BFrame->configure( -relief => 'raised', -bd => 1 );
  $BFrame->Button( -text => 'Save' ,
		   -takefocus => 1,
		   -command => [ save  => $w ] 
		   )->grid( -row => 0, -column => 0, -ipadx => 2);
  $BFrame->Button( -text => 'Close',
		   -takefocus => 1,
		   -command => [close => $w ]
		 )->grid( -row => 0,
			  -column => 1, -ipadx => 2);
  $BFrame->Button( -text => 'Reset' ,
		   -takefocus => 1, 
		   -command => [ reset => $w ] 
		   )->grid( -row => 0, -column => 2, -ipadx => 2);


  $BFrame->pack( -side => 'bottom', -fill => 'x' );
  $frame->pack( -expand => 1, -fill => 'both' );
  $SFrame->pack( -expand => 1, -fill => 'both' );
}

## Internal Utility ##
# Name: reset
# Purpose: reset the value in the widget to the original value
# Parameters: sections hash ref
# Returns: nothing
sub reset
{
  my $w = shift;
  my $obj = shift;
  if (!$obj) {
    my $id = $w->cget(-id);
    my $sections = $w->cget(-sections);
    $obj = $sections->{id};
  }
  
  my $pData = $w->privateData;
  my $data = $pData->{secData};
  my $chg = $pData->{chgData};
  
  $data->{cols} = $obj->{cols};
  $chg->{cols} = 0;

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
  my $data = $pData->{secData};
  my $chg = $pData->{chgData};
  my $changed = 0;

  foreach my $key (keys %$chg) {
    $changed++ if $chg->{$key};
  }

  $changed = 1 if $changed > 0;
  return $changed;
}

## Internal Utility ##
# Name: save
# Purpose: save all changes to proposal object and database
# Parameters: none
# Returns: nothing
sub save
{
  my $w = shift;
  my $id = $w->cget('-id');
  my $sections = $w->cget(-sections);
  my $table = $w->cget(-table);
  my $dbh = $w->cget(-dbh);
  my $panel_id = $w->cget(-panel_id);

  my $pData = $w->privateData;
  my $chg = $pData->{chgData};
  my $data = $pData->{secData};

  if ($chg->{cols}) {
    $sections->{$id}->{cols}->{col_idx} = $data->{cols}->{col_idx};

    my $col_ref = $data->{cols}->{col_idx};
    if (scalar @$col_ref) {
      my $col_frame = $table->mk_pack_frame($id+1, 4);
      my $count = 0;
      foreach my $idx (@$col_ref) {
	$count++;
	my $label = $count . ": " . 
	  $sections->{$id}->{cols}->{col_names}->[$idx];
	$col_frame->Label(-text => $label,
			  -anchor => 'w')->pack(-side => 'top',
						-expand => 1,
						-fill => 'both');
      }
    }
    else {
      $table->print_field($id+1, 4, 'No Columns Selected', 'w');
    }
    $table->update;
  }

  $w->reset;    # reset the private data and change values
  $w->Callback( '-changecmd', $w->changed_data );
  $w->Callback( '-savecmd' );
}

## Internal Utility ##
# Name: editColumns
# Purpose: Edit the columns choosen for a section
# Parameters: frame
#             listbox
#             array ref of selected columns
#             array ref of available column names
# Returns: nothing
sub editColumns
{
  my ( $w,$frame, $lb, $sel_cols, $col_names ) = @_;

  my $pData = $w->privateData;
  my $data = $pData->{secData};
  my $chg = $pData->{chgData};

  $data->{cols}->{col_idx} = [];
  # edit Columns
  my $d = $frame->DialogBox( -title => 'PR: Edit Columns',
			     -buttons => ["OK"] );

  $d->add( 'EnumList', 
	   -orderedlist => $sel_cols,
	   -choicelist => $col_names,
	   -duplicate => 0 )->pack;
	   
  $d->Show;
  
  # now show columns in list box
  $lb->delete(0,'end');

  foreach (@$sel_cols) {
    $lb->insert( 'end', 
		 $col_names->[$_] );
    my $dcols = $data->{cols}->{col_idx};
    push @$dcols, $_;
    $data->{cols}->{col_idx} = $dcols;
  }

  $chg->{cols} = 1;
  $w->Callback( '-changecmd', $w->changed_data );
}

## Internal Utility ##
# Name: close
# Purpose: call back to destroy page
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

BVPage - This widget populates a build view page.

=head1 VERSION

$Revision: 1.6 $

=head1 SYNOPSIS

   require BVPage;
   my $self = BVNotebook->new( $mw, $quit );
   my $page = $self->nb->add($id, -label => "$id");

   $page->BVPage( -type	=> $type,
 		  -id => $id,
		  -closecmd => [ delPage  => ( $self, $id ) ],
		)->pack(-expand => 1,  -fill => 'both');

=head1 DESCRIPTION

BVPage - Provides a convenient interface for building sections of a view.

=head1 PUBLIC METHODS

=head2 Populate($w, $args)

Populates the notebook page

=over 

=item $w - the notebook page

=item $args - reference to hash of arguments

Need to fill this in

=back

=head1 PRIVATE METHODS

=head2 fill_rl_grid

Populates a grid with the first column right justified and the second column
left justified

=head2 myEntry

Entry widget retains focus until the data passes validation

=head2 validate

Checks the validity of data in a myEntry widget

=head2 mkFrame

Returns a frame

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

Copyright (c) 2006, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
