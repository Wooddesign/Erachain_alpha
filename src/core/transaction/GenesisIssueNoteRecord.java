package core.transaction;

import java.math.BigDecimal;
//import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
 import org.apache.log4j.Logger;

//import ntp.NTP;

import org.json.simple.JSONObject;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import core.account.Account;
import core.account.PrivateKeyAccount;
import core.account.PublicKeyAccount;
import core.crypto.Base58;
import core.crypto.Crypto;
import core.item.notes.NoteCls;
import core.item.notes.NoteFactory;
import database.ItemNoteMap;
import utill.Transaction;
//import database.BalanceMap;
import database.DBSet;

public class GenesisIssueNoteRecord extends GenesisIssue_ItemRecord 
{
	
	private static final byte TYPE_ID = (byte)GENESIS_ISSUE_NOTE_TRANSACTION;
	private static final String NAME_ID = "GENESIS Issue Note";
	
	public GenesisIssueNoteRecord(NoteCls note) 
	{
		super(TYPE_ID, NAME_ID, note);
	}

	//PARSE CONVERT
	public static Transaction Parse(byte[] data) throws Exception
	{	
		//CHECK IF WE MATCH BLOCK LENGTH
		if(data.length < BASE_LENGTH)
		{
			throw new Exception("Data does not match block length");
		}
		
		// READ TYPE
		int position = SIMPLE_TYPE_LENGTH;
	
		//READ NOTE
		// read without reference
		NoteCls note = NoteFactory.getInstance().parse(Arrays.copyOfRange(data, position, data.length), false);
		//position += note.getDataLength(false);
						
		return new GenesisIssueNoteRecord(note);
	}	
	
}
