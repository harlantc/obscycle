/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <limits.h>
#include <float.h>


void utn_ar_mix_copy_c( char** tmp );
void utn_ar_mix_numeric_cast_elt( const void* in, const DataType intype, const DataType outtype, void* out, long inoffset, long outoffset );
logical utn_ar_mix_get_cast_elt( const MixArray array, const long offset, DataType ttype, DataType* typep, void* value );
/*
 *  Cast typed element from intype; used in numeric cast macro
 */

#define utn_ar_mix_numeric_cast_type( in, out, type, inoffset, outoffset )  { \
 switch( type ) {                                                \
  case DT_INT2:  ((short*)(out))[outoffset] = (short)((in)[inoffset]); break; \
  case DT_INT4:  ((long*)(out))[outoffset] = (long)((in)[inoffset]); break; \
  case DT_REAL4: ((float*)(out))[outoffset] = (float)((in)[inoffset]); break; \
  case DT_REAL8: ((double*)(out))[outoffset] = (double)((in)[inoffset]); break; \
  case DT_UINT2: ((unsigned short*)(out))[outoffset] = (unsigned short)((in)[inoffset]); break; \
  case DT_UINT4:  ((unsigned long*)(out))[outoffset] = (unsigned long)((in)[inoffset]); break; \
  case DT_UINT1: case DT_BIT: ((uchar*)(out))[outoffset] = (uchar)((in)[inoffset]); break; \
  case DT_LOGICAL:   ((logical*)(out))[outoffset] = (logical)((in)[inoffset]); break; \
  default: out = NULL; break;                                    \
 } }


void* utn_ar_gen_alloc( const DataType type, const long n )
{
 long size = utn_ar_mix_type_size( type );
 return calloc( n, size );
}
/*
 *  (internal)Public interface  to NumericCast macro
 */
void utn_ar_mix_numeric_cast( const void* in, const DataType intype, const DataType outtype, void* out )
{
 utn_ar_mix_numeric_cast_elt( in, intype, outtype, out, 0, 0 );
}

/* 
 *   Cast element from intype to outtype: see utn_ar_mix_numeric_cast
 */
void utn_ar_mix_numeric_cast_elt( const void* in, const DataType intype, const DataType outtype, void* out, long inoffset, long outoffset )
{
 switch( intype ) {                                  
  case DT_INT2:  utn_ar_mix_numeric_cast_type( (short*)(in),          out, outtype, inoffset, outoffset ); break;       
  case DT_INT4:   utn_ar_mix_numeric_cast_type( (long*)(in),           out, outtype, inoffset, outoffset ); break;       
  case DT_REAL4:  utn_ar_mix_numeric_cast_type( (float*)(in),          out, outtype, inoffset, outoffset ); break;       
  case DT_REAL8: utn_ar_mix_numeric_cast_type( (double*)(in),         out, outtype, inoffset, outoffset ); break;       
  case DT_UINT2: utn_ar_mix_numeric_cast_type( (unsigned short*)(in), out, outtype, inoffset, outoffset ); break;       
  case DT_UINT4:  utn_ar_mix_numeric_cast_type( (unsigned long*)(in),  out, outtype, inoffset, outoffset ); break;      
  case DT_UINT1: case DT_BIT:  utn_ar_mix_numeric_cast_type( (uchar*)(in),  out, outtype, inoffset, outoffset ); break;     
  case DT_LOGICAL:   utn_ar_mix_numeric_cast_type( (logical*)(in),         out, outtype, inoffset, outoffset ); break;      
  default: out = NULL; break;                                         \
 } 
}


/* Cast a numeric array */
void utn_ar_mix_numeric_cast_array( const void* in, const DataType intype, const DataType outtype, void* out, const long nvals )
{
 long i = 0;
 switch( intype ) {                                  
  case DT_INT2: 
    switch( outtype )
    {
     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((short*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((short*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((short*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((short*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((short*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((short*)in)[i]; }; break;
     case DT_UINT1:   case DT_BIT:   
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((short*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((short*)in)[i]; }; break;
     default: out = NULL; break;                                    
    }
    break;    


  case DT_INT4:     
   switch( outtype ) 
   {
     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((long*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((long*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((long*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((long*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((long*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((long*)in)[i]; }; break;
     case DT_UINT1: case DT_BIT:  
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((long*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((long*)in)[i]; }; break;
     default: out = NULL; break;                                    
   }
   break;
  case DT_REAL4: 
   switch( outtype )
   {
     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((float*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((float*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((float*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((float*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((float*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((float*)in)[i]; }; break;
     case DT_UINT1: case DT_BIT:  
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((float*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((float*)in)[i]; }; break;
     default: out = NULL; break;                                    
  }
   break;
  case DT_REAL8:    
   switch( outtype )
   {
     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((double*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((double*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((double*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((double*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((double*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((double*)in)[i]; }; break;
     case DT_UINT1: case DT_BIT:  
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((double*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((double*)in)[i]; }; break;
     default: out = NULL; break;                                    
   }
   break;
  case DT_UINT2:    
   switch( outtype )
   {
     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((unsigned short*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((unsigned short*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((unsigned short*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((unsigned short*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((unsigned short*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((unsigned short*)in)[i]; }; break;
     case DT_UINT1: case DT_BIT:  
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((unsigned short*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((unsigned short*)in)[i]; }; break;
     default: out = NULL; break;                                    
   }
   break;
  case DT_UINT4:    
   switch( outtype )
   {
     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((unsigned long*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((unsigned long*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((unsigned long*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((unsigned long*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((unsigned long*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((unsigned long*)in)[i]; }; break;
     case DT_UINT1: case DT_BIT:  
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((unsigned long*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((unsigned long*)in)[i]; }; break;
     default: out = NULL; break;                                    
   }
   break;
   case DT_UINT1: case DT_BIT:   
   switch( outtype )
   {
     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((uchar*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((uchar*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((uchar*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((uchar*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((uchar*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((uchar*)in)[i]; }; break;
     case DT_UINT1: case DT_BIT:  
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((uchar*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((uchar*)in)[i]; }; break;
     default: out = NULL; break;                                    
   }
   break;
  case DT_LOGICAL:  
   switch( outtype )
   {

     case DT_INT2:   
     { for ( i = 0; i < nvals; i++ ) ((short*)(out))[i] = (short)((logical*)in)[i]; }; break;
     case DT_INT4:  
     { for ( i = 0; i < nvals; i++ ) ((long*)(out))[i] = (long)((logical*)in)[i]; }; break;
     case DT_REAL4:
     { for ( i = 0; i < nvals; i++ ) ((float*)(out))[i] = (float)((logical*)in)[i]; }; break;
     case DT_REAL8: 
     { for ( i = 0; i < nvals; i++ ) ((double*)(out))[i] = (double)((logical*)in)[i]; }; break;
     case DT_UINT2: 
     { for ( i = 0; i < nvals; i++ ) ((unsigned short*)(out))[i] = (unsigned short)((logical*)in)[i]; }; break;
     case DT_UINT4:   
     { for ( i = 0; i < nvals; i++ ) ((unsigned long*)(out))[i] = (unsigned long)((logical*)in)[i]; }; break;
     case DT_UINT1: case DT_BIT:  
     { for ( i = 0; i < nvals; i++ ) ((uchar*)(out))[i] = (uchar)((logical*)in)[i]; }; break;
     case DT_LOGICAL: 
     { for ( i = 0; i < nvals; i++ ) ((logical*)(out))[i] = (logical)((logical*)in)[i]; }; break;
     default: out = NULL; break;                                    
   }
   break;
  default: out = NULL; break;                                         
 } 
}



/*
 *  Cast from one type to another, with allocation copy in case of text
 */
void utn_ar_mix_cast_array( const void* in, const DataType intype, const DataType outtype, void* out, const long nvals )
{
 double dval;
 double* dptr = &dval;
 char* rem;
 TextCard buf;
 long i;
 if ( intype == DT_CHAR )
 {
  if ( outtype == DT_CHAR )
  {
   for ( i = 0; i < nvals; i++ )
    ((char**)out)[i] = utn_cs_dup( ((char**)in)[i] );
  }
  else
  {
   for ( i = 0; i < nvals; i++ )
   {
    dval = strtod( ((char**)in)[i], &rem );
    utn_ar_mix_numeric_cast_elt( dptr, DT_REAL8, outtype, out, 0, i );
   }
  }
 } 
 else if ( outtype == DT_CHAR )
 {
  for ( i = 0; i < nvals; i++ )
  {
   utn_ar_mix_numeric_cast_elt( in, intype, DT_REAL8, dptr, i, 0 );
   snprintf( buf, UT_CARD_SIZE, "%f", dval );
   ((char**)out)[i] = utn_cs_dup( buf );
  }
 }
 else 
  utn_ar_mix_numeric_cast_array( in, intype, outtype, out, nvals );
}


MixArray utn_ar_mix_alloc( const long n )
{
 return utn_ar_mix_alloc_type( DT_UNK, 0, n );
}


MixArray utn_ar_mix_alloc_type( const DataType type, const long tsize, const long n )
{
 MixArray array;
 long size = tsize;
 if ( size == 0 )
  size = utn_ar_mix_type_size( type );
 array = (MixArray)calloc( 1, sizeof( struct MixArray_s ) );
 if ( !array ) return NULL;
 array->type = type;
 array->types= NULL;
 array->data = NULL;
 if ( n > 0 )
 {
  array->data = (void*)calloc( n, size );
  if ( type == DT_UNK )
  {
   array->types= (DataType*)calloc( n, sizeof( DataType ));
  } 
 }
 array->size = size;
 array->n    = 0;
 array->nmax = n;
 return array;
}

long utn_ar_mix_array_size( const MixArray array )
{
 if ( array )
  return array->size;
 else
  return 0;
}

/* Calloc-like realloc */
void* utn_realloc( void* data, long const old, long const new, const long size )
{
 void* ndata;
 if( new <= old )
  ndata = data;
 else
 {
  ndata = realloc( data, new * size );
  memset( (char*)ndata+old*size, 0, (new-old)*size );
 }
 return ndata;
}
/*
 *  Extend a Mixed type array
 */

logical utn_ar_mix_extend( MixArray array, const long n )
{
 long nmax;
 if ( !array ) return UT_FALSE;

 nmax = array->nmax; 
 array->data = (void*)utn_realloc( array->data, nmax, n, array->size );

 if ( !array->data ) return UT_FALSE;

 if ( array->type == DT_UNK )
 {
  array->types= (DataType*)utn_realloc( array->types, nmax, n, sizeof( DataType ) );
  if ( !array->types ) return UT_FALSE;
 }

 array->nmax = n;

 return UT_TRUE;
}

void utn_ar_mix_free( MixArray array )
{
 long offset;
 void* ptr;
 char* str;
 DataType type;

 if ( array ) 
 {
  type = array->type;
  for ( offset = 0; offset < array->n; offset++ )
  {
   if ( array->type == DT_UNK ) 
    type = array->types[offset];

   if ( type == DT_CHAR )
   {
    ptr = utn_ar_mix_ptr( array, offset );
    str = *(char**)ptr;

    if ( str ) free( str );

   }
  }

  free( array->data );

  if ( array->types )
   free( array->types );

  free( array );
 }
}

void utn_ar_mix_copy_c( char** tmp )
{
 *tmp = utn_cs_dup( *tmp );
}


void utn_ar_mix_cast_elt( MixArray array, const long offset, const DataType type, void* value )
{
 DataType mixType;
 utn_ar_mix_get_cast_elt( array, offset, type, &mixType, value );
}
   
int utn_ar_mix_cast_elt_cp( MixArray array, const long offset, char* value )
{
 GenericType tmp;
 DataType mixType;

 if ( !value )
  return UT_FALSE;

 utn_ar_mix_get_elt( array, offset, &mixType, (void*)&tmp );
 if ( mixType == DT_CHAR )
 {
  utn_cs_copy( *(char**)&tmp, value );
  return UT_TRUE;
 }
 else
 {
  *value = '\0';
  return UT_FALSE;
 }
}
   
/*
 *  Print a given element of an array of specified type
 *  leaving the char* in the user-provided buf and returning a pointer
 *  to that buf. Debug routine.
 */

char* utn_ar_mix_print_elt( const MixArray array, const long offset, char* buf, long size )
{
 void* ptr;
 DataType type;

 if ( !array || !buf )
   return NULL;

 ptr = (void*)utn_ar_mix_ptr( array, offset );
 if ( !ptr ) 
  return NULL;


 type = array->type;
 if ( type == DT_UNK )
  type = array->types[offset];

 utn_ar_mix_print_generic( ptr, type, buf, size );

 return buf;
}

void utn_ar_mix_print_generic( void* ptr, DataType type, char* buf, long size )
{
 switch( type )
 {
  case DT_INT2:
   snprintf( buf, size, "%d", *(short*)ptr );
   break;
  case DT_UINT2:
   snprintf( buf, size, "%u", *(unsigned short*)ptr );
   break;
  case DT_UINT4: 
   snprintf( buf, size, "%lu", *(unsigned long*)ptr );
   break;
  case DT_UINT1: case DT_BIT:
   snprintf( buf, size, "%d", *(uchar*)ptr );
   break;
  case DT_INT4:
   snprintf( buf, size, "%ld", *(long*)ptr );
   break;
  case DT_REAL4:
   snprintf( buf, size, "%f",  *(float*)ptr );
   break;
  case DT_REAL8:
#if 0
   snprintf( buf, size, "%g", *(double*)ptr );
#endif
/* DM1 compat */
   { 
   double x = *(double*)ptr;
   double abs_x = fabs(x);
   if ( abs_x < 1.0E-3 || abs_x > 1.0E9 ) {
    snprintf( buf, size, "%G", x );
   } else {
    if ( abs_x < 1.0E6 ) {
     snprintf( buf, size, "%16.8f", x );
    } else {
     snprintf( buf, size, "%20.8f", x );
    }
   }
   }
   break;
  case DT_LOGICAL:
   snprintf( buf, size, "%d", *(logical*)ptr );
   break;
  case DT_CHAR:
   utn_cs_copy_siz( *(char**)ptr, buf, size );
   break;
  case DT_PTR:
   utn_cs_copy_siz( *(char**)ptr, buf, size );
   break;
  default:
   utn_cs_copy( " ", buf );
   break;
 }
 return;
  
}

/*
 *  Set element number offset+1 of the array given the element's type
 *  In the case of a char* value, *(char**)value must point to
 *  allocated memory. A new copy of the char* is not made.
 *  The routine frees any char* previously stored
 *  in that mixed array element.
 */
logical utn_ar_mix_set_elt( MixArray array, const long offset, const DataType type, const void* value )
{
 void* ptr;
 char* str;
 long size;

 if ( !array || !array->data )
   return UT_FALSE;

 if ( offset < 0 || offset > array->n )
  return UT_FALSE;

 if ( offset == array->n )
 {
  /* Allocate ample extra space if needed */
  if( offset >= array->nmax ) 
      utn_ar_mix_extend( array, 2 * offset );
  array->n++;
 }
 size = array->size;
 if ( array->type == DT_UNK )
 {
  array->types[offset] = type;
  size = utn_ar_mix_type_size( type );
 }
 else if ( array->type != type )
   return UT_FALSE;

 ptr = (void*)utn_ar_mix_ptr( array, offset );
 if ( type == DT_CHAR )
 {
  str = *(char**)ptr;
  if ( str ) free( str ); 
 }
 utn_ar_mix_copy_value( value, type, ptr, UT_FALSE, size );
 return UT_TRUE;
}

void* utn_ar_mix_elt( const MixArray array, const long offset )
{
 void* ptr = NULL;
 DataType type;
 utn_ar_mix_get_elt( array, offset, &type, &ptr );
 if ( type != DT_PTR )
  return NULL;
 else
  return ptr;
}
/*
 *  Get the value of element number offset+1 of the array
 */
logical utn_ar_mix_get_elt( const MixArray array, const long offset, DataType* typep, void* value )
{
 return utn_ar_mix_get_cast_elt( array, offset, DT_UNK, typep, value );
}

logical utn_ar_gen_set_elt( void* array, const long offset, const DataType type, void* value )
{
 long size;
 void* ptr;

 size = utn_ar_mix_type_size( type );
 ptr = utn_ar_gen_ptr( array, type, size, offset );
 if ( !value  || offset < 0 ) 
 {
  memset( ptr, 0, size );
  return UT_FALSE;
 }
 utn_ar_mix_copy_value( value, type, ptr, UT_FALSE, size );
 return UT_TRUE;
}


/* Simple call with no checking */
logical utn_ar_gen_get_elt( const void* array, const long offset, const DataType type, void* value )
{
 return utn_ar_gen_get_cast_elt( array, offset, offset+1, type, type, value );
}

logical utn_ar_gen_get_cast_elt( const void* array, const long offset, const long n, const DataType array_type, const DataType type, void* value )
{
 long size;
 void* ptr;

 if ( !value ) return UT_FALSE;
 size = utn_ar_mix_type_size( type );
 if ( !array  || offset < 0 || offset > n ) 
 {
  memset( value, 0, size );
  return UT_FALSE;
 }
 if ( type == array_type )
 {
  ptr = utn_ar_gen_ptr( array, type, size, offset );
  utn_ar_mix_copy_value( ptr, type, value, UT_FALSE, size );
 } else {
  size = utn_ar_mix_type_size( array_type );
  ptr = utn_ar_gen_ptr( array, array_type, size, offset );
  if ( type == DT_CHAR )
   utn_ar_mix_copy_c( (char**)ptr );
  utn_ar_mix_numeric_cast( (void*)ptr, array_type, type, value );
 }
 return UT_TRUE; 
}

logical utn_ar_mix_get_cast_elt( const MixArray array, const long offset, DataType ttype, DataType* typep, void* value )
{

 void* ptr = NULL;
 long size;
 logical q = UT_TRUE;
 logical in_range = UT_TRUE;
 DataType type;

 if ( !value ) return UT_FALSE;

 if ( !array || !array->data )
   q = UT_FALSE;

 if ( q && (offset < 0 || offset >= array->n ))
   in_range = UT_FALSE;

 if ( !q )
 {
  type = DT_UNK;
  size = UT_SZ_P;
 }
 else if ( array->type == DT_UNK && in_range )
 {
  type = array->types[offset];
  size = utn_ar_mix_type_size( type );
 }
 else 
 {
  type = array->type;
  size = array->size;
 }
 if ( q && in_range )
  ptr = utn_ar_mix_ptr( array, offset );

 if ( type == ttype || ttype == DT_UNK )
 {
  utn_ar_mix_copy_value( ptr, type, value, UT_FALSE, size );
 }
 else
 { 
  if ( type == DT_CHAR )
   utn_ar_mix_copy_c( (char**)ptr );
  utn_ar_mix_numeric_cast( (void*)ptr, type, ttype, value );
 }

 if( typep )
  *typep = type;

 return ( q && in_range );
}


/*
 *  Copy a mixed type value; if copyText is true and type is text, copy the char*, else just the pointer.
 */
void utn_ar_mix_copy_value( const void* ptr, const DataType type, void* value, logical copy, long size )
{

 if ( !ptr && value ) 
 {
  memset( value, 0, size );  /* Generic set to zero */
  return;
 }

 if ( type == DT_CHAR  )
 {
  if ( copy ) 
   *(char**)value = utn_cs_dup( *(char**)ptr );
  else
   *(char**)value = *(char**)ptr;
 } else {
  if ( size == 0 )  
   size = utn_ar_mix_type_size( type );

  if ( ptr ) 
   memcpy( value, ptr, size );
  else
   memset( value, 0, size );
 }
}
/*
 *  Return pointer to array of types, if array is mixed.
 */
DataType* utn_ar_mix_array_types( MixArray array )
{
 if ( array )
  return array->types;
 else
  return NULL;
} 
/*
 *  If we change the type of an element to char*,
 *  and then set it, there's a problem if the pointer is
 *  not preset to null since SetMixElt will try and
 *  free it.
 *
 */
void utn_ar_mix_delete_elt( MixArray array, const long offset )
{
 void* ptr;
 char* str;
 DataType type;

 type = array->type;
 if ( type == DT_UNK )
  type = array->types[offset];

 ptr = utn_ar_mix_ptr( array, offset );

 switch( type )
 {
  case DT_CHAR:
   str = *(char**)ptr;
   if ( str ) free( str );
  case DT_PTR: /* Fallthrough intended */
   *(void**)ptr = NULL;
   break;
  case DT_REAL8:
   *(double*)ptr = 0.0;
   break;
  case DT_REAL4:
   *(float*)ptr = 0.0;
   break;
  case DT_INT4:
  case DT_UINT4:
   *(long*)ptr = 0;
   break;
  case DT_INT2:
  case DT_UINT2:
   *(short*)ptr = 0;
   break;
  case DT_BIT:
  case DT_UINT1:
  default:
   *(uchar*)ptr = 0;
   break;
 }
}  


/* 
 *  Copy a mixed array element from one mixed array to another (or to
 *  another location in the same one ).
 */
void utn_ar_mix_copy_elt( const MixArray src, const long srcoffset, MixArray dest, const long destoffset )
{ 
 GenericType tmp = 0;    /* Storage to copy element */
 char** pptr = NULL;
 DataType type;
 utn_ar_mix_get_elt( src, srcoffset, &type, (void*)&tmp );
 if( type == DT_CHAR )
 {
  pptr = (char**)&tmp;
  *pptr = utn_cs_dup( *pptr );
 }
 utn_ar_mix_set_elt( dest, destoffset, type, (void*)&tmp );
}

/*
 *  Copy a mixed array element from one mixed array to another (or to
 *  another location in the same one ). Zero the pointer in
 *  the source array (as in etgDeleteElt) but don't free any associated
 *  char* memory.
 */
void utn_ar_mix_move_elt( MixArray src, const long srcoffset, MixArray dest, const long destoffset )
{
 void* ptr;
 GenericType tmp;    /* Storage to copy element */
 DataType type;
 utn_ar_mix_get_elt( src, srcoffset, &type, (void*)&tmp );
#if 0
 if( type == DT_CHAR )
  utn_ar_mix_copy_c( (char**)&tmp );
#endif
 utn_ar_mix_set_elt( dest, destoffset, type, (void*)&tmp );
 ptr = utn_ar_mix_ptr( src, srcoffset );
 *(char**)ptr = NULL;
}



/*
 *  Return a pointer to element number offset+1 of the array
 */
void* utn_ar_mix_ptr( const MixArray array, const long offset )
{
 if ( !array || !array->data || offset > array->nmax )
  return NULL;

/* Auto-extend if needed */
 if ( offset == array->nmax )  
  utn_ar_mix_extend( array, 2 * offset );

 return utn_ar_gen_ptr( array->data, array->type, array->size, offset );
}

void* utn_ar_gen_ptr( const void* data, const DataType type, const long size, const long offset )
{
 if ( !data )
  return NULL;

 if ( offset < 0 )
  return NULL;

 switch( type )
 {
  case DT_UNK:
   return &((GenericType*)data)[offset];
  case DT_INT2:
   return &((short*)data)[offset];
  case DT_UINT2:
   return &((unsigned short*)data)[offset] ;
  case DT_UINT4:
   return &((unsigned long*)data)[offset];
  case DT_UINT1: case DT_BIT:
   return &((uchar*)data)[offset];
  case DT_INT4:
   return &((long*)data)[offset];
  case DT_REAL4:
   return &((float*)data)[offset];
  case DT_REAL8:
   return &((double*)data)[offset];
  case DT_LOGICAL:
   return &((logical*)data)[offset];
  case DT_CHAR:
   return &((char**)data)[offset];
  case DT_PTR:
  default:
   return ((char*)data + offset * size);
   break;
 }

}
 
   




void utn_ar_mix_type_name( const DataType type, char* buf, long maxlen )
{


#if OLD_DM
 char* names[] = {  "Void", "Int2", "Ptr", "Int4", "Real4", "Real8", "Text", "Byte", 
                    "Logical", "UNKNOWN", "UInt2", "UInt4", "Bit" }; 
#else
 char* names[] = {  "UNKNOWN", "Logical", "Text", "Byte", "UInt2", "Int2", "UInt4", "Int4", "UInt8", "Int8", "Real4", "Real8",
                     "Bit", "Ptr", "Void" };
#endif
 long nmax = 13;
 long offset;
 
 offset = (long)type;
 if ( offset < 0 || offset >= nmax )
  offset = 0;
 utn_cs_copy_siz( names[offset], buf, maxlen );
}
/*
 *  The size of DT_CHAR is UT_SZ_P not UT_SZ_C
 *  since we always pass in character strings as pointers to these routines.
 */

long utn_ar_mix_type_size( const DataType type )
{
 switch( type )
 {
  case DT_UNK:   return UT_SZ_D;
  case DT_INT2:  return UT_SZ_S;
  case DT_INT4:  return UT_SZ_I;
  case DT_REAL4: return UT_SZ_R;
  case DT_REAL8: return UT_SZ_D;
  case DT_UINT2: return UT_SZ_S;
  case DT_UINT4: return UT_SZ_I;
  case DT_UINT1: return UT_SZ_B;
  case DT_LOGICAL: return UT_SZ_Q;
  case DT_CHAR:  return UT_SZ_P;
  case DT_PTR: return UT_SZ_P;
  case DT_BIT: return UT_SZ_B;
  default: return 0;
 }
}



logical utn_ar_mix_is_min( const DataType type, void* value )
{
 if ( !value ) return UT_FALSE;
 switch( type )
 {
  case DT_INT2:
   return (*(short*)value == -SHRT_MAX - 1);
  case DT_UINT2:
   return (*(unsigned short*)value == 0);
  case DT_UINT4:
   return(*(unsigned long*)value == 0);
  case DT_UINT1:  
   return (*(uchar*)value == 0);
  case DT_INT4:
   return( *(long*)value == -LONG_MAX-1);
  case DT_REAL4:
   return(*(float*)value == -FLT_MAX);
  case DT_REAL8:
   return( *(double*)value == -DBL_MAX);
  case DT_LOGICAL:
   return( *(logical*)value == UT_FALSE);
  case DT_CHAR:
   return (*(char**)value== NULL);
  case DT_PTR:
   return( *(char**)value== NULL);
  case DT_BIT:
   return (*(char*)value== 0);
  default:
   return UT_FALSE;
 }

}

/*
 *   Return the minimum legal value for a given data type
 */
void utn_ar_mix_type_min( const DataType type, void* value )
{
 if ( !value ) return;

 switch( type )
 {
  case DT_INT2:
   *(short*)value = -SHRT_MAX - 1;
   break;
  case DT_UINT2:
   *(unsigned short*)value = 0;
   break;
  case DT_UINT4:
   *(unsigned long*)value = 0;
   break;
  case DT_UINT1:  
   *(uchar*)value = 0;
   break;
  case DT_INT4:
   *(long*)value = -LONG_MAX-1;
   break;
  case DT_REAL4:
   *(float*)value = -FLT_MAX;
   break;
  case DT_REAL8:
   *(double*)value = -DBL_MAX;
   break;
  case DT_LOGICAL:
   *(logical*)value = UT_FALSE;
   break;
  case DT_CHAR:
   *(char**)value= NULL;
   break;
  case DT_PTR:
   *(char**)value= NULL;
   break;
  case DT_BIT:
   *(char*)value= 0;
   break;
  default:
   break;
 }

}

logical utn_ar_mix_is_numeric( const DataType type )
{
 switch( type )
 {
  case DT_INT2:
  case DT_UINT2:
  case DT_UINT4:
  case DT_UINT1:  
  case DT_INT4:
  case DT_REAL4:
  case DT_REAL8:
  case DT_LOGICAL:
   return UT_TRUE;

  case DT_CHAR:
  case DT_PTR:
  case DT_BIT:
  default:
   return UT_FALSE;

 }
}

logical utn_ar_mix_is_max( const DataType type, void* value )
{
 if ( !value ) return UT_FALSE;
 switch( type )
 {
  case DT_INT2:
   return (*(short*)value == SHRT_MAX );
  case DT_UINT2:
   return (*(unsigned short*)value == USHRT_MAX );
  case DT_UINT4:
   return(*(unsigned long*)value == ULONG_MAX );
  case DT_UINT1:  
   return (*(uchar*)value == UCHAR_MAX );
  case DT_INT4:
   return( *(long*)value == LONG_MAX);
  case DT_REAL4:
   return(*(float*)value == FLT_MAX);
  case DT_REAL8:
   return( *(double*)value == DBL_MAX);
  case DT_LOGICAL:
   return( *(logical*)value == UT_TRUE);
  case DT_CHAR:
   return (*(char**)value== NULL);
  case DT_PTR:
   return( *(char**)value== NULL);
  case DT_BIT:
   return (*(unsigned char*)value== 0);
  default:
   return UT_FALSE;
 }

}


int utn_ar_mix_eq( const DataType type, void* value1, void* value2 )
{
 int q;
 switch( type )
 {
  case DT_INT2:
   q = *(short*)value1 == *(short*)value2;
   break;
  case DT_UINT2:
   q = *(unsigned short*)value1 == *(unsigned short*)value2;
   break;
  case DT_UINT4:
   q=  *(unsigned long*)value1 == *(unsigned long*)value2;
   break;
  case DT_UINT1:
   q = *(uchar*)value1 == *(uchar*)value2;
   break;
  case DT_INT4:
   q =    *(long*)value1 == *(long*)value2;
   break;
  case DT_REAL4:
   q = *(float*)value1 ==  *(float*)value2;
   break;

  case DT_REAL8:
   q = *(double*)value1 == *(double*)value2;
   break;
  case DT_LOGICAL:
   q = *(logical*)value1 == *(logical*)value2;
   break;
  case DT_CHAR:
   q = utn_cs_eq( *(char**)value1, *(char**)value2 );
   break;
  case DT_PTR:
   q = ( *(char**)value1 == *(char**)value2 );
   break;
  case DT_BIT:
/* A bit dubious - bit value array size not known. This tests only 1-byte */
   q = *(unsigned char*)value1 == *(unsigned char*)value2;
   break;
  default:
   q = UT_FALSE;
   break;
 }
 return q;

}

/*
 *  Return the maximum legal value for a given data type.
 */
void utn_ar_mix_type_max( const DataType type, void *value )
{
 switch( type )
 {
  case DT_INT2:
   *(short*)value= SHRT_MAX;
   break;
  case DT_UINT2:
   *(unsigned short*)value = USHRT_MAX;
   break;
  case DT_UINT4:
   *(unsigned long*)value = ULONG_MAX;
   break;
  case DT_UINT1:
   *(uchar*)value = UCHAR_MAX;
   break;
  case DT_INT4:
   *(long*)value = LONG_MAX;
   break;
  case DT_REAL4:
   *(float*)value = FLT_MAX;
   break;
  case DT_REAL8:
   *(double*)value = DBL_MAX;
   break;
  case DT_LOGICAL:
   *(logical*)value = UT_TRUE;
   break;
  case DT_CHAR:
   *(char**)value= NULL;
   break;
  case DT_PTR:
   *(char**)value= NULL;
   break;
  case DT_BIT:
   *(unsigned char*)value= (unsigned char)UCHAR_MAX;
   break;
  default:
   break;
 }
   
}
  



/*
 *   Return the zero value
 */
void utn_ar_mix_type_zero( const DataType type, void* value )
{
 if ( !value ) return;

 switch( type )
 {
  case DT_INT2:
   *(short*)value = 0;
   break;
  case DT_UINT2:
   *(unsigned short*)value = 0;
   break;
  case DT_UINT4:
   *(unsigned long*)value = 0;
   break;
  case DT_UINT1:  
   *(uchar*)value = 0;
   break;
  case DT_INT4:
   *(long*)value = 0;
   break;
  case DT_REAL4:
   *(float*)value = 0;
   break;
  case DT_REAL8:
   *(double*)value = 0;
   break;
  case DT_LOGICAL:
   *(logical*)value = UT_FALSE;
   break;
  case DT_CHAR:
   *(char**)value= NULL;
   break;
  case DT_PTR:
   *(char**)value= NULL;
   break;
  case DT_BIT:
   *(char*)value= 0;
   break;
  default:
   break;
 }

}



long utn_ar_mix_n( MixArray array )
{
 if ( array )
  return array->n;
 else
  return 0;
}


long utn_ar_mix_nmax( MixArray array )
{
 if ( array )
  return array->nmax;
 else
  return 0;
}

