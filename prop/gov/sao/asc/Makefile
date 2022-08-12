# Makefile for the Java applications and their subordinate modules

MK_TOP = ../../../../..
PROP_DIR = ../../..

# The root directory where make will find the Java sources.
ROOT_SRCDIR = $(PROP_DIR)
ROOT_CLASSDIR = $(ROOT_SRCDIR)

# Makefile macro definitions used to build all the ObsCat Editor Java
# class files.
#
# NOTE: The following macros must be defined prior to including this input.
#
#		CLASSDIR - Where to strore the generated class files
#		SOURCES - The set of .java files comprising the module.

# Include the highest level macro definitions, mainly pointers.
include $(MK_TOP)/infra/Makefile.master

# The Java claspath environment variable.
CLASSPATH = $(ROOT_CLASSDIR)


# Override various Makefile.master values, including, possibly, the
# CLASSPATH setting.
include Makefile.master.overrides

# The modules are not to be confused with Java packages.  At this
# level modules identify applications.
MODULES = captcha jarrunner obsed/servlet obsed servlet toolkit webreports  too
WEBINSTALL_MODULES = jarrunner obsed 

include $(MK_TOP)/infra/Makefile.module




