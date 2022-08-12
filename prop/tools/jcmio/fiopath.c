/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fiolist.h"
void utn_fio_list_free_path( FioFileList f )
{
 if ( f->pathlist )
 {
   utn_cs_namelist_free( f->pathlist );
   f->pathlist = NULL;
 }
}

void utn_fio_list_set_path( FioFileList f, char* pathlist )
{


 TextBuf jcmpath;
 utn_fio_list_free_path( f );

 if ( !pathlist )
 {
  utn_sys_getenv( "JCMPATH", jcmpath );
  if ( utn_cs_is_blank( jcmpath ))
   utn_sys_getenv( "JCMLIBDATA", jcmpath );
  pathlist = jcmpath;

 } 
 f->pathlist = utn_fio_pathlist_expand( pathlist );
}

void utn_fio_list_list_path( FioFileList f )
{
 long i;
 NameList list = f->pathlist;
 if ( !list )
  utn_fio_msg( "JCMPATH search path is not set" );
 else 
 {
  utn_fio_msg( "JCMPATH directory search path:" );
  for ( i = 1; i <= list->n; i++ ) {
   utn_fio_msg( list->data[i-1] );
  }
 }
}


/* If exist is false, just return a copy of the input name.
 * If exist is true, search for the file in the path and return the first
 * valid path found, or NULL if the file is not found at all.
 */

char* utn_fio_expand_path( NameList pathlist, char* filename, logical exist )
{ 
 char* truefile = NULL;
 Filename pathfile;
 logical q = UT_TRUE;
 integer k;
/* Null terminate for safety */
 utn_cs_copy_siz( filename, pathfile, FN_SIZE );
 k = utn_cs_ends( pathfile );
 pathfile[k] = '\0';
 if ( exist )
  q = utn_fio_pathlist_search( pathlist, filename, "f", pathfile, FN_SIZE );
 if ( q )
  truefile = utn_cs_dup( pathfile );
 return truefile;
}


logical utn_fio_list_inquire_path( FioFileList f, char* name, char* pathname, long siz )
{
/* Test for existence */
 return utn_fio_pathlist_search( f->pathlist, name, "f", pathname, siz );

}

