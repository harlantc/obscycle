/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"


void utn_tpio_g_cs( char* prompt, char** pptr, char* token, long siz )
{
 utn_tpio_token(  prompt, pptr, TMODE_STRING, token, siz );
}

void utn_tpio_g_c( char* prompt, char** pptr, char* token, long siz )
{
 utn_tpio_token(  prompt, pptr, TMODE_WORD, token, siz );
}



/* TOKEN PARSER IO using global tpio handles */
/*----------------------------------------------------------------------*/
/*
 * ccop
 * Uses cop.h
 */

void utn_tpio_token( char* prompt, char** pptr, long mode, char* token, long siz )
{
 utn_tpio_tp_token( NULL, NULL, NULL, prompt, pptr, mode, token, siz );
}

void utn_tpio_cs( char* prompt, char* token, long siz )
{
 char** pptr = NULL;
 utn_tpio_tp_token( NULL, NULL, NULL, prompt, pptr, TMODE_STRING, token, siz );
}


void utn_tpio_cc( char* prompt, char* buf, long siz )
{
 utn_tpio_tp_cc( NULL, NULL, NULL, prompt, buf, siz );
}

/*----------------------------------------------------------------------*/
/*
 * icop
 * Uses cop.h readproto.h
 */

long utn_tpio_i( char* prompt )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 return( utn_cs_read_i( token ) );
}


double utn_tpio_d( char* prompt )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 return( utn_cs_read_d( token ) );
}

short utn_tpio_s( char* prompt )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 return( utn_cs_read_s( token ) );
}

float utn_tpio_r( char* prompt )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 return( utn_cs_read_r( token ) );
}

/*----------------------------------------------------------------------*/
/*
 * ccopw
 * Uses cop.h
 *
 */

void utn_tpio_c( char* prompt, char* token, long siz )
{
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, siz );
}

/*----------------------------------------------------------------------*/
/*
 * ccopl
 * Uses cop.h
 *
 */

void utn_tpio_cl( char* prompt, char* token, long siz )
{
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_LINE, token, siz );
}

long utn_tpio_d_i( char* prompt, long xdefault )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 if ( utn_cs_is_blank( token ) ) {
  return( xdefault );
 } else {
  return( utn_cs_read_i( token ) );
 }
}

double utn_tpio_d_d( char* prompt, double xdefault )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 if ( utn_cs_is_blank( token ) ){
  return( xdefault );
 } else {
  return( utn_cs_read_d( token ) );
 }
}

short utn_tpio_d_s( char* prompt, short xdefault )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 if ( utn_cs_is_blank( token ) ) {
  return( xdefault );
 } else {
  return( utn_cs_read_s( token ) );
 }
}

float utn_tpio_d_r( char* prompt, float xdefault )
{
 TextCard token;
 char** pptr = NULL;
 utn_tpio_token( prompt, pptr, TMODE_WORD, token, CARD_SIZE );
 if ( utn_cs_is_blank( token ) ) {
  return( xdefault );
 } else {
  return( utn_cs_read_r( token ) );
 }
}



double utn_bi_tpio_d( char* prompt, char** ptr )
{
 if ( !ptr )
  return utn_tpio_d( prompt );
 else
  return utn_cs_get_d( ptr );
}

void utn_bi_tpio_c( char* prompt, char* buf, long size, char** ptr )
{
 if ( !ptr )
  utn_tpio_c( prompt, buf, size );
 else
  utn_cs_get_c( ptr, buf, size );
}

