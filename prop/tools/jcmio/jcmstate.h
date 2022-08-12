/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/


#include <stdio.h>
#include "jcmutils.h"
#include "fio.h"
#include "utsize.h"
#include "fiolist.h"
#include "tpio.h"
#include "tpiolib.h"


/*
 ================================================================================
 */

/* UtilVersionData: JCMUTILS internal object describing stack of program versions */

struct UtilVersionData_s {
 long nver;
 char** program;
 char** version;
};





struct UtilNull_s {
 unsigned short s_null;
 unsigned short s_inf; 
 unsigned short s_ninf;
 long i_null;
 long i_inf;
 long i_ninf;
 float r_null;
 float r_inf;
 float r_ninf;
 double d_null;
 double d_inf;
 double d_ninf;
 float r_ieee_nan;
 float r_ieee_inf;
 float r_ieee_ninf;
 double d_ieee_nan;
 double d_ieee_inf;
 double d_ieee_ninf;
};



/*
 ================================================================================
 */

struct UtilState_s {
 char* ver;
 UtilVersionData versions;
 long global_dbg;
 ErrorGlobal error_global;
 unsigned int seed;
/* Files */
 FioFileList fio;
 Tpio tpio;
 UtilEscData escapes;
 UtilNulls nulls;
 GenError error;
};

UtilNulls utn_state_nulls( UtilState state );

#ifdef UT_STATE_DEF
UtilState utn_jcmlib_state = NULL;
#else
extern UtilState utn_jcmlib_state;
#endif





