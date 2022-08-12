/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "nhlib.h"
#include <math.h>

#define XSIZE 361
#define YSIZE 181


MapData map_data_alloc( integer nx, integer ny )
{
 MapData data;
 integer i; 
 data = calloc( 1, sizeof( struct MapData_s ));
 data->naxes = 2;
 data->axes[0]= nx;
 data->axes[1]= ny;

 data->map = calloc( nx, UT_SZ_P );
 for ( i = 0; i < nx; i++ )
  data->map[i] = calloc( ny, UT_SZ_D );
 return data;
}



void nh_close_nrao( void* datap )
{
 MapData data = (MapData)datap;
 integer nx = data->axes[0];
 integer i;
 for ( i = 0; i < nx; i++ )
  free( data->map[i] );

 for ( i = 0; i < data->naxes; i++ )
 {
  free( data->axname[i] );
  free( data->projtype[i] );
 } 
 free( data->map );
 free( data );
}

void* nh_read_nrao( char* filename, char* opt )
{
/* Read the Dickey and Lockman (1990) data 
 * data is in galactic coordinates , with GLS projection
 */

 MapData data = NULL;
 integer i;
 
 double scale = 1.0e20;
 TextCard name;
 FioFits file;
 logical there;
 logical ok;



 TextCard axname;
 TextCard projtype;
 integer* axes;
 integer naxes;


 integer hduid;
 double longpole;
 TextCard ctype;

 printf( "Looking for %s\n", filename );
 there = utn_fio_inquire( filename ); /* idx_file */
 if ( !there ) return NULL;
 file = utn_fits_open_r( filename );
 if ( !file ) return NULL;
 hduid = utn_fits_next_hdu( file, 1 ); 
 axes = utn_fits_get_axes( file, &naxes );
 data = map_data_alloc( axes[0], axes[1] );
 ok = utn_fits_read_key_d( file, "LONGPOLE", &longpole ); 
 if ( !ok ) longpole = 180.0;
 for ( i = 0; i < data->naxes; i++ )
 {
  sprintf( name, "CTYPE%ld", i+1 );
  utn_fits_read_key_c( file, name, ctype, UT_CARD_SIZE );
  utn_fits_parse_ctype( ctype, axname, projtype );
  data->axname[i] = utn_cs_dup( axname );
  data->projtype[i] = utn_cs_dup( projtype );
  sprintf( name, "CRVAL%ld", i+1 );
  utn_fits_read_key_d( file, name, &data->crval[i] );
  sprintf( name, "CDELT%ld", i+1 );
  utn_fits_read_key_d( file, name, &data->cdelt[i] );
  sprintf( name, "CRPIX%ld", i+1 );
  utn_fits_read_key_d( file, name, &data->crpix[i] );
 }
 data->longpole = longpole;
 nrao_get_data( data->map, axes, scale, file );
 utn_fits_close( file );
 free( axes );
 return (void*)data;
}

void nrao_get_data( double** map, integer* axes, double scale, FioFits file )
{
 integer nvals;
 float* dbuf;
 integer i,j;
 float sval;
 integer offset;
 nvals = axes[0] * axes[1];
 dbuf = calloc( nvals, UT_SZ_R );
 utn_fits_img_getr( file, dbuf, nvals );
 for ( j = 0; j < axes[1]; j++ )
 {
  for ( i = 0; i < axes[0]; i++ )
  {
/* Replace by image call! */
   offset = j*axes[0]+i;
   sval = dbuf[offset];
   map[i][j] = sval / scale;
  }
 }
 free( dbuf );
}
double nrao_map_interp( double x, double y, integer nx, integer ny, double** map )
{
 integer i,j;
 double t,u;
 double val = 0;
 i = (integer)x;
 j = (integer)y;
 if ( i >= nx ) i = nx-1;
 if ( j >= ny ) j = ny-1;
 t = x - i;
 u = y - j;
 val = ( 1 - t ) * ( 1 - u ) * map[i-1][j-1]
     +   t   * ( 1 -u )      * map[i][j-1]
     +   t   *   u           * map[i][j]
     + (1 - t ) *   u         * map[i-1][j];
#if 0
 {

printf( "I J %ld %ld\n", i, j );
printf( "NRAO INTERP WTS %f %f  MAPS %f %f %f %f VAL %f\n",
  t, u, map[i-1][j-1], map[i][j-1], map[i][j], map[i-1][j], val );
 }
#endif
 return val;
}


double colden_eval_nrao( ColdenRecord* grid, void* data, double ra, double dec, double gl, double gb,
 double vmin, double vmax, char* codebuf )
{
 double nhval;
 nhval = nh_eval_nrao( gl, gb, data );
 utn_cs_copy( " ", codebuf );
 return nhval;
}

double nh_eval_nrao( double gl, double gb, MapData data )
{
 double nh;
 double xp[2];
 double xw[2];

 if ( utn_cs_is_blank( data->projtype[0] )) 
 {
  nh = 0.0;
 } else {
  xw[0] = gl;
  xw[1] = gb;
  wcs_world_to_pixel( xw, "GLS", data->crval, data->crpix, data->cdelt, data->longpole, xp );
  nh = nrao_map_interp( xp[0], xp[1], data->axes[0], data->axes[1], data->map );
 }
 return nh; 
}


