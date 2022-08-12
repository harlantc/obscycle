/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef JCMFITS_H
#define JCMFITS_H
#include "jcmutils.h"
#include "fitsio.h"

#define UTN_FITS_UNK   0
#define UTN_FITS_IMAGE 1
#define UTN_FITS_BTABLE 2
#define UTN_FITS_ATABLE 3

#define FITS_UINT2_OFFSET 32768.0
#define FITS_UINT4_OFFSET 2147483648.0

#define FKEY_R4_PREC 7
#define FKEY_R8_PREC 13

#define FKT_INTEGER 0
#define FKT_REAL    1
#define FKT_DREAL   2
#define FKT_CHAR    3
#define FKT_LOGICAL 4
#define FKT_UREAL   5


typedef struct FioFits_s {
 fitsfile* fptr;
 long hduid;
 long colno;
 integer ncols;
 long row;
 int status;
/* Per file data */
 integer n;
 integer nmax;
 integer* hdus;
} *FioFits;

FioFits utn_fits_open_w( char* filename );
integer utn_fits_get_status( FioFits file );
FioFits utn_fits_open_r( char* filename );
void utn_fits_close( FioFits file );
long utn_fits_get_nrows( FioFits file );
long utn_fits_next_hdu( FioFits file, long n );
long utn_fits_get_ncols( FioFits file );
short utn_fits_gets( FioFits file );
float utn_fits_getr( FioFits file );
void utn_fits_get_array_r( FioFits file, float* data, integer n );
double utn_fits_getd( FioFits fits );
integer utn_fits_geti( FioFits fits );
void utn_fits_set_row( FioFits file, integer row );
void utn_fits_next_row( FioFits file, integer row );
logical utn_fits_read_key_c( FioFits file, char* name, char* value, long maxlen );
logical utn_fits_read_key_d( FioFits file, char* name, double* value );
void utn_fits_parse_ctype( char* ctype, char* axname, char* projtype );
integer* utn_fits_get_axes( FioFits file, integer* naxes );
logical utn_fits_img_gets( FioFits file, short* data, integer nvals );
logical utn_fits_img_getr( FioFits file, float* data, integer nvals );
void utn_fits_create_null_pri( FioFits file );
void utn_fits_key_write( FioFits file, char* name, DataType type, void* value, char* unit, char* desc );
void utn_fits_key_write_d( FioFits file, char* name, double value, char* unit, char* desc );
void utn_fits_key_write_i( FioFits file, char* name, integer value, char* desc );
void utn_fits_key_write_c( FioFits file, char* name, char* value, char* desc );
void utn_fits_key_write_q( FioFits file, char* name, logical value, char* desc );
void utn_fits_key_write_cq( FioFits file, char* name, char* value, char* desc );

integer utn_parse_bitpix( integer bitpix, double bscale, double bzero ); 
integer utn_fits_bitpix( DataType type, double* bscale, double* bzero );
void fits_compose_bt_tform( DataType type, integer size, integer length, logical variable, char* tform, integer maxlen );
integer utn_fits_add_column( FioFits file, char* name, DataType type, integer size, integer length );
void utn_fits_add_simple_column( FioFits file, char* name, DataType type );
void utn_fits_write_array_d( FioFits file, integer col, double* data, integer n );
void utn_fits_write_array_i( FioFits file, integer col, integer* data, integer n );

FioFits utn_fits_alloc( fitsfile* );
void utn_fits_add_hdu( FioFits file, integer type );
void utn_fits_create_table( FioFits file, char* hduname, logical ascii  );
void utn_fits_key_write_dq( FioFits file, char* name, double value, char* unit, char* desc );
void utn_fits_flush_buf( FioFits file );
#endif

