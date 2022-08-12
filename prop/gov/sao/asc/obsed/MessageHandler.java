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

import gov.sao.asc.obsed.view.View;
import gov.sao.asc.obsed.view.ViewMessage;
import gov.sao.asc.util.LogClient;
import java.util.Vector;

/******************************************************************************/

public class MessageHandler extends Thread
{
  private Vector<Message> messageQueue;

  /****************************************************************************/

  public MessageHandler()
  {
    messageQueue = new Vector<Message>();
  }

  /****************************************************************************/
  /**
   * addMessage: Place a message onto the message handler queue.
   */
  
  public synchronized void addMessage(Message message)
  {
    messageQueue.addElement( message );
    notify();
  }

  /****************************************************************************/

  public void handleMessage( ViewMessage message )
  {
    int command = message.getCommand();
    View view = message.getView();

    switch (command)
    {
    case ViewMessage.INIT:
      {
        if (view instanceof View)
        {
          ((View) view).init();
        }
        
        break;
      }
    case ViewMessage.REFRESH:
      {
        if (view instanceof View)
        {
          ((View) view).refresh();
        }
        
        break;
      }
    default:
      {
        break;
      }
    }
  }
  
  /****************************************************************************/ 

  public synchronized void run()
  {
    while( true )
    {
      if (messageQueue.size() == 0)
      {
	try
	{
	  wait();
	}
	catch (Exception e)
	{
	  LogClient.printStackTrace( e );
	}
      }
      else
      {
	Message message = (Message) messageQueue.elementAt(0);
	
	if ( message instanceof ViewMessage )
	{
	  handleMessage( (ViewMessage) message );
	}
	
	messageQueue.removeElementAt( 0 );
      }
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
