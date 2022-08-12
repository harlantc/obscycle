#!/usr/bin/perl

#*****************************************************************************
# find_ecliptic.pl
#
# This script prints out approved targets with ecliptic latitude <-55  or > 55
# depends on prop_precess_exe
#*****************************************************************************

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION $dbh1);

{
    my $str;
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


    open OFILE,"> $param{out}" or 
	die "Unable to open $param{out} for writing.\n";

    printf OFILE "%3.3s, %8.8s, %-5.5s, %-5.5s, %3.3s, \"%-30.30s\", %-7.7s, %-7.7s, %-9.9s, %-9.9s, %-10.10s, %-10.10s\n",
	"Pnl","Proposal","PStat","TStat","Tgt","TargName","ReqTime","AppTime","  RA","  Dec","Ec Lon","Ec Lat";

    my $stmt = qq(select proposal.panel_id,proposal.prop_id,prop_status,targ_status,
	targ_num,targ_name,req_time,app_time,ra,dec,ec_lon,ec_lat
        from proposal , target where proposal.prop_id=target.prop_id
	and proposal.panel_id = target.panel_id
	and prop_status not in ('B')
	and (ec_lat <= $param{min} or ec_lat >= $param{max})
        order by proposal.panel_id,prop_id,targ_num );

    my $get_props = $dbh1->prepare($stmt);
    $get_props->execute();
 
    my (@row,$str,@arr);
    my $reqtot = 0;
    my $apptot = 0;
    while (@row = $get_props->fetchrow_array) {
       $reqtot += $row[6];
       $apptot += $row[7] if ($row[2] =~ /Y|G/ && $row[3] =~/Y/) ;
       printf OFILE "%3.3s, %08.8d, %-5.5s, %-5.5s, %3.3d, \"%-30.30s\", %7.2f, %7.2f, %9.4f, %9.4f, %10.6f, %10.6f \n",@row;
    }
    $get_props->finish;

    printf OFILE ("%3.3s, %8.8s, %-5.5s, %-5.5s, %3.3s,  %-30.30s , %7.2f, %7.2f, %-9.9s, %-9.9s, %-10.10s, %-10.10s\n",
                 "","Totals"," "," "," "," ",$reqtot,$apptot, " ", " ", " ", " ");
    close OFILE;
  
    chmod(0660,$param{out});
}


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    out => undef,
	    latitude => 55,
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "out=s",
	      "latitude=f",
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
  $param{max} = abs($param{latitude});
  $param{min} = $param{max} * -1;

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

find_ecliptic.pl [options]

=head1 OPTIONS

B<find_ecliptic.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read

=item B<-out>

name of output file.  Since output is csv, file should have .csv extension

=item B<-latitude>

latitude range for search.  Default is 55

=item B<-help>

displays documentation for B<find_ecliptic.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script reads the panel data from the peer review database and
writes comma separated format.  All Y,G targets of Y,G proposals with 
ecliptic latitude < -55 or > 55. 


=head1 AUTHOR

CXCDS
