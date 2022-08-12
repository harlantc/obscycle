#!/bin/tcsh -f
# 
# This script is used to setup the environment to create PDFs from the
# CPS web page.  It expects 4 positional parameters:
# 1:user 2:server 3:proposalNumber 4:output directory
#
setenv LANG en_US.UTF-8
setenv LD_LIBRARY_PATH "${ASCDS_INSTALL}/lib:${ASCDS_INSTALL}/ots/lib:${SYBASE}/OCS-16_0/lib"
setenv PYTHONPATH ${ASCDS_INSTALL}/lib/python3.7/site-packages:${SYBPYTHON}

#ddt
if ( $5 == 'b' ) then
  setenv HOME $OBSCYCLE_DATA_PATH/prop/cps/
  set ddtlog = ${HOME}/ddtpdf.log
  touch $ddtlog
  set x = `cat $OBSCYCLE_DATA_PATH/prop/cps/cps_ascrc`
  set cdate = `date`
  echo "$cdate $3 - $x" >>$ddtlog
  source ${ASCDS_INSTALL}/config/system/.ascrc $x 
  nohup ${ASCDS_INSTALL}/bin/pdfGen_cli.py -u $1 -d $2  -l $3  -o $4 --show -f merge >>& $ddtlog &
# Anonymous
else if ( $5 == 'h' ) then
  ${ASCDS_INSTALL}/bin/pdfGen_cli.py -u $1 -d $2  -l $3  -o $4 --hide
# Non-anonymous
else
    ${ASCDS_INSTALL}/bin/pdfGen_cli.py -u $1 -d $2  -l $3  -o $4 --show
endif
exit $status

