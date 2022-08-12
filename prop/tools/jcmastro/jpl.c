/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jpl.h"
#define JPL_NN 15
static long nnames = JPL_NN;
static char* planet_names[JPL_NN] =
 { "Sun", "Mercury", "Venus", "Earth/Moon", "Mars",
   "Jupiter", "Saturn", "Uranus", "Neptune",
  "Pluto", "Moon", "Libration", "Nutation", "Earth" };
static char* mode_names[] = { "Off", "Pos", "PosVel" };

/* 
 * WARNING WARNING: Sun C compiler has "#define sun 1" so you can't 
 * WARNING WARNING: use a variable called "sun". Aaargh.
 */
static void jpl_record_eval_lib_nut( JplEph* eph, double t_record, JplState* state );
static void jpl_record_eval( JplEph* eph, integer planet, double t_record, double* pv, double* Sun );
static void jpl_interp( double* buf, JplEphPtr* ptr, double t_record, double dt_record,
                  integer dim, double dist_unit, double time_unit,
                  double* data );

/* Print the header of the jpl ephemeris */
void jpl_print( JplEph* eph )
{
 integer i;
 double GMB, GME;
 TextBuf abuf =" ";
 printf( "Ephemeris %s  Record size = %ld bytes Coeffs = %ld\n", 
  eph->filename, eph->nbytes, eph->nc );
 printf( "Ephemeris JPL ID: %ld\n", eph->numde );
 for ( i = 0; i < 3; i++ )
 {
  printf( "%s\n", eph->title[i] );
 }
 printf( "Number of constants: %ld\n", eph->ncon );
 printf( "File pointers: \n" );
 for ( i = 0; i < 13; i++ )
 {
  printf( "PTR[%2ld] = %6ld %4ld %4ld\n", i+1, eph->ptr[i].offset, eph->ptr[i].ncoeff,
   eph->ptr[i].nsets );
 }

 printf( "Ephemeris time range: %20.2f %20.2f Step: %12.2f\n",
   eph->t1, eph->t2, eph->dt );
 printf( "1 AU = %20.2f\n", eph->au );
 printf( "Earth/Moon =     %20.12f\n", eph->em );
 GMB = eph->values[10];
 GME = GMB / ( 1 + 1.0 / eph->em );
 for ( i = 0; i < eph->ncon; i++ ) 
 {
  integer no;
  double ks;
  double kk;
  double ke;
  utn_cs_copy( " ", abuf );
  no = i + 1;
  if ( (no >= 9 && no <= 18) || (no >= 93 && no <= 95) || (no >= 142 && no <= 144))
  {
   double krt;
   ke = eph->values[i] / GME;
   ks = 1.0e9 * eph->values[i] / eph->values[17];
   kk = ke * 5.976e9;
   krt = eph->values[17]/eph->values[i]; 
   sprintf( abuf, "%20.12g ME = %20.12g nMS = %20.12g Tt KR %20.12g", ke, ks, kk, krt );
  }
  printf( "C%03ld %-8s = %20.12g  %s\n", i+1, eph->names[i], eph->values[i], abuf );
 }
 for ( i = 1; i <= 12; i++ )
 {
  printf( "%-20s %-8s\n", planet_names[i], mode_names[(eph->mask[i-1])]);
 }
}


JplEph* jpl_alloc( char* infile )
{
 JplEph* eph;
 eph = (JplEph*)calloc(1,sizeof(JplEph));
 utn_cs_copy( infile, eph->filename );
 eph->state =(JplState*)calloc(1, sizeof( JplState ));
 jpl_init_state( eph->state );
 eph->km = 0;
 eph->bary = UT_TRUE;
 eph->fid = 0;
 return eph;
}


void jpl_free( JplEph* eph )
{
 if ( eph )
 {
  free( eph->state );
  free( eph );
 }
}

/*
 *   Initalize the state vector pointers. 
 *   Each pointer marks the start of a 6-double state vector
 *   in the state->data area.
 */
void jpl_init_state( JplState* state )
{
 integer i;
 for ( i = 0; i < JPL_NPLANETS; i++ )
 {
  state->pv[i] = &state->data[6*i];
 }
}


/*
 *   Set state variables.
 *   KM sets the output format to km, km/s   vs AU, AU/yr
 *   BARY sets the output coords to barycentric
 *   A planet name selects a particular mode for the given planet.
 *   Set km 1
 *   Set km 0
 *   set bary 1
 *   set bary 0
 *   set Jupiter 1
 * etc
 */
void jpl_set( JplEph* eph, char* name, integer mode )
{
 integer offset;

 if ( utn_cs_uceq( name, "KM" ))
 {
  eph->km = mode;
  if ( eph->km )
  {
   eph->time_unit = 86400.0;
   eph->dist_unit = 1.0;
  } else {
   eph->time_unit = 1.0;   /* Days */
   eph->dist_unit = 1.0/eph->au;
  }
  return;
 }
 else if  ( utn_cs_uceq( name, "BARY" ))
 {
  eph->bary = mode;
  return;
 }

 offset = jpl_offset( name );
 if ( offset < 0 )
  printf( "Unknown %s\n", name );
 else
 {
  eph->mask[offset] = mode;
 }
}


/*
 *   Convert planet name to offset in the ephemeris object
 */
integer jpl_offset( char* name )
{
 integer i;
 for( i =0; i < nnames; i++ )
 {
  if ( utn_cs_uceq( name, planet_names[i] ) )
   return i;
 }
 return -1;
}

static int dbd = 0;
/*
 *  Evaluate state at a given time t+dt.
 *  The use of the two variables t and dt allows for extra precision
 */
void jpl_eval( JplEph* eph, edouble et0 )
{
 JplState* state;
 integer planet;
 double t_record;
 double tt;
 integer jpl_bin_offset;  /* Offset in JPL binary file */
 TextWord date;
 if ( !eph )
 {
  printf( "No ephemeris\n" );
  return;
 }

 state = eph->state;
 state->et = cal_ed_resolve( et0 );
 tt = state->et.t + state->et.dt;
 if ( tt < eph->t1 ||
      tt > eph->t2 )
 {
   cal_fmt_date( tt, date, WORD_SIZE );
   printf( " -- Requested time %12.2f %s outside valid range -- \n", tt, date );
   return;
 }



 t_record = jpl_read_record( eph, state->et );

 /* Sun state */
 jpl_record_eval( eph, JPL_B_SUN_OFFSET, t_record, state->pv[JPL_S_SUN_OFFSET], NULL );

 for ( planet =1; planet <= 9; planet++ )
 {
  jpl_bin_offset = planet-1;
  if ( eph->mask[jpl_bin_offset] ) 
   jpl_record_eval( eph, jpl_bin_offset, t_record, state->pv[planet], state->pv[JPL_S_SUN_OFFSET] );

 }

 if ( eph->mask[JPL_B_MOON_OFFSET] )
   jpl_record_eval( eph, JPL_B_MOON_OFFSET, t_record, state->pv[JPL_S_MOON_OFFSET], NULL );

 jpl_record_eval_lib_nut( eph, t_record, state );
 jpl_eval_earthmoon( eph, state );

}

void jpl_eval_earthmoon( JplEph* eph, JplState* state )
{
 double* earthcenter;
 double* luna;
 double* earth;
 integer i;
 double wearth, wmoon;
 luna = state->pv[JPL_S_MOON_OFFSET];  /* Relative to Earth */
 earth = state->pv[JPL_S_EARTH_OFFSET]; /* is already EMB */
 earthcenter = state->pv[JPL_S_EM_OFFSET];
 wearth = eph->em / ( 1.0 + eph->em );
 wmoon = 1.0 / ( 1.0 + eph->em );
 for ( i = 0; i < 6; i++ )
  earthcenter[i] = earth[i] - wmoon * luna[i];

}


static void jpl_record_eval_lib_nut( JplEph* eph, double t_record, JplState* state )
{
 integer i;
 double* vector;
 logical q;
 double pnut[4];
 double rad = utn_radin_d( 1.0 );
 double* luna = state->pv[JPL_S_MOON_OFFSET];  /* Relative to Earth */
 integer dim = 3;/* Nutations */
 if ( eph->mask[NUT_OFFSET] > 0 && eph->ptr[NUT_OFFSET+1].ncoeff > 0 )
 {
  jpl_interp( eph->buf, &eph->ptr[NUT_OFFSET+1], t_record, eph->dt, 2, 1.0, 1.0, 
              pnut );
  vector = state->pv[JPL_S_NUT_CODE];
  vector[0] = pnut[0];
  vector[1] = pnut[1];
  vector[2] = 0.0;
  vector[3] = pnut[2];
  vector[4] = pnut[3];
  vector[5] = 0.0;
#if DEB
printf( "NUT = DPSI %12.4e DEPS %12.4e DPSI/DT %12.4e DEPS/DT %12.4e \n",
  vector[0]/rad, vector[1]/rad, vector[3]/rad, vector[4]/rad );
#endif
 }

 i = LIB_OFFSET+1;
 q= eph->mask[LIB_OFFSET] > 0 && eph->ptr[i].ncoeff > 0;
#if DEB
printf( "LIB CALC %d\n", q );
#endif
 if ( q )
 {
  double euler[3];
  double rotate[9];
  double dot1;
  double* ex = &rotate[0];
  /*   double* ey = &rotate[3];  */
  double* ez = &rotate[6];
  TextBuf buf;
  double vcpts[3];
  double cel_pole[3] = { 0.0, 0.0, 1.0 };
  double inc[3];
  double nodes[3];
  TextCard dbuf;
  double pole[2]; 
 double alpha;
 double lon[3];
  double merid[2];
  double cpts[3];
/*
C101 PHI      =     0.00512995970516    0.294 deg
C102 THT      =       0.382390655877   21.9 deg
C103 PSI      =        1.29414222411   74.1 deg
C104 OMEGAX   =    4.52470449902e-05    380.2 yr
C105 OMEGAY   =   -2.23092763199e-06    7711 yr
C106 OMEGAZ   =       0.229944858701    27.325 days?
for 1969 Jun 28
*/
  jpl_interp( eph->buf, &eph->ptr[i], t_record, eph->dt, dim, 1.0, 1.0,
               state->pv[JPL_S_LIB_CODE] );
  vector = state->pv[JPL_S_LIB_CODE];
#if DEB
printf( "LIB EULER = PHI %12.6f TH %12.6f PSI %12.6f RATES %12.6f %12.6f %12.6f \n",
  vector[0]/rad, vector[1]/rad, vector[2]/rad, vector[3]/rad, vector[4]/rad, vector[5]/rad );
#endif
  euler[0] = vector[0] / rad + 270;
  euler[1] = vector[1] / rad;
  euler[2] = vector[2] / rad - 270;
  utn_ar_euler_rotate( euler, rotate );
#if DEB
printf( "LIB EX: %8.3f %8.3f %8.3f EY: %8.3f %8.3f %8.3f EZ: %8.3f %8.3f %8.3f\n",
         ex[0], ex[1], ex[2], ey[0], ey[1], ey[2], ez[0], ez[1], ez[2] );
#endif
  utn_ar_polarll_d( ex, &merid[0], &merid[1] );
  utn_ar_polarll_d( ez, &pole[0], &pole[1] );
  cal_fmt_date( t_record, dbuf, UT_CARD_SIZE );
  sprintf( buf, "%-24s Pole: %8.3f %8.3f  Meridian: %8.3f %8.3f", dbuf, pole[0], pole[1],
   merid[0], merid[1] );
  utn_ar_vcross_d( cel_pole, ez, nodes );
  utn_ar_vcross_d( ez, nodes, inc );
  cpts[0] = utn_ar_vdot_d( nodes, ex );
  cpts[1] = utn_ar_vdot_d( inc, ex );
  cpts[2] = utn_ar_vdot_d( ez, ex );
  alpha = utn_argd_d( cpts[0], cpts[1] );
#if DEB
printf( "CPTS = %8.3f %8.3f %8.3f  ALPHA = %8.3f\n", cpts[0], cpts[1], cpts[2], alpha );
 utn_fio_msg( buf );
#endif

  cpts[0] = -luna[0];
  cpts[1] = -luna[1];
  cpts[2] = -luna[2];  /* EX for Luna center to Earth center vector */
  vcpts[0] = luna[3];
  vcpts[1] = luna[4];
  vcpts[2] = luna[5];  /* EX for Luna center to Earth center vector */
  utn_ar_vunit_d( cpts );
  utn_ar_vunit_d( vcpts );
  utn_ar_polarll_d( cpts, &merid[0], &merid[1] );  
  dot1 = utn_ar_vdot_d( cpts, ex );
  alpha = utn_dacosd( dot1 );

#if DEB
printf( "SubEarth Direction = %8.3f %8.3f Libration = %8.3f\n", merid[0], merid[1], alpha );
#endif
  utn_ar_vcross_d( vcpts, cpts, lon );  /* Lunar orbit normal */
#if DEB
printf( "LON = %8.3f %8.3f %8.3f  EZ = %8.3f %8.3f %8.3f\n", lon[0], lon[1], lon[2],      ez[0], ez[1], ez[2] );
#endif
  utn_ar_polarll_d( vcpts, &merid[0], &merid[1] );  
#if DEB
printf( "Velocity Vector Direction = %8.3f %8.3f\n", merid[0], merid[1] );
#endif
  utn_ar_polarll_d( lon, &pole[0], &pole[1] );  
  dot1 = utn_ar_vdot_d( lon, ez );
  alpha = utn_dacosd( dot1 );
#if DEB
printf( "LON = %8.3f %8.3f %8.3f  EZ = %8.3f %8.3f %8.3f\n", lon[0], lon[1], lon[2],     ez[0], ez[1], ez[2] );
printf( "Orbit Normal Direction = %8.3f %8.3f CosE = %8.3f Obliquity = %8.3f\n", pole[0], pole[1], dot1, alpha );
#endif
 }

}

static void jpl_record_eval( JplEph* eph, integer planet, double t_record, double* pv, double* Sun )
{ 
 integer dim = 3;
 integer j;
 jpl_interp( eph->buf, &eph->ptr[planet], t_record, eph->dt, dim, eph->dist_unit, eph->time_unit, 
       pv );
 
 if ( planet < 9 && !eph->bary  && Sun )
 {
    for ( j = 0; j < 6; j++ )
     pv[j] -=  Sun[j];
 }
}




/*
 * Read the record corresponding to et
 * Return the time offset into the record 
 */
double jpl_read_record( JplEph* eph, edouble et )
{
 integer offset;
 integer record_no;
 double tstart;
 double t_record;

 offset = ( et.t - eph->t1 ) / eph->dt;
 record_no = (integer)offset;
 if ( et.t == eph->t2 ) record_no--;
 tstart = eph->t1 + record_no * eph->dt;
 t_record =  ( et.t - tstart ) + et.dt;
#if 0
   cal_fmt_date( tt, date, WORD_SIZE );
printf( "T %12.2f DT %12.2f T1 %12.2f FDT %12.2f %s\n", t, dt, t1, fdt, date );
#endif
 if ( record_no != eph->rec )
 {
  jpl_eph_rec( eph, record_no );
 }
 return t_record;
}


/* Interpolate chebyshev */

/* Buffer is :

  Subinterval 0:   X coeffs  Y coeffs Z coeffs
  Subinterval 1:   X coeffs  Y coeffs Z coeffs


 */
static void jpl_interp( double* buf, JplEphPtr* ptr, double t_record, double dt_record,
                  integer dim, double dist_unit, double time_unit,
                  double* data )
{
 double pc[18];
 double vc[18];
/* buf( ptr->ncoeff, dim, * ) : total eph->nc */
 double* velocity;
 integer subinterval;
 integer subinterval_offset;
 double dt_subinterval;
 double t_subinterval;
 double t_scaled;
 double dt_scaled;  /* Unit of tc  */
 double* position;
 double vel_unit;
 double scaled_vel_unit;
 integer offset = ptr->offset-1;


 position = data;
 velocity = data + dim;  

 /* Time bin is divided into nsets subintervals. Within
    each subinterval, we use a scaled time running from -1 to +1. 
    The Chebyshev coeffs are tuned to this scaled time */

 dt_subinterval  = dt_record / ptr->nsets; 
 subinterval = rmod_d( t_record, &t_subinterval, dt_subinterval );
 t_scaled  = 2.0 * ( 2.0 * ( t_subinterval / dt_subinterval ) - 1.0 );
 dt_scaled = dt_subinterval / 2.0;
 vel_unit = dist_unit / time_unit;
 scaled_vel_unit = vel_unit / dt_scaled;

 cheby_weight( t_scaled, pc, ptr->ncoeff );
 cheby_weight_v( t_scaled, pc, vc, ptr->ncoeff );
 subinterval_offset = subinterval * dim * ptr->ncoeff;
 cheby_eval( pc, vc, buf+offset+subinterval_offset, ptr->ncoeff, position, velocity, dim,
             dist_unit, scaled_vel_unit );
}

/*
 *   Evaluate position and velocity Chebyshev polys
 *   pc  Weights for this time value
 *   vc  
 *   coeffs  Buffer of coefficients
 *   n       Degree
 *   
 *   position, velocity        Output data
 *   dim                       Number of axes to evaluate
 *  The results are scaled by the given factors.
 */
void cheby_eval( double* pc, double* vc, double* coeffs, integer n,
   double* position, double* velocity, integer dim, double dist_unit, double scaled_vel_unit )
{
 integer axis;
 integer coeff_no;
 integer axis_offset;
 double coeff;
 for ( axis = 0; axis < dim; axis++ )
 { 
  position[axis] = 0.0;
  velocity[axis] = 0.0;
  axis_offset = axis * n;
  for ( coeff_no = n-1; coeff_no >= 0; coeff_no-- )
  {
   coeff = coeffs[axis_offset+coeff_no];
   position[axis] += pc[coeff_no] * dist_unit * coeff;
   velocity[axis] += vc[coeff_no] * scaled_vel_unit * coeff;
if ( dbd )
printf( "VEL[%ld] = %f  VC[%ld] = %f  COEFF = %f SVU = %f\n", axis, velocity[axis], coeff_no, vc[coeff_no], coeff, scaled_vel_unit );
  }
if ( dbd )
printf ("FINAL VEL[%ld] = %20.10f\n", axis, velocity[axis] );
 }
#if 0
if ( dbd )
 for ( axis = 0; axis < dim; axis++ )
 { 
printf ("FINAL POS[%ld] = %20.10f\n", axis, position[axis] );
 }
#endif
}


void cheby_weight( double t, double* pc, integer n )
{
 integer coeff_no;
 /* Calculate weights
   Each entry in the pc[i] array is a degree i Chebyshev polynomial
   evaluated at time tc;
    pc0 = 1
    pc1 = tc;
    pc2 = 2 tc^2 pc1 - 1;
    pc3 = 2 tc pc2 - pc1;  etc;
  For greater eff one could save this 
  */
 pc[0] = 1.0;
 pc[1] = 0.5 * t;
 coeff_no = 1;
if ( dbd )
printf( "T = %f PC[%ld] = %f\n", t, coeff_no, pc[coeff_no] );
 for ( coeff_no = 2; coeff_no< n; coeff_no++ )
 {
  pc[coeff_no] = t * pc[coeff_no-1] - pc[coeff_no-2];
if ( dbd )
printf( "T = %f PC[%ld] = %f\n", t, coeff_no, pc[coeff_no] );
 }
}

/*
 *  Chebyshev weights for derivative polynomials
 *  t is scaled argument
 *  pc are weights for chebyshev of degree n (input)
 *  vc are weights for derivative
 */
void cheby_weight_v( double t, double* pc, double* vc, integer n )
{
 integer coeff_no;
 vc[0] = 0.0;
 vc[1] = 1.0;
 vc[2] = 2 * t;
 for ( coeff_no = 3; coeff_no < n; coeff_no++ )
 {
   vc[coeff_no] = t * vc[coeff_no-1] + 2  * pc[coeff_no-1] - vc[coeff_no-2];
#if 0
printf( "VC I = %ld %10.5f %10.5f %10.5f %10.5f %10.5f\n",  coeff_no, vc[coeff_no], t, vc[coeff_no-1], vc[coeff_no-2], pc[coeff_no-1] );
#endif
 }
}

void jpl_print_state( JplEph* eph )
{
 JplState* state = eph->state;
 integer i;
 for ( i = 0; i < JPL_NPLANETS; i++ )
 {
  jpl_print_planet( eph, state, i );
 }
}

void jpl_print_planet( JplEph* eph, JplState* state, integer i )
{
 TextWord tag;
 double r;
 double* vector;
 double re;
 double lat;
 double phi;
 TextBuf buf;
 char* fmt;
 TextBuf fmt1 = "%-12s %14.3f %23.1f %23.1f %23.1f %23.1f %23.6f %23.6f %23.6f %8.3f %8.3f";
 TextBuf fmt2 = "%-12s %14.3f %16.6f %16.6f %16.6f %16.6f %16.6f %16.6f %16.6f %8.3f %8.3f";
 TextBuf fmt3 = "%-12s %14.3f %g %g %g %g %g %g";
 sprintf( tag, "%s", planet_names[i] );
 vector = state->pv[i];

 if ( i >= JPL_S_LIB_CODE )
 {
  sprintf( buf, fmt3, tag, state->et.t + state->et.dt, vector[0], vector[1], vector[2],
      vector[3], vector[4], vector[5] );
  
 }
 else
 {
  if ( eph->km )
   fmt = fmt1;  
  else
   fmt = fmt2;

  r = utn_ar_vnorm_d( vector );  
  re = sqrt( vector[0] * vector[0] + vector[1] * vector[1] );
  lat = argd_d( re, vector[2] );
  phi = argd_d( vector[0], vector[1] );
  sprintf( buf, fmt, tag, state->et.t + state->et.dt, vector[0], vector[1], vector[2],
      vector[3], vector[4], vector[5], r, phi, lat );
  
 }

  utn_fio_msg( buf );

}


void jpl_track( JplEph* eph, integer id, double t1, double t2, double dt, JplState* state )
{
 double t;
 edouble et;
 for ( t = t1; t <= t2; t += dt )
 {
  et = cal_ed_make( t, 0.0 );
  jpl_eval( eph, et );
  jpl_print_planet( eph, state, id );
 }
}

/*
 *  Calculate position vector of planet at given time.
 */
void jpl_ephem_eval( JplEph* eph, integer planet, edouble et, double* pv  )
{
 JplState* state;

/* Calculate state if required */
 state = eph->state;
 if ( !cal_ed_eq( state->et, et ))
  jpl_eval( eph, et );


 utn_ar_copy_d( state->pv[planet], pv, 6 ); 
}




void jpl_close( JplEph* eph )
{
 if ( eph && eph->fid )
  utn_fio_file_close( eph->fid ); 
 jpl_free( eph );
  
}


/* Open the JPL ephemeris and read its header */

JplEph* jpl_open( char* infile )
{
 JplEph* eph;
 FioFile in;
 integer n, nw; 
 char buf[10000];
 integer i;
 char* ptr;
 integer* ipt_hi;

 integer ncoeffs;
 integer ipt[39];
 integer nt = 13;
 integer nd = 3;
 integer kmx;
 integer khi;
 integer nbytes;
 integer ksize;
 double ss[3];
/*
 *  Header record
 *  Byte
 *  1-252    TTL(14,3)  char*6
 *  253-2652  CNAM(400)  char*6
 *  2653-2676  SS(3)      real*8
 *  2677-2680  NCON       int*4
 *  2681-2688  AU         real*8
 *  2689-2696  EMRAT      real*8
 *  2697-2840  IPT(3,12)  int*4
 *  2841-2844  NUMDE      int*4
 *  2845-2856  IPT(3,1)   int*4
 */
 integer next;

 eph = jpl_alloc( infile );

 eph->title = utn_ar_alloc_c( 84, 3 );
 eph->names = utn_ar_alloc_c( 8, 400 );
 in = utn_fio_open_sr( infile );
 if ( in == 0 )
 {
  printf( "Failed to open %s\n", infile );
  return NULL;
 }
 eph->fid = in;
 /* Read header */
 n = 2856;
 utn_fio_read_sio( in, buf, n, &nw, UT_SZ_C );
 ptr = buf;
/* 14 x 3 x 6 bytes = 252 bytes of string */
 for ( i = 0; i < 3; i++ )
 {
  utn_cs_copy_siz( ptr, eph->title[i], 84 );
  ptr += 84;
 }
 for ( i = 0; i < 400; i++ )
 {
  utn_cs_copy_siz( ptr, eph->names[i], 6 );
  ptr += 6;
 }

 utn_ieee_get_d( &buf[2652], ss, 3 );
 eph->t1 = ss[0];
 eph->t2 = ss[1];
 eph->dt = ss[2];
 utn_ieee_get_i( &buf[2676], &eph->ncon, 1 );
 utn_ieee_get_d( &buf[2680], &eph->au, 1 );
 utn_ieee_get_d( &buf[2688], &eph->em, 1 );
 utn_ieee_get_i( &buf[2696], ipt, 36 );
 utn_ieee_get_i( &buf[2840], &eph->numde, 1 );
 utn_ieee_get_i( &buf[2844], ipt+36, 3 );
 eph->time_unit = 1.0;   /* Days */
 eph->dist_unit = 1.0/eph->au;

 kmx = 0;
 khi = 0;
 for ( i = 0; i < nt; i++ )
 {
  if ( ipt[3*i] > kmx )
  {
   kmx = ipt[3*i];
   khi = i;
  }
  eph->ptr[i].offset = ipt[3*i];
  eph->ptr[i].ncoeff = ipt[3*i+1];
  eph->ptr[i].nsets = ipt[3*i+2];
 }
 if ( khi == 11 ) nd = 2; 
 ipt_hi = ipt + 3 * khi;
 ksize = 2 * ( ipt_hi[0] + nd * ipt_hi[1] * ipt_hi[2] -1 );
 nbytes = ksize * 4;
 ncoeffs = ksize / 2;
 printf( "Opened %s  Record size = %ld bytes Coeffs = %ld\n", infile, nbytes, ncoeffs );
 eph->nbytes = nbytes;
 eph->nc = ncoeffs;
 next = nbytes - n;
 utn_fio_read_sio( in, buf, next, &nw, UT_SZ_C ); /* Discard */
 utn_fio_read_sio( in, buf, nbytes, &nw, UT_SZ_C );
 utn_ieee_get_d( buf, eph->values, 400 );
 for ( i =0; i < 12; i++ )
 {
  eph->mask[i] = 2;
 }
 
 eph->rec = 0;
 return eph;
}

/*  
 *   Read a specified record from the binary JPL file into the EPH buffer
 */
void jpl_eph_rec( JplEph* eph, integer nr )
{
  integer nw;
  integer zero_offset = 2;
  integer pos;
  double buf[JPL_BUF_MAX];
  eph->rec = nr;
  pos = eph->nbytes * (nr+zero_offset);
  utn_fio_seek( eph->fid, pos );
  utn_fio_read_sio( eph->fid, buf, eph->nc, &nw, UT_SZ_D );
  utn_ieee_get_d( (char*)&buf[0], eph->buf, eph->nc );
  if ( nw != eph->nc )
   printf( "Read error on record %ld\n", nr );

}


