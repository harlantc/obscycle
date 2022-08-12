#!/usr/bin/perl

#*****************************************************************************
# find_grade_status.pl
#
# This script prints out counts of grades vs #proposals
#*****************************************************************************

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION $dbh1);

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
    $dbh1 = DBI->connect($dsn1, "", "");

    $dbh1->do("create table tmpg ( panel_id integer, graded integer)"); 
    $dbh1->do("insert into tmpg (select panel_id,count(proposal.prop_id) from proposal where fg_avg > 0 group  by panel_id)");

    printf STDOUT ("%-5.5s\t%-7.7s\t%s\n","Panel","#Graded","#Props");
    printf STDOUT ("%-5.5s\t%-7.7s\t%s\n","-----","-------","------");

    my $stmt = qq(select proposal.panel_id, graded, count(proposal.prop_id)
	from proposal
          LEFT OUTER JOIN tmpg on proposal.panel_id = tmpg.panel_id
	group by proposal.panel_id,graded
	order by panel_id);

    my $get_props = $dbh1->prepare($stmt);
    $get_props->execute();
    my $print;
    my @row;
    while (@row = $get_props->fetchrow_array) {
        printf STDOUT ("%-5.5s\t  %3d  \t %4d\n",@row);
    }
    $get_props->finish;
    $dbh1->do("drop table tmpg");

    close OUT;
}


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
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

find_grade_status.pl [options]

=head1 OPTIONS

B<find_grade_status.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read


=item B<-help>

displays documentation for B<find_grade_status.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script reads the peer review database and displays info on the panel for number of proposals graded per panel.  Used to track progress of panels.

=head1 AUTHOR

CXCDS
