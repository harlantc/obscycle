/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef FIOLIST_H
#define FIOLIST_H

#include <termios.h>
#include "utsize.h"
#include "fio.h"

struct FioPager_s {
 long nlines;
 long line;
 logical quiet;
 logical on;
};

#include "iostack.h"

struct FioRaw_s {
 struct termios termios_arg;
 char termios_cc_save[UT_TERMIOS_TSIZE];
 char termios_cc_raw[UT_TERMIOS_TSIZE];
 long termios_ncc;
 long termios_flags_save[4];
 long termios_flags_raw[4];
};

struct FioFileList_s {
 logical retry;
 NameList pathlist;
 FioPager pager;
 FioStack iofs;
 GenStack drivers;
 GenStack data;
 long umask;
};

FioFile utn_fio_alloc( void );

#endif
