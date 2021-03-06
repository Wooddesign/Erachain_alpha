package core.transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import ntp.NTP;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import core.account.Account;
import core.account.PublicKeyAccount;
import core.crypto.Crypto;
import core.item.assets.AssetCls;
import core.payment.Payment;
import database.ItemAssetBalanceMap;
import utill.Block;
import utill.Transaction;
import database.DBSet;

public class MultiPaymentTransaction extends Transaction {

	private static final byte TYPE_ID = (byte)Transaction.MULTI_PAYMENT_TRANSACTION;
	private static final String NAME_ID = "Multi Send";
	private static final int PAYMENTS_SIZE_LENGTH = 4;
	private static final int BASE_LENGTH = TransactionAmount.BASE_LENGTH + PAYMENTS_SIZE_LENGTH;

	private List<Payment> payments;
	
	public MultiPaymentTransaction(byte[] typeBytes, PublicKeyAccount creator, List<Payment> payments, byte feePow, long timestamp, Long reference) 
	{
		super(typeBytes, NAME_ID, creator, feePow, timestamp, reference);		
		this.payments = payments;
	}
	public MultiPaymentTransaction(byte[] typeBytes, PublicKeyAccount creator, List<Payment> payments, byte feePow, long timestamp, Long reference, byte[] signature) 
	{
		this(typeBytes, creator, payments, feePow, timestamp, reference);
		this.signature = signature;
		this.calcFee();
	}
	// as pack
	public MultiPaymentTransaction(byte[] typeBytes, PublicKeyAccount creator, List<Payment> payments, Long reference, byte[] signature) 
	{
		this(typeBytes, creator, payments, (byte)0, 0l, reference);
		this.signature = signature;
	}
	public MultiPaymentTransaction(PublicKeyAccount creator, List<Payment> payments, byte feePow, long timestamp, Long reference) 
	{
		this(new byte[]{TYPE_ID, 0, 0, 0}, creator, payments, feePow, timestamp, reference);
	}
	public MultiPaymentTransaction(PublicKeyAccount creator, List<Payment> payments, Long reference)
	{
		this(new byte[]{TYPE_ID, 0, 0, 0}, creator, payments, (byte)0, 0l, reference);		
	}
	
	//GETTERS/SETTERS

	//public static String getName() { return "Multi Send"; }

	public List<Payment> getPayments()
	{
		return this.payments;
	}
	
	public boolean hasPublicText() {
		return true;
	}

	//PARSE/CONVERT
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJson() 
	{
		//GET BASE
		JSONObject transaction = this.getJsonBase();
				
		//ADD CREATOR/PAYMENTS
		transaction.put("creator", this.creator.getAddress());
		
		JSONArray payments = new JSONArray();
		for(Payment payment: this.payments)
		{
			payments.add(payment.toJson());
		}
		transaction.put("payments", payments);
				
		return transaction;	
	}
		
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

		/////				
		//READ PAYMENTS SIZE
		byte[] paymentsLengthBytes = Arrays.copyOfRange(data, position, position + PAYMENTS_SIZE_LENGTH);
		int paymentsLength = Ints.fromByteArray(paymentsLengthBytes);
		position += PAYMENTS_SIZE_LENGTH;
		
		if(paymentsLength < 1 || paymentsLength > 400)
		{
			throw new Exception("Invalid payments length");
		}
		
		//READ PAYMENTS
		List<Payment> payments = new ArrayList<Payment>();
		for(int i=0; i<paymentsLength; i++)
		{
			Payment payment = Payment.parse(Arrays.copyOfRange(data, position, position + Payment.BASE_LENGTH));
			payments.add(payment);
			
			position += Payment.BASE_LENGTH;
		}

		if (!asPack) {
			return new MultiPaymentTransaction(typeBytes, creator, payments, feePow, timestamp, reference, signatureBytes);	
		} else {
			return new MultiPaymentTransaction(typeBytes, creator, payments, reference, signatureBytes);	
		}

	}	
	

	//@Override
	public byte[] toBytes(boolean withSign, Long releaserReference) 
	{

		byte[] data = super.toBytes(withSign, releaserReference);

		//WRITE PAYMENTS SIZE
		int paymentsLength = this.payments.size();
		byte[] paymentsLengthBytes = Ints.toByteArray(paymentsLength);
		data = Bytes.concat(data, paymentsLengthBytes);
		
		//WRITE PAYMENTS
		for(Payment payment: this.payments)
		{
			data = Bytes.concat(data, payment.toBytes());
		}
		
		return data;
	}

	@Override
	public int getDataLength(boolean asPack) 
	{
		int paymentsLength = 0;
		for(Payment payment: this.getPayments())
		{
			paymentsLength += payment.getDataLength();
		}
		
		if (asPack) {
			return BASE_LENGTH_AS_PACK + paymentsLength;
		} else {
			return BASE_LENGTH + paymentsLength;
		}
	}
	
	//VALIDATE
	
	//@Override
	public int isValid(DBSet db, Long releaserReference) 
	{
		
		//CHECK PAYMENTS SIZE
		if(this.payments.size() < 1 || this.payments.size() > 400)
		{
			return INVALID_PAYMENTS_LENGTH;
		}
		
		//REMOVE FEE
		DBSet fork = db.fork();
		//this.creator.setBalance(FEE_KEY, this.creator.getBalance(fork, FEE_KEY).subtract(this.fee), fork);
		this.creator.changeBalance(fork, true, FEE_KEY, this.fee);
		
		//CHECK IF CREATOR HAS ENOUGH FEE BALANCE
		if(this.creator.getBalance(fork, FEE_KEY).a.compareTo(BigDecimal.ZERO) == -1)
		{
			return NO_BALANCE;
		}	
		
		//CHECK PAYMENTS
		for(Payment payment: this.payments)
		{	
			//CHECK IF RECIPIENT IS VALID ADDRESS
			if(!Crypto.getInstance().isValidAddress(payment.getRecipient().getAddress()))
			{
				return INVALID_ADDRESS;
			}
			
			//CHECK IF AMOUNT IS POSITIVE
			if(payment.getAmount().compareTo(BigDecimal.ZERO) <= 0)
			{
				return NEGATIVE_AMOUNT;
			}
			
			//CHECK IF CREATOR HAS ENOUGH ASSET BALANCE
			if(this.creator.getBalance(fork, payment.getAsset()).a.compareTo(payment.getAmount()) == -1)
			{
				return NO_BALANCE;
			}
			
			//CHECK IF AMOUNT IS DIVISIBLE
			AssetCls aa = (AssetCls)db.getItemAssetMap().get(payment.getAsset()); 
			if(!aa.isDivisible())
			{
				//CHECK IF AMOUNT DOES NOT HAVE ANY DECIMALS
				if(payment.getAmount().stripTrailingZeros().scale() > 0)
				{
					//AMOUNT HAS DECIMALS
					return INVALID_AMOUNT;
				}
			}
			
			//PROCESS PAYMENT IN FORK
			payment.process(this.creator, fork);
		}
		
		return super.isValid(db, releaserReference);
	}

	//PROCESS/ORPHAN
	
	//@Override
	public void process(DBSet db, Block block, boolean asPack) 
	{
		//UPDATE CREATOR
		super.process(db, block, asPack);
								
		//PROCESS PAYMENTS
		for(Payment payment: this.payments)
		{
			payment.process(this.creator, db);
			
			//UPDATE REFERENCE OF RECIPIENT
			if(payment.getRecipient().getLastReference(db) == null)
			{
				payment.getRecipient().setLastReference(this.timestamp, db);
			}		
		}
	}

	//@Override
	public void orphan(DBSet db, boolean asPack) 
	{
		//UPDATE CREATOR
		super.orphan(db, asPack);
		
		//ORPHAN PAYMENTS
		for(Payment payment: this.payments)
		{
			payment.orphan(this.creator, db);
								
			//UPDATE REFERENCE OF RECIPIENT
			if(payment.getRecipient().getLastReference(db).equals(this.timestamp))
			{
				payment.getRecipient().removeReference(db);
			}
		}
	}

	//REST
	
	@Override
	public HashSet<Account> getInvolvedAccounts()
	{
		HashSet<Account> accounts = new HashSet<Account>();
		accounts.add(this.creator);
		accounts.addAll(this.getRecipientAccounts());
		return accounts;
	}
	
	@Override
	public HashSet<Account> getRecipientAccounts()
	{
		HashSet<Account> accounts = new HashSet<>();
		
		for(Payment payment: this.payments)
		{
			accounts.add(payment.getRecipient());
		}
		
		return accounts;
	}
	
	@Override
	public boolean isInvolved(Account account) 
	{
		String address = account.getAddress();
		
		for(Account involved: this.getInvolvedAccounts())
		{
			if(address.equals(involved.getAddress()))
			{
				return true;
			}
		}
		
		return false;
	}

	//@Override
	public BigDecimal getAmount(Account account) 
	{
		BigDecimal amount = BigDecimal.ZERO.setScale(8);
		String address = account.getAddress();
		
		//IF CREATOR
		if(address.equals(this.creator.getAddress()))
		{
			amount = amount.subtract(this.fee);
		}

		//CHECK PAYMENTS
		for(Payment payment: this.payments)
		{
			//IF FEE ASSET
			if(payment.getAsset() == FEE_KEY)
			{
				//IF CREATOR
				if(address.equals(this.creator.getAddress()))
				{
					amount = amount.subtract(payment.getAmount());
				}
				
				//IF RECIPIENT
				if(address.equals(payment.getRecipient().getAddress()))
				{
					amount = amount.add(payment.getAmount());
				}
			}
		}
		
		return amount;
	}

	public Map<String, Map<Long, BigDecimal>> getAssetAmount() 
	{
		Map<String, Map<Long, BigDecimal>> assetAmount = new LinkedHashMap<>();
		
		assetAmount = subAssetAmount(assetAmount, this.creator.getAddress(), FEE_KEY, this.fee);
		
		for(Payment payment: this.payments)
		{
			assetAmount = subAssetAmount(assetAmount, this.creator.getAddress(), payment.getAsset(), payment.getAmount());
			assetAmount = addAssetAmount(assetAmount, payment.getRecipient().getAddress(), payment.getAsset(), payment.getAmount());
		}
		
		return assetAmount;
	}
	public int calcBaseFee() {
		return calcCommonFee();
	}
	
}