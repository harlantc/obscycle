#!/usr/bin/perl

#*****************************************************************************
# migrate_tables .pl
#
# This script dumps the proposal information to tab-delimited tables.
#*****************************************************************************

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: migrate_tables.pl,v 1.1 2012/02/15 16:24:56 wink Exp $';

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
    
    my %exclude = ();
    if ($param{exclude}) {
	open (EXCLUDE, "$param{dir}/$param{exclude}") || 
	    die "Sorry, can't open $param{dir}/$param{exclude}: $!\n";
	foreach my $prop (<EXCLUDE>) {
	    chomp $prop;
	    $prop =~ s/^\s+//;
	    $prop =~ s/\s+$//;
	    $exclude{$prop} = 1;
	}
	close EXCLUDE;
    }
    
    my $list;
    if ($param{list}) {
	open (LIST, "$param{dir}/$param{list}") || die "Sorry, can't open $param{dir}/$param{list}: $!\n";
	foreach my $prop (<LIST>) {
	    chomp $prop;
	    $prop =~ s/^\s+//;
	    $prop =~ s/\s+$//;
	    $list .= "$prop,";
	}
	close LIST;
	$list =~ s/,$//;
    }
    
    open (PROP, ">$param{dir}/$param{out}_proposal") ||
	die "Sorry can't open $param{dir}/$param{out}_proposal: $!\n";
    print PROP "#proposal_number\tstatus\tfg_norm\thst\txmm\tnoao\tnrao\trxte\tspitzer\tsuzaku\tswift\tnustar\n";
    
    open (TARG, ">$param{dir}/$param{out}_target") ||
	die "Sorry can't open $param{dir}/$param{out}_target: $!\n";
    print TARG "#proposal_number\ttarg_status\ttargid\ttargnum\tapp_time\tapp_time+1\tapp_time+2\tprobability\n";
    
    open (GRID, ">$param{dir}/$param{out}_grid") ||
	die "Sorry can't open $param{dir}/$param{out}_grid: $!\n";
    print GRID "#proposal_number\ttarg_status\ttargid\ttargnum\tapp_nbr\tapp_nbr+1\tapp_nbr+2\n";
    
    open (TOO, ">$param{dir}/$param{out}_too+mon") ||
	die "Sorry can't open $param{dir}/$param{out}_too+mon: $!\n";
    print TOO "#proposal_number\ttarg_status\ttargid\ttargnum\tordr\tapp_time\tcharge_ao_str\n";
    
    open (ALT, ">$param{dir}/$param{out}_alt") ||
	die "Sorry can't open $param{dir}/$param{out}_alt: $!\n";
    print ALT "#alt_id\tapproved_count\n";
    
    open (ARC, ">$param{dir}/$param{out}_archive+theory") ||
	die "Sorry can't open $param{dir}/$param{out}_archive+theory: $!\n";
    print ARC "#prop_num\tstatus\tfg_norm\trecommended_funds\n";
    
    # Database connection 1
    my $dsn = "dbi:Pg:dbname=$param{U}";
    my $dbh = DBI->connect($dsn, "", "");
    
    my $get_proposal = $dbh->prepare(qq(select prop_id, fg_norm, rank, 
					joint_flag,
					hst_app, xmm_app, noao_app, nrao_app, 
					rxte_app, spitzer_app, suzaku_app,
					swift_app,nustar_app,
					prop_status, type, panel_id from 
					proposal where prop_status != 'B'
					order by prop_id));
    #override with this if proposal number is provided
    $get_proposal = $dbh->prepare(qq(select prop_id, fg_norm, rank, joint_flag,
				     hst_app, xmm_app, noao_app, nrao_app, 
				     rxte_app, spitzer_app, suzaku_app,
			 	     swift_app,nustar_app,
				     prop_status, type, panel_id from 
				     proposal where prop_status != 'B'
				     and prop_id = $param{proposal}
				     order by prop_id)) if $param{proposal};
    #override with this if list of proposal numbers is provided
    $get_proposal = $dbh->prepare(qq(select prop_id, fg_norm, rank, joint_flag,
				     hst_app, xmm_app, noao_app, nrao_app, 
				     rxte_app, spitzer_app, suzaku_app,
			 	     swift_app,nustar_app,
				     prop_status, type, panel_id from 
				     proposal where prop_status != 'B'
				     and prop_id in ($list)
				     order by prop_id)) if $param{list};
    
    # BPP grade is the average of  
    # 2*fg_avg (XVP) + fg_norm (regular Panels) 
    # we will recalculate just in case the normal panels have changed based
    # on final results
    my $get_grade_BPP = $dbh->prepare(qq( select panel_id,fg_avg,fg_norm 
		from proposal where
 		prop_id = ? and panel_id != 99));
    my $get_target = $dbh->prepare(qq(select targ_status, 
		targ_status_1, targ_status_2, monitor, 
		targ_num, 
		app_time, app_time_1, app_time_2, targ_id 
				      from target t where targ_status != 'B' and 
                                      prop_id = ?));
    my $get_too = $dbh->prepare(qq(select obs_status, app_obs_time, ordr, ao
				   from too where req_obs_time > 0 and
				   targ_id = ? and obs_status != 'B'));
    
    my $get_alt = $dbh->prepare(qq(select alt_id, app_cnt 
		from alternate_target_group,proposal 
		where prop_status = 'Y' and proposal.prop_id = ?
		and proposal.prop_id = alternate_target_group.prop_id));
    my $get_grid = $dbh->prepare(qq(select num_obs_app, num_obs_app_1, 
				    num_obs_app_2 from target where 
				    grid_name != '' and targ_status = 'Y' and
				    targ_id = ?));
    my $get_too_prob = $dbh->prepare(qq(select too_prob_app from target where 
                                        targ_status = 'Y' and 
					response_time != '' and targ_id = ?
					and too_prob_req != too_prob_app));
    my $get_high_pass_rank = $dbh->prepare(qq(select max(rank) from proposal 
					where panel_id = ? 
					and type not in ('ARCHIVE','THEORY')
					and prop_status = 'Y'));
    my $get_money_info = $dbh->prepare(qq(select prop_status, total_app_time 
					  from proposal where prop_id = ?));
    
    $get_proposal->execute();
    while (my($prop_id, $norm, $pan_rank, $joint, $hst, 
	      $xmm, $noao, $nrao, 
	      $rxte, $spitzer, $suzaku, $swift,$nustar,$status, 
	      $type, $panel_id) = $get_proposal->fetchrow_array) {
	
	next if $exclude{$prop_id};
	$hst = 0 if !$hst;
	$xmm = 0 if !$xmm;
	$noao = 0 if !$noao;
	$nrao = 0 if !$nrao;
	$rxte = 0 if !$rxte;
	$spitzer = 0 if !$spitzer;
	$suzaku = 0 if !$suzaku;
	$swift = 0 if !$swift;
	$nustar = 0 if !$nustar;
	
        if ($status =~ /G/i) {
            print STDERR "Warning: status is gray for $prop_id\n";
            $status = "N";
        }
	# GRADES: proposal_update_rank "proposal_number", normalized_rank, rank
	#   where normalized_rank is the letter grade  and 
	#         rank is the statistics rank (which can never be <= 3.5 if the
	#                                      proposal was REJECTED)
	if ($panel_id == 99) {
	    $get_grade_BPP->execute($prop_id);
            $norm = 0;
            my($ii) = 0;
            my($reportGrade) = 0;
            my(@tarr,$fg_norm,$fg_avg);
            while( @tarr = $get_grade_BPP->fetchrow_array) {
              ($panel_id,$fg_avg,$fg_norm) = @tarr;
              if( $panel_id == 98) {
                $reportGrade += $fg_avg;
                $reportGrade += $fg_avg;
                $ii += 2;
              } else {
                $ii++;
                $reportGrade += $fg_norm;
              }
            }
            $norm = sprintf("%3.2f",(($reportGrade+.00001)/$ii));
	    if ($status =~ /Y/ and $norm < 3.50) {
                    print STDOUT "ERROR: Calculated BPP grade for APPROVED $prop_id is $norm\n";
                    print STDOUT "       It was reset to 3.5\n";
		    $norm = 3.50;
            #print STDOUT "$prop_id\t$norm\n";
            }

	    $get_grade_BPP->finish;
	}
	elsif ($type =~ /ARCHIVE|THEORY/) {
	    # We do nothing for now
	}
	else {
	    # For a proposal which was rejected but has fg_norm >= 3.5, check the 
	    # rank and compare it the highest rank for approved proposals. If the 
	    # rank is > that,  assign a grade of 3.49 for Diane to use in the 
	    # letter.  If the rank is < that, keep the higher grade for Diane to 
	    # use in the letter. 
	    $get_high_pass_rank->execute($panel_id);
	    my ($pass_rank) = $get_high_pass_rank->fetchrow_array;
	    $get_high_pass_rank->finish;
	    if ($status =~ /N/ and $norm >= 3.50) {
		if ($pan_rank > $pass_rank) {
		    $norm = 3.49;
		}
	    }
	}
	
	# Proposal and Archive
	if ($type !~/ARCHIVE|THEORY/) {
	    print PROP "$prop_id\t$status\t$norm\t$hst\t$xmm\t$noao\t$nrao\t$rxte\t$spitzer\t$suzaku\t$swift\t$nustar\n";
	}
	else {
	    $get_money_info->execute($prop_id);
	    my $money = $get_money_info->fetchrow_array;
	    $get_money_info->finish;
	    $money = 0 if $status eq 'N';
	    print ARC "$prop_id\t$status\t$norm\t$money\t$noao\n";
	}
	
	# Alternate target group updates
	$get_alt->execute($prop_id);
	while (my($alt_id, $cnt) = $get_alt->fetchrow_array) {
	   print ALT "$alt_id\t$cnt\n";
  	}
	$get_alt->finish;
	
	# Targets
	if ($type !~ /ARCHIVE|THEORY/) {
	    $get_target->execute($prop_id);
	    while (my($targ_status, $targ_status_1, 
		      $targ_status_2, $monitor, $targ_num, $app_time, 
		      $app_time_1, $app_time_2, 
		      $targ_id) = $get_target->fetchrow_array) {
		$targ_status = 'N' if $status eq 'N';
		$targ_status = 'N' if $status eq 'G';
                if ($targ_status =~ /G/ ) {
                  print STDERR "Warning: status is gray for $prop_id, target $targ_id\n";
		  $targ_status = 'N'; 
                }

		$app_time = 0 if $targ_status eq 'N' or $status eq 'N';
		$app_time_1 = 0 if $targ_status_1 eq 'N' or $status eq 'N' or $targ_status eq 'N';
		$app_time_2 = 0 if $targ_status_2 eq 'N' or $status eq 'N' or $targ_status eq 'N';
		
		# Grid
		$get_grid->execute($targ_id);
		while (my($pnt, $pnt_1, $pnt_2) = $get_grid->fetchrow_array) {
		    $pnt = 0 if $targ_status eq 'N' or $status eq 'N';
		    $pnt_1 = 0 if $targ_status_1 eq 'N' or $status eq 'N';
		    $pnt_2 = 0 if $targ_status_2 eq 'N' or $status eq 'N';
		    print GRID
			qq($prop_id\t$targ_status\t$targ_id\t$targ_num\t$pnt\t$pnt_1\t$pnt_2\n);
		}
		$get_grid->finish;
		
		
		# set the TOO probability if it changed at Peer Review
		my $prob = '';
		if ($type =~ /TOO/ and $targ_status =~ /accepted/) {
		    # Only give Diane the too_prob only for TOOs, even if 
		    # too_prob was changed for accounting purposes
		    # too_update_probability "prop_id", targ_num, prob
		    $get_too_prob->execute($targ_id);
		    while (my ($prob_app) = $get_too_prob->fetchrow_array) {
			$prob = $prob_app;
		    }
		    $get_too_prob->finish;
		}
	    
		print TARG qq($prop_id\t$targ_status\t$targ_id\t$targ_num\t$app_time\t$app_time_1\t$app_time_2\t$prob\n);
		
		# TOOs and Monitors
		$get_too->execute($targ_id);
		while (my($obs_status, $obs_app_time,
			  $order, $ao) = $get_too->fetchrow_array) {
		    $obs_app_time = 0 if  $obs_status eq 'N' or 
		      $targ_status eq 'N' or 
			$status eq 'N';
		    $obs_app_time = 0 if  $obs_status eq 'G' ; 
		    print TOO 
			qq($prop_id\t$targ_status\t$targ_id\t$targ_num\t$order\t$obs_app_time\t$ao\n);
		}
		$get_too->finish;
	    }
	    $get_target->finish;
	    
	}
    }
    $get_proposal->finish;
}
  
#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts {

  %param = (
	    U => undef,
            dir => undef,
	    out => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
              "dir=s",
	      "out=s",
	      "proposal=i",
	      "list=s",
              "exclude=s",
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
sub usage {
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

migrate_tables.pl [options]

=head1 OPTIONS

B<migrate_tables.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U> database (required)

database to query

=item B<-dir> directory (required)

directory where output goes

=item B<-out> name (required)

base name of group files created

=item B<-proposal> proposal number

migrate single proposal

=item B<-list> filename

list of proposals to migrate

=item B<-exclude>

list of proposal to exclude in the migration.  This is ignored if the 
B<-proposal> option is used.

=item B<-help>

displays documentation for B<migrate.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script dumps the proposal information to tab-delimited tables.

=head1 AUTHOR

Sherry L. Winkelman
