package database;

import java.util.Stack;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Fun.Tuple3;

import core.item.statuses.StatusCls;
import utils.ObserverMessage;
import database.DBSet;
import utill.DBMap;

// key + key to Name_Stack End_Date Map
// union in union has name
// TODO
public class KKN_Map extends DBMap<
			Long, // item1 Key
			TreeMap<String, // item2 Key
					Stack<Tuple3<
						Long, // end_date
						Integer, // block.getHeight
						byte[] // transaction.getReference
				>>>>
{
	
	private Map<Integer, Integer> observableData = new HashMap<Integer, Integer>();
	private String name;
		
	public KKN_Map(DBSet databaseSet, DB database,
			String name, int observerMessage_add, int observerMessage_remove)
	{
		super(databaseSet, database);
		
		this.name = name;
		this.observableData.put(DBMap.NOTIFY_ADD, observerMessage_add);
		this.observableData.put(DBMap.NOTIFY_REMOVE, observerMessage_remove);
		//this.observableData.put(DBMap.NOTIFY_LIST, ObserverMessage.LIST_PERSON_STATUSTYPE);

	}

	public KKN_Map(KKN_Map parent, DBSet dbSet) 
	{
		super(parent, dbSet);
	}

	
	protected void createIndexes(DB database){}

	@Override
	protected Map<Long, TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>>> getMap(DB database) 
	{
		//OPEN MAP
		BTreeMap<Long, TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>>> map =  database.createTreeMap(name)
				.keySerializer(BTreeKeySerializer.BASIC)
				.counterEnable()
				.makeOrGet();
				
		//RETURN
		return map;
	}

	@Override
	protected Map<Long, TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>>> getMemoryMap() 
	{
		// HashMap ?
		return new TreeMap<Long, TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>>>();
	}

	@Override
	protected TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>> getDefaultValue() 
	{
		return new TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>>();
	}
	
	@Override
	public Map<Integer, Integer> getObservableData() 
	{
		return this.observableData;
	}

	@SuppressWarnings("unchecked")
	public void addItem(Long key, String nameKey, Tuple3<Long, Integer, byte[]> item)
	{

		TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>> value = this.get(key);
		
		TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>> value_new;
		
		if (this.parent == null)
			value_new = value;
		else {
			// !!!! NEEED .clone() !!!
			// need for updates only in fork - not in parent DB
			value_new = (TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>>)value.clone();
		}

		Stack<Tuple3<Long, Integer, byte[]>> stack = value_new.get(nameKey);
		if (stack == null) {
			stack = new Stack<Tuple3<Long, Integer, byte[]>>();
			stack.push(item);
			value_new.put(nameKey, stack);
		} else {
			if (this.parent == null) {
				stack.push(item);
				value_new.put(nameKey, stack);
			} else {
				Stack<Tuple3<Long, Integer, byte[]>> stack_new;
				stack_new = (Stack<Tuple3<Long, Integer, byte[]>>)stack.clone();
				stack_new.push(item);
				value_new.put(nameKey, stack_new);
			}
		}
				
		this.set(key, value_new);

	}
	
	public Tuple3<Long, Integer, byte[]> getItem(Long key, String nameKey)
	{
		TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>> value = this.get(key);
		Stack<Tuple3<Long, Integer, byte[]>> stack = value.get(nameKey);
		return stack != null? stack.size()> 0? stack.peek(): null : null;
	}
	
	// remove only last item from stack for this key of nameKey
	@SuppressWarnings("unchecked")
	public void removeItem(Long key, String nameKey)
	{
		TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>> value = this.get(key);
		
		TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>> value_new;
		if (this.parent == null)
			value_new = value;
		else {
			// !!!! NEEED .clone() !!!
			// need for updates only in fork - not in parent DB
			value_new = (TreeMap<String, Stack<Tuple3<Long, Integer, byte[]>>>)value.clone();
		}

		Stack<Tuple3<Long, Integer, byte[]>> stack = value_new.get(nameKey);
		if (stack==null || stack.size() == 0) return;

		if (this.parent == null) {
			stack.pop();
			value_new.put(nameKey, stack);
		} else {
			Stack<Tuple3<Long, Integer, byte[]>> stack_new;
			stack_new = (Stack<Tuple3<Long, Integer, byte[]>>)stack.clone();
			stack_new.pop();
			value_new.put(nameKey, stack_new);
		}
		
		this.set(key, value_new);
		
	}
	
}
