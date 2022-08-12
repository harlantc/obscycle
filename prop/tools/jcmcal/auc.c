/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
integer cal_ides_day( integer m );
#define BISSEXTILE -1
#define KALENDS     1
#define AUC_ZERO  -752
#define NPRAE 21
#define COSMAX 1500
#define COS_BUF_MAX 256
typedef struct {
 integer yr; /* Year OS */
 char data[COS_BUF_MAX];
} Annum;

static Annum Fasti[COSMAX];
static integer nFasti = 0;

static char* praenomina[NPRAE] = {
 "Appius", "Aulus", "Gaius", "Gnaeus", "Decimus", "Kaeso",
 "Lucius", "Marcus", "Manius", "Publius", "Quintus", "Servius", "Spurius", 
 "Titus", "Tiberius", "Flavius", "Numerius", "Agrippa", "Hostus", "Postumus", "Sextus" };

static char* praefix[NPRAE] = {
 "App.", "A.", "C.", "Cn.", "D.", "K.", "L.", "M.", "M'.", "P.", "Q.", "Ser.", "Sp.", "T.",
 "Ti.", "Fl.", " ", " ", " ", " ", " " };

enum { Kalends, Nones, Ides } Sections;
void auc_section_dayname( integer sday, char* buf );
void rome_get_praenomen_abbr( integer prae, char* buf );
integer rome_get_praenomen( char* name );
void rome_cos_parse( char* buf, char* name, char* title, char* rpt );
void rome_cos_parse_line( char* line, char* tbuf, char* title1, char* title2 );

integer cal_ides_day( integer m )
{
 static integer ides[] = {  13, 13, 15, 13, 15, 13, 15, 13, 13, 15, 13, 13 };
 if ( m == 0 ) return 0;
 return ides[m-1];
}



void cal_auc_date( double t, char* date, integer maxlen )
{
 integer y,m,sect,sday;
 cal_auc_calc( t, &y, &m, &sect, &sday );
 cal_auc_format( y, m, sect, sday, date, maxlen );
}



void cal_auc_calc( double t, integer* y, integer *m, integer* sect, integer* sday )
{
 double utd;
 integer y_os, m_os, d;
 logical qly;
 integer aucyear = 0;
 integer aucmon;
 integer lmon;
 integer day_ides;
 integer day_nones;

 cal_jde_to_os( cal_ed_make( t, 0.0 ), &y_os, &m_os, &d, &utd );
 qly = cal_os_lyr( y_os ) == 1;
 
 aucyear = y_os - AUC_ZERO + 1;
 *y = aucyear;
 if ( aucyear < 1 )
 {
/* Ignore years prior to 1 */
  *sect = 1;
  *sday = 0;
  *m = m_os;
  return;
 }
/* Month lengths are the same as today */

 lmon = cal_month_length( m_os );
 day_ides = cal_ides_day( m_os );

 aucmon = m_os;
 if ( qly && m_os == 2 && d >= 23 ) 
 {
  *sect = Kalends;
  aucmon++;
  if ( d == 24 ) 
   *sday = BISSEXTILE; 
  else
   *sday = 30 - d;
 } else if ( d <= day_ides ) {
  day_nones = day_ides - 8;
  if ( d == KALENDS )
  {
   *sect = Kalends;
   *sday = 0;
  } else if ( d <= day_nones ) {
   *sect = Nones;
   *sday = day_nones - d;
  } else {
   *sect = Ides;
   *sday = day_ides - d;
  }
 } else {
   *sect = Kalends;
   *sday = lmon + 1 - d;
   aucmon++;
   if( aucmon == 13 ) aucmon = 1;

 }
 *m = aucmon;
}

void cal_auc_format( integer y, integer m, integer sect, integer sday, char* aucdate, integer maxlen )
{
 char* sectnames[] = { "KAL.", "NON.", "ID." };
 TextCard dayname;
 TextBuf consuls;
 TextCard monthname;
 TextCard cyr;
 TextCard date; 
 long size = TEXT_SIZE;
 TextBuf buf;
 char* ptr = buf;
 char* pdate;  /* If we want to left just */

 if ( y < 1 )
 {
  utn_cs_copy( "ANTE VRBE CONDITA", date );
  return;
 }
 
 auc_section_dayname( sday, dayname );
 cal_mon( m, monthname );
 utn_cs_upper( monthname );
 snprintf( date, UT_CARD_SIZE, "%s %-4s %s", dayname, sectnames[sect], monthname );
 utn_cs_roman_numeral( y, cyr );
 cal_rome_get_cos( y, consuls, size );

 pdate = date;
 while ( *pdate == ' ' ) pdate++;

 utn_cs_put_c( pdate, &ptr, &size );
 utn_cs_put_c( cyr, &ptr, &size );
 utn_cs_put_c( "AVC", &ptr, &size );
 while( ptr - buf < 30 )
 {
    *ptr++ = ' '; size--;
 }

 utn_cs_put_c( consuls, &ptr, &size );
 utn_cs_copy_siz( buf, aucdate, maxlen );
} 


void auc_section_dayname( integer sday, char* buf )
{
 if ( sday == 0 )
  utn_cs_copy( " ", buf );
 else if ( sday == 1 )
  utn_cs_copy( "PRID.", buf );
 else if ( sday == BISSEXTILE )
  utn_cs_copy( "BIS-VI", buf );
 else
 {
  utn_cs_copy( "A.D. ", buf );
  utn_cs_roman_numeral( sday, buf+5 );
 }
}


void cal_rome_read_cos( char* filename )
{
 FioFile in;
 TextBuf line;
 TextBuf tbuf=" ";
 TextCard title1=" ";
 TextCard title2=" ";
 char* defaultline = "   1 -753        Romulus, Rex";
 in = utn_fio_open_ar( filename );
 if ( !in )
  rome_cos_parse_line( defaultline, tbuf, title1, title2 );
 else
 {
  while( utn_fio_read_line( in, line, TEXT_SIZE ))
  {
   if ( !utn_cs_eq_siz( line, "    ", 4 ) )   /* First four spaces blank means cos. suffecti */
    rome_cos_parse_line( line, tbuf, title1, title2 );
  }
  utn_fio_file_close( in );
 } 
}


void rome_cos_parse_line( char* line, char* tbuf, char* title1, char* title2 )
{
 TextCard tmp;
 TextCard cos1;
 TextCard cos2;
 TextBuf name1;
 TextBuf name2;
 TextCard rpt1;
 TextCard rpt2;
 integer k;
 integer y;

 TextCard regnalyr;
 integer size;
 char* tptr;
 TextCard yrbuf;
 Annum* annum;
 integer osyr;
 integer aucyr;
 utn_cs_get_ss( line, tmp, 1, 4 );
 aucyr = utn_cs_read_i( tmp );
 utn_cs_get_ss( line, tmp, 6, 9 );
 osyr = utn_cs_read_i( tmp ); 
 utn_cs_get_ss( line, cos1, 18, 79 );
 if ( utn_cs_ends( line) > 79 )
  utn_cs_copy( line+79, cos2 );
 else 
  utn_cs_copy( " ", cos2 );

 if( aucyr > nFasti && aucyr < COSMAX ) 
 {
  annum = &Fasti[aucyr-1];
  if ( utn_cs_eq_siz( cos1, "post con", 8 )) 
  {
   utn_cs_copy( cos1, annum->data );
  } else if ( utn_cs_eq_siz( cos1, "[Anarc", 6 )) {
   utn_cs_copy( " ", annum->data );
  } else {
   k = 1;
   for ( y = nFasti+1; y < aucyr; y++ )
   { 
    annum = &Fasti[y-1];
    if ( utn_cs_eq( title1, "Rex" )) 
    {  
     utn_cs_roman_numeral( ++k, yrbuf );
     snprintf( regnalyr, UT_CARD_SIZE, "a. %s", yrbuf );
     utn_cs_conc1( tbuf, regnalyr, annum->data );
    }
    else if ( utn_cs_eq( title1, "Interrex" )) 
    {
     utn_cs_copy( title1, annum->data );
    }
    else
     utn_cs_copy( " ", annum->data );
   }      
   annum = &Fasti[aucyr-1];
   if ( utn_cs_eq_siz( cos1, "Interrex", 8 ))
   {
    utn_cs_copy( "Interrex", title1 );
   } else {
    rome_cos_parse( cos1, name1, title1, rpt1 );
    rome_cos_parse( cos2, name2, title2, rpt2 );
    utn_cs_copy( " ", tbuf );   
   }

   if ( utn_cs_eq( title1, "Decemviri" ) || utn_cs_eq( title1, "Interrex" ))
    utn_cs_copy( title1, tbuf );
   else
   {
    tptr = tbuf;
    size = UT_TEXT_SIZE;
    utn_cs_put_token( name1, &tptr, ',', &size );
    if ( !utn_cs_is_blank( name2 ) && !utn_cs_eq( title1, "dict.")) 
    {
     *tptr++ = ' ';
     utn_cs_put_token( name2, &tptr, ',', &size );
     if ( !utn_cs_eq( title1, title2 ))
     {
      snprintf( cal_global_state->errbuf, UT_TEXT_SIZE, "%s Title1 = :%s: Title2 = :%s:", line, title1, title2 );
     }
    }
    *tptr++ = ' ';
    utn_cs_put_c( title1, &tptr, &size );
   }
   utn_cs_copy_siz( tbuf, annum->data, COS_BUF_MAX );
  }
  nFasti = aucyr;
 }
}

void rome_cos_parse( char* buf, char* name, char* title, char* rpt )
{
 char* ptr = buf;
 char* save = buf;
 TextCard word;
 TextCard pfx;
 TextCard pfx1;
 TextCard sub; 
 char* tptr;
 TextWord pbuf;
 integer prae;
 integer j;
 integer k;
 TextCard rem;
 TextCard rname = " ";
 utn_cs_copy( " ", name );
 utn_cs_copy( " ", title );
 utn_cs_copy( " ", rpt );
 utn_cs_copy( " ", rem );
 if ( utn_cs_is_blank( buf ) )
  return;

 utn_cs_copy( " ", pfx );
 utn_cs_get_c( &ptr, word, CARD_SIZE );
 if ( utn_cs_eq( word, "D.N." ) || utn_cs_eq( word, "Imperator" ))
 {
  utn_cs_copy( word, pfx );
  save = ptr;
  utn_cs_get_c( &ptr, word, CARD_SIZE );
 }

 prae = rome_get_praenomen( word );

 if ( !prae ) ptr = save;
 j = ptr - buf + 1;
 k = utn_cs_index_char( buf, ',' );
 if ( k > 0 )  
 {
  rome_get_praenomen_abbr( prae, pbuf );
  utn_cs_conc1( pfx, pbuf, pfx1 );

  utn_cs_get_ss( buf, sub, j, k-1 );
  utn_cs_conc1( pfx1, sub, name );
  ptr = buf + k - 1;
  tptr = buf + k;
  utn_cs_get_c( &tptr, word, CARD_SIZE );  
  if ( tptr && *tptr < 'a' )
    utn_cs_copy( tptr, rem );
  if ( utn_cs_eq( word, "cos." ))
  {
   tptr = rem;
   utn_cs_get_c( &tptr, rpt, CARD_SIZE );  
   if ( tptr )
   {  
    TextCard errbuf;
    snprintf( errbuf, UT_CARD_SIZE, "Discarding %s", tptr );
    utn_msg_d( 1, errbuf );
   }
   utn_cs_copy( "cos.", title );
   utn_cs_conc1( name, rpt, rname );
   utn_cs_copy( rname, name );
  } else if ( utn_cs_eq( word, "decemvir" ) ) {
   utn_cs_copy( "Decemviri", title );
  } else if ( utn_cs_eq( word, "Rex" )) {
   utn_cs_copy( "Rex", title );
  } else if ( utn_cs_eq( word, "mag." )) {
   utn_cs_copy( " ", title );
   utn_cs_copy( " ", name );
  } else if ( utn_cs_eq_siz( word, "dict", 4 )) {
   utn_cs_copy( word, title );
   utn_cs_copy( rem, rpt );
   utn_cs_conc1( name, rpt, rname );
   utn_cs_copy( rname, name );
  } else if ( utn_cs_eq_siz( word, "trib", 4 )) {
   utn_cs_copy( word, title );
   utn_cs_copy( rem, rpt );
   utn_cs_conc1( name, rpt, rname );
   utn_cs_copy( rname, name );
  } else {
   snprintf( cal_global_state->errbuf, UT_TEXT_SIZE, "Bad title  %s : %s", buf, word );
  }
 } else {
   snprintf( cal_global_state->errbuf, UT_TEXT_SIZE, "Bad entry: %s", buf );
 }
/* Trim trailing spaces */
 utn_cs_copy( name, rname );
 ptr = rname;
 while( *ptr == ' ' ) ptr ++; 
 k = utn_cs_ends( ptr );
 ptr[k] = '\0';
 utn_cs_copy( ptr, name );
}


void cal_rome_get_cos( integer y, char* buf, integer siz )
{
 Annum* annum;
 utn_cs_copy( " ", buf );
 
 if ( nFasti == 0 ) cal_rome_read_cos( "COS" );
 if ( y > 0 && y <= nFasti ) 
 {
  annum = &Fasti[y-1];
  utn_cs_copy_siz( annum->data, buf, siz );
 }
}

/* Map praenomina and their standard abbreviations */

integer rome_get_praenomen( char* name )
{
 return utn_ar_match_c( name, praenomina, NPRAE );
}

void rome_get_praenomen_abbr( integer prae, char* buf )
{
 if ( prae == 0 )
  utn_cs_copy( " ", buf );
 else
 {
  utn_cs_copy( praefix[prae-1], buf );
  if ( utn_cs_is_blank( buf ))
   utn_cs_copy( praenomina[prae-1], buf );
 }
}
