MK_TOP = ../../../..

MAKELOG	  =	makeconf.log
FILES	  =	config.log config.status config.cache  $(MAKELOG)
RC_FILES =	$(FILES) configure  # Makevars Makevars32 inc*/utconfig.h


all:

install:


conf:	configure 
	-$(RM) $(FILES)
	-./configure | tee $(MAKELOG) | grep -i "configure"
	@echo

configure:	configure.ac
	autoconf
	@echo

.PHONEY:	clean realclean
clean:
	/bin/rm -f $(FILES)
	@-if [ ! -f Makevars ]; then \
	echo "" > Makevars; \
	fi
	@-if [ ! -f Makevars32 ]; then \
	echo "" > Makevars32; \
	fi

realclean:
	/bin/rm -f $(RC_FILES)
	/bin/rm -f -R autom4te.cache/

uninstall: clean
