#! /bin/sh

# March 2008

# This is the official template for pipetool regression test scripts.
# In addition to supporting the "SHORTTEST" option, this script also
# allows the user to run individual subtests from the command line.
# The script will accept a series of test identifiers, generally of
# the form "test1" "test2" ... which are to be run.

# Portions of the script which must be customized are marked with "!!",
# below.

# The complete list of tests must be placed in "alltests", !!3, below.
# The test[s] for the SHORTTEST must be placed in "shortlist", !!4 below.

# !!1
# dates.t
# test script for dates

# !!2
# syntax:
# dates.t [<testid> ... ]
 
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
toolname="prop_dates"
toolE="prop_dates_exe"

# set up list of tests
# !!4
alltests="test1 test2 test3 test4_a test4_b test4_c test6_a test6_b test7_a test7_b test8 test9 test10
          test11 test12 test14_a test14_b test15 test16_a test16_b test4_d test4_e"

# "short" test to run
# !!5
shortlist="test1"


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
find_tool ${toolE}

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
  outfile=$OUTDIR/${testid}.txt
  savfile=$SAVDIR/${testid}.txt
  outstd=${outfile}.std
  savstd=${outfile}.ref

  # delete old outputs 
  \rm -f ${outfile}   ${outstd}  ${savstd}

  echo "running $testid" | tee -a $LOGFILE
  echo "" | tee -a $LOGFILE
  ####################################################################
  # run the tool
  case ${testid} in
    # !!6
    # <tool> from EST to TAI:<infile>:<outfile>
    test1 ) test1_string="$toolname from EST to TAI:$INDIR/date_list.in:$outfile \
                           > /dev/null 2>>$LOGFILE"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test2 ) test1_string="$toolname to greg p0 e 1996 Day 356 > $outfile"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test3 ) test1_string="$toolname to greg/CST e 1996 Day 356 042232.21 > $outfile"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    # test1_string="${toolname} f greg t days eval .."
    test4_a ) test1_string="${toolname} f greg t days e 1998 JAN 2.0  > $outfile"
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
    # test1_string="${toolname} f greg t days eval .."
    test4_b ) test1_string="${toolname} f greg t days e 1994 Aug 13 > $outfile"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    #! test AUG 
    test4_c ) test1_string="${toolname} f greg t days e 1994 AUG 13 > $outfile"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    #! month is numerical
    test4_d ) test1_string="${toolname} f greg t days e 1994 8 13 > $outfile"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    #! month is numerical
    test4_e ) test1_string="${toolname} f greg t days e 1994 08 13 > $outfile"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    # test zero from greg to days
    test6_a ) test1_string="${toolname} from greg to days\(greg 1994 Jan 0.0 \)\; eval 1994 Aug 13 142022.3 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
    # test zero from greg to days
    test6_b ) test1_string="${toolname} from greg to days\(greg 1998 Jan 2.0 \)\; eval 1998 Jan 3.0 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
    # test zero from greg to time
    test7_a ) test1_string="${toolname} from greg to time\(greg 1994 Jan 0.0 \)\; eval 1994 Aug 13 142022.3 > ${outfile} "
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    # test zero from greg to time
    test7_b ) test1_string="${toolname} from greg to time\(greg 1994 Jan 1.0 \)\; eval 1994 Jan 1.0 > ${outfile} "
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
    test8 ) test1_string="${toolname} from greg to jd e 2000 May 20.365 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test9 ) test1_string="${toolname} from greg to mjd e 2000 May 20.365 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test10 ) test1_string="${toolname} from greg/UTC to greg/EST e 1858 Nov 17.0 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test11 ) test1_string="${toolname} from greg/UTC to Greg/UT1 e 1858 Nov 17.0 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test12 ) test1_string="${toolname} from greg/UTC to Greg/TAI e 1858 Nov 17.0 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test14_a ) test1_string="${toolname} from jd to time eval 2450814.5 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE 
            echo "" | tee -a  $LOGFILE
	    eval $test1_string
            ;;
    test14_b ) test1_string="${toolname} from jd to time\(jd 2450813.5\)\; eval 2450814.5 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
    test15 ) test1_string="${toolname} from time\( JD 2450813.5 \) to doy eval 86400 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
    test16_a ) test1_string="${toolname} from time\( JD 0.0 \) to jd eval 86400 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
    test16_b ) test1_string="${toolname} from time\( BC 4714 Nov 24 120000.0 \) to jd eval 86400 > ${outfile}"
            echo $test1_string | tee -a  $LOGFILE
            echo "" | tee -a  $LOGFILE
            eval $test1_string
            ;;
  esac

  ####################################################################
  # if the tool failed to run, set mismatch to 0.
  #
  if test $? -ne 0; then
     echo "" | tee -a $LOGFILE
     echo "$toolname failed to run" | tee -a $LOGFILE
     echo "" | tee -a $LOGFILE
     mismatch=0           
  fi

  ####################################################################
  # check the ascii outputs
  #
  
  find_file  ${outfile}
  find_file  ${savfile}

  case ${testid} in
    test1 ) cp ${outfile} ${outstd}
            cp ${savfile} ${savstd}
            ;;
    test2 ) tail -1 ${outfile} > ${outstd}
            tail -1 ${savfile} > ${savstd}
            ;;
    test3 ) cat ${outfile} | grep Gregorian > ${outstd}
            cat ${savfile} | grep Gregorian > ${savstd}
            ;;
    test4_a|test4_b|test4_c|test4_d|test4_e|test6_a|test6_b ) cat ${outfile} | grep Days > ${outstd}
            cat ${savfile} | grep -i DAYS > ${savstd}
            ;;
    test7_a|test7_b|test14_a|test14_b ) cat ${outfile} | grep Time > ${outstd}
            cat ${savfile} | grep -i TIME > ${savstd}
            ;;
    test8|test9|test15|test16_a|test16_b ) cat ${outfile} | grep -i day > ${outstd}
            cat ${savfile} | grep -i day > ${savstd}
            ;;
    test10 ) cat ${outfile} | grep -i est > ${outstd}
            cat ${savfile} | grep -i est > ${savstd}
            ;;
    test11 ) cat ${outfile} | grep -i ut1 > ${outstd}
            cat ${savfile} | grep -i ut1 > ${savstd}
            ;;
    test12 ) cat ${outfile} | grep -i tai > ${outstd}
            cat ${savfile} | grep -i tai > ${savstd}
            ;;
  esac

  diff -b  ${outstd}  ${savstd} > /dev/null 2>>$LOGFILE
  if  test $? -ne 0 ; then
      echo "ERROR: MISMATCH in ${outstd}" >> $LOGFILE
      mismatch=0
  fi

  ####################################################################
  # Did we get an error?
  if test $mismatch -eq 0 ; then
    echo "${testid} NOT-OK"
    script_succeeded=1
  else
    echo "${testid} OK"
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
