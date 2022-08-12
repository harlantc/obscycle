package info;
/*
  Copyrights:
 
  Copyright (c) 2000-2021 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and  sell  this
  software  and  its  documentation  for any purpose is hereby
  granted without  fee,  provided  that  the  above  copyright
  notice  appear  in  all  copies and that both that copyright
  notice and this permission notice appear in supporting docu-
  mentation,  and  that  the  name  of the  Smithsonian Astro-
  physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific,
  written  prior  permission.   The Smithsonian  Astrophysical
  Observatory makes no representations about  the  suitability
  of  this  software for any purpose.  It is provided  "as is"
  without express or implied warranty.
  THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
  ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
  INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
  OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
  OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
  OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
  THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/
/**
 * Provide regular expressions and constants used by one or more tools.
 */

public interface ToolkitConstants
{
  
  /**
   * Generic decimal expression consisting of an optional sign, zero
   * or more leading digits [0-9], an optional decimal point and zero
   * or more trailing digits.
   */

  public static final String RE_DECIMAL = "[+-]?\\d*\\.?\\d*";

  /**
   * A positive decimal expression consisting of an optional (plus)
   * sign, zero or more leading digits], an optional decimal point and
   * zero or more trailing digits.
   */

  public static final String RE_NONNEGATIVE_DECIMAL = "[+]?\\d*\\.?\\d*";

  /**
   * An equinox value consisting of a leading `B' or `J', one or more
   * leading digits, an optional decimal point and zero or more trailing
   * digits.
   */

  public static final String RE_EQUINOX = "[BJ]\\d+\\.?\\d*";

  /**
   * A sexagesimal value consisting of one of two forms: 1) an
   * optional sign followed by two integers separated by whitespace or
   * 2) an optional sign followed by two integers separated by
   * whitespace and either an integer or a decimal value preceded by
   * whitespace.
   */

  public static final String RE_SEXAGESIMAL =
    "([+-]?\\d+) (\\d+) (\\d*\\.?\\d+)|([+-]?\\d+) (\\d+)";

  /**
   * An extractable decimal expression.
   */

  public static final String RE_MATCHED_NUMBER = "(" + RE_DECIMAL + ")";

  /**
   * A sexagesimal expression guaranteed to be positive.
   */

  public static final String RE_NONNEGATIVE_SEXAGESIMAL =
    "([+]?\\d+) (\\d+) (\\d*\\.?\\d+)|([+]?\\d+) (\\d+)";

  /**
   * An optional sign followed by one or more digits.
   */

  public static final String RE_INTEGER = "[+-]?\\d+";

  /**
   * An extractable number which can be a decimal expression or an
   * integer expression with either followed by an optional exponent.
   * The exponent is case insensitive and is of the form `e' followed
   * by an optional sign, one or more digits and a optional float `f'
   * or double `d' indication.
   */

  public static final String RE_SCIENTIFIC =
    "(" + RE_DECIMAL + "|" + RE_INTEGER + ")[eE][+-]?\\d+[FfDd]?";

  /**
   * One or more of a space, tab, or newline character.
   */

  public static final String RE_WHITESPACE = "\\s+";

  /**
   * Either a scientific number, a decimal or an integer.
   */

  public static final String RE_NUMBER =
    RE_SCIENTIFIC + "|" + RE_DECIMAL + "|" + RE_INTEGER;

  /**
   * An optional sign followed by zero or more digts, an optional
   * decimal point and an optional trailing digit.
   */

  public static final String RE_FRAMETIME = "[+-]?\\d*\\.?\\d?";

  /**
   * Whitespace followed by an extractable sequence consisting of a
   * decimal expression, more whitespace and the string "UTC".
   */

  public static final String RE_JD_VALUE = RE_WHITESPACE + "(" + RE_DECIMAL +
      RE_WHITESPACE + "UTC)";

  /**
   * An extractable sequence consisting of one or more digits, one or
   * more word tokens, Whitespace and anything else.
   */

  public static final String RE_DATE = "(\\d+\\s+\\w+\\s+.+)";

  /**
   * The string "JD" followed by a JD expression.
   */

  public static final String RE_JD = "JD" + RE_JD_VALUE;

  /**
   * The string "MJD" followed by a JD expression.
   */

  public static final String RE_MJD = "MJD" + RE_JD_VALUE;

  /**
   * Relative Time format: DDD:HH:MM:SS[.SSS]
   */

  public static final String RE_RELATIVE_TIME = "(\\d+):(\\d+):(\\d+):(\\d+\\.?\\d*)";

  /**
   * Absolute Time format #1: YYYY:DDD:HH:MM:SS[.SSS]
   */

  public static final String RE_ABSOLUTE_TIME1 = "(\\d+):(\\d+):(\\d+):(\\d+):(\\d+\\.?\\d*)";

  /**
   * Absolute Time format #2: YYYY-MM-DDTHH:MM:SS
   */

  public static final String RE_ABSOLUTE_TIME2 = "(\\d+)-(\\d+)-(\\d+)T(\\d+):(\\d+):(\\d+\\.?\\d*)";

  /**
   * One or more digits followed by a decimal point and another set of
   * digits.
   */

  public static final String RE_DOY = "(\\d+\\.\\d+)";

  /**
   * An extactable number.
   */

  public static final String RE_TIME = "(" + RE_NUMBER + ")";

  /**
   * One or more digits followed by a decimal point and another set of
   * digits.
   */

  public static final String RE_EMBEDDED_PERIOD = "(\\d+\\.\\d+)";

  /**
   * Ecliptic Coordinate System constant.
   */

  public static final int SYSTEM_ECLIPTIC = 0;

  /**
   * Equatorial Coordinate System constant.
   */

  public static final int SYSTEM_EQUATORIAL = 1;

  /**
   * Galactic Coordinate System constant.
   */

  public static final int SYSTEM_GALACTIC = 2;
  
  /**
   * Decimal Format constant.
   */

  public static final int FORMAT_DECIMAL = 0;

  /**
   * Sexagesimal Format constant.
   */

  public static final int FORMAT_SEXAGESIMAL = 1;

  /**
   * Raymond Smith cutoff constant.
   */

  public static final double RAYMOND_SMITH_CUTOFF = 8.0;

  /****************************************************************************/
  public static String SITE_KEY="6LdyL08UAAAAAIN1Ssu3fj3xjHxZ5v_Ikb3CDYgx";


  public static String[] lblmissionChoices = { 
     "ASCA",
     "HITOMI",
     "CHANDRA-Cycle 24",
     "CHANDRA-Cycle 23",
     "CHANDRA-Cycle 22",
     "CHANDRA-Cycle 21",
     "CHANDRA-Cycle 20",
     "CHANDRA-Cycle 19",
     "CHANDRA-Cycle 18",
     "CHANDRA-Cycle 17",
     "CHANDRA-Cycle 16",
     "CHANDRA-Cycle 15",
     "CHANDRA-Cycle 14",
     "CHANDRA-Cycle 13",
     "CHANDRA-Cycle 12",
     "CHANDRA-Cycle 11",
     "CHANDRA-Cycle 10",
     "CHANDRA-Cycle 9",
     "CHANDRA-Cycle 8",
     "CHANDRA-Cycle 7",
     "CHANDRA-Cycle 6",
     "CHANDRA-Cycle 5",
     "CHANDRA-Cycle 4",
     "CHANDRA-Cycle 3",
     "EINSTEIN",
     "EXOSAT",
     "GINGA",
     "NUSTAR",
     "ROSAT",
     "SAX",
     "SUZAKU",
     "SWIFT",
     "XMM",
     "XTE"
   };

   public static String[] missionChoices = {
     "ASCA",
     "HITOMI",
     "CHANDRA-AO24",
     "CHANDRA-AO23",
     "CHANDRA-AO22",
     "CHANDRA-AO21",
     "CHANDRA-AO20",
     "CHANDRA-AO19",
     "CHANDRA-AO18",
     "CHANDRA-AO17",
     "CHANDRA-AO16",
     "CHANDRA-AO15",
     "CHANDRA-AO14",
     "CHANDRA-AO13",
     "CHANDRA-AO12",
     "CHANDRA-AO11",
     "CHANDRA-AO10",
     "CHANDRA-AO9",
     "CHANDRA-AO8",
     "CHANDRA-AO7",
     "CHANDRA-AO6",
     "CHANDRA-AO5",
     "CHANDRA-AO4",
     "CHANDRA-AO3",
     "EINSTEIN",
     "EXOSAT",
     "GINGA",
     "NUSTAR",
     "ROSAT",
     "SAX",
     "SUZAKU",
     "SWIFT",
     "XMM",
     "XTE"
   };


}

/******************************************************************************/
