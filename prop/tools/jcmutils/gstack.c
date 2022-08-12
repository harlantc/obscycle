/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/* Generic push-pop stack, entries are allocated elsewhere */

integer utn_stack_size( GenStack stack )
{
 if ( stack )
  return stack->n;
 return 0;
}

void utn_stack_free( GenStack stack )
{
 if ( stack )
 {
  free( stack->data );
  free( stack );
 }
}

GenStack utn_stack_alloc( void )
{
 GenStack stack = calloc( 1, sizeof( struct GenStack_s ));
 if ( stack )
 {
  stack->n = 0;  
  stack->data = NULL;
 }
 return stack;
}


void* utn_stack_entry( GenStack stack, integer i )
{
 if ( i > 0 && i <= stack->n )
  return stack->data[i-1];
 else
  return NULL;
}

void utn_stack_set( GenStack stack, integer i, void* entry )
{
 if ( i > 0 && i <= stack->n )
   stack->data[i-1] = entry;
}

integer utn_stack_push( GenStack stack, void* entry )
{
 if ( stack->n >= stack->nmax )
 {
  stack->nmax = 2 * stack->nmax + 20;
  stack->data = realloc( stack->data, stack->nmax * UT_SZ_P );
 }
 stack->data[stack->n] = entry;
 stack->n++;
 return (stack->n);
}

void* utn_stack_pop( GenStack stack )
{
 void* ptr = utn_stack_top( stack );
 stack->n--;
 return ptr;
}

void* utn_stack_top( GenStack stack )
{
 if ( stack->n > 0 )
  return stack->data[stack->n-1];
 else
  return NULL;
}


void utn_stack_clear( GenStack stack )
{
 stack->n = 0;
}



