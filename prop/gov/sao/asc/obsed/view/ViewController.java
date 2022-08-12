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

package gov.sao.asc.obsed.view;

/******************************************************************************/

import gov.sao.asc.obsed.constraint.ConstraintViolationException;
import gov.sao.asc.obsed.view.event.ComponentChangeEvent;
import gov.sao.asc.obsed.view.event.ComponentChangeListener;
import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

/******************************************************************************/
/**
 * tbd
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public abstract class ViewController extends Object
{
  protected Configuration configuration;
  protected EventListenerList listenerList = new EventListenerList();
  protected ViewModel model;
  protected View view;

  /****************************************************************************/
  /**
   * Default constructor.
   */

  public ViewController(View view)
  {
    this.view = view;
  }

  /****************************************************************************/
  /**
   * Add a component change listener.
   *
   * @param listener  The component change listener.
   */

  public void addComponentChangeListener( ComponentChangeListener listener )
  {
    listenerList.add( ComponentChangeListener.class, listener );
  }

  /****************************************************************************/
  /**
   * Return the view object being controlled.
   */

  public View getView()
  {
    return view;
  }

  /****************************************************************************/
  /**
   * Tell the change event listeners that a new observation is now
   * being displayed.
   */

  protected void fireChangeEvent( ComponentChangeEvent changeEvent )
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event:
    if ( changeEvent == null )
    {
      changeEvent = new ComponentChangeEvent( null, null, null );
    }

    // Process the listeners last to first, notifying those that are
    // interested in this event
    for ( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if ( listeners[ i ] == ComponentChangeListener.class )
      {
	((ComponentChangeListener) listeners[ i + 1 ]).componentChanged( changeEvent);
      }              
    }
  } 

  /****************************************************************************/
  /**
   * Remove a component change listener.
   *
   * @param listener  The component change listener to remove.
   */

  public void removeComponentChangeListener( ComponentChangeListener listener )
  {
    listenerList.remove( ComponentChangeListener.class, listener );
  }

  /****************************************************************************/
  /**
   * 
   */

  public void setConfiguration( Configuration configuration )
  {
    this.configuration = configuration;
  }

  /****************************************************************************/
  /**
   * 
   */

  public void setModel( ViewModel model )
  {
    this.model = model;
  }

  /****************************************************************************/

}

/******************************************************************************/
