#!/usr/bin/perl

#*****************************************************************************
# get_BPP_prelim.pl
#
# This script gets the final average grades from the regular panels for BPP
# proposals and creates a preliminary grade file to use for entering the
# preliminary grades into the BPP panel.
#
# Note: the final average grade is the normalized grade
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

    open (OUT, ">$param{out}") ||
	die "Sorry can't open $param{out}: $!\n";
    open (OUT2, ">$param{out}.bpp.sql") ||
	die "Sorry can't open $param{out2}: $!\n";
    print OUT2 "delete from bpp_panel_grades;\n\n";

    my $get_props = $dbh1->prepare(qq(select prop_id from 
				       proposal where panel_id = 99));

    my $get_grades = $dbh2->prepare(qq(select panel_id,
		fg_avg,fg_med,fg_stdev,fg_norm,rank
		from proposal where 
		prop_id = ? 
	        and panel_id != 99 and panel_id != 97 
		and prop_status = 'B' 
));

    $get_props->execute();
    my $print;
    my (@tarr,$panel_id,$fg_norm,$fg_avg,$fg_stdev,$fg_med,$rank);

    # for each proposal in the BPP panel
    # get the grades from the proposal table for each panel
    while (my ($prop_id) = $get_props->fetchrow_array) {
	$print = "99\t$prop_id\t";
	$get_grades->execute($prop_id);
        my($ii) = 0;
        while( @tarr = $get_grades->fetchrow_array) {
          for (my $tt=0; $tt<=$#tarr;$tt++) {
            if (!defined $tarr[$tt] || $tarr[$tt] <= 0) {
              $tarr[$tt] = 0;
            }
          }
          ($panel_id,$fg_avg,$fg_med,$fg_stdev,$fg_norm,$rank) = @tarr;
#print STDERR "$panel_id $fg_avg  \n";
          if( $panel_id == 98) {
            if ($fg_avg > 0) {
	      $print .= "$fg_avg\t$fg_avg\t";
            }
          } else {
            $ii++;
            if ($param{test} && $ii > 2 && $panel_id < 90)  {
            } else {
	      $print .= "$fg_norm\t";
            } 
          }

          if ($param{test} && $ii > 2 && $panel_id < 90)  {
          } else {
          print OUT2 qq(insert into bpp_panel_grades(prop_id,panel_id,
		fg_avg,fg_med,fg_stdev,fg_norm,rank) 
		values( $prop_id, $panel_id,
		$fg_avg,$fg_med,$fg_stdev,$fg_norm,$rank);\n);
          }

	}
	$print =~ s/\s+$//;
	$print .= "\n";
	print OUT $print;
    }
    $get_props->finish;

    close OUT;
    close OUT2;
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    out => undef,
            verbose => 0,
            test => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "out=s",
              "verbose",
              "test",
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

get_BPP_prelim.pl [options]

=head1 OPTIONS

B<get_BPP_prelim.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to populate

=item B<-out> filename

output filename

=item B<-help>

displays documentation for B<get_BPP_prelim.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script gets the final average grades from the regular panels for BPP 
proposals and creates a preliminary grade file to use for entering the 
preliminary grades into the BPP panel.

=head1 AUTHOR

Sherry L. Winkelman
