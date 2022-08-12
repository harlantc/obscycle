#
# Proposal.pm - This object contains a data associated with a proposal,
#               including target and too objects when necessary
#
# Purpose: Provides a single place to access elements of a proposal
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Proposal;

use strict;
use Carp;
use Data::Dumper;
use Target;
use AltGrp;
use config;


use constant SEGTIME => 30.0;
use constant TAXRATE => 1.5;
use vars qw (%prop_fmts);
use base qw(Class::Accessor::Ref);
Proposal->mk_accessors(qw(dbh open_edit prop_id category tag_num last_name rank
			  type type_sort big_proj big_proj_sort num_monitor
			  tc_prop conflict rev_too title short_ttl abstract 	
			  scicat mcop mon_flag grid_flag
			  prop_app_time prop_app_time_1 prop_app_time_2
                          prop_req_time prop_req_time_1 prop_req_time_2
			  total_req_time total_req_time_1 total_req_time_2
                          total_app_time total_app_time_1 total_app_time_2
			  prop_req_hel prop_req_hel_1 prop_req_hel_2
			  prop_app_hel prop_app_hel_1 prop_app_hel_2
			  total_req_hel total_req_hel_1 total_req_hel_2
			  total_app_hel total_app_hel_1 total_app_hel_2
			  num_targets num_targets_1 num_targets_2
                          num_targ_req num_targ_req_1 num_targ_req_2
                          num_targ_app num_targ_app_1 num_targ_app_2
			  num_obs_req num_obs_req_1 num_obs_req_2
                          num_obs_app num_obs_app_1 num_obs_app_2
                          tax_tot tax_tot_1 tax_tot_2
                          tax_req tax_req_1 tax_req_2
		          rc_score_req rc_score_req_1 rc_score_req_2
                          rc_score_app rc_score_app_1 rc_score_app_2
			  tc_e_req tc_e_req_1 tc_e_req_2
                          tc_e_app tc_e_app_1 tc_e_app_2
                          tc_a_req tc_a_req_1 tc_a_req_2
                          tc_a_app tc_a_app_1 tc_a_app_2
                          tc_d_req tc_d_req_1 tc_d_req_2
                          tc_d_app tc_d_app_1 tc_d_app_2
			  joint_flag joint_sort panel_id group_id link_id 
			  hst_req noao_req rxte_req nrao_req xmm_req
			  spitzer_req suzaku_req swift_req nustar_req
			  hst_app noao_app rxte_app nrao_app xmm_app
			  spitzer_app suzaku_app swift_app nustar_app
			  vf_req vf_app f_req f_app s_req s_app vs_req vs_app
			  comments prop_status prop_status_bck temp_status
			  cdo_approval infeasible
			  pg1 pg2 pg3 pg4 pg5 pg6 pg7 pg8 pg9 pg10 pg11
			  pg_avg pg_med pg_stdev
			  g1 g2 g3 g4 g5 g6 g7 g8 g9 g10 g11 g12 g13 g14 g15
			  g16 g17 g18 g19 g20 g21 g22 g23 g24 g25
			  fg_avg fg_med fg_stdev fg_norm  g_cmt
			  ag1 ag2 ag3 ag4 ag5 ag6 ag7 ag8 ag9 ag10 ag11 ag12 
			  ag13 ag14 ag15
			  ag16 ag17 ag18 ag19 ag20 ag21 ag22 ag23 ag24 ag25
			  afg_avg afg_med afg_stdev afg_norm  a_cmt
			  sup_triage triage triage_sort
			  joint changed show_lower_triage targets alt_grps 
			  cum_time_temp 
			  cum_lcd_app_time cum_lcd_app_time_1 
                          cum_lcd_app_time_2
			  cum_lcd_app_hel cum_lcd_app_hel_1
			  cum_lcd_app_hel_2
                          cum_lcd_arc cum_lcd_the 
			  cum_lcd_tc cum_lcd_ftoo 
			  cum_lcd_vftoo cum_lcd_stoo
			  cum_lcd_hst cum_lcd_noao cum_lcd_rxte cum_lcd_nrao 
			  cum_lcd_spitzer cum_lcd_suzaku cum_lcd_swift cum_lcd_xmm  cum_lcd_nustar
                          cum_lcd_rc_score cum_lcd_rc_score_1 cum_lcd_rc_score_2
			  cum_lcd_lp_app_time cum_lcd_lp_app_time_1 
                          cum_lcd_lp_app_time_2
			  cum_lcd_lp_app_hel cum_lcd_lp_app_hel_1
			  cum_lcd_lp_app_hel_2
                          cum_lcd_lp_arc cum_lcd_lp_the 
			  cum_lcd_lp_tc cum_lcd_lp_ftoo 
			  cum_lcd_lp_vftoo cum_lcd_lp_stoo
			  cum_lcd_lp_hst cum_lcd_lp_noao cum_lcd_lp_rxte cum_lcd_lp_nrao 
			  cum_lcd_lp_spitzer cum_lcd_lp_suzaku cum_lcd_lp_swift cum_lcd_lp_xmm  cum_lcd_lp_nustar
                          cum_lcd_lp_rc_score cum_lcd_lp_rc_score_1 cum_lcd_lp_rc_score_2
			  cum_lcd_vlp_app_time cum_lcd_vlp_app_time_1 
                          cum_lcd_vlp_app_time_2
			  cum_lcd_vlp_app_hel cum_lcd_vlp_app_hel_1
			  cum_lcd_vlp_app_hel_2
                          cum_lcd_vlp_arc cum_lcd_vlp_the 
			  cum_lcd_vlp_tc cum_lcd_vlp_ftoo 
			  cum_lcd_vlp_vftoo cum_lcd_vlp_stoo
			  cum_lcd_vlp_hst cum_lcd_vlp_noao cum_lcd_vlp_rxte cum_lcd_vlp_nrao 
			  cum_lcd_vlp_spitzer cum_lcd_vlp_suzaku cum_lcd_vlp_swift cum_lcd_vlp_xmm  cum_lcd_vlp_nustar
                          cum_lcd_vlp_rc_score cum_lcd_vlp_rc_score_1 cum_lcd_vlp_rc_score_2
			  cum_fac_app_time cum_fac_app_time_1 
                          cum_fac_app_time_2
			  cum_fac_app_hel cum_fac_app_hel_1
			  cum_fac_app_hel_2
                          cum_fac_arc cum_fac_the 
			  cum_fac_tc cum_fac_ftoo 
			  cum_fac_vftoo cum_fac_stoo
			  cum_fac_hst cum_fac_noao cum_fac_rxte cum_fac_nrao 
			  cum_fac_spitzer cum_fac_suzaku cum_fac_swift cum_fac_xmm  cum_fac_nustar
                          cum_fac_rc_score cum_fac_rc_score_1 cum_fac_rc_score_2
			  hst_dtype noao_dtype rxte_dtype nrao_dtype 
			  xmm_dtype spitzer_dtype suzaku_dtype swift_dtype nustar_dtype
			  hst_units noao_units rxte_units nrao_units
			  xmm_units spitzer_units suzaku_units swift_units nustar_units
			  PROPOSAL_PROPERTIES TARGET_PROPERTIES 
			  JOINT_PROPERTIES GRADE_PROPERTIES TOO_PROPERTIES
			  ALT_GRP_PROPERTIES verbose taxrate segtime
			 ));
Proposal->mk_refaccessors(qw(total_app_time total_app_time_1 total_app_time_2
                             num_targ_app num_targ_app_1 num_targ_app_2
                             prop_status group_id
			     prop_app_time prop_app_time_1 prop_app_time_2
                             prop_req_time prop_req_time_1 prop_req_time_2
				 prop_req_hel prop_req_hel_1 prop_req_hel_2
				 prop_app_hel prop_app_hel_1 prop_app_hel_2
				 total_req_hel total_req_hel_1 total_req_hel_2
				 total_app_hel total_app_hel_1 total_app_hel_2
			     cum_lcd_arc cum_lcd_the 
                             cum_lcd_app_time cum_lcd_app_time_1 
                             cum_lcd_app_time_2
			     cum_lcd_lp_arc cum_lcd_lp_the 
                             cum_lcd_lp_app_time cum_lcd_lp_app_time_1 
                             cum_lcd_lp_app_time_2
			     cum_lcd_vlp_arc cum_lcd_vlp_the 
                             cum_lcd_vlp_app_time cum_lcd_vlp_app_time_1 
                             cum_lcd_vlp_app_time_2
			     cum_fac_arc cum_fac_the 
                             cum_fac_app_time cum_fac_app_time_1 
                             cum_fac_app_time_2
			     rank comments
			     cum_time_temp  
			     cum_lcd_rc_score cum_lcd_rc_score_1 cum_lcd_rc_score_2
			     cum_lcd_ftoo cum_lcd_vftoo cum_lcd_stoo
			     cum_lcd_lp_rc_score cum_lcd_lp_rc_score_1 cum_lcd_lp_rc_score_2
			     cum_lcd_lp_ftoo cum_lcd_lp_vftoo cum_lcd_lp_stoo
			     cum_lcd_vlp_rc_score cum_lcd_vlp_rc_score_1 cum_lcd_vlp_rc_score_2
			     cum_lcd_vlp_ftoo cum_lcd_vlp_vftoo cum_lcd_vlp_stoo
			     cum_fac_rc_score cum_fac_rc_score_1 cum_fac_rc_score_2
			     cum_fac_ftoo cum_fac_vftoo cum_fac_stoo
			     num_obs_app num_obs_app_1 num_obs_app_2
                             tc_prop 
                             tax_tot tax_tot_1 tax_tot_2
                             tax_req tax_req_1 tax_req_2
			     cum_lcd_hst cum_lcd_noao cum_lcd_rxte cum_lcd_nrao
			     cum_lcd_xmm  cum_lcd_spitzer cum_lcd_suzaku  cum_lcd_swift cum_lcd_nustar
			     cum_lcd_lp_hst cum_lcd_lp_noao cum_lcd_lp_rxte cum_lcd_lp_nrao
			     cum_lcd_lp_xmm  cum_lcd_lp_spitzer cum_lcd_lp_suzaku  cum_lcd_lp_swift cum_lcd_lp_nustar
			     cum_lcd_vlp_hst cum_lcd_vlp_noao cum_lcd_vlp_rxte cum_lcd_vlp_nrao
			     cum_lcd_vlp_xmm  cum_lcd_vlp_spitzer cum_lcd_vlp_suzaku  cum_lcd_vlp_swift cum_lcd_vlp_nustar
			     cum_fac_hst cum_fac_noao cum_fac_rxte cum_fac_nrao
			     cum_fac_xmm  cum_fac_spitzer cum_fac_suzaku cum_fac_swift cum_fac_nustar
			     sup_triage
			     fg_avg fg_med fg_stdev fg_norm triage temp_status
			     g1 g2 g3 g4 g5 g6 g7 g8 g9 g10 g11 g12 g13 g14 g15
			     g16 g17 g18 g19 g20 g21 g22 g23 g24 g25 g_cmt
			     afg_avg afg_med afg_stdev afg_norm  a_cmt
			     ag1 ag2 ag3 ag4 ag5 ag6 ag7 ag8 ag9 ag10 ag11 ag12 ag13 ag14 ag15
			     ag16 ag17 ag18 ag19 ag20 ag21 ag22 ag23 ag24 ag25
			     hst_app noao_app rxte_app nrao_app 
			     xmm_app spitzer_app suzaku_app swift_app nustar_app cdo_approval
			     vf_req vf_app f_req f_app s_req s_app vs_req 
			     vs_app
			     rc_score_req rc_score_req_1 rc_score_req_2
                             rc_score_app rc_score_app_1 rc_score_app_2
			     tc_e_req tc_e_req_1 tc_e_req_2
                             tc_e_app tc_e_app_1 tc_e_app_2
                             tc_a_req tc_a_req_1 tc_a_req_2
                             tc_a_app tc_a_app_1 tc_a_app_2
                             tc_d_req tc_d_req_1 tc_d_req_2
			     tc_d_app tc_d_app_1 tc_d_app_2
			     taxrate segtime infeasible
			     ));

%prop_fmts = (
total_req_time => "%7.2f",
total_req_time_1 => "%7.2f",
total_req_time_2 => "%7.2f",
total_app_time => "%7.2f",
total_app_time_1 => "%7.2f",
total_app_time_2 => "%7.2f",
total_req_hel => "%7.2f",
total_req_hel_1 => "%7.2f",
total_req_hel_2 => "%7.2f",
total_app_hel => "%7.2f",
total_app_hel_1 => "%7.2f",
total_app_hel_2 => "%7.2f",
num_targ_req  => "%3d",
num_targ_req_1  => "%3d",
num_targ_req_2  => "%3d",
num_targ_app  => "%3d",
num_targ_app_1  => "%3d",
num_targ_app_2  => "%3d",
prop_app_time => "%7.2f",
prop_app_time_1 => "%7.2f",
prop_app_time_2 => "%7.2f",
prop_req_time  => "%7.2f",
prop_req_time_1  => "%7.2f",
prop_req_time_2 => "%7.2f",
prop_app_hel => "%7.2f",
prop_app_hel_1 => "%7.2f",
prop_app_hel_2 => "%7.2f",
prop_req_hel  => "%7.2f",
prop_req_hel_1  => "%7.2f",
prop_req_hel_2 => "%7.2f",
cum_lcd_arc  => "%8.2f",
cum_lcd_the  => "%8.2f",
cum_lcd_app_time  => "%8.2f",
cum_lcd_app_time_1  => "%8.2f",
cum_lcd_app_time_2 => "%8.2f",
cum_lcd_app_hel  => "%8.2f",
cum_lcd_app_hel_1  => "%8.2f",
cum_lcd_app_hel_2 => "%8.2f",
cum_lcd_lp_arc  => "%8.2f",
cum_lcd_lp_the  => "%8.2f",
cum_lcd_lp_app_time  => "%8.2f",
cum_lcd_lp_app_time_1  => "%8.2f",
cum_lcd_lp_app_time_2 => "%8.2f",
cum_lcd_lp_app_hel  => "%8.2f",
cum_lcd_lp_app_hel_1  => "%8.2f",
cum_lcd_lp_app_hel_2 => "%8.2f",
cum_lcd_vlp_arc  => "%8.2f",
cum_lcd_vlp_the  => "%8.2f",
cum_lcd_vlp_app_time  => "%8.2f",
cum_lcd_vlp_app_time_1  => "%8.2f",
cum_lcd_vlp_app_hel  => "%8.2f",
cum_lcd_vlp_app_hel_1  => "%8.2f",
cum_lcd_vlp_app_hel_2  => "%8.2f",
cum_fac_arc  => "%8.2f",
cum_fac_the  => "%8.2f",
cum_fac_app_time  => "%8.2f",
cum_fac_app_time_1  => "%8.2f",
cum_fac_app_time_2 => "%8.2f",
cum_fac_app_hel  => "%8.2f",
cum_fac_app_hel_1  => "%8.2f",
cum_fac_app_hel_2 => "%8.2f",
rank => "%2.2d",
cum_time_temp  => "%7.2f",
cum_lcd_rc_score => "%8.2f",
cum_lcd_rc_score_1 => "%8.2f",
cum_lcd_rc_score_2 => "%8.2f",
cum_lcd_ftoo => "%7.2f",
cum_lcd_vftoo => "%7.2f",
cum_lcd_stoo=> "%7.2f",
cum_lcd_lp_rc_score => "%8.2f",
cum_lcd_lp_rc_score_1 => "%8.2f",
cum_lcd_lp_rc_score_2=> "%8.2f",
cum_lcd_lp_ftoo => "%7.2f",
cum_lcd_lp_vftoo => "%7.2f",
cum_lcd_lp_stoo=> "%7.2f",
cum_lcd_vlp_rc_score => "%8.2f",
cum_lcd_vlp_rc_score_1 => "%8.2f",
cum_lcd_vlp_rc_score_2=> "%8.2f",
cum_lcd_vlp_ftoo => "%7.2f",
cum_lcd_vlp_vftoo => "%7.2f",
cum_lcd_vlp_stoo=> "%7.2f",
cum_fac_rc_score => "%8.2f",
cum_fac_rc_score_1 => "%8.2f",
cum_fac_rc_score_2=> "%8.2f",
cum_fac_ftoo => "%7.2f",
cum_fac_vftoo => "%7.2f",
cum_fac_stoo=> "%7.2f",
num_obs_req => "%3d",
num_obs_req_1 => "%3d",
num_obs_req_2=> "%3d",
num_obs_app => "%3d",
num_obs_app_1 => "%3d",
num_obs_app_2=> "%3d",
tax_tot => "%7.2f",
tax_tot_1 => "%7.2f",
tax_tot_2=> "%7.2f",
tax_req => "%7.2f",
tax_req_1 => "%7.2f",
tax_req_2=> "%7.2f",
cum_lcd_hst => "%7.2f",
cum_lcd_noao => "%7.2f",
cum_lcd_rxte => "%7.2f",
cum_lcd_nrao=> "%7.2f",
cum_lcd_xmm => "%7.2f",
cum_lcd_spitzer => "%7.2f",
cum_lcd_suzaku => "%7.2f",
cum_lcd_swift => "%7.2f",
cum_lcd_nustar=> "%7.2f",
cum_lcd_lp_hst => "%7.2f",
cum_lcd_lp_noao => "%7.2f",
cum_lcd_lp_rxte => "%7.2f",
cum_lcd_lp_nrao=> "%7.2f",
cum_lcd_lp_xmm => "%7.2f",
cum_lcd_lp_spitzer => "%7.2f",
cum_lcd_lp_suzaku => "%7.2f",
cum_lcd_lp_swift => "%7.2f",
cum_lcd_lp_nustar=> "%7.2f",
cum_lcd_vlp_hst => "%7.2f",
cum_lcd_vlp_noao => "%7.2f",
cum_lcd_vlp_rxte => "%7.2f",
cum_lcd_vlp_nrao=> "%7.2f",
cum_lcd_vlp_xmm => "%7.2f",
cum_lcd_vlp_spitzer => "%7.2f",
cum_lcd_vlp_suzaku => "%7.2f",
cum_lcd_vlp_swift => "%7.2f",
cum_lcd_vlp_nustar=> "%7.2f",
cum_fac_hst => "%7.2f",
cum_fac_noao => "%7.2f",
cum_fac_rxte => "%7.2f",
cum_fac_nrao=> "%7.2f",
cum_fac_xmm => "%7.2f",
cum_fac_spitzer => "%7.2f",
cum_fac_suzaku => "%7.2f",
cum_fac_swift => "%7.2f",
cum_fac_nustar=> "%7.2f",
pg_avg => "%4.2f",
pg_med => "%4.2f",
pg_stdev => "%4.2f",
pg_norm => "%4.2f",
pg1 => "%4.2f",
pg2 => "%4.2f",
pg3 => "%4.2f",
pg4 => "%4.2f",
pg5 => "%4.2f",
pg6 => "%4.2f",
pg7 => "%4.2f",
pg8 => "%4.2f",
pg9 => "%4.2f",
pg10 => "%4.2f",
pg11 => "%4.2f",
fg_avg => "%4.2f",
fg_med => "%4.2f",
fg_stdev => "%4.2f",
fg_norm => "%4.2f",
g1 => "%4.2f",
g2 => "%4.2f",
g3 => "%4.2f",
g4 => "%4.2f",
g5 => "%4.2f",
g6 => "%4.2f",
g7 => "%4.2f",
g8 => "%4.2f",
g9 => "%4.2f",
g10 => "%4.2f",
g11 => "%4.2f",
g12 => "%4.2f",
g13 => "%4.2f",
g14 => "%4.2f",
g15=> "%4.2f",
g16 => "%4.2f",
g17 => "%4.2f",
g18 => "%4.2f",
g19 => "%4.2f",
g20 => "%4.2f",
g21 => "%4.2f",
g22 => "%4.2f",
g23 => "%4.2f",
g24 => "%4.2f",
g25=> "%4.2f",
afg_avg => "%4.2f",
afg_med => "%4.2f",
afg_stdev => "%4.2f",
afg_norm => "%4.2f",

ag1 => "%4.2f",
ag2 => "%4.2f",
ag3 => "%4.2f",
ag4 => "%4.2f",
ag5 => "%4.2f",
ag6 => "%4.2f",
ag7 => "%4.2f",
ag8 => "%4.2f",
ag9 => "%4.2f",
ag10 => "%4.2f",
ag11 => "%4.2f",
ag12 => "%4.2f",
ag13 => "%4.2f",
ag14 => "%4.2f",
ag15=> "%4.2f",
ag16 => "%4.2f",
ag17 => "%4.2f",
ag18 => "%4.2f",
ag19 => "%4.2f",
ag20 => "%4.2f",
ag21 => "%4.2f",
ag22 => "%4.2f",
ag23 => "%4.2f",
ag24 => "%4.2f",
ag25=> "%4.2f",
hst_req => "%d",
noao_req => "%7.2f",
rxte_req => "%7.2f",
nrao_req => "%7.2f",
xmm_req => "%7.2f",
spitzer_req => "%7.2f",
suzaku_req => "%7.2f",
swift_req => "%7.2f",
nustar_req => "%7.2f",
hst_app => "%d",
noao_app => "%7.2f",
rxte_app => "%7.2f",
nrao_app => "%7.2f",
xmm_app => "%7.2f",
spitzer_app => "%7.2f",
suzaku_app => "%7.2f",
swift_app => "%7.2f",
nustar_app => "%7.2f",
vf_req  => "%7.2f",
vf_app  => "%7.2f",
f_req  => "%7.2f",
f_app  => "%7.2f",
s_req  => "%7.2f",
s_app  => "%7.2f",
vs_req  => "%7.2f",
vs_app => "%7.2f",
rc_score_req => "%8.2f",
rc_score_req_1 => "%8.2f",
rc_score_req_2=> "%8.2f",
rc_score_app => "%8.2f",
rc_score_app_1 => "%8.2f",
rc_score_app_2=> "%8.2f",
tc_e_req => "%7.2f",
tc_e_req_1 => "%7.2f",
tc_e_req_2=> "%7.2f",
tc_e_app => "%7.2f",
tc_e_app_1 => "%7.2f",
tc_e_app_2=> "%7.2f",
tc_a_req => "%7.2f",
tc_a_req_1 => "%7.2f",
tc_a_req_2=> "%7.2f",
tc_a_app => "%7.2f",
tc_a_app_1 => "%7.2f",
tc_a_app_2=> "%7.2f",
tc_d_req => "%7.2f",
tc_d_req_1 => "%7.2f",
tc_d_req_2=> "%7.2f",
tc_d_app => "%7.2f",
tc_d_app_1 => "%7.2f",
tc_d_app_2=> "%7.2f",
taxrate => "%7.2f",
segtime  => "%7.2f",
);
## Class Method ##
# Name: new
# Purpose: create a new Proposal object
# Parameters: database handle
#             proposal id
#             panel id
#             verbosity
# Returns: Proposal object
sub new {
    my $self = {};
    bless $self,shift;
    $self->_init(@_);
    print "Proposal::new - creating new object\n" if $self->verbose > 2;
    return $self;
}

sub get_ref
{
  my($self,$member) = @_;
  my($ff) = $prop_fmts{$member};
  if ($ff) {
  #  print STDERR "getref  $member ---  " . $self->$member ."\n";
    if (defined $self->$member) {
      my $val = sprintf($ff,$self->$member);
      $self->SUPER::set($member, $val);
    }
  }
  #format then 
  return \$self->{$member};
}

## Internal Utility ##
# Name: _init
# Purpose: initializes a new Proposal object
# Parameters: database handle
#             proposal id
#             panel id
#             verbosity
# Returns: Proposal object
sub _init {
    my ($self, $dbh, $prop_id, $panel_id, $if, $verbose) = @_;
    my %init = %$self;
    my %changed = ();
    $init{changed} = \%changed;
    $init{verbose} = $verbose;
    $init{open_edit} = $if;

    my @PROPOSAL_PROPERTIES = (qw( prop_id last_name type title short_ttl abstract 
                                   scicat rank mon_flag grid_flag
				   total_req_time total_req_time_1 
                                   total_req_time_2
                                   total_app_time total_app_time_1 
                                   total_app_time_2
                                   num_targets num_targets_1 num_targets_2
				   rev_too mcop
                                   prop_app_time prop_app_time_1 
                                   prop_app_time_2
                                   prop_req_time prop_req_time_1 
                                   prop_req_time_2
				   prop_req_hel prop_req_hel_1 prop_req_hel_2
				   prop_app_hel prop_app_hel_1 prop_app_hel_2
				   total_req_hel total_req_hel_1 total_req_hel_2
				   total_app_hel total_app_hel_1 total_app_hel_2
				   num_targ_req num_targ_req_1 num_targ_req_2
                                   num_targ_app num_targ_app_1 num_targ_app_2
				   tax_tot tax_tot_1 tax_tot_2
                                   tax_req tax_req_1 tax_req_2
                                   temp_status
				   joint_flag panel_id prop_status group_id 
				   link_id category comments big_proj 
				   tag_num pg_avg pg_med pg_stdev 
				   fg_avg fg_med fg_stdev fg_norm
				   afg_avg afg_med afg_stdev afg_norm
				   triage triage_sort tc_prop conflict
				   prop_status_bck cdo_approval 
                                   num_obs_req num_obs_req_1 num_obs_req_2
                                   num_obs_app num_obs_app_1 num_obs_app_2
				   g1 g2 g3 g4 g5 g6 g7 g8 g9 g10 
				   g11 g12 g13 g14 g15 g16 g17 g18 g19 g20
                                   g21 g22 g23 g24 g25
				   ag1 ag2 ag3 ag4 ag5 ag6 ag7 ag8 ag9 ag10 
				   ag11 ag12 ag13 ag14 ag15 ag16 ag17 ag18 ag19
                                   ag20 ag21 ag22 ag23 ag24 ag25
                                   g_cmt a_cmt
				   sup_triage num_monitor
				   hst_app noao_app xmm_app nrao_app rxte_app 
                                   spitzer_app suzaku_app swift_app nustar_app
				   vf_req vf_app f_req f_app s_req s_app 
				   vs_req vs_app
				   rc_score_req rc_score_req_1 rc_score_req_2
                                   rc_score_app rc_score_app_1 rc_score_app_2
				   tc_e_req tc_e_req_1 tc_e_req_2
                                   tc_e_app tc_e_app_1 tc_e_app_2
                                   tc_a_req tc_a_req_1 tc_a_req_2
                                   tc_a_app tc_a_app_1 tc_a_app_2
				   tc_d_req tc_d_req_1 tc_d_req_2
                                   tc_d_app tc_d_app_1 tc_d_app_2
				   infeasible cum_arc cum_the
                                   cum_app_time cum_app_time_1 cum_app_time_2
		                           cum_app_hel cum_app_hel_1 cum_app_hel_2
				 ));
    my @TARGET_PROPERTIES = (qw( prop_id targ_name at_hel
				 detector grating targ_id targ_num time_crit response_time
				 too_prob_app too_start
			  	 rc_score rc_score_1 rc_score_2
				 req_time req_time_1 req_time_2 too_prob_req
                                 app_time app_time_1 app_time_2
				 req_hel req_hel_1 req_hel_2
				 app_hel app_hel_1 app_hel_2
                                 targ_status targ_status_1 targ_status_2
                                 num_obs_req num_obs_req_1 num_obs_req_2
				 num_obs_app num_obs_app_1 num_obs_app_2
                                 time_obs_req time_obs_req_1 time_obs_req_2
                                 time_obs_app time_obs_app_1 time_obs_app_2
				 alt_id monitor raster_scan rev_too
                                 grid_name max_radius uninterrupt 
				 multitelescope group_obs mcop
                                 group_interval group_interval_1 
                                 group_interval_2
				 group_name constr_in_remarks
				 num_pnt_req num_pnt_req_1 num_pnt_req_2
                                 num_pnt_app num_pnt_app_1 num_pnt_app_2
				 targ_status_bck
			         tc_group_app tc_group_app_1 tc_group_app_2
                                 tc_group_req tc_group_req_1 tc_group_req_2
                                 tc_grade_app tc_grade_app_1 tc_grade_app_2
				 tc_grade_req tc_grade_req_1 tc_grade_req_2
				 tc tc_1 tc_2 tc_req tc_req_1 tc_req_2
                                 tax tax_1 tax_2 tax_req tax_req_1 tax_req_2
                                 tc_too tc_too_1 tc_too_2
                                 tc_too_req tc_too_req_1 tc_too_req_2
                                 tc_time tc_time_1 tc_time_2
                                 tc_roll tc_coor
				 tc_phase t
				 tc_phase tc_phase_1 tc_phase_2
                                 tc_const_rem tc_const_rem_1 tc_const_rem_2
                                 tc_monitor tc_monitor_1 tc_monitor_2
                                 tc_monitor_req tc_monitor_req_1 
                                 tc_monitor_req_2
				 tc_override tc_override_1 tc_override_2
			       )); 
    my @JOINT_PROPERTIES = (qw( units datatype req_time app_time));
    my @GRADE_PROPERTIES = (qw( pg_avg pg_med pg_stdev 
				fg_avg fg_med fg_stdev fg_norm
				afg_avg  afg_med afg_stdev afg_norm));
    my @TOO_PROPERTIES = (qw( prop_id targ_id ordr ao req_obs_time app_obs_time
			      obs_status trigflag obs_status_bck num_too_pt
                              pre_min_lead pre_max_lead fractol));
    my @ALT_GRP_PROPERTIES = (qw( alt_id alt_grp_name req_cnt app_cnt));
    $init{PROPOSAL_PROPERTIES} = \@PROPOSAL_PROPERTIES;
    $init{TARGET_PROPERTIES} = \@TARGET_PROPERTIES;
    $init{JOINT_PROPERTIES} = \@JOINT_PROPERTIES;
    $init{GRADE_PROPERTIES} = \@GRADE_PROPERTIES;
    $init{TOO_PROPERTIES} = \@TOO_PROPERTIES;
    $init{ALT_GRP_PROPERTIES} = \@ALT_GRP_PROPERTIES;

    $init{show_lower_triage} = 0;
    $init{cum_app_time} = '';
    $init{cum_app_time_1} = '';
    $init{cum_app_time_2} = '';
    $init{cum_arc} = '';
    $init{cum_the} = '';

    $init{taxrate} = TAXRATE;
    $init{segtime} = SEGTIME;

    my $proposal_query = $dbh->prepare(qq(select * from proposal
					  where prop_id = ? and panel_id = ?));

    $proposal_query->execute($prop_id, $panel_id);
    my ($row_ref) = $proposal_query->fetchrow_hashref('NAME_lc');
    $proposal_query->finish;

    my (%joint);

    foreach my $xx (qw(hst_req noao_req rxte_req nrao_req 
			xmm_req spitzer_req suzaku_req swift_req nustar_req)) {
        if (defined($row_ref->{$xx})) {
          my $val = sprintf($prop_fmts{$xx},$row_ref->{$xx} );
          $init{$xx} = $val;
        } else {
          $init{$xx} = $row_ref->{$xx} ;
        }
    }
      
    my $observatory_query = $dbh->prepare(qq(select * from observatory 
					      where obs = ?));

    foreach my $observ (qw(hst noao xmm nrao rxte spitzer suzaku swift nustar)) {
	$observatory_query->execute(uc $observ);
	my ($obs, $units, $datatype) = $observatory_query->fetchrow_array;
	$observatory_query->finish;

	$init{"$observ\_dtype"} = $datatype;
	$init{"$observ\_units"} = $units;
    }


    
    foreach my $xx (@PROPOSAL_PROPERTIES) {
        my $val = $row_ref->{$xx};
        my $fmt = $prop_fmts{$xx};
        if (defined($row_ref->{$xx}) && $fmt) {
          $val = sprintf($prop_fmts{$xx},$val );
          $init{$xx} = $val;
        } else {
          $init{$xx} = $row_ref->{$xx} ;
        }
    }


    $row_ref->{prop_id} =~ /^\d{2}(\d{2})/;
    $init{cat_id} = $1;

    foreach my $xx (qw(pg1 pg2 pg3 pg4 pg5 pg6 pg7 pg8 pg9 pg10 pg11))   {
        my $val = $row_ref->{$xx};
        if (defined($row_ref->{$xx})) {
          $val = sprintf("%3.2f",$val );
          $init{$xx} = $val;
        } else {
          $init{$xx} = $row_ref->{$xx} ;
        }
    }

    $init{dbh} = $dbh;
    $init{joint} = \%joint;

    my $monitor = $dbh->prepare(qq(select count(*) from target where 
                                        monitor in ( 'Y' ,'P')
                                        and panel_id = ? and prop_id = ?));
    $monitor->execute($panel_id, $prop_id);
    ($init{num_monitor}) = $monitor->fetchrow_array;
    $monitor->finish;

    my @target_list;
    for (my $i = 1; $i <= $row_ref->{'num_targets'}; $i++) {
	my $target = new Target($dbh, TAXRATE, SEGTIME, $prop_id, $i, 
				\@TARGET_PROPERTIES, \@TOO_PROPERTIES, 
				$panel_id, $verbose);
	$target_list[$i] = $target;
    }
    $init{targets} = \@target_list;

    my %alt_grp_list;
    my $alt_grp_query = $dbh->prepare(qq(select alt_id from 
                                         alternate_target_group where 
                                         prop_id = ? and panel_id = ?));

    $alt_grp_query->execute($prop_id, $panel_id);
    while (my($alt_id) = $alt_grp_query->fetchrow_array) {
      my $alt_grp = new AltGrp($dbh, $prop_id, $alt_id, \@ALT_GRP_PROPERTIES, 
			       $panel_id, $verbose);
      $alt_grp_list{$alt_id} = $alt_grp;
    }
    $alt_grp_query->finish;
    $init{alt_grps} = \%alt_grp_list;

    %$self = %init;

    # needed for editable text fields 
    if (!defined $self->{g_cmt}) { $self->{g_cmt} =""; }
    if (!defined $self->{a_cmt}) { $self->{a_cmt} =""; }
    if (!defined $self->{comments}) { $self->{comments} =""; }

    $self->_pop_joint();
}

## Class Method ##
# Name: set
# Purpose: overloads the set accessor from Class::Accessor to update %changed
#          to reflect that there are unsaved changes in the object
# Parameters: field to access
#             array of values
# Returns: nothing
sub set {
    my($self, $key, @values) = @_;
    
    if ($key !~ /changed/) {
	my $changed = $self->changed;
	my %changed = %$changed;
	$changed{$key} = 1;
	$self->changed(\%changed);
    }
    
    $self->SUPER::set($key, @values);
}
 
## Class Method ##
# Name: dump
# Purpose: does a data dump of the proposal object
# Parameters: none
# Returns: nothing
sub dump {
    my $self = shift;
    print Dumper($self);
}

## Internal Utility ##
# Name: _pop_joint
# Purpose: initializes the joint hash from the database
# Parameters: none
# Returns: nothing
sub _pop_joint {
    my ($self) = @_;
    my $joint = $self->joint;
    my %joint = %$joint;
    my $observatory_query = $self->dbh->prepare(qq(select * from observatory 
						   where obs = ?));

    foreach my $observ (qw(hst noao xmm nrao rxte spitzer suzaku swift nustar)) {
	$observatory_query->execute(uc $observ);
	my ($obs, $units, $datatype) = $observatory_query->fetchrow_array;
	$observatory_query->finish;
	my %observatory;
	if ($self->get("$observ\_req") ){
	    $observatory{units} = $units;
	    $observatory{datatype} = $datatype;
	    $observatory{app_time} = $self->get_ref("$observ\_app");
	    $observatory{req_time} = $self->get("$observ\_req");
	}
	$joint{uc $obs} = \%observatory;
    }
}
    
## Class Method ##
# Name: proposal
# Purpose: provide general get/set accessor to all simple elements of a 
#          proposal
# Parameters: field
#             value if used as a setter
# Returns: value of field if value not supplied
sub proposal {
#don't think this is ever called???
    my ($self, $method, $value) = @_;
    if ($method =~ /comments/) {
	$self->comments($value) if $value;
	return $self->comments if !$value;
    }
    elsif ($method =~ /group_id/) {
	$self->group_id($value) if $value;
	return $self->group_id if !$value;
    }
    else {
        my($fmt) = $prop_fmts{$method};
        if ($fmt) {
	  return sprintf($fmt,$self->{$method}) if !$value;
        } else {
	  return $self->{$method} if !$value;
        }
    }
}

## Class Method ##
# Name: joint_prop
# Purpose: provide general get/set accessor to joint elements of a proposal
# Parameters: observatory
#             field
#             value if used as a setter
# Returns: value of field if value not supplied
sub joint_prop {
    my ($self, $obs, $method, $value) = @_;
    my $joint = $self->joint();
    my %joint = %$joint;
    my $obs_data = $joint{$obs};
    $$obs_data{$method} = $value if defined($value);
    return $$obs_data{$method} if !defined($value);
}

## Class Method ##
# Name: target
# Purpose: provide general get/set accessor to target elements of a proposal
# Parameters: target index
#             field
#             value if used as a setter
# Returns: value of field if value not supplied
sub target {
    my ($self, $index, $method, $value) = @_;
    my $targets = $self->targets();
    my @targets = @$targets;
    if ($method eq 'app_time') {
	$targets[$index]->app_time($value) if defined($value);
	return $targets[$index]->get_ref('app_time') if !defined($value);
    }
    elsif ($method eq 'app_time_1') {
	$targets[$index]->app_time_1($value) if defined($value);
	return $targets[$index]->get_ref('app_time_1') if !defined($value);
    }
    elsif ($method eq 'app_time_2') {
	$targets[$index]->app_time_2($value) if defined($value);
	return $targets[$index]->get_ref('app_time_2') if !defined($value);
    }
    elsif ($method eq 'targ_status') {
      $targets[$index]->targ_status($value) if defined($value);
      return $targets[$index]->get_ref('targ_status') if !defined($value);
    }
    elsif ($method eq 'targ_status_1') {
      $targets[$index]->targ_status_1($value) if defined($value);
      return $targets[$index]->get_ref('targ_status_1') if !defined($value);
    }
    elsif ($method eq 'targ_status_2') {
      $targets[$index]->targ_status_2($value) if defined($value);
      return $targets[$index]->get_ref('targ_status_2') if !defined($value);
    }
    else {
	return $targets[$index];
    }
}

## Class Method ##
# Name: alt_grp
# Purpose: provide general get/set accessor to alternate target group elements
#          of a proposal
# Parameters: alternate target group id
#             field
#             value if used as a setter
# Returns: reference to value of field if value not supplied
sub alt_grp {
  my ($self, $id, $method, $value) = @_;
  my $alt_grps = $self->alt_grps();
  my %alt_grps = %$alt_grps;
  if ($method =~ /app_cnt/) {
    $alt_grps{$id}->app_cnt($value) if defined($value);
    return $alt_grps{$id}->get_ref('app_cnt') if !defined($value);
  }
  else {
    return $alt_grps{$id};
  }
}

## Class Method ##
# Name: final_grade_ref
# Purpose: provide general get/set accessor to reference to a final grade
# Parameters: grade index
#             field
#             value if used as a setter
# Returns: reference to value of field if value not supplied
sub final_grade_ref {
    my ($self, $index, $value) = @_;
    my $grade = "g$index";
    return $self->get_ref($grade) if !defined($value);
}

## Class Method ##
# Name: final_grade
# Purpose: provide general get/set accessor to a final grade
# Parameters: grade index
#             field
#             value if used as a setter
# Returns: value of field if value not supplied
sub final_grade {
    my ($self, $index, $value) = @_;
    my $grade = "g$index";
    return $self->$grade if !defined($value);
    $self->$grade(sprintf("%4.2f",$value));
}

## Class Method ##
# Name: prelim_grade_ref
# Purpose: provide general get/set accessor to reference to a prelimnary grade
# Parameters: grade index
#             field
#             value if used as a setter
# Returns: reference to value of field if value not supplied
sub prelim_grade_ref {
    my ($self, $index, $value) = @_;
    my $grade = "pg$index";
    return $self->get_ref($grade) if !defined($value);
    $self->$grade(sprintf("%4.2f",$value));
}

## Class Method ##
# Name: prelim_grade
# Purpose: provide general get/set accessor to a preliminary grade
# Parameters: grade index
#             field
#             value if used as a setter
# Returns: value of field if value not supplied
sub prelim_grade {
    my ($self, $index, $value) = @_;
    my $grade = "pg$index";
    return $self->$grade if !defined($value);
    $self->$grade($value);
}

## Class Method ##
# Name: getGradeStats
# Purpose: determines the average and standard deviation for a list of grades
# Parameters: none
# Returns: average, standard deviation, median
sub getGradeStats {
  my ($self,$glbl) = @_;
  my @grades;
  # mean
  my $total = 0.0;
  my $count = 0;
  for (my $i = 1; $i <= $num_grades; $i++) {
      my $grade = $glbl . "$i";
      if (defined($self->$grade) and $self->$grade ne '') {
	  $count++;
	  $total += $self->$grade;
	  push @grades, $self->$grade;
      }
  }
  my $avg = 0.0;
  $avg = $total /$count if $count > 0;
  
  # stdev
  # We are dealing with a population, not a sample, so the formula becomes
  #  stdev = sqrt(sum(x^2)/N - mean^2)
  my $sum = 0.0;
  for ( my $i = 1; $i <= $num_grades; $i++) {
      my $grade = $glbl . "$i";
      if (defined($self->$grade) and $self->$grade ne '') {
	$sum += $self->$grade * $self->$grade;
#	  my $ss = $self->$grade - $avg;
#	  $var += ($ss * $ss);
      }
  }
  my $stdev = "n/a";
  if ($count) {
      my($xx) = ($sum/$count) - ($avg * $avg);
      if ($xx < 0) { $xx = 0; }
      $stdev = sqrt($xx);
      #$stdev = sqrt(($sum / $count) - ($avg * $avg));
  }

  # median
  my $median = 0.0;
  @grades = (sort {$a <=> $b} @grades);
  my $center = int($count/2);

  if ($count%2) {
    $median = $grades[$center];
  }
  else {
    $median = ($grades[$center] + $grades[$center-1]) / 2;
  }

  $avg = sprintf "%4.2f", $avg if defined($avg);
  $stdev = sprintf "%4.2f", $stdev if ($stdev !~ /n/);
  $median = sprintf "%4.2f", $median if defined($median);
  return $avg, $stdev, $median;
}
## Class Method ##
# Name: num_pg
# Purpose: calculate the number of prelim grades entered 
# Parameters: 
# Returns: integer
sub num_pg {
  my $self = shift;
  my $pgcnt=0;
  foreach my $xx (qw(pg1 pg2 pg3 pg4 pg5 pg6 pg7 pg8 pg9 pg10 pg11))   {
    my $val = $self->{$xx};
    $pgcnt+=1 if (defined $val);
  }
  return $pgcnt;
}


## Class Method ##
# Name: num_pnt
# Purpose: calculate the total number of pointings approved for the proposal
# Parameters: initialize flag
# Returns: integer
sub num_pnt {
  my $self = shift;
  my $init = shift;
  my $segments;
  my $alt_grps= {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->num_pnt($init);
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$segments += $target_list[$i]->num_pnt($init) if 
	  $target_list[$i]->targ_status !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$segments += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $segments = $self->num_obs_req;
  }
  else {
    $segments = 0;
  }

  return sprintf("%3d",$segments);
}

## Class Method ##
# Name: num_pnt_1
# Purpose: calculate the total number of pointings approved for the proposal
#          for cycle n+1
# Parameters: initialize flag
# Returns: integer
sub num_pnt_1 {
  my $self = shift;
  my $init = shift;
  my $segments;
  my $alt_grps= {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_1 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->num_pnt_1($init);
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$segments += $target_list[$i]->num_pnt_1($init) if 
	  $target_list[$i]->targ_status_1 !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$segments += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $segments = $self->num_obs_req_1;
  }
  else {
    $segments = 0;
  }

  return sprintf("%3d",$segments);
}

## Class Method ##
# Name: num_pnt_2
# Purpose: calculate the total number of pointings approved for the proposal
#          for cycle n+2
# Parameters: initialize flag
# Returns: integer
sub num_pnt_2 {
  my $self = shift;
  my $init = shift;
  my $segments;
  my $alt_grps= {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_2 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->num_pnt_2($init);
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$segments += $target_list[$i]->num_pnt_2($init) if 
	  $target_list[$i]->targ_status_2 !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$segments += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $segments = $self->num_obs_req_2;
  }
  else {
    $segments = 0;
  }

  return sprintf("%3d",$segments);
}

## Class Method ##
# Name: num_targs
# Purpose: calculate the total number of targets approved for the proposal
# Parameters: initialize flag
# Returns: integer
sub num_targs {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps = {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 1;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$count++ if $target_list[$i]->targ_status !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the number of
    # approved targets in group

    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @targ = reverse sort values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count++;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->num_targ_req;
  }
  else {
    $count = 0;
  }

  return $count;
}

## Class Method ##
# Name: num_targs_1
# Purpose: calculate the total number of targets approved for the proposal for
#          cycle n+1
# Parameters: initialize flag
# Returns: integer
sub num_targs_1 {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps = {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_1 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 1;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$count++ if $target_list[$i]->targ_status_1 !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the number of
    # approved targets in group

    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @targ = reverse sort values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count++;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->num_targ_req_1;
  }
  else {
    $count = 0;
  }

  return $count;
}

## Class Method ##
# Name: num_targs_2
# Purpose: calculate the total number of targets approved for the proposal for
#          cycle n+2
# Parameters: initialize flag
# Returns: integer
sub num_targs_2 {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps = {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_2 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 1;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$count++ if $target_list[$i]->targ_status_2 !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the number of
    # approved targets in group

    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @targ = reverse sort values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count++;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->num_targ_req_2;
  }
  else {
    $count = 0;
  }

  return $count;
}

## Class Method ##
# Name: num_tax
# Purpose: calculate the total tax of pointings approved for the proposal
# Parameters: initialize flag
# Returns: integer
sub num_tax {
  my $self = shift;
  my $init = shift;
  my $tax;
  my $alt_grps= {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->tax;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$tax += $target_list[$i]->tax if 
	  $target_list[$i]->targ_status !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$tax += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $tax = $self->tax_req;
  }
  else {
    $tax = 0;
  }

  $tax = sprintf("%7.2f",$tax) if defined $tax;
  return $tax;
}

## Class Method ##
# Name: num_tax_1
# Purpose: calculate the total tax of pointings approved for the proposal for
#          cycle n+1
# Parameters: initialize flag
# Returns: integer
sub num_tax_1 {
  my $self = shift;
  my $init = shift;
  my $tax;
  my $alt_grps= {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_1 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->tax_1;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$tax += $target_list[$i]->tax_1 if 
	  $target_list[$i]->targ_status_1 !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$tax += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $tax = $self->tax_req_1;
  }
  else {
    $tax = 0;
  }

  $tax = sprintf("%7.2f",$tax) if defined $tax;
  return $tax;
}

## Class Method ##
# Name: num_tax_2
# Purpose: calculate the total tax of pointings approved for the proposal for
#          cycle n+2
# Parameters: initialize flag
# Returns: integer
sub num_tax_2 {
  my $self = shift;
  my $init = shift;
  my $tax;
  my $alt_grps= {};

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the pointing information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_2 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->tax_2;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$tax += $target_list[$i]->tax_2 if 
	  $target_list[$i]->targ_status_2 !~ /N|G/;
      }
    }

    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$tax += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $tax = $self->tax_req_2;
  }
  else {
    $tax = 0;
  }

  $tax = sprintf("%7.2f",$tax) if defined $tax;
  return $tax;
}
## Class Method ##
# Name: num_too_new
# Purpose: calculate the total number of triggers for a response time
#  approved for the proposal
# Parameters: initialize flag
# Returns: integer
sub num_too_new {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};
  my $vfcnt=0;
  my $fcnt=0;
  my $scnt=0;
  my $vscnt=0;

  # We need to count the first target in a reverse TOO as a TOO trigger
  # not dealing with these... haven't used in years!
  if ($self->rev_too =~ /Y/) {
     return ($vfcnt,$fcnt,$scnt,$vscnt);
  }

  # All other non-TOOs can be skipped
  return undef,undef,undef,undef if $self->type !~ /TOO/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    # for each target, count the trigger, and the followups
    for (my $i = 1; $i < scalar @target_list; $i++) {
	if ($target_list[$i]->alt_id){
          my $altvf = 0; 
          my $altf = 0; 
          my $alts = 0; 
          my $altvs = 0;
	  # Add the pointing information to the alternate group hash for 
	  # processing later (add as string "vf f m s" then sort later and get worst case
	  if ($target_list[$i]->targ_status !~ /N|G/) {
            if ($target_list[$i]->too_start  < 5) { $altvf++; }
            elsif ($target_list[$i]->too_start  < 20) { $altf++; }
            elsif ($target_list[$i]->too_start  < 40) { $alts++; }
            elsif ($target_list[$i]->too_start  >= 40) { $altvs++; }
            # now add in .5 for any followups
            my $toos = $target_list[$i]->toos;
            my @too_list = @$toos;
            my $days = $target_list[$i]->too_start ;
            for (my $i = 1; $i < scalar @too_list; $i++) {
              if ($too_list[$i]->ordr > 1 && $too_list[$i]->obs_status !~ /N|G/) {
                $days += $too_list[$i]->pre_min_lead;
                if ($days < 5) { $altvf +=.5; }
                elsif ($days < 20) { $altf += .5; }
                elsif ($days < 40) { $alts += .5; }
                elsif ($days >= 40) { $altvs += .5; }
              }
            }
	  }
          my $astr = sprintf("%03.1f %03.1f %03.1f %03.1f ",$altvf,$altf,$alts,$altvs);
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = $astr;
	}    
	elsif ($target_list[$i]->targ_status !~ /N|G/) {
          if ($target_list[$i]->too_start  < 5) { $vfcnt++; }
          elsif ($target_list[$i]->too_start  < 20) { $fcnt++; }
          elsif ($target_list[$i]->too_start  < 40) { $scnt++; }
          elsif ($target_list[$i]->too_start  >= 40) { $vscnt++; }
          # now add in .5 for any followups
          my $toos = $target_list[$i]->toos;
          my @too_list = @$toos;
          my $days = $target_list[$i]->too_start;
 #print STDERR "TOO GOT Days = $days\n";
          for (my $i = 1; $i < scalar @too_list; $i++) {
            if ($too_list[$i]->ordr > 1 && $too_list[$i]->obs_status !~ /N|G/) {
              $days += $too_list[$i]->pre_min_lead;
 #print STDERR "Days = $days\n";
              if ($days < 5) { $vfcnt +=.5; }
              elsif ($days < 20) { $fcnt += .5; }
              elsif ($days < 40) { $scnt += .5; }
              elsif ($days >= 40) { $vscnt += .5; }
            }
          }
	}
      }

    # Cycle through alternate target group hash to get the highest number of 
    # VF, then F, then M, then S until you meet the approved count
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
        my @cnts = split(/ /,(pop @pnt));
        my $jj=0;
	$vfcnt += $cnts[$jj++];
	$fcnt += $cnts[$jj++];
	$scnt += $cnts[$jj++];
	$vscnt += $cnts[$jj++];
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    # return the <response_time>_req value for the proposal
    $vfcnt = $self->get("vf_req");
    $fcnt = $self->get("f_req");
    $scnt = $self->get("s_req");
    $vscnt = $self->get("vs_req");
  }

  return ($vfcnt,$fcnt,$scnt,$vscnt);
}

## Class Method ##
# Name: num_too
# Purpose: calculate the total number of triggers for a response time
#  approved for the proposal
# Parameters: response (vf, f, s, vs)
#             initialize flag
# Returns: integer
sub num_too {
  my $self = shift;
  my $response = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};
  my %resp = (vf => '0-5',
	      f => '5-20',
	      s => '20-30',
	      vs => '>30');

  # We need to count the first target in a reverse TOO as a TOO trigger
  if ($self->rev_too =~ /Y/) {
    $count = 0;
    if ($self->prop_status =~ /Y/ or $init) {
      my $targets = $self->targets;
      my @target_list = @$targets;
      for (my $i = 1; $i < scalar @target_list; $i++) {
	$count++ if ($target_list[$i]->rev_too =~ /$resp{$response}/ and 
		     $target_list[$i]->targ_status !~ /N|G/);
      }
    }
    return $count;
  }

  # All other non-TOOs can be skipped
  return undef if $self->type !~ /TOO/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->response_time =~ /$resp{$response}/){
	if ($target_list[$i]->alt_id){
	  # Add the pointing information to the alternate group hash for 
	  # processing later
	  if ($target_list[$i]->targ_status !~ /N|G/) {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 1;
	  }
	  else {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	  }
	}    
	else {
	  $count++ if $target_list[$i]->targ_status !~ /N|G/;
	}
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    # return the <response_time>_req value for the proposal
    $count = $self->get("${response}_req");
  }
  else {
    $count = 0;
  }

  return $count;
}

## Class Method ##
# Name: num_rc_score
# Purpose: calculate the total resource cost (RC) approved 
#          for the proposal
# Returns: integer
sub num_rc_score {
  my $self = shift;
  my $init = shift;
  my $count = 0;
  my $alt_grps= {};

  # No resource cost for these
  return $count if $self->type =~ /ARC|THE/;

  # Ignore entries with resource score < 0
  if ($self->prop_status =~ /Y/ or $init) {
    # Count all the targets
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->targ_status !~ /N|G/) {
        $count += $target_list[$i]->rc_score if 
          $target_list[$i]->rc_score > 0;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->get("rc_score_req");
  }
  else {
    $count = 0;
  }
  $count = sprintf("%7.2f",$count);
  return $count;
}

## Class Method ##
# Name: num_rc_score_1
# Purpose: calculate the total resource cost (RC) approved 
#          for the proposal for cycle n+2
# Returns: integer
sub num_rc_score_1 {
  my $self = shift;
  my $init = shift;
  my $count = 0;
  my $alt_grps= {};

  # No resource cost for these
  return $count if $self->type =~ /ARC|THE/;

  # Ignore entries with resource score < 0
  if ($self->prop_status =~ /Y/ or $init) {
    # Count all the targets
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->targ_status_1 !~ /N|G/) {
        $count += $target_list[$i]->rc_score_1 if 
          $target_list[$i]->rc_score_1 > 0;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->get("rc_score_req_1");
  }
  else {
    $count = 0;
  }
  $count = sprintf("%7.2f",$count);
  return $count;
}

## Class Method ##
# Name: num_rc_score_2
# Purpose: calculate the total resource cost (RC) approved 
#          for the proposal for cycle n+2
# Returns: integer
sub num_rc_score_2 {
  my $self = shift;
  my $init = shift;
  my $count = 0;
  my $alt_grps= {};

  # No resource cost for these
  return $count if $self->type =~ /ARC|THE/;

  # Ignore entries with resource score < 0
  if ($self->prop_status =~ /Y/ or $init) {
    # Count all the targets
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->targ_status_2 !~ /N|G/) {
        $count += $target_list[$i]->rc_score_2 if 
          $target_list[$i]->rc_score_2 > 0;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->get("rc_score_req_2");
  }
  else {
    $count = 0;
  }
  $count = sprintf("%7.2f",$count);
  return $count;
}

## Class Method ##
# Name: num_tc
# Purpose: calculate the total number TCs approved for a TC grade
#          for the proposal
# Parameters: grade (e, a, d)
#             initialize flag
# Returns: integer
sub num_tc {
  my $self = shift;
  my $grade = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->tc_grade_app =~ /$grade/){
	if ($target_list[$i]->alt_id){
	  # Add the pointing information to the alternate group hash for 
	  # processing later
	  if ($target_list[$i]->targ_status !~ /N|G/) {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	      $target_list[$i]->tc;
	  }
	  else {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	  }
	}    
	else {
	  $count += $target_list[$i]->tc if 
	  $target_list[$i]->targ_status !~ /N|G/;
	}
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->get("tc_${grade}_req");
  }
  else {
    $count = 0;
  }
  $count = sprintf("%7.2f",$count);
  return $count;
}

## Class Method ##
# Name: num_tc_1
# Purpose: calculate the total number TCs approved for a TC grade
#          for the proposal for cycle n+1
# Parameters: grade (e, a, d)
#             initialize flag
# Returns: integer
sub num_tc_1 {
  my $self = shift;
  my $grade = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->tc_grade_app_1 =~ /$grade/){
	if ($target_list[$i]->alt_id){
	  # Add the pointing information to the alternate group hash for 
	  # processing later
	  if ($target_list[$i]->targ_status_1 !~ /N|G/) {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	      $target_list[$i]->tc_1;
	  }
	  else {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	  }
	}    
	else {
	  $count += $target_list[$i]->tc_1 if 
	  $target_list[$i]->targ_status_1 !~ /N|G/;
	}
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->get("tc_${grade}_req_1");
  }
  else {
    $count = 0;
  }

  $count = sprintf("%7.2f",$count) if defined $count;
  return $count;
}

## Class Method ##
# Name: num_tc_2
# Purpose: calculate the total number TCs approved for a TC grade
#          for the proposal for cycle n+2
# Parameters: grade (e, a, d)
#             initialize flag
# Returns: integer
sub num_tc_2 {
  my $self = shift;
  my $grade = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->tc_grade_app_2 =~ /$grade/){
	if ($target_list[$i]->alt_id){
	  # Add the pointing information to the alternate group hash for 
	  # processing later
	  if ($target_list[$i]->targ_status_2 !~ /N|G/) {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	      $target_list[$i]->tc_2;
	  }
	  else {
	    $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	  }
	}    
	else {
	  $count += $target_list[$i]->tc_2 if 
	  $target_list[$i]->targ_status_2 !~ /N|G/;
	}
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @pnt = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @pnt;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->get("tc_${grade}_req_2");
  }
  else {
    $count = 0;
  }

  $count = sprintf("%7.2f",$count) if defined $count;
  return $count;
}

## Class Method ##
# Name: prop_time
# Purpose: calculate the total approved time with probability + tax
#          for the proposal
# Parameters: initialize flag
# Returns: float
sub prop_time {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the total time information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->app_time * $target_list[$i]->too_prob_app + 
	      $target_list[$i]->tax;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
         if ($target_list[$i]->monitor =~ /P/) {
	   $count += $target_list[$i]->app_time  +
	    $target_list[$i]->tax if $target_list[$i]->targ_status !~ /N|G/;
         } else {
	   $count += $target_list[$i]->app_time * 
	    $target_list[$i]->too_prob_app + 
	    $target_list[$i]->tax if $target_list[$i]->targ_status !~ /N|G/;
         }
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @time = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @time;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->prop_req_time;
  }
  else {
    $count = 0;
  }

  $count = sprintf("%7.2f",$count) if defined $count;
  return $count;
}

## Class Method ##
# Name: prop_time_1
# Purpose: calculate the total approved time with probability + tax
#          for the proposal for cycle n+1
# Parameters: initialize flag
# Returns: float
sub prop_time_1 {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the total time information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_1 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->app_time_1 * $target_list[$i]->too_prob_app + 
	      $target_list[$i]->tax_1;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
        if ($target_list[$i]->monitor =~ /P/) {
	  $count += $target_list[$i]->app_time_1 + 
	  $target_list[$i]->tax_1 if $target_list[$i]->targ_status_1 !~ /N|G/;
        } else {
	  $count += $target_list[$i]->app_time_1 * 
	  $target_list[$i]->too_prob_app + 
	  $target_list[$i]->tax_1 if $target_list[$i]->targ_status_1 !~ /N|G/;
        }
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @time = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @time;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->prop_req_time_1;
  }
  else {
    $count = 0;
  }

  $count = sprintf("%7.2f",$count) if defined $count;
  return $count;
}

## Class Method ##
# Name: prop_time_2
# Purpose: calculate the total approved time with probability + tax
#          for the proposal for cycle n+2
# Parameters: initialize flag
# Returns: float
sub prop_time_2 {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the total time information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_2 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->app_time_2 * $target_list[$i]->too_prob_app + 
	      $target_list[$i]->tax_2;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
        if ($target_list[$i]->monitor =~ /P/) {
	  $count += $target_list[$i]->app_time_2 + 
	  $target_list[$i]->tax_2 if $target_list[$i]->targ_status_2 !~ /N|G/;
        } else {
	  $count += $target_list[$i]->app_time_2 * 
	  $target_list[$i]->too_prob_app + 
	  $target_list[$i]->tax_2 if $target_list[$i]->targ_status_2 !~ /N|G/;
        }
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @time = sort {$a <=> $b} values %$targids;
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @time;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->prop_req_time_2;
  }
  else {
    $count = 0;
  }

  $count = sprintf("%7.2f",$count) if defined $count;
  return $count;
}

## Class Method ##
# Name: prop_hel
# Purpose: calculate the total HEL approved time with probability + tax
#          for the proposal
# Parameters: initialize flag
# Returns: float
sub prop_hel {
	my $self = shift;
	my $init = shift;
	my $count;
	my $alt_grps= {};

	return '' if $self->type =~ /ARC|THE/;

	if ($self->prop_status =~ /Y/ or $init) {
		# First count the non-alternate target groups
		my $targets = $self->targets;
		my @target_list = @$targets;
		for (my $i = 1; $i < scalar @target_list; $i++) {
			if ($target_list[$i]->at_hel eq 'Y') {
				if ($target_list[$i]->alt_id) {
					# Add the total time information to the alternate group hash for
					# processing later
					if ($target_list[$i]->targ_status !~ /N|G/) {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} =
							$target_list[$i]->app_time * $target_list[$i]->too_prob_app +
								$target_list[$i]->tax;
					}
					else {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
					}
				}
				else {
					if ($target_list[$i]->monitor =~ /P/) {
						$count += $target_list[$i]->app_time +
							$target_list[$i]->tax if $target_list[$i]->targ_status !~ /N|G/;
					}
					else {
						$count += $target_list[$i]->app_time *
							$target_list[$i]->too_prob_app +
							$target_list[$i]->tax if $target_list[$i]->targ_status !~ /N|G/;
					}
				}
			}
		}
		# Cycle through alternate target group hash to get the highest number of
		# pointings in group
		#
		# Assume that number of pointings from an alternate target group is
		# determined in descending num_pnt for the targets
		foreach my $alt_id (keys %$alt_grps) {
			my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
			my $targids = $$alt_grps{$alt_id};
			my @time = sort {$a <=> $b} values %$targids;
			for (my $i = 1; $i <= $$num_targs; $i++) {
				$count += pop @time;
			}
		}
	}
	elsif ($self->prop_status =~ /$STAT_BPP/) {
		$count = $self->prop_req_time;
	}
	else {
		$count = 0;
	}

	$count = sprintf("%7.2f",$count) if defined $count;
	return $count;
}

## Class Method ##
# Name: prop_hel_1
# Purpose: calculate the total approved time with probability + tax
#          for the proposal for cycle n+1
# Parameters: initialize flag
# Returns: float
sub prop_hel_1 {
	my $self = shift;
	my $init = shift;
	my $count;
	my $alt_grps= {};

	return '' if $self->type =~ /ARC|THE/;

	if ($self->prop_status =~ /Y/ or $init) {
		# First count the non-alternate target groups
		my $targets = $self->targets;
		my @target_list = @$targets;
		for (my $i = 1; $i < scalar @target_list; $i++) {
			if ($target_list[$i]->at_hel eq 'Y') {
				if ($target_list[$i]->alt_id) {
					# Add the total time information to the alternate group hash for
					# processing later
					if ($target_list[$i]->targ_status_1 !~ /N|G/) {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} =
							$target_list[$i]->app_time_1 * $target_list[$i]->too_prob_app +
								$target_list[$i]->tax_1;
					}
					else {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
					}
				}
				else {
					if ($target_list[$i]->monitor =~ /P/) {
						$count += $target_list[$i]->app_time_1 +
							$target_list[$i]->tax_1 if $target_list[$i]->targ_status_1 !~ /N|G/;
					}
					else {
						$count += $target_list[$i]->app_time_1 *
							$target_list[$i]->too_prob_app +
							$target_list[$i]->tax_1 if $target_list[$i]->targ_status_1 !~ /N|G/;
					}
				}
			}
		}
		# Cycle through alternate target group hash to get the highest number of
		# pointings in group
		#
		# Assume that number of pointings from an alternate target group is
		# determined in descending num_pnt for the targets
		foreach my $alt_id (keys %$alt_grps) {
			my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
			my $targids = $$alt_grps{$alt_id};
			my @time = sort {$a <=> $b} values %$targids;
			for (my $i = 1; $i <= $$num_targs; $i++) {
				$count += pop @time;
			}
		}
	}
	elsif ($self->prop_status =~ /$STAT_BPP/) {
		$count = $self->prop_req_time_1;
	}
	else {
		$count = 0;
	}

	$count = sprintf("%7.2f",$count) if defined $count;
	return $count;
}

## Class Method ##
# Name: prop_hel_2
# Purpose: calculate the total approved HEL time with probability + tax
#          for the proposal for cycle n+2
# Parameters: initialize flag
# Returns: float
sub prop_hel_2 {
	my $self = shift;
	my $init = shift;
	my $count;
	my $alt_grps= {};

	return '' if $self->type =~ /ARC|THE/;

	if ($self->prop_status =~ /Y/ or $init) {
		# First count the non-alternate target groups
		my $targets = $self->targets;
		my @target_list = @$targets;
		for (my $i = 1; $i < scalar @target_list; $i++) {
			if ($target_list[$i]->at_hel eq 'Y') {
				if ($target_list[$i]->alt_id) {
					# Add the total time information to the alternate group hash for
					# processing later
					if ($target_list[$i]->targ_status_2 !~ /N|G/) {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} =
							$target_list[$i]->app_time_2 * $target_list[$i]->too_prob_app +
								$target_list[$i]->tax_2;
					}
					else {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
					}
				}
				else {
					if ($target_list[$i]->monitor =~ /P/) {
						$count += $target_list[$i]->app_time_2 +
							$target_list[$i]->tax_2 if $target_list[$i]->targ_status_2 !~ /N|G/;
					}
					else {
						$count += $target_list[$i]->app_time_2 *
							$target_list[$i]->too_prob_app +
							$target_list[$i]->tax_2 if $target_list[$i]->targ_status_2 !~ /N|G/;
					}
				}
			}
		}
		# Cycle through alternate target group hash to get the highest number of
		# pointings in group
		#
		# Assume that number of pointings from an alternate target group is
		# determined in descending num_pnt for the targets
		foreach my $alt_id (keys %$alt_grps) {
			my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
			my $targids = $$alt_grps{$alt_id};
			my @time = sort {$a <=> $b} values %$targids;
			for (my $i = 1; $i <= $$num_targs; $i++) {
				$count += pop @time;
			}
		}
	}
	elsif ($self->prop_status =~ /$STAT_BPP/) {
		$count = $self->prop_req_time_2;
	}
	else {
		$count = 0;
	}

	$count = sprintf("%7.2f",$count) if defined $count;
	return $count;
}

## Class Method ##
# Name: calc_status
# Purpose: determines the status of the proposal
# Parameters: none
# Returns: status
sub calc_status {
  my $self = shift;

  return $self->prop_status if $self->prop_status =~ /$STAT_BPP/;
  return 'N' if $self->infeasible eq 'Y';
  return 'Y' if $self->infeasible eq 'N' && $self->type =~ /ARC|THE/;

  # The status is Y if any targets are approved
  my $targets = $self->targets;
  my $status = 'N';
  foreach my $target (@$targets) {
    next if !$target;  # This is because target[0] is undefined
    $status = 'Y' if $target->targ_status =~ /Y/;
  }

  return $status;
}

## Class Method ##
# Name: tot_time
# Purpose: calculate the total approved time with no probability + tax
#          for the proposal
# Parameters: initialize flag
# Returns: float
sub tot_time {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the total time information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->app_time;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$count += $target_list[$i]->app_time if 
	  $target_list[$i]->targ_status !~ /N|G/;
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @time = sort {$a <=> $b} values %$targids;
      foreach my $targid (%$targids) {
      }
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @time;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->total_req_time;
  }
  else {
    $count = 0;
  }

  return $count;
}

## Class Method ##
# Name: tot_time_1
# Purpose: calculate the total approved time with no probability + tax
#          for the proposal for cycle n+1
# Parameters: initialize flag
# Returns: float
sub tot_time_1 {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the total time information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_1 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->app_time_1;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$count += $target_list[$i]->app_time_1 if 
	  $target_list[$i]->targ_status_1 !~ /N|G/;
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @time = sort {$a <=> $b} values %$targids;
      foreach my $targid (%$targids) {
      }
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @time;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->total_req_time_1;
  }
  else {
    $count = 0;
  }

  return $count;
}

## Class Method ##
# Name: tot_time_2
# Purpose: calculate the total approved time with no probability + tax
#          for the proposal for cycle n+2
# Parameters: initialize flag
# Returns: float
sub tot_time_2 {
  my $self = shift;
  my $init = shift;
  my $count;
  my $alt_grps= {};

  return '' if $self->type =~ /ARC|THE/;

  if ($self->prop_status =~ /Y/ or $init) {
    # First count the non-alternate target groups
    my $targets = $self->targets;
    my @target_list = @$targets;
    for (my $i = 1; $i < scalar @target_list; $i++) {
      if ($target_list[$i]->alt_id){
	# Add the total time information to the alternate group hash for 
	# processing later
	if ($target_list[$i]->targ_status_2 !~ /N|G/) {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 
	    $target_list[$i]->app_time_2;
	}
	else {
	  $$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
	}
      }    
      else {
	$count += $target_list[$i]->app_time_2 if 
	  $target_list[$i]->targ_status_2 !~ /N|G/;
      }
    }
    # Cycle through alternate target group hash to get the highest number of 
    # pointings in group
    #
    # Assume that number of pointings from an alternate target group is 
    # determined in descending num_pnt for the targets
    foreach my $alt_id (keys %$alt_grps) {
      my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
      my $targids = $$alt_grps{$alt_id};
      my @time = sort {$a <=> $b} values %$targids;
      foreach my $targid (%$targids) {
      }
      for (my $i = 1; $i <= $$num_targs; $i++) {
	$count += pop @time;
      }
    }
  }
  elsif ($self->prop_status =~ /$STAT_BPP/) {
    $count = $self->total_req_time_2;
  }
  else {
    $count = 0;
  }

  return $count;
}

## Class Method ##
# Name: tot_hel
# Purpose: calculate the total approved HEL time with no probability + tax
#          for the proposal
# Parameters: initialize flag
# Returns: float
sub tot_hel {
	my $self = shift;
	my $init = shift;
	my $count;
	my $alt_grps= {};

	return '' if $self->type =~ /ARC|THE/;

	if ($self->prop_status =~ /Y/ or $init) {
		# First count the non-alternate target groups
		my $targets = $self->targets;
		my @target_list = @$targets;
		for (my $i = 1; $i < scalar @target_list; $i++) {
			if ($target_list[$i]->at_hel eq 'Y') {
				if ($target_list[$i]->alt_id) {
					# Add the total time information to the alternate group hash for
					# processing later
					if ($target_list[$i]->targ_status !~ /N|G/) {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} =
							$target_list[$i]->app_time;
					}
					else {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
					}
				}
				else {
					$count += $target_list[$i]->app_time if
						$target_list[$i]->targ_status !~ /N|G/;
				}
			}
		}
		# Cycle through alternate target group hash to get the highest number of
		# pointings in group
		#
		# Assume that number of pointings from an alternate target group is
		# determined in descending num_pnt for the targets
		foreach my $alt_id (keys %$alt_grps) {
			my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
			my $targids = $$alt_grps{$alt_id};
			my @time = sort {$a <=> $b} values %$targids;
			foreach my $targid (%$targids) {
			}
			for (my $i = 1; $i <= $$num_targs; $i++) {
				$count += pop @time;
			}
		}
	}
	elsif ($self->prop_status =~ /$STAT_BPP/) {
		$count = $self->total_req_time;
	}
	else {
		$count = 0;
	}

	return $count;
}

## Class Method ##
# Name: tot_hel_1
# Purpose: calculate the total approved HEL time with no probability + tax
#          for the proposal for cycle n+1
# Parameters: initialize flag
# Returns: float
sub tot_hel_1 {
	my $self = shift;
	my $init = shift;
	my $count;
	my $alt_grps= {};

	return '' if $self->type =~ /ARC|THE/;

	if ($self->prop_status =~ /Y/ or $init) {
		# First count the non-alternate target groups
		my $targets = $self->targets;
		my @target_list = @$targets;
		for (my $i = 1; $i < scalar @target_list; $i++) {
			if ($target_list[$i]->at_hel eq 'Y') {
				if ($target_list[$i]->alt_id) {
					# Add the total time information to the alternate group hash for
					# processing later
					if ($target_list[$i]->targ_status_1 !~ /N|G/) {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} =
							$target_list[$i]->app_time_1;
					}
					else {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
					}
				}
				else {
					$count += $target_list[$i]->app_time_1 if
						$target_list[$i]->targ_status_1 !~ /N|G/;
				}
			}
		}
		# Cycle through alternate target group hash to get the highest number of
		# pointings in group
		#
		# Assume that number of pointings from an alternate target group is
		# determined in descending num_pnt for the targets
		foreach my $alt_id (keys %$alt_grps) {
			my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
			my $targids = $$alt_grps{$alt_id};
			my @time = sort {$a <=> $b} values %$targids;
			foreach my $targid (%$targids) {
			}
			for (my $i = 1; $i <= $$num_targs; $i++) {
				$count += pop @time;
			}
		}
	}
	elsif ($self->prop_status =~ /$STAT_BPP/) {
		$count = $self->total_req_time_1;
	}
	else {
		$count = 0;
	}

	return $count;
}

## Class Method ##
# Name: tot_hel_2
# Purpose: calculate the total approved HEL time with no probability + tax
#          for the proposal for cycle n+2
# Parameters: initialize flag
# Returns: float
sub tot_hel_2 {
	my $self = shift;
	my $init = shift;
	my $count;
	my $alt_grps = {};

	return '' if $self->type =~ /ARC|THE/;

	if ($self->prop_status =~ /Y/ or $init) {
		# First count the non-alternate target groups
		my $targets = $self->targets;
		my @target_list = @$targets;
		for (my $i = 1; $i < scalar @target_list; $i++) {
			if ($target_list[$i]->at_hel eq 'Y') {
				if ($target_list[$i]->alt_id) {
					# Add the total time information to the alternate group hash for
					# processing later
					if ($target_list[$i]->targ_status_2 !~ /N|G/) {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} =
							$target_list[$i]->app_time_2;
					}
					else {
						$$alt_grps{$target_list[$i]->alt_id}{$target_list[$i]->targ_id} = 0;
					}
				}
				else {
					$count += $target_list[$i]->app_time_2 if
						$target_list[$i]->targ_status_2 !~ /N|G/;
				}
			}
		}
		# Cycle through alternate target group hash to get the highest number of
		# pointings in group
		#
		# Assume that number of pointings from an alternate target group is
		# determined in descending num_pnt for the targets
		foreach my $alt_id (keys %$alt_grps) {
			my $num_targs = $self->alt_grp($alt_id, 'app_cnt');
			my $targids = $$alt_grps{$alt_id};
			my @time = sort {$a <=> $b} values %$targids;
			foreach my $targid (%$targids) {
			}
			for (my $i = 1; $i <= $$num_targs; $i++) {
				$count += pop @time;
			}
		}
	}
	elsif ($self->prop_status =~ /$STAT_BPP/) {
		$count = $self->total_req_time_2;
	}
	else {
		$count = 0;
	}

	return $count;
}

sub hasAlternates {
  my ($self) = @_;

  if ($self->ag1 > 0 || $self->ag2 > 0) {
    return 1; 
  } else {
    return 0;
  }
}

## Class Method ##
# Name: switch_grades
# Purpose: switches the final grades with the alternate grades
# Parameters: none
# Returns: nothing
sub switch_grades {
  my ($self) = @_;

  my($ac) = $self->get("a_cmt");
  my($gc) = $self->get("g_cmt");
  if (!defined $gc) { $gc = ""; }
  if (!defined $ac) { $ac = ""; }
  #print STDERR "GOT $gc -- $ac\n";
  $self->save_member("a_cmt", $gc);
  $self->save_member("g_cmt", $ac);
  #print STDERR "g_cmt = " . $self->get("g_cmt") . "\n";
  #print STDERR "a_cmt = " . $self->get("a_cmt") . "\n";
  for (my $i = 1; $i <= $num_grades; $i++) {
    my($g) = $self->get("g$i");
    my($ag) = $self->get("ag$i");
    $self->save_member("g$i", $ag);
    $self->save_member("ag$i", $g);
  }

}

## Class Method ##

## Class Method ##
# Name: clear_grades
# Purpose: clears the final grades, sets the normalized grade and average 
#          grade to 0.0 and sets the standard deviation to undef
# Parameters: none
# Returns: nothing
sub clear_grades {
  my ($self) = @_;


  for (my $i = 1; $i <= $num_grades; $i++) {
    $self->save_member("g$i", undef);
    $self->save_member("ag$i", undef);
  }

  $self->save_member('fg_norm', 0.0);
  $self->save_member('fg_avg', 0.0);
  $self->save_member('fg_med', 0.0);
  $self->save_member('fg_stdev', undef);
  $self->save_member('afg_norm', 0.0);
  $self->save_member('afg_avg', 0.0);
  $self->save_member('afg_med', 0.0);
  $self->save_member('afg_stdev', undef);

  my $update = $self->dbh->prepare(qq(update proposal set 
                       fg_avg = 0.0, fg_med = 0.0,
                       fg_stdev = null,
                       g1 = null, g2 = null, g3 = null,
                       g4 = null, g5 = null, g6 = null,
                       g7 = null, g8 = null, g9 = null,
                       g10 = null ,
                       afg_avg = 0.0, afg_med = 0.0,
                       afg_stdev = null,
                       ag1 = null, ag2 = null, ag3 = null,
                       ag4 = null, ag5 = null, ag6 = null,
                       ag7 = null, ag8 = null, ag9 = null,
                       ag10 = null 
                       where panel_id = ? and prop_id = ?));
  $update->execute($self->panel_id, $self->prop_id);
  $update->finish;
  $self->save2database();
  if ($self->panel_id == 99) {
    $update = $self->dbh->prepare(qq(update proposal set g11 = null, 
                         g12 = null, g13 = null, g14 = null, 
                         g15 = null, g16 = null, g17 = null,
                         g18 = null, g19 = null, g20 = null,
                         g21 = null, g22 = null, g23 = null,
                         g24 = null, g25 = null ,
                         ag11 = null, 
                         ag12 = null, ag13 = null, ag14 = null, 
                         ag15 = null, ag16 = null, ag17 = null,
                         ag18 = null, ag19 = null, ag20 = null,
                         ag21 = null, ag22 = null, ag23 = null,
                         ag24 = null, ag25 = null 
                          where panel_id = ? and prop_id = ?));
    $update->execute($self->panel_id, $self->prop_id);
    $update->finish;
    $self->save2database();
  }

}

## Class Method ##
# Name: save_member
# Purpose: saves the new value of a field to the proposal object
# Parameters: field
#             value
# Returns: nothing
sub save_member {
  my ($self, $member, $value) = @_;
  my $dbFields = $self->PROPOSAL_PROPERTIES;
  
  if (defined($value) or  $member =~ /^g\d+/ or $member =~ /^ag\d+/) {
    $self->set($member, $value);
    # only save to database those members that are database fields
    my @matching = grep {$_ =~ /$member/} @$dbFields;
    $self->save2database();
  }
}

## Class Method ##
# Name: save2database
# Purpose: saves the proposal object to the database
# Parameters: panel_id
# Returns: nothing
sub save2database {
    my ($self, $panel) = @_;
    my $changed = $self->changed;
    my %changed = %$changed;
    my $dbFields = $self->PROPOSAL_PROPERTIES;

    my $grade_update = 0;
    my $agrade_update = 0;
    foreach my $member (keys %changed) {
      if ($self->verbose and $member !~ /norm|cum/) {
	print "Proposal::save2database\n\tSaving ", $self->get($member), 
	  " to $member in proposal ", $self->prop_id, "\n";
      }

      my @matching = grep {$_ =~ /$member/} @$dbFields;
      if (scalar @matching > 0 and $member !~/cum|final_stat/) {
	my $update = $self->dbh->prepare(qq(update proposal set $member = ?
						where panel_id = ? and 
						prop_id = ?));	    
	if ($member =~ /^g\d+/) {
	  $grade_update++;
	  if ($self->get($member) !~ /\d+/) {
	    my $grade = undef;
	    $update->execute($grade, $self->panel_id, 
			     $self->prop_id);
	  }
	  else {
	    $update->execute($self->get($member), $self->panel_id, 
			     $self->prop_id);
	  }
	}
	elsif ($member =~ /^ag\d+/) {
	  $agrade_update++;
	  if ($self->get($member) !~ /\d+/) {
	    my $grade = undef;
	    $update->execute($grade, $self->panel_id, 
			     $self->prop_id);
	  }
	  else {
	    $update->execute($self->get($member), $self->panel_id, 
			     $self->prop_id);
	  }
	}
	else {
	  my $mem_val = $self->get($member);
          if ($member !~ /comments/ && $member !~ /a_cmt/ && $member !~ /g_cmt/)
          {
            $mem_val = 0 if !$mem_val; #and $member =~ /num_targ_app/;
          }
	  $update->execute($mem_val, $self->panel_id, 
			   $self->prop_id);
	}
      }
    }
    
    # Update the calculated fields3
    
     # average grade and std_dev if grade updated
    if ($grade_update) {
	my ($avg, $stdev, $median) = $self->getGradeStats("g");
	$stdev = undef if $stdev =~ /n/i;
	my $update = $self->dbh->prepare(qq(update proposal set fg_avg = ?, 
					    fg_med = ?, fg_stdev = ? where 
					    prop_id = ? and panel_id = ?));
	$update->execute($avg, $median, $stdev,
			 $self->prop_id, $self->panel_id);
	$update->finish;
	
	$self->fg_avg($avg);
	$self->fg_med($median);
	$self->fg_stdev($stdev);
    }
    if ($agrade_update) {
	my ($avg, $stdev, $median) = $self->getGradeStats("ag");
	$stdev = undef if $stdev =~ /n/i;
	my $update = $self->dbh->prepare(qq(update proposal set afg_avg = ?, 
					    afg_med = ?, afg_stdev = ? where 
					    prop_id = ? and panel_id = ?));
	$update->execute($avg, $median, $stdev,
			 $self->prop_id, $self->panel_id);
	$update->finish;
	
	$self->afg_avg($avg);
	$self->afg_med($median);
	$self->afg_stdev($stdev);
    }


    # Reset the changed hash
    %changed = ();
    $self->changed(\%changed);
}

1;

__END__

=head1 NAME

Proposal - This object contains data associated with a proposal,
including target and too objects when necessary.

=head1 VERSION

$Revision: 1.70 $

=head1 SYNOPSIS

    use Proposal;
    my $proposal = new Proposal($dbh, $prop_id, $panel_id, $verbosity);

=head1 DESCRIPTION

Provides a single place to access elements of a proposal

=head1 PUBLIC METHODS

=head2 new($dbh, $prop_id, $panel_id, $verbosity)

Creates a new Proposal object

=over 

=item $dbh - database handle

=item $prop_id - proposal id

=item $panel_id - panel id

=item $verbosity - level of verbosity

=back

=head2 set($field, $value)

Overloads the set accessor from Class::Accessor to update %changed to
reflect that there are unsaved changes in the object.

=head2 dump

Prints a data dump of the proposal object

=head2 proposal($field, $value)

Provides a general get/set accessor to all simple elements of a
proposal 

=head2 joint_prop($observatory, $field, $value)

Provides a general get/set accessor to joint elements of a proposal

=head2 target($targ_index, $field, $value)

Provides a general get/set accessor to target elements of a proposal

=head2 target($alt_grp_id, $field, $value)

Provides a general get/set accessor to alternate target group elements of 
a proposal

=head2 final_grade_ref($grade_index, $value)

Provides a general get/set accessor to a reference to a final grade

=head2 final_grade($grade_index, $value)

Provides a general get/set accessor to a final grade

=head2 prelim_grade_ref

Provides a general get/set accessor to a reference to a preliminary grade

=head2 prelim_grade($grade_index, $value)

Provides a general get/set accessor to a preliminary grade

=head2 getGradeStats

Returns the average and standard deviation of the final grades.

=head2 num_pnt

Calcuates the total number of pointings approved for the proposal

=over 

=item  initialize flag - 1 is used for initializing the database

=back

=head2 num_targs

Calcuates the total number of targets approved for the proposal

=over 

=item  initialize flag - 1 is used for initializing the database

=back

=head2 num_tax

Calculates the total tax of pointings approved for the proposal

=over 

=item  initialize flag - 1 is used for initializing the database

=back

=head2 num_too

Calculates the total number of approved TOOs for a response time for the 
proposal

=over 

=item response (vf, f, s, vs)

=item initialize flag - 1 is used for initializing the database

=back

=head2 num_tc

Calculates the total number of approved TCs for a TC grade for the proposal

=over 

=item grade (e, a, d)

=item initialize flag - 1 is used for initializing the database

=back

=head2 prop_time

Calculates the total time w/slew tax plus probability approved for the 
proposal

=head2 calc_status

Calculates the status for the proposal

=over 

=item  initialize flag - 1 is used for initializing the database

=back

=head2 total_time

Returns the total time w/slew tax plus probability approved for the proposal

=over 

=item  initialize flag - 1 is used for initializing the database

=back

=head2 clear_grades

Clears the final grades, sets the normalized grade and average grade
to 0.0 and sets the standard deviation to undef.

=head2 save_member($field, $value)

Saves the new value of a field to the proposal object

=head2 save2database($panel_id)

Saves the proposal object to the database to the proposal in the
database from the panel.

=head1 PRIVATE METHODS

=head2 _init

Initializes new Proposal object.

=head2 _pop_joint

Initializes the joint observatory hash from the database

=head1 DEPENDENCIES

This module has no dependencies

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
