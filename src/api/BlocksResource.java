package api;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;

import utils.APIUtils;
import utils.Pair;
import controller.Controller;
import core.account.Account;
import core.crypto.Base58;
import core.crypto.Crypto;
import database.BlockMap;
import database.DBSet;
import utill.Block;
import utill.BlockChain;
import utill.BlockGenerator;
import utill.GenesisBlock;
import utill.Transaction;

@Path("blocks")
@Produces(MediaType.APPLICATION_JSON)
public class BlocksResource 
{
	@Context
	HttpServletRequest request;

	@SuppressWarnings("unchecked")
	@GET
	@Path("/addresses/{limit}")	
	public String getLastAccountsBlocks(@PathParam("limit") int limit)
	{
		//String password = null;
		//APIUtils.askAPICallAllowed(password, "GET blocks", request);

		//CHECK IF WALLET EXISTS
		if(!Controller.getInstance().doesWalletExists())
		{
			throw ApiErrorFactory.getInstance().createError(ApiErrorFactory.ERROR_WALLET_NO_EXISTS);
		}
		
		List<Pair<Account, Block>> blocks = Controller.getInstance().getLastBlocks(limit);
		JSONArray array = new JSONArray();
		
		for(Pair<Account, Block> block: blocks)
		{
			array.add(block.getB().toJson());
		}
		
		return array.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/address/{address}/{limit}")	
	public String getBlocks(@PathParam("address") String address, @PathParam("limit") int limit)
	{

		//CHECK ADDRESS
		if(!Crypto.getInstance().isValidAddress(address))
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_ADDRESS);
		}

		//CHECK IF WALLET EXISTS
		if(!Controller.getInstance().doesWalletExists())
		{
			throw ApiErrorFactory.getInstance().createError(ApiErrorFactory.ERROR_WALLET_NO_EXISTS);
		}

		//CHECK ACCOUNT IN WALLET
		Account account = Controller.getInstance().getAccountByAddress(address);	
		if(account == null)
		{
			throw ApiErrorFactory.getInstance().createError(ApiErrorFactory.ERROR_WALLET_ADDRESS_NO_EXISTS);
		}
		
		JSONArray array = new JSONArray();
		for(Block block: Controller.getInstance().getLastBlocks(account, limit))
		{
			array.add(block.toJson());
		}
		
		return array.toJSONString();
	}
	
	@GET
	@Path("/{signature}")	
	public static String getBlock(@PathParam("signature") String signature)
	{
		//DECODE SIGNATURE
		byte[] signatureBytes;
		try
		{
			signatureBytes = Base58.decode(signature);
		}
		catch(Exception e)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}
				
		Block block = Controller.getInstance().getBlock(signatureBytes);
				
		//CHECK IF BLOCK EXISTS
		if(block == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_BLOCK_HEIGHT);
		}
		
		return block.toJson().toJSONString();
	}
	
	@GET
	@Path("/first")	
	public String getFirstBlock()
	{
		return new GenesisBlock().toJson().toJSONString();
	}
	
	@GET
	@Path("/last")	
	public static String getLastBlock()
	{
		return Controller.getInstance().getLastBlock().toJson().toJSONString();
	}
	
	@GET
	@Path("/child/{signature}")	
	public String getChild(@PathParam("signature") String signature)
	{
		//DECODE SIGNATURE
		byte[] signatureBytes;
		try
		{
			signatureBytes = Base58.decode(signature);
		}
		catch(Exception e)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}
				
		Block block = Controller.getInstance().getBlock(signatureBytes);
				
		//CHECK IF BLOCK EXISTS
		if(block == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_BLOCK_HEIGHT);
		}
		
		Block child = block.getChild(DBSet.getInstance());
		
		//CHECK IF CHILD EXISTS
		if(child == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_BLOCK_HEIGHT);
		}
		
		return child.toJson().toJSONString();
	}
	
	@GET
	@Path("/generatingbalance")
	public String getGeneratingBalance()
	{
		long generatingBalance = Controller.getInstance().getNextBlockGeneratingBalance();
		return String.valueOf(generatingBalance);
	}
	
	@GET
	@Path("/generatingbalance/{signature}")
	public String getGeneratingBalance(@PathParam("signature") String signature)
	{
		//DECODE SIGNATURE
		byte[] signatureBytes;
		try
		{
			signatureBytes = Base58.decode(signature);
		}
		catch(Exception e)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}
		
		Block block = Controller.getInstance().getBlock(signatureBytes);
		
		//CHECK IF BLOCK EXISTS
		if(block == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_BLOCK_HEIGHT);
		}
		
		long generatingBalance = block.getGeneratingBalance(DBSet.getInstance());
		return String.valueOf(generatingBalance);
	}
	
	/*
	@GET
	@Path("/time")
	public String getTimePerBlock()
	{
		Block block = Controller.getInstance().getLastBlock();
		long timePerBlock = BlockGenerator.getBlockTime(block.getGeneratingBalance());
		return String.valueOf(timePerBlock);
	}
	
	@GET
	@Path("/time/{generatingbalance}")
	public String getTimePerBlock(@PathParam("generating") long generatingbalance)
	{
		long timePerBlock = BlockGenerator.getBlockTime(generatingbalance);
		return String.valueOf(timePerBlock);
	}
	*/
	
	@GET
	@Path("/height")
	public static String getHeight() 
	{
		return String.valueOf(Controller.getInstance().getMyHeight());
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/fromheight/{height}")
	public static String getFromHeight(@PathParam("height") int height) 
	{
		DBSet db = DBSet.getInstance();
		
		byte[] signature = db.getBlockHeightsMap().get((long)(height - 1));
		if(signature == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}
		
		List<byte[]> headers = Controller.getInstance().getNextHeaders(signature);
		
		//CHECK IF BLOCK EXISTS
		if(headers == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}
		
		
		JSONArray array = new JSONArray();
		
		BlockMap dbMap = db.getBlockMap();
		for ( byte[] header: headers) {
			array.add(dbMap.get(header).toJson());
		}
		
		return array.toJSONString();
	}
	
	@GET
	@Path("/height/{signature}")
	public static String getHeight(@PathParam("signature") String signature) 
	{
		//DECODE SIGNATURE
		byte[] signatureBytes;
		try
		{
			signatureBytes = Base58.decode(signature);
		}
		catch(Exception e)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}

		Block block = DBSet.getInstance().getBlockMap().get(signatureBytes);
				
		//CHECK IF BLOCK EXISTS
		if(block == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_BLOCK_HEIGHT);
		}
		
		return String.valueOf(block.getHeight(DBSet.getInstance()));
	}

	/*
	 * GET HEADES as on nerwork communication
	 *  -> response callback in controller.Controller.onMessage(Message)
		 type = GET_SIGNATURES_TYPE
		 FOR - core.Synchronizer.getBlockSignatures(byte[], Peer)

	 */
	@GET
	@Path("/headers/{signature}")
	public static String getHeaders(@PathParam("signature") String signature) 
	{
		//DECODE SIGNATURE
		byte[] signatureBytes;
		try
		{
			signatureBytes = Base58.decode(signature);
		}
		catch(Exception e)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}

		List<byte[]> headers = Controller.getInstance().getNextHeaders(signatureBytes);
		
		//CHECK IF BLOCK EXISTS
		if(headers == null)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_SIGNATURE);
		}
		List<String> result = new ArrayList<String>();
		for ( byte[] sign: headers) {
			result.add(Base58.encode(sign));
		}
		
		return String.valueOf(result);
	}

	@GET
	@Path("/byheight/{height}")
	public static String getbyHeight(@PathParam("height") int height) 
	{
		Block block;
		try
		{
			block = Controller.getInstance().getBlockByHeight(height);
			if(block == null)
			{
				throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_BLOCK_HEIGHT);
			}
		}
		catch(Exception e)
		{
			throw ApiErrorFactory.getInstance().createError(Transaction.INVALID_BLOCK_HEIGHT);
		}
		return block.toJson().toJSONString();
	}
}
