package gui.models;
import java.util.Observable;
import java.util.Observer;

import javax.validation.constraints.Null;

import org.apache.log4j.Logger;
import org.mapdb.Fun.Tuple2;

import utils.DateTimeFormat;
import utils.ObserverMessage;
import utils.Pair;
import controller.Controller;
import core.wallet.Wallet;
import database.DBSet;
import database.SortableList;
import database.wallet.BlockMap;
import lang.Lang;
import utill.Block;

@SuppressWarnings("serial")
public class WalletBlocksTableModel extends TableModelCls<Tuple2<String, String>, Block> implements Observer{

	public static final int COLUMN_HEIGHT = 0;
	public static final int COLUMN_TIMESTAMP = 1;
	public static final int COLUMN_GENERATOR = 2;
	public static final int COLUMN_BASETARGET = 3;
	public static final int COLUMN_TRANSACTIONS = 4;
	public static final int COLUMN_FEE = 5;
	
	private SortableList<Tuple2<String, String>, Block> blocks;
	
	private String[] columnNames = Lang.getInstance().translate(new String[]{"Height", "Timestamp", "Generator",
			"GB pH VW tVW", //"Generating Balance",
			"Transactions", "Fee"});
	private Boolean[] column_AutuHeight = new Boolean[]{false,true,true,false,true,false};
	
	static Logger LOGGER = Logger.getLogger(WalletBlocksTableModel.class.getName());

	public WalletBlocksTableModel()
	{
		Controller.getInstance().addWalletListener(this);
		blocks.registerObserver();
	}
	
	@Override
	public SortableList<Tuple2<String, String>, Block> getSortableList() {
		return this.blocks;
	}
	
	public Class<? extends Object> getColumnClass(int c) {     // set column type
		Object o = getValueAt(0, c);
		return o==null?Null.class:o.getClass();
    }
	
	// читаем колонки которые изменяем высоту	   
		public Boolean[] get_Column_AutoHeight(){
			
			return this.column_AutuHeight;
		}
	// устанавливаем колонки которым изменить высоту	
		public void set_get_Column_AutoHeight( Boolean[] arg0){
			this.column_AutuHeight = arg0;	
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
		if(blocks == null)
		{
			return 0;
		}
		
		return blocks.size();
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		try 
		{
			if(this.blocks == null || this.blocks.size() -1 < row)
			{
				return null;
			}
			
			//
			Pair<Tuple2<String, String>, Block> data = this.blocks.get(row);
			
			if (data == null || data.getB() == null) {
				return -1;
			}

			Block block = data.getB();
			if (block == null)
				return null;
			
			switch(column)
			{
			case COLUMN_HEIGHT:
				
				return block.getHeight(DBSet.getInstance());
				
			case COLUMN_TIMESTAMP:
				
				return DateTimeFormat.timestamptoString(block.getTimestamp(DBSet.getInstance())); // + " " + block.getTimestamp(DBSet.getInstance()) / 1000;
				
			case COLUMN_GENERATOR:
				
				return block.getCreator().getPersonAsString();
				
			case COLUMN_BASETARGET:

				long iii = block.calcWinValueTargeted(DBSet.getInstance());
				return block.getGeneratingBalance(DBSet.getInstance()) + " "
					+ iii;
				
			case COLUMN_TRANSACTIONS:
				
				return block.getTransactionCount();
				
			case COLUMN_FEE:	
				
				return block.getTotalFee().toPlainString();
				
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage() +  " row:" + row, e);
		}
		
		return null;
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
		if(message.getType() == ObserverMessage.LIST_BLOCK_TYPE)
		{
			if(this.blocks == null)
			{
				this.blocks = (SortableList<Tuple2<String, String>, Block>) message.getValue();
				this.blocks.registerObserver();
				this.blocks.sort(BlockMap.TIMESTAMP_INDEX, true);
			}
			
			this.fireTableDataChanged();
		}
		
		//CHECK IF LIST UPDATED
		if(message.getType() == ObserverMessage.ADD_BLOCK_TYPE || message.getType() == ObserverMessage.REMOVE_BLOCK_TYPE)
		{
			this.fireTableDataChanged();
		}	
	}
}
