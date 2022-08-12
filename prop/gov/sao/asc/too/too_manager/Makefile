# Makefile for the TOO Manager package.

# Don't change this
MK_TOP = ../../../../../../..

# Set ASCDS_BLD_ env. variables
include $(MK_TOP)/infra/Makefile.master
include ../../Makefile.master.overrides

# Setup the class path.
ROOT_CLASSDIR = ../lib


LOCAL_JAVA_LIBS = \
	$(ASCDSAPILIB) \
        $(MAILLIB) \
	$(COMMONSLANG3LIB) \
	../lib/too_api.jar

JAVA_FILES = \
	DDTConflictServlet.java \
	DDTManagerServlet.java \
	DDTMigrateOcatServlet.java \
	DDTOcatServlet.java \
	DDTUpdateServlet.java \
	TOOConflictServlet.java \
	TOOLoginServlet.java \
	TOOLogoutServlet.java \
	TOOManagerServlet.java \
	TOOObscatUpdate.java  \
	TOOUpdateServlet.java

APP_HTML_FILES = \
        index.html \
	displayOcat.jsp \
	displayDDTProposal.jsp \
	displayDDTUpdate.jsp \
	displayDDTManager.jsp \
	displayDDTMigrate.jsp \
	displayTOOManager.jsp \
	displayTOOUpdate.jsp \
	displayObsCatUpdates.jsp \
	tooManagerHead.jsp \
	tooManagerLinks.jsp \
	ddtManagerHead.jsp \
	ddtManagerLinks.jsp \
        displayFile.jsp \
        displaySentMessage.jsp \
        displaySentDDT.jsp \
        login.jsp \
	toomanager.css \
	toomanager.js \
	$(JQUERY_FILES) \
        $(DHTMLX_FILES) \
	../graphics/header_left.gif \
	../graphics/small_tri_right.gif \
	../graphics/small_tri_down.gif \
	../graphics/blank.gif \
	../triggertoo.properties

# Internal documentation support.
DOC_PACKAGES = $(JAVA_FILES)  db info
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_TITLE = "TOO Manager Implementation Documentation"

APP_NAME = toomanager-app
APP_WAR_FILE = $(APP_NAME).war
APP_SERVLET_FILES = $(JAVA_FILES:%.java=%.class) 
APP_CONFIG_FILES = web.xml 
APP_DIR = $(WEB_APPS_DIR)/
APP_STAGING_DIR = $(APP_NAME)
APP_CLASSES_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/classes
APP_CONFIG_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)
WEB_APP_INF_DIR = WEB-INF


MAKEALL = $(APP_WAR_FILE) javadoc
MAKECLEAN = $(APP_SERVLET_FILES) $(APP_STAGING_DIR) $(DOC_DIR)

INSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(WWW_APPS_DIR)/%)
INSTALL_FILES = $(INSTALL_WAR_FILE)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

$(APP_WAR_FILE): $(APP_SERVLET_FILES) $(APP_CONFIG_FILES) $(APP_HTML_FILES)
	$(RMDIR) $(APP_STAGING_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR) && \
	$(MKDIR) $(APP_CONFIG_DIR)/lib && \
	$(CP) -r $(DHTMLX_SKYBLUE) $(APP_STAGING_DIR) && \
	$(CP) $(APP_SERVLET_FILES) $(APP_CLASSES_DIR) && \
	$(CP) $(APP_CONFIG_FILES) $(APP_CONFIG_DIR) && \
	$(CP) $(LOCAL_JAVA_LIBS) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_HTML_FILES) $(APP_STAGING_DIR) && \
	cd $(APP_STAGING_DIR) && $(JAR) cf ../$@ *


