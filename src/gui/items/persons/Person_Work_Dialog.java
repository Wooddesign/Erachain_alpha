package gui.items.persons;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.mapdb.Fun.Tuple3;

import controller.Controller;
import core.item.persons.PersonCls;
import database.DBSet;
import gui.items.accounts.Account_Send_Dialog;
import gui.items.mails.Mail_Send_Dialog;
import gui.library.MButton;
import gui.models.PersonAccountsModel;
import gui.records.VouchRecordDialog;
import lang.Lang;
import utill.Transaction;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Саша
 */
public class Person_Work_Dialog extends JDialog {

    /**
     * Creates new form Person_Work_Dialog
     */
    public Person_Work_Dialog(PersonCls person) {
        super();
        this.setModal(true);
        
        getContentPane().setLayout(new java.awt.GridLayout(0, 1));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      //  setAlwaysOnTop(true);
        List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon16.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon32.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon64.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon128.png"));
		this.setIconImages(icons);
		
  	   
		jButton1 = new MButton(Lang.getInstance().translate("Set Status to Person"), 3);
      	getContentPane().add(jButton1);
    	jButton1.addActionListener(new ActionListener(){
  		@Override
    	public void actionPerformed(ActionEvent e) {
   
  		  	@SuppressWarnings("unused")
			PersonSetStatusDialog fm = new PersonSetStatusDialog( person);	
  		  	dispose();
    	}});
    	   	
    	jButton2= new MButton(Lang.getInstance().translate("Attest Public Key for Person"), 3);
    	getContentPane().add(jButton2);
    	jButton2.addActionListener(new ActionListener(){
  		@Override
    	public void actionPerformed(ActionEvent e) {
   
  
    		@SuppressWarnings("unused")
			PersonConfirmDialog fm = new PersonConfirmDialog(person, person.getOwner());	
    		dispose();
    		}});
    	
 
    	jButton3 = new MButton(Lang.getInstance().translate("Vouch the Person Info"), 3);
    	getContentPane().add(jButton3);
    	jButton3.addActionListener(new ActionListener(){
  		@Override
    	public void actionPerformed(ActionEvent e) {
    		
			PersonCls per = person;
			byte[] ref = per.getReference();
			Transaction transaction = Transaction.findByDBRef(DBSet.getInstance(), ref);
			int blockNo = transaction.getBlockHeight(DBSet.getInstance());
			int recNo = transaction.getSeqNo(DBSet.getInstance());
    		new VouchRecordDialog(blockNo, recNo);	
    		dispose();
  		}});
    	
	
		PersonAccountsModel person_Accounts_Model = new PersonAccountsModel(person.getKey());
    	if (person_Accounts_Model.getRowCount() > 0) {
  	    	jButton4 =new MButton(Lang.getInstance().translate("Send Asset to Person"), 3);
  	    	getContentPane().add(jButton4);
  	    	jButton4.addActionListener(new ActionListener(){
  	  		@Override
  	    	public void actionPerformed(ActionEvent e) {
  	  			
  				TreeMap<String, Stack<Tuple3<Integer, Integer, Integer>>> addresses = DBSet.getInstance().getPersonAddressMap().getItems(person.getKey());
  				if (addresses.isEmpty()) {
  					
  				} else {
  					Account_Send_Dialog fm = new Account_Send_Dialog(null,null,null, person);				
  				}
  				dispose();	
    		}});
  	    	
  	    	
  	      	jButton5 = new MButton(Lang.getInstance().translate("Send Mail to Person"), 3);
  	    	getContentPane().add(jButton5);
  	    	jButton5.addActionListener(new ActionListener(){
  	  		@Override
  	    	public void actionPerformed(ActionEvent e) {
  	  		
  				TreeMap<String, Stack<Tuple3<Integer, Integer, Integer>>> addresses = DBSet.getInstance().getPersonAddressMap().getItems(person.getKey());
  				if (addresses.isEmpty()) {
  					
  				} else {
  					Mail_Send_Dialog fm = new Mail_Send_Dialog(null,null,null, person);
  				}
  				dispose();	
    		}});
  	    	
    	}
    	
        
        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }
   
               
    private MButton jButton1;
    private MButton jButton2;
    private MButton jButton3;
    private MButton jButton4;
    private MButton jButton5;
  
    // End of variables declaration                   
}
