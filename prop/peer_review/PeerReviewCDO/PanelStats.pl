#!/usr/bin/perl
#
# PanelStats.pl
#
# Purpose: Dump panel statistics with running totals for specific groups of 
#          proposals
#
# Copyright (c) 2006 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
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

    if ($param{version})
    {
        print $VERSION, "\n";
        exit( 0 );
    }

    if ($param{help})
    {
        usage(0);
    }

    my ($sec, $min, $hr, $day, $month, $year) = (localtime)[0, 1, 2, 3, 4, 5];
    $year += 1900;
    $month++;
    $month = sprintf("%02d", $month);
    $day = sprintf("%02d", $day);
    $sec = sprintf("%02d", $sec);
    $min = sprintf("%02d", $min);
    $hr = sprintf("%02d", $hr);
    my $date = "$year$month${day}_$hr$min$sec";

    # Database connection 1
    my $dsn1 = "dbi:Pg:dbname=$param{U}";
    my $dbh = DBI->connect($dsn1, "", "", {
        PrintError => 0,
        RaiseError => 1});

    my $get_go_app_rank = $dbh->prepare(qq(select panel_id ,max(rank) 
		from proposal where prop_status='Y' 
		and type  in('GO','TOO')
		group by panel_id));
    my $get_arcthe_count = $dbh->prepare(qq(select panel_id ,type,count(*) 
		from proposal where (type='ARCHIVE' or type='THEORY')
		group by panel_id,type));
    my $get_archive_ranks = $dbh->prepare(qq(select panel_id,  prop_id
                from proposal
                where type = 'ARCHIVE' and fg_norm > 3.4
                order by panel_id,rank ASC,fg_norm DESC));
    my $get_theory_ranks = $dbh->prepare(qq(select panel_id,  prop_id
                from proposal
                where type = 'THEORY' and fg_norm > 3.4
                order by panel_id,rank ASC,fg_norm DESC));

    my $get_archive = $dbh->prepare(qq(select panel_id, last_name, prop_id, 
                                       prop_status, fg_norm, total_app_time ,
					rank
                                       from proposal
                                       where type = 'ARCHIVE' and fg_norm > 3.4
                                       order by fg_norm DESC,rank));
    my $get_theory = $dbh->prepare(qq(select panel_id, last_name, prop_id, 
                                      prop_status, fg_norm, total_app_time ,
					rank
                                      from proposal
                                      where type = 'THEORY' and fg_norm > 3.4
                                      order by fg_norm DESC,rank));
    my $get_joint = $dbh->prepare(qq(select panel_id, last_name, prop_id,
                   prop_status, fg_norm, hst_app, xmm_app, 
                   noao_app,nrao_app,spitzer_app,
                   swift_app,nustar_app from proposal 
                   where (hst_req > 0 or xmm_req > 0 or 
                   nrao_req > 0 or noao_req > 0 or 
                   spitzer_req > 0  or
                   swift_req > 0 or nustar_req > 0)
		   and prop_status != 'B' and fg_norm > 3.4
                   order by fg_norm DESC));
    my $get_tc = $dbh->prepare(qq(select panel_id, last_name, prop_id,
                   prop_status, fg_norm, rc_score_app, total_app_hel,
                   vf_app, f_app, s_app from proposal 
                   where prop_status != 'B' and fg_norm > 3.4
                   and tc_prop = 'Y' or type = 'TOO'
                   order by fg_norm DESC));
    my $get_time = $dbh->prepare(qq(select last_name, prop_id, 
                   prop_status, type, big_proj, fg_norm, 
                   prop_app_time, cdo_approval from proposal
                   where type not in ('ARCHIVE', 'THEORY') 
                   and prop_status != 'B' and fg_norm > 3.0
                   and panel_id = ?
                   order by fg_norm DESC));
    # get totals for archive, theory files
    my(%pan_app_rank) = ();
    my(%pan_arc) = ();
    my(%pan_the) = ();
    $get_go_app_rank->execute();
    while (my($panel_id,$rank) = $get_go_app_rank->fetchrow_array) {
         $pan_app_rank{$panel_id} = $rank;
    }
    $get_arcthe_count->execute();
    while (my($panel_id,$type,$cnt) = $get_arcthe_count->fetchrow_array) {
      if ($type =~ /arc/i) {
        $pan_arc{$panel_id} = $cnt;
      } else {
        $pan_the{$panel_id} = $cnt;
      }
    }
    $get_arcthe_count->finish;

    # Archive
    open(OUT, ">${date}_archive.txt") || 
      die "Sorry, can't open ${date}_archive.txt: $!\n";
    binmode(OUT, ":utf8");
    print OUT "pan\tprop_id\tstatus\trank\tfg_norm\tmoney\ttotal\tpi\tArchive\n";

    my(%prop_ranks) = ();
    $get_archive_ranks->execute();
    my($ii);
    my($oldpan) = 0;
    while (my($panel_id, $prop_id) = $get_archive_ranks->fetchrow_array) {
       if ($panel_id != $oldpan) {
         $ii = 1;
         $oldpan = $panel_id;
       }
       $prop_ranks{$prop_id} = $ii++;
    }
    $get_archive_ranks->finish;

    my $arc_total = 0;
    $get_archive->execute();
    while (my($panel_id, $pi, $prop_id, 
	      $prop_status, $fg_norm, 
	      $money,$rank) = $get_archive->fetchrow_array) {
      $arc_total += $money if $prop_status =~ /Y/;
      print OUT "$panel_id\t$prop_id\t$prop_status\t$rank \\ $pan_app_rank{$panel_id}\t$fg_norm\t$money\t$arc_total\t$pi\t";
      print OUT "$prop_ranks{$prop_id}" . "\\" . "$pan_arc{$panel_id}\n";
    }
    $get_archive->finish;
    close OUT;

    # Theory
    open(OUT, ">${date}_theory.txt") || 
      die "Sorry, can't open ${date}_theory.txt: $!\n";
    binmode(OUT, ":utf8");
    print OUT "pan\tprop_id\tstatus\trank\tfg_norm\tmoney\ttotal\tpi\tTheory\n";

    %prop_ranks = ();
    $get_theory_ranks->execute();
    my($ii);
    my($oldpan) = 0;
    while (my($panel_id, $prop_id) = $get_theory_ranks->fetchrow_array) {
       if ($panel_id != $oldpan) {
         $ii = 1;
         $oldpan = $panel_id;
       }
       $prop_ranks{$prop_id} = $ii++;
    }
    $get_theory_ranks->finish;

    my $the_total = 0;
    $get_theory->execute();
    while (my($panel_id, $pi, $prop_id, 
	      $prop_status, $fg_norm, 
	      $money,$rank) = $get_theory->fetchrow_array) {
      $the_total += $money if $prop_status =~ /Y/;
      print OUT "$panel_id\t$prop_id\t$prop_status\t$rank \\ $pan_app_rank{$panel_id}\t$fg_norm\t$money\t$the_total\t$pi\t";
      print OUT "$prop_ranks{$prop_id}" . "\\" . "$pan_the{$panel_id}\n";
    }
    $get_theory->finish;
    close OUT;

    # Joint
    open(OUT, ">${date}_joint.txt") || 
      die "Sorry, can't open ${date}_joint.txt: $!\n";
    binmode(OUT, ":utf8");
    print OUT "pan\tprop_id\tstatus\tfg_norm\thst\thst_total\txmm\txmm_total\tnoao\tnoao_total\tnrao\tnrao_total\tspitzer\tspitzer_total\tswift\tswift_total\tnustar\tnustar_total\tpi\n";
    my $hst_total = 0;
    my $xmm_total = 0;
    my $noao_total = 0;
    my $nrao_total = 0;
    my $spitzer_total = 0;
    my $swift_total = 0;
    my $nustar_total = 0;
    $get_joint->execute();
    while (my($panel_id, $pi, $prop_id, 
	      $prop_status, $fg_norm, 
	      $hst, $xmm, $noao,$nrao,$spitzer,$swift,$nustar) = $get_joint->fetchrow_array) {
      $hst_total += $hst if $prop_status =~ /Y/;
      $xmm_total += $xmm if $prop_status =~ /Y/;
      $noao_total += $noao if $prop_status =~ /Y/;
      $nrao_total += $nrao if $prop_status =~ /Y/;
      $spitzer_total += $spitzer if $prop_status =~ /Y/;
      $swift_total += $swift if $prop_status =~ /Y/;
      $nustar_total += $nustar if $prop_status =~ /Y/;
      print OUT "$panel_id\t$prop_id\t$prop_status\t$fg_norm\t$hst\t$hst_total\t$xmm\t$xmm_total\t$noao\t$noao_total\t$nrao\t$nrao_total\t$spitzer\t$spitzer_total\t$swift\t$swift_total\t$nustar\t$nustar_total\t$pi\n";
    }
    $get_joint->finish;
    close OUT;

    # RC and HEL
    open(OUT, ">${date}_rc_hel.txt") ||
      die "Sorry, can't open ${date}_rc_hel.txt: $!\n";
    binmode(OUT, ":utf8");
    print OUT "pan\tprop_id\tstatus\tfg_norm\trc_app\trc_total\tHEL_app\tHEL_total\tvf_app\tvf_total\tf_app\tf_total\ts_app\ts_total\tpi\n";
    my $rc_total = 0;
    my $app_hel_totals = 0;
    my $vf_total = 0;
    my $f_total = 0;
    my $s_total = 0;
    $get_tc->execute();
    while (my($panel_id, $pi, $prop_id, 
	      $prop_status, $fg_norm,
	      $rc_score, $total_app_hel,
	      $vf, $f, $s) = $get_tc->fetchrow_array) {
      $rc_total += $rc_score if $prop_status =~ /Y/;
      $app_hel_totals += $total_app_hel if $prop_status =~ /Y/;
      $vf_total += $vf if $prop_status =~ /Y/;
      $f_total += $f if $prop_status =~ /Y/;
      $s_total += $s if $prop_status =~ /Y/;
      print OUT "$panel_id\t$prop_id\t$prop_status\t$fg_norm\t$rc_score\t$rc_total\t$total_app_hel\t$app_hel_totals\t$vf\t$vf_total\t$f\t$f_total\t$s\t$s_total\t$pi\n";
    }
    $get_tc->finish;
    close OUT;

    # Time
    open(OUT, ">${date}_time.txt") || 
      die "Sorry, can't open ${date}_time.txt: $!\n";
    binmode(OUT, ":utf8");

      
    my $get_panels = $dbh->prepare(qq(select distinct panel_id from proposal
        where panel_id != 98 order by panel_id));
    $get_panels->execute();
    while (my($panel) = $get_panels->fetchrow_array) {
      print OUT "panel\tprop_id\tstatus\ttype\tBPP\tfg_norm\ttime\ttotal\tcdo\tpi\n";
      my $time_total = 0;
      $get_time->execute($panel);
      while (my($pi, $prop_id, 
		$prop_status, $type, $big_proj, $fg_norm, 
		$time, $cdo_app) = $get_time->fetchrow_array) {
	$time_total += $time if $prop_status =~ /Y/;
      print OUT "$panel\t$prop_id\t$prop_status\t$type\t$big_proj\t$fg_norm\t$time\t$time_total\t$cdo_app\t$pi\n";
      }
      $get_time->finish;
      print OUT "\n\n"
    }
    $get_panels->finish;
    close OUT;
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
sub parse_opts
{

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
    while ( my ( $par, $val ) = each ( %param ) )
    {
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
sub usage
{
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}


__END__

=head1 NAME

PanelStats.pl - Script for printing TOO and time tallies correctly.

=head1 USAGE

PanelStats.pl [options]

=head1 DESCRIPTION

This is a script to print running totals for TOOs, RC, HEL, time, and joint time.
First run with the
-list option to get a listing of sorts available for calculating the running
totals.  Then run the script again using the -sort option.  Redirect the 
output to a file to capture for printing.  There is a -hide_triage option 
(default is Y) so you can hide/display triaged proposals.

=head1 OPTIONS

B<PanelStats.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U> <user name>

peer review user name (required)

=item B<-panel>

panel number you wish to load (required)

=item B<-hide_triage>

flag to hide triaged proposals (default Y)

=item B<-list>

flag to list the sorts available to the panel

=item B<-sort>

sort number to apply to running total columns

=item B<-help>

displays documentation for B<PanelStats.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 Examples

=head2 1. Get list of available sorts

    PanelStats.pl -U pan1 -panel 1 -list

=head2 2. Print (to STDOUT) tallies with sort 1 (triaged proposals hidden)

    PanelStats.pl -U pan1 -panel 1 -sort 1

=head2 3. Print (to file.txt) tallies with sort 1 (triaged proposals hidden)

    PanelStats.pl -U pan1 -panel 1 -sort 1 > file.txt

=head2 4. Print (to STDOUT) tallies with sort 1 (triaged proposals displayed)

    PanelStats.pl -U pan1 -panel 1 -sort 1 -hide_triage N

=head2 5. Print (to file.txt) tallies with sort 1 (triaged proposals displayed)

    PanelStats.pl -U pan1 -panel 1 -sort 1 -hide_triage N > file.txt

=head1 Printing Notes

To create a postscript version of the output that fits across a landscape 
paper without wrapping lines, use enscript.  The following will usually work:

  enscript -rG -fCourier8 -ofile.ps file.txt

You can change the font size to your tastes.

=head1 AUTHOR

Sherry L. Winkelman

=head1 VERSION

1.0
