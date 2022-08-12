/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

static JplEph* jpl_eph = NULL;


void orrery_eval_ec( World world, edouble et, double* matrix, double* pv )  
{
 double pve[6];
 orrery_eval( world, et, pve );
 utn_ar_lmat_postx_d( matrix, pve, pv );
 utn_ar_lmat_postx_d( matrix, pve+3, pv+3 );
  
}

void orrery_print_header( void )
{
 jpl_print( jpl_eph );
}

void orrery_eval( World world, edouble et, double* pv )
{
 utn_ar_zero_d( pv, 6 );
 if ( utn_cs_eq( world->eph_source, "JPL" ))
 {
  if ( !jpl_eph )
  {
   jpl_eph = jpl_open( "DE405.unix" );
  }
  if ( jpl_eph)
   jpl_ephem_eval( jpl_eph, world->eph_id, et, pv );
 }
}


void orrery_close( void )
{
 jpl_close( jpl_eph );
}
