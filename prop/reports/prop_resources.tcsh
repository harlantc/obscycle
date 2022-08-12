#!/bin/tcsh -f
# 
setenv LANG en_US.UTF-8
# this env variable is needed by the MP script
# if the MP resource_allocator.pl script moves from /data/mpcritrc/bin
# pass a -m <path> parameter on this line
${ASCDS_INSTALL}/bin/prop_resources.pl $* 
exit $status

