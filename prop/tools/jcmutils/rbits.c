/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

static long utn_bit_setbyte( const long* bits );
static void utn_bit_getbyte( const long byte, long* bits );
static void utn_bit_wfmt( const long* bits, const long nbits, const long base_bits, char* buf );
static long utn_bit_rfmt( const char* buf, long* bits, const long base_bits );
static long utn_bit_read( const char* buf, long* bits, const char mode );

long utn_bit_setbyte( const long* bits )
{
 long bit;
 long byte = 0;
 for ( bit = 0; bit < UT_BYTE_BITS; bit++ ) {
  if ( bits[bit] ) utn_bit_set_i( &byte, bit );
 }
 return( byte );
}

void utn_bit_getbyte( const long byte, long* bits )
{
 long bit;
 utn_ar_zero_i( bits, UT_BYTE_BITS );
 for ( bit = 0; bit < UT_BYTE_BITS; bit++ ) {
  if ( utn_bit_test_i( byte, bit ) ) bits[bit] = 1;
 }
}

long utn_bit_ar_get_r( const float value, long* bits )
{
 long bytes[UT_SZ_R];
 long nbytes = UT_SZ_R;
 long nbits = UT_SZ_R * UT_BYTE_BITS;
 long byte_no;
 long bit_pos;

 utn_ar_zero_i( bits, nbits ); 
 utn_bytes_split_r( value, bytes );
 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  utn_bit_getbyte( bytes[byte_no], bits+bit_pos );
 }
 return( nbits );
}

long utn_bit_ar_get_d( const double value, long* bits )
{
 long bytes[UT_SZ_D];
 long nbytes = UT_SZ_D;
 long nbits = UT_SZ_D * UT_BYTE_BITS;
 long byte_no;
 long bit_pos;

 utn_ar_zero_i( bits, nbits ); 
 utn_bytes_split_d( value, bytes );
 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  utn_bit_getbyte( bytes[byte_no], bits+bit_pos );
 }
 return( nbits );
}

long utn_bit_ar_get_s( const short value, long* bits )
{
 long bytes[UT_SZ_S];
 long nbytes = UT_SZ_S;
 long nbits = UT_SZ_S * UT_BYTE_BITS;
 long byte_no;
 long bit_pos;

 utn_ar_zero_i( bits, nbits ); 
 utn_bytes_split_s( value, bytes );
 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  utn_bit_getbyte( bytes[byte_no], bits+bit_pos );
 }
 return( nbits );
}

long utn_bit_ar_get_i( const long value, long* bits )
{
 long bytes[DT_SZ_I];
 long nbytes = DT_SZ_I;
 long nbits = DT_SZ_I * UT_BYTE_BITS;
 long byte_no;
 long bit_pos;

 utn_ar_zero_i( bits, nbits ); 
 utn_bytes_split_i( value, bytes );
 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  utn_bit_getbyte( bytes[byte_no], bits+bit_pos );
 }
 return( nbits );
}


short utn_bit_ar_set_s( const long* bits )
{
 short value;
 long bytes[UT_SZ_S];
 long nbytes = UT_SZ_S;
 long byte_no;
 long bit_pos;

 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  bytes[byte_no] = utn_bit_setbyte( bits+bit_pos );
 }
 value = utn_bytes_combine_s( bytes );
 return( value );
}

long utn_bit_ar_set_i( const long* bits )
{
 long value;
 long bytes[DT_SZ_I];
 long nbytes = DT_SZ_I;
 long byte_no;
 long bit_pos;
 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  bytes[byte_no] = utn_bit_setbyte( bits+bit_pos );
 }
 value = utn_bytes_combine_i( bytes );
 return( value );
}

float utn_bit_ar_set_r( const long* bits )
{
 float value;
 long bytes[UT_SZ_R];
 long nbytes = UT_SZ_R;
 long byte_no;
 long bit_pos;
 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  bytes[byte_no] = utn_bit_setbyte( bits+bit_pos );
 }
 value = utn_bytes_combine_r( bytes );
 return( value );
}

double utn_bit_ar_set_d( const long* bits )
{
 double value;
 long bytes[UT_SZ_D];
 long nbytes = UT_SZ_D;
 long byte_no;
 long bit_pos;
 for ( byte_no = 0; byte_no < nbytes; byte_no++ ) {
  bit_pos =  byte_no  * UT_BYTE_BITS;
  bytes[byte_no] = utn_bit_setbyte( bits+bit_pos );
 }
 value = utn_bytes_combine_d( bytes );
 return( value );
}

long utn_bits_unsigned( const short value )
{
 long result = ( value < 0 ) ? value + UT_BIG_INT : value;
 return( result );
}


long utn_bits_val( const long* bits, const long m )
{
 long n = m;
 long result = 0;
 const long* ptr = bits + n - 1;
 while ( n-- ) {
  result = result * 2 + *ptr--;
 }
 return( result );
}

void utn_bit_wfmt( const long* bits, const long nbits, const long base_bits, char* buf )
{
 char digits[]= "0123456789ABCDEF";
 long bit;
 long pos;
 long value;
 long tpos;
 long step;
 long nsteps;
 long r;
/*
 * Convert an array of bits to a char* in hex, octal or binary notation.
 * Case of binary (base_bits = 1):
 *  the leftmost char in the output char* is the most significant 
 *  bit, which is the last bit in the array: bits[nbits-1].
 *  Each bit corresponds to a 0 or 1 in the output char*.
 * Case of octal or hex: We work in steps of base_bits.
 *  For each step, we accumulate the value from the bits
 *  and then convert it to a char.
 */   
 buf[0] = '\0';
 if ( base_bits == 1 ) {
  for ( bit = 0; bit < nbits; bit++ ) {
   pos = bits[nbits-bit-1];
   buf[bit] = digits[pos];
  }

  buf[nbits] = '\0';
 } else {
  nsteps = nbits / base_bits;
  if ( nsteps * base_bits < nbits ) nsteps++;

  for ( tpos = 0; tpos < nsteps; tpos++ ) {
   step = nsteps - tpos - 1;
   value = 0;
   for ( pos = base_bits - 1; pos >= 0; pos-- ) {
    bit = base_bits * step + pos;
    r   = ( bit >= nbits ) ? 0 : bits[bit];
    value = value * 2 + r;
   }
   buf[tpos] = digits[value];

  }
  buf[nsteps] = '\0';

 }
}

/*
 *  Parse a char* containing a bit pattern, return an array of 0s and 1s
 */
long utn_bit_rfmt( const char* buf, long* bits, const long base_bits )
{
 long nbits;
 long buf_size;
 long bit_no;
 long pos;
 long byte_pos;
 long hex_byte;

 buf_size = utn_cs_ends( buf );
 nbits = buf_size * base_bits;
 if ( base_bits == 1 ) {
/* B */  /* Note O or Z branch will also work for this case, but for clarity we treat this specially */
  for ( pos = 0; pos < buf_size; pos++ ) {
   bit_no = nbits - pos - 1;
   bits[bit_no] = utn_cs_read_digit( buf[pos] );
  }
 } else {
/* O or Z */
  for ( pos = 0; pos < buf_size; pos++ ) {
   hex_byte = utn_cs_read_hex_digit( buf[pos] );
   for ( byte_pos = 0; byte_pos < base_bits; byte_pos++ ) {
    bit_no = nbits - base_bits * ( pos + 1 ) + byte_pos;
    bits[bit_no] = hex_byte % 2;
    hex_byte /= 2;
   }
  }  
 }
 return( nbits );
}


void utn_bit_write( const long* bits, const long nbits, const char mode, char* buf )
{
 long base_bits;
 switch( mode ) {
  case 'B': 
   base_bits = 1;
   break;
  case 'O':
   base_bits = 3;
   break;
  case 'Z':
   base_bits = 4;
   break;
  default:
   base_bits = 0;
   break;
 }
 utn_bit_wfmt( bits, nbits, base_bits, buf ); 
}

long utn_bit_read( const char* buf, long* bits, const char mode )
{
 long nbits;
 long base_bits;
 switch( mode ) {
  case 'B': 
   base_bits = 1;
   break;
  case 'O':
   base_bits = 3;
   break;
  case 'Z':
   base_bits = 4;
   break;
  default:
   base_bits = 0;
   break;
 }
 nbits = utn_bit_rfmt( buf, bits, base_bits );
 return( nbits );
}


void utn_cs_write_binary_i( const long value, char* buf, const char mode )
{
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_ar_get_i( value, bits );
 utn_bit_write( bits, n, mode, buf );
}

void utn_cs_write_binary_s( const short value, char* buf, const char mode )
{
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_ar_get_s( value, bits );
 utn_bit_write( bits, n, mode, buf );
}

void utn_cs_write_binary_r( const float value, char* buf, const char mode )
{
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_ar_get_r( value, bits );
 utn_bit_write( bits, n, mode, buf );
}

void utn_cs_write_binary_d( const double value, char* buf, const char mode )
{
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_ar_get_d( value, bits );
 utn_bit_write( bits, n, mode, buf );
}

short utn_cs_read_binary_s( const char* buf, const char mode )
{
 short value;
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_read( buf, bits, mode );
 value = utn_bit_ar_set_s( bits );
 return( value );
}

long utn_cs_read_binary_i( const char* buf, const char mode )
{
 long value;
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_read( buf, bits, mode );
 value = utn_bit_ar_set_i( bits );
 return( value );
}

float utn_cs_read_binary_r( const char* buf, const char mode )
{
 float value;
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_read( buf, bits, mode );
 value = utn_bit_ar_set_r( bits );
 return( value );
}

double utn_cs_read_binary_d( const char* buf, const char mode )
{
 double value;
 long bits[UT_BITS_MAX];
 long n;
 n = utn_bit_read( buf, bits, mode );
 value = utn_bit_ar_set_d( bits );
 return( value );
}


