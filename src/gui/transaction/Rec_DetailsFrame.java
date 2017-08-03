package gui.transaction;

import gui.PasswordPane;
import gui.library.M_Accoutn_Text_Field;
import lang.Lang;
import utill.Transaction;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.bouncycastle.crypto.InvalidCipherTextException;

import utils.Converter;
import utils.DateTimeFormat;
import utils.MenuPopupUtil;
import controller.Controller;
import core.account.Account;
import core.account.PrivateKeyAccount;
import core.account.PublicKeyAccount;
import core.crypto.AEScrypto;
import core.crypto.Base58;
import database.DBSet;

@SuppressWarnings("serial")
public class Rec_DetailsFrame extends JPanel //JFrame
{

	public GridBagConstraints labelGBC = new GridBagConstraints();
	public GridBagConstraints detailGBC = new GridBagConstraints();
	public JTextField signature;
	Transaction record;
	
	public Rec_DetailsFrame(Transaction record1)
	{

		this.record = record1;
		DBSet db = DBSet.getInstance();

		//ICON
		List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon16.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon32.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon64.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon128.png"));
//		this.setIconImages(icons);
		
		//CLOSE
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//LAYOUT
		this.setLayout(new GridBagLayout());
		
		//PADDING
//		((JComponent) this.getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		
		//LABEL GBC
		labelGBC = new GridBagConstraints();
		labelGBC.insets = new Insets(0, 5, 5, 0);
		labelGBC.fill = GridBagConstraints.HORIZONTAL;   
		labelGBC.anchor = GridBagConstraints.FIRST_LINE_START;//..NORTHWEST;
		labelGBC.weightx = 0;	
		labelGBC.gridx = 0;
		
		
		//DETAIL GBC
		detailGBC = new GridBagConstraints();
		detailGBC.insets = new Insets(0, 5, 5, 0);
		detailGBC.fill = GridBagConstraints.HORIZONTAL;  
		detailGBC.anchor = GridBagConstraints.FIRST_LINE_START;//.NORTHWEST;
		detailGBC.weightx = 1;	
		detailGBC.gridwidth = 3;
		detailGBC.gridx = 1;		
		
		
		int componentLevel = 0;
		
		/*
		//LABEL TYPE
		labelGBC.gridy = componentLevel;
		
		JLabel typeLabel = new JLabel(Lang.getInstance().translate("Type") + ":");
		this.add(typeLabel, labelGBC);
		
		//TYPE
		detailGBC.gridy = componentLevel;
		JLabel type = new JLabel(Lang.getInstance().translate("Message Transaction"));
		this.add(type, detailGBC);
		componentLevel ++;
		*/
		
		//LABEL Height + Seq
		labelGBC.gridy = componentLevel;
		JLabel heSeqLabel = new JLabel(Lang.getInstance().translate("Short Info") + ":");
		this.add(heSeqLabel, labelGBC);
				
		//Height + Seq
		detailGBC.gridy = componentLevel++;
		JTextField shorn_Info = new JTextField(DateTimeFormat.timestamptoString(record.getTimestamp())
				+ " [" + record.viewHeightSeq(db) + " " + record.getReference() + "] "
				+ String.valueOf(record.getDataLength(false)) + "^" + String.valueOf(record.getFeePow())
				+ "=" + record.getFeeLong() //+ ">>" + core.item.assets.AssetCls.FEE_ABBREV
				+ ">>" + record.getConfirmations(db));
		shorn_Info.setEditable(false);
//		MenuPopupUtil.installContextMenu(shorn_Info);
		this.add(shorn_Info, detailGBC);

		

		
		//LABEL CREATOR
		componentLevel ++;
		labelGBC.gridy = componentLevel;
		JLabel creatorLabel = new JLabel(Lang.getInstance().translate("Creator") + ":");
		this.add(creatorLabel, labelGBC);
		
		//CREATOR
		detailGBC.gridy = componentLevel;
		M_Accoutn_Text_Field creator = new M_Accoutn_Text_Field(record.getCreator());
		
		creator.setEditable(false);
		
		this.add(creator, detailGBC);

		String personStr = record.getCreator().viewPerson();
		if (personStr.length()>0) {
			//LABEL PERSON
			componentLevel++;
			detailGBC.gridy = componentLevel;
	//		this.add(new JLabel(personStr), detailGBC);
		}

		//LABEL CREATOR PUBLIC KEY
		componentLevel ++;
		labelGBC.gridy = componentLevel;
		JLabel creator_Pub_keyLabel = new JLabel(Lang.getInstance().translate("Creator Publick Key") + ":");
	//	this.add(creator_Pub_keyLabel, labelGBC);
		
		//CREATOR
		detailGBC.gridy = componentLevel;
						
		JTextField creator_Pub_key = new JTextField(record.getCreator().getBase58());
		creator_Pub_key.setEditable(false);
		MenuPopupUtil.installContextMenu(creator_Pub_key);
	//	this.add(creator_Pub_key, detailGBC);
		
		componentLevel++;
		//LABEL SIGNATURE
				labelGBC.gridy = componentLevel;
				JLabel signatureLabel = new JLabel(Lang.getInstance().translate("Signature") + ":");
	//			this.add(signatureLabel, labelGBC);
						
				//SIGNATURE
				detailGBC.gridy = componentLevel;
				//JTextField signature = new JTextField(Base58.encode(record.getSignature()).substring(0, 12) + "..");
				signature = new JTextField(Base58.encode(record.getSignature()));
				signature.setEditable(false);
				MenuPopupUtil.installContextMenu(signature);
	//			this.add(signature, detailGBC);
		
		
		
		/*
		//LABEL FEE POWER
		componentLevel ++;
		labelGBC.gridy = componentLevel;
		JLabel feePowLabel = new JLabel(Lang.getInstance().translate("Size")+" & "+ Lang.getInstance().translate("Fee") + ":");
		this.add(feePowLabel, labelGBC);
						
		//FEE POWER
		detailGBC.gridy = componentLevel;
		JTextField feePow = new JTextField(
				String.valueOf(record.getDataLength(false)) + "^" + String.valueOf(record.getFeePow())
				+ "=" + record.getFeeLong() //+ ">>" + core.item.assets.AssetCls.FEE_ABBREV
				+ ">>" + record.getConfirmations(db));
		feePow.setEditable(false);
		MenuPopupUtil.installContextMenu(feePow);
		this.add(feePow, detailGBC);	
						
		//LABEL CONFIRMATIONS
		componentLevel ++;
		labelGBC.gridy = componentLevel;
		JLabel confirmationsLabel = new JLabel(Lang.getInstance().translate("Confirmations") + ":");
		this.add(confirmationsLabel, labelGBC);
								
		//CONFIRMATIONS
		detailGBC.gridy = componentLevel;
		JLabel confirmations = new JLabel(String.valueOf(record.getConfirmations(db)));
		this.add(confirmations, detailGBC);
		*/	
		  
				new JTextField(DateTimeFormat.timestamptoString(record.getTimestamp())
						
						+ String.valueOf(record.getDataLength(false)) + "^" + String.valueOf(record.getFeePow())
						+ "=" + record.getFeeLong() //+ ">>" + core.item.assets.AssetCls.FEE_ABBREV
						+ ">>" + record.getConfirmations(db));
				
				
				
				JPopupMenu shorn_Info_Meny = new JPopupMenu();
				JMenuItem copy_Transaction_Referencw = new JMenuItem(Lang.getInstance().translate("Copy Referince"));
				copy_Transaction_Referencw.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
			
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						StringSelection value = new StringSelection(record.getReference().toString());
						clipboard.setContents(value, null);
					}
				});
				shorn_Info_Meny.add(copy_Transaction_Referencw);	
				
				JMenuItem copy_Transaction_Sign = new JMenuItem(Lang.getInstance().translate("Copy Signature"));
				copy_Transaction_Sign.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
			
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						StringSelection value = new StringSelection(Base58.encode(record.getSignature()));
						clipboard.setContents(value, null);
					}
				});
				shorn_Info_Meny.add(copy_Transaction_Sign);
				
				JMenuItem copy_Heigt_Block = new JMenuItem(Lang.getInstance().translate("Copy Block"));
				copy_Heigt_Block.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
			
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						StringSelection value = new StringSelection(record.viewHeightSeq(db));
						clipboard.setContents(value, null);
					}
				});
				shorn_Info_Meny.add(copy_Heigt_Block);
				
				
				
				
				
				shorn_Info.setComponentPopupMenu(shorn_Info_Meny);
				
				
	}
}
