package zOut;

import java.io.Serializable;
import java.text.NumberFormat;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 7423120658539374238L;
	private int numChecks, numCards;
	private String date;
	double checkSubTotal, cardSubTotal, cashSubTotal, registerStartingAmt,
			subTotal, total;
	private NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();

	public Transaction(String aDate,
			int numberChecks, double aCheckSubTotal, int numberCards,
			double aCardSubTotal, double aCashSubTotal,
			double registerStartingAmount, double aSubTotal, double theTotal) {
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
	 * 
	 * @return the date of the transaction
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Accessor for the total of this transaction
	 * @return the total of the transaction
	 */
	public double getTotal() {
		return total;
	}
	
	/**
	 * The transaction variables are only formatted to have commas and dollar signs in the toString.
	 * Format the values and return the concatenated string.
	 */
	@Override
	public String toString() {
		String calculatedTotal = null;
		/*
		 * when invoking .format on a negative number formatting utilizes () to signify a negative number.
		 * what is being done below is removing the () and adding a - sign to signify negative, as well as
		 * add the $ sign. if the number was positive then just normally format it. 
		 */
		if(total < 0){
			String data = moneyFormat.format(total);
			data = data.substring(2, data.length() - 1);
		    calculatedTotal = "$-" + data;
		}else{
			calculatedTotal = moneyFormat.format(total);
		}
		String str = date + "\n" + "\n" + "Number of Checks: " + numChecks
				+ "\n" + "Check Sub-Total: "
				+ moneyFormat.format(checkSubTotal) + "\n" + "\n"
				+ "Number of Credit Cards: " + numCards + "\n"
				+ "Credit Sub-Total: " + moneyFormat.format(cardSubTotal)
				+ "\n" + "\n" + "Cash Sub-Total: "
				+ moneyFormat.format(cashSubTotal) + "\n" + "\n"
				+ "Register Starting Amount: "
				+ moneyFormat.format(registerStartingAmt) + "\n"
				+ "Sub-Total: " + moneyFormat.format(subTotal) + "\n"
				+ "Total: " + calculatedTotal + "\n"
				+ "------------------------------------";
		return str;
	}
}
