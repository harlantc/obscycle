# Makefile for the special ObsCat Editor files.

MK_TOP = ../../../../../..

# Include macro definitions
include $(MK_TOP)/infra/Makefile.master
include ../Makefile.master.overrides

# The root directory where make will find the Java sources.
ROOT_CLASSDIR = $(MK_TOP)/obscycle/prop

OBSED_JAR_FILE = obsed.jar
APP_NAME = obsed

# JavaDoc support macros
DOC_PACKAGES = *.java */*.java */*/*.java
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_TITLE = "ObsCat Editor Implementation Documentation"

# Setup the installation macros
BIN_DIR = $(ASCDS_BLD_BIN)
JAR_DIR = $(HTML_DIR)

SCRIPTS = obscat_editor
SCRIPT_SRC = $(SCRIPTS).src


MAKEALL = $(SCRIPTS) $(OBSED_JAR_FILE) 
#javadoc don't make this right now 'cause there's way too many warnings
MAKECLEAN = obsedclean

INSTALL_FILES = $(SCRIPTS:%=$(BIN_DIR)/%) $(INSTALL_CGI_FILES)
INSTALL_CGI_FILES = $(OBSED_JAR_FILE:%=$(ASCDS_BLD_WWW_HTDOC)/%)

# Manifest file
MANIFEST = gov/sao/asc/obsed/Manifest.txt



WEBINSTALL_JAR_FILES = $(OBSED_JAR_FILE:%=$(JAR_DIR)/%)
WEBINSTALL_FILES = $(WEBINSTALL_BIN_FILES) $(WEBINSTALL_JAR_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

# Build the script
$(SCRIPTS): $(SCRIPT_SRC)
	cat $(SCRIPT_SRC) | sed -e s%{JAVA_EXE_PATH}%$(JRE_BIN)/java% \
                                -e s%{WEB_HOST}%$(WEB_HOST)% \
                                -e s%{WEB_CLASSPATH}%$(WEB_CLASSPATH)% \
                                -e s%{JCONNECT}%$(JCONNECTLIB)% \
                                > $@

# Build the distribution jar file containing the needed classes.
$(OBSED_JAR_FILE): obsedclean
	$(JAVAC) $(JAVAC_OPTIONS) Editor.java && \
	cd ../../../..; CLASSPATH=$(CLASSPATH); export CLASSPATH; \
	$(JAR) cmf $(MANIFEST) gov/sao/asc/obsed/$@ `find gov \( \
	-name '*.cf' -o \
	-name '*.class' -o \
	-name '*.dtd' -o \
	-name '*.gif' -o \
	-name '*.pf' -o \
	-name '*.txt' -o \
	-name '*.hlp' -o \
	-name '*.xml' \) -print | \
        egrep -e 'obsed|util|event' `



obsedclean:
	rm -f obsed.jar; find . -name '*.class' -exec rm {} \;
