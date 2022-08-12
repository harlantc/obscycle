/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef JPL_H
#define JPL_H
#include "jcmutils.h"
#include "jcmcal.h"
#include "jcmastro.h"
#include <math.h>
#include "edouble.h"

/* Offsets in State array */
#define JPL_NPLANETS 14
#define JPL_S_SUN_OFFSET  0
#define JPL_S_EARTH_OFFSET 3
#define JPL_S_MOON_OFFSET 10
#define JPL_S_EM_OFFSET 13
#define JPL_S_LIB_CODE 11
#define JPL_S_NUT_CODE 12


#define MODE_NO  0
#define MODE_P   1
#define MODE_PV  2

/* Offsets in JPL binary file */
#define JPL_B_MOON_OFFSET 9
#define NUT_OFFSET 10
#define LIB_OFFSET 11
#define JPL_B_SUN_OFFSET 10
#define LIB_STATE_OFFSET 10


/* 
 *   pv[10]  Moon
 *   pv[11]  Libration
 *   pv[12]  Nutation:  psi, eps, 0, psidot, epsdot, 0
 */
#define JPL_BUF_MAX 1500


struct JplState_s {
 edouble et;
 double data[6*JPL_NPLANETS];
 double* pv[JPL_NPLANETS];  /* Pointers to offsets in state->data */
};



typedef struct {
 integer offset;
 integer ncoeff;
 integer nsets;
} JplEphPtr;


struct JplEph_s {
 Filename filename;
 FioFile fid; /* Input stream */
 integer numde; /* Ephemeris id number */
 integer nbytes; /* Number of bytes per record */
 integer nc;  /* Number of coefficients in each record */
 integer rec; /* Current record */
 double t1;
 double t2;
 double dt;
 integer ncon; /* Number of consts */

 double au;    /* AU size in m */
 double em;    /* Earth-moon ratio */
 char** title; /* Ephem title */
 char** names; /* Variable names */
 double values[400]; /* Variable values */
 JplEphPtr ptr[13];

 integer mask[12];  /* Mask objects */
 double buf[JPL_BUF_MAX];  /* Current buffer */
 logical km;
 logical bary;
 double dist_unit;
 double time_unit;

 JplState* state;
};



double jpl_read_record( JplEph* eph, edouble et );
void cheby_weight_v( double t, double* pc, double* vc, integer n );
void cheby_weight( double t, double* pc, integer n );
void cheby_eval( double* pc, double* vc, double* coeffs, integer n,
   double* position, double* velocity, integer dim, double dist_unit, double scaled_vel_unit );
void jpl_eval_earthmoon( JplEph* eph, JplState* state );

#endif

