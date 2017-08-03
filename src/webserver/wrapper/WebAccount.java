package webserver.wrapper;

import java.math.BigDecimal;

import org.mapdb.Fun.Tuple3;

import core.account.Account;
import utill.Transaction;

/**
 * Web respresentation of an Account for read only usage with pebble
 * @author Skerberus
 *
 */
public class WebAccount {
	
	private final String address;
	private final Tuple3<BigDecimal, BigDecimal, BigDecimal> balance;
	private String stringRepresentation;

	public WebAccount(Account account) {
		
		address = account.getAddress();
		balance = account.getConfBalance3(0, Transaction.FEE_KEY);
		stringRepresentation = account.toString();
	}

	public Tuple3<BigDecimal, BigDecimal, BigDecimal> getUnconfirmedBalance() {
		return balance;
	}

	public String getAddress() {
		return address;
	}
	
	@Override
	public String toString() {
		return stringRepresentation;
	}

}
