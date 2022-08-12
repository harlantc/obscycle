# Makefile for the Trigger TOO package.

# Don't change this
MK_TOP = ../../../../../../..

# Set ASCDS_BLD_ env. variables
include $(MK_TOP)/infra/Makefile.master
include ../../Makefile.master.overrides


# Setup the class path.
ROOT_CLASSDIR = .:../lib

WEBINC_DIR = $(MK_TOP)/obscycle/ui/cxc_include

LOCAL_JAVA_LIBS = \
	$(ASCDSAPILIB) \
	$(COMMONSLANG3LIB) \
	$(MAILLIB) \
        $(CAPTCHALIBS) \
        ../../captcha/captcha.jar \
	../lib/too_api.jar 

CLASSPATH = $(ROOTLIBS):$(SERVLETLIB):$(JCONNECTLIB):$(ASCDSAPILIB):$(MAILLIB):$(COMMONSLANG3LIB):../../captcha/captcha.jar

JAVA_FILES = \
	TOOSearchServlet.java \
	TOOTriggerServlet.java 

APP_HTML_FILES = \
	index.html \
	displayTOOSearch.jsp \
	displayTOOList.jsp \
	displayTrigger.jsp \
	displayFile.jsp \
	displaySuccessfulSubmit.jsp \
	triggertooHead.html \
	triggertooHelp.jsp \
	triggertoo.css \
	triggertoo.js \
        ../triggertoo.properties \
        $(WEBINC_DIR)/cxcfooterj.html \
        $(WEBINC_DIR)/cxcds_meta.html \
        $(WEBINC_DIR)/cxcheader.html 

# Internal documentation support.
DOC_PACKAGES = $(JAVA_FILES) db info
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_TITLE = "Trigger TOO Implementation Documentation"

APP_NAME = triggertoo-app
APP_WAR_FILE = $(APP_NAME).war
APP_SERVLET_FILES = $(JAVA_FILES:%.java=%.class) 
APP_CONFIG_FILES = web.xml 
APP_DIR = $(WEB_APPS_DIR)/
APP_STAGING_DIR = $(APP_NAME)
APP_CLASSES_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/classes
APP_CONFIG_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)
WEB_APP_INF_DIR = WEB-INF


MAKEALL = announce $(APP_WAR_FILE) javadoc
MAKECLEAN = $(APP_SERVLET_FILES) $(APP_STAGING_DIR) $(DOC_DIR)

INSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(WWW_APPS_DIR)/%)
INSTALL_FILES = $(INSTALL_WAR_FILE)

# Prepare for Web installation
WEBINSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(APP_DIR)/%)
WEBINSTALL_FILES = $(WEBINSTALL_WAR_FILE)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

$(APP_WAR_FILE): $(APP_SERVLET_FILES) $(APP_CONFIG_FILES) $(APP_HTML_FILES)
	$(RMDIR) $(APP_STAGING_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR) && \
	$(MKDIR) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_SERVLET_FILES) $(APP_CLASSES_DIR) && \
	$(CP) $(APP_CONFIG_FILES) $(APP_CONFIG_DIR) && \
	$(CP) $(LOCAL_JAVA_LIBS) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_HTML_FILES) $(APP_STAGING_DIR) && \
        $(CP) $(MK_TOP)/infra/VERSION $(APP_STAGING_DIR) && \
	cd $(APP_STAGING_DIR) && $(JAR) cf ../$@ *

announce:
	@echo "   /-----------------------------------------------------\\"
	@echo "   |              Building Trigger TOO Application       |"
	@echo "   \-----------------------------------------------------/"












