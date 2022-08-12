#!/usr/bin/perl 

#******************************************************************************
# peer_conflicts.pl
#
# This script prints conflicts for all Y/G proposals
# Entries are only once so lowest proposal number/panel is base
#
# included in output is status and normalized grade
#
#******************************************************************************

use strict;
use DBI;
use Data::Dumper;
use vars qw($pwdProp %param $VERSION);

$VERSION = '$Id: peer_conflicts.pl,v 1.0 2014/07/26  dmh Exp $';

my $dbh1;

my $simple = "";
my $cross = "";
my %bppchairs;


{

  use Getopt::Long;
  my($row,$str,%conflicts);
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
  

#  DBI->trace(1);
  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  $dbh1 = DBI->connect($dsn1, "", "", {
					  PrintError => 1,
					  RaiseError => 0});
  my ($min,$hr,$day, $month, $year) = (localtime)[1,2,3,4,5];
  $year += 1900;
  $month++;
  my $date = sprintf("$year%02d%02d%02d%02d",$month,$day,$hr,$min);
  my $fname = "conflicts_" . $date . ".txt";
  open SIMPLE,"> $fname"  or 
	die "Unable to open $fname for writing\n";
  binmode(SIMPLE, ":utf8");
  print STDERR "Writing output to $fname\n";
  my(@row,$ii,$sql);

  $sql = qq(select distinct prop_id,panel_member.last_name 
	from proposal,panel_member
	where proposal.prop_status='B' 
	and proposal.panel_id = panel_member.panel_id 	
	and panel_member.type='Chair');
  my $get_chairs = $dbh1->prepare($sql);
  $get_chairs->execute();
  while (@row = $get_chairs->fetchrow_array) {
     $bppchairs{$row[0]} .= " $row[1]" ;
  }
  $get_chairs->finish;

  # query to get all distinct targets with conflicts 
  $sql = qq(select distinct conflicts.prop_id,conflicts.targ_id,
	conflict_propid,conflict_targ_id
	from conflicts ,target t1
	where conflicts.prop_id = t1.prop_id 
	and conflicts.targ_id = t1.targ_id
	and ra != 0
  	order by conflicts.prop_id,conflicts.targ_id,conflict_propid,conflict_targ_id);
  my $get_pids = $dbh1->prepare($sql);
  $get_pids->execute();
  while (@row = $get_pids->fetchrow_array) {
    my $key1 = $row[0] ."_" . $row[1] . "_" . $row[2] ."_" . $row[3];
    my $key2 = $row[2] ."_" . $row[3] . "_" . $row[0] ."_" . $row[1];
    if (!$conflicts{$key1} && !$conflicts{$key2} ) {
      $conflicts{$key1} = 1;
    }
  }
  my @tarr = %conflicts;
  my @carr = sort @tarr;
  for ($ii=0;$ii<$#carr;$ii++) {
    if ($carr[$ii] != 1) {
      my ($p1,$t1,$p2,$t2) = split("_",$carr[$ii]);
      do_conflict($p1,$t1,$p2,$t2);
    }
  }


  my(@hdrs) = ("Panel","Chair","LP Chairs","Proposal","PI","TargNum","PStat","TStat","NormG","Type","AppTime","TargName","Detector","Grating","Grid?","Conflict","Sep");
  if ($param{single}) {
    my(@hdrs)=( "Panel","Chair","LP Chairs","Proposal","PI","TargNum","PStat","TStat","NormG","Type","AppTime","TargName","Detector","Grating","Grid?","C-Panel","C-Chair","C-Proposal","C-PI","C-TargNum","C-PStat","C-TStat","C-NormG","C-Type","C=AppTime","C-TargName","C-Detector","C-Grating","C-Grid?","Conflict","Sep");
  } 
  print SIMPLE  join($param{delim},@hdrs) . "\n";

  print SIMPLE "$simple\n\n";
  print SIMPLE "$cross";

}

sub do_conflict
{
  #input is conflict
  my ($p1,$t1,$p2,$t2) = @_;
  my($sql,@row,$ii,@vals);

  # get more info about the conflict
  if ($param{single}) {
    $sql = qq(select distinct p1.panel_id,pm1.last_name,NULL,p1.prop_id,p1.last_name,t1.targ_num, p1.prop_status,t1.targ_status,p1.fg_norm,p1.type || '/' || p1.big_proj, t1.app_time,t1.targ_name, t1.detector,t1.grating, p1.grid_flag, 
	p2.panel_id,pm2.last_name,NULL,p2.prop_id,p2.last_name,t2.targ_num, p2.prop_status,t2.targ_status,p2.fg_norm,p2.type || '/' || p2.big_proj, t2.app_time,t2.targ_name, t2.detector,t2.grating,p2.grid_flag,conflict_type,conflict_sep);
   
  } else {
    $sql = qq(select distinct p1.panel_id,pm1.last_name,NULL,p1.prop_id,p1.last_name,t1.targ_num, p1.prop_status,t1.targ_status,p1.fg_norm,p1.type || '/' || p1.big_proj, t1.app_time,t1.targ_name, t1.detector,t1.grating, p1.grid_flag, conflict_type,conflict_sep,
	p2.panel_id,pm2.last_name,NULL,p2.prop_id,p2.last_name,t2.targ_num, p2.prop_status,t2.targ_status,p2.fg_norm,p2.type || '/' || p2.big_proj, t2.app_time,t2.targ_name, t2.detector,t2.grating,p2.grid_flag,NULL,NULL);
  }
  $sql .= qq( from ((( conflicts  
	 JOIN proposal p1 on p1.prop_id=conflicts.prop_id 
           and p1.prop_status in  ('G','Y')
 	   and p1.panel_id != 98 
	   LEFT JOIN panel_member pm1 on pm1.panel_id=p1.panel_id 
	     and (pm1.type = 'Chair' or pm1.type is null or pm1.type = ''))
	 JOIN proposal p2 on p2.prop_id=conflicts.conflict_propid 
           and p2.prop_status in ('G','Y')
 	   and p2.panel_id != 98 
	   LEFT JOIN panel_member pm2 on pm2.panel_id=p2.panel_id 
             and (pm2.type = 'Chair' or pm2.type is null or pm2.type = ''))
         JOIN target t1 on t1.prop_id = p1.prop_id and t1.panel_id=p1.panel_id
         JOIN target t2 on t2.prop_id = p2.prop_id and t2.panel_id=p2.panel_id)
	where conflicts.targ_id = t1.targ_id
	and conflicts.prop_id = $p1 and conflicts.targ_id =$t1 
	and conflicts.conflict_targ_id = t2.targ_id
	and conflicts.conflict_propid = $p2 and conflicts.conflict_targ_id =$t2
	order by p1.panel_id,p2.panel_id);

  #print STDERR "$sql\n";
  my $get_tots = $dbh1->prepare($sql);
  $get_tots->execute();
  my ($pan1) = 0;
  my ($chr1) = 2;
  my ($pid1) = 3;
  my ($pan2) = 17;
  my ($chr2) = 19;
  my ($pid2) = 20;
  $pan2 = 15 if $param{single};
  $chr2 = 17 if $param{single};
  $pid2 = 18 if $param{single};
  while (@row = $get_tots->fetchrow_array) {
    for ($ii=0;$ii<=$#row;$ii++) {
      $row[$ii] =~ s/\/N\/A//;
      if ($row[$ii] =~ /,/) {
        $row[$ii] = qq("$row[$ii]");
      }
      $row[$chr1] = qq("$bppchairs{$row[$pid1]}") if ($row[$pan1] == 99) ;
      $row[$chr2] = qq("$bppchairs{$row[$pid2]}") if ($row[$pan2] == 99) ;

      $row[$pan1+1] = "BPP" if ($row[$pan1] == 99);
      $row[$pan2+1] = "BPP" if ($row[$pan2] == 99);
      if ($row[$pan1] == $row[$pan2]) {
         $simple .= $row[$ii]. $param{delim};
         if (!defined $param{single} && $ii == int($#row/2)) { 
	   $simple .= "\n"; 
         }
      } else {
         $cross .= $row[$ii]. $param{delim};
         if (!defined $param{single} && $ii == int($#row/2)) { 
           $cross .= "\n"; 
         }
      }
    }
    if ($row[$pan1] == $row[$pan2]) {
       if ($param{single}) {
         $simple .= "\n";
       }else {
         $simple .= "\n\n";
       }
    } else {
       if ($param{single}) {
         $cross .= "\n";
       }else {
         $cross .= "\n\n";
       }
    }
         
  }
  $get_tots->finish;
}

   



#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
            delim => ",",
            verbose => 0
        
           );

  GetOptions( \%param,
	      "U=s",
	      "delim=s",
	      "single",
              "verbose=i",
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

peer_conflicts.pl [options]

=head1 OPTIONS

B<peer_conflicts.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name on postgresql server

=item B<-single>

write conflict to single line instead of 2 

=item B<-help>

displays documentation for B<peer_conflicts.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script writes out simple/cross conflicts for all Y/G targets excluding panels 97/98

=head1 AUTHOR

CXCDS
