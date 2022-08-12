/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef utn_tpio_H
#define utn_tpio_H

typedef struct {
 TextWord lvar;
 long value;
 long step;
 long maxval;
 long addr;
} TpioLoop;

typedef struct utn_tpio_s {
 logical cmd_mode;
 char** buffer;
 char* cmd;
 char** bptr;
 char** ptr; 
 long depth;
 long nloop;
 long maxloop;
 TpioLoop* loop;
} UtilCommandData;

TpioLoop* utn_tpio_loop_push( Tpio tpio );

#endif

