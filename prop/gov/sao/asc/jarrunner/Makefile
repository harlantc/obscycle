# Makefile for JarRunner package.

# Don't change this
CP      = cp
MK_TOP = ../../../../../..

# Set ASCDS_BLD_ env. variables
include $(MK_TOP)/infra/Makefile.master

include ../Makefile.master.overrides


JAVA_TOP = ../../../..

MAKECLEAN		= $(CLASS_FILES)
MAKEALL			= $(CLASS_FILES)
CLASS_FILES		= JarRunner.class JarClassLoader.class
INSTALL_CLASS_FILES	= $(CLASS_FILES:%=$(ASCDS_BLD_WWW_BIN)/java_class/%)
INSTALL_FILES		= $(INSTALL_CLASS_FILES)

WEBINSTALL_BIN_FILES	= $(CLASS_FILES:%=$(WEB_BIN_DIR)/java_class/%)
WEBINSTALL_FILES	= $(WEBINSTALL_BIN_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

%.class: %.java
	$(JAVAC) $(JAVAC_OPTIONS) $<
