#
#
MK_TOP  = ../../../../
include $(MK_TOP)/infra/Makefile.cxc

MODULES = data

include $(MK_TOP)/infra/Makefile.module       # Makefile for whole directory tree

DATA_DIR = $(PR_TOP)/PeerReviewDB/

DATA_FILES = \
	backup_PR_tables \
	backup_PR_tables_for_mods \
	calc_allot.pl \
	calc_ecliptic.pl \
	calc_num_obs.pl \
	calc_tc.pl \
	calc_tc_roll.pl \
	clean_tables.sql \
	consistency_queries.sql \
	create_permissions.pl \
	create_peer_results_sql.pl \
	ddl_postgresql_peer_review \
	dump_gui_tables.pl \
	gen_grades.pl \
	get_BPP_prelim.pl \
	get_GTO_prelim.pl \
	load_PR_tables \
	load_roll_coor.pl \
	manual_triage.pl \
	move_proposal.pl \
	observatory.sql \
	panel_slew.pl \
	pop_reviewers.pl \
	pop_conflicts.pl \
	pop_groups.pl \
	pop_gui_tables.pl \
	pop_prelim.pl \
	pop_panel.pl \
	pop_pr.pl \
	set_gto_groupname.pl \
	set_mcop_monitor.pl

include $(MK_TOP)/infra/Makefile.all
COPY_P 	= cp -f

# DO NOT DELETE THIS LINE -- make depend depends on it.
