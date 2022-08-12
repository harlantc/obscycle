/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"


CalendarList* cal_cal_list_alloc( integer n )
{
 CalendarList* list;
 list = calloc( 1, sizeof( CalendarList ));
 list->n = 0;
 list->nmax = n;
 list->data = calloc( n, sizeof( Calendar ));
 return list;
}

void cal_cal_list_free( CalendarList* list )
{
/* The calendar labels are now static pointers and don't need to be freed. */
#if 0
 integer i;
 Calendar* t;
 for ( i =0; i < list->n; i++ )
 {
  t= &list->data[i];
  free( t->name ); 
  free( t->label );
 }
#endif
 free( list->data );
 free( list );
}


integer cal_calendar_get_type( Calendar* t )
{
 integer type =0;
 if ( t )
  type = t->type;
 return type;
}

void cal_calendar_get_label( Calendar* t, char* label )
{
 if ( t )
  utn_cs_copy( t->label, label );
 else
  utn_cs_copy( " ", label );
 
}

void cal_calendar_get_name( Calendar* t, char* label )
{
 if ( t )
  utn_cs_copy( t->name, label );
 else
  utn_cs_copy( " ", label );
 
}

integer cal_calendar_get_id( CalendarList* list, char* opt )
{
 Calendar* t;
 integer id;
 
 if ( utn_cs_is_blank( opt ) )
  return 0;

 for ( id = 1; id <= list->n; id++ )
 {
  t = &list->data[id-1];
  if ( utn_cs_uceq( opt, t->name ))
  {
   if ( gcal_verbose)
    printf( "CCGI MATCH %s with %s on entry %ld\n", opt, t->name, id );
   return id;  
  }
 }
 return 0;
}

Calendar* cal_calendar_parse_spec( CalendarList* list, char* opt )
{
 integer id;
 Calendar* t = NULL;

 id = cal_calendar_get_id( list, opt );

#if 0

 if ( id == 0 )
 {
  char* default_cal = "GREG";
  id = cal_calendar_get_id( list, default_cal );
 }
#endif

 if ( id > 0 )
  t = &list->data[id-1];

 if ( t && gcal_verbose) 
  printf( "CCPS GOT CAL %s TYPE %ld\n", t->name, t->type );

 return t;
}


void cal_calendar_list( CalendarList* list )
{
 integer cal_id;

 for ( cal_id = 1; cal_id <= list->n; cal_id++ )
 {
  cal_calendar_print( cal_id, &list->data[cal_id-1]);
 }
}

void cal_calendar_print( integer id, Calendar* cal )
{
 TextBuf buf;
 TextCard typename;
 cal_caltype_get_name( cal->type, typename );
 sprintf( buf, "%8ld %-12s%-32s %-24s",
   id, cal->name, cal->label, typename );
 utn_fio_msg( buf );
}
