#!/home/ascds/DS.release/ots/bin/perl

#******************************************************************************
# panel_slew.pl
#
# Creates the sql with the panel time allotments for the given cycle.
# It will create calc_panel_times.sql  for Sybase and
# calc_panel_times.psql for postgres.  
# Send the results calc_panel_times.list to CDO(Andrea)
#
# The formula is from Fred Seward days :-)
# Use the times from the middle 10 proposals and calculate an average for each
# panel. Then split the times up based on that and number of proposals in panel
#
# Can optionally determine allotments for a subset of sources (e.g. HEL) by
# reading list of targets from file and excluding proposals that don't have
# targets in the list.
#
#***************************************************************************

BEGIN
{
    $ENV{SYBASE} = "/soft/SYBASE16.0";
}

use vars qw (%param $cycle_allot $slewfactor $dbh
	@panels $total_pcnt $over_ss  $total_ptime 
        );

use strict;
use Getopt::Long;
use DBI;

{
  my($stmp, $sth, $dsn,$xtime, $tot_slew, $tslew,$tot_allot);
  my($ii, @tarr, @row,$panel_cnt,$pid,$prop_cnt,$pavg);
  my($avg_tottime);
  my($hel, @hel_targids);

  &parse_input;

  $slewfactor = 1.05;

  #database connection
  $stmp = "server=$param{S};database=proposal";
  $dsn = "DBI:Sybase:$stmp";
  $dbh = DBI->connect($dsn, $param{U}, $param{q}, {
        PrintError => 1,
        RaiseError => 1});
  if ( !defined $dbh) {
    exit 1;
  }

  open DFILE,"> $param{d}/debug.txt";

  # HEL string for modifying
  if ($param{l}) {
    $hel = "HEL_"
  }
  #OVERRIDE
  $cycle_allot = $param{t};   # panel time without slew   

  #get number of panels
  my(@pnames)= ();
  $stmp = qq(select panel_name from panel,ao 
	where panel.ao = ao.ao_id
	and ao.ao_str="$param{a}"
	and panel_name not like "%P"
	order by panel_id);
  print DFILE "$stmp\n\n" if $param{z};
  $sth = $dbh->prepare($stmp);
  $sth->execute;
  while (@row = $sth->fetchrow_array) {
     push(@pnames,$row[0]);
  }
  $panel_cnt = $#pnames + 1;
  print DFILE "-- avail=$cycle_allot  cnt=$panel_cnt\n";

  # Get HEL targets if exist and prepare sql statement
  my $hel_stmp = "";
  if ( $hel) {
    @hel_targids = get_hel_targids();
      my $targs = join(",", @hel_targids);
      $hel_stmp = " and target.targid in ($targs)";
  }
  # get total times for proposals excluding BPPs, GTOs etc
  # this is used to calculate an average time per panel
  &get_total_prop($hel_stmp);

  $avg_tottime=0;
  #now get #props,avg time per panel
  foreach my $panelname (@pnames) {
     ($pid,$prop_cnt) = get_prop_cnt($panelname, $hel_stmp);
     # returns average* numberOfProposals
     $pavg = get_prop_avg($panelname,$prop_cnt,$hel_stmp);
     $avg_tottime  += $pavg;
     push (@panels,"$panelname-$pid-$prop_cnt-$pavg");
     print DFILE "Count/avg time: $panelname-$pid-$prop_cnt-$pavg\n";
  }
  
  #print STDERR "1: $cycle_allot  $ptime  $panel_cnt  $avg_tottime  \n";
  print STDERR "cycle_allot=$cycle_allot  avg_tottime=$avg_tottime\n";

  open(OFILE3,"> $param{d}/${hel}calc_panel_times.psql");
  # Don't need Sybase updates for HEL sources
  if (!$hel) {
    open(OFILE,"> $param{d}/calc_panel_times.sql");
  }
  open(O2FILE,"> $param{d}/${hel}calc_panel_times.list");
  print O2FILE "Panel\t Allotted\t  w/Slew\n";
  print O2FILE "------------------------------------\n";



  printf STDERR "over_ss=%4.2f propcnt=%d\n",$over_ss,$total_pcnt;

  print STDERR "\n\n    avgtime  n      Allot.Time\t   Slew\n";
  print STDERR "----------------------------------------\n";
  $tot_allot=0;
  foreach my $hh (@panels) {
    @tarr = split(/-/,$hh);

    # from the Fred Seward days....
    $xtime = ((($cycle_allot/2)/$total_pcnt) * $tarr[2]) ;
    $xtime += $tarr[3] * (($cycle_allot/2)/$avg_tottime);
    $xtime = int($xtime * 100);
    $xtime = $xtime/100.;
    #print STDERR "THIS: $cycle_allot -- $total_pcnt -- $tarr[2]\n";
    #print STDERR "    : $tarr[3] -- $cycle_allot -- $avg_tottime\n";

    printf OFILE ("exec panel_update_allotted_exp_time $tarr[1],%7.2f\ngo\n",$xtime);
    printf STDERR "%02.2d  %7.2f  %2d  ",
	$tarr[0],$tarr[3], $tarr[2];
    printf STDERR "  %7.2f",$xtime;
    $tslew = int(($slewfactor * $xtime)*100);
    $tslew = $tslew/100.;
    $tot_slew += $tslew;
    printf STDERR "\t %7.2f",$tslew;
    $tot_allot += $xtime;
    print STDERR "\n";
    printf O2FILE ("%02.2d\t %7.2f\t %7.2f\n",$tarr[0],$xtime , $tslew);
    my $total_time = "total_exp_time";
    if ($hel) {
      $total_time = "total_hel_time";
      $tslew = $xtime
    }
    printf OFILE3 "update allotment set %s= %7.2f  where panel_id = $tarr[0];\n",$total_time, $tslew;

  }

  
  printf STDERR "%-16.16s  --------\t--------\n"," ";
  printf STDERR "%-16.16s  %8.2f\t%8.2f\n"," ",$tot_allot,$tot_slew;
  printf O2FILE "  \t--------\t--------\n";
  printf O2FILE "  \t%8.2f\t%8.2f\n",$tot_allot,$tot_slew;

  close OFILE;
  close O2FILE;
  close OFILE3;
  close DFILE;
  chmod(0660,"$param{d}/calc_panel_times.sql");
  chmod(0660,"$param{d}/calc_panel_times.list");
  chmod(0660,"$param{d}/calc_panel_times.psql");
  chmod(0660,"$param{d}/debug.txt");

}


sub get_prop_avg
{
  my($s1,$prop_cnt,$hel_stmp) = @_;
  my($avg,$jj,$idx,$nn);
  my($pname,$stmp,$sth,@row);
  my($mycnt,@myp,$middle);

  $pname = sprintf("%02.2d",$s1);
  print STDERR "Processing $pname\n";
  $stmp = qq(select distinct proposal.proposal_id,proposal.total_time
	from panel,proposal,panel_proposal,target
 	where panel.panel_id = panel_proposal.panel_id and
	proposal.ao_str = "$param{a}" and
	proposal.proposal_id = panel_proposal.proposal_id and
	proposal.proposal_id = target.proposal_id and
	proposal.type like "GO" and
	multi_cycle != 'Y' and
        panel.panel_name = "$pname"
        $hel_stmp
  UNION select proposal.proposal_id,
	sum(prop_exposure_time - est_time_cycle_n1 - est_time_cycle_n2) 'total_time'
	from panel,proposal,panel_proposal,target
 	where panel.panel_id = panel_proposal.panel_id and
	proposal.ao_str = "$param{a}" and
	proposal.proposal_id = panel_proposal.proposal_id and
	proposal.proposal_id = target.proposal_id and
	proposal.type like "GO%" and
	multi_cycle = 'Y' and
        panel.panel_name = "$pname"
        $hel_stmp
	group by proposal.proposal_id
  UNION
        select proposal.proposal_id,
	sum(prop_exposure_time * too.probability) "total_time"
	from panel,proposal,panel_proposal,target,too
 	where panel.panel_id = panel_proposal.panel_id and
	proposal.proposal_id = panel_proposal.proposal_id and
	proposal.ao_str = "f$param{a}" and
	proposal.type =  "TOO" and
	proposal.proposal_id = target.proposal_id and
	target.targid = too.targid and
	multi_cycle != 'Y' and
	proposal.proposal_id not in (select proposal_id from alternate_target_group) and
        panel.panel_name = "$pname"
        $hel_stmp
	group by proposal.proposal_id
        order by total_time desc
);


  print DFILE "$stmp\n\n" if $param{z};
  $sth = $dbh->prepare($stmp);
  $sth->execute;
  $jj = 0;
  $avg = 0.0;
  $middle = ($prop_cnt+1)/2;
  $middle -= 5;
  if ($middle < 0) { $middle=0;}
  while (@row = $sth->fetchrow_array) {
    push @myp,$row[1];
  }
  for ($jj=$middle,$mycnt=0; $jj < ($middle + 10) && $jj<= $#myp;$jj++,$mycnt++) {
    #print STDERR "$myp[$jj] ";
    $avg += $myp[$jj];
  }
  if ($mycnt > 0) {
    $avg = $avg / $mycnt;
  }
  printf DFILE ("\n$pname  avg=%6.2f  propcnt=%3d    avgtime=%7.2f\n",$avg,$prop_cnt,($avg*$prop_cnt));
  $avg = $avg * $prop_cnt;

  return $avg;

}
	

# get proposal count excluding Arc/The, GTO, BPPs
sub get_prop_cnt
{
  my($s1, $hel_stmp) = @_;
  my($prop_cnt) = 0;
  my($pname);
  my($pid) = 0;
  my($stmp,$sth,@row);

  $pname = sprintf("%02.2d",$s1);
  $stmp = qq(select distinct panel.panel_id,
	count(distinct panel_proposal.proposal_id)
	from panel,proposal,panel_proposal,target
 	where 
	panel.panel_id = panel_proposal.panel_id and
	panel_proposal.proposal_id = proposal.proposal_id and
	target.proposal_id = proposal.proposal_id and
	proposal.ao_str ="$param{a}" and
	proposal.type not like "GTO%" and
	proposal.type not like "ARC%" and
	proposal.type not like "THE%" and
	proposal.type not like "%LP%" and 
	proposal.type not like "%XVP%" and 
	proposal.type not like "DDT%" and
	proposal.type not like "CAL%" and
	panel_name = "$pname"
	$hel_stmp
        group by panel.panel_id);
	
  print DFILE "$stmp\n\n" if $param{z};
  $sth = $dbh->prepare($stmp);
  $sth->execute;
  $prop_cnt = 0;
  while (@row = $sth->fetchrow_array) {
    $pid = $row[0];
    $prop_cnt += $row[1];
  }
  #print STDERR "**** $pid **** $prop_cnt\n\n";
  return ($pid,$prop_cnt);
}



# get total times for each proposal
# Exclude TOO, GTO,Archive/Theory, DDT, CAL and LP/VLP/XVP
sub get_total_prop
{
  my($hel_stmp)=@_;
  my($stmp,$sth,@row);
  $total_pcnt=0;
  $total_ptime=0;



  # first get count of all the regular proposals  
  $stmp = qq(select count(distinct proposal.proposal_id)
        from proposal, panel_proposal pp, target where
	proposal.ao_str="$param{a}" and
        proposal.proposal_id = pp.proposal_id and
        proposal.proposal_id = target.proposal_id and
        proposal.type not like "GTO%" and
        proposal.type not like "ARC%" and
        proposal.type not like "THE%" and
        proposal.type not like "DDT%" and
        proposal.type not like "CAL%" and
        proposal.type not like "%XVP%" and
        proposal.type not like "%LP%"
        $hel_stmp);
  print DFILE "$stmp\n\n" if $param{z};
  $sth = $dbh->prepare($stmp);
  $sth->execute;
  while (@row = $sth->fetchrow_array) {
     $total_pcnt += $row[0];
  }

  # now get a time  and use too probability
  $stmp = qq(select sum(total_time)  
	from proposal, target where
	proposal.ao_str = "$param{a}"  and
    proposal.proposal_id = target.proposal_id and
	(proposal.status = "PROPOSED" or rank is not null) and
	proposal.type not like "TOO%" and
	proposal.type not like "GTO%" and
	proposal.type not like "ARC%" and
	proposal.type not like "THE%" and
	proposal.type not like "DDT%" and
	proposal.type not like "CAL%" and
	proposal.type not like "%XVP%" and
	proposal.type not like "%LP%"
    $hel_stmp
	UNION
	select sum(prop_exposure_time*too.probability) "total_time"
        from proposal,target,too where
        proposal.ao_str = "$param{a}" and
        (proposal.status = "PROPOSED" or rank is not null) and
        proposal.type = "TOO" 
        and proposal.proposal_id = target.proposal_id
	and proposal.proposal_id not in (select proposal_id from alternate_target_group) 
        and target.targid = too.targid
        $hel_stmp
        group by proposal.proposal_id
);
  print DFILE "$stmp\n\n" if $param{z};
  $sth = $dbh->prepare($stmp);
  $sth->execute;
  while (@row = $sth->fetchrow_array) {
     $total_ptime += $row[0];
  }
  $over_ss = $total_ptime/$cycle_allot;

  print DFILE "-- #props=$total_pcnt   time=$total_ptime  over=$over_ss\n";
}


# Read HEL target ids from calc_ecliptic.pl output, return array of targids
sub get_hel_targids{
  my $targfile = "$param{d}/HEL_targids.list";
  open IFILE,"< ${targfile}" or
        die "Unable to open ${targfile}for reading. Have you run calc_ecliptic in this directory?\n";
  chomp(my @targets = <IFILE>);

  close IFILE;

  return(@targets)

}

sub parse_input
{
  my ($pwd);
  my $err = 0;
  %param = (
             U=> undef,
             S=> "$ENV{DB_PROP_SQLSRV}",
             a=> "$ENV{ASCDS_PROP_AO}",
             d=> ".",
             t=> undef,
        );
  GetOptions( \%param,
          "U=s",
          "S=s",
          "a=s",
          "d=s",
          "t=f",
          "l",
          "z"
	);

  if (!$param{h} ) {
    while ( my ( $par, $val ) = each ( %param ) ) {
      next if defined $val && $val ne "";
      warn("parameter '$par' not set\n");
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


  use Term::ReadKey;
   {
       ReadMode 'noecho';
       print STDERR "Enter password for $param{U} on $param{S}: ";
       $param{q} = ReadLine( 0 );
       chomp $param{q};
       ReadMode 'restore';
       print STDERR  "\n";
    }
}

sub  usage
{
    print STDERR "\nUSAGE: panel_slew.pl -USat\n";
    print STDERR "       -U database user name\n";
    print STDERR "       -S database server, default is DB_PROP_SQLSRV\n";
    print STDERR "       -a AO , default is ASCDS_PROP_AO\n";
    print STDERR "       -t allotted time without slew for this cycle\n";
    print STDERR "       -d output directory, default is current\n";
    print STDERR "       -l calculate allotment for High Ecliptic Latitude sources\n";
}


