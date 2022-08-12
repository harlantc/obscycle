#!/home/ascds/DS.release/ots/bin/perl
#
#******************************************************************************
# pop_reviewers.pl
#
# Creates the sql to populate the panel_member table with the panel,last name of Chair
# and all the reviewers.  Used by EditPage for grades and by conflict script
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
  
  if ($param{help}) 
    { 
      usage(0);
    }
  
  
  my $proposal;
  my $propserver = "server=$param{SProposal}";
  my $propdb = "database=proposal";
  my $script_name = "scriptName=pop_reviewers.pl";
  $pwdProp=$param{q};
  
  # Database connection 1 (Proposal)
  my $dsnProp1 = "DBI:Sybase:$propserver;$script_name;$propdb";
  my $dbhProp1 = DBI->connect($dsnProp1, "$param{UProposal}", 
			      $pwdProp, {
					 PrintError => 0,
					 RaiseError => 1});

  my $chair_query = qq(select panel_name,last,member_type
	from panel,panel_member,axafusers..person_short ps,context
	where ao=context.current_ao_id 
	and panel.panel_id *= panel_member.panel_id 
	and panel_member.pers_id *= ps.pers_id
	order by panel_name);
	

  open (OFILE, "> $param{out}") || 
    die "Sorry, can't open $param{out}: $!\n";
  print OFILE "delete from panel_member;\n";

  my($pquery) = $dbhProp1->prepare($chair_query);
  $pquery->execute(); 
  while (my ($panel_id, $last_name,$mtype ) = $pquery->fetchrow_array) {
     $mtype = "Deputy"  if ($mtype =~ /Deputy Chair/) ;
     #BPP panel is panel 99, special LP (if they have it ) is 98
     if ($panel_id =~ /LP/) {
         $panel_id= "99";
     } 
     $last_name =~ s/'/''/g;
     $last_name =~ s/"/\\"/g;

     if ($mtype eq "Chair") {
       print OFILE qq(insert into panel_member values (99,E'$last_name','Reviewer');\n);
     }
     #just in case there's a panel 98
     if ($panel_id==99 && $mtype =~ /Pundit/) {
       print OFILE qq(insert into panel_member values (98,E'$last_name','$mtype');\n);
     } 
     print OFILE qq(insert into panel_member values ($panel_id,E'$last_name','$mtype');\n);
  }
  print OFILE qq(insert into panel_member values (97,E'GTO','Chair');\n);
 
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
	    out => "pop_reviewers.sql",
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

pop_reviewers.pl [options]

=head1 OPTIONS

B<pop_reviewers.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-UProposal>

username for logging onto sql server with proposal database

=item B<-SProposal> 

name of sql server with proposal database

=item B<-out> 

name of the sql file to be generated. Default is pop_reviewers.sql

=item B<-help>

displays documentation for B<pop_reviewers.pl>

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the sql to populate the panel_member table for the 
Peer Review database.  The script queries the proposal database on 
the production server to get the raw data for the Peer Review.  

=head1 AUTHOR

CXCDS

