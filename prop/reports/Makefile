# makefile for ascds proposal background programs 
#
#

MK_TOP = ../../..
include $(MK_TOP)/infra/Makefile.obscycle

MODULES = pdfGen
include $(MK_TOP)/infra/Makefile.module


EXECUTABLES 	= .pingest \
		prop_reports \
		prop_read_reviews \
		prop_archive_retrieve \
		prop_target_reports 

SCRIPTS 	= prop_genPDF.tcsh pdfGen_wrapper \
		prop_resources.tcsh prop_ddt.tcsh 

PERLSCRIPTS 	= \
                prop_resources.pl \
                prop_receiver_stats.pl \
                prop_history.pl \
	        prop_ddt_confirmation.pl\
	        prop_allow_edit.pl\
	        prop_withdraw_proposal.pl\
	        pi_coi_list.pl\
		prop_build_joint.pl\
		peer_bpp_chair_conflict.pl\
		peer_reassignments.pl \
		modify_institute.pl \
		pi_coi.pl \
		prop_abstract_search.pl \
		prop_acis_lists.pl \
		prop_coordcheck.pl \
		prop_country_stats.pl \
		prop_dbroutines.pl \
		prop_dbroutines_multi.pl \
		prop_routines.pl \
		propconflict.pl \
		prop_panel_stats.pl \
		parse_propconflict.pl parse_simbad.pl \
		parse_ocatconflict.pl parse_xmmconflict.pl \
		parse_suzakuconflict.pl \
		parse_rass.pl \
		parse_single_conflict_files.pl \
 		peer_constraints_ingest.pl \
		peer_lists.pl \
		peer_joint_lists.pl \
		peer_panel_member_lists.pl \
		peer_proposal_lists.pl \
		peer_reviewer_conflicts.pl \
		peer_reviewer_lists.pl \
		post_peer_lists.pl \
		proposal_lists.pl  \
		prop_all_multi.pl  \
		prop_all_stats.pl  \
		prop_approved_lists.pl \
		prop_approved_tc.pl \
		prop_conflict_lists.pl \
		prop_tc.pl \
		prop_approved_observers.pl \
		prop_build_upload_list.pl \
		prop_detector_for_proposals.pl \
		prop_final_checks.pl  \
		prop_final_reviews.pl  \
		prop_gto_stats.pl \
		prop_ingest_checks.pl \
		prop_joint_stats.pl  \
		prop_linked_obsids.pl \
		prop_lp_vlp_stats.pl \
 		prop_modified_institutions.pl \
 		prop_multicycle_checks.pl \
 		prop_notify_cois.pl \
		prop_obscat_for_xmm.pl \
		prop_obscat_for_usint.pl \
		prop_panel_checks.pl  \
		prop_panel_for_proposal.pl \
		prop_pre_conflicts.pl \
		prop_prelim_grades.pl \
		prop_proposal_lists.pl \
		prop_rps_checks.pl  \
		prop_science_category_abstract.pl \
		prop_too_lists.pl \
		prop_view_linked.pl \
		prop_view_tgtkeyword.pl  \
		prop_winner_stats.pl \
		prop_possible_reviewers.pl 


PAR_FILES 	= 


LOCAL_INCLUDES = \
	$(DBINC) \
	$(PROPINCLUDES) \
	$(STLDEFS)

LOCAL_LIBS = \
        $(LIBFRAME) \
	$(DBLIBS) \
        $(PROPLIB) \
        $(PROPPARAMLIB) \
        $(OBSLIBS)

INSTALL_BIN_FILES = $(EXECUTABLES:%=$(BIN_DIR)/%) $(SCRIPTS:%=$(BIN_DIR)/%) \
		    $(PERLSCRIPTS:%=$(BIN_DIR)/%)
INSTALL_FILES= $(INSTALL_BIN_FILES) 

MAKEALL = $(EXECUTABLES) $(PERLSCRIPTS)
MAKECLEAN = $(MAKEALL) *.o 


include $(MK_TOP)/infra/Makefile.all

REV_OBJS	= \
	PropReadRevApp.o \
	PropReadRevParameter.o

REP_OBJS	= \
	PropRepApp.o \
	PropRepParameter.o

TGTREP_OBJS	= \
	PropTgtRepApp.o \
	PropTgtRepParameter.o

P_OBJS = prop_getpwd.o


REP_SRCS	= $(REP_OBJS:.o=.cc)
REV_SRCS	= $(REV_OBJS:.o=.cc)
TGTREP_SRCS	= $(TGTREP_OBJS:.o=.cc)
P_SRCS  	= $(P_OBJS:.o=.cc)
SRCS		= $(REP_SRCS) $(TGT_SRCS) \
		 $(REV_SRCS)  $(P_SRCS)


prop_archive_retrieve:	prop_archive_retrieve.o
	$(LINKCPP) -o $@ prop_archive_retrieve.o -L$(ASCDS_BLD_LIB) $(LIBGEN) \
	$(DBLIBS) $(PROPLIB) $(OBSLIBS) $(COMMON)


prop_read_reviews:	$(REV_OBJS)
	$(LINKCPP) -o $@ $(REV_OBJS) $(LIBS_CMDLINE)


prop_reports:	$(REP_OBJS)
	$(LINKCPP) -o $@ $(REP_OBJS) $(LIBS_CMDLINE)

prop_target_reports:	$(TGTREP_OBJS)
	$(LINKCPP) -o $@ $(TGTREP_OBJS) $(LIBS_CMDLINE)

.pingest:       $(P_OBJS)
	$(LINKCPP) -o $@ $(P_OBJS)  $(OBSLIB)


depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.

