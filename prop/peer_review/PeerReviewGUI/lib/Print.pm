#
# Print.pm - This module creates latex files and turns them into postscript
#
# Purpose: Create latex file for a view of a panel and generate the postscript
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Print;
require Exporter;

use strict;
use Data::Dumper;
use Data::Dumper;

use config;
use Cwd;
use File::Basename;
use File::Spec;
use IO::File;

our @ISA = qw(Exporter);
our @EXPORT = qw(createFile printPreview printFile printStatus);

our @Sizes = 
(
 [ tiny		=> 'Tiny'	  ],
 [ scriptsize	=> 'VeryVerySmall'],
 [ footnotesize	=> 'VerySmall'	  ],
 [ small	=> 'Small'	  ],
 [ normalsize	=> 'Normal'	  ],
 [ large	=> 'Large'	  ],
 [ Large	=> 'VeryLarge'	  ],
 [ LARGE	=> 'VeryVeryLarge'],
 [ huge		=> 'Huge'	  ],
 [ Huge		=> 'Gigantic'	  ]
);

our @LaTeXSizes = map { $_->[0] } @Sizes;

use constant AUTOFIT => 'Autofit';
our %SizeMap = ( AUTOFIT, AUTOFIT, map { ($_->[1] => $_->[0]) } @Sizes );
our @SizeOptions = ( AUTOFIT, map { $_->[1] } @Sizes );

## Class Method ##
# Name: printPreview
# Purpose: create latex of panel view and pop-up a viewer to look at the 
#          output
# Parameters: verbosity
#             panel object
#             view id
#             group by string
#             sort by string
#             'hide_triage' flag
#             'hide_arc_the' flag
#             'hide_lp_vlp' flag
#             font size
#             print orientation
#             font point family
# Returns: nothing
sub printPreview {
  my ($panel, $view_id, $groupby, $sortby, $hide_triage, $hide_arc_the, 
      $hide_lp_vlp, $size,
      $orient, $pnt_size) = @_;

  # Generate the latex file and latex it
  my $file = 'PRprint';

  $size = $SizeMap{$size};

  if (AUTOFIT eq $size) {
      $pnt_size = 12;
      my $status =  autofitFile($panel, $view_id, $groupby, $sortby, 
				$hide_triage, $hide_arc_the, $hide_lp_vlp, 
				$orient, $pnt_size, $file);
      if (!$status) {
	  $pnt_size--;
	  $status =  autofitFile($panel, $view_id, $groupby, $sortby, 
				 $hide_triage, $hide_arc_the, $hide_lp_vlp, 
				 $orient, $pnt_size, $file);
      }

      if (!$status) {
	  $pnt_size--;
	  $status =  autofitFile($panel, $view_id, $groupby, $sortby, 
				 $hide_triage, $hide_arc_the, $hide_lp_vlp, 
				 $orient, $pnt_size, $file);
      }
      die( "Unable to make fonts small enough to fit table on page.  Please select a point family and font size to force file creation\n" )
	  if ( !$status );
  }
  else {
      createFile($panel, $view_id, $groupby, $sortby, 
		 $hide_triage, $hide_arc_the, $hide_lp_vlp, 
		 $orient, $size, $pnt_size, $file);
      latexFile($file);
  }
  
  # dvips the file
  #psFile( $file, $orient );
  
  
  system("$main::param{gv} $file.pdf") == 0 
     or die "Trouble opening file:   $?\n";

  #system("$main::param{gv} $file.ps") == 0 
}

## Class Method ##
# Name: printFile
# Purpose: create latex of panel view and send the postscript to the printer
#          or save to a file
# Parameters: panel object
#             view id
#             group by string
#             sort by string
#             'hide_triage' flag
#             'hide_arc_the' flag
#             'hide_lp_vlp' flag
#             font size
#             print orientation
#             font point family
#             file name
# Returns: nothing
sub printFile {
  my ($panel, $view_id, $groupby, $sortby, $hide_triage, $hide_arc_the, 
      $hide_lp_vlp, $size, 
      $orient, $pnt_size, $file) = @_;
  
  $size = $SizeMap{$size};
  my $save2file = 1;
  if (!defined $file) {
    $file = 'PRprint';
    $save2file = 0;
  }

  # remove suffix
  my ( $name, $path, $suffix ) = fileparse( $file, '.ps' );
  $file = File::Spec->catfile( $path, $name );


  # Generate the latex file and latex it

  if ( AUTOFIT eq $size) {
      $pnt_size = 12;
      my $status =  autofitFile($panel, $view_id, $groupby, $sortby, 
				$hide_triage, $hide_arc_the, $hide_lp_vlp, 
				$orient, $pnt_size, $file);
      if (!$status) {
	  $pnt_size--;
	  $status =  autofitFile($panel, $view_id, $groupby, $sortby, 
				 $hide_triage, $hide_arc_the, $hide_lp_vlp, 
				 $orient, $pnt_size, $file);
      }

      if (!$status) {
	  $pnt_size--;
	  $status =  autofitFile($panel, $view_id, $groupby, $sortby, 
				 $hide_triage, $hide_arc_the, $hide_lp_vlp, 
				 $orient, $pnt_size, $file);
      }
      die( "Unable to make fonts small enough to fit table on page.  Please select a point family and font size to force file creation\n" )
	  if ( !$status );
  }
  else {
    createFile($panel, $view_id, $groupby, $sortby, 
	       $hide_triage, $hide_arc_the, $hide_lp_vlp, 
	       $orient, $size, $pnt_size, $file);
    latexFile($file);
  }

  # generate PostScript
  #psFile( $file, $orient );

  # Send to printer if not save to file
  if (!$save2file) {
    my $log = $file . '.lp.log';
    print "$main::param{printer}\n";
    system("lp -o fitplot -d $main::param{printer} $file.ps") == 0
      or die "Troubling printing file to printer: see $log\n";
  }
}

## Internal Utility ##
# Name: autofitFile
# Purpose: create file with largest font possible
# Parameters: panel object
#             view id
#             group by string
#             sort by string
#             'hide_triage' flag
#             'hide_arc_the' flag
#             'hide_lp_vlp' flag
#             print orientation
#             starting font point family
#             file name
# Returns: true if font found
sub autofitFile {
  my ($panel, $view_id, $groupby, $sortby, $hide_triage, $hide_arc_the, 
      $hide_lp_vlp, 
      $landscape, $pnt_size, $file) = @_;

  # Start at normalsize and go up until we find the maximum size
  my ( $sz_index ) = 
    grep { $LaTeXSizes[$_] eq 'normalsize' } 0..@LaTeXSizes-1;

  # keep track of which sizes succeed.
  my %size_ok;

  # size used for last latex run
  my $last_size_index;

  my $status = 1;

  while ( $status ) {
    $last_size_index = $sz_index;
    my $size = $LaTeXSizes[$sz_index];

    createFile($panel, $view_id, $groupby, $sortby, 
	       $hide_triage, $hide_arc_the, $hide_lp_vlp, 
	       $landscape, $size, $pnt_size, $file);
    
    # latexFile returns true if overfull boxes
    my $overfull = latexFile($file);


    # keep track of sizes that work
    $size_ok{$sz_index}++
      unless $overfull;

    $sz_index += $overfull ? -1 : 1;

    # if we've used this size before, and it's ok,
    # it means we're backing off from an overfill to a font
    # which worked before, so success!
    if ($size_ok{$sz_index}) {
	last;
    }

    # no font too big
    if ( $sz_index == @LaTeXSizes ) {
      # back off to a legal index
      $sz_index--;
      last;
    }

    # no font too small
    $status = 0 if (-1 == $sz_index);
  }

  return 0 if !$status;
  # latex file again if last run had a different font size
  if ( $last_size_index != $sz_index ) {
     createFile($panel, $view_id, $groupby, $sortby, 
	       $hide_triage, $hide_arc_the, $hide_lp_vlp, $landscape, 
	       $LaTeXSizes[$sz_index], $pnt_size, $file);
     my $size = $LaTeXSizes[$sz_index];
    die( "Family point of $pnt_size with font size of $size produced an overfull table!  Please select a family point and point size and try again\n" )
      if latexFile($file);
  }
  return 1;
}

    
## Class Method ##
# Name: printStatus
# Purpose: print status window
# Parameters: panel object
#             type of status (panel or final)
#             hash of final values (if $type is final)
# Returns: nothing
sub printStatus {
  my ($panel, $type, $final_vals) = @_;
  my $file = "${type}_status";


  my %status_vals = (num_graded=> $panel->num_graded,
		     num_ungraded=> $panel->num_ungraded,
		     arc_num => $panel->arc_num,
		     the_num=> $panel->the_num,
		     std_prop_y => $panel->std_prop_y,
		     std_prop_n => $panel->std_prop_n,
		     std_prop_g => $panel->std_prop_g,
		     std_prop_p => $panel->std_prop_p,
		     std_targ_y => $panel->std_targ_y,
		     std_targ_n => $panel->std_targ_n,
		     std_targ_g => $panel->std_targ_g,
		     std_targ_p => $panel->std_targ_p,
		     too_prop_y => $panel->too_prop_y,
		     too_prop_n => $panel->too_prop_n,
		     too_prop_g => $panel->too_prop_g,
		     too_prop_p => $panel->too_prop_p,
		     too_targ_y => $panel->too_targ_y,
		     too_targ_n => $panel->too_targ_n,
		     too_targ_g => $panel->too_targ_g,
		     too_targ_p => $panel->too_targ_p,
		     bpp_prop_y => $panel->bpp_prop_y,
		     bpp_prop_n=> $panel->bpp_prop_n,
		     bpp_prop_g=> $panel->bpp_prop_g,
		     bpp_prop_p=> $panel->bpp_prop_p,
		     bpp_targ_y=> $panel->bpp_targ_y,
		     bpp_targ_n => $panel->bpp_targ_n,
		     bpp_targ_g => $panel->bpp_targ_g,
		     bpp_targ_p => $panel->bpp_targ_p,
		     lp_targ => $panel->lp_targ,
		     arc_y=> $panel->arc_y,
		     arc_n=> $panel->arc_n,
		     arc_prop=> $panel->arc_prop,
		     the_y => $panel->the_y,
		     the_n=> $panel->the_n,
		     the_prop=> $panel->the_prop,
		     std_prop => $panel->std_prop,
		     std_targ => $panel->std_targ,
		     lp_prop=> $panel->lp_prop,
		     lp_targ => $panel->lp_targ,
		     vlp_prop=> $panel->vlp_prop,
		     vlp_targ=> $panel->vlp_targ,
		     xvp_prop=> $panel->xvp_prop,
		     xvp_targ=> $panel->xvp_targ,
		     too_prop=> $panel->too_prop,
		     too_targ=> $panel->too_targ,
		     arc_prop=> $panel->arc_prop,
		     the_prop=> $panel->the_prop,
		     num_props=> $panel->num_props,
		     num_targs=> $panel->num_targs,
		     norm_high=> $panel->fmt_value("1",$panel->norm_high),
		     grade_high=> $panel->fmt_value("1",$panel->grade_high),
		     norm_pass=> $panel->fmt_value("1",$panel->norm_pass),
		     grade_pass=> $panel->fmt_value("1",$panel->grade_pass),
		     norm_low=> $panel->fmt_value("1",$panel->norm_low),
		     grade_low=> $panel->fmt_value("1",$panel->grade_low),
		     time_req=> $panel->fmt_value("1",$panel->time_req),
		     time_slew_req => $panel->fmt_value("1",$panel->time_slew_req),
		     time_slew_prob_req => $panel->fmt_value("1",$panel->time_slew_prob_req),
	  		 hel_slew_prob_req => $panel->fmt_value("1",$panel->hel_slew_prob_req),
		     time_slew_prob_allot => $panel->fmt_value("1",$panel->time_slew_prob_allot),
	  		 hel_slew_prob_allot => $panel->fmt_value("1",$panel->hel_slew_prob_allot),
		     time_cur => $panel->fmt_value("1",$panel->time_cur),
		     time_slew_cur => $panel->fmt_value("1",$panel->time_slew_cur),
		     time_slew_prob_cur=> $panel->fmt_value("1",$panel->time_slew_prob_cur),
	  		 hel_slew_prob_cur=> $panel->fmt_value("1",$panel->hel_slew_prob_cur),
		     time_slew_prob_bal=> $panel->fmt_value("1",$panel->time_slew_prob_bal),
	  		 hel_slew_prob_bal=> $panel->fmt_value("1",$panel->hel_slew_prob_bal),
		     time_req_1 => $panel->fmt_value("1",$panel->time_req_1),
		     time_slew_req_1 => $panel->fmt_value("1",$panel->time_slew_req_1),
		     time_slew_prob_req_1 => $panel->fmt_value("1",$panel->time_slew_prob_req_1),
	  		 hel_slew_prob_req_1 => $panel->fmt_value("1",$panel->hel_slew_prob_req_1),
		     time_slew_prob_allot_1 => $panel->fmt_value("1",$panel->time_slew_prob_allot_1),
	  		 hel_slew_prob_allot_1 => $panel->fmt_value("1",$panel->hel_slew_prob_allot_1),
		     time_cur_1 => $panel->fmt_value("1",$panel->time_cur_1),
		     time_slew_cur_1 => $panel->fmt_value("1",$panel->time_slew_cur_1),
		     time_slew_prob_cur_1 => $panel->fmt_value("1",$panel->time_slew_prob_cur_1),
	  		 hel_slew_prob_cur_1 => $panel->fmt_value("1",$panel->hel_slew_prob_cur_1),
		     time_slew_prob_bal_1 => $panel->fmt_value("1",$panel->time_slew_prob_bal_1),
	  		 hel_slew_prob_bal_1 => $panel->fmt_value("1",$panel->hel_slew_prob_bal_1),
		     time_req_2 => $panel->fmt_value("1",$panel->time_req_2),
		     time_slew_req_2 =>$panel->fmt_value("1", $panel->time_slew_req_2),
		     time_slew_prob_req_2 => $panel->fmt_value("1",$panel->time_slew_prob_req_2),
	  		 hel_slew_prob_req_2 => $panel->fmt_value("1",$panel->hel_slew_prob_req_2),
		     time_slew_prob_allot_2 => $panel->fmt_value("1",$panel->time_slew_prob_allot_2),
	  		 hel_slew_prob_allot_2 => $panel->fmt_value("1",$panel->hel_slew_prob_allot_2),
		     time_cur_2 => $panel->fmt_value("1",$panel->time_cur_2),
		     time_slew_cur_2 => $panel->fmt_value("1",$panel->time_slew_cur_2),
		     time_slew_prob_cur_2 => $panel->fmt_value("1",$panel->time_slew_prob_cur_2),
	  		 hel_slew_prob_cur_2 => $panel->fmt_value("1",$panel->hel_slew_prob_cur_2),
		     time_slew_prob_bal_2 => $panel->fmt_value("1",$panel->time_slew_prob_bal_2),
	  		 hel_slew_prob_bal_2 => $panel->fmt_value("1",$panel->hel_slew_prob_bal_2),
		     lp_time_req => $panel->fmt_value("1",$panel->lp_time_req),
		     lp_time_slew_req => $panel->fmt_value("1",$panel->lp_time_slew_req),
		     lp_time_slew_prob_req => $panel->fmt_value("1",$panel->lp_time_slew_prob_req),
	  		 lp_hel_slew_prob_req => $panel->fmt_value("1",$panel->lp_hel_slew_prob_req),
		     lp_time_slew_prob_allot => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_allot),
		     lp_hel_slew_prob_allot =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_allot),
		     lp_time_cur => $panel->fmt_value("1",$panel->lp_time_cur),
		     lp_time_slew_cur => $panel->fmt_value("1",$panel->lp_time_slew_cur),
		     lp_time_slew_prob_cur=> $panel->fmt_value("1",$panel->lp_time_slew_prob_cur),
	  		 lp_hel_slew_prob_cur=> $panel->fmt_value("1",$panel->lp_hel_slew_prob_cur),
		     lp_time_slew_prob_bal=> $panel->fmt_value("1",$panel->lp_time_slew_prob_bal),
	  		 lp_hel_slew_prob_bal=> $panel->fmt_value("1",$panel->lp_hel_slew_prob_bal),
		     lp_time_req_1 => $panel->fmt_value("1",$panel->lp_time_req_1),
		     lp_time_slew_req_1 => $panel->fmt_value("1",$panel->lp_time_slew_req_1),
		     lp_time_slew_prob_req_1 => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_req_1),
		     lp_hel_slew_prob_req_1 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_req_1),
		     lp_time_slew_prob_allot_1 => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_allot_1),
		     lp_hel_slew_prob_allot_1 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_allot_1),
		     lp_time_cur_1 => $panel->fmt_value("1",$panel->lp_time_cur_1),
		     lp_time_slew_cur_1 => $panel->fmt_value("1",$panel->lp_time_slew_cur_1),
		     lp_time_slew_prob_cur_1 => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_cur_1),
		     lp_hel_slew_prob_cur_1 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_cur_1),
		     lp_time_slew_prob_bal_1 => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_bal_1),
		     lp_hel_slew_prob_bal_1 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_bal_1),
		     lp_time_req_2 => $panel->fmt_value("1",$panel->lp_time_req_2),
		     lp_time_slew_req_2 => $panel->fmt_value("1",$panel->lp_time_slew_req_2),
		     lp_time_slew_prob_req_2 =>
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_req_2),
		     lp_hel_slew_prob_req_2 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_req_2),
		     lp_time_slew_prob_allot_2 => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_allot_2),
		     lp_hel_slew_prob_allot_2 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_allot_2),
		     lp_time_cur_2 => $panel->fmt_value("1",$panel->lp_time_cur_2),
		     lp_time_slew_cur_2 => $panel->fmt_value("1",$panel->lp_time_slew_cur_2),
		     lp_time_slew_prob_cur_2 => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_cur_2),
		     lp_hel_slew_prob_cur_2 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_cur_2),
		     lp_time_slew_prob_bal_2 => 
		     $panel->fmt_value("1",$panel->lp_time_slew_prob_bal_2),
		     lp_hel_slew_prob_bal_2 =>
			     $panel->fmt_value("1",$panel->lp_hel_slew_prob_bal_2),
		     vlp_time_req=> $panel->fmt_value("1",$panel->vlp_time_req),
		     vlp_time_slew_req => $panel->fmt_value("1",$panel->vlp_time_slew_req),
		     vlp_time_slew_prob_req => $panel->fmt_value("1",$panel->vlp_time_slew_prob_req),
	  		 vlp_hel_slew_prob_req => $panel->fmt_value("1",$panel->vlp_hel_slew_prob_req),
		     vlp_time_slew_prob_allot => 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_allot),
		     vlp_hel_slew_prob_allot =>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_allot),
		     vlp_time_cur => $panel->fmt_value("1",$panel->vlp_time_cur),
		     vlp_time_slew_cur => $panel->fmt_value("1",$panel->vlp_time_slew_cur),
		     vlp_time_slew_prob_cur=> $panel->fmt_value("1",$panel->vlp_time_slew_prob_cur),
	  		 vlp_hel_slew_prob_cur=> $panel->fmt_value("1",$panel->vlp_hel_slew_prob_cur),
		     vlp_time_slew_prob_bal=> $panel->fmt_value("1",$panel->vlp_time_slew_prob_bal),
	  		 vlp_hel_slew_prob_bal=> $panel->fmt_value("1",$panel->vlp_hel_slew_prob_bal),
		     vlp_time_req_1 => $panel->fmt_value("1",$panel->vlp_time_req_1),
		     vlp_time_slew_req_1 => $panel->fmt_value("1",$panel->vlp_time_slew_req_1),
		     vlp_time_slew_prob_req_1 => 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_req_1),
	  		 vlp_hel_slew_prob_req_1 =>
		  		 $panel->fmt_value("1",$panel->vlp_hel_slew_prob_req_1),
		     vlp_time_slew_prob_allot_1 => 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_allot_1),
		     vlp_hel_slew_prob_allot_1 =>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_allot_1),
		     vlp_time_cur_1 => $panel->fmt_value("1",$panel->vlp_time_cur_1),
		     vlp_time_slew_cur_1 => $panel->fmt_value("1",$panel->vlp_time_slew_cur_1),
		     vlp_time_slew_prob_cur_1=> 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_cur_1),
		     vlp_hel_slew_prob_cur_1=>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_cur_1),
		     vlp_time_slew_prob_bal_1=> 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_bal_1),
		     vlp_hel_slew_prob_bal_1=>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_bal_1),
		     vlp_time_req_2 => $panel->fmt_value("1",$panel->vlp_time_req_2),
		     vlp_time_slew_req_2 => $panel->fmt_value("1",$panel->vlp_time_slew_req_2),
		     vlp_time_slew_prob_req_2 => 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_req_2),
		     vlp_hel_slew_prob_req_2 =>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_req_2),
		     vlp_time_slew_prob_allot_2 => 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_allot_2),
		     vlp_hel_slew_prob_allot_2 =>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_allot_2),
		     vlp_time_cur_2 => $panel->fmt_value("1",$panel->vlp_time_cur_2),
		     vlp_time_slew_cur_2 => $panel->fmt_value("1",$panel->vlp_time_slew_cur_2),
		     vlp_time_slew_prob_cur_2=> 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_cur_2),
		     vlp_hel_slew_prob_cur_2=>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_cur_2),
		     vlp_time_slew_prob_bal_2=> 
		     $panel->fmt_value("1",$panel->vlp_time_slew_prob_bal_2),
		     vlp_hel_slew_prob_bal_2=>
			     $panel->fmt_value("1",$panel->vlp_hel_slew_prob_bal_2),
		     xvp_time_req=> $panel->fmt_value("1",$panel->xvp_time_req),
		     xvp_time_slew_req => $panel->fmt_value("1",$panel->xvp_time_slew_req),
		     xvp_time_slew_prob_req => $panel->fmt_value("1",$panel->xvp_time_slew_prob_req),
	  		 xvp_hel_slew_prob_req => $panel->fmt_value("1",$panel->xvp_hel_slew_prob_req),
		     xvp_time_slew_prob_allot => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_allot),
		     xvp_hel_slew_prob_allot =>
			     $panel->fmt_value("1",$panel->xvp_hel_slew_prob_allot),
		     xvp_time_cur => $panel->fmt_value("1",$panel->xvp_time_cur),
		     xvp_time_slew_cur => $panel->fmt_value("1",$panel->xvp_time_slew_cur),
		     xvp_time_slew_prob_cur=> $panel->fmt_value("1",$panel->xvp_time_slew_prob_cur),
	  		 xvp_hel_slew_prob_cur=> $panel->fmt_value("1",$panel->xvp_hel_slew_prob_cur),
		     xvp_time_slew_prob_bal=> $panel->fmt_value("1",$panel->xvp_time_slew_prob_bal),
	  		 xvp_hel_slew_prob_bal=> $panel->fmt_value("1",$panel->xvp_hel_slew_prob_bal),
		     xvp_time_req_1 => $panel->fmt_value("1",$panel->xvp_time_req_1),
		     xvp_time_slew_req_1 => $panel->fmt_value("1",$panel->xvp_time_slew_req_1),
		     xvp_time_slew_prob_req_1 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_req_1),
		  	 xvp_hel_slew_prob_req_1 =>
			 	  $panel->fmt_value("1",$panel->xvp_hel_slew_prob_req_1),
		     xvp_time_slew_prob_allot_1 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_allot_1),
		     xvp_hel_slew_prob_allot_1 =>
			     $panel->fmt_value("1",$panel->xvp_hel_slew_prob_allot_1),
		     xvp_time_cur_1 => $panel->fmt_value("1",$panel->xvp_time_cur_1),
		     xvp_time_slew_cur_1 => $panel->fmt_value("1",$panel->xvp_time_slew_cur_1),
		     xvp_time_slew_prob_cur_1 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_cur_1),
		     xvp_hel_slew_prob_cur_1 =>
			     $panel->fmt_value("1",$panel->xvp_hel_slew_prob_cur_1),
		     xvp_time_slew_prob_bal_1 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_bal_1),
		     xvp_hel_slew_prob_bal_1 =>
			     $panel->fmt_value("1",$panel->xvp_hel_slew_prob_bal_1),
		     xvp_time_req_2 => $panel->fmt_value("1",$panel->xvp_time_req_2),
		     xvp_time_slew_req_2 => $panel->fmt_value("1",$panel->xvp_time_slew_req_2),
		     xvp_time_slew_prob_req_2 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_req_2),
	  		 xvp_hel_slew_prob_req_2 =>
		  		 $panel->fmt_value("1",$panel->xvp_hel_slew_prob_req_2),
		     xvp_time_slew_prob_allot_2 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_allot_2),
		     xvp_hel_slew_prob_allot_2 =>
			     $panel->fmt_value("1",$panel->xvp_hel_slew_prob_allot_2),
		     xvp_time_cur_2 => $panel->fmt_value("1",$panel->xvp_time_cur_2),
		     xvp_time_slew_cur_2 => $panel->fmt_value("1",$panel->xvp_time_slew_cur_2),
		     xvp_time_slew_prob_cur_2 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_cur_2),
		     xvp_hel_slew_prob_cur_2 =>
			     $panel->fmt_value("1",$panel->xvp_hel_slew_prob_cur_2),
		     xvp_time_slew_prob_bal_2 => 
		     $panel->fmt_value("1",$panel->xvp_time_slew_prob_bal_2),
		     xvp_hel_slew_prob_bal_2 =>
			     $panel->fmt_value("1",$panel->xvp_hel_slew_prob_bal_2),
		     arc_req=> $panel->fmt_value("1",$panel->arc_req),
		     the_req=> $panel->fmt_value("1",$panel->the_req),
		     arc_allot=> $panel->fmt_value("1",$panel->arc_allot),
		     the_allot=> $panel->fmt_value("1",$panel->the_allot),
		     arc_cur=> $panel->fmt_value("1",$panel->arc_cur),
		     the_cur=> $panel->fmt_value("1",$panel->the_cur),
		     arc_bal=> $panel->fmt_value("1",$panel->arc_bal),
		     the_bal => $panel->fmt_value("1",$panel->the_bal),
		     rc_req => $panel->fmt_value("1",$panel->rc_score_req),
		     rc_req_1 => $panel->fmt_value("1",$panel->rc_score_req_1),
		     rc_req_2 => $panel->fmt_value("1",$panel->rc_score_req_2),
		     vf_req=> $panel->fmt_value("1",$panel->vf_req),
		     f_req=> $panel->fmt_value("1",$panel->f_req),
		     s_req => $panel->fmt_value("1",$panel->s_req),
		     vs_req => $panel->fmt_value("1",$panel->vs_req),
		     rc_allot => $panel->fmt_value("1",$panel->rc_score_allot),
		     rc_allot_1 => $panel->fmt_value("1",$panel->rc_score_allot_1),
		     rc_allot_2 => $panel->fmt_value("1",$panel->rc_score_allot_2),
		     vf_allot=> $panel->fmt_value("1",$panel->vf_allot),
		     f_allot => $panel->fmt_value("1",$panel->f_allot),
		     s_allot=> $panel->fmt_value("1",$panel->s_allot),
		     vs_allot=> $panel->fmt_value("1",$panel->vs_allot),	     
		     rc_cur => $panel->fmt_value("1",$panel->rc_score_cur),
		     rc_cur_1 => $panel->fmt_value("1",$panel->rc_score_cur_1),
		     rc_cur_2 => $panel->fmt_value("1",$panel->rc_score_cur_2),
		     vf_cur=> $panel->fmt_value("1",$panel->vf_cur),
		     f_cur => $panel->fmt_value("1",$panel->f_cur),
		     s_cur=> $panel->fmt_value("1",$panel->s_cur),
		     vs_cur => $panel->fmt_value("1",$panel->vs_cur),	     
		     rc_bal=> $panel->fmt_value("1",$panel->rc_score_bal),
		     rc_bal_1 => $panel->fmt_value("1",$panel->rc_score_bal_1),
		     rc_bal_2 => $panel->fmt_value("1",$panel->rc_score_bal_2),
		     vf_bal=> $panel->fmt_value("1",$panel->vf_bal),
		     f_bal => $panel->fmt_value("1",$panel->f_bal),
		     s_bal => $panel->fmt_value("1",$panel->s_bal),
		     vs_bal => $panel->fmt_value("1",$panel->vs_bal),
		     hst_req => $panel->fmt_value("1",$panel->hst_req),
		     xmm_req => $panel->fmt_value("1",$panel->xmm_req),
		     rxte_req => $panel->fmt_value("1",$panel->rxte_req),
		     noao_req => $panel->fmt_value("1",$panel->noao_req),
		     nrao_req => $panel->fmt_value("1",$panel->nrao_req),
		     spitzer_req => $panel->fmt_value("1",$panel->spitzer_req),
		     swift_req => $panel->fmt_value("1",$panel->swift_req),
		     nustar_req => $panel->fmt_value("1",$panel->nustar_req),
		     hst_allot=> $panel->fmt_value("1",$panel->hst_allot),
		     xmm_allot=> $panel->fmt_value("1",$panel->xmm_allot),
		     rxte_allot=> $panel->fmt_value("1",$panel->rxte_allot),
		     noao_allot=> $panel->fmt_value("1",$panel->noao_allot),
		     nrao_allot=> $panel->fmt_value("1",$panel->nrao_allot),
		     spitzer_allot => $panel->fmt_value("1",$panel->spitzer_allot),
		     swift_allot => $panel->fmt_value("1",$panel->swift_allot),
		     nustar_allot => $panel->fmt_value("1",$panel->nustar_allot),
		     hst_cur=> $panel->fmt_value("1",$panel->hst_cur),
		     xmm_cur=> $panel->fmt_value("1",$panel->xmm_cur),
		     rxte_cur=> $panel->fmt_value("1",$panel->rxte_cur),
		     noao_cur=> $panel->fmt_value("1",$panel->noao_cur),
		     nrao_cur=> $panel->fmt_value("1",$panel->nrao_cur),
		     spitzer_cur => $panel->fmt_value("1",$panel->spitzer_cur),
		     swift_cur => $panel->fmt_value("1",$panel->swift_cur),
		     nustar_cur => $panel->fmt_value("1",$panel->nustar_cur),
		     hst_bal=> $panel->fmt_value("1",$panel->hst_bal),
		     xmm_bal=> $panel->fmt_value("1",$panel->xmm_bal),
		     rxte_bal=> $panel->fmt_value("1",$panel->rxte_bal),
		     noao_bal=> $panel->fmt_value("1",$panel->noao_bal),
		     nrao_bal=> $panel->fmt_value("1",$panel->nrao_bal),
		     spitzer_bal => $panel->fmt_value("1",$panel->spitzer_bal),
		     swift_bal => $panel->fmt_value("1",$panel->swift_bal),
		     nustar_bal => $panel->fmt_value("1",$panel->nustar_bal));

  if ($type eq 'final'){
    foreach my $key (keys %$final_vals){
      my $value = $$final_vals{$key};
      $status_vals{$key} = $value;
    }
  }
  
  my $dbh = $panel->dbh;
  my $panel_id = $panel->panel_id;

  my $fh = IO::File->new( ">$file.tex" )
    or die "Sorry, can't open $file.tex: $!:\n";
  my $pscape = "portrait";
  my $th = "9in";
  my $tw = "7in";
  if ($panel_id == 99) {
    $pscape="landscape";
    $th="6.5in";
    $tw="9in";
  }
    

  # Preamble Stuff
  my $tex = q{
\special{! TeXDict begin /landplus90{true} store end}
\documentclass[letterpaper,10pt,notitlepage,%pscape%]{article}
\usepackage[textwidth=%tw%,textheight=%th%]{geometry}
\usepackage{fancyhdr}
\usepackage[T1]{fontenc}
\usepackage{multicol}
%\setlength{\topmargin}{-25mm}
\begin{document}

\pagestyle{fancy}

\lhead{Panel %Panel%}
\chead{%Type% Status}
\rhead{%DATE%}

\raggedcolumns
};

  substitute( $tex, 
	      Panel => $panel_id,
	      pscape => $pscape,
	      th => $th,
	      tw => $tw,
	      Type => ucfirst($type),
	      DATE => scalar localtime());
  print $fh $tex;

  # Grade Summary and Money Summary
  $tex = q{
\begin{multicols}{3}
[\section*{Grade and Money Summary}]
\begin{center} \textbf{\# Graded}

\begin{tabular}{|l|r|} \hline
\# Graded & %Graded% \\\\ \hline
\# Not Graded & %NotGraded% \\\\ \hline
\end{tabular}
\end{center}

\columnbreak

\begin{center} \textbf{High - Low Grades}

\begin{tabular}{|l|c|c|} \hline
& NormG & AvgG \\\\ \hline
High & %NormHigh% & %AvgHigh% \\\\ \hline
Pass & %NormPass% & %AvgPass% \\\\ \hline
Low & %NormLow% & %AvgLow% \\\\ \hline
\end{tabular}
\end{center}

\columnbreak

\begin{center} \textbf{Money Allotment}
\begin{tabular} {|r|r|r|} \hline
& Archive & Theory \\\\ \hline
\# Approved & %ARCapp% & %THEapp% \\\\ \hline \hline
Requested & %ARCreq% & %THEreq% \\\\ \hline
Allotted & %ARCall% & %THEall% \\\\ \hline
Current & %ARCcur% & %THEcur% \\\\ \hline
Balance & %ARCbal% & %THEbal% \\\\ \hline
\end{tabular}
\end{center}

\end{multicols}

};

  substitute($tex,
	     Graded => $status_vals{num_graded},
	     NotGraded => $status_vals{num_ungraded},
	     NormHigh => $status_vals{norm_high},
	     AvgHigh => $status_vals{grade_high},
	     NormPass =>$status_vals{norm_pass},
	     AvgPass => $status_vals{grade_pass},
	     NormLow => $status_vals{norm_low},
	     AvgLow => $status_vals{grade_low},
		ARCapp => $status_vals{arc_num},
		THEapp => $status_vals{the_num},
		ARCreq => $status_vals{arc_req},
		THEreq => $status_vals{the_req},
		ARCall => $status_vals{arc_allot},
		THEall => $status_vals{the_allot},
		ARCcur => $status_vals{arc_cur},
		THEcur => $status_vals{the_cur},
		ARCbal => $status_vals{arc_bal},
		THEbal => $status_vals{the_bal});
  print $fh $tex;

  if ($panel_id != 99) {
    # Exposure Time Summary
    $tex = q{
\section*{Exposure Time Summary}
\begin{center}
\begin{tabular} {|r|r||r||r|} \hline
&  w/slew+prob & AO+1 & AO+2  \\\\ \hline
Requested &   %TSPreq% &  %TSPreq1% &  %TSPreq2% \\\\ \hline
Allotted &  %Allot% &  %Allot1% & %Allot2% \\\\ \hline
Current &  %TSPcur% &  %TSPcur1% &  %TSPcur2% \\\\ \hline
Balance &  %Balance% &  %Balance1% & %Balance2% \\\\ \hline
\end{tabular}
\end{center}

};

    substitute($tex,
	       Treq => $status_vals{time_req},
	       TSPreq =>$status_vals{time_slew_prob_req},
	       Allot => $status_vals{time_slew_prob_allot},
	       Tcur => $status_vals{time_cur},
	       TSPcur => $status_vals{time_slew_prob_cur},
	       Balance => $status_vals{time_slew_prob_bal},
	       Treq1 => $status_vals{time_req_1},
	       TSPreq1 =>$status_vals{time_slew_prob_req_1},
	       Allot1 => $status_vals{time_slew_prob_allot_1},
	       Tcur1 => $status_vals{time_cur_1},
	       TSPcur1 => $status_vals{time_slew_prob_cur_1},
	       Balance1 => $status_vals{time_slew_prob_bal_1},
	       Treq2 => $status_vals{time_req_2},
	       TSPreq2 =>$status_vals{time_slew_prob_req_2},
	       Allot2 => $status_vals{time_slew_prob_allot_2},
	       Tcur2 => $status_vals{time_cur_2},
	       TSPcur2 => $status_vals{time_slew_prob_cur_2},
	       Balance2 => $status_vals{time_slew_prob_bal_2},
	      );
    print $fh $tex;

};


  if ($panel_id == 99) {
    # Exposure Time Summary
#    $tex = q{
#\begin{center} \textbf{LP + XVP}

#\begin{tabular} {|r|r|r|} \hline
#& Time & w/slew+prob \\\\ \hline
#Requested & %Treq% & %TSPreq% \\\\ \hline
#Allotted & N/A & %Allot% \\\\ \hline
#Current & %Tcur% & %TSPcur% \\\\ \hline
#Balance & N/A & %Balance% \\\\ \hline
#\end{tabular}
#\end{center}

#};

#    substitute($tex,
#	       Treq => $status_vals{time_req},
#	       TSPreq =>$status_vals{time_slew_prob_req},
#	       Allot => $status_vals{time_slew_prob_allot},
#	       Tcur => $status_vals{time_cur},
#	       TSPcur => $status_vals{time_slew_prob_cur},
#	       Balance => $status_vals{time_slew_prob_bal});
#    print $fh $tex;

    # LP Exposure Time Summary
    $tex = q{
\section*{Exposure Time Summary}
};
if ($isXVP || $isVLP) {
$tex .= q{
\begin{multicols}{2}
};
}
$tex .= q{

\begin{center} \textbf{LP}

\begin{tabular} {|r|r||r||r|} \hline
&  Slew+p & AO+1  & AO+2  \\\\ \hline
Req &  %LPTSPreq% &  %LPTSPreq1% &  %LPTSPreq2% \\\\ \hline
Allot &  %LPAllot% &  %LPAllot1% &  %LPAllot2% \\\\ \hline
Cur &  %LPTSPcur%  & %LPTSPcur1%  & %LPTSPcur2% \\\\ \hline
Bal &  %LPBalance% &  %LPBalance1% &  %LPBalance2% \\\\ \hline
\end{tabular}
\end{center}
};
if ($isXVP || $isVLP) {
$tex .= q{
\columnbreak

};
}

    substitute($tex,
	       LPTreq => $status_vals{lp_time_req},
	       LPTSPreq =>$status_vals{lp_time_slew_prob_req},
	       LPAllot => $status_vals{lp_time_slew_prob_allot},
	       LPTcur => $status_vals{lp_time_cur},
	       LPTSPcur => $status_vals{lp_time_slew_prob_cur},
	       LPBalance => $status_vals{lp_time_slew_prob_bal},
	       LPTreq1 => $status_vals{lp_time_req_1},
	       LPTSPreq1 =>$status_vals{lp_time_slew_prob_req_1},
	       LPAllot1 => $status_vals{lp_time_slew_prob_allot_1},
	       LPTcur1 => $status_vals{lp_time_cur_1},
	       LPTSPcur1 => $status_vals{lp_time_slew_prob_cur_1},
	       LPBalance1 => $status_vals{lp_time_slew_prob_bal_1},
	       LPTreq2 => $status_vals{lp_time_req_2},
	       LPTSPreq2 =>$status_vals{lp_time_slew_prob_req_2},
	       LPAllot2 => $status_vals{lp_time_slew_prob_allot_2},
	       LPTcur2 => $status_vals{lp_time_cur_2},
	       LPTSPcur2 => $status_vals{lp_time_slew_prob_cur_2},
	       LPBalance2 => $status_vals{lp_time_slew_prob_bal_2},
	      );
    print $fh $tex;

    # VLP Exposure Time Summary
  if ($isVLP) {
    $tex = q{
\begin{center} \textbf{VLP}

\begin{tabular} {|r|r||r||r|} \hline
&  Slew+p & AO+1 &  AO+2  \\\\ \hline
Req &  %VLPTSPreq%  & %VLPTSPreq1% &  %VLPTSPreq2% \\\\ \hline
Allot &  %VLPAllot% &  %VLPAllot1% &  %VLPAllot2% \\\\ \hline
Cur &  %VLPTSPcur% &  %VLPTSPcur1% &  %VLPTSPcur2% \\\\ \hline
Bal &  %VLPBalance% &  %VLPBalance1% &  %VLPBalance2% \\\\ \hline
\end{tabular}
\end{center}
\end{multicols}
};

    substitute($tex,
	       VLPTreq => $status_vals{vlp_time_req},
	       VLPTSPreq =>$status_vals{vlp_time_slew_prob_req},
	       VLPAllot => $status_vals{vlp_time_slew_prob_allot},
	       VLPTcur => $status_vals{vlp_time_cur},
	       VLPTSPcur => $status_vals{vlp_time_slew_prob_cur},
	       VLPBalance => $status_vals{vlp_time_slew_prob_bal},
	       VLPTreq1 => $status_vals{vlp_time_req_1},
	       VLPTSPreq1 =>$status_vals{vlp_time_slew_prob_req_1},
	       VLPAllot1 => $status_vals{vlp_time_slew_prob_allot_1},
	       VLPTcur1 => $status_vals{vlp_time_cur_1},
	       VLPTSPcur1 => $status_vals{vlp_time_slew_prob_cur_1},
	       VLPBalance1 => $status_vals{vlp_time_slew_prob_bal_1},
	       VLPTreq2 => $status_vals{vlp_time_req_2},
	       VLPTSPreq2 =>$status_vals{vlp_time_slew_prob_req_2},
	       VLPAllot2 => $status_vals{vlp_time_slew_prob_allot_2},
	       VLPTcur2 => $status_vals{vlp_time_cur_2},
	       VLPTSPcur2 => $status_vals{vlp_time_slew_prob_cur_2},
	       VLPBalance2 => $status_vals{vlp_time_slew_prob_bal_2});
    print $fh $tex;

    }
    
    # XVP Exposure Time Summary
  if ($isXVP) {
    $tex = q{
\begin{center} \textbf{XVP}

\begin{tabular} {|r|r||r||r|} \hline
&  Slew+p & AO+1 &  AO+2  \\\\ \hline
Req &  %XVPTSPreq%  & %XVPTSPreq1% &  %XVPTSPreq2% \\\\ \hline
Allot &  %XVPAllot% &  %XVPAllot1% &  %XVPAllot2% \\\\ \hline
Cur &  %XVPTSPcur% &  %XVPTSPcur1% &  %XVPTSPcur2% \\\\ \hline
Bal &  %XVPBalance% &  %XVPBalance1% &  %XVPBalance2% \\\\ \hline
\end{tabular}
\end{center}
\end{multicols}
};

    substitute($tex,
	       XVPTreq => $status_vals{xvp_time_req},
	       XVPTSPreq =>$status_vals{xvp_time_slew_prob_req},
	       XVPAllot => $status_vals{xvp_time_slew_prob_allot},
	       XVPTcur => $status_vals{xvp_time_cur},
	       XVPTSPcur => $status_vals{xvp_time_slew_prob_cur},
	       XVPBalance => $status_vals{xvp_time_slew_prob_bal},
	       XVPTreq1 => $status_vals{xvp_time_req_1},
	       XVPTSPreq1 =>$status_vals{xvp_time_slew_prob_req_1},
	       XVPAllot1 => $status_vals{xvp_time_slew_prob_allot_1},
	       XVPTcur1 => $status_vals{xvp_time_cur_1},
	       XVPTSPcur1 => $status_vals{xvp_time_slew_prob_cur_1},
	       XVPBalance1 => $status_vals{xvp_time_slew_prob_bal_1},
	       XVPTreq2 => $status_vals{xvp_time_req_2},
	       XVPTSPreq2 =>$status_vals{xvp_time_slew_prob_req_2},
	       XVPAllot2 => $status_vals{xvp_time_slew_prob_allot_2},
	       XVPTcur2 => $status_vals{xvp_time_cur_2},
	       XVPTSPcur2 => $status_vals{xvp_time_slew_prob_cur_2},
	       XVPBalance2 => $status_vals{xvp_time_slew_prob_bal_2});
    print $fh $tex;

    }
  }

  # RC and TOO Summary
  $tex = q{
\section*{RC \& TOOs}
\begin{center}
\begin{tabular} {|r|c|c|c||c|c|c||c|c|c|c|} \hline
& \multicolumn{3}{c||}{Resource Cost} & \multicolumn{3}{c||}{High Ecliptic Latitude} & \multicolumn{4}{c|}{TOOs}  \\\\ \hline
& AO & AO+1 & AO+2 & AO & AO+1 & AO+2 &  \(<\)5d & 5-20d & 20-40d & \(>=\)40d \\\\ \hline
Requested & %TCreq% & %TCreq1% & %TCreq2% & %Helreq% & %Helreq1% & %Helreq2% &  %VFreq% & %Freq% & %Sreq% & %VSreq% \\\\ \hline
Allotted & %TCall% & %TCall1% & %TCall2% & %Helall% & %Helall1% & %Helall2% & %VFall% & %Fall% & %Sall% & %VSall% \\\\ \hline
Current & %TCcur% & %TCcur1% & %TCcur2%  & %Helcur% & %Helcur1% & %Helcur2%  & %VFcur% & %Fcur% & %Scur% & %VScur% \\\\ \hline
Balance & %TCbal% & %TCbal1% & %TCbal2% & %Helbal% & %Helbal1% & %Helbal2% & %VFbal% & %Fbal% & %Sbal% & %VSbal% \\\\ \hline
\end{tabular}
\end{center}
Exposure Time Summary
};

  substitute($tex,
	     Helreq => $status_vals{hel_slew_prob_req},
	     Helreq1 => $status_vals{hel_slew_prob_req_1},
	     Helreq2 => $status_vals{hel_slew_prob_req_2},
	     TCreq => $status_vals{rc_req},
	     TCreq1 => $status_vals{rc_req_1},
	     TCreq2 => $status_vals{rc_req_2},
	     VFreq => $status_vals{vf_req},
	     Freq => $status_vals{f_req},
	     Sreq => $status_vals{s_req},
	     VSreq => $status_vals{vs_req},
	     TCall => $status_vals{rc_allot},
	     TCall1 => $status_vals{rc_allot_1},
	     TCall2 => $status_vals{rc_allot_2},
	     Helall => $status_vals{hel_slew_prob_allot},
	     Helall1 => $status_vals{hel_slew_prob_allot_1},
	     Helall2 => $status_vals{hel_slew_prob_allot_2},
	     VFall => $status_vals{vf_allot},
	     Fall => $status_vals{f_allot},
	     Sall => $status_vals{s_allot},
	     VSall => $status_vals{vs_allot},	     
	     TCcur => $status_vals{rc_cur},
	     TCcur1 => $status_vals{rc_cur_1},
	     TCcur2 => $status_vals{rc_cur_2},
	     Helcur => $status_vals{hel_slew_prob_cur},
	     Helcur1 => $status_vals{hel_slew_prob_cur_1},
	     Helcur2 => $status_vals{hel_slew_prob_cur_2},
	     VFcur => $status_vals{vf_cur},
	     Fcur => $status_vals{f_cur},
	     Scur => $status_vals{s_cur},
	     VScur => $status_vals{vs_cur},	     
	     TCbal => $status_vals{rc_bal},
	     TCbal1 => $status_vals{rc_bal_1},
	     TCbal2 => $status_vals{rc_bal_2},
	     Helbal => $status_vals{hel_slew_prob_bal},
	     Helbal1 => $status_vals{hel_slew_prob_bal_1},
	     Helbal2 => $status_vals{hel_slew_prob_bal_2},
	     VFbal => $status_vals{vf_bal},
	     Fbal => $status_vals{f_bal},
	     Sbal => $status_vals{s_bal},
	     VSbal => $status_vals{vs_bal});
  print $fh $tex;

  # Joint Summary
  $tex = q{
\section*{Joint Summary}
\begin{center}
%\begin{tabular} {|r|r|r|r|r|r|r|r|r|} \hline
\begin{tabular} {|r|r|r|r|r|r|r|r|} \hline
& HST & XMM & NOAO & NRAO & SWIFT & NUSTAR \\\\ \hline
Requested & %HSTreq% & %XMMreq% & %NOAOreq% & %NRAOreq% &  %SWIFTreq% & %NUSTARreq% \\\\ \hline
Allotted & %HSTall% & %XMMall% & %NOAOall% & %NRAOall% &  %SWIFTall%  & %NUSTARall%\\\\ \hline
Current & %HSTcur% & %XMMcur% & %NOAOcur% & %NRAOcur% &  %SWIFTcur%  & %NUSTARcur%\\\\ \hline
Balance & %HSTbal% & %XMMbal% & %NOAObal% & %NRAObal% &  %SWIFTbal% & %NUSTARbal% \\\\ \hline
\end{tabular}
\end{center}

};

  substitute($tex,
	     HSTreq => $status_vals{hst_req},
	     XMMreq => $status_vals{xmm_req},
	     NOAOreq => $status_vals{noao_req},
	     NRAOreq => $status_vals{nrao_req},
	     SPITZERreq => $status_vals{spitzer_req},
	     SWIFTreq => $status_vals{swift_req},
	     NUSTARreq => $status_vals{nustar_req},
	     HSTall => $status_vals{hst_allot},
	     XMMall => $status_vals{xmm_allot},
	     NOAOall => $status_vals{noao_allot},
	     NRAOall => $status_vals{nrao_allot},
	     SPITZERall => $status_vals{spitzer_allot},
	     SWIFTall => $status_vals{swift_allot},
	     NUSTARall => $status_vals{nustar_allot},
	     HSTcur => $status_vals{hst_cur},
	     XMMcur => $status_vals{xmm_cur},
	     NOAOcur => $status_vals{noao_cur},
	     NRAOcur => $status_vals{nrao_cur},
	     SPITZERcur => $status_vals{spitzer_cur},
	     SWIFTcur => $status_vals{swift_cur},
	     NUSTARcur => $status_vals{nustar_cur},
	     HSTbal => $status_vals{hst_bal},
	     XMMbal => $status_vals{xmm_bal},
	     NOAObal => $status_vals{noao_bal},
	     NRAObal => $status_vals{nrao_bal},
	     SPITZERbal => $status_vals{spitzer_bal},
	     SWIFTbal => $status_vals{swift_bal},
	     NUSTARbal => $status_vals{nustar_bal},
	    );
  print $fh $tex;

  if ($panel_id != 99) {
    # Status Summary
    $tex = q{
\begin{multicols}{2}
\section*{Status Summary}
\begin{center}
\begin{tabular} {|r|r|r|r|r|r|} \hline
& Y & N & G & B & All \\\\ \hline
GO Props & %StdPropY% & %StdPropN% & %StdPropG% & %StdPropP% & %StdProp% \\\\ \hline
GO Targets & %StdTargY% & %StdTargN% & %StdTargG% & %StdTargP% & %StdTarg% \\\\ \hline
TOO Props & %TooPropY% & %TooPropN% & %TooPropG% & %TooPropP% & %TooProp% \\\\ \hline
TOO Targets & %TooTargY% & %TooTargN% & %TooTargG% & %TooTargP% & %TooTarg% \\\\ \hline
LP Props & %BppPropY% & %BppPropN% & %BppPropG% & %BppPropP% & %BppProp% \\\\ \hline
LP Targs & %BppTargY% & %BppTargN% & %BppTargG%  & %BppTargP% & %BppTarg% \\\\ \hline
Archive & %ArcY% & %ArcN% & & & %Arc% \\\\ \hline
Theory & %TheY% & %TheN% & & & %The% \\\\ \hline
\end{tabular}
\end{center}

\columnbreak

};
    
    substitute($tex,
	       StdPropY => $status_vals{std_prop_y},
	       StdPropN => $status_vals{std_prop_n},
	       StdPropG => $status_vals{std_prop_g},
	       StdPropP => $status_vals{std_prop_p},
	       StdProp => $status_vals{std_prop},
	       StdTargY => $status_vals{std_targ_y},
	       StdTargN => $status_vals{std_targ_n},
	       StdTargG => $status_vals{std_targ_g},
	       StdTargP => $status_vals{std_targ_p},
	       StdTarg => $status_vals{std_targ},
	       TooPropY => $status_vals{too_prop_y},
	       TooPropN => $status_vals{too_prop_n},
	       TooPropG => $status_vals{too_prop_g},
	       TooPropP => $status_vals{too_prop_p},
	       TooProp => $status_vals{too_prop},
	       TooTargY => $status_vals{too_targ_y},
	       TooTargN => $status_vals{too_targ_n},
	       TooTargG => $status_vals{too_targ_g},
	       TooTargP => $status_vals{too_targ_p},
	       TooTarg => $status_vals{too_targ},
	       BppPropY => $status_vals{bpp_prop_y},
	       BppPropN => $status_vals{bpp_prop_n},
	       BppPropG => $status_vals{bpp_prop_g},
	       BppPropP => $status_vals{bpp_prop_p},
	       BppProp => ($status_vals{lp_prop} + $status_vals{vlp_prop}),
	       BppTargY => $status_vals{bpp_targ_y},
	       BppTargN => $status_vals{bpp_targ_n},
	       BppTargG => $status_vals{bpp_targ_g},
	       BppTargP => $status_vals{bpp_targ_p},
	       BppTarg => ($status_vals{lp_targ} + $status_vals{vlp_targ}),
	       ArcY => $status_vals{arc_y},
	       ArcN => $status_vals{arc_n},
	       Arc => $status_vals{arc_prop},
	       TheY => $status_vals{the_y},
	       TheN => $status_vals{the_n},
	       The => $status_vals{the_prop});
    print $fh $tex;
    
    # Fixed Totals
    $tex = q{
\section*{Fixed Totals}
\begin{center}
\begin{tabular} {|r|r||r|r|} \hline
Std Prop: & %StdProp% & Std Targ: & %StdTarg% \\\\ \hline
LP Prop: & %LpProp% & LP Targ: & %LpTarg% \\\\ \hline
VLP Prop: & %VlpProp% & XVP Targ: & %VlpTarg% \\\\ \hline
TOO Prop: & %TooProp% & TOO Targ: & %TooTarg% \\\\ \hline
ARC Prop: & %Arc% & & \\\\ \hline
THE Prop: & %The% & & \\\\ \hline
Total Prop: & %Prop% & Total Targ: & %Targ% \\\\ \hline
\end{tabular}
\end{center}
\end{multicols}

};

    substitute($tex, 
	       StdProp => $status_vals{std_prop},
	       StdTarg => $status_vals{std_targ},
	       LpProp => $status_vals{lp_prop},
	       LpTarg => $status_vals{lp_targ},
	       VlpProp => $status_vals{vlp_prop},
	       VlpTarg => $status_vals{vlp_targ},
	       XvpProp => $status_vals{xlp_prop},
	       XvpTarg => $status_vals{xlp_targ},
	       TooProp => $status_vals{too_prop},
	       TooTarg => $status_vals{too_targ},
	       Arc => $status_vals{arc_prop},
	       The => $status_vals{the_prop},
	       Prop => $status_vals{num_props},
	       Targ => $status_vals{num_targs});
    print $fh $tex;
  }

  
  printEnd($fh);  
  $fh->close;

  latexFile($file);

  # generate PostScript
  #if ($panel_id != 99) {
    #psFile( $file, 'portrait' );
  #} else {
    #psFile( $file, 'landscape' );
  #}
 system("$main::param{gv} $file.pdf") == 0
     or die "Trouble opening PDF file:   $?  $file.pdf\n";

  # Send to printer
  #my $log = $file . '.lp.log';
  #print "$main::param{printer}\n";
  #system("lp -d $main::param{printer} $file.ps") == 0
    #or die "Troubling printing file to printer: see $log\n";
}

## Class Method ##
# Name: createFile
# Purpose: create latex of panel view
# Parameters: panel object
#             view id
#             group by string
#             sort by string
#             'hide_triage' flag
#             'hide_arc_the' flag
#             'hide_lp_vlp' flag
#             font size
#             print orientation
#             font point family
#             file name
# Returns: nothing
sub createFile {
  my ($panel, $view_id, $groupby, $sortby, $hide_triage, $hide_arc_the, 
      $hide_lp_vlp, 
      $landscape, $size, $pnt_size, $file) = @_;

  my $dbh = $panel->dbh;
  my $panel_id = $panel->panel_id;

  unlink("$file.tex") if (-e "$file.tex") ;
  my $fh = IO::File->new( ">$file.tex" )
    or die "Sorry, can't open $file.tex: $!:\n";

  printStyle($fh, 'final', $landscape, $size, $pnt_size);
  printHeaders($fh, $dbh, $panel_id, $view_id,$panel->{lcd_sortname});
 

  my %gslist = $panel->get_group_list($groupby, $sortby, 
				      $hide_triage, $hide_arc_the, 
				      $hide_lp_vlp);
  my $count = 0;
  foreach my $group (sort keys %gslist) {
      $count++;
      my ($layout, $headers, $sections) = getColHdrs($dbh, $view_id, 
						     $panel_id);
      beginTable($fh, $group, $layout);
      printColHdrs($fh, $headers, $size, 1);
      my $sorted_list = $gslist{$group};
      my %sorted_list = %$sorted_list;
      foreach my $prop_order (sort {$a <=> $b} keys %$sorted_list) {
	  my $prop_id = $sorted_list{$prop_order};
	  my $proposal = $panel->proposals->proposal($prop_id);
	  printRow($fh, $sections, $proposal, $size);
      }
      endTable($fh);
  }
  printEnd($fh);
  $fh->close;
}

## Internal Utility ##
# Name: printStyle
# Purpose: writes the style section of the latex document to a file handle
# Parameters: file handle
#             draft mode
#             orientation
#             font size
#             point size
# Returns: nothing
sub printStyle {
  my ($fh, $draft, $orient, $size, $pnt_size) = @_;
  my $tex = q{
\special{! TeXDict begin /landplus90{true} store end}
\documentclass[letterpaper,onecolumn,%PNTSIZE%pt,notitlepage,%DRAFT%,%ORIENT%]{article}
\usepackage[letterpaper,left=0.25in,right=0.25in,headheight=14.5pt]{geometry}
\usepackage{newcent}
\usepackage{fancyhdr}
\usepackage[T1]{fontenc}
\usepackage{longtable}
\usepackage{lastpage}
\oddsidemargin 0in
%\setlength\LTleft{-.25in}
\addtolength{\hoffset}{-.70in}
\newcommand{\grpid}{PLEASE SET THE GRPID, LAZYBONES}
\begin{document}
\%SIZE%
};

  substitute( $tex, 
	      DRAFT  => $draft,
	      ORIENT => $orient,
	      SIZE   => $size,
	      PNTSIZE => $pnt_size);

  print $fh $tex;
}

## Internal Utility ##
# Name: printEnd
# Purpose: prints the end statements to a filehandle
# Parameters: file handle
# Returns: nothing
sub printEnd {
  my $fh = shift;
  print $fh "\\end{document}\n";
}

## Internal Utility ##
# Name: printHeaders
# Purpose: prints the header and footer sections to a filehandle
# Parameters: file handle
#             database handle
#             panel id
#             view id
# Returns: nothing
sub printHeaders {
  my ($fh, $dbh, $panel_id, $view_id,$sortname) = @_;

  my $query = $dbh->prepare(qq(select view_name from views where panel_id = ? 
                               and view_id = ?));
  $query->execute($panel_id, $view_id);
  my $title = $query->fetchrow_array;
  $query->finish;
  my($stitle) = sanitize($title);

  my $tex = q{
\pagestyle{fancy}
\lhead{\bf{Panel %PANELID%, Group \grpid}}
\chead{\bf{%TITLE%} \footnotesize{%SORTNAME%} }
\rhead{\bf{%DATE%}}
\lfoot{}
\cfoot{\thepage\ of \pageref{LastPage}}
\rfoot{}
\renewcommand{\footrulewidth}{0.4pt}
};

  if (!defined $sortname) { $sortname=""; }
  else { $sortname = "by " . $sortname; }
  my($ssortname) = sanitize($sortname);

  substitute( $tex, 
	      PANELID => $panel_id,
	      TITLE   => $stitle,
	      SORTNAME => $ssortname,
	      DATE    => scalar localtime,
	    );

  print $fh $tex;
}

## Internal Utility ##
# Name: beginTable
# Purpose: prints the begin table statements to a filehandle
# Parameters: file handle
#             caption
#             layout of table
# Returns: nothing
sub beginTable {
  my ($fh, $caption, $layout) = @_;

  my($scap) = sanitize($caption);
  print $fh '\renewcommand{\grpid}{' . $scap . "}\n";
  print $fh '\begin{longtable}';
  print $fh "$layout  ";
  print $fh '\hline';
  print $fh "\n";
}

## Internal Utility ##
# Name: endTable
# Purpose: prints statements to end a table to a file handle
# Parameters: file handle
# Returns: nothing
sub endTable {
  my $fh = shift;
  print $fh "\\end{longtable}\n";
  print $fh "\\newpage\n";
}

## Internal Utility ##
# Name: getColHdrs
# Purpose: retrieves the column headers from the database
# Parameters: database handle
#             view id
#             panel id
# Returns: column layout
#          reference to array of header information
#          reference to array of section information
sub getColHdrs {
  my ($dbh, $view_id, $panel_id) = @_;

  #my $query = $dbh->prepare(qq(select section_id, section_name, section_type
			       #from sections where panel_id = ? and view_id = ?
			       #order by section_order));
  #this query will exclude any empty sections
  my $query = $dbh->prepare(qq(select distinct sections.section_id, 
	section_name, section_type, sections.section_order
     	from sections,section_columns sc
 	where sections.panel_id = ? and sections.view_id = ?
	and sections.panel_id = sc.panel_id
	and sections.view_id = sc.view_id
	and sections.section_id = sc.section_id
     	order by sections.section_order));


  $query->execute($panel_id, $view_id);
  #print STDERR "print $panel_id  view=$view_id \n";
  my @blocks;
  while (my ($sec_id, $sec_name, $sec_type,$ordr) = $query->fetchrow_array) {
      #print STDERR "Print: sec_name = $sec_name\n";
      my %blk = (id => $sec_id,
		 name => $sec_name,
		 type => $sec_type);
      push @blocks, \%blk;
  }
  $query = $dbh->prepare(qq(select max(rank) from proposal where panel_id = ?));
  $query->execute($panel_id);
  my ($is_ranked) = $query->fetchrow_array;
  $query->finish;
  $query = $dbh->prepare(qq(select pub from views where view_id = ? and panel_id = ?));
  $query->execute($view_id,$panel_id);
  my ($is_final) = $query->fetchrow_array;
  $query->finish;


  $query = $dbh->prepare(qq(select col_name, attribute, justify, display, 
			    table_id from columns c, section_columns s where 
			    c.col_id = s.col_id and s.panel_id = c.panel_id 
			    and s.panel_id = ? and s.view_id = ? and 
			    section_id = ? order by col_order));
  my @hdrs;
  my @sections;
  my $layout = "{|";
  foreach my $blk (@blocks) {
      if ($$blk{type} =~ /prop_id/) {
	  push @hdrs, "Prop\#\tspreadsheet\Tlabel";
	  my @atts = "prop_id\tc";
	  my %sec = (name => 'Prop#',
		     type => 'spreadsheet',
		     atts => \@atts);
	  push @sections, \%sec;

	  push @hdrs, "PI\tspreadsheet\tlabel";
	  my @atts2 = "last_name\tl";
	  my %sec = (name => 'PI',
		     type => 'spreadsheet',
		     atts => \@atts2);
	  push @sections, \%sec;
	  $layout .= "c|l|";

          #not final view and rank exists
          if ($is_ranked > 0 && $is_final !~ /F/) {
	    push @hdrs, "Rank\tspreadsheet\tlabel";
	    my @atts3 = "rank\tl";
	    my %sec = (name => 'Rank',
		       type => 'spreadsheet',
		       atts => \@atts3);
	    push @sections, \%sec;
	    $layout .= "c|l|";
          }
      }
      elsif ($$blk{type} =~ /spreadsheet|prop_edit|prop_status|combo_cols|multi_cols/) {
	  $query->execute($panel_id, $view_id, $$blk{id});
	  while ( my($col_name, $att, $justify,
		     $display, $table_id) = $query->fetchrow_array) {
	    $justify = '@{ }' . $justify . '@{ }';
	      push @hdrs, "$col_name\tspreadsheet";
	      $layout .= "$justify|";
	      my @atts = "$att\t$justify\t$display\t$table_id";
	      my %sec = (name => $col_name,
			 type => $$blk{type},
			 atts => \@atts);
	      push @sections, \%sec;
	  }
	  $query->finish;
      }
      elsif ($$blk{type} =~ /proposal|target/) {
	  $query->execute($panel_id, $view_id, $$blk{id});
	  push @hdrs, $$blk{name} . "\t" . $$blk{type};
	  $layout .= "c|";
	  my @atts;
	  while ( my($col_name, $att, $justify,  
		     $display, $table_id) = $query->fetchrow_array) {
	      push @atts, "$col_name\t$att\t$justify\t$display\t$table_id";
	  }
	  $query->finish;

	  my %sec = (name => $$blk{name},
		     type => $$blk{type},
		     atts => \@atts);
	  push @sections, \%sec;
      }
  }

  $layout .= '}';

  return $layout, \@hdrs, \@sections;
}

## Internal Utility ##
# Name: sanitize
# Purpose: escapes special characters
# Parameters: string to sanitize
# Returns: sanitized string
sub sanitize {
  # These are special characters which need to be escaped
  #       # $ % & ~ _ ^ { }'
  #  / also needs to be escaped, but I haven't found the magic yet

  my $val = shift;
  $val =~ s/\#/\\\#/g;
  $val =~ s/\$/\\\$/g;
  $val =~ s/\%/\\\%/g;
  $val =~ s/\&/\\\&/g;
  $val =~ s/\~/\\\~/g;
  $val =~ s/\_/\\\_/g;
  $val =~ s/\^/\\\^/g;
  $val =~ s/\{/\\\{/g;
  $val =~ s/\}/\\\}/g;
  return $val;
}

## Internal Utility ##
# Name: printColHdrs
# Purpose: prints column headers to a file handle unless for a target table
# Parameters: file handle
#             reference to header information
#             font size
#             flag indicating that sanitize needs to be performed
# Returns: returns the text if for a target table
sub printColHdrs {
  my ($fh, $headers, $size, $sanitize) = @_;
  # Get the font size for the column headers.  It will be two sizes smaller
  # than the table font size, unless the size is already too small
  my ( $sz_index ) = 
    grep { $LaTeXSizes[$_] eq $size } 0..@LaTeXSizes-1;
  $sz_index -= 3 if $sz_index > 3;
  $size = $LaTeXSizes[$sz_index];

  my ($hdr_row, $target);
  foreach my $col (@$headers) {
      my ($heading, $type) = split /\t/, $col;
      my @word = split /\s/, $heading;
      # Make multi-word spreadsheet headings be multi-line
      if ($type =~ /spreadsheet|prop_status|prop_edit|combo_cols|multi_cols/ and 
	  @word > 1) {
	  $hdr_row .= "\n";
	  $hdr_row .= '\begin{tabular}{|c|} ';
	  foreach my $word (@word) {
	      $col = sanitize($word) if $sanitize;
	      $hdr_row .= "\\multicolumn\{1\}\{c\}\{\\";
	      $hdr_row .= $size;
	      $hdr_row .= '{';
	      $hdr_row .= $col;
	      $hdr_row .= '}} \\';
	      $hdr_row .= '\\ ';
	  }
	  $hdr_row .= '\end{tabular} & ';
      }
      elsif (!$type) {
	  $target = 1;
	  $hdr_row .= "\n";
	  $hdr_row .= '\begin{tabular}{|c|} ';
	  foreach my $word (@word) {
	      $col = sanitize($word) if $sanitize;
	      $hdr_row .= "\\multicolumn\{1\}\{c\}\{\\";
	      $hdr_row .= $size;
	      $hdr_row .= '{';
	      $hdr_row .= $col;
	      $hdr_row .= '}} \\';
	      $hdr_row .= '\\ ';
	  }
	  $hdr_row .= '\end{tabular} & ';
      }
      else {
	  $hdr_row .= "\n";
	  $col = sanitize($heading) if $sanitize;
	  $hdr_row .= "\\multicolumn\{1\}\{c\|\}\{\\";
	  $hdr_row .= $size;
	  $hdr_row .= '{';
	  $hdr_row .= $col;
	  $hdr_row .= '}} & ';
      }
  }
  $hdr_row =~ s/\{c\|\}/\{\|c\|\}/;
  $hdr_row =~ s/\&\s+$//;
  $hdr_row .= ' \\\\ \hline ' if !$target;
  $hdr_row .= '\endhead' if !$target;
  $hdr_row .= "\n";

  print $fh $hdr_row if !$target;
  return $hdr_row;
}


## Internal Utility ##
# Name: printRow
# Purpose: prints a row of a table to a file handle
# Parameters: file handle
#             reference to section information
#             proposal object
#             font size
#             target headers
# Returns: nothing
sub printRow {
  my ($fh, $sections, $proposal, $size, $target_hdr) = @_;
  my $row;
  foreach my $sec (@$sections){
    if ($$sec{type} =~ /spreadsheet|prop_status|prop_edit/) {
      my $col_def = $$sec{atts};
      my ($att, $justify, $display) = split /\t/, @$col_def[0];
      $att =~ s/cum_/cum_lcd_/ if $att =~ /cum/;
      my $val = $proposal->get($att);
      $val = sanitize($val);
      $row .= "$val & ";
    }
    elsif ($$sec{type} =~ /combo_cols/) {
      my $val = ' ';
      my $col_def = $$sec{atts};
      my ($att, $justify, $display) = split /\t/, @$col_def[0];
      my $vala = $proposal->get($att);
      $att =~ s/app/req/;
      my $valr = $proposal->get($att);
      if ($valr > 0) {
        $val = $vala . ' / ' . $valr;
      }
      $val = sanitize($val);
      $row .= "$val & ";
    }
    elsif ($$sec{type} =~ /multi_cols/) {
      my $col_def = $$sec{atts};
      my ($att, $justify, $display) = split /\t/, @$col_def[0];
      my $val = $proposal->get($att);
      $att .= '_1';
      my $v1  = $proposal->get($att);
      $att =~ s/_1/_2/;
      my $v2  = $proposal->get($att);
      if ($v1 > 0 || $v2 > 0) {
        $val .= ' / ' . $v1;
        if ($v2)  {
          $val .= ' / ' . $v2;
        }
      }
      $val =~ s/\/  \/ $//;
      $val = sanitize($val);
      $row .= "$val & ";
    }
    elsif ($$sec{type} =~ /proposal/) {
      $row .= "\n";
      $row .= '\begin{tabular}{rl} ';
      my $atts = $$sec{atts};
      my %tables;
      foreach my $col_def (@$atts) {
	my ($label, $att, $justify, 
	    $display, $table_id) = split /\t/, $col_def;
	if ($display !~ /table/) {
	  my $val = $proposal->get($att);
	  $val = sanitize($val);
	  $row .= "$label: & $val \\\\ ";
	}
	else{
	  #put all columns for each table together
	  if (exists $tables{$table_id}) {
	    my $cols = $tables{$table_id};
	    push @$cols, "$label\t$att\t$justify";
	    $tables{$table_id} = $cols;
	  }
	  else {
	    my @cols;
	    push @cols, "$label\t$att\t$justify";
	    $tables{$table_id} = \@cols;
	  }
	}
      }
      # Now we can do the tables
      my $query = $proposal->dbh->prepare(qq(select col_name, table_name 
						 from table_columns where
						 panel_id = ? and table_id = ? 
						 order by col_order));
      foreach my $table_id (sort {$a <=> $b} keys %tables) {
	# get the column headings
	$query->execute($proposal->panel_id, $table_id);
	my @hdr;
	my $table_name;
        my $nn=0;
        my $ngrade=$num_grades;
	while (my($col_name, $name) = $query->fetchrow_array) {
	  $table_name = $name;
          if ($table_name =~ /Grades/i && $nn >= $num_grades) { last; }
	  push @hdr, $col_name;
          $nn++;
	}
	$query->finish;
	
	$row .= "\n";
	if ($table_name =~ /Joint/) {
	  $row .= '\multicolumn{2}{c}{';
	  $row .= "$table_name} \\\\ ";
	  $row .= "\n";
	  $row .= '\multicolumn{2}{c}{\begin{tabular}{|c|';
	}
	else {
	  $row .= '{\begin{tabular}{|c|';
	}
	
	#finish the column justification
	foreach (my $i = 0; $i < scalar @hdr; $i++) {
	  $row .= 'r|';
	}
	$row .= '} \hline';
	$row .= "\n";

if ($table_name !~ /Grades/) {
	$row .= '& ';   # This leaves the heading of the first
	# column blank
	$row .= printColHdrs($fh, \@hdr, $size, 1);
	$row .= ' \\\\ \hline';
	$row .= "\n";
}
	
	# Now put in the data
	if ($table_name =~ /Joint/) {
	  my $cols = $tables{$table_id};
	  foreach my $col (@$cols) {
	    my ($label, $att, $justify) = split /\t/, $col;
	    $label = sanitize($label);
	    $row .= "$label &";
	    
	    foreach my $obs (@hdr) {
	      my $att = lc($obs);
	      $att .= '_req' if $label =~ /Req/;
	      $att .= '_app' if $label =~ /App/;
	      my $val = $proposal->get($att);
	      $val = sanitize($val);
	      $row .= "$val & ";
	    }
	    $row =~ s/&\s+$//;
	    $row .= ' \\\\ \hline';
	    $row .= "\n";
	  }
	  $row .= '\end{tabular}}';
	}
	elsif ($table_name =~ /Grades/) {
          my $ngrade = $num_grades;
	  my $cols = $tables{$table_id};
	  foreach my $col (@$cols) {
	    my ($label, $att, $justify) = split(/\t/, $col);
            if ($label =~ /PrelimGrades/)  {
               $ngrade=$num_pgrades;
            }
            if ($label !~ /FinalGrades/ && $label !~ /PrelimGrades/) {
	      $label = sanitize($label);
  	      $row .= "$label &";
            }

	    #print Dumper \@hdr;
            my $nn=0;
	    foreach my $grade (@hdr) {
              if ($nn < $ngrade) {
	        my $att = "pg$grade" if $label =~ /Prelim/;
	        $att = "g$grade" if $label =~ /Final/;
	        my $val = $proposal->get($att);
	        $val = sanitize($val);
                if (!defined $val ) {
                  $val = "\\phantom {0.00}";
                } else {
                  $val = sprintf("%4.2f",$val);
                }
	        $row .= "$val & ";
              }
              $nn++;
	    }
	    $row =~ s/&\s+$//;
	    $row .= ' \\\\ \hline';
	    $row .= "\n";
	  }
	  $row .= '\end{tabular}}';
	}
      }
      $row =~ s/\\\\\s+$//;
      $row .= '\end{tabular} & ';
    }
    elsif ($$sec{type} =~ /target/) {
      # There is no table for archive and theory proposals
      if ($proposal->type !~ /ARC|THE/) {
	$row .= "\n";
	$row .= '\begin{tabular}{|';
	
	# get the column headings
	my @hdr;
	my $atts = $$sec{atts};
	foreach my $col_def (@$atts) {
	  my ($label, $att, $justify, $display) = split /\t/, $col_def;
	  push @hdr, $label;
	  $row .= "$justify|";
	}
	$row .= '} \hline';
	$row .= "\n";
	$row .= printColHdrs($fh, \@hdr, $size, 1);
	$row .= ' \\\\ \hline';
	$row .= "\n";
	
	# Now put in the data
	for (my $i = 1; $i <= $proposal->num_targets; $i++) {
	  foreach my $col_def (@$atts) {
	    my ($label, $att, 
		$justify, $display) = split /\t/, $col_def;
	    my $val = $proposal->target($i)->get($att);
	    $val = sanitize($val);
	    $row .= "$val & ";
	  }
	  $row =~ s/&\s+$//;
	  $row .= ' \\\\ \hline';
	  $row .= "\n";
	}
	
	$row .= '\end{tabular} & ';
      }
      else {$row .= " & ";}
    }
  }
  $row =~ s/&\s+$//;
  $row .= ' \\\\ \hline';
  $row .= "\n";

  print $fh $row;
}

## Internal Utility ##
# Name: latexFile
# Purpose: latex's the file
# Parameters: filename
# Returns: nothing
sub latexFile {
  my $file = shift;

  my ( $name, $path, $suffix ) = fileparse( $file, qw/.pdf .ps .tex / );
  my $log = File::Spec->catfile( $path, $name . '.log' );
  my $cwd = cwd;

  # Run latex in batchmode so the user is not prompted if there are errors
  # The errors will be printed to the log file
  #my $command = "latex -interaction=batchmode $name >/dev/null 2>&1";
  my $command = "pdflatex -interaction=batchmode $name >/dev/null 2>&1";
  #print STDERR "$command\n";

  my $return = 
    eval {

      chdir $path or die( "unable to cd to $path\n" );

      # Check to see if latex needs to be run again for indexing or
      # tablewidth purposes.  Stop after 4 runs and print error message if
      # indexing has not been resolved
      my $count = 0;
      while (1) {
	system($command) == 0 or
	  die "error runing LaTeX: ($command) failed: $?. see $log\n";

	# we're done if no reruns requested
	last unless f_grep( $log, 'Rerun' );

	# if we've run out of attempts, say so
	die( "unable to resolve LaTeX rerun requests. see $log\n" )
	  if ++$count > 4;
      }

      return f_grep( $log, qr/Overfull \\hbox/ );

    };

  my $error = $@;
  chdir $cwd;

  die $error if $error;

  return $return;
}

## Internal Utility ##
# Name: psFile
# Purpose: dvips the file
# Parameters: filename
#             orientation
# Returns: nothing
sub psFile {
  my ( $file, $orient ) = @_;

  my ( $name, $path, $suffix ) = fileparse( $file, qw/ .ps .tex / );
  my $log = File::Spec->catfile( $path, $name . '.ps.log' );
  my $cwd = cwd;

  # Generate the postscript
  my $orient = $orient =~ /landscape/ ? '-t landscape' : '';

#  my $command = "dvips $orient -t letter -o $name.ps $name > $log 2>&1" ;
  my $command = "dvips -t letter $orient -o $name.ps $name > $log 2>&1" ;
  eval {
    chdir $path or die( "unable to cd to $path\n" );
    system($command) == 0
      or die "Trouble generating postscript: see $log\n";
  };
  
  my $error = $@;
  chdir $cwd;
  die $error if $error;
}


## Internal Utility ##
# Name: f_grep
# Purpose: grep a regex from LaTex log file
# Parameters: filename
#             regex
# Returns: nothing
sub f_grep {
  my ( $file, $rx ) = @_;

  open( LOG, $file )
    or die( "unable to open LaTeX log file: $file.log\n" );
  my $result = grep { /$rx/ } <LOG>;
  close LOG;

  $result;
}


## Internal Utility ##
# Name: substitute
# Purpose: put input into tag holders in template
# Parameters: list of strings to substitute
#             list of new strings
# Returns: nothing
sub substitute {
  # last elements in args are subs; avoid the first,
  # which is the string to substitute
  my @subs = @_;
  shift @subs;

  while( my ( $tag, $value ) = splice(@subs,0,2) ) {

    # must use $_[0] to alter original string!
    $_[0] =~ s/%$tag%/$value/g;
  }

}

1;

__END__

=head1 NAME

Print - This module creates LaTex files of views and turns them into 
postscript

=head1 VERSION

$Revision: 1.29 $

=head1 DESCRIPTION

Create LaTex file of a view of a panel and generate the postscript

=head1 PUBLIC METHODS

=head2 printPreview

Create LaTex of a panel view and pop-up a viewer to look at the output.

=over 

=item panel object

=item view id

=item 'group by' string

=item 'sort by' string

=item 'hide triage' flag

=item 'hide archive/theory' flag

=item 'hide LP/VLP' flag

=item font size

=item print orientation

=item font point family

=back

=head2 printFile

Create LaTex of a panel view andsent the postscript to the printer or save
to a file

=over

=item verbosity

=item panel object

=item view id

=item 'group by' string

=item 'sort by' string

=item 'hide triage' flag

=item 'hide archive/theory' flag

=item 'hide LP/VLP flag

=item font size

=item print orientation

=item font point family

=item filename

=back

=head2 createFile

Create LatTex file of panel view

=over 

=item panel object

=item view id

=item 'group by' string

=item 'sort by' string

=item 'hide triage' flag

=item 'hide archive/theory' flag

=item 'hide LP/VLP' flag

=item font size

=item print orientation

=item font point family

=item filename

=back

=head1 PRIVATE METHODS

=head2 autofitFile

Create file with largest font possible

=head2 printStyle

Writes the style section of the LaTex document to a filehandle

=head2 printEnd

Writes the end statements of the LaTex document to a filehandle

=head2 printHeaders

Writes the header and footer sections of the LaTex document to a filehandle

=head2 beginTable

Writes the statements to begin a table to a filehandle

=head2 endTable

Writes the statements to end a table to a filehandle

=head2 getColHdrs

Retrieves the columns headers for tables from the database

=head2 sanitize

Escapes special characters in a string

=head2 printColHdrs

Writes the column headers of a table to a filehandle

=head2 printRow

Writes a row of a table to a filehandle

=head2 latexFile

LaTex's the file

=head2 psFile

dvips's the file

=head2 f_grep

grep a regex from a LaTex log file

=head2 substitute

Substitute a set of strings into string holders in a template

=head1 DEPENDENCIES

This module has no dependencies.

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
