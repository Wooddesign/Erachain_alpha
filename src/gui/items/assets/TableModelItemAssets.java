package gui.items.assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.validation.constraints.Null;

import controller.Controller;
import core.item.ItemCls;
import core.item.assets.AssetCls;
import core.item.persons.PersonCls;
import database.DBSet;
import database.ItemAssetMap;
import database.SortableList;
import gui.models.TableModelCls;
import utils.NumberAsString;
import utils.ObserverMessage;
import lang.Lang;

@SuppressWarnings("serial")
public class TableModelItemAssets extends TableModelCls<Long, AssetCls> implements Observer
{
	public static final int COLUMN_KEY = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_ADDRESS = 2;
	public static final int COLUMN_MOVABLE = 3;
	public static final int COLUMN_AMOUNT = 4;
	public static final int COLUMN_DIVISIBLE = 5;
	public static final int COLUMN_FAVORITE = 6;
	public static final int COLUMN_I_OWNER = 7;

	//private SortableList<Long, AssetCls> assets;
	
	private String[] columnNames = Lang.getInstance().translate(new String[]{"Key", "Name", "Owner", "Movable", "Quantity", "Divisible", "Favorite", "I Owner"});
	private Boolean[] column_AutuHeight = new Boolean[]{false,true,true,false,false,false,false,false};
	private List<ItemCls> list;
	private String filter_Name = "";
	private long key_filter =0;
	private ItemAssetMap db;
	
	public TableModelItemAssets()
	{
		//Controller.getInstance().addObserver(this);
		db = DBSet.getInstance().getItemAssetMap();
	}
	public void set_Filter_By_Name(String str) {
		filter_Name = str;
		list = db.get_By_Name(filter_Name, false);
		this.fireTableDataChanged();

	}
	public void clear(){
		list =new ArrayList<ItemCls>();
		this.fireTableDataChanged();
		
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
	public SortableList<Long, AssetCls> getSortableList() 
	{
		return null;
	}
	
	public Class<? extends Object> getColumnClass(int c) {     // set column type
		Object o = getValueAt(0, c);
		return o==null?Null.class:o.getClass();
	    }
	
	public AssetCls getAsset(int row)
	{
		return (AssetCls) this.list.get(row);
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
		if (this.list == null)
			return 0;
		;
		return this.list.size();
		
	}

	@Override
	public Object getValueAt(int row, int column) 
	{
		if(this.list == null || row > this.list.size() - 1 )
		{
			return null;
		}
		
		AssetCls asset = (AssetCls) this.list.get(row);
		
		switch(column)
		{
		case COLUMN_KEY:
			
			return asset.getKey();
		
		case COLUMN_NAME:
			
			return asset.getName();
		
		case COLUMN_ADDRESS:
			
			return asset.getOwner().getPersonAsString();
			
		case COLUMN_MOVABLE:
			
			return asset.isMovable();

		case COLUMN_AMOUNT:
			
			return asset.getTotalQuantity();
			
		case COLUMN_DIVISIBLE:
			
			return asset.isDivisible();
			
		case COLUMN_FAVORITE:
			
			return asset.isFavorite();
		
		case COLUMN_I_OWNER:
			
			if (Controller.getInstance().isAddressIsMine(asset.getOwner().getAddress()))
				return true;
			return false;
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
		/*
		//CHECK IF NEW LIST
		if(message.getType() == ObserverMessage.LIST_ASSET_TYPE)
		{			
			if(this.assets == null)
			{
				this.assets = (SortableList<Long, AssetCls>) message.getValue();
				this.assets.addFilterField("name");
				this.assets.registerObserver();
			}	
			
			this.fireTableDataChanged();
		}
		
		//CHECK IF LIST UPDATED
		if(message.getType() == ObserverMessage.ADD_ASSET_TYPE || message.getType() == ObserverMessage.REMOVE_ASSET_TYPE)
		{
			this.fireTableDataChanged();
		}
		*/
		if (key_filter >0){
			
			return;	
			}
			
			// CHECK IF NEW LIST
			if (message.getType() == ObserverMessage.LIST_ASSET_TYPE) {
				if (this.list == null && !filter_Name.equals("")) {
					list = db.get_By_Name(filter_Name, false);
					this.fireTableDataChanged();
					// this.persons = (SortableList<Tuple2<String, String>,
					// PersonCls>) message.getValue();
					// this.persons.addFilterField("name");
					// this.persons.registerObserver();
				}

			}

			// CHECK IF LIST UPDATED
			if (message.getType() == ObserverMessage.ADD_ASSET_TYPE) {
				AssetCls ppp = (AssetCls) message.getValue();
				if (ppp.getName().contains(filter_Name))
					list.add(ppp);
				// list = db.getPerson_By_Name(filter_Name);

				this.fireTableDataChanged();
			}

			// CHECK IF LIST UPDATED
			if (message.getType() == ObserverMessage.REMOVE_ASSET_TYPE) {
				 AssetCls ppp = (AssetCls) message.getValue();
				if (ppp.getName().contains(filter_Name))
					list.remove(ppp);
				// list = db.getPerson_By_Name(filter_Name);

				this.fireTableDataChanged();
			}
		
	}
	
	public void removeObservers() 
	{
	//	this.assets.removeObserver();
		Controller.getInstance().deleteObserver(this);
	}
	
	public void Find_item_from_key(String text) {
		// TODO Auto-generated method stub
		if (text.equals("") || text == null) return;
		if (!text.matches("[0-9]*"))return;
			key_filter = new Long(text);
			list =new ArrayList<ItemCls>();
			 AssetCls pers = Controller.getInstance().getAsset(key_filter);
			if ( pers == null) return;
			list.add(pers);
			this.fireTableDataChanged();
		
		
	}
}
