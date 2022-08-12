#
# PrintPreview.pm - This is a widget for previewing a view in a postscript
#                   viewer
#
# Purpose: Provides a convenient interface for previewing a view printout in
#          a postscript viewer
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package PrintPreview;

use strict;
use warnings;

use strict;
use Carp;

use base qw/ Tk::Frame /;
use Print;

use vars qw($verbose);

Construct Tk::Widget 'PrintPreview';

## Class Method ##
# Name: Populate
# Purpose: populate the widget
# Parameters: top level widget
#             -panel - a panel object
#             -view_id - view id
#             -sortby - sort by string
#             -groupby - group by string
#             -hide_triage - flag for hiding triaged proposals
#             -hide_arc_the - flag for hiding archive/theory proposals
#             -hide_lp_vlp - flag for hiding LP/VLP proposals
#             -verbose - level of verbosity
#             -closecmd - callback for closing widget
#             -changecmd - callback for actions to take if data has changed
# Returns: nothing
sub Populate {
  my ($w, $args) = @_;
  
  croak( "must specify panel" )
    unless exists $args->{-panel};
  
  my $obj = $args->{-panel};
  $verbose = $args->{-verbose};

  croak ( "-panel must be a Panel object" )
    unless UNIVERSAL::isa( $obj, 'Panel');
  
  $w->ConfigSpecs(-view_id => [ 'PASSIVE'],
		  -sortby => [ 'PASSIVE' ],
		  -groupby => [ 'PASSIVE' ],
		  -hide_triage => [ 'PASSIVE' ],
		  -hide_arc_the => [ 'PASSIVE' ],
		  -hide_lp_vlp => [ 'PASSIVE' ],
		  -panel => [ 'PASSIVE' ],
                  -verbose => [ 'PASSIVE' ],
		  -closecmd => [ 'CALLBACK' ],
		  -changecmd => [ 'CALLBACK' ],
		 );
  
  $w->SUPER::Populate( $args );
  my $pData = $w->privateData;
  $pData->{size} = $Print::SizeOptions[0];
  $pData->{orient} = 'landscape';
  $pData->{pnt_size} = 12;
  
  print "PrintPreview::Populate - populating widget" if $verbose > 2;
  my $gr_frame = $w->Frame( -border => 3, -relief => 'groove' );
  {
    my $frame = $gr_frame->Frame;
    for my $orient ( qw(landscape portrait) ) {

      $frame->Radiobutton(-text => $orient,
			     -variable => \$pData->{orient},
			     -value => $orient
			    )->pack( -side => 'left' ) ;
    }

    $frame->pack;
    $gr_frame->Label( -text => 'Orientation: ' )->grid( $frame,
							-sticky => 'w' );
  }

  {
    my $frame = $gr_frame->Frame;
    for my $pnt_size ( qw(10 11 12) ) {

      $frame->Radiobutton(-text => $pnt_size,
			     -variable => \$pData->{pnt_size},
			     -value => $pnt_size
			    )->pack( -side => 'left' ) ;
    }

    $frame->pack;
    $gr_frame->Label( -text => 'Point Family: ' )->grid( $frame,
							-sticky => 'w' );
  }

  my $font_size = $gr_frame->Optionmenu
    ( -variable => \$pData->{size},
      -options => \@Print::SizeOptions,
    );
  $gr_frame->Label(-text => 'Font Size:' )->grid( $font_size, 
						  -sticky => 'w'  );
  $gr_frame->pack;

  
  # These are the buttons to Preview/Close
  my $button_frame = $w->Frame;
  my $save = $button_frame->Button( -text => 'Preview'  , 
				    -command => [ preview  => $w  ] 
				  )->pack(-side => 'left',
					  -expand => 1);
  my $close = $button_frame->Button( -text => 'Close',  
				     -command => [ close => $w ] 
				   )->pack(-side => 'left',
					   -expand => 1);
  $button_frame->pack;
}

## Internal Utility ##
# Name: close
# Purpose: call back to destroy window
# Parameters: none
# Returns: nothing
sub close
{
    my $w = shift;
    
    defined $w->cget( '-closecmd' ) ? $w->Callback( '-closecmd' ) : $w->destroy;
}

## Internal Utility ##
# Name: preview
# Purpose: previews the view in a postscript viewer
# Parameters: none
# Returns: nothing
sub preview {
  my $w = shift;
  my $panel = $w->cget('-panel');
  my $view_id = $w->cget('-view_id');
  my $sortby = $w->cget('-sortby');
  my $groupby = $w->cget('-groupby');
  my $hide_triage = $w->cget('-hide_triage');
  my $hide_arc_the = $w->cget('-hide_arc_the');
  my $hide_lp_vlp = $w->cget('-hide_lp_vlp');
  my $pData = $w->privateData;
  my $size = $pData->{size};
  my $orient = $pData->{orient};
  my $pnt_size = $pData->{pnt_size};

  printPreview($panel, $view_id,
	       $groupby, $sortby, 
	       $hide_triage, $hide_arc_the, $hide_lp_vlp,
	       $size, $orient, $pnt_size);
}

1;

__END__

=head1 NAME

PrintPreview - This is a widget for previewing a view with a postscript
viewer

=head1 VERSION

$Revision: 1.4 $

=head1 SYNOPSIS

   use PrintPreview;
   my $preview = $mw->Toplevel();
   $preview->PrintPreview(-view_id => $view_id,
		          -groupby => $panel->groupby,
		          -sortby => $panel->sortby,
		          -hide_triage => $hide_triage,
		          -hide_arc_the => $hide_arc_the,
		          -hide_lp_vlp => $hide_lp_vlp,
		          -panel => $panel,
		          -closecmd => [ \&closePrintPreview => ($panel) ],
		         )->pack(-expand => 1,
			         -fill => 'both');

=head1 DESCRIPTION

Provides a convenient interface for previewing views in a postscript viewer

=head1 PUBLIC METHODS

=head2 Populate($w, $args)

Populates the widget with panel information and entry widgets for assigning
proposals to groups

=over 

=item $w - toplevel widget

=item $args - reference to hash of arguments

   -panel - panel objec

   -view_id - view id

   -sortby - sort string

   -groupby - group string

   -hide_triage - flag for hiding triaged proposals

   -hide_arc_the - flag for hiding archive/theory proposals

   -hide_lp_vlp - flag for hiding LP/VLP proposals

   -verbose - level of verbosity

   -closecmd - call back for closing widget

   -changecmd - call back for handling changed data

=back

=head1 PRIVATE METHODS

=head2 close

Call back to destroy page

=head2 preview

Launches the preview

=head1 DEPENDENCIES

This module has no dependencies

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
