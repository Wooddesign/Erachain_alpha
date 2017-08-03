package gui.items.accounts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import controller.Controller;
import core.account.Account;
import core.item.assets.AssetCls;
import lang.Lang;
import utils.NumberAsString;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.JScrollPane;

import gui.Send_Panel;
//import gui.items.persons.IssuePersonFrame;
//import gui.items.persons.MyPersonsPanel;
//import gui.items.persons.PersonFrame;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

public class Account_Repay_Debt_Dialog extends JDialog{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Account_Repay_Debt_Dialog (AssetCls asset, Account account)
	{
	
		
		//ICON
				List<Image> icons = new ArrayList<Image>();
				icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon16.png"));
				icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon32.png"));
				icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon64.png"));
				icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon128.png"));
				this.setIconImages(icons);
		Account_Repay_Debt_Panel panel = new Account_Repay_Debt_Panel(asset, account);
        getContentPane().add(panel, BorderLayout.CENTER);
	         
       //SHOW FRAME
        this.pack();
   //     this.setMaximizable(true);
		this.setTitle(Lang.getInstance().translate("Repay Debt"));
	//	this.setClosable(true);
		this.setResizable(false);
		//this.setSize(new Dimension( (int)parent.getSize().getWidth()-80,(int)parent.getSize().getHeight()-150));
	//	this.setLocation(20, 20);
	//	this.setIconImages(icons);
		//CLOSE
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		
	//	 setMinimumSize(new java.awt.Dimension(650, 23));
		setModal(true);
   //     setPreferredSize(new java.awt.Dimension(650, 650));
	    
        
        
        
        
	    
		//PACK
		
    //    this.setResizable(false);
        this.setLocationRelativeTo(null);

        this.setVisible(true);
	
	}

}