/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"
#include <unistd.h>


/*
 * Special local+global path list for paramlib compatibility
 * Repeatedly expand all environment variables in a char*.
 * Each environment variable reference begins with a $ and ends with
 * a space, colon or semicolon.
 */

NameList utn_fio_pathlist_expand( char* path )
{
 NameList list;
 char mark;
 Filename word;
 Filename dest;
 logical truncated = UT_FALSE;

 char esc='\\';
 char* ptr = path; 
 list = utn_cs_namelist_alloc();
 while( ptr != NULL && *ptr != '\0' )
 {
/* Copy up to next : or ; or space to get name of env var; make note of separator */
   mark = utn_cs_copy_token_esc( &ptr, " :;", "", esc, word, FN_SIZE, &truncated );

   while( word[0] == '$' ) {
/* Found an environment variable */
/* Recursively expand the variable into the output buffer */
    utn_sys_getenv( word+1, dest );
    utn_cs_copy( dest, word );
   }

   utn_cs_namelist_add( list, word );
 }
 return list;
}


/*
 * Search a path list, testing access for a file.
 * Compatible with SAO parameter interface search path.
 * Repeatedly expands environment variable names in the path list.
 */

char* utn_fio_pathlist_search_list( NameList list, char* name, char* mode )
{
 char* dir; 
 long n;
 long i;
 Filename filename;
 char* result = NULL;
 if ( list ) 
  n = utn_cs_namelist_size( list );
 else
  n = 1;

   for ( i = 1; i <= n; i++ )
   {
    dir = utn_cs_namelist_entry( list, i );
    utn_sys_filepath( dir, name, " ", filename, FN_SIZE );
    if ( utn_fio_posix_access( filename, mode ) )
    {
     result = utn_cs_dup( filename );
     return result;
    }
   }   

  return result;
}

logical utn_fio_pathlist_search( NameList list, char* name, char* mode, char* pathname, integer maxlen )
{
 char* dir; 
 long n;
 long i;
 Filename filename;
 logical q;
 utn_cs_copy_siz( name, pathname, maxlen ); 
 q = utn_fio_inquire( pathname );  /* Always search directly first */
 if ( q ) return q;

 if ( list ) 
  n = utn_cs_namelist_size( list );
 else
  n = 1;

   for ( i = 1; i <= n; i++ )
   {
    dir = utn_cs_namelist_entry( list, i );
    utn_sys_filepath( dir, name, " ", filename, FN_SIZE );
    if ( utn_fio_posix_access( filename, mode ) )
    {
     utn_cs_copy_siz( filename, pathname, maxlen );
     return( UT_TRUE );
    }
   }   

  return( UT_FALSE );
}


