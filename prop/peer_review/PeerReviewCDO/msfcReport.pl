#!/usr/bin/perl
#
# msfcReport.pl
#
# Purpose: Provide the output files needed for the MSFC report for each panel.
#
# Copyright (c) 2008,2020-2021 Smithsonian Astrophysical Observatory
# All Rights Reserved.  Std. disclaimer applies.
#

use strict;
use DBI;
use Data::Dumper;

use vars qw ($VERSION %param);

$VERSION = '1.0';

#******#
# Main #
#******#
{
  # Option Parsing
  use Getopt::Long;
  parse_opts();
  
  if ($param{version}) {
    print $VERSION, "\n";
    exit( 0 );
  }

  if ($param{help}) {
    usage(0);
  }

  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  my $dbh = DBI->connect($dsn1, "", "", {
					 PrintError => 0,
					 RaiseError => 1});
  
  # Database connection 2
  my $dsn2 = "dbi:Pg:dbname=$param{U}";
  my $dbh2 = DBI->connect($dsn2, "", "", {
					  PrintError => 0,
					  RaiseError => 1});
  
  # Compile queries
  my $get_panels = $dbh->prepare(qq(select distinct panel_id from proposal 
                                    order by panel_id));
  my $get_propinfo = $dbh2->prepare(qq(select prop_id, fg_avg, fg_norm, 
                                       prop_status, big_proj, type, rc_score_app,
                                       total_app_hel, vf_app, f_app,
                                       total_req_time, prop_app_time, 
                                       hst_app, xmm_app, nrao_app,nustar_app,
                                       cdo_approval, last_name from proposal
                                       where panel_id = ? 
                                       order by fg_norm DESC));
  my $allot = $dbh2->prepare(qq(select rc_score, total_hel_time,  vf_too, f_too, hst,
                               xmm, nrao,nustar, total_exp_time, archive_allot, 
                               theory_allot from allotment where 
                               panel_id = ?));
  my $money = $dbh2->prepare(qq(select total_app_time from proposal where 
                                prop_id = ? and panel_id = ?));

  # Print the output for each panel
  $get_panels->execute();
  while (my ($panel) = $get_panels->fetchrow_array) {
    open (OUT, ">msfc_pan$panel.txt") ||
      die "Sorry, can't open msfc_$panel.txt: $!\n";
    binmode(OUT, ":utf8");
    print OUT "\t\tPanel $panel Sorted on: Normalized Grade\t", 
      scalar localtime, "\n";
    print OUT "PropId  NormGr  Status  SubType     Type     RC      HEL      VF  F    ReqTime  AppTime  PI\n";
    
    my $run_time = 0;
    
    # Get the data
    $get_propinfo->execute($panel);
    while (my($prop_id, $fg_avg, $fg_norm, $prop_status, 
	      $big_proj, $type, $rc_score, $prop_app_hel,  $vf,
	      $f, $req_time, $app_time, $hst, $xmm, 
	      $nrao, $nustar,$cdo, $pi) = $get_propinfo->fetchrow_array) {
      if ($type =~ /ARCHIVE|THEORY/) {
	$money->execute($prop_id, $panel);
	($app_time) = $money->fetchrow_array;
	$money->finish;
      }

      $app_time = 0 if $prop_status =~ /B|P|N|G/;
      $run_time += $app_time if $type !~ /ARCHIVE|THEORY/;
      $fg_avg = sprintf "%4.2f", $fg_avg;
      $fg_norm = sprintf "%4.2f", $fg_norm;
      $prop_status = sprintf "%6s", $prop_status;
      $big_proj = sprintf "%6s", $big_proj;
      $type = sprintf "%7s", $type;
      $rc_score = sprintf "%9.2f", $rc_score;
      $prop_app_hel = sprintf "%.1f", $prop_app_hel;
      $prop_app_hel = sprintf "%7s", $prop_app_hel;
      $vf = sprintf "%2s", $vf;
      $vf = '  ' if !$vf;
      $f = sprintf "%2s", $f;
      $f = '  ' if !$f;
      $req_time = sprintf "%7.1f", $req_time;
      $app_time = sprintf "%.1f", $app_time;
      $app_time = sprintf "%7s", $app_time;
      $run_time = sprintf "%.1f", $run_time;
      $run_time = sprintf "%7s", $run_time;
      $hst = sprintf "%3s", $hst;
      $xmm = sprintf "%3s", $xmm;
      $nrao = sprintf "%4s", $nrao;
      $nustar = sprintf "%4s", $nustar;
      $cdo = sprintf "%7s", $cdo;

      print OUT "$prop_id  $fg_norm  $prop_status   $big_proj  $type  $rc_score  $prop_app_hel  $vf  $f   $req_time  $app_time  $pi\n";
    }
    $get_propinfo->finish;
    
    # Print the allotment information
    $allot->execute($panel);
    my ($rc_score, $HEL_time, $vf, $f, $hst, $xmm,
	$nrao,$nustar, $total_time, $archive, $theory) = $allot->fetchrow_array;
    $allot->finish;
    print OUT "\n\nAllotments:\n";
    print OUT "\tTime:\t\t$total_time\t\t\tHST:\t\t$hst\n";
    print OUT "\tRC:\t\t$rc_score\t\t\tXMM:\t\t$xmm\n";
    print OUT "\t     \t\t\t\t\tNRAO:\t\t$nrao\n";
    print OUT "\t     \t\t\t\t\tNuSTAR:\t\t$nustar\n";
    print OUT "\tVF:\t\t$vf\t\t\tArchive:\t$archive\n";
    print OUT "\tF:\t\t$f\t\t\tTheory:\t\t$theory\n";
    print OUT "\tHEL:\t\t$HEL_time\n";

    close OUT;
  }
  $get_panels->finish;
}
#**********#
# End Main #
#**********#


## Internal Utility ##
# Name: parse_opts
# Purpose: define permitted commandline options
# Usage: parse_opts()
# Returns: nothing;
#          exits if an unrecognized parameter is given or if a required
#          parameter is not given
sub parse_opts {
  %param = (
	    U => undef,
	    verbose => 0
	   );
  
  GetOptions( \%param,
	      "U=s",
	      "verbose",
	      "version",
	      "help",
	    ) or exit(1);
  
  return if $param{help} or $param{version};
  
  
  my $err = 0;
  while ( my ( $par, $val ) = each ( %param ) ) {
    next if defined $val;
    warn("parameter `$par' not set\n");
    $err++;
  }
  
  exit(1) if $err;
}

## Internal Utility ##
# Name: usage
# Purpose: display help documentation and exit
# Usage: usage(0)
# Returns: nothing
sub usage {
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}


__END__

=head1 NAME

msfcReport.pl - Script for printing output files for MSFC report

=head1 USAGE

msfcReport.pl -U belinda

=head1 DESCRIPTION

This script will generate ascii output files used for the MSFC report.  There
is one file per panel.

=head1 OPTIONS

B<msfcReport.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U> <user name>

peer review user name (required)

=item B<-help>

displays documentation for B<msfcReport.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 Printing Notes

To create a postscript version of the output that fits across a landscape 
paper without wrapping lines, use enscript.  The following will usually work:

  enscript -r -fCourier8 -ofile.ps file.out

You can change the font size to your tastes.

=head1 AUTHOR

Sherry L. Winkelman

=head1 VERSION

1.0
