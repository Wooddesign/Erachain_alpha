package api;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import controller.Controller;
import core.account.Account;
import core.account.PrivateKeyAccount;
import core.crypto.Crypto;
import core.item.assets.AssetCls;
import core.payment.Payment;
import utill.Transaction;
import utils.APIUtils;
import utils.Pair;

@Path("multipayment")
@Produces(MediaType.APPLICATION_JSON)
public class MultiPaymentResource 
{
	
	private static final Logger LOGGER = Logger
			.getLogger(MultiPaymentResource.class);
	@Context
	HttpServletRequest request;
	
	@POST
	@Consumes(MediaType.WILDCARD)
	public String createMultiPayment(String x)
	{
		try
		{
			String password = null;
			APIUtils.askAPICallAllowed(password, "POST multipayment\n" + x, request );

			//READ JSON
			JSONObject jsonObject = (JSONObject) JSONValue.parse(x);
			String sender = (String) jsonObject.get("sender");
			long lgAsset = 0L;
			if(jsonObject.containsKey("asset")) {
				lgAsset = ((Long) jsonObject.get("asset")).intValue();
			}
			String feePowStr = (String) jsonObject.get("feePow");
			
			AssetCls defaultAsset;
			try {
				defaultAsset = Controller.getInstance().getAsset(new Long(lgAsset));
			} catch (Exception e) {
				throw ApiErrorFactory.getInstance().createError(
					Transaction.ITEM_ASSET_NOT_EXIST);
			}

			List<Payment> payments = jsonPaymentParser((JSONArray)jsonObject.get("payments"), defaultAsset);
			
			// CHECK ADDRESS
			if (!Crypto.getInstance().isValidAddress(sender)) {
				throw ApiErrorFactory.getInstance().createError(
						Transaction.INVALID_ADDRESS);
			}
			
			// CHECK IF WALLET EXISTS
			if (!Controller.getInstance().doesWalletExists()) {
				throw ApiErrorFactory.getInstance().createError(
						ApiErrorFactory.ERROR_WALLET_NO_EXISTS);
			}

			// CHECK WALLET UNLOCKED
			if (!Controller.getInstance().isWalletUnlocked()) {
				throw ApiErrorFactory.getInstance().createError(
						ApiErrorFactory.ERROR_WALLET_LOCKED);
			}
			
			// GET ACCOUNT
			PrivateKeyAccount account = Controller.getInstance()
					.getPrivateKeyAccountByAddress(sender);
			if (account == null) {
				throw ApiErrorFactory.getInstance().createError(
						Transaction.INVALID_ADDRESS);
			}
			
			int feePow=0;
			if(feePowStr != null) {
				try
				{
					feePow = Integer.parseInt(feePowStr);
				}
				catch(Exception e)
				{
				}	
			} else {
			}
			
			Pair<Transaction, Integer> result = Controller.getInstance().sendMultiPayment(account, payments, feePow);
			
			if (result.getB() == Transaction.VALIDATE_OK)
				return result.getA().toJson().toJSONString();
			else
				throw ApiErrorFactory.getInstance().createError(result.getB());

		}
		catch(NullPointerException | ClassCastException e)
		{
			//JSON EXCEPTION
			LOGGER.info(e);
			throw ApiErrorFactory.getInstance().createError(ApiErrorFactory.ERROR_JSON);
		}
	}

	public static List<Payment> jsonPaymentParser(JSONArray jsonArray)
	{
		return jsonPaymentParser(jsonArray, Controller.getInstance().getAsset(0L));
	}
	
	public static List<Payment> jsonPaymentParser(JSONArray jsonArray, AssetCls defaultAsset)
	{
		List<Payment> payments = new ArrayList<Payment>();
		
		if(jsonArray == null) {
			return payments;
		}
		
		for(int i=0; i<jsonArray.size(); i++)
		{
			JSONObject jsonPayment = (JSONObject) jsonArray.get(i);
			
			String recipient = jsonPayment.get("recipient").toString();
			if (!Crypto.getInstance().isValidAddress(recipient)) {
				throw ApiErrorFactory.getInstance().createError(
						Transaction.INVALID_ADDRESS);
			}
			Account paymentRecipient = new Account(jsonPayment.get("recipient").toString());
			
			AssetCls paymentAsset = defaultAsset;
			if(jsonPayment.containsKey("asset")) {
				try {
					paymentAsset = Controller.getInstance().getAsset(new Long(jsonPayment.get("asset").toString()));
				} catch (Exception e) {
					throw ApiErrorFactory.getInstance().createError(
							Transaction.INVALID_ITEM_VALUE);
				}
			}
			
			BigDecimal bdAmount;
			try 
			{
				bdAmount = new BigDecimal(jsonPayment.get("amount").toString());
				bdAmount = bdAmount.setScale(8);
			} catch (Exception e) {
				throw ApiErrorFactory.getInstance().createError(
						Transaction.INVALID_AMOUNT);
			}
			
			Payment payment = new Payment(paymentRecipient, paymentAsset.getKey(), bdAmount);
			
			payments.add(payment);
		}	
		
		return payments;
	}
}
