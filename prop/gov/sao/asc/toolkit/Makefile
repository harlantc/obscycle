# ----------------------------------------------------------------------
# Makefile for the Toolkit package.
# ----------------------------------------------------------------------
# Don't change this
MK_TOP = ../../../../../..
ASC_DIR = ..
PROP_DIR = $(ASC_DIR)/../../..

# Set ASCDS_BLD_ env. variables
include $(MK_TOP)/infra/Makefile.master
include ../Makefile.master.overrides

# Test Dependencies
JAVA	        = $(JAVA2)/bin/java
JUNIT-CLASSES	= $(IVY)/junit/junit/jars/junit-4.12.jar
HAMCREST-CORE-CLASSES= $(IVY)/org.hamcrest/hamcrest-core/jars/hamcrest-core-1.3.jar

# Locations
WEB_APP_INF_DIR	= WEB-INF
APP_NAME	= toolkit-app
APP_DIR		= $(WEB_APPS_DIR)/
APP_STAGING_DIR	= $(APP_NAME)
APP_CLASSES_DIR	= $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/classes
APP_CONFIG_DIR	= $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)
TEST_JAVA_DIR	= test
TEST_RESOURCES_DIR= $(TEST_JAVA_DIR)/resources
TEST_CLASSES_DIR= $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/test-classes
TEST_REPORTS_DIR= $(APP_STAGING_DIR)/$(WEB_APP_INF_DIR)/test-reports
WEBINC_DIR	= $(MK_TOP)/obscycle/ui/cxc_include


CAPTCHA_JAR	= ../captcha/captcha.jar
JAVA_LIB_JAR	= \
	$(CAPTCHA_JAR) \
	$(CAPTCHALIBS) \
	$(LOG4J_JAR) \
	$(ASCDSAPILIB) 

# Source files
JAVA_FILES = \
	ToolkitLogFilter.java \
	Colden.java \
	Dates.java \
	Pimms.java \
	Precess.java \
	RCCalc.java

JAVA_LIB_FILES = \
	info/AbsoluteTime.java \
	info/Energy.java \
	info/RelativeTime.java \
	info/MPCat.java \
	info/MPCatRecord.java \
	info/Parameter.java \
	info/RCCalcValidator.java \
	info/ToolkitConstants.java \
	info/ToolkitValidator.java \
	info/Toolkit.java

# Test files
TEST_JAVA_FILES = \
	$(TEST_JAVA_DIR)/MockSession.java \
	$(TEST_JAVA_DIR)/MockRequest.java \
	$(TEST_JAVA_DIR)/info/AbsoluteTimeTest.java \
	$(TEST_JAVA_DIR)/info/RelativeTimeTest.java \
	$(TEST_JAVA_DIR)/info/MPCatTest.java \
	$(TEST_JAVA_DIR)/info/MPCatRecordTest.java \
	$(TEST_JAVA_DIR)/info/RCCalcValidatorTest.java \
	$(TEST_JAVA_DIR)/info/ToolkitValidatorTest.java \
	$(TEST_JAVA_DIR)/RCCalcTest.java

# JSP Files
APP_COLDEN_FILES = colden/*.jsp
APP_DATES_FILES	=  dates/*.jsp
APP_PIMMS_FILES	=  pimms/*.jsp pimms/*.js
APP_PRECESS_FILES = precess/*.jsp
APP_RCCALC_FILES = rccalc/*.jsp
APP_JSP_FILES = buttons.jsp

APP_HTML_FILES = toolkit.html toolkit-header.html \
	prop_help_toolkit.jsp  \
	toolkit.properties \
	toolkit.css \
	footer.html \
        $(WEBINC_DIR)/cxcfooterj.html \
        $(WEBINC_DIR)/cxcds_meta.html \
	$(APP_JSP_FILES) \
	$(APP_COLDEN_FILES) \
	$(APP_PIMMS_FILES) \
	$(APP_DATES_FILES) \
	$(APP_PRECESS_FILES) \
	$(APP_RCCALC_FILES)

APP_WAR_FILE = $(APP_NAME).war
APP_SERVLET_FILES = $(JAVA_FILES:%.java=%.class)
APP_SERVLET_LIB_FILES = $(JAVA_LIB_FILES:%.java=%.class)
APP_CONFIG_FILES = web.xml 

# Internal documentation support.
DOC_PACKAGES = $(JAVA_FILES) $(JAVA_LIB_FILES)
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_TITLE = "Proposal Toolkit Implementation Documentation"

# Test build/run macros
TEST_NAMES	 = AbsoluteTimeTest RelativeTimeTest MPCatTest MPCatRecordTest RCCalcTest RCCalcValidatorTest ToolkitValidatorTest
#TEST_NAMES	 = AbsoluteTimeTest
TEST_CLASS_FILES = $(patsubst $(TEST_JAVA_DIR)/%.java,$(TEST_CLASSES_DIR)/%.class,$(TEST_JAVA_FILES))

# Classpaths
CLASSPATH	= $(ROOTLIBS):$(SERVLETLIB):$(ASCDSAPILIB):.:$(CAPTCHA_JAR):$(LOG4J_JAR)
TEST_CLASSPATH	= $(JUNIT-CLASSES):$(HAMCREST-CORE-CLASSES):$(SERVLETLIB):$(TEST_RESOURCES_DIR)


MAKEALL	  = $(APP_WAR_FILE) javadoc
MAKECLEAN = $(APP_SERVLET_LIB_FILES) $(APP_SERVLET_FILES) info/MPCatRecord*.class web.xml buttons.jsp $(APP_STAGING_DIR)

INSTALL_WAR_FILE = $(APP_WAR_FILE:%=$(WWW_APPS_DIR)/%)
INSTALL_FILES = $(INSTALL_WAR_FILE)

include $(PROP_DIR)/Makefile.obswww

test: $(TEST_NAMES)

test-compile: $(APP_WAR_FILE) $(TEST_CLASS_FILES)

$(TEST_CLASSES_DIR):
	test -d $@ || $(MKDIR) $@

$(TEST_REPORTS_DIR):
	test -d $@ || $(MKDIR) $@

$(TEST_CLASSES_DIR)/%.class: $(TEST_JAVA_DIR)/%.java $(TEST_CLASSES_DIR)
	@echo test-compile...
	$(JAVAC) $(DEBUG_FLAG) $(LINT_FLAG) -d $(TEST_CLASSES_DIR) -sourcepath $(TEST_JAVA_DIR) -classpath "$(TEST_CLASSES_DIR):$(APP_CLASSES_DIR):$(TEST_CLASSPATH)" $<

$(TEST_NAMES): test-compile $(TEST_REPORTS_DIR)
	@echo $@
	$(JAVA) -classpath "$(TEST_CLASSES_DIR):$(APP_CLASSES_DIR):$(CLASSPATH):$(TEST_CLASSPATH)" org.junit.runner.JUnitCore $@ 2>&1 | tee $(TEST_REPORTS_DIR)/$@.txt

$(APP_WAR_FILE): $(APP_SERVLET_LIB_FILES) $(APP_SERVLET_FILES)  $(APP_CONFIG_FILES) $(APP_HTML_FILES) 
	$(RMDIR) $(APP_STAGING_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR) && \
	$(MKDIR) $(APP_CLASSES_DIR)/info && \
	$(MKDIR) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_SERVLET_FILES) $(APP_CLASSES_DIR) && \
        $(CP) info/*.class $(APP_CLASSES_DIR)/info && \
	$(CP) $(APP_CONFIG_FILES) $(APP_CONFIG_DIR) && \
	$(CP) $(JAVA_LIB_JAR) $(APP_CONFIG_DIR)/lib && \
	$(CP) $(APP_HTML_FILES) $(APP_STAGING_DIR) && \
	$(CP) $(MK_TOP)/infra/VERSION $(APP_STAGING_DIR) && \
	cd $(APP_STAGING_DIR) && $(JAR) cf ../$@ *

buttons.jsp: FORCE
	cat buttons.jsp.src > $@


web.xml:  FORCE
	cat web.xml.src > $@

FORCE:

