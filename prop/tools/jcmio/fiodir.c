/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include <string.h>
#include <dirent.h>
#include <sys/stat.h>

struct FileDirectory_s {
 DIR* dirptr; 
 char* name;
};

FileDirectory utn_fio_dir_open( const char* idirname )
{
 char* dirname = (char*)idirname;
 FileDirectory dir;
 char* current = "./";   /* Unix current directory */
 dir = calloc( 1, sizeof( struct FileDirectory_s ));
 dir->name = utn_cs_dup( dirname );
 if ( !dirname || !(*dirname ))
  dirname = current;
 dir->dirptr = opendir( dirname );
 return dir;
}

void utn_fio_dir_close( FileDirectory dir )
{
 if ( !dir ) return;
 closedir( dir->dirptr );
 free( dir->name );
 free( dir );
}

char* utn_fio_dir_next_file( FileDirectory dir )
{
  struct dirent* dp;
  logical loop = UT_TRUE;


  while ( loop )
  {
   dp = readdir( dir->dirptr );
   loop = ( dp != NULL );
   if ( loop )
    loop = (utn_cs_eq( dp->d_name, "." ) || utn_cs_eq( dp->d_name, ".." ));
  }
/* dp is null, or a non-dot file */
  if ( dp )
   return dp->d_name;
  else
   return NULL;
}

long utn_fio_dir_next_filepath( FileDirectory dir, char* fn, long maxlen )
{
  struct dirent* dp;
  Filename name;
  dp = readdir( dir->dirptr );
  if ( dp )
  {
   utn_cs_copy_siz( dp->d_name, name, FN_SIZE );
   utn_sys_filepath( dir->name, name, " ", fn, maxlen );
   return utn_fio_stat_type( fn );
  }
  else
  {
   *fn = 0;
   return FIO_NOFILE;
  }
}

long utn_fio_dir_next_normal_file( FileDirectory dir, char* name, long maxlen )
{
	long type = FIO_NOFILE;
	logical loop;
	loop = UT_TRUE;

	while( loop )
	{
	 type = utn_fio_dir_next_filepath( dir, name, maxlen );
         loop = type != FIO_NOFILE && type != FIO_FILE_STD && type != FIO_FILE_DIR && type != FIO_FILE_EXE;
	}
	if ( type == FIO_FILE_DIR )
	 strcat( name, "/" );
	return type;
}

