/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
#include <string.h>
#include "kepriv.h"
#include <math.h>


/* Wrapper for per-world-registered orientation function */

logical world_orient( World world, double t, double* ex, double* ey, double* ez )
{
 logical q = UT_FALSE;
 if ( world->orient )
 {
  q = UT_TRUE;
  world->orient( world, t, ex, ey, ez );
 } else {
  ex[0] = 1;  ey[0] = 0;  ez[0] = 0;
  ex[1] = 0;  ey[1] = 1;  ez[1] = 0;
  ex[2] = 0;  ey[2] = 0;  ez[2] = 1;
 }
 return q;
}

logical world_state( World world, double t, integer frame, double* pos, double* vel )
{
 logical q = UT_FALSE;
 if ( world->state )
 {
  q = UT_TRUE;
  world->state( world, t, frame, pos, vel );
 } else {
  utn_ar_vzero_d( pos );
  utn_ar_vzero_d( vel );
 }
 return q;


}


/* EarthPtr is a static pointer to cache the Earth data */
World solar_system_earth( GenList system )
{
 if ( !EarthPtr )
 {
  if ( system )
   EarthPtr = solar_system_world( system, "Earth" );
  else
  {
   EarthPtr = &EarthData;
   world_init_default( EarthPtr, "Earth" );
  }
 }
 return EarthPtr;
}

World solar_system_world( GenList system, char* namep )
{
 World world;
 integer id;
 char* ptr;
 TextBuf name;
 TextCard tmp;
 utn_cs_copy( namep, name );

/* Special case */
 if( utn_cs_uceq( name, "Moon" ))
  utn_cs_copy( "Luna", name );

 for ( id = 0; id < system->n; id++ )
 {
  world = (World)utn_genlist_entry( system, id );
  if ( utn_cs_uceq( world->name, name ) )
   return world;
  if ( world->name[0] == '(' ) 
  {
/* Asteroid */
   ptr = world->name;
   utn_cs_get_c( &ptr, tmp, CARD_SIZE );
   if ( utn_cs_uceq( ptr, name ))
    return world;
  }
 }
 return NULL;
}



World solar_system_world_tag( GenList system, char* name )
{
 World world;
 integer id;

 for ( id = 0; id < system->n; id++ )
 {
  world = (World)utn_genlist_entry( system, id );
  if ( utn_cs_uceq( world->idname, name ) )
   return world;
 }
 return NULL;
}



/*
 *  Initialize the default (Earth);
 */
World world_earth( void )
{
 return solar_system_earth( NULL );
}

/*
 *  List properties of all known primaries
 */


void solar_system_summary( GenList system )
{
 printf( "System: %ld objects\n", system->n );
/* Expand with categories */
}

integer solar_system_n( GenList system )
{
 return system->n;
}

World solar_system_world_no( GenList system, integer no )
{
 return ((World)utn_genlist_entry( system, no-1 ));
}

void solar_system_list( GenList system )
{
 integer i;
 World world;

 for ( i =0; i < system->n; i++ )
 {
  world = (World)utn_genlist_entry( system, i );
  printf( "%-20s %-20s %f %f\n", world->name, world->alt_name, world->mass, world->rotation_period );
 }
}


/* Used to reinitialize derived quantites in a World object */
void world_init( World primary )
{
 double precess_rad;
 double period;
 double radius;
 radius = primary->radius;

 if ( utn_cs_eq( primary->name, "Earth"))
  primary->idmask |= WORLD_IS_EARTH;

 if ( utn_cs_eq( primary->name, "Luna"))
  primary->orient = luna_orient;

 primary->k3_power = 2.0 / 3.0;
 primary->precess_power = -7.0 / 3.0;
 period =  primary->rotation_period;

/* Use Kepler 3 to calculate surface skimming orbital period in seconds */
 primary->surface_period   =  sqrt( ( 4 * M_PI * M_PI / primary->GM ) * ipow_d( radius, 3 ) );
/* In revs per day */
 primary->stationary_motion = DL_SEC_D / primary->rotation_period;
/* Use Kepler 3 to calculate radius of stationary orbit */

 primary->stationary_radius = radius * pow( period / primary->surface_period, primary->k3_power );

 precess_rad = 3 * M_PI * primary->J2 * pow( primary->surface_period, 4.0 / 3.0 );
/* In degrees per day for surface skimming orbit */
 primary->precess_const = radout_d( precess_rad ) * DL_SEC_D;

}



/*
 * =============================================
 * Precomputed solar system values
 * =============================================
 */
/*
 * Initialize workd as one of the default precomputed names
 * Shouldnt need this now
 */
integer world_init_default( World primary, char* name )
{
 integer id;

 id = utn_ar_cmatch_c( name, Planet_names, Planet_n );
 if ( id >= 0 )
  world_set( primary, name, Sun_GM / Planet_GM[id], Planet_radius[id], Planet_period[id], Planet_J2[id] );
 return id;
}


/* When you don't have the System file, initialize hard coded values */
GenList solar_system_default( void )
{
 GenList system;
 World world;
 long id;
 system = utn_genlist_alloc( "System", Planet_n, sizeof( struct World_s ));
 for ( id = 0; id < Planet_n; id++ )
 { 
  world = (World)utn_genlist_add( system );
  world_set( world, Planet_names[id], Sun_GM / Planet_GM[id], Planet_radius[id], Planet_period[id], Planet_J2[id] );
 }
 return system;
}


