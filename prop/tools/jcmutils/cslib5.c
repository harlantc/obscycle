/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

logical utn_cs_white_char( char c )
{
 return ( c == ' ' || c == '\t' );
}



/* translate token:	chop a token off a string
		return a pointer past the token
Ignore whitespace
Space or tab terminated, or quote delimited

Replace special chars with ASCII escapes.
~r VT
~n VT
~l LF
~t HT
~b BS
~f FF

where ~ is the esc argument
*/



void utn_cs_translate_escapes( char* src, char* dest, char esc )
{
 char* ptr = src;

 char escapes[] = "rnltbf";
 char vals[] = { UT_CHAR_CR, UT_CHAR_CR, UT_CHAR_LF, UT_CHAR_HT, UT_CHAR_BS, UT_CHAR_FF };

 integer iesc;
 while( *ptr )
 {
  if ( *ptr == esc )
  {
   ++ptr;
   iesc = utn_cs_index_char( escapes, *ptr ); 
   if ( iesc > 0 )
    *dest++ = vals[iesc-1];
   else
    *dest++ = *ptr;
  } else {
   *dest++ = *ptr;
  }
  ptr++;
 }
 *dest = '\0';
}


/*	Filter a char* through a translation table	*/

void utn_cs_translate_string(char* source, char* lookup, char* trans, char* result)
/* char* to filter */
/* characters to translate */
/* resulting translated characters */
{
	char *sp;	/* pointer into source table */
	char *rp;	/* pointer into result */

	char c;
	integer k;
	/* scan source char* */
	sp = source;
	rp = result;
	while (*sp) {
		/* scan lookup table for a match */
		c = *sp++;
		k = utn_cs_index_char( lookup, c );
		if ( k > 0 )
		 c = trans[k-1];
		*rp++ = c;
	}

	/* terminate and return the result */
	*rp = 0;
}


char* utn_cs_fix_null(char* s)	/* Don't return NULL pointers! */
{
	if (s == NULL)
		return("");
	else
		return(s);
}

/*	ne_string_read_i: ascii string is integer......This is too
		inconsistent to use the system's	*/

logical utn_cs_is_number( char* s )
{
 char* p = NULL;
 logical ok = UT_FALSE;
 double x;
 if ( s && *s )
 {
  x= strtod( s, &p );
  ok = ( p == NULL ); 
 }
 return ok;
} 


void utn_cs_pad(char* s, integer len)	/* pad a string to indicated length */
{
  integer m;
  integer dn;
  char* ptr;

  m = strlen(s);
  dn = len - m;
  ptr = s + m;
  while ( dn > 0 )
  {
   *ptr = ' ';
    ptr++; dn--;
  }
  s[len] = 0;
}


/* Looks for match on cpos characters in array of strings.
 * Updates name[cpos] with next expected character.
 * Returns 1 for unique match, 0 for no match, -1 for multiple matches. */
integer utn_cs_match_complete( char** names, integer n, char* name, integer cpos )
{   
 integer q;
 logical matchflag;	/* did this command name match? */
 integer keyno = 1;
 integer keymatch = 0;
 char c = ' ';
/* first, we start at the first command and scan the list */

 matchflag = FALSE;
 for ( keyno = 1; keyno <= n; keyno++ )
 {
/* is this a match? */
   matchflag = utn_cs_eq_siz( name, names[keyno-1], cpos );
  /* if it is a match */
   if (matchflag) {

  /* if this is the first match, simply record it */
    if (keymatch == 0) {
      keymatch = keyno;
      c          = names[keyno-1][cpos];
      name[cpos] = c;
    } else {  /* Another but distinct match */
	/* if there's a difference, stop here */
      if ( c != names[keyno-1][cpos])
 	  return -1;
    }
  }
 }
 q = ( matchflag ? 1 : 0 );
 return q;
}


