/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fio.h"
logical utn_fio_write_sio_siz( FioFile file, void* buf, long n, long* nw, size_t siz, size_t fsiz );
logical utn_fio_read_sio_siz( FioFile file, void* buf, long n, long* nw, size_t siz, size_t fsiz );

logical utn_fio_write_q( FioFile file, logical* buf, long n, long* nw )
{
 return( utn_fio_write_sio( file, (void *)buf, n, nw, UT_SZ_Q ) );
}

logical utn_fio_write_d( FioFile file, double* buf, long n, long* nw )
{
 return( utn_fio_write_sio( file, (void *)buf, n, nw, UT_SZ_D ) );
}

logical utn_fio_write_r( FioFile file, float* buf, long n, long* nw )
{
 return( utn_fio_write_sio( file, (void *)buf, n, nw, UT_SZ_R ) );
}

logical utn_fio_write_i( FioFile file, long* buf, long n, long* nw )
{
 return( utn_fio_write_sio_siz( file, (void *)buf, n, nw, UT_SZ_I, DT_SZ_I ) );
}

logical utn_fio_write_s( FioFile file, short* buf, long n, long* nw )
{
 return( utn_fio_write_sio( file, (void *)buf, n, nw, UT_SZ_S ) );
}

logical utn_fio_write_c( FioFile file, char* buf, long n, long* nw )
{
 return( utn_fio_write_sio( file, (void *)buf, n, nw, UT_SZ_C ) );
}


logical utn_fio_write_sio( FioFile file, void* buf, long n, long* nw, size_t siz )
{
 FILE* fptr;
 long m;
 logical status;

 fptr = utn_fio_file_get_fptr( file );
 m = fwrite( (void *)buf, siz, n, fptr );
 status = (m == n );
 utn_fio_file_set_status( file, status );
 utn_fio_delta_pos( file, m * siz );
 *nw = m;
 return( status );
}

/* Casting */
logical utn_fio_write_sio_siz( FioFile file, void* buf, long n, long* nw, size_t siz, size_t fsiz )
{
 FILE* fptr;
 long m;
 logical status;
 integer i;
 char* ptr;
 integer4 tmp;
 m = 0;
 fptr = utn_fio_file_get_fptr( file );
 if ( siz != fsiz )
 {
   ptr = buf;
   for( i = 0; i < n; i++ )
   {
    tmp = (integer4) *(integer*)ptr;
    m += fwrite( (void *)ptr, fsiz, 1, fptr );    
    ptr += siz;
   }
 } else {
   m = fwrite( (void *)buf, fsiz, n, fptr );    
 }
 status = (m == n );
 utn_fio_file_set_status( file, status );
 utn_fio_delta_pos( file, m * fsiz );
 *nw = m;
 return( status );
}


logical utn_fio_read_sio( FioFile file, void* buf, long n, long* nw, size_t siz )
{
 FILE* fptr;
 long m;
 logical status;

 fptr = utn_fio_file_get_fptr( file );
 m = fread( (void *)buf, siz, n, fptr );
 status = ! ( feof( fptr ) || ferror( fptr ) );
 utn_fio_file_set_status( file, status );
 utn_fio_delta_pos( file, m * siz );
 *nw = m;
 return( status );
}

logical utn_fio_read_sio_siz( FioFile file, void* buf, long n, long* nw, size_t siz, size_t fsiz )
{
 FILE* fptr;
 long m = 0;
 logical status;
 integer4 tmp;
 integer i;
 char* ptr;
 fptr = utn_fio_file_get_fptr( file );
 if ( fsiz != siz )
 {
  ptr = buf;
  for ( i = 0; i < n; i++ ) 
  {
   m = fread( (void*)&tmp, fsiz, 1, fptr );
   *(integer*)ptr = tmp;
   ptr += siz;   
  }
 } else {
  m = fread( (void *)buf, siz, n, fptr );
 }
 status = ! ( feof( fptr ) || ferror( fptr ) );
 utn_fio_file_set_status( file, status );
 utn_fio_delta_pos( file, m * siz );
 *nw = m;
 return( status );
}


logical utn_fio_read_q( FioFile file, logical* buf, long n, long* nw )
{
 return( utn_fio_read_sio( file, (void *)buf, n, nw, UT_SZ_Q ) );
}

logical utn_fio_read_d( FioFile file, double* buf, long n, long* nw )
{
 return( utn_fio_read_sio( file, (void *)buf, n, nw, UT_SZ_D ) );
}

logical utn_fio_read_c( FioFile file, char* buf, long n, long* nw )
{
 return( utn_fio_read_sio( file, (void *)buf, n, nw, UT_SZ_C ) );
/* bug fix 1997 Mar 10 */
}

logical utn_fio_read_r( FioFile file, float* buf, long n, long* nw )
{
 return( utn_fio_read_sio( file, (void *)buf, n, nw, UT_SZ_R ) );
}

logical utn_fio_read_i( FioFile file, long* buf, long n, long* nw )
{
 return( utn_fio_read_sio( file, (void *)buf, n, nw, UT_SZ_I ) );
}

logical utn_fio_read_s( FioFile file, short* buf, long n, long* nw )
{
 return( utn_fio_read_sio( file, (void *)buf, n, nw, UT_SZ_S ) );
}



/*
 *  Currently support SIO files only
 */
logical utn_fio_seek( FioFile file, long pos )
{
 if ( utn_cs_eq_begin( file->type, "SIO" ))
  return utn_fio_seek_s( file, pos );
 else if( file->flags[FIO_FLAG_MAP] )
  return utn_fio_seek_map( file, pos );
 return UT_FALSE;
}

logical utn_fio_seek_s( FioFile file, long pos )
{
 FILE* fptr;
 logical status;

 utn_fio_file_clear_eof( file );
 fptr = utn_fio_file_get_fptr( file );
 status = fseek( fptr, pos, SEEK_SET );
 return( !status );
}



