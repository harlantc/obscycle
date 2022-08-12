#!/home/ascds/DS.release/ots/bin/perl
#
#******************************************************************************
# set_mcop_monitor
#
#
# Note: This script uses Arcops perl modules to set the Sybase environment
#******************************************************************************

BEGIN
{
   $ENV{SYBASE} = "/soft/SYBASE16.0";
}

use strict;
use DBI;
use Getopt::Long;
use vars qw($pwdProp %param $VERSION);
$VERSION = '$Id: set_mcop_monitor.pl,v 1.59 2012/02/14 14:10:22 wink Exp $';

{

  my(@row,$str);
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
  
  
  my $propserver = "server=$param{S}";
  my $propdb = "database=proposal";
  my $script_name = "scriptName=set_mcop_monitor.pl";
  $pwdProp=$param{q};
  
  # Database connection 1 (Proposal)
  my $dsnProp1 = "DBI:Sybase:$propserver;$script_name;$propdb";
  my $dbh = DBI->connect($dsnProp1, "$param{U}", 
			      $pwdProp, {
					 PrintError => 0,
					 RaiseError => 1});

  open (OFILE,"> $param{out}" ) or 
	die "Unable to open $param{out} for writing.\n";
  # any mcop 
  my $sql = "select proposal_number,target.targid,
	prop_exposure_time,
	est_time_cycle_n1,
	est_time_cycle_n2
        from proposal,target
	where  proposal.proposal_id = target.proposal_id
	and ao_str = '$param{ao}'
	and multi_cycle='Y'
	and (est_time_cycle_n1 >0 or est_time_cycle_n2 > 0)
	and (
	targid in (select distinct targid from timereq) or
	targid in (select distinct targid from rollreq) or
	targid in (select distinct targid from phasereq) )
	order by proposal_number,target.targid ";
  print STDERR "$sql\n";
  my $sth = $dbh->prepare($sql);
  $sth->execute();
  while (@row = $sth->fetchrow_array) {
      $str = join(',',@row);
      printf OFILE "-- $str\n";
      print STDERR "$str\n";
   }
   $sth->finish;
   print OFILE "-- -------------------------------------\n";

    
  my $sql = "select proposal_number,target.targid,
	prop_exposure_time,time,
	est_time_cycle_n1,
	est_time_cycle_n2,
        ordr,obs_time from proposal,target,too,observation
	where  proposal.proposal_id = target.proposal_id
	and target.targid *= too.targid
	and target.targid = observation.targid
	and ao_str = '$param{ao}'
	and multi_cycle='Y'
	and (est_time_cycle_n1 >0 or est_time_cycle_n2 > 0)
	order by target.targid,ordr desc";
  print STDERR "$sql\n";

  my(@row,$targid,$num1,$num2,$num0,$est_time_0,$est_time_1,$est_time_2);
  my $sth = $dbh->prepare($sql);
  $sth->execute();
  $targid = 0; 
  while (@row = $sth->fetchrow_array) {
     if ($targid  != $row[1]) {
        if ($targid > 0) {
          print OFILE qq(update target set 
		num_obs_req=$num0,num_obs_app=$num0,
		num_obs_req_1=$num1,num_obs_app_1=$num1,
		num_obs_req_2=$num2,num_obs_app_2=$num2
		where targ_id = $targid;\n\n);
        }
        $est_time_0 = $row[2] - $row[4] - $row[5];
        $est_time_1 = $row[4];
        $est_time_2 = $row[5];
	$num0 = 0;
	$num1 = 0;
	$num2 = 0;
        $targid = $row[1];
        if ($row[3] > 0) { $num0=1; }
     }
     if ($row[3] > 0) {
        $row[6] += 1;
     }
     if ($est_time_2 > 0) {
        $est_time_2 -= $row[7];
        print OFILE qq(update too set ao=2 where prop_id=$row[0]
	and targ_id=$row[1] and ordr=$row[6];\n);
        $num2++;
     }
     elsif ($est_time_1 > 0) {
        $est_time_1 -= $row[7];
        print OFILE qq(update too set ao=1 where prop_id=$row[0]
	and targ_id=$row[1] and ordr=$row[6];\n);
        $num1++;
     }
     else {
       $num0++;
     }

  }
  if ($targid > 0) {
          print OFILE qq(update target set 
		num_obs_req=$num0,num_obs_app=$num0,
		num_obs_req_1=$num1,num_obs_app_1=$num1,
		num_obs_req_2=$num2,num_obs_app_2=$num2
		where targ_id = $targid;\n\n);
  }


  print OFILE "-- check ao for window constraints \n";
  print OFILE "-- update timereq set ao=? where targ_id=? and ordr = ?\n";
  # slimy
  my $yr = 2000 + int($param{ao}) - 1;
  $sql = "select proposal_number,target.targid,
	ordr,time_constraint,tstart,tstop , datepart(yy,tstart)
        from proposal,target,timereq
	where  proposal.proposal_id = target.proposal_id
	and target.targid = timereq.targid
	and ao_str = '$param{ao}'
	and multi_cycle='Y'
	and (est_time_cycle_n1 >0 or est_time_cycle_n2 > 0)
	order by proposal_number,target.targid,ordr";
  $sth = $dbh->prepare($sql);
  $sth->execute();
  while (@row = $sth->fetchrow_array) {
     my($diff) = $row[$#row] - $yr;
     printf OFILE "-- %d  %d  %d  %s %s %s %s\n",@row;
     if ($diff > 0) {
       print OFILE "update timereq set ao=$diff where targ_id=$row[1] and ordr=$row[2];\n";
     }
  }


  close OFILE;

}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{
  my($pwd);

  %param = (
            U=> undef,
	    S=> undef,
	    ao => undef,
	    out => undef,
            verbose => 0
           );

  GetOptions( \%param,
              "U=s",
	      "S=s",
	      "out=s",
	      "ao=s",
	      "q=s",
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
  
  if(!$param{q}) {
    $pwd = get_password($param{U},$param{S});
  }
  else {
    $pwd = read_password($param{q});
  }
  $param{q} = $pwd;


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
sub read_password
{
  my($fname) = @_;
  my($pwd);

  open PFILE,"< $fname";
  while ( <PFILE> ){
    chomp;
    $pwd = $_;
  }
  close PFILE;

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
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

set_mcop_monitor.pl [options]

=head1 OPTIONS

B<pop_pr.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U> (user)

username for logging onto sql server with proposal database

=item B<-S> (server)

name of sql server with proposal database

=item B<-out> (filename)

name of the sql file to be generated

=item B<-ao> (ao string)

the ao string with leading zero. Using this flag will dump only the accepted
proposals from that ao cycle.  If this flag is not used, then all non-CAL and
non-DDT proposals from the ao listed in the proposal..context table will be 
dumped.

=item B<-help>

displays documentation for B<set_mcop_monitor.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the sql to populate the pr_proposal and pr_target tables
for a Peer Review database.  The script queries the proposal database on 
the production server to get the raw data for the Peer Review.  A single
panel can be dumped by using the B<-panel> flag.

=head1 AUTHOR

Sherry L. Winkelman
