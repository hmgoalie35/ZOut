package zOut;

import java.io.Serializable;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 7423120658539374238L;
	private int numChecks, numCards;
	private String date, checkSubTotal, cardSubTotal, cashSubTotal,
			registerStartingAmt, subTotal, total;

	public Transaction(String aDate, int numberChecks, String aCheckSubTotal,
			int numberCards, String aCardSubTotal, String aCashSubTotal,
			String registerStartingAmount, String aSubTotal, String theTotal) {
		this.date = aDate;
		this.numChecks = numberChecks;
		this.checkSubTotal = aCheckSubTotal;
		this.numCards = numberCards;
		this.cardSubTotal = aCardSubTotal;
		this.cashSubTotal = aCashSubTotal;
		this.registerStartingAmt = registerStartingAmount;
		this.subTotal = aSubTotal;
		this.total = theTotal;
	}
	
	/**
	 * Accessor for the date this transaction was create
	 * @return the date of the transaction
	 */
	public String getDate(){
		return date;
	}
	
	/**
	 * Accessor for the total of this transaction
	 * @return the total of the transaction
	 */
	public String getTotal(){
		return total;
	}
@Override
public String toString() {
	String str = date + "\n" + "\n" + "Number of Checks: " + numChecks
			+ "\n" + "Check Sub-Total: " + checkSubTotal + "\n" + "\n"
			+ "Number of Credit Cards: " + numCards + "\n"
			+ "Credit Sub-Total: " + cardSubTotal + "\n" + "\n"
			+ "Cash Sub-Total: " + cashSubTotal + "\n" + "\n"
			+ "Register Starting Amount: " + registerStartingAmt + "\n"
			+ "Sub-Total: " + subTotal + "\n" + "Total: " + total + "\n"
			+ "------------------------------------";
	return str;
}
}
