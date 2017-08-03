package gui.records;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.JTextPane;

import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple4;

import core.crypto.Base58;
import database.DBSet;
//import gui.MainFrame;
import lang.Lang;
import utill.Transaction;

// Info for record
public class Record_Info extends JTextPane {
	
	//private static final long serialVersionUID = 4763074704570450206L;
	private static final long serialVersionUID = 2717571093561259483L;

	
	public  Record_Info() {
	
		this.setContentType("text/html");
	//	this.setBackground(MainFrame.getFrames()[0].getBackground());
		
	}
	
	
	static String Get_HTML_Record_Info_001(Transaction record)
	{
		
		DBSet db = DBSet.getInstance();

		String message = "";
		SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy"); // HH:mm");
		
		if (record == null) return message += "Empty Record";
		
		if (!record.isConfirmed(db)) {
			message = Lang.getInstance().translate("Not confirmed");
		} else {
			message = "Block Height - SeqNo.: " + record.viewHeightSeq(db) + ", Confs.: " + record.getConfirmations(DBSet.getInstance()) + ", Block Ver.: " + record.getBlockVersion(db);
		}
		message = "<div><b>" + message + "</b>"
			+ ", time: " + record.viewTimestamp() + "</div>";
		message += "<div> type: <b>" + record.viewFullTypeName() + "</b>, size: " + record.viewSize(false) + ", fee:" + record.viewFee() + "</div>";
	
		message += "<div>REF: <font size='2'>" + record.viewReference() + "</font></div>";
		message += "<div>SIGN: <font size='2'>" + record.viewSignature() + "</font></div>";

		message += "<div>Creator: <font size='4'>" + record.viewCreator() + "</font></div>";
		message += "<div>Item: <font size='4'>" + record.viewItemName() + "</font></div>";
		message += "<div>Amount: <font size='4'>" + record.viewAmount() + "</font></div>";
		message += "<div>Recipient: <font size='4'>" + record.viewRecipient() + "</font></div>";
		message += "<div>JSON: <font size='4'>" + record.toJson().toString() + "</font></div>";

		return message;
	}
	 	
	public void show_001(Transaction record){
		
		setText("<html>" + Get_HTML_Record_Info_001(record) + "</html>");
		return;
	}	
	public void show_mess(String mess){
		
		setText("<html>" + mess + "</html>");
		return;
	}	

}
