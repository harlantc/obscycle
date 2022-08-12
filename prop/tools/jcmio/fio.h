/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef utn_fio_H
#define utn_fio_H

#include <stdio.h>

#define FIO_FLAG_TTY 0
#define FIO_FLAG_RAW 1
#define FIO_FLAG_CTTY 2
#define FIO_FLAG_MAP  3

#define FIO_MODE_R   1
#define FIO_MODE_W   2



typedef struct FioFileObject_s {
Filename name;
char* path;
char type[12];  /* Type of file */
long status;    /* 0 = ok */
long  line;     /* Position in file */
long  pos;      /* Position in line */
long recl;      /* Recl in bytes */
long stream; /* File ID for native file type */
FILE *fptr;    /* STDIO file pointer, if any */
FioFileList list; /* Parent file list */
long fid;    /* UTILS FIO FID */
logical flags[4];   
integer mode;  /* R, W or RW */
void (*resync)( void*, int );
void* resync_driver;
FioDriver driver;
void* aux;
} FioFileObject;



#endif
