/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
#include <string.h>
#include <math.h>
static char* std_planet_names[9] =
 { "Mercury", "Venus", "Earth", "Mars",
   "Jupiter", "Saturn", "Uranus", "Neptune",
  "Pluto" };

World solar_system_std_world( GenList system, integer no )
{
 World world;
 world = solar_system_world( system, std_planet_names[no-1] );
 return world;
}


void world_list_els( World world, double t1, double t2, double dt )
{
 double els[6];
 double t;
 TextBuf buf;
 TextCard date;
 t = t1;
 while ( t <= t2 )
 {
  world_eval_els( world, t, els );
  cal_fmt_date( t, date, UT_TEXT_SIZE );
  sprintf( buf, "%-24s %20.2f %12.4f %12.8f %12.4f %8.4f %8.4f %8.4f", date, t, els[0], els[1], els[2], els[3], els[4], els[5] );
  utn_fio_msg( buf );
  t += dt;
 }

}


void world_eval_els( World world, double t, double* els )
{
 double dt = t - JD_J2000;
 double dc = dt / 36525.0;
 integer idc; 
 double fdc;
 double manom;
 double arcsec = 3600.0;

 frac_d( dc, &idc, &fdc );

 els[0] = world->a +  dc * world->rates[0];
 els[1] = world->e +  dc * world->rates[1];
 els[2] = world->i +  ( dc / arcsec ) * world->rates[2];
 els[3] = utn_circ_d( world->node + ( dc /arcsec ) * world->rates[3] );
 els[4] = utn_circ_d( world->aop  + ( dc /arcsec ) * world->rates[4] );
/* rates6 is revs per century */
 manom = world->manom + ( dc /arcsec ) * world->rates[5] + ( fdc * 360.0 )* world->rates[6];
 els[5] = utn_circ_d( manom ); 
}



void system_read_els( GenList system, char* filename )
{
 World world;
 FioFile in;
 TextBuf line;
 integer i;
 double wlon;
 integer n = 9;
 char* ptr;
 integer no = 0;
 logical part_one = UT_TRUE;
/* Read els */
 in = utn_fio_open_ar( filename );
 while( utn_fio_read_line( in, line, UT_TEXT_SIZE ))
 {
  if ( line[0] != '#' && !utn_cs_is_blank( line ))
  {
   ptr = line;
   world = solar_system_std_world( system, no+1 );
   world->epoch = JD_J2000;
   no++;
   if ( part_one )
   {
    world->a = utn_cs_get_d( &ptr );
    world->e = utn_cs_get_d( &ptr );
    world->i = utn_cs_get_d( &ptr );
    world->node = utn_cs_get_d( &ptr );
    world->aop = utn_cs_get_d( &ptr );
    wlon = utn_cs_get_d( &ptr );
    world->manom = utn_circ_d( wlon - world->node - world->aop );
    part_one = no < n;
    if ( !part_one ) no = 0;
   } else {
    for ( i =0; i < 7; i++) 
     world->rates[i] = utn_cs_get_d( &ptr );
   }

   
 
  }
  
 }
 utn_fio_file_close( in );



}
