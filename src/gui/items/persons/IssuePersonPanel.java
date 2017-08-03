package gui.items.persons;

import gui.MainFrame;
import gui.PasswordPane;
import gui.library.Issue_Confirm_Dialog;
import gui.library.MButton;
import gui.library.My_Add_Image_Panel;
import gui.models.AccountsComboBoxModel;
import lang.Lang;
import utill.Transaction;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;
import com.toedter.calendar.JDateChooser;
import utils.Pair;
import controller.Controller;
import core.account.Account;
import core.account.PrivateKeyAccount;
import core.account.PublicKeyAccount;
import core.crypto.Base58;
import core.item.persons.PersonHuman;
import core.transaction.IssuePersonRecord;
import gui.transaction.IssuePersonDetailsFrame;
import gui.transaction.OnDealClick;

@SuppressWarnings("serial")
public class IssuePersonPanel extends JPanel 
{
	
	protected  MaskFormatter AccFormat;
	protected JComboBox<Account> cbxFrom;
	protected JTextField txtFeePow;
	protected JTextField txtName;
	protected JTextArea txtareaDescription; 
	protected JDateChooser txtBirthday;
	protected JDateChooser txtDeathday;
//	protected JButton iconButton;
	@SuppressWarnings("rawtypes")
	protected JComboBox txtGender;
	protected JTextField txtSNILS;
	protected JTextField txtBirthLatitude;
	protected JTextField txtBirthLongitude;
	protected JTextField txtSkinColor;
	protected JTextField txtEyeColor;
	protected JTextField txtHairСolor;
	protected JTextField txtHeight;
    protected MButton copyButton;
    protected MButton issueButton;
    protected javax.swing.JLabel jLabel_Fee;
    protected javax.swing.JLabel jLabel9;
    protected javax.swing.JLabel jLabel_Account;
    protected javax.swing.JLabel jLabel_BirthLatitude;
    protected javax.swing.JLabel jLabel_BirthLongitude;
    protected javax.swing.JLabel jLabel_Born;
    protected javax.swing.JLabel jLabel_Dead;
    protected javax.swing.JLabel jLabel_Description;
    protected javax.swing.JLabel jLabel_EyeColor;
    protected javax.swing.JLabel jLabel_Gender;
    protected javax.swing.JLabel jLabel_HairСolor;
    protected javax.swing.JLabel jLabel_Height;
    protected javax.swing.JLabel jLabel_Name;
    protected javax.swing.JLabel jLabel_SNILS;
    protected javax.swing.JLabel jLabel_SkinColor;
    protected javax.swing.JLabel jLabel_Title;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel2;
    protected javax.swing.JScrollPane jScrollPane1;
    protected My_Add_Image_Panel add_Image_Panel;
    protected JCheckBox alive_CheckBox ;
  
    // End of variables declaration
	
	protected byte[] imgButes;
	protected JPanel Panel;
	protected JPanel mainPanel;
	protected JScrollPane mainScrollPane1;
	private IssuePersonPanel th;

	@SuppressWarnings({ "unchecked" })
	public IssuePersonPanel()
	{

		th = this;
		initComponents();
		initLabelsText();
		
		cbxFrom.setModel(new AccountsComboBoxModel());
      
		
		
		txtName.setText("");
	// проверка на код
		txtName.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				if (txtName.getText().getBytes().length<2) {
					
					JOptionPane.showMessageDialog(null, Lang.getInstance().translate("the name must be longer than 2 characters"), Lang.getInstance().translate("Error"), JOptionPane.ERROR_MESSAGE);
					txtName.requestFocus();
				}
				
			}
			
			
			
		});
      	
    	String[] items = {
      			Lang.getInstance().translate("Male"),
      			Lang.getInstance().translate("Female"),
      			Lang.getInstance().translate("-")
        	};	
       	txtGender.setModel(new javax.swing.DefaultComboBoxModel<>(items));
       	
        //Calendar can = txtBirthday.getJCalendar().getCalendar();
        //can.set(1999, 11, 12, 13, 14);

       	txtSNILS.setText("");
   //    	this.txtBirthLatitude.setText("0");
       	this.txtBirthLongitude.setText("0");
       	this.txtHeight.setText("170");
       	this.txtFeePow.setText("0");
 
        this.setMinimumSize(new Dimension(0,0)); 
        this.setVisible(true);
        
	}
	
	
	
    @SuppressWarnings("resource")
	protected static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        is.close();
        return bytes;
    }
    
    protected void initLabelsText(){
    	
    	jLabel_Title.setText("");
		jLabel_Account.setText(Lang.getInstance().translate("Account") + ":")	;
		jLabel_Name.setText(Lang.getInstance().translate("Name") + ":");
		jLabel_Description.setText(Lang.getInstance().translate("Description") + ":");
    	jLabel_Gender.setText(Lang.getInstance().translate("Gender") + ":");
     	jLabel_Born.setText(Lang.getInstance().translate("Birthday") + ":");
    	jLabel_SkinColor.setText(Lang.getInstance().translate("Skin Color") + ":");
    	jLabel_EyeColor.setText(Lang.getInstance().translate("Eye Color") + ":");
    	jLabel_HairСolor.setText(Lang.getInstance().translate("Hair Сolor") + ":");
        jLabel_Height.setText(Lang.getInstance().translate("Height") + ":");
        jLabel_Fee.setText(Lang.getInstance().translate("Fee Power") + ":");
    	jLabel_BirthLongitude.setText(Lang.getInstance().translate("Coordinates of Birth") + ":");
    	jLabel_BirthLatitude.setText(Lang.getInstance().translate("Coordinates of Birth") + ":");
    	jLabel_SNILS.setText(Lang.getInstance().translate("Person number") + ":");
    	jLabel_Dead.setText(Lang.getInstance().translate("Deathday") + ":");
    	alive_CheckBox.setText(Lang.getInstance().translate("Alive") + "?");
    	alive_CheckBox.setSelected(true);
    	alive_CheckBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (alive_CheckBox.isSelected()){
					txtDeathday.setVisible(false);
					jLabel_Dead.setVisible(false);
	
				}
				else {
					txtDeathday.setVisible(true);
					jLabel_Dead.setVisible(true);
				}
			}
    		
    		
    		
    		
    	});
    	txtDeathday.setVisible(false);
    	jLabel_Dead.setVisible(false);
    	
    }
	


	@SuppressWarnings("deprecation")
	public void onIssueClick(boolean forIssue)
	{
		//DISABLE
		this.issueButton.setEnabled(false);
		this.copyButton.setEnabled(false);
	
		//CHECK IF NETWORK OK
		if(false && forIssue && Controller.getInstance().getStatus() != Controller.STATUS_OK)
		{
			//NETWORK NOT OK
			JOptionPane.showMessageDialog(null, Lang.getInstance().translate("You are unable to send a transaction while synchronizing or while having no connections!"), Lang.getInstance().translate("Error"), JOptionPane.ERROR_MESSAGE);
			
			//ENABLE
			this.issueButton.setEnabled(true);
			this.copyButton.setEnabled(true);
			
			return;
		}
		
		//CHECK IF WALLET UNLOCKED
		if(!Controller.getInstance().isWalletUnlocked())
		{
			//ASK FOR PASSWORD
			String password = PasswordPane.showUnlockWalletDialog(this); 
			if(!Controller.getInstance().unlockWallet(password))
			{
				//WRONG PASSWORD
				JOptionPane.showMessageDialog(null, Lang.getInstance().translate("Invalid password"), Lang.getInstance().translate("Unlock Wallet"), JOptionPane.ERROR_MESSAGE);
				
				//ENABLE
				this.issueButton.setEnabled(false);
				this.copyButton.setEnabled(false);
				
				return;
			}
		}
		
		//READ CREATOR
		Account sender = (Account) this.cbxFrom.getSelectedItem();

		int parse = 0;
		int feePow = 0;
		byte gender = 0;
		long birthday = 0;
		long deathday = 0;
		float birthLatitude = 0;
		float birthLongitude = 0;
		int height = 0;
		try
		{
			
			//READ FEE POW
			feePow = Integer.parseInt(this.txtFeePow.getText());
			
			//READ GENDER
			parse++;
			gender = (byte) (this.txtGender.getSelectedIndex());
			
			parse++;

			birthday = this.txtBirthday.getCalendar().getTimeInMillis();

			parse++;
			// END DATE
			try {
				deathday = this.txtDeathday.getCalendar().getTimeInMillis();
			} catch(Exception ed1) {
				deathday = birthday - 1;
			}
			if (alive_CheckBox.isSelected())deathday = birthday - 1;
			parse++;
	//		birthLatitude = Float.parseFloat(this.txtBirthLatitude.getText());
			 String[] numArr = this.txtBirthLatitude.getText().split(",");
			 birthLatitude = Float.parseFloat(numArr[0]);
			parse++;
	//		birthLongitude = Float.parseFloat(this.txtBirthLongitude.getText());
			birthLongitude = Float.parseFloat(numArr[1]);
			parse++;
			height = Integer.parseInt(this.txtHeight.getText());
			
		}
		catch(Exception e)
		{
			String mess = "Invalid pars... " + parse;
			switch(parse)
			{
			case 0:
				mess = "Invalid fee power 0..6";
				break;
			case 1:
				mess = "Invalid gender";
				break;
			case 2:
				mess = "Invalid birthday [YYYY-MM-DD] or [YYYY-MM-DD hh:mm:ss]";
				break;
			case 3:
				mess = "Invalid deathday [YYYY-MM-DD] or [YYYY-MM-DD hh:mm:ss]";
				break;
			case 4:
				mess = "Invalid Coordinates of Birth, example: 43.123032, 131.917828";
				break;
			case 5:
				mess = "Invalid Coordinates of Birth, example: 43.123032, 131.917828";
				break;
			case 6:
				mess = "Invalid height 10..255";
				break;
			}
			JOptionPane.showMessageDialog(new JFrame(), Lang.getInstance().translate(mess), Lang.getInstance().translate("Error"), JOptionPane.ERROR_MESSAGE);
			
			this.issueButton.setEnabled(true);
			this.copyButton.setEnabled(true);
			return;
		}
						
		//CREATE ASSET
		//protectedKeyAccount creator, String fullName, int feePow, long birthday,
		//byte gender, String race, float birthLatitude, float birthLongitude,
		//String skinColor, String eyeColor, String hairСolor, int height, String description
		PrivateKeyAccount creator = Controller.getInstance().getPrivateKeyAccountByAddress(sender.getAddress());
		PublicKeyAccount owner = (PublicKeyAccount)creator;
		Pair<Transaction, Integer> result = Controller.getInstance().issuePerson(
				forIssue,
				creator, this.txtName.getText(), feePow, birthday, deathday,
				gender, this.txtSNILS.getText(), birthLatitude, birthLongitude,
				this.txtSkinColor.getText(), this.txtEyeColor.getText(),
				this.txtHairСolor.getText(), height,
				null, add_Image_Panel.imgButes, this.txtareaDescription.getText(),
				owner, null);
		
		
		
		
		
		//CHECK VALIDATE MESSAGE
		if (result.getB() == Transaction.VALIDATE_OK) {
		
			if (!forIssue) {
				IssuePersonRecord issuePersonRecord = (IssuePersonRecord)result.getA();
				PersonHuman person = (PersonHuman)issuePersonRecord.getItem();
				// SIGN
				person.sign(creator);
				
				String base58str = Base58.encode(person.toBytes(false, false));
				// This method writes a string to the system clipboard.
				// otherwise it returns null.
			    StringSelection sss = new StringSelection(base58str);
			    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sss, null);				
			    JOptionPane.showMessageDialog(new JFrame(), Lang.getInstance().translate(
						"Person issue has been copy to buffer!"),
						Lang.getInstance().translate("Success"), JOptionPane.INFORMATION_MESSAGE);
			
			  //ENABLE
				this.issueButton.setEnabled(true);
				this.copyButton.setEnabled(true); 
			return;
			}
			
		    String Status_text = "<HTML>"+ Lang.getInstance().translate("Size")+":&nbsp;"+ result.getA().viewSize(true)+" Bytes, ";
		    Status_text += "<b>" +Lang.getInstance().translate("Fee")+":&nbsp;"+ result.getA().getFee().toString()+" COMPU</b><br></body></HTML>";
		    
	//	  System.out.print("\n"+ text +"\n");
	//	    UIManager.put("OptionPane.cancelButtonText", "Отмена");
	//	    UIManager.put("OptionPane.okButtonText", "Готово");
		
	//	int s = JOptionPane.showConfirmDialog(MainFrame.getInstance(), text, Lang.getInstance().translate("Issue Asset"),  JOptionPane.YES_NO_OPTION);
		
		Issue_Confirm_Dialog dd = new Issue_Confirm_Dialog(MainFrame.getInstance(), true," ", (int) (th.getWidth()/1.2), (int) (th.getHeight()/1.2),Status_text, Lang.getInstance().translate("Confirmation Transaction") +" " + Lang.getInstance().translate("Issue Person"));
	
		IssuePersonDetailsFrame ww = new IssuePersonDetailsFrame((IssuePersonRecord) result.getA());
	//	ww.jPanel2.setVisible(false);
		dd.jScrollPane1.setViewportView(ww);
		dd.setLocationRelativeTo(th);
		dd.setVisible(true);
//		JOptionPane.OK_OPTION
			if (dd.isConfirm){ //s!= JOptionPane.OK_OPTION)	{
				
							
			//VALIDATE AND PROCESS
			Integer result1 = Controller.getInstance().getTransactionCreator().afterCreate(result.getA(), false);
			if (result1 != Transaction.VALIDATE_OK){
					JOptionPane.showMessageDialog(new JFrame(), Lang.getInstance().translate(OnDealClick.resultMess(result1)), Lang.getInstance().translate("Error"), JOptionPane.ERROR_MESSAGE);
			}
			else{
				JOptionPane.showMessageDialog(new JFrame(), Lang.getInstance().translate(
						"Person issue has been sent!"),
						Lang.getInstance().translate("Success"), JOptionPane.INFORMATION_MESSAGE);
				
			}
		
			}	
		//	reset();
			
		} else {		
			JOptionPane.showMessageDialog(new JFrame(), Lang.getInstance().translate(OnDealClick.resultMess(result.getB())), Lang.getInstance().translate("Error"), JOptionPane.ERROR_MESSAGE);
		}
		
		
		
		//ENABLE
		this.issueButton.setEnabled(true);
		this.copyButton.setEnabled(true);
	}

	protected void reset() {
		//txtFeePow.setText("0");
		txtName.setText("");
		txtareaDescription.setText("");
		//txtBirthday.setText("0000-00-00");
		//txtDeathday.setText("0000-00-00");
		
		//txtGender.setSelectedIndex(2);
		//txtRace.setText("");
		//txtBirthLatitude.setText("");
		//txtBirthLongitude.setText("");
		//txtSkinColor.setText("");
		//txtEyeColor.setText("");
		//txtHairСolor.setText("");
		//txtHeight.setText("");
		add_Image_Panel.reset();
	}
                             
    @SuppressWarnings({ "unchecked" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    protected void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel_Gender = new javax.swing.JLabel();
        jLabel_Born = new javax.swing.JLabel();
        jLabel_Dead = new javax.swing.JLabel();
        jLabel_SNILS = new javax.swing.JLabel();
        txtSNILS = new javax.swing.JTextField();
        jLabel_SkinColor = new javax.swing.JLabel();
        txtSkinColor = new javax.swing.JTextField();
        jLabel_EyeColor = new javax.swing.JLabel();
        txtEyeColor = new javax.swing.JTextField();
        jLabel_HairСolor = new javax.swing.JLabel();
        txtHairСolor = new javax.swing.JTextField();
        jLabel_Height = new javax.swing.JLabel();
        txtHeight = new javax.swing.JTextField();
        jLabel_BirthLatitude = new javax.swing.JLabel();
        txtBirthLatitude = new javax.swing.JTextField();
        jLabel_BirthLongitude = new javax.swing.JLabel();
        txtBirthLongitude = new javax.swing.JTextField();
        jLabel_Fee = new javax.swing.JLabel();
        txtFeePow = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtareaDescription = new javax.swing.JTextArea();
        txtName = new javax.swing.JTextField();
        jLabel_Name = new javax.swing.JLabel();
        jLabel_Account = new javax.swing.JLabel();
        jLabel_Description = new javax.swing.JLabel();
        cbxFrom = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        issueButton = new MButton(Lang.getInstance().translate("Create and insert Person"), 2);
        copyButton = new MButton(Lang.getInstance().translate("Create Person and copy to clipboard"), 2);
        jLabel_Title = new javax.swing.JLabel();
        txtGender = new javax.swing.JComboBox<>();
        mainPanel = new javax.swing.JPanel();
        mainScrollPane1 = new javax.swing.JScrollPane();
        add_Image_Panel = new My_Add_Image_Panel(Lang.getInstance().translate("Add Image (%1% - %2% bytes)")
        		.replace("%1%", "" + (IssuePersonRecord.MAX_IMAGE_LENGTH - (IssuePersonRecord.MAX_IMAGE_LENGTH>>2)))
        		.replace("%2%", "" + IssuePersonRecord.MAX_IMAGE_LENGTH), 350,0);
        alive_CheckBox = new JCheckBox();
    	
        this.issueButton.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		        onIssueClick(true);
		    }
		});
        
       	
        this.copyButton.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		        onIssueClick(false);
		    }
		});
        
        // SET ONE TIME ZONE for Birthday 
		TimeZone tz  = TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        txtBirthday =  new JDateChooser("yyyy-MM-dd HH:mm 'UTC'", "####-##-## ##:##", '_');
        Calendar ccc = Calendar.getInstance(tz);
        ccc.set(1990, 10, 11, 12, 13, 01);
        txtBirthday.setCalendar(ccc);
        txtDeathday = new JDateChooser("yyyy-MM-dd HH:mm 'UTC'", "####-##-## ##:##", '_');
		TimeZone.setDefault(tz);
		 this.setLayout(new java.awt.BorderLayout());
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        layout.rowHeights = new int[] {0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0};
        mainPanel.setLayout(layout);

      
// born
        jLabel_Born.setText("born");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 0, 0);
        mainPanel.add(jLabel_Born, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        mainPanel.add( txtBirthday, gridBagConstraints);        
        txtBirthday.setFont(UIManager.getFont("TextField.font"));

// dead
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainPanel.add(alive_CheckBox, gridBagConstraints);
        
        
        jLabel_Dead.setText("Dead");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainPanel.add(jLabel_Dead, gridBagConstraints);
      
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        mainPanel.add(txtDeathday, gridBagConstraints);
        txtDeathday.setFont(UIManager.getFont("TextField.font"));
// gender
        jLabel_Gender.setText("Pol");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 0, 0);
        mainPanel.add(jLabel_Gender, gridBagConstraints);
        
        txtGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        mainPanel.add(txtGender, gridBagConstraints);
        
// CNILS
        
        jLabel_SNILS.setText("Person number");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainPanel.add(jLabel_SNILS, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        mainPanel.add(txtSNILS, gridBagConstraints);
// SkinColor
        jLabel_SkinColor.setText("SkinColor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 0, 0);
        mainPanel.add(jLabel_SkinColor, gridBagConstraints);

      
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        mainPanel.add(txtSkinColor, gridBagConstraints);
// EyeColor
        jLabel_EyeColor.setText("EyeColor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainPanel.add(jLabel_EyeColor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        mainPanel.add(txtEyeColor, gridBagConstraints);
//HairСolor
        jLabel_HairСolor.setText("HairСolor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 0, 0);
        mainPanel.add(jLabel_HairСolor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        mainPanel.add(txtHairСolor, gridBagConstraints);
// Height
        jLabel_Height.setText("P.Height");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainPanel.add(jLabel_Height, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        mainPanel.add(txtHeight, gridBagConstraints);
// BirthLatitude
        jLabel_BirthLatitude.setText("Coordinates of Birth");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 0, 0);
        mainPanel.add(jLabel_BirthLatitude, gridBagConstraints);

   
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        mainPanel.add(txtBirthLatitude, gridBagConstraints);

// Fee

        jLabel_Fee.setText("Fee");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 0, 0);
        mainPanel.add(jLabel_Fee, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 1);
        mainPanel.add(txtFeePow, gridBagConstraints);

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        jPanel1Layout.rowHeights = new int[] {0, 4, 0, 4, 0};
        jPanel1.setLayout(jPanel1Layout);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.05;
     //   jPanel1.add(iconButton, gridBagConstraints);
        
        jPanel1.add(add_Image_Panel, gridBagConstraints);
        txtareaDescription.setColumns(20);
        txtareaDescription.setRows(5);
        jScrollPane1.setViewportView(txtareaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        jPanel1.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.1;
        jPanel1.add(txtName, gridBagConstraints);

        jLabel_Name.setText("Name");
        jLabel_Name.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabel_Name, gridBagConstraints);

        jLabel_Account.setText("Account");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanel1.add(jLabel_Account, gridBagConstraints);

        jLabel_Description.setText("Description");
        jLabel_Description.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabel_Description, gridBagConstraints);

   //     cbxFrom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(cbxFrom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 18, 0, 16);
        mainPanel.add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.2;
        mainPanel.add(jLabel9, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());
        
     //   jPanel2.add(issueButton, new java.awt.GridBagConstraints());

        jPanel2.add(copyButton, new java.awt.GridBagConstraints());
        

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        mainPanel.add(jPanel2, gridBagConstraints);

        jLabel_Title.setText("jLabel8");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        mainPanel.add(jLabel_Title, gridBagConstraints);

      

     /*     
        try {
			AccFormat = new MaskFormatter("****-**-**");
			AccFormat.setValidCharacters("1,2,3,4,5,6,7,8,9,0");
	     //   AccFormat.setPlaceholder("yyyy-mm-dd");
	        AccFormat.setPlaceholderCharacter('_');
	      //  AccFormat.setOverwriteMode(true);
	        
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        JFormattedTextField txtBirthday = new JFormattedTextField(AccFormat);    
    */    
         
        //txtBirthday.setDateFormatString("yyyy-MM-dd");
      
        
   //     txtBirthday.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
      
  //      txtDeathday.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
  //      txtDeathday.addActionListener(new java.awt.event.ActionListener() {
 //           public void actionPerformed(java.awt.event.ActionEvent evt) {
 //               txtDeathdayActionPerformed(evt);
 //           }
 //       });
        
        ///txtDeathday.setDateFormatString("yyyy-MM-dd");
      
   //     mainScrollPane1.setHorizontalScrollBar(null);
        mainScrollPane1.setViewportView(mainPanel);
   //   
       this.add(mainScrollPane1,  java.awt.BorderLayout.CENTER);
    }// </editor-fold>                        

}
// Фильтр выбора файлов определенного типа
class FileFilterExt extends javax.swing.filechooser.FileFilter 
{
	String extension  ;  // расширение файла
	String description;  // описание типа файлов

	FileFilterExt(String extension, String descr)
	{
		this.extension = extension;
		this.description = descr;
	}
	@Override
	public boolean accept(java.io.File file)
	{
		if(file != null) {
			if (file.isDirectory())
				return true;
			if( extension == null )
				return (extension.length() == 0);
			return file.getName().endsWith(extension);
		}
		return false;
	}
	// Функция описания типов файлов
	@Override
	public String getDescription() {
		return description;
	}
	
}

