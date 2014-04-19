package zOut;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JProgressBar;

import java.awt.Color;

/**
 * Create the Graphical User Interface
 * 
 * @author Harris Pittinsky
 */
public class ZOutGUI {
	// Constants
	private final int TWO_DOLLAR = 2;
	private final int FIVE = 5;
	private final int TEN = 10;
	private final int TWENTY = 20;
	private final int FIFTY = 50;
	private final int HUNDRED = 100;
	private final double PENNY = .01;
	private final double NICKEL = .05;
	private final double DIME = .10;
	private final double QUARTER = .25;
	// GUI Components
	private JFrame mainWindow, helpFrame;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, viewMenu;
	private JMenuItem restartMenuItem, viewHistoryMenuItem, helpMenuItem,
			deleteHistoryMenuItem, quitMenuItem, deleteCreditMenuItem,
			deleteCheckMenuItem, deleteTransactionMenuItem;
	private JTextField oneEntry, twoEntry, fiveEntry, tenEntry, twentyEntry,
			fiftyEntry, hundredEntry, creditEntry, checkEntry, pennyEntry,
			nickelEntry, dimeEntry, quarterEntry, modifyEntry;
	private JLabel onesLabel, twoLabel, fiveLabel, tenLabel, twentyLabel,
			fiftyLabel, hundredLabel, creditCountLabel, checkCountLabel,
			pennyLabel, nickelLabel, dimeLabel, quarterLabel, creditLabel,
			checkLabel, totalLabel, totalVar, registerLabel, subtotalLabel,
			subVar, creditAmtLabel, checkAmtLabel, amountLabel, dollarAmtLabel,
			twoAmtLabel, fiveAmtLabel, tenAmtLabel, twentyAmtLabel,
			fiftyAmtLabel, hundredAmtLabel, pennyAmtLabel, nickelAmtLabel,
			dimeAmtLabel, quarterAmtLabel;
	private JButton modifyBtn, calculateBtn, okButton, resetFieldsBtn;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	// Misc.
	private NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
	private ArrayList<Double> checkList, creditCardList; // list appended to whenever cc or check is added.
	private static ArrayList<Transaction> transactionList = new ArrayList<>();
	private double dollarVar = 0, twoVar = 0, fiveVar = 0, tenVar = 0,
			twentyVar = 0, fiftyVar = 0, hundredVar = 0, pennyVar = 0,
			nickelVar = 0, dimeVar = 0, quarterVar = 0, creditVar = 0,
			checkVar = 0, moneyAmount = 0, total = 0;

	private String modifyData;
	private static String path;
	private File file, dir;
	private static File fileStatic;
	private Properties properties;

	/*
	 * We implement a progress bar so the user can more easily see that the
	 * calculation has occurred.
	 */
	private JProgressBar progressBar;
	private Timer timer;

	/**
	 * Constructor that sets up the necessary folder if it does not already
	 * exist as well as the History.zof file if it does not exist. Sets up
	 * necessary components.
	 */
	public ZOutGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			JOptionPane
					.showMessageDialog(
							mainWindow,
							"Can't Set Default Look and Feel for this OS \n Default L&F will be used",
							"Error", JOptionPane.ERROR_MESSAGE);
		}

		timer = new Timer(2, new TimerListener());
		// Create folder/files if aren't already created.
		dir = new File("History"); // creates folder with this name.
		file = new File(dir, "History.zof");// creates file in the above folder with the name History.zof
		fileStatic = new File(dir, "History.zof");
		File propertyFile = new File(dir, "properties.txt");// creates the properties.txt file in the folder specified above.
		properties = new Properties();
		/*
		 * create the History folder and its contents if they do not exist
		 */
		try {
			if (!dir.exists()) {
				dir.mkdir();
				file.createNewFile();
				propertyFile.createNewFile();
			} else if (!file.exists()) {
				file.createNewFile();
			} else if (!propertyFile.exists()) {
				propertyFile.createNewFile();
			}
			path = dir.getPath();
			file.createNewFile();
		} catch (FileNotFoundException arg) {
			JOptionPane
					.showMessageDialog(
							mainWindow,
							"History file not created, please delete the History folder and restart the program",
							"Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			JOptionPane
					.showMessageDialog(
							mainWindow,
							"History file not created, please delete the History folder and restart the program",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
		try {
			properties.load(new FileInputStream(path + "/properties.txt"));
			if (properties.getProperty("RegisterAmount") == null) {
				try {
					properties.setProperty("RegisterAmount", "100");
					properties.store(new FileOutputStream(path
							+ "/properties.txt"), "Register Starting Amount");
				} catch (FileNotFoundException e1) {
					JOptionPane
							.showMessageDialog(
									mainWindow,
									"Cannot locate properties.txt File, please restart the program",
									"Error locating properties.txt",
									JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					JOptionPane
							.showMessageDialog(
									mainWindow,
									"Error reading properties.txt file, please delete the properties.txt file and restart the program",
									"Error reading properties.txt",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (IOException ex) {
			JOptionPane
					.showMessageDialog(
							mainWindow,
							"Cannot locate properties.txt File, please restart the program",
							"Error locating properties.txt",
							JOptionPane.ERROR_MESSAGE);
		}
		/*
		 * Call this function that populates the transactionList with data from
		 * the history.zof file
		 */
		onStartup();

		checkList = new ArrayList<Double>();
		creditCardList = new ArrayList<Double>();

		mainWindow = new JFrame("Z-Out");
		mainWindow.addWindowListener(new MainWindowWindowListener());
		mainWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(
				ZOutGUI.class.getResource("/zOut/zOutIcon.gif")));
		mainWindow.setResizable(false);
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.setBounds(400, 100, 400, 570);

		menuBar = new JMenuBar();
		mainWindow.setJMenuBar(menuBar);

		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		restartMenuItem = new JMenuItem("Restart");
		restartMenuItem.addActionListener(new RestartMenuItemActionListener());
		restartMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_MASK));
		fileMenu.add(restartMenuItem);

		helpMenuItem = new JMenuItem("Help");
		helpMenuItem.addActionListener(new HelpMenuItemActionListener());
		fileMenu.add(helpMenuItem);

		fileMenu.addSeparator();

		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new QuitMenuItemActionListener());
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		fileMenu.add(quitMenuItem);

		editMenu = new JMenu("Edit");
		menuBar.add(editMenu);

		deleteCreditMenuItem = new JMenuItem("Delete a Credit Card");
		deleteCreditMenuItem
				.addActionListener(new DeleteCreditMenuItemActionListener());
		editMenu.add(deleteCreditMenuItem);
		deleteCreditMenuItem.setEnabled(false);

		deleteCheckMenuItem = new JMenuItem("Delete a Check");
		deleteCheckMenuItem.setEnabled(false);
		deleteCheckMenuItem
				.addActionListener(new DeleteCheckMenuItemActionListener());
		editMenu.add(deleteCheckMenuItem);

		editMenu.addSeparator();
		deleteTransactionMenuItem = new JMenuItem("Delete a Transaction");
		deleteTransactionMenuItem.setEnabled(false);
		deleteTransactionMenuItem
				.addActionListener(new DeleteTransactionMenuItemActionListener());
		//enable the delete transaction menu item if there are transactions in the transaction list, otherwise keep it disabled.
		if (transactionList.size() > 0) {
			deleteTransactionMenuItem.setEnabled(true);
		}
		editMenu.add(deleteTransactionMenuItem);

		viewMenu = new JMenu("Transaction History");
		menuBar.add(viewMenu);

		viewHistoryMenuItem = new JMenuItem("View Transaction History");
		viewHistoryMenuItem
				.addActionListener(new ViewHistoryMenuItemActionListener());
		viewHistoryMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_T, InputEvent.CTRL_MASK));
		viewMenu.add(viewHistoryMenuItem);
		viewMenu.addSeparator();
		deleteHistoryMenuItem = new JMenuItem("Delete Transaction History");
		deleteHistoryMenuItem
				.addActionListener(new DeleteHistoryMenuItemActionListener());
		viewMenu.add(deleteHistoryMenuItem);

		mainWindow.getContentPane().setLayout(null);

		mainWindow.getContentPane().setFocusable(true);

		onesLabel = new JLabel("$1");
		onesLabel.setBounds(15, 24, 46, 14);
		mainWindow.getContentPane().add(onesLabel);

		oneEntry = new JTextField();
		oneEntry.addKeyListener(new OneEntryKeyListener());
		oneEntry.setBounds(60, 22, 40, 20);
		mainWindow.getContentPane().add(oneEntry);
		oneEntry.setColumns(10);

		twoLabel = new JLabel("$2");
		twoLabel.setBounds(15, 53, 46, 14);
		mainWindow.getContentPane().add(twoLabel);

		twoEntry = new JTextField();
		twoEntry.addKeyListener(new TwoEntryKeyListener());
		twoEntry.setBounds(60, 50, 40, 20);
		mainWindow.getContentPane().add(twoEntry);
		twoEntry.setColumns(10);

		fiveEntry = new JTextField();
		fiveEntry.addKeyListener(new FiveEntryKeyListener());
		fiveEntry.setBounds(60, 78, 40, 20);
		mainWindow.getContentPane().add(fiveEntry);
		fiveEntry.setColumns(10);

		tenEntry = new JTextField();
		tenEntry.addKeyListener(new TenEntryKeyListener());
		tenEntry.setBounds(60, 106, 40, 20);
		mainWindow.getContentPane().add(tenEntry);
		tenEntry.setColumns(10);

		twentyEntry = new JTextField();
		twentyEntry.addKeyListener(new TwentyEntryKeyListener());
		twentyEntry.setBounds(60, 134, 40, 20);
		mainWindow.getContentPane().add(twentyEntry);
		twentyEntry.setColumns(10);

		fiveLabel = new JLabel("$5");
		fiveLabel.setBounds(15, 81, 46, 14);
		mainWindow.getContentPane().add(fiveLabel);

		tenLabel = new JLabel("$10");
		tenLabel.setBounds(14, 110, 46, 14);
		mainWindow.getContentPane().add(tenLabel);

		twentyLabel = new JLabel("$20");
		twentyLabel.setBounds(14, 139, 46, 14);
		mainWindow.getContentPane().add(twentyLabel);

		fiftyEntry = new JTextField();
		fiftyEntry.addKeyListener(new FiftyEntryKeyListener());
		fiftyEntry.setBounds(60, 162, 40, 20);
		mainWindow.getContentPane().add(fiftyEntry);
		fiftyEntry.setColumns(10);

		hundredEntry = new JTextField();
		hundredEntry.addKeyListener(new HundredEntryKeyListener());
		hundredEntry.setBounds(60, 190, 40, 20);
		mainWindow.getContentPane().add(hundredEntry);
		hundredEntry.setColumns(10);

		fiftyLabel = new JLabel("$50");
		fiftyLabel.setBounds(14, 166, 46, 14);
		mainWindow.getContentPane().add(fiftyLabel);

		hundredLabel = new JLabel("$100");
		hundredLabel.setBounds(13, 193, 46, 14);
		mainWindow.getContentPane().add(hundredLabel);

		pennyEntry = new JTextField();
		pennyEntry.addKeyListener(new PennyEntryKeyListener());
		pennyEntry.setBounds(60, 218, 40, 20);
		mainWindow.getContentPane().add(pennyEntry);
		pennyEntry.setColumns(10);

		nickelEntry = new JTextField();
		nickelEntry.addKeyListener(new NickelEntryKeyListener());
		nickelEntry.setBounds(60, 246, 40, 20);
		mainWindow.getContentPane().add(nickelEntry);
		nickelEntry.setColumns(10);

		dimeEntry = new JTextField();
		dimeEntry.addKeyListener(new DimeEntryKeyListener());
		dimeEntry.setBounds(60, 274, 40, 20);
		mainWindow.getContentPane().add(dimeEntry);
		dimeEntry.setColumns(10);

		quarterEntry = new JTextField();
		quarterEntry.addKeyListener(new QuarterEntryKeyListener());
		quarterEntry.setBounds(60, 302, 40, 20);
		mainWindow.getContentPane().add(quarterEntry);
		quarterEntry.setColumns(10);

		pennyLabel = new JLabel("1\u00A2");
		pennyLabel.setBounds(15, 221, 46, 14);
		mainWindow.getContentPane().add(pennyLabel);

		nickelLabel = new JLabel("5\u00A2");
		nickelLabel.setBounds(15, 249, 46, 14);
		mainWindow.getContentPane().add(nickelLabel);

		dimeLabel = new JLabel("10\u00A2");
		dimeLabel.setBounds(14, 277, 46, 14);
		mainWindow.getContentPane().add(dimeLabel);

		quarterLabel = new JLabel("25\u00A2");
		quarterLabel.setBounds(14, 305, 46, 14);
		mainWindow.getContentPane().add(quarterLabel);

		creditEntry = new JTextField();
		creditEntry.addActionListener(new CreditEntryActionListener());
		creditEntry.setToolTipText("Press enter to add credit card amount");
		creditEntry.setBounds(60, 330, 40, 20);
		mainWindow.getContentPane().add(creditEntry);
		creditEntry.setColumns(10);

		checkEntry = new JTextField();
		checkEntry.addActionListener(new CheckEntryActionListener());
		checkEntry.setToolTipText("Press enter to add check amount");
		checkEntry.setBounds(60, 358, 40, 20);
		mainWindow.getContentPane().add(checkEntry);
		checkEntry.setColumns(10);

		creditLabel = new JLabel("Credit\r\n Card");
		creditLabel.setBounds(1, 333, 62, 14);
		mainWindow.getContentPane().add(creditLabel);

		checkLabel = new JLabel("Check");
		checkLabel.setBounds(10, 361, 46, 14);
		mainWindow.getContentPane().add(checkLabel);

		modifyEntry = new JTextField();
		modifyEntry.setEnabled(false);
		modifyEntry.setEditable(false);
		modifyEntry.setText(properties.getProperty("RegisterAmount"));
		modifyEntry.setBounds(60, 386, 40, 20);
		mainWindow.getContentPane().add(modifyEntry);
		modifyEntry.setColumns(10);

		modifyBtn = new JButton("Modify");
		modifyBtn.addActionListener(new ModifyBtnActionListener());
		modifyBtn.setBounds(110, 385, 89, 23);
		mainWindow.getContentPane().add(modifyBtn);

		registerLabel = new JLabel("<HTML>Register<BR>Amount</HTML>  ");
		registerLabel.setBounds(7, 373, 46, 48);
		mainWindow.getContentPane().add(registerLabel);

		subtotalLabel = new JLabel("Subtotal: ");
		subtotalLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		subtotalLabel.setBounds(15, 417, 85, 14);
		mainWindow.getContentPane().add(subtotalLabel);

		subVar = new JLabel(moneyFormat.format(moneyAmount));
		subVar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		subVar.setBounds(135, 417, 85, 14);
		mainWindow.getContentPane().add(subVar);

		totalLabel = new JLabel("Total: ");
		totalLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		totalLabel.setBounds(20, 444, 46, 14);
		mainWindow.getContentPane().add(totalLabel);

		totalVar = new JLabel(moneyFormat.format(0));
		totalVar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		totalVar.setBounds(135, 444, 85, 14);
		mainWindow.getContentPane().add(totalVar);

		amountLabel = new JLabel("Amount");
		amountLabel.setBounds(142, 0, 46, 14);
		mainWindow.getContentPane().add(amountLabel);

		dollarAmtLabel = new JLabel(moneyFormat.format(dollarVar));
		dollarAmtLabel.setBounds(145, 22, 70, 14);
		mainWindow.getContentPane().add(dollarAmtLabel);

		twoAmtLabel = new JLabel(moneyFormat.format(twoVar));
		twoAmtLabel.setBounds(145, 50, 70, 14);
		mainWindow.getContentPane().add(twoAmtLabel);

		fiveAmtLabel = new JLabel(moneyFormat.format(fiveVar));
		fiveAmtLabel.setBounds(145, 78, 70, 14);
		mainWindow.getContentPane().add(fiveAmtLabel);

		tenAmtLabel = new JLabel(moneyFormat.format(tenVar));
		tenAmtLabel.setBounds(145, 106, 70, 14);
		mainWindow.getContentPane().add(tenAmtLabel);

		twentyAmtLabel = new JLabel(moneyFormat.format(twentyVar));
		twentyAmtLabel.setBounds(145, 134, 70, 14);
		mainWindow.getContentPane().add(twentyAmtLabel);

		fiftyAmtLabel = new JLabel(moneyFormat.format(fiftyVar));
		fiftyAmtLabel.setBounds(145, 162, 70, 14);
		mainWindow.getContentPane().add(fiftyAmtLabel);

		hundredAmtLabel = new JLabel(moneyFormat.format(hundredVar));
		hundredAmtLabel.setBounds(145, 190, 70, 14);
		mainWindow.getContentPane().add(hundredAmtLabel);

		pennyAmtLabel = new JLabel(moneyFormat.format(pennyVar));
		pennyAmtLabel.setBounds(145, 218, 70, 14);
		mainWindow.getContentPane().add(pennyAmtLabel);

		nickelAmtLabel = new JLabel(moneyFormat.format(nickelVar));
		nickelAmtLabel.setBounds(145, 246, 70, 14);
		mainWindow.getContentPane().add(nickelAmtLabel);

		dimeAmtLabel = new JLabel(moneyFormat.format(dimeVar));
		dimeAmtLabel.setBounds(145, 274, 70, 14);
		mainWindow.getContentPane().add(dimeAmtLabel);

		quarterAmtLabel = new JLabel(moneyFormat.format(quarterVar));
		quarterAmtLabel.setBounds(145, 302, 70, 14);
		mainWindow.getContentPane().add(quarterAmtLabel);

		creditAmtLabel = new JLabel(moneyFormat.format(creditVar));
		creditAmtLabel.setBounds(145, 329, 70, 14);
		mainWindow.getContentPane().add(creditAmtLabel);

		checkAmtLabel = new JLabel(moneyFormat.format(checkVar));
		checkAmtLabel.setBounds(145, 357, 70, 14);
		mainWindow.getContentPane().add(checkAmtLabel);

		calculateBtn = new JButton("Calculate Total");
		calculateBtn.addActionListener(new CalculateBtnActionListener());
		calculateBtn.setBounds(46, 469, 108, 23);
		mainWindow.getContentPane().add(calculateBtn);

		okButton = new JButton("Ok"); // becomes visible after the modify button is pressed.
		okButton.addActionListener(new OkButtonActionListener());
		okButton.setVisible(false);
		okButton.setEnabled(false);
		okButton.setBounds(110, 385, 89, 23);
		mainWindow.getContentPane().add(okButton);

		creditCountLabel = new JLabel(creditCardList.size() + " Credit Card(s)");
		creditCountLabel.setBounds(202, 329, 108, 14);
		mainWindow.getContentPane().add(creditCountLabel);

		checkCountLabel = new JLabel(checkList.size() + " Check(s)");
		checkCountLabel.setBounds(202, 357, 85, 14);
		mainWindow.getContentPane().add(checkCountLabel);

		resetFieldsBtn = new JButton("Reset Fields");
		resetFieldsBtn.addActionListener(new BtnResetFieldsActionListener());
		resetFieldsBtn.setBounds(228, 469, 108, 23);
		mainWindow.getContentPane().add(resetFieldsBtn);

		progressBar = new JProgressBar();
		progressBar.setFont(new Font("Calibri", Font.PLAIN, 11));
		progressBar.setForeground(Color.BLUE);
		progressBar.setString("Calculating...");
		progressBar.setName("");
		progressBar.setBounds(0, 507, 400, 20);
		mainWindow.getContentPane().add(progressBar);

		mainWindow.setVisible(true);
	}

	/**
	 * checks to see if the str parameter can be parsed to an integer of base
	 * 10. ex: "1", "3", are valid while "a", "~" are invalid. any floating
	 * point number like "5.5" is also invalid. the canBeDouble method is used
	 * to test for floating point numbers (decimal numbers)
	 * @param str the string to check and determine if it is a valid integer
	 * @return true if the string can be an integer, false if it cannot.
	 */
	public boolean canBeInt(String str) {
		boolean retVal = false;
		try {
			Integer.parseInt(str, 10);
			retVal = true;
		} catch (NumberFormatException e) {
			retVal = false;
		}
		return retVal;
	}

	/**
	 * checks to see if the str parameter can be parsed to a double of base 10.
	 * ex: "5.5", "1.5", "3" are valid while "@", "f" are invalid. Note--
	 * integer values do work
	 * @param str the string to check and determine if it is a valid double
	 * @return true if the string can be a double, false if it cannot.
	 */
	public boolean canBeDouble(String str) {
		boolean retVal = false;
		try {
			double amount = Double.parseDouble(str);
			if (amount > 0)
				retVal = true;
		} catch (NumberFormatException e) {
			retVal = false;
		}
		return retVal;
	}

	/**
	 * Adds up all of the variables pertaining to the totals of singles, fives,
	 * tens, pennies, nickels, checks, credit cards, etc. Note-- if the user
	 * does not put in any data for a specific entry, this method still works
	 * because the variables are all initialized to 0. (some users may think
	 * they have to put in 0 for all of the entries they did not use, this is
	 * incorrect) Sets the Sub-Total amount to the formatted representation of
	 * all of the variables
	 */
	public void calculateSubTotal() {
		double amount = dollarVar + twoVar + fiveVar + tenVar + twentyVar
				+ fiftyVar + hundredVar + pennyVar + nickelVar + dimeVar
				+ quarterVar + checkVar + creditVar;
		moneyAmount = amount;
		subVar.setText(moneyFormat.format(amount));
	}

	/**
	 * Allows the others classes in this package to access the transactionList.
	 * This is so the other classes can read what objects are in the
	 * transactionList.
	 * @return transactionList
	 */
	static ArrayList<Transaction> getTransactionList() {
		return transactionList;
	}

	/**
	 * Deletes a transaction from the transactionList and then saves the list to file. 
	 * Utilized by the DeleteTransactionWindow class.
	 * @param index the index to delete the transaction from.
	 */
	static void deleteTransaction(int index) {
		transactionList.remove(index);
		saveStatic();
	}

	/**
	 * Invoked whenever the program starts. Reads any data from the History.zof
	 * file and adds it to the transactionList.
	 */
	public void onStartup() {
		boolean done = false;
		transactionList.clear();
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (!done) {
				transactionList.add((Transaction) ois.readObject());
			}
			fis.close();
			ois.close();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(mainWindow,
					"Error: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (EOFException ex) {
			done = true;
		} catch (ClassNotFoundException e1) {
			JOptionPane.showMessageDialog(mainWindow,
					"Error: " + e1.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainWindow,
					"Error: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called whenever a new Transaction object is created. Called in the
	 * CalculateBtnActionListener class. Saves all of the objects in the
	 * transactionList to the file, overwriting anything that was previously in
	 * the file. This is ok though because we just write the data that is in the
	 * transactionList, which contains the data that was previously in the file
	 * (on startup the transactionList is populated with any data that was in
	 * the file)
	 */
	public static void saveStatic() {
		try {
			FileOutputStream fos = new FileOutputStream(fileStatic);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (Transaction t : transactionList) {
				oos.writeObject(t);
			}
			fos.flush();
			fos.close();
			oos.flush();
			oos.close();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null, "Error" + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Error" + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called whenever a new Transaction object is created. Called in the
	 * CalculateBtnActionListener class. Saves all of the objects in the
	 * transactionList to the file, overwriting anything that was previously in
	 * the file. This is ok though because we just write the data that is in the
	 * transactionList, which contains the data that was previously in the file
	 * (on startup the transactionList is populated with any data that was in
	 * the file)
	 */
	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (Transaction t : transactionList) {
				oos.writeObject(t);
			}
			fos.flush();
			fos.close();
			oos.flush();
			oos.close();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(mainWindow,
					"Error" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(mainWindow,
					"Error" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * KeyListener for when a key is pressed in the $1 entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 12 is in the entry, the label will
	 * show $12. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. we do nothing if the
	 * enter key is pressed. recall the values of the label will vary depending
	 * on which entry is being edited. the five entry with 2 in it will display
	 * $10. if the user entered value is invalid a dialog box warning the user
	 * is presented.
	 * @author Harris Pittinsky
	 */
	private class OneEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent arg0) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (arg0.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& arg0.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(oneEntry.getText())) {
					int amount = Integer.parseInt(oneEntry.getText());
					dollarVar = amount;
					calculateSubTotal();
					dollarAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					oneEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(oneEntry.getText())) {
					int amount = Integer.parseInt(oneEntry.getText());
					dollarVar = amount;
					calculateSubTotal();
					dollarAmtLabel.setText(moneyFormat.format(amount));
				} else {
					dollarVar = 0;
					calculateSubTotal();
					dollarAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the $2 entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 12 is in the entry, the label will
	 * show $24. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class TwoEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(twoEntry.getText())) {
					int amount = Integer.parseInt(twoEntry.getText())
							* TWO_DOLLAR;
					twoVar = amount;
					calculateSubTotal();
					twoAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					twoEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(twoEntry.getText())) {
					int amount = Integer.parseInt(twoEntry.getText())
							* TWO_DOLLAR;
					twoVar = amount;
					calculateSubTotal();
					twoAmtLabel.setText(moneyFormat.format(amount));
				} else {
					twoVar = 0;
					calculateSubTotal();
					twoAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the $5 entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 12 is in the entry, the label will
	 * show $60. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class FiveEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(fiveEntry.getText())) {
					int amount = Integer.parseInt(fiveEntry.getText()) * FIVE;
					fiveVar = amount;
					calculateSubTotal();
					fiveAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					fiveEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(fiveEntry.getText())) {
					int amount = Integer.parseInt(fiveEntry.getText()) * FIVE;
					fiveVar = amount;
					calculateSubTotal();
					fiveAmtLabel.setText(moneyFormat.format(amount));
				} else {
					fiveVar = 0;
					calculateSubTotal();
					fiveAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the $10 entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 12 is in the entry, the label will
	 * show $120. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class TenEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent arg0) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (arg0.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& arg0.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(tenEntry.getText())) {
					int amount = Integer.parseInt(tenEntry.getText()) * TEN;
					tenVar = amount;
					calculateSubTotal();
					tenAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					tenEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(tenEntry.getText())) {
					int amount = Integer.parseInt(tenEntry.getText()) * TEN;
					tenVar = amount;
					calculateSubTotal();
					tenAmtLabel.setText(moneyFormat.format(amount));
				} else {
					tenVar = 0;
					calculateSubTotal();
					tenAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the $20 entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 2 is in the entry, the label will
	 * show $40. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class TwentyEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(twentyEntry.getText())) {
					int amount = Integer.parseInt(twentyEntry.getText())
							* TWENTY;
					twentyVar = amount;
					calculateSubTotal();
					twentyAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					twentyEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(twentyEntry.getText())) {
					int amount = Integer.parseInt(twentyEntry.getText())
							* TWENTY;
					twentyVar = amount;
					calculateSubTotal();
					twentyAmtLabel.setText(moneyFormat.format(amount));
				} else {
					twentyVar = 0;
					calculateSubTotal();
					twentyAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the $50 entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 2 is in the entry, the label will
	 * show $100. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class FiftyEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(fiftyEntry.getText())) {
					int amount = Integer.parseInt(fiftyEntry.getText()) * FIFTY;
					fiftyVar = amount;
					calculateSubTotal();
					fiftyAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					fiftyEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(fiftyEntry.getText())) {
					int amount = Integer.parseInt(fiftyEntry.getText()) * FIFTY;
					fiftyVar = amount;
					calculateSubTotal();
					fiftyAmtLabel.setText(moneyFormat.format(amount));
				} else {
					fiftyVar = 0;
					calculateSubTotal();
					fiftyAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the $100 entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 5 is in the entry, the label will
	 * show $500. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class HundredEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(hundredEntry.getText())) {
					int amount = Integer.parseInt(hundredEntry.getText())
							* HUNDRED;
					hundredVar = amount;
					calculateSubTotal();
					hundredAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					hundredEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(hundredEntry.getText())) {
					int amount = Integer.parseInt(hundredEntry.getText())
							* HUNDRED;
					hundredVar = amount;
					calculateSubTotal();
					hundredAmtLabel.setText(moneyFormat.format(amount));
				} else {
					hundredVar = 0;
					calculateSubTotal();
					hundredAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the 1¢ entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 1 is in the entry, the label will
	 * show $.01. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class PennyEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(pennyEntry.getText())) {
					double amount = Integer.parseInt(pennyEntry.getText())
							* PENNY;
					pennyVar = amount;
					calculateSubTotal();
					pennyAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					pennyEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(pennyEntry.getText())) {
					double amount = Integer.parseInt(pennyEntry.getText())
							* PENNY;
					pennyVar = amount;
					calculateSubTotal();
					pennyAmtLabel.setText(moneyFormat.format(amount));
				} else {
					pennyVar = 0;
					calculateSubTotal();
					pennyAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the 5¢ entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 12 is in the entry, the label will
	 * show $.60. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class NickelEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(nickelEntry.getText())) {
					double amount = Integer.parseInt(nickelEntry.getText())
							* NICKEL;
					nickelVar = amount;
					calculateSubTotal();
					nickelAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					nickelEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(nickelEntry.getText())) {
					double amount = Integer.parseInt(nickelEntry.getText())
							* NICKEL;
					nickelVar = amount;
					calculateSubTotal();
					nickelAmtLabel.setText(moneyFormat.format(amount));
				} else {
					nickelVar = 0;
					calculateSubTotal();
					nickelAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the 10¢ entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 12 is in the entry, the label will
	 * show $1.20. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class DimeEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(dimeEntry.getText())) {
					double amount = Integer.parseInt(dimeEntry.getText())
							* DIME;
					dimeVar = amount;
					calculateSubTotal();
					dimeAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					dimeEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(dimeEntry.getText())) {
					double amount = Integer.parseInt(dimeEntry.getText())
							* DIME;
					dimeVar = amount;
					calculateSubTotal();
					dimeAmtLabel.setText(moneyFormat.format(amount));
				} else {
					dimeVar = 0;
					calculateSubTotal();
					dimeAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * KeyListener for when a key is pressed in the 25¢ entry. this updates the
	 * corresponding label under the "Amount" column provided the key pressed is
	 * a valid integer, or if the backspace or enter keys are pressed. if the
	 * backspace key is pressed the fields as above are updated to the new
	 * amount specified by the entry. ex: if 1 is in the entry, the label will
	 * show $.25. if backspace is pressed then the entry will show 1 and the
	 * label will show $1. if backspace is pressed so that there is no more data
	 * in the entry, the label is set to $0. whenever a key is pressed the
	 * subtotal at the bottom of the gui is also updated. recall the values of
	 * the label will vary depending on which entry is being edited. the five
	 * entry with 2 in it will display $10. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class QuarterEntryKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyChar() != KeyEvent.VK_ENTER) {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				if (canBeInt(quarterEntry.getText())) {
					double amount = Integer.parseInt(quarterEntry.getText())
							* QUARTER;
					quarterVar = amount;
					calculateSubTotal();
					quarterAmtLabel.setText(moneyFormat.format(amount));
				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					quarterEntry.setText(null);
				}
			} else {
				totalVar.setText(moneyFormat.format(0));
				total = 0;
				calculateSubTotal();
				if (canBeInt(quarterEntry.getText())) {
					double amount = Integer.parseInt(quarterEntry.getText())
							* QUARTER;
					quarterVar = amount;
					calculateSubTotal();
					quarterAmtLabel.setText(moneyFormat.format(amount));
				} else {
					quarterVar = 0;
					calculateSubTotal();
					quarterAmtLabel.setText(moneyFormat.format(0));
				}
			}
		}
	}

	/**
	 * ActionListener for when the enter key is pressed in the credit card
	 * entry. this updates the corresponding label under the "Amount" column
	 * provided the data entered into the entry is a valid double. if the data
	 * is valid the label showing the amount of credit cards is also updated to
	 * show the size of the current list, which is one more than the previous
	 * amount. the subtotal at the bottom of the gui is also updated. the valid
	 * value that was entered by the user is added to a list so it is easy to
	 * remove these values from the subtotal and the current credit card sum and
	 * # of credit cards if the user chooses they want to remove it. if the user
	 * entered value is invalid a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class CreditEntryActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (canBeDouble(creditEntry.getText())) {
				double amount = Double.parseDouble(creditEntry.getText());
				creditVar += amount;
				calculateSubTotal();
				creditAmtLabel.setText(moneyFormat.format(creditVar));
				creditCardList.add(amount);
				creditCountLabel.setText(creditCardList.size()
						+ " Credit Card(s)");
				creditEntry.setText(null);
				deleteCreditMenuItem.setEnabled(true);
			} else {
				JOptionPane.showMessageDialog(mainWindow,
						"Please Enter a Valid Number", "Invalid Entry",
						JOptionPane.ERROR_MESSAGE);
				creditEntry.setText(null);

			}
		}
	}

	/**
	 * ActionListener for when the enter key is pressed in the check entry. this
	 * updates the corresponding label under the "Amount" column provided the
	 * data entered into the entry is a valid double. if the data is valid the
	 * label showing the amount of checks is also updated to show the size of
	 * the current list which is one more than the previous amount. the subtotal
	 * at the bottom of the gui is also updated. the valid value that was
	 * entered by the user is added to a list so it is easy to remove these
	 * values from the subtotal and the current check sum and # of checks if the
	 * user chooses they want to remove it. if the user entered value is invalid
	 * a dialog box warning the user is presented.
	 * 
	 * @author Harris Pittinsky
	 */
	private class CheckEntryActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			calculateBtn.setEnabled(true);
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			if (canBeDouble(checkEntry.getText())) {
				double amount = Double.parseDouble(checkEntry.getText());
				checkVar += amount;
				calculateSubTotal();
				checkAmtLabel.setText(moneyFormat.format(checkVar));
				checkList.add(amount);
				checkCountLabel.setText(checkList.size() + " Check(s)");
				checkEntry.setText(null);
				deleteCheckMenuItem.setEnabled(true);
			} else {
				JOptionPane.showMessageDialog(mainWindow,
						"Please Enter a Valid Number", "Invalid Entry",
						JOptionPane.ERROR_MESSAGE);
				checkEntry.setText(null);
			}
		}
	}

	/**
	 * ActionListener for when the Modify button is pressed. This button allows
	 * the user to change the amount that is to be subtracted from the subtotal
	 * to calculate the final total. Generally registers start with $100 so that
	 * is the default. when this button is pressed all other entries and buttons
	 * except the newly shown ok button are disabled. the edit and Transaction-
	 * History menus are also disabled. the corresponding entry is now enabled
	 * and the user can edit the amount. by default the entry is disabled.
	 * 
	 * @author Harris Pittinsky
	 */
	private class ModifyBtnActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			modifyData = modifyEntry.getText();

			oneEntry.setEnabled(false);
			twoEntry.setEnabled(false);
			fiveEntry.setEnabled(false);
			tenEntry.setEnabled(false);
			twentyEntry.setEnabled(false);
			fiftyEntry.setEnabled(false);
			hundredEntry.setEnabled(false);
			pennyEntry.setEnabled(false);
			nickelEntry.setEnabled(false);
			dimeEntry.setEnabled(false);
			quarterEntry.setEnabled(false);
			creditEntry.setEnabled(false);
			checkEntry.setEnabled(false);
			calculateBtn.setEnabled(false);

			editMenu.setEnabled(false);
			viewMenu.setEnabled(false);
			modifyEntry.setEnabled(true);
			modifyEntry.setEditable(true);
			modifyEntry.requestFocus();
			modifyEntry.setCaretPosition(modifyEntry.getText().length());
			okButton.setVisible(true);
			okButton.setEnabled(true);
			modifyBtn.setVisible(false);
			resetFieldsBtn.setEnabled(false);

			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);

		}
	}

	/**
	 * ActionListener for when the ok button is pressed. this button is only
	 * visible after the modify button has been pressed. if the user has entered
	 * data that can be a valid double, we prompt the user if they really want
	 * to change the amount and if yes we set the modify entry text to the new
	 * amount and enable all of the buttons and menus that were disabled when
	 * the modify button was pressed. The modify entry is then disabled again.
	 * (until the user presses the modify button again) if the user decides they
	 * dont want to change the value all of the previously disabled buttons and
	 * menus are enabled, the modify entry is disabled and the value that was
	 * previously stored in the modifyEntry (before the user pressed the modify
	 * button) is then what the modify entry text is set to. if a valid number
	 * is not entered, a dialog box warning the user pops up.
	 * 
	 * @author Harris Pittinsky
	 */
	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (canBeDouble(modifyEntry.getText())) {
				int i = JOptionPane.showConfirmDialog(mainWindow,
						"Are you sure you want to change the register starting amount to $"
								+ modifyEntry.getText() + "?", "Are You Sure?",
						JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION) {
					okButton.setEnabled(false);
					okButton.setVisible(false);
					modifyBtn.setVisible(true);
					modifyEntry.setEnabled(false);
					modifyEntry.setEditable(false);

					oneEntry.setEnabled(true);
					twoEntry.setEnabled(true);
					fiveEntry.setEnabled(true);
					tenEntry.setEnabled(true);
					twentyEntry.setEnabled(true);
					fiftyEntry.setEnabled(true);
					hundredEntry.setEnabled(true);
					pennyEntry.setEnabled(true);
					nickelEntry.setEnabled(true);
					dimeEntry.setEnabled(true);
					quarterEntry.setEnabled(true);
					creditEntry.setEnabled(true);
					checkEntry.setEnabled(true);
					calculateBtn.setEnabled(true);
					resetFieldsBtn.setEnabled(true);

					editMenu.setEnabled(true);
					viewMenu.setEnabled(true);
					oneEntry.requestFocus();

					try {
						properties.setProperty("RegisterAmount",
								modifyEntry.getText());
						properties.store(new FileOutputStream(path
								+ "/properties.txt"),
								"Register Starting Amount");
					} catch (FileNotFoundException e1) {
						JOptionPane
								.showMessageDialog(
										mainWindow,
										"Cannot locate properties.txt File, please restart the program",
										"Error locating properties.txt",
										JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						JOptionPane
								.showMessageDialog(
										mainWindow,
										"Error reading properties.txt file, please delete the properties.txt file and restart the program",
										"Error reading properties.txt",
										JOptionPane.ERROR_MESSAGE);
					}
				} else if (i == JOptionPane.NO_OPTION) {
					okButton.setEnabled(false);
					okButton.setVisible(false);
					modifyBtn.setVisible(true);
					modifyEntry.setEnabled(false);
					modifyEntry.setEditable(false);
					oneEntry.setEnabled(true);
					twoEntry.setEnabled(true);
					fiveEntry.setEnabled(true);
					tenEntry.setEnabled(true);
					twentyEntry.setEnabled(true);
					fiftyEntry.setEnabled(true);
					hundredEntry.setEnabled(true);
					pennyEntry.setEnabled(true);
					nickelEntry.setEnabled(true);
					dimeEntry.setEnabled(true);
					quarterEntry.setEnabled(true);
					creditEntry.setEnabled(true);
					checkEntry.setEnabled(true);
					calculateBtn.setEnabled(true);
					resetFieldsBtn.setEnabled(true);
					editMenu.setEnabled(true);
					viewMenu.setEnabled(true);
					oneEntry.requestFocus();
					modifyEntry.setText(modifyData);
				}
			} else {
				JOptionPane.showMessageDialog(mainWindow,
						"Please Enter a Valid Number", "Invalid Entry",
						JOptionPane.ERROR_MESSAGE);
				modifyEntry.setText(null);
				modifyEntry.requestFocus();
			}
		}
	}

	/**
	 * ActionListener for when the calculate total button is pressed. parse the
	 * amount in the modify entry to a double. this is the amount to be
	 * subtracted from the subtotal to get the final total. if the resulting
	 * total is >= 0 we set the corresponding label to display the new amount in
	 * the gui and get the time, day of week, month, day, year, #checks, check
	 * total, #credit cards, credit card total, cash subtotal, subtotal and
	 * final total and write it to the History.zof file. this is so records of
	 * the transactions are kept. if the subtotal minus the amount in the modify
	 * entry is negative for some reason, it is possible that the user might
	 * have entered data incorrectly, or maybe the business lost money for the
	 * day. Either way the user is asked to confirm that the resulting negative
	 * calculation is correct, and if it is, then the same process as if the
	 * total was >= 0 is performed, writing to file, etc. if the user realizes
	 * they messed up and don't confirm then they are brought back to the gui
	 * where they can edit their inputs. At this point we know there are
	 * transactions in the transaction list so we enable the delete a
	 * transaction button
	 * 
	 * @author Harris Pittinsky
	 */
	private class CalculateBtnActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(creditEntry.getText().equals("") && checkEntry.getText().equals("")){
			
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			double modifyAmt = Double.parseDouble(modifyEntry.getText());
			calculateSubTotal();			
			double amount = moneyAmount - modifyAmt;
			if (amount >= 0) {
				timer.start();
				progressBar.setStringPainted(true);
				totalVar.setText(moneyFormat.format(amount));
				total = amount;
				GregorianCalendar cal = new GregorianCalendar();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"EEEE MM-dd-yyyy hh:mm:ssa");
				String date = dateFormat.format(cal.getTime());
				Transaction transaction = new Transaction(date, checkList.size(), checkVar, creditCardList.size(), creditVar, dollarVar + twoVar + fiveVar+ tenVar + twentyVar + fiftyVar + hundredVar	+ pennyVar + nickelVar + dimeVar + quarterVar, Double.parseDouble(modifyEntry.getText()), moneyAmount, total);
				transactionList.add(transaction);
				deleteTransactionMenuItem.setEnabled(true);
				save();
				calculateBtn.setEnabled(false);
			} else {
				int i = JOptionPane.showConfirmDialog(mainWindow,"Resulting Calculation is Negative \n Are You Sure This is Correct?", "Negative Result",JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION) {
					timer.start();
					progressBar.setStringPainted(true);
					String data = moneyFormat.format(amount);
					data = data.substring(2, data.length() - 1);
					String edited = "$-" + data;
					totalVar.setText(edited);
					total = amount;
					GregorianCalendar cal = new GregorianCalendar();
					SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MM-dd-yyyy hh:mm:ssa");
					String date = dateFormat.format(cal.getTime());
					Transaction transaction = new Transaction(date, checkList.size(), checkVar, creditCardList.size(), creditVar, dollarVar + twoVar + fiveVar+ tenVar + twentyVar + fiftyVar + hundredVar	+ pennyVar + nickelVar + dimeVar + quarterVar, Double.parseDouble(modifyEntry.getText()), moneyAmount, total);
					transactionList.add(transaction);
					deleteTransactionMenuItem.setEnabled(true);
					save();
					calculateBtn.setEnabled(false);
				}else if (i == JOptionPane.NO_OPTION){
					totalVar.setText(moneyFormat.format(0));
					total = 0;
				}
			}
			//refreshes the ViewTransactionHistoryWindow to show the newly added transactions
			if(ViewTransactionHistoryWindow.isFrameCreated()){
				ViewTransactionHistoryWindow.closeTransHistoryWindow();
				new ViewTransactionHistoryWindow();
			}
		}else{
			JOptionPane.showMessageDialog(mainWindow, "A Credit Card and/or Check Entry was not Added, Please Press Enter in the Corresponding Entry Box to Add the Credit Card/Check","Data Not Entered Correctly",JOptionPane.ERROR_MESSAGE);
		}
	}
}

	/**
	 * ActionListener for when the View TransactionHistory menu item is pressed
	 * in the Transaction History menu creates a new
	 * ViewTransactionHistoryWindow. see documentation in this class for further
	 * information
	 * 
	 * @author Harris Pittinsky
	 */
	private class ViewHistoryMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			new ViewTransactionHistoryWindow();
		}
	}

	/**
	 * getter for the path of where the History.zof file is located. Not to be
	 * confused with the getPath() called on the dir in the beginning of the
	 * constructor. Completely different methods. This method is called in the
	 * ViewTransactionHistoryWindow class so the file can be located and read as
	 * input.
	 * 
	 * @return the path where the History.zof file is located.
	 */
	public static String getPath() {
		return path;
	}

	/**
	 * ActionListener for when the restart menu item in the File menu is
	 * pressed. among confirmation from the user all open windows are closed via
	 * the closeAllWindows method. if the user presses no then they are taken
	 * back to the gui
	 * 
	 * @author Harris Pittinsky
	 */
	private class RestartMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int i = JOptionPane.showConfirmDialog(mainWindow,
					"Are You Sure You Want To Restart?", "Restart?",
					JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {
				closeAllWindows();
				new ZOutGUI();
			} else {
				mainWindow.repaint();
			}
		}
	}

	/**
	 * Closes all open windows if they are open. uses the static
	 * closeTransHistoryWindow of the ViewTransactionHistoryWindow class see
	 * documentation in the ViewTransactionHistoryWindow class file. if the
	 * helpFrame is open it is closed also and same with the
	 * DeleteTransactionWindow
	 */
	private void closeAllWindows() {
		try {
			ViewTransactionHistoryWindow.closeTransHistoryWindow();
		} catch (NullPointerException e) {
		}
		try {
			helpFrame.dispose();
		} catch (NullPointerException ex) {
		}
		try {
			DeleteTransactionWindow.closeDeleteTransactionWindow();
		} catch (NullPointerException ex) {
		}
		mainWindow.dispose();
	}

	/**
	 * WindowListener for when the x is pressed in the window. among
	 * confirmation from the user all open windows are closed via the
	 * closeAllWindows method. if the user presses no then they are taken back
	 * to the gui
	 * 
	 * @author Harris Pittinsky
	 */
	private class MainWindowWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			int i = JOptionPane.showConfirmDialog(mainWindow,
					"Are You Sure You Want To Quit?", "Quit?",
					JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {
				closeAllWindows();
			} else {
				mainWindow.repaint();
			}
		}
	}

	/**
	 * ActionListener for when the quit menu item in the File menu is pressed.
	 * among confirmation from the user all open windows are closed via the
	 * closeAllWindows method. if the user presses no then they are taken back
	 * to the gui
	 * 
	 * @author Harris Pittinsky
	 */
	private class QuitMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int i = JOptionPane.showConfirmDialog(mainWindow,
					"Are You Sure You Want To Quit?", "Quit?",
					JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {
				closeAllWindows();
			} else {
				mainWindow.repaint();
			}
		}
	}

	/**
	 * ActionListener for when the help menu item in the File menu is pressed.
	 * creates a frame that displays the help.txt file which contains the help
	 * info if the user is confused about something.
	 * 
	 * @author Harris Pittinsky
	 */
	private class HelpMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			InputStream is = ZOutGUI.class
					.getResourceAsStream("/zOut/Help.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			StringBuilder bldr = new StringBuilder();
			try {
				String line;
				while ((line = in.readLine()) != null) {
					bldr.append(line + "\n");
				}
			} catch (FileNotFoundException arg) {
				JOptionPane.showMessageDialog(mainWindow,
						"Error" + arg.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException arg) {
				JOptionPane.showMessageDialog(mainWindow,
						"Error" + arg.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			try {
				helpFrame.dispose();
			} catch (NullPointerException ex) {
			}
			helpFrame = new JFrame("Help File");
			helpFrame.setBounds(200, 150, 700, 550);
			helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			helpFrame.setResizable(false);

			helpFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(
					ZOutGUI.class.getResource("/zOut/zOutIcon.gif")));
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setFont(new Font(null, Font.PLAIN, 14));
			textArea.setText(bldr.toString());
			textArea.setCaretPosition(0);

			scrollPane = new JScrollPane(textArea,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setWheelScrollingEnabled(true);
			helpFrame.getContentPane().add(scrollPane);

			helpFrame.setVisible(true);
		}

	}

	/**
	 * ActionListener for when the delete transaction history menu item in the
	 * Transaction History menu is pressed. after two confirmations by the user
	 * the transaction history file is cleared of all data it contained. note
	 * that the file itself is not actually deleted, all of the data in the file
	 * is removed so a new clean file is present. the view transaction history
	 * window is also closed when this occurs if it was opened.
	 * 
	 * @author Harris Pittinsky
	 */
	private class DeleteHistoryMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int i = JOptionPane.showConfirmDialog(mainWindow,
					"Do You Really Want To Reset The Transaction History?",
					"Confirmation", JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {
				int j = JOptionPane.showConfirmDialog(mainWindow,
						"Are You Sure, This CANNOT Be Undone", "Confirmation",
						JOptionPane.YES_NO_OPTION);
				if (j == JOptionPane.YES_OPTION) {
					try {
						ViewTransactionHistoryWindow.closeTransHistoryWindow();
					} catch (NullPointerException ex) {
					}
					if (file.exists()) {
						transactionList.clear();
						deleteTransactionMenuItem.setEnabled(false);
						System.gc();
						try {
							Thread.sleep(50);
							if (file.delete()) {
								JOptionPane.showMessageDialog(mainWindow,
										"         History.zof Reset",
										"Reset Successful",
										JOptionPane.INFORMATION_MESSAGE);

							} else {
								JOptionPane.showMessageDialog(mainWindow,
										"         History.zof Reset Failed",
										"Reset Unsuccessful",
										JOptionPane.ERROR_MESSAGE);
							}
						} catch (InterruptedException e1) {
							JOptionPane.showMessageDialog(mainWindow, "Error: "
									+ e1.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);

						}

						// note that the file still exists, a blank file
						// just overwrites the old file, clearing it of any
						// data.
						// the file needs to exists because it is a vital part
						// of the program.
					} else {
						JOptionPane.showMessageDialog(mainWindow,
								"History.zof Does Not Exist",
								"Reset Unsuccessful",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
	}

	/**
	 * ActionListener for when the the delete credit card menu item in the Edit
	 * menu is pressed. An input dialog box is brought up which allows the user
	 * to input a number. If the input is not a number, or that credit card
	 * value does not exist, a warning is displayed to the user. The current
	 * list of credit cards that have been added is also displayed in this input
	 * dialog. If the user did enter a valid credit card amount, then this
	 * amount is removed from the list containing all of the credit cards, and
	 * the subtotal and credit card total label (simply the .size() of the list)
	 * is updated. If the value entered happens to be the last item in the list,
	 * then the delete credit card menu item is disabled, because then there are
	 * no more entries.
	 * 
	 * @author Harris Pittinsky
	 */
	private class DeleteCreditMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			String data = JOptionPane.showInputDialog(mainWindow,
					"Enter the Credit Card Value to Be Deleted \nCurrent List: "
							+ creditCardList, "Delete Credit Card",
					JOptionPane.QUESTION_MESSAGE);
			if (data != null) {
				if (canBeDouble(data)) {
					double value = Double.parseDouble(data);
					if (creditCardList.contains(value)
							&& creditCardList.size() >= 0) {
						creditCardList.remove(value);
						creditVar -= value;
						creditCountLabel.setText(creditCardList.size()
								+ " Credit Card(s)");
						creditAmtLabel.setText(moneyFormat.format(creditVar));
						calculateSubTotal();
						totalVar.setText(moneyFormat.format(0));
						total = 0;
						if (creditCardList.size() == 0) {
							deleteCreditMenuItem.setEnabled(false);
						}
					} else {
						JOptionPane.showMessageDialog(mainWindow,
								"           Card Not Found",
								"Card Not Deleted",
								JOptionPane.INFORMATION_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					deleteCreditMenuItem.doClick();
				}
			}
		}
	}

	/**
	 * ActionListener for when the the delete check menu item in the Edit menu
	 * is pressed. An input dialog box is brought up which allows the user to
	 * input a number. If the input is not a number, or that check value does
	 * not exist, a warning is displayed to the user. The current list of checks
	 * that have been added is also displayed in this input dialog. If the user
	 * did enter a valid check amount, then this amount is removed from the list
	 * containing all of the checks, and the subtotal and check total label
	 * (simply the .size() of the list) is updated. If the value entered happens
	 * to be the last item in the list, then the delete check menu item is
	 * disabled, because then there are no more entries.
	 * 
	 * @author Harris Pittinsky
	 */
	private class DeleteCheckMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			progressBar.setString("Calculating...");
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			String data = JOptionPane.showInputDialog(mainWindow,
					"Enter the Check Value to Be Deleted \nCurrent List: "
							+ checkList, "Delete check",
					JOptionPane.QUESTION_MESSAGE);
			if (data != null) {
				if (canBeDouble(data)) {
					double value = Double.parseDouble(data);
					if (checkList.contains(value) && checkList.size() >= 0) {
						checkList.remove(value);
						checkVar -= value;
						checkCountLabel.setText(checkList.size() + " Check(s)");
						checkAmtLabel.setText(moneyFormat.format(checkVar));
						calculateSubTotal();
						totalVar.setText(moneyFormat.format(0));
						total = 0;
						if (checkList.size() == 0) {
							deleteCheckMenuItem.setEnabled(false);
						}
					} else {
						JOptionPane.showMessageDialog(mainWindow,
								"           Check Not Found",
								"Check Not Deleted",
								JOptionPane.INFORMATION_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(mainWindow,
							"Please Enter a Valid Number", "Invalid Entry",
							JOptionPane.ERROR_MESSAGE);
					deleteCheckMenuItem.doClick();
				}
			}
		}
	}

	/**
	 * ActionListener for when the reset fields button is pressed. When pressed
	 * a confirmation dialog is brought up which prompts the user if they really
	 * want to reset the entries. EVERYTHING!! is cleared, including the credit
	 * card and check lists.
	 * 
	 * @author Harris
	 */
	private class BtnResetFieldsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			int i = JOptionPane.showConfirmDialog(mainWindow,
					"           Reset ->All<- Fields?", "Reset Fields?",
					JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {

				oneEntry.setText(null);
				dollarVar = 0;
				dollarAmtLabel.setText(moneyFormat.format(dollarVar));
				twoEntry.setText(null);
				twoVar = 0;
				twoAmtLabel.setText(moneyFormat.format(twoVar));
				fiveEntry.setText(null);
				fiveVar = 0;
				fiveAmtLabel.setText(moneyFormat.format(fiveVar));
				tenEntry.setText(null);
				tenVar = 0;
				tenAmtLabel.setText(moneyFormat.format(tenVar));
				twentyEntry.setText(null);
				twentyVar = 0;
				twentyAmtLabel.setText(moneyFormat.format(twentyVar));
				fiftyEntry.setText(null);
				fiftyVar = 0;
				fiftyAmtLabel.setText(moneyFormat.format(fiftyVar));
				hundredEntry.setText(null);
				hundredVar = 0;
				hundredAmtLabel.setText(moneyFormat.format(hundredVar));
				pennyEntry.setText(null);
				pennyVar = 0;
				pennyAmtLabel.setText(moneyFormat.format(pennyVar));
				nickelEntry.setText(null);
				nickelVar = 0;
				nickelAmtLabel.setText(moneyFormat.format(nickelVar));
				dimeEntry.setText(null);
				dimeVar = 0;
				dimeAmtLabel.setText(moneyFormat.format(dimeVar));
				quarterEntry.setText(null);
				quarterVar = 0;
				quarterAmtLabel.setText(moneyFormat.format(quarterVar));
				deleteCreditMenuItem.setEnabled(false);
				creditVar = 0;
				creditEntry.setText(null);
				creditAmtLabel.setText(moneyFormat.format(creditVar));
				creditCardList.clear();
				creditCountLabel.setText(creditCardList.size()
						+ " Credit Card(s)");
				deleteCheckMenuItem.setEnabled(false);
				checkVar = 0;
				checkEntry.setText(null);
				checkAmtLabel.setText(moneyFormat.format(checkVar));
				checkList.clear();
				checkCountLabel.setText(checkList.size() + " Check(s)");
				calculateSubTotal();
				totalVar.setText(moneyFormat.format(0));
				total = 0;

				progressBar.setString("Calculating...");
				progressBar.setValue(0);
				progressBar.setStringPainted(false);
				calculateBtn.setEnabled(true);
			}
		}
	}

	/**
	 * Timer listener used to update the progress bar every how ever many
	 * seconds specified.
	 * 
	 * @author Harris
	 * 
	 */
	private class TimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (progressBar.getValue() == HUNDRED) {
				progressBar.setString("Done");
				timer.stop();
			}
			progressBar.setValue(progressBar.getValue() + 1);
		}
	}

	/**
	 * ActionListener that creates new DeleteTransactionWindow whenever the
	 * "Delete a Transaction" menu item is pressed see further documentation in
	 * the DeleteTransactionWindow class file.
	 * 
	 * @author Harris
	 * 
	 */
	private class DeleteTransactionMenuItemActionListener implements
			ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			new DeleteTransactionWindow();
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new ZOutGUI();
	}
}