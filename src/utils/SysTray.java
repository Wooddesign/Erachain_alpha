package utils;
// 30/03
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import controller.Controller;
import database.wallet.TransactionMap;
import gui.ClosingDialog;
import gui.ConsolePanel;
import gui.Gui;
import gui.MainFrame;
import gui.PasswordPane;
import gui.CoreRowSorter;
import gui.Send_Panel;
import gui.items.accounts.Account_Send_Dialog;
import gui.items.accounts.Account_Send_Panel;
import gui.items.accounts.My_Accounts_SplitPanel;
import gui.items.assets.AssetsPanel;
import gui.items.assets.Search_Assets_Tab;
import gui.models.WalletTransactionsTableModel;
import gui.naming.NamingServicePanel;
import gui.settings.SettingsFrame;
import gui.transaction.TransactionDetailsFactory;
import gui.voting.VotingPanel;
import lang.Lang;
import settings.Settings;
import utill.Transaction;

public class SysTray implements Observer{

	
	private static final Logger LOGGER = Logger.getLogger(SysTray.class);
	private static SysTray systray = null;
	private TrayIcon icon = null;
	private PopupMenu createPopupMenu;

	public static SysTray getInstance() {
		if (systray == null) {
			systray = new SysTray();
		}

		return systray;
	}
	
	public SysTray()
	{
		Controller.getInstance().addObserver(this);	
	}
	
	public void createTrayIcon() throws HeadlessException,
			MalformedURLException, AWTException, FileNotFoundException {
		if (icon == null) {
			if (!SystemTray.isSupported()) {
				LOGGER.info("SystemTray is not supported");
			} else {
				
				//String toolTipText = "Erachain.org "	+ Controller.getInstance().getVersion();
				createPopupMenu = createPopupMenu();
				TrayIcon icon = new TrayIcon(createImage(
						"images/icons/icon32.png", "tray icon"), "erachain.org"
						+ Controller.getInstance().getVersion(),
						createPopupMenu);
				
				icon.setImageAutoSize(true);
				
				SystemTray.getSystemTray().add(icon);
				this.icon = icon;
			
				icon.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							Gui.getInstance().bringtoFront();
						} catch (Exception e1) {
							LOGGER.error(e1.getMessage(),e1);
						}
					}
				});
				
				this.update(new Observable(), new ObserverMessage(ObserverMessage.NETWORK_STATUS, Controller.getInstance().getStatus()));
			}
		}
	}
	
	public void sendMessage(String caption, String text, TrayIcon.MessageType messagetype  )
	{
		if(icon != null)
		{
			icon.displayMessage(caption, text,
					messagetype);
		}
	}

	// Obtain the image URL
	private  Image createImage(String path, String description)
			throws MalformedURLException, FileNotFoundException {

		File file = new File(path);

		if (!file.exists()) {
			throw new FileNotFoundException("Iconfile not found: " + path);
		}

		URL imageURL = file.toURI().toURL();
		return (new ImageIcon(imageURL, description)).getImage();
	}

	private PopupMenu createPopupMenu() throws HeadlessException {
		PopupMenu menu = new PopupMenu();
		
		
		MenuItem decentralizedWeb = new MenuItem("erachain.org Web/Social Network");
		decentralizedWeb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
        			URLViewer.openWebpage(new URL("http://127.0.0.1:"+Settings.getInstance().getWebPort()));
				} catch (MalformedURLException e1) {
					LOGGER.error(e1.getMessage(),e1);
				}
			}
		});
		menu.add(decentralizedWeb);
		MenuItem walletStatus = new MenuItem("Wallet Status (web)");
		walletStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					URLViewer.openWebpage(new URL("http://127.0.0.1:"+Settings.getInstance().getWebPort() + "/index/status.html"));
				} catch (MalformedURLException e1) {
					LOGGER.error(e1.getMessage(),e1);
				}
			}
		});
		menu.add(walletStatus);
		
		
		
		MenuItem lock = new MenuItem(Lang.getInstance().translate("Lock/Unlock Wallet"));
		lock.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(Controller.getInstance().isWalletUnlocked())
				{
					Controller.getInstance().lockWallet();
				}else
				{
					String password = PasswordPane.showUnlockWalletDialog(MainFrame.getInstance()); 
					if(!password.equals("") && !Controller.getInstance().unlockWallet(password))
					{
						JOptionPane.showMessageDialog(null, Lang.getInstance().translate("Invalid password"),Lang.getInstance().translate("Unlock Wallet"), JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		menu.add(lock);
		
		
		MenuItem settings = new MenuItem(Lang.getInstance().translate("Settings"));
		settings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new SettingsFrame();
			}
		});
		menu.add(settings);
		
		
		MenuItem console = new MenuItem(Lang.getInstance().translate("Console"));
		console.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					JFrame frame = new JFrame(Lang.getInstance().translate("Console"));

				    frame.setSize(800, 600);
				    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				    frame.setVisible(true);
					ConsolePanel ap = new ConsolePanel();
					frame.getContentPane().add(ap);
					
			}
		});
		menu.add(console);
		
		MenuItem transactions = new MenuItem(Lang.getInstance().translate("Transactions"));
		transactions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					JFrame frame = new JFrame(Lang.getInstance().translate("Transactions"));

				    frame.setSize(800, 600);
				    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				    frame.setVisible(true);
				 final   WalletTransactionsTableModel transactionsModel = new WalletTransactionsTableModel();
				final    JTable transactionsTable = new JTable(transactionsModel);
					
					//TRANSACTIONS SORTER
					Map<Integer, Integer> indexes = new TreeMap<Integer, Integer>();
					indexes.put(WalletTransactionsTableModel.COLUMN_CONFIRMATIONS, TransactionMap.TIMESTAMP_INDEX);
					indexes.put(WalletTransactionsTableModel.COLUMN_TIMESTAMP, TransactionMap.TIMESTAMP_INDEX);
					indexes.put(WalletTransactionsTableModel.COLUMN_CREATOR, TransactionMap.ADDRESS_INDEX);
					indexes.put(WalletTransactionsTableModel.COLUMN_AMOUNT, TransactionMap.AMOUNT_INDEX);
					CoreRowSorter sorter = new CoreRowSorter(transactionsModel, indexes);
					transactionsTable.setRowSorter(sorter);
					
					//TRANSACTION DETAILS
					transactionsTable.addMouseListener(new MouseAdapter() 
					{
						public void mouseClicked(MouseEvent e) 
						{
							if(e.getClickCount() == 2) 
							{
								//GET ROW
						        int row = transactionsTable.getSelectedRow();
						        row = transactionsTable.convertRowIndexToModel(row);
						        
						        //GET TRANSACTION
						        Transaction transaction = transactionsModel.getTransaction(row);
						         
						        //SHOW DETAIL SCREEN OF TRANSACTION
						        TransactionDetailsFactory.getInstance().createTransactionDetail(transaction);
						    }
						}
					});			
					
					frame.getContentPane().add(new JScrollPane(transactionsTable)  );
			}
		});
		menu.add(transactions);
				
		MenuItem messages = new MenuItem(Lang.getInstance().translate("Send"));
		messages.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					
				JFrame frame = new JFrame(Lang.getInstance().translate("Send"));

				//    frame.setSize(800, 600);
				    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				    
					 Account_Send_Panel ap = new Account_Send_Panel(null, null, null, null);
					frame.getContentPane().add(ap);
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icons/icon32.png"));
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
			}
		});
		menu.add(messages);

		MenuItem assets = new MenuItem(Lang.getInstance().translate("Assets"));
		assets.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					JFrame frame = new JFrame(Lang.getInstance().translate("Assets"));
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icons/icon32.png"));
				    frame.setSize(800, 600);
				    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				   Search_Assets_Tab ap = new Search_Assets_Tab(true);
					frame.getContentPane().add(ap);
					frame.pack();
					frame.setLocationRelativeTo(null);
					 frame.setVisible(true);
					
			}
		});
		menu.add(assets);
		
		MenuItem my_Accounts = new MenuItem(Lang.getInstance().translate("My Accounts"));
		my_Accounts.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					JFrame frame = new JFrame(Lang.getInstance().translate("My Accounts"));

				    frame.setSize(800, 600);
				    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				   
					frame.getContentPane().add(
							new My_Accounts_SplitPanel());
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icons/icon32.png"));
					frame.pack();
					frame.setLocationRelativeTo(null);
					 frame.setVisible(true);
					
			}
		});
		menu.add(my_Accounts);
		
				
		MenuItem exit = new MenuItem(Lang.getInstance().translate("Quit"));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ClosingDialog();
			}
		});
		menu.add(exit);
		
		return menu;
	}
	
	public void setToolTipText(String text)
	{
		this.icon.setToolTip(text);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		if(this.icon == null) {
			return;
		}
		
		ObserverMessage message = (ObserverMessage) arg1;
			
		int currentHeight;
		String networkStatus = "";
		String syncProcent = "";
		String toolTipText = "erachain.org " + Controller.getInstance().getVersion() + "\n";
		
		
		if(Controller.getInstance().getStatus() == Controller.STATUS_NO_CONNECTIONS) {
			networkStatus = Lang.getInstance().translate("No connections");
			syncProcent = "";
		} else if(Controller.getInstance().getStatus() == Controller.STATUS_SYNCHRONIZING) {
			networkStatus = Lang.getInstance().translate("Synchronizing");
		} else if(Controller.getInstance().getStatus() == Controller.STATUS_OK)	{
			networkStatus = Lang.getInstance().translate("OK");
			syncProcent = "";
		}	

		if(message.getType() == ObserverMessage.WALLET_SYNC_STATUS)	{
			currentHeight = (int)message.getValue();
			if(currentHeight == -1)
			{
				this.update(null, new ObserverMessage(
						ObserverMessage.NETWORK_STATUS, Controller.getInstance().getStatus()));
				currentHeight = Controller.getInstance().getMyHWeight(false).a;
				return;
			}
			networkStatus = Lang.getInstance().translate("Wallet Synchronizing");
			
			syncProcent = 100 * currentHeight/Controller.getInstance().getMyHWeight(false).a + "%";
			
			toolTipText += networkStatus + " " + syncProcent;
			toolTipText += "\n" + Lang.getInstance().translate("Height") +": " + currentHeight + "/" + Controller.getInstance().getMyHWeight(false).a + "/" + Controller.getInstance().getMaxPeerHWeight(false).a;
			setToolTipText(toolTipText);
			
		} else if(message.getType() == ObserverMessage.BLOCKCHAIN_SYNC_STATUS) {
			currentHeight = (int)message.getValue(); 

			if(Controller.getInstance().getStatus() == Controller.STATUS_SYNCHRONIZING)
			{
				syncProcent = 100 * currentHeight/Controller.getInstance().getMaxPeerHWeight(false).a + "%";	
			}
			
			toolTipText += networkStatus + " " + syncProcent;
			toolTipText += "\n" + Lang.getInstance().translate("Height") +": " + currentHeight + "/" + Controller.getInstance().getMaxPeerHWeight(false);
			setToolTipText(toolTipText);
			
		} else {
			if(Controller.getInstance().getStatus() == Controller.STATUS_OK || Controller.getInstance().getStatus() == Controller.STATUS_NO_CONNECTIONS) {
				toolTipText += networkStatus + " " + syncProcent;
				toolTipText += "\n" + Lang.getInstance().translate("Height") +": " + Controller.getInstance().getMyHWeight(false).a;
				setToolTipText(toolTipText);
			}
		}
	}
	
	
}