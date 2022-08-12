#!/bin/tcsh -f
# 
setenv LANG en_US.UTF-8
nohup ${ASCDS_INSTALL}/bin/prop_ddt_confirmation.pl $* >>& ${ASCDS_PROP_LOGS}/../ddtconfirm.log
exit $status

