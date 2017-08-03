package core.transaction;

import java.math.BigDecimal;
//import java.math.BigDecimal;
//import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
//import java.util.List;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import org.apache.log4j.Logger;
import java.util.List;

import org.json.simple.JSONObject;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import core.account.Account;
import core.account.PublicKeyAccount;
import core.crypto.Base58;
import core.item.ItemCls;
//import database.BalanceMap;
import database.DBSet;
import utill.BlockChain;
import utill.Transaction;


// issue statement
public class IssueStatementRecord extends Transaction {

	private static final byte TYPE_ID = (byte) ISSUE_STATEMENT_TRANSACTION;
	private static final String NAME_ID = "Issue Statement";

	protected static final byte HAS_NOTE_MASK = (byte)(1 << 7);
	/*
	PROPERTIES:
	[0] - type
	[1] - version 
	[2] bits[0] - =1 - has Note 
	[2] bits [6,7] - signers: 0 - none; 1..3 = 1..3; 4 = LIST -> 1 byte for LIST.len + 3 
	[3] - < 0 - has DATA
		*/
	protected long key; // key for Note
	protected byte[] data;
	protected byte[] encrypted;
	protected byte[] isText;
	protected PublicKeyAccount[] signers; // for all it need ecnrypt
	protected byte[][] signatures; // - multi sign
	
	public IssueStatementRecord(byte[] typeBytes, PublicKeyAccount creator, byte feePow, long noteKey, byte[] data, byte[] isText, byte[] encrypted, long timestamp, Long reference) {
		
		super(typeBytes, NAME_ID, creator, feePow, timestamp, reference);

		this.key = noteKey;
		this.data = data;
		this.encrypted = encrypted;
		this.isText = isText;
	}
	public IssueStatementRecord(byte[] typeBytes, PublicKeyAccount creator, byte feePow, long noteKey, byte[] data, byte[] isText, byte[] encrypted, long timestamp, Long reference, byte[] signature) {
		this(typeBytes, creator, feePow, noteKey, data, isText, encrypted, timestamp, reference);
		this.signature = signature;
		this.calcFee();
	}
	// asPack
	public IssueStatementRecord(byte[] typeBytes, PublicKeyAccount creator, long noteKey, byte[] data, byte[] isText, byte[] encrypted, Long reference, byte[] signature) {
		this(typeBytes, creator, (byte)0, noteKey, data, isText, encrypted, 0l, reference);
		this.signature = signature;
		// not need this.calcFee();
	}
	public IssueStatementRecord(PublicKeyAccount creator, byte feePow, long noteKey, byte[] data, byte[] isText, byte[] encrypted, long timestamp, Long reference, byte[] signature) {
		this(new byte[]{TYPE_ID, 0, 0, 0}, creator, feePow, noteKey, data, isText, encrypted, timestamp, reference, signature);
		// set props
		this.setTypeBytes();
	}
	public IssueStatementRecord(PublicKeyAccount creator, byte feePow, long noteKey, byte[] data, byte[] isText, byte[] encrypted, long timestamp, Long reference) {
		this(new byte[]{TYPE_ID, 0, 0, 0}, creator, feePow, noteKey, data, isText, encrypted, timestamp, reference);
		// set props
		this.setTypeBytes();
	}
	public IssueStatementRecord(byte[] typeBytes, PublicKeyAccount creator, byte feePow, long noteKey, byte[] data,
			byte[] isText, byte[] encrypted, PublicKeyAccount[] signers, byte[][] signatures, long timestamp, Long reference, byte[] signature)
	{
		this(typeBytes, creator, feePow, noteKey, data, isText, encrypted, timestamp, reference, signature);
		this.signers = signers;
		this.signatures = signatures;
		this.setTypeBytes();
	}
	// as Pack
	public IssueStatementRecord(byte[] typeBytes, PublicKeyAccount creator, long noteKey, byte[] data,
			byte[] isText, byte[] encrypted, PublicKeyAccount[] signers, byte[][] signatures, Long reference, byte[] signature)
	{
		this(typeBytes, creator, noteKey, data, isText, encrypted, reference, signature);
		this.signers = signers;
		this.signatures = signatures;
		this.setTypeBytes();
	}
	public IssueStatementRecord(byte prop1, byte prop2, byte prop3, PublicKeyAccount creator, byte feePow, long noteKey, byte[] data, byte[] isText, byte[] encrypted, long timestamp, Long reference)
	{
		this(new byte[]{TYPE_ID, prop1, prop2, prop3}, creator, feePow, noteKey, data, isText, encrypted, timestamp, reference);
	}

	//GETTERS/SETTERS
	public void setSidnerSignature(int index, byte[] signature) {
		if (signatures == null)
			signatures = new byte[signers.length][];
		
		signatures[index] = signature;
		
	}
	
	public static boolean hasNote(byte[] typeBytes) {
		if (typeBytes[2] < 0 ) return true;
		return false;
	}
	protected boolean hasNote() {
		return hasNote(this.typeBytes);
	}
	public static int getSignersLength(byte[] typeBytes) {
		byte mask = ~HAS_NOTE_MASK;
		return typeBytes[2] & mask;
	}
	
	protected void setTypeBytes() {

		byte vers = 0;
		
		byte prop1 = 0;
		if (this.signers != null && this.signers.length > 0) {
			int len = this.signers.length; 
			if (len < 4) {
				prop1 = (byte)len;
			} else {
				prop1 = (byte)4;
			}
		}
		// set has NOTE byte
		if (this.key > 0) prop1 = (byte) (HAS_NOTE_MASK | prop1);
			
		byte prop2 = 0;
		if (data != null && data.length > 0) {
			prop2 = (byte)(prop2 | (byte)-128);
		}

		if (this.typeBytes == null) {
			this.typeBytes  = new byte[]{TYPE_ID, vers, prop1, prop2};
		} else {
			this.typeBytes[2] = prop1; // property 1
			this.typeBytes[3] = prop2; // property 2
		}
	}

	//public static String getName() { return "Statement"; }

	public long getKey() 
	{
		return this.key;
	}
	
	public byte[] getData() 
	{
		return this.data;
	}
	
	public boolean isText()
	{
		if (data == null || data.length == 0) return false;
		return (Arrays.equals(this.isText,new byte[1]))?false:true;
	}
	
	public boolean isEncrypted()
	{
		if (data == null || data.length == 0) return false;
		return (Arrays.equals(this.encrypted, new byte[1]))?false:true;
	}

	public PublicKeyAccount[] getSigners() 
	{
		return this.signers;
	}
	public String[] getSignersB58() 
	{
		String[] pbKeys = new String[0];
		int i = 0;
		for (PublicKeyAccount key: this.signers)
		{
			pbKeys[i++] = Base58.encode(key.getPublicKey());
		};
		return pbKeys;
	}

	public byte[][] getSignersSignatures() 
	{
		return this.signatures;
	}
	public String[] getSignersSignaturesB58() 
	{
		String[] items = new String[0];
		int i = 0;
		for (byte[] item: this.signatures)
		{
			items[i++] = Base58.encode(item);
		};
		return items;
	}

	public boolean hasPublicText() {
		if (data == null || data.length == 0)
			return false;
		if (!Arrays.equals(this.encrypted,new byte[1]))
			return false;
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJson() 
	{
		//GET BASE
		JSONObject transaction = this.getJsonBase();

		//ADD CREATOR/SERVICE/DATA
		if (data != null && data.length > 0) {

			//ADD CREATOR/SERVICE/DATA
			if ( this.isText() && !this.isEncrypted() )
			{
				transaction.put("data", new String(this.data, Charset.forName("UTF-8")));
			}
			else
			{
				transaction.put("data", Base58.encode(this.data));
			}
			transaction.put("encrypted", this.isEncrypted());
			transaction.put("isText", this.isText());
		}

		if (this.key > 0)
			transaction.put("note", this.key);

		if (signers != null && signers.length >0) {
			transaction.put("singers", this.getSignersB58());
			transaction.put("signatures", this.getSignersSignaturesB58());
		}
		return transaction;	
	}
	
	// releaserReference = null - not a pack
	// releaserReference = reference for releaser account - it is as pack
	public static Transaction Parse(byte[] data, Long releaserReference) throws Exception
	{
		boolean asPack = releaserReference != null;
		
		//CHECK IF WE MATCH BLOCK LENGTH
		if (data.length < BASE_LENGTH_AS_PACK
				| !asPack & data.length < BASE_LENGTH)
		{
			throw new Exception("Data does not match block length " + data.length);
		}
		
		// READ TYPE
		byte[] typeBytes = Arrays.copyOfRange(data, 0, TYPE_LENGTH);
		int position = TYPE_LENGTH;

		long timestamp = 0;
		if (!asPack) {
			//READ TIMESTAMP
			byte[] timestampBytes = Arrays.copyOfRange(data, position, position + TIMESTAMP_LENGTH);
			timestamp = Longs.fromByteArray(timestampBytes);	
			position += TIMESTAMP_LENGTH;
		}

		Long reference = null;
		if (!asPack) {
			//READ REFERENCE
			byte[] referenceBytes = Arrays.copyOfRange(data, position, position + REFERENCE_LENGTH);
			reference = Longs.fromByteArray(referenceBytes);	
			position += REFERENCE_LENGTH;
		} else {
			reference = releaserReference;
		}
		
		//READ CREATOR
		byte[] creatorBytes = Arrays.copyOfRange(data, position, position + CREATOR_LENGTH);
		PublicKeyAccount creator = new PublicKeyAccount(creatorBytes);
		position += CREATOR_LENGTH;
		
		byte feePow = 0;
		if (!asPack) {
			//READ FEE POWER
			byte[] feePowBytes = Arrays.copyOfRange(data, position, position + 1);
			feePow = feePowBytes[0];
			position += 1;
		}
		
		//READ SIGNATURE
		byte[] signatureBytes = Arrays.copyOfRange(data, position, position + SIGNATURE_LENGTH);
		position += SIGNATURE_LENGTH;

		//////// local parameters
		
		long key = 0l;
		if (hasNote(typeBytes)) 
		{
			//READ KEY
			byte[] keyBytes = Arrays.copyOfRange(data, position, position + KEY_LENGTH);
			key = Longs.fromByteArray(keyBytes);	
			position += KEY_LENGTH;
		}

		// DATA +++ - from core.transaction.R_Send.Parse(byte[], Long)
		byte[] arbitraryData = null;
		byte[] encryptedByte = null;
		byte[] isTextByte = null;
		if (typeBytes[3] < 0) {
			// IF here is DATA

			//READ DATA SIZE
			byte[] dataSizeBytes = Arrays.copyOfRange(data, position, position + DATA_SIZE_LENGTH);
			int dataSize = Ints.fromByteArray(dataSizeBytes);	
			position += DATA_SIZE_LENGTH;
	
			//READ DATA
			arbitraryData = Arrays.copyOfRange(data, position, position + dataSize);
			position += dataSize;
			
			encryptedByte = Arrays.copyOfRange(data, position, position + ENCRYPTED_LENGTH);
			position += ENCRYPTED_LENGTH;
			
			isTextByte = Arrays.copyOfRange(data, position, position + IS_TEXT_LENGTH);
			position += IS_TEXT_LENGTH;
		}

		int signersLen = getSignersLength(typeBytes);
		PublicKeyAccount[] signers = null;
		byte[][] signatures = null;
		if (signersLen > 0) {
			if (signersLen == 4) {
				//READ ONE BITE for len
				byte[] signersLenBytes = Arrays.copyOfRange(data, position, position + 1);
				signersLen = Byte.toUnsignedInt(signersLenBytes[0]) + 4;
				position ++;
			}
			signers = new PublicKeyAccount[signersLen];
			signatures = new byte[signersLen][];
			for (int i = 0; i < signersLen ; i++) {
				signers[i] = new PublicKeyAccount(Arrays.copyOfRange(data, position, position + CREATOR_LENGTH));
				position += CREATOR_LENGTH;
				signatures[i] = Arrays.copyOfRange(data, position, position + SIGNATURE_LENGTH);
				position += SIGNATURE_LENGTH;
			}
		}
		
		if (signersLen == 0) {
			if (!asPack) {
				return new IssueStatementRecord(typeBytes, creator, feePow, key, arbitraryData, isTextByte, encryptedByte, timestamp, reference, signatureBytes);
			} else {
				return new IssueStatementRecord(typeBytes, creator, key, arbitraryData, isTextByte, encryptedByte, reference, signatureBytes);
			}
		} else {
			if (!asPack) {
				return new IssueStatementRecord(typeBytes, creator, feePow, key, arbitraryData, isTextByte, encryptedByte, signers, signatures, timestamp, reference, signatureBytes);
			} else {
				return new IssueStatementRecord(typeBytes, creator, key, arbitraryData, isTextByte, encryptedByte, signers, signatures, reference, signatureBytes);
			}
			
		}

	}

	//@Override
	public byte[] toBytes(boolean withSign, Long releaserReference) {

		byte[] data = super.toBytes(withSign, releaserReference);

		if (this.key > 0 ) {
			//WRITE KEY
			byte[] keyBytes = Longs.toByteArray(this.key);
			keyBytes = Bytes.ensureCapacity(keyBytes, KEY_LENGTH, 0);
			data = Bytes.concat(data, keyBytes);
			
		}
		if (this.data != null ) {
			
			//WRITE DATA SIZE
			byte[] dataSizeBytes = Ints.toByteArray(this.data.length);
			data = Bytes.concat(data, dataSizeBytes);
	
			//WRITE DATA
			data = Bytes.concat(data, this.data);
			
			//WRITE ENCRYPTED
			data = Bytes.concat(data, this.encrypted);
			
			//WRITE ISTEXT
			data = Bytes.concat(data, this.isText);
		}

		return data;	
	}

	@Override
	public int getDataLength(boolean asPack) {
		int add_len = 0;
		if (this.data != null && this.data.length > 0)
			add_len += IS_TEXT_LENGTH + ENCRYPTED_LENGTH + DATA_SIZE_LENGTH + this.data.length;
		if (this.key > 0)
			add_len += KEY_LENGTH;
		
		if (asPack) {
			return BASE_LENGTH_AS_PACK + add_len;
		} else {
			return BASE_LENGTH + add_len;
		}
	}

	//@Override
	public int isValid(DBSet db, Long releaserReference) {
		
		//CHECK DATA SIZE
		if(data.length > BlockChain.MAX_REC_DATA_BYTES || data.length < 1)
		{
			return INVALID_DATA_LENGTH;
		}
	

		int result = super.isValid(db, releaserReference);
		if (result != Transaction.VALIDATE_OK) return result; 
		
		// ITEM EXIST? - for assets transfer not need - amount expect instead
		if (!db.getItemNoteMap().contains(this.key))
			return Transaction.ITEM_DOES_NOT_EXIST;

		return Transaction.VALIDATE_OK;

	}
	
	@Override
	public HashSet<Account> getInvolvedAccounts()
	{
		HashSet<Account> accounts = new HashSet<Account>();
		accounts.add(this.creator);
		return accounts;
	}
	
	@Override
	public HashSet<Account> getRecipientAccounts()
	{
		return new HashSet<>();
	}
	
	@Override
	public boolean isInvolved(Account account) 
	{
		String address = account.getAddress();
		
		if(address.equals(this.creator.getAddress()))
		{
			return true;
		}
		
		return false;
	}

	@Override
	public int calcBaseFee() {
		return calcCommonFee();
	}
}
