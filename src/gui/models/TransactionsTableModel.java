package gui.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.validation.constraints.Null;

import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.Fun.Tuple2;

import utils.DateTimeFormat;
import utils.NumberAsString;
import utils.ObserverMessage;
import utils.Pair;
import controller.Controller;
import core.account.Account;
import core.item.assets.AssetCls;
import core.transaction.GenesisTransferAssetTransaction;
import database.DBSet;
import database.SortableList;
import database.TransactionFinalMap;
import database.TransactionMap;
import lang.Lang;
import utill.Block;
import utill.Transaction;

@SuppressWarnings("serial")
// IN gui.DebugTabPane used
public class TransactionsTableModel extends TableModelCls<byte[], Transaction> implements Observer {
	
	//public static final int COLUMN_NO = 0;
	public static final int COLUMN_TIMESTAMP = 0;
	public static final int COLUMN_TYPE = 1;
	public static final int COLUMN_AMOUNT = 2;
	public static final int COLUMN_FEE = 3;
	
	//private SortableList<byte[], Transaction> transactions;
	private Integer blockNo = null;
	Long block_Height;
	Integer block_No;
	List<Transaction> transactions;
	private String[] columnNames = Lang.getInstance().translate(new String[]{ "Timestamp", "Type", "Amount", AssetCls.FEE_NAME});
	private String ac;

	static Logger LOGGER = Logger.getLogger(TransactionsTableModel.class.getName());

	public TransactionsTableModel()
	{
		Controller.getInstance().addObserver(this);

	}
	
	public Class<? extends Object> getColumnClass(int c)
	{     // set column type
		Object o = getValueAt(0, c);
		return o==null?Null.class:o.getClass();
    }
	
	public void setBlockNumber(String string){
		
		// byte[] block_key = DBSet.getInstance().getBlockHeightsMap().get(Long.parseLong(string));
		// Block block = DBSet.getInstance().getBlockMap().get(block_key);
		// transactions = block.getTransactions();	
		
		try {
			block_No = Integer.parseInt(string);
		} catch (NumberFormatException e) {
			transactions = new ArrayList<>();
			Transaction transaction = core.transaction.R_Vouch.getVouchingRecord(DBSet.getInstance(), string);
			if (transaction != null) {
				transactions.add(transaction);
			}
			this.fireTableDataChanged();
			return;
		}
		
		transactions = DBSet.getInstance().getTransactionFinalMap().getTransactionsByBlock(block_No);
		this.fireTableDataChanged();
		
	}

	public void Find_Transactions_from_Address(String address){
		String sender;
		String recipient;
		int minHeight;
		int maxHeight;
		int type;
		int service;
		boolean desc;
		int offset;
		int limit;
		
		if (address == null || address.equals(""))return;
		Tuple2<Account, String> accountResult = Account.tryMakeAccount(address);
		Account account = accountResult.a;

		
		
		if (account != null) {
			transactions = new ArrayList();
			// read Genessis block
			List<Transaction> genesisTransactions = new ArrayList();
			genesisTransactions.addAll(DBSet.getInstance().getTransactionFinalMap().getTransactionsByBlock(1));
			int k =0;
			for (Transaction gT:genesisTransactions){
				if(gT.getType() ==Transaction.GENESIS_SEND_ASSET_TRANSACTION){
					GenesisTransferAssetTransaction assetTransfer = (GenesisTransferAssetTransaction) gT;
					if(assetTransfer.getOwner()!=null){
						Account ow = assetTransfer.getOwner();
						ac = assetTransfer.getOwner().getAddress();
		//				System.out.print("\n k="+ k + "ac="+ac);
					if (ac.equals(address)) {
						transactions.add(gT);
					}
					}
					
				}
				k++;
			}
			
			
			transactions.addAll(DBSet.getInstance().getTransactionFinalMap().getTransactionsByAddress(account.getAddress()));//.findTransactions(address, sender=address, recipient=address, minHeight=0, maxHeight=0, type=0, service=0, desc=false, offset=0, limit=0);//.getTransactionsByBlock(block_No);
		

		this.fireTableDataChanged();
		} else {
			;
		}
		
	
	}
	
	
	
	
	public void view_Transactioms_From_Adress(String str){
		
		transactions = DBSet.getInstance().getTransactionFinalMap().getTransactionsByAddress(str);
		 this.fireTableDataChanged();	
		
		
	}
	
	public void view_Clear(){
		transactions.clear();
		 this.fireTableDataChanged();	
		
		
		
	}
	
	
	public Transaction getTransaction(int row)
	{
		Transaction data = transactions.get(row);
		if (data == null ) {
			return null;
		}
		return data;
	}
	
	public List<Transaction> getTransactions()
	{
		return transactions;
	}
	
	
	@Override
	public int getColumnCount() 
	{
		return columnNames.length;
	}
	
	@Override
	public String getColumnName(int index) 
	{
		return columnNames[index];
	}

	@Override
	public int getRowCount() 
	{
		if(this.transactions == null)
		{
			return 0;
		}
		
		return this.transactions.size();
	}

	@Override
	public Object getValueAt(int row, int column) 
	{
		try
		{
			if(this.transactions == null || this.transactions.size() -1 < row)
			{
				return null;
			}
			
			Transaction transaction = transactions.get(row);
			if (transaction == null)  {
				return null;
			}

			
			switch(column)
			{
			case COLUMN_TIMESTAMP:
				
				return DateTimeFormat.timestamptoString(transaction.getTimestamp());
				//return transaction.viewTimestamp() + " " + transaction.getTimestamp() / 1000;
				
			case COLUMN_TYPE:
				
				//return Lang.transactionTypes[transaction.getType()];
				return Lang.getInstance().translate(transaction.viewTypeName());
				
			case COLUMN_AMOUNT:
				
				return transaction.getAmount();//.getAmount(transaction.getCreator()));
				
			case COLUMN_FEE:
				
				return transaction.getFee();		
			
	//		case COLUMN_NO:
	//			return row;
			}
		
			
			return null;
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			return null;
		}
	}

	@Override
	public void update(Observable o, Object arg) 
	{
		try
		{
			this.syncUpdate(o, arg);
		}
		catch(Exception e)
		{
			//GUI ERROR
		}
	}
	
	@SuppressWarnings("unchecked")	
	public synchronized void syncUpdate(Observable o, Object arg)
	{
		ObserverMessage message = (ObserverMessage) arg;
		
		//CHECK IF NEW LIST
		if(message.getType() == ObserverMessage.LIST_TRANSACTION_TYPE)
		{
			if(this.transactions == null)
			{
			//	this.transactions = (SortableList<byte[], Transaction>) message.getValue();
			//	this.transactions.registerObserver();
			//	this.transactions.sort(TransactionMap.TIMESTAMP_INDEX, true);
			}
			
			this.fireTableDataChanged();
		}
		
		//CHECK IF LIST UPDATED
		if(message.getType() == ObserverMessage.ADD_TRANSACTION_TYPE || message.getType() == ObserverMessage.REMOVE_TRANSACTION_TYPE)
		{
			this.fireTableDataChanged();
		}	
	}

	public void removeObservers() 
	{
	//	this.transactions.removeObserver();
		Controller.getInstance().deleteObserver(this);		
	}

	@Override
	public SortableList<byte[], Transaction> getSortableList() {
		// TODO Auto-generated method stub
		return null;
	}
}
