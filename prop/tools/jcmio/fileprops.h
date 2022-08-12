/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/


typedef struct FileProps_s 
{
 integer type;
 logical ok;
 mode_t mode;
 dev_t  dev;
 dev_t  rdev;
 integer inode;
} *FileProps;

mode_t utn_file_props_mode( FileProps props );
FileProps utn_file_props( char* filename );
FileProps utn_file_props_alloc( void );
void utn_file_props_free( FileProps props );
logical utn_file_props_same( FileProps inprops, FileProps  outprops );
FileProps utn_file_props_alloc( void );
integer utn_file_type( char* filename );
