/*
      Copyrights:
 
      Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
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

package gov.sao.asc.obsed;

/******************************************************************************/

public class Message extends Object
{
  public static final int ADD = 0;
  public static final int DELETE = 1;
  public static final int GET = 2;
  public static final int SET = 3;
  public static final int REFRESH = 4;
  public static final int INIT = 5;

  private int command;
  private String name;

  /****************************************************************************/

  public Message()
  {
  }

  /****************************************************************************/

  public Message( int command, String name )
  {
    this.command = command;
    this.name = name;
  }

  /****************************************************************************/

  public int getCommand() 
  {
    return command;
  }

  /****************************************************************************/

  public String getName()
  {
    return name;
  }

  /****************************************************************************/

  public void setCommand( int command )
  {
    this.command = command;
  }

  /****************************************************************************/

  public void setName( String name )
  {
    this.name = name;
  }

  /****************************************************************************/

}

/******************************************************************************/
