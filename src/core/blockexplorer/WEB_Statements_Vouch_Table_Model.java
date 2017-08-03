package core.blockexplorer;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

import javax.swing.table.AbstractTableModel;

import org.json.simple.JSONArray;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple5;

import controller.Controller;
import core.account.Account;
import core.account.PublicKeyAccount;
import core.item.assets.AssetCls;
import core.transaction.R_SignNote;
//import core.transaction.R_SignStatement_old;
import core.transaction.R_Vouch;
import database.DBSet;
import database.SortableList;
import database.TransactionFinalMap;
import lang.Lang;
import utill.Block;
import utill.BlockChain;
import utill.DBMap;
import utill.GenesisBlock;
import utill.Transaction;
import utils.ObserverMessage;
import utils.Pair;

public class WEB_Statements_Vouch_Table_Model extends AbstractTableModel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int COLUMN_TIMESTAMP = 0;
	// public static final int COLUMN_TYPE = 1;
	public static final int COLUMN_CREATOR = 1;
	 public static final int COLUMN_CREATOR_ADDRESS = 2;
	public static final int COLUMN_TRANSACTION = 3;
	// public static final int COLUMN_FEE = 3;
	List<Transaction> transactions;

	// private SortableList<byte[], Transaction> transactions;

	private String[] columnNames = Lang.getInstance().translate(new String[] { "Timestamp", "Creator", "Address_Creator" , "Transaction"});// ,
																											// AssetCls.FEE_NAME});
	private Boolean[] column_AutuHeight = new Boolean[] { true, true };
	// private Map<byte[], BlockingQueue<Block>> blocks;
	// private Transaction transaction;
	private int blockNo;
	private int recNo;

	TransactionFinalMap table;

	private ObserverMessage message;

	private String sss;

	public WEB_Statements_Vouch_Table_Model(Transaction transaction) {
		table = DBSet.getInstance().getTransactionFinalMap();
		blockNo = transaction.getBlockHeight(DBSet.getInstance());
		recNo = transaction.getSeqNo(DBSet.getInstance());
		transactions = new ArrayList<Transaction>();
		// transactions = read_Sign_Accoutns();
		DBSet.getInstance().getTransactionFinalMap().addObserver(this);
		DBSet.getInstance().getVouchRecordMap().addObserver(this);

	}

	public Class<? extends Object> getColumnClass(int c) { // set column type
		Object o = getValueAt(0, c);
		return o == null ? null : o.getClass();
	}

	
	// читаем колонки которые изменяем высоту
	public Boolean[] get_Column_AutoHeight() {

		return this.column_AutuHeight;
	}

	// устанавливаем колонки которым изменить высоту
	public void set_get_Column_AutoHeight(Boolean[] arg0) {
		this.column_AutuHeight = arg0;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return this.columnNames.length;
	}

	@Override
	public String getColumnName(int index) {
		return this.columnNames[index];
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		if (transactions == null)
			return 0;
		int c = 0;
		for (Transaction a : this.transactions) {
			if (a != null)
				++c;
		}
		return c; // transactions.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		try {
			if (this.transactions == null || this.transactions.size() - 1 < row) {
				return null;
			}

			Transaction transaction = this.transactions.get(row);

			// R_Vouch i;
			switch (column) {
			case COLUMN_TIMESTAMP:

				// return
				// DateTimeFormat.timestamptoString(transaction.getTimestamp())
				// + " " + transaction.getTimestamp();
				return transaction.viewTimestamp(); // + " " +
													// transaction.getTimestamp()
													// / 1000;

			case COLUMN_CREATOR_ADDRESS:
				
				
				return transaction.getCreator().getAddress().toString();
			case COLUMN_CREATOR:

				return transaction.getCreator().getPerson().b;
				
			case COLUMN_TRANSACTION:
				
				return transaction;
			
			}

			return null;

		} catch (Exception e) {
			// LOGGER.error(e.getMessage(),e);
			return null;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// try
		// {
		this.syncUpdate(o, arg);
		// }
		// catch(Exception e)
		// {
		// GUI ERROR
		// }
	}

	public synchronized void syncUpdate(Observable o, Object arg) {
		message = (ObserverMessage) arg;
		// System.out.println( message.getType());

		// CHECK IF NEW LIST
		if (message.getType() == ObserverMessage.LIST_VOUCH_TYPE) {
			if (this.transactions.size() == 0) {
				transactions = read_Sign_Accoutns();
				this.fireTableDataChanged();
			}

		}

		// CHECK IF LIST UPDATED
		if (message.getType() == ObserverMessage.ADD_TRANSACTION_TYPE
		// || message.getType() == ObserverMessage.REMOVE_VOUCH_TYPE
		// || message.getType() == ObserverMessage.LIST_STATEMENT_TYPE
		// || message.getType() == ObserverMessage.REMOVE_STATEMENT_TYPE

		) {
			Transaction ss = (Transaction) message.getValue();
			if (ss.getType() == Transaction.VOUCH_TRANSACTION) {
				R_Vouch ss1 = (R_Vouch) ss;
				if (ss1.getVouchHeight() == blockNo
						&& ss1.getVouchSeq() == recNo
						)

					if (!this.transactions.contains(ss)){
						this.transactions.add(ss);
						this.fireTableDataChanged();
					}
			}

			
		}
	}

	private List<Transaction> read_Sign_Accoutns() {
		List<Transaction> tran = new ArrayList<Transaction>();
		// ArrayList<Transaction> db_transactions;
		// db_transactions = new ArrayList<Transaction>();
		// tran = new ArrayList<Transaction>();
		// база данных
		// DBSet dbSet = DBSet.getInstance();

		/*
		 * Tuple2<BigDecimal, List<Tuple2<Integer, Integer>>> signs =
		 * DBSet.getInstance().getVouchRecordMap().get(blockNo, recNo);
		 * 
		 * 
		 * if (signs == null) return null; for(Tuple2<Integer, Integer> seq:
		 * signs.b) {
		 * 
		 * Transaction kk = table.getTransaction(seq.a, seq.b); if
		 * (!tran.contains(kk)) tran.add(kk); }
		 */

		@SuppressWarnings("unchecked")
		SortableList<Tuple2<Integer, Integer>, Tuple2<BigDecimal, List<Tuple2<Integer, Integer>>>> rec = (SortableList<Tuple2<Integer, Integer>, Tuple2<BigDecimal, List<Tuple2<Integer, Integer>>>>) message
				.getValue();

		Iterator<Pair<Tuple2<Integer, Integer>, Tuple2<BigDecimal, List<Tuple2<Integer, Integer>>>>> ss = rec
				.iterator();
		while (ss.hasNext()) {
			Pair<Tuple2<Integer, Integer>, Tuple2<BigDecimal, List<Tuple2<Integer, Integer>>>> a = (Pair<Tuple2<Integer, Integer>, Tuple2<BigDecimal, List<Tuple2<Integer, Integer>>>>) ss
					.next();
			// block
			if (a.getA().a == blockNo && a.getA().b == recNo) {
				List<Tuple2<Integer, Integer>> ff = a.getB().b;

				for (Tuple2<Integer, Integer> ll : ff) {
					Integer bl = ll.a;
					Integer seg = ll.b;

					Transaction kk = table.getTransaction(bl, seg);
					if (!tran.contains(kk))
						tran.add(kk);
				}
			}

		}
		return tran;
	}

}
