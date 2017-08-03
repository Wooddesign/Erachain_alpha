package gui.models;
////////
import java.util.Observable;
import java.util.Observer;

import org.mapdb.Fun.Tuple2;

import utils.ObserverMessage;
import controller.Controller;
import core.item.notes.NoteCls;
import database.DBSet;
import database.SortableList;
import lang.Lang;

@SuppressWarnings("serial")
public class WalletItemNotesTableModel extends TableModelCls<Tuple2<String, String>, NoteCls> implements Observer
{
	public static final int COLUMN_KEY = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_ADDRESS = 2;
	public static final int COLUMN_CONFIRMED = 3;
	public static final int COLUMN_FAVORITE = 4;
	
	private SortableList<Tuple2<String, String>, NoteCls> notes;
	
	private String[] columnNames = Lang.getInstance().translate(new String[]{"Key", "Name", "Owner", "Confirmed", "Favorite"});
	
	public WalletItemNotesTableModel()
	{
		Controller.getInstance().addWalletListener(this);
	}
	
	@Override
	public SortableList<Tuple2<String, String>, NoteCls> getSortableList() {
		return this.notes;
	}
	
	public NoteCls getItem(int row)
	{
		return this.notes.get(row).getB();
	}
	
	@Override
	public int getColumnCount() 
	{
		return this.columnNames.length;
	}
	
	@Override
	public String getColumnName(int index) 
	{
		return this.columnNames[index];
	}

	@Override
	public int getRowCount() 
	{
		 return this.notes.size();
	}

	@Override
	public Object getValueAt(int row, int column) 
	{
		if(this.notes == null || row > this.notes.size() - 1 )
		{
			return null;
		}
		
		NoteCls note = this.notes.get(row).getB();
		
		switch(column)
		{
		case COLUMN_KEY:
			
			return note.getKey(DBSet.getInstance());
		
		case COLUMN_NAME:
			
			return note.getName();
		
		case COLUMN_ADDRESS:
			
			return note.getOwner().getPersonAsString();
						
		case COLUMN_CONFIRMED:
			
			return note.isConfirmed();
			
		case COLUMN_FAVORITE:
			
			return note.isFavorite();
			
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
		if(message.getType() == ObserverMessage.LIST_NOTE_TYPE)
		{
			if(this.notes == null)
			{
				this.notes = (SortableList<Tuple2<String, String>, NoteCls>) message.getValue();
				this.notes.registerObserver();
				//this.notes.sort(PollMap.NAME_INDEX);
			}
			
			this.fireTableDataChanged();
		}
		
		//CHECK IF LIST UPDATED
		if(message.getType() == ObserverMessage.ADD_NOTE_TYPE || message.getType() == ObserverMessage.REMOVE_NOTE_TYPE)
		{
			this.fireTableDataChanged();
		}	
	}
}
