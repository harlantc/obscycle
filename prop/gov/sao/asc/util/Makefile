# Makefile for the Java packages in database
#

MK_TOP = ../../../../../..
ASCDIR = ./..

# The root directory where the Java compiler will store classes.
ROOT_CLASSDIR = $(ASCDIR)/../../../classes

# The root directory where make will find the Java sources.
ROOT_SRCDIR = $(ASCDIR)/../../..

# Get the standard macro definitions
include $(MK_TOP)/infra/Makefile.master

# Override certain macros to deal with JDK bugs/limitations, etc.
include ../Makefile.master.overrides

# Get the common Java macro definitions that apply to the "ASC" Java
# tools.
include ../Makefile.inc

JAVA_FILES = \
	ASCCheckBoxModel.java \
	ASCComboBoxModel.java \
	ASCPasswordField.java \
	CheckBoxBorderPanel.java \
	ComboBox.java \
	ComboBoxBorderPanel.java \
	ComboBoxModel.java \
	DateTextField.java \
	GridBagLayoutUtil.java \
	LocationUtil.java \
	Logger.java \
	Profiler.java \
	ToggleLookAndFeel.java

CLASS_FILES = $(JAVA_FILES:%.java=%.class)

MAKEALL       = $(CLASS_FILES)

%.class: %.java
	$(JAVAC) $(JAVAC_OPTIONS) $<

include $(MK_TOP)/infra/Makefile.all		# Makefile for whole directory tree

