/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"
#include <unistd.h>
#include <string.h>

/*
 * Special local+global path list for paramlib compatibility
 * Repeatedly expand all environment variables in a char*.
 * Each environment variable reference begins with a $ and ends with
 * a space, colon or semicolon.
 */

void utn_fio_parpath_free( PathList plist )
{
 if ( !plist ) return;
 utn_cs_namelist_free( plist->local );
 utn_cs_namelist_free( plist->global );
 free( plist );
}


PathList utn_fio_parpath_expand( char* path )
{
 PathList plist;
 NameList list;
 char mark;
 Filename word;
 Filename dest;
 logical truncated = UT_FALSE;

 char esc='\\';
 char* ptr = path; 
 plist = calloc( 1, sizeof( struct PathList_s ));
 plist->local = utn_cs_namelist_alloc();
 plist->global = NULL;  
 list = plist->local;
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

/* The semicolon marks the start of the global list */
   if ( mark == ';' )
   {
    plist->global = utn_cs_namelist_alloc();
    list = plist->global;
   }
 }

 return plist;
}


logical utn_fio_parpath_search( PathList pathlist, char* name, char* mode, char* pathname, integer maxlen )
{
  
  logical global;
  logical local;
  logical absolute;
  logical q = UT_FALSE;
  utn_cs_copy( " ", pathname );

 utn_cs_copy_siz( name, pathname, maxlen ); 
 q = utn_fio_inquire( pathname );  /* Always search directly first; this defeats the IRAF-style back compat for certain modes */
 if ( q ) return q;

  global = strpbrk(mode, ">") != NULL;  /* skip if > */
  local = strpbrk(mode, "<") != NULL;  /* pick if < */

  utn_cs_copy( " ", pathname );
  if ( global && local ) return(UT_FALSE);
/* absolute path? */
  absolute = ( name[0] == '.' && name[1] == '/' ) || ( name[0] == '/' );
  if ( !pathlist || absolute )
    q = utn_fio_pathlist_search( NULL, name, mode, pathname, FN_SIZE );

  if ( !q && !global )
    q = utn_fio_pathlist_search( pathlist->local, name, mode, pathname, FN_SIZE );
  
  if ( !q && !local )
    q = utn_fio_pathlist_search( pathlist->global, name, mode, pathname, FN_SIZE );

  return q;
}
