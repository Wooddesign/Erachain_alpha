package database;

import java.util.Stack;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple5;

import core.item.statuses.StatusCls;
import utils.ObserverMessage;
import database.DBSet;
import utill.DBMap;

// key+key to key_Stack for End_Date Map
// in days
public class KK_K_Map extends DBMap<
			Tuple2<Long, Long>, // item1 Key + item2 Key
			TreeMap<Long, // item3 Key
					Stack<Tuple3<
						Long, // end_date
						Integer, // block.getHeight
						byte[] // transaction.getReference
				>>>>
{
	
	private Map<Integer, Integer> observableData = new HashMap<Integer, Integer>();
	private String name;
		
	public KK_K_Map(DBSet databaseSet, DB database,
			String name, int observerMessage_add, int observerMessage_remove)
	{
		super(databaseSet, database);
		
		this.name = name;
		this.observableData.put(DBMap.NOTIFY_ADD, observerMessage_add);
		this.observableData.put(DBMap.NOTIFY_REMOVE, observerMessage_remove);
		//this.observableData.put(DBMap.NOTIFY_LIST, ObserverMessage.LIST_PERSON_STATUSTYPE);

	}

	public KK_K_Map(KK_K_Map parent) 
	{
		super(parent, null);
	}

	
	protected void createIndexes(DB database){}

	@Override
	protected Map<Tuple2<Long, Long>, TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>>> getMap(DB database) 
	{
		//OPEN MAP
		BTreeMap<Tuple2<Long, Long>, TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>>> map =  database.createTreeMap(name)
				.keySerializer(BTreeKeySerializer.TUPLE2)
				.counterEnable()
				.makeOrGet();
				
		//RETURN
		return map;
	}

	@Override
	protected Map<Tuple2<Long, Long>, TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>>> getMemoryMap() 
	{
		// HashMap ?
		return new TreeMap<Tuple2<Long, Long>, TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>>>();
	}

	@Override
	protected TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>> getDefaultValue() 
	{
		return new TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>>();
	}
	
	@Override
	public Map<Integer, Integer> getObservableData() 
	{
		return this.observableData;
	}

	@SuppressWarnings("unchecked")
	public void addItem(Long key1, Long key2, Long itemKey, Tuple3<Long, Integer, byte[]> item)
	{

		Tuple2<Long, Long> key = new Tuple2<Long, Long>(key1, key2);
		
		TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>> value = this.get(key);
		
		TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>> value_new;
		if (this.parent == null)
			value_new = value;
		else {
			// !!!! NEEED .clone() !!!
			// need for updates only in fork - not in parent DB
			value_new = (TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>>)value.clone();
		}

		Stack<Tuple3<Long, Integer, byte[]>> stack = value_new.get(itemKey);
		if (stack == null) {
			stack = new Stack<Tuple3<Long, Integer, byte[]>>();
			stack.push(item);			
			value_new.put(itemKey, stack);
		} else {
			if (this.parent == null) {
				stack.push(item);			
				value_new.put(itemKey, stack);				
			} else {
				Stack<Tuple3<Long, Integer, byte[]>> stack_new;
				stack_new = (Stack<Tuple3<Long, Integer, byte[]>>)stack.clone();		
				stack_new.push(item);			
				value_new.put(itemKey, stack_new);				
			}
		}
		
		this.set(key, value_new);

	}
	
	public Tuple3<Long, Integer, byte[]> getItem(Long key1, Long key2, Long itemKey)
	{
		Tuple2<Long, Long> key = new Tuple2<Long, Long>(key1, key2);

		TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>> value = this.get(key);
		Stack<Tuple3<Long, Integer, byte[]>> stack = value.get(itemKey);

		//stack.elementAt()
		return stack != null? stack.size()> 0? stack.peek(): null : null;
	}
	
	// remove only last item from stack for this key of itemKey
	@SuppressWarnings("unchecked")
	public void removeItem(Long key1, Long key2, Long itemKey)
	{
		Tuple2<Long, Long> key = new Tuple2<Long, Long>(key1, key2);

		TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>> value = this.get(key);
		
		TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>> value_new;
		if (this.parent == null)
			value_new = value;
		else {
			// !!!! NEEED .clone() !!!
			// need for updates only in fork - not in parent DB
			value_new = (TreeMap<Long, Stack<Tuple3<Long, Integer, byte[]>>>)value.clone();
		}

		Stack<Tuple3<Long, Integer, byte[]>> stack = value_new.get(itemKey);
		if (stack==null || stack.size() == 0) return;

		if (this.parent == null) {
			stack.pop();
			value_new.put(itemKey, stack);
		} else {
			Stack<Tuple3<Long, Integer, byte[]>> stack_new;
			stack_new = (Stack<Tuple3<Long, Integer, byte[]>>)stack.clone();
			stack_new.pop();
			value_new.put(itemKey, stack_new);
		}

		this.set(key, value_new);
		
	}
	
}