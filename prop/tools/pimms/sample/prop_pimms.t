#! /bin/sh

# 10/18/2016 - initial version 

# !!1
# prop_pimms.t
# test script for precess

# !!2
# syntax:
# precess.t [<testid> ... ]
 
######################################################################
# subroutine
# error_exit <message>
# Fatal error exit

error_exit()
{
  echo ""   | tee -a $LOGFILE
  echo "$1" | tee -a $LOGFILE
  echo ""   | tee -a $LOGFILE
  echo "${toolname} : FAIL" | tee -a $LOGFILE
  exit 1
}

######################################################################
# subroutine
# find_tool <toolname>
# checks that tool exists and is runnable

find_tool()
{
  s1=`type $1`
  s2=`echo $s1 | awk -F" " '{ print $3}'`
  if test -x $s2 ; then
    :
  else
    error_exit "tool $1 not found"
  fi
}

find_file()
{
  if (test -f $1 ); then
    :
  else
    error_exit "ERROR:  file '$1'  not found!"
  fi
}

######################################################################
# Initialization

# !!3
toolname="prop_pimms_exe"

# set up list of tests
# !!4
alltests="ex1 ex2 ex3 ex4 comp1 comp2 comp3 test1 chandra3"

# "short" test to run
# !!5
shortlist="ex1"


# compute date string for log file
DT=`date +'%d%b%Y_%T'`

# convenience definitions
OUTDIR=$TESTOUT/$toolname
SAVDIR=$TESTSAV/$toolname
INDIR=$TESTIN/$toolname
LOGDIR=$TESTLOG/$toolname

# set up log file name
LOGFILE=$LOGDIR/${toolname}_log.$DT

#get rid of old logs
\rm -f $LOGDIR/${toolname}_log.*


# Any tests specified on command line?
if test $# -gt 0; then
  # yes, do those tests
  testlist=$*
else
  # No, see if we are to do "short" test
  if test "x$SHORTTEST" = "x" ; then
    # No, do everything
    testlist=$alltests
  else
    # yes, do short test
    testlist=$shortlist
  fi
fi


# Make sure we have a log directory
if test -d $LOGDIR ; then
 :
else
  mkdir -p $LOGDIR 
  if test $? -ne 0 ; then
    error_exit ""
  fi
fi


# Make sure we have an output directory
if test -d $OUTDIR ; then
 :
else
  mkdir -p $OUTDIR >> $LOGFILE 2>&1
  if test $? -ne 0 ; then
    error_exit "can't create output directory $OUTDIR"
  fi
fi

# Make sure we have an input directory
if test -d $INDIR ; then
:
else
  if test $? -ne 0 ; then
    error_exit "can't find input directory : $INDIR"
  fi
fi

# Make sure we have a save directory
if test -d $SAVDIR ; then
:
else
  if test $? -ne 0 ; then
    error_exit "can't find save directory : $SAVDIR"
  fi
fi

# check for directory environment variables
if test "x${TESTIN}" = "x" -o "x${TESTOUT}" = "x" -o "x${TESTSAV}" = "x" \
   -o "x${TESTLOG}" = "x" ; then
  error_exit "one or more of TESTIN/TESTOUT/TESTSAV/TESTLOG not defined" 
fi

# if test "x${JCMLIBDATA}" = "x" ; then 
#  echo "JCMLIBDATA not defined."
#  echo "set it to ${ASCDS_INSTALL}/config/jcm_data"
# fi

# check for tools
# if a utility is used in the form "utility <args> > outfile", and 'utility'
# cannot be run, 'outfile' will still be created.  If utility is used on 
# both the output and reference files of a tool the resultant utility output 
# files will both exist and be empty, and will pass a diff.

find_tool ${toolname}

# announce ourselves
echo ""
echo "${toolname} regression" | tee $LOGFILE
echo "" | tee -a $LOGFILE

# ----- copy some files -----
# cp ${INDIR}/file  ${OUTDIR}/.


script_succeeded=0

######################################################################
# Begin per-test loop

for testid in $testlist
do
  ####################################################################
  # Init per-test error flag
  #
  mismatch=1    

  # Set up file names
  savfile=$SAVDIR/log.${testid}

  echo "running $testid" | tee -a $LOGFILE
  echo "" | tee -a $LOGFILE
  ####################################################################
  infile=${INDIR}/${testid}.xco.in
  tmp_inF=${OUTDIR}/tmp_${testid}.xco

  rm -f ${OUTDIR}/tmp_${testid}.xco
  cat $infile | sed "s|testout|$TESTOUT|g" >  ${tmp_inF}

  # delete old outputs 
  # eg. TESTOUT/prop_pimms_exe/log.ex1
  outfile=${OUTDIR}/log.${testid}
  \rm -f ${outfile}

  ### run the tool
  test1_string="${toolname} @${tmp_inF}"
  printf "$test1_string \n" | tee -a  $LOGFILE 
  eval $test1_string
  if test $? -ne 0; then
     ### if the tool failed to run, set mismatch to 0.
     echo "" | tee -a $LOGFILE
     echo "$toolname failed to run" | tee -a $LOGFILE
     echo "" | tee -a $LOGFILE
     mismatch=0           
  fi

  # remove after the 'eval' command
  rm -f ${tmp_inF}

  ####################################################################
  # check the ascii outputs
  #
  
  find_file  ${outfile}
  find_file  ${savfile}

  # printf " outfile = ${outfile}  \n"
  # printf " savfile = ${savfile}  \n"
  diff -q  ${outfile}  ${savfile} 
  if  test $? -ne 0 ; then
      echo "ERROR: MISMATCH in ${outstd}" >> $LOGFILE
      mismatch=0
  fi

  ####################################################################
  # Did we get an error?
  if test $mismatch -eq 0 ; then
    echo "${testid} NOT-OK" | tee -a $LOGFILE
    script_succeeded=1
  else
    echo "${testid} OK" | tee -a $LOGFILE
  fi

  echo "" | tee -a $LOGFILE

done
# end per-test loop
######################################################################


######################################################################
# report results

# blank line
echo "" | tee -a $LOGFILE

if test $script_succeeded -eq 0; then
    echo "${toolname} : PASS" | tee -a $LOGFILE
else
    echo "${toolname} : FAIL" | tee -a $LOGFILE
fi

echo ""
echo "log file in ${LOGFILE}"
echo ""

exit $script_succeeded
