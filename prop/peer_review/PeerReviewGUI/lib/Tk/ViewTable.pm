#
# Tk::ViewTable.pm - This is a widget which adds functions to a Tk::Table
#
# Purpose: Adds focus to children within the table
#          
# Copyright (c) 2005 Diab Jerius
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Tk::ViewTable;

use strict;
use warnings;

use Tk;
use Tk::widgets qw/ Table /;

use base qw/ Tk::Derived Tk::Table/;

Construct Tk::Widget 'ViewTable';

## Class Method ##
# Name: ClassInit
# Purpose: overrides buttons so that clicking on them causes focus to shift 
#          to it
# Parameters: none
# Returns: nothing
sub ClassInit
{
  my ( $class, $mw ) = @_;

  $class->SUPER::ClassInit($mw);

  # clicking on this causes focus to shift to it
  $mw->bind( $class, '<Button-1>', [ 'focus' ] );

}

## Class Method ##
# Name: Populate
# Purpose: populates the widget
# Parameters: none
# Returns: nothing
sub Populate
{
  my ( $self, $args ) = @_;

  $self->SUPER::Populate( $args );

  # set up a tagged binding for children which will refocus to the
  # table widget if they don't take focus when Button1 is clicked.
  my $tag = 'Focus-' . $self->PathName;

  $self->privateData->{bindtag} = $tag;

  $self->bind( $tag, '<Button-1>', [ \&tableFocus, $self ] );
  $self->bind( $tag, '<Escape>',  sub{ $self->focus } );
}


## Class Method ##
# Name: tableFocus
# Purpose: call back which sets focus on table if widget doesn't take focus
# Parameters: none
# Returns: nothing
sub tableFocus {
  my ( $w, $table ) = @_;

  # do nothing if the widget can take the focus
  return if $w->FocusOK;

  # else, set focus to table.
  $table->focus;
}

## Internal Utility ##
# Name: put
# Purpose:
# Here's where step 1 of the magic begins.  we need to override
# the 'Tab' and 'LeftTab' bindings for the children of the table
# so that we can direct focus motion along the grid, rather than
# in the "natural" order.

# The tricky part is when the child widget is a container.  We only
# care about the first and last grandchildren, as those are the ones
# where a tab focus transition (either forward or backward) will
# interact with our grid based motion.  Essentially, we want LeftTab
# transitions from the first grandchild and Tab transitions from the
# last grandchild to obey our focus rules.  Focus motion within the
# container is not our purview.

# The first and last grandchildren are determined from the container's
# list of children when the container is "put" into the table.  The
# criteria are simple: they are the first and last children which are
# able to take focus (-takefocus => 1).  They need not be currently
# viewable. (This may be a problem if they're not viewable when focus
# is directed at them, but that's a problem for another day.)  Note
# that if the container does anything to its children which breaks
# the above criteria, strange behavior will ensue.
sub put
{
  my ( $self, $row, $col, $w ) = @_;

  my $old = $self->SUPER::put( $row, $col, $w );

  # we want the actual widget.  if $w is a string, SUPER::put will
  # create one

  $w = $self->get( $row, $col )
    unless Exists( $w );

  # firstFocus and lastFocus will be the same if the widget
  # is not a container, else they will be the first and last
  # grandchildren.  it is important that if $w is a container
  # that it NOT change the list of children, as that is hard
  # wired here.

  my $first = firstFocus( $w );
  my $last  = lastFocus( $w );

  # if this widget has no children and doesn't take focus,
  # $first == $last == undef.

  $last->bind( '<Tab>', 
	       sub { $self->focusNextEntry($w,1);
		     $self->break;
		   })
    if defined $last;

  $first->bind( '<<LeftTab>>', 
		sub { $self->focusNextEntry($w,-1);
		      $self->break;
		    } )
    if defined $first;

  # and add refocus binding
  my $tag = $self->privateData->{bindtag};

  $w->bindtags( [$tag, $w->bindtags ] );

  $old;
}


## Internal Utility ##
# Name: focusNextEntry
# Purpose: puts focus on next or previous entry
# Parameters: entry
#             direction
# Returns: nothing
sub focusNextEntry
{
  my ( $self, $entry, $dir ) = @_;

  # $dir is -1 or 1 for previous or next.

  # get table size; we need this to navigate to the
  # next entry
  my $nrows = $self->totalRows;
  my $ncols = $self->totalColumns;

  # determine address of next entry, if we're at the extrema
  # of a row, move to the next or previous row.  if we can't
  # do that, we're out of entries and move to the next top level
  # focus

  my ( $row, $col ) = $self->Posn($entry);

  my $next;
  while(1)
  {
    $col += $dir;
    if ( $col == $ncols || $col < 0 )
    {
      $col = $dir > 0 ? 0 : $ncols-1 ;

      $row += $dir;

      if ($row == $nrows)
      {
	$self->configure( -takefocus => 1 );
	$self->focusNext;
	$self->configure( -takefocus => 0 );
	return;
      }
      elsif ($row < 0)
      {
	$self->configure( -takefocus => 1 );
	$self->focusPrev;
	$self->configure( -takefocus => 0 );
	return;
      }
    }

    # grab the widget to pass the focus to
    $next = $self->get($row, $col);

    next unless defined $next;

    $next = $dir == -1 ? lastFocus($next) : firstFocus($next);

    last if defined $next;
  }


  my $from =  $entry->isa('Tk::Entry') ? ${$entry->cget('-textvariable')} : '';
  my $to   =  $next->isa('Tk::Entry') ? ${$next->cget('-textvariable')} : '';

  # make sure its visible.
  $self->see( $row, $col );

  # we need to do this in order to get the visible
  # selection stuff to happen.  dunno why.
  $self->update;

  # focus on the new widget
  $next->focus;
}

## Internal Utility ##
# Name: firstFocus
# Purpose: inefficient means of finding the first child of a parent which will
#          accept focus.  this will return the parent if the parent will
#          accept focus.
sub firstFocus
{
  my $w = shift;

  return $w if myFocusOK($w);

  our @children = $w->FocusChildren;

  while ( @children )
  {
    $w = shift @children;

    return $w if myFocusOK($w);

    unshift @children, $w->FocusChildren;
  }

  undef;
}

## Class Method ##
# Name: lastFocus
# Purpose: inefficient means of finding the last child of a parent which will
#          accept focus.  this will return the parent if the parent will
#          accept focus.
sub lastFocus
{
  my $w = shift;

  return $w if myFocusOK($w);

  our @children = $w->FocusChildren;

  while ( @children )
  {
    $w = pop @children;

    return $w if myFocusOK($w);

    push @children, $w->FocusChildren;
  }

  undef;
}

# 
## Internal Utility ##
# Name: myFocusOK
# Purpose: based upon Tk::FocusOK, but doesn't care if the widget is viewable
# Parameters: none
# Returns: returns true if takefocus is successful
sub myFocusOK
{
 my $w = shift;
 my $value;
 Tk::catch { $value = $w->cget('-takefocus') };
 if (!$@ && defined($value))
  {
   return 0 if ($value eq '0');
   return 1 if ($value eq '1');
   if ($value)
    {
     $value = $w->$value();
     return $value if (defined $value);
    }
  }
 Tk::catch { $value = $w->cget('-state') } ;
 if (!$@ && defined($value) && $value eq 'disabled')
  {
   return 0;
  }
 $value = grep(/Key|Focus/,$w->Tk::bind(),$w->Tk::bind(ref($w)));
 return $value;
}

1;

__END__

=head1 NAME

Tk::ViewTable - Add functions to a Tk::Table widget

=head1 VERSION

$Revision: 1.2 $

=head1 SYNOPSIS

   require Tk::ViewTable
   $table = $frame->Table(-rows => 6,
			  -columns => 13,
			  -fixedrows => 2,
			  -scrollbars => 'w',
			  -takefocus => 0,
			  )->pack(-side => 'top');

=head1 DESCRIPTION

Adds methods to shift and retain focus on widgets within the table.

=head1 PUBLIC METHODS

=head2 Populate

Sets up a tagged binding for children which will refocus to the table widget
if they don't take focus when Button1 (left mouse button) is clicked

=head2 tableFocus

Call back which sets focus on the table if the widget doesn't take focus

=head1 PRIVATE METHODS

=head2 ClassInit

Overrides buttons so that clicking on them causes focus to shift to it

=head2 put

Overrides the put command of a Table widget to get the focus to step through
children in a table

=head2 focusNextEntry

Puts focus on next or previous entry

=head2 firstFocus

Finds first child of a parent which will accept focus.

=head2 lastFocus

Finds last child of a parent which will accept focus.

=head2 myFocusOK

Returns true if takefocus is successful

=head1 DEPENDENCIES

This module has no dependencies.

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Diab Jerius

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Diab Jerius
