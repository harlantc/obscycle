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

import gov.sao.asc.obsed.event.FontSizeInterface;
import gov.sao.asc.obsed.event.FontSizeListener;
import gov.sao.asc.util.ToggleLookAndFeel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/******************************************************************************/
/**
 * The <code>PreferencesMenu</code> class sets up the Preferences menu
 * for the ObsCat GUI applet and application.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class PreferencesMenu extends JMenu implements ItemListener
{
  static final String TOP_TABS = "Tabs on Top";
  static final String LEFT_TABS = "Left Side Tabs";
  static final String RIGHT_TABS = "Right Side Tabs";
  static final String BOTTOM_TABS = "Bottom Tabs";

  JRadioButtonMenuItem topMenuItem;
  JRadioButtonMenuItem leftMenuItem;
  JRadioButtonMenuItem rightMenuItem;
  JRadioButtonMenuItem bottomMenuItem;
  ToggleLookAndFeel toggleLAF;

  /****************************************************************************/

  public PreferencesMenu(FontSizeInterface fontSizeInterface)
  {
    super("Preferences");

    add( initLookAndFeelMenu() );

    add( new JSeparator() );

    add( initTabPlacementMenu() );

    add( new JSeparator() );

    add( initFontSizeMenu(fontSizeInterface) );
  }

  /****************************************************************************/

  public JMenu initFontSizeMenu(FontSizeInterface fontSizeInterface)
  {
    JMenu result = new JMenu("Font Size");

    ButtonGroup buttonGroup = new ButtonGroup();

    JRadioButtonMenuItem eightPointMenuItem = new JRadioButtonMenuItem("8");

    FontSizeListener eightPointListener =
      new FontSizeListener(fontSizeInterface, 8);

    eightPointMenuItem.addActionListener(eightPointListener);

    buttonGroup.add(eightPointMenuItem);
    result.add(eightPointMenuItem);

    JRadioButtonMenuItem tenPointMenuItem = new JRadioButtonMenuItem("10");

    FontSizeListener tenPointListener =
      new FontSizeListener(fontSizeInterface, 10);

    tenPointMenuItem.addActionListener(tenPointListener);

    buttonGroup.add(tenPointMenuItem);
    result.add(tenPointMenuItem);

    JRadioButtonMenuItem twelvePointMenuItem = new JRadioButtonMenuItem("12");

    twelvePointMenuItem.setSelected(true);

    FontSizeListener twelvePointListener =
      new FontSizeListener(fontSizeInterface, 12);

    twelvePointMenuItem.addActionListener(twelvePointListener);

    buttonGroup.add(twelvePointMenuItem);
    result.add(twelvePointMenuItem);

    JRadioButtonMenuItem fourteenPointMenuItem = new JRadioButtonMenuItem("14");

    FontSizeListener fourteenPointListener =
      new FontSizeListener(fontSizeInterface, 14);

    fourteenPointMenuItem.addActionListener(fourteenPointListener);

    buttonGroup.add(fourteenPointMenuItem);
    result.add(fourteenPointMenuItem);

    JRadioButtonMenuItem sixteenPointMenuItem = new JRadioButtonMenuItem("16");

    FontSizeListener sixteenPointListener =
      new FontSizeListener(fontSizeInterface, 16);

    sixteenPointMenuItem.addActionListener(sixteenPointListener);

    buttonGroup.add(sixteenPointMenuItem);
    result.add(sixteenPointMenuItem);

    JRadioButtonMenuItem eighteenPointMenuItem = new JRadioButtonMenuItem("18");

    FontSizeListener eighteenPointListener =
      new FontSizeListener(fontSizeInterface, 18);

    eighteenPointMenuItem.addActionListener(eighteenPointListener);

    buttonGroup.add(eighteenPointMenuItem);
    result.add(eighteenPointMenuItem);

    return(result);
  }

  /****************************************************************************/

  public JMenu initLookAndFeelMenu()
  {
    JMenu result = new JMenu("Look And Feel");

    // Look and Feel Radio control
    ButtonGroup buttonGroup = new ButtonGroup();

    toggleLAF = new ToggleLookAndFeel();

    JRadioButtonMenuItem windowsMenuItem = 
      new JRadioButtonMenuItem("Windows Style Look and Feel");

    result.add(windowsMenuItem);
    buttonGroup.add(windowsMenuItem);
    windowsMenuItem.addItemListener(toggleLAF);

    JRadioButtonMenuItem motifMenuItem = 
      new JRadioButtonMenuItem("Motif Look and Feel");

    result.add(motifMenuItem);
    buttonGroup.add(motifMenuItem);
    motifMenuItem.addItemListener(toggleLAF);

    JRadioButtonMenuItem metalMenuItem = 
      new JRadioButtonMenuItem("Metal Look and Feel");

    result.add(metalMenuItem);
    buttonGroup.add(metalMenuItem);
    metalMenuItem.addItemListener(toggleLAF);

    JRadioButtonMenuItem macintoshMenuItem = 
      new JRadioButtonMenuItem("Macintosh Look and Feel");

    result.add(macintoshMenuItem);
    buttonGroup.add(macintoshMenuItem);
    macintoshMenuItem.addItemListener(toggleLAF);

    String lookAndFeel = UIManager.getLookAndFeel().getName();

    if ( lookAndFeel.equals("Windows") )
    {
      windowsMenuItem.setSelected(true);
    }
    else if ( lookAndFeel.equals("CDE/Motif") )
    {
      motifMenuItem.setSelected(true);
    }
    else if ( lookAndFeel.equals("Metal") )
    {
      metalMenuItem.setSelected(true);
    }
    else if ( lookAndFeel.equals("Macintosh") )
    {
      macintoshMenuItem.setSelected(true);
    }

    return(result);
  }

  /****************************************************************************/

  public JMenu initTabPlacementMenu()
  {
    JMenu result = new JMenu("Tab Placement");

    ButtonGroup tabGroup = new ButtonGroup();

    topMenuItem = new JRadioButtonMenuItem(TOP_TABS);

    topMenuItem.getAccessibleContext().setAccessibleDescription
      ("Position the TabbedPane on the top of the window");

    topMenuItem.addItemListener(this);

    result.add(topMenuItem);

    tabGroup.add(topMenuItem);
        
    leftMenuItem = new JRadioButtonMenuItem(LEFT_TABS);

    leftMenuItem.getAccessibleContext().setAccessibleDescription
      ("Position the TabbedPane on the left of the window");

    leftMenuItem.addItemListener(this);

    result.add(leftMenuItem);

    tabGroup.add(leftMenuItem);

    bottomMenuItem = new JRadioButtonMenuItem(BOTTOM_TABS);

    bottomMenuItem.getAccessibleContext().setAccessibleDescription
      ("Position the TabbedPane on the bottom of the window");

    bottomMenuItem.setSelected(true);

    bottomMenuItem.addItemListener(this);

    result.add(bottomMenuItem);

    tabGroup.add(bottomMenuItem);

    rightMenuItem = new JRadioButtonMenuItem(RIGHT_TABS);

    rightMenuItem.getAccessibleContext().setAccessibleDescription
      ("Position the TabbedPane on the right of the window");

    rightMenuItem.addItemListener(this);

    result.add(rightMenuItem);

    tabGroup.add(rightMenuItem);

    return(result);
  }

  /****************************************************************************/

  public void itemStateChanged( ItemEvent e ) 
  {
    JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) e.getSource();
    JTabbedPane tabbedPanel = Editor.getInstance().getTabbedPanel();

    if ( menuItem.isSelected() ) 
    {
      String selected = menuItem.getText();

      if (selected.equals(TOP_TABS)) 
      {
	tabbedPanel.setTabPlacement(JTabbedPane.TOP);
      } 
      else if (selected.equals(LEFT_TABS)) 
      {
	tabbedPanel.setTabPlacement(JTabbedPane.LEFT);
      } 
      else if (selected.equals(BOTTOM_TABS)) 
      {
	tabbedPanel.setTabPlacement(JTabbedPane.BOTTOM);
      } 
      else 
      {
	tabbedPanel.setTabPlacement(JTabbedPane.RIGHT);
      }

      tabbedPanel.validate();
    }
  }

  /****************************************************************************/

  public void setEnabled( boolean enabled )
  {
    JTabbedPane tabbedPanel = Editor.getInstance().getTabbedPanel();

    super.setEnabled( enabled );

    if ( ! enabled )
    {
      topMenuItem.setSelected( true );
    }
    else
    {
      int placement = tabbedPanel.getTabPlacement();
      switch ( placement )
      {
      case JTabbedPane.TOP:
        {
          topMenuItem.setSelected( true );
          break;
        }
      case JTabbedPane.LEFT:
        {
          leftMenuItem.setSelected( true );
          break;
        }
      case JTabbedPane.BOTTOM:
        {
          bottomMenuItem.setSelected( true );
          break;
        }
      case JTabbedPane.RIGHT:
        {
          rightMenuItem.setSelected( true );
          break;
        }
      default:
      }
    }
  }

  /****************************************************************************/

}
  
/******************************************************************************/
