package gui.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.DefaultComboBoxModel;

import utils.ObserverMessage;
import controller.Controller;
import core.item.ItemCls;

@SuppressWarnings("serial")
public class ComboBoxModelItems extends DefaultComboBoxModel<ItemCls> implements Observer {
	
	Lock lock = new ReentrantLock();
	int observerMessageType;
	private int itemType; 
	
	public ComboBoxModelItems(int observerMessageType, int itemType)
	{
		Controller.getInstance().addWalletListener(this);
		this.observerMessageType = observerMessageType;
		this.itemType = itemType;
	}
	
	@Override
	public void update(Observable o, Object arg) 
	{
		try
		{
			if (lock.tryLock()) {
				try {
					this.syncUpdate(o, arg);
				}
				finally {
					lock.unlock();
				}
			}
			
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
		
		//CHECK IF LIST UPDATED
		if(message.getType() == observerMessageType)
		{
			//GET SELECTED ITEM
			ItemCls selected = (ItemCls) this.getSelectedItem();
						
			//EMPTY LIST
			this.removeAllElements();
				
			//INSERT ALL ACCOUNTS
			Set<Long> keys = (Set<Long>) message.getValue();
			List<ItemCls> items = new ArrayList<ItemCls>();
			for(Long key: keys)
			{				
				//GET ITEM
				ItemCls item = Controller.getInstance().getItem(itemType, key);
				items.add(item);
				
				//ADD
				this.addElement(item);
			}
				
			//RESET SELECTED ITEM
			if(this.getIndexOf(selected) != -1)
			{
				for(ItemCls item: items)
				{
					if(item.getKey() == selected.getKey())
					{
						this.setSelectedItem(item);
						return;
					}
				}
			}
		}
	}
}
