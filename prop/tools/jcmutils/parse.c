/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

ParseTokenType p_type[] = {
 PTT_Null, 
 PTT_Control, PTT_Control, PTT_Control, PTT_Control, PTT_Control, 
 PTT_Control, PTT_Control, PTT_Control, PTT_Control, PTT_Control, 
 PTT_Control, PTT_Control, PTT_Control, PTT_Control, PTT_Control, 
 PTT_Control, PTT_Control, PTT_Control, PTT_Control, PTT_Control, 
 PTT_Control, PTT_Control, PTT_Control, PTT_Control, PTT_Control, 
 PTT_Control, PTT_Control, PTT_Control, PTT_Control, PTT_Control, 
 PTT_Control, PTT_Space,   PTT_Special, PTT_Special, PTT_Special,
 PTT_Special, PTT_Special, PTT_Special, PTT_Special, PTT_LPar,
 PTT_RPar,    PTT_Op,      PTT_Sign,    PTT_Comma,   PTT_Sign,
 PTT_Point,   PTT_Op,      PTT_Digit,   PTT_Digit,   PTT_Digit,
 PTT_Digit,   PTT_Digit,   PTT_Digit,   PTT_Digit,   PTT_Digit,
 PTT_Digit,   PTT_Digit,   PTT_Special, PTT_Special, PTT_Rel,
 PTT_Rel,     PTT_Rel,     PTT_Special, PTT_Special, PTT_UC_Let,
 PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let,
 PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let,
 PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let,
 PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let,
 PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let, PTT_UC_Let,
 PTT_SLPar,  PTT_Special, PTT_SRPar,   PTT_Special, PTT_Underbar, 
 PTT_Special, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let,
 PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let,
 PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let,
 PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let,
 PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let, PTT_LC_Let,
 PTT_LC_Let, PTT_LC_Let, PTT_Special, PTT_Special, PTT_Special, 
 PTT_Special, PTT_Special
};

char* p_name[] = { "Null", "Control", "Special Char", "Space",
  "LPar", "RPar", "Op", "Sign", "Comma", "Dot", "Rel", "SLPar",
  "SRPar", "Underbar", "Digit", "UC Letter", "LC Letter", 
  "Letter", "Letters", "Alphanumeric", "Integer", "Real", "Real(E)",
  "Double", "Logical", "Word", "Number", "Symbol"
};


logical utn_cs_parse_compare( ParseTokenType code, ParseTokenType gcode );
void char_type( ParseTokenType code, char* type );
void utn_cs_parser_efmt( char* buf, long* itemcode, long* itempos, long* nitems );
void utn_cs_parse_efmtpack( long i, long pack,long* itemcode, 
  long* itempos, long* n, long itemlen, long epos, long ecode );
void utn_cs_parse_efmtread( char* item, long itemlen, long* epos_p, logical* efmt_p, ParseTokenType* ecode_p );
void utn_cs_parser_chars( char* buf, long* itemcode, long* nitems );

void utn_cs_parser_groups( char* buf, long* itemcode, long* itempos, long* nitems, logical allow_an, logical debug );
void utn_cs_parser_words( char* buf, long* itemcode, long* itempos, long* nitems, logical debug );
void utn_cs_parser_class( long* itemcode, long nitems );
void utn_cs_parse_repack( long i, long* nitems, long* itemcode, long* itempos, long pack );


void utn_cs_char_name( character c, char* name )
{
 long i;
 i = (long)c;
 if ( i < 0 || i >= 2 * UT_NCHARS ) {
  utn_cs_copy( "##", name );
 } else {
  if ( i >= UT_NCHARS ) {
   *name++ = '@';
   i -= UT_NCHARS;
  }
  switch( i ) {
   case 0:
    utn_cs_copy( "NUL", name );
   case 27: 
    utn_cs_copy( "ESC", name ); break;
   case 28:
    utn_cs_copy( "FS", name ); break;
   case 29:
    utn_cs_copy( "GS", name ); break;
   case 30:
    utn_cs_copy( "RS", name ); break;
   case 31:
    utn_cs_copy( "US", name ); break;
   case 32:
    utn_cs_copy( "SP", name ); break;
   case 127:
    utn_cs_copy( "DEL", name ); break;
   default:
    if ( i < 26 ) {
     *name++ = '^';
     *name++ = 'A' + i - 1;
     *name   = '\0';
    } else {
     *name++ = (char)i;
     *name   = '\0';
    }
    break;
  }
 }

}


void char_type( ParseTokenType code, char* type )
{
/* Bug fix 1997 Jan 27: code >=0 gives warning, code is unsigned */
 if ( code < UT_PARSE_NTYPES ) {
  utn_cs_copy( p_name[code], type );
 } else {
  utn_cs_copy( "Unknown", type );
 }
}


ParseTokenType utn_cs_parse_namecode( char* name )
{
 return( utn_ar_cmatch_c( name, p_name, UT_PARSE_NTYPES ) );
}

ParseTokenType utn_char_code( character c )
{
 long i;

 i = (int)c;
 if ( i >= UT_NCHARS ) {
  return( -1 );
 } else {
  return( p_type[i] );
 }
}

void utn_cs_parser( char* buf, long* itemcode, long* itempos, long* nitems, char* cmode, logical debug )
{
 TextWord mode;

 logical an;
 integer item_id;
 utn_cs_copy( cmode, mode );
 utn_cs_upper( mode );
 if ( utn_cs_ss( mode, "CHAR" ) ) {
  utn_cs_parser_chars( buf,itemcode, nitems );
  for ( item_id = 1; item_id <= *nitems; item_id++ ) {
   itempos[item_id-1] = item_id;
  }
  if ( debug ) utn_cs_parse_disp( buf, itemcode, itempos, *nitems );
 }
 if ( utn_cs_ss( mode, "GROUP" ) ) {
  an = !utn_cs_ss( mode, "NOAN" );
  utn_cs_parser_groups( buf, itemcode, itempos, nitems, an, debug );
 }

 if ( utn_cs_ss( mode, "WORD" ) ) {
  utn_cs_parser_words( buf, itemcode, itempos, nitems, debug );
 }

 if ( utn_cs_ss( mode, "CLASS" ) ) {
  utn_cs_parser_class( itemcode, *nitems );
  if ( debug ) utn_cs_parse_disp( buf, itemcode, itempos, *nitems );
 }
}


void utn_cs_parser_chars( char* buf, long* itemcode, long* nitems )
{
 long nchars;
 long i;
 nchars = utn_cs_ends( buf );
 for ( i = 0; i < nchars; i++ ) {
  itemcode[i] = utn_char_code( *buf++ );
 }
 *nitems = nchars;
}

/* itempos set to end pos of item */
void utn_cs_parser_groups( char* buf, long* itemcode, long* itempos, long* nitems, logical allow_an, logical debug )
{
 long n;
 ParseTokenType icode;
 ParseTokenType jcode;
 integer item_id = 1;
 integer offset;
 logical newitem;
 logical q_text, q_an1, q_an2;

 n = utn_cs_ends( buf );
 icode = utn_char_code( buf[0] );
 itemcode[0] = icode;
 for ( offset = 1; offset < n; offset++ ) {
  jcode = utn_char_code( buf[offset] );
  newitem = utn_cs_parse_compare( jcode, PTT_Symbol ) && 
   ! ( jcode == PTT_Op || jcode == PTT_Rel || jcode == PTT_Sign || jcode == PTT_Underbar );
  if( jcode == icode ) {
   if ( icode == PTT_Digit ) itemcode[item_id-1] = PTT_Integer;
   if ( utn_cs_parse_compare( icode, PTT_Letter ) ) itemcode[item_id-1] = PTT_Letters;
  } else {
   q_text = utn_cs_parse_compare( jcode, PTT_Letter ) && utn_cs_parse_compare( icode, PTT_Letters );
   q_an1 = utn_cs_parse_compare( icode, PTT_Word );
   q_an1  = utn_cs_parse_compare( jcode, PTT_Digit ) && utn_cs_parse_compare( icode, PTT_Word );
   q_an2  = ( utn_cs_parse_compare( icode, PTT_Integer ) || icode == PTT_Alphanumeric ) && utn_cs_parse_compare( jcode, PTT_Letter );
   if ( q_text ) {
    icode = PTT_Letters;
    itemcode[item_id-1]= PTT_Letters;
   } else if ( allow_an && ( q_an1 || q_an2 ) ) {
    icode = PTT_Alphanumeric;    
    itemcode[item_id-1] = icode;
   } else {
    newitem = UT_TRUE;
   } 
  }
  if ( newitem ) {
/* Last word is previous pos  = (offset-1)+1 */
   itempos[item_id-1] = offset;
   item_id++;
   itemcode[item_id-1] = jcode;
   icode = jcode;
  }
 }
/* Last item pos is eos */
 itempos[item_id-1] = n;
 *nitems = item_id;
 if ( debug ) utn_cs_parse_disp( buf, itemcode, itempos, *nitems );
}



logical utn_cs_parse_compare( ParseTokenType code, ParseTokenType gcode )
{
 logical match;
 match = ( code == gcode );
 if ( !match ) {
  if ( gcode == PTT_Letter ) {
   match =  ( code == PTT_UC_Let || code == PTT_LC_Let || code == PTT_Underbar );
  } else if ( gcode == PTT_Integer ) {
   match =  ( code == PTT_Digit );
  } else if ( gcode == PTT_Letters ) {
   match = ( code == PTT_Letter || code == PTT_UC_Let ||
             code == PTT_LC_Let || code == PTT_Underbar );
  } else if ( gcode == PTT_Number ) {
   match = ( code == PTT_Digit ) || ( code == PTT_Integer ) ||
           ( code == PTT_Real  ) || ( code == PTT_Real_E ) || ( code == PTT_Double );
  } else if ( gcode == PTT_Word ) {
   match = ( code == PTT_Letter ) || ( code == PTT_UC_Let ) || ( code == PTT_LC_Let )
           || ( code == PTT_Alphanumeric ) || ( code == PTT_Letters ) || ( code == PTT_Underbar );
  } else if ( gcode == PTT_Symbol ) {
   match = ( code == PTT_Special ) || ( code == PTT_Op ) || ( code == PTT_Comma ) ||
   ( code == PTT_Rel ) || ( code == PTT_Sign ) || ( code == PTT_Logical ) || 
   ( code == PTT_LPar ) || ( code == PTT_RPar ) || ( code == PTT_Point ) || ( code == PTT_SLPar )
   || ( code == PTT_SRPar ) || ( code == PTT_Underbar );
  }
 }
 return( match );
}



void utn_cs_parse_fmt( char* buf, long* itemcode, long* itempos, long nitems, char* mbuf )
{
 char* ptr;
 TextWord name;
 TextCard word;
 TextCard tmp;
 long siz = 512;

 long i1,i2;
 long n;
 integer item_id;
 ptr = mbuf;
 for ( item_id = 1; item_id <= nitems; item_id++ ) {
  char_type( itemcode[item_id-1], name );
/* Beginning of field is last item + 1 */
  i1 = ( item_id == 1 ) ? 1 : itempos[item_id-2]+1; 
/* itempos for this item gives end of field */
  i2 = itempos[item_id-1];
  utn_cs_get_ss( buf, word, i1, i2 );
  n = snprintf( tmp, UT_CARD_SIZE, "%s[%s] ", name, word );
  siz -= n;
  if (siz > 0 ) {
   utn_cs_copy_siz( tmp, ptr, siz );
   ptr += n;
  } 
 }

}

void utn_cs_parse_tdisp( char* buf, long* itemcode, long* itempos, long nitems, char* mbuf )
{

 char* ptr;
 TextWord name;
 TextCard word;
 TextCard tmp;
 long siz = 512;
 long i;
 long i1,i2;
 long n;
 integer item_id;
 ptr = mbuf;
 for ( item_id = 1; item_id <= nitems; item_id++ ) {
  i = item_id -1;
  char_type( itemcode[i], name );
  i1 = ( i == 0 ) ? 1 : itempos[i-1]+1; 
  i2 = itempos[i];
  utn_cs_get_ss( buf, word, i1, i2 );
  n = snprintf( tmp, UT_CARD_SIZE, "%s[%s] ", name, word );
  siz -= n;
  if (siz > 0 ) {
   utn_cs_copy_siz( tmp, ptr, siz );
   ptr += n;
  } 
 }

}



void utn_cs_parse_read( integer item_id, char* buf, long* itempos, char* token, long* siz )
{
 long j1, j2;
 if ( item_id == 1 ) {
  j1 = 1;
 } else {
  j1 = itempos[item_id-2]+1;
 }
 j2 = itempos[item_id-1];
 utn_cs_get_ss( buf, token, j1, j2 );
 *siz = j2 - j1 + 1;
}

void utn_cs_parser_words( char* buf, long* itemcode, long* itempos, long* nitems, logical debug )
{
 long n;

 long icode;
 integer item_id;
 integer item_offset;
 n = *nitems;
 item_id = 1;

 while ( item_id <= *nitems ) {
  item_offset = item_id - 1;
  icode = itemcode[item_offset];

/* LOGICAL */
  if ( icode == PTT_Point && item_id <= *nitems-2 ) {
   if ( utn_cs_parse_compare( itemcode[item_offset+1], PTT_Letters )  && itemcode[item_offset+2] == PTT_Point ) {
    itemcode[item_offset] = PTT_Logical;
    icode = PTT_Logical;
    utn_cs_parse_repack( item_id, nitems, itemcode, itempos, 2 );
   }
  }
  item_id++;
 }

/* REAL */
 item_id = 1;
 while ( item_id <= *nitems ) {
  item_offset = item_id - 1;
  icode = itemcode[item_offset];
  if ( utn_cs_parse_compare( icode, PTT_Integer ) && item_id < *nitems  ) {
   if ( itemcode[item_offset+1] == PTT_Point ) {
    itemcode[item_offset] = PTT_Real;
    icode = PTT_Real;
    if ( item_id < *nitems - 1 && utn_cs_parse_compare( itemcode[item_offset+2], PTT_Integer ) ) {
     utn_cs_parse_repack( item_id, nitems, itemcode, itempos, 2 );
    } else {
     utn_cs_parse_repack( item_id, nitems, itemcode, itempos, 1 );     
    }
   }
  } else if ( icode == PTT_Point && item_id < *nitems ) {
   if ( utn_cs_parse_compare( itemcode[item_offset+1], PTT_Integer ) ) {
    itemcode[item_offset] = PTT_Real;
    icode = PTT_Real;
    utn_cs_parse_repack( item_id, nitems, itemcode, itempos, 1 );
   }
  }
  item_id++;
 }

 utn_cs_parser_efmt( buf, itemcode, itempos, nitems );

 item_id = 1;
 while ( item_id <= *nitems ) {
  item_offset = item_id - 1;
  icode = itemcode[item_offset]; 
  if ( icode == PTT_Space ) {
   if ( item_id > 1 ) itempos[item_offset-1] = itempos[item_offset];
   itemcode[item_offset] = itemcode[item_offset+1];
   utn_cs_parse_repack( item_id, nitems, itemcode, itempos, 1 );
  }
  item_id++;
 }
 if ( debug ) utn_cs_parse_disp( buf, itemcode, itempos, *nitems );
}


void utn_cs_parser_class( long* itemcode, long nitems )
{
 integer item_id; 
 integer item_offset;
 long icode;
 for ( item_id = 1; item_id <= nitems; item_id++ ) {
  item_offset = item_id - 1;
  icode = itemcode[item_offset];
  if ( utn_cs_parse_compare( icode, PTT_Number ) ) {
   itemcode[item_offset] = PTT_Number;
  } else if ( utn_cs_parse_compare( icode, PTT_Word )) {
   itemcode[item_offset] = PTT_Word;
  } else {
   itemcode[item_offset] = PTT_Symbol;
  }
 }
}


void utn_cs_parse_repack( long item_id, long* nitems, long* itemcode, long* itempos, long pack )
{
 long n;
 long k;
 integer item_offset = item_id -1;
 n = (*nitems) - pack;
 itempos[item_offset] = itempos[item_offset+pack];
 if ( item_id < n ) {
  for ( k = item_offset+1; k < n; k++ ) {
   itemcode[k] = itemcode[k+pack];
   itempos[k]  = itempos[k+pack];
  }
 }
 *nitems = n;
}


void utn_cs_parse_efmtread( char* item, long itemlen, long* epos_p, logical* efmt_p, ParseTokenType* ecode_p )
{
 long epos, dpos;
 long ecode;
 logical efmt;
 long pos;
 long icode;
 ecode = -1;
 epos = utn_cs_index_char( item, 'E' );
 dpos = utn_cs_index_char( item, 'D' );
 if ( dpos > 0 ) epos = dpos;
 efmt = epos > 0;
 if ( efmt ) {
  pos = 0;
  while( pos < itemlen && efmt ) {
   pos++;
   if ( pos != epos ) {
    icode = utn_char_code( item[pos-1] );
    efmt = icode == PTT_Digit || icode == PTT_Sign || icode == PTT_Point;
   }
  }
 }
 if ( efmt ) {
  if ( dpos > 0 ) {
   ecode = PTT_Double;
  } else {
   ecode = PTT_Real_E;
  }
 }
 *epos_p = epos;
 *ecode_p = ecode;
 *efmt_p = efmt;
}


void utn_cs_parse_efmtpack( long item_id, long pack,long* itemcode, 
  long* itempos, long* n, long itemlen, long epos, long ecode )
{
 long j;
 integer item_offset = item_id - 1;
 integer new_item_offset;
 if ( epos != itemlen ) {
  j = pack - 2;
  if ( j > 0 ) utn_cs_parse_repack( item_id, n, itemcode, itempos, j );
  itemcode[item_offset] = ecode;
 } else if ( item_offset < *n - pack ) {
  new_item_offset = item_offset + pack;
  if ( itemcode[new_item_offset-1] == PTT_Sign
    && utn_cs_parse_compare( itemcode[new_item_offset], PTT_Integer ) ) {
   utn_cs_parse_repack( item_id, n, itemcode, itempos, pack );
   itemcode[item_offset] = ecode;
  } 
 } else if ( utn_cs_parse_compare( itemcode[item_offset+pack-1], PTT_Integer ) ) {
  utn_cs_parse_repack( item_id, n, itemcode, itempos, pack-1 );
  itemcode[item_offset] = ecode;
 }
}


void utn_cs_parser_efmt( char* buf, long* itemcode, long* itempos, long* nitems )
{

 long icode;
 long kcode = 0;
 long itemlen = 0;
 logical efmt;
 TextCard item;
 long epos;
 ParseTokenType ecode;
 integer item_id;
 integer item_offset;
 item_id = 1;
 while( item_id <= *nitems ) {
  item_offset = item_id-1;
  icode = itemcode[item_offset];
  if ( item_id < *nitems  ) {
   kcode = itemcode[item_offset+1];
   utn_cs_parse_read( item_id+1, buf, itempos, item, &itemlen );
   utn_cs_upper( item );
  }
  if ( utn_cs_parse_compare( icode, PTT_Number ) && item_id < *nitems  ) {
   if ( utn_cs_parse_compare( kcode, PTT_Letter ) || kcode == PTT_Alphanumeric ) {
    utn_cs_parse_efmtread( item, itemlen, &epos, &efmt, &ecode );
    if ( efmt ) {
     utn_cs_parse_efmtpack( item_id, 3, itemcode, itempos, nitems, itemlen, epos, ecode );
     icode = itemcode[item_offset];
    }
   }   
  } else if ( icode == PTT_Point && item_id < *nitems  ) {
   if ( utn_cs_parse_compare( kcode, PTT_Letter ) || kcode == PTT_Alphanumeric ) {
    utn_cs_parse_efmtread( item, itemlen, &epos, &efmt, &ecode );
    if ( efmt && epos != 1 ) {
     utn_cs_parse_efmtpack( item_id, 3, itemcode, itempos, nitems, itemlen, epos, ecode );
     icode = itemcode[item_offset];
    }
   }
  } else if ( icode == PTT_Alphanumeric ) {
   utn_cs_parse_read( item_id, buf, itempos, item, &itemlen );
   utn_cs_upper( item );
   utn_cs_parse_efmtread( item, itemlen, &epos, &efmt, &ecode );
   if ( efmt && epos != 1 ) {
    utn_cs_parse_efmtpack( item_id, 2, itemcode, itempos, nitems, itemlen, epos, ecode );
    icode = itemcode[item_offset];
   }
  }
  item_id++;
 }
}
