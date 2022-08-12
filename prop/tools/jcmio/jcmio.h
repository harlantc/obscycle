/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef JCMIO_H
#define JCMIO_H
#include "jcmutils.h"

#define UT_VERBOSE_FIO 10
#define UT_VERBOSE 1
typedef struct UtilState_s *UtilState;
typedef struct UtilVersionData_s *UtilVersionData;
typedef struct FioRaw_s *FioRaw;

typedef struct FileBuffer_s {
 char* data;
 long size;         /* Bytes in buffer */
 long top;          /* Bytes filled */
 long chunk_size;   /* Bytes to I/O at once */
 long* line;          /* Array of pointers to start of each line */
/* These are long offsets so we can realloc the data without recalc */
 long nmax;     /* Size of pointer array */
 long n;            /* Number of lines found */
 long pos;      /* Current line */
} *FileBuffer;

/* The File IO objects */
typedef struct FioFileList_s   *FioFileList;
typedef struct FioFileObject_s *FioFile;
typedef struct FioStackData_s  *FioStack;
typedef struct FioPager_s      *FioPager;
typedef struct FioDriver_s *FioDriver;
typedef struct utn_tpio_s          *Tpio;

void utn_fio_pop( long mode );
void utn_fio_push( FioFile file );

logical utn_fio_read_line_alloc( FioFile file, char** line, long* lmax );
logical utn_fio_get_line_alloc( FioFile file, char** bufp, long* sizp, logical dynamic );
logical utn_fio_put_line_n( FioFile file, const char* buf, logical strip, logical nl, long size );
logical utn_fio_write_text_n( FioFile file, char* buf, integer n );
logical utn_fio_read_line_raw( FioFile file, char* line, integer maxlen );
logical utn_fio_file_raw( FioFile file );
void utn_fio_file_set_raw( FioFile file );
void utn_tpio_exit( void );
void utn_fio_raw_set_resync( FioFile file, void (*resync)( void*, int ), void* driver );
void utn_tpio_open_raw( void (*resync)( void*, int ), void* driver );
FioFile utn_fio_open_cwm( FioFileList list, char* filename, mode_t mode );
FioFile utn_fio_open_crm( FioFileList list, char* filename, mode_t mode );
void utn_tpio_cmdargs2( 
 char** args,         /* (i) Command line args */
 long nargs,       /* (i) Number of args */
 logical* interact,   /* (o) Interactive? */
 logical* opts,        /* (o) Command line was not blank */
 logical fail_on_err
);
integer utn_fio_file_bytes( FioFile file  );
struct FioDriver_s {
 char* name;
 logical exist;  /* Pre existing file on open */
 char mode[8];   /* C mode */
 FioFile (*open)( FioDriver, NameList, char*, integer, GenError* );
 logical (*close)( FioFile );
};
integer utn_fio_file_tell( FioFile file  );
logical utn_fio_pathlist_search_namelist( NameList list, char* name, char* mode, char* result );
NameList utn_cs_namelist_assign( char** names, integer n );
logical utn_fio_pathlist_search( NameList list, char* name, char* mode, char* result, integer maxlen );
char* utn_fio_pathlist_search_list( NameList list, char* name, char* mode );

FioDriver utn_fio_driver_search( GenStack dlist, char* name, GenError* errorp );
FioFile utn_fio_create( FioDriver fio, char* name );
FioDriver utn_fio_driver_select( FioFileList list, char* name );
FioDriver utn_fio_driver_alloc( char* name, logical exist, char* cmode );
FioDriver utn_fio_driver( FioFile file );
FioFile utn_fio_fcreate( FioFileList list, char* name, char* mode );




FileBuffer utn_fio_file_buffer_alloc( long size, long chunk_size, long maxlines );
long utn_fio_file_buffer_lines( FileBuffer buffer );
void utn_fio_file_buffer_free( FileBuffer buffer );
void utn_fio_file_buffer_clear( FileBuffer buffer );
void utn_fio_file_buffer_status( FileBuffer buffer );
void utn_fio_file_buffer_find_lines( FileBuffer buffer );
logical utn_fio_file_buffer_read_line( FileBuffer buffer, char* line, long maxlen );
logical utn_fio_file_buffer_seek( FileBuffer buffer, long n );
void utn_fio_file_buffer_write_line( FileBuffer buffer, char* line );
logical utn_fio_file_buffer_read( char* filename, FileBuffer buffer );
int utn_fio_file_buffer_write( FileBuffer buffer, char* filename );
void utn_fio_file_buffer_write_partial_line( FileBuffer buffer, char* line );
void utn_fio_file_buffer_write_general_line( FileBuffer buffer, char* line, logical newline );
void utn_fio_file_buffer_msg( char* buf, logical newline );
void utn_fio_file_buffer_output( FileBuffer buffer );
long utn_fio_file_buffer_pos( FileBuffer buffer );
logical utn_fio_file_buffer_move( FileBuffer buffer, long n);
logical utn_fio_file_buffer_skip_blanks( FileBuffer buffer );
logical utn_fio_file_buffer_print_line( FileBuffer buffer );
void utn_fio_file_buffer_output( FileBuffer buffer );
void utn_fio_file_buffer_insert_lines( FileBuffer buffer, char** lines, long nlines );
void utn_fio_file_buffer_put_line( FileBuffer buffer, char* line );



void utn_fio_list_free_path( FioFileList f );
void utn_fio_list_set_path( FioFileList f, char* pathlist );
void utn_fio_list_list_path( FioFileList f );
char* utn_fio_expand_path( NameList pathlist, char* filename, logical exist );
logical utn_fio_list_inquire_path( FioFileList f, char* name, char* pathname, long siz );

void utn_ccio_set( FioRaw f, long fd, char* ptr, long* fptr );
void utn_ccio_set_raw( char* ptr, long* fptr, logical nocr );
FioRaw utn_fio_raw_init( integer fd, logical nocr );
void utn_cio_set_raw( FioRaw raw, long fd );
void utn_cio_clear_raw( FioRaw list, long fd );
long utn_ccio_get( FioRaw f, long fd, char* ptr, long* fptr );



logical utn_fio_write_line( FioFile file, const char* buf );
logical utn_fio_write_text_n( FioFile file, char* buf, integer n );
logical utn_fio_write_text( FioFile file, char* buf );
long utn_fio_terminate_buf( long pos, const char* buf, char* buf2, logical strip, logical nl, long siz );
logical utn_fio_read_line( FioFile file, char* line, long lmax );
logical utn_fio_read_line_alloc( FioFile file, char** line, long* lmax );
logical utn_fio_test_retry( FioFile tty_err, char* fullname );


long utn_fio_file_status( FioFile file, long mode );
void utn_fio_set_status_pos( FioFile file, logical ios, long pos );
void utn_fio_file_set_status( FioFile file, logical status );
void utn_fio_delta_pos( FioFile file, long pos );
void utn_fio_file_rewind( FioFile file );
void utn_fio_line_reset( FioFile file );
void utn_fio_file_clear( FioFile file );
long utn_fio_file_get_cfd( FioFile file );
logical utn_fio_seek_s( FioFile file, long pos );
void utn_fio_file_set_pos( FioFile file, long pos );
logical utn_fio_inquire_path( char* name, char* pathname, long siz );

logical utn_fio_get_line( FioFile file, char* buf, long siz );
void utn_fio_tty_pager_query( FioPager pager );
logical utn_fio_have_raw( GenStack files );

void utn_init_jcmlib( void );
void utn_free_jcmlib( void );
void utn_dbg_set( long g );
long utn_dbg_get( void );
UtilState utn_state( void );
UtilState utn_state_alloc( void );
void utn_state_free( UtilState state );
void utn_ver_jcmlib( char* ver, const long maxlen );
void utn_init_state( UtilState state );


void utn_tpio_in( char* buf );
void utn_tpio_tp_in( FioFileList list, FioStack iofs, char* buf );
void utn_tpio_open_raw( void (*resync)( void*, int ), void* driver );
void utn_tpio_out( char* buf, logical append, logical syserr );
void utn_tpio_tp_out( FioFileList list, FioStack iofs, char* buf, logical append, logical syserr );
void utn_loop_do( Tpio tpio, FioStack iofs );
void utn_loop_end( Tpio tpio, FioStack iofs );
void utn_loop_goto( FioStack iofs, long addr );
void utn_loop_echo( Tpio tpio, FioStack iofs );
void utn_tpio_disp( long i );
void utn_tpio_tp_disp( Tpio tpio, FioStack iofs, FioFileList list, long i );
void utn_tpio_print_state( void );
void utn_tpio_tp_print_state( Tpio tpio, FioStack iofs );
void utn_tpio_tp_cc( Tpio tpio, FioFileList list, FioStack iofs, char* prompt, char* buf, long siz );
void utn_tpio_tp_cc_start_check( Tpio tpio, char* buf, logical* mode );
void utn_tpio_tp_cc_cmd( Tpio tpio, FioFileList list, FioStack iofs, char* prompt, logical* mode, char* buf, long siz );
void utn_tpio_tp_cc_check( Tpio tpio, FioFileList list, FioStack iofs,
 char* prompt, logical* eot, logical* found_esc, char* buf, long siz );
void utn_tpio_tp_cc_esc( Tpio tpio, FioFileList list, FioStack iofs,
  char* ecmd, char* prompt, logical* eot, 
 logical* found_esc, char* buf, long siz );
void utn_tpio_setbuf( char** arr, long n );
void utn_tpio_tp_setbuf( Tpio tpio, FioStack iofs, char** arr, long n );

void utn_tpio_cmdargs( 
 char** args,         /* (i) Command line args */
 long nargs,       /* (i) Number of args */
 logical* interact,   /* (o) Interactive? */
 logical* opts        /* (o) Command line was not blank */
);
void utn_tpio_cmdargs2( 
 char** args,         /* (i) Command line args */
 long nargs,       /* (i) Number of args */
 logical* interact,   /* (o) Interactive? */
 logical* opts,        /* (o) Command line was not blank */
 logical fail_on_err
);
void utn_tpio_dbg( void );
void utn_tpio_tp_dbg( Tpio tpio );
void utn_tpio_tp_init( Tpio tpio );
char** utn_tpio_set( Tpio tpio, FioStack iofs );
logical utn_tpio_getbuf( Tpio tpio, FioStack iofs, char* prompt );
void utn_tpio_exit( void );
void utn_tpio_tp_token( Tpio tpio, FioFileList list, FioStack iofs, char* prompt, char** pptr, long mode, char* token, long siz );
void utn_tpio_free( Tpio tpio );
void utn_tpio_cclear( Tpio tpio, long level );
char** utn_tpio_set( Tpio tpio, FioStack iofs );
void utn_tpio_loc( Tpio tpio, FioStack iofs, long* level, long* pos );
void utn_tpio_level_check( Tpio tpio, long level );
Tpio utn_tpio_get( void );
Tpio utn_tpio_data_init( void );

void utn_fio_list_free( FioFileList list );
FioFileList utn_fio_list_init( void );
void utn_fio_list_set_retry( FioFileList fiolist, logical q );
logical utn_fio_list_get_retry( FioFileList fiolist );
long utn_fio_list_n( FioFileList f );
FioFile utn_fio_list_get_file( FioFileList f, long fid );
logical utn_fio_list_any_eof( FioFileList f, long mode );
FioFile utn_fio_list_tty_in( FioFileList f );
FioFile utn_fio_list_tty_out( FioFileList f );
FioFile utn_fio_list_tty_err( FioFileList f );
long utn_fio_list_find_free_fid( GenStack files );
long utn_fio_list_add( GenStack files, FioFile file );

FioFile utn_fio_alloc( void );
FioDriver utn_fio_driver_select( FioFileList list, char* name );
FioFile utn_fio_open_cio_raw( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
void utn_fio_set_umask( FioFileList list, integer umask );
integer utn_fio_get_umask( FioFileList list );
FioFile utn_fio_create( FioDriver driver, char* name );
FioDriver utn_fio_driver( FioFile file );
FioDriver utn_fio_driver_search( GenStack dlist, char* name, GenError* errorp );
GenStack utn_fio_std_drivers( void );
FioDriver utn_fio_driver_alloc( char* name, logical exist, char* cmode );
FioFile utn_fio_open_gen( GenStack drivers, NameList pathlist, char* filename, char* mode, integer umask, GenError* errorp );
FioFile utn_fio_open_seq( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
FioFile utn_fio_open_seq( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
FioFile utn_fio_open_null( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
FioFile utn_fio_open_tty( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
FioFile utn_fio_open_cio_raw( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
FioFile utn_fio_open_cio( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
FioFile utn_fio_list_open_file( FioFileList list, const char* filename, char* mode );

FioFileList utn_fio_get_list( void );
void utn_fio_parpath_free( PathList plist );
PathList utn_fio_parpath_expand( char* path );
logical utn_fio_parpath_search( PathList pathlist, char* name, char* mode, char* pathname, integer maxlen );
void utn_fio_set_umask( FioFileList list, integer umask );
integer utn_fio_get_umask( FioFileList list );


logical utn_fio_pathlist_search_namelist( NameList list, char* name, char* mode, char* result );
NameList utn_cs_namelist_assign( char** names, integer n );
logical utn_fio_pathlist_search( NameList list, char* name, char* mode, char* result, integer maxlen );
char* utn_fio_pathlist_search_list( NameList list, char* name, char* mode );
void utn_fio_file_set_type( FioFile file, char* type );
logical utn_fio_file_close_null( FioFile file );
logical utn_fio_file_close_seq( FioFile file );
logical utn_fio_file_close_cio( FioFile file );
logical utn_fio_file_close_raw( FioFile file );

FioStack utn_fio_iostack_init( void );
void utn_fio_iostack_free( FioStack iofs );
FioFile utn_fio_iostack_file( FioStack iofs, long mode );
void utn_fio_iostack_realloc( FioStack iofs, long n );
void utn_fio_iostack_ipush( FioStack iofs, FioFileList list, FioFile file );
void utn_fio_iostack_opush( FioStack iofs, FioFileList list, FioFile file );
void utn_fio_iostack_ipop( FioStack iofs, FioFileList list, long mode );
void utn_fio_iostack_opop( FioStack iofs, FioFileList list, long mode );
long utn_fio_iostack_get_level( FioStack iofs, long mode );
void utn_fio_iostack_iclear( FioFileList list, FioStack iofs );
void utn_fio_iostack_reinit( FioFileList list, FioStack iofs );
void utn_fio_stack_reinit( void );
void utn_fio_iostack_set( FioStack iofs, FioFileList list, FioFile file, long mode );
void utn_fio_basic_print_state( void );
logical utn_fio_input_test_eof( void );
void utn_fio_std_msg( char* buf, logical newline );
void utn_fio_msg_endline( void );
logical utn_fio_tty_input( char* buf, long siz );
void utn_fio_dashline( long n );
void utn_fio_iostack_dashline( FioStack iofs, long n );
FioFile utn_fio_iostack_get_entry( FioStack iofs, char* io_type, long level, char* buf, char* status );
void utn_fio_iostack_print_top( FioStack iofs, char* type, char* pfx );


void utn_fio_stack_print_state( void );
void utn_fio_iostack_print_state( FioStack iofs );
void utn_fio_iostack_print_stack( FioStack iofs, char* type, long top );
void utn_fio_iostack_set_detab( FioStack f, logical q );
void utn_fio_iostack_set_input( FioStack iofs, FioFileList list );
FioFile utn_fio_iostack_input_entry( FioStack iofs, long level );
long utn_fio_stack_status( long mode );
logical utn_fio_stack_ok( long mode );
logical utn_fio_stack_eof( long mode );
void utn_fio_iostack_rewind_input( FioStack iofs );

char* utn_fio_tmpname( void );

unsigned int* utn_state_seed( UtilState state );
FioFileList utn_state_fio_list( UtilState state );
void utn_state_dbg_set( UtilState state, long g );
long utn_state_dbg_get( UtilState state );
ErrorGlobal utn_state_error_global( UtilState state );
UtilVersionData utn_state_ver_data( UtilState state );
Tpio utn_state_tpio( UtilState state );

void utn_fio_pager_set( FioPager pager, logical q ); 
void utn_fio_pager_set_length( FioPager pager, long page_length ); 
void utn_fio_pager_print_state( FioPager pager ); 
logical utn_fio_pager_tty_read( FioPager pager, char* buf, long siz ); 
logical utn_fio_pager_tty_write( FioPager pager, const char* buf,logical newline ); 
void utn_fio_pager_free( FioPager pager );
FioPager utn_fio_pager_init( void );
FioPager utn_fio_pager( FioFileList list );

void utn_ver_jcmlib( char* ver, const long maxlen ); 
logical utn_ver_init( const char* prog, const char* ver ); 
void utn_ver_msg( void ); 
void utn_version_data_free( UtilVersionData verdata );
UtilVersionData utn_version_data_create( void );
UtilVersionData utn_ver_data( void );
logical utn_ver_init_data( UtilVersionData verdata, const char* prog, const char* ver );

void utn_tpio_g_cs( char* prompt, char** pptr, char* token, long siz );
void utn_tpio_g_c( char* prompt, char** pptr, char* token, long siz );
void utn_tpio_token( char* prompt, char** pptr, long mode, char* token, long siz );
void utn_tpio_cs( char* prompt, char* token, long siz );
void utn_tpio_cc( char* prompt, char* buf, long siz );
long utn_tpio_i( char* prompt );
double utn_tpio_d( char* prompt );
short utn_tpio_s( char* prompt );

float utn_tpio_r( char* prompt );
void utn_tpio_c( char* prompt, char* token, long siz );
void utn_tpio_cl( char* prompt, char* token, long siz );
long utn_tpio_d_i( char* prompt, long xdefault );
double utn_tpio_d_d( char* prompt, double xdefault );
short utn_tpio_d_s( char* prompt, short xdefault );
double utn_tpio_d_d( char* prompt, double xdefault );
short utn_tpio_d_s( char* prompt, short xdefault );
float utn_tpio_d_r( char* prompt, float xdefault );
double utn_bi_tpio_d( char* prompt, char** ptr );
void utn_bi_tpio_c( char* prompt, char* buf, long size, char** ptr );

void utn_fio_list_free_path( FioFileList f );
void utn_fio_list_set_path( FioFileList f, char* pathlist );
void utn_fio_list_list_path( FioFileList f );
char* utn_fio_expand_path( NameList pathlist, char* filename, logical exist );
logical utn_fio_list_inquire_path( FioFileList f, char* name, char* pathname, long siz );


FioFile utn_fio_open_file( const char* filename, char* mode );
logical utn_fio_inquire( const char* name ); 
long utn_fio_access( const char* name ); 
long utn_fio_stat_type( const char* name );
void utn_fio_file_get_name( FioFile file, char* name, const long maxlen ); 
void utn_fio_file_get_type( FioFile file, char* type, const long maxlen );	 
long utn_fio_file_status( FioFile file, long mode ); 
FioFile utn_fio_file_ptr( long FID ); 
logical utn_fio_file_close( FioFile file ); 
long utn_fio_file_get_lineno( FioFile file ); 
FILE* utn_fio_file_get_fptr(FioFile file); 
logical utn_fio_file_test_eof(FioFile file); 
void utn_fio_file_clear_eof( FioFile file ); 
logical utn_fio_file_mode_eof( FioFile file, long mode ); 
logical utn_fio_any_eof( long mode ); 
void utn_fio_file_set_status ( FioFile file, logical ios ) ; 
long utn_fio_file_get_cfd( FioFile file ); 
long utn_fio_file_n( void );	 
void utn_fio_list( void ); 
void utn_fio_file_rewind( FioFile file ); 
long utn_fio_file_get_lineno( FioFile file ); 
long utn_fio_file_get_pos( FioFile file ); 
long utn_fio_file_fid( FioFile file ); 
logical utn_fio_file_interactive( FioFile file ); 
void utn_fio_set_retry( logical q ); 
logical utn_fio_get_retry( void ); 
FioFile utn_fio_tty_in( void );  
FioFile utn_fio_tty_out( void );  
FioFile utn_fio_tty_err( void ); 
void utn_fio_file_flush( FioFile file ); 
long utn_fio_file_get_recl( FioFile file ); 
void utn_fio_file_set_recl( FioFile file, long recl ); 
FioFileList utn_fio_get_list(void); 
void utn_fio_path_set( char* pathlist ); 
void utn_fio_path_list( void ); 

char* utn_fio_tmpname( void );



FioFile utn_fio_open_sr( const char* filename );  
FioFile utn_fio_open_sw( const char* filename );  
FioFile utn_fio_open_srw( const char* filename ); 
logical utn_fio_seek( FioFile file, long pos ); 
logical utn_fio_write_q( FioFile file, logical* buf, long n, long* nw );  
logical utn_fio_write_c( FioFile file, char* buf, long n, long* nw );  
logical utn_fio_write_d( FioFile file, double* buf, long n, long* nw );  
logical utn_fio_write_r( FioFile file, float* buf, long n, long* nw );  
logical utn_fio_write_i( FioFile file, long* buf, long n, long* nw );  
logical utn_fio_write_s( FioFile file, short* buf, long n, long* nw ); 
logical utn_fio_read_c( FioFile file, char* buf, long n, long* nw );  
logical utn_fio_read_d( FioFile file, double* buf, long n, long* nw );  
logical utn_fio_read_r( FioFile file, float* buf, long n, long* nw );  
logical utn_fio_read_q( FioFile file, logical* buf, long n, long* nw );  
logical utn_fio_read_i( FioFile file, long* buf, long n, long* nw );  
logical utn_fio_read_s( FioFile file, short* buf, long n, long* nw ); 
logical utn_fio_read_sio( FioFile file, void* buf, long n, long* nw, size_t siz );  
logical utn_fio_write_sio( FioFile file, void* buf, long n, long* nw, size_t siz ); 
FioFile utn_fio_open_cr( const char* filename );  
FioFile utn_fio_open_cw( const char* filename );  
FioFile utn_fio_open_crw( const char* filename ); 
logical utn_fio_write_bytes( FioFile file, char* buf, long n, long* m ); 
logical utn_fio_read_bytes( FioFile file, char* buf, long n, long* m ); 
logical utn_fio_file_tty( FioFile file ); 
long utn_fio_read_byte( FioFile file ); 
FioFile utn_fio_open_raw( void ); 
long utn_fio_read_raw( FioFile file ); 
FioFile utn_fio_open_ar( const char* filename ); 
FioFile utn_fio_open_aw( const char* filename ); 
FioFile utn_fio_open_aa( const char* filename ); 
logical utn_fio_read_line( FioFile file, char* line, long lmax );  
logical utn_fio_tty_read( char* buf, long siz ); 
logical utn_fio_write_line( FioFile file, const char* buf );  
logical utn_fio_tty_write( const char* buf ); 
logical utn_fio_write_text( FioFile file, char* buf );  
logical utn_fio_tty_writel( char* buf ); 
void utn_fio_pager_set( FioPager pager, logical q ); 
void utn_fio_pager_set_length( FioPager pager, long page_length ); 
void utn_fio_pager_print_state( FioPager pager ); 
void utn_fio_process_file( char* filename, void (*process_line)( char* ) );  
void utn_fio_process_file1( char* filename, void (*process_line)( void*, char* ), void* ptr ); 
void utn_fio_stack_reinit( void ); 
void utn_fio_stack_in( FioFile file ); 
void utn_fio_stack_out( FioFile file ); 
void utn_fio_stack_set_err( FioFile file ); 
void utn_ver_msg( void ); 
void utn_msg_d( const long level, const char* buf );  
void utn_fio_msg( char* buf ); 
void utn_fio_tmsg( char* buf ); 
void utn_fio_msgl( char* buf ); 
void utn_fio_tmsgl( char* buf ); 
void utn_fio_std_msg( char* buf, logical newline ); 
void utn_fio_set_msg( void (*msgfunc)(char*, logical )); 
void utn_fio_msg_endline( void ); 
void utn_fio_dashline( long n ); 
void utn_err_msg( const char* buf );  
logical utn_fio_stack_read_input( char* buf, long siz );	 		  
logical utn_fio_tty_input( char* buf, long siz );	 
void utn_fio_stack_rewind_input( void ); 
logical utn_fio_stack_interactive( long  fiomode ); 
FioFile utn_fio_stack_file( long mode ); 
logical utn_fio_input_test_eof( void ); 
void utn_fio_basic_print_state( void ); 
FioStack utn_fio_get_stack( void ); 
void utn_fio_stack_opush( FioFile file );  
void utn_fio_stack_ipush( FioFile file ); 
void utn_fio_stack_ipop( long mode );  
void utn_fio_stack_opop( long mode ); 
void utn_fio_stack_print_state( void ); 
logical utn_fio_stack_eof( long mode );  
logical utn_fio_stack_ok( long mode );  
long utn_fio_stack_status( long mode ); 
void utn_fio_stack_set_detab( logical q ); 
logical utn_fio_stack_read( char* buf, char* prompt, long siz ); 
long utn_fio_stack_get_level( void ); 
FioFile utn_fio_stack_input_entry( long level ); 
long utn_fio_list_n(FioFileList list); 
FioFileList utn_fio_list_init(void);
void utn_fio_list_free(FioFileList list); 
void utn_fio_list_print(FioFileList list); 
FioFile utn_fio_list_open_file( FioFileList list, const char* filename, char* mode );  
FioFile utn_fio_list_get_file(FioFileList list, long FID); 
logical utn_fio_list_get_retry(FioFileList list); 
void utn_fio_list_set_retry(FioFileList list, logical q); 
FioFile utn_fio_list_tty_in(FioFileList list); 
FioFile utn_fio_list_tty_err(FioFileList list); 
FioFile utn_fio_list_tty_out(FioFileList list); 
logical utn_fio_list_any_eof(FioFileList list, long mode); 
void utn_fio_list_set_path(FioFileList list, char* pathlist); 
void utn_fio_list_list_path(FioFileList list); 
logical utn_fio_list_inquire_path( FioFileList f, char* name, char* pathname, long siz ); 
logical utn_fio_pager_tty_read( FioPager pager, char* buf, long siz ); 
logical utn_fio_pager_tty_write( FioPager pager, const char* buf,logical newline ); 
FioStack utn_fio_iostack_init(void ); 
void utn_fio_iostack_free( FioStack stack ); 
void utn_fio_iostack_opush( FioStack stack, FioFileList list, FioFile file );  
void utn_fio_iostack_ipush( FioStack stack,  FioFileList list, FioFile file ); 
void utn_fio_iostack_ipop( FioStack stack,  FioFileList list, long mode );  
void utn_fio_iostack_opop( FioStack stack, FioFileList list, long mode ); 
FioFile utn_fio_iostack_file( FioStack stack, long fiomode ); 
long utn_fio_iostack_get_level( FioStack stack, long fiomode ); 
void utn_fio_iostack_set( FioStack stack, FioFileList list, FioFile file, long fiomode ); 
void utn_fio_iostack_rewind_input( FioStack stack ); 
FioFile utn_fio_iostack_input_level( FioStack stack, long level ); 
void utn_fio_iostack_set_detab(FioStack stack, logical q); 
logical utn_fio_iostack_read( FioStack stack, char* buf, char* prompt, long siz ); 
void utn_fio_iostack_print_state( FioStack stack ); 
void utn_fio_iostack_msg( FioStack iofs, FioFileList list, char* buf, logical newline, logical tty ); 
void utn_fio_iostack_set_msg( FioStack iofs, void (*msgfunc)(char*, logical )); 
void utn_fio_iostack_dashline( FioStack iofs, long n ) ;
void utn_tpio_tp_init( Tpio tpio ); 
Tpio utn_tpio_get( void ); 
void utn_tpio_c( char* prompt, char* token, long siz );  
void utn_tpio_cs( char* prompt, char* token, long siz );  
void utn_tpio_g_cs( char* prompt, char** pptr, char* token, long siz );
void utn_tpio_g_c( char* prompt, char** pptr, char* token, long siz );

void utn_tpio_token( char* prompt, char** ptr, long mode, char* token, long siz );
void utn_tpio_cl( char* prompt, char* token, long siz ); 
long utn_tpio_i( char* prompt );  
short utn_tpio_s( char* prompt );  
float utn_tpio_r( char* prompt );  
double utn_tpio_d( char* prompt ); 
long utn_tpio_d_i( char* prompt, long xdefault );  
double utn_tpio_d_d( char* prompt, double xdefault );  
short utn_tpio_d_s( char* prompt, short xdefault );  
float utn_tpio_d_r( char* prompt, float xdefault ); 
void utn_tpio_in( char* filename ); 
void utn_tpio_cc( char* prompt, char* buf, long siz ); 
void utn_tpio_dbg( void ); 
void utn_tpio_print_state( void );  
void utn_tpio_disp( long i ); 
void utn_tpio_tp_dbg( Tpio tpio ); 
void utn_tpio_tp_disp( Tpio tpio, FioStack stack, FioFileList list, long i ); 
void utn_tpio_tp_print_state( Tpio tpio, FioStack iofs );  
void utn_tpio_setbuf( char** arr, long n ); 
void utn_tpio_cmdargs( char** args, long nargs, logical* interact, logical* opts ); 
void utn_tpio_tp_in( FioFileList list, FioStack iofs, char* filename ); 
void utn_tpio_tp_out( FioFileList list, FioStack iofs, char* filename,logical append, logical syserr ); 
void utn_tpio_tp_cc(Tpio tpio, FioFileList list, FioStack iofs, char* pr, char* buf, long siz ); 


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
void utn_error_print( GenError error );
void utn_state_clear_error( UtilState state );
GenError utn_state_get_error( UtilState state );
void utn_state_print_error( UtilState state  );
void utn_state_error( UtilState state, char* buf );
void utn_fio_file_free( FioFile file );
void utn_error_status( void );

FioFile utn_fio_map_ar( const char* filename );
FioFile utn_fio_open_map( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp );
logical utn_fio_file_close_map( FioFile file );
void utn_fio_rewind_map( FioFile file );
logical utn_fio_seek_map( FioFile file, integer pos );
logical utn_fio_read_line_map( FioFile file, char* buf, long siz );

logical utn_fio_tty( FioFile file );
#endif
