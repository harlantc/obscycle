/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

typedef struct FioStackData_s {
 FioFile* input_stack;
 FioFile* output_stack;
 FioFile* input;
 FioFile* output;
 FioFile err_chan;
 long ilevel;
 long olevel;
 long depth;
 logical detab_flag;
 void (*msgfunc)( char*, logical );
} FioStackData;




