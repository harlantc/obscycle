/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fiolist.h"

FioStack utn_fio_get_stack( void )
{
 FioFileList list = utn_fio_get_list();
 return list->iofs;
}

long utn_fio_stack_get_level( void )
{
 return utn_fio_iostack_get_level( NULL, FIO_INPUT );
}

void utn_fio_stack_opop( long mode )
{
 utn_fio_iostack_opop( NULL, NULL, mode );
}

void utn_fio_stack_ipush( FioFile file )
{
 utn_fio_iostack_ipush( NULL, NULL, file );
}

void utn_fio_stack_ipop( long mode )
{
 utn_fio_iostack_ipop(  NULL, NULL, mode );
}

void utn_fio_stack_opush( FioFile file )
{
 utn_fio_iostack_opush( NULL, NULL, file );
}

/* Synonym */

void utn_fio_push( FioFile file )
{
 utn_fio_iostack_opush( NULL, NULL, file );
}
void utn_fio_pop( long mode )
{
 utn_fio_iostack_opop( NULL, NULL, mode );
}




void utn_err_msg( const char* buf )
{
 utn_fio_write_line( utn_fio_iostack_file( NULL, FIO_ERR), buf );
}

FioFile utn_fio_stack_file( long mode )
{
 return utn_fio_iostack_file( NULL, mode );
}

void utn_fio_stack_set_err( FioFile file )
{
 utn_fio_iostack_set( NULL, NULL, file, FIO_ERR );
}

void utn_fio_stack_out( FioFile file )
{
 utn_fio_iostack_set( NULL, NULL, file, FIO_OUTPUT );
}

void utn_fio_stack_in( FioFile file )
{
 utn_fio_iostack_set( NULL, NULL, file, FIO_INPUT );
}


logical utn_fio_stack_interactive( long mode )
{
 return( utn_fio_file_interactive( utn_fio_iostack_file( NULL, mode ) ));
}

void utn_fio_stack_set_detab( logical q )
{
 utn_fio_iostack_set_detab( utn_fio_get_stack(), q );
}

logical utn_fio_stack_read( char* buf, char* prompt, long siz )
{
 return utn_fio_iostack_read( NULL, buf, prompt, siz );
}


void utn_fio_stack_rewind_input( void )
{
 utn_fio_iostack_rewind_input( NULL );
}

FioFile utn_fio_stack_input_entry( long level )
{
 return utn_fio_iostack_input_entry( NULL, level );
}

logical utn_fio_stack_read_input( char* buf, long siz )
{ 
 FioFile file = utn_fio_iostack_file( NULL, FIO_INPUT );
 return( utn_fio_read_line( file, buf, siz ) );
} 


