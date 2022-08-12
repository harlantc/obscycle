#
# EnumList.pm - This is a widget for displaying items and adding them to a list
#
# Purpose: Provides a convenient interface for selecting items to add to a 
#          list and provides an interface for editing the list
#          
# Copyright (c) 2005 Diab Jerius
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Tk::EnumList;

use Carp;

use Tk 804.027;
use Tk::Widget;
require Tk::Frame;
require Tk::LabFrame;
require Tk::Button;
require Tk::Checkbutton;
use base qw/ Tk::Frame /;

Construct Tk::Widget 'EnumList';

use strict;
use warnings;

## Class Method ##
# Name: Populate
# Purpose: populate the widget
# Parameters: top level widget
#             -orderedlist - initial ordered list of items
#             -choicelist - list of items to choose from
#             -duplicate - are duplicate values allowed
#             -aspectratio - the aspect ratio
#             -nrows - number of rows to display data in
#             -ncols -number of columns to display data in
# Returns: nothing
sub Populate {
  my ( $w, $args ) = @_;

  my $def_aspectratio = 8/3;

  # our private stash
  my $pd = $w->privateData;

  # the initial ordered list
  my $orderedlist = delete $args->{-orderedlist} || [];

  # the list of choices
  my $choices  = $pd->{choicelist} = delete $args->{-choicelist};
  

  # are duplicate values allowed?
  my $duplicate = $pd->{duplicate} = delete $args->{-duplicate};
  $duplicate = 0 unless defined $duplicate;

  # the aspect ratio
  my $aspectratio  = delete $args->{-aspectratio} || $def_aspectratio;

  my $sortbtns = delete $args->{-sortbtn};
  $sortbtns = 0 unless defined $sortbtns;

  $pd->{sortdirection} = delete $args->{-sortdirection} || [];

  # requested columns and rows
  my $ncols        = delete $args->{ncols};
  my $nrows        = delete $args->{nrows};

  # go up stream
  $w->SUPER::Populate( $args );

  # verify some input parameters
  croak( "-orderedlist must be an arrayref" )
    unless 'ARRAY' eq ref $orderedlist;

  croak( "-choicelist must be specified and must be an array ref" )
    unless defined $choices && 'ARRAY' eq ref $choices;

  # ensure that the input list of ordered list values is a subset of the
  # possible choices
  croak( "input -orderedlist contains elements not in -choicelist" )
    if grep { $orderedlist->[$_] < 0 || $orderedlist->[$_] >= @$choices }
	  0..@$orderedlist-1;

  # if not in duplicate mode, complain if there are duplicates in the
  # input list
  if ( ! $duplicate )
  {
    my %dups;
    $dups{$_}++ foreach @$orderedlist;
    croak( "duplicate elements in -orderedlist" )
      if grep { $_ > 1 } values %dups;
  }

  # calculate the dimensions of the choices grid and stash them
  ( $ncols, $nrows ) = calcGrid( $aspectratio, $ncols, $nrows, 
				 scalar @$choices);
  $pd->{ncols} = $ncols;
  $pd->{nrows} = $nrows;

  # create various subwidgets.  we don't advertise them.
  my $wlist   = $w->OrderedList( $nrows );
  my $mkgrid  = $duplicate ? \&ButtonGrid : \&CheckbuttonGrid ;
  my $wchoice = $mkgrid->( $w, $ncols, $nrows, $choices, $orderedlist );

  $wlist->grid(-row => 0,   -column => 0, -sticky => 'nsew' );
  $wchoice->grid(-row => 0, -column => 1, -sticky => 'nsew',
		 -rowspan => 2
		);

  my $bfr = $w->Frame( -relief => 'groove', -borderwidth => 1 );
  $w->listButtons($sortbtns)->pack( -in => $bfr, -expand => 1, -fill => 'x' );
  $w->allButtons->pack( -in => $bfr, -expand => 1 , -fill => 'x' );
  $bfr->grid( -row => 1, -column => 0, -sticky => 'nsew' );

  # make sure that the listbox gets the focus.  we've turned focus off
  # everywhere else
  $w->bind('<Enter>', sub { $wlist->focus } );

  # initialize the list box. play a shell game with $pd->{orderedlist}
  # as it should eventually point to the user supplied ordered list,
  # but addOrderedEntries will add to it, which will lead to duplicated
  # entries.
  $pd->{orderedlist} = [];
  $w->addOrderedEntries( $orderedlist );
  $pd->{orderedlist} = $orderedlist;

  # deactiveate widgets that require a selected entry in the ordered
  # list listbox
  $w->setSelectState;
}

## Class Method ##
# Name: nrows
# Purpose: access nrows
# Parameters: nothing
# Returns: nrows
sub nrows { $_[0]->privateData->{nrows} }

## Class Method ##
# Name: ncols
# Purpose: access ncols
# Parameters: nothing
# Returns: ncols
sub ncols { $_[0]->privateData->{ncols} }

## Class Method ##
# Name: get
# Purpose: access orderedlist
# Parameters: nothing
# Returns: ordered array
sub get   { @{$_[0]->privateData->{orderedlist}} }

## Class Method ##
# Name: OrderedList
# Purpose: create the listbox holding the ordered list
# Parameters: height
# Returns: LabFrame
sub OrderedList
{
  my ( $w, $height ) = @_;

  my $fr = $w->LabFrame( -label => 'Selected' );
  my $list = $w->privateData->{list} = 
    $fr->Scrolled('Listbox', 
		  -scrollbars => 'oe',
		  -takefocus => 0,
		  -activestyle => 'none',
		  -height => $height,
		 )->pack( -expand => 1, -fill => 'both',
			);

  $list->Subwidget('yscrollbar')->configure(-takefocus => 0);
  $list->bind('<Escape>' => sub { $list->selectionClear(0, 'end');
				  $w->setSelectState } );
  $list->bind( '<<ListboxSelect>>', sub { $w->setSelectState } );

  $fr->Delegates( 'DEFAULT' => $list );

  $fr;
}

## Class Method ##
# Name: listButtons
# Purpose: create some buttons to manage the list
# Parameters: none
# Returns: Frame
sub listButtons
{
  my ( $w, $sortbtns ) = @_;

  my $fr = $w->Frame( -borderwidth => 2 );

  my %common = ( -padx => 2,
		 -pady => 2,
		 -takefocus => 0,
	       );

  my %pcommon = ( -side => 'left',
		  -expand => 1,
		  -fill => 'x' );

  $w->privateData->{delete} =
    $fr->Button
      (-text => 'Delete',
       %common,
       -command => 
       sub { $w->delSelected;
	     $w->setSelectState;
	   },
      )->pack( %pcommon );

  $w->privateData->{up} =
    $fr->Button
      ( -text => 'Up',
	-command => [ mvSelected => $w, -1 ],
	%common,
      )->pack( %pcommon );

  $w->privateData->{down} =
    $fr->Button
      ( -text => 'Down',
	-command => [ mvSelected => $w, 1 ],
	%common,
      )->pack( %pcommon );

  if ($sortbtns == 1) {
    $w->privateData->{asc} =
      $fr->Button
        ( -text => 'Ascending',
	  -command => [ setSortDirection => $w, -1 ],
	  %common,
        )->pack( %pcommon );

    $w->privateData->{desc} =
      $fr->Button
        ( -text => 'Descending',
	  -command => [ setSortDirection => $w, 1 ],
	  %common,
        )->pack( %pcommon );
  }

  $fr->Button
    ( -text => 'Sort',
      -command => sub { $w->sortOrderedList;
			$w->setSelectState;
		      },
      %common,
    )->pack( %pcommon );

  $fr;
}

## Class Method ##
# Name: allButtons
# Purpose: create a button to select all
# Parameters: none
# Returns: Frame
sub allButtons
{
  my ( $w ) = @_;

  my %common = ( -padx => 2,
		 -pady => 2,
		 -takefocus => 0,
	       );

  my %pcommon = (
		 -side => 'left',
		 -expand => 1,
		 -fill => 'x',
		 );

  my $fr = $w->Frame( -borderwidth => 2 );
  $fr->Button( -text => 'Delete All',
	       %common,
		-command => sub { $w->deleteAll;
				  $w->setSelectState;
				},
	       )->pack(%pcommon);

  $fr->Button( -text => 'Choose All',
	       %common,
		-command => sub { $w->chooseAll;
				  $w->setSelectState;
				}
	       )->pack(%pcommon);
  $fr;
}

## Class Method ##
# Name: setSelectState
# Purpose: activate/deactiveate widgets that work only if there's a selection
#          in the ordered list listbox
# Parameters: none
# Returns: nothing
sub setSelectState
{
  my ( $w ) = @_;

  my ( $list, @buttons ) = @{$w->privateData}{qw/ list delete up down /};

  $_->configure( -state  => 
		 ( $list->curselection ? 
		   'normal' : 'disabled' ) )
    foreach @buttons;
}

## Class Method ##
# Name: chooseAll
# Purpose: choose all the possible entries
# Parameters: none
# Returns: nothing
sub chooseAll
{
  my ( $w ) = @_;

  # first clear all
  $w->deleteAll;

  # then load 'em all up
  my $nentries = @{$w->privateData->{choicelist}};
  $w->addOrderedEntry( $_, 'end' ) for 0..$nentries-1;
  $w->privateData->{list}->see(0);
}

## Class Method ##
# Name: deleteAll
# Purpose: delete all of the entries in the ordered list
# Parameters: none
# Returns: nothing
sub deleteAll
{
  my ( $w ) = @_;

  my $list = $w->privateData->{list};
  $list->selectionSet( 0, 'end' );
  $w->delSelected;
}
## Class Method ##
# Name: setSortDirection
# Purpose: set ascending or descending values for the sort field
# Parameters: type
# Returns: nothing
sub setSortDirection
{
  my ( $w, $sortDir ) = @_;

  my $list = $w->privateData->{list};

  my ($idx) = $list->curselection;

  return if 
    # no selection
    ! defined $idx ;

  # fix up Listbox
  my $entry = $list->get($idx);
  my $sortdirection = $w->privateData->{sortdirection};
  my $orderedlist = $w->privateData->{orderedlist};
  my $sidx =  @{$orderedlist}[ $idx ];
  my $ii=0;

  if ($sortDir == -1) {
    $list->delete($idx);
    $entry =~ s/   DESCENDING//;
    $list->insert($idx,$entry);
    foreach (@$sortdirection) {
       if ($_ == $sidx) {
            splice(@$sortdirection,$ii);
       } else {
         $ii++;
       }
    }
  } else {
    $list->delete($idx);
    $entry =~ s/   DESCENDING//;
    $entry .= "   DESCENDING";
    $list->insert($idx,$entry);
    push @$sortdirection, $sidx;
  }
  $list->selectionSet($idx);
  $list->see($idx);

}

## Class Method ##
# Name: mvSelected
# Purpose: move the selected entry in the ordered list box up or down
#          because of the way our listbox is configure, there's only one
#          possible entry.
# Parameters: direction
# Returns: nothing
sub mvSelected
{
  my ( $w, $dir ) = @_;

  my $list = $w->privateData->{list};

  my ($idx) = $list->curselection;

  return if 
    # no selection
    ! defined $idx 
    # up and at top
    || ( $dir == -1 && $idx == 0 )
    # down and at bottom
    || ( $dir ==  1 && $idx == $list->size - 1 );

  # fix up Listbox
  my $entry = $list->get($idx);
  $list->delete($idx);
  $list->insert($idx + $dir, $entry );
  $list->selectionSet($idx + $dir);
  $list->see($idx + $dir);

  # fix up list of selected indices (swap $idx, $idx+$dir)
  my $orderedlist = $w->privateData->{orderedlist};
  @{$orderedlist}[ $idx, $idx + $dir ] = @{$orderedlist}[ $idx + $dir, $idx ];
}

## Class Method ##
# Name: sortOrderedList
# Purpose: sort (alphabetically) the entries in the ordered list
# Parameters: none
# Returns: nothing
sub sortOrderedList
{
  my ( $w ) = @_;

  my ( $list, $orderedlist, $choices ) = 
    @{$w->privateData}{ qw/list orderedlist choicelist / };

  my @orderedlist = sort { $choices->[$a] cmp $choices->[$b] } @$orderedlist;
  $w->deleteAll;

  $w->addOrderedEntries( \@orderedlist );
  $w->privateData->{list}->see(0);
}

## Class Method ##
# Name: delSelected
# Purpose: delete the entries in the ordered list which are currently selected
# Parameters: none
# Returns: nothing
sub delSelected
{
  my ( $w ) = @_;

  my ( $list, $orderedlist, $gridWidgets ) = 
    @{$w->privateData}{ qw/list orderedlist gridWidgets / };

  # fix up Listbox
  my @idxs = $list->curselection;
  return unless @idxs;

  foreach my $idx ( reverse sort { $a <=> $b } @idxs )
  {
    $list->delete($idx);

    $gridWidgets->[$orderedlist->[$idx]]->deselect
      if $gridWidgets;
    # fix up selected list
    splice(@$orderedlist, $idx, 1);
  }

  $list->selectionSet($idxs[0] >= @$orderedlist ? 'end' : $idxs[0]);
  $list->eventGenerate("<<ListboxSelect>>");
}

## Class Method ##
# Name: toggleOrderedEntry
# Purpose: toggle the state of an entry in the ordered list.  this does *not*
#          do anything to the button in the grade.
# Parameters: state, index
# Returns: nothing
sub toggleOrderedEntry
{
  my ( $w, $state, $idx ) = @_;

  # state is the *new* state (true == on, false == off)
  if ( $state )
  {
    $w->addOrderedEntry( $idx );
  }
  else
  {
    $w->delOrderedEntry( $idx );
  }

}

## Class Method ##
# Name: delOrderedEntry
# Purpose: delete an entry from the ordered list
# Parameters: index
# Returns: nothing
sub delOrderedEntry
{
  my ( $w, $idx ) = @_;

  # find out which element in the list box corresponds to this element
  my ( $list, $orderedlist, $gridWidget ) = 
    @{$w->privateData}{ qw/list orderedlist/ };

  for my $lidx ( reverse sort { $a <=> $b } 
		 grep { $orderedlist->[$_] == $idx } 0..@$orderedlist-1 )
  {
    $list->see( $lidx );
    $list->delete( $lidx );
    splice(@$orderedlist,$lidx,1);
  }

  $list->eventGenerate("<<ListboxSelect>>");
}

## Class Method ##
# Name: addOrderedEntries
# Purpose: add one or more entries to the ordered list
# Parameters: list
# Returns: nothing
sub addOrderedEntries
{
  my ( $w, $list ) = @_;

  $w->addOrderedEntry( $_, 'end' ) foreach @$list;
}

## Class Method ##
# Name: addOrderedEntry
# Purpose: add one entry to a location
# Parameters: index, position
# Returns: nothing
sub addOrderedEntry
{
  my ( $w, $idx, $where ) = @_;

  my ( $list, $choices, $orderedlist, $gridWidgets ) = 
    @{$w->privateData}{qw/ list choicelist orderedlist gridWidgets/};

  # if not specified, put it before the current selection
  ($where ) = $list->curselection
    unless defined $where;
  # but, use the end of the list if no current selection
  $where = 'end' unless defined $where;

  $list->insert( $where, $choices->[$idx] );

  if ( 'end' eq $where )
  {
    push @$orderedlist, $idx;
  }
  else
  {
    splice(@$orderedlist,$where,0,$idx);
  }
  $list->see($where);

  $gridWidgets->[$idx]->select
    if $gridWidgets;

}

## Internal Utility ##
# Name: calcGrid
# Purpose: calculate the choices grid dimensions
# Parameters: aspect ratio
#             number of rows
#             number of columns
#             number of entries
# Returns: number of rows
#          number of columns
sub calcGrid
{
  my ( $aspectratio, $nrows, $ncols, $nentries ) = @_;

  # if just rows were specified, make columns dominant
  if ( defined $nrows && ! defined $ncols )
  {
    $ncols = int( $nentries / $nrows);
    $ncols++ while $nentries > $ncols * $nrows;
  }

  # else, rows are dominant
  {
    $ncols ||= int(sqrt( $nentries / $aspectratio ) + 0.5);
    $nrows ||= int( $nentries / $ncols + 0.5 );

    $nrows++ while $nentries > $ncols * $nrows;
  }

  ( $ncols, $nrows );

}

## Class Method ##
# Name: CheckbuttonGrid
# Purpose: create a choices grid using check buttons.  this is done for
#          non-duplicate entries in the ordered list
# Parameters: number of columns
#             number of rows
#             choices
#             ordered list
# Returns: frame
sub CheckbuttonGrid
{
  my ( $w, $ncols, $nrows, $choices, $orderedlist ) = @_;

  my %orderedlist = map { $_ => 1 } @$orderedlist;

  my $nchoices = @$choices;

  my $fr = $w->LabFrame( -label => 'Choices' );
  my $fr2 = $fr->Frame;
 
  my $idx = 0;
  my @widgets;
  for ( my $col=0; $idx < $nchoices && $col < $ncols ; $col++ )
  {
    for ( my $row=0; $idx < $nchoices && $row < $nrows ; $idx++, $row++ )
    {
      my $b = $fr2->Checkbutton( -text => $choices->[$idx],
				-anchor => 'nw',
				-indicatoron => 0,
				-borderwidth => 1,,
				-padx => 2,
				-pady => 2,
			      );
      $b->configure( -command => [ sub { $w->toggleOrderedEntry( $b->{'Value'},
								 $_[0] ) },
				   $idx
				   ]);
      $b->configure( -selectcolor => '' );
      $b->grid( -sticky => 'news', -row => $row, -column => $col);
      $b->select if $orderedlist{$idx};
      push @widgets, $b;
    }
  }

  $w->privateData->{gridWidgets} = \@widgets;

  $fr2->pack( -side => 'top' );
  $fr;
}

## Class Method ##
# Name: ButtonGrid
# Purpose: create a choices grid using buttons.  this allows for duplicate
#          entries in the ordered list
# Parameters: number of columns
#             number of rows
#             choices
# Returns: frame
sub ButtonGrid
{
  my ( $w, $ncols, $nrows, $choices ) = @_;

  my $nchoices = @$choices;

  my $fr = $w->LabFrame( -label => 'Choices' );
  my $fr2 = $fr->Frame;

  my $idx = 0;
  for ( my $col=0; $idx < $nchoices && $col < $ncols ; $col++ )
  {
    for ( my $row=0; $idx < $nchoices && $row < $nrows ; $idx++, $row++ )
    {
      my $b = $fr2->Button( -text => $choices->[$idx],
			   -command => [ addOrderedEntry => $w, $idx ],
			   -anchor => 'w',
			   -borderwidth => 1,
			   -padx => 2,
			   -pady => 2,
			   -takefocus => 0,
			 );
      $b->grid(-sticky => 'nsew', -row => $row, -column => $col);
    }
  }

  $fr2->pack( -side => 'top' );

  $fr;
}

1;

__END__

=head1 NAME

Tk::EnumList - manage an enumerated list

=head1 SYNOPSIS

package TK::EnumList;

I<$enumlist> = I<$parent>-E<gt>B<EnumList>(?I<options>?)

=head1 DESCRIPTION

This widget is used to create and manage an ordered list of strings
from a prescribed set.  The set of available strings is presented as a
grid of buttons; the ordered list is presented in a Listbox.

The list may optionally allow duplicate values.

=head2 Bindings

If the C<E<lt>EscapeE<gt>> key is struck, the entry selected in the
listbox is deselected.

=head1 WIDGET-SPECIFIC OPTIONS

=head2 Construction time only Options

These options are available only when calling the widget constructor.
Unless otherwise noted, the option values are I<not> available via the
B<$w->cget()> method.

=over 

=item B<-orderedlist>

This specifies the reference to an array which will used to initially
populate the ordered list.  The array should contain I<indices> into
the list of available choices, not the actual entries.  This array
will always reflect the current state of the orderedlist.  It may be
retrieved with C<$widget->cget(-orderedlist)>.  Do B<not> muck about
with the contents of this array after passing it to the widget; this
will corrupt the widget's state.

=item B<-choicelist>

This specifies the reference to an array which holds the values
available to choose from.  For efficiency reasons this array is used
I<in situ>. Do B<not> alter it, as this will corrupt the widget
state.


=item B<-duplicate>

This dictates whether an element may be duplicated in the chosen list.
It is a boolean value and defaults to false.

=item B<-sortbtns>

This dictates whether the ascending and descending buttons are added
to options for the chosen list.  Sorts default to ascending.

=item B<-aspectratio>

The row to column ratio used to determine the dimensions of the grid
holding the possible choices.  It defaults to C<8/3>.  See L</CHOICE
GRID ALGORITHM> for more on how this is used.

=item B<-ncols>

The number of columns in the grid of possible choices.  This is rarely
specified.  See L</Choice Grid Algorithm> for more on how this is
used.

=item B<-nrows>

The number of rows in the grid of possible choices.  This is rarely
specified.  See L</Choice Grid Algorithm> for more on how this is
used.

=back

=head1 WIDGET METHODS

The B<EnumList> method creates a widget object. The widget
also inherits all the methods provided by the generic Tk::Widget
class.

The following additional methods are available for B<EnumList> widgets:

=over

=item nrows

returns the actual number of rows in the choices grid.

=item ncols

returns the actual number of cols in the choices grid.

=item get

returns the list of chosen entries.  Just as with the B<-orderedlist>
configuration option, this is a list of I<indices> into the
B<-choicelist> array.

=back

=head1 CHOICE GRID ALGORITHM

B<EnumList> uses a simple algorithm to calculate the dimensions of the
grid holding the list of choices.  The grid dimensions are fixed at
construction; the grid will not resize.

If neither C<-nrows> or C<-ncols> is specified, the initial estimate
is determined by using the C<-aspectratio>; the number of rows is then
tweaked until there are enough grid locations available.

If C<-ncols> is specified, the number of columns is fixed to that value,
and the number of rows is calculated.

If C<-nrows> is specified, and C<-ncols> is not, the number of rows is
fixed to that value and the number of columns is calculated.

If both C<-ncols> and C<-nrows> are specified, these are used as the
initial guess, and the number of rows is incremented if necessary to
accomodate the number of entries.

=head1 ADVERTISED WIDGETS

None.

=head1 AUTHOR

Diab Jerius E<lt>djerius@cpan.orgE<gt>

=head1 COPYRIGHT AND LICENSE

Copyright 2005 Diab Jerius

This software is released under the GNU General Public License. You
may find a copy at L<http://www.fsf.org/copyleft/gpl.html>.
