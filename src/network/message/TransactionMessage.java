package network.message;

import com.google.common.primitives.Bytes;

import core.transaction.TransactionFactory;
import utill.Transaction;

public class TransactionMessage extends Message{

	private Transaction transaction;
	
	public TransactionMessage(Transaction transaction)
	{
		super(TRANSACTION_TYPE);	
		
		this.transaction = transaction;
	}
	
	public Transaction getTransaction()
	{
		return this.transaction;
	}
	
	public static TransactionMessage parse(byte[] data) throws Exception
	{
		//PARSE TRANSACTION
		Transaction transaction = TransactionFactory.getInstance().parse(data, null);
		
		return new TransactionMessage(transaction);
	}
	
	public byte[] toBytes() 
	{
		byte[] data = new byte[0];
		
		//WRITE BLOCK
		byte[] blockBytes = this.transaction.toBytes(true, null);
		data = Bytes.concat(data, blockBytes);
		
		//ADD CHECKSUM
		data = Bytes.concat(super.toBytes(), this.generateChecksum(data), data);
		
		return data;
	}	
	
	protected int getDataLength()
	{
		return this.transaction.getDataLength(false);
	}
	
}
