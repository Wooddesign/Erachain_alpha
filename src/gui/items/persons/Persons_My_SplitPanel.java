	package gui.items.persons;

	import java.awt.Color;
	import java.awt.Component;
	import java.awt.Cursor;
	import java.awt.Dimension;
	import java.awt.GridLayout;
	import java.awt.Point;
	import java.awt.Rectangle;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.awt.event.FocusEvent;
	import java.awt.event.FocusListener;
	import java.awt.event.MouseAdapter;
	import java.awt.event.MouseEvent;
	import java.awt.event.MouseListener;
	import java.awt.event.MouseMotionListener;
	import java.awt.event.WindowEvent;
	import java.awt.event.WindowFocusListener;
	import java.awt.image.ColorModel;
	import javax.swing.Timer;
	import java.awt.*;

import javax.swing.AbstractButton;
import javax.swing.DefaultRowSorter;
	import javax.swing.JButton;
	import javax.swing.JDialog;
	import javax.swing.JFrame;
	import javax.swing.JInternalFrame;
	import javax.swing.JMenuItem;
	import javax.swing.JPanel;
	import javax.swing.JPopupMenu;
	import javax.swing.JScrollPane;
	import javax.swing.JTable;
	import javax.swing.JTextField;
	import javax.swing.RowFilter;
	import javax.swing.RowSorter;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
	import javax.swing.event.DocumentListener;
	import javax.swing.event.ListSelectionEvent;
	import javax.swing.event.ListSelectionListener;
	import javax.swing.event.PopupMenuEvent;
	import javax.swing.event.PopupMenuListener;
	import javax.swing.table.TableColumn;
	import javax.swing.table.TableColumnModel;
	import javax.swing.table.TableRowSorter;

	import controller.Controller;
	import core.item.assets.AssetCls;
	import core.item.persons.PersonCls;
	import gui.Main_Internal_Frame;
	import gui.Split_Panel;
	import gui.items.assets.IssueAssetPanel;
	import gui.items.assets.TableModelItemAssets;
import gui.library.MTable;
import gui.models.Renderer_Boolean;
	import gui.models.Renderer_Left;
	import gui.models.Renderer_Right;
	import gui.models.WalletItemAssetsTableModel;
	import gui.models.WalletItemPersonsTableModel;
	import lang.Lang;


	public class Persons_My_SplitPanel extends Split_Panel{
		private static final long serialVersionUID = 2717571093561259483L;

		
		private WalletItemPersonsTableModel my_PersonsModel;
		private MTable my_Person_table;
		private TableRowSorter my_Sorter;
		
	// для прозрачности
	     int alpha =255;
	     int alpha_int;
		
		
	public Persons_My_SplitPanel(){
		super("Persons_My_SplitPanel");
	
		this.setName(Lang.getInstance().translate("My Persons"));
			this.searthLabel_SearchToolBar_LeftPanel.setText(Lang.getInstance().translate("Search") +":  ");
			// not show buttons
			this.button1_ToolBar_LeftPanel.setVisible(false);
			this.button2_ToolBar_LeftPanel.setVisible(false);
			this.jButton1_jToolBar_RightPanel.setVisible(false);
			this.jButton2_jToolBar_RightPanel.setVisible(false);
			

			// not show My filter
			this.searth_My_JCheckBox_LeftPanel.setVisible(false);
			
			//TABLE
			my_PersonsModel = new WalletItemPersonsTableModel();
			my_Person_table = new MTable(my_PersonsModel);
			
			TableColumnModel columnModel = my_Person_table.getColumnModel(); // read column model
			columnModel.getColumn(0).setMaxWidth((100));
			
			this.addAncestorListener(new AncestorListener(){

				@Override
				public void ancestorAdded(AncestorEvent arg0) {
					// TODO Auto-generated method stub
					my_PersonsModel.addObservers();
				}

				@Override
				public void ancestorMoved(AncestorEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void ancestorRemoved(AncestorEvent arg0) {
					// TODO Auto-generated method stub
					my_PersonsModel.removeObservers();
				}
				
				
				
			});	
					
	//		my_Sorter = new TableRowSorter(my_PersonsModel);
	//		my_Person_table.setRowSorter(my_Sorter);
	//		my_Person_table.getRowSorter();
	//		if (my_PersonsModel.getRowCount() > 0) my_PersonsModel.fireTableDataChanged();
			
			//CHECKBOX FOR CONFIRMED
			TableColumn confirmedColumn = my_Person_table.getColumnModel().getColumn(WalletItemPersonsTableModel.COLUMN_CONFIRMED);
			// confirmedColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));
			
			confirmedColumn.setMinWidth(50);
			confirmedColumn.setMaxWidth(50);
			confirmedColumn.setPreferredWidth(50);//.setWidth(30);
			
			
			//CHECKBOX FOR FAVORITE
			TableColumn favoriteColumn = my_Person_table.getColumnModel().getColumn(WalletItemPersonsTableModel.COLUMN_FAVORITE);
			//favoriteColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));
			
			favoriteColumn.setMinWidth(50);
			favoriteColumn.setMaxWidth(50);
			favoriteColumn.setPreferredWidth(50);//.setWidth(30);
			
			my_Person_table.addMouseMotionListener(new MouseMotionListener() {
			    public void mouseMoved(MouseEvent e) {
			       
			        if(my_Person_table.columnAtPoint(e.getPoint())==WalletItemPersonsTableModel.COLUMN_FAVORITE)
			        {
			     
			        	my_Person_table.setCursor(new Cursor(Cursor.HAND_CURSOR));
			        } else {
			        	my_Person_table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			        }
			    }

			    public void mouseDragged(MouseEvent e) {
			    }
			});
			
			
			
			
			// UPDATE FILTER ON TEXT CHANGE
			this.searchTextField_SearchToolBar_LeftPanel.getDocument().addDocumentListener(new My_Search());
			// SET VIDEO			
			this.jTable_jScrollPanel_LeftPanel.setModel(my_PersonsModel);
			this.jTable_jScrollPanel_LeftPanel = my_Person_table;
			this.jScrollPanel_LeftPanel.setViewportView(this.jTable_jScrollPanel_LeftPanel);		
	//		this.setRowHeightFormat(true);
			 
			// EVENTS on CURSOR
			my_Person_table.getSelectionModel().addListSelectionListener(new My_Tab_Listener());
			
			jTable_jScrollPanel_LeftPanel.addMouseListener(new MouseAdapter() 
			{
				@Override
				public void mousePressed(MouseEvent e) 
				{
					Point p = e.getPoint();
					int row = jTable_jScrollPanel_LeftPanel.rowAtPoint(p);
					jTable_jScrollPanel_LeftPanel.setRowSelectionInterval(row, row);
					
					
					if(e.getClickCount() == 1 & e.getButton() == e.BUTTON1)
					{
						
						if (jTable_jScrollPanel_LeftPanel.getSelectedColumn() == TableModelPersons.COLUMN_FAVORITE){
							row = jTable_jScrollPanel_LeftPanel.convertRowIndexToModel(row);
							 PersonCls asset = my_PersonsModel.getItem(row);
							favorite_set( jTable_jScrollPanel_LeftPanel);	
							
							
							
						}
						
						
					}
			     }
			});
		

	//		 Dimension size = MainFrame.getInstance().desktopPane.getSize();
	//		 this.setSize(new Dimension((int)size.getWidth()-100,(int)size.getHeight()-100));
			// jSplitPanel.setDividerLocation((int)(size.getWidth()/1.618));
			
		  
		    
		}
	
	// set favorine My
		void favorite_my(JTable table){
			int row = table.getSelectedRow();
			row = table.convertRowIndexToModel(row);

			PersonCls person = my_PersonsModel.getItem(row);
			//new AssetPairSelect(asset.getKey());

			
				//CHECK IF FAVORITES
				if(Controller.getInstance().isItemFavorite(person))
				{
					
					Controller.getInstance().removeItemFavorite(person);
				}
				else
				{
					
					Controller.getInstance().addItemFavorite(person);
				}
					

				table.repaint();

		}

		
	
	
	

	


		class My_Tab_Listener implements ListSelectionListener {
			
			//@SuppressWarnings("deprecation")
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				
				PersonCls person = null;
				if (my_Person_table.getSelectedRow() >= 0 )	person = my_PersonsModel.getItem(my_Person_table.convertRowIndexToModel(my_Person_table.getSelectedRow()));
				if (person == null) return;
				Person_Info_002 info_panel = new Person_Info_002(person, false);
				info_panel.setPreferredSize(new Dimension(jScrollPane_jPanel_RightPanel.getSize().width-50,jScrollPane_jPanel_RightPanel.getSize().height-50));
				jScrollPane_jPanel_RightPanel.setViewportView(info_panel);
			}
			
		}
		
		class My_Search implements DocumentListener {
			public void changedUpdate(DocumentEvent e) {
				onChange();
			}
		
			public void removeUpdate(DocumentEvent e) {
				onChange();
			}
		
			public void insertUpdate(DocumentEvent e) {
				onChange();
			}
		
			public void onChange() {
				// GET VALUE
				String search = searchTextField_SearchToolBar_LeftPanel.getText();
				// SET FILTER
				my_PersonsModel.fireTableDataChanged();
			
				RowFilter filter = RowFilter.regexFilter(".*" + search + ".*", 1);
				((DefaultRowSorter)  my_Sorter).setRowFilter(filter);
					
				my_PersonsModel.fireTableDataChanged();

			}
		}
		
	@Override
	public void delay_on_close(){
		// delete observer left panel
		my_PersonsModel.removeObservers();
		// get component from right panel
		Component c1 = jScrollPane_jPanel_RightPanel.getViewport().getView();
		// if Person_Info 002 delay on close
		  if (c1 instanceof Person_Info_002) ( (Person_Info_002)c1).delay_on_Close();
		
	}
	
	 public void favorite_set(JTable personsTable){


		 int row = personsTable.getSelectedRow();
		 row = personsTable.convertRowIndexToModel(row);

		  PersonCls person = my_PersonsModel.getItem(row);
		 //new AssetPairSelect(asset.getKey());

		
		 	//CHECK IF FAVORITES
		 	if(Controller.getInstance().isItemFavorite(person))
		 	{
		 		
		 		Controller.getInstance().removeItemFavorite(person);
		 	}
		 	else
		 	{
		 		
		 		Controller.getInstance().addItemFavorite(person);
		 	}
		 		

		 	personsTable.repaint();

		 
		 }
	
	
	}




