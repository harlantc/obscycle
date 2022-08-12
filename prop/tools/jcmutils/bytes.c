/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <stdio.h>
#include "utlib.h"


static void utn_ieee_put( const char* iptr, char* buf, const long nn, const long size, const long fsize );
/* utn_ieee_put maps from internal to file buffer 
 * utn_ieee_get maps from file buffer to internal 
 */

/* Promote structure buffer entry to be aligned as required 
 * total  the number of bytes up to end of data for entry n
 * size   size of one element of entry n+1
 * old_size size of one element of entry n
 * pad    extra bytes to put on end of entry n to get
 *       the beginning of entry n+1 on the right boundary
 *
 *
 * Little endian integers on Opteron:
 * 8 byte integers
 * VAL: 28120 = 109 * 256 + 216
 * ADDR 0   1   2 3 4 5 6 7 
 * VAL  216 109 0 0 0 0 0 0
 *
 * Same on Sun if in 8-byte
 * ADDR 0   1   2 3 4 5 6   7 
 * VAL  0   0   0 0 0 0 109 216 
 *
 *
 */
long utn_sys_align( long total, long size, long old_size )
{
 long pad = 0;
 long align = 0;

 if ( old_size < size )
 {
  align = size;

#ifdef LINUX_X86
  if ( align > UT_SZ_I ) align = UT_SZ_I;
#endif
  pad = align - ( total % align );
  if ( pad == align ) pad = 0;
 }
 return pad;
}

void utn_ieee_put_r( const float* data, char* buf, const long n )
{
 utn_ieee_put( (char*)data, buf, n, (long)UT_SZ_R, (long)DT_SZ_R );
}

void utn_ieee_put_d( const double* data, char* buf, const long n )
{
 utn_ieee_put( (char*)data, buf, n, (long)UT_SZ_D, (long)DT_SZ_D);
}
void utn_ieee_put_s( const short* data, char* buf, const long n )
{
 utn_ieee_put( (char*)data, buf, n, (long)UT_SZ_S, (long)DT_SZ_S );
}
void utn_ieee_put_i( const long* data, char* buf, const long n )
{
 utn_ieee_put( (char*)data, buf, n, (long)UT_SZ_I, (long)DT_SZ_I );
}


void utn_ieee_get_r( const char* buf, float* data, const long n )
{
 utn_ieee_get( buf, (char*)data, n, UT_SZ_R, DT_SZ_R );
}

void utn_ieee_get_d( const char* buf, double* data, const long n )
{
 utn_ieee_get( buf, (char*)data, n, UT_SZ_D, DT_SZ_D );
}

void utn_ieee_get_s( const char* buf, short* data, const long n )
{
 utn_ieee_get( buf, (char*)data, n, UT_SZ_S, DT_SZ_S );
}

void utn_ieee_get_i( const char* buf, long* data, const long n )
{
 utn_ieee_get( buf, (char*)data, n, UT_SZ_I, DT_SZ_I );
}

void utn_ieee_get( const char* ibuf, char* ptr, const long nn, long size, long fsize )
{
 char* buf = (char*)ibuf;
 long m;
 long n = nn;
 long nbytes;
 integer dm;
 dm = size - fsize;
 nbytes = size * n; 
#if BYTE_SWAPPED
 while( n-- ) {
  m = fsize;
  while ( m > 0 ) {
   m--;
   *ptr = *(buf + m );
   ptr++;
  }
  buf += fsize;
  m = dm;
  while( m-- ) 
  {
   *ptr = 0;
   ptr++;
  }
 }
#else
 if ( dm > 0 )
 {
  while( n-- ) 
  {
   m = dm;
   while( m-- ) 
   {
    *ptr = 0;
    ptr++;
   }
   m = 0;
   while ( m < fsize ) {
    *ptr = *(buf + m );
    ptr++;
    m++;
   }
   buf += fsize;
  }
 

 } else {
  memcpy( ptr, buf, nbytes );
 }
#endif
}

static void utn_ieee_put( const char* iptr, char* buf, const long nn, const long size, const long fsize )
{
 char* ptr = (char*)iptr;
 long nbytes;
 long n = nn;
 long m;
 integer dm;
  nbytes = n * size;
 dm = size - fsize;
 m = fsize;
#if BYTE_SWAPPED
 while( n-- ) {
  while( m > dm ) {
   m--;
   *buf = *(ptr + m );
   buf++;
  }
  m = fsize;
  ptr += size;
 }
#else
 if ( dm > 0 )
 {
  m = dm;
  while( n-- ) 
  {
   while ( m < size )    
   {
    *buf = *(ptr + m );
    buf++;
    m++;
   }
   m = dm;
   ptr += size;
  }
 } else {
  memcpy( buf, ptr, nbytes );
 }
#endif
}



void utn_bytes_split_i( const long value, long* bytes )
{
 long byte_no;
 for ( byte_no = 1; byte_no <= 4; byte_no++ ) {
  bytes[byte_no-1] = utn_byte_get_i( value, byte_no );
 }
}

void utn_bytes_split_r( const float value, long* bytes )
{
 long byte_no;
 for ( byte_no = 1; byte_no <= 4; byte_no++ ) {
  bytes[byte_no-1] = utn_byte_get_r( value, byte_no );
 }
}

void utn_bytes_split_d( const double value, long* bytes )
{
 long byte_no;
 for ( byte_no = 1; byte_no <= 8; byte_no++ ) {
  bytes[byte_no-1] = utn_byte_get_d( value, byte_no );
 }
}

void utn_bytes_split_s( const short value, long* bytes )
{
 long byte_no;
 for ( byte_no = 1; byte_no <= 2; byte_no++ ) {
  bytes[byte_no-1] = utn_byte_get_s( value, byte_no );
 }
}

void utn_bytes_split_c( const char* value, long* bytes, const long n_bytes )
{
 long byte_no;
 for ( byte_no = 1; byte_no <= n_bytes; byte_no++ ) {
  bytes[byte_no-1] = utn_byte_get_c( value, byte_no );
 }
}


long utn_bytes_combine_i( const long* bytes )
{
 long byte_no;
 long value = 0;
 for ( byte_no = 1; byte_no <= 4; byte_no++ ) {
  utn_byte_set_i( &value, bytes[byte_no-1], byte_no );
 }
 return( value );
}

short utn_bytes_combine_s( const long* bytes )
{
 long byte_no;
 short value = 0;
 for ( byte_no = 1; byte_no <= 2; byte_no++ ) {
  utn_byte_set_s( &value, bytes[byte_no-1], byte_no );
 }
 return( value );
}


float utn_bytes_combine_r( const long* bytes )
{
 long byte_no;
 float value = 0;
 for ( byte_no = 1; byte_no <= 4; byte_no++ ) {
  utn_byte_set_r( &value, bytes[byte_no-1], byte_no );
 }
 return( value );
}


double utn_bytes_combine_d( const long* bytes )
{
 long byte_no;
 double value = 0;
 for ( byte_no = 1; byte_no <= 8; byte_no++ ) {
  utn_byte_set_d( &value, bytes[byte_no-1], byte_no );
 }
 return( value );
}


void utn_bytes_combine_c( const long* bytes, char* value, const long n_bytes )
{
 long byte_no;

 for ( byte_no = 1; byte_no <= n_bytes; byte_no++ ) {
  utn_byte_set_c( value, bytes[byte_no-1], byte_no );
 }
}



void utn_byte_set_s( short* number, const long value, const long byte_no )
{
 short buffer;
 buffer = utn_bit_shft_s( (short)value, UT_BYTE_BITS * ( byte_no - 1 ) );
 *number |= buffer;
}


void utn_byte_set_i( long* number, const long value, const long byte_no )
{
 long buffer;
 buffer = utn_bit_shft_i( value, UT_BYTE_BITS * ( byte_no - 1 ) );
 *number |= buffer;
}

void utn_byte_set_r( float* number, const long value, const long byte_no )
{
 long* ptr;
 ptr = (long *)number;
 utn_byte_set_i( ptr, value, byte_no );
}


void utn_byte_set_d( double* number, const long value, const long byte_no )
{
 long* ptr;
 long segment;
 long ibyte;

 ptr = (long *)number;
 utn_sys_byte_d( byte_no, &segment, &ibyte );
 utn_byte_set_i( ptr + segment - 1, value, ibyte );
}

long utn_byte_get_i( const long value, const long byte_no )
{
 long start_bit;
 long result;

 start_bit = ( byte_no - 1 ) * UT_BYTE_BITS;
 result = utn_bit_unpack_i( value, start_bit, UT_BYTE_BITS );
 return( result ); 
}


long utn_byte_get_r( const float value, const long byte_no )
{
 long* buffer;
 long result;
 buffer = (long *)(&value);
 result =  utn_byte_get_i( *buffer, byte_no );
 return( result );
}

long utn_byte_get_s( const short value, const long byte_no )
{
 long start_bit;
 long result;
 start_bit = ( byte_no - 1 ) * UT_BYTE_BITS;
 result = utn_bit_unpack_s( value, start_bit, UT_BYTE_BITS );
 return( result ); 
}

long utn_byte_get_d( const double value, const long byte_no )
{
 long* ptr;
 long segment;
 long ibyte;
 long result;
 
 ptr = (long *)(&value);
 utn_sys_byte_d( byte_no, &segment, &ibyte );
 result = utn_byte_get_i( *(ptr + segment - 1), ibyte );
 return( result );
}


long utn_byte_get_c( const char* value, const long byte_no )
{
 uchar c;
 c = (uchar)value[byte_no-1];
 return( (long)c );
}

void utn_byte_set_c( char* data, const long value, const long byte_no )
{
 data[byte_no-1] = (uchar)value;
}


void utn_sys_byte_d( const long byte_no, long* segment, long* ibyte )
{
 if ( byte_no > 4 ) {
  *ibyte = byte_no - 4;
  *segment = UT_R8_WORD2;
 } else {
  *ibyte = byte_no;
  *segment = UT_R8_WORD1;
 }
}


logical utn_sys_big_endian( void )
{
#if BYTE_SWAPPED
 return 0;
#else
 return 1;
#endif
}

void utn_sys_get_bytes( const char* byte_buffer, char* result_buffer, const long result_nvals, long memory_bytes, long disk_bytes, logical disk_big_endian )
{
 char* input_buffer = (char*)byte_buffer;
 long m;
 long nvals = result_nvals;
 long nbytes;
 logical mem_big_endian;
 integer spare_bytes;

 mem_big_endian = utn_sys_big_endian();
 spare_bytes = memory_bytes - disk_bytes;
 nbytes = memory_bytes * nvals; 

 if ( disk_big_endian && !mem_big_endian )
 {

/* Big endian on disk, little endian in memory */

  while( nvals-- ) {

/* Move data bytes from end of input to beginning of result */
   m = disk_bytes;
   while ( m > 0 ) {
    m--;
    *result_buffer = *(input_buffer + m );
    result_buffer++;
   }
   input_buffer += disk_bytes;
/* Pad result with spare bytes after */
   m = spare_bytes;
   while( m-- ) 
   {
    *result_buffer = 0;
    result_buffer++;
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
     *result_buffer = 0;
     result_buffer++;
    }
    m = 0;
/* Copy bytes from start of input buffer to next pos in result */
    while ( m < disk_bytes ) 
    {
     *result_buffer = *(input_buffer + m );
     result_buffer++;
     m++;
    }
    input_buffer += disk_bytes;
   }
  } else {

/* Same endian, and same size */

   memcpy( result_buffer, input_buffer, nbytes );
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
     *result_buffer = *(input_buffer + m );
     result_buffer++;
     m++;
    }
    input_buffer += disk_bytes;
/* Pad result with spare bytes after */
    m = spare_bytes;
    while( m-- ) 
    {
     *result_buffer = 0;
     result_buffer++;
    }
   }
  } else {

/* Same endian, and same size */

   memcpy( result_buffer, input_buffer, nbytes );
  }


 }
}

void utn_sys_get_bytes_d( const char* buf, double* data, const long n, logical disk_endian )
{
 utn_sys_get_bytes( buf, (char*)data, n, UT_SZ_D, DT_SZ_D, disk_endian );
}

void utn_sys_get_bytes_r( const char* buf, float* data, const long n, logical disk_endian )
{
 utn_sys_get_bytes( buf, (char*)data, n, UT_SZ_R, DT_SZ_R, disk_endian );
}

void utn_sys_get_bytes_i( const char* buf, integer* data, const long n, logical disk_endian )
{
 utn_sys_get_bytes( buf, (char*)data, n, UT_SZ_I, DT_SZ_I, disk_endian );
}

void utn_sys_get_bytes_ii( const char* buf, int* data, const long n, logical disk_endian )
{
 utn_sys_get_bytes( buf, (char*)data, n, UT_SZ_INT, DT_SZ_I, disk_endian );
}


