# Makefile for the Reports package.

# Don't change this
MK_TOP = ../../../../../..

# Set ASCDS_BLD_ env. variables
include $(MK_TOP)/infra/Makefile.master
include ../Makefile.master.overrides

WEBINC_DIR = $(MK_TOP)/obscycle/ui/cxc_include


# Setup the files for the webreports
JAVA_LIB_JAR = \
	$(ASCDSAPILIB) \
	$(MAILLIB) \
	$(COMMONSLANG3LIB)


JAVA_LIB_FILES = \
	db/DBConnection.java \
	info/ReassignRequest.java \
	info/FormatUtils.java \
	info/Institution.java \
	info/GradeConflict.java \
	info/GradeConflictList.java \
	info/Lock.java \
	info/Notes.java \
	info/CmtEdits.java \
	info/ModifiedInstitutions.java \
	info/User.java \
	info/Proposal.java \
	info/ProposalComparer.java \
	info/ProposalFile.java \
	info/ProposalFileList.java \
	info/ReviewReport.java \
	info/ReportsConstants.java \
	info/Reports.java \
	info/PrelimGradesList.java \
	info/ProposalConflictsList.java  \
	info/ProposalReviewerList.java  \
	info/ProposalGroupsList.java 


JAVA_FILES = \
	ListServlet.java \
	AssignGradesServlet.java \
	AssignGroupsServlet.java \
	ConflictsServlet.java \
	GetReportServlet.java \
	LoginServlet.java \
	LogoutServlet.java \
	MemberListServlet.java \
	PreConflictServlet.java \
	BPPConflictServlet.java \
	PrintReportsServlet.java \
	ProposalListServlet.java \
	ReassignReportServlet.java \
	ReviewReportsServlet.java \
	TechListServlet.java \
	UpdateReportServlet.java \
	ViewGradesSummary.java \
	ViewPanelServlet.java \
	ViewLPServlet.java

# Internal documentation support.
DOC_PACKAGES = db info $(JAVA_FILES)
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_TITLE = "WebReports Implementation Documentation"

APP_NAME = reports-app
APP_WAR_FILE = $(APP_NAME).war
APP_SERVLET_FILES = $(JAVA_FILES:%.java=%.class) 
APP_SERVLET_LIB_FILES = $(JAVA_LIB_FILES:%.java=%.class) 
APP_CONFIG_FILES = web.xml 
APP_DIR = $(WEB_APPS_DIR)/
APP_STAGING_DIR = $(APP_NAME)
APP_CLASSES_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/classes
APP_CONFIG_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)
WEB_APP_INF_DIR = WEB-INF


# all the other files needed in the war file  (no compile needed)
# jquery is used for the table sorting, tracking resizeable text boxes
APP_JSP_FILES = reports.properties \
		reports.css \
		reports.js \
		reportsPage.js \
		timeout.js \
		reportsHead.html \
		index.html \
		footer.html \
		gold.gif \
		arrow-left.png \
		$(JQUERY_FILES) \
		$(WEBINC_DIR)/cxcds.css \
		$(WEBINC_DIR)/cxcfooterj.html \
		$(WEBINC_DIR)/cxcheader.html \
		$(WEBINC_DIR)/cxcds_meta.html \
		$(WEBINC_DIR)/blank.gif \
		$(WEBINC_DIR)/cxcheaderlogo.png \
		checkPassword.jsp \
		displayList.jsp \
		displayCSV.jsp \
		displayConflicts.jsp \
		displayLoginPage.jsp \
		displayFacilitatorView.jsp \
		displayReportsList.jsp \
		displayReassign.jsp \
		displayLPReports.jsp \
		displayReport.jsp \
		displayPanel.jsp \
		displayTechList.jsp \
		displayGroups.jsp \
		displayGroupsPrint.jsp \
		displayAdminPage.jsp \
		displayMemberList.jsp \
		displayPreConflict.jsp \
		displayBPPConflict.jsp \
		displayPrelimGrades.jsp \
		displayPrelimPrint.jsp \
		displayPunditLink.jsp \
		displayFile.jsp \
		displayPrintVersion.jsp \
		modeButtons.jsp \
		header.jsp \
		panelUnavailable.jsp \
		generalHelp.jsp \
		topMenuViewHelp.jsp \
		reportViewHelp.jsp \
		reportFieldHelp.html \
		reportsListHelp.jsp \
		conflictsViewHelp.jsp \
		gradesViewHelp.jsp \
		groupsViewHelp.jsp \
		panelViewHelp.jsp \
		propListHelp.jsp \
		lpViewHelp.jsp \
		developerLogin.html \
		displayTimedOut.jsp \
		displayWarning.jsp


MAKEALL = $(APP_WAR_FILE) javadoc
MAKECLEAN = $(APP_SERVLET_LIB_FILES) $(APP_SERVLET_FILES) $(APP_STAGING_DIR)


INSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(WWW_APPS_DIR)/%)
INSTALL_FILES = $(INSTALL_WAR_FILE) 


include $(MK_TOP)/obscycle/prop/Makefile.obswww


$(APP_WAR_FILE): $(APP_SERVLET_LIB_FILES) $(APP_SERVLET_FILES) $(APP_CONFIG_FILES)  
	$(RMDIR) $(APP_STAGING_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR)/info && \
	$(MKDIR) $(APP_CLASSES_DIR)/db && \
        $(MKDIR) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_SERVLET_FILES) $(APP_CLASSES_DIR) && \
	$(CP) $(APP_CONFIG_FILES) $(APP_CONFIG_DIR) && \
	$(CP) info/*.class $(APP_CLASSES_DIR)/info && \
	$(CP) db/*.class $(APP_CLASSES_DIR)/db && \
	$(CP) $(JAVA_LIB_JAR) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_JSP_FILES) $(APP_STAGING_DIR) && \
        $(CP) $(MK_TOP)/infra/VERSION $(APP_STAGING_DIR) && \
	cd $(APP_STAGING_DIR) && $(JAR) cf ../$@ *


