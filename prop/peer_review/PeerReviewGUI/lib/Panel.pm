#
# Panel.pm - This is a panel object.  It contains a list of proposal object
#            and fields used for bookkeeping.
#
# Purpose: Provides a single place to get bookkeeping information for a panel.
#          
# Copyright (c) 2005-2021
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Panel;
use strict;
use Carp;
use ProposalList;
use Monitor;
use config;
use Data::Dumper;
use base qw(Class::Accessor::Ref);

my(%fld_fmt) =(
1   => "%8.2f",
);

my ($empty) = '-  ';

Panel->mk_accessors(qw(panel_id proposals dbh verbose open_edit
		       num_grades_not_triaged
		       lp_num_graded lp_num_ungraded 
		       lp_grade_high lp_grade_low lp_grade_pass
		       lp_norm_high lp_norm_low lp_norm_pass
		       xvp_num_graded xvp_num_ungraded 
		       xvp_grade_high xvp_grade_low xvp_grade_pass
		       xvp_norm_high xvp_norm_low xvp_norm_pass
		       vlp_num_graded vlp_num_ungraded 
		       vlp_grade_high vlp_grade_low vlp_grade_pass
		       vlp_norm_high vlp_norm_low vlp_norm_pass
		       num_graded num_ungraded 
                       num_props num_props_1 num_props_2
		       num_targs num_targs_1 num_targs_2
                       lcd_sortname lcd_sortby lcd_groupby changed
                       fac_sortname fac_sortby fac_groupby
		       hide_triage triage_applied locked
		       hide_arc_the hide_lp_vlp
		       grade_high grade_low grade_pass
		       norm_high norm_low norm_pass
		       std_prop lp_prop vlp_prop too_prop arc_prop the_prop 
                       xvp_prop 
		       std_prop_1 lp_prop_1 vlp_prop_1 too_prop_1 xvp_prop_1
		       std_prop_2 lp_prop_2 vlp_prop_2 too_prop_2 xvp_prop_2
		       std_targ std_targ_1 std_targ_2
                       lp_targ lp_targ_1 lp_targ_2
                       vlp_targ vlp_targ_1 vlp_targ_2
                       xvp_targ xvp_targ_1 xvp_targ_2
                       too_targ too_targ_1 too_targ_2
		       std_prop_y std_targ_y std_targ_1_y std_targ_2_y
                       too_prop_y too_targ_y too_targ_1_y too_targ_2_y
		       bpp_prop_y bpp_targ_y bpp_targ_1_y bpp_targ_2_y
		       arc_y the_y arc_n the_n
		       std_prop_n std_targ_n std_targ_1_n std_targ_2_n
                       too_prop_n too_targ_n too_targ_1_n too_targ_2_n
		       bpp_prop_n bpp_targ_n bpp_targ_1_n bpp_targ_2_n
		       std_prop_g std_targ_g std_targ_1_g std_targ_2_g
                       too_prop_g too_targ_g too_targ_1_g too_targ_2_g
		       bpp_prop_g bpp_targ_g bpp_targ_1_g bpp_targ_2_g
		       std_prop_p std_targ_p std_targ_1_p std_targ_2_p
                       too_prop_p too_targ_p too_targ_1_p too_targ_2_p
		       bpp_prop_p bpp_targ_p bpp_targ_1_p bpp_targ_2_p
		       hst_req xmm_req rxte_req noao_req nrao_req
		       spitzer_req suzaku_req swift_req nustar_req
		       hst_allot xmm_allot rxte_allot noao_allot nrao_allot 
		       spitzer_allot suzaku_allot swift_allot nustar_allot
		       hst_cur xmm_cur rxte_cur noao_cur nrao_cur 
		       spitzer_cur suzaku_cur swift_cur nustar_cur
		       hst_bal xmm_bal rxte_bal noao_bal nrao_bal 
		       spitzer_bal suzaku_bal swift_bal nustar_bal
		       time_req time_slew_req time_slew_prob_req hel_slew_prob_req
		       time_slew_prob_allot hel_slew_prob_allot time_cur hel_cur time_slew_cur hel_slew_cur
		       time_slew_prob_cur hel_slew_prob_cur time_slew_prob_bal hel_slew_prob_bal
		       time_req_1 time_slew_req_1 time_slew_prob_req_1 hel_slew_prob_req_1
		       time_slew_prob_allot_1 hel_slew_prob_allot_1 time_cur_1 hel_cur_1 time_slew_cur_1 hel_slew_cur_1
		       time_slew_prob_cur_1 hel_slew_prob_cur_1 time_slew_prob_bal_1 hel_slew_prob_bal_1
		       time_req_2 time_slew_req_2 time_slew_prob_req_2 hel_slew_prob_req_2
		       time_slew_prob_allot_2 hel_slew_prob_allot_2 time_cur_2 hel_cur_2 time_slew_cur_2 hel_slew_cur_2
		       time_slew_prob_cur_2 hel_slew_prob_cur_2 time_slew_prob_bal_2 hel_slew_prob_bal_2
		       lp_time_req lp_time_slew_req lp_time_slew_prob_req lp_hel_slew_prob_req
		       lp_time_slew_prob_allot lp_hel_slew_prob_allot lp_time_cur lp_hel_cur lp_time_slew_cur lp_hel_slew_cur
		       lp_time_slew_prob_cur  lp_hel_slew_prob_cur lp_time_slew_prob_bal lp_hel_slew_prob_bal
		       lp_time_req_1 lp_time_slew_req_1 lp_time_slew_prob_req_1 lp_hel_slew_prob_req_1
		       lp_time_slew_prob_allot_1 lp_hel_slew_prob_allot_1 lp_time_cur_1 lp_hel_cur_1
                       lp_time_slew_cur_1 lp_hel_slew_cur_1
		       lp_time_slew_prob_cur_1 lp_hel_slew_prob_cur_1 lp_time_slew_prob_bal_1 lp_hel_slew_prob_bal_1
		       lp_time_req_2 lp_time_slew_req_2 lp_time_slew_prob_req_2 lp_hel_slew_prob_req_2
		       lp_time_slew_prob_allot_2 lp_hel_slew_prob_allot_2 lp_time_cur_2 lp_hel_cur_2
                       lp_time_slew_cur_2 lp_hel_slew_cur_2
		       lp_time_slew_prob_cur_2 lp_hel_slew_prob_cur_2 lp_time_slew_prob_bal_2 lp_hel_slew_prob_bal_2
		       vlp_time_req vlp_time_slew_req vlp_time_slew_prob_req vlp_hel_slew_prob_req
		       vlp_time_slew_prob_allot vlp_hel_slew_prob_allot vlp_time_cur vlp_hel_cur vlp_time_slew_cur vlp_hel_slew_cur
		       vlp_time_slew_prob_cur vlp_hel_slew_prob_cur vlp_time_slew_prob_bal vlp_hel_slew_prob_bal
		       vlp_time_req_1 vlp_time_slew_req_1 vlp_time_slew_prob_req_1
               vlp_hel_slew_prob_req_1 vlp_time_slew_prob_allot_1 vlp_hel_slew_prob_allot_1
                       vlp_time_cur_1 vlp_hel_cur_1 vlp_time_slew_cur_1 vlp_hel_slew_cur_1
		       vlp_time_slew_prob_cur_1 vlp_hel_slew_prob_cur_1 vlp_time_slew_prob_bal_1 vlp_hel_slew_prob_bal_1
		       vlp_time_req_2 vlp_time_slew_req_2 vlp_time_slew_prob_req_2
               vlp_hel_slew_prob_req_2 vlp_time_slew_prob_allot_2 vlp_hel_slew_prob_allot_2
                       vlp_time_cur_2 vlp_hel_cur_2 vlp_time_slew_cur_2 vlp_hel_slew_cur_2
		       vlp_time_slew_prob_cur_2 vlp_hel_slew_prob_cur_2 vlp_time_slew_prob_bal_2 vlp_hel_slew_prob_bal_2
		       xvp_time_req xvp_time_slew_req xvp_time_slew_prob_req xvp_hel_slew_prob_req
		       xvp_time_slew_prob_allot xvp_hel_slew_prob_allot xvp_time_cur xvp_hel_cur xvp_time_slew_cur xvp_hel_slew_cur
		       xvp_time_slew_prob_cur  xvp_hel_slew_prob_cur xvp_time_slew_prob_bal xvp_hel_slew_prob_bal
		       xvp_time_req_1 xvp_time_slew_req_1 xvp_time_slew_prob_req_1
               xvp_hel_slew_prob_req_1 xvp_time_slew_prob_allot_1 xvp_hel_slew_prob_allot_1
                       xvp_time_cur_1 xvp_hel_cur_1 xvp_time_slew_cur_1 xvp_hel_slew_cur_1
		       xvp_time_slew_prob_cur_1 xvp_hel_slew_prob_cur_1 xvp_time_slew_prob_bal_1 xvp_hel_slew_prob_bal_1
		       xvp_time_req_2 xvp_time_slew_req_2 xvp_time_slew_prob_req_2
               xvp_hel_slew_prob_req_2 xvp_time_slew_prob_allot_2 xvp_hel_slew_prob_allot_2
                       xvp_time_cur_2 xvp_hel_cur_2 xvp_time_slew_cur_2 xvp_hel_slew_cur_2
		       xvp_time_slew_prob_cur_2 xvp_hel_slew_prob_cur_2 xvp_time_slew_prob_bal_2 xvp_hel_slew_prob_bal_2
               bpp_hel_slew_prob_allot bpp_hel_slew_prob_allot_1 bpp_hel_slew_prob_allot_2
		       vf_req f_req s_req vs_req
		       vf_allot f_allot s_allot vs_allot
		       vf_cur f_cur s_cur vs_cur
		       vf_bal f_bal s_bal vs_bal
		       rc_score_req rc_score_req_1 rc_score_req_2
		       rc_score_allot rc_score_allot_1 rc_score_allot_2
		       rc_score_bal rc_score_bal_1 rc_score_bal_2
		       rc_score_cur rc_score_cur_1 rc_score_cur_2
		       arc_req the_req arc_num arc_cur the_num the_cur
		       arc_allot the_allot arc_bal the_bal groups
		       ));
Panel->mk_refaccessors(qw(
                       lp_grade_high lp_grade_low lp_grade_pass
                       lp_norm_high lp_norm_low lp_num_graded lp_num_ungraded
                       xvp_grade_high xvp_grade_low xvp_grade_pass
                       xvp_norm_high xvp_norm_low xvp_num_graded xvp_num_ungraded
                       vlp_grade_high vlp_grade_low vlp_grade_pass
                       vlp_norm_high vlp_norm_low vlp_num_graded vlp_num_ungraded
			grade_high grade_low grade_pass
			  norm_high norm_low num_graded num_ungraded
			  std_prop_y std_targ_y std_targ_1_y std_targ_2_y
                          too_prop_y too_targ_y too_targ_1_y too_targ_2_y
			  bpp_prop_y bpp_targ_y bpp_targ_1_y bpp_targ_2_y
			  arc_y the_y arc_n the_n 
			  std_prop_n std_targ_n std_targ_1_n std_targ_2_n
                          too_prop_n too_targ_n too_targ_1_n too_targ_2_n
			  bpp_prop_n bpp_targ_n bpp_targ_1_n bpp_targ_2_n
			  std_prop_g std_targ_g std_targ_1_g std_targ_2_g
                          too_prop_g too_targ_g too_targ_1_g too_targ_2_g
			  bpp_prop_g bpp_targ_g bpp_targ_1_g bpp_targ_2_g
			  std_prop_p std_targ_p std_targ_1_p std_targ_2_p
                          too_prop_p too_targ_p too_targ_1_p too_targ_2_p
			  bpp_prop_p bpp_targ_p bpp_targ_1_p bpp_targ_2_p
			  hst_req xmm_req rxte_req noao_req nrao_req 
			  spitzer_req suzaku_req swift_req nustar_req
			  hst_allot xmm_allot rxte_allot noao_allot nrao_allot 
			  spitzer_allot suzaku_allot swift_allot nustar_allot
			  hst_cur xmm_cur rxte_cur noao_cur nrao_cur 
			  spitzer_cur suzaku_cur swift_cur nustar_cur
			  hst_bal xmm_bal rxte_bal noao_bal nrao_bal 
			  spitzer_bal suzaku_bal swift_bal nustar_bal
			  time_req time_slew_req time_slew_prob_req hel_slew_prob_req
			  time_slew_prob_allot hel_slew_prob_allot time_cur time_slew_cur
			  time_slew_prob_cur hel_slew_prob_cur time_slew_prob_bal hel_slew_prob_bal
			  time_req_1 time_slew_req_1 time_slew_prob_req_1 hel_slew_prob_req_1
			  time_slew_prob_allot_1 hel_slew_prob_allot_1 time_cur_1 time_slew_cur_1
			  time_slew_prob_cur_1 hel_slew_prob_cur_1 time_slew_prob_bal_1 hel_slew_prob_bal_1
			  time_req_2 time_slew_req_2 time_slew_prob_req_2 hel_slew_prob_req_2
			  time_slew_prob_allot_2 hel_slew_prob_allot_2 time_cur_2 time_slew_cur_2
			  time_slew_prob_cur_2 hel_slew_prob_cur_2 time_slew_prob_bal_2 hel_slew_prob_bal_2
			  lp_time_req lp_time_slew_req lp_time_slew_prob_req lp_hel_slew_prob_req
			  lp_time_slew_prob_allot lp_hel_slew_prob_allot lp_time_cur lp_time_slew_cur
			  lp_time_slew_prob_cur lp_hel_slew_prob_cur lp_time_slew_prob_bal lp_hel_slew_prob_bal
			  lp_time_req_1 lp_time_slew_req_1 lp_time_slew_prob_req_1
              lp_hel_slew_prob_req_1 lp_time_slew_prob_allot_1 lp_hel_slew_prob_allot_1
                          lp_time_cur_1 lp_time_slew_cur_1
			  lp_time_slew_prob_cur_1 lp_hel_slew_prob_cur_1 lp_time_slew_prob_bal_1 lp_hel_slew_prob_bal_1
			  lp_time_req_2 lp_time_slew_req_2 lp_time_slew_prob_req_2
              lp_hel_slew_prob_req_2 lp_time_slew_prob_allot_2 lp_hel_slew_prob_allot_2
                          lp_time_cur_2 lp_time_slew_cur_2
			  lp_time_slew_prob_cur_2 lp_hel_slew_prob_cur_2 lp_time_slew_prob_bal_2 lp_hel_slew_prob_bal_2
			  vlp_time_req vlp_time_slew_req 
			  vlp_time_slew_prob_req vlp_hel_slew_prob_req
			  vlp_time_slew_prob_allot vlp_hel_slew_prob_allot vlp_time_cur
			  vlp_time_slew_cur 
			  vlp_time_slew_prob_cur vlp_hel_slew_prob_cur vlp_time_slew_prob_bal vlp_hel_slew_prob_bal
			  vlp_time_req_1 vlp_time_slew_req_1
			  vlp_time_slew_prob_req_1 vlp_hel_slew_prob_req_1
			  vlp_time_slew_prob_allot_1 vlp_hel_slew_prob_allot_1 vlp_time_cur_1
			  vlp_time_slew_cur_1
			  vlp_time_slew_prob_cur_1 vlp_hel_slew_prob_cur_1 vlp_time_slew_prob_bal_1 vlp_hel_slew_prob_bal_1
			  vlp_time_req_2 vlp_time_slew_req_2
			  vlp_time_slew_prob_req_2 vlp_hel_slew_prob_req_1
			  vlp_time_slew_prob_allot_2 vlp_hel_slew_prob_allot_2 vlp_time_cur_2
			  vlp_time_slew_cur_2
			  vlp_time_slew_prob_cur_2 vlp_hel_slew_prob_cur_2 vlp_time_slew_prob_bal_2 vlp_hel_slew_prob_bal_2
			  xvp_time_req xvp_time_slew_req 
			  xvp_time_slew_prob_req xvp_hel_slew_prob_req
			  xvp_time_slew_prob_allot xvp_time_cur 
			  xvp_time_slew_cur 
			  xvp_time_slew_prob_cur xvp_hel_slew_prob_cur xvp_time_slew_prob_bal xvp_hel_slew_prob_bal
			  xvp_time_req_1 xvp_time_slew_req_1
			  xvp_time_slew_prob_req_1 xvp_hel_slew_prob_req_1
			  xvp_time_slew_prob_allot_1 xvp_hel_slew_prob_allot_1 xvp_time_cur_1
			  xvp_time_slew_cur_1
			  xvp_time_slew_prob_cur_1 xvp_hel_slew_prob_cur_1 xvp_time_slew_prob_bal_1 xvp_hel_slew_prob_bal_1
			  xvp_time_req_2 xvp_time_slew_req_2
			  xvp_time_slew_prob_req_2 xvp_hel_slew_prob_req_2
			  xvp_time_slew_prob_allot_2 xvp_hel_slew_prob_allot_2 xvp_time_cur_2
			  xvp_time_slew_cur_2
			  xvp_time_slew_prob_cur_2 xvp_hel_slew_prob_cur_2 xvp_time_slew_prob_bal_2 xvp_hel_slew_prob_bal_2
              bpp_hel_slew_prob_allot bpp_hel_slew_prob_allot_1 bpp_hel_slew_prob_allot_2
			  vf_req f_req s_req vs_req
			  vf_allot f_allot s_allot vs_allot
			  vf_cur f_cur s_cur vs_cur
			  vf_bal f_bal s_bal vs_bal
			  rc_score_req rc_score_req_1 rc_score_req_2
			  rc_score_allot rc_score_allot_1 rc_score_allot_2
			  rc_score_bal rc_score_bal_1 rc_score_bal_2
		          rc_score_cur rc_score_cur_1 rc_score_cur_2
			  arc_req the_req arc_num arc_cur the_num the_cur
			  arc_allot the_allot arc_bal the_bal
		       ));

# Name: new
# Purpose: create a new Panel object
# Parameters: database handle
#             panel id
#             'finalize applied' flag
#             verbosity
# Returns: Panel object
sub new {
    my $self = {};
    bless $self,shift;
    $self->_init(@_);
    return $self;
}

## Internal Utility ##
# Name: _init
# Purpose: initializes a new Panel object
# Parameters: database handle
#             panel id
#             'ignore finalize' flag
#             verbosity
# Returns: Panel object
sub _init {
    my ($self, $dbh, $panel_id, $if, $verbose) = @_;
    my (%init) = %$self;

    print "Panel::new - creating new object\n" if $self->verbose > 2;
    my %changed = ();
    $init{changed} = \%changed;
    $init{panel_id} = $panel_id;
    $init{dbh} = $dbh;
    $init{verbose} = $verbose;
    $init{proposals} = new ProposalList($dbh, $panel_id, $if, $verbose);
    $init{norm_pass} = "3.50";
    $init{lp_norm_pass} = "3.50";
    $init{xvp_norm_pass} = "3.50";
    $init{vlp_norm_pass} = "3.50";
    $init{lcd_sortby} = 'prop_id';
    $init{lcd_groupby} = '';
    $init{lcd_sortname} = 'Prop#';
    $init{fac_sortby} = 'prop_id';
    $init{fac_groupby} = '';
    $init{fac_sortname} = 'Prop#';
    $init{hide_arc_the} = 'N';
    $init{hide_lp_vlp} = 'N';
    $init{hide_triage} = 'N';
    $init{open_edit} = $if;

    my $cnt_triage = $dbh->prepare(qq(select count(*) from proposal 
				      where triage in ('Y', 'R') and
				      panel_id = ?));
    my $cnt_grades_not_triaged = $dbh->prepare(qq(select count(*) from proposal 
				      where triage in ('N', 'A') and
                                      fg_avg > 0 and
				      panel_id = ?));
    $cnt_triage->execute($panel_id);
    my ($cnt) = $cnt_triage->fetchrow_array;
    $cnt_triage->finish;
    $init{triage_applied} = 'N';
    $init{triage_applied} = 'Y' if $cnt > 0;

    $cnt_grades_not_triaged->execute($panel_id);
    my ($cnt) = $cnt_grades_not_triaged->fetchrow_array;
    $cnt_grades_not_triaged->finish;
    $init{num_grades_not_triaged} = $cnt;

    my $lock_q = $dbh->prepare(qq(select locked from final_comments where 
                                  panel_id = ?));
    $lock_q->execute($panel_id);
    my ($locked) = $lock_q->fetchrow_array;
    $lock_q->finish;
    $init{locked} = $locked;

    %$self = %init;

    # populate the groups hash with all distinct groups in group_id column
    $self->set_groups();

    # target count information
    $self->set_prop_counts();
    $self->set_targ_counts();
    $self->set_prop_y();
    $self->set_prop_n();
    $self->set_prop_g();
    $self->set_prop_p();
    $self->set_targ_y();
    $self->set_targ_n();
    $self->set_targ_g();
    $self->set_targ_p();

    # normalized grade information
    $self->set_grades();
    $self->set_lp_grades();
    $self->set_xvp_grades();
    $self->set_vlp_grades();

    # joint count information
    $self->set_joint_req();
    $self->set_joint_allot();
    $self->set_joint_cur();
    $self->set_joint_bal();

    #tc and too count information
    $self->set_tc_req();
    $self->set_tc_allot();
    $self->set_tc_cur();
    $self->set_tc_bal();

    # time information
    $self->set_time_req();
    $self->set_time_slew_req();
    $self->set_time_slew_prob_req();
    $self->set_time_slew_prob_allot();
    $self->set_time_cur();
    $self->set_time_slew_cur();
    $self->set_time_slew_prob_cur();
    $self->set_time_slew_prob_bal();

    # HEL information
    $self->set_hel_slew_prob_req();
    $self->set_hel_slew_prob_allot();
    $self->set_hel_slew_prob_cur();
    $self->set_hel_slew_prob_bal();

    # LP time information
    $self->set_lp_time_req();
    $self->set_lp_time_slew_req();
    $self->set_lp_time_slew_prob_req();
    $self->set_lp_time_slew_prob_allot();
    $self->set_lp_time_cur();
    $self->set_lp_time_slew_cur();
    $self->set_lp_time_slew_prob_cur();
    $self->set_lp_time_slew_prob_bal();

    # VLP time information
    $self->set_vlp_time_req();
    $self->set_vlp_time_slew_req();
    $self->set_vlp_time_slew_prob_req();
    $self->set_vlp_time_slew_prob_allot();
    $self->set_vlp_time_cur();
    $self->set_vlp_time_slew_cur();
    $self->set_vlp_time_slew_prob_cur();
    $self->set_vlp_time_slew_prob_bal();

    # XVP time information
    $self->set_xvp_time_req();
    $self->set_xvp_time_slew_req();
    $self->set_xvp_time_slew_prob_req();
    $self->set_xvp_time_slew_prob_allot();
    $self->set_xvp_time_cur();
    $self->set_xvp_time_slew_cur();
    $self->set_xvp_time_slew_prob_cur();
    $self->set_xvp_time_slew_prob_bal();

    # archive and theory information
    $self->set_arc_req();
    $self->set_the_req();
    $self->set_arc_cur();
    $self->set_the_cur();
    $self->set_arc_the_allot();
    $self->set_arc_the_bal();

    # set cumulative totals
    $self->set_running_totals();
}

## Class Method ##
# Name: set_groups
# Purpose: populates the groups hash with distinct groups in group_id 
# Parameters: none
# Returns: nothing
sub set_groups {
  my ($self) = shift;
  my $get_groups = $self->dbh->prepare(qq(select distinct group_name from 
                         groups where panel_id = ? order by group_name));
  $get_groups->execute($self->panel_id);
  my %groups;
  my $count = 0;
  while (my $group = $get_groups->fetchrow_array) {
    $count++;
    $groups{$count} = $group;
  }
  $get_groups->finish;

  $self->{groups} = \%groups;
}

## Class Method ##
# Name: new_group
# Purpose: insert a new group into the groups table and adds the group to the 
#          groups hash
# Parameters: group name
# Returns: nothing
sub new_group {
  my $self = shift;
  my $group = shift;
  my $get_count = $self->dbh->prepare(qq(select max(group_id) from 
                                         groups where panel_id = ?));
  $get_count->execute($self->panel_id);
  my $count = $get_count->fetchrow_array;
  $count++;
  $get_count->finish;
  my $insert = $self->dbh->prepare(qq(insert into groups values (?, ?, ?)));
  $insert->execute($count, $self->panel_id, $group);
  $insert->finish;
  $self->set_groups;
}

## Class Method ##
# Name: delete_group
# Purpose: delete a group from the groups table and from the groups hash
# Parameters: group name
# Returns: nothing
sub delete_group {
  my $self = shift;
  my $group = shift;
  my $get_count = $self->dbh->prepare(qq(select count(*) from proposal
                                         where group_id = ? and panel_id = ?));
  $get_count->execute($group, $self->panel_id);
  my $count = $get_count->fetchrow_array;
  $get_count->finish;
  if (!$count) {
    my $delete = $self->dbh->prepare(qq(delete from groups where panel_id = ? 
                                        and group_name = ?));
    $delete->execute($self->panel_id, $group);
    $delete->finish;
    my $get_props = $self->dbh->prepare(qq(select prop_id from proposal where
                                         group_id = ? and panel_id = ?));
    $get_props->execute($group, $self->panel_id);
    while (my($prop_id) = $get_props->fetchrow_array) {
      $self->proposals->proposal($prop_id)->save_member('group_id', undef);
    }
    $get_props->finish;

    $self->set_groups;
  }
}

## Class Method ##
# Name: update_group
# Purpose: update a group in the groups table, the groups hash, and all 
#          proposals pointing to the group
# Parameters: old name
#             new name
# Returns: nothing
sub update_group {
  my $self = shift;
  my $old_name = shift;
  my $new_name = shift;
  my $update_groups = $self->dbh->prepare(qq(update groups set group_name = ?
                                             where group_name = ? and 
                                             panel_id = ?));
  $update_groups->execute($new_name, $old_name, $self->panel_id);
  $update_groups->finish;

  my $get_props = $self->dbh->prepare(qq(select prop_id from proposal where
                                         group_id = ? and panel_id = ?));
  $get_props->execute($old_name, $self->panel_id);
  while (my($prop_id) = $get_props->fetchrow_array) {
    $self->proposals->proposal($prop_id)->save_member('group_id', $new_name);
  }
  $get_props->finish;

  $self->set_groups;
}

## Class Method ##
# Name: set_triage
# Purpose: set the 'hide triage' flag and recalculate running totals
# Parameters: 'hide triage' flag value
# Returns: nothing
sub set_triage {
    my ($self, $hide_triage) = @_;
    $self->hide_triage($hide_triage);

    $self->set_running_totals();
}

## Class Method ##
# Name: set_arc_the
# Purpose: set the 'hide archive/theory' flag and recalculate running totals
# Parameters: 'hide archive/theory' flag value
# Returns: nothing
sub set_arc_the {
    my ($self, $hide_arc_the) = @_;
    $self->hide_arc_the($hide_arc_the);

    $self->set_running_totals();
}

## Class Method ##
# Name: set_lp_vlp
# Purpose: set the 'hide LP/VLP' flag and recalculate running totals
# Parameters: 'hide LP/VLP' flag value
# Returns: nothing
sub set_lp_vlp {
    my ($self, $hide_lp_vlp) = @_;
    $self->hide_lp_vlp($hide_lp_vlp);

    $self->set_running_totals();
}

## Class Method ##
# Name: set_lcd_sort
# Purpose: set the lcd_groupby, lcd_sortby and lcd_sortname fields
# Parameters: groupby value
#             sortby value
#             sortname value
# Returns: nothing
sub set_sort {
    my ($self, $view, $groupby, $sortby, $sortname) = @_;

    
    $sortby .= ",prop_id";
    if ($view =~ /lcd/) {
      $self->lcd_groupby($groupby);
      $self->lcd_sortby($sortby);
      $self->lcd_sortname($sortname);
    }
    else {
      $self->fac_groupby($groupby);
      $self->fac_sortby($sortby);
      $self->fac_sortname($sortname);
    }
    $self->set_running_totals();
}

## Class Method ##
# Name: lower_triage
# Purpose: lower the average grade of all triaged proposals by a stated amount
# Parameters: amount to lower by
# Returns: new highest triage grade
sub lower_triage {
  my $self = shift;
  my $amt = shift;

  my @props;

  my $query = $self->dbh->prepare(qq(select prop_id from proposal where triage 
                                     in ('Y', 'A') and panel_id = ?));
  $query->execute($self->panel_id);
  while (my($prop_id) = $query->fetchrow_array) {
    push @props, $prop_id;
  }

  foreach my $prop_id (@props) {
    my $avg = $self->proposals->proposal($prop_id)->fg_avg;
    my $new = sprintf "%4.2f", ($avg -$amt);
    $self->proposals->proposal($prop_id)->save_member('fg_avg', $new);
  }
  $query->finish;

  $query = $self->dbh->prepare(qq(select max(fg_avg) from proposal where
                                  triage in ('Y', 'A') and panel_id = ?));
  $query->execute($self->panel_id);
  my ($max_tr) = $query->fetchrow_array;
  $query->finish;

  return $max_tr;
}

## Class Method ##
# Name: set_grades
# Purpose: calculates the normalized grade for each proposal
#          and then resets the new high, low and passing average grades for
#          the panel
# Parameters: none
# Returns: nothing
sub set_grades {
    my ($self) = @_;

    my $query = $self->dbh->prepare(qq(select max(fg_avg), min(fg_avg) from 
				       proposal where panel_id = ? and 
                                       infeasible = 'N'));
    $query->execute($self->panel_id);
    my ($grade_high, $grade_low) = $query->fetchrow_array;
    $grade_high= sprintf("%4.2f",$grade_high);
    $grade_low= sprintf("%4.2f",$grade_low);
    $query->finish;

    $query = $self->dbh->prepare(qq(select min(fg_avg) from proposal where 
				    panel_id = ? and prop_status = 'Y'));
    $query->execute($self->panel_id);
    my ($grade_pass) = sprintf("%4.2f",$query->fetchrow_array);
    $query->finish;

    $self->grade_high($grade_high);
    $self->grade_low($grade_low);
    $self->grade_pass($grade_pass);

    my $prop = $self->proposals->list;
    my %prop = %$prop;
    foreach my $prop_id (keys %prop) {
	my $norm;
	my $dbg;
        $dbg =  $self->proposals->proposal($prop_id)->fg_avg;
      
	if (!$self->proposals->proposal($prop_id)->fg_avg) {
	    $norm = undef;
	}
	elsif ($self->grade_high == $self->grade_low) {
            if ($self->panel_id != 97) {
	      $norm = undef;
            } else {
              print STDERR "Panel 97 normalized grade high=low\n";
              $norm = $self->proposals->proposal($prop_id)->fg_avg;
            }
	}
	elsif ($self->proposals->proposal($prop_id)->infeasible eq 'Y') {
	  $norm = 0;
	}
	elsif ($self->proposals->proposal($prop_id)->fg_avg < 
	       $self->grade_pass) {

           
	    $norm = (($self->norm_pass - $self->grade_low) /
		     ($self->grade_pass - $self->grade_low)) *
		     $self->proposals->proposal($prop_id)->fg_avg 
		     +
		     (($self->grade_pass - $self->norm_pass) /
		      ($self->grade_pass - $self->grade_low)) *
		      $self->grade_low;
	}
	elsif ($self->grade_high == $self->grade_pass) {
	    $norm = $self->norm_pass;
	}
	else {
	    $norm = (($self->grade_high - $self->norm_pass) / 
		     ($self->grade_high - $self->grade_pass)) *
		     $self->proposals->proposal($prop_id)->fg_avg
		     + 
		     (($self->norm_pass - $self->grade_pass) /
		      ($self->grade_high - $self->grade_pass)) *
		      $self->grade_high;
	}

	# Set the normalized grade to 1 if it is less than 1
	if (defined $norm && $norm < 1 and 
	    $self->proposals->proposal($prop_id)->infeasible eq 'N'){
	  $norm = 1;
	}
        if (defined $norm && $norm > 2.5 && (
            $self->proposals->proposal($prop_id)->triage eq 'Y' ||
            $self->proposals->proposal($prop_id)->triage eq 'A' )) {
          #print STDERR "resetting normGrade to 2.5 for triaged  $prop_id \n";
          $norm=2.5;
        }
        if (defined $norm && $norm < 2.51 && 
		$self->proposals->proposal($prop_id)->triage eq 'N' && 
		$self->proposals->proposal($prop_id)->infeasible eq 'N') {
          $norm=2.51;
          #print STDERR "forcing normGrade to 2.51 for non-triaged  $prop_id \n";
        }


	$norm = sprintf "%4.2f", $norm;
	$self->proposals->proposal($prop_id)->save_member('fg_norm', $norm);
    }

    my $query2 = $self->dbh->prepare(qq(select max(fg_norm), min(fg_norm)
		 from proposal where panel_id = ? 
		and infeasible = 'N'));
    $query2->execute($self->panel_id);
    my ($norm_high, $norm_low) = $query2->fetchrow_array;
    $norm_high= sprintf("%4.2f",$norm_high);
    $norm_low= sprintf("%4.2f",$norm_low);
    $query2->finish;

    $self->norm_high($norm_high);
    $self->norm_low($norm_low);

    my $query3 = $self->dbh->prepare(qq(select count(*) from proposal where 
					((g1 > 0.0 or g2 > 0.0 or g3 > 0.0 or 
					 g4 > 0.0 or g5 > 0.0 or g6 > 0.0 or 
					 g7 > 0.0 or g8 > 0.0 or g9 > 0.0 or 
					 g10 > 0.0 or g11 > 0.0 or g12 > 0.0 
					 or g13 > 0.0 or g14 > 0.0 or 
					 g15 > 0.0 or g16 > 0.0 or g17 > 0.0 or g18 > 0.0 or g19 > 0.0 or g20 > 0.0 
					  or g21 > 0.0 or g22 > 0.0 or g23 > 0.0 or g24 > 0.0 or g25 > 0.0) 
					 or infeasible = 'Y') and 
                                         panel_id = ?));
    $query3->execute($self->panel_id);
    my ($count) = $query3->fetchrow_array;
    $query3->finish;

    $self->num_graded($count);
    $self->num_ungraded($self->num_props - $count);

}

## Class Method ##
# Name: set_lp_grades
# Purpose: calculates the normalized grade for each LP proposal
#          and then resets the new high, low and passing average grades for
#          the panel
# Parameters: none
# Returns: nothing
sub set_lp_grades {
    my ($self) = @_;

    my $query = $self->dbh->prepare(qq(select max(fg_avg), min(fg_avg) from 
				       proposal where panel_id = ? and 
					big_proj in ('LP','GTO-LP') and
                                       infeasible = 'N'));
    $query->execute($self->panel_id);
    my ($grade_high, $grade_low) = $query->fetchrow_array;
    $grade_high= sprintf("%4.2f",$grade_high);
    $grade_low= sprintf("%4.2f",$grade_low);
    $query->finish;

    $query = $self->dbh->prepare(qq(select min(fg_avg) from proposal where 
					big_proj in ('LP','GTO-LP') and
				    panel_id = ? and prop_status = 'Y'));
    $query->execute($self->panel_id);
    my ($grade_pass) = sprintf("%4.2f",$query->fetchrow_array);
    $query->finish;

    $self->lp_grade_high($grade_high);
    $self->lp_grade_low($grade_low);
    $self->lp_grade_pass($grade_pass);


    my $query2 = $self->dbh->prepare(qq(select max(fg_norm), min(fg_norm) from 
					proposal where panel_id = ? and
					big_proj in ('LP','GTO-LP') ));
    $query2->execute($self->panel_id);
    my ($norm_high, $norm_low) = $query2->fetchrow_array;
    $query2->finish;
    $norm_high = sprintf("%4.2f",$norm_high);
    $norm_low = sprintf("%4.2f",$norm_low);

    $self->lp_norm_high($norm_high);
    $self->lp_norm_low($norm_low);

    my $query3 = $self->dbh->prepare(qq(select count(*) from proposal where 
					((g1 > 0.0 or g2 > 0.0 or g3 > 0.0 or 
					 g4 > 0.0 or g5 > 0.0 or g6 > 0.0 or 
					 g7 > 0.0 or g8 > 0.0 or g9 > 0.0 or 
					 g10 > 0.0 or g11 > 0.0 or g12 > 0.0 
					 or g13 > 0.0 or g14 > 0.0 or 
					 g15 > 0.0 or g16 > 0.0 or g17 > 0.0 or g18 > 0.0 or g19 > 0.0 or g20 > 0.0 
					  or g21 > 0.0 or g22 > 0.0 or g23 > 0.0 or g24 > 0.0 or g25 > 0.0) 
					 or infeasible = 'Y') and 
					 big_proj in ('LP','GTO-LP') and
                                         panel_id = ?));
    $query3->execute($self->panel_id);
    my ($count) = $query3->fetchrow_array;
    $query3->finish;

    $self->lp_num_graded($count);
    $self->lp_num_ungraded($self->lp_prop - $count);

}
## Class Method ##
# Name: set_vlp_grades
# Purpose: calculates the normalized grade for each XVP proposal
#          and then resets the new high, low and passing average grades for
#          the panel
# Parameters: none
# Returns: nothing
sub set_vlp_grades {
    my ($self) = @_;

    my $query = $self->dbh->prepare(qq(select max(fg_avg), min(fg_avg) from 
				       proposal where panel_id = ? and 
					big_proj = 'VLP' and
                                       infeasible = 'N'));
    $query->execute($self->panel_id);
    my ($grade_high, $grade_low) = $query->fetchrow_array;
    $grade_high= sprintf("%4.2f",$grade_high);
    $grade_low= sprintf("%4.2f",$grade_low);
 
    $query->finish;

    $query = $self->dbh->prepare(qq(select min(fg_avg) from proposal where 
					big_proj = 'VLP'	 and
				    panel_id = ? and prop_status = 'Y'));
    $query->execute($self->panel_id);
    my ($grade_pass) = sprintf("%4.2f",$query->fetchrow_array);
    $query->finish;

    $self->vlp_grade_high($grade_high);
    $self->vlp_grade_low($grade_low);
    $self->vlp_grade_pass($grade_pass);

    my $query2 = $self->dbh->prepare(qq(select max(fg_norm), min(fg_norm) from 
					proposal where panel_id = ? and
					big_proj='VLP'));
    $query2->execute($self->panel_id);
    my ($norm_high, $norm_low) = $query2->fetchrow_array;
    $norm_high= sprintf("%4.2f",$norm_high);
    $norm_low= sprintf("%4.2f",$norm_low);
    $query2->finish;

    $self->vlp_norm_high($norm_high);
    $self->vlp_norm_low($norm_low);

    my $query3 = $self->dbh->prepare(qq(select count(*) from proposal where 
					((g1 > 0.0 or g2 > 0.0 or g3 > 0.0 or 
					 g4 > 0.0 or g5 > 0.0 or g6 > 0.0 or 
					 g7 > 0.0 or g8 > 0.0 or g9 > 0.0 or 
					 g10 > 0.0 or g11 > 0.0 or g12 > 0.0 
					 or g13 > 0.0 or g14 > 0.0 or 
					 g15 > 0.0 or g16 > 0.0 or g17 > 0.0 or g18 > 0.0 or g19 > 0.0 or g20 > 0.0 
					  or g21 > 0.0 or g22 > 0.0 or g23 > 0.0 or g24 > 0.0 or g25 > 0.0) 
					 or infeasible = 'Y') and 
					 big_proj='VLP' and 
                                         panel_id = ?));
    $query3->execute($self->panel_id);
    my ($count) = $query3->fetchrow_array;
    $query3->finish;

    $self->vlp_num_graded($count);
    $self->vlp_num_ungraded($self->vlp_prop - $count);

}


## Class Method ##
# Name: set_xvp_grades
# Purpose: calculates the normalized grade for each XVP proposal
#          and then resets the new high, low and passing average grades for
#          the panel
# Parameters: none
# Returns: nothing
sub set_xvp_grades {
    my ($self) = @_;

    my $query = $self->dbh->prepare(qq(select max(fg_avg), min(fg_avg) from 
				       proposal where panel_id = ? and 
					big_proj = 'XVP' and
                                       infeasible = 'N'));
    $query->execute($self->panel_id);
    my ($grade_high, $grade_low) = $query->fetchrow_array;
    $grade_high= sprintf("%4.2f",$grade_high);
    $grade_low= sprintf("%4.2f",$grade_low);
 
    $query->finish;

    $query = $self->dbh->prepare(qq(select min(fg_avg) from proposal where 
					big_proj = 'XVP'	 and
				    panel_id = ? and prop_status = 'Y'));
    $query->execute($self->panel_id);
    my ($grade_pass) = sprintf("%4.2f",$query->fetchrow_array);
    $query->finish;

    $self->xvp_grade_high($grade_high);
    $self->xvp_grade_low($grade_low);
    $self->xvp_grade_pass($grade_pass);

    my $query2 = $self->dbh->prepare(qq(select max(fg_norm), min(fg_norm) from 
					proposal where panel_id = ? and
					big_proj='XVP'));
    $query2->execute($self->panel_id);
    my ($norm_high, $norm_low) = $query2->fetchrow_array;
    $norm_high= sprintf("%4.2f",$norm_high);
    $norm_low= sprintf("%4.2f",$norm_low);
    $query2->finish;

    $self->xvp_norm_high($norm_high);
    $self->xvp_norm_low($norm_low);

    my $query3 = $self->dbh->prepare(qq(select count(*) from proposal where 
					((g1 > 0.0 or g2 > 0.0 or g3 > 0.0 or 
					 g4 > 0.0 or g5 > 0.0 or g6 > 0.0 or 
					 g7 > 0.0 or g8 > 0.0 or g9 > 0.0 or 
					 g10 > 0.0 or g11 > 0.0 or g12 > 0.0 
					 or g13 > 0.0 or g14 > 0.0 or 
					 g15 > 0.0 or g16 > 0.0 or g17 > 0.0 or g18 > 0.0 or g19 > 0.0 or g20 > 0.0 
					  or g21 > 0.0 or g22 > 0.0 or g23 > 0.0 or g24 > 0.0 or g25 > 0.0) 
					 or infeasible = 'Y') and 
					 big_proj='XVP' and 
                                         panel_id = ?));
    $query3->execute($self->panel_id);
    my ($count) = $query3->fetchrow_array;
    $query3->finish;

    $self->xvp_num_graded($count);
    $self->xvp_num_ungraded($self->xvp_prop - $count);

}

## Internal Utility ##
# Name: set_prop_counts
# Purpose: sets all the various proposal counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_prop_counts {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				     panel_id = ? and big_proj not in 
				     ('LP', 'VLP', 'XVP','GTO-LP') and type = 'GO'));
  $query->execute($self->panel_id);
  my ($std_prop) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_1 > 0 and
				  panel_id = ? and big_proj not in 
				  ('LP', 'VLP', 'XVP','GTO-LP') and type = 'GO'));
  $query->execute($self->panel_id);
  my ($std_prop_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_2 > 0 and
				  panel_id = ? and big_proj not in 
				  ('LP', 'VLP', 'XVP','GTO-LP') and type = 'GO'));
  $query->execute($self->panel_id);
  my ($std_prop_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and big_proj in ('LP','GTO-LP')));
  $query->execute($self->panel_id);
  my ($lp_prop) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_1 > 0 and
				  panel_id = ? and big_proj in( 'LP','GTO-LP')));
  $query->execute($self->panel_id);
  my ($lp_prop_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_2 > 0 and
				  panel_id = ? and big_proj in ('LP','GTO-LP')));
  $query->execute($self->panel_id);
  my ($lp_prop_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and big_proj = 'VLP'));
  $query->execute($self->panel_id);
  my ($vlp_prop) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_1 > 0 and
				  panel_id = ? and big_proj = 'VLP'));
  $query->execute($self->panel_id);
  my ($vlp_prop_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_2 > 0 and
				  panel_id = ? and big_proj = 'VLP'));
  $query->execute($self->panel_id);
  my ($vlp_prop_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and big_proj = 'XVP'));
  $query->execute($self->panel_id);
  my ($xvp_prop) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where
                                  total_req_time_1 > 0 and
				  panel_id = ? and big_proj = 'XVP'));
  $query->execute($self->panel_id);
  my ($xvp_prop_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where
                                  total_req_time_2 > 0 and
				  panel_id = ? and big_proj = 'XVP'));
  $query->execute($self->panel_id);
  my ($xvp_prop_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type = 'TOO' and 
                                  big_proj not in ('LP', 'VLP', 'XVP','GTO-LP')));
  $query->execute($self->panel_id);
  my ($too_prop) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_1 > 0 and
				  panel_id = ? and type = 'TOO' and 
                                  big_proj not in ('LP', 'VLP', 'XVP','GTO-LP')));
  $query->execute($self->panel_id);
  my ($too_prop_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
                                  total_req_time_2 > 0 and
				  panel_id = ? and type = 'TOO' and 
                                  big_proj not in ('LP', 'VLP', 'XVP','GTO-LP')));
  $query->execute($self->panel_id);
  my ($too_prop_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type like 'ARCHIVE'));
  $query->execute($self->panel_id);
  my ($arc_prop) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type like 'THEORY'));
  $query->execute($self->panel_id);
  my ($the_prop) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where
				  panel_id = ?));
  $query->execute($self->panel_id);
  my ($num_props) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where
				  total_req_time_1 > 0 and panel_id = ?));
  $query->execute($self->panel_id);
  my ($num_props_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where
				  total_req_time_2 > 0 and panel_id = ?));
  $query->execute($self->panel_id);
  my ($num_props_2) = $query->fetchrow_array;
  $query->finish;

  $self->std_prop($std_prop);
  $self->std_prop_1($std_prop_1);
  $self->std_prop_2($std_prop_2);
  $self->lp_prop($lp_prop);
  $self->lp_prop_1($lp_prop_1);
  $self->lp_prop_2($lp_prop_2);
  $self->vlp_prop($vlp_prop);
  $self->vlp_prop_1($vlp_prop_1);
  $self->vlp_prop_2($vlp_prop_2);
  $self->xvp_prop($xvp_prop);
  $self->xvp_prop_1($xvp_prop_1);
  $self->xvp_prop_2($xvp_prop_2);
  $self->too_prop($too_prop);
  $self->too_prop_1($too_prop_1);
  $self->too_prop_2($too_prop_2);
  $self->arc_prop($arc_prop);
  $self->the_prop($the_prop);
  $self->num_props($num_props);
  $self->num_props_1($num_props_1);
  $self->num_props_2($num_props_2);
}

## Internal Utility ##
# Name: set_prop_y
# Purpose: sets all the various prop_y counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_prop_y {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				     panel_id = ? and big_proj not in 
				     ('LP','GTO-LP', 'VLP', 'XVP') and type = 'GO' and 
				     prop_status = 'Y'));
  $query->execute($self->panel_id);
  my ($std_prop_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and big_proj in ('LP','GTO-LP', 'VLP', 'XVP')
				  and prop_status = 'Y'));
  $query->execute($self->panel_id);
  my ($bpp_prop_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type = 'TOO' and 
				  prop_status = 'Y'));
  $query->execute($self->panel_id);
  my ($too_prop_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type like 'ARCHIVE'
				  and prop_status = 'Y'));
  $query->execute($self->panel_id);
  my ($arc_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type like 'THEORY'
				  and prop_status = 'Y'));
  $query->execute($self->panel_id);
  my ($the_y) = $query->fetchrow_array;
  $query->finish;
  $self->std_prop_y($std_prop_y);
  $self->bpp_prop_y($bpp_prop_y);
  $self->too_prop_y($too_prop_y);
  $self->arc_y($arc_y);
  $self->the_y($the_y);
}

## Internal Utility ##
# Name: set_prop_n
# Purpose: sets all the various prop_n counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_prop_n {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				     panel_id = ? and big_proj not in 
				     ('LP','GTO-LP', 'VLP', 'XVP') and type = 'GO' and 
				     prop_status = 'N'));
  $query->execute($self->panel_id);
  my ($std_prop_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and big_proj in ('LP','GTO-LP', 'VLP', 'XVP')
				  and prop_status = 'N'));
  $query->execute($self->panel_id);
  my ($bpp_prop_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type = 'TOO' and 
				  prop_status = 'N'));
  $query->execute($self->panel_id);
  my ($too_prop_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type like 'ARCHIVE'
				  and prop_status = 'N'));
  $query->execute($self->panel_id);
  my ($arc_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type like 'THEORY'
				  and prop_status = 'N'));
  $query->execute($self->panel_id);
  my ($the_n) = $query->fetchrow_array;
  $query->finish;
  $self->std_prop_n($std_prop_n);
  $self->bpp_prop_n($bpp_prop_n);
  $self->too_prop_n($too_prop_n);
  $self->arc_n($arc_n);
  $self->the_n($the_n);
}

## Internal Utility ##
# Name: set_prop_g
# Purpose: sets all the various prop_g counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_prop_g {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				     panel_id = ? and big_proj not in 
				     ('LP','GTO-LP', 'VLP', 'XVP') and type = 'GO' and 
				     prop_status = 'G'));
  $query->execute($self->panel_id);
  my ($std_prop_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and big_proj in ('LP','GTO-LP', 'VLP', 'XVP')
				  and prop_status = 'G'));
  $query->execute($self->panel_id);
  my ($bpp_prop_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type = 'TOO' and 
				  prop_status = 'G'));
  $query->execute($self->panel_id);
  my ($too_prop_g) = $query->fetchrow_array;
  $query->finish;

  $self->std_prop_g($std_prop_g);
  $self->bpp_prop_g($bpp_prop_g);
  $self->too_prop_g($too_prop_g);
}

## Internal Utility ##
# Name: set_prop_p
# Purpose: sets all the various prop_p counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_prop_p {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				     panel_id = ? and big_proj not in 
				     ('LP','GTO-LP', 'VLP', 'XVP') and type = 'GO' and 
				     prop_status = '$STAT_BPP'));
  $query->execute($self->panel_id);
  my ($std_prop_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and big_proj in ('LP','GTO-LP', 'VLP', 'XVP')
				  and prop_status = '$STAT_BPP'));
  $query->execute($self->panel_id);
  my ($bpp_prop_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from proposal where 
				  panel_id = ? and type = 'TOO' and 
				  prop_status = '$STAT_BPP'));
  $query->execute($self->panel_id);
  my ($too_prop_p) = $query->fetchrow_array;
  $query->finish;

  $self->std_prop_p($std_prop_p);
  $self->bpp_prop_p($bpp_prop_p);
  $self->too_prop_p($too_prop_p);
}

## Internal Utility ##
# Name: set_targ_counts
# Purpose: sets all the various targ counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_targ_counts {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				     where target.prop_id = proposal.prop_id 
				     and target.panel_id = proposal.panel_id 
				     and proposal.panel_id = ? and 
				     big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
				     type = 'GO'));
  $query->execute($self->panel_id);
  my ($std_targ) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
				  and target.panel_id = proposal.panel_id 
				  and proposal.panel_id = ? and 
				  big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  type = 'GO' and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
				  and target.panel_id = proposal.panel_id 
				  and proposal.panel_id = ? and 
				  big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  type = 'GO' and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP')));
  $query->execute($self->panel_id);
  my ($lp_targ) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP') and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($lp_targ_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP') and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($lp_targ_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP'));
  $query->execute($self->panel_id);
  my ($vlp_targ) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($vlp_targ_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($vlp_targ_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP'));
  $query->execute($self->panel_id);
  my ($xvp_targ) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($xvp_targ_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($xvp_targ_2) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and ao = 0));
  $query->execute($self->panel_id);
  my ($too_targ) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and ao = 1));
  $query->execute($self->panel_id);
  my ($too_targ_1) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and ao = 2));
  $query->execute($self->panel_id);
  my ($too_targ_2) = $query->fetchrow_array;
  $query->finish;

  $self->std_targ($std_targ);
  $self->std_targ_1($std_targ_1);
  $self->std_targ_2($std_targ_2);

  $self->lp_targ($lp_targ);
  $self->lp_targ_1($lp_targ_1);
  $self->lp_targ_2($lp_targ_2);

  $self->vlp_targ($vlp_targ);
  $self->vlp_targ_1($vlp_targ_1);
  $self->vlp_targ_2($vlp_targ_2);

  $self->xvp_targ($xvp_targ);
  $self->xvp_targ_1($xvp_targ_1);
  $self->xvp_targ_2($xvp_targ_2);

  $self->too_targ($too_targ);
  $self->too_targ_1($too_targ_1);
  $self->too_targ_2($too_targ_2);

  $self->num_targs($std_targ + $lp_targ + $vlp_targ + $xvp_targ + $too_targ);
  $self->num_targs_1($std_targ_1 + $lp_targ_1 + $vlp_targ_1 + $xvp_targ_1 + $too_targ_1);
  $self->num_targs_2($std_targ_2 + $lp_targ_2 + $vlp_targ_2 + $xvp_targ_2 + $too_targ_2);
}

## Internal Utility ##
# Name: set_targ_y
# Purpose: sets all the various targ_y counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_targ_y {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				     where target.prop_id = proposal.prop_id 
				     and target.panel_id = proposal.panel_id 
				     and proposal.panel_id = ? and 
				     big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
				     type = 'GO' and targ_status = 'Y'));
  $query->execute($self->panel_id);
  my ($std_targ_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
			          and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_1 = 'Y' and
                                  req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_1_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
			          and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_2 = 'Y' and
                                  req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_2_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  targ_status = 'Y'));
  $query->execute($self->panel_id);
  my ($bpp_targ_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  targ_status_1 = 'Y' and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($bpp_targ_1_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  targ_status_2 = 'Y' and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($bpp_targ_2_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = 'Y' and ao = 0));
  $query->execute($self->panel_id);
  my ($too_targ_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = 'Y' and ao = 1));
  $query->execute($self->panel_id);
  my ($too_targ_1_y) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = 'Y' and ao = 2));
  $query->execute($self->panel_id);
  my ($too_targ_2_y) = $query->fetchrow_array;
  $query->finish;

  $self->std_targ_y($std_targ_y);
  $self->std_targ_1_y($std_targ_1_y);
  $self->std_targ_2_y($std_targ_2_y);

  $self->bpp_targ_y($bpp_targ_y);
  $self->bpp_targ_1_y($bpp_targ_1_y);
  $self->bpp_targ_2_y($bpp_targ_2_y);

  $self->too_targ_y($too_targ_y);
  $self->too_targ_1_y($too_targ_1_y);
  $self->too_targ_2_y($too_targ_2_y);
}

## Internal Utility ##
# Name: set_targ_n
# Purpose: sets all the various prop_n counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_targ_n {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				     where target.prop_id = proposal.prop_id 
				     and target.panel_id = proposal.panel_id 
				     and proposal.panel_id = ? and 
				     big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
				     type = 'GO' and targ_status = 'N'));
  $query->execute($self->panel_id);
  my ($std_targ_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
			          and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_1 = 'N' and
                                  req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_1_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
			          and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_2 = 'N' and
                                  req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_2_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP')and targ_status = 'N'));
  $query->execute($self->panel_id);
  my ($lp_targ_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP')and targ_status_1 = 'N'
                                  and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($lp_targ_1_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP')and targ_status_2 = 'N'
                                  and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($lp_targ_2_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and targ_status = 'N'));
  $query->execute($self->panel_id);
  my ($vlp_targ_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and 
				  targ_status_1 = 'N' and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($vlp_targ_1_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and 
				  targ_status_2 = 'N' and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($vlp_targ_2_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and targ_status = 'N'));
  $query->execute($self->panel_id);
  my ($xvp_targ_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and 
				  targ_status_1 = 'N' and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($xvp_targ_1_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and 
				  targ_status_2 = 'N' and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($xvp_targ_2_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = 'N' and ao = 0));
  $query->execute($self->panel_id);
  my ($too_targ_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = 'N' and ao = 1));
  $query->execute($self->panel_id);
  my ($too_targ_1_n) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = 'N' and ao = 2));
  $query->execute($self->panel_id);
  my ($too_targ_2_n) = $query->fetchrow_array;
  $query->finish;

  $self->std_targ_n($std_targ_n);
  $self->std_targ_1_n($std_targ_1_n);
  $self->std_targ_2_n($std_targ_2_n);

  $self->bpp_targ_n($lp_targ_n + $vlp_targ_n + $xvp_targ_n);
  $self->bpp_targ_1_n($lp_targ_1_n + $vlp_targ_1_n + $xvp_targ_1_n);
  $self->bpp_targ_2_n($lp_targ_2_n + $vlp_targ_2_n + $xvp_targ_2_n);

  $self->too_targ_n($too_targ_n);
  $self->too_targ_1_n($too_targ_1_n);
  $self->too_targ_2_n($too_targ_2_n);
}

## Internal Utility ##
# Name: set_targ_g
# Purpose: sets all the various prop_g counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_targ_g {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				     where target.prop_id = proposal.prop_id 
				     and target.panel_id = proposal.panel_id 
				     and proposal.panel_id = ? and 
				     big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
				     type = 'GO' and targ_status = 'G' and 
                                     prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($std_targ_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
				  and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_1 = 'G' and 
                                  req_time_1 > 0 and 
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($std_targ_1_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				  where target.prop_id = proposal.prop_id 
				  and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_2 = 'G' and 
                                  req_time_2 > 0 and 
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($std_targ_2_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP')
				  and targ_status = 'G' and 
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($lp_targ_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP')
				  and targ_status_1 = 'G' and req_time_1 > 0
                                  and prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($lp_targ_1_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP')
				  and targ_status_2 = 'G' and req_time_2 > 0
                                  and prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($lp_targ_2_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and 
                                  targ_status = 'G' and 
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($vlp_targ_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and 
                                  targ_status_1 = 'G' and req_time_1 > 0 and
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($vlp_targ_1_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'VLP' and 
                                  targ_status_2 = 'G' and req_time_2 > 0 and
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($vlp_targ_2_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and 
                                  targ_status = 'G' 
                                  and prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($xvp_targ_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and 
                                  targ_status_1 = 'G' and req_time_1 > 0
                                  and prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($xvp_targ_1_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj = 'XVP' and 
                                  targ_status_2 = 'G' and req_time_2 > 0
                                  and prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($xvp_targ_2_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status in ('G') and ao = 0 and
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($too_targ_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status in ('G') and ao = 1 and
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($too_targ_1_g) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status in ('G') and ao = 2 and
                                  prop_status in ('Y','G')));
  $query->execute($self->panel_id);
  my ($too_targ_2_g) = $query->fetchrow_array;
  $query->finish;

  $self->std_targ_g($std_targ_g);
  $self->std_targ_1_g($std_targ_1_g);
  $self->std_targ_2_g($std_targ_2_g);

  $self->bpp_targ_g($lp_targ_g + $vlp_targ_g + $xvp_targ_g);
  $self->bpp_targ_1_g($lp_targ_1_g + $vlp_targ_1_g + $xvp_targ_1_g);
  $self->bpp_targ_2_g($lp_targ_2_g + $vlp_targ_2_g + $xvp_targ_2_g);

  $self->too_targ_g($too_targ_g);
  $self->too_targ_1_g($too_targ_1_g);
  $self->too_targ_2_g($too_targ_2_g);
}


## Internal Utility ##
# Name: set_targ_p
# Purpose: sets all the various targ_p counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_targ_p {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
				     where target.prop_id = proposal.prop_id 
				     and target.panel_id = proposal.panel_id 
				     and proposal.panel_id = ? and 
				     big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
				     type = 'GO' and targ_status = '$STAT_BPP'));
  $query->execute($self->panel_id);
  my ($std_targ_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
		        	  where target.prop_id = proposal.prop_id 
			          and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_1 = '$STAT_BPP' and
                                  req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_1_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal 
		        	  where target.prop_id = proposal.prop_id 
			          and target.panel_id = proposal.panel_id 
			          and proposal.panel_id = ? and 
			          big_proj not in ('LP','GTO-LP', 'VLP', 'XVP') and 
			          type = 'GO' and targ_status_2 = '$STAT_BPP' and
                                  req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($std_targ_2_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  targ_status = '$STAT_BPP'));
  $query->execute($self->panel_id);
  my ($bpp_targ_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  targ_status_1 = '$STAT_BPP' and req_time_1 > 0));
  $query->execute($self->panel_id);
  my ($bpp_targ_1_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from target, proposal where 
				  target.prop_id = proposal.prop_id and 
				  target.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and big_proj in ('LP','GTO-LP', 'VLP', 'XVP') and 
				  targ_status_2 = '$STAT_BPP' and req_time_2 > 0));
  $query->execute($self->panel_id);
  my ($bpp_targ_2_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = '$STAT_BPP' and ao = 0));
  $query->execute($self->panel_id);
  my ($too_targ_p) = $query->fetchrow_array;
  $query->finish;

  $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = '$STAT_BPP' and ao = 1));
  $query->execute($self->panel_id);
  my ($too_targ_1_p) = $query->fetchrow_array;
  $query->finish;

   $query = $self->dbh->prepare(qq(select count(*) from too, proposal where 
				  too.prop_id = proposal.prop_id and 
				  too.panel_id = proposal.panel_id and
				  proposal.panel_id = ? 
				  and type like 'TOO' and too.req_obs_time > 0
				  and obs_status = '$STAT_BPP' and ao = 2));
  $query->execute($self->panel_id);
  my ($too_targ_2_p) = $query->fetchrow_array;
  $query->finish;

  $self->std_targ_p($std_targ_p);
  $self->std_targ_1_p($std_targ_1_p);
  $self->std_targ_2_p($std_targ_2_p);

  $self->bpp_targ_p($bpp_targ_p);
  $self->bpp_targ_1_p($bpp_targ_1_p);
  $self->bpp_targ_2_p($bpp_targ_2_p);

  $self->too_targ_p($too_targ_p);
  $self->too_targ_1_p($too_targ_1_p);
  $self->too_targ_2_p($too_targ_2_p);
}

## Internal Utility ##
# Name: set_joint_req
# Purpose: sets all the various joint req counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_joint_req {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(hst_req), sum(xmm_req), 
				     sum(rxte_req), sum(noao_req), 
				     sum(nrao_req), sum(spitzer_req),
                                     sum(suzaku_req), sum(swift_req), sum(nustar_req) from proposal 
				     where panel_id = ? and prop_status != '$STAT_BPP'
				     ));
  $query->execute($self->panel_id);
  my ($hst, $xmm, $rxte, $noao, $nrao, 
      $spitzer, $suzaku, $swift, $nustar) = $query->fetchrow_array;
  $query->finish;
  $self->hst_req(sprintf("%.02f",$hst));
  $self->xmm_req(sprintf("%.02f",$xmm));
  $self->rxte_req(sprintf("%.02f",$rxte));
  $self->noao_req(sprintf("%.02f",$noao));
  $self->nrao_req(sprintf("%.02f",$nrao));
  $self->spitzer_req(sprintf("%.02f",$spitzer));
  $self->suzaku_req(sprintf("%.02f",$suzaku));
  $self->swift_req(sprintf("%.02f",$swift));
  $self->nustar_req(sprintf("%.02f",$nustar));
}

## Internal Utility ##
# Name: set_joint_allot
# Purpose: sets all the various joint allot counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_joint_allot {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select hst, xmm, rxte, noao, nrao, 
                                     spitzer, suzaku, swift,nustar  from 
                                     allotment where panel_id = ?
				     ));
  $query->execute($self->panel_id);
  my ($hst, $xmm, $rxte, $noao, $nrao, 
      $spitzer, $suzaku, $swift,$nustar) = $query->fetchrow_array;
  $query->finish;
  $hst = sprintf("%.02f",$hst) if $hst;
  $hst = $empty if $hst < 0;
  $xmm = sprintf("%.02f",$xmm) if $xmm;
  $xmm = $empty if $xmm < 0;
  $rxte = sprintf("%.02f",$rxte) if $rxte;
  $rxte = $empty if $rxte < 0;
  $noao = sprintf("%.02f",$noao) if $noao;
  $noao = $empty if $noao < 0;
  $nrao = sprintf("%.02f",$nrao) if $nrao;
  $nrao = $empty if $nrao < 0;
  $spitzer = sprintf("%.02f",$spitzer) if $spitzer;
  $spitzer = $empty if $spitzer < 0;
  $suzaku = sprintf("%.02f",$suzaku) if $suzaku;
  $suzaku = $empty if $suzaku < 0;
  $swift = sprintf("%.02f",$swift) if $swift;
  $swift = $empty if $swift < 0;
  $nustar = sprintf("%.02f",$nustar) if $nustar;
  $nustar = $empty if $nustar < 0;
  $self->hst_allot($hst);
  $self->xmm_allot($xmm);
  $self->rxte_allot($rxte);
  $self->noao_allot($noao);
  $self->nrao_allot($nrao);
  $self->spitzer_allot($spitzer);
  $self->suzaku_allot($suzaku);
  $self->swift_allot($swift);
  $self->nustar_allot($nustar);
}

## Internal Utility ##
# Name: set_joint_cur
# Purpose: sets all the various joint cur counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_joint_cur {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(hst_app), 
		sum(xmm_app), 
		sum(rxte_app), sum(noao_app), 
		sum(nrao_app), sum(spitzer_app),
                sum(suzaku_app), sum(swift_app), sum(nustar_app) 
		from proposal 
	        where panel_id = ?  and prop_status like 'Y'));
  $query->execute($self->panel_id);
  my ($hst, $xmm, $rxte, $noao, $nrao,
      $spitzer, $suzaku, $swift , $nustar) = $query->fetchrow_array;
  $query->finish;
  $self->hst_cur(sprintf("%.02f",$hst));
  $self->xmm_cur(sprintf("%.02f",$xmm));
  $self->rxte_cur(sprintf("%.02f",$rxte));
  $self->noao_cur(sprintf("%.02f",$noao));
  $self->nrao_cur(sprintf("%.02f",$nrao));
  $self->spitzer_cur(sprintf("%.02f",$spitzer));
  $self->suzaku_cur(sprintf("%.02f",$suzaku));
  $self->swift_cur(sprintf("%.02f",$swift));
  $self->nustar_cur(sprintf("%.02f",$nustar));
}

## Internal Utility ##
# Name: set_joint_bal
# Purpose: sets all the various joint bal counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_joint_bal {
  my ($self) = @_;
  $self->set_joint_cur();

  $self->hst_bal(sprintf("%.02f",$self->hst_allot - $self->hst_cur)) if $self->hst_allot !~ /$empty/;
  $self->hst_bal($empty) if $self->hst_allot =~ /$empty/;

  $self->xmm_bal(sprintf("%.02f",$self->xmm_allot - $self->xmm_cur)) if $self->xmm_allot !~ /$empty/;
  $self->xmm_bal($empty) if $self->xmm_allot =~ /$empty/;

  $self->rxte_bal(sprintf("%.02f",$self->rxte_allot - $self->rxte_cur)) if 
      $self->rxte_allot !~ /$empty/;
  $self->rxte_bal($empty) if $self->rxte_allot =~ /$empty/;

  $self->noao_bal(sprintf("%.02f",$self->noao_allot - $self->noao_cur)) if 
      $self->noao_allot !~ /$empty/;
  $self->noao_bal($empty) if $self->noao_allot =~ /$empty/;

  $self->nrao_bal(sprintf("%.02f",$self->nrao_allot - $self->nrao_cur)) if 
    $self->nrao_allot !~ /$empty/;
  $self->nrao_bal($empty) if $self->nrao_allot =~ /$empty/;

  $self->spitzer_bal(sprintf("%.02f",$self->spitzer_allot - $self->spitzer_cur)) if 
    $self->spitzer_allot !~ /$empty/;
  $self->spitzer_bal($empty) if $self->spitzer_allot =~ /$empty/;

  $self->suzaku_bal(sprintf("%.02f",$self->suzaku_allot - $self->suzaku_cur)) if 
    $self->suzaku_allot !~ /$empty/;
  $self->suzaku_bal($empty) if $self->suzaku_allot =~ /$empty/;

  $self->swift_bal(sprintf("%.02f",$self->swift_allot - $self->swift_cur)) if 
    $self->swift_allot !~ /$empty/;
  $self->swift_bal($empty) if $self->swift_allot =~ /$empty/;

  $self->nustar_bal(sprintf("%.02f",$self->nustar_allot - $self->nustar_cur)) if 
    $self->nustar_allot !~ /$empty/;
  $self->nustar_bal($empty) if $self->nustar_allot =~ /$empty/;
}

## Internal Utility ##
# Name: set_tc_req
# Purpose: sets all the various tc req counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_tc_req {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select 
		sum(rc_score_req), sum(rc_score_req_1),sum(rc_score_req_2),
                sum(vf_req), sum(f_req), 
                sum(s_req), sum(vs_req) from proposal
		where panel_id = ? and 
                prop_status != '$STAT_BPP'));
  $query->execute($self->panel_id());
  my ($rc_score,$rc_score_1,$rc_score_2,
      $vf, $f, $s, $vs) = $query->fetchrow_array;
  $query->finish;

  $self->rc_score_req(sprintf("%.02f",$rc_score));
  $self->rc_score_req_1(sprintf("%.02f",$rc_score_1));
  $self->rc_score_req_2(sprintf("%.02f",$rc_score_2));

  $self->vf_req($vf);
  $self->f_req($f);
  $self->s_req($s);
  $self->vs_req($vs);
}

## Internal Utility ##
# Name: set_tc_allot
# Purpose: sets all the various tc allot counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_tc_allot {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select 
			rc_score, rc_score_1, rc_score_2,
                        vf_too, f_too, s_too, vs_too 
			from allotment where panel_id = ?));
  $query->execute($self->panel_id());
  my ( $rc_score, $rc_score_1, $rc_score_2,
      $vf, $f, $s, $vs) = $query->fetchrow_array;
  $query->finish;
  $rc_score = sprintf("%.02f",$rc_score) if $rc_score;
  $rc_score = $empty if $rc_score < 0;
  $rc_score_1 = sprintf("%.02f",$rc_score_1) if $rc_score_1;
  $rc_score_1 = $empty if $rc_score_1 < 0;
  $rc_score_2 = sprintf("%.02f",$rc_score_2) if $rc_score_2;
  $rc_score_2 = $empty if $rc_score_2 < 0;


  $vf = $empty if $vf < 0;
  $f = $empty if $f < 0;
  $s = $empty if $s < 0;
  $vs = $empty if $vs < 0;

  $self->rc_score_allot($rc_score) ;
  $self->rc_score_allot_1($rc_score_1);
  $self->rc_score_allot_2($rc_score_2);

  $self->vf_allot($vf);
  $self->f_allot($f);
  $self->s_allot($s);
  $self->vs_allot($vs);
}

## Internal Utility ##
# Name: set_tc_cur
# Purpose: sets all the various tc cur counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_tc_cur {
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select 
		sum(rc_score_app), sum(rc_score_app_1),sum(rc_score_app_2),
                sum(vf_app), sum(f_app), sum(s_app), sum(vs_app) from proposal
		where panel_id = ? and 
                prop_status = 'Y'));
  $query->execute($self->panel_id());
  my ($rc_score,$rc_score_1,$rc_score_2,
      $vf, $f, $s, $vs) = $query->fetchrow_array;
  $query->finish;

  $self->rc_score_cur(sprintf("%.02f",$rc_score));
  $self->rc_score_cur_1(sprintf("%.02f",$rc_score_1));
  $self->rc_score_cur_2(sprintf("%.02f",$rc_score_2));
  $self->vf_cur($vf);
  $self->f_cur($f);
  $self->s_cur($s);
  $self->vs_cur($vs);
}

## Internal Utility ##
# Name: set_tc_bal
# Purpose: sets all the various tc bal counts for monitoring window
# Parameters: none
# Returns: nothing
sub set_tc_bal {
  my ($self) = @_;
  $self->set_tc_cur();

  $self->rc_score_bal(sprintf("%7.2f",$self->rc_score_allot - $self->rc_score_cur)) if 
    $self->rc_score_allot !~ /$empty/;
  $self->rc_score_bal_1(sprintf("%7.2f",$self->rc_score_allot_1 - $self->rc_score_cur_1)) if 
    $self->rc_score_allot_1 !~ /$empty/;
  $self->rc_score_bal_2(sprintf("%7.2f",$self->rc_score_allot_2 - $self->rc_score_cur_2)) if 
    $self->rc_score_allot_2 !~ /$empty/;

  $self->vf_bal($self->vf_allot - $self->vf_cur) if 
    $self->vf_allot !~ /$empty/;
  $self->vf_bal($empty) if $self->vf_allot =~ /$empty/;
  $self->f_bal($self->f_allot - $self->f_cur) if 
    $self->f_allot !~ /$empty/;
  $self->f_bal($empty) if $self->f_allot =~ /$empty/;
  $self->s_bal($self->s_allot - $self->s_cur) if 
    $self->s_allot !~ /$empty/;
  $self->s_bal($empty) if $self->s_allot =~ /$empty/;
  $self->vs_bal($self->vs_allot - $self->vs_cur) if 
    $self->vs_allot !~ /$empty/;
  $self->vs_bal($empty) if $self->vs_allot =~ /$empty/;
}

## Internal Utility ##
# Name: set_time_req
# Purpose: sets the time_req
# Parameters: none
# Returns: nothing
sub set_time_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_req_time),
                                     sum(total_req_time_1), 
                                     sum(total_req_time_2) from
				     proposal where panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status != '$STAT_BPP'));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $time_1 = sprintf "%.02f", $time_1;
  $time_2 = sprintf "%.02f", $time_2;
  $self->time_req($time);
  $self->time_req_1($time_1);
  $self->time_req_2($time_2);
}

## Internal Utility ##
# Name: set_time_cur
# Purpose: sets the time_cur
# Parameters: none
# Returns: nothing
sub set_time_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_app_time),
                                     sum(total_app_time_1),
                                     sum(total_app_time_2) from proposal 
                                     where panel_id = ? and type not in
				     ('ARCHIVE', 'THEORY') and 
				     prop_status = 'Y'));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $time_1 = sprintf "%.02f", $time_1;
  $time_2 = sprintf "%.02f", $time_2;

  $self->time_cur($time);
  $self->time_cur_1($time_1);
  $self->time_cur_2($time_2);
}

## Internal Utility ##
# Name: set_hel_cur
# Purpose: sets the hel_cur
# Parameters: none
# Returns: nothing
sub set_hel_cur{
    my ($self) = @_;
    my $query = $self->dbh->prepare(qq(select sum(total_app_hel),
                                     sum(total_app_hel_1),
                                     sum(total_app_hel_2) from proposal
                                     where panel_id = ? and type not in
				     ('ARCHIVE', 'THEORY') and
				     prop_status = 'Y'));
    $query->execute($self->panel_id());
    my ($time, $time_1, $time_2) = $query->fetchrow_array;
    $query->finish;

    $time = sprintf "%.02f", $time;
    $time_1 = sprintf "%.02f", $time_1;
    $time_2 = sprintf "%.02f", $time_2;

    $self->hel_cur($time);
    $self->hel_cur_1($time_1);
    $self->hel_cur_2($time_2);
}

## Internal Utility ##
# Name: set_time_slew_req
# Purpose: sets the time_slew_req
# Parameters: none
# Returns: nothing
sub set_time_slew_req {
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_req_time + tax_req) 
                                     from proposal where panel_id = ? and 
                                     prop_status != '$STAT_BPP' and  type = 'GO'));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;
  
  my $goTot_1 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_1 + tax_req_1) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO'));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;
  
  my $goTot_2 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_2 + tax_req_2) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO'));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;
  
  my $tooTot = $self->dbh->prepare(qq(select 
                                      sum(req_time + tax_req/too_prob_req)
				      from target where targ_status != '$STAT_BPP' and
				      panel_id = ? and alt_id is null and
                                      response_time is not null));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;
  
  my $tooTot_1 = $self->dbh->prepare(qq(select 
                                       sum(req_time_1 + tax_req_1/too_prob_req)
		       		       from target where targ_status_1 != '$STAT_BPP'
                                       and req_time_1 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;
  
   my $tooTot_2 = $self->dbh->prepare(qq(select 
                                       sum(req_time_2 + tax_req_2/too_prob_req)
		       		       from target where targ_status_2 != '$STAT_BPP'
                                       and req_time_2 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;
  
  my $alts = $self->dbh->prepare(qq(select prop_id, req_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ?));
  my $subAlt = $self->dbh->prepare(qq(select (req_time + tax/too_prob_req)
                                      from target where targ_status != '$STAT_BPP' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_req)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                       (req_time_1 + tax_1/too_prob_req)
                                       from target where targ_status_1 != '$STAT_BPP' 
                                       and req_time_1 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_1 + tax_1/too_prob_req)  
                                       DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                       (req_time_2 + tax_2/too_prob_req)
                                       from target where targ_status_2 != '$STAT_BPP' 
                                       and req_time_2 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_2 + tax_2/too_prob_req)  
                                       DESC LIMIT ?));
  
  
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my (@alt);
  while (my ($prop_id, $req_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       req_cnt => $req_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
    $subAlt->execute($self->panel_id, $$altgrp{prop_id}, 
		     $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time) = $subAlt->fetchrow_array) {
      $subalt += $req_obs_time;
    }
    $subAlt->finish;

    $subAlt_1->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_1) = $subAlt_1->fetchrow_array) {
      $subalt_1 += $req_obs_time_1;
    }
    $subAlt_1->finish;

    $subAlt_2->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_2) = $subAlt_2->fetchrow_array) {
      $subalt_2 += $req_obs_time_2;
    }
    $subAlt_2->finish;
  }

  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->time_slew_req($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->time_slew_req_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->time_slew_req_2($time_2);
}

## Internal Utility ##
# Name: set_time_slew_cur
# Purpose: sets the time_slew_cur
# Parameters: none
# Returns: nothing
sub set_time_slew_cur{
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_app_time + tax_tot) 
                                     from proposal where panel_id = ? and 
                                     prop_status like 'Y' and type = 'GO'));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;

  my $goTot_1 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_1 + tax_tot_1) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO'));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;

  my $goTot_2 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_2 + tax_tot_2) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO'));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;

  my $tooTot = $self->dbh->prepare(qq(select
                                      sum(app_time + tax/too_prob_app) from 
                                      target where targ_status = 'Y' and
                                      alt_id is null and panel_id = ? and 
                                      response_time is not null));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;

  my $tooTot_1 = $self->dbh->prepare(qq(select
                                        sum(app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;

  my $tooTot_2 = $self->dbh->prepare(qq(select
                                        sum(app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;

  my $alts = $self->dbh->prepare(qq(select prop_id, app_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ?));
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my @alt;
  while (my ($prop_id, $app_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       app_cnt => $app_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subAlt = $self->dbh->prepare(qq(select (app_time + tax/too_prob_app)
                                      from target where targ_status ='Y' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_app)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                        (app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 ='Y' 
                                        and app_time_1 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_1 + tax_1/too_prob_app)
                                        DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                        (app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 ='Y' 
                                        and app_time_2 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_2 + tax_2/too_prob_app)
                                        DESC LIMIT ?));
 
  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
      $subAlt->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time) = $subAlt->fetchrow_array) {
	$subalt += $app_time;
      }
      $subAlt->finish;

      $subAlt_1->execute($self->panel_id(), 
			 $$altgrp{prop_id}, $$altgrp{alt_id},
			 $$altgrp{app_cnt}); 
      while (my ($app_time_1) = $subAlt_1->fetchrow_array) {
	$subalt_1 += $app_time_1;
      }
      $subAlt_1->finish;

      $subAlt_2->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time_2) = $subAlt_2->fetchrow_array) {
	$subalt_2 += $app_time_2;
      }
      $subAlt_2->finish;
  }
  
  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->time_slew_cur($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->time_slew_cur_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->time_slew_cur_2($time_2);
}

## Internal Utility ##
# Name: set_hel_slew_cur
# Purpose: sets the hel_slew_cur
# Parameters: none
# Returns: nothing
sub set_hel_slew_cur{
    my ($self) = @_;
    my $goTot = $self->dbh->prepare(qq(select
                                     sum(total_app_hel + tax_tot)
                                     from proposal where panel_id = ? and
                                     prop_status like 'Y' and type = 'GO'));
    $goTot->execute($self->panel_id());
    my ($goTime) = $goTot->fetchrow_array;
    $goTot->finish;

    my $goTot_1 = $self->dbh->prepare(qq(select
                                       sum(total_app_hel_1 + tax_tot_1)
                                       from proposal where panel_id = ? and
                                       prop_status like 'Y' and type = 'GO'));
    $goTot_1->execute($self->panel_id());
    my ($goTime_1) = $goTot_1->fetchrow_array;
    $goTot_1->finish;

    my $goTot_2 = $self->dbh->prepare(qq(select
                                       sum(total_app_hel_2 + tax_tot_2)
                                       from proposal where panel_id = ? and
                                       prop_status like 'Y' and type = 'GO'));
    $goTot_2->execute($self->panel_id());
    my ($goTime_2) = $goTot_2->fetchrow_array;
    $goTot_2->finish;

    my $tooTot = $self->dbh->prepare(qq(select
                                      sum(app_hel + tax/too_prob_app) from
                                      target where targ_status = 'Y' and
                                      alt_id is null and panel_id = ? and
                                      response_time is not null));
    $tooTot->execute($self->panel_id());
    my ($tooTime) = $tooTot->fetchrow_array;
    $tooTot->finish;

    my $tooTot_1 = $self->dbh->prepare(qq(select
                                        sum(app_hel_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null));
    $tooTot_1->execute($self->panel_id());
    my ($tooTime_1) = $tooTot_1->fetchrow_array;
    $tooTot_1->finish;

    my $tooTot_2 = $self->dbh->prepare(qq(select
                                        sum(app_hel_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null));
    $tooTot_2->execute($self->panel_id());
    my ($tooTime_2) = $tooTot_2->fetchrow_array;
    $tooTot_2->finish;

    my $alts = $self->dbh->prepare(qq(select prop_id, app_cnt, alt_id from
                                    alternate_target_group where
                                    panel_id = ?));
    # Now subtract out the alternate targets
    $alts->execute($self->panel_id);
    my @alt;
    while (my ($prop_id, $app_cnt, $alt_id) = $alts->fetchrow_array) {
        my %alt = (prop_id => $prop_id,
            app_cnt => $app_cnt,
            alt_id => $alt_id);
        push @alt, \%alt;
    }
    $alts->finish;

    my $subAlt = $self->dbh->prepare(qq(select (app_hel + tax/too_prob_app)
                                      from target where targ_status ='Y' and
                                      panel_id = ? and prop_id = ? and
                                      alt_id = ?
                                      order by (app_hel + tax/too_prob_app)
                                      DESC LIMIT ?));
    my $subAlt_1 = $self->dbh->prepare(qq(select
                                        (app_hel_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 ='Y'
                                        and app_hel_1 > 0 and
                                        panel_id = ? and prop_id = ? and
                                        alt_id = ?
                                        order by
                                        (app_hel_1 + tax_1/too_prob_app)
                                        DESC LIMIT ?));
    my $subAlt_2 = $self->dbh->prepare(qq(select
                                        (app_hel_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 ='Y'
                                        and app_hel_2 > 0 and
                                        panel_id = ? and prop_id = ? and
                                        alt_id = ?
                                        order by
                                        (app_hel_2 + tax_2/too_prob_app)
                                        DESC LIMIT ?));

    my $subalt = 0;
    my $subalt_1 = 0;
    my $subalt_2 = 0;
    foreach my $altgrp (@alt) {
        $subAlt->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
            $$altgrp{app_cnt});
        while (my ($app_time) = $subAlt->fetchrow_array) {
            $subalt += $app_time;
        }
        $subAlt->finish;

        $subAlt_1->execute($self->panel_id(),
            $$altgrp{prop_id}, $$altgrp{alt_id},
            $$altgrp{app_cnt});
        while (my ($app_time_1) = $subAlt_1->fetchrow_array) {
            $subalt_1 += $app_time_1;
        }
        $subAlt_1->finish;

        $subAlt_2->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
            $$altgrp{app_cnt});
        while (my ($app_time_2) = $subAlt_2->fetchrow_array) {
            $subalt_2 += $app_time_2;
        }
        $subAlt_2->finish;
    }

    my $time = $goTime + $tooTime + $subalt;
    $time = sprintf "%.02f", $time;
    $self->hel_slew_cur($time);

    my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
    $time_1 = sprintf "%.02f", $time_1;
    $self->hel_slew_cur_1($time_1);

    my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
    $time_2 = sprintf "%.02f", $time_2;
    $self->hel_slew_cur_2($time_2);
}

## Internal Utility ##
# Name: set_time_slew_prob_req
# Purpose: sets the time_slew_prob_req
# Parameters: none
# Returns: nothing
sub set_time_slew_prob_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_req_time), 
                                     sum(prop_req_time_1), sum(prop_req_time_2)
                                     from proposal 
                                     where  panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status != '$STAT_BPP'));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->time_slew_prob_req($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->time_slew_prob_req_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->time_slew_prob_req_2($time_2);
}

## Internal Utility ##
# Name: set_hel_slew_prob_req
# Purpose: sets the hel_slew_prob_req
# Parameters: none
# Returns: nothing
sub set_hel_slew_prob_req{
    my ($self) = @_;
    my $query = $self->dbh->prepare(qq(select sum(prop_req_hel),
                                     sum(prop_req_hel_1), sum(prop_req_hel_2)
                                     from proposal
                                     where  panel_id = ? and
                                     type not in ('ARCHIVE', 'THEORY') and
				     prop_status != '$STAT_BPP'));

    $query->execute($self->panel_id);
    my ($time, $time_1, $time_2) = $query->fetchrow_array;
    $query->finish;

    $time = sprintf "%.02f", $time;
    $self->hel_slew_prob_req($time);

    $time_1 = sprintf "%.02f", $time_1;
    $self->hel_slew_prob_req_1($time_1);

    $time_2 = sprintf "%.02f", $time_2;
    $self->hel_slew_prob_req_2($time_2);
}

## Internal Utility ##
# Name: set_time_slew_prob_allot
# Purpose: sets the time_slew_prob_allot
# Parameters: none
# Returns: nothing
sub set_time_slew_prob_allot{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select total_exp_time, total_exp_time_1, 
                                     total_exp_time_2 from allotment 
				     where panel_id = ?));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->time_slew_prob_allot($time);

  $time_1 = sprintf "%.02f", $time_1;
  $time_1 = $empty if $time_1 <= 0.0;
  $self->time_slew_prob_allot_1($time_1);
  
  $time_2 = sprintf "%.02f", $time_2;
  $time_2 = $empty if $time_2 <= 0.0;
  $self->time_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_hel_slew_prob_allot
# Purpose: sets the hel_slew_prob_allot.
# Parameters: none
# Returns: nothing
sub set_hel_slew_prob_allot{
    my ($self) = @_;
    my ($time, $time_1, $time_2);

    if ($self->panel_id() != 99) {
        my $query = $self->dbh->prepare(qq(select total_hel_time, total_hel_time_1,
                                         total_hel_time_2 from allotment
                         where panel_id = ?));
        $query->execute($self->panel_id());
        ($time, $time_1, $time_2) = $query->fetchrow_array;
        $query->finish;
    }
    else{
        $self->set_bpp_hel_slew_prob_allot();
        $time = $self->bpp_hel_slew_prob_allot;
        $time_1 = $self->bpp_hel_slew_prob_allot_1;
        $time_2 = $self->bpp_hel_slew_prob_allot_2;
    }

    $time = sprintf "%.02f", $time;
    $self->hel_slew_prob_allot($time);

    $time_1 = sprintf "%.02f", $time_1;
    # $time_1 = $empty if $time_1 <= 0.0; # match RC formatting
    $self->hel_slew_prob_allot_1($time_1);

    $time_2 = sprintf "%.02f", $time_2;
    # $time_2 = $empty if $time_2 <= 0.0; # match RC formatting
    $self->hel_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_time_slew_prob_cur
# Purpose: sets the time_slew_prob_cur
# Parameters: none
# Returns: nothing
sub set_time_slew_prob_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_app_time), 
                                     sum(prop_app_time_1), sum(prop_app_time_2)
                                     from proposal 
                                     where  panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status = 'Y'));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->time_slew_prob_cur($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->time_slew_prob_cur_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->time_slew_prob_cur_2($time_2);
}

## Internal Utility ##
# Name: set_hel_slew_prob_cur
# Purpose: sets the time_slew_prob_cur
# Parameters: none
# Returns: nothing
sub set_hel_slew_prob_cur{
    my ($self) = @_;
    my $query = $self->dbh->prepare(qq(select sum(prop_app_hel),
                                     sum(prop_app_hel_1), sum(prop_app_hel_2)
                                     from proposal
                                     where  panel_id = ? and
                                     type not in ('ARCHIVE', 'THEORY') and
				     prop_status = 'Y'));

    $query->execute($self->panel_id);
    my ($time, $time_1, $time_2) = $query->fetchrow_array;
    $query->finish;

    $time = sprintf "%.02f", $time;
    $self->hel_slew_prob_cur($time);

    $time_1 = sprintf "%.02f", $time_1;
    $self->hel_slew_prob_cur_1($time_1);

    $time_2 = sprintf "%.02f", $time_2;
    $self->hel_slew_prob_cur_2($time_2);
}

## Internal Utility ##
# Name: set_time_slew_prob_bal
# Purpose: sets the time_slew_prob_bal
# Parameters: none
# Returns: nothing
sub set_time_slew_prob_bal{
  my ($self) = @_;
  $self->set_time_slew_prob_cur();
  my $time = $self->time_slew_prob_allot - $self->time_slew_prob_cur;
  $time = sprintf "%.02f", $time;
  $self->time_slew_prob_bal($time);

  my $time_1 = $self->time_slew_prob_allot_1 - $self->time_slew_prob_cur_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->time_slew_prob_bal_1($time_1);
  $self->time_slew_prob_bal_1($empty) if $self->time_slew_prob_allot_1 =~ /$empty/;

  my $time_2 = $self->time_slew_prob_allot_2 - $self->time_slew_prob_cur_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->time_slew_prob_bal_2($time_2);
  $self->time_slew_prob_bal_2($empty) if $self->time_slew_prob_allot_2 =~ /$empty/;
}

## Internal Utility ##
# Name: set_hel_slew_prob_bal
# Purpose: sets the hel_slew_prob_bal
# Parameters: none
# Returns: nothing
sub set_hel_slew_prob_bal{
    my ($self) = @_;
    $self->set_hel_slew_prob_cur();
    my $time = $self->hel_slew_prob_allot - $self->hel_slew_prob_cur;
    $time = sprintf "%.02f", $time;
    $self->hel_slew_prob_bal($time);

    my $time_1 = $self->hel_slew_prob_allot_1 - $self->hel_slew_prob_cur_1;
    $time_1 = sprintf "%.02f", $time_1;
    $self->hel_slew_prob_bal_1($time_1);
    $self->hel_slew_prob_bal_1($empty) if $self->hel_slew_prob_allot_1 =~ /$empty/;

    my $time_2 = $self->hel_slew_prob_allot_2 - $self->hel_slew_prob_cur_2;
    $time_2 = sprintf "%.02f", $time_2;
    $self->hel_slew_prob_bal_2($time_2);
    $self->hel_slew_prob_bal_2($empty) if $self->hel_slew_prob_allot_2 =~ /$empty/;
}

## Internal Utility ##
# Name: set_lp_time_req
# Purpose: sets the lp_time_req
# Parameters: none
# Returns: nothing
sub set_lp_time_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_req_time),
                                     sum(total_req_time_1), 
                                     sum(total_req_time_2) from
				     proposal where panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and
                                     big_proj in ('LP','GTO-LP') and prop_status != '$STAT_BPP'));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $self->lp_time_req($time);
  $self->lp_time_req_1($time_1);
  $self->lp_time_req_2($time_2);
}

## Internal Utility ##
# Name: set_lp_time_cur
# Purpose: sets the lp_time_cur
# Parameters: none
# Returns: nothing
sub set_lp_time_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_app_time),
                                     sum(total_app_time_1),
                                     sum(total_app_time_2) from proposal 
                                     where panel_id = ? and big_proj in ('LP','GTO-LP') 
				     and prop_status = 'Y' and type not in
                                     ('ARCHIVE', 'THEORY')));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $time_1 = sprintf "%.02f", $time_1;
  $time_2 = sprintf "%.02f", $time_2;

  $self->lp_time_cur($time);
  $self->lp_time_cur_1($time_1);
  $self->lp_time_cur_2($time_2);
}

## Internal Utility ##
# Name: set_lp_time_slew_req
# Purpose: sets the lp_time_slew_req
# Parameters: none
# Returns: nothing
sub set_lp_time_slew_req {
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_req_time + tax_req) 
                                     from proposal where panel_id = ? and 
                                     prop_status != '$STAT_BPP' and type = 'GO' and
                                     big_proj in ('LP','GTO-LP')));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;
  
  my $goTot_1 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_1 + tax_req_1) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO' and 
                                      big_proj in ('LP','GTO-LP')));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;
  
  my $goTot_2 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_2 + tax_req_2) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO' and
                                      big_proj in ('LP','GTO-LP')));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;
  
  my $tooTot = $self->dbh->prepare(qq(select 
                                      sum(req_time + tax_req/too_prob_req)
				      from target where targ_status != '$STAT_BPP' and
				      panel_id = ? and alt_id is null and
                                      response_time is not null and prop_id in
                                      (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;
  
  my $tooTot_1 = $self->dbh->prepare(qq(select 
                                       sum(req_time_1 + tax_req_1/too_prob_req)
		       		       from target where targ_status_1 != '$STAT_BPP'
                                       and req_time_1 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null and prop_id
                                       in (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;
  
   my $tooTot_2 = $self->dbh->prepare(qq(select 
                                       sum(req_time_2 + tax_req_2/too_prob_req)
		       		       from target where targ_status_2 != '$STAT_BPP'
                                       and req_time_2 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null and prop_id in
                                       (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;
  
  my $alts = $self->dbh->prepare(qq(select prop_id, req_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ? and prop_id in
                                    (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  my $subAlt = $self->dbh->prepare(qq(select (req_time + tax/too_prob_req)
                                      from target where targ_status != '$STAT_BPP' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_req)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                       (req_time_1 + tax_1/too_prob_req)
                                       from target where targ_status_1 != '$STAT_BPP' 
                                       and req_time_1 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_1 + tax_1/too_prob_req)  
                                       DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                       (req_time_2 + tax_2/too_prob_req)
                                       from target where targ_status_2 != '$STAT_BPP' 
                                       and req_time_2 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_2 + tax_2/too_prob_req)  
                                       DESC LIMIT ?));
  
  
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my (@alt);
  while (my ($prop_id, $req_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       req_cnt => $req_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
    $subAlt->execute($self->panel_id, $$altgrp{prop_id}, 
		     $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time) = $subAlt->fetchrow_array) {
      $subalt += $req_obs_time;
    }
    $subAlt->finish;

    $subAlt_1->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_1) = $subAlt_1->fetchrow_array) {
      $subalt_1 += $req_obs_time_1;
    }
    $subAlt_1->finish;

    $subAlt_2->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_2) = $subAlt_2->fetchrow_array) {
      $subalt_2 += $req_obs_time_2;
    }
    $subAlt_2->finish;
  }

  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->lp_time_slew_req($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->lp_time_slew_req_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->lp_time_slew_req_2($time_2);
}

## Internal Utility ##
# Name: set_lp_time_slew_cur
# Purpose: sets the lp_time_slew_cur
# Parameters: none
# Returns: nothing
sub set_lp_time_slew_cur{
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_app_time + tax_tot) 
                                     from proposal where panel_id = ? and 
                                     prop_status like 'Y' and type = 'GO' and
                                     big_proj in ('LP','GTO-LP')));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;

  my $goTot_1 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_1 + tax_tot_1) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO' and
                                       big_proj in ('LP','GTO-LP')));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;

  my $goTot_2 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_2 + tax_tot_2) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO' and
                                       big_proj in ('LP','GTO-LP')));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;

  my $tooTot = $self->dbh->prepare(qq(select
                                      sum(app_time + tax/too_prob_app) from 
                                      target where targ_status = 'Y' and
                                      alt_id is null and panel_id = ? and 
                                      response_time is not null and prop_id in
                                      (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;

  my $tooTot_1 = $self->dbh->prepare(qq(select
                                        sum(app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null and prop_id
                                        in (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;

  my $tooTot_2 = $self->dbh->prepare(qq(select
                                        sum(app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null and prop_id
                                        in (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;

  my $alts = $self->dbh->prepare(qq(select prop_id, app_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ? and prop_id in
                                    (select prop_id from proposal where 
                                       big_proj in ('LP','GTO-LP'))));
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my @alt;
  while (my ($prop_id, $app_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       app_cnt => $app_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subAlt = $self->dbh->prepare(qq(select (app_time + tax/too_prob_app)
                                      from target where targ_status ='Y' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_app)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                        (app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 ='Y' 
                                        and app_time_1 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_1 + tax_1/too_prob_app)
                                        DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                        (app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 ='Y' 
                                        and app_time_2 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_2 + tax_2/too_prob_app)
                                        DESC LIMIT ?));
 
  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
      $subAlt->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time) = $subAlt->fetchrow_array) {
	$subalt += $app_time;
      }
      $subAlt->finish;

      $subAlt_1->execute($self->panel_id(), 
			 $$altgrp{prop_id}, $$altgrp{alt_id},
			 $$altgrp{app_cnt}); 
      while (my ($app_time_1) = $subAlt_1->fetchrow_array) {
	$subalt_1 += $app_time_1;
      }
      $subAlt_1->finish;

      $subAlt_2->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time_2) = $subAlt_2->fetchrow_array) {
	$subalt_2 += $app_time_2;
      }
      $subAlt_2->finish;
  }
  
  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->lp_time_slew_cur($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->lp_time_slew_cur_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->lp_time_slew_cur_2($time_2);
}

## Internal Utility ##
# Name: set_lp_time_slew_prob_req
# Purpose: sets the time_slew_prob_req
# Parameters: none
# Returns: nothing
sub set_lp_time_slew_prob_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_req_time), 
                                     sum(prop_req_time_1), sum(prop_req_time_2)
                                     from proposal 
                                     where  panel_id = ? and
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status != '$STAT_BPP' and big_proj in ('LP','GTO-LP')));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->lp_time_slew_prob_req($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->lp_time_slew_prob_req_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->lp_time_slew_prob_req_2($time_2);
}

## Internal Utility ##
# Name: set_lp_time_slew_prob_allot
# Purpose: sets the lp_time_slew_prob_allot
# Parameters: none
# Returns: nothing
sub set_lp_time_slew_prob_allot{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select lp, lp_1, lp_2 from allotment 
				     where panel_id = ?));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->lp_time_slew_prob_allot($time);

  $time_1 = sprintf "%.02f", $time_1;
  $time_1 = $empty if $time_1 <= 0.0;
  $self->lp_time_slew_prob_allot_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $time_2 = $empty if $time_2 <= 0.0;
  $self->lp_time_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_lp_hel_slew_prob_allot
# Purpose: sets the lp_hel_slew_prob_allot
# Parameters: none
# Returns: nothing
sub set_lp_hel_slew_prob_allot{
    my ($self) = @_;
    my $query = $self->dbh->prepare(qq(select hel_lp, hel_lp_1, hel_lp_2 from allotment
				     where panel_id = ?));
    $query->execute($self->panel_id());
    my ($time, $time_1, $time_2) = $query->fetchrow_array;
    $query->finish;

    $time = sprintf "%.02f", $time;
    $self->lp_hel_slew_prob_allot($time);

    $time_1 = sprintf "%.02f", $time_1;
    $time_1 = $empty if $time_1 <= 0.0;
    $self->lp_hel_slew_prob_allot_1($time_1);

    $time_2 = sprintf "%.02f", $time_2;
    $time_2 = $empty if $time_2 <= 0.0;
    $self->lp_hel_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_lp_time_slew_prob_cur
# Purpose: sets the lp_time_slew_prob_cur
# Parameters: none
# Returns: nothing
sub set_lp_time_slew_prob_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_app_time), 
                                     sum(prop_app_time_1), sum(prop_app_time_2)
                                     from proposal 
                                     where  panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status = 'Y' and big_proj in ('LP','GTO-LP')));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->lp_time_slew_prob_cur($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->lp_time_slew_prob_cur_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->lp_time_slew_prob_cur_2($time_2);
}

## Internal Utility ##
# Name: set_lp_time_slew_prob_bal
# Purpose: sets the lp_time_slew_prob_bal
# Parameters: none
# Returns: nothing
sub set_lp_time_slew_prob_bal{
  my ($self) = @_;
  $self->set_lp_time_slew_prob_cur();
  my $time = $self->lp_time_slew_prob_allot - $self->lp_time_slew_prob_cur;
  $time = sprintf "%.02f", $time;
  $self->lp_time_slew_prob_bal($time);

  my $time_1 = $self->lp_time_slew_prob_allot_1 - $self->lp_time_slew_prob_cur_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->lp_time_slew_prob_bal_1($time_1);
  $self->lp_time_slew_prob_bal_1($empty) if $self->lp_time_slew_prob_allot_1 =~ /$empty/;

  my $time_2 = $self->lp_time_slew_prob_allot_2 - $self->lp_time_slew_prob_cur_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->lp_time_slew_prob_bal_2($time_2);
  $self->lp_time_slew_prob_bal_2($empty) if $self->lp_time_slew_prob_allot_2 =~ /$empty/;
}

## Internal Utility ##
# Name: set_vlp_time_req
# Purpose: sets the vlp_time_req
# Parameters: none
# Returns: nothing
sub set_vlp_time_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_req_time),
                                     sum(total_req_time_1), 
                                     sum(total_req_time_2) from
				     proposal where panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and
                                     big_proj = 'VLP' and prop_status != '$STAT_BPP'));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $self->vlp_time_req($time);
  $self->vlp_time_req_1($time_1);
  $self->vlp_time_req_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_time_cur
# Purpose: sets the vlp_time_cur
# Parameters: none
# Returns: nothing
sub set_vlp_time_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_app_time),
                                     sum(total_app_time_1),
                                     sum(total_app_time_2) from proposal 
                                     where panel_id = ? and big_proj = 'VLP' 
				     and prop_status = 'Y' and type not in
                                     ('ARCHIVE', 'THEORY')));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $time_1 = sprintf "%.02f", $time_1;
  $time_2 = sprintf "%.02f", $time_2;

  $self->vlp_time_cur($time);
  $self->vlp_time_cur_1($time_1);
  $self->vlp_time_cur_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_time_slew_req
# Purpose: sets the vlp_time_slew_req
# Parameters: none
# Returns: nothing
sub set_vlp_time_slew_req {
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_req_time + tax_req) 
                                     from proposal where panel_id = ? and 
                                     prop_status != '$STAT_BPP' and type = 'GO' and
                                     big_proj = 'VLP'));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;
  
  my $goTot_1 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_1 + tax_req_1) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO' and 
                                      big_proj = 'VLP'));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;
  
  my $goTot_2 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_2 + tax_req_2) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO' and
                                      big_proj = 'VLP'));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;
  
  my $tooTot = $self->dbh->prepare(qq(select 
                                      sum(req_time + tax_req/too_prob_req)
				      from target where targ_status != '$STAT_BPP' and
				      panel_id = ? and alt_id is null and
                                      response_time is not null and prop_id in
                                      (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;
  
  my $tooTot_1 = $self->dbh->prepare(qq(select 
                                       sum(req_time_1 + tax_req_1/too_prob_req)
		       		       from target where targ_status_1 != '$STAT_BPP'
                                       and req_time_1 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null and prop_id
                                       in (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;
  
   my $tooTot_2 = $self->dbh->prepare(qq(select 
                                       sum(req_time_2 + tax_req_2/too_prob_req)
		       		       from target where targ_status_2 != '$STAT_BPP'
                                       and req_time_2 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null and prop_id in
                                       (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;
  
  my $alts = $self->dbh->prepare(qq(select prop_id, req_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ? and prop_id in
                                    (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  my $subAlt = $self->dbh->prepare(qq(select (req_time + tax/too_prob_req)
                                      from target where targ_status != '$STAT_BPP' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_req)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                       (req_time_1 + tax_1/too_prob_req)
                                       from target where targ_status_1 != '$STAT_BPP' 
                                       and req_time_1 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_1 + tax_1/too_prob_req)  
                                       DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                       (req_time_2 + tax_2/too_prob_req)
                                       from target where targ_status_2 != '$STAT_BPP' 
                                       and req_time_2 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_2 + tax_2/too_prob_req)  
                                       DESC LIMIT ?));
  
  
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my (@alt);
  while (my ($prop_id, $req_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       req_cnt => $req_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
    $subAlt->execute($self->panel_id, $$altgrp{prop_id}, 
		     $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time) = $subAlt->fetchrow_array) {
      $subalt += $req_obs_time;
    }
    $subAlt->finish;

    $subAlt_1->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_1) = $subAlt_1->fetchrow_array) {
      $subalt_1 += $req_obs_time_1;
    }
    $subAlt_1->finish;

    $subAlt_2->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_2) = $subAlt_2->fetchrow_array) {
      $subalt_2 += $req_obs_time_2;
    }
    $subAlt_2->finish;
  }

  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->vlp_time_slew_req($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->vlp_time_slew_req_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->vlp_time_slew_req_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_time_slew_cur
# Purpose: sets the vlp_time_slew_cur
# Parameters: none
# Returns: nothing
sub set_vlp_time_slew_cur{
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_app_time + tax_tot) 
                                     from proposal where panel_id = ? and 
                                     prop_status like 'Y' and type = 'GO' and
                                     big_proj = 'VLP'));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;

  my $goTot_1 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_1 + tax_tot_1) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO' and
                                       big_proj = 'VLP'));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;

  my $goTot_2 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_2 + tax_tot_2) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO' and
                                       big_proj = 'VLP'));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;

  my $tooTot = $self->dbh->prepare(qq(select
                                      sum(app_time + tax/too_prob_app) from 
                                      target where targ_status = 'Y' and
                                      alt_id is null and panel_id = ? and 
                                      response_time is not null and prop_id in
                                      (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;

  my $tooTot_1 = $self->dbh->prepare(qq(select
                                        sum(app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null and prop_id
                                        in (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;

  my $tooTot_2 = $self->dbh->prepare(qq(select
                                        sum(app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null and prop_id
                                        in (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;

  my $alts = $self->dbh->prepare(qq(select prop_id, app_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ? and prop_id in
                                    (select prop_id from proposal where 
                                       big_proj = 'VLP')));
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my @alt;
  while (my ($prop_id, $app_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       app_cnt => $app_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subAlt = $self->dbh->prepare(qq(select (app_time + tax/too_prob_app)
                                      from target where targ_status ='Y' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_app)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                        (app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 ='Y' 
                                        and app_time_1 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_1 + tax_1/too_prob_app)
                                        DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                        (app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 ='Y' 
                                        and app_time_2 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_2 + tax_2/too_prob_app)
                                        DESC LIMIT ?));
 
  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
      $subAlt->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time) = $subAlt->fetchrow_array) {
	$subalt += $app_time;
      }
      $subAlt->finish;

      $subAlt_1->execute($self->panel_id(), 
			 $$altgrp{prop_id}, $$altgrp{alt_id},
			 $$altgrp{app_cnt}); 
      while (my ($app_time_1) = $subAlt_1->fetchrow_array) {
	$subalt_1 += $app_time_1;
      }
      $subAlt_1->finish;

      $subAlt_2->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time_2) = $subAlt_2->fetchrow_array) {
	$subalt_2 += $app_time_2;
      }
      $subAlt_2->finish;
  }
  
  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->vlp_time_slew_cur($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->vlp_time_slew_cur_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->vlp_time_slew_cur_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_time_slew_prob_req
# Purpose: sets the vlp_time_slew_prob_req
# Parameters: none
# Returns: nothing
sub set_vlp_time_slew_prob_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_req_time), 
                                     sum(prop_req_time_1), sum(prop_req_time_2)
                                     from proposal 
                                     where  panel_id = ? and
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status != '$STAT_BPP' and big_proj = 'VLP'));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->vlp_time_slew_prob_req($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->vlp_time_slew_prob_req_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->vlp_time_slew_prob_req_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_time_slew_prob_allot
# Purpose: sets the vlp_time_slew_prob_allot
# Parameters: none
# Returns: nothing
sub set_vlp_time_slew_prob_allot{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select vlp, vlp_1, vlp_2 from allotment
				     where panel_id = ?));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->vlp_time_slew_prob_allot($time);

  $time_1 = sprintf "%.02f", $time_1;
  $time_1 = $empty if $time_1 <= 0.0;
  $self->vlp_time_slew_prob_allot_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $time_2 = $empty if $time_2 <= 0.0;
  $self->vlp_time_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_hel_slew_prob_allot
# Purpose: sets the vlp_hel_slew_prob_allot
# Parameters: none
# Returns: nothing
sub set_vlp_hel_slew_prob_allot{
    my ($self) = @_;
    my $query = $self->dbh->prepare(qq(select hel_vlp, hel_vlp_1, hel_vlp_2 from allotment
				     where panel_id = ?));
    $query->execute($self->panel_id());
    my ($time, $time_1, $time_2) = $query->fetchrow_array;
    $query->finish;

    $time = sprintf "%.02f", $time;
    $self->vlp_hel_slew_prob_allot($time);

    $time_1 = sprintf "%.02f", $time_1;
    $time_1 = $empty if $time_1 <= 0.0;
    $self->vlp_hel_slew_prob_allot_1($time_1);

    $time_2 = sprintf "%.02f", $time_2;
    $time_2 = $empty if $time_2 <= 0.0;
    $self->vlp_hel_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_time_slew_prob_cur
# Purpose: sets the vlp_time_slew_prob_cur
# Parameters: none
# Returns: nothing
sub set_vlp_time_slew_prob_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_app_time), 
                                     sum(prop_app_time_1), sum(prop_app_time_2)
                                     from proposal 
                                     where  panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status = 'Y' and big_proj = 'VLP'));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->vlp_time_slew_prob_cur($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->vlp_time_slew_prob_cur_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->vlp_time_slew_prob_cur_2($time_2);
}

## Internal Utility ##
# Name: set_vlp_time_slew_prob_bal
# Purpose: sets the vlp_time_slew_prob_bal
# Parameters: none
# Returns: nothing
sub set_vlp_time_slew_prob_bal{
  my ($self) = @_;
  $self->set_vlp_time_slew_prob_cur();
  my $time = $self->vlp_time_slew_prob_allot - $self->vlp_time_slew_prob_cur;
  $time = sprintf "%.02f", $time;
  $self->vlp_time_slew_prob_bal($time);

  my $time_1 = $self->vlp_time_slew_prob_allot_1 - $self->vlp_time_slew_prob_cur_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->vlp_time_slew_prob_bal_1($time_1);
  $self->vlp_time_slew_prob_bal_1($empty) if $self->vlp_time_slew_prob_allot_1 =~ /$empty/;

  my $time_2 = $self->vlp_time_slew_prob_allot_2 - $self->vlp_time_slew_prob_cur_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->vlp_time_slew_prob_bal_2($time_2);
  $self->vlp_time_slew_prob_bal_2($empty) if $self->vlp_time_slew_prob_allot_2 =~ /$empty/;
}

## Internal Utility ##
# Name: set_xvp_time_req
# Purpose: sets the xvp_time_req
# Parameters: none
# Returns: nothing
sub set_xvp_time_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_req_time),
                                     sum(total_req_time_1), 
                                     sum(total_req_time_2) from
				     proposal where panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and
                                     big_proj = 'XVP' and prop_status != '$STAT_BPP'));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $self->xvp_time_req($time);
  $self->xvp_time_req_1($time_1);
  $self->xvp_time_req_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_time_cur
# Purpose: sets the xvp_time_cur
# Parameters: none
# Returns: nothing
sub set_xvp_time_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_app_time),
                                     sum(total_app_time_1),
                                     sum(total_app_time_2) from proposal 
                                     where panel_id = ? and big_proj = 'XVP' 
				     and prop_status = 'Y' and type not in
                                     ('ARCHIVE', 'THEORY')));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $time_1 = sprintf "%.02f", $time_1;
  $time_2 = sprintf "%.02f", $time_2;

  $self->xvp_time_cur($time);
  $self->xvp_time_cur_1($time_1);
  $self->xvp_time_cur_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_time_slew_req
# Purpose: sets the xvp_time_slew_req
# Parameters: none
# Returns: nothing
sub set_xvp_time_slew_req {
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_req_time + tax_req) 
                                     from proposal where panel_id = ? and 
                                     prop_status != '$STAT_BPP' and type = 'GO' and
                                     big_proj = 'XVP'));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;
  
  my $goTot_1 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_1 + tax_req_1) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO' and 
                                      big_proj = 'XVP'));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;
  
  my $goTot_2 = $self->dbh->prepare(qq(select 
                                      sum(total_req_time_2 + tax_req_2) 
                                      from proposal where panel_id = ? and 
                                      prop_status != '$STAT_BPP' and  type = 'GO' and
                                      big_proj = 'XVP'));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;
  
  my $tooTot = $self->dbh->prepare(qq(select 
                                      sum(req_time + tax_req/too_prob_req)
				      from target where targ_status != '$STAT_BPP' and
				      panel_id = ? and alt_id is null and
                                      response_time is not null and prop_id in
                                      (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;
  
  my $tooTot_1 = $self->dbh->prepare(qq(select 
                                       sum(req_time_1 + tax_req_1/too_prob_req)
		       		       from target where targ_status_1 != '$STAT_BPP'
                                       and req_time_1 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null and prop_id
                                       in (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;
  
   my $tooTot_2 = $self->dbh->prepare(qq(select 
                                       sum(req_time_2 + tax_req_2/too_prob_req)
		       		       from target where targ_status_2 != '$STAT_BPP'
                                       and req_time_2 > 0
				       and panel_id = ? and alt_id is null and
                                       response_time is not null and prop_id in
                                       (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;
  
  my $alts = $self->dbh->prepare(qq(select prop_id, req_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ? and prop_id in
                                    (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  my $subAlt = $self->dbh->prepare(qq(select (req_time + tax/too_prob_req)
                                      from target where targ_status != '$STAT_BPP' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_req)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                       (req_time_1 + tax_1/too_prob_req)
                                       from target where targ_status_1 != '$STAT_BPP' 
                                       and req_time_1 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_1 + tax_1/too_prob_req)  
                                       DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                       (req_time_2 + tax_2/too_prob_req)
                                       from target where targ_status_2 != '$STAT_BPP' 
                                       and req_time_2 > 0 and
                                       panel_id = ? and prop_id = ? and 
                                       alt_id = ? 
                                       order by 
                                       (app_time_2 + tax_2/too_prob_req)  
                                       DESC LIMIT ?));
  
  
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my (@alt);
  while (my ($prop_id, $req_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       req_cnt => $req_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
    $subAlt->execute($self->panel_id, $$altgrp{prop_id}, 
		     $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time) = $subAlt->fetchrow_array) {
      $subalt += $req_obs_time;
    }
    $subAlt->finish;

    $subAlt_1->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_1) = $subAlt_1->fetchrow_array) {
      $subalt_1 += $req_obs_time_1;
    }
    $subAlt_1->finish;

    $subAlt_2->execute($self->panel_id, $$altgrp{prop_id}, 
		       $$altgrp{alt_id}, $$altgrp{req_cnt});
    while (my ($req_obs_time_2) = $subAlt_2->fetchrow_array) {
      $subalt_2 += $req_obs_time_2;
    }
    $subAlt_2->finish;
  }

  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->xvp_time_slew_req($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->xvp_time_slew_req_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->xvp_time_slew_req_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_time_slew_cur
# Purpose: sets the xvp_time_slew_cur
# Parameters: none
# Returns: nothing
sub set_xvp_time_slew_cur{
  my ($self) = @_;
  my $goTot = $self->dbh->prepare(qq(select 
                                     sum(total_app_time + tax_tot) 
                                     from proposal where panel_id = ? and 
                                     prop_status like 'Y' and type = 'GO' and
                                     big_proj = 'XVP'));
  $goTot->execute($self->panel_id());
  my ($goTime) = $goTot->fetchrow_array;
  $goTot->finish;

  my $goTot_1 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_1 + tax_tot_1) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO' and
                                       big_proj = 'XVP'));
  $goTot_1->execute($self->panel_id());
  my ($goTime_1) = $goTot_1->fetchrow_array;
  $goTot_1->finish;

  my $goTot_2 = $self->dbh->prepare(qq(select 
                                       sum(total_app_time_2 + tax_tot_2) 
                                       from proposal where panel_id = ? and 
                                       prop_status like 'Y' and type = 'GO' and
                                       big_proj = 'XVP'));
  $goTot_2->execute($self->panel_id());
  my ($goTime_2) = $goTot_2->fetchrow_array;
  $goTot_2->finish;

  my $tooTot = $self->dbh->prepare(qq(select
                                      sum(app_time + tax/too_prob_app) from 
                                      target where targ_status = 'Y' and
                                      alt_id is null and panel_id = ? and 
                                      response_time is not null and prop_id in
                                      (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  $tooTot->execute($self->panel_id());
  my ($tooTime) = $tooTot->fetchrow_array;
  $tooTot->finish;

  my $tooTot_1 = $self->dbh->prepare(qq(select
                                        sum(app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null and prop_id
                                        in (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  $tooTot_1->execute($self->panel_id());
  my ($tooTime_1) = $tooTot_1->fetchrow_array;
  $tooTot_1->finish;

  my $tooTot_2 = $self->dbh->prepare(qq(select
                                        sum(app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 = 'Y'
                                        and alt_id is null and panel_id = ? and
                                        response_time is not null and prop_id
                                        in (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  $tooTot_2->execute($self->panel_id());
  my ($tooTime_2) = $tooTot_2->fetchrow_array;
  $tooTot_2->finish;

  my $alts = $self->dbh->prepare(qq(select prop_id, app_cnt, alt_id from 
                                    alternate_target_group where 
                                    panel_id = ? and prop_id in
                                    (select prop_id from proposal where 
                                       big_proj = 'XVP')));
  # Now subtract out the alternate targets
  $alts->execute($self->panel_id);
  my @alt;
  while (my ($prop_id, $app_cnt, $alt_id) = $alts->fetchrow_array) {
    my %alt = (prop_id => $prop_id,
	       app_cnt => $app_cnt,
	       alt_id => $alt_id);
    push @alt, \%alt;
  }
  $alts->finish;

  my $subAlt = $self->dbh->prepare(qq(select (app_time + tax/too_prob_app)
                                      from target where targ_status ='Y' and 
                                      panel_id = ? and prop_id = ? and 
                                      alt_id = ? 
                                      order by (app_time + tax/too_prob_app)  
                                      DESC LIMIT ?));
  my $subAlt_1 = $self->dbh->prepare(qq(select 
                                        (app_time_1 + tax_1/too_prob_app)
                                        from target where targ_status_1 ='Y' 
                                        and app_time_1 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_1 + tax_1/too_prob_app)
                                        DESC LIMIT ?));
  my $subAlt_2 = $self->dbh->prepare(qq(select 
                                        (app_time_2 + tax_2/too_prob_app)
                                        from target where targ_status_2 ='Y' 
                                        and app_time_2 > 0 and
                                        panel_id = ? and prop_id = ? and 
                                        alt_id = ? 
                                        order by 
                                        (app_time_2 + tax_2/too_prob_app)
                                        DESC LIMIT ?));
 
  my $subalt = 0;
  my $subalt_1 = 0;
  my $subalt_2 = 0;
  foreach my $altgrp (@alt) {
      $subAlt->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time) = $subAlt->fetchrow_array) {
	$subalt += $app_time;
      }
      $subAlt->finish;

      $subAlt_1->execute($self->panel_id(), 
			 $$altgrp{prop_id}, $$altgrp{alt_id},
			 $$altgrp{app_cnt}); 
      while (my ($app_time_1) = $subAlt_1->fetchrow_array) {
	$subalt_1 += $app_time_1;
      }
      $subAlt_1->finish;

      $subAlt_2->execute($self->panel_id(), $$altgrp{prop_id}, $$altgrp{alt_id},
		       $$altgrp{app_cnt}); 
      while (my ($app_time_2) = $subAlt_2->fetchrow_array) {
	$subalt_2 += $app_time_2;
      }
      $subAlt_2->finish;
  }
  
  my $time = $goTime + $tooTime + $subalt;
  $time = sprintf "%.02f", $time;
  $self->xvp_time_slew_cur($time);

  my $time_1 = $goTime_1 + $tooTime_1 + $subalt_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->xvp_time_slew_cur_1($time_1);

  my $time_2 = $goTime_2 + $tooTime_2 + $subalt_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->xvp_time_slew_cur_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_time_slew_prob_req
# Purpose: sets the xvp_time_slew_prob_req
# Parameters: none
# Returns: nothing
sub set_xvp_time_slew_prob_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_req_time), 
                                     sum(prop_req_time_1), sum(prop_req_time_2)
                                     from proposal 
                                     where  panel_id = ? and
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status != '$STAT_BPP' and big_proj = 'XVP'));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->xvp_time_slew_prob_req($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->xvp_time_slew_prob_req_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->xvp_time_slew_prob_req_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_time_slew_prob_allot
# Purpose: sets the xvp_time_slew_prob_allot
# Parameters: none
# Returns: nothing
sub set_xvp_time_slew_prob_allot{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select xvp, xvp_1, xvp_2 from allotment 
				     where panel_id = ?));
  $query->execute($self->panel_id());
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->xvp_time_slew_prob_allot($time);

  $time_1 = sprintf "%.02f", $time_1;
  $time_1 = $empty if $time_1 <= 0.0;
  $self->xvp_time_slew_prob_allot_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $time_2 = $empty if $time_2 <= 0.0;
  $self->xvp_time_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_hel_slew_prob_allot
# Purpose: sets the xvp_hel_slew_prob_allot
# Parameters: none
# Returns: nothing
sub set_xvp_hel_slew_prob_allot{
    my ($self) = @_;
    my $query = $self->dbh->prepare(qq(select hel_xvp, hel_xvp_1, hel_xvp_2 from allotment
				     where panel_id = ?));
    $query->execute($self->panel_id());
    my ($time, $time_1, $time_2) = $query->fetchrow_array;
    $query->finish;

    $time = sprintf "%.02f", $time;
    $self->xvp_hel_slew_prob_allot($time);

    $time_1 = sprintf "%.02f", $time_1;
    $time_1 = $empty if $time_1 <= 0.0;
    $self->xvp_hel_slew_prob_allot_1($time_1);

    $time_2 = sprintf "%.02f", $time_2;
    $time_2 = $empty if $time_2 <= 0.0;
    $self->xvp_hel_slew_prob_allot_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_time_slew_prob_cur
# Purpose: sets the xvp_time_slew_prob_cur
# Parameters: none
# Returns: nothing
sub set_xvp_time_slew_prob_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(prop_app_time), 
                                     sum(prop_app_time_1), sum(prop_app_time_2)
                                     from proposal 
                                     where  panel_id = ? and 
                                     type not in ('ARCHIVE', 'THEORY') and 
				     prop_status = 'Y' and big_proj = 'XVP'));

  $query->execute($self->panel_id);
  my ($time, $time_1, $time_2) = $query->fetchrow_array;
  $query->finish;

  $time = sprintf "%.02f", $time;
  $self->xvp_time_slew_prob_cur($time);

  $time_1 = sprintf "%.02f", $time_1;
  $self->xvp_time_slew_prob_cur_1($time_1);

  $time_2 = sprintf "%.02f", $time_2;
  $self->xvp_time_slew_prob_cur_2($time_2);
}

## Internal Utility ##
# Name: set_xvp_time_slew_prob_bal
# Purpose: sets the xvp_time_slew_prob_bal
# Parameters: none
# Returns: nothing
sub set_xvp_time_slew_prob_bal{
  my ($self) = @_;
  $self->set_xvp_time_slew_prob_cur();
  my $time = $self->xvp_time_slew_prob_allot - $self->xvp_time_slew_prob_cur;
  $time = sprintf "%.02f", $time;
  $self->xvp_time_slew_prob_bal($time);

  my $time_1 = $self->xvp_time_slew_prob_allot_1 - $self->xvp_time_slew_prob_cur_1;
  $time_1 = sprintf "%.02f", $time_1;
  $self->xvp_time_slew_prob_bal_1($time_1);
  $self->xvp_time_slew_prob_bal_1($empty) if $self->xvp_time_slew_prob_allot_1 =~ /$empty/;

  my $time_2 = $self->xvp_time_slew_prob_allot_2 - $self->xvp_time_slew_prob_cur_2;
  $time_2 = sprintf "%.02f", $time_2;
  $self->xvp_time_slew_prob_bal_2($time_2);
  $self->xvp_time_slew_prob_bal_2($empty) if $self->xvp_time_slew_prob_allot_2 =~ /$empty/;
}

## Internal Utility ##
# Name: set_bpp_hel_slew_prob_allot
# Purpose: sets the bpp_hel_slew_prob_allot
# Parameters: none
# Returns: nothing
# vlp and lp HEL allotment is displayed combined in the RC,HEL,TOO table.
# This method just sums the lp and vp allot.
sub set_bpp_hel_slew_prob_allot{
    my ($self) = @_;
    $self->set_lp_hel_slew_prob_allot();
    $self->set_vlp_hel_slew_prob_allot();

    my $hel_lp = $self->lp_hel_slew_prob_allot;
    my $hel_vlp =  $self->vlp_hel_slew_prob_allot;
    my $bpp = sprintf "%.02f", ($hel_lp + $hel_vlp);
    $self->bpp_hel_slew_prob_allot($bpp);

    my $hel_lp_1 =  $self->lp_hel_slew_prob_allot_1;
    my $hel_vlp_1 =  $self->vlp_hel_slew_prob_allot_1;
    my $bpp1 = sprintf "%.02f", ($hel_lp_1 + $hel_vlp_1);
    $self->bpp_hel_slew_prob_allot_1($bpp1);

    my $hel_lp_2 =  $self->lp_hel_slew_prob_allot_2;
    my $hel_vlp_2 =  $self->vlp_hel_slew_prob_allot_2;
    my $bpp2 = sprintf "%.02f", ($hel_lp_2 + $hel_vlp_2);
    $self->bpp_hel_slew_prob_allot_2($bpp2);
}

## Internal Utility ##
# Name: set_arc_req
# Purpose: sets the arc_req
# Parameters: none
# Returns: nothing
sub set_arc_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_req_time) from proposal 
				     where proposal.panel_id = ? and 
				     prop_status != '$STAT_BPP' 
				     and type = 'ARCHIVE'));
  $query->execute($self->panel_id());
  my ($money) = $query->fetchrow_array;
  $query->finish;
  $money = sprintf "%.02f", $money;

  $self->arc_req($money);
}

## Internal Utility ##
# Name: set_the_req
# Purpose: sets the the_req
# Parameters: none
# Returns: nothing
sub set_the_req{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select sum(total_req_time) from proposal 
				     where panel_id = ? and prop_status != '$STAT_BPP' 
				     and type = 'THEORY'));
  $query->execute($self->panel_id());
  my ($money) = $query->fetchrow_array;
  $query->finish;
  $money = sprintf "%.02f", $money;

  $self->the_req($money);
}

## Internal Utility ##
# Name: set_arc_cur
# Purpose: sets the arc_cur
# Parameters: none
# Returns: nothing
sub set_arc_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*), sum(total_app_time) 
				     from proposal 
				     where panel_id = ? and prop_status = 'Y' 
				     and type = 'ARCHIVE'));
  $query->execute($self->panel_id());
  my ($num, $money) = $query->fetchrow_array;
  $query->finish;
  $money = sprintf "%.02f", $money;

  $self->arc_num($num);
  $self->arc_cur($money);
}

## Internal Utility ##
# Name: set_the_cur
# Purpose: sets the the_cur
# Parameters: none
# Returns: nothing
sub set_the_cur{
  my ($self) = @_;
  my $query = $self->dbh->prepare(qq(select count(*), sum(total_app_time) 
				     from proposal 
				     where panel_id = ? and prop_status = 'Y' 
				     and type = 'THEORY'));
  $query->execute($self->panel_id());
  my ($num, $money) = $query->fetchrow_array;
  $query->finish;
  $money = sprintf "%.02f", $money;

  $self->the_num($num);
  $self->the_cur($money);
}

## Internal Utility ##
# Name: set_arc_the_allot
# Purpose: sets the arc_allot and the_allot
# Parameters: none
# Returns: nothing
sub set_arc_the_allot{
    my ($self) = @_;
    my $query = $self->dbh->prepare(qq(select archive_allot, theory_allot from 
				       allotment where panel_id = ?));
    $query->execute($self->panel_id());
    my ($arc, $the) = $query->fetchrow_array;
    $query->finish;

    $arc = sprintf "%.02f", $arc;
    $the = sprintf "%.02f", $the;
    $self->arc_allot($arc);
    $self->the_allot($the);
}

## Internal Utility ##
# Name: set_arc_the_bal
# Purpose: sets the arc_allot and the_bal
# Parameters: none
# Returns: nothing
sub set_arc_the_bal{
  my ($self) = @_;
  $self->set_arc_cur();
  $self->set_the_cur();
  my $arcbal = $self->arc_allot - $self->arc_cur;
  $arcbal = sprintf "%.02f", $arcbal;
  my $thebal = $self->the_allot - $self->the_cur;
  $thebal = sprintf "%.02f", $thebal;
  $self->arc_bal($arcbal);
  $self->the_bal($thebal);
}

## Internal Utility ##
# Name: set_running_money
# Purpose: sets running money for archive and theory proposals
# Parameters: type (ARC|THE)
# Returns: nothing
sub set_running_money {
  my ($self, $view, $type) = @_;
  my $uc_type = uc $type;
  my $lc_type = lc $type;
  my $run_name = "cum_${view}_$lc_type";

  my %gslist = $self->get_group_list($self->fac_groupby, $self->fac_sortby,
				  'N', 'N'. 'N') if $view =~ /fac/;;
  %gslist = $self->get_group_list($self->lcd_groupby, $self->lcd_sortby,
				  $self->hide_triage, $self->hide_arc_the,
				  $self->hide_lp_vlp) if $view =~ /lcd/;
  foreach my $group (sort keys %gslist) {
    my $cum_tot = 0;
    my $sorted_list = $gslist{$group};
    if (%$sorted_list) {
      foreach my $prop_order (sort {$a <=> $b} keys %$sorted_list) {
	my $prop_id = $sorted_list->{$prop_order};
	$cum_tot += $self->proposals->proposal($prop_id)->
	  total_app_time if 
	    ($self->proposals->proposal($prop_id)->prop_status =~ /Y/ 
	     and
	     $self->proposals->proposal($prop_id)->type =~ /$uc_type/);
	$self->proposals->proposal($prop_id)->set($run_name, $cum_tot);
      }
    }
  }
}

## Internal Utility ##
# Name: set_running_total
# Purpose: sets the desired running total column
# Parameters: column to calculate running total for
#             column running total is stored in
# Returns: nothing
sub set_running_total {
  my ($self, $view, $col_name, $run_name) = @_;

  my %gslist = $self->get_group_list($self->fac_groupby, $self->fac_sortby,
				  'N', 'N', 'N') if $view =~ /fac/;
  %gslist = $self->get_group_list($self->lcd_groupby, $self->lcd_sortby,
				  $self->hide_triage, $self->hide_arc_the,
				  $self->hide_lp_vlp) if $view =~ /lcd/;

  foreach my $group (sort keys %gslist) {
    my $cum_tot = 0;
    my $sorted_list = $gslist{$group};
    if (%$sorted_list) {
      foreach my $prop_order (sort {$a <=> $b} keys %$sorted_list) {
	my $prop_id = $sorted_list->{$prop_order};
	
        if ($self->proposals->proposal($prop_id)->type !~ /ARC|THE/) {
          if ($view !~ /lp/) {
	    $cum_tot += $self->proposals->proposal($prop_id)->get($col_name) if 
	 	 $self->proposals->proposal($prop_id)->prop_status =~ /Y/ ;
          } 
          elsif ($view =~ /_vlp/ && $self->proposals->proposal($prop_id)->big_proj =~ /VLP/) {
	    $cum_tot += $self->proposals->proposal($prop_id)->get($col_name) if 
	 	 $self->proposals->proposal($prop_id)->prop_status =~ /Y/ ;
          }
          elsif ($view =~ /_lp/ && $self->proposals->proposal($prop_id)->big_proj =~ /LP/ && $self->proposals->proposal($prop_id)->big_proj !~ /VLP/ ) {
	    $cum_tot += $self->proposals->proposal($prop_id)->get($col_name) if 
	 	 $self->proposals->proposal($prop_id)->prop_status =~ /Y/ ;
          }
        }
	$self->proposals->proposal($prop_id)->set($run_name, sprintf("%.02f",$cum_tot));
      }
    }
  }
}

## Class Method ##
# Name: get_group_list
# Purpose: arrange proposals into groups according to group and sort
# Parameters: groupby 
#             sortby
#             hide_triage flag
#             hide_arc_the flag
#             hide_lp_vlp flag
#             LP/XVP flag
# Returns: reference to hash of groups of proposals
sub get_group_list {
    my ($self, $groupby, $sortby, 
	$hide_triage, $hide_arc_the, $hide_lp_vlp, $BPP) = @_;

    my %gslist;
    my $sort_list_query = qq(select prop_id from proposal where panel_id = ?
			     );

    if ($BPP) {
      $sort_list_query .= " and big_proj = '$BPP'";
    }

    if ($hide_triage =~ /Y/) {
      $sort_list_query .= " and triage not in ('Y','A')";
    }

    if ($hide_arc_the =~ /Y/) {
      $sort_list_query .= " and type not in ('ARCHIVE','THEORY')";
    }

    if ($hide_lp_vlp =~ /Y/) {
      $sort_list_query .= " and big_proj not in ('LP','GTO-LP','VLP','XVP')";
    }

    if ($groupby) {
	$sort_list_query .= " and $groupby = ?";
	
	my $group_list_query = qq(select $groupby from proposal where 
				  panel_id = ?);
	my $group_list = $self->dbh->prepare($group_list_query);
	
	$group_list->execute($self->panel_id);
	while (my($group) = $group_list->fetchrow_array) {
	    $gslist{$group} = '';
	}
	$group_list->finish;
    }
    else {
	$gslist{1} = '';
    }
    $sort_list_query .= " order by $sortby";

    #print STDERR "$sort_list_query\n";

    my $sort_list = $self->dbh->prepare($sort_list_query);
    foreach my $group (sort keys %gslist) {
	$sort_list->execute($self->panel_id, $group) if $groupby;
	$sort_list->execute($self->panel_id) if !$groupby;
	my %sorted;
	my $count = 0;
	while (my ($prop_id) = $sort_list->fetchrow_array()) {
	    $count++;

	    $sorted{$count} = $prop_id;
	}
	$sort_list->finish;
	
	$gslist{$group} = \%sorted;
    }
    return %gslist;
}

## Class Method ##
# Name: lock
# Purpose: sets and saves the locked status
# Parameters: value (optional)
# Returns: value
sub lock {
  my $self = shift;
  my $new_value = shift;

  return $self->locked if !$new_value;

  my $lock_update = $self->dbh->prepare(qq(update final_comments set 
                                           locked = ? where panel_id = ?));
  $lock_update->execute($new_value, $self->panel_id);
  $lock_update->finish;
  $self->locked($new_value);
}

## Class Method ##
# Name: update_stats
# Purpose: update all the bookkeeping parameters
# Parameters: none
# Returns: nothing
sub update_stats {
    my $self = shift;

    # joint count information
    $self->set_joint_cur();
    $self->set_joint_bal();

    #tc and too count information
    $self->set_tc_cur();
    $self->set_tc_bal();

    # time information
    $self->set_time_cur();
    $self->set_time_slew_cur();
    $self->set_time_slew_prob_cur();
    $self->set_time_slew_prob_bal();

    # HEL information
    $self->set_hel_cur();
    $self->set_hel_slew_cur();
    $self->set_hel_slew_prob_cur();
    $self->set_hel_slew_prob_bal();

    # lp time information
    $self->set_lp_time_cur();
    $self->set_lp_time_slew_cur();
    $self->set_lp_time_slew_prob_cur();
    $self->set_lp_time_slew_prob_bal();

    # vlp time information
    $self->set_vlp_time_cur();
    $self->set_vlp_time_slew_cur();
    $self->set_vlp_time_slew_prob_cur();
    $self->set_vlp_time_slew_prob_bal();

    # xlp time information
    $self->set_xvp_time_cur();
    $self->set_xvp_time_slew_cur();
    $self->set_xvp_time_slew_prob_cur();
    $self->set_xvp_time_slew_prob_bal();

    # archive and theory information
    $self->set_arc_cur();
    $self->set_the_cur();
    $self->set_arc_the_bal();

    # Change colors in monitor window
    set_color($self);

    #set approved counts
    $self->set_prop_y();
    $self->set_prop_n();
    $self->set_prop_g();
    $self->set_prop_p();
    $self->set_targ_y();
    $self->set_targ_n();
    $self->set_targ_g();
    $self->set_targ_p();
}

## Class Method ##
# Name: set_running_totals
# Purpose: set all the running totals in the lcd and fac views
# Parameters: none
# Returns: nothing
sub set_running_totals {
    my $self = shift;

    $self->set_running_total('lcd', 'prop_app_time', 'cum_lcd_app_time');
    $self->set_running_total('lcd', 'prop_app_time_1', 'cum_lcd_app_time_1');
    $self->set_running_total('lcd', 'prop_app_time_2', 'cum_lcd_app_time_2');

    $self->set_running_total('lcd', 'prop_app_hel', 'cum_lcd_app_hel');
    $self->set_running_total('lcd', 'prop_app_hel_1', 'cum_lcd_app_hel_1');
    $self->set_running_total('lcd', 'prop_app_hel_2', 'cum_lcd_app_hel_2');

    $self->set_running_money('lcd', 'Arc');
    $self->set_running_money('lcd', 'The');

    $self->set_running_total('lcd', 'rc_score_app', 'cum_lcd_rc_score');
    $self->set_running_total('lcd', 'rc_score_app_1', 'cum_lcd_rc_score_1');
    $self->set_running_total('lcd', 'rc_score_app_2', 'cum_lcd_rc_score_2');

    $self->set_running_total('lcd', 'vf_app', 'cum_lcd_vftoo');
    $self->set_running_total('lcd', 'f_app', 'cum_lcd_ftoo');
    $self->set_running_total('lcd', 's_app', 'cum_lcd_stoo');
    $self->set_running_total('lcd', 'hst_app', 'cum_lcd_hst');
    $self->set_running_total('lcd', 'noao_app', 'cum_lcd_noao');
    $self->set_running_total('lcd', 'nrao_app', 'cum_lcd_nrao');
    $self->set_running_total('lcd', 'xmm_app', 'cum_lcd_xmm');
    $self->set_running_total('lcd', 'rxte_app', 'cum_lcd_rxte');
    $self->set_running_total('lcd', 'spitzer_app', 'cum_lcd_spitzer');
    $self->set_running_total('lcd', 'suzaku_app', 'cum_lcd_suzaku');
    $self->set_running_total('lcd', 'swift_app', 'cum_lcd_swift');
    $self->set_running_total('lcd', 'nustar_app', 'cum_lcd_nustar');

    $self->set_running_total('lcd_lp', 'prop_app_time', 'cum_lcd_lp_app_time');
    $self->set_running_total('lcd_lp', 'prop_app_time_1', 'cum_lcd_lp_app_time_1');
    $self->set_running_total('lcd_lp', 'prop_app_time_2', 'cum_lcd_lp_app_time_2');

    $self->set_running_total('lcd_lp', 'prop_app_hel', 'cum_lcd_lp_app_hel');
    $self->set_running_total('lcd_lp', 'prop_app_hel_1', 'cum_lcd_lp_app_hel_1');
    $self->set_running_total('lcd_lp', 'prop_app_hel_2', 'cum_lcd_lp_app_hel_2');

    $self->set_running_money('lcd_lp', 'Arc');
    $self->set_running_money('lcd_lp', 'The');

    $self->set_running_total('lcd_lp', 'rc_score_app', 'cum_lcd_lp_rc_score');
    $self->set_running_total('lcd_lp', 'rc_score_app_1', 'cum_lcd_lp_rc_score_1');
    $self->set_running_total('lcd_lp', 'rc_score_app_2', 'cum_lcd_lp_rc_score_2');

    $self->set_running_total('lcd_lp', 'vf_app', 'cum_lcd_lp_vftoo');
    $self->set_running_total('lcd_lp', 'f_app', 'cum_lcd_lp_ftoo');
    $self->set_running_total('lcd_lp', 's_app', 'cum_lcd_lp_stoo');
    $self->set_running_total('lcd_lp', 'hst_app', 'cum_lcd_lp_hst');
    $self->set_running_total('lcd_lp', 'noao_app', 'cum_lcd_lp_noao');
    $self->set_running_total('lcd_lp', 'nrao_app', 'cum_lcd_lp_nrao');
    $self->set_running_total('lcd_lp', 'xmm_app', 'cum_lcd_lp_xmm');
    $self->set_running_total('lcd_lp', 'rxte_app', 'cum_lcd_lp_rxte');
    $self->set_running_total('lcd_lp', 'spitzer_app', 'cum_lcd_lp_spitzer');
    $self->set_running_total('lcd_lp', 'suzaku_app', 'cum_lcd_lp_suzaku');
    $self->set_running_total('lcd_lp', 'swift_app', 'cum_lcd_lp_swift');
    $self->set_running_total('lcd_lp', 'nustar_app', 'cum_lcd_lp_nustar');

    $self->set_running_total('lcd_vlp', 'prop_app_time', 'cum_lcd_vlp_app_time');
    $self->set_running_total('lcd_vlp', 'prop_app_time_1', 'cum_lcd_vlp_app_time_1');
    $self->set_running_total('lcd_vlp', 'prop_app_time_2', 'cum_lcd_vlp_app_time_2');

    $self->set_running_total('lcd_vlp', 'prop_app_hel', 'cum_lcd_vlp_app_hel');
    $self->set_running_total('lcd_vlp', 'prop_app_hel_1', 'cum_lcd_vlp_app_hel_1');
    $self->set_running_total('lcd_vlp', 'prop_app_hel_2', 'cum_lcd_vlp_app_hel_2');

    $self->set_running_money('lcd_vlp', 'Arc');
    $self->set_running_money('lcd_vlp', 'The');

    $self->set_running_total('lcd_vlp', 'rc_score_app', 'cum_lcd_vlp_rc_score');
    $self->set_running_total('lcd_vlp', 'rc_score_app_1', 'cum_lcd_vlp_rc_score_1');
    $self->set_running_total('lcd_vlp', 'rc_score_app_2', 'cum_lcd_vlp_rc_score_2');

    $self->set_running_total('lcd_vlp', 'vf_app', 'cum_lcd_vlp_vftoo');
    $self->set_running_total('lcd_vlp', 'f_app', 'cum_lcd_vlp_ftoo');
    $self->set_running_total('lcd_vlp', 's_app', 'cum_lcd_vlp_stoo');
    $self->set_running_total('lcd_vlp', 'hst_app', 'cum_lcd_vlp_hst');
    $self->set_running_total('lcd_vlp', 'noao_app', 'cum_lcd_vlp_noao');
    $self->set_running_total('lcd_vlp', 'nrao_app', 'cum_lcd_vlp_nrao');
    $self->set_running_total('lcd_vlp', 'xmm_app', 'cum_lcd_vlp_xmm');
    $self->set_running_total('lcd_vlp', 'rxte_app', 'cum_lcd_vlp_rxte');
    $self->set_running_total('lcd_vlp', 'spitzer_app', 'cum_lcd_vlp_spitzer');
    $self->set_running_total('lcd_vlp', 'suzaku_app', 'cum_lcd_vlp_suzaku');
    $self->set_running_total('lcd_vlp', 'swift_app', 'cum_lcd_vlp_swift');
    $self->set_running_total('lcd_vlp', 'nustar_app', 'cum_lcd_vlp_nustar');

    $self->set_running_total('fac', 'prop_app_time', 'cum_fac_app_time');
    $self->set_running_total('fac', 'prop_app_time_1', 'cum_fac_app_time_1');
    $self->set_running_total('fac', 'prop_app_time_2', 'cum_fac_app_time_2');

    $self->set_running_total('fac', 'prop_app_hel', 'cum_fac_app_hel');
    $self->set_running_total('fac', 'prop_app_hel_1', 'cum_fac_app_hel_1');
    $self->set_running_total('fac', 'prop_app_hel_2', 'cum_fac_app_hel_2');

    $self->set_running_money('fac', 'Arc');
    $self->set_running_money('fac', 'The');

    $self->set_running_total('fac', 'rc_score_app', 'cum_fac_rc_score');
    $self->set_running_total('fac', 'rc_score_app_1', 'cum_fac_rc_score_1');
    $self->set_running_total('fac', 'rc_score_app_2', 'cum_fac_rc_score_2');

    $self->set_running_total('fac', 'vf_app', 'cum_fac_vftoo');
    $self->set_running_total('fac', 'f_app', 'cum_fac_ftoo');
    $self->set_running_total('fac', 's_app', 'cum_fac_stoo');
    $self->set_running_total('fac', 'hst_app', 'cum_fac_hst');
    $self->set_running_total('fac', 'noao_app', 'cum_fac_noao');
    $self->set_running_total('fac', 'nrao_app', 'cum_fac_nrao');
    $self->set_running_total('fac', 'xmm_app', 'cum_fac_xmm');
    $self->set_running_total('fac', 'rxte_app', 'cum_fac_rxte');
    $self->set_running_total('fac', 'spitzer_app', 'cum_fac_spitzer');
    $self->set_running_total('fac', 'suzaku_app', 'cum_fac_suzaku');
    $self->set_running_total('fac', 'swift_app', 'cum_fac_swift');
    $self->set_running_total('fac', 'nustar_app', 'cum_fac_nustar');
}

## Class Method ##
# Name: move_rank
# Purpose: move the rank up or down in the list
# Parameters: prop_id
#             new rank
# Returns: nothing
sub move_rank {
  my $self = shift;
  my $prop_id = shift;
  my $new_rank = $self->proposals->proposal($prop_id)->rank;

  # $new_rank must be between 1 and highest rank
  # This also prevents deleting a rank
  $new_rank = 1 if $new_rank < 1;

  my $query = $self->dbh->prepare(qq(select max(rank) from proposal where 
                                    panel_id = ?));
  $query->execute($self->panel_id);
  my ($high_rank) = $query->fetchrow_array;
  $query->finish;
  $new_rank = $high_rank if $new_rank > $high_rank;

  # Get the old rank
  $query = $self->dbh->prepare(qq(select rank from proposal where 
                                  prop_id = ? and panel_id = ?));
  $query->execute($prop_id, $self->panel_id);
  my ($old_rank) = $query->fetchrow_array;
  $query->finish;

  # Get the range of proposals which need to move up or down in the list
  $query = $self->dbh->prepare(qq(select prop_id, rank from proposal where 
                                  panel_id = ? and rank between ? and ?));
  $query->execute($self->panel_id, $old_rank, $new_rank) if 
    $old_rank <= $new_rank;
  $query->execute($self->panel_id, $new_rank, $old_rank) if 
    $old_rank > $new_rank;

  # Update the ranks
  while (my($change_prop, $rank) = $query->fetchrow_array) {
    if ($change_prop == $prop_id) {
      $self->proposals->proposal($prop_id)->save_member('rank', $new_rank);
    }
    else {
      if ($old_rank > $new_rank) {
	$rank++;
      }
      else {
	$rank--;
      }
      $self->proposals->proposal($change_prop)->save_member('rank', $rank);
    }
  }
}

sub fmt_value
{
  my($self,$fld,$value) = @_;

  my($str) = $value;

  #print STDERR "panel:  $fld -- $value\n";

  if ($value eq $empty) {
     $str = "  " . $empty . "  ";
     #print STDERR "str=$str*****";
  } elsif ($value != undef&&  $value && $value ne $empty && $value ne 'N/A' && $fld_fmt{$fld} ) {
     $str = sprintf("$fld_fmt{$fld}",$value);
  }
  return $str;
}

  

1;

__END__

=head1 NAME

Panel - This object contains a list of proposal objects and fields used for
bookkeeping.

=head1 VERSION

$Revision: 1.63 $

=head1 SYNOPSIS

   use Panel;
   my $panel = new Panel($dbh, $panel_id, $if_flag, $verbosity);

=head1 DESCRIPTION

Panel - Provides a single place to get bookkeeping information for a panel.

=head1 PUBLIC METHODS

=head2 new($dbh, $panel_id, $if_flag, $verbosity)

Creates a new Panel object.

=over 

=item $dbh - database handle

=item $panel_id - panel number

=item $if_flag - flag indicating if finalize results should be applied

=item $verbosity - level of verbosity

=back

=head2 lock

Returns the lock status if no value given and sets the lock status if a value
is given

=head2 move_rank($prop_id, $new_rank)

Moves the rank up or down in the proposal list, changing all ranks in between 
the old and new rank.

=head2 set_groups

Populates the groups hash with distinct groups in group_id column

=head2 set_triage($hide_triage)

Sets the 'hide triage' flag and recalculates the running totals

=over 

=item $hide_triage - 'hide triage' flag value

=back

=head2 set_arc_the($hide_arc_the)

Sets the 'hide archive/theory' flag and recalculates the running totals

=over 

=item $hide_arc_the - 'hide archive/theory' flag value

=back

=head2 set_lp_vlp($hide_lp_vlp)

Sets the 'hide LP/VLP' flag and recalculates the running totals

=over 

=item $hide_lp_vlp - 'hide LP/VLP' flag value

=back

=head2 lower_triage($amt)

Lower the average grade of all triaged proposals by a stated amount and 
return the new highest triage grade

=head2 set_lcd_sort($groupby, $sortby, $sortname)

Set the lcd_groupby, lcd_sortby and lcd_sortname fields

=over

=item $groupby - group by string

=item $sortby - sort by string

=item $sortname - name of sort applied

=back


=head2 set_fac_sort($groupby, $sortby, $sortname)

Set the fac_groupby, fac_sortby and fac_sortname fields

=over

=item $groupby - group by string

=item $sortby - sort by string

=item $sortname - name of sort applied

=back

=head2 set_grades

Calculates the normalized grade for each proposal and then resets the new 
high, low and passing average grades for the panel.

=head2 set_lp_grades

Calculates the new high, low and passing average grades for the LP in the panel.

=head2 set_xvp_grades

Calculates the new high, low and passing average grades for the XVP in the panel.

=head2 get_group_list($groupby, $sortby, $hide_triage, $hide_arc_the, $hide_lp_vlp)

arrange proposals into groups according to group and sort specified

=over 

=item $groupby - group by string

=item $sortby - sort by string

=item $hide_triage - flag indicating whether to include triaged proposals

=item $hide_arc_the - flag indicating whether to include archive/theory proposals

=item $hide_lp_vlp - flag indicating whether to include LP/VLP proposals

=back

=head2 update_stats

Updates all the bookkeeping parameters

=head2 set_running_totals

Sets the values in the cummulative total columns

=head1 PRIVATE METHODS

=head2 _init

Initializes a new panel object.

=head2 set_prop_counts

Sets the various proposal counts for status tracking

=head2 set_prop_y

Sets the number of approved proposals

=head2 set_prop_n

Sets the number of denied proposals

=head2 set_prop_g

Sets the number of gray area proposals

=head2 set_prop_p

Sets the number of proposals which go to the large panel discussion

=head2 set_targ_counts

Sets all the target counts for status tracking

=head2 set_targ_y

Sets the number of approved targets in the panel

=head2 set_targ_n

Sets the number of denied targets in the panel

=head2 set_targ_g

Sets the number of gray area targets in the panel

=head2 set_targ_p

Sets the number of targets which go to the large panel discussion

=head2 set_joint_req

Sets the number of requested joint proposals for each the joint observatory
program

=head2 set_joint_allot

Sets the number of allotted joint proposals for each the joint observatory
program

=head2 set_joint_cur

Sets the number of approved joint proposals for each the joint observatory
program

=head2 set_joint_bal

Sets the number of available joint proposals for each the joint observatory
program

=head2 set_tc_req

Sets the number of easy, average, and difficult time constraint counts and 
very fast, fast, slow and very slow TOOs requested

=head2 set_tc_allot

Sets the number of easy, average, and difficult time constraint counts and 
very fast, fast, slow and very slow TOOs allotted

=head2 set_tc_cur

Sets the number of easy, average, and difficult time constraint counts and 
very fast, fast, slow and very slow TOO's that have been
approved

=head2 set_tc_bal

Sets the number of easy, average, and difficult time constraint counts and 
very fast, fast, slow and very slow TOO's available

=head2 set_time_req

Sets the total time requested in the panel

=head2 set_time_cur

Sets the total time approved

=head2 set_time_slew_prob_req

Sets the total time plus slew with probability requested in the panel

=head2 set_hel_slew_prob_req

Sets the total HEL time plus slew with probability requested in the panel

=head2 set_time_slew_prob_allot

Sets the total time plus slew with probability allotted to the panel

=head2 set_hel_slew_prob_allot

Sets the total HEL time plus slew with probability allotted to the panel

=head2 set_time_slew_prob_cur

Sets the total time plus slew with probability approved

=head2 set_hel_slew_prob_cur

Sets the total HEL time plus slew with probability approved

=head2 set_time_slew_prob_bal

Sets the total time plus slew with probability remaining

=head2 set_hel_slew_prob_bal

Sets the total HEL time plus slew with probability remaining

=head2 set_lp_time_req

Sets the total time requested for LPs

=head2 set_lp_time_cur

Sets the total time approved for LPs

=head2 set_lp_time_slew_req

Sets the total time plus slew requested for LPs

=head2 set_lp_time_slew_cur

Sets the total time plus slew approved for LPs

=head2 set_lp_time_slew_prob_req

Sets the total time plus slew with probability requested for LPs

=head2 set_lp_time_slew_prob_allot

Sets the total time plus slew with probability allotted for LPs

=head2 set_lp_hel_slew_prob_allot

Sets the total HEL time plus slew with probability allotted for LPs

=head2 set_lp_time_slew_prob_cur

Sets the total time plus slew with probability approved for LPs

=head2 set_lp_time_slew_prob_bal

Sets the total time plus slew with probability remaining for LPs

=head2 set_vlp_time_req

Sets the total time requested for VLPs

=head2 set_vlp_time_cur

Sets the total time approved for VLPs

=head2 set_vlp_time_slew_req

Sets the total time plus slew requested for VLPs

=head2 set_vlp_time_slew_cur

Sets the total time plus slew approved for VLPs

=head2 set_vlp_time_slew_prob_req

Sets the total time plus slew with probability requested for VLPs

=head2 set_vlp_hel_slew_prob_allot

Sets the total time plus slew with probability allotted for VLPs

=head2 set_vlp_hel_slew_prob_allot

Sets the total time plus slew with probability allotted for VLPs

=head2 set_vlp_time_slew_prob_cur

Sets the total time plus slew with probability approved for VLPs

=head2 set_vlp_time_slew_prob_bal

Sets the total time plus slew with probability remaining for VLPs

=head2 set_xvp_time_req

Sets the total time requested for XVPs

=head2 set_xvp_time_cur

Sets the total time approved for XVPs

=head2 set_xvp_time_slew_req

Sets the total time plus slew requested for XVPs

=head2 set_xvp_time_slew_cur

Sets the total time plus slew approved for XVPs

=head2 set_xvp_time_slew_prob_req

Sets the total time plus slew with probability requested for XVPs

=head2 set_xvp_time_slew_prob_allot

Sets the total time plus slew with probability allotted for XVPs

=head2 set_xvp_time_hel_prob_allot

Sets the total HEL time plus slew with probability allotted for XVPs

=head2 set_xvp_time_slew_prob_cur

Sets the total time plus slew with probability approved for XVPs

=head2 set_xvp_time_slew_prob_bal

Sets the total time plus slew with probability remaining for XVPs

=head2 set_arc_req

Sets the total dollar amount requested for archive proposals

=head2 set_the_req

Sets the total dollar amount requested for theory proposals

=head2 set_arc_cur

Sets the total dollar amount approved for archive proposals

=head2 set_the_cur

Sets the total dollar amount approved for theory proposals

=head2 set_arc_the_allot

Sets the total dollar amount approved for archive and theory proposals

=head2 set_arc_the_bal

Sets the total dollar amount remaining for archive and theory proposals

=head2 set_running_money($view, $type)

Sets the running dollar amount approved for each archive or theory
proposal based on the sort applied

=over 

=item $view

lcd or fac

=item $type

(arc)hive or (the)ory

=back

=head2 set_running_total($view, $tot_app_col, $running_col)

Sets the running total of the desired column using the values from the column

=over 

=item $view

lcd or fac

=item $tot_app_col

Column to sum over

=item $running_col

Column to store running totals in

=back



=head1 DEPENDENCIES

None.

=head1 BUGS AND LIMITATIONS

Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head2 Request Time w/Slew + prob

Before any targets have been turned-off, the requested and current cells 
should be equal.  They are not because the requested time is over counting 
TOO follow-ups in the total.

=head2 TOO counting problems

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
