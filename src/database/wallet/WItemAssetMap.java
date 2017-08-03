package database.wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import core.account.Account;
import core.item.ItemCls;
import core.item.assets.AssetCls;

import org.mapdb.BTreeMap;

import utils.ObserverMessage;
import utils.Pair;
import database.serializer.ItemSerializer;
import database.serializer.TransactionSerializer;
import utill.DBMap;
import utill.GenesisBlock;
public class WItemAssetMap extends WItem_Map
{	
	//static Logger LOGGER = Logger.getLogger(WItemAssetMap.class.getName());

	static final String NAME = "asset";
	static final int TYPE = ItemCls.ASSET_TYPE;

	public WItemAssetMap(WalletDatabase walletDatabase, DB database)
	{
		super(walletDatabase, database,
				TYPE, NAME,
				ObserverMessage.ADD_ASSET_TYPE,
				ObserverMessage.REMOVE_ASSET_TYPE,
				ObserverMessage.LIST_ASSET_TYPE
				);
	}

	public WItemAssetMap(WItemAssetMap parent) 
	{
		super(parent);
	}
	
	@Override
	// type+name not initialized yet! - it call as Super in New
	protected Map<Tuple2<String, String>, ItemCls> getMap(DB database) 
	{
		//OPEN MAP
		return database.createTreeMap(NAME)
				.keySerializer(BTreeKeySerializer.TUPLE2)
				.valueSerializer(new ItemSerializer(TYPE))
				.counterEnable()
				.makeOrGet();
	}

	/*
	//@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void createIndexes(DB database)
	{
		//NAME INDEX
	}

	@Override
	protected Map<Tuple2<String, String>, AssetCls> getMap(DB database) 
	{
		//OPEN MAP
		return database.createTreeMap("asset")
				.keySerializer(BTreeKeySerializer.TUPLE2)
				.valueSerializer(new AssetSerializer())
				.counterEnable()
				.makeOrGet();
	}

	@Override
	protected Map<Tuple2<String, String>, AssetCls> getMemoryMap() 
	{
		return new TreeMap<Tuple2<String, String>, AssetCls>(Fun.TUPLE2_COMPARATOR);
	}

	@Override
	protected AssetCls getDefaultValue() 
	{
		return null;
	}
	
	@Override
	protected Map<Integer, Integer> getObservableData() 
	{
		return this.observableData;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<AssetCls> get(Account account)
	{
		List<AssetCls> assets = new ArrayList<AssetCls>();
		
		try
		{
			Map<Tuple2<String, String>, AssetCls> accountAssets = ((BTreeMap) this.map).subMap(
					Fun.t2(account.getAddress(), null),
					Fun.t2(account.getAddress(), Fun.HI()));
			
			//GET ITERATOR
			Iterator<AssetCls> iterator = accountAssets.values().iterator();
			
			while(iterator.hasNext())
			{
				assets.add(iterator.next());
			}
		}
		catch(Exception e)
		{
			//ERROR
			LOGGER.error(e.getMessage(),e);
		}
		
		return assets;
	}
	
	public List<Pair<Account, AssetCls>> get(List<Account> accounts)
	{
		List<Pair<Account, AssetCls>> assets = new ArrayList<Pair<Account, AssetCls>>();		

		try
		{
			//FOR EACH ACCOUNTS
			synchronized(accounts)
			{
				for(Account account: accounts)
				{
					List<AssetCls> accountAssets = get(account);
					for(AssetCls asset: accountAssets)
					{
						assets.add(new Pair<Account, AssetCls>(account, asset));
					}
				}
			}
		}
		catch(Exception e)
		{
			//ERROR
			LOGGER.error(e.getMessage(),e);
		}
		
		return assets;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void delete(Account account)
	{
		//GET ALL POLLS THAT BELONG TO THAT ADDRESS
		Map<Tuple2<String, String>, AssetCls> accountAssets = ((BTreeMap) this.map).subMap(
				Fun.t2(account.getAddress(), null),
				Fun.t2(account.getAddress(), Fun.HI()));
		
		//DELETE NAMES
		for(Tuple2<String, String> key: accountAssets.keySet())
		{
			this.delete(key);
		}
	}
	
	public void delete(AssetCls asset)
	{
		this.delete(asset.getCreator(), asset);
	}
	
	public void delete(Account account, AssetCls asset) 
	{
		this.delete(new Tuple2<String, String>(account.getAddress(), new String(asset.getReference())));	
	}
	
	public void deleteAll(List<Account> accounts)
	{
		for(Account account: accounts)
		{
			this.delete(account);
		}
	}
	
	public boolean add(AssetCls asset)
	{
		return this.set(new Tuple2<String, String>(asset.getCreator().getAddress(),
				new String(asset.getReference())), asset);
	}
	
	public void addAll(Map<Account, List<AssetCls>> assets)
	{
		//FOR EACH ACCOUNT
	    for(Account account: assets.keySet())
	    {
	    	//FOR EACH TRANSACTION
	    	for(AssetCls asset: assets.get(account))
	    	{
	    		this.add(asset);
	    	}
	    }
	}
	*/
}
