package database;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;

import com.google.common.primitives.UnsignedBytes;

import core.transaction.CancelSellNameTransaction;
import database.DBSet;
import utill.DBMap;

public class CancelSellNameMap extends DBMap<byte[], BigDecimal> 
{
	private Map<Integer, Integer> observableData = new HashMap<Integer, Integer>();
	
	public CancelSellNameMap(DBSet databaseSet, DB database)
	{
		super(databaseSet, database);
	}

	public CancelSellNameMap(CancelSellNameMap parent) 
	{
		super(parent, null);
	}
	
	protected void createIndexes(DB database){}

	@Override
	protected Map<byte[], BigDecimal> getMap(DB database) 
	{
		//OPEN MAP
		return database.createTreeMap("cancelNameOrphanData")
				.keySerializer(BTreeKeySerializer.BASIC)
				.comparator(UnsignedBytes.lexicographicalComparator())
				.makeOrGet();
	}

	@Override
	protected Map<byte[], BigDecimal> getMemoryMap() 
	{
		return new TreeMap<byte[], BigDecimal>(UnsignedBytes.lexicographicalComparator());
	}

	@Override
	protected BigDecimal getDefaultValue() 
	{
		return null;
	}
	
	@Override
	public Map<Integer, Integer> getObservableData() 
	{
		return this.observableData;
	}

	public void delete(CancelSellNameTransaction transaction) {
		this.delete(transaction.getSignature());
	}
	
	public BigDecimal get(CancelSellNameTransaction transaction)
	{
		return this.get(transaction.getSignature());
	}
	
	public void set(CancelSellNameTransaction transaction, BigDecimal value)
	{
		this.set(transaction.getSignature(), value);
	}
}
