package database;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple4;

import core.account.Account;
import core.item.assets.AssetCls;
import utils.ObserverMessage;
import database.DBSet;
import utill.DBMap;
import utill.Transaction;

// balances for Essence + Asset ->
// TODO SOFT HARD TRUE
// -> in_OWN(a, b), in_RENT(a, b), on_HOLD(a, b), SPEND(a, b)
//   = in_USE (TOTAL on HAND)
// a - income
// b - balance
// outcome = b - a
public class _BalanceMap extends DBMap<Tuple2<Long, Long>,
	Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
		Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>> 
{
	
	private Map<Integer, Integer> observableData = new HashMap<Integer, Integer>();
	private String name;
	
	@SuppressWarnings("rawtypes")
	private BTreeMap assetKeyMap;
	
	public _BalanceMap(DBSet databaseSet, DB database,
			String name, int add_BALANCE_TYPE, int remove_BALANCE_TYPE)
	{
		super(databaseSet, database);
		
		this.name = name;
		// ObserverMessage.ADD_BALANCE_TYPE
		this.observableData.put(DBMap.NOTIFY_ADD, add_BALANCE_TYPE);
		this.observableData.put(DBMap.NOTIFY_REMOVE, remove_BALANCE_TYPE);
		//this.observableData.put(DBMap.NOTIFY_LIST, list_BALANCE_TYPE);
	}

	public _BalanceMap(_BalanceMap parent) 
	{
		super(parent, null);
	}
	
	protected void createIndexes(DB database){}

	@SuppressWarnings({ "unchecked"})
	@Override
	protected Map<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>,
	Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>> getMap(DB database) 
	{
		//OPEN MAP
		BTreeMap<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>,
						Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>
			map =  database.createTreeMap("assets_balances_" + this.name)
				.keySerializer(BTreeKeySerializer.TUPLE2)
				.counterEnable()
				.makeOrGet();
		
		//HAVE/WANT KEY
		this.assetKeyMap = database.createTreeMap("balances_key_asset_" + this.name)
				.comparator(Fun.COMPARATOR)
				.counterEnable()
				.makeOrGet();
		
		//BIND ASSET KEY
		/*
		Bind.secondaryKey(map, this.assetKeyMap, new Fun.Function2<Tuple3<Long, BigDecimal, byte[]>, Tuple2<Long, Long>, BigDecimal>() {
			@Override
			public Tuple3<Long, BigDecimal, byte[]> run(Tuple2<Long, Long> key, BigDecimal value) {
				return new Tuple3<Long, BigDecimal, String>(key.b, value.negate(), key.a);
			}	
		});*/
		Bind.secondaryKey(map, this.assetKeyMap, new Fun.Function2<Tuple3<Long, Tuple4<Tuple2<BigDecimal, BigDecimal>,
				Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>, Long>,
				Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>,
				Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>() {
			@Override
			public Tuple3<Long, Tuple4<Tuple2<BigDecimal, BigDecimal>,
			Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>, Long> run(Tuple2<Long, Long> key, Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> value) {
				return new Tuple3<Long, Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>, Long>(
						key.b, new Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>
								(
									new Tuple2<BigDecimal, BigDecimal>(value.a.a.negate(), value.a.b.negate()),
									new Tuple2<BigDecimal, BigDecimal>(value.b.a.negate(), value.b.b.negate()),
									new Tuple2<BigDecimal, BigDecimal>(value.c.a.negate(), value.c.b.negate()),
									new Tuple2<BigDecimal, BigDecimal>(value.d.a.negate(), value.d.b.negate())
								),
						key.a);
			}	
		});
		
		//RETURN
		return map;
	}

	@Override
	protected Map<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>,
	Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>> getMemoryMap() 
	{
		return new TreeMap<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>,
				Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>(Fun.TUPLE2_COMPARATOR);
	}

	@Override
	protected Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> getDefaultValue() 
	{
		return new Tuple4<Tuple2<BigDecimal, BigDecimal>,
				Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>
					(
							new Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO.setScale(8), BigDecimal.ZERO.setScale(8)),
							new Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO.setScale(8), BigDecimal.ZERO.setScale(8)),
							new Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO.setScale(8), BigDecimal.ZERO.setScale(8)),
							new Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO.setScale(8), BigDecimal.ZERO.setScale(8))
					);
	}
	
	@Override
	public Map<Integer, Integer> getObservableData() 
	{
		return this.observableData;
	}
	
	/*
	public void set(String assence, BigDecimal value)
	{
		this.set(assence, FEE_KEY, value);
	}
	*/
	
	public void set(Long essence, long key, Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> value)
	{
		this.set(new Tuple2<Long, Long>(essence, key), value);
	}
	
	/*
	public BigDecimal get(String assence)
	{
		return this.get(assence, FEE_KEY);
	}
	*/
	
	public Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> get(long essence, long key)
	{
		return this.get(new Tuple2<Long, Long>(essence, key));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SortableList<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>> getAssetBalancesSortableList(long key)
	{
		//FILTER ALL KEYS
		Collection<Tuple2<Long, Long>> keys = ((BTreeMap<Tuple3, Tuple2<Long, Long>>) this.assetKeyMap).subMap(
				Fun.t3(key, null, null),
				Fun.t3(key, Fun.HI(), Fun.HI())).values();
		
		//RETURN
		return new SortableList<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>(this, keys);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SortableList<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>> getEssenceBalancesSortableList(long essence) 
	{
		BTreeMap map = (BTreeMap) this.map;
		
		//FILTER ALL KEYS
		Collection keys = ((BTreeMap<Tuple2, BigDecimal>) map).subMap(
				Fun.t2(essence, null),
				Fun.t2(essence, Fun.HI())).keySet();
		
		//RETURN
		return new SortableList<Tuple2<Long, Long>, Tuple4<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>(this, keys);
	}
}
