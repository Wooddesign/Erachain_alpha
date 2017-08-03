package database;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;

import com.google.common.primitives.UnsignedBytes;

import database.DBSet;
import utill.Block;
import utill.DBMap;

public class ChildMap extends DBMap<byte[], byte[]> 
{
	private Map<Integer, Integer> observableData = new HashMap<Integer, Integer>();
	
	//private BlockMap blockMap;
	
	public ChildMap(DBSet databaseSet, DB database)
	{
		super(databaseSet, database);
	}

	// dbSet - current DBSet for access to others Maps
	public ChildMap(ChildMap parent, DBSet dbSet) 
	{
		super(parent, dbSet);
	}
	
	protected void createIndexes(DB database){}

	@Override
	protected Map<byte[], byte[]> getMap(DB database) 
	{
		//OPEN MAP
		return database.createTreeMap("children")
				.keySerializer(BTreeKeySerializer.BASIC)
				.comparator(UnsignedBytes.lexicographicalComparator())
				.makeOrGet();
	}

	@Override
	protected Map<byte[], byte[]> getMemoryMap() 
	{
		return new TreeMap<byte[], byte[]>(UnsignedBytes.lexicographicalComparator());
	}

	@Override
	protected byte[] getDefaultValue() 
	{
		return null;
	}
	
	@Override
	public Map<Integer, Integer> getObservableData() 
	{
		return this.observableData;
	}
	
	public Block get(Block parent)
	{
		if(this.contains(parent.getSignature()))
		{
			return getDBSet().getBlockMap().get(this.get(parent.getSignature()));
		}
		
		return null;
	}
	
	public void set(Block parent, Block child)
	{
		this.set(parent.getSignature(), child.getSignature());
	}
}
