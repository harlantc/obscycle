#!/usr/bin/perl
#---------------------------------------------------------------
# 
#  spreadsheet for projection: 
#    - group_id is set to bpp_pass, bpp_gray
#    . passing-ranked LP/XVPs separately, by panel in rank order 
#    . columns: blank,panel,proposal #, type,rank, PI,group, title
#---------------------------------------------------------------

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION %pri);
$VERSION = '$Id: ranked_BPP_by_panel.pl,v 1.1 2014/06/22 anon Exp $';

{
  use Getopt::Long;
  parse_opts();
  
  if ($param{version}) {
    print $VERSION, "\n";
    exit( 0 );
  }

  if ($param{help}) {
    usage(0);
  }
  
  
  # Database connection
  my $dsn = "dbi:Pg:dbname=$param{U}";
  my $dbh = DBI->connect($dsn, "", "");
  my(@row);
  my($sql);
  my(%props);

  read_pundits($param{p});

  my $get_prop = $dbh->prepare(qq(
	select prop_id,panel_id,rank,group_id
	from proposal where panel_id < 99 
	and big_proj like '%P'
	order by panel_id,prop_id));
  $get_prop->execute();
  while (@row = $get_prop->fetchrow_array) {
    if ($row[3] =~ /bpp_pass/i ) {
       $row[3]="Y";
    } elsif ($row[3] =~ /bpp_gray/i ) {
       $row[3]="G";
    } else {
       $row[3] = "N";
    }
    if (!$row[2] ) {
      $row[2] = "";
    }
    $props{$row[0]} .= "$row[1]\_$row[2]\_$row[3],";
  }

  open (OUT, ">BPP_ranked_panels.txt") ||
        die "Sorry can't open BPP_ranked_panel.txt: $!\n";
  binmode(OUT, ":utf8");
 printf OUT ("Discuss?\tProposal\tPI\tType\tPanel1\tRank1\tPass1\tPanel2\tRank2\tPass2\tPass3\tPrimary\tSecondary\tTitle\n");
  my $get_prop = $dbh->prepare(qq(
	select distinct prop_id,big_proj,last_name,title,type
	from proposal where panel_id < 99 
	and big_proj like '%P'
	order by prop_id));
  $get_prop->execute();
  while (@row = $get_prop->fetchrow_array) {
    my(@arr) = split(",",$props{$row[0]});
    my($p1) = "";
    my($r1) = "";
    my($g1) = "";
    my($p2) = "";
    my($r2) = "";
    my($g2) = "";
    if ($row[4] =~ /TOO/) {
       $row[2] .= " (TOO)"
    }
    
    if ($#arr >= 0) {
      #print STDERR "$row[0]: $arr[0]  --- $arr[1]\n";
      ($p1,$r1,$g1) = split(/\_/,$arr[0]);
      if ($#arr > 0) {
        ($p2,$r2,$g2) = split(/\_/,$arr[1]);
      }
    }
    printf OUT ("\t%8d\t%-20.20s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t  \t  \t%s\t%s\n",
        $row[0],$row[2],$row[1],$p1,$r1,$g1,$p2,$r2,$g2,$pri{$row[0]},$row[3]);
	  
  }
  close OUT;
    

}

sub read_pundits
{
  my($fname) = @_;
  my($str,@arr);
  

  if (-e $fname) {
    open (IFILE,"< $fname") or 
	die "Unable to open $fname for reading\n";

    while ($str = <IFILE>) {
      if ($str !~ /proposal/i &&  
          $str !~ /-----/i &&  
          $str !~ /rows aff/i ) {
         $str =~ s/  / /g;
         $str =~ s/^ //g;
      }
      @arr = split(' ',$str);
      $pri{$arr[0]} = $arr[1];
    }
    close IFILE;
  } else {
    print STDERR "WARNING: $fname doesn't exist. Skipping Pundit primary review assignments\n";
  }
}
#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
            U => undef,
            verbose => 0,
            p => "/soft/data/pundit_pri.txt"
           );

  GetOptions( \%param,
              "U=s",
              "p=s",
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

ranked_BPP_new.pl [options]

=head1 OPTIONS

B<ranked_BPP_by_panel.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to query

=item B<-p>

pundit primary reviewer list 

=item B<-help>

displays documentation for B<ranked_BPP.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script prints the BPP ranked ordered list for the first evening session.

=head1 AUTHOR

prefers to remain anonymous!
