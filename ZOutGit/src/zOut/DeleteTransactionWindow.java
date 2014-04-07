package zOut;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class DeleteTransactionWindow {
	private static JFrame frame;
	private static boolean isFrameCreated;
	private JLabel searchByDateLabel;
	
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
				
		frame.setBounds(600, 200, 400, 150);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new frameWindowListener());
		frame.setResizable(false);	
		frame.getContentPane().setLayout(null);
		
		searchByDateLabel = new JLabel("Search By Date: ");
		searchByDateLabel.setBounds(10, 11, 110, 14);
		frame.getContentPane().add(searchByDateLabel);
		
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
	public static void main(String[] args) {
		new DeleteTransactionWindow();
	}
}