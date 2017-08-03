package core.wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.mapdb.Fun.Tuple2;

import controller.Controller;
import core.account.Account;
import core.item.notes.NoteCls;
import database.DBSet;
import database.SortableList;
import gui.Gui;
import utill.Transaction;
import utils.ObserverMessage;


public class NotesFavorites implements Observer{

	private List<Long> favorites;
	
	public NotesFavorites() {
		this.favorites = new ArrayList<Long>(); 
		
		Controller.getInstance().addWalletListener(this);
		Controller.getInstance().addObserver(this);
	}
	
	public List<Long> getKeys()
	{
		return this.favorites;
	}
	
	public List<NoteCls> getNotes()
	{
		List<NoteCls> notes = new ArrayList<NoteCls>();
		//notes.add(Controller.getInstance().getNote(Transaction.FEE_KEY));		
		//notes.add(Controller.getInstance().getNote(Transaction.FEE_KEY + 1l));		
		for (Long key : this.favorites) {
			notes.add(Controller.getInstance().getItemNote(key));
		}
		return notes;
	}
	
	
	@Override
	// if some changed in wallet - reload favorites
	public void update(Observable o, Object arg) {

		if(!Gui.isGuiStarted()){
			return;
		}
		
		ObserverMessage message = (ObserverMessage) arg;

		if((message.getType() == ObserverMessage.NETWORK_STATUS && (int) message.getValue() == Controller.STATUS_OK)
			||((Controller.getInstance().getStatus() == Controller.STATUS_OK) && 
					(
							message.getType() == ObserverMessage.ADD_ACCOUNT_TYPE
							|| message.getType() == ObserverMessage.REMOVE_ACCOUNT_TYPE
							//|| message.getType() == ObserverMessage.ADD_BALANCE_TYPE
							//|| message.getType() == ObserverMessage.REMOVE_BALANCE_TYPE
					)))
		{
			List<Long> favoritesUpadate = new ArrayList<Long>();
			//favoritesUpadate.add(0L);
			
			/* balancec = nil
			for (Account account : Controller.getInstance().getAccounts()) {
				SortableList<Tuple2<String, Long>, BigDecimal> balancesList = DBSet.getInstance().getBalanceMap().getBalancesSortableList(account);
				
				for (Pair<Tuple2<String, Long>, BigDecimal> balance : balancesList) {
					if(balance.getB().compareTo(BigDecimal.ZERO) > 0) {
						if(!favoritesUpadate.contains(balance.getA().b)){
							favoritesUpadate.add(balance.getA().b);
						}
					}
				}
			}
			*/
			
			////////this.favorites = favoritesUpadate;

			///////Controller.getInstance().replaseNotesFavorites();
		}
	}
}