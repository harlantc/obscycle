/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <stdio.h>
#include "utlib.h"

void utn_sys_put_bytes( char* mem_buffer, char* output_buffer,   long result_nvals, long memory_bytes, long disk_bytes, logical disk_big_endian )
{
 long m;
 long nvals = result_nvals;
 long nbytes;
 logical mem_big_endian;
 integer spare_bytes;

 mem_big_endian = utn_sys_big_endian();
 spare_bytes = disk_bytes - memory_bytes;
 nbytes = memory_bytes * nvals; 

 if ( disk_big_endian && !mem_big_endian )
 {

/* Big endian on disk, little endian in memory */

  while( nvals-- ) {

   m = disk_bytes;
   while ( m > 0 ) {
    m--;
    *(output_buffer + m ) = *mem_buffer;
    mem_buffer++;
   }
   output_buffer += disk_bytes;
/* Pad result with spare bytes after */
   m = spare_bytes;
   while( m-- ) 
   {
    *output_buffer = 0;
    output_buffer++;
   }
  }
 } else if ( disk_big_endian && mem_big_endian ) {

/* From big endian on disk to big endian inside */

  if ( spare_bytes > 0 )
  {
   while( nvals-- ) 
   {

/* Pad result with spare bytes before */
    m = spare_bytes;
    while( m-- ) 
    {
     *output_buffer = 0;
     output_buffer++;
    }
    m = 0;
/* Copy bytes from start of input buffer to next pos in result */
    while ( m < disk_bytes ) 
    {
     *(output_buffer+m) = *mem_buffer;
     mem_buffer++;
     m++;
    }
    output_buffer += disk_bytes;
   }
  } else {

/* Same endian, and same size */

   memcpy( output_buffer, mem_buffer, nbytes );
  }

 } else if ( !disk_big_endian && !mem_big_endian ) {

/* From little endian on disk to little endian inside */

  if ( spare_bytes > 0 )
  {
   while( nvals-- ) 
   {

    m = 0;
/* Copy bytes from start of input buffer to next pos in result */
    while ( m < disk_bytes ) 
    {
     *(output_buffer + m ) = *mem_buffer;
     mem_buffer++;
     m++;
    }
    output_buffer += disk_bytes;
/* Pad result with spare bytes after */
    m = spare_bytes;
    while( m-- ) 
    {
     *output_buffer = 0;
     output_buffer++;
    }
   }
  } else {

/* Same endian, and same size */

   memcpy( output_buffer, mem_buffer, nbytes );
  }


 }
}


void utn_sys_put_bytes_d(   char* buf, double* data,   long n, logical disk_endian )

{
 utn_sys_put_bytes( (char*)data, buf, n, UT_SZ_D, DT_SZ_D, disk_endian );
}

void utn_sys_put_bytes_r(   char* buf, float* data,   long n, logical disk_endian )
{
 utn_sys_put_bytes( (char*)data, buf, n, UT_SZ_R, DT_SZ_R, disk_endian );
}

void utn_sys_put_bytes_i(   char* buf, integer* data,   long n, logical disk_endian )
{
 utn_sys_put_bytes( (char*)data, buf, n, UT_SZ_I, DT_SZ_I, disk_endian );
}




