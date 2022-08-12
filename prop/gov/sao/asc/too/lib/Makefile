# Makefile for ascds library
#

MK_TOP = ../../../../../../..

include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/obscycle/prop/gov/sao/asc/Makefile.master.overrides



JAVA_FILES  = db/Database.java \
	db/DBDDT.java \
	db/DBObservation.java \
	db/DBTriggerToo.java \
	info/AOCycle.java \
	info/AcisWin.java \
	info/AcisParam.java \
	info/RollReq.java \
	info/TimeReq.java \
	info/PropTarget.java \
	info/TOOInfo.java \
	info/DDTInfo.java \
        info/DDTFileFilter.java \
	info/DDTFollowup.java \
	info/PropTargetList.java \
	info/DDTEntry.java \
	info/MailUtility.java \
        info/FormatUtils.java \
	info/Parameter.java \
        info/CommentHistory.java \
        info/Observation.java \
        info/ObservationList.java \
        info/TriggerToo.java \
        info/TriggerTooConstants.java \
        info/TriggerTooEntry.java

APP_NAME = too_api
TOOAPI_JAR = $(APP_NAME).jar


APP_FILES = $(JAVA_FILES:%.java=%.class) 
APP_JAR_FILES =	$(TOOAPI_JAR)

#INSTALL_JAR_FILES = $(APP_JAR_FILES:%=$(WWW_APPS_DIR)/%)
#INSTALL_FILES = $(INSTALL_JAR_FILES)


MAKEALL		= announce $(TOOAPI_JAR) 
MAKECLEAN	= $(APP_FILES) $(TOOAPI_JAR) $(INSTALL_JAR_FILES) 

include $(MK_TOP)/obscycle/prop/Makefile.obswww


announce:
	@echo "   /-----------------------------------------------------\\"
	@echo "   |              Building too library              |"
	@echo "   \-----------------------------------------------------/"


$(TOOAPI_JAR): $(APP_FILES)
	$(JAR) cf $@ $(APP_FILES) 

