#!/home/ascds/DS.release/ots/bin/perl

#*****************************************************************************
# calc_ecliptic.pl
#
# This script runs prop_precess_exe to get the ecliptic latitude for each 
# target, determine if the source is at a high ecliptic latitude, and create
# the sql to update the corresponding PR database fields.
#*****************************************************************************
BEGIN
{
   $ENV{SYBASE} = "/soft/SYBASE16.0";
}

use strict;
use Data::Dumper;
use DBI;

use vars qw(%param $dbh1 $VERSION);

$VERSION = 'calc_ecliptic.p, v 1.0 2020/03/03 ASCDS $';


{
    my $str;
    use Getopt::Long;
    my($pwd) = parse_opts();
    
    if ($param{help})
    {
        usage(0);
    }


   open(PFILE,"which prop_precess_exe 2>&1 |");
   while ($str = <PFILE>) {
     if ($str =~ /no prop_precess/ ) {
       print STDERR "Error: $str\n";
       print STDERR "This script must be run on a machine with access to the release.\n";
       exit(1);
     }
   }

   my $propserver = "server=$param{S}";
   my $propdb = "database=proposal";
   my $script_name = "scriptName=calc_ecliptic.pl";
   my $type = length $param{type} ? "$param{type}%": "%";

   # Database connection 1
   my $dsnProp1 = "DBI:Sybase:$propserver;$script_name;$propdb";
   $dbh1 = DBI->connect($dsnProp1, "$param{U}", $pwd, {
	PrintError => 0,
	RaiseError => 1});
  
   open OFILE,"> $param{out}" or 
	die "Unable to open $param{out} for writing.\n";

  if ($type ne "%"){
    print OFILE "ec_lat, targ_id, proposal_number\n";
  }
    # Ouput target list
    if ( $type eq "%" ) {
    open OFILE1,"> HEL_targids.list" or
        die "Unable to open HEL_targids.list for writing.\n";
    }
   my $stmt = qq(select proposal_number,targid,ra,dec
        from proposal , target where proposal.ao_str = "$param{ao}"
	and proposal.proposal_id = target.proposal_id
	and ra is not null
  and type like "${type}"
	and proposal.status not in ("INCOMPLETE","WITHDRAWN")
        order by proposal_number,targid );

    my $get_props = $dbh1->prepare($stmt);
    $get_props->execute();
 
    my (@row,$str,@arr);
    while (@row = $get_props->fetchrow_array) {

      my $cmd = "prop_precess_exe FROM J2000/DEG TO EC2000/DEG CONVERT " . $row[2] . " " .  $row[3] . " GO QUIT";
      #print STDERR "$cmd\n";
      open PFILE,"$cmd | ";
      while ($str = <PFILE>) {
         if ($str =~ /Eclip/i) {
            chomp($str);
            @arr = split(' ',$str);
            #print "$str\n";
            my $ec_lat = $arr[$#arr];
            my $hel = "N";
            if ($ec_lat <= $param{min} or $ec_lat >= $param{max}){
              $hel = "Y";
              print OFILE1 qq($row[1]\n) unless $type ne "%";
            }
            if ($type ne "%") {
              print OFILE "$ec_lat, $row[1], $row[0]\n";
            }
            else {
              print OFILE qq(update target set ec_lon=$arr[$#arr-1], ec_lat=$ec_lat, at_hel='$hel' where targ_id = $row[1] and prop_id= $row[0];\n);
            }
         }
      }
    }
    $get_props->finish;

    close OFILE;
    close OFILE1 unless $type ne "%";
    chmod(0660,$param{out});
}


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{
  my $pwd;

  %param = (
	    U => undef,
	    S => undef,
            ao => undef,
      latitude => 55,
	    out => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "S=s",
              "ao=s",
        "type:s",
        "latitude=f",
	      "out=s",
              "verbose",
              "help"
            ) or exit(1);

  return if $param{help} ;


  my $err = 0;
  while ( my ( $par, $val ) = each ( %param ) )
  {
    next if defined $val;
    warn("parameter `$par' not set\n");
    $err++;
  }

  exit(1) if $err;
  $pwd = get_password($param{U},$param{S});
  $param{max} = abs($param{latitude});
  $param{min} = $param{max} * -1;

  return $pwd;
}

sub get_password
{
  my($usr,$srvr) = @_;
  my($pwd);

  use Term::ReadKey;
  {
       ReadMode 'noecho';
       print "Enter password for $usr on $srvr: ";
       $pwd = ReadLine( 0 );
       chomp $pwd;
       ReadMode 'restore';
       print "\n";
  }

  return $pwd;
}


#******************************************************************************
# Subroutine for usage statements
#******************************************************************************
sub usage
{
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0, *STDOUT );
  exit $exit;
}

__END__

=head1 USAGE

calc_ecliptic.pl [options]

=head1 OPTIONS

B<calc_ecliptic.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U> (user)

username for logging into the sql server for the proposal database

=item B<-S> (server)

name of sql server for the proposal database

=item B<-ao> (ao string)

cycle for retrieving proposals

=item B<-type> (proposal type)

Optional type of proposal to limit query for (e.g. GTO). Will match using
type% wildcard. If type not supplied, returns values for all proposal types. 
If type is supplied, it's likely that the use-case is for post peer review
work (e.g. GTOs), so output to csv rather than sql.

=item B<-latitude>

latitude range for search.  Default is 55

=item B<-out> (output file)

name of output file for postgres sql statements.

=item B<-help>

displays documentation for B<calc_ecliptic.pl>

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script retrieves the targets going to the peer review for the given cycle
and creates the sql needed to populate the ec_lon,ec_lat fields. It also 
determines if the source is a high ecliptic latitude source (defined by the
latitude parameter, which defaults to -55 <= hel <= 55) and includes the sql
to populate the hel_source filed. A file with a list of targids for HEL sources
is output (if the type param is not supplied) and is intended for use with
panel_slew.pl to allot HEL time for panels. If the type argument is supplied
a csv file containing ec_lat, targ_id, proposal_number is output instead of the
sql file. This script uses prop_precess_exe from the CXC release to convert
coordinates, so it must be in your PATH.

=head1 AUTHOR

CXCDS
