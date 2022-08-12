/*
  Copyrights:
 
  Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting documentation, and that the name of the Smithsonian
  Astrophysical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific, written
  prior permission.  The Smithsonian Astrophysical Observatory makes
  no representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN
  ASTROPHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
  AND FITNESS, IN NO EVENT SHALL THE SMITHSONIAN INSTITUTION AND/OR
  THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY BE LIABLE FOR ANY SPECIAL,
  INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
  RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
  CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
  CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

package gov.sao.asc.util;

/******************************************************************************/

import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/******************************************************************************/

public class XML
{

  /****************************************************************************/

  public static Vector<Node> getChildNodes(Node parent, String elementName)
  {
    Vector<Node> result = new Vector<Node>();

    NodeList nodeList = parent.getChildNodes();

    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node node = nodeList.item(i);

      if (node instanceof Element)
      {
        if ( node.getNodeName().equals(elementName) )
        {
          result.addElement( node );
        }
      }
    }
    
    return(result);
  }

  /****************************************************************************/

  public static Element getChildElement(Node parent, String elementName)
  {
    Element result = null;

    NodeList nodeList = parent.getChildNodes();

    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node node = nodeList.item(i);

      if (node instanceof Element)
      {
        if ( node.getNodeName().equals(elementName) )
        {
          result = (Element) node;
          break;
        }
      }
    }
    
    return(result);
  }

  /****************************************************************************/

  public static String getTextForChildElement(Node parent, String elementName)
  {
    String result = null;

    Element element = getChildElement(parent, elementName);

    if (element != null)
    {
      Node firstChild = element.getFirstChild();

      if (firstChild != null)
      {
        result = firstChild.getNodeValue();
      }
    }
    
    return(result);
  }

  /****************************************************************************/

}

/******************************************************************************/
