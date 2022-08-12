# Makefile for ascds library
#

MK_TOP = ../../../../../..

include $(MK_TOP)/infra/Makefile.master
include ../Makefile.master.overrides


DOC_PACKAGES = 	captcha

JAVA_FILES =	captcha/VerifyRecaptcha.java \
		captcha/UseRecaptcha.java  \
		captcha/ClientIPAddress.java 


APP_NAME = captcha
CAPTCHA_JAR = $(APP_NAME).jar

APP_FILES = $(JAVA_FILES:%.java=%.class) 
APP_JAR_FILES =	$(CAPTCHA_JAR)

CLASSPATH = $(ROOTLIBS):$(SERVLETLIB):$(CAPTCHAPATH)

# Internal documentation support.
DOC_PACKAGES = $(JAVA_FILES) 
DOC_DIR = $(OBS_DOC_DIR)/$(APP_NAME)
DOC_TITLE = "Captcha Library"


MAKEALL		= announce $(CAPTCHA_JAR) javadoc
MAKECLEAN	= $(APP_FILES) $(CAPTCHA_JAR) $(INSTALL_JAR_FILES)  $(DOC_DIR)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

announce:
	@echo "   /-----------------------------------------------------\\"
	@echo "   |              Building captcha library              |"
	@echo "   \-----------------------------------------------------/"


$(CAPTCHA_JAR): $(APP_FILES)
	$(JAR)  cf $@ $(APP_FILES) 
