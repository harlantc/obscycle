#!/home/ascds/DS.release/ots/bin/perl
#
#******************************************************************************
# set_gto_groupname.pl
#
#
#******************************************************************************

BEGIN
{
   $ENV{SYBASE} = "/soft/SYBASE16.0";
}

use strict;
use DBI;
use vars qw($pwdProp %param $VERSION);
$VERSION = '$Id: pop_pr.pl,v 1.59 2012/02/14 14:10:22 wink Exp $';

{
  use Getopt::Long;
  parse_opts();
  my($pno,$gname,@row);
  
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
  my $script_name = "scriptName=set_gto_groupname.pl";
  $pwdProp=$param{q};
  
  # Database connection 1 (Proposal)
  my $dsnProp1 = "DBI:Sybase:$propserver;$script_name;$propdb";
  my $dbh = DBI->connect($dsnProp1, "$param{U}", 
			      $pwdProp, {
					 PrintError => 0,
					 RaiseError => 1});

  open (OFILE,"> $param{out}" ) or 
	die "Unable to open $param{out} for writing.\n";
  my $sql = "select proposal_number,panel_name
        from proposal,panel,panel_proposal
	where type like '%GTO%' 
	and ao_str = '$param{ao}'
	and proposal.proposal_id = panel_proposal.proposal_id
	and panel_proposal.panel_id = panel.panel_id
	and panel_name != 'LP'
	order by proposal_number,panel_name ";
  print STDERR "$sql\n";
  my $sth = $dbh->prepare($sql);
  $sth->execute();
  $pno = 0;
  $gname = "";
  while (@row = $sth->fetchrow_array) {
     if ($pno != $row[0]) {
       if ($pno > 0)  {
         print OFILE "update proposal set group_id='$gname' where prop_id=$pno and panel_id = 97;\n";
       }
       $pno = $row[0];
       $gname = "";
     }
     $gname .= $row[1] . " ";
  }
  # do the last one
  print OFILE "update proposal set group_id='$gname' where prop_id=$pno and panel_id = 97;\n";
       
       
  $sth->finish;
    
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

set_gto_groupname.pl [options]

=head1 OPTIONS

B<set_gto_groupname.pl> uses long option names.  You can type as few characters as
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
