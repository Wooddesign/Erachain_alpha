package core.transaction;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
// import org.apache.log4j.Logger;
import java.util.HashSet;

import org.json.simple.JSONObject;
import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple4;

import com.google.common.primitives.Bytes;

//import ntp.NTP;

//import org.json.simple.JSONObject;

//import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

import core.account.Account;
import core.crypto.Base58;
import core.crypto.Crypto;
//import core.account.Account;
//import core.account.PublicKeyAccount;
//import core.crypto.Crypto;
//import core.item.ItemCls;
import core.item.persons.PersonCls;
import core.item.persons.PersonFactory;
import core.item.statuses.StatusCls;
//import database.ItemMap;
import database.DBSet;
import utill.Block;
import utill.GenesisBlock;
import utill.Transaction;

public class GenesisIssuePersonRecord extends GenesisIssue_ItemRecord 
{
	private static final byte TYPE_ID = (byte)GENESIS_ISSUE_PERSON_TRANSACTION;
	private static final String NAME_ID = "GENESIS Issue Person";

	public GenesisIssuePersonRecord(PersonCls person) 
	{
		super(TYPE_ID, NAME_ID, person);
	}
	
	//PARSE CONVERT
	
	public static Transaction Parse(byte[] data) throws Exception
	{	

		//CHECK IF WE MATCH BLOCK LENGTH
		if (data.length < BASE_LENGTH)
		{
			throw new Exception("Data does not match block length " + data.length);
		}
		
		// READ TYPE
		int position = SIMPLE_TYPE_LENGTH;
							
		//READ PERSON
		// read without reference
		PersonCls person = PersonFactory.getInstance().parse(Arrays.copyOfRange(data, position, data.length), false);
		//position += note.getDataLength(false);

		return new GenesisIssuePersonRecord(person);
		
	}
	
	//@Override
	public int isValid(DBSet db, Long releaserReference) 
	{
						
		int res = super.isValid(db, releaserReference);
		if (res != Transaction.VALIDATE_OK) return res;
		
		PersonCls person = (PersonCls) this.getItem();
		// birthLatitude -90..90; birthLongitude -180..180
		if (person.getBirthLatitude() > 90 || person.getBirthLatitude() < -90) return Transaction.ITEM_PERSON_LATITUDE_ERROR;
		if (person.getBirthLongitude() > 180 || person.getBirthLongitude() < -180) return Transaction.ITEM_PERSON_LONGITUDE_ERROR;
		if (person.getRace().length() <1 || person.getRace().length() > 125) return Transaction.ITEM_PERSON_RACE_ERROR;
		if (person.getGender() < 0 || person.getGender() > 10) return Transaction.ITEM_PERSON_GENDER_ERROR;
		if (person.getSkinColor().length() <1 || person.getSkinColor().length() >255) return Transaction.ITEM_PERSON_SKIN_COLOR_ERROR;
		if (person.getEyeColor().length() <1 || person.getEyeColor().length() >255) return Transaction.ITEM_PERSON_EYE_COLOR_ERROR;
		if (person.getHairСolor().length() <1 || person.getHairСolor().length() >255) return Transaction.ITEM_PERSON_HAIR_COLOR_ERROR;
		//int ii = Math.abs(person.getHeight());
		if (Math.abs(person.getHeight()) < 40) return Transaction.ITEM_PERSON_HEIGHT_ERROR;
				
		return VALIDATE_OK;
	
	}


}
