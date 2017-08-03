package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.JScrollPane;

import gui.Send_Panel;
//import gui.items.persons.IssuePersonFrame;
//import gui.items.persons.MyPersonsPanel;
//import gui.items.persons.PersonFrame;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

public class Send_Frame extends JInternalFrame{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Send_Frame (AssetCls asset, Account account)
	{
	
		this.setFrameIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("images/icons/icon16.png")));
		Send_Panel panel = new Send_Panel(asset, account);
        getContentPane().add(panel, BorderLayout.CENTER);
	         
       //SHOW FRAME
        this.pack();
        this.setMaximizable(true);
		this.setTitle(Lang.getInstance().translate("Send"));
		this.setClosable(true);
		this.setResizable(true);
		//this.setSize(new Dimension( (int)parent.getSize().getWidth()-80,(int)parent.getSize().getHeight()-150));
		this.setLocation(20, 20);
	//	this.setIconImages(icons);
		//CLOSE
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);
//        splitPane_1.setDividerLocation((int)((double)(this.getHeight())*0.7));//.setDividerLocation(.8);
        this.setVisible(true);
	
	}

}