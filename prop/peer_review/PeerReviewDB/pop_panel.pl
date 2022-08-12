#!/home/ascds/DS.release/ots/bin/perl
#
#******************************************************************************
# pop_panel.pl
#
# Creates the sql to populate the panel table with the correct number of
# reviewers
#******************************************************************************

BEGIN
{
   $ENV{SYBASE} = "/soft/SYBASE16.0";
}

use strict;
use DBI;
use vars qw($pwdProp %param );

{
  use Getopt::Long;
  parse_opts();
  my($proposal,$bppcnt,$cnt,$pundits);
  
  if ($param{help}) 
    { 
      usage(0);
    }
  
  
  my $propserver = "server=$param{SProposal}";
  my $propdb = "database=proposal";
  my $script_name = "scriptName=pop_panel.pl";
  $pwdProp=$param{q};
  
  # Database connection 1 (Proposal)
  my $dsnProp1 = "DBI:Sybase:$propserver;$script_name;$propdb";
  my $dbhProp1 = DBI->connect($dsnProp1, "$param{UProposal}", 
			      $pwdProp, {
					 PrintError => 0,
					 RaiseError => 1});
  my $panel_query = qq(select count(panel.panel_id) from panel,context
		where ao=context.current_ao_id 
                and panel_name not in ("LP","XVP","BPP")
		);

  my $rev_query = qq(select panel_name, count(pm.pers_id) 
		from panel_member pm,panel,context 
		where ao=context.current_ao_id 
		and panel.panel_id = pm.panel_id
		group by panel_name
		order by panel_name);

  open (OFILE, "> $param{out}") || 
    die "Sorry, can't open $param{out}: $!\n";

  $proposal = $dbhProp1->prepare($panel_query);
  $proposal->execute(); 
  my ($bppcnt ) = $proposal->fetchrow_array;
  $proposal->finish();

  print OFILE "delete from panel;\n";
  $proposal = $dbhProp1->prepare($rev_query);
  $proposal->execute(); 
  while (my ($panel_id, $cnt ) = $proposal->fetchrow_array) {
     if ($panel_id =~ /LP/) {
       $pundits =  $cnt;
       $bppcnt +=  $cnt;
     } elsif ($panel_id =~ /XVP/)  {
       print OFILE "insert into panel values (98,$cnt,$cnt);\n";
       $pundits =  $cnt;
       $bppcnt +=  $cnt;
     } else {
       print OFILE "insert into panel values ($panel_id,$cnt,$cnt);\n";
     }
  }
  # special GTO panel
  print OFILE "insert into panel values (97,4,4);\n";
  # BPP panel
  print OFILE "insert into panel values (99,$pundits,$bppcnt);\n";
 
  close OFILE;
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{
  my($pwd);

  %param = (
            UProposal => undef,
	    SProposal => undef,
	    out => "pop_panel.sql",
            verbose => 0
           );

  GetOptions( \%param,
              "UProposal=s",
	      "SProposal=s",
	      "out=s",
	      "q=s",
              "verbose",
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
    $pwd = get_password($param{UProposal},$param{SProposal});
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
  Pod::Text::pod2text( '-75', $0 ,*STDOUT);
  exit $exit;
}

__END__

=head1 USAGE

pop_panel.pl [options]

=head1 OPTIONS

B<pop_panel.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-UProposal>

username for logging onto sql server with proposal database

=item B<-SProposal> 

name of sql server with proposal database

=item B<-out> 

name of the sql file to be generated. Default is pop_panel.sql

=item B<-help>

displays documentation for B<pop_panel.pl>

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the sql to populate the panel table for the Peer Review 
database.  The script queries the proposal database on 
the production server to get the raw data for the Peer Review.  

=head1 AUTHOR

Diane Hall

