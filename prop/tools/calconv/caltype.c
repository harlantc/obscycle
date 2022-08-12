/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"


/* Calendar types */

char* caltyn[CALTYPE_N] = { "Unknown", "Gregorian Calendar",
                   "Julian Day Number",
                   "Julian Calendar",
                   "Roman Calendar",
                   "French Revolutionary Calendar",
                   "Greenwich Sidereal Date",
                   "Elapsed Seconds", "Epoch", "Maya", 
                  };

char* caltyc[CALTYPE_N] = { "UNK", "GREG", "JD", "OS", "AUC", "THERMIDOR", "GST", "TIME", "EPOCH", "MAYA" };

/* cal_cs_eval_fail not used */


CalendarList* cal_init_calendars( void )
{
 CalendarList* list;
 Calendar cals[]= { 
 { "GREG", "Gregorian date",
      TGREG_,     CAL_ANNUAL,    
      cal_cs_print_greg,
      cal_cs_parse_greg,                         
      cal_cs_eval_greg,
      cal_cs_utd_to_jde },
 { "JD",   "Julian Day", 
      TJD_,       CAL_NOT_ANNUAL,
      cal_cs_print_jd,
      cal_parse_jd_date,
      cal_cs_eval_jd,
      cal_cs_jd_to_jde },
 { "MJD",  "Modified Julian Day", 
      TJD_,       CAL_NOT_ANNUAL,
      cal_cs_print_mjd,
      cal_parse_jd_date,
      cal_cs_eval_jd,
      cal_cs_jd_to_jde },
 { "DAYS", "Elapsed Days",
      TJD_,       CAL_NOT_ANNUAL,
      cal_cs_print_days,
      cal_parse_jd_date,
      cal_cs_eval_jd,
      cal_cs_jd_to_jde },
 { "DATE", "Date",             
      TGREG_,     CAL_ANNUAL, 
      cal_cs_print_simple_greg,
      cal_cs_parse_greg,
      cal_cs_eval_greg,
      cal_cs_utd_to_jde },
 { "PACK", "Packed date",
     TGREG_,     CAL_ANNUAL,
     cal_cs_print_pack,
      cal_cs_parse_pack,
     cal_cs_eval_greg,
     cal_cs_utd_to_jde },
 { "DOY",  "Day of year",
     TGREG_,     CAL_ANNUAL,
     cal_cs_print_doy,
      cal_cs_parse_doy,
      cal_cs_eval_greg,
      cal_cs_utd_to_jde },
 { "OS",   "Julian Calendar", 
     JULIAN_CAL_,CAL_ANNUAL,
     cal_cs_print_os,
     cal_cs_parse_os,
     cal_cs_eval_greg,
     cal_cs_utd_to_jde},
 { "ROMAN", "Roman calendar", 
     TROMAN_,    CAL_ANNUAL,
     cal_cs_print_auc, 
     cal_cs_parse_fail,
     cal_cs_eval_auc,
     cal_cs_fail_to_jde },
{ "RF",   "French Rev. Calendar",
     TFRENCH_,   CAL_ANNUAL,
     cal_cs_print_thermidor,
     cal_cs_parse_fail,
     cal_cs_eval_thermidor,
     cal_cs_fail_to_jde },
 { "GSD",  "Greenwich Sidereal Date",
     TGST_,      CAL_NOT_ANNUAL,
     cal_cs_print_gsd,
     cal_cs_parse_fail,
     cal_cs_eval_jd,
     cal_cs_fail_to_jde },
 { "GST",  "Sidereal Time",
    TGST_,      CAL_NOT_ANNUAL,
     cal_cs_print_gst,
     cal_cs_parse_fail,
     cal_cs_eval_jd,
     cal_cs_fail_to_jde },     
 { "TIME", "Elapsed Seconds",
     TELAPSE_,      CAL_NOT_ANNUAL,
     cal_cs_print_elapse,
     cal_parse_elapse,
     cal_cs_eval_elapse,
     cal_cs_elapse_to_jde  },
 { "JEPOCH", "Julian Epoch",
     TEPOCH_,      CAL_ANNUAL,
     cal_cs_print_epoch,
     cal_parse_epoch,
     cal_cs_eval_epoch,
     cal_cs_epoch_to_jde  },
 { "BEPOCH", "Besselian Epoch",
     TEPOCH_,      CAL_ANNUAL,
     cal_cs_print_epoch,
     cal_parse_epoch,
     cal_cs_eval_epoch,
     cal_cs_epoch_to_jde  },
 { "MAYA", "Maya Long Count",
     TMAYA_,      CAL_NOT_ANNUAL,
     cal_cs_print_maya,
     cal_cs_parse_fail,
     cal_cs_eval_maya,
     cal_cs_maya_to_jde  },
 { "AZTEC", "Aztec calendar",
     TMAYA_,      CAL_NOT_ANNUAL,
     cal_cs_print_aztec,
     cal_cs_parse_fail,
     cal_cs_eval_maya,
     cal_cs_maya_to_jde  }
};

 integer n;
 integer i;
 n = sizeof( cals )/sizeof( Calendar );
 list = cal_cal_list_alloc( n );
/* This copies the names and descriptions as static char pointers */
 for ( i = 0; i < n; i++ )
 {
  list->data[i] = cals[i];
 }
 list->n = n;
 return list;
}




void cal_caltype_get_name( integer type, char* name )
{
 utn_cs_copy( caltyn[type], name );
}

integer cal_caltype_get_id( char* code )
{
 TextCard tcode;
 integer id;
 utn_cs_copy( code, tcode );
 utn_cs_upper( tcode );
 id = utn_ar_cmatch_c( tcode, caltyc, CALTYPE_N );
 if ( gcal_verbose )
  printf( "Matched calendar %s to type %ld\n", code, id );
 return id;
}

void cal_caltype_list( void )
{
 TextBuf buf;
 integer id;

 utn_fio_msg( " " );
 utn_fio_msg( " No.     Calendar "  );
 utn_fio_msg( " " );
 for ( id = 1; id < CALTYPE_N; id++ )
 {
  sprintf( buf, "%6ld %s", id, caltyn[id] );
  utn_fio_msg( buf );
 }

}
