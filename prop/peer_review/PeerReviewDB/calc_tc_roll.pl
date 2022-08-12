#!/home/ascds/DS.release/ots/bin/perl

#******************************************************************************
# calc_tc_roll.pl
#
# This script uses the MP script roll2win.pl to calculate a grade for the roll
# constraint.  By default, the script uses the current MP ephemeris.  If this
# does not cover the observing dates for the new cycle, you will need to have 
# MP create and ephemeris for you to use with the b<-ephem> option.
#
# The output from this script is then used by load_roll_coor.pl
#
# Note:  This script used arcops modules to set the Sybase environment.
#******************************************************************************

BEGIN
{
    $ENV{SYBASE} = "/soft/SYBASE16.0";
}


use strict;                      
use DBI;
use vars qw($password %param $VERSION);

$VERSION = '$Id: calc_tc_roll.pl,v 1.8 2012/02/14 14:28:30 wink Exp $';
#'

# Get the flags/values from the commandline and store in a hash
use Getopt::Long;           
parse_opts();

# Handle some of the commandline flags
if ($param{version}) {
  print $VERSION, "\n";
  exit( 0 );
}
    
if ($param{help}) { usage(0);}

# get the password    
use Term::ReadKey;
ReadMode 'noecho';
print "Enter password for $param{U} on $param{S}: ";
$password = ReadLine( 0 );
chomp $password;
ReadMode 'restore';
print "\n";



# Make connection to $param{S}
my $dsn1 = "DBI:Sybase:server=$param{S};database=proposal";
my $dbh1 = DBI->connect($dsn1, $param{U}, $password, {
						   PrintError =>0,
						   RaiseError => 1});
my $ao_str = $param{cycle};
$ao_str = "0".$param{cycle} if $param{cycle} < 10;
my $start = $param{start};

my $get_args = $dbh1->prepare(qq(select distinct t.targid, ra, dec, ordr, 
                                 roll, roll_tolerance, roll_180 
                                 from proposal p, target t, rollreq r
                                 where p.status="PROPOSED" and
				 p.proposal_id = t.proposal_id and 
                                 t.targid = r.targid 
				 and ao_str = '$ao_str' 
                                 order by t.targid
				 ));


open(OUT, ">$param{output}") || die "Can't open $param{output}: $!\n";
open(COMPLETE, ">$param{complete}") || die "Can't open $param{complete}: $!\n";

print COMPLETE "targ_id\tordr\tgrade\n";
print COMPLETE "ra\tdec\troll\troll_tolerance\troll_180\n";
print COMPLETE "mp_roll2win command\n";
print COMPLETE "--------------------------------------------------------------------------\n\n";

$get_args->execute();
while (my ($targ_id, $ra, $dec, $ordr, $roll, $roll_tol, 
	   $roll_180) = $get_args->fetchrow_array) {
  
  # run the MP script
  my $output = `mp_roll2win $start 365 $ra $dec $roll $roll_tol $roll_180` ;
  print "\n$output";
  # get the answer from the output
  my $grade;
  if ($output =~ /Total time satisfying constraint: (\d+\.*\d*) days/){
    $grade = $1;
  }
  
  # write results to output file
  print "$targ_id\t$ordr\t$grade\n";
  print OUT "$targ_id\t$ordr\t$grade\n";
  print COMPLETE "$targ_id\t$ordr\t$grade\n";
  print COMPLETE "$ra\t$dec\t$roll\t$roll_tol\t$roll_180\n";
  print COMPLETE "mp_roll2win $start 365 $ra $dec $roll $roll_tol $roll_180\n\n";
}

close(OUT);
close(COMPLETE);


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
            U => undef,
            S => "sqlocc",
            output => undef,
	    cycle => undef,
	    start => undef,
	    complete => undef,
            verbose => 0
           );

  GetOptions( \%param,
              "U=s",
              "S=s",
              "output=s",
	      "cycle=s",
	      "start=s",
	      "complete=s",
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

calc_tc_roll.pl [options]

=head1 OPTIONS

B<calc_tc_roll.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name on sqlocc

=item B<-output>

Name of the output file for load_roll_coor.pl

=item B<-complete>

Name of the file that contains the output plus the MP script inputs and the 
actual command running the MP script.  (The default is to use the current MP
ephemeris.)

=item B<-cycle>

Cycle to use (i.e. 8, 9, etc.)

=item B<-start>

Start date (Julian date) for beginning observing the AO (for cycle 10, that was Jan 1, 2009 = 2454832.50000)

=item B<-help>

displays documentation for B<calc_tc_roll.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script uses the MP script roll2win.pl to calculate a grade for the roll 
constraint.  By default, the script uses the current MP ephemeris.  If this 
does not cover the observing dates for the new cycle, you will need to have MP
create and ephemeris for you to use with the b<-ephem> option.

=head1 AUTHOR

Sarah Blecksmith
