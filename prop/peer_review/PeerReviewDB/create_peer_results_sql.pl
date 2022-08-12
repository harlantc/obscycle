#!/home/ascds/DS.release/ots/bin/perl
#*H****************************************************************************
#* Copyright (c) 2014-2020, Smithsonian Astrophysical Observatory
#  You may do anything you like with this file except remove this
#  copyright.
#
#* FILE NAME: create_peer_results_sql.pl
#
#* DEVELOPMENT: ObsCycle
#
#* DESCRIPTION:
#
#     This script reads the files with peer results produced by the migrate_tables.pl
#     and creates the SQL for the proposal and target updates on Sybase .
#
#   Cycle##.proposal
#      proposal_number
#      status
#      normalized_grade
#      hst_app
#      xmm_app
#      noao_app
#      nrao_app
#      rxte_app
#      spitzer_app
#      suzaku_app
#      swift_app
#      nustar_app
#
#   Cycle##.archive_theory
#      proposal_number
#      status
#      normalized_grade
#      recommended_funds
#      noao_app
#
#    Cycle##.target
#      proposal_number
#      target_status
#      targid
#      targnum
#      app_time
#      app_time+1
#      app_time+2
#      probability (if changed)
#
#    Cycle##.grid
#      proposal_number
#      target_status
#      targid
#      targnum
#      app_nbr
#      app_nbr+1
#      app_nbr+2
#
#    Cycle##.too_mon
#      proposal_number
#      target_status
#      targid
#      targnum
#      ordr
#      app_time
#      charge_ao_str
#
#    Cycle##.alt (only changed entries)
#      alt_id
#      approved_count
#
#
#
#* NOTES:
#
#    status from peer review is Y/N and needs to be changed to APPROVED/REJECTED
#    charge_ao_str for monitors is 0,1,2
#
#* REVISION HISTORY:
#
#
#H****************************************************************************/
BEGIN
{
    $ENV{SYBASE} = "/soft/SYBASE16.0";
}



use vars qw ($dbh %param  %ptype %pjoint $delim  %targnums $PASS_GRADE
	);

$PASS_GRADE = 3.5;
$delim = '\t';
use strict;
use Getopt::Long;
use DBI;

# -------------------------- MAIN ----------------------------------
{
  my($stmp,$dsn,$sth,@row);

  &parse_input;

  #database connection
  $stmp = "server=$param{S};database=proposal";
  $dsn = "DBI:Sybase:$stmp";
  $dbh = DBI->connect($dsn, $param{U}, $param{q}, {
        PrintError => 1,
        RaiseError => 1});
  if ( !defined $dbh) {
    exit 1;
  }

  $stmp = qq(select proposal_number,type,joint from proposal
	where ao_str="$param{a}");
  $sth= $dbh->prepare($stmp);
  $sth->execute();
  while (@row = $sth->fetchrow_array) {
    $ptype{$row[0]} = $row[1];
    $pjoint{$row[0]} = $row[2];
  }
  $sth->finish;
  $stmp = qq(select targid,targ_num from 
	proposal,target
	where ao_str="$param{a}"
	and proposal.proposal_id = target.proposal_id);
  $sth= $dbh->prepare($stmp);
  $sth->execute();
  while (@row = $sth->fetchrow_array) {
    $targnums{$row[0]} = $row[1];
  }
  $sth->finish;
  
  
  open (PFILE,"< $param{i}") or 
	die "Unable to open $param{i} for reading.\n";
  open (OFILE,"> $param{o}") or 
	die "Unable to open $param{o} for writing.\n";
  print OFILE "use proposal\ngo\n";

  if ($param{t} =~ /^p/i) {
    print STDERR "processing proposal file $param{i}\n";
    do_proposal(0);
  } elsif ($param{t} =~ /^arc/i) {
    print STDERR "processing archive/theory file $param{i}\n";
    do_proposal(1);
  } elsif ($param{t} =~ /^t/i) {
    print STDERR "processing target file $param{i}\n";
    do_target();
  } elsif ($param{t} =~ /^g/i) {
    print STDERR "processing grid file $param{i}\n";
    do_grid();
  } elsif ($param{t} =~ /^o/i) {
    print STDERR "processing observation file $param{i}\n";
    do_observation();
  } elsif ($param{t} =~ /^alt/i) {
    print STDERR "processing alternate file $param{i}\n";
    do_alternate();
  }
 

  close PFILE;
  close OFILE;
  chmod (0660,"$param{o}");

}

sub do_proposal
{
  my($isArchiveTheory) = @_;
  my($str,@arr);
  my($pno,$status,$ng,$funds);
  my($hst,$xmm,$spitzer,$rxte,$noao,$nrao,$suzaku,$swift,$nustar);
  my($rank,$app,$pname);
  my($thst,$txmm,$tspitzer,$tnoao,$tnrao,$tsuzaku,$tswift,$tnustar,$tfunds);

  
  $pname = $param{o} ;
  if ($pname =~ /sql/i) {
    $pname =~ s/sql/propno/i;
  } else {
    $pname .= ".propno";
  }
  open PNFILE,">$pname" or
	die "Unable to open $pname for writing\n";

  open PSFILE,">$param{o}.pstatus" or 
	die "Unable to open $param{o}.pstatus for writing\n";
  $app=0;

  while ($str = <PFILE> ){ 
    if ($str !~ /#/)  {
      chomp($str);
      while ($str =~ s/ //g) {;}
      while ($str =~ s/null//i) {;}
      if ($isArchiveTheory <= 0) {
        ($pno,$status,$ng,$hst,$xmm,$noao,$nrao,$rxte,$spitzer,$suzaku,$swift,$nustar) = split($delim,$str);
      } else {
        ($pno,$status,$ng,$funds,$noao) = split($delim,$str);
      } 
      $rank = $ng;
      if ($status !~ /Y/i && $ng > 3.49)  {
        $rank = 3.49;
      }
      if ($status =~ /y/i) {
         $status= "APPROVED";
         print PNFILE qq("$pno",\n);
      } else {
         $status= "REJECTED";
      }
      if ($rank > 3.49) {
        $app++;
      }
      else {
        $funds = 0;
      }
      
      print PSFILE qq(exec peer_update_status "$pno","$status"\ngo\n);
      print OFILE qq(exec proposal_update_rank "$pno",$ng,$rank\ngo\n);

      if ($ptype{$pno} =~ /ARC/i || $ptype{$pno} =~ /THE/i) { 
        $tfunds += $funds;
        print OFILE qq(exec update_recommended_funds "$pno",$funds\ngo\n);
      }
      if ($pjoint{$pno} =~ /HST/i) {
        if (!$hst) { $hst=0};
        if ($rank < $PASS_GRADE) { $hst=0;}
        print OFILE qq(exec proposal_update_joint_hst "$pno",$hst\ngo\n);
        $thst+= $hst;
      }
      if ($pjoint{$pno} =~ /XMM/i) {
        if (!$xmm) { $xmm=0};
        if ($rank < $PASS_GRADE) { $xmm=0;}
        print OFILE qq(exec proposal_update_joint_xmm "$pno",$xmm\ngo\n);
	$txmm+= $xmm;
      }
      if ($pjoint{$pno} =~ /Spitzer/i) {
        if (!$spitzer) { $spitzer=0};
        if ($rank < $PASS_GRADE) { $spitzer=0;}
        print OFILE qq(exec proposal_update_joint_spitzer "$pno",$spitzer\ngo\n);
	$tspitzer+= $spitzer;
      }
      if ($pjoint{$pno} =~ /NOAO/i) {
        if (!$noao) { $noao=0};
        if ($rank < $PASS_GRADE) { $noao=0;}
        print OFILE qq(exec proposal_update_joint_noao "$pno",$noao\ngo\n);
	$tnoao+= $noao;
      }
      if ($pjoint{$pno} =~ /NRAO/i) {
        if (!$nrao) { $nrao=0};
        if ($rank < $PASS_GRADE) { $nrao=0;}
        print OFILE qq(exec proposal_update_joint_nrao "$pno",$nrao\ngo\n);
	$tnrao+=$nrao;
      }
      if ($pjoint{$pno} =~ /Suzaku/i) {
        if (!$suzaku) { $suzaku=0};
        if ($rank < $PASS_GRADE) { $suzaku=0;}
        print OFILE qq(exec proposal_update_joint_suzaku "$pno",$suzaku\ngo\n);
	$tsuzaku+=$suzaku;
      }
      if ($pjoint{$pno} =~ /Swift/i) {
        if (!$swift) { $swift=0};
        if ($rank < $PASS_GRADE) { $swift=0;}
        print OFILE qq(exec proposal_update_joint_swift "$pno",$swift\ngo\n);
	$tswift+=$swift;
      }
      if ($pjoint{$pno} =~ /Nustar/i) {
        if (!$nustar) { $nustar=0};
        if ($rank < $PASS_GRADE) { $nustar=0;}
        print OFILE qq(exec proposal_update_joint_nustar "$pno",$nustar\ngo\n);
	$tnustar+=$nustar;
      }
      
    }
  }
  close PSFILE;
  chmod (0660,"$param{o}.pstatus");
  close PNFILE;
  chmod (0660,"$pname");
  print STDERR "#approved proposals= $app\n";
  print STDERR "Total joint: hst=$thst  xmm=$txmm  spitzer=$tspitzer  noao=$tnoao  nrao=$tnrao  suzaku=$tsuzaku  swift=$tswift  nustar=$tnustar\n";
  print STDERR "Total recommended funds: $tfunds\n";
}
  

sub do_target
{
  my($str,@arr);
  my($pno,$tstatus,$targid,$tno,$app_n,$app_n1,$app_n2,$prob);
  my($status,$totalT);

  while ($str = <PFILE> ){ 
    if ($str !~ /#/)  {
      chomp($str);
      while ($str =~ s/ //g) {;}
      while ($str =~ s/null//i) {;}
      ($pno,$tstatus,$targid,$tno,$app_n,$app_n1,$app_n2,$prob) = split($delim,$str);
#if ($app_n > 0) {
#print STDERR "$pno,$tno,$app_n\n";
#}
      if ($tno != $targnums{$targid} ) {
         print STDERR "Target: Target number is different for $pno, $targid, $targnums{$targid} -> $tno\n";
         $tno = $targnums{$targid};
      }
      #if ($tno != $targnums{$targid} && $tstatus=~ /y/i) {
         #print STDERR "Target: Target number is different for $pno, $targid, $targnums{$targid} -> $tno\n";
         #$tno = $targnums{$targid};
      #}
      if ($tstatus =~ /y/i ) {
        $status=qq("accepted");
	$totalT = $app_n + $app_n1 + $app_n2;
      } else {
        $status = "null";
        $totalT = 0;
        $app_n = 0;
        $app_n1 = 0;
        $app_n2 = 0;
      }
      print OFILE qq(exec target_update_status_apptime "$pno",$tno,$status,$totalT,$app_n1,$app_n2\ngo\n);
    }
    if (length($prob) > 0) {
      print OFILE qq(exec too_update_probability "$pno",$tno,$prob\ngo\n);
    }
  }
}


sub do_grid
{
  my($str,@arr);
  my($pno,$tstatus,$targid,$tno,$app_n,$app_n1,$app_n2);
  my($tot0,$tot1,$tot2,$ng)  ;

  $tot0=$tot1=$tot2=$ng= 0 ;
  while ($str = <PFILE> ){ 
    if ($str !~ /#/)  {
      chomp($str);
      while ($str =~ s/ //g) {;}
      while ($str =~ s/null//i) {;}
      ($pno,$tstatus,$targid,$tno,$app_n,$app_n1,$app_n2) = split($delim,$str);
      if ($tstatus !~ /y/i) {
        $app_n = $app_n1 = $app_n2 = 0;
      } else {
        $ng += 1;
        $tot0 += $app_n;
        $tot1 += $app_n1;
        $tot2 += $app_n2;
      }
      if ($tno != $targnums{$targid}) {
         print STDERR "Grid: Target number is different for $pno, $targid, $targnums{$targid} -> $tno\n";
         $tno = $targnums{$targid};
      }
      print OFILE qq(exec target_update_appr_nbr_grid "$pno",$tno,$app_n,$app_n1,$app_n2\ngo\n);
    }
  }

  print STDERR "Total Grid $ng :  c0=$tot0   c1=$tot1   c2=$tot2\n";
}

sub do_observation
{
  my($str,@arr);
  my($pno,$tstatus,$targid,$tno,$ordr,$app_n,$charge_ao);
  my($status);
  my($toocnt) = 0;
  my($moncnt) = 0;
  my($xcnt) = 0;

  while ($str = <PFILE> ){ 
    if ($str !~ /#/)  {
      chomp($str);
      while ($str =~ s/ //g) {;}
      while ($str =~ s/null//i) {;}
      ($pno,$tstatus,$targid,$tno,$ordr,$app_n,$charge_ao) = split($delim,$str);
      $charge_ao += $param{a};
      if ($tstatus=~  /y/i && $app_n > 0) {
        $xcnt += 1;
        $status=qq("accepted");
        if ($ptype{$pno} =~ /TOO/i  && $ordr ==1) {
           $toocnt += 1;
        } elsif ($ptype{$pno} !~ /TOO/i  && $ordr ==1) {
           $moncnt += 1;
        }
 
      } else {
        $status = "null";
        $app_n = 0;
      }
      if ($tno != $targnums{$targid}) {
         print STDERR "Monitor: Target number is different for $pno, $targid, $targnums{$targid} -> $tno\n";
         $tno = $targnums{$targid};
      }
      if ($ptype{$pno} =~ /TOO/i) {
        $ordr -= 1;
      }
      if ($ordr == 0 && $app_n > 0) {
         print OFILE qq(exec too_update_initial_time "$pno",$tno,$app_n\ngo\n);
      } elsif ($ordr > 0) {
        print OFILE qq(exec observation_update_status "$pno",$tno,$ordr,$status,"$charge_ao",$app_n\ngo\n);
      }
    }
  }
  print STDERR "OBS: $xcnt\n";
  print STDERR "TOO: $toocnt\n";
  print STDERR "MONITOR: $moncnt\n";
}
sub do_alternate
{
  my($str,@arr);
  my($alt_id,$app_cnt);

  while ($str = <PFILE> ){ 
    if ($str !~ /#/)  {
      chomp($str);
      while ($str =~ s/ //g) {;}
      while ($str =~ s/null//i) {;}
      ($alt_id,$app_cnt) = split($delim,$str);
      print OFILE qq(exec update_approved_alternate_cnt $alt_id,$app_cnt\n);
      print OFILE qq(go\n);
   
    }
  }
}

# ----------------------------------------------------------
# parse input parameters
# ----------------------------------------------------------
sub parse_input
{

  my($pwd,$ii);
  my $err = 0;

  %param = (
             U=> undef,
             S=> "$ENV{DB_PROP_SQLSRV}",
             a=> "$ENV{ASCDS_PROP_AO}",
             i=> undef,
             o=> undef,
             t=> undef
              
        );

  GetOptions( \%param,
          "U=s",
          "S=s",
          "a=s",
          "i=s",
          "t=s",
          "o=s",
          "q=s",
          "h"
        ) or exit (1);

  if (!$param{h} ) {
    while ( my ( $par, $val ) = each ( %param ) ) {
      next if defined $val && $val ne "";
      warn("parameter `$par' not set\n");
      $err++;
    }
  }
  else {
    &usage;
    exit(0);
  }

  if ($err) {
    &usage;
    exit(1);
  }

  if(!$param{q}) {
    use Term::ReadKey;
     {
       ReadMode 'noecho';
       print "Enter password for $param{U} on $param{S}: ";
       $pwd = ReadLine( 0 );
       chomp $pwd;
       ReadMode 'restore';
       print "\n";
     }
  }
  else {
    open PFILE,"< $param{q}";
    while ( <PFILE> ){
      chomp;
      $pwd = $_;
    }
  }
  $param{q} = $pwd;
  
}

sub usage
{
    print STDERR "\nUSAGE: create_peer_results_sql.pl -Uiot [ -Sa]\n";
    print STDERR "       -U database user name\n";
    print STDERR "       -a AO, default ASCDS_PROP_AO \n";
    print STDERR "       -i input file \n";
    print STDERR "       -o output filename \n";
    print STDERR "       -t type of file:  prop,archive,target,grid,observation,alt\n";
    print STDERR "       -S database server, default is ASCDS_PROP_SQLSRV\n";
}


