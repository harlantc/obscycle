/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmfits.h"
#include <string.h>

FioFits utn_fits_alloc( fitsfile* fptr )
{
  FioFits file = calloc( 1, sizeof( struct FioFits_s ));
  file->fptr = fptr;
  file->hduid = 1;
  file->colno = 1;
  file->row = 1;
  file->status = 0;
  file->n = 0;
  file->nmax = 20;
  file->hdus = calloc( file->nmax, UT_SZ_I );
  return file;
}



FioFits utn_fits_open_r( char* filename )
{
 FioFits file = NULL;
 fitsfile* fptr;
 int status = 0;
 fits_open_file( &fptr, filename, READONLY, &status );
 if ( status == 0 )
 {
  file = utn_fits_alloc( fptr );
 }
 return file;
}

FioFits utn_fits_open_w( char* filename )
{
 FioFits file = NULL;
 fitsfile* fptr = NULL;
 int status = 0;


/* Clobber */
 if ( utn_fio_inquire( filename ))
  utn_sys_delete( filename );

 fits_create_file( &fptr, filename, &status );
 if ( status == 0 )
 {
  file = utn_fits_alloc( fptr );
 } else {
printf( "FITS STATUS %d %s\n", status, filename );
 }
 return file;
}

void utn_fits_close( FioFits file )
{
 int status = 0;
 fits_close_file( file->fptr, &status );
 free( file );
}


long utn_fits_next_hdu( FioFits file, long n )
{
 int status = 0;
 int hdutype = 0;
 file->hduid += n;
 fits_movrel_hdu( file->fptr, n, &hdutype, &status );
 file->row = 1;
 file->colno = 1;
 return file->hduid;
}

long utn_fits_get_nrows( FioFits file )
{
 long n = 0;
 int status = 0;
 fits_get_num_rows( file->fptr, &n, &status );
 return n;
}

long utn_fits_get_ncols( FioFits file )
{
 int n = 0;
 int status = 0;
 fits_get_num_cols( file->fptr, &n, &status );
 return (long)n;
}


/* Get a double from the next col */
double utn_fits_getd( FioFits file )
{
 double val = 0.0;
 int status = 0;
 int nelem = 1;
 double nulval = 0.0;

 fits_read_col_dbl( file->fptr, file->colno, file->row, 1, nelem, nulval,
   &val, NULL, &status   );
 file->colno++;
 return val;
}

integer utn_fits_geti( FioFits file )
{
 long val = 0;
 int status = 0;
 int nelem = 1;
 long nulval = 0;

 fits_read_col_lng( file->fptr, file->colno, file->row, 1, nelem, nulval,
   &val, NULL, &status   );
 file->colno++;
 return (integer)val;
}

float utn_fits_getr( FioFits file )
{
 float val = 0.0;
 int status = 0;
 int nelem = 1;
 float nulval = 0.0;

 fits_read_col_flt( file->fptr, file->colno, file->row, 1, nelem, nulval,
   &val, NULL, &status   );
 file->colno++;
 return val;
}

logical utn_fits_img_gets( FioFits file, short* data, integer nvals )
{
 long group = 1;
 int status = 0;
 long firstelem = 1;
 short nulval = 0;
 fits_read_img_sht( file->fptr, group, firstelem, nvals, nulval, data, NULL, &status );
 return ( status == 0 );
}

logical utn_fits_img_getr( FioFits file, float* data, integer nvals )
{
 long group = 1;
 int status = 0;
 long firstelem = 1;
 float nulval = 0;
 fits_read_img_flt( file->fptr, group, firstelem, nvals, nulval, data, NULL, &status );
 return ( status == 0 );
}


short utn_fits_gets( FioFits file )
{
 short val = 0;
 int status = 0;
 int nelem = 1;
 short nulval = 0.0;

 fits_read_col_sht( file->fptr, file->colno, file->row, 1, nelem, nulval,
   &val, NULL, &status   );
 file->status = status;
 file->colno++;
 return val;
}

void utn_fits_get_array_r( FioFits file, float* data, integer n )
{
 int nelem = n;
 int status = 0;
 float nulval = 0.0;
 fits_read_col_flt( file->fptr, file->colno, file->row, 1, nelem, nulval,
   data, NULL, &status   );
 file->status = status;
 file->colno++;
}

integer* utn_fits_get_axes( FioFits file, integer* naxes )
{
 int status = 0;
 integer* axes = NULL; 
 TextCard name;
 integer i;
 fits_read_key_lng( file->fptr, "NAXIS", (long*)naxes, NULL, &status ); 
 axes = calloc( *naxes, UT_SZ_I );
 for ( i = 0; i < *naxes; i++ )
 {
  sprintf( name, "NAXIS%ld", i+1 );
  fits_read_key_lng( file->fptr, name, &axes[i], NULL, &status );
  file->status = status;
 }
 return axes;
}


void utn_fits_set_row( FioFits file, integer row )
{
 file->row = row;
 file->colno = 1;
}

void utn_fits_next_row( FioFits file, integer row )
{
 file->row += row;
 file->colno = 1;
}


logical utn_fits_read_key_d( FioFits file, char* name, double* value )
{
 logical ok;
 int status = 0;
 fits_read_key_dbl( file->fptr, name, value, NULL, &status );
 file->status = status;
 ok = ( status == 0 );
 return ok;

}


integer utn_fits_get_status( FioFits file )
{ 
 integer status;
 status = file->status;
 return status;
}

logical utn_fits_read_key_c( FioFits file, char* name, char* value, long maxlen )
{
 logical ok;
 int status = 0;
 char* valp;
 fits_read_key_longstr( file->fptr, name, &valp, NULL, &status );
 utn_cs_copy_siz( valp, value, maxlen );
 free( valp );
 ok = ( status == 0 );
 return ok;


}


void utn_fits_parse_ctype( char* ctype, char* axname, char* projtype )
{
 integer n;
 n = 4;
 while ( *(ctype+n-1) == '-' ) 
  n--;
 utn_cs_copy_siz( ctype, axname, n );
 n = 4; 
 while ( *(ctype+n) == '-' ) 
  n++;
 utn_cs_copy( ctype + n, projtype );
}


void utn_fits_create_table( FioFits file, char* hduname, logical ascii  )
{
 integer type  = ascii ? UTN_FITS_ATABLE: UTN_FITS_BTABLE;
 if ( ascii )
 {
  fits_insert_atbl( file->fptr, 0, 0, 0, NULL, NULL, NULL, NULL, hduname, &file->status );
 } else {
  fits_insert_btbl( file->fptr, 0, 0, NULL, NULL, NULL, hduname, 0, &file->status );
 }
 utn_fits_add_hdu( file, type );
}

void utn_fits_add_hdu( FioFits file, integer type )
{
 if ( file->n >= file->nmax )
 {
  file->nmax = 2 * file->n + 100;
  file->hdus = realloc( file->hdus, file->nmax * UT_SZ_I );
 }
 file->hdus[file->n] = type;
 file->n++;
 file->row = 1;
 file->colno = 1;
 file->ncols = 0;
 file->hduid = file->n;
}

void utn_fits_create_null_pri( FioFits file )
{
 long naxes = 0;           /* Default image parameters */
 long axes[2] = { 0, 0 };
 double bscale = 1.0;
 double bzero = 0.0;

 DataType imdatatype = DT_INT2;  /* DM data type code */
 int bitpix;   /* FITS data type code */

 bitpix = utn_fits_bitpix( imdatatype, &bscale, &bzero );
 fits_create_img( file->fptr, bitpix, naxes, axes, &file->status ); /* CFITSIO */
}


integer utn_fits_bitpix( DataType type, double* bscale, double* bzero )
{
  integer bpvals[] = { -64, -32, 8,   16, 32, 16, 32 };
  DataType types[] = { DT_REAL8, DT_REAL4, DT_UINT1, DT_INT2, DT_INT4, DT_UINT2, DT_UINT4 };
 integer n = 7;
 integer i;
 integer bitpix = 16; /* Default */ 
 double bzeros[] = { 0.0, 0.0, 0.0, 0.0, 0.0, FITS_UINT2_OFFSET, FITS_UINT4_OFFSET }; 

 *bzero = 0.0;
 *bscale = 1.0;  /* Always */


 for ( i = 0; i < n; i++ )
 {
   if ( types[i] == type )
   {
     bitpix = bpvals[i];
     *bzero = bzeros[i];
     break;
   }
  }
  return bitpix;
} 

integer utn_parse_bitpix( integer bitpix, double bscale, double bzero )
{
  integer bpvals[] = { -64, -32, 8,   16, 32, 16, 32 };
  DataType types[] = { DT_REAL8, DT_REAL4, DT_UINT1, DT_UINT2, DT_UINT4, DT_INT2, DT_INT4 };
  double bzeros[] = { 0.0, 0.0, 0.0, 0.0, 0.0, FITS_UINT2_OFFSET, FITS_UINT4_OFFSET, 0.0, 0.0 };
  integer n = 7;
  integer i;
  logical qz;

 for ( i = 0; i < n; i++ )
 {
/* We test UINT before INT to catch the special case */
   qz = ( bzeros[i] == bzero && bscale == 1.0 );
   if ( bitpix == bpvals[i] )
   {
    if ( qz || bzeros[i] == 0.0 )    /* Requre qz if we are in a special case, otherwise anything goes */
     return types[i];
   }
 }
 return DT_UNK;
}


void fits_compose_bt_tform( DataType type, integer isize, integer length, logical variable, char* tform, integer maxlen )
{
 char multiplier[UT_CARD_SIZE]="";
 char letter= ' ';
 TextCard mbuf;
 char suffix[UT_CARD_SIZE] ="";
 long size = isize;
 long type_id;
 long ntypes= 10;  
 long types[] = { DT_BIT, DT_UINT1, DT_INT2, DT_INT4, DT_REAL4, DT_REAL8, DT_LOGICAL, DT_UINT2, DT_UINT4, DT_CHAR };
 char letters[] = "XBIJEDLIJA";
/* Force scalars to be of size 1 */
 if ( !variable && size == 0 ) size = 1;
 
 if ( type == DT_BIT )
  snprintf( multiplier, UT_CARD_SIZE, "%ld", length );
 else if ( variable )
  utn_cs_copy( "1P", multiplier );
 else if ( type == DT_CHAR )
 {
  snprintf( multiplier, UT_CARD_SIZE, "%ld", length * size );
  if ( size > 1 )
   snprintf( suffix, UT_CARD_SIZE, "%ld", length );
 }
 else
  snprintf( multiplier, UT_CARD_SIZE, "%ld", size );
 
 type_id = utn_ar_match_i( (long)type, types, ntypes );
 if ( type_id > 0 )
  letter = letters[type_id-1];
 else
  utn_cs_copy_siz( "Bad datatype in TFORM", mbuf, UT_CARD_SIZE ); /* not implemented */

 snprintf( tform, maxlen, "%s%c%s", multiplier, letter, suffix );
 if( variable && size > 0 )
 {
  snprintf( suffix, UT_CARD_SIZE, "(%ld)", size );
  strcat( tform, suffix );
 }

}


void utn_fits_add_simple_column( FioFits file, char* name, DataType type )
{
 utn_fits_add_column( file, name, type, 1L, 1L );
}

integer utn_fits_add_column( FioFits file, char* name, DataType type, integer size, integer length )
{
 integer no;
 TextWord tform;
 logical var = UT_FALSE; 
 no = ++file->ncols;
 fits_compose_bt_tform( type, size, length, var, tform, UT_WORD_SIZE );
 fits_insert_col( file->fptr, no, name, tform, &file->status );
 return no;
}

void utn_fits_key_write_i( FioFits file, char* name, integer value, char* desc )
{
 utn_fits_key_write( file, name, DT_INT4, (void*)&value, NULL, desc );
}

void utn_fits_key_write_q( FioFits file, char* name, logical value, char* desc )
{
 utn_fits_key_write( file, name, DT_LOGICAL, (void*)&value, NULL, desc );
}

void utn_fits_key_write_c( FioFits file, char* name, char* value, char* desc )
{
 utn_fits_key_write( file, name, DT_CHAR, (void*)&value, NULL, desc );
}

/* Don't write if blank */
void utn_fits_key_write_cq( FioFits file, char* name, char* value, char* desc )
{
 if ( name && !utn_cs_is_blank( value ))
  utn_fits_key_write( file, name, DT_CHAR, (void*)&value, NULL, desc );
}

void utn_fits_key_write_d( FioFits file, char* name, double value, char* unit, char* desc )
{
 utn_fits_key_write( file, name, DT_REAL8, (void*)&value, unit, desc );
}

void utn_fits_key_write_dq( FioFits file, char* name, double value, char* unit, char* desc )
{
 if ( name  && !utn_qnan_d( value ))
  utn_fits_key_write( file, name, DT_REAL8, (void*)&value, unit, desc );
}

void utn_fits_key_write( FioFits file, char* name, DataType type, void* value, char* unit, char* desc )
{

  TextCard comment;
  integer lval;
  float rval;
  int qval;
  double dval;
  char* cval;
  integer maxlen = UT_CARD_SIZE;
  integer fkt = 0;
  char* ptr = comment;

  if ( !utn_cs_is_blank( unit ))
  {
   ptr += sprintf( ptr, "[%s] ", unit );
   maxlen -= ( ptr - comment );
  }
  utn_cs_copy_siz( desc, ptr, maxlen );
  fkt = FKT_INTEGER;

  switch( type )
  {
   case DT_INT4:
    lval = *(long*)value;
    break;
   case DT_INT2:
    lval = (long)*(short*)value;
    break;
   case DT_UINT1:
    lval = (long)*(unsigned char*)value;
    break;
   case DT_UINT2:
    lval = *(unsigned short*)value;
    break;
   case DT_UINT4:
    dval = (double)*(unsigned long*)value;
    fkt = FKT_UREAL;
    break;
   case DT_REAL4:
    rval = *(float*)value;
    fkt = FKT_REAL;
    break;
   case DT_REAL8:
    dval = *(double*)value;
    fkt = FKT_DREAL;
    break;
   case DT_LOGICAL:
    qval = *(int*)value;
    fkt = FKT_LOGICAL;
    break;
   case DT_CHAR:
    cval = *(char**)value;
    fkt = FKT_CHAR;
    break;
   default:
    break;
  }


  if ( fkt == FKT_INTEGER )
    fits_write_key_lng( file->fptr, name, lval, comment, &file->status );
  else if ( fkt == FKT_REAL )
    fits_write_key_flt( file->fptr, name, rval, FKEY_R4_PREC, comment, &file->status );
  else if ( fkt == FKT_DREAL )
  {

    fits_write_key_dbl( file->fptr, name, dval, FKEY_R8_PREC, comment, &file->status );
  }
  else if ( fkt == FKT_UREAL )
    fits_write_key_fixdbl( file->fptr, name, dval,0,  comment, &file->status );
  else if ( fkt == FKT_LOGICAL )
    fits_write_key_log( file->fptr, name, qval, comment, &file->status );
  else if ( fkt == FKT_CHAR )
  {
    fits_write_key_longwarn( file->fptr, &file->status );
    if ( utn_cs_is_blank( cval ))
     fits_write_key_str( file->fptr, name, " ", comment, &file->status );
    else
     fits_write_key_longstr( file->fptr, name, cval, comment, &file->status );
  }




}



void utn_fits_write_array_d( FioFits file, integer col, double* data, integer n )
{
 fits_write_col_dbl( file->fptr, col, file->row, 1, n, data, &file->status );
}
void utn_fits_write_array_i( FioFits file, integer col, integer* data, integer n )
{
 fits_write_col_lng( file->fptr, col, file->row, 1, n, data, &file->status );
}


void utn_fits_flush_buf( FioFits file )
{
 fits_flush_buffer( file->fptr, FALSE, &file->status );
}
