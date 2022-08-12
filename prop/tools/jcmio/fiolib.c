/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fio.h"
/*
 *------------------------------------------------------*
 */

  
/* Return size without FIO open */
integer utn_file_bytes( char* name )        
{
   FILE *fp;
   long  filesize = -1;
  
   fp = fopen( name, "r");
   if (fp) {
       fseek(fp, 0L, 2);
       filesize = ftell(fp);
       fclose(fp);
   }
   return filesize;
} 

integer utn_fio_file_tell( FioFile file  )
{
  integer filepos;
  FILE* fp = utn_fio_file_get_fptr( file );
  filepos = ftell(fp);
  return filepos;
}

integer utn_fio_file_bytes( FioFile file  )
{
  integer filesize;
  FILE* fp = utn_fio_file_get_fptr( file );
  fseek(fp, 0L, SEEK_END);
  filesize = ftell(fp);
  fseek(fp, 0L, SEEK_SET);
  return filesize;
}



FioFile utn_fio_tty_in( void )
{
 return utn_fio_list_tty_in( utn_fio_get_list());
}

FioFile utn_fio_tty_out( void )
{
 return utn_fio_list_tty_out( utn_fio_get_list());
}

FioFile utn_fio_tty_err( void )
{
 return utn_fio_list_tty_err( utn_fio_get_list());
}


logical utn_fio_any_eof( long mode )
{
 return utn_fio_list_any_eof( utn_fio_get_list(), mode );
}

void utn_fio_set_retry( logical q )
{
 utn_fio_list_set_retry( utn_fio_get_list(), q );
}

logical utn_fio_get_retry( void )
{
 return utn_fio_list_get_retry( utn_fio_get_list() );
}

FioFile utn_fio_file_ptr( long file )
{
 return utn_fio_list_get_file( utn_fio_get_list(), file );
}

void utn_fio_path_set( char* pathlist )
{
 utn_fio_list_set_path( utn_fio_get_list(), pathlist );
}


void utn_fio_path_list( void )
{
 utn_fio_list_list_path( utn_fio_get_list() );
}


logical utn_fio_inquire_path( char* name, char* pathname, long siz )
{
 logical q;
 q = utn_fio_list_inquire_path( utn_fio_get_list(), name, pathname, siz );
 return q;
}

logical utn_fio_tty_read( char* buf, long siz )
{
 return utn_fio_pager_tty_read( NULL, buf, siz );
}
 
logical utn_fio_tty_writel( char* buf )
{
 return utn_fio_pager_tty_write( NULL, buf, UT_FALSE );
}

logical utn_fio_tty_write( const char* buf )
{
 return utn_fio_pager_tty_write( NULL, buf, UT_TRUE );
}


