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

package gov.sao.asc.obsed.view.event;

/******************************************************************************/
/**
 * ControlEvent is used to notify interested parties that a `control'
 * event has occurred.  Control events are `save', `save all',
 * `cancel' and `cancel all' which can be generated either from the
 * ViewMenu or the FormView control panel.
 */

public class ControlEvent
{
  
  String controlCommand;

  /****************************************************************************/
  /**
   * Constructs a ControlEvent.
   * 
   * @param command the command associated with the control event
   * (such as `save' or `cancel', etc).
   */
  public ControlEvent( String command )
  {
    this.controlCommand = command;
  }

  /****************************************************************************/
  /**
   * Return the control event command string.
   */
  public String getControlCommand()
  {
    return( controlCommand );
  }
  
  /****************************************************************************/

}

/******************************************************************************/
