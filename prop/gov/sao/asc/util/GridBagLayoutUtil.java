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

package gov.sao.asc.util;

/******************************************************************************/

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/******************************************************************************/

public class GridBagLayoutUtil
{

  /****************************************************************************/

  public static void addComponent(Container container, Component component,
                                  int xGrid, int yGrid, 
                                  int gridWidth, int gridHeight,
                                  int fill, int anchor, 
                                  double xWeight, double yWeight,
                                  int top, int left, int bottom, int right)
  {
    GridBagLayout layout;
    GridBagConstraints constraints;
    
    constraints = new GridBagConstraints();
    
    constraints.gridx = xGrid; 
    constraints.gridy = yGrid;

    constraints.gridwidth = gridWidth; 
    constraints.gridheight = gridHeight;

    constraints.fill = fill; 

    constraints.anchor = anchor;

    constraints.weightx = xWeight; 
    constraints.weighty = yWeight;

    if (top + bottom + left + right > 0)
      {
        constraints.insets = new Insets(top, left, bottom, right);
      }
        
    layout = (GridBagLayout) container.getLayout();
    
    layout.setConstraints(component, constraints);
      
    container.add(component);
  }

  /****************************************************************************/    

  public static void addComponent(Container container, Component component,
				  int xGrid, int yGrid, 
                                  int gridWidth, int gridHeight)
  {
    GridBagLayout layout;
    GridBagConstraints constraints;
    
    constraints = new GridBagConstraints();

    constraints.gridx = xGrid; 
    constraints.gridy = yGrid;

    constraints.gridwidth = gridWidth; 
    constraints.gridheight = gridHeight;

    constraints.fill = GridBagConstraints.BOTH; 

    constraints.anchor = GridBagConstraints.CENTER;

    constraints.weightx = 1; 
    constraints.weighty = 1;

    layout = (GridBagLayout) container.getLayout();
    
    layout.setConstraints(component, constraints);
    
    container.add(component);
  }

  /****************************************************************************/
  
  public static void addComponent(Container container, Component component,
				  int xGrid, int yGrid, 
                                  int gridWidth, int gridHeight,
				  int fill, int anchor, 
                                  double xWeight, double yWeight)

  {
    GridBagLayout layout;
    GridBagConstraints constraints;

    constraints = new GridBagConstraints();

    constraints.gridx = xGrid; 
    constraints.gridy = yGrid;

    constraints.gridwidth = gridWidth; 
    constraints.gridheight = gridHeight;

    constraints.fill = fill; 

    constraints.anchor = anchor;

    constraints.weightx = xWeight; 
    constraints.weighty = yWeight;

    layout = (GridBagLayout) container.getLayout();

    layout.setConstraints(component, constraints);

    container.add(component);
  }

  /****************************************************************************/

}

/******************************************************************************/
