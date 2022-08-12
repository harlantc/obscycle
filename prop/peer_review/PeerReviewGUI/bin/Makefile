#
#
MK_TOP  = ../../../../../
include $(MK_TOP)/infra/Makefile.cxc

COPY_P 	= cp -f
REMOVE_F = \rm -f

DATA_DIR = $(PR_TOP)/PeerReviewGUI/bin

CDO_DIR = ../../PeerReviewCDO
CDO_FILES = \
	get_grades_for_proposal.pl \
	conflicts.pl

DATA_FILES = \
	PR


INSTALL_DATA_FILES = $(DATA_FILES:%=$(DATA_DIR)/%) \
	$(CDO_FILES:%=$(DATA_DIR)/%) 


MAKEALL = $(CDO_FILES) $(DATA_FILES)

include $(MK_TOP)/infra/Makefile.all


$(CDO_FILES): $(CDO_FILES:%=$(CDO_DIR)/%)
	$(REMOVE_F) $@
	$(COPY_P) $(CDO_DIR)/$@ $(@D)
	echo "chmod a+x $@"



# DO NOT DELETE THIS LINE -- make depend depends on it.
