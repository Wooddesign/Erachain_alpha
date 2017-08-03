package gui.items.notes;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import controller.Controller;
import core.item.assets.AssetCls;
import core.item.notes.NoteCls;
import core.item.persons.PersonCls;
import database.DBSet;
import gui.CoreRowSorter;
import gui.Split_Panel;
import gui.items.unions.TableModelUnions;
import gui.library.MTable;
import gui.models.Renderer_Boolean;
import gui.models.Renderer_Left;
import gui.models.Renderer_Right;
import gui.models.WalletItemImprintsTableModel;
import gui.records.VouchRecordDialog;
import lang.Lang;
import utill.Transaction;

public class Search_Notes_Tab extends Split_Panel {
	private TableModelNotes tableModelNotes;
	final MTable notesTable;
	protected int row;
	
	
	public Search_Notes_Tab(){
		super ("Search_Notes_Tab");
		
		setName(Lang.getInstance().translate("Search Templates"));
		searthLabel_SearchToolBar_LeftPanel.setText(Lang.getInstance().translate("Search") +":  ");
		// not show buttons
			button1_ToolBar_LeftPanel.setVisible(false);
			button2_ToolBar_LeftPanel.setVisible(false);
			jButton1_jToolBar_RightPanel.setVisible(false);
			jButton2_jToolBar_RightPanel.setVisible(false);
			

	//CREATE TABLE
	tableModelNotes = new TableModelNotes();
	 notesTable = new MTable(tableModelNotes);
	
	
		
	 
	//CHECKBOX FOR DIVISIBLE
//	TableColumn divisibleColumn = assetsTable.getColumnModel().getColumn(TableModelItemAssets.COLUMN_DIVISIBLE);
//	divisibleColumn.setCellRenderer(assetsTable.getDefaultRenderer(Boolean.class));
	
	//CHECKBOX FOR FAVORITE
//	TableColumn favoriteColumn = assetsTable.getColumnModel().getColumn(TableModelItemAssets.COLUMN_FAVORITE);
//	favoriteColumn.setCellRenderer(assetsTable.getDefaultRenderer(Boolean.class));

	//ASSETS SORTER
//	Map<Integer, Integer> indexes = new TreeMap<Integer, Integer>();
//	CoreRowSorter sorter = new CoreRowSorter(tableModelItemAssets, indexes);
//	assetsTable.setRowSorter(sorter);
		// column #1
		TableColumn column1 = notesTable.getColumnModel().getColumn(tableModelNotes.COLUMN_KEY);//.COLUMN_CONFIRMED);
		column1.setMinWidth(50);
		column1.setMaxWidth(100);
		column1.setPreferredWidth(50);
		// column #1
				TableColumn column2 = notesTable.getColumnModel().getColumn(tableModelNotes.COLUMN_NAME);//.COLUMN_KEY);//.COLUMN_CONFIRMED);
				column2.setMinWidth(50);
				column2.setMaxWidth(1000);
				column2.setPreferredWidth(50);
				// column #1
				TableColumn column3 = notesTable.getColumnModel().getColumn(tableModelNotes.COLUMN_ADDRESS);//.COLUMN_KEY);//.COLUMN_CONFIRMED);
				column3.setMinWidth(50);
				column3.setMaxWidth(300);
				column3.setPreferredWidth(300);
				TableColumn column4 = notesTable.getColumnModel().getColumn(tableModelNotes.COLUMN_FAVORITE);//.COLUMN_KEY);//.COLUMN_CONFIRMED);
				column4.setMinWidth(50);
				column4.setMaxWidth(50);
				column4.setPreferredWidth(50);
								
				
				
				
				
//Sorter
		RowSorter sorter =   new TableRowSorter(tableModelNotes);
		notesTable.setRowSorter(sorter);	
	// UPDATE FILTER ON TEXT CHANGE
		searchTextField_SearchToolBar_LeftPanel.getDocument().addDocumentListener(new DocumentListener() {
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
				RowFilter filter;
	// GET VALUE
				String search = searchTextField_SearchToolBar_LeftPanel.getText();
				tableModelNotes.fireTableDataChanged();
				
				 if (searth_My_JCheckBox_LeftPanel.isSelected()) {
			            
			            filter = RowFilter.regexFilter("true",tableModelNotes.COLUMN_FAVORITE);
						 	            
			        }else{
			        	filter = RowFilter.regexFilter("false",tableModelNotes.COLUMN_FAVORITE);
			        }
				 ((DefaultRowSorter) sorter).setRowFilter(filter);
				
	// SET FILTER
				
				 filter = RowFilter.regexFilter(".*" + search + ".*", tableModelNotes.COLUMN_NAME);
				 
				((DefaultRowSorter) sorter).setRowFilter(filter);
				tableModelNotes.fireTableDataChanged();
								
			}
		});
				
	// set showvideo			
		jTable_jScrollPanel_LeftPanel.setModel(this.tableModelNotes);
		jTable_jScrollPanel_LeftPanel = notesTable;
		jScrollPanel_LeftPanel.setViewportView(jTable_jScrollPanel_LeftPanel);
		
	
		
// изменение высоты строки при изменении ширины  
		
//		this.setRowHeightFormat(true);
	
		// Event LISTENER		
					jTable_jScrollPanel_LeftPanel.getSelectionModel().addListSelectionListener(new search_listener());
		
		
		
		
		
	
	// MENU
	JPopupMenu search_notes_menu = new JPopupMenu();
	search_notes_menu.addAncestorListener(new AncestorListener(){

		

		@Override
		public void ancestorAdded(AncestorEvent arg0) {
			// TODO Auto-generated method stub
			row = notesTable.getSelectedRow();
			if (row < 1 ) {
				search_notes_menu.disable();
		}
		
		row = notesTable.convertRowIndexToModel(row);
			
			
		}

		@Override
		public void ancestorMoved(AncestorEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ancestorRemoved(AncestorEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	});
	
	JMenuItem favorite = new JMenuItem(Lang.getInstance().translate(""));
	favorite.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			favorite_set( notesTable);
			
		}
	});
	
	
	search_notes_menu.addPopupMenuListener(new PopupMenuListener(){

		@Override
		public void popupMenuCanceled(PopupMenuEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
			// TODO Auto-generated method stub
			
			row = notesTable.getSelectedRow();
			row = notesTable.convertRowIndexToModel(row);
			NoteCls note = tableModelNotes.getNote(row);
			
							favorite.setVisible(true);
				//CHECK IF FAVORITES
				if(Controller.getInstance().isItemFavorite(note))
				{
					favorite.setText(Lang.getInstance().translate("Remove Favorite"));
				}
				else
				{
					favorite.setText(Lang.getInstance().translate("Add Favorite"));
				}
			}
		
	}
	
	);
	search_notes_menu.add(favorite);
	
	JMenuItem vouch_Item= new JMenuItem(Lang.getInstance().translate("Vouch"));
    
	vouch_Item.addActionListener(new ActionListener(){
	
		@Override
		public void actionPerformed(ActionEvent e) {
			
		
				
			
			 NoteCls note = tableModelNotes.getNote(row);
			if (note == null) return;
			
			Transaction trans = DBSet.getInstance().getTransactionFinalMap().get(DBSet.getInstance().getTransactionFinalMapSigns()
								.get(note.getReference()));
			
			
			VouchRecordDialog vouch_panel = new VouchRecordDialog(trans.getBlockHeight(DBSet.getInstance()),trans.getSeqNo(DBSet.getInstance()));
		
		}
	});
	
	
	search_notes_menu.add(favorite);
	search_notes_menu.add(vouch_Item);
	notesTable.setComponentPopupMenu(search_notes_menu);
	notesTable.addMouseListener(new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();
			row = notesTable.rowAtPoint(p);
			notesTable.setRowSelectionInterval(row, row);
			
			if(e.getClickCount() == 2)
			{
				row = notesTable.convertRowIndexToModel(row);
				NoteCls note = tableModelNotes.getNote(row);
		//		new AssetPairSelect(asset.getKey(), "","");
		//		new AssetFrame(asset);
			}
			if(e.getClickCount() == 1 & e.getButton() == e.BUTTON1)
			{
				
				if (notesTable.getSelectedColumn() == tableModelNotes.COLUMN_FAVORITE){
					row = notesTable.convertRowIndexToModel(row);
					NoteCls note = tableModelNotes.getNote(row);
					favorite_set( notesTable);	
					
					
					
				}
				
				
			}
		}
	});

	// hand cursor  for Favorite column
	notesTable.addMouseMotionListener(new MouseMotionListener() {
	    public void mouseMoved(MouseEvent e) {
	       
	        if(notesTable.columnAtPoint(e.getPoint())==tableModelNotes.COLUMN_FAVORITE)
	        {
	     
	        	notesTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        } else {
	        	notesTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	        }
	    }

	    public void mouseDragged(MouseEvent e) {
	    }
	});
	
	
	
	
}

public void removeObservers() {
	this.tableModelNotes.removeObservers();
}

public void favorite_set(JTable assetsTable){


NoteCls note = tableModelNotes.getNote(row);
//new AssetPairSelect(asset.getKey());

if(note.getKey() >= NoteCls.INITIAL_FAVORITES)
{
	//CHECK IF FAVORITES
	if(Controller.getInstance().isItemFavorite(note))
	{
		
		Controller.getInstance().removeItemFavorite(note);
	}
	else
	{
		
		Controller.getInstance().addItemFavorite(note);
	}
		

	assetsTable.repaint();

}
}

// listener select row	 
class search_listener implements ListSelectionListener  {
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if (notesTable.getSelectedRow() < 0 )
				return;

			NoteCls note = tableModelNotes.getNote(notesTable.convertRowIndexToModel(notesTable.getSelectedRow()));
			Info_Notes info_Note = new Info_Notes(note);
			info_Note.setPreferredSize(new Dimension(jScrollPane_jPanel_RightPanel.getSize().width-50,jScrollPane_jPanel_RightPanel.getSize().height-50));
			jScrollPane_jPanel_RightPanel.setViewportView(info_Note);
		}
	}


}
