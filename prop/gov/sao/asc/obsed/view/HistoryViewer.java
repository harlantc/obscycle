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

import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.util.ComponentUtil;
import gov.sao.asc.util.GridBagLayoutUtil;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/******************************************************************************/

public class HistoryViewer extends JFrame
  implements OKInterface
{
  Vector<Vector<String>> history;


  /****************************************************************************/

  public HistoryViewer(String title, String columnName, Integer observationID)
  {
    Database database = Editor.getInstance().getDatabase();

    history = (Vector<Vector<String>>)database.getHistory(columnName, observationID);
    
    init(title);

    pack();

    setLocation( ComponentUtil.center(this) );

    setVisible(true);
  }

  /****************************************************************************/

  public HistoryViewer(String title, String columnName, String sequenceNumber)
  {
    Database database = Editor.getInstance().getDatabase();

    history = (Vector<Vector<String>>)database.getHistory(columnName, sequenceNumber);

    init(title);

    pack();

    setLocation( ComponentUtil.center(this) );

    setVisible(true);
  }

  /****************************************************************************/

  public Dimension getPreferredSize()
  {
    return( new Dimension(600, 400) );
  }

  /****************************************************************************/

  public void init(String title)
  {
    setTitle("History Viewer: " + title);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    Container contentPane = getContentPane();
    
    contentPane.setLayout( new GridBagLayout() );

    JScrollPane scrollPane = new JScrollPane();

    DefaultTableModel tableModel = new DefaultTableModel();

    JTable table = new JTable(tableModel);

    initModel(tableModel);

    scrollPane.getViewport().add(table);

    GridBagLayoutUtil.addComponent(contentPane, scrollPane, 
                                   0, 0, 1, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER,
                                   1.0, 1.0, 5, 5, 5, 5);

    JPanel buttonPanel = new JPanel();

    buttonPanel.setLayout( new GridBagLayout() );

    JButton okButton = new JButton("OK");

    okButton.addActionListener( new OKButtonListener(this) );

    GridBagLayoutUtil.addComponent(buttonPanel, okButton,
                                   0, 0, 1, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 0, 0, 0, 0);

    GridBagLayoutUtil.addComponent(contentPane, buttonPanel, 
                                   0, 1, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);
  }

  /****************************************************************************/

  public void initModel(DefaultTableModel tableModel)
  {
    tableModel.addColumn("Column");
    tableModel.addColumn("Changed By");
    tableModel.addColumn("Old Value");
    tableModel.addColumn("New Value");
    tableModel.addColumn("Changed On");

    for ( int i = 0; i < history.size(); i++ )
      {
        Vector<String> row = (Vector<String>) history.elementAt( i );

        tableModel.addRow( row );
      }
  }

  /****************************************************************************/

  public void ok()
  {
    dispose();
  }

  /****************************************************************************/

}

/******************************************************************************/
