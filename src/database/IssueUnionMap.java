package database;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;

import com.google.common.primitives.UnsignedBytes;

import database.DBSet;
import utill.Transaction;

public class IssueUnionMap extends Issue_ItemMap
{
	
	public IssueUnionMap(DBSet databaseSet, DB database)
	{
		super(databaseSet, database, "union");
	}

	public IssueUnionMap(IssueUnionMap parent) 
	{
		super(parent);
	}

}
