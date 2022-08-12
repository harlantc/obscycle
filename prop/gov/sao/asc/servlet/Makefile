# Makefile for the ASC Logger application (servlet support).
MK_TOP = ../../../../../..

# Include the highest level macro definitions, mainly pointers.
include $(MK_TOP)/infra/Makefile.master
include ../Makefile.master.overrides

# Setup the class path.
CLASSPATH = $(SERVLETLIB)


JAVA_FILES = LogServer.java


# Setup the files for the servlet engine 
APP_NAME = logger-app
APP_WAR_FILE = $(APP_NAME).war
APP_SERVLET_FILES = $(JAVA_FILES:%.java=%.class)
APP_CONFIG_FILES = web.xml
APP_STAGING_DIR = $(APP_NAME)
WEB_APP_INF_DIR = WEB-INF
APP_CLASSES_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/classes
APP_CONFIG_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)

#JAVA documentation
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_PACKAGES = $(JAVA_FILES)
DOC_TITLE = "Logger Implementation Documentation"


# Satisfy the contract with the ASCDS build system.
MAKEALL = $(APP_WAR_FILE) javadoc
MAKECLEAN = $(APP_SERVLET_FILES) $(APP_STAGING_DIR) $(APP_WAR_FILE) doc

INSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(WWW_APPS_DIR)/%)
INSTALL_FILES = $(INSTALL_WAR_FILE)

# Prepare for Web installation
#WEBINSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(WEB_APPS_DIR)/%)
#WEBINSTALL_FILES = $(WEBINSTALL_WAR_FILE) 


include $(MK_TOP)/obscycle/prop/Makefile.obswww

$(APP_WAR_FILE): $(APP_SERVLET_FILES) $(APP_CONFIG_FILES)
	$(RMDIR) $(APP_STAGING_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR) && \
	$(CP) $(APP_SERVLET_FILES) $(APP_CLASSES_DIR) && \
	$(CP) $(APP_CONFIG_FILES) $(APP_CONFIG_DIR) && \
	cd $(APP_STAGING_DIR) && $(JAR) cf ../$@ *

