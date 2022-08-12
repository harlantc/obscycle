#
#
MK_TOP  = ../../../../
include $(MK_TOP)/infra/Makefile.cxc

MODULES = SQL
include $(MK_TOP)/infra/Makefile.module       # Makefile for whole directory tree

DATA_DIR = $(PR_TOP)/PeerReviewCDO/

DATA_FILES = \
	README \
	BPP_checklist.odt \
	BPP_checklist.pdf \
	dofinal_lists \
	dofinal_stats \
	PanelStats.pl \
	appr_allot_tallies.pl \
	comments.pl \
	conflicts.pl \
	final_checks.pl \
	find_grade_status.pl \
	find_finalized.pl \
	find_ecliptic.pl \
	get_grades_for_proposal.pl \
	gui2tab.pl \
	joint.pl \
	megalist.pl \
	msfcReport.pl \
	peer_conflicts.pl \
	ranked_BPP.pl \
	tc_summaries.pl \
	tc_tallies.pl 

include $(MK_TOP)/infra/Makefile.all
COPY_P 	= cp -f

# DO NOT DELETE THIS LINE -- make depend depends on it.
