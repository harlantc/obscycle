#!/usr/bin/perl
#
# final_checks.pl
#
# Purpose: Provide files with specified info at end of review
#
# Copyright (c) 2015-2020
# All Rights Reserved.  Std. disclaimer applies.
#

use strict;
use DBI;
use Data::Dumper;

use vars qw ($VERSION %param);

$VERSION = '1.0';

#******#
# Main #
#******#
{
  my($str);
  # Option Parsing
  use Getopt::Long;
  parse_opts();
  
  if ($param{version}) {
    print $VERSION, "\n";
    exit( 0 );
  }

  if ($param{help}) {
    usage(0);
  }
 
  my ($sec, $min, $hr, $day, $month, $year) = (localtime)[0, 1, 2, 3, 4, 5];
  $year += 1900;
  $month++;
  $month = sprintf("%02d", $month);
  $day = sprintf("%02d", $day);
  $sec = sprintf("%02d", $sec);
  $min = sprintf("%02d", $min);
  $hr = sprintf("%02d", $hr);
  my $date = "$year$month${day}_$hr$min$sec";


  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  my $dbh = DBI->connect($dsn1, "", "", {
					 PrintError => 0,
					 RaiseError => 1});
    
  # Compile queries
  my $alt_chk = $dbh->prepare(qq( select distinct t.panel_id, t.prop_id, num_targets,
	count(too.targ_id) 
	from proposal , target t, too 
	where proposal.prop_id = t.prop_id 
	and proposal.panel_id = t.panel_id 
	and t.prop_id = too.prop_id 
	and t.panel_id = too.panel_id 
	and prop_status = 'Y' and obs_status = 'N' and trigflag = 'T' 
	group by t.panel_id, t.prop_id, num_targets, t.targ_id 
	having count(t.*) > 1 
	order by t.panel_id, t.prop_id ));


  $str = join($param{sep},("Panel","Proposal","#Tgts","#Trig Rej"));
  writeIt($alt_chk,$date,$str,"trigger_reject");
  $alt_chk->finish;

  
  my $joint_chk = $dbh->prepare(qq(
	select panel_id,prop_id,('"' || last_name || '"'), 
	'hst' joint ,hst_req  req ,hst_app  app from proposal
	where prop_status='Y' and hst_req != hst_app
	UNION
	select panel_id,prop_id,('"' || last_name || '"'), 
	'xmm',xmm_req,xmm_app from proposal
	where prop_status='Y' and xmm_req != xmm_app
	UNION
	select panel_id,prop_id,('"' || last_name || '"'), 
	'noao',noao_req,noao_app from proposal
	where prop_status='Y' and noao_req != noao_app
	UNION
	select panel_id,prop_id,('"' || last_name || '"'), 
	'nrao',nrao_req,nrao_app from proposal
	where prop_status='Y' and nrao_req != nrao_app
	UNION
	select panel_id,prop_id,('"' || last_name || '"'), 
	'spitzer',spitzer_req,spitzer_app from proposal
	where prop_status='Y' and spitzer_req != spitzer_app
	UNION
	select panel_id,prop_id,('"' || last_name || '"'), 
	'nustar',nustar_req,nustar_app from proposal
	where prop_status='Y' and nustar_req != nustar_app
	UNION
	select panel_id,prop_id,('"' || last_name || '"'), 
	'swift',swift_req,swift_app from proposal
	where prop_status='Y' and swift_req != swift_app
	order by panel_id,prop_id));
  $str = join($param{sep},("Panel","Proposal","Last","Joint","Req","App"));
  writeIt($joint_chk,$date,$str,"joint_diffs");
  $joint_chk->finish;
   

  my $at_chk = $dbh->prepare(qq(select distinct proposal.panel_id,proposal.prop_id,
	type, ('"' || last_name || '"'), 
	total_req_time,total_app_time,fg_norm,rank,('"'||comments||'"')
	from proposal
	where prop_status='Y' 
	and  (total_req_time != total_app_time or comments is not null)
	and type in ('ARCHIVE','THEORY')
	order by proposal.panel_id,proposal.prop_id));
  $str = join($param{sep},("Panel","Proposal","Type","Last","Req","App","NormG","Rank","Comments"));
  writeIt($at_chk,$date,$str,"arc_the_reduction");
  $at_chk->finish;

  my $prop_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,type,
	type, ('"' || last_name || '"'), 
	prop_req_time,prop_app_time,mcop,fg_norm,rank
	from proposal
	where prop_status='Y' 
	and  (prop_req_time != prop_app_time
	or prop_req_time_1 != prop_app_time_1
	or prop_req_time_2 != prop_app_time_2)
	order by proposal.panel_id,proposal.prop_id));
  $str = join($param{sep},("Panel","Proposal","Type","Last","Req","App","MCOP","NormG","Rank"));
  writeIt($prop_chk,$date,$str,"prop_time_reduction");
  $prop_chk->finish;

  my $tgt_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,
	type, ('"' || last_name || '"'), 
	('"'||targ_name|| '"'),
	req_time,app_time,target.mcop,fg_norm,rank
	from proposal,target
	where proposal.prop_id = target.prop_id
	and proposal.panel_id = target.panel_id
	and prop_status='Y' and targ_status='Y'
	and  (req_time != app_time
	or req_time_1 != app_time_1
	or req_time_2 != app_time_2)
	order by proposal.panel_id,proposal.prop_id));
  $str = join($param{sep},("Panel","Proposal","Type","Last","TargName",
	"Req","App","MCOP","NormG","Rank"));
  writeIt($tgt_chk,$date,$str,"tgt_time_reduction");
  $tgt_chk->finish;

  my $tc_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,
	('"' || last_name || '"'), fg_norm,rank,
	rc_score_req as RC_Req,tc_prop,
	('"' || substring(title,0,20) || '"')
	from proposal
	where (prop_status in ('N','G') )
	and fg_norm >= 3.5 and rc_score_req > 0
	order by fg_norm desc,rank asc));
	#and (tc_e_app > 0 or tc_a_app >0 or tc_d_app > 0)
  $str = join($param{sep},("Panel","Proposal","Last","NormG","Rank","RC","TC","Title"));
  writeIt($tc_chk,$date,$str,"rc_hirank_unapp");
  $tc_chk->finish;

  my $too_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,
	('"' || last_name || '"'), fg_norm,rank,
	vf_app,f_app,s_app,vs_app,
	('"' || substring(title,0,20) || '"')
	from proposal,target
	where (prop_status in ('N','G') or targ_status in ('N','G'))
	and proposal.prop_id = target.prop_id
	and proposal.panel_id = target.panel_id
	and fg_norm >= 3.5 
	and (vf_app > 0 or f_app >0 or s_app>0 or vs_app > 0)
	order by fg_norm desc,rank asc));
  $str = join($param{sep},("Panel","Proposal","Last","NormG","Rank","VF","F","S","VS","Title"));
  writeIt($too_chk,$date,$str,"too_hirank_unapp");
  $too_chk->finish;

  my $vf_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,
	('"' || last_name || '"'), 
	('"' || targ_name || '"'), 
	fg_norm,rank, atg.app_cnt,alt_grp_name
	from proposal
   	JOIN target LEFT JOIN alternate_target_group atg on
    	target.alt_id = atg.alt_id
     	on proposal.prop_id =target.prop_id
	and proposal.panel_id = target.panel_id
	and proposal.prop_status='Y' and targ_status='Y'
	and response_time = '0-5'
	order by panel_id,prop_id));
  $str = join($param{sep},("Panel","Proposal","Last","TargName","NormG","Rank","AltApp","AltGrp"));
  writeIt($vf_chk,$date,$str,"too_vf");
  $vf_chk->finish;

  my $f_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,
	('"' || last_name || '"'), 
	('"' || targ_name || '"'), 
	fg_norm,rank, atg.app_cnt,alt_grp_name
	from proposal
   	JOIN target LEFT JOIN alternate_target_group atg on
    	target.alt_id = atg.alt_id
     	on proposal.prop_id =target.prop_id
	and proposal.panel_id = target.panel_id
	and proposal.prop_status='Y' and targ_status='Y'
	and response_time = '5-20'
	order by panel_id,prop_id));
  $str = join($param{sep},("Panel","Proposal","Last","TargName","NormG","Rank","AltApp","AltGrp"));
  writeIt($f_chk,$date,$str,"too_f");
  $f_chk->finish;

  # less approved time, no joint cut
  # keep in mind that the time for current cycle is displayed...not future
  my $jt_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,
	type, ('"' || last_name || '"'), 
	(prop_req_time+prop_req_time_1+prop_req_time_2),
	(prop_app_time+prop_app_time_1+prop_app_time_2),
	joint_flag,mcop,fg_norm,rank
	from proposal
	where prop_status='Y' 
	and  (prop_req_time != prop_app_time
	or prop_req_time_1 != prop_app_time_1
	or prop_req_time_2 != prop_app_time_2)
	and (( xmm_req is not null and xmm_req = xmm_app) 
	or (hst_req is not null and hst_req = hst_app) 
	or (nustar_req is not null and nustar_req = nustar_app) 
	or (noao_req is not null and noao_req = noao_app) 
	or (nrao_req is not null and nrao_req = nrao_app) 
	or (spitzer_req is not null and spitzer_req = spitzer_app) 
	or (swift_req is not null and swift_req = swift_app) 
	or (suzaku_req is not null and suzaku_req = suzaku_app) )
	order by proposal.panel_id,proposal.prop_id));
  $str = join($param{sep},("Panel","Proposal","Type","Last","Req","App","Joint","MCOP","NormG","Rank"));
  writeIt($jt_chk,$date,$str,"time_no_joint_reduction");
  $jt_chk->finish;

  my $all_chk = $dbh->prepare(qq( select distinct proposal.panel_id,proposal.prop_id,
	('"' || last_name || '"'), fg_norm,rank,type,big_proj
	from proposal,target
	where (prop_status in ('N','G') )
	and fg_norm >= 3.5 
	order by fg_norm desc,rank asc));
  $str = join($param{sep},("Panel","Proposal","Last","NormG","Rank","Type","SubType"));
  writeIt($all_chk,$date,$str,"all_rank_unapp");
  $tc_chk->finish;

}    
#**********#
# End Main #
#**********#


sub writeIt
{
  my($sth,$date,$hdr,$name) = @_;
  my($str,@row,$fname);

  # Print the output for each panel
  if ($param{sep} eq ",") {
    $fname = $date . "_" . $name . ".csv";
  } else {
    $fname = $date . "_" . $name . ".txt";
  }
  $sth->execute();
  open (OUT, ">$fname") ||
      die "Sorry, can't open $fname: $!\n";
  binmode(OUT, ":utf8");
  print STDERR "Writing $fname\n";
  print  OUT "$hdr\n";
  while (@row = $sth->fetchrow_array) {
      $str = join($param{sep},@row);
      print  OUT "$str\n";
  }
  close OUT;
}



## Internal Utility ##
# Name: parse_opts
# Purpose: define permitted commandline options
# Usage: parse_opts()
# Returns: nothing;
#          exits if an unrecognized parameter is given or if a required
#          parameter is not given
sub parse_opts {
  %param = (
	    U => undef,
	    sep => ",",
	    verbose => 0
	   );
  
  GetOptions( \%param,
	      "U=s",
	      "sep=s",
	      "verbose",
	      "version",
	      "help",
	    ) or exit(1);
  
  return if $param{help} or $param{version};
  
  
  my $err = 0;
  while ( my ( $par, $val ) = each ( %param ) ) {
    next if defined $val;
    warn("parameter `$par' not set\n");
    $err++;
  }
  
  exit(1) if $err;
}

## Internal Utility ##
# Name: usage
# Purpose: display help documentation and exit
# Usage: usage(0)
# Returns: nothing
sub usage {
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}


__END__

=head1 NAME

final_checks.pl - Script for printing final checks at end of review

=head1 USAGE

final_checks.pl -U cdo1

=head1 DESCRIPTION

This script will generate ascii output files of the final checks,
one file per type of check.

=head1 OPTIONS

B<final_check.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U> <user name>

peer review user name (required)

=item B<-help>

displays documentation for B<msfcReport.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 AUTHOR

Prefers to remain anonymous

=head1 VERSION

1.0
