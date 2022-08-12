#
# EditNotebook.pm - This is a notebook widget which contains pages for
#                   editing proposals
#
# Purpose: Provides a convenient interface for updating information for 
#          proposals
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package EditNotebook;

use strict;
use warnings;
use Data::Dumper;

use Tk;
use Tk::LabEntry;
require Tk::NoteBook;
require EditPage;

# Create as a singleton object.
our $self;

use vars qw($verbose);

## Class Method ##
# Name: new
# Purpose: create a new EditNotebook object as singleton class
# Parameters: GUI main window
#             quit filemenu widget
#             verbosity
# Returns: EditNotebook object
sub new {
  my ( $class, $mw, $quit, $vb ) = @_;
  
  $self = bless {}, $class;
  
  
  $self->{mw} = $mw;
  $self->{quit} = $quit;
  $verbose = $vb;
  print "EditNotebook::new - create new widget\n" if $verbose > 2;
  $self;
}

## Class Method ##
# Name: _create
# Purpose: create a new EditNotebook if one doesn't already exist
# Parameters: none
# Returns: EditNotebook object
sub _create {
  unless ( $self->{top} ) {
    $self->{top} = $self->{mw}->Toplevel() unless $self->{top};
    $self->top->title('Edit Notebook');
    $self->top->protocol( 'WM_DELETE_WINDOW' => sub {} );
    $self->{nb} = $self->top->NoteBook();
    $self->top->geometry("1350x700");
  }

  $self;
}


## Class Method ##
# Name: instance
# Purpose: return the EditBook widget
# Parameters: none
sub instance { $self };

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
# Name: edit_prop
# Parameters: proposal id and panel object
# Returns: nothing
sub edit_prop {
  my ( $self, $prop_id, $panel ) = @_;

  $self->_create;

  $self->{quit}->configure(-state => 'disabled');

  # don't add page if it already exists
  return if grep { $prop_id eq $_ } $self->nb->pages;

  my $pi = $panel->proposals->proposal($prop_id)->last_name;
  # pi can be null for dual anonymous.
  $pi = "" unless $pi;

  my $page = $self->nb->add($prop_id, 
			    -label => "$prop_id\n$pi",
			    -raisecmd => [focusPage => ($self)]);

  $page->EditPage( -panel	=> $panel,
		   -prop_id	=> $prop_id,
		   -verbose     => $verbose,
		   -savecmd	=> [savePage  => ( $self, $prop_id, $panel )],
		   -closecmd	=> [ delPage  => ( $self, $prop_id ) ],
		   -changecmd	=> [ updLabel => ( $self, $prop_id ) ]
		 )->pack(-expand => 1,  -fill => 'both');


  $self->nb->pack( -expand => 1, -fill => 'both');

  $self->nb->raise( $prop_id );

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
# Purpose: call back which saves changes made on page to database
# Name: savePage
# Parameters: proposal id and panel id
# Returns: nothing
sub savePage {
    my ($self, $propId, $panel ) = @_;
    $panel->update_stats();
}

## Class Method ##
# Purpose: call back which adds an asterisk to the page label if there are 
#          unsaved changes for the proposal
# Name: updLabel
# Parameters: proposal id and changed flag
# Returns: nothing
sub updLabel
{
  my ( $self, $propId, $changed ) = @_;
  
  my $label = $self->nb->pagecget( $propId, '-label' );
  
  $self->nb->pageconfigure( $propId, -label => '*' . $label )
    if $changed && '*' ne substr($label,0,1);
  
  if (! $changed && $label =~ /^[*]/){
    $label =~ s/\*//;
    $self->nb->pageconfigure( $propId, -label => $label );
  }
}

## Class Method ##
# Purpose: call back which deletes a page from the notebook
# Name: delPage
# Parameters: proposal edit page
# Returns: nothing
sub delPage
{
  my ( $self, $page ) = @_;

  $self->nb->delete($page);

  unless ($self->nb()->pages) {
      $self->top->withdraw;
      $self->{quit}->configure(-state => 'normal');
  }
}

1;

__END__

=head1 NAME

EditNotebook - This is a notebook widget which contains pages for editing 
proposals

=head1 VERSION

$Revision: 1.7 $

=head1 SYNOPSIS

   use EditNotebook;
   EditNotebook->new( $mw, $quit );

=head1 DESCRIPTION

EditNotebook - Provides a convenient interface for updating information for
proposals.

=head1 PUBLIC METHODS

=head2 new($mw, $quit, $verbosity)

Creates a new EditNotebook object as a singleton class.

=over  

=item $mw - GUI main window

=item $quit - quit filemenu widget

=item $verbosity - level of verbosity

=back 

=head2 instance()

Returns the EditNotebook object.

=head2 nb()

Returns the notebook widget.

=head2 top()

Returns the top level which contains the notebook widget.

=head2 edit_prop($prop_id, $panel_id)

Creates a page in the notebook for a proposal

=over 

=item $prop_id - proposal id for page

=item $panel - panel object

=back


=head2 savePage($prop_id, $panel)

Call back which saves changes made on page to database

=over  

=item $prop_id - proposal id for page

=item $panel - panel object

=back


=head2 updLabel($prop_id, $panel)

Call back which adds an asterisk to the page label if there are unsaved
changes for the proposal.

=over

=item $prop_id - proposal id for page

=item $panel - panel object

=back

=head2 delPage($page)

Call back which deletes a page from the notebook.

=over  

=item $page - page to delete

=back

=head1 PRIVATE METHODS

=head2 _create

Creates a new EditNotebook object as a singleton class

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
