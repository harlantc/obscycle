#
# BVNotebook.pm - This is a notebook widget which contains pages for
#                 building views
#
# Purpose: Provides a convenient interface for building views
#          
# Copyright (c) 2006 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package BVNotebook;

use strict;
use warnings;
use Data::Dumper;

use Tk;
use Tk::LabEntry;
require Tk::NoteBook;
require BVPage;

# Create as a singleton object.
our $self;

use vars qw($verbose);

## Class Method ##
# Name: new
# Purpose: create a new BVNotebook object as singleton class
# Parameters: GUI main window
#             sections
#             table
#             database connection
#             panel id
#             verbosity
# Returns: BVNotebook object
sub new {
  my ( $class, $mw, $sections, $table, $dbh, $panel_id, $vb ) = @_;
  
  $self = bless {}, $class;
  
  
  $self->{mw} = $mw;
  $self->{sections} = $sections;
  $self->{table} = $table;
  $self->{dbh} = $dbh;
  $self->{panel_id} = $panel_id;
  $verbose = $vb;

  print "BVNotebook::new - creating new object\n" if $verbose > 2;
  $self;
}

## Class Method ##
# Name: _create
# Purpose: create a new BVNotebook if one doesn't already exist
# Parameters: none
# Returns: BVNotebook object
sub _create {
  unless ( $self->{top} ) {
    $self->{top} = $self->{mw}->Toplevel() unless $self->{top};
    $self->top->title('Build Sections');
    $self->top->protocol( 'WM_DELETE_WINDOW' => sub {} );
    $self->{nb} = $self->top->NoteBook();
  }

  $self;
}


## Class Method ##
# Name: instance
# Purpose: return the BVNotebook widget
# Parameters: none
sub instance { $self };

## Class Method ##
# Name: clean_nb
# Purpose: deletes all the pages from the notebook
# Parameters: none
sub clean_nb {
  my $self = shift;
  my @pages = $self->nb->pages if $self->nb;
  foreach my $page (@pages) {
    $self->nb->delete($page);
  }
}

## Class Method ##
# Purpose: return the notebook widget
# Name: nb
# Parameters: none
sub nb { $_[0]->{nb} }

## Class Method ##
# Purpose: return the top level which contains the notebook widget
# Name: top
# Parameters: none
sub top { $_[0]->{top} }

## Class Method ##
# Purpose: create a page in the notebook (this is where the editing is done)
# Name: build_section
# Parameters: 
# Returns: nothing
sub build_section {
  my ( $self, $id, $sec_lb, $sec_names, $sec_idx ) = @_;

  $self->{sec_lb} = $sec_lb;
  $self->{sec_names} = $sec_names;
  $self->{sec_idx} = $sec_idx;

  $self->_create;

  # don't add section if it already has a page
  if (grep { $id eq $_ } $self->nb->pages) {
    $self->nb->raise( $id );
    unless ( $self->top->ismapped )
      {
	$self->top->deiconify();
	$self->top->raise();
      }
    return;
  }


  my $page = $self->nb->add($id, 
			    -label => "$id",
			    -raisecmd => [focusPage => ($self)]);

  $page->BVPage(   -id	=> $id,
		   -sections => $self->{sections},
		   -table => $self->{table},
		   -dbh => $self->{dbh},
		   -panel_id => $self->{panel_id},
		   -verbose => $verbose,
		   -savecmd	=> [ savePage  => ( $self, $id )],
		   -closecmd	=> [ delPage  => ( $self, $id ) ],
		   -changecmd	=> [ updLabel => ( $self, $id ) ]
		 )->pack(-expand => 1,  -fill => 'both');


  $self->nb->pack( -expand => 1, -fill => 'both');

  $self->nb->raise( $id );

  $self->nb->update(); 
  unless ( $self->top->ismapped )
  {
    $self->top->deiconify();
    $self->top->raise();
  }
}

## Class Method ##
# Purpose: call back to put focus in raised page
# Parameters: none
# Returns: nothing
sub focusPage {
 my ($self) = @_;
 $self->nb->focusNext;
}

## Class Method ##
# Purpose: call back to finish save
# Parameters: none
# Returns: nothing
sub  savePage {
 my ($self, $id) = @_;

 # Need to update the section name array and redisplay the section list box
 $self->{sec_names}->[$id-2] = "$id " . 
   $self->{sections}->{$id}->{section_name};

 $self->{sec_lb}->delete(0,'end');
 my $sel_sec = $self->{sec_idx};
 $self->{sec_lb}->insert( 'end', $self->{sec_names}->[$_] ) foreach @$sel_sec;
}

## Class Method ##
# Purpose: call back which adds an asterisk to the page label if there are 
#          unsaved changes for the proposal
# Name: updLabel
# Parameters: proposal id and changed flag
# Returns: nothing
sub updLabel
{
  my ( $self, $id, $changed ) = @_;
  
  my $label = $self->nb->pagecget( $id, '-label' );
  
  $self->nb->pageconfigure( $id, -label => '*' . $label )
    if $changed && '*' ne substr($label,0,1);
  
  if (! $changed && $label =~ /^[*]/){
    $label =~ s/\*//;
    $self->nb->pageconfigure( $id, -label => $label );
  }
}

## Class Method ##
# Purpose: call back which deletes a page from the notebook
# Name: delPage
# Parameters: section edit page
# Returns: nothing
sub delPage
{
  my ( $self, $page ) = @_;

  $self->nb->delete($page);

  unless ($self->nb()->pages) {
      $self->top->withdraw;
  }
}

1;

__END__

=head1 NAME

BVNotebook - This is a notebook widget which contains pages for building views.

=head1 VERSION

$Revision: 1.5 $

=head1 SYNOPSIS

   use BVNotebook;
   BVNotebook->new( $mw, $sections, $table, $dbh, $panel_id, $verbosity );

=head1 DESCRIPTION

BVNotebook - Provides a convenient interface for updating information for
building views.

=head1 PUBLIC METHODS

=head2 new($mw, $sections, $table, $dbh, $panel_id $verbosity)

Creates a new BVNotebook object as a singleton class.

=over 

=item $mw - top level widget parent of notebook

=item $quit - quit filemenu widget

=back 

=head2 instance()

Returns the BVNotebook object.

=head2 nb()

Returns the notebook widget.

=head2 top()

Returns the top level which contains the notebook widget.

=head2 clean_nb()

Deletes all the pages from the notebook.

=head2 build_section($id, $type)

Creates a page in the notebook for a building a proposal

=over 

=item $id - id for page

=item $type - type of section to build

=back

=head2 delPage($page)

Call back which deletes a page from the notebook.

=over 

=item $page - page to delete

=back

=head1 PRIVATE METHODS

=head2 _create

Creates a new BVNotebook object as a singleton class

=head1 DEPENDENCIES

This module uses TK::LabEntry and Tk::Notebook (available from CPAN)

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2006, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
