package gui.items.mails;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import core.item.persons.PersonCls;
import lang.Lang;
import utils.NumberAsString;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;

import gui.Send_Panel;
//import gui.items.persons.IssuePersonFrame;
//import gui.items.persons.MyPersonsPanel;
//import gui.items.persons.PersonFrame;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

public class Mail_Send_Dialog extends JDialog{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Mail_Send_Dialog (AssetCls asset, Account account, Account account_To, PersonCls person)
	{
	
		Mail_Send_Panel panel = new Mail_Send_Panel(asset, account, account_To, person);
        getContentPane().add(panel, BorderLayout.CENTER);
	         
       //SHOW FRAME
 //       this.pack();
   //     this.setMaximizable(true);
		this.setTitle(Lang.getInstance().translate("Send"));
	//	this.setClosable(true);
		this.setResizable(true);
		//this.setSize(new Dimension( (int)parent.getSize().getWidth()-80,(int)parent.getSize().getHeight()-150));
	//	this.setLocation(20, 20);
	//	this.setIconImages(icons);
		//CLOSE
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		
	//	 setMinimumSize(new java.awt.Dimension(650, 23));
		setModal(true);
   //     setPreferredSize(new java.awt.Dimension(650,600));
	    
        
        
        
        
	    
		//PACK
        this.pack();
        this.setResizable(true);
        this.setLocationRelativeTo(null);
		
  //      this.setResizable(true);
//        splitPane_1.setDividerLocation((int)((double)(this.getHeight())*0.7));//.setDividerLocation(.8);
        this.setVisible(true);
	
	}

}