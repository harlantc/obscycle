#!/usr/bin/perl 

#******************************************************************************
# calc_allot.pl
#
# This script calculates allotments for joint,too,constraints
#
# For each cycle: update the allotments in the @allot table
#******************************************************************************

use strict;
use DBI;
use Data::Dumper;
use vars qw($pwdProp %param $VERSION @bpptots);

$VERSION = '$Id: calc_allot.pl,v 1.0 2014/07/26  dmh Exp $';

my $aCOL = 1;
my $aALLOT = 2;
my $aFMT = 3;

# extra BPP allotments
my $hst_bpp=150;
my $xmm_bpp=600;
my $nustar_bpp=500;

# proposal_column,allotment_column,allotment_from_CDO,format
my @allot = (
"hst_req","hst",100,"%d",
"xmm_req","xmm",400,"%4.1f",
"spitzer_req","spitzer",0,"%4.1f",
"swift_req","swift",500,"%4.1f",
"nustar_req","nustar",500,"%4.1f",
"noao_req","noao",15,"%4.1f",
"nrao_req","nrao",391,"%4.1f",
"vf_req","vf_too",10,"%d",
"f_req","f_too",20,"%d",
"s_req","s_too",30,"%d",
"vs_req","vs_too",40,"%d",
"rc_score_req","rc_score",23823.0,"%8.2f",
"rc_score_req_1","rc_score_1",4198.0,"%8.2f",
"rc_score_req_1","rc_score_2",2099,"%8.2f",
"prop_req_time_1","total_exp_time_1",2000,"%7.2f",
"prop_req_time_2","total_exp_time_2",1000,"%7.2f",
"prop_req_hel_1","total_hel_time_1",240,"%7.2f",
"prop_req_hel_2","total_hel_time_2",240,"%7.2f",
);

my $archive_allot = 1050;
my $theory_allot  =  600;

print STDERR "HEY!  Don't forget to review the multicycle times for LP/VLP panel\n";
# The BPP time calculated for the multicycles is then split between LP and VLP
my $lp_allot  =  3000*1.05;
my $lp_allot_1  =  0;   
my $lp_allot_2  =  0;

# dropped *1.05 for HEL
my $hel_lp_allot  =  373;
my $hel_lp_allot_1  =  0;
my $hel_lp_allot_2  =  0;

my $xvp_allot  =  0;
my $xvp_allot_1  =  0;
my $xvp_allot_2  =  0;

my $hel_xvp_allot  =  0;
my $hel_xvp_allot_1  =  0;
my $hel_xvp_allot_2  =  0;

my $vlp_allot  =  1000*1.05;
my $vlp_allot_1  =  0; 
my $vlp_allot_2  =  0;

my $hel_vlp_allot  =  124;
my $hel_vlp_allot_1  =  0;
my $hel_vlp_allot_2  =  0;
#my $vlp_allot_1_xtra = 2000;  # For cycle 21, VLP cycle 22 gets extra 2000 
my $vlp_allot_1_xtra = 0;  

my $bpp_allot_1;  
my $bpp_allot_2;
my $bpp_req_1;
my $bpp_req_2;
my $lp_req_1;
my $lp_req_2;

my $dbh1;


{

  use Getopt::Long;
  my($row,$str);
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

  print STDOUT "delete from allotment;\n";

  # create entries in allotment table for all panels
  my($sql) = qq(select distinct panel_id from proposal order by panel_id);
  my $get_pcnt = $dbh1->prepare($sql);
  $get_pcnt->execute();
  my @row;
  while (@row = $get_pcnt->fetchrow_array) {
    print STDOUT "insert into allotment (panel_id) values($row[0]);\n";
  }
  $get_pcnt->finish();

  # retrieve the sum of panel 99 for the special bpp allotments
  my($sql) = "select ";
  for (my $ii=0;$ii<=$#allot;$ii+=4) {
     $sql .= "sum($allot[$ii]),";
  }
  chop($sql);
  $sql .= qq( from proposal where panel_id = 99);
  my $get_bpptots = $dbh1->prepare($sql);
  $get_bpptots->execute();
  my @row;
  while (@row = $get_bpptots->fetchrow_array) {
    for (my $ii=0;$ii<=$#row;$ii++) {
       $bpptots[$ii] = $row[$ii];
    }
  }
  $get_bpptots->finish();

  # get the sum of the multicycle times for just the LPs
  $sql  = qq(select sum(prop_req_time_1),sum(prop_req_time_2) from proposal where panel_id=99 and big_proj = 'LP');
  print STDERR "$sql\n" if ($param{verbose});
  my $get_lp_share = $dbh1->prepare($sql);
  $get_lp_share->execute();
  while (@row = $get_lp_share->fetchrow_array) {
       $lp_req_1=$row[0];
       $lp_req_2=$row[1];
  }
  $get_lp_share->finish();

  # Retrieve the sum of all requested values for each allotment type
  $sql = "select ";
  for (my $ii=0;$ii<=$#allot;$ii+=4) {
     $sql .= "sum($allot[$ii]),";
  }
  chop($sql);
  $sql .= qq( from proposal where prop_status != 'B');
  if ($param{t}) {
    $sql .= qq( and triage != 'Y' and triage != 'A');
  }

  my $get_tots = $dbh1->prepare($sql);
  $get_tots->execute();
    while (@row = $get_tots->fetchrow_array) {
       my($ii,$jj);
       # Determine if we need allotment
       # As of cycle 17, Tara wants requested=allotment if no allotment
       # is needed so always do the allotment
       # Note: if triage option used and they resurrect a proposal, the
       # allotments might be skewed, but they are 'fuzzy' allotments anyways
       # and CDO reviews the totals for all the panels at the end. 
       for ($jj=0, $ii=0;$ii<=$#allot;$ii+=4,$jj++) {
         my $needit = 0;
         print STDERR ("\n$allot[$ii] --- $row[$jj] -- bpp=$bpptots[$jj]\n");
         # for these 3, subtract the panel 99 totals to determine if you need
         # allotments for them
         if (($allot[$ii] =~ /hst/ && $hst_bpp>0) ||
             ($allot[$ii] =~ /xmm/ && $xmm_bpp>0) ||
             ($allot[$ii] =~ /nustar/ && $nustar_bpp>0)) {
           if (($row[$jj] - $bpptots[$jj]) > $allot[$ii+$aALLOT]) {
              $needit =1;
           }
         }
         elsif ($row[$jj] > $allot[$ii+$aALLOT]) {
           $needit = 1;
         }   
         # save off the multicyle requested times for the BPP
         $bpp_req_1 = $bpptots[$jj] if ($allot[$ii] =~ /req_time_1/) ;
         $bpp_req_2 = $bpptots[$jj] if ($allot[$ii] =~ /req_time_2/) ;

         # go calculate the allotments and create the sql statements 
         do_allot($ii,$row[$jj],$needit,$bpptots[$jj]);
       }
    }
   $get_tots->finish();


   # archive
    $sql = qq(select sum(total_req_time) from proposal where type like 'AR%' 	and prop_status !='B' );
    if ($param{t}) {
      $sql .= qq( and triage != 'Y' and triage != 'A');
    }
    my $get_arc = $dbh1->prepare($sql);
    $get_arc->execute();
    my $arctot=0;
    while (@row = $get_arc->fetchrow_array) {
       $arctot=$row[0];
    }
    $sql = qq(select panel_id,sum(total_req_time) from proposal 
	where type like 'AR%'
	and panel_id < 90
	and prop_status != 'B');
    if ($param{t}) {
      $sql .= qq( and triage != 'Y' and triage != 'A');
    }
    $sql .= qq( group by panel_id order by panel_id);
    my $get_arcp = $dbh1->prepare($sql);
    $get_arcp->execute();
    while (@row = $get_arcp->fetchrow_array) {
       if ($row[1] > 0) {
         my $xx =  $row[1] / $arctot;
         my $yy =  $xx * $archive_allot; 
         printf STDOUT "update allotment set archive_allot=%5.2f where panel_id=$row[0];\n",$yy;
       } 
    }
   # theory
    $sql = qq(select sum(total_req_time) from proposal where type like 'TH%' and prop_status != 'B');
    if ($param{t}) {
      $sql .= qq( and triage != 'Y' and triage != 'A');
    }
    my $get_the = $dbh1->prepare($sql);
    $get_the->execute();
    my $thetot=0;
    while (@row = $get_the->fetchrow_array) {
       $thetot=$row[0];
    }
    $sql = qq(select panel_id,sum(total_req_time) from proposal 
	where type like 'TH%' and prop_status != 'B'
	and panel_id < 90);
    if ($param{t}) {
      $sql .= qq( and triage != 'Y' and triage != 'A');
    }
    $sql .= qq( group by panel_id order by panel_id);
    my $get_thep = $dbh1->prepare($sql);
    $get_thep->execute();
    while (@row = $get_thep->fetchrow_array) {
       if ($row[1] > 0) {
         my $xx =  $row[1] / $thetot;
         my $yy =  $xx * $theory_allot; 
         printf STDOUT "update allotment set theory_allot=%5.2f where panel_id=$row[0];\n",$yy;
       } 
    }
    
    #LP/XVP allotments
    print STDERR " bppallot1=$bpp_allot_1 bppallot2=$bpp_allot_2\n" if ($param{verbose});
    print STDERR (" bpp1=$bpp_req_1 bpp2=$bpp_req_2\n") if ($param{verbose});
    print STDERR (" lp1=$lp_req_1 lp2=$lp_req_2\n") if ($param{verbose});
    # if the BPP requests more than alloted
    if ($bpp_req_1 > $bpp_allot_1) {
       my $vlp_req_1= $bpp_req_1 - $lp_req_1;
       if ($lp_req_1 > 0) {
         if ($vlp_req_1 > $vlp_allot_1_xtra) {
           $lp_allot_1 = ($bpp_allot_1 * ($lp_req_1/$bpp_req_1));
         } else {
           $lp_allot_1 = $bpp_allot_1;
         }
         $lp_allot_1 = $lp_req_1 if ($lp_req_1 < $lp_allot_1);
       }
       if ($vlp_req_1 > 0) {
         $vlp_allot_1 = ($bpp_allot_1 - $lp_allot_1) + $vlp_allot_1_xtra;
         $vlp_allot_1 = $vlp_req_1 if ($vlp_req_1 < $vlp_allot_1) ;
       }
    }
    if ($bpp_req_2 > $bpp_allot_2) {
       my $vlp_req_2= $bpp_req_2 - $lp_req_2;
       if ($lp_req_2 > 0) {
         $lp_allot_2 = ($bpp_allot_2 * ($lp_req_2/$bpp_req_2));
         $lp_allot_2 = $lp_req_2 if ($lp_req_2 < $lp_allot_2);
       }
       if ($vlp_req_2 > 0) {
         $vlp_allot_2 = ($bpp_allot_2 - $lp_allot_2) ;
         $vlp_allot_2 = $vlp_req_2 if ($vlp_req_2 < $vlp_allot_2) ;
       }
    }

    printf STDOUT "update allotment set lp=%6.2f where panel_id=99;\n",$lp_allot  if ($lp_allot > 0) ;
    printf STDOUT "update allotment set lp_1=%6.2f where panel_id=99;\n",$lp_allot_1  if ($lp_allot_1 > 0) ;
    printf STDOUT "update allotment set lp_2=%6.2f where panel_id=99;\n",$lp_allot_2  if ($lp_allot_2 > 0) ;
    printf STDOUT "update allotment set xvp=%6.2f where panel_id=99;\n",$xvp_allot if ($xvp_allot > 0);
    printf STDOUT "update allotment set xvp_1=%6.2f where panel_id=99;\n",$xvp_allot_1  if ($xvp_allot_1 > 0) ;
    printf STDOUT "update allotment set xvp_2=%6.2f where panel_id=99;\n",$xvp_allot_2  if ($xvp_allot_2 > 0) ;
    printf STDOUT "update allotment set vlp=%6.2f where panel_id=99;\n",$vlp_allot if ($vlp_allot > 0);
    printf STDOUT "update allotment set vlp_1=%6.2f where panel_id=99;\n",$vlp_allot_1  if ($vlp_allot_1 > 0) ;
    printf STDOUT "update allotment set vlp_2=%6.2f where panel_id=99;\n",$vlp_allot_2  if ($vlp_allot_2 > 0) ;

    #For HEL
    printf STDOUT "update allotment set hel_lp=%6.2f where panel_id=99;\n",$hel_lp_allot  if ($hel_lp_allot > 0) ;
    printf STDOUT "update allotment set hel_lp_1=%6.2f where panel_id=99;\n",$hel_lp_allot_1  if ($hel_lp_allot_1 > 0) ;
    printf STDOUT "update allotment set hel_lp_2=%6.2f where panel_id=99;\n",$hel_lp_allot_2  if ($hel_lp_allot_2 > 0) ;
    printf STDOUT "update allotment set hel_xvp=%6.2f where panel_id=99;\n",$hel_xvp_allot if ($hel_xvp_allot > 0);
    printf STDOUT "update allotment set hel_xvp_1=%6.2f where panel_id=99;\n",$hel_xvp_allot_1  if ($hel_xvp_allot_1 > 0) ;
    printf STDOUT "update allotment set hel_xvp_2=%6.2f where panel_id=99;\n",$hel_xvp_allot_2  if ($hel_xvp_allot_2 > 0) ;
    printf STDOUT "update allotment set hel_vlp=%6.2f where panel_id=99;\n",$hel_vlp_allot if ($hel_vlp_allot > 0);
    printf STDOUT "update allotment set hel_vlp_1=%6.2f where panel_id=99;\n",$hel_vlp_allot_1  if ($hel_vlp_allot_1 > 0) ;
    printf STDOUT "update allotment set hel_vlp_2=%6.2f where panel_id=99;\n",$hel_vlp_allot_2  if ($hel_vlp_allot_2 > 0) ;
}

sub do_allot
{
  my($indx,$tot,$needit,$bpptot)=@_;
  my($xx,$yy);

  print STDERR "do_allot: tot=$tot bpp=$bpptot  needit=$needit for $allot[$indx+$aCOL]\n" if ($param{verbose});
  
  # now get requested time by panel for each allotment
  my($sql) = qq(select panel_id,sum($allot[$indx]) from proposal 
	where prop_status != 'B');
  if ($param{t}) {
    $sql .= qq( and triage != 'Y' and triage != 'A');
  }
  $sql .= qq( group by panel_id order by panel_id);
  print STDERR "$sql\n" if ($param{verbose});
  my $get_pans = $dbh1->prepare($sql);
  $get_pans->execute();

  my @row;
  my @panels;
  my @val;
  my $addedExtraCnt=0;
  my $dopan99=1;
  my $tt=0;
  my $leftover=0;

  # if this is a special type, remove the bpp panel from the total
  if ($allot[$indx] =~ /hst|xmm|nustar/) { 
    $tot = $tot-$bpptot; 
    $dopan99=0;
    if (!$needit) {
      $leftover = $allot[$indx+$aALLOT] - $tot;
      $leftover =0  if ($leftover < 0) ;
    }
    print STDERR "Reset $allot[$indx] tot=$tot leftover=$leftover\n" if ($param{verbose}) ;
  }
  while (@row = $get_pans->fetchrow_array) {
    if ($dopan99 || $row[0] != 99) {
      if (!$needit ) {
        if ($row[1]> 0 && ($dopan99 || $row[0] != 99)) {
          printf STDOUT "update allotment set $allot[$indx+$aCOL] = $allot[$indx+$aFMT] where panel_id = $row[0];\n",$row[1];  
        }
      } elsif ($row[1] >  0) {
        print STDERR "$row[0] " if ($param{verbose}) ;
        $xx= $row[1] / $tot;
        $yy= $xx *  $allot[$indx+$aALLOT];
        print STDERR " yy=$yy" if ($param{verbose}) ;
        push(@panels,$row[0]);
        # if the allotment is integer, you can't award <1  
        if ($allot[$indx+$aFMT] =~ /d/) {
          $yy = round($yy);
          if ($yy < 1 ) {
            $yy=1.0;
            $addedExtraCnt++;
          }
        }
        push(@val,$yy);
        $tt+= $yy;
        print STDERR "   yy=$yy  tt=$tt\n" if ($param{verbose});
        #print STDERR "  xx=$xx  yy=$yy  allot=$allot[$indx+$aALLOT]   $allot[$indx+$aFMT]\n" if ($param{verbose});
      }
    }
  }
  print STDERR "$allot[$indx]: cnt=$addedExtraCnt total=$tt  allot=$allot[$indx+$aALLOT]\n" if ($param{verbose});
     
  # we need allotments and we've added extra to round some panels up, 
  # so then we need to deduct that from the other panels that got the 
  # highest allotments
  while ($needit && $addedExtraCnt > 0 && $tt > $allot[$indx+$aALLOT]) {
    my $max=0;
    my $maxnn=0;
    for (my $nn=0;$nn<=$#val;$nn++) {
      print STDERR "$nn $panels[$nn]:  val=$val[$nn] max=$max maxnn=$maxnn \n" if ($param{verbose});
      if ($val[$nn] > $max) {
        $max=$val[$nn];
        $maxnn=$nn;
      }
    }
    if ($val[$maxnn] > 2 ) {
      $val[$maxnn] -= 1;
      $tt-=1;
    }
    $addedExtraCnt -= 1;
  }

  for (my $nn=0;$nn<=$#val;$nn++) {
    printf STDERR "update allotment set $allot[$indx+$aCOL] = $allot[$indx+$aFMT] where panel_id = $panels[$nn];\n",$val[$nn] if ($param{verbose});
    printf STDOUT "update allotment set $allot[$indx+$aCOL] = $allot[$indx+$aFMT] where panel_id = $panels[$nn];\n",$val[$nn];

    # save off the BPP amounts for use later when we split between LP and VLP
    $bpp_allot_1 = $val[$nn]  if ($panels[$nn] == 99 && $allot[$indx] =~ /_req_time_1/);
    $bpp_allot_2 = $val[$nn]  if ($panels[$nn] == 99 && $allot[$indx] =~ /_req_time_2/);
      
  }
  #special case
  if ($allot[$indx] =~ /hst|xmm|nustar/) { 
    my($bppallot)=0;
    $bppallot = $hst_bpp + $leftover if ($allot[$indx+$aCOL] =~ /hst/ );
    $bppallot = $xmm_bpp + $leftover if ($allot[$indx+$aCOL] =~ /xmm/ );
    $bppallot = $nustar_bpp + $leftover if ($allot[$indx+$aCOL] =~ /nustar/ );
    print STDERR "Special BPP: $allot[$indx+$aCOL] $bppallot  + leftover=$leftover\n";
    printf STDOUT "update allotment set $allot[$indx+$aCOL] = $allot[$indx+$aFMT] where panel_id = 99;\n",$bppallot;
    printf STDERR "update allotment set $allot[$indx+$aCOL] = $allot[$indx+$aFMT] where panel_id = 99;\n",$bppallot if ($param{verbose});
  }
}
  

sub round {
  $_[0] > 0 ? int($_[0] + .5) : -int(-$_[0] + .5)
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
	      "t",
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

calc_allot.pl [options]

=head1 OPTIONS

B<calc_allot.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name on postgresql server

=item B<-t>

Exclude TRIAGED proposals

=item B<-help>

displays documentation for B<move_proposal.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the SQL for Joint, TOO and Constraint allotments
at the peer review.

=head1 AUTHOR

Diane Hall
