/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/





/* UT SYSTEM */


/* UT BITS */

void utn_sys_byte_d( const long byte, long* segment, long* ibyte );

/* UT ERROR */


void utn_error_free_state( ErrorGlobal error_global );
void utn_error_init_user( ErrorGlobal error_global, char* buf );
long utn_error_mask( ErrorGlobal eg );
ErrorClasses utn_error_class_global( ErrorGlobal error_global );
long utn_error_user_mask( void );
ErrorGlobal utn_error_global( void );

void utn_error_class_free( ErrorClasses list );

/* UT NULLS */
void utn_null_state_ieee( UtilNulls nulls );
void utn_null_state_set( UtilNulls nulls, char typecode, char* opt, char* pattern );
void utn_null_state_init( UtilNulls nulls );



/* AR SORT */
void utn_cheapsort_d( const double* x, const long n, long* jsort );	/* Use heapsort algorithm */
void utn_cheapsort_i( const long* x, const long n, long* jsort );
void utn_cheapsort_c( char** x, const long n, long* jsort );
void utn_cheapsort_r( const float* x, const long n, long* jsort );	/* Use heapsort algorithm */
void utn_cheapsort_s( const short* x, const long n, long* jsort );
void utn_cshellsort_d( const double* x, const long n, long* jsort );	/* Use shellsort algorithm */
void utn_cshellsort_i( const long* x, const long n, long* jsort );
void utn_cshellsort_c( char** x, const long n, long* jsort );
void utn_cshellsort_r( const float* x, const long n, long* jsort );	/* Use shellsort algorithm */
void utn_cshellsort_s( const short* x, const long n, long* jsort );

void utn_heapsort_d( const double* x, const long n, long* jsort );	/* Use utn_heapsort algorithm */
void utn_heapsort_i( const long* x, const long n, long* jsort );
void utn_heapsort_c( char** x, const long n, long* jsort );
void utn_shellsort_d( const double* x, const long n, long* jsort );	/* Use utn_shellsort algorithm */
void utn_shellsort_i( const long* x, const long n, long* jsort );
void utn_shellsort_c( char** x, const long n, long* jsort );
void utn_heapsort_r( const float* x, const long n, long* jsort );	/* Use utn_heapsort algorithm */
void utn_heapsort_s( const short* x, const long n, long* jsort );
void utn_shellsort_r( const float* x, const long n, long* jsort );	/* Use utn_shellsort algorithm */
void utn_shellsort_s( const short* x, const long n, long* jsort );

