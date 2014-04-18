package zOut;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DeleteTransactionWindow {
	private static JFrame frame;
	private static boolean isFrameCreated;
	private JLabel instructionLabel;
	private JList<String> jList;
	private String[] transList;

	/**
	 * Constructor that sets up the necessary components. We check to see if
	 * this frame was already created and if it was then we dispose it and
	 * create a new one. this is done in case the user presses the view
	 * transaction history window a few times in a row.
	 */
	public DeleteTransactionWindow() {	
		if(!ViewTransactionHistoryWindow.isFrameCreated()){
			new ViewTransactionHistoryWindow();
		}
		if (isFrameCreated) {
			frame.dispose();
			isFrameCreated = false;
		}
		frame = new JFrame("Delete a Transaction");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				ViewTransactionHistoryWindow.class
						.getResource("/zOut/zOutIcon.gif")));
		isFrameCreated = true;

		frame.setBounds(500, 200, 208, 250);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new frameWindowListener());
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		transList = new String[ZOutGUI.getTransactionList().size()];
		int i = 0;
		for (Transaction t : ZOutGUI.getTransactionList()) {
			transList[i] = t.getDate();
			i++;
		}
		instructionLabel = new JLabel("Select The Transaction To Be Deleted");
		instructionLabel.setBounds(10, 3, 250, 14);
		frame.getContentPane().add(instructionLabel);

		jList = new JList<String>(transList);
		jList.setBounds(40, 20, 160, 190);
		jList.setVisibleRowCount(transList.length / 2);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.addListSelectionListener(new listListener());
		JScrollPane scroll = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(10, 20, 180, 200);
		scroll.setViewportView(jList);
		frame.getContentPane().add(scroll);
		
		frame.setVisible(true);
	}

	/**
	 * Invoked when the user presses the X to close the window. Closes the view
	 * transaction history window and also closes the search window if it was
	 * opened. if it wasnt then nothing happens. the
	 */
	public static void closeDeleteTransactionWindow() {
		frame.dispose();
		isFrameCreated = false;
	}

	/**
	 * Getter for if the main frame has been created. *Note* this is static
	 * 
	 * @return true if the frame has been created, false otherwise
	 */
	public static boolean isFrameCreated() {
		return isFrameCreated;
	}

	/**
	 * ActionListener for when the X is pressed to close the window. Calls the
	 * closeTransHistoryWindow() method.
	 * 
	 * @author Harris
	 */
	private class frameWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			closeDeleteTransactionWindow();
		}
	}

	private class listListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			try {
				Thread.sleep(120);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int i = JOptionPane.showConfirmDialog(frame,
					"Do You Really Want To Delete " + "\n" + jList.getSelectedValue()
							+ "?", "Confirm Deletion",
					JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {
				ZOutGUI.deleteTransaction(jList.getSelectedIndex());
				JOptionPane.showMessageDialog(frame, "      Deletion Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
				frame.dispose();
				if(ViewTransactionHistoryWindow.isFrameCreated()){
					ViewTransactionHistoryWindow.closeTransHistoryWindow();
					new ViewTransactionHistoryWindow();
				}
			}
		}
	}
}