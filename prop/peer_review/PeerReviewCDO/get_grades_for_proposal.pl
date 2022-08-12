#!/usr/bin/perl

#*****************************************************************************
# get_grades_for_proposal.pl
#
# This script gets the final grades from the specified panel for a
# proposal
#
#*****************************************************************************

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: get_BPP_prelim.pl,v 1.2 2012/02/14 15:46:03 wink Exp $';

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
    my $dsn1 = "dbi:Pg:dbname=$param{U}";
    my $dbh1 = DBI->connect($dsn1, "", "");

    # Database connection 2
    my $dsn2 = "dbi:Pg:dbname=$param{U}";
    my $dbh2 = DBI->connect($dsn2, "", "");

    my $get_grades = $dbh1->prepare(qq(select panel_id,prop_id,g1,g2,g3,g4,g5
		g6,g7,g8,g9,g10,g11,g12,g13,g14,g15,g16,g17,g18,g19,g20,g21,g22,
		g23,g24,g25,fg_avg,fg_med,fg_stdev 
		from proposal where prop_id= ? 
		order by panel_id));

    $get_grades->execute($param{p});
    my (@tarr);
    $get_grades->execute($param{p});
    while( @tarr = $get_grades->fetchrow_array) {
       printf STDOUT "%2d ",$tarr[0];
       for (my $tt=1; $tt<=$#tarr;$tt++) {
          if ($tarr[$tt]) {
            print STDOUT "$tarr[$tt]  ";
          }
          if ($tt==25) { print STDOUT "    "; }
       }
       print STDOUT "\n"

    }
    $get_grades->finish;

}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    p => undef,
           );

  GetOptions( \%param,
	      "U=s",
              "p=s",
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

#******************************************************************************
# Subroutine for usage statements
#******************************************************************************
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

get_grades_for_proposal.pl [options]

=head1 OPTIONS

B<get_grades_for_proosal.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to populate

=item B<-p> proposal number

proposal number

=item B<-help>

displays documentation for B<get_BPP_prelim.pl>

=back

=head1 DESCRIPTION

This script gets the current final grades for the specfied proposal

=head1 AUTHOR


