/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2012)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* 7/19/2012 - fix bug #13328 for running colden with a file */

#include "nhlib.h"
#include <math.h>

integer nh_bell_set_code( ColdenRecord* record, integer code )
{
 logical eq_flag;
 TextCard buf;
 TextCard posbuf;
 double dec = record->dec;

 eq_flag = dec > -0.2 && dec <  0.2;
 if ( eq_flag ) 
  code = NH_CODE_EQ;
 if ( !utn_dbg_level(1)) return code;

 utn_fio_dashline( 80 );
 utn_cs_copy( " ", buf );
 coord_write( record->ra, record->dec, posbuf );
 sprintf( buf, "Seq: %5ld   %24s Integration (s): %6.0f  NH20(Tot)=%6.2f",
   record->seq, posbuf, record->t, record->nh );
 utn_fio_msg( buf );
 if ( eq_flag )
   utn_fio_msg( "Warning: Points near declination 0 may suffer from large gain fluctuations" );
 return code;
}


void nh_bell_free( void* datap )
{
 BellData data = (BellData)datap;
 if ( data->cube )
  utn_fits_close( data->cube );  

 free( data->ra );
 free( data->dec );
 free( data->tint );
 free( data->nh );
 free( data->seq );
 free( data->ptr );
 free( data->index );
 free( data->record );
 free( data );
}

BellData nh_bell_alloc( integer n )
{
 BellData data;
 BellIndex index;
 data = calloc( 1, sizeof( struct BellData_s ));
 data->ra = calloc( n, UT_SZ_D );
 data->dec = calloc( n, UT_SZ_D );
 data->tint= calloc( n, UT_SZ_D );
 data->nh = calloc( n, UT_SZ_D );
 data->seq = calloc( n, UT_SZ_D );
 data->ptr = calloc( n, UT_SZ_D );
 data->n = n;
 index = calloc( 1, sizeof( struct BellIndex_s ));
 index->nbins = BELL_NBINS;
 data->index = index;
 data->ra_bin_size = 10.0;
 data->north_cap = 80.0;
 data->north_cap_bin = 36;
 data->south_boundary = -40.0;
 data->search_radius = 4.0;
 data->record = calloc( 1, sizeof( struct BellRecord_s ));
 return data;
}


void nh_bell_find_nearest( ColdenRecord* grid, BellData bell, double targ_ra, double targ_dec,
 double ra_scale, integer binno )
{
 double dec_tol = 5.0;
 ColdenRecord* record;
 double ra_tol;
 integer r1,r2;
 integer i;
 integer direct;
 double ra_i,dec_i;
 double rdist;
 BellIndex index = bell->index;
 ra_tol = dec_tol / ra_scale;
 r1 = index->ptr[binno];
 r2 = index->top[binno];
 for ( i = r1; i <= r2; i++ )
 {
  ra_i = bell->ra[i];
  dec_i = bell->dec[i];  

  if ( sph_close( targ_ra, targ_dec, ra_i, dec_i, ra_tol, dec_tol ))
  {
   rdist = sph_nbr( targ_ra, targ_dec, ra_i, dec_i, &direct );
   record = &grid[direct];
   if ( record->seq == 0 || rdist < record->dist )
   {
    record->seq = bell->seq[i];
    record->ptr = bell->ptr[i];
    record->t = bell->ptr[i];
    record->nh = bell->nh[i];
    record->ra = bell->ra[i];
    record->dec = bell->dec[i];
    record->dist = rdist;
    if ( direct == DIRECT_OO ) return;            
   }
  }
 }
}

void nh_bell_search_index( ColdenRecord* grid, BellData bell, double targ_ra, double targ_dec )
{
 integer bin;
 integer ra_bin;
 integer targ_bin;
 double ra_scale;
 BellIndex index = bell->index;
 integer search_bins;

 integer search_bins_max;
 double ra_scale_tol = 0.001;
 if( targ_dec - bell->search_radius < bell->north_cap )
 {
  ra_scale = dcosd( targ_dec );
  if ( ra_scale < ra_scale_tol )
   ra_scale = ra_scale_tol;
  search_bins = ( bell->search_radius / bell->ra_bin_size ) / ra_scale;
  search_bins_max = index->nbins/2;
  if ( search_bins > search_bins_max ) search_bins = search_bins_max;
  if ( search_bins < 2 ) search_bins = 2;
  targ_bin = (integer)( targ_ra / bell->ra_bin_size );
  for( ra_bin = targ_bin - search_bins; ra_bin <= targ_bin + search_bins; ra_bin++ )
  {
   bin = ra_bin;
   if ( bin < 0 ) bin += index->nbins;
   if ( bin > index->nbins ) bin -= index->nbins;
   nh_bell_find_nearest( grid, bell, targ_ra, targ_dec, ra_scale, bin );
  }

 }
 if ( targ_dec + bell->search_radius > bell->north_cap )
 {
  ra_scale = 0.001;
  nh_bell_find_nearest( grid, bell, targ_ra, targ_dec, ra_scale, bell->north_cap_bin );
 }
}

void grid_clear( ColdenRecord* grid );

double colden_eval_bell( ColdenRecord* grid, void* data, double ra, double dec, double gl, double gb,
 double vmin, double vmax, char* codebuf )
{
 double nh;
 grid_clear( grid );
 nh = nh_eval_bell( grid, data, ra, dec, vmin, vmax, codebuf );
 return nh;
}


void grid_clear( ColdenRecord* grid )
{
 integer i;
 ColdenRecord* record;
 for ( i = 0; i < COLDEN_NGRID; i++ )
 {
  record = &grid[i];
  record->seq = 0;
  record->ptr = 0;
  record->ra = 0;
  record->dec = 0;
  record->t = 0.0;
  record->nh = 0.0;
  record->dist = 0.0;
 }
}

double nh_eval_bell( ColdenRecord* grid, BellData bell, double ra, double dec, double vmin, double vmax, char* codebuf )
{
 integer code;
 double nhval = 0.0;
 double targ_ra, targ_dec;
 integer i;
 logical north;
 ColdenRecord* record;
 targ_ra = ra;
 targ_dec = dec;
 north = targ_dec > bell->south_boundary;
 if ( north ) {
  nh_bell_search_index( grid, bell, targ_ra, targ_dec );
  code = 0;
  record = &grid[0];
  if (record->seq > 0 ) 
  {
   utn_fio_tmsg( "Exact match" );
   code = 1;
   code  = nh_bell_set_code( record, code );
   record->nh = nh_bell_integrate( bell, record->ptr, vmin, vmax );
  } else {
/* 4 nearest neighbours */
   for ( i = 1; i <= 4; i++ )
   {
    record = &grid[i];
    code = 0;
    code = nh_bell_set_code( record, code );
    record->nh = nh_bell_integrate( bell, record->ptr, vmin, vmax );
   }
   nhval = nhbase_interp( grid, targ_ra, targ_dec, &code );
  }
 } else { /* South */
   code = 4;
   nhval = utn_nan_d();
 }


 nh_bell_get_code( code, codebuf ); 
 return nhval;
}

logical nh_bell_get_rec( BellData bell, integer irec, BellRecord bellrec )
{
 logical ok = UT_TRUE;
 FioFits file = bell->cube;
 int status;
 utn_fits_set_row( file, irec );
 bellrec->seq = utn_fits_gets( file );
 status = utn_fits_get_status( file );
 bellrec->gl = utn_fits_getr( file );
 bellrec->gb = utn_fits_getr( file );
 bellrec->cchan = utn_fits_gets( file );
 bellrec->lsr   = utn_fits_getr( file );
 if ( status )
  printf( "Error status %d on rec %ld\n", status, irec );

 utn_fits_get_array_r( file, bellrec->temp, BELL_NTEMP );
 return ok;
}

double nh_bell_integrate( BellData bell, integer ptr, double vmin, double vmax )
{
 integer db = 4;
 integer irec;
 double nhval = 0;
 logical ok;
/* badcount and hitemp may be nonzero near the galactic plane */
 integer badcount;  /* number of channels above 1E21 */
 integer hitemp;    /* Number of channels with T>40 */
 double ch_v1 = 0.0;
 double ch_v2 = 0.0;
 logical first = UT_TRUE;
 double dv_chan = 5.2765;   /* Velocity width of channel */
 double nh_norm = 0.096824;   /* conversion 1E20 cm**-2/K */
 double badval= 10.0;
 double hival = 40.0;
 double ch_v, ch_nh, ch_temp;
 integer chan;
 BellRecord record; 
 irec = ptr + 1;
 record = bell->record;
 ok = nh_bell_get_rec( bell, irec, record );
 if ( !ok ) 
 {
  printf( "Problem reading NH data cube\n" );
  return nhval;
 }
 badcount = 0;
 hitemp = 0;
if ( utn_dbg_level(db))
{
printf( " ----------- \n" );
}
 for ( chan = 1; chan <= BELL_NTEMP; chan++ )
 {
  ch_v = record->lsr + ( chan - record->cchan ) * dv_chan;
  ch_temp = record->temp[chan-1];
  ch_nh = nh_norm * ch_temp;
  if ( ch_v >= vmin && ch_v <= vmax )
  {
   ch_v2 = ch_v;
   if ( first )
   {
    ch_v1 = ch_v;
    first = UT_FALSE;
   }
   nhval += ch_nh;
if ( utn_dbg_level(db))
{
printf( "I V CNH NH: %6ld %10.3f %12.4f %12.4f\n", chan, ch_v, ch_nh, nhval );
}
   if( ch_nh > badval ) badcount++;
   if( ch_temp > hival ) hitemp++;
  }
 }
 if ( utn_dbg_level( 1 )) 
 {
  TextBuf mbuf;
  sprintf( mbuf, "Vel range %6.0f to %6.0f Central vel: %6.0f and temp: %9.5f",
   ch_v1, ch_v2, record->lsr, record->temp[record->cchan] );
  utn_fio_msg( mbuf );
  sprintf( mbuf, "Integrated hydrogen density %15.4E", nhval );
  utn_fio_msg( mbuf );
  if ( badcount > 0 )
  {
   sprintf( mbuf, "Warning: %ld channels had NH > 1E21 (Tau > 0.1)", badcount );
   utn_fio_msg( mbuf );
  }
  if( hitemp > 0 )
  {
   sprintf( mbuf, "Warning: %ld channels had T-ANTENNA > 40K", hitemp );
   utn_fio_msg( mbuf );
  }
 }
 return nhval;
}

void* nh_read_bell( char* idx_file, char* cube_file )
{
 long hduid;
 logical there;
 long nrows,ncols;
 long row;
 integer bin;
 integer old_bin = -1;

 BellData bell;
 BellIndex index;
 FioFits file = NULL;
 there = utn_fio_inquire( idx_file );
 if ( there )
 {
  file = utn_fits_open_r( idx_file );
 } else {
  printf( "Failed to find file %s\n", idx_file );
  return NULL;
 }
/* Skip null header */
 hduid = utn_fits_next_hdu( file, 1 ); 
 nrows = utn_fits_get_nrows( file );
 ncols = utn_fits_get_ncols( file );
 bell = nh_bell_alloc( nrows );
 index = bell->index;
 for ( row = 0; row < nrows; row++ )
 {
  bell->ra[row] =   utn_fits_getd( file );
  bell->dec[row] =  utn_fits_getd( file );
  bell->tint[row] =  utn_fits_getd( file );
  bell->nh[row] =  utn_fits_getd( file );
  bell->seq[row] =  utn_fits_geti( file );
  bell->ptr[row] =  utn_fits_geti( file );
  utn_fits_next_row( file, 1 );
  bin = bell->ra[row] / bell->ra_bin_size;
  if ( bell->dec[row] > bell->north_cap ) 
   bin = bell->north_cap_bin;
  if ( bin > old_bin )
  {
   index->ptr[bin] = row+1;

  }
  if ( old_bin >= 0 ) 
   index->top[old_bin] = row;
  else if ( bin < old_bin )
  {
   printf( "Bad bin order %ld %ld %ld %f %f\n", 
    old_bin, bin, row, bell->ra[row], bell->dec[row] );
  }
  old_bin = bin;
 }
 index->top[bell->north_cap_bin] = nrows;
 utn_fits_close( file );
 bell->cube = utn_fits_open_r( cube_file );
 if ( bell->cube )
 {
  hduid = utn_fits_next_hdu( bell->cube, 1 ); 
 } else {
  printf( "Failed to open data cube %s\n", cube_file ); 
 }
 return (void*)bell;
}

void nh_bell_get_code( integer code, char* code_message )
{
 char* wflags[] = { "(Interpolated)", "(At target)", "(At closest point)",
      "(Gain uncertain)", "(Too far south)" };

 if ( code >= 0 && code <= 4 )
  utn_cs_copy( wflags[code], code_message );
 else
  utn_cs_copy( " ", code_message );
}


