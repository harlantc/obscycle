#!/usr/bin/perl

#*****************************************************************************
# gen_grades.pl
#
# This script generates random grades and the sql to put the grades (final or
# preliminary) for a panel into the database.
#******************************************************************************

use strict;
#use Data::Dumper;
#use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: gen_grades.pl,v 1.4 2020/03/05 ASCDS Exp $';

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
  
  # Database connection 1
  #my $dsn1 = "dbi:Pg:dbname=$param{database}";
  #my $dbh1 = DBI->connect($dsn1, "", "");
  
  # Prepared sql statements
  my @theprops=();
  #if (!$param{list}) {
    #my $get_props = $dbh1->prepare(qq(select prop_id from proposal where 
                                    #panel_id = ?));
    #$get_props->execute($param{panel});
    #while (my ($prop_id) = $get_props->fetchrow_array) {
        #push(@theprops,$prop_id);
    #}
  #} else
  if (-e $param{list}) {
    open(PLIST,"< $param{list}") or die "Unable to open $param{list} for reading.\n"; 
    my($str);
    while ($str = <PLIST>) {
       chomp($str);
       $str =~ s/[\/a-zA-z]//g;
       while ($str =~ s/ //g) {;}
       if ($str !~ /#/ && length($str) > 4) {
         push(@theprops,$str);
       }
    }
    close PLIST;
  }

  open( OUT, ">$param{out}") ||
    die "Sorry, can't open $param{out}: $!\n";
  open( OUTI, ">$param{out}.web") ||
    die "Sorry, can't open $param{out}: $!\n";

  my($prop_id);
  foreach $prop_id (@theprops) {
    my @grades = gen_grades();
    my ($avg, $stdev, $median) = get_stats(@grades);

    if ($param{type} eq 'f') {
      #print OUT qq(update proposal set fg_avg = $avg, fg_stdev = $stdev, fg_med = $median, g1 = $grades[0], g2 = $grades[1], g3 = $grades[2], g4 = $grades[3], g5 = $grades[4], g6 = $grades[5], g7 = $grades[6], g8 = $grades[7]  where panel_id = $param{panel} and prop_id = $prop_id;\n);
      print OUT qq(update proposal set fg_avg = $avg, fg_stdev = $stdev, fg_med = $median);
      for (my $gg=0;$gg<=$#grades;$gg++) {
        my $col = "g" . ($gg+1);
        print OUT  ",$col = $grades[$gg] ";
      }
      print OUT " where panel_id = $param{panel} and prop_id = $prop_id;\n";
      print OUTI "$param{panel}\t$prop_id\t";
      for (my $ti=0;$ti<=$#grades;$ti++) {
         print OUTI "$grades[$ti]\t";
      }
      print OUTI "\n";
    }
    else {

      print OUT qq(update proposal set pg_avg = $avg, pg_stdev = $stdev, pg_med = $median);
      for (my $gg=0;$gg<=$#grades;$gg++) {
        my $col = "pg" . ($gg+1);
        print OUT  ",$col = $grades[$gg] ";
      }
      print OUT " where panel_id = $param{panel} and prop_id = $prop_id;\n";
      print OUTI "$param{panel}\t$prop_id\t";
      for (my $ti=0;$ti<=$#grades;$ti++) {
         print OUTI "$grades[$ti]\t";
      }
      print OUTI "\n";
    }
  }


  close OUT;
  close OUTI;
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
    my $stdev = "NULL";
    if ($count) {
      if (($sum / $count) - ($avg * $avg) > 0) {
	$stdev = sqrt(($sum / $count) - ($avg * $avg));
      }
      else {
	my $sum2 = ($sum / $count);
	$sum2 = printf "%4.2f", $sum2;
      }
    }
    
    # median
    my $median = 0.0;
    @grades = (sort {$a <=> $b} @grades);
    my $center = int($count/2);
    
    if ($count%2) {
      $median = $grades[$center];
    }
    else {
      $median = ($grades[$center] + $grades[$center-1]) / 2;
    }

    $avg = sprintf "%4.2f", $avg if defined($avg);
    $stdev = sprintf "%4.2f", $stdev if ($stdev !~ /n/i);
    $median = sprintf "%4.2f", $median if defined($median);
    return $avg, $stdev, $median;
}

sub gen_grades {
  my @grades;
  my $ngrades = int(rand(6));
  $ngrades += 3;
  for (my $i = 1; $i < $ngrades; $i++) {
    my $grade = int(rand(4)) + 1 + rand;
    push @grades, sprintf "%3.2f", $grade;
  }

  return @grades;
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts {
  
  %param = (
	    out => undef,
	    type => 'f',
	    panel => undef,
	    verbose => 0
	   );
  
  GetOptions( \%param,
	      "type=s",
	      "out=s",
	      "database=s",
	      "panel=i",
	      "list=s",
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
  if (!$param{database} and !$param{list} ) {
    warn("must enter -database or -list\n");
    $err++;
  }

  if ($param{type} ne 'f' and $param{type} ne 'p') {
    warn("type must be f or p\n");
    $err++;
  }
  
  exit(1) if $err;
  
}

#******************************************************************************
# Subroutine for usage statements
#******************************************************************************
sub usage {
  my ( $exit ) = @_;
  
  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

gen_grades.pl [options]

=head1 OPTIONS

B<gen_grades.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B <-out>

name of output file

=item B <-database>

database to get panel information from

=item B<-panel>

panel number you wish to generate grades for

=item B<-type>

type of grades to generate (p or f)

=item B<-list>

input file containing proposal ids

=item B<-help>

displays documentation for B<gen_grades.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script generates the sql to put grades (final or prelimnary) for a panel
into the database.  It now generates a random number of grades for each proposal.

=head1 AUTHOR

Sherry L. Winkelman
