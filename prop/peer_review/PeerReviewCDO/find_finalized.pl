#!/usr/bin/perl

#*****************************************************************************
# find_finalized.pl
#
# This script prints out which panels are currently finalized(locked).
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



    printf STDOUT ("%-5.5s\t%-8.8s\t%s\n","Panel","Locked");
    printf STDOUT ("%-5.5s\t%-8.8s\t%s\n","-----","--------");

    my $stmt = qq(select panel_id ,locked
        from final_comments
        order by panel_id );

    my $get_props = $dbh1->prepare($stmt);
    $get_props->execute();
    my $print;
    my @row;
    while (@row = $get_props->fetchrow_array) {
        printf STDOUT ("%-5.5s\t%-s\n",$row[0],$row[1]);
    }
    $get_props->finish;

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

find_finalized.pl [options]

=head1 OPTIONS

B<find_finalized.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read


=item B<-help>

displays documentation for B<find_finalized.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script reads the peer review database and displays info on the panel and whether it has been locked to indicate that all edits are complete and the panel is in it's finalized state.

=head1 AUTHOR

CXCDS
