#!/bin/tcsh -f
#
#NOTE: If you modify the server setup using the -s option for the
#      .ascrc, please remember to modify the server in the 
#      .htop file which is used by the WEB application
setenv MANPATH
alias uname /bin/uname
set x = `cat $OP_DIR/op_ascrc`
source $OP_DIR/.ascrc $x
printenv | grep DB_LOCAL_
printenv | grep DB_REMOTE_
arc4gl -i$1
exit $status

