/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


char** utn_cs_parse_parseq_list( const char* buffer, char quote, char sep, long* np );

/*----------------------------------------------------------------------*/
/*
 *  utn_cs_put_token
 *
 * JCMLIB library routine (TOKEN)
 * Copy from buf to *dest, appending the sep character or until dest is full up to siz.
 * Update the dest pointer to point to the end of the text
 */

void utn_cs_put_token( const char* ibuf, char** dest, const char sep, long* siz )
{
 char* buf = (char*)ibuf;
 char* ptr = *dest;
 while ( *siz && *buf != '\0' ) 
 {
  *ptr = *buf++; ptr++; (*siz)--;
 }
 if ( *siz && sep )
 {
  *ptr = sep; ptr++;
 }
 *ptr = '\0';
 *dest = ptr;
}

/*----------------------------------------------------------------------*/
/*
 *
 * utn_cs_get_token
 *
 * Uses: escproto.h: esc_get_char
 * tokenproto.h: utn_cs_copy_token
 * csproto.h: utn_cs_copy

 */

void utn_cs_get_token( char** buf, char* token, const long mode, const long siz )
{
 char  seps[5]=" ";
 char pars[2];
 logical truncated = UT_FALSE;
 if ( !buf || !(*buf) ) { *token = '\0'; return; }
 switch( mode ) {
  case TMODE_WORD: 
   pars[0] = utn_esc_special_char( LEFT_PAREN );
   pars[1] = utn_esc_special_char( RIGHT_PAREN );
   seps[0]= ' ';
   seps[1]= '\t';
   seps[2]= utn_esc_special_char( WORD_SEP );
   seps[3]= utn_esc_special_char( STRING_SEP );  
   seps[4] = 0;
   utn_cs_copy_token_esc( buf, seps, pars, 0, token, siz, &truncated );
   break;
  case TMODE_STRING: 
   pars[0] = utn_esc_special_char( LEFT_PAREN );
   pars[1] = utn_esc_special_char( RIGHT_PAREN );
   seps[0] = utn_esc_special_char( STRING_SEP );
   seps[1] = 0;
   utn_cs_copy_token_esc( buf, seps, pars, 0, token, siz, &truncated );
   break;
  case TMODE_LINE:
   utn_cs_copy( *buf, token );
   *buf = NULL;
   break;
  default:
   break;
 }
}

/*----------------------------------------------------------------------*/
/*
 * utn_cs_put
 *
 */

void utn_cs_put_cs( const char* token, char** buf, long* siz )
{
 char sep;
 sep = utn_esc_special_char( STRING_SEP );
 utn_cs_put_token( token, buf, sep, siz );
}

/*----------------------------------------------------------------------*/
/*
 * utn_cs_putw
 *
 */

void utn_cs_put_c( const char* token, char** buf, long* siz )
{
 utn_cs_put_token( token, buf, ' ', siz );
}

void utn_cs_put_i( const long token, char** buf, long* siz )
{
 TextWord word;
 utn_cs_put_token( utn_cs_write_i(token, word, WORD_SIZE), buf, ' ', siz );
}

void utn_cs_put_s( const short token, char** buf, long* siz )
{
 TextWord word;
 utn_cs_put_token( utn_cs_write_s(token, word, WORD_SIZE), buf, ' ', siz );
}

void utn_cs_put_r( const float token, char** buf, long* siz )
{
 TextWord word;
 utn_cs_put_token( utn_cs_write_r(token, word, WORD_SIZE), buf, ' ', siz );
}

void utn_cs_put_d( const double token, char** buf, long* siz )
{
 TextWord word;
 utn_cs_put_token( utn_cs_write_d(token, word, WORD_SIZE), buf, ' ', siz );
}



/*----------------------------------------------------------------------*/
/*
 * utn_cs_puts
 *
 */

void utn_cs_put_cn( const char* token, char** buf, long* siz )
{
 utn_cs_put_token( token, buf, '\0', siz );
}

/*----------------------------------------------------------------------*/
/*
 * utn_cs_get
 *
 */

void utn_cs_get_cs( char** buf, char* token, const long siz )
{
 utn_cs_get_token( buf, token, TMODE_STRING, siz );
}

/*----------------------------------------------------------------------*/
/*
 * utn_cs_get_c
 *
 */

void utn_cs_get_c( char** buf, char* token, const long siz )
{
 utn_cs_get_token( buf, token, TMODE_WORD, siz );
}

/*----------------------------------------------------------------------*/
/*
 * utn_cs_getl
 *
 */

void utn_cs_get_cl( char** buf, char* token, const long siz )
{
 utn_cs_get_token( buf, token, TMODE_LINE, siz );
}


long utn_cs_get_i( char** buf )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 return( utn_cs_read_i( token ) );
}
short utn_cs_get_s( char** buf )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 return( utn_cs_read_s( token ) );
}
logical utn_cs_get_q( char** buf )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 return( utn_cs_read_q( token ) );
}
float utn_cs_get_r( char** buf )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 return( utn_cs_read_r( token ) );
}
double utn_cs_get_d( char** buf )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 return( utn_cs_read_d( token ) );
}

long utn_cs_getd_i( char** buf, const long xdefault )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 if ( utn_cs_is_blank( token ) ) {
  return( xdefault );
 } else {
  return( utn_cs_read_i( token ) );
 }
}
short utn_cs_getd_s( char** buf, const short xdefault )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 if( utn_cs_is_blank( token ) ) {
  return( xdefault );
 } else {
  return( utn_cs_read_s( token ) );
 }
}
float utn_cs_getd_r( char** buf,  const float xdefault )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 if ( utn_cs_is_blank( token ) ) {
  return( xdefault ); 
 } else {
  return( utn_cs_read_r( token ) );
 }
}
double utn_cs_getd_d( char** buf, const double xdefault )
{
 TextWord token;
 utn_cs_get_c( buf, token, WORD_SIZE );
 if ( utn_cs_is_blank( token )) {
  return( xdefault );
 } else {
  return( utn_cs_read_d( token ) );
 }
}



long utn_cs_list_parse( char* buf, char** list, const long nsiz )
{
 long n;

 n = 0;
 while ( buf != NULL && !utn_cs_is_blank( buf ) ) {
  utn_cs_get_c( &buf, list[n++], nsiz );
 }
 return( n );
}


long utn_cs_count_char( const char* buf, const char c )
{
 char* ptr = (char*)buf;
 long n = 0;
 while ( *ptr )
 {
  if ( *ptr++ == c ) n++;
 }
 return n;
}


void utn_cs_compose_paren_list( char** items, const long n, char* buf, const long maxlen )
{
 long i; 
 /* Compose the output char* */
 if ( maxlen ) { }; /* Not used yet */

 buf[0] = '\0';
 if ( items[0])
 {
  strcat( buf, items[0] );
  if ( n > 0 )
   strcat( buf, "(" );
 }
 for ( i = 1; i<= n; i++ )
 {
  strcat( buf, items[i] );
  if ( i < n ) strcat( buf, "," );
 }
 if ( items[0] && n > 0 )
  strcat( buf, ")" );
}

/* Need to improve this to handle embedded commas*/
/* entries[0] to entries[*np] where [0] is the prefix  E0(E1,E2,...En-1) */
char** utn_cs_parse_paren_list( const char* buffer, long* np )
{
 return utn_cs_parse_parsep_list( buffer, "()", ',', np, UT_TRUE );
}

/* Parse comma list */
/* entries[0] to entries[*np-1] */
char** utn_cs_parse_comma_list( const char* buffer, long* np )
{
 return utn_cs_parse_parsep_list( buffer, NULL, ',', np, UT_FALSE );
}

void utn_cs_compose_comma_list( char** names, long n, char* buf, long maxlen )
{
 long i;
 long nleft = maxlen;
 char* p;
 long m;
 *buf = '\0';
 for ( i = 0; i < n; i++ )
 {
  p = names[i];
  m = strlen( p );
  if ( p && m < nleft )
  {
   if ( i > 0 ) strcat( buf, "," );
   strcat( buf, p );
   nleft -= m;
  }
 }
 
}


/* Obsolete? */
char** utn_cs_parse_parcom_list( const char* buffer, long* np, logical mode )
{
 return utn_cs_parse_parsep_list( buffer, "()", ',', np, mode );
}

char** utn_cs_parse_parsep_list( const char* buffer, char* pars, char sep, long* np, logical mode )
{
 long n = 0;
 char* ptr = NULL; 
 long len;
 char** result;
 char* next;
 char* tail= NULL;
 long nmax; 
 long extra = 0;
 *np = 0;
 if ( !buffer || utn_cs_is_blank( buffer ) ) return NULL;
 if ( mode ) extra = 1;
 nmax = utn_cs_count_char( buffer, sep ) + 1;
 result = calloc( nmax+1, UT_SZ_P );
 if ( pars && pars[0] )
  ptr = strchr( buffer, pars[0] );
 if ( ptr && mode )
 {
   len = ptr- buffer;
   result[0] = utn_cs_dup_siz( buffer, len );
   ptr++;
 } else if ( ptr == buffer ) {
   ptr++;
 } else if ( !ptr ) {
  ptr = (char*)buffer;
 }
 if ( pars && pars[1] )
  tail = strchr( ptr, pars[1] );

 while( ptr )
 {
  if ( n >= nmax ) 
  {
   nmax = n+extra;
   result = realloc( result,( nmax+1 )* UT_SZ_P);
  }
  next = strchr( ptr, sep );
  if ( next )
  {
   result[n+extra] = utn_cs_dup_siz( ptr, next-ptr );

   n++;
   ptr = ++next;
  }
  else if ( tail )
  {
   if ( tail - ptr > 0 )
   {
    result[n+extra] = utn_cs_dup_siz( ptr, tail-ptr );
    n++;
   }
   ptr = NULL;
  } else {
   result[n+extra] = utn_cs_dup( ptr );
   n++;
   ptr = NULL;
  }
 }
 *np = n;

 return result;

}

char** utn_cs_parse_parseq_list( const char* buffer, char quote, char sep, long* np )
{
 long n = 0;
 char* ptr = NULL; 

 char** result;
 long nmax; 
 char* last;
 int inquote = 0;

 *np = 0;
 if ( !buffer || utn_cs_is_blank( buffer ) ) return NULL;
 nmax = utn_cs_count_char( buffer, sep )+1;
 result = calloc( nmax+1, UT_SZ_P );
 ptr = (char*)buffer;
 last = ptr;
 while( ptr )
 {
  if ( n >= nmax ) 
  {
   nmax = n;
   result = realloc( result, ( nmax+1 )* UT_SZ_P);
  }
  if ( *ptr == quote)  
  {
   inquote = !inquote;
   ptr++;
  }
  else if ( (*ptr == '\0') || (!inquote && *ptr == sep ))
  {
   result[n] = utn_cs_dup_siz( last, ptr-last );
   n++;
   if ( *ptr == '\0' ) 
    ptr = NULL;
   else
   {
    ptr++;
    last = ptr;
   }
  } else {
   ptr++;
  }
 }
 *np = n;

 return result;

}


