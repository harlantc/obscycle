/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/*----------------------------------------------------------------------*/
/*+af1_parse_fmt:   Parse a Fortran style format; return C format, data type and field length */
logical utn_cs_fs_format_parse( const char*  Fortran_Format, char* C_Format, integer maxlen, char* DataType, integer* Field_Length )
{
 logical q = UT_TRUE;
 char* ptr;
 int length; /* Field length */
 int dp;  /* Decimal points */
 int dp_pos;
 char type;
 TextCard tmp;

 type = *Fortran_Format;
 ptr =  (char*)(Fortran_Format + 1); /* Remove const */
 if ( utn_cs_is_blank( ptr ) ) {
  if ( type == ' ' ) type = 'A';
  length = ( type == 'A' ) ? 0 : 1;   /* Special case of 'A' meaning whole char* */
  dp = 0;
 } else {
  dp_pos = utn_cs_index( ptr, "." );    /* utn_cs_index returns the 1-based position in the char*, or 0 on failure */
  if ( dp_pos == 0 ) {   /* No decimal point */
   dp = 0;
   length = utn_cs_read_i( ptr );   /* read_i is a fancier version of atoi */
  } else {
   utn_cs_get_ss( ptr, tmp, 1, dp_pos-1 );  /* utn_cs_get_ss extracts the subchar* into tmp (if only C did subchar*s...) */
   length = utn_cs_read_i( tmp );   /* Read long up to position of decimal point */
   ptr += dp_pos;
   dp = utn_cs_read_i( ptr );       /* Read long following decimal point */
  }
 }

 switch( type ) {
  case 'I' :                /*  Note that Fortran makes no distinction of %d and %ld */
   if ( dp > 0 ) {
    snprintf( C_Format, maxlen, "%%%d.%dld ", length, dp );     /* I4.3 -> %4.3ld; */
   } else {
    snprintf( C_Format, maxlen, "%%%dld", length );       /* I4 -> %4ld */
   }
   break;
  
  case 'A' :
   if ( length == 0 ) {
    snprintf( C_Format, maxlen, "%%s" );     /* A -> %s, special case */
   } else { 
    snprintf( C_Format, maxlen, "%%-%d.%ds ", length, length );     /* A4 -> %-4.4s */
   }
   break;
  
  case 'F' :
   snprintf( C_Format, maxlen, "%%%d.%df ", length, dp );   /* F4.2 -> %4.2f */
   break;

  case 'E' :
   snprintf( C_Format, maxlen, "%%%d.%dE ", length , dp );   /* E4.2 -> %4.2E */
   break;

  case 'G' :
   snprintf( C_Format, maxlen, "%%%d.%dG ", length, dp );    /* G4.2 -> %4.2G */
   break;

  case 'Z' :
   if ( dp > 0 ) {
    snprintf( C_Format, maxlen, "%%%d.%dX ", length, dp );     /* Z4.2 -> %4.2X;    */
   } else {
    snprintf( C_Format, maxlen, "%%%dX", length );       /* Z4 -> %4X */
   }
   break;

  default:
   q = UT_FALSE;
   utn_cs_copy( " ", C_Format );
   break; 
  }
 *DataType = type;
 *Field_Length = length;
 return q;
}



