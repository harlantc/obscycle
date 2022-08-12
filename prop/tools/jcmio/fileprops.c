/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <sys/types.h>
#include <unistd.h>
#include <sys/stat.h>
#include "tpiolib.h"
#include "fio.h"


FileProps utn_file_props_alloc( void )
{
 FileProps props = calloc( 1, sizeof( struct FileProps_s ));
 return props;
}

void utn_file_props_free( FileProps props )
{
 if ( props )
  free( props );
}

integer utn_file_type( char* filename )
{
 integer ft;
 FileProps props;
 props = utn_file_props( filename );
 ft = props->type; 
 utn_file_props_free( props );
 return ft;

}

FileProps utn_file_props( char* filename )
{
 FileProps props = NULL;
 integer rv;
 struct stat st;


 props = utn_file_props_alloc();
 rv = utn_fio_stat_type( filename );
 if ( rv != FIO_NOFILE )
 {
   props->ok = stat( filename, &st );
   props->mode = st.st_mode;
   props->dev = st.st_dev;
   props->rdev = st.st_rdev;
   props->inode = st.st_ino;
 } else {

   props->ok = -1;
   props->mode = 0;
   props->dev =  0;
   props->rdev = 0;
   props->inode = 0;

 }

 return props;

}


logical utn_file_props_same( FileProps inprops, FileProps  outprops )
{
  logical exists;
  logical same;
  if ( !inprops || !outprops ) return UT_FALSE;

  exists = ( inprops->type >= 0 && outprops->type >= 0 );
  same = ( inprops->dev == outprops->dev && inprops->inode == outprops->inode );
  return ( exists && same );
}


mode_t utn_file_props_mode( FileProps props )
{
 if ( props )
  return props->mode;
 else
  return 0;
}
