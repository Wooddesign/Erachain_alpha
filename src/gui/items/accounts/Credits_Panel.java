package gui.items.accounts;

import gui.items.assets.AssetsComboBoxModel;
import gui.library.MTable;
import gui.models.AccountsTableModel;
import gui.models.Renderer_Left;
import gui.models.Renderer_Right;
import lang.Lang;
import utill.Transaction;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableRowSorter;

import utils.BigDecimalStringComparator;
import utils.NumberAsString;
import utils.TableMenuPopupUtil;
import core.account.Account;
import core.account.PublicKeyAccount;
import core.item.assets.AssetCls;
import core.item.persons.PersonCls;
import gui.Gui;
@SuppressWarnings("serial")
public class Credits_Panel extends JPanel // implements ItemListener


//JInternalFrame
{
	//private JFrame parent;

	public JComboBox<AssetCls> cbxFavorites;
	public CreditsTableModel tableModel;
	JTable table;

	@SuppressWarnings("unchecked")
	public Credits_Panel()
	{
		//this.parent = parent;
		this.setLayout(new GridBagLayout());
		
		//PADDING
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
	
		
		//TABLE GBC
		GridBagConstraints tableGBC = new GridBagConstraints();
		tableGBC.fill = GridBagConstraints.BOTH; 
		tableGBC.anchor = GridBagConstraints.NORTHWEST;
		tableGBC.weightx = 1;
		tableGBC.weighty = 1;
		tableGBC.gridx = 1;	
		tableGBC.gridy= 1;	
		
		//BUTTON GBC
		GridBagConstraints buttonGBC = new GridBagConstraints();
		buttonGBC.insets = new Insets(10, 0, 0, 0);
		buttonGBC.fill = GridBagConstraints.NONE;  
		buttonGBC.anchor = GridBagConstraints.NORTHWEST;
		buttonGBC.gridx = 1;	
		buttonGBC.gridy = 2;	
		
		//FAVORITES GBC
		GridBagConstraints favoritesGBC = new GridBagConstraints();
		favoritesGBC.insets = new Insets(10, 0, 10, 0);
		favoritesGBC.fill = GridBagConstraints.BOTH;  
		favoritesGBC.anchor = GridBagConstraints.NORTHWEST;
		favoritesGBC.weightx = 1;
		favoritesGBC.gridx = 1;	
		favoritesGBC.gridy = 0;	
		
		//ASSET FAVORITES
		cbxFavorites = new JComboBox<AssetCls>(new AssetsComboBoxModel());
		this.add(cbxFavorites, favoritesGBC);
		
		//TABLE
		tableModel = new CreditsTableModel();
		// start data in model
		tableModel.setAsset( (AssetCls) cbxFavorites.getSelectedItem());
		// table = Gui.createSortableTable(tableModel, 0);
		 table = new MTable(tableModel);
			
	//	TableRowSorter<AccountsTableModel> sorter =  (TableRowSorter<AccountsTableModel>) table.getRowSorter();
	//	sorter.setComparator(AccountsTableModel.COLUMN_CONFIRMED_BALANCE, new BigDecimalStringComparator());
	//	sorter.setComparator(AccountsTableModel.COLUMN_WAINTING_BALANCE, new BigDecimalStringComparator());
	//	sorter.setComparator(AccountsTableModel.COLUMN_FEE_BALANCE, new BigDecimalStringComparator());
		
		
		//ON FAVORITES CHANGE
		cbxFavorites.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
			
				
				if(e.getStateChange() == ItemEvent.SELECTED) 
				{		
					AssetCls asset = (AssetCls) cbxFavorites.getSelectedItem();
						tableModel.setAsset(asset);  
				} 	
				
				
			}
		});
		
				
		
		
		//MENU
		JPopupMenu menu = new JPopupMenu();	
		
		JMenuItem sendAsset = new JMenuItem(Lang.getInstance().translate("Send"));
		sendAsset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				AssetCls asset = (AssetCls) cbxFavorites.getSelectedItem();
				Account account = tableModel.getAccount(row);
        		//Menu.selectOrAdd( new SendMessageFrame(asset, account), MainFrame.desktopPane.getAllFrames());
				//Menu.selectOrAdd( new Account_Send_Dialog(asset, account), null);
				
				 new Account_Send_Dialog(asset, account, null, null); 
						
			}
		});
		menu.add(sendAsset);

		JMenuItem holdAsset = new JMenuItem(Lang.getInstance().translate("Hold"));
		holdAsset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				AssetCls asset = (AssetCls) cbxFavorites.getSelectedItem();
				Account account = tableModel.getAccount(row);
        		//Menu.selectOrAdd( new SendMessageFrame(asset, account), MainFrame.desktopPane.getAllFrames());
				//Menu.selectOrAdd( new Account_Send_Dialog(asset, account), null);
				
				 new Account_Take_Hold_Dialog(asset, account); 
						
			}
		});
		menu.add(holdAsset);

		menu.addSeparator();  

		JMenuItem lend_Debt_Asset = new JMenuItem(Lang.getInstance().translate("Lend"));
		lend_Debt_Asset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				AssetCls asset = (AssetCls) cbxFavorites.getSelectedItem();
				Account account = tableModel.getAccount(row);
        		//Menu.selectOrAdd( new SendMessageFrame(asset, account), MainFrame.desktopPane.getAllFrames());
				//Menu.selectOrAdd( new Account_Send_Dialog(asset, account), null);
				
				 new Account_Lend_Dialog(asset, account); 
						
			}
		});
		menu.add(lend_Debt_Asset);

		
		JMenuItem repay_Debt_Asset = new JMenuItem(Lang.getInstance().translate("Repay Debt"));
		repay_Debt_Asset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				AssetCls asset = (AssetCls) cbxFavorites.getSelectedItem();
				Account account = tableModel.getAccount(row);
        		//Menu.selectOrAdd( new SendMessageFrame(asset, account), MainFrame.desktopPane.getAllFrames());
				//Menu.selectOrAdd( new Account_Send_Dialog(asset, account), null);
				
				 new Account_Repay_Debt_Dialog(asset, account); 
						
			}
		});
		menu.add(repay_Debt_Asset);
				
		JMenuItem confiscate_Debt_Asset = new JMenuItem(Lang.getInstance().translate("Confiscate Debt"));
		confiscate_Debt_Asset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				AssetCls asset = (AssetCls) cbxFavorites.getSelectedItem();
				Account account = tableModel.getAccount(row);
        		//Menu.selectOrAdd( new SendMessageFrame(asset, account), MainFrame.desktopPane.getAllFrames());
				//Menu.selectOrAdd( new Account_Send_Dialog(asset, account), null);
				
				 new Account_Confiscate_Debt_Dialog(asset, account); 
						
			}
		});
		menu.add(confiscate_Debt_Asset);
		
		
		
		
		
		
		
		menu.addSeparator();

		JMenuItem copyAddress = new JMenuItem(Lang.getInstance().translate("Copy Account"));
		copyAddress.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				Account account = tableModel.getAccount(row);
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection value = new StringSelection(account.getAddress());
			    clipboard.setContents(value, null);
			}
		});
		menu.add(copyAddress);
				
		JMenuItem copyBalance = new JMenuItem(Lang.getInstance().translate("Copy Balance"));
		copyBalance.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				Account account = tableModel.getAccount(row);
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				long key = cbxFavorites.getItemAt(cbxFavorites.getSelectedIndex()).getKey();
				StringSelection value = new StringSelection(account.getBalanceUSE(key).toPlainString());
			    clipboard.setContents(value, null);
			}
		});
		
		menu.add(copyBalance);
		
		JMenuItem copyConfirmedBalance = new JMenuItem(Lang.getInstance().translate("Copy Confirmed Balance"));
		copyConfirmedBalance.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				Account account = tableModel.getAccount(row);
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection value = new StringSelection(account.getBalanceUSE(Transaction.FEE_KEY).toPlainString());
			    clipboard.setContents(value, null);
			}
		});
		menu.add(copyConfirmedBalance);
		
		/*
		JMenuItem copyGeneratingBalance = new JMenuItem(Lang.getInstance().translate("Copy Generating Balance"));
		copyGeneratingBalance.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				Account account = tableModel.getAccount(row);
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection value = new StringSelection("" + account.getGeneratingBalance());
			    clipboard.setContents(value, null);
			}
		});
		menu.add(copyGeneratingBalance);
		*/

		menu.addSeparator();
		JMenuItem copyPublicKey = new JMenuItem(Lang.getInstance().translate("Copy Public Key"));
		copyPublicKey.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);
				
				PublicKeyAccount publicKeyAccount = tableModel.getPublicKeyAccount(row);
				//PublicKeyAccount publicKeyAccount = Controller.getInstance().getPublicKeyAccountByAddress(
				//		account.getAddress());
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection value = new StringSelection(publicKeyAccount.getBase58());
			    clipboard.setContents(value, null);
			}
		});
		menu.add(copyPublicKey);

		////////////////////
		TableMenuPopupUtil.installContextMenu(table, menu);  // SELECT ROW ON WHICH CLICKED RIGHT BUTTON
		
		table.addMouseListener(new MouseAdapter() 
		{
		     @Override
		     public void mousePressed(MouseEvent e) 
		     {
		        Point p = e.getPoint();
		        int row = table.rowAtPoint(p);
		        table.setRowSelectionInterval(row, row);
		     }
		});
		
		
		
				
		//ADD TOTAL BALANCE
		final JLabel totalBalance = new JLabel(Lang.getInstance().translate("Confirmed Balance") + ": " + tableModel.getTotalBalance().toPlainString());
		this.add(totalBalance, buttonGBC);
		
		//ON TABLE CHANGE
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
		//		totalBalance.setText(Lang.getInstance().translate("Confirmed Balance") + ": " + NumberAsString.getInstance().numberAsString(tableModel.getTotalBalance()));				
			}		
		});
		
		//ADD ACCOUNTS TABLE
		this.add(new JScrollPane(table), tableGBC);

		/*
		//ADD NEW ACCOUNT BUTTON
		buttonGBC.gridy++;
		JButton newButton = new JButton(Lang.getInstance().translate("New account"));
		newButton.setPreferredSize(new Dimension(150, 25));
		newButton.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		        onNewClick();
		    }
		});	
		this.add(newButton, buttonGBC);
		*/
		
	}
	
	
/*	
	@Override
	public void itemStateChanged(ItemEvent e) 
	{
		
		if(e.getStateChange() == ItemEvent.SELECTED) 
		{		
			AssetCls asset = (AssetCls) cbxFavorites.getSelectedItem();
        	tableModel.setAsset(asset);  
		} 
	}
*/	
	// set select in Favorites to FEE asset
	


}
