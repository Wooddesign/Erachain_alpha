	package gui.items.mails;

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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.DefaultRowSorter;
	import javax.swing.JButton;
	import javax.swing.JDialog;
	import javax.swing.JFrame;
	import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
	import javax.swing.JPanel;
	import javax.swing.JPopupMenu;
	import javax.swing.JScrollPane;
	import javax.swing.JTable;
	import javax.swing.JTextField;
	import javax.swing.RowFilter;
	import javax.swing.RowSorter;
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
import core.transaction.R_Send;
	import gui.Main_Internal_Frame;
	import gui.Split_Panel;
	import gui.items.assets.IssueAssetPanel;
	import gui.items.assets.TableModelItemAssets;
import gui.items.persons.TableModelPersons;
import gui.library.MTable;
import gui.models.Renderer_Boolean;
	import gui.models.Renderer_Left;
	import gui.models.Renderer_Right;
	import gui.models.WalletItemAssetsTableModel;
	import gui.models.WalletItemPersonsTableModel;
	import lang.Lang;
import utils.TableMenuPopupUtil;


	public class Outcoming_Mails_SplitPanel extends Split_Panel{
		private static final long serialVersionUID = 2717571093561259483L;

		
		private TableModelMails incoming_Mails_Model;
		private MTable inciming_Mail_Table;
		private TableRowSorter my_Sorter;

	// для прозрачности
	     int alpha =255;
	     int alpha_int;
		
		
	public Outcoming_Mails_SplitPanel(){
		super("Outcoming_Mails_SplitPanel");
	
		this.setName(Lang.getInstance().translate("Outcoming Mails"));
			this.searthLabel_SearchToolBar_LeftPanel.setText(Lang.getInstance().translate("Search") +":  ");
			// not show buttons
			this.button1_ToolBar_LeftPanel.setVisible(false);
			this.button2_ToolBar_LeftPanel.setVisible(false);
			this.jButton1_jToolBar_RightPanel.setVisible(false);
			this.jButton2_jToolBar_RightPanel.setVisible(false);
			

			// not show My filter
			this.searth_My_JCheckBox_LeftPanel.setVisible(false);
			
			//TABLE
			incoming_Mails_Model = new TableModelMails(false);
			inciming_Mail_Table = new MTable(incoming_Mails_Model);
			inciming_Mail_Table.setAutoCreateRowSorter(true);
			
	//		TableColumnModel columnModel = inciming_Mail_Table.getColumnModel(); // read column model
	//		columnModel.getColumn(0).setMaxWidth((100));
	//		columnModel.getColumn(1).setMaxWidth((100));
			
				
					
		//	my_Sorter = new TableRowSorter(incoming_Mails_Model);
		//	inciming_Mail_Table.setRowSorter(my_Sorter);
		//	inciming_Mail_Table.getRowSorter();
		//	if (incoming_Mails_Model.getRowCount() > 0) incoming_Mails_Model.fireTableDataChanged();
	/*		
			//CHECKBOX FOR CONFIRMED
			TableColumn confirmedColumn = inciming_Mail_Table.getColumnModel().getColumn(WalletItemPersonsTableModel.COLUMN_CONFIRMED);
			// confirmedColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));
			confirmedColumn.setCellRenderer(new Renderer_Boolean());
			confirmedColumn.setMinWidth(50);
			confirmedColumn.setMaxWidth(50);
			confirmedColumn.setPreferredWidth(50);//.setWidth(30);
			*/		

			//MENU
			JPopupMenu menu = new JPopupMenu();	

		
			
			
			JMenuItem copySender = new JMenuItem(Lang.getInstance().translate("Copy Sender Account"));
			copySender.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
								        
					int row = inciming_Mail_Table.getSelectedRow();
					row = inciming_Mail_Table.convertRowIndexToModel(row);
					
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection value = new StringSelection(((R_Send)incoming_Mails_Model.getTransaction(row)).getCreator().getAddress());
				    clipboard.setContents(value, null);
				}
			});
			menu.add(copySender);
					
			JMenuItem copyRecipient = new JMenuItem(Lang.getInstance().translate("Copy Recipient Account"));
			copyRecipient.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
								       
			        int row = inciming_Mail_Table.getSelectedRow();
					row = inciming_Mail_Table.convertRowIndexToModel(row);
					
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection value = new StringSelection(((R_Send)incoming_Mails_Model.getTransaction(row)).getRecipient().getAddress());
				    clipboard.setContents(value, null);
				}
			});
			
			menu.add(copyRecipient);
			
	
			TableMenuPopupUtil.installContextMenu(inciming_Mail_Table, menu);  // SELECT ROW ON WHICH CLICKED RIGHT BUTTON
			
			
		/*	
			
			//CHECKBOX FOR FAVORITE
			TableColumn favoriteColumn = inciming_Mail_Table.getColumnModel().getColumn(WalletItemPersonsTableModel.COLUMN_FAVORITE);
			//favoriteColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));
			favoriteColumn.setCellRenderer(new Renderer_Boolean());
			favoriteColumn.setMinWidth(50);
			favoriteColumn.setMaxWidth(50);
			favoriteColumn.setPreferredWidth(50);//.setWidth(30);
		*/	
			// UPDATE FILTER ON TEXT CHANGE
			this.searchTextField_SearchToolBar_LeftPanel.getDocument().addDocumentListener(new My_Search());
			// SET VIDEO			
			this.jTable_jScrollPanel_LeftPanel.setModel(incoming_Mails_Model);
			this.jTable_jScrollPanel_LeftPanel = inciming_Mail_Table;
			this.jScrollPanel_LeftPanel.setViewportView(this.jTable_jScrollPanel_LeftPanel);		
	//		this.setRowHeightFormat(true);
			 
			// EVENTS on CURSOR
			inciming_Mail_Table.getSelectionModel().addListSelectionListener(new My_Tab_Listener());
			
		
	//		 Dimension size = MainFrame.getInstance().desktopPane.getSize();
	//		 this.setSize(new Dimension((int)size.getWidth()-100,(int)size.getHeight()-100));
		//	 jSplitPanel.setDividerLocation((int)(size.getWidth()/1.618));
		}
		class My_Tab_Listener implements ListSelectionListener {
			
			//@SuppressWarnings("deprecation")
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				
				
				if (inciming_Mail_Table.getSelectedRow() < 0 )
					return;
					
				R_Send mail = (R_Send)incoming_Mails_Model.getTransaction(inciming_Mail_Table.convertRowIndexToModel(inciming_Mail_Table.getSelectedRow()));
				if (mail == null) return;
				Mail_Info info_panel = new Mail_Info(mail);
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
				incoming_Mails_Model.fireTableDataChanged();
			
				RowFilter filter = RowFilter.regexFilter(".*" + search + ".*", 1);
				((DefaultRowSorter)  my_Sorter).setRowFilter(filter);
					
				incoming_Mails_Model.fireTableDataChanged();

			}
		}
	
		@Override
		public void delay_on_close(){
			// delete observer left panel
			incoming_Mails_Model.removeObservers();
			// get component from right panel
			Component c1 = jScrollPane_jPanel_RightPanel.getViewport().getView();
			// if Person_Info 002 delay on close
			  if (c1 instanceof Mail_Info) ( (Mail_Info)c1).delay_on_Close();
			
		}
		
	
	}




