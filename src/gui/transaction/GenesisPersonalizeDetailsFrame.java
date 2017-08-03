package gui.transaction;


import javax.swing.JLabel;
import javax.swing.JTextField;

import controller.Controller;
import core.transaction.GenesisIssuePersonRecord;
import database.DBSet;
import lang.Lang;
import utils.MenuPopupUtil;

@SuppressWarnings("serial")
public class GenesisPersonalizeDetailsFrame extends RecGenesis_DetailsFrame
{
	public GenesisPersonalizeDetailsFrame(GenesisIssuePersonRecord record)
	{
		super(record);
		
		//LABEL RECIPIENT
		++labelGBC.gridy;
		JLabel recipientLabel = new JLabel(Lang.getInstance().translate("Recipient") + ":");
		this.add(recipientLabel, labelGBC);
		
		//RECIPIENT
		++detailGBC.gridy;
		JTextField recipient = new JTextField(record.viewRecipient());
		recipient.setEditable(false);
		MenuPopupUtil.installContextMenu(recipient);
		this.add(recipient, detailGBC);		
		
		//LABEL PERSON
		++labelGBC.gridy;
		JLabel assetLabel = new JLabel(Lang.getInstance().translate("Person") + ":");
		this.add(assetLabel, labelGBC);
		
		//PERSON
		++detailGBC.gridy;
		JTextField asset = new JTextField(String.valueOf(Controller.getInstance().getPerson(record.getItem().getKey(DBSet.getInstance())).toString()));
		asset.setEditable(false);
		MenuPopupUtil.installContextMenu(asset);
		this.add(asset, detailGBC);	
		           
        //PACK
	//	this.pack();
   //     this.setResizable(false);
   //     this.setLocationRelativeTo(null);
        this.setVisible(true);
	}
}
