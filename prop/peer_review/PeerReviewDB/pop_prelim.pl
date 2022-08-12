#!/usr/bin/perl

#******************************************************************************
# pop_prelim.pl
#
# This script creates the sql to populate the preliminary grades for a panel.
#******************************************************************************

use strict;
use Data::Dumper;

use vars qw(%param $VERSION);
$VERSION = '$Id: pop_prelim.pl,v 1.5 2012/02/14 16:04:04 wink Exp $';
{
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
    

    open (IN, "$param{in}") ||
	die "Sorry, can't open $param{in}: $!\n"; 
    open (OUT, ">$param{out}") ||
	die "Sorry, can't open $param{out}: $!\n"; 
    
    foreach my $line (<IN>) {
      chomp $line;	
      if ($line !~ /^#/) {
	my ($panel_id, $prop_id, $g1, $g2, $g3, $g4, $g5, 
	    $g6, $g7, $g8, $g9, $g10,$g11) = split /\t/, $line;
	$panel_id = $param{panel} if $param{panel};
	my ($avg, $stdev, $median) = get_stats($g1, $g2, $g3, $g4, $g5, 
					       $g6, $g7, $g8, $g9, $g10,$g11);
	$stdev = 'null' if $stdev =~ /n/;
	$g1 = 'null' if !$g1;
	$g2 = 'null' if !$g2;
	$g3 = 'null' if !$g3;
	$g4 = 'null' if !$g4;
	$g5 = 'null' if !$g5;
	$g6 = 'null' if !$g6;
	$g7 = 'null' if !$g7;
	$g8 = 'null' if !$g8;
	$g9 = 'null' if !$g9;
	$g10 = 'null' if !$g10;
	$g11 = 'null' if !$g11;

	if ($param{final}) {	
	  print OUT qq(update proposal set fg_avg = $avg, fg_med = $median, fg_stdev = $stdev, g1 = $g1, g2 = $g2, g3 = $g3, g4 = $g4, g5 = $g5, g6 = $g6, g7 = $g7, g8 = $g8, g9 = $g9, g10 = $g10, g11=$g11 where panel_id = $panel_id and prop_id = $prop_id;\n);
	}
	else {
          #if ($panel_id!=99) {
	    print OUT qq(update proposal set pg_avg = $avg, pg_med = $median, pg_stdev = $stdev, pg1 = $g1, pg2 = $g2, pg3 = $g3, pg4 = $g4, pg5 = $g5, pg6 = $g6, pg7 = $g7, pg8 = $g8, pg9 = $g9, pg10 = $g10, pg11=$g11 where panel_id = $panel_id and prop_id = $prop_id;\n);
	  #} else {
	    #print OUT qq(update proposal set pg_avg = $avg, pg_med = $median, pg_stdev = $stdev, pg1 = $g1, pg2 = null, pg3 = $g3, pg4 = $g4, pg5 = $g5, pg6 = $g6, pg7 = $g7, pg8 = $g8, pg9 = $g9, pg10 = $g10 where panel_id = $panel_id and prop_id = $prop_id;\n);
          #}
	}
      }
    }
    close IN;
    close OUT;   
}

sub get_stats {
    my (@grades) = @_;
    my $total = 0.0;
    my $count = 0;
    for (my $i = 0; $i < scalar @grades; $i++) {
	if (defined($grades[$i]) and $grades[$i] ne '') {
	    $count++;
	    $total += $grades[$i];
	}
    }
    my $avg = 0.0;
    $avg = $total /$count if $count > 0;

    # stdev
    # We are dealing with a population, not a sample, so the formula becomes
    #  stdev = sqrt(sum(x^2)/N - mean^2)
    my $sum = 0.0;
    for ( my $i = 0; $i < scalar @grades; $i++) {
	if (defined($grades[$i]) and $grades[$i] ne '') {
	  $sum += $grades[$i] * $grades[$i];
	}
    }
    my $stdev = "n/a";
    if ($count) {
      $stdev = sqrt(($sum / $count) - ($avg * $avg));
    }
    

    # median
    my $median = 0.0;
    @grades = (sort {$b <=> $a} @grades);

    my $center = int($count/2);

    if ($count%2) {
      $median = $grades[$center];
    }
    else {
      $median = ($grades[$center] + $grades[$center-1]) / 2;
    }
    $avg = sprintf "%4.2f", $avg;
    $stdev = sprintf "%4.2f", $stdev if ($stdev !~ /n/);
    $median = sprintf "%4.2f", $median;

    return $avg, $stdev, $median;
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
            in => undef,
            out => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "panel:i",
              "in:s",
              "out:s",
              "final",
              "verbose",
              "version",
              "help"
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

#***************************************************************************
# Subroutine for usage statements
#***************************************************************************
sub usage
{
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

pop_prelim.pl [options]

=head1 OPTIONS

B<pop_prelim.pl> uses long option names.  You can type as few characters 
as
are necessary to match the option name.

=over 4

=item B<-in>

name of input file (required)

=item B<-out>

name of output file (required)

=item B<-final>

flag indicating that the grades are final grades

=item B<-panel>

override the panel in the the file with this panel

=item B<-help>

displays documentation for B<pop_prelim.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the sql to populate the preliminary grades for a panel.

=head1 AUTHOR

Sherry L. Winkelman
