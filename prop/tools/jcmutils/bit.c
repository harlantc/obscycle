/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/* Bit functions; note bits are numbered 1 to n */

/*
 *  Give me a number with a single bit set in the specified position.
 */
unsigned short utn_bit_single_s( const long utn_bit_no )
{
 unsigned short value = 1;
 return( value << utn_bit_no );
}

unsigned long int utn_bit_single_i( const long utn_bit_no )
{
 unsigned long int value = 1;
 return( value << utn_bit_no );
}

/*
 * Set bit in value 
 */
void utn_bit_set_i( long* value, const long utn_bit_no )
{
 *value |= utn_bit_single_i( utn_bit_no );
}


void utn_bit_set_s( short* value, const long utn_bit_no )
{
 *value |= utn_bit_single_i( utn_bit_no );
}

void utn_bit_clr_i(  long* value, const long utn_bit_no )
{
 *value &= ~utn_bit_single_i( utn_bit_no );
}

void utn_bit_clr_s(  short* value, const long utn_bit_no )
{
 *value &= ~ utn_bit_single_s( utn_bit_no );
}


short utn_bit_shft_s( short value, const long bits_to_shift )
{
 if ( bits_to_shift >= 0 ) {
  return( value << bits_to_shift );
 } else { 
  return( value >> -bits_to_shift );
 }
}

long utn_bit_shft_i( long value, const long bits_to_shift )
{
 if ( bits_to_shift >= 0 ) {
  return( value << bits_to_shift );
 } else { 
  return( value >> -bits_to_shift );
 }
}

logical utn_bit_test_i( long value, const long utn_bit_no )
{
 long tmp;
 tmp =  utn_bit_single_i( utn_bit_no ) & value ;
 return( tmp != 0 );
}

logical utn_bit_test_s( short value, const long utn_bit_no )
{
 short tmp;
 logical result;
 tmp =  utn_bit_single_s( utn_bit_no ) & value ;
 result = ( tmp != 0 );
 return( result );
}

long utn_bit_unpack_i( const long value, const long start_bit, const long n_bits )
{
 long result;
 unsigned long int mask;

 mask = ~( ~0 << n_bits );  /* n ones at the right */
 result = mask & ( value >> start_bit );
 return( result );
}

long utn_bit_unpack_s( const short value, const long start_bit, const long n_bits )
{
 unsigned short result;
 unsigned short mask;
 unsigned long int iresult;

 mask = ~( ~0 << n_bits );  /* n ones at the right */
 result = mask & ( value >> start_bit );
 iresult = (unsigned long int) result;
 return( (long)iresult );
}

