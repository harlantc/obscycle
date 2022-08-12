/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"

void utn_cs_parse_disp( char* buf, long* itemcode, long* itempos, long nitems )
{
 character mbuf[513];
 utn_cs_parse_fmt( buf, itemcode, itempos, nitems, mbuf );
 utn_fio_msg( mbuf );
}

void utn_esc_status( void )
{
 /* Dump the esc struct */
 long type;
 char echar;
 char edefault;
 char name[8];
 TextCard buf;
 FioStack iofs = NULL;
 if ( !iofs ) iofs = utn_fio_get_stack();
 utn_fio_iostack_msg( iofs, NULL, "--- JCMLIB Program Control Characters ---", UT_TRUE, UT_FALSE );
 utn_fio_iostack_msg( iofs, NULL, "No  Name   Value  Default Value Default", UT_TRUE, UT_FALSE);
 for ( type = 0; type < ESC_NTYPES; type++ ) {
  echar = utn_esc_special_char( type );
  edefault = utn_esc_get_default_char( type );
  utn_esc_get_name( type, name );
  snprintf( buf, UT_CARD_SIZE, "%2ld %-8s %c     %c  %8d %8d", (long)type, name, echar, edefault,
    echar, edefault );
  utn_fio_iostack_msg( iofs, NULL, buf, UT_TRUE, UT_FALSE );
 }
}
  

