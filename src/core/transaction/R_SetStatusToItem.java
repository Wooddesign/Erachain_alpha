package core.transaction;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.EnumSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple5;
import org.mapdb.Fun.Tuple6;
import org.json.simple.JSONObject;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import controller.Controller;
import core.account.Account;
import core.account.PrivateKeyAccount;
import core.account.PublicKeyAccount;
import core.crypto.Base58;
import core.crypto.Crypto;
import core.item.statuses.StatusCls;
import core.item.ItemCls;
import ntp.NTP;
import utill.Block;
import utill.BlockChain;
import utill.DBMap;
import utill.Transaction;
import utils.DateTimeFormat;
import database.DBSet;

public class R_SetStatusToItem extends Transaction {

	private static final byte TYPE_ID = (byte)Transaction.SET_STATUS_TO_ITEM_TRANSACTION;
	private static final String NAME_ID = "Set Status";
	private static final int DATE_LENGTH = Transaction.TIMESTAMP_LENGTH; // one year + 256 days max
	private static final int VALUE_LENGTH = 8; // one year + 256 days max
	private static final int REF_LENGTH = 8;
	private static final int ITEM_TYPE_LENGTH = 1;
	// need RIGHTS for non PERSON account

	protected Long key; // STATUS KEY
	protected int itemType; // ITEM TYPE (CAnnot read ITEMS on start DB - need reset ITEM after
	protected Long itemKey; // ITEM KEY
	protected long beg_date;
	protected long end_date = Long.MAX_VALUE;
	protected long value_1; // first any value
	protected long value_2; // second any value
	protected byte[] data_1; // addition data
	protected byte[] data_2; // addition data
	protected byte[] description; // addition data
	protected long ref_to_parent; // reference to parent record as int + int (block height + record sequence number)
	
	// DESCRIPTION as tail
	private static final int SELF_LENGTH = 2*DATE_LENGTH + KEY_LENGTH + ITEM_TYPE_LENGTH + KEY_LENGTH;
	
	protected static final int BASE_LENGTH_AS_PACK = Transaction.BASE_LENGTH_AS_PACK + SELF_LENGTH;
	protected static final int BASE_LENGTH = Transaction.BASE_LENGTH + SELF_LENGTH;

	public R_SetStatusToItem(byte[] typeBytes, PublicKeyAccount creator, byte feePow, long key, int itemType, long itemKey,
			Long beg_date, Long end_date,
			long value_1, long value_2, byte[] data_1, byte[] data_2, long ref_to_parent, byte[] description,
			long timestamp, Long reference) {
		super(typeBytes, NAME_ID, creator, feePow, timestamp, reference);		

		this.key = key;
		this.itemType = itemType;
		this.itemKey = itemKey;
		if (beg_date == null || beg_date == 0) beg_date = Long.MIN_VALUE;
		this.beg_date = beg_date;		
		if (end_date == null || end_date == 0) end_date = Long.MAX_VALUE;
		this.end_date = end_date;
		this.value_1 = value_1;
		this.value_2 = value_2;
		
		if (data_1 != null && data_1.length == 0)
			data_1 = null;
		this.data_1 = data_1;
		
		if (data_2 != null && data_2.length == 0)
			data_2 = null;
		this.data_2 = data_2;
		
		this.ref_to_parent = ref_to_parent;

		if (description != null && description.length == 0)
			description = null;
		this.description = description;
				
		// make parameters
		this.typeBytes[3] = (byte)(
					  (value_1 == 0?0:1)
					| (value_2 == 0?0:2)
					| (data_1 == null?0:4)
					| (data_2 == null?0:8)
					| (ref_to_parent == 0l?0:16)
					| (description == null?0:32)
					);
	}

	public R_SetStatusToItem(PublicKeyAccount creator, byte feePow, long key, int itemType, long itemKey,
			Long beg_date, Long end_date,
			long value_1, long value_2, byte[] data_1, byte[] data_2, long ref_to_parent, byte[] description,
			long timestamp, Long reference) {
		this(new byte[]{TYPE_ID, (byte)0, 0, 0}, creator, feePow, key, itemType, itemKey,
				beg_date, end_date,
				value_1, value_2, data_1, data_2, ref_to_parent, description,
				timestamp, reference);
	}
	// set default date
	public R_SetStatusToItem(PublicKeyAccount creator, byte feePow, long key, int itemType, long itemKey,
			long timestamp, Long reference) {
		this(new byte[]{TYPE_ID, (byte)0, 0, 0}, creator, feePow, key, itemType, itemKey,
				Long.MIN_VALUE, Long.MAX_VALUE, 0l, 0l, null, null, 0L, null, timestamp, reference);
	}
	public R_SetStatusToItem(byte[] typeBytes, PublicKeyAccount creator, byte feePow, long key, int itemType, long itemKey,
			Long beg_date, Long end_date,
			long value_1, long value_2, byte[] data_1, byte[] data_2, long ref_to_parent, byte[] description,
			long timestamp, Long reference, byte[] signature) {
		this(typeBytes, creator, feePow, key, itemType, itemKey,
				beg_date, end_date,
				value_1, value_2, data_1, data_2, ref_to_parent, description,
				timestamp, reference);
		this.signature = signature;
		this.calcFee();
	}
	// as pack
	public R_SetStatusToItem(byte[] typeBytes, PublicKeyAccount creator, long key, int itemType, long itemKey,
			Long beg_date, Long end_date,
			long value_1, long value_2, byte[] data_1, byte[] data_2, long ref_to_parent, byte[] description,
			byte[] signature) {
		this(typeBytes, creator, (byte)0, key, itemType, itemKey,
				beg_date, end_date,
				value_1, value_2, data_1, data_2, ref_to_parent, description,
				0l, null);
		this.signature = signature;
	}
	public R_SetStatusToItem(PublicKeyAccount creator, byte feePow, long key, int itemType, long itemKey,
			Long beg_date, Long end_date,
			long value_1, long value_2, byte[] data_1, byte[] data_2, long ref_to_parent, byte[] description,
			long timestamp, Long reference, byte[] signature) {
		this(new byte[]{TYPE_ID, (byte)0, 0, 0}, creator, feePow, key, itemType, itemKey,
				beg_date, end_date,
				value_1, value_2, data_1, data_2, ref_to_parent, description,
				timestamp, reference);
	}

	// as pack
	public R_SetStatusToItem(PublicKeyAccount creator, long key, int itemType, long itemKey,
			Long beg_date, Long end_date,
			long value_1, long value_2, byte[] data_1, byte[] data_2, long ref_to_parent, byte[] description,
			byte[] signature) {
		this(new byte[]{TYPE_ID, (byte)0, (byte)0, 0}, creator, (byte)0, key, itemType, itemKey,
				beg_date, end_date,
				value_1, value_2, data_1, data_2, ref_to_parent, description,
				0l, null);
	}
	
	//GETTERS/SETTERS

	//public static String getName() { return "Send"; }
	
	public long getKey()
	{
		return this.key;
	}

	public int getItemType()
	{
		return this.itemType;
	}
	public long getItemKey()
	{
		return this.itemKey;
	}

	public Long getBeginDate()
	{
		return this.beg_date;
	}

	public Long getEndDate()
	{
		return this.end_date;
	}
	public long getValue1()
	{
		return this.value_1;
	}
	public long getValue2()
	{
		return this.value_2;
	}
	public byte[] getData1()
	{
		return this.data_1;
	}
	public byte[] getData2()
	{
		return this.data_2;
	}
	public byte[] getDescription()
	{
		return this.description;
	}

	public boolean hasPublicText() {
		return true;
	}

	
	// pack values for DB
	public byte[] packData()
	{
		byte[] add_data = new byte[0];
		add_data = Bytes.concat(add_data, Longs.toByteArray(this.value_1));
		add_data = Bytes.concat(add_data, Longs.toByteArray(this.value_2));
		
		if (this.data_1 != null) {
			byte len1 = (byte)this.data_1.length;
			add_data = Bytes.concat(add_data, new byte[]{len1});
			add_data = Bytes.concat(add_data, data_1);
		} else {
			add_data = Bytes.concat(add_data, new byte[]{0});			
		}
		if (this.data_2 != null) {
			byte len2 = (byte)this.data_2.length;
			add_data = Bytes.concat(add_data, new byte[]{len2});
			add_data = Bytes.concat(add_data, data_2);
		} else {
			add_data = Bytes.concat(add_data, new byte[]{0});			
		}

		add_data = Bytes.concat(add_data, Longs.toByteArray(this.ref_to_parent));
		
		if (this.description != null && this.description.length > 0)
			add_data = Bytes.concat(add_data, this.description);
		
		return add_data;
	}

	// Unpack data from DB
	// value 1, value 2, data_1, data_2, parent_ref as (int, int), description, 
	public static Tuple6<Long, Long, byte[], byte[], Long, byte[]> unpackData(byte[] data_add)
	{
		
		if (data_add.length == 0) {
			// in Sertify Person = null
			return null;
		}
		
		int position = 0;
		
		byte[] value_1Bytes = Arrays.copyOfRange(data_add, position, position + VALUE_LENGTH);
		long value_1 = Longs.fromByteArray(value_1Bytes);
		position += VALUE_LENGTH;

		byte[] value_2Bytes = Arrays.copyOfRange(data_add, position, position + VALUE_LENGTH);
		long value_2 = Longs.fromByteArray(value_2Bytes);
		position += VALUE_LENGTH;

		//READ DATA 1 SIZE
		byte[] data_1SizeBytes = Arrays.copyOfRange(data_add, position, position + 1);
		int data_1Size = Byte.toUnsignedInt(data_1SizeBytes[0]);	
		position += 1;
		
		//READ ADDITIONAL DATA 1
		byte[] data_1 = null;
		if (data_1Size > 0) {
			data_1 = Arrays.copyOfRange(data_add, position, position + data_1Size);
			position += data_1Size;
		}

		//READ DATA 2 SIZE
		byte[] data_2SizeBytes = Arrays.copyOfRange(data_add, position, position + 1);
		int data_2Size = Byte.toUnsignedInt(data_2SizeBytes[0]);	
		position += 1;
		
		//READ ADDITIONAL DATA 2
		byte[] data_2 = null;
		if (data_2Size > 0) {
			data_2 = Arrays.copyOfRange(data_add, position, position + data_2Size);
			position += data_2Size;
		}

		// READ REFFERENCE TO PARENT RECORD
		byte[] ref_to_recordBytes = Arrays.copyOfRange(data_add, position, position + REF_LENGTH);
		long ref_to_parent = Longs.fromByteArray(ref_to_recordBytes);	
		position += REF_LENGTH;

		//READ ADDITIONAL DATA
		byte[] description = null;
		if (position > data_add.length)
			description = Arrays.copyOfRange(data_add, position, data_add.length);
		

		return new Tuple6<Long, Long, byte[], byte[], Long, byte[]>(
				value_1, value_2, data_1, data_2,
				ref_to_parent, description
				);
	}


	public long getRefParent()
	{
		return this.ref_to_parent;
	}
	
	// VIEWS
	public String viewRefParent()
	{
		byte[] bytes = Longs.toByteArray(this.ref_to_parent);
		int blockID = Ints.fromByteArray(Arrays.copyOfRange(bytes, 0, 4)); 
		int seqNo = Ints.fromByteArray(Arrays.copyOfRange(bytes, 4, 8));
		return blockID + "-" + seqNo;
	}

	
	@Override
	public String viewItemName() {
		ItemCls status = DBSet.getInstance().getItemStatusMap().get(this.key);
		return status==null?"null" : status.toString();
	}
	
	@Override
	public String viewAmount(String address) {
		return DateTimeFormat.timestamptoString(end_date);
	}
	
	@Override
	public String viewRecipient() {
		ItemCls item = ItemCls.getItem(DBSet.getInstance(), this.itemType, this.itemKey);
		return item==null?"null" : item.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJson() 
	{
		//GET BASE
		JSONObject transaction = this.getJsonBase();

		//ADD CREATOR/SERVICE/DATA
		transaction.put("key", this.key);
		transaction.put("itemType", this.itemType);
		transaction.put("itemKey", this.itemKey);
		transaction.put("begin_date", this.beg_date);
		transaction.put("end_date", this.end_date);
		
		if (this.value_1 != 0)
			transaction.put("value1", this.value_1);
		
		if (this.value_2 != 0)
			transaction.put("value2", this.value_2);
		
		if (this.data_1 != null)
			transaction.put("data1", new String(this.data_1, Charset.forName("UTF-8")));
		if (this.data_2 != null)
			transaction.put("data2", new String(this.data_2, Charset.forName("UTF-8")));
		if (this.description != null)
			transaction.put("description", new String(this.description, Charset.forName("UTF-8")));
		
		if (this.ref_to_parent != 0l)
			transaction.put("ref_parent", this.ref_to_parent);
		
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
		byte[] signature = Arrays.copyOfRange(data, position, position + SIGNATURE_LENGTH);
		position += SIGNATURE_LENGTH;

		//READ STATUS KEY
		byte[] keyBytes = Arrays.copyOfRange(data, position, position + KEY_LENGTH);
		long key = Longs.fromByteArray(keyBytes);	
		position += KEY_LENGTH;

		//READ ITEM
		// ITEM TYPE - ITEM_TYPE_LENGTH = 1
		Byte itemType = data[position];
		position ++;
		
		// ITEM KEY
		byte[] itemKeyBytes = Arrays.copyOfRange(data, position, position + KEY_LENGTH);
		long itemKey = Longs.fromByteArray(itemKeyBytes);	
		position += KEY_LENGTH;
		
		// READ BEGIN DATE
		byte[] beg_dateBytes = Arrays.copyOfRange(data, position, position + DATE_LENGTH);
		Long beg_date = Longs.fromByteArray(beg_dateBytes);	
		position += DATE_LENGTH;

		// READ END DATE
		byte[] end_dateBytes = Arrays.copyOfRange(data, position, position + DATE_LENGTH);
		Long end_date = Longs.fromByteArray(end_dateBytes);	
		position += DATE_LENGTH;

		long value_1 = 0l;
		if ( (typeBytes[3] & 1) > 0 ) {
			// READ VALUE 1
			byte[] value_1Bytes = Arrays.copyOfRange(data, position, position + VALUE_LENGTH);
			value_1 = Longs.fromByteArray(value_1Bytes);
			position += VALUE_LENGTH;
		}

		long value_2 = 0l;
		if ( (typeBytes[3] & 2) > 0 ) {
			// READ VALUE 2
			byte[] value_2Bytes = Arrays.copyOfRange(data, position, position + VALUE_LENGTH);
			value_2 = Longs.fromByteArray(value_2Bytes);
			position += VALUE_LENGTH;
		}

		byte[] data_1 = null;
		if ( (typeBytes[3] & 4) > 0 ) {
			//READ DATA SIZE
			byte[] data_1SizeBytes = Arrays.copyOfRange(data, position, position + 1);
			int data_1Size = Byte.toUnsignedInt(data_1SizeBytes[0]);	
			position += 1;
			
			//READ ADDITIONAL DATA 1
			data_1 = Arrays.copyOfRange(data, position, position + data_1Size);
			position += data_1Size;
		}

		byte[] data_2 = null;
		if ( (typeBytes[3] & 8) > 0 ) {
			//READ DATA SIZE
			byte[] data_2SizeBytes = Arrays.copyOfRange(data, position, position + 1);
			int data_2Size = Byte.toUnsignedInt(data_2SizeBytes[0]);	
			position += 1;
			
			//READ ADDITIONAL DATA 2
			data_2 = Arrays.copyOfRange(data, position, position + data_2Size);
			position += data_2Size;
		}

		long ref_to_parent = 0;
		if ( (typeBytes[3] & 16) > 0 ) {
			// READ REFFERENCE TO PARENT RECORD
			byte[] ref_to_recordBytes = Arrays.copyOfRange(data, position, position + REF_LENGTH);
			ref_to_parent = Longs.fromByteArray(ref_to_recordBytes);	
			position += REF_LENGTH;
		}

		byte[] additonalData = null;
		if ( (typeBytes[3] & 32) > 0 ) {
			//READ DATA SIZE
			//byte[] dataSizeBytes = Arrays.copyOfRange(data, position, position + DATA_SIZE_LENGTH);
			//int dataSize = Ints.fromByteArray(dataSizeBytes);	
			//position += DATA_SIZE_LENGTH;
			
			//READ ADDITIONAL DATA
			additonalData = Arrays.copyOfRange(data, position, data.length);
			//position += dataSize;
		}

		if (!asPack) {
			return new R_SetStatusToItem(typeBytes, creator, feePow, key, itemType, itemKey,
					beg_date, end_date, value_1, value_2, data_1, data_2, ref_to_parent, additonalData,
					timestamp, reference, signature);
		} else {
			return new R_SetStatusToItem(typeBytes, creator, key, itemType, itemKey,
					beg_date, end_date, value_1, value_2, data_1, data_2, ref_to_parent, additonalData,
					signature);
		}

	}

	//@Override
	public byte[] toBytes(boolean withSign, Long releaserReference) {

		byte[] data = super.toBytes(withSign, releaserReference);

		//WRITE STATUS KEY
		byte[] keyBytes = Longs.toByteArray(this.key);
		keyBytes = Bytes.ensureCapacity(keyBytes, KEY_LENGTH, 0);
		data = Bytes.concat(data, keyBytes);
		
		//WRITE ITEM KEYS
		// TYPE
		byte[] itemTypeKeyBytes = new byte[1];
		itemTypeKeyBytes[0] = (byte)this.itemType;
		data = Bytes.concat(data, itemTypeKeyBytes);
		// KEY
		byte[] itemKeyBytes = Longs.toByteArray(this.itemKey);
		keyBytes = Bytes.ensureCapacity(itemKeyBytes, KEY_LENGTH, 0);
		data = Bytes.concat(data, keyBytes);
		
		//WRITE BEGIN DATE
		data = Bytes.concat(data, Longs.toByteArray(this.beg_date));

		//WRITE END DATE
		data = Bytes.concat(data, Longs.toByteArray(this.end_date));

		// WRITE VALUE 1
		if (this.value_1 != 0) {
			data = Bytes.concat(data, Longs.toByteArray(this.value_1));
		}

		// WRITE VALUE 2
		if (this.value_2 != 0) {
			data = Bytes.concat(data, Longs.toByteArray(this.value_2));
		}

		if (this.data_1 != null) {
			//WRITE DATA 1 SIZE
			byte[] data_1SizeBytes = new byte[]{(byte)this.data_1.length};
			data = Bytes.concat(data, data_1SizeBytes);
	
			//WRITE DATA
			data = Bytes.concat(data, this.data_1);
		}
		if (this.data_2 != null) {
			//WRITE DATA 2 SIZE
			byte[] data_2SizeBytes = new byte[]{(byte)this.data_2.length};
			data = Bytes.concat(data, data_2SizeBytes);
	
			//WRITE DATA
			data = Bytes.concat(data, this.data_2);
		}

		//WRITE REFFERENCE TO PARENT
		if (this.ref_to_parent != 0l) {
			byte[] ref_to_parentBytes = Longs.toByteArray(this.ref_to_parent);
			ref_to_parentBytes = Bytes.ensureCapacity(ref_to_parentBytes, REF_LENGTH, 0);
			data = Bytes.concat(data, ref_to_parentBytes);
		}

		if (this.description != null) {
			//WRITE DATA SIZE
			//byte[] dataSizeBytes = Ints.toByteArray(this.description.length);
			//data = Bytes.concat(data, dataSizeBytes);
	
			//WRITE DATA
			data = Bytes.concat(data, this.description);
		}

		return data;
	}

	@Override
	public int getDataLength(boolean asPack)
	{
		// not include note reference
		int len = asPack? BASE_LENGTH_AS_PACK : BASE_LENGTH;
		len +=    (this.value_1 == 0? 0: VALUE_LENGTH)
				+ (this.value_2 == 0? 0: VALUE_LENGTH)
				+ (this.data_1 == null? 0: 1 + this.data_1.length)
				+ (this.data_2 == null? 0: 1 + this.data_2.length)
				+ (this.ref_to_parent == 0? 0: REF_LENGTH)
				+ (this.description == null? 0: this.description.length);
		return len;
	}

	//VALIDATE

	public int isValid(DBSet db, Long releaserReference) {
		
		int result = super.isValid(db, releaserReference);
		if (result != Transaction.VALIDATE_OK) {
			return result; 
		}

		if (this.data_1 != null ) {
			//CHECK DATA SIZE
			if(data_1.length > 255)
			{
				return INVALID_DATA_LENGTH;
			}
		}

		if (this.data_2 != null ) {
			//CHECK DATA SIZE
			if(data_2.length > 255)
			{
				return INVALID_DATA_LENGTH;
			}
		}

		if (this.description != null ) {
			//CHECK DATA SIZE
			if(description.length > BlockChain.MAX_REC_DATA_BYTES)
			{
				return INVALID_DATA_LENGTH;
			}
		}

		if ( !db.getItemStatusMap().contains(this.key) )
		{
			return Transaction.ITEM_STATUS_NOT_EXIST;
		}

		if (this.itemType != ItemCls.PERSON_TYPE
				&& this.itemType != ItemCls.ASSET_TYPE
				&& this.itemType != ItemCls.UNION_TYPE)
			return ITEM_DOES_NOT_STATUSED;

		ItemCls item = db.getItem_Map(this.itemType).get(this.itemKey);
		if ( item == null )
		{
			return Transaction.ITEM_DOES_NOT_EXIST;
		}
		
		if (this.ref_to_parent != 0l) {
			// TODO seek parent record
			byte[] bytes = Longs.toByteArray(this.ref_to_parent);
			int height = Ints.fromByteArray(Arrays.copyOfRange(bytes, 0, 4)); 
			int seqNo = Ints.fromByteArray(Arrays.copyOfRange(bytes, 4, 8));
			Transaction tx = db.getTransactionFinalMap().getTransaction(height, seqNo);
			if (tx == null )
				return INVALID_BLOCK_TRANS_SEQ_ERROR;
		}
		
		
		if (BlockChain.DEVELOP_USE) {
			for ( String admin: BlockChain.GENESIS_ADMINS) {
				if (this.creator.equals(admin)) {
					return VALIDATE_OK;
				}
			}
		} else {
			BigDecimal balERA = this.creator.getBalanceUSE(RIGHTS_KEY, db);
			if ( balERA.compareTo(BlockChain.MINOR_ERA_BALANCE_BD)<0 )
				return Transaction.NOT_ENOUGH_RIGHTS;
		}
		
		return Transaction.VALIDATE_OK;
	}

	//PROCESS/ORPHAN
	
	public void process(DBSet db, Block block, boolean asPack) {

		//UPDATE SENDER
		super.process(db, block, asPack);
		
		// pack additional data
		byte[] add_data = packData();

		Tuple2<Integer, Integer> heightSeqNo = this.getHeightSeqNo(db, block);
		
		Tuple5<Long, Long, byte[], Integer, Integer> itemP = 
				new Tuple5<Long, Long, byte[], Integer, Integer>
				(
					beg_date, end_date,
					add_data,
					heightSeqNo.a, heightSeqNo.b
				);

		StatusCls status = (StatusCls)db.getItemStatusMap().get(this.key);
		if (status.isUnique()) {	
			// SET STATUS of ITEM for DURATION
			// TODO set STATUSES by reference of it record - not by key!
			/// or add MAP by reference as signature - as IssueAsset - for orphans delete
			if (this.itemType == ItemCls.PERSON_TYPE)
				db.getPersonStatusMap().putItem(this.itemKey, this.key, itemP);
			else if (this.itemType == ItemCls.ASSET_TYPE)
				db.getAssetStatusMap().putItem(this.itemKey, this.key, itemP);
			else if (this.itemType == ItemCls.UNION_TYPE)
				db.getUnionStatusMap().putItem(this.itemKey, this.key, itemP);
		} else {
			if (this.itemType == ItemCls.PERSON_TYPE)
				db.getPersonStatusMap().addItem(this.itemKey, this.key, itemP);
			else if (this.itemType == ItemCls.ASSET_TYPE)
				db.getAssetStatusMap().addItem(this.itemKey, this.key, itemP);
			else if (this.itemType == ItemCls.UNION_TYPE)
				db.getUnionStatusMap().addItem(this.itemKey, this.key, itemP);
			
		}

	}

	public void orphan(DBSet db, boolean asPack) {

		//UPDATE SENDER
		super.orphan(db, asPack);
		
						
		// UNDO ALIVE PERSON for DURATION
		if (this.itemType == ItemCls.PERSON_TYPE)
			db.getPersonStatusMap().removeItem(this.itemKey, this.key);
		else if (this.itemType == ItemCls.ASSET_TYPE)
			db.getAssetStatusMap().removeItem(this.itemKey, this.key);
		else if (this.itemType == ItemCls.UNION_TYPE)
			db.getUnionStatusMap().removeItem(this.itemKey, this.key);

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

}