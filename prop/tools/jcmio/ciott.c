/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fiolist.h"


FioRaw utn_fio_raw_init( integer fd, logical nocr )
{
 FioRaw f = calloc( 1, sizeof( struct FioRaw_s ));
 f->termios_ncc= utn_ccio_get( f, fd, f->termios_cc_save, f->termios_flags_save );
 utn_cs_copy_siz( f->termios_cc_save, f->termios_cc_raw, UT_TERMIOS_TSIZE );
 utn_ar_copy_i( f->termios_flags_save, f->termios_flags_raw, 4 );
 utn_ccio_set_raw( f->termios_cc_raw, f->termios_flags_raw, nocr );
 utn_cio_set_raw( f, fd );
 return f;
}


void utn_cio_set_raw( FioRaw raw, long fd )
{
 utn_ccio_set( raw, fd, raw->termios_cc_raw, raw->termios_flags_raw );
}

void utn_cio_clear_raw( FioRaw list, long fd )
{
 utn_ccio_set( list, fd, list->termios_cc_save, list->termios_flags_save );
}


long utn_ccio_get( FioRaw f, long fd, char* ptr, long* fptr )
{
 long i;

 (void)tcgetattr( fd, &f->termios_arg );

/* Save initial values */

 for ( i = 0; i < NCCS; i++ ) {
  *ptr = f->termios_arg.c_cc[i];
  ptr++;
 }
 *ptr = f->termios_arg.c_cc[VMIN]; ptr++;
 *ptr = f->termios_arg.c_cc[VTIME]; ptr++;
 *fptr = (long) f->termios_arg.c_iflag; fptr++;
 *fptr = (long) f->termios_arg.c_oflag; fptr++;
 *fptr = (long) f->termios_arg.c_cflag; fptr++;
 *fptr = (long) f->termios_arg.c_lflag; fptr++;
 return( NCCS );
}


void utn_ccio_set( FioRaw f, long fd, char* ptr, long* fptr )
{
 long i;

 for ( i = 0; i < NCCS; i++ ) {
  f->termios_arg.c_cc[i] = *ptr++;
 }
 f->termios_arg.c_iflag = (unsigned int)*fptr++;
 f->termios_arg.c_oflag = (unsigned int)*fptr++;
 f->termios_arg.c_cflag = (unsigned int)*fptr++;
 f->termios_arg.c_lflag = (unsigned int)*fptr++;

 (void)tcsetattr(fd,TCSADRAIN,&f->termios_arg);
}

void utn_ccio_set_raw( char* ptr, long* fptr, logical nocr )
{
/* Set raw mode values */ 
 if ( nocr )
 {
  fptr[0] = ~( INLCR | ICRNL );
  fptr[3] = ~( ICANON | ECHO );
 }
 ptr[VMIN] = 1;
 ptr[VTIME] = 0;
 ptr[VSUSP] = -1;
}

