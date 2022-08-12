#!/home/ascds/DS.release/ots/bin/perl

#******************************************************************************
# pop_pr.pl
#
# Creates the sql to migrate all proposals in a panel to the GUI database. It
# can take a list of proposals to assign to a specific panel which is useful
# for building test databases. By default the script only uses proposals which
# have been approved or have yet to be reviewed, but there are flags to reduce
# this restriction. When it is time to create the final panels for Peer Review,
# the script will find all of the proposals for the panel that you are
# building.
#
# Note: This script uses Arcops perl modules to set the Sybase environment
#******************************************************************************

BEGIN
{
   $ENV{SYBASE} = "/soft/SYBASE16.0";
}

use strict;
use POSIX;
use DBI;
use vars qw($pwdProp %param $VERSION);
$VERSION = '$Id: pop_pr.pl,v 1.59 2012/02/14 14:10:22 wink Exp $';

{
  use Getopt::Long;
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
  
  
  my $propserver = "server=$param{SProposal}";
  my $propdb = "database=proposal";
  my $script_name = "scriptName=pop_pr.pl";
  $pwdProp=$param{q};
  
  # Database connection 1 (Proposal)
  my $dsnProp1 = "DBI:Sybase:$propserver;$script_name;$propdb";
  my $dbhProp1 = DBI->connect($dsnProp1, "$param{UProposal}", 
			      $pwdProp, {
					 PrintError => 0,
					 RaiseError => 1});
  # Database connection 2 (Target)
  my $dsnProp2 = "DBI:Sybase:$propserver;$script_name;$propdb";
  my $dbhProp2 = DBI->connect($dsnProp2, "$param{UProposal}", 
			      $pwdProp, {
					 PrintError => 0,
					 RaiseError => 1});
  # Database connection 3 (Others)
  my $dsnProp3 = "DBI:Sybase:$propserver;$script_name;$propdb";
  my $dbhProp3 = DBI->connect($dsnProp3, "$param{UProposal}", 
			      $pwdProp, {
					 PrintError => 0,
					 RaiseError => 1});
  
  my $prop_query = qq(select pan.panel_name, prop.proposal_number,
			prop.proposal_id, convert(char(35), pers.institution),
			pers.last, prop.title, prop.abstract, prop.type, 
			prop.category_descrip,
			prop.joint, j.hst_time, j.noao_time, j.xmm_time, 
			j.nrao_time, j.rxte_time, j.spitzer_time, 
                        j.suzaku_time, j.swift_time,j.nustar_time,
			total_time,
			prop.linked_propnum, num_targets from 
			proposal prop, axafusers..person_short pers, 
			panel pan, panel_proposal pp, joint j where 
			prop.piid = pers.pers_id and 
			prop.proposal_id = pp.proposal_id and 
			pp.panel_id = pan.panel_id and 
			prop.proposal_id *= j.proposal_id and 
                        prop.status = 'PROPOSED' and 
			pan.panel_name != 'LP' and
			prop.type not in ("CAL", "DDT") and ao_str = ?);

  my $gto_query = qq(select "97", prop.proposal_number,
			prop.proposal_id, convert(char(35), pers.institution),
			pers.last, prop.title, prop.abstract, prop.type, 
			prop.category_descrip,
			prop.joint, j.hst_time, j.noao_time, j.xmm_time, 
			j.nrao_time, j.rxte_time, j.spitzer_time, 
                        j.suzaku_time, j.swift_time,j.nustar_time,
			total_time,
			prop.linked_propnum, num_targets from 
			proposal prop, axafusers..person_short pers, joint j
			where
			prop.piid = pers.pers_id and 
			prop.type like "%GTO%" and
			prop.proposal_id *= j.proposal_id and 
                        prop.status = 'PROPOSED' and 
			ao_str = ?);

  my $bpp_query = qq(select "99" 'panel_name', prop.proposal_number,
			prop.proposal_id, convert(char(35), pers.institution),
			pers.last, prop.title, prop.abstract, prop.type, 
			prop.category_descrip,
			prop.joint, j.hst_time, j.noao_time, j.xmm_time, 
			j.nrao_time, j.rxte_time, j.spitzer_time, 
                        j.suzaku_time, j.swift_time,j.nustar_time,
			total_time,
			prop.linked_propnum, num_targets from 
			proposal prop, axafusers..person_short pers, joint j
 			where
			prop.piid = pers.pers_id and 
			prop.proposal_id *= j.proposal_id and 
                        prop.status = 'PROPOSED' and 
			(prop.type like "%LP%" or prop.type like "%XVP%")  and
			ao_str = ?);

  # if target.num_observations = 1, the target is a too
  # if target.num_obsobservations > 1, the target is a monitor
  my $targ_query = qq(select too.type, too.probability, too.followup, 
                      too.trigger_target,too.start,
		      t.targname, t.ss_object, i.instrument_name, 
		      t.targ_num, t.targid, t.time_critical, 
		      t.prop_exposure_time, t.num_observations,
                      t.est_time_cycle_n1, t.est_time_cycle_n2,
		      t.raster_scan, grid_name, max_radius, 
                      grid.num_pointings, t.monitor_flag, t.uninterrupt, t.multitelescope, 
                      t.group_obs, t.group_id, t.constr_in_remarks, 
                      g.grating_name, t.alternate_id, 
                      t.group_interval, t.ra, t.dec,
                      t.multitelescope_interval, t.remarks from 
                      too, target t, grating g, instrument i, grid where 
	      	      t.use_grating_id = g.grating_id and 
	       	      i.instrument_id = t.use_instrument_id and
		      t.targid *= too.targid  and t.targid *= grid.targid
                      and proposal_id = ?
			order by t.targ_num);

  # determine monitor on a proposal basis. 
  # If any target a monitor, then proposal is a monitor.
  my $is_mon_query = qq(select count(target.targid) from target 
                           where num_observations > 1
                           and proposal_id = ? );
  # determine grid on a proposal basis. 
  # If any target a grid, then proposal is a grid.
  my $is_grid_query = qq(select count(target.targid) from target,grid 
                           where target.targid = grid.targid
		           and proposal_id = ? 
                           and grid.num_pointings > 0);
  my $num_targs_query = qq(select count(*) from target where proposal_id = ? 
			   and prop_exposure_time > 0 and 
                           alternate_id = null);
  my $num_obs_query = qq(select count(*) from observation where targid = ?);
  my $alt_targ_query = qq(select alternate_id, group_name, requested_count
                            from alternate_target_group where 
                            proposal_id = ?);
# Need to adjust to new table
  my $too_query = qq(select ordr, obs_time, pre_min_lead, pre_max_lead from 
		     observation where targid = ?);
  my $rollreq_query = qq(select ordr, roll_constraint, roll_180, 
			   roll, roll_tolerance from rollreq where targid = ?);
  my $timereq_query = qq(select distinct ordr, time_constraint, 
			   convert(char(8), tstart, 112), 
			   convert(char(8), tstart, 108), 
			   convert(char(8), tstop, 112), 
			   convert(char(8), tstop, 108),
                           datediff(hour, tstart, tstop)/24.0 from timereq 
			   where targid = ?);
  my $phasereq_query = qq(select phase_period, phase_epoch, 
			    phase_start, phase_end, phase_start_margin, 
			    phase_end_margin, phase_constraint_flag from 
			    phasereq where targid = ?);
  
  my ($ao);
  if (!$param{list}) {
    if (!$param{ao}) {
      my $ao_query = $dbhProp1->prepare(qq(select current_ao_str 
					     from context));
      $ao_query->execute();
      ($ao) = $ao_query->fetchrow_array;
      $ao_query->finish;
    }
    else {
      $ao = $param{ao};
      if ($param{all}) {
	$prop_query =~ s/prop.status = 'PROPOSED' and//;
	$bpp_query =~ s/prop.status = 'PROPOSED' and//;
      }
      else {
	$prop_query =~ s/PROPOSED/APPROVED/;
	$bpp_query =~ s/PROPOSED/APPROVED/;
      }
    }
    
    if ($param{panel} !~ /BPP|XVP|GTO/) {
      $prop_query .= qq( and pan.panel_name = '$param{panel}');
    }
    else {
      $prop_query .= qq( and prop.type = 'XVP') if $param{panel} eq 'XVP';
      $prop_query = $gto_query if $param{panel} eq 'GTO';
    }
  } 
  else {
    open (IN, "$param{list}") || 
      die "Sorry, can't open $param{list}: $!\n";

    my $list;
    foreach my $prop_id (<IN>) {
      next if $prop_id =~ /#/;
      chomp $prop_id;
      $prop_id =~ s/[\/a-zA-z]//g;
      $prop_id =~ s/^\s+//;
      $prop_id =~ s/\s+$//;
      $list .= "'$prop_id', ";
    }
    close IN;
    $list =~ s/,\s+$//;

    #print "$list\n";

    my $panel = $param{panel};
    $panel = 97 if $param{panel} eq 'GTO';
    $panel = 98 if $param{panel} eq 'XVP';
    $panel = 99 if $param{panel} eq 'BPP';
    $prop_query = qq(select $panel, prop.proposal_number, prop.proposal_id, convert(char(35), pers.institution), pers.last, prop.title, prop.abstract, prop.type,prop.category_descrip, prop.joint, j.hst_time, j.noao_time, j.xmm_time, j.nrao_time, j.rxte_time, j.spitzer_time, j.suzaku_time, j.swift_time,j.nustar_time,total_time, prop.linked_propnum, num_targets from proposal prop, axafusers..person_short pers, joint j where prop.piid = pers.pers_id and prop.proposal_id *= j.proposal_id and prop.proposal_number in ($list));
    $bpp_query = $prop_query;
  }
   
  open (OUT, "> $param{out}.sql") || 
    die "Sorry, can't open $param{out}.sql: $!\n";

  #open (TIME, ">$param{out}_time") ||
  #  die "Sorry, can't open $param{out}_time: $!\n";
  #print TIME "prop_id\t\ttargid\twindow\tstart date\tstop date\n";

  #open (GROUP, ">$param{out}_group") ||
    #die "Sorry, can't open $param{out}_group: $!\n";
  #print GROUP "prop_id\t\ttargid\tT_targ\tinter\tT_ksec\tT_days\ttc_group\tgroup_id\n";


    
  print OUT qq(insert into final_comments values ($param{panel}, null, 0, 'N');), "\n" if ($param{panel} !~ /BPP|XVP|GTO/);
  print OUT qq(insert into final_comments values (99, null, 0, 'N');), "\n" if ($param{panel} =~ /BPP/);
  print OUT qq(insert into final_comments values (98, null, 0, 'N');), "\n" if ($param{panel} =~ /XVP/);
  print OUT qq(insert into final_comments values (97, null, 0, 'N');), "\n" if ($param{panel} =~ /GTO/);
  
  my %prop;  # This hash is necessary to prevent duplicating proposals for
  # the BPP
  # Proposal table
  my($proposal);
  #print STDERR "$prop_query\n";
  if ($param{panel} !~ /BPP/) { 
    $proposal = $dbhProp1->prepare($prop_query);
  } else {
    $proposal = $dbhProp1->prepare($bpp_query);
  }
  $proposal->execute($ao) if !$param{list};
  $proposal->execute() if $param{list};

  while (my ($panel_id, $prop_id, $proposal_id, $institution, $last_name, 
	     $title, $abstract,  $prop_type, $descrip,  $joint_flag, $hst_req, $noao_req, 
	     $xmm_req, $nrao_req, $rxte_req, $spitzer_req, $suzaku_req, 
	     $swift_req,$nustar_req,
	     $total_time, $link_id, 
	     $num_targets) = $proposal->fetchrow_array) {
    $institution =~ s/^\s+//;
    $institution =~ s/\s+$//;

    #archive/theory might have null num_targets, reset to 0
    $num_targets= 0 if (!defined $num_targets) ;
    # we need to skip proposals which are from other observatories
    if ($joint_flag =~ /CXO/ and !$param{joint}) {
      print "Proposal $prop_id is a joint proposal from another observatory ($joint_flag).  Skipping this proposal.\n";
      next;
    }

    # map type and big_proj correctly
    my $type;
    my $big_proj = 'None';
    if ($prop_type =~ /GTO/) {
      $type= "GO"; 
      $big_proj="GTO";
      $type= "TOO" if $prop_type =~ /TOO/;
      $big_proj="GTO-LP" if ($prop_type =~ /LP/);
    }
    elsif ($prop_type =~ /TOO/) {
      $type = 'TOO';
      $big_proj = 'LP' if ($prop_type =~ /LP/);
    }
    elsif ($prop_type =~ /LP|XVP/) {
      $type = 'GO';
      $big_proj = $prop_type;
    }
    elsif ($prop_type =~ /ARC|THE|GO/) {
      $type = $prop_type;
    }
    else {
      print "Can't handle $prop_type for $prop_id\n";
      exit(1);
    }

    # map joint_sort column to joint_flag 
    my $joint_sort = 0;
    $joint_sort = 1 if $joint_flag eq 'None';
    
    # map type_sort column to type
    my $type_sort = 5;
    $type_sort = 1 if $type eq 'ARCHIVE';
    $type_sort = 2 if $type eq 'THEORY';
    $type_sort = 3 if $type eq 'GO';
    $type_sort = 4 if $type eq 'TOO';

    # map big_proj_sort column to big_proj
    my $big_proj_sort = 6;
    $big_proj_sort = 1 if $big_proj eq 'LP';
    $big_proj_sort = 1 if $big_proj eq 'JCLP';
    $big_proj_sort = 2 if $big_proj eq 'VLP';
    $big_proj_sort = 3 if $big_proj eq 'XVP';
    $big_proj_sort = 4 if $big_proj eq 'GTO-LP';
    $big_proj_sort = 5 if $big_proj eq 'GTO';
    $big_proj_sort = 6 if $big_proj eq 'None'; 

    my $prop_status = "Y";
    $prop_status = "B" if $big_proj =~ /LP|XVP|VLP|GTO/;
    
    if ($param{panel} =~ /BPP/) {
      $panel_id = 99;
      $prop_status = 'Y'  if ($big_proj !~ /GTO/);
    }
    if ($param{panel} =~ /GTO/) {
      $panel_id = 97;
      $prop_status = 'Y';
      #$prop_status = "B" if $big_proj =~ /LP|XVP|VLP/;
    }
    if ($param{panel} =~ /XVP|VLP/) {
      $panel_id = 98;
      #$prop_status = 'Y';
    }
    
    $last_name =~ s/'/''/g;
    $last_name =~ s/"/\\"/g;
    $institution =~ s/'/''/g;
    $institution =~ s/"/\\"/g;
    $title =~ s/'/''/g;
    $title =~ s/"/\\"/g;
    $abstract =~ s/'/''/g;
    $abstract =~ s/"/\\"/g;
    my $cat_id = $prop_id;
    $cat_id =~ s/^\d{2}//;
    $cat_id =~ s/\d{4}$//;
    my $tag_num = $prop_id;
    $tag_num =~ s/^\d{4}//;
    
    $hst_req = 'null' if !$hst_req;
    $hst_req = 'null' if ($joint_flag =~ /HST-c/i);
    $noao_req = 'null' if !$noao_req;
    $xmm_req = 'null' if !$xmm_req;
    $xmm_req = 'null' if ($joint_flag =~ /XMM-c/i);
    $nrao_req = 'null' if !$nrao_req;
    $rxte_req = 'null' if !$rxte_req;
    $spitzer_req = 'null' if !$spitzer_req;
    $spitzer_req = 'null' if ($joint_flag =~ /Spitzer-c/i);
    $suzaku_req = 'null' if !$suzaku_req;
    $suzaku_req = 'null' if ($joint_flag =~ /Suzaku-c/i);
    $swift_req = 'null' if !$swift_req;
    $swift_req = 'null' if ($joint_flag =~ /Swift-c/i);
    $nustar_req = 'null' if !$nustar_req;
    $nustar_req = 'null' if ($joint_flag =~ /nustar-c/i);
    $link_id = 'null' if !$link_id;
    
    # The the number of targets that are not alternate configurations
    my $targs = $dbhProp2->prepare($num_targs_query);
    $targs->execute($proposal_id);
    my ($num_targs) = $targs->fetchrow_array;
    $targs->finish;

    my $monp = $dbhProp1->prepare($is_mon_query);
    $monp->execute($proposal_id);
    my ($monp_cnt) = $monp->fetchrow_array;
    $monp->finish;
    if ($monp_cnt > 0) {
      $monp_cnt = "Y";
    } else  {
      $monp_cnt = "N";
    }
    my $gridp = $dbhProp1->prepare($is_grid_query);
    $gridp->execute($proposal_id);
    my ($gridp_cnt) = $gridp->fetchrow_array;
    $gridp->finish;
    if ($gridp_cnt > 0) {
      $gridp_cnt = "Y";
    } else  {
      $gridp_cnt = "N";
    }
   
    
    if (!$prop{$prop_id}) {
      if ($param{anon}){
        # for anon, don't include last_name
        print OUT qq(insert into proposal (prop_id, category, tag_num, institution, type, type_sort, title, short_ttl, abstract, scicat, total_req_time, total_app_time, num_targets, num_targ_req, num_targ_app, joint_flag, joint_sort, panel_id, link_id, hst_req, hst_app, noao_req, noao_app, xmm_req, xmm_app, nrao_req, nrao_app, rxte_req, rxte_app, spitzer_req, spitzer_app, suzaku_req, suzaku_app, swift_req,swift_app,nustar_req,nustar_app,prop_status, big_proj, big_proj_sort,mon_flag,grid_flag) values ($prop_id, '$cat_id', '$tag_num', E'$institution', '$type', $type_sort, E'$title', (left(E'$title', 25)), E'$abstract', E'$descrip', $total_time, $total_time, $num_targets, $num_targs, $num_targs, '$joint_flag', $joint_sort, $panel_id, $link_id, $hst_req, $hst_req, $noao_req, $noao_req, $xmm_req, $xmm_req, $nrao_req, $nrao_req, $rxte_req, $rxte_req, $spitzer_req, $spitzer_req, $suzaku_req, $suzaku_req, $swift_req,$swift_req,$nustar_req,$nustar_req,'$prop_status', '$big_proj', $big_proj_sort,'$monp_cnt','$gridp_cnt');), "\n";
      }
      else{
        print OUT qq(insert into proposal (prop_id, category, tag_num, last_name, institution, type, type_sort, title, short_ttl, abstract, scicat, total_req_time, total_app_time, num_targets, num_targ_req, num_targ_app, joint_flag, joint_sort, panel_id, link_id, hst_req, hst_app, noao_req, noao_app, xmm_req, xmm_app, nrao_req, nrao_app, rxte_req, rxte_app, spitzer_req, spitzer_app, suzaku_req, suzaku_app, swift_req,swift_app,nustar_req,nustar_app,prop_status, big_proj, big_proj_sort,mon_flag,grid_flag) values ($prop_id, '$cat_id', '$tag_num', E'$last_name', E'$institution', '$type', $type_sort, E'$title', (left(E'$title', 25)), E'$abstract', E'$descrip', $total_time, $total_time, $num_targets, $num_targs, $num_targs, '$joint_flag', $joint_sort, $panel_id, $link_id, $hst_req, $hst_req, $noao_req, $noao_req, $xmm_req, $xmm_req, $nrao_req, $nrao_req, $rxte_req, $rxte_req, $spitzer_req, $spitzer_req, $suzaku_req, $suzaku_req, $swift_req,$swift_req,$nustar_req,$nustar_req,'$prop_status', '$big_proj', $big_proj_sort,'$monp_cnt','$gridp_cnt');), "\n";
      }
      
      # Alternate_target_group Table
      my $alt = $dbhProp2->prepare($alt_targ_query);
      $alt->execute($proposal_id);
      while (my ($alt_id, $alt_grp_name, 
		 $req_cnt) = $alt->fetchrow_array) {
	$num_targs += $req_cnt;
	print OUT qq(insert into alternate_target_group values ($alt_id, $prop_id, $panel_id, '$alt_grp_name', $req_cnt, $req_cnt);), "\n";
      }
      $alt->finish;
      print OUT qq(update proposal set num_targ_req = $num_targs, num_targ_app = $num_targs where prop_id = $prop_id and panel_id = $panel_id;), "\n";
      
      # Target Table
      my $target = $dbhProp2->prepare($targ_query);
      $target->execute($proposal_id);
      my($pr_targ_num) = 1;
      while (my ($response, $too_prob, $too_follow, $trig_target, $too_start,
		 $targname, $ss_obj, $instrument, 
		 $targ_num, $targid, $time_crit, $req_exp_time, $num_obs, 
                 $req_exp_time_1, $req_exp_time_2,
                 $raster_scan, $grid_name, $max_radius,
		 $grid_num_pnt, $monitor,$uninterrupt, $multitelescope,
		 $group_obs, $group_id, $constr_in_remarks,
		 $grating, $alt_id, $group_interval, $ra, $dec, 
		 $tc_coor, $mp_remarks) = $target->fetchrow_array) {
	print "$proposal_id: no raster_scan\n" if !$raster_scan;
	#exit if !$raster_scan; # There should be a value
        $multitelescope= 'N' if (!defined $multitelescope) ;
        $raster_scan= 'N' if (!defined $raster_scan) ;

	# Need to set the time for future cycles to undef if the value is 0
	$req_exp_time_1 = undef if $req_exp_time_1 and $req_exp_time_1 == 0;
	$req_exp_time_2 = undef if $req_exp_time_2 and $req_exp_time_2 == 0;


	$trig_target = 'Y' if !$trig_target;
	$too_prob = 1 if !$too_prob;
	$too_prob = .8 if $monitor =~ /P/;
	my $mcop = 'N';
	$mcop = 'Y' if $req_exp_time_1 or $req_exp_time_2;

	my $group_interval_1 = 'null';
	my $group_interval_2 = 'null';
	$group_interval = 'null' if !$group_interval;
	$group_interval_1 = $group_interval if $req_exp_time_1;
	$group_interval_2 = $group_interval if $req_exp_time_2;

	$ra = 0.0 if !$ra;
	$dec = 0.0 if !$dec;
	$tc_coor = 'null' if !$tc_coor;
	$mp_remarks = 'null' if !$mp_remarks;

	my $targ_status = "Y";
	$targ_status = "B" if $big_proj =~ /LP|XVP|VLP|GTO/;
        $targ_status="Y" if ($param{panel} =~ /BPP/ && $big_proj !~ /GTO/) ;
        if ($param{panel} =~ /GTO/)  {
          $targ_status="Y" ;
          #$targ_status = "B" if $big_proj =~ /LP|XVP|VLP/;
        }
	
	$targname = $ss_obj if !$targname;
	$targname =~ s/'/\\'/g;
	$targname =~ s/"/\\"/g;
	$targname = "Dummy Target" if length($targname) < 2;
	$mp_remarks =~ s/'/\\'/g;
	$mp_remarks =~ s/"/\\"/g;

	if (!$monitor ) {
          $monitor = 'N';
	  $monitor = 'Y' if $num_obs > 1;
        }

	# Decode the too type to response_time
	# if ($response =~ /0-4/i) {
	#     $response = "VF";
	# }
	# elsif ($response =~ /4-12/i) {
	#     $response = "F";
	# }
	# elsif ($response =~ /12-30/i) {
	#     $response = "S";
	# }
	# elsif ($response =~ />30/i) {
	#     $response = "VS";
	# }
	
	if (!$response) {
	  $response = "null";
	}
	else {
	  $response = qq('$response');
	}
	$too_prob = "null" if !$too_prob;
        $too_start= "null" if !$too_start;
	$alt_id = "null" if !$alt_id;
	
	if ($type =~ /TOO/) {
	  # Note that the number of observations for MCOPs will be recalculated
	  # in cacl_num_obs.pl
	  $num_obs = 1;
	  if ($too_follow) {
	    # get the number of follow-ups for the target
	    my $num_too_obs = $dbhProp3->prepare($num_obs_query);
	    $num_too_obs->execute($targid);
	    my ($num_follow) = $num_too_obs->fetchrow_array;
	    $num_too_obs->finish;
	    $num_obs += $num_follow;
	  }
	  # TOOs are considered TC if they have followups
	   $time_crit = 'Y' if $too_follow;
	}

	if ($monitor =~ /Y|P/) {
	  # Note that the number of observations for MCOPs will be recalculated
	  # in cacl_num_obs.pl
	    my $num_too_obs = $dbhProp3->prepare($num_obs_query);
	    $num_too_obs->execute($targid);
	    $num_obs = $num_too_obs->fetchrow_array;
	}

	# This allows pre-grid cycles to be used as test data
        my $grid_num_pnt_1=0;
        my $grid_num_pnt_2=0;
	if ($raster_scan eq 'Y') {
	  $max_radius = 0 if !$max_radius;
	  $grid_name = 'raster' if !$grid_name;
	  $grid_num_pnt = $num_obs if !$grid_num_pnt;
          if ($req_exp_time_1 || $req_exp_time_2) {
            my $grid_obs= $req_exp_time/$grid_num_pnt;
            $grid_num_pnt_1 = ceil($req_exp_time_1/$grid_obs) if $req_exp_time_1;
            $grid_num_pnt_2 = ceil($req_exp_time_2/$grid_obs) if $req_exp_time_2;
            $grid_num_pnt -= $grid_num_pnt_1;
            $grid_num_pnt -= $grid_num_pnt_2;
            if ((($grid_num_pnt_1 * $grid_obs) > $req_exp_time_1) || 
                (($grid_num_pnt_2 * $grid_obs) > $req_exp_time_2) ) {
               print STDERR "$prop_id, $targid,$targ_num: Grid time does not split evenly for multi_cycles with exp time=";
               print STDERR "$req_exp_time t+1=$req_exp_time_1 t+2=$req_exp_time_2\n";
               print STDERR "$grid_obs ks--pnt=$grid_num_pnt pnt+1=$grid_num_pnt_1 pnt+2=$grid_num_pnt_2 \n";
	    }
	  }
        }

	if ($group_obs eq 'Y') {
	  $group_id = qq('$group_id');
	}
	else {
	  $group_id = 'null';
	}

	my $tc_const_rem = 'null';
	$tc_const_rem = 0 if $constr_in_remarks =~ /Y/;

	$req_exp_time -= $req_exp_time_1 if $req_exp_time_1;
	$req_exp_time -= $req_exp_time_2 if $req_exp_time_2;

	$req_exp_time_1 = 'null' if !$req_exp_time_1 or $mcop eq 'N';
	$req_exp_time_2 = 'null' if !$req_exp_time_2 or $mcop eq 'N';

	my $targ_status_1 = 'N';
	$targ_status_1 = $targ_status if $req_exp_time_1 ne 'null';
	my $targ_status_2 = 'N';
	$targ_status_2 = $targ_status if $req_exp_time_2 ne 'null';

	print OUT qq(insert into target (prop_id, targ_name, ra, dec, detector, grating, targ_id, targ_num, alt_id, time_crit, response_time, too_prob_req, too_prob_app, too_start, req_time, app_time, req_time_1, app_time_1, req_time_2, app_time_2, targ_status, targ_status_1, targ_status_2, num_obs_req, num_obs_app, num_obs_req_1, num_obs_app_1, num_obs_req_2, num_obs_app_2, tc_coor, tc_const_rem, monitor, raster_scan, uninterrupt, group_name, group_obs, group_interval, group_interval_1, group_interval_2, multitelescope, constr_in_remarks, mp_remarks, panel_id) values ($prop_id, E'$targname', $ra, $dec, '$instrument', '$grating', $targid, $pr_targ_num, $alt_id, '$time_crit', $response, $too_prob, $too_prob, $too_start,$req_exp_time, $req_exp_time, $req_exp_time_1, $req_exp_time_1, $req_exp_time_2, $req_exp_time_2, '$targ_status', '$targ_status_1', '$targ_status_2', $num_obs, $num_obs, $num_obs, $num_obs, $num_obs, $num_obs, $tc_coor, $tc_const_rem, '$monitor', '$raster_scan', '$uninterrupt', $group_id, '$group_obs', $group_interval, $group_interval, $group_interval, '$multitelescope', '$constr_in_remarks', E'$mp_remarks', $panel_id);), "\n" if $raster_scan eq 'N' and $trig_target eq 'Y';
	print OUT qq(insert into target (prop_id, targ_name, ra, dec, detector, grating, targ_id, targ_num, alt_id, time_crit, response_time, too_prob_req, too_prob_app, too_start, req_time, app_time, req_time_1, app_time_1, req_time_2, app_time_2, targ_status, targ_status_1, targ_status_2, num_obs_req, num_obs_app,  num_obs_req_1, num_obs_app_1, num_obs_req_2, num_obs_app_2, num_pnt_req, num_pnt_app, num_pnt_req_1, num_pnt_app_1, num_pnt_req_2, num_pnt_app_2, tc_coor, tc_const_rem, monitor, raster_scan, grid_name, max_radius, uninterrupt, group_name, group_obs, group_interval, group_interval_1, group_interval_2, multitelescope, constr_in_remarks, mp_remarks, panel_id) values ($prop_id, '$targname', $ra, $dec, '$instrument', '$grating', $targid, $pr_targ_num, $alt_id, '$time_crit', $response, $too_prob, $too_prob, $too_start, $req_exp_time, $req_exp_time, $req_exp_time_1, $req_exp_time_1, $req_exp_time_2, $req_exp_time_2, '$targ_status', '$targ_status_1', '$targ_status_2', $grid_num_pnt, $grid_num_pnt, $grid_num_pnt_1, $grid_num_pnt_1, $grid_num_pnt_2, $grid_num_pnt_2, $grid_num_pnt, $grid_num_pnt, $grid_num_pnt_1, $grid_num_pnt_1, $grid_num_pnt_2, $grid_num_pnt_2, $tc_coor, $tc_const_rem, '$monitor', '$raster_scan', '$grid_name', $max_radius, '$uninterrupt', $group_id, '$group_obs', $group_interval, $group_interval_1, $group_interval_2, '$multitelescope', '$constr_in_remarks', E'$mp_remarks', $panel_id);), "\n" if $raster_scan eq 'Y';

        if ($raster_scan eq 'N' and $trig_target eq 'Y') {
          $pr_targ_num++;
        } elsif ($raster_scan eq 'Y')  {
          $pr_targ_num++;
        }
	
	# If there is a time constraint target in a proposal, 
	# reflect that up to the proposal level
	if ($time_crit =~ /Y/) {
	  print OUT qq(update proposal set tc_prop = 'Y' where prop_id = $prop_id and panel_id = $panel_id;), "\n";
	}

	print OUT qq(update proposal set mcop = 'Y' where prop_id = $prop_id and panel_id = $panel_id;), "\n" if $mcop eq 'Y';
	print OUT qq(update target set mcop = 'Y' where targ_id = $targid and panel_id = $panel_id;), "\n" if $mcop eq 'Y';
	print OUT qq(update target set num_obs_req_1 = 0, num_obs_req_2 = 0, num_obs_app_1 = 0, num_obs_app_2 = 0 where targ_id = $targid and panel_id = $panel_id;), "\n" if $mcop eq 'N';

	# If there is no follow-up target record, we need to reduce the 
        # num_targets count in proposal by 1
	if ($trig_target eq 'N') {
	  print OUT qq(update proposal set num_targets = num_targets - 1 where prop_id = $prop_id and panel_id = $panel_id;), "\n";
	}
	
	# too table includes too's and monitor segments
	if ($type =~ /TOO/ or $monitor !~ /N/) {
	  my $obs_status = "Y";
	  $obs_status = "B" if $big_proj =~ /LP|XVP|VLP|GTO/;
          $obs_status="Y" if ($param{panel} =~ /BPP/ && $big_proj !~ /GTO/);
          if ($param{panel} =~ /GTO/)  {
            $obs_status="Y" ;
            #$obs_status = "B" if $big_proj =~ /LP|XVP|VLP/;
          }

	  my $count_obs = $dbhProp3->prepare($num_obs_query);
	  $count_obs->execute($targid);
	  my ($count) = $count_obs->fetchrow_array;
	  $count_obs->finish;

	  if ($count) {
	    my $follow_time = 0;
	    my $too = $dbhProp3->prepare($too_query);
	    $too->execute($targid);
	    while (my ($ordr, $obs_time, 
		       $pre_min_lead, $pre_max_lead) = $too->fetchrow_array) {
	      $ordr++ if $type =~ /TOO/;
	      $follow_time += $obs_time;
	      my $too_type = 'F';
	      $too_type = 'A' if !$obs_time;
	      $too_type = 'M' if $monitor =~ /Y|P/;

	      my $fractol = ($pre_max_lead - $pre_min_lead) / ($pre_max_lead + $pre_min_lead);
	      print OUT qq(insert into too values ($prop_id, $targid, $ordr, '$mcop', 0, $obs_time, $obs_time, 0, $pre_min_lead, $pre_max_lead, $fractol, '$obs_status', '$too_type', $panel_id);), "\n";
	    }
	    $too->finish;
	    if ($type =~ /TOO/) {
	      my $trig_time = $req_exp_time;
	      $trig_time += $req_exp_time_1 if $req_exp_time_1 ne 'null';
	      $trig_time += $req_exp_time_2 if $req_exp_time_2 ne 'null';
	      $trig_time -= $follow_time;
	      print OUT qq(insert into too values ($prop_id, $targid, 1, '$mcop', 0, $trig_time, $trig_time, 0, null, null, null, '$obs_status', 'T', $panel_id);), "\n";
	    }
	  }
	  else {
	    print OUT qq(insert into too values ($prop_id, $targid, 1, '$mcop', 0, $req_exp_time, $req_exp_time, 0, null, null, null, '$obs_status', 'T', $panel_id);), "\n";
	  }
	}
	
	# rollreq table
	my $rollreq = $dbhProp3->prepare($rollreq_query);
	$rollreq->execute($targid);
	while (my($order, $roll_constraint, 
		  $roll_180, $roll, 
		  $roll_tolerance) = $rollreq->fetchrow_array) {
	  if (!$roll_180) {
	    $roll_180 = "null";
	  }
	  else {
	    $roll_180 = qq('$roll_180');
	  }
	  print OUT qq(insert into rollreq values ($targid, $order, '$roll_constraint', $roll_180, $roll, $roll_tolerance, 0, $panel_id);), "\n";
	}
	$rollreq->finish;
	
	# timereq table
	my $timereq = $dbhProp3->prepare($timereq_query);
	$timereq->execute($targid);
	while (my($order, $time_constraint, 
		  $tstart_date, $tstart_time,
		  $tstop_date, $tstop_time, 
		  $tc_time) = $timereq->fetchrow_array) {
	  print OUT qq(insert into timereq values ($targid, $order, 0, '$time_constraint', '$tstart_date $tstart_time', '$tstop_date $tstop_time', $tc_time, $panel_id);), "\n";
	  #print TIME "$prop_id\t$targid\t$tc_time\t$tstart_date $tstart_time\t$tstop_date $tstop_time\n";
	}
	$timereq->finish;
	
	# phasereq table
	my $phasereq = $dbhProp3->prepare($phasereq_query);
	$phasereq->execute($targid);
	while (my($phase_period, $phase_epoch, 
		  $phase_start, $phase_end, 
		  $phase_start_margin, $phase_end_margin, 
		  $phase_constraint_flag) = $phasereq->fetchrow_array) {
	  print OUT qq(insert into phasereq values ($targid, $phase_period, $phase_epoch, $phase_start, $phase_end, $phase_start_margin, $phase_end_margin, '$phase_constraint_flag', $phase_period, $panel_id);), "\n";
	}
	$phasereq->finish;
      }
      $target->finish;
    }
    $prop{$prop_id} = $prop_id;
  }
  $proposal->finish;
  #if ($param{panel} =~ /XVP|VLP/) {
    #print OUT qq(update target set targ_status = 'B' where panel_id = 98 and prop_id in (select prop_id from proposal where panel_id = 98 and big_proj in ('LP', 'XVP','VLP'));), "\n" ;
    #close OUT; 
  #} elsif ($param{panel} =~ /GTO/) {
    #print OUT qq(update target set targ_status = 'B' where panel_id = 97 and prop_id in (select prop_id from proposal where panel_id = 97 and big_proj in ('LP', 'XVP','VLP'));), "\n" ;
    #close OUT; 
  #} else {
  #print OUT qq(update target set targ_status = 'B' where panel_id = $param{panel} and prop_id in (select prop_id from proposal where panel_id = $param{panel} and big_proj in ('LP', 'XVP','VLP'));), "\n" if $param{panel} !~ /BPP/i;
  #close OUT; 
  #}

  close OUT;
  #close TIME;
  #close GROUP;
  chmod (0640,"$param{out}.sql");
  #chmod (0640,"$param{out}_time");
  #chmod (0640,"$param{out}_group");
  
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
	    out => undef,
	    panel => undef,
            verbose => 0
           );

  GetOptions( \%param,
              "UProposal=s",
	      "SProposal=s",
	      "out=s",
	      "ao=s",
	      "q=s",
	      "list=s",
	      "all",
	      "joint",
	      "panel=s",
              "verbose",
              "version",
              "anon",
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
  Pod::Text::pod2text( '-75', $0, *STDOUT );
  exit $exit;
}

__END__

=head1 USAGE

pop_pr.pl [options]

=head1 OPTIONS

B<pop_pr.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-UProposal> (user)

username for logging onto sql server with proposal database

=item B<-SProposal> (server)

name of sql server with proposal database

=item B<-out> (filename)

name of the sql file to be generated

=item B<-ao> (ao string)

the ao string with leading zero. Using this flag will dump only the accepted
proposals from that ao cycle.  If this flag is not used, then all non-CAL and
non-DDT proposals from the ao listed in the proposal..context table will be 
dumped.

=item B<-all>

Using this flag will dump all non-CAL and non-DDT proposals from that ao cycle
(must be used with B<-ao>)

=item B<-joint>

Proposals joint from other observatories will be included in the panel build

=item B<-list>

Filename of list containing proposal numbers to put in panel.  The proposal will be assigned to panel 1.

=item B<-panel> (panel number)

the panel_id with leading zero which will be dumped. If the flag is not 
used, all panels will be dumped if B<-ao> is specified.  B<-panel> should be 
BPP to get the large project panel (panel 99) to dump.  B<-panel> should be 
XVP to get the XVP panel (panel 98) to dump.

=item B<-anon>
Don't include PI last name in output sql.

=item B<-help>

displays documentation for B<pop_pr.pl>

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
