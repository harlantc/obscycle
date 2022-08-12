# Makefile for the Obsed web application (servlet support).

MK_TOP = ../../../../../../..

# Include the highest level macro definitions, mainly pointers.
include $(MK_TOP)/infra/Makefile.master
include ../../Makefile.master.overrides

# Setup the class path.
CLASSPATH = $(SERVLETLIB):$(MK_TOP)/dsguilibs/java/

DOC_PACKAGES = $(JAVA_FILES) 
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_TITLE = "ObscatEditor Server Mapper"

# Setup the files for the servlet
JAVA_FILES = DBServerMapper.java
APP_NAME = obsed-app
APP_WAR_FILE = $(APP_NAME).war
APP_SERVLET_FILES = $(JAVA_FILES:%.java=%.class)
APP_CONFIG_FILES = web.xml
APP_DIR = $(WEB_APPS_DIR)/
APP_STAGING_DIR = $(APP_NAME)
APP_CLASSES_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/classes
APP_CONFIG_DIR = $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)
WEB_APP_INF_DIR = WEB-INF

# Satisfy the contract with the ASCDS build system.
MAKEALL = $(APP_WAR_FILE) javadoc
MAKECLEAN = $(APP_SERVLET_FILES) $(APP_STAGING_DIR) doc


INSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(WWW_APPS_DIR)/%)
INSTALL_FILES = $(INSTALL_WAR_FILE)

# Prepare for Web installation
#WEBINSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(APP_DIR)/%)
#WEBINSTALL_PROPERTY_FILES = $(APP_PROPERTY_FILES:%=$(APP_DIR)/%)
#WEBINSTALL_FILES = $(WEBINSTALL_WAR_FILE) $(WEBINSTALL_PROPERTY_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

$(APP_WAR_FILE): $(APP_SERVLET_FILES) $(APP_CONFIG_FILES)
	$(RMDIR) $(APP_STAGING_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR) && \
	$(MKDIR) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_SERVLET_FILES) $(APP_CLASSES_DIR) && \
	$(CP) $(APP_CONFIG_FILES) $(APP_CONFIG_DIR) && \
	cd $(APP_STAGING_DIR) && $(JAR) cf ../$@ *

