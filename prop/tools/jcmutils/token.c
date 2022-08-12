/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/*======================================================================*/
/*
 * Token parsing routines
 *
 */
/*======================================================================*/


/*----------------------------------------------------------------------*/
/*
 * check_pars
 *
 * Local routine to check parenthesis level in char*
 *
 */

static logical check_pars( const char source, long* paren_level, const logical paren_use, const char lpar, const char rpar )
{
 logical status;

 if ( !paren_use ) return( UT_TRUE );
 status = UT_FALSE;
 if ( source == lpar ) 
  (*paren_level)++;
 else if ( source == rpar )
  (*paren_level)--;
 else
  status = (*paren_level <= 0 );

 return( status );
}


void utn_cs_simple_token( char** buf, const char sep, char* dest, const long gsiz )
{
 char c;
 logical trn = 0;
 char seps[2] = " ";
 seps[0] = sep;
 c = utn_cs_copy_token_esc( buf, seps, NULL, '\0', dest, gsiz, &trn );
}



/*----------------------------------------------------------------------*/
/*
 * utn_cs_copy_token
 *
 * Copy from *buf to dest until you get to one of the seps characters lying
 * outside of all parentheses; or until dest is full up to siz.
 * 1996 Aug 26 JCM
 * 1997 Aug  5 JCM modified to return the separator found.
 * Args:
 *	buf		(i/o) 	Pointer to input buffer
 *	dest		(o)	Output buffer
 *	seps		(i)	Token separator characters
 *	pars		(i)	Left and right paren chars
 *	siz		(i)	Max allowed size of output buffer
 */



char utn_cs_copy_token_esc( char** buf, const char* seps, const char* ppars, const char esc, char* dest, const long gsiz, logical* truncated )
{
 char* pars = (char*)ppars;
 long siz = gsiz;
 long paren_level;
 logical paren_use = 0;
 logical loop;
 char lpar = '\0';
 char rpar = '\0';
 char* source;
 char found = 0;
 char c;
 logical is_esc = 0;

 *truncated = UT_FALSE;
 if ( !buf || !(*buf) ) {
  utn_cs_copy( " ", dest );
  return( 0 );
 }
 /* Advance to next non blank character.  */
 utn_cs_advptr( buf );
 source = *buf;
 if ( *source != '\0' ) {
  paren_level = 0;
  paren_use   = pars && !utn_cs_eq_begin( pars, "  " );
  if ( pars )
  {
   lpar = pars[0];
   rpar = pars[1];
  }
 /* Copy source characters to destination */
  loop = UT_TRUE;
  while ( *source != '\0' && loop ) {
   c = *source;
   if ( check_pars ( c, &paren_level, paren_use, lpar, rpar ) ) {
    loop = is_esc || !utn_cs_ss_char( seps, c );
    if( !loop ) found = c;
    }
   is_esc = ( esc && esc == c );
   if ( !is_esc && loop )
   {
    if ( siz ) 
    { 
       *dest++ = c; 
       siz--; 
    } else {
       *truncated = UT_TRUE;
    }
   }
   source++;
  }  /* end while */

 /* Advance source pointer to end of token */

  *buf = source;
 /* Advance to next non blank character.  */

  utn_cs_advptr( buf );
 }  /* end if non null */

 if ( utn_cs_is_blank( *buf ) ) 
 { 
  *buf = NULL;
 }
 *dest = '\0'; 
 return( found );
}

/*
 *  Return the parsed token in *result and a pointer to the remainder
 *  of the input buffer as the function value.
 *  If the maxlen character is 0, or result is NULL on input,
 *  the result is allocated internally
 *  and must be freed by the user. Otherwise it is assumed that result
 *  is preallocated and of size at least maxlen+1.
 *  
 *  The token is copied to a working buffer.
 *  For a fixed output, the working buffer is then copied to the output
 *  For an allocated output, the working buffer is copied to an allocated
 *   char*, or directly becomes the output if it is already allocated.
 *
 * The initial separator depth is 0, meaning that the left separator
 * is expected to be the first character of the char*.
 *
 *  The backslash character is used to escape separators.
 */
char*  utn_cs_parse_paren_token( char** pptr, char lsep, char rsep, char* result, long maxlen )
{
 TextBuf gbuf; /* The default working buffer, for small char*s - saves a malloc */
 char* buf = gbuf;     /* The working buffer */
 char* iptr = *pptr;     /* The input buffer pointer */
 char* optr;           /* The output buffer pointer */
 char* obuf = result;  /* The output buffer */
 logical out_alloc  = UT_FALSE;        /* The output is a fixed buffer */
 logical work_alloc = UT_FALSE;        /* The working buffer is an allocated buffer */
 logical last_esc = UT_FALSE;
 long depth = 0;       /* Separator depth */
 long pdepth = 0;      /* Paren depth */
 long input_size;           /* Input buffer size */
 long size = TEXT_SIZE;
 char c;
 char esc= '\\';
 char lpar = '(';
 char rpar= ')';
/* Check if output buffer is fixed */
 if ( maxlen && obuf ) 
 { 
  obuf[0] = UT_NULL_CHAR;
 } else {
  out_alloc = UT_TRUE;
  obuf = NULL;
 }

/* Skip spaces */
 while( *iptr == ' ' ) iptr++;

/* If the target char* is bigger than the standard working buffer,
   use an allocated working buffer */

 input_size = strlen( iptr )+2;
 if( input_size > TEXT_SIZE )
 {
  buf = malloc( input_size * UT_SZ_C );
  size = input_size;
  work_alloc = 1;
 }
 optr = buf;


 /* 
  *   Copy until we find a token delimeted by rsep.
  *   Usually lsep = [  rsep = ]
  *   However, skip lsep and rsep pairs as well as () pairs.
  *
  *  Thus
  *      bbb]ccc
  *  returns *result = bbb   value = ccc
  *      bbb[ccc]de]fg
  *  returns *result = bbb[ccc]de  value = fg
  *      bbb(a])cc]ee  
  *  returns *result = bbb(a])cc   value = ee
  */
 depth = 1;
 if ( *iptr == lsep ) iptr++;
 while ( depth > 0 && *iptr != UT_NULL_CHAR && size-- ) 
 {
    c = *iptr++;   
    if ( !last_esc )
    {
     if ( c == lpar ) pdepth++;
     if ( c == rpar ) pdepth--;
     if ( pdepth <= 0 ) 
     {
      if ( c == rsep ) depth--;
      if ( c == lsep ) depth++;
     }
    }
    last_esc = ( c == esc ) && !last_esc;
    if ( depth > 0 && !last_esc )
    {
     *optr = c; optr++;
    }
 }
 *optr = '\0';
/* Remove trailing blanks */
 while ( optr > buf && *(--optr) == ' ' )
  *optr = '\0';

/* Copy working buffer to output */
 
 if ( *buf ) 
 {
  if ( !out_alloc )
   utn_cs_copy_siz( buf, obuf, maxlen );
  else if  ( work_alloc ) 
   obuf = buf;
  else 
   obuf = utn_cs_dup( buf );
 } else if ( work_alloc ) {
  free( buf );
 }

/* Advance past trailing spaces in input buffer */

 if ( *iptr )
 {
 /* Skip spaces */
  while( *iptr == ' ' ) iptr++;
 }
 *pptr = iptr;
 return obuf;
}


/*
 * Perform the same task as utn_cs_parse_paren_token(), but check
 * that each opened separator or parenthesis is closed (in the
 * correct order) before the end of the token.  If so, status
 * is set to UT_TRUE, and the function returns normally.  If
 * not, status is set to UT_FALSE, and the function returns
 * NULL.
 *
 * The token is assumed to begin with the left separator,
 * regardless of whether the first character is lsep.
 *
 * A maximum of sizeof(long) levels of separator nesting
 * (including parentheses) is allowed.  If this limit is
 * exceeded, status is set to UT_FALSE, and the function
 * returns NULL.
 */
char*  utn_cs_parse_paren_token_validate( char** pptr, char lsep, char rsep, char* result, long maxlen, logical* status )
{
 TextBuf gbuf; /* The default working buffer, for small char*s - saves a malloc */
 char* buf = gbuf;     /* The working buffer */
 char* iptr = *pptr;     /* The input buffer pointer */
 char* optr;           /* The output buffer pointer */
 char* obuf = result;  /* The output buffer */
 logical out_alloc  = UT_FALSE;        /* The output is a fixed buffer */
 logical work_alloc = UT_FALSE;        /* The working buffer is an allocated buffer */
 logical last_esc = UT_FALSE;
 long sep_stack = 0;                     /* Stack of open separators (lsep and '(') */
 long depth = 0;                         /* Separator depth */
 long max_depth = 8*sizeof(sep_stack);   /* Maximum separator depth */
 long input_size;           /* Input buffer size */
 long size = TEXT_SIZE;
 char c;
 char esc= '\\';
 char lpar = '(';
 char rpar= ')';
/* Check if output buffer is fixed */
 if ( maxlen && obuf ) 
 { 
  obuf[0] = UT_NULL_CHAR;
 } else {
  out_alloc = UT_TRUE;
  obuf = NULL;
 }

/* Skip spaces */
 while( *iptr == ' ' ) iptr++;

/* If the target char* is bigger than the standard working buffer,
   use an allocated working buffer */

 input_size = strlen( iptr )+2;
 if( input_size > TEXT_SIZE )
 {
  buf = malloc( input_size * UT_SZ_C );
  size = input_size;
  work_alloc = 1;
 }
 optr = buf;


 /* 
  * Copy until all lsep and '(' characters (including
  * the lsep that marks the start of the token) are closed
  * by a matching rsep or ')', or the separator depth
  * exceeds max_depth, or we hit the end of the string.
  *
  * depth counts the number of open separators, and the
  * depth low-order bits of sep_stack denote the type
  * of each separator: a 0 bit indicates an lpar, while a
  * 1 bit indicates an lsep.
  */
 sep_stack = 1;
 depth = 1;
 if ( *iptr == lsep ) iptr++;
 while ( depth > 0 && depth <= max_depth && *iptr != UT_NULL_CHAR && size-- ) 
 {
    c = *iptr++;   
    if ( !last_esc )
    {
     if ( c == lpar )
     {
      sep_stack <<= 1;
      depth++;
     }
     else if ( c == rpar )
     {
      /* Break if the last open separator was lsep (if should have been lpar) */
      if ( sep_stack & 1 ) break;

      sep_stack >>= 1;
      depth--;
     }
     else if ( c == lsep )
     {
      sep_stack <<= 1;
      sep_stack++;
      depth++;
     }
     else if ( c == rsep )
     {
      /* Break if the last open separator was lpar (if should have been lsep) */
      if ( ~sep_stack & 1 ) break;

      sep_stack >>= 1;
      depth--;
     }
    }
    last_esc = ( c == esc ) && !last_esc;
    if ( depth > 0 && !last_esc )
    {
     *optr = c; optr++;
    }
 }

 /*
  * If we exited the loop with separators still open, signal
  * an error
  */
 if ( depth > 0 )
 {
  *status = UT_FALSE;
  return NULL;
 }

 *optr = '\0';
/* Remove trailing blanks */
 while ( optr > buf && *(--optr) == ' ' )
  *optr = '\0';

/* Copy working buffer to output */
 
 if ( *buf ) 
 {
  if ( !out_alloc )
   utn_cs_copy_siz( buf, obuf, maxlen );
  else if  ( work_alloc ) 
   obuf = buf;
  else 
   obuf = utn_cs_dup( buf );
 } else if ( work_alloc ) {
  free( buf );
 }

/* Advance past trailing spaces in input buffer */

 if ( *iptr )
 {
 /* Skip spaces */
  while( *iptr == ' ' ) iptr++;
 }
 *pptr = iptr;
 *status = UT_TRUE;
 return obuf;
}


char*  utn_cs_parse_term_token( char** pptr, char rsep )
{
 return utn_cs_parse_paren_token( pptr, UT_NULL_CHAR, rsep, NULL, 0 );
}



/*
 * Split char* into  <name><op><remainder>
 * where <op> is one of the characters in opset: !=, =, <, >
 * Ignore everything within parentheses, except if the ( character is part of opset.
 *
 */
void utn_cs_parse_assignment( char** pptr, char* name, long maxlen, char* op )
{
 char opset[] = "!=<>";
 char* iptr = *pptr;
 char* optr = name;
 char* op_ptr = op;
 long depth = 0;
 long pdepth = 0;
 int seek = UT_TRUE;
 long size = maxlen;
 char c;
 int doparens = UT_TRUE;    /* If ( is a valid opset character, don't check nested parens */

 *op_ptr = '\0';
 *optr   = '\0';
 if ( strchr( opset, '(' ) ) doparens = UT_FALSE;

/* Skip leading blanks */
 while( *iptr == ' ' ) iptr++;

 while ( seek && *iptr != '\0' && size-- )
 {
  c = *iptr;
  if ( c == '('  && doparens ) pdepth++;
  if ( c == ')'  && doparens ) pdepth--;
  if ( c == '['  ) depth++;
  if ( c == ']'  ) depth--;
  if ( pdepth == 0 && depth == 0 )
  {
    if ( strchr( opset, c ))
    {
     if ( c == '!' && *(iptr+1) != '=' )  /* !> disallowed; ! must be part of != */
      seek = 1;
     else
      seek = 0;
    }
  }
  if ( seek ) *optr++ = *iptr++; 
 }
 *optr = '\0';

/* Delete trailing spaces */
  optr--;
  while( *optr == ' ' ) 
  {
   *optr = '\0';
   optr--;
  }

  if ( !*iptr )
  {
   *pptr = NULL;
   return;
  }

/* Copy operator characters to op char*; allow !=, <=, ==, etc but disallow =! 
 * since in chip=!circle(..) the ! belongs to the region not the operator  */
  if ( !seek )
  {
   *op_ptr = *iptr++;
   op_ptr++;
   if ( strchr( opset, *iptr ) && *iptr!= '!' ) 
   {
    *op_ptr = *iptr++;
    op_ptr++;
   }
   *op_ptr = '\0';
/* Copy remaining characters to value char* by returning tptr */
  }

/* Skip spaces */
 while( *iptr == ' ' ) iptr++;

 *pptr = iptr;

}






/* Specialized routine: space separated words but quotes supported */

char utn_cs_copy_token_arg( char** buf, char* item, const long gsiz )
{
 char squote = '\'';
char* dest = item;
 char dquote = '"';
 char esc = '\\';
 logical quoted;
 long siz = gsiz;


 logical loop;
 char* source; 
 static char* ptr = NULL;
 char this_quote = ' ';
 char found = 0;
 char c;
 logical is_esc = 0;
 if ( ptr== NULL ) ptr = *buf;

 if ( !buf || !(*buf) ) {
  utn_cs_copy( " ", dest );
  return( 0 );
 }
 /* Advance to next non blank character.  */
 utn_cs_advptr( buf );
 source = *buf;
 quoted = UT_FALSE;
 if ( *source != '\0' ) {

  c = *source;
  quoted = ( c == squote || c == dquote );
  if ( quoted ) { 
   source++; 
   this_quote = c;
   c = *source;  
  }

 /* Copy source characters to destination */
  loop = UT_TRUE;
  while ( c != '\0' && loop ) {
   c = *source;
   is_esc = ( c == esc );
   if ( is_esc )
   {

     source++;
     c = *source;
   } else {
     if ( quoted && c == this_quote ) 
     {

       loop = UT_FALSE;
       c = '\0';
     }
   }
   if ( (c == ' ' && !quoted ) || ( c == '\0' ))  loop = UT_FALSE;

   if ( c && loop && siz ) { *dest++ = c; siz--; } 

   source++;
   c = *source;

  }  /* end while */

 /* Advance source pointer to end of token */
 *dest = '\0'; 


  *buf = source;

 /* Advance to next non blank character.  */
  if ( *buf )
   utn_cs_advptr( buf );

 }  /* end if non null */
 if ( *buf && utn_cs_is_blank( *buf ) ) 
 {
   *buf = NULL;
 }



 return( found );
}


