package zOut;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DeleteTransactionWindow {
	private static JFrame frame;
	private static boolean isFrameCreated;
	private JLabel searchByDateLabel;
	private JLabel lblMonth;
	private JLabel lblDay;
	private JLabel lblYear;
    private JComboBox<Integer> monthBox;
    private JComboBox<Integer> dayBox;
    private JComboBox<Integer> yearBox;
    private Integer[] days = new Integer[31];
    private int year;
    private Integer aYear = 0;
    private JButton searchButton;

	/**
	 * Constructor that sets up the necessary components.
	 * We check to see if this frame was already created and if it was then we dispose it and create a new one. this is 
	 * done in case the user presses the view transaction history window a few times in a row. 
	 */
   public DeleteTransactionWindow(){
	   /*Sunday 04-06-2014 08:21:42PM*/
	   /*$390.00*/
		if(isFrameCreated){
			frame.dispose();
			isFrameCreated = false;
		}
		frame = new JFrame("Delete a Transaction");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ViewTransactionHistoryWindow.class.getResource("/zOut/zOutIcon.gif")));
		isFrameCreated = true;
				
		frame.setBounds(600, 200, 275, 110);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new frameWindowListener());
		frame.setResizable(false);	
		frame.getContentPane().setLayout(null);
		
		searchByDateLabel = new JLabel("Search By Date: ");
		searchByDateLabel.setBounds(95, 3, 110, 14);
		frame.getContentPane().add(searchByDateLabel);
		
		lblMonth = new JLabel("Month");
		lblMonth.setBounds(10, 20, 46, 14);
		frame.getContentPane().add(lblMonth);
		
		lblDay = new JLabel("Day");
		lblDay.setBounds(70, 20, 46, 14);
		frame.getContentPane().add(lblDay);
		
		lblYear = new JLabel("Year");
		lblYear.setBounds(126, 20, 46, 14);
		frame.getContentPane().add(lblYear);
		
		Calendar cal = new GregorianCalendar();					 
		Integer numDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};		
		year = cal.get(Calendar.YEAR);
		aYear = year;
		Integer[] years = new Integer[20];
	    days = new Integer[numDays];
		for(int i = 0; i < numDays; i++){
			days[i] = i+1;
		}
		for (int i = year - 19, j = 0; i <= year; i++, j++) {
			years[j] = i;
		}	
		
		dayBox = new JComboBox<Integer>();
		dayBox.setBounds(63, 42, 40, 25);
		dayBox.setFocusable(false);
		dayBox.setBorder(null);
		frame.getContentPane().add(dayBox);
		
		for(int i = 0; i < numDays; i++){
			dayBox.addItem(i+1);
		}
		dayBox.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH) -1);
	
		
		monthBox = new JComboBox<Integer>(months);
		monthBox.setBounds(10, 42, 40, 25);
		monthBox.setSelectedIndex(cal.get(Calendar.MONTH));
		monthBox.setBorder(null);
		monthBox.setFocusable(false);
		monthBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dayBox.removeAllItems();
				Integer month = (Integer) monthBox.getSelectedItem();
				if(month == 9 || month == 4 || month == 11 || month == 6){
					days = new Integer[30];
					for(int i = 0; i < 30; i++){
						dayBox.addItem(i+1);
					}
				}else if(month == 2){
					if(new GregorianCalendar().isLeapYear(aYear)){
						days = new Integer[29];
						for(int i = 0; i < 29; i++){
							dayBox.addItem(i+1);
						}
					}else{
						days = new Integer[28];
						for(int i = 0; i < 28; i++){
							dayBox.addItem(i+1);
						}
					}				
				}else{
					days = new Integer[31];
					for(int i = 0; i < 31; i++){
						dayBox.addItem(i+1);
					}
				}
				dayBox.setSelectedIndex(new GregorianCalendar().get(Calendar.DAY_OF_MONTH) -1);
			}
		});
		
		
		frame.getContentPane().add(monthBox);

		yearBox = new JComboBox<Integer>(years);
		yearBox.setBounds(115, 42, 60, 25);
		yearBox.setSelectedIndex(years.length - 1);
		yearBox.setBorder(null);
		yearBox.setFocusable(false);
		yearBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				aYear = (Integer) yearBox.getSelectedItem();
			}
		});
		frame.getContentPane().add(yearBox);
		
		searchButton = new JButton("Search");
		searchButton.setBounds(184, 45, 75, 20);
		searchButton.setFocusable(false);
		searchButton.addActionListener(new searchBtnListener());
		frame.getContentPane().add(searchButton);
			
		
		
		frame.setVisible(true);		
	}
	/**
	 * Invoked when the user presses the X to close the window. Closes the view transaction history window and also closes
	 * the search window if it was opened. if it wasnt then nothing happens. the 
	 */
	public static void closeDeleteTransactionWindow(){
		frame.dispose();
		isFrameCreated = false;
	}
	/**
	 * Getter for if the main frame has been created.
	 * *Note* this is static
	 * @return true if the frame has been created, false otherwise
	 */
	public static boolean isFrameCreated(){
		return isFrameCreated;
	}
	/**
	 * ActionListener for when the X is pressed to close the window.
	 * Calls the closeTransHistoryWindow() method.
	 * @author Harris
	 */
	private class frameWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			closeDeleteTransactionWindow();
		}
	}
	private class searchBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//resize the frame to show a confirmation box so user can confirm the total
		}
		
	}
	public static void main(String[] args) {
		new DeleteTransactionWindow();
	}
}