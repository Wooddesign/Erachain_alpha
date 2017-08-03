package gui.items.unions;

import java.util.Observable;
import java.util.Observer;

import javax.validation.constraints.Null;

import controller.Controller;
import core.item.unions.UnionCls;
import utils.NumberAsString;
import utils.ObserverMessage;
import database.DBSet;
import database.SortableList;
import gui.models.TableModelCls;
import lang.Lang;

@SuppressWarnings("serial")
public class TableModelUnions extends TableModelCls<Long, UnionCls> implements Observer
{
	public static final int COLUMN_KEY = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_ADDRESS = 2;
	public static final int COLUMN_FAVORITE = 3;

	private SortableList<Long, UnionCls> unions;
	
	private String[] columnNames = Lang.getInstance().translate(new String[]{"Key", "Name", "Creator", "Favorite"});
	private Boolean[] column_AutuHeight = new Boolean[]{false,true,true,false};
	
	public TableModelUnions()
	{
		Controller.getInstance().addObserver(this);
	}
	
	@Override
	public SortableList<Long, UnionCls> getSortableList() 
	{
		return this.unions;
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
		
	
	public UnionCls getUnion(int row)
	{
		return this.unions.get(row).getB();
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
		return this.unions.size();
		
	}

	@Override
	public Object getValueAt(int row, int column) 
	{
		if(this.unions == null || row > this.unions.size() - 1 )
		{
			return null;
		}
		
		UnionCls union = this.unions.get(row).getB();
		
		switch(column)
		{
		case COLUMN_KEY:
			
			return union.getKey(DBSet.getInstance());
		
		case COLUMN_NAME:
			
			return union.getName();
		
		case COLUMN_ADDRESS:
			
			return union.getOwner().getPersonAsString();
			
		case COLUMN_FAVORITE:
			
			return union.isFavorite();

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
		if(message.getType() == ObserverMessage.LIST_UNION_TYPE)
		{			
			if(this.unions == null)
			{
				this.unions = (SortableList<Long, UnionCls>) message.getValue();
				this.unions.addFilterField("name");
				this.unions.registerObserver();
			}	
			
			this.fireTableDataChanged();
		}
		
		//CHECK IF LIST UPDATED
		if(message.getType() == ObserverMessage.ADD_UNION_TYPE || message.getType() == ObserverMessage.REMOVE_UNION_TYPE)
		{
			this.fireTableDataChanged();
		}
	}
	
	public void removeObservers() 
	{
		this.unions.removeObserver();
		Controller.getInstance().deleteObserver(this);
	}
}
