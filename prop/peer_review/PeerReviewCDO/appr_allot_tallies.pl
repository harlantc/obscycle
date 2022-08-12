#!/usr/bin/perl

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION %totals
    $pnt_cur  $pnt_cur_1  $pnt_cur_2  $hst_cur  $xmm_cur  $noao_cur  
	$nrao_cur  $rxte_cur  $spitzer_cur  $suzaku_cur $swift_cur $nustar_cur 
	$rc_score_cur  $rc_score_cur_1 $rc_score_cur_2
    $hel_cur  $hel_cur_1 $hel_cur_2
    $vf_cur  $f_cur  $s_cur  $vs_cur
	$req_time  $req_time_1  $req_time_2  $time_cur 
	$time_cur_1  $time_cur_2  $targ_cur  $targ_cur_1  $targ_cur_2
        $arc_cur $noao_arc $the_cur $req_time_notoo  $req_time_notoo_1  $req_time_notoo_2
       ) ;
#$VERSION = '$Id: stats.pl,v 1.9 2011/06/10 18:22:22 wink Exp $';
$VERSION = '$Id: stats.pl,v 1.n 2021/06/28 ASCDS Exp $';

{
  use Getopt::Long;
  parse_opts();
  my $got99 =0;
  
  if ($param{version}) {
    print $VERSION, "\n";
    exit( 0 );
  }
  
  if ($param{help}) {
    usage(0);
  }
  
  %totals = (hst => 0,
		noao => 0,
		xmm => 0,
		nrao => 0,
		rxte => 0,
		spitzer =>0,
		suzaku => 0,
		swift => 0,
		nustar => 0,
		req_time_notoo => 0,
		req_time => 0,
		prop_time => 0,
		arc => 0,
		the => 0,
		vf => 0,
		f => 0,
		s => 0,
		vs => 0,
		rc_score => 0,
        hel => 0,
		targs => 0,
		pnt => 0,);

  open (OUTSUM, ">StatsCycle$param{cycle}\_summary.txt") ||
    die "Sorry can't open StatsCycle$param{cycle}\_summary.txt: $!\n";

  open (OUT, ">StatsCycle$param{cycle}.txt") ||
    die "Sorry can't open StatsCycle$param{cycle}.txt: $!\n";
  print "Output file: StatsCycle$param{cycle}.txt\n";
  print "             StatsCycle$param{cycle}+1.txt\n";
  print "             StatsCycle$param{cycle}+2.txt\n";
  print "             StatsCycle$param{cycle}\_summary.txt\n";
  print OUT "Panel\t";
  print OUT "HST\taHST\t" if $param{hst};
  print OUT "XMM\taXMM\t" if $param{xmm};
  print OUT "NOAO\taNOAO\t" if $param{noao};
  print OUT "NRAO\taNRAO\t" if $param{nrao};
  print OUT "RXTE\taRXTE\t" if $param{rxte};
  print OUT "SPTZR\taSPTZR\t" if $param{spitzer};
  print OUT "SUZAKU\taSUZAKU\t" if $param{suzaku};
  print OUT "SWIFT\taSWIFT\t" if $param{swift};
  print OUT "NUSTAR\taNUSTAR\t" if $param{nustar};
  print OUT "VF\taVF\tF\taF\tS\taS\tVS\taVS\t";
  print OUT "RC\taRC\tHEL\taHEL\t";
  print OUT "Tm-TOO\tTime\tTime+Tax+Prob\taTime\tArchive\taArc\tTheory\taTheory\t# Targ\t# Pnt\n";

  printf OUTSUM "Panel\t ReqTime\tRunReqTime\tTime-TOO\tAppTime\tRunAppTime\n";
  
  open (OUT1, ">StatsCycle$param{cycle}+1.txt") ||
    die "Sorry, can't open StatsCycle$param{cycle}+1.txt: $!\n";
  print OUT1 "Panel\tRC\taRC\tHEL\taHEL\t";
  print OUT1 "Time-TOO\tTime\tTime+Tax\taTime\t# Targ\t# Pnt\n";
  open (OUT2, ">StatsCycle$param{cycle}+2.txt") ||
    die "Sorry, can't open StatsCycle$param{cycle}+2.txt: $!\n";
  print OUT2 "Panel\tRC\taRC\tHEL\taHEL\t";
  print OUT2 "Time-TOO\tTime\tTime+Tax\taTime\t# Targ\t# Pnt\n";

  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  my $dbh1 = DBI->connect($dsn1, "", "");
  
  # Database connection 2
  my $dsn2 = "dbi:Pg:dbname=$param{U}";
  my $dbh2 = DBI->connect($dsn2, "", "");
  
  my $get_panels = $dbh1->prepare(qq(select distinct panel_id from proposal 
		where panel_id != 97 and panel_id !=98 order by panel_id));

  my $get_topical_allot_tots = $dbh2->prepare(qq(select 
	sum(rc_score),  sum(rc_score_1), sum(rc_score_2),
	sum(total_hel_time), sum(total_hel_time_1), sum(total_hel_time_2),
	sum(vf_too), sum(f_too), sum(s_too), sum(vs_too), 
	sum(hst), sum(xmm), sum(noao), sum(nrao), sum(rxte), sum(spitzer),
	sum(suzaku),sum(swift), sum(nustar),
	sum(total_exp_time), sum(total_exp_time_1), sum(total_exp_time_2), 
	sum(archive_allot), sum(theory_allot), 
	sum(lp), sum(lp_1), sum(lp_2), 
	sum(vlp), sum(vlp_1), sum(vlp_2),
    sum(hel_lp), sum(hel_lp_1), sum(hel_lp_2),
	sum(hel_vlp), sum(hel_vlp_1), sum(hel_vlp_2),
	sum(xvp), sum(xvp_1), sum(xvp_2) 
	from allotment where panel_id not in (97,98,99)));

  my $get_allot_tots = $dbh2->prepare(qq(select 
	sum(rc_score), sum(rc_score_1),sum(rc_score_2),
	sum(total_hel_time), sum(total_hel_time_1), sum(total_hel_time_2),
	sum(vf_too), sum(f_too), sum(s_too), sum(vs_too), 
	sum(hst), sum(xmm), sum(noao), sum(nrao), sum(rxte), sum(spitzer),
	sum(suzaku),sum(swift), sum(nustar),
	sum(total_exp_time), sum(total_exp_time_1), sum(total_exp_time_2), 
	sum(archive_allot), sum(theory_allot), 
	sum(lp), sum(lp_1), sum(lp_2),
	sum(vlp), sum(vlp_1), sum(vlp_2),
    sum(hel_lp), sum(hel_lp_1), sum(hel_lp_2),
	sum(hel_vlp), sum(hel_vlp_1), sum(hel_vlp_2),
	sum(xvp), sum(xvp_1), sum(xvp_2) 
	from allotment where panel_id not in (97,98)));

  my $get_allot = $dbh2->prepare(qq(select rc_score, rc_score_1, rc_score_2,
    total_hel_time, total_hel_time_1, total_hel_time_2,
	vf_too, f_too, s_too, vs_too, 
	hst, xmm, noao, nrao, rxte, spitzer,
	suzaku,swift, nustar,
	total_exp_time, total_exp_time_1, total_exp_time_2, 
	archive_allot, theory_allot, 
	lp, lp_1, lp_2, vlp, vlp_1,vlp_2,
	hel_lp, hel_lp_1, hel_lp_2, hel_vlp, hel_vlp_1, hel_vlp_2,
	xvp, xvp_1, xvp_2
	from allotment where panel_id = ?));

  my $get_obs = $dbh2->prepare(qq(select sum(num_obs_app), sum(num_obs_app_1),
	sum(num_obs_app_2), sum(hst_app), 
	sum(xmm_app), sum(noao_app), sum(nrao_app), 
	sum (rxte_app), sum(spitzer_app), 
	sum(suzaku_app), sum(swift_app),sum(nustar_app),
	sum(rc_score_app), sum(rc_score_app_1), sum(rc_score_app_2),
	sum(total_app_hel), sum(total_app_hel_1), sum(total_app_hel_2),
	sum(vf_app), sum(f_app), sum(s_app), 
	sum(vs_app), sum(total_app_time),
	sum(total_app_time_1), sum(total_app_time_2),
	sum(prop_app_time), sum(prop_app_time_1), 
	sum(prop_app_time_2), sum(num_targ_app),
	sum(num_targ_app_1), sum(num_targ_app_2) 
	from proposal where panel_id = ? and 
	prop_status = 'Y' and type not in ('ARCHIVE', 'THEORY')));

  my $get_non_too_time = $dbh2->prepare(qq(select sum(total_app_time),
	sum(total_app_time_1), sum(total_app_time_2)
	from proposal where panel_id = ? and prop_status = 'Y'
	and type not in ('TOO','ARCHIVE', 'THEORY')));

  my $get_archive = $dbh2->prepare(qq(select sum(total_app_time),
	sum(noao_app) from proposal 
	where panel_id = ? and prop_status = 'Y'
	and type = 'ARCHIVE'));

  my $get_theory = $dbh2->prepare(qq(select sum(total_app_time) 
	from proposal 
	where panel_id = ? and prop_status = 'Y'
	and type = 'THEORY'));

  my $get_BPP_time = $dbh2->prepare(qq(select sum(total_app_time), 
	sum(total_app_time_1), sum(total_app_time_2),
	 sum(total_app_hel), sum(total_app_hel_1), sum(total_app_hel_2),
	 sum(num_obs_app),
	sum(num_obs_app_1), sum(num_obs_app_2), 
	sum(prop_app_time),
	sum(prop_app_time_1),
	sum(prop_app_time_2), sum(num_targ_app),
	sum(num_targ_app_1), sum(num_targ_app_2)
	from proposal where panel_id = 99 and 
	prop_status = 'Y' and type not in 
	('ARCHIVE', 'THEORY') and 
	big_proj = ?));

  $get_allot_tots->execute();
  my( $atot_tc, $atot_tc_1, $atot_tc_2,
    $atot_hel, $atot_hel_1, $atot_hel_2,
	$atot_vf_too, $atot_f_too, $atot_s_too, $atot_vs_too, 
	$atot_hst, $atot_xmm, $atot_noao, $atot_nrao, $atot_rxte, $atot_spitzer,
	$atot_suzaku,$atot_swift, $atot_nustar,
	$atot_total_exp_time, $atot_total_exp_time_1, $atot_total_exp_time_2, 
	$atot_archive_allot, $atot_theory_allot, 
	$atot_lp, $atot_lp_1, $atot_lp_2, 
	$atot_vlp, $atot_vlp_1, $atot_vlp_2,
    $atot_hel_lp, $atot_hel_lp_1, $atot_hel_lp_2,
    $atot_hel_vlp, $atot_hel_vlp_1, $atot_hel_vlp_2,
      $atot_xvp, $atot_xvp_1,
	$atot_xvp_2) = $get_allot_tots->fetchrow_array;

  $get_topical_allot_tots->execute();
  my( $tatot_tc, $tatot_tc_1, $tatot_tc_2,
    $tatot_hel, $tatot_hel_1, $tatot_hel_2,
	$tatot_vf_too, $tatot_f_too, $tatot_s_too, $tatot_vs_too, 
	$tatot_hst, $tatot_xmm, $tatot_noao, $tatot_nrao, $tatot_rxte, $tatot_spitzer,
	$tatot_suzaku,$tatot_swift, $tatot_nustar,
	$tatot_total_exp_time, $tatot_total_exp_time_1, $tatot_total_exp_time_2, 
	$tatot_archive_allot, $tatot_theory_allot, 
	$tatot_lp, $tatot_lp_1, $tatot_lp_2, 
	$tatot_vlp, $tatot_vlp_1, $tatot_vlp_2,
    $tatot_hel_lp, $tatot_hel_lp_1, $tatot_hel_lp_2,
    $tatot_hel_vlp, $tatot_hel_vlp_1, $tatot_hel_vlp_2,
      $tatot_xvp, $tatot_xvp_1,
	$tatot_xvp_2) = $get_topical_allot_tots->fetchrow_array;

   #print "HST = $atot_hst\n";

  $get_panels->execute();
  while (my ($panel_id) = $get_panels->fetchrow_array) {
    $get_allot->execute($panel_id);
    my ($tc_allot, $tc_allot_1, $tc_allot_2,
    $hel_allot, $hel_allot_1, $hel_allot_2,
	$vf_allot, $f_allot, $s_allot, $vs_allot, 
	$hst_allot, $xmm_allot, $noao_allot, $nrao_allot, 
	$rxte_allot, $spitzer_allot, $suzaku_allot,$swift_allot, $nustar_allot,
	$time_allot, $time_allot_1, $time_allot_2, $arc_allot, $the_allot,
	$lp_allot, $lp_allot_1, $lp_allot_2, 
	$vlp_allot, $vlp_allot_1, $vlp_allot_2,
    $hel_lp_allot, $hel_lp_allot_1, $hel_lp_allot_2,
    $hel_vlp_allot, $hel_vlp_allot_1, $hel_vlp_allot_2,
        $xvp_allot, $xvp_allot_1,
        $xvp_allot_2) = $get_allot->fetchrow_array;
    $get_allot->finish;

    $get_obs->execute($panel_id);
    ($pnt_cur, $pnt_cur_1, $pnt_cur_2, $hst_cur, $xmm_cur, $noao_cur, 
	$nrao_cur, $rxte_cur, $spitzer_cur, $suzaku_cur,$swift_cur,$nustar_cur,
	$rc_score_cur, $rc_score_cur_1, $rc_score_cur_2,
    $hel_cur, $hel_cur_1, $hel_cur_2, $vf_cur, $f_cur,
	$s_cur, $vs_cur, $req_time, $req_time_1, $req_time_2, $time_cur,
	$time_cur_1, $time_cur_2, $targ_cur, $targ_cur_1, $targ_cur_2
       ) = $get_obs->fetchrow_array;
    $get_obs->finish;
    $hst_cur = 0 if !$hst_cur;
    $xmm_cur = 0 if !$xmm_cur;
    $noao_cur = 0 if !$noao_cur;
    $nrao_cur = 0 if !$nrao_cur;
    $rxte_cur = 0 if !$rxte_cur;
    $spitzer_cur = 0 if !$spitzer_cur;
    $suzaku_cur = 0 if !$suzaku_cur;
    $swift_cur = 0 if !$swift_cur;
    $nustar_cur = 0 if !$nustar_cur;
    $rc_score_cur = 0 if !$rc_score_cur;
    $rc_score_cur_1 = 0 if !$rc_score_cur_1;
    $rc_score_cur_2 = 0 if !$rc_score_cur_2;
    $hel_cur = 0 if !$hel_cur;
    $hel_cur_1 = 0 if !$hel_cur_1;
    $hel_cur_2 = 0 if !$hel_cur_2;
    $vf_cur = 0 if !$vf_cur;
    $f_cur = 0 if !$f_cur;
    $s_cur = 0 if !$s_cur;
    $vs_cur = 0 if !$vs_cur;

    $rc_score_cur = sprintf "%.01f", $rc_score_cur;

    $time_cur = sprintf "%.02f", $time_cur;
    $time_cur_1 = sprintf "%.02f", $time_cur_1;
    $time_cur_2 = sprintf "%.02f", $time_cur_2;

	$get_non_too_time->execute($panel_id);
	($req_time_notoo, $req_time_notoo_1, 
	$req_time_notoo_2) = $get_non_too_time->fetchrow_array;
	$get_non_too_time->finish;

    $get_archive->execute($panel_id);
    ($arc_cur,$noao_arc) = $get_archive->fetchrow_array;
    if ($noao_arc > 0) {
      $noao_cur += $noao_arc;
    }
    $get_archive->finish;
    
    $get_theory->execute($panel_id);
    ($the_cur) = $get_theory->fetchrow_array;
    $get_theory->finish;
    
    #if ($panel_id <= 20 or $panel_id == 99) 
    if ($panel_id <= 20 ) {
      sum_totals();
      #screen display
      printf OUTSUM "%5d\t%9.2f\t%9.2f\t%9.2f\t%9.2f\t%9.2f\n",$panel_id,$req_time,$totals{req_time},$totals{req_time_notoo},$time_cur,$totals{prop_time};
    }

    my ($lp_req_time, $lp_req_time_1, $lp_req_time_2, $lp_pnt_cur,
        $lp_tot_hel, $lp_tot_hel_1, $lp_tot_hel_2,
	$lp_pnt_cur_1, $lp_pnt_cur_2, $lp_time_cur, $lp_time_cur_1, 
	$lp_time_cur_2, $hel_lp_time_cur, $hel_lp_time_cur_1, $hel_lp_time_cur_2,
        $lp_targ_cur, $lp_targ_cur_1, $lp_targ_cur_2);

    my ($vlp_req_time, $vlp_req_time_1, $vlp_req_time_2, $vlp_pnt_cur,
        $vlp_tot_hel, $vlp_tot_hel_1, $vlp_tot_hel_2,
	$vlp_pnt_cur_1, $vlp_pnt_cur_2, $vlp_time_cur, $vlp_time_cur_1, 
	$vlp_time_cur_2, $hel_vlp_time_cur, $hel_vlp_time_cur_1, $hel_vlp_time_cur_2,
        $vlp_targ_cur, $vlp_targ_cur_1, $vlp_targ_cur_2);

    my ($xvp_req_time, $xvp_req_time_1, $xvp_req_time_2, $xvp_pnt_cur, 
	$xvp_pnt_cur_1, $xvp_pnt_cur_2, $xvp_time_cur, $xvp_time_cur_1, 
	$xvp_time_cur_2, $xvp_targ_cur, $xvp_targ_cur_1, $xvp_targ_cur_2);

    if ($panel_id == 99) {
      $got99=1;
      $get_BPP_time->execute('LP');
      ($lp_req_time, $lp_req_time_1, $lp_req_time_2,
          $lp_tot_hel, $lp_tot_hel_1, $lp_tot_hel_2, $lp_pnt_cur,
       $lp_pnt_cur_1, $lp_pnt_cur_2, $lp_time_cur, $lp_time_cur_1, 
       $lp_time_cur_2, $lp_targ_cur, $lp_targ_cur_1, $lp_targ_cur_2
      ) = $get_BPP_time->fetchrow_array;
      $get_BPP_time->finish;
      $lp_time_cur = sprintf "%.02f", $lp_time_cur;
      $lp_time_cur_1 = sprintf "%.02f", $lp_time_cur_1;
      $lp_time_cur_2 = sprintf "%.02f", $lp_time_cur_2;
      $lp_tot_hel = sprintf "%.02f", $lp_tot_hel;
      $lp_tot_hel_1 = sprintf "%.02f", $lp_tot_hel_1;
      $lp_tot_hel_2 = sprintf "%.02f", $lp_tot_hel_2;

      $get_BPP_time->execute('VLP');
      ($vlp_req_time, $vlp_req_time_1, $vlp_req_time_2,
          $vlp_tot_hel, $vlp_tot_hel_1, $vlp_tot_hel_2, $vlp_pnt_cur,
       $vlp_pnt_cur_1, $vlp_pnt_cur_2, $vlp_time_cur, $vlp_time_cur_1, 
       $vlp_time_cur_2, $vlp_targ_cur, $vlp_targ_cur_1, $vlp_targ_cur_2
      ) = $get_BPP_time->fetchrow_array;
      $get_BPP_time->finish;
      $vlp_time_cur = sprintf "%.02f", $vlp_time_cur;
      $vlp_time_cur_1 = sprintf "%.02f", $vlp_time_cur_1;
      $vlp_time_cur_2 = sprintf "%.02f", $vlp_time_cur_2;
     $vlp_tot_hel = sprintf "%.02f", $vlp_tot_hel;
     $vlp_tot_hel_1 = sprintf "%.02f", $vlp_tot_hel_1;
     $vlp_tot_hel_2 = sprintf "%.02f", $vlp_tot_hel_2;

      $get_BPP_time->execute('XVP');
      ($xvp_req_time, $xvp_req_time_1, $xvp_req_time_2, $xvp_pnt_cur, 
       $xvp_pnt_cur_1, $xvp_pnt_cur_2, $xvp_time_cur, $xvp_time_cur_1, 
       $xvp_time_cur_2, $xvp_targ_cur, $xvp_targ_cur_1, $xvp_targ_cur_2
      ) = $get_BPP_time->fetchrow_array;
      $get_BPP_time->finish;

      $xvp_time_cur = sprintf "%.02f", $xvp_time_cur;
      $xvp_time_cur_1 = sprintf "%.02f", $xvp_time_cur_1;
      $xvp_time_cur_2 = sprintf "%.02f", $xvp_time_cur_2;
    }
    
      if ($panel_id <= 20) {
	print OUT "$panel_id \t";
	print OUT "$hst_cur\t$hst_allot\t" if $param{hst};
	print OUT "$xmm_cur\t$xmm_allot\t" if $param{xmm};
	print OUT "$noao_cur\t$noao_allot\t" if $param{noao};
	print OUT "$nrao_cur\t$nrao_allot\t" if $param{nrao};
	print OUT "$rxte_cur\t$rxte_allot\t" if $param{rxte};
	print OUT "$spitzer_cur\t$spitzer_allot\t" if $param{spitzer};
	print OUT "$suzaku_cur\t$suzaku_allot\t" if $param{suzaku};
	print OUT "$swift_cur\t$swift_allot\t" if $param{swift};
	print OUT "$nustar_cur\t$nustar_allot\t" if $param{nustar};

	print OUT "$vf_cur\t$vf_allot\t$f_cur\t$f_allot\t$s_cur\t$s_allot\t$vs_cur\t$vs_allot\t";
	print OUT "$rc_score_cur\t$tc_allot\t$hel_cur\t$hel_allot\t";
        print OUT "$req_time_notoo\t$req_time\t$time_cur\t$time_allot\t$arc_cur\t$arc_allot\t$the_cur\t$the_allot\t$targ_cur\t$pnt_cur\n";

	print OUT1 "$panel_id\t$rc_score_cur_1\t$tc_allot_1\t$hel_cur_1\t$hel_allot_1\t";
	print OUT1 "$req_time_notoo_1\t$req_time_1\t$time_cur_1\t$time_allot_1\t$targ_cur_1\t$pnt_cur_1\n";
	print OUT2 "$panel_id\t$rc_score_cur_2\t$tc_allot_2\t$hel_cur_2\t$hel_allot_2\t";
	print OUT2 "$req_time_notoo_2\t$req_time_2\t$time_cur_2\t$time_allot_2\t$targ_cur_2\t$pnt_cur_2\n";

      }
      elsif ($panel_id == 99) {
  print OUT "Topical\t";
  print OUT "$totals{hst}\t$tatot_hst\t" if $param{hst};
  print OUT "$totals{xmm}\t$tatot_xmm\t" if $param{xmm};
  print OUT "$totals{noao}\t$tatot_noao\t" if $param{noao};
  print OUT "$totals{nrao}\t$tatot_nrao\t" if $param{nrao};
  print OUT "$totals{rxte}\t$tatot_rxte\t" if $param{rxte};
  print OUT "$totals{spitzer}\t$tatot_spitzer\t" if $param{spitzer};
  print OUT "$totals{suzaku}\t$tatot_suzaku\t" if $param{suzaku};
  print OUT "$totals{swift}\t$tatot_swift\t" if $param{swift};
  print OUT "$totals{nustar}\t$tatot_nustar\t" if $param{nustar};
  print OUT "$totals{vf}\t$tatot_vf_too\t$totals{f}\t$tatot_f_too\t$totals{s}\t$tatot_s_too\t$totals{vs}\t$tatot_vs_too\t";
  print OUT "$totals{rc_score}\t$tatot_tc\t";
  print OUT "$totals{hel}\t$tatot_hel\t";
  print OUT "$totals{req_time_notoo}\t$totals{req_time}\t$totals{prop_time}\t$tatot_total_exp_time\t$totals{arc}\t$tatot_archive_allot\t$totals{the}\t$tatot_theory_allot\t$totals{targs}\t$totals{pnt}\n\n";


	print OUT "$panel_id LP\t";
	print OUT "$hst_cur\t$hst_allot\t" if $param{hst};
	print OUT "$xmm_cur\t$xmm_allot\t" if $param{xmm};
	print OUT "$noao_cur\t$noao_allot\t" if $param{noao};
	print OUT "$nrao_cur\t$nrao_allot\t" if $param{nrao};
	print OUT "$rxte_cur\t$rxte_allot\t" if $param{rxte};
	print OUT "$spitzer_cur\t$spitzer_allot\t" if $param{spitzer};
	print OUT "$suzaku_cur\t$suzaku_allot\t" if $param{suzaku};
	print OUT "$swift_cur\t$swift_allot\t" if $param{swift};
	print OUT "$nustar_cur\t$nustar_allot\t" if $param{nustar};
	print OUT "$vf_cur\t$vf_allot\t$f_cur\t$f_allot\t$s_cur\t$s_allot\t$vs_cur\t$vs_allot\t";
	print OUT "$rc_score_cur\t$tc_allot\t$lp_tot_hel\t$hel_lp_allot\t";
        print OUT "$lp_req_time\t$lp_time_cur\t$lp_allot\t\t\t\t\t$lp_targ_cur\t$lp_pnt_cur\n";
        #now for vlp
	print OUT "$panel_id VLP\t";
	print OUT "\t\t" if $param{hst};
	print OUT "\t\t" if $param{xmm};
	print OUT "\t\t" if $param{noao};
	print OUT "\t\t" if $param{nrao};
	print OUT "\t\t" if $param{rxte};
	print OUT "\t\t" if $param{spitzer};
	print OUT "\t\t" if $param{suzaku};
	print OUT "\t\t" if $param{swift};
	print OUT "\t\t" if $param{nustar};
        print OUT "\t\t\t\t\t\t\t\t";
        print OUT "\t\t$vlp_tot_hel\t$hel_vlp_allot\t";
        print OUT "$vlp_req_time\t$vlp_time_cur\t$vlp_allot\t\t\t\t\t$vlp_targ_cur\t$vlp_pnt_cur\n";


	print OUT1 "$panel_id LP\t$rc_score_cur_1\t$tc_allot_1\t$lp_tot_hel_1\t$hel_lp_allot_1\t";
	print OUT1 "$lp_req_time_1\t$lp_time_cur_1\t$lp_allot_1\t$lp_targ_cur_1\t$lp_pnt_cur_1\n";
	print OUT1 "$panel_id VLP\t\t\t$vlp_tot_hel_1\t$hel_vlp_allot_1\t";
	print OUT1 "$vlp_req_time_1\t$vlp_time_cur_1\t$vlp_allot_1\t$vlp_targ_cur_1\t$vlp_pnt_cur_1\n";

	print OUT2 "$panel_id LP\t$rc_score_cur_2\t$tc_allot_2\t$lp_tot_hel_2\t$hel_lp_allot_2\t";
	print OUT2 "$lp_req_time_2\t$lp_time_cur_2\t$lp_allot_2\t$lp_targ_cur_2\t$lp_pnt_cur_2\n";
	print OUT2 "$panel_id VLP\t\t$vlp_tot_hel_2\t$hel_vlp_allot_2\t";
	print OUT2 "$vlp_req_time_2\t$vlp_time_cur_2\t$vlp_allot_2\t$vlp_targ_cur_2\t$vlp_pnt_cur_2\n";

      }
    }
  $get_panels->finish;

  sum_totals() if ($got99);
  
  print OUT "TOTAL\t";
  print OUT "$totals{hst}\t$atot_hst\t" if $param{hst};
  print OUT "$totals{xmm}\t$atot_xmm\t" if $param{xmm};
  print OUT "$totals{noao}\t$atot_noao\t" if $param{noao};
  print OUT "$totals{nrao}\t$atot_nrao\t" if $param{nrao};
  print OUT "$totals{rxte}\t$atot_rxte\t" if $param{rxte};
  print OUT "$totals{spitzer}\t$atot_spitzer\t" if $param{spitzer};
  print OUT "$totals{suzaku}\t$atot_suzaku\t" if $param{suzaku};
  print OUT "$totals{swift}\t$atot_swift\t" if $param{swift};
  print OUT "$totals{nustar}\t$atot_nustar\t" if $param{nustar};
  print OUT "$totals{vf}\t$atot_vf_too\t$totals{f}\t$atot_f_too\t$totals{s}\t$atot_s_too\t$totals{vs}\t$atot_vs_too\t";
  print OUT "$totals{rc_score}\t$atot_tc\t";
  print OUT "$totals{hel}\t$atot_hel\t";
  $atot_total_exp_time += $atot_lp + $atot_xvp;
  print OUT "$totals{req_time_notoo}\t$totals{req_time}\t$totals{prop_time}\t$atot_total_exp_time\t$totals{arc}\t$atot_archive_allot\t$totals{the}\t$atot_theory_allot\t$totals{targs}\t$totals{pnt}\n\n";
  close OUT;

  print OUT1 "TOTAL\t$totals{rc_score_1}\t$atot_tc_1\t$totals{hel_1}\t$atot_hel_1\t$totals{req_time_notoo_1}\t$totals{req_time_1}\t$totals{prop_time_1}\t$atot_total_exp_time_1\t$totals{targs_1}\t$totals{pnt_1}\n\n";
  print OUT2 "TOTAL\t$totals{rc_score_2}\t$atot_tc_2\t$totals{hel_2}\t$atot_hel_2\t$totals{req_time_notoo_2}\t$totals{req_time_2}\t$totals{prop_time_2}\t$atot_total_exp_time_2\t$totals{targs_2}\t$totals{pnt_2}\n\n";
}
sub sum_totals
{
      $totals{hst} += $hst_cur;
      $totals{xmm} += $xmm_cur;
      $totals{noao} += $noao_cur;
      $totals{nrao} += $nrao_cur;
      $totals{rxte} += $rxte_cur;
      $totals{spitzer} += $spitzer_cur;
      $totals{suzaku} += $suzaku_cur;
      $totals{swift} += $swift_cur;
      $totals{nustar} += $nustar_cur;
      $totals{vf} += $vf_cur;
      $totals{f} += $f_cur;
      $totals{'s'} += $s_cur;
      $totals{vs} += $vs_cur;
      $totals{rc_score} += $rc_score_cur;
      $totals{rc_score_1} += $rc_score_cur_1;
      $totals{rc_score_2} += $rc_score_cur_2;
      $totals{hel} += $hel_cur;
      $totals{hel_1} += $hel_cur_1;
      $totals{hel_2} += $hel_cur_2;
      $totals{req_time} += $req_time;
      $totals{req_time_1} += $req_time_1;
      $totals{req_time_2} += $req_time_2;
      $totals{prop_time} += $time_cur;
      $totals{prop_time_1} += $time_cur_1;
      $totals{prop_time_2} += $time_cur_2;
      $totals{arc} += $arc_cur;
      $totals{the} += $the_cur;
      $totals{targs} += $targ_cur;
      $totals{targs_1} += $targ_cur_1;
      $totals{targs_2} += $targ_cur_2;
      $totals{pnt} += $pnt_cur;
      $totals{pnt_1} += $pnt_cur_1;
      $totals{pnt_2} += $pnt_cur_2;
	  $totals{req_time_notoo} += $req_time_notoo;
	  $totals{req_time_notoo_1} += $req_time_notoo_1;
	  $totals{req_time_notoo_2} += $req_time_notoo_2;
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    cycle => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "cycle=i",
	      "hst",
	      "noao",
	      "xmm",
	      "nrao",
	      "rxte",
	      "spitzer",
	      "suzaku",
	      "swift",
	      "nustar",
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

stats.pl [options]

=head1 OPTIONS

B<stats.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to populate

=item B<-cycle>

cycle to prepare stats for

=item B<-hst>

flag indicating hst should be included in stats

=item B<-noao>

flag indicating noao should be included in stats

=item B<-xmm>

flag indicating xmm should be included in stats

=item B<-nrao>

flag indicating nrao should be included in stats

=item B<-rxte>

flag indicating rxte should be included in stats

=item B<-spitzer>

flag indicating spitzer should be included in stats

=item B<-suzaku>

flag indicating suzaku should be included in stats

=item B<-swift>

flag indicating swift should be included in stats

=item B<-nustar>

flag indicating nustar should be included in stats

=item B<-help>

displays documentation for B<appr_allot_tallies.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script prints the stats for all panels.

=head1 AUTHOR

Sherry L. Winkelman
