/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*======================================================================*/
/*
 *    cs2
 *
 *    Null terminated char* ("C char*") handling routines
 *
 */
/*======================================================================*/
#include <ctype.h>
#include "utlib.h"

/*----------------------------------------------------------------------*/
/*    utn_cs_strpak
 *    
 * Pack a char*, forcing to uppercase and removing spaces and underbars.
 * Useful for name resolvers.
 * 1996 Sep 30 JCM
 */

void utn_cs_strpak( const char* isource, char* dest )
{
 char* source = (char*)isource;
 while ( *source != '\0' ) {
  while ( *source == ' ' || *source == '_' ) source++;
  if ( *source !='\0' ) 
  {
   *dest = toupper( *source++ );
   dest++;
  }
 }
 *dest = '\0';
}

/*----------------------------------------------------------------------*/
/*
 *+cs_gparen
 *
 */

void utn_cs_gparen( const char* isource, const char lpar, const char rpar, char* dest )
{
 long k;
 char* source = (char*)isource;
 *dest++ = lpar;
 k = utn_cs_ends( source );
 while ( k-- ) { *dest = *source++; dest++; }
 *dest = rpar; dest++;
 *dest   = '\0';
}

/*----------------------------------------------------------------------*/
/*
 *+cs_paren
 *
 */
char* utn_cs_paren( const char* source, char* dest )
{
 utn_cs_gparen( source, '(', ')', dest );
 return( dest );
}

/*----------------------------------------------------------------------*/
/*
 *+cs_sparen
 *
 */
char* utn_cs_sparen( const char* source, char* dest )
{
 utn_cs_gparen( source, '[', ']', dest );
 return( dest );
}


void utn_cs_ubar( char* source )
{
 long n;
 n = utn_cs_ends( source );
 while ( n-- ) {
  if ( *source == ' ' ) *source = '_';
  source++;
 } 
}

void utn_cs_texubar( char* sourcep )
{
 long n;
 char* source = sourcep;
 TextBuf buf;
 char* ptr = buf;
 n = utn_cs_ends( source );
 while ( n-- ) {
  if ( *source == ' ' ) 
  {
   *ptr = '\\';
   ptr++;
   *ptr = '_';
  } else {
   *ptr = *source;
  }
  source++;
  ptr++;
 } 
 *ptr = 0;
 utn_cs_copy( buf, sourcep );
}

void utn_cs_deubar( char* source )
{
 long n;
 n = utn_cs_ends( source );
 while ( n-- ) {
  if ( *source == '_' ) *source = ' ';
  source++;
 } 
}

logical utn_cs_nullstr( const char* source )
{
 return( *source == '\0' );
}



void utn_cs_deparen( char** buf, char* dest )
{
 char* ptr;
 const char lpar = '(';
 const char rpar = ')';
 long b,e;
 long npar;

 npar = 1;
 ptr = *buf;
 b = utn_cs_begs( ptr );
 e = utn_cs_ends( ptr );
 ptr += b - 1;
 if (*ptr == lpar ) ptr++;
 while ( npar > 0 && ptr - *buf + 1 <= e  ) {
  if ( *ptr == lpar ) {
   npar++;
  } else if ( *ptr == rpar ) {
   npar--;
  }
  if ( npar > 0 ) { *dest++ = *ptr++; }
 }
 
 if ( npar > 0 ) { 
  *buf = NULL;
  *dest = '\0';
 } else {
  *buf =++ptr;
  *dest = '\0';
 }  
}


