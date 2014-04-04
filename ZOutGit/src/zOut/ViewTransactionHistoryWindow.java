package zOut;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
/**
 * Creates the window where the transaction history is displayed.
 * @author Harris
 */
public class ViewTransactionHistoryWindow {
	private static JFrame frame, searchFrame;
	private static boolean isFrameCreated, isSearchCreated;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private Scanner in;
	private JMenuBar menuBar;
	private JMenu searchMenu;
	private JMenuItem searchMenuItem;
	private JTextField entry;
	private JCheckBox caseSen;
	private ArrayList<Integer> positions = new ArrayList<Integer>();
	private JButton nextWordButton, previousWordButton;
	private int count = 0, searchedFor;
	private boolean nextBtnPressed, prevBtnPressed;
	
	/**
	 * Constructor that sets up the necessary components.
	 * We check to see if this frame was already created and if it was then we dispose it and create a new one. this is 
	 * done in case the user presses the view transaction history window a few times in a row. 
	 */
	public ViewTransactionHistoryWindow(){
		if(isFrameCreated){
			frame.dispose();
			isFrameCreated = false;
		}
		frame = new JFrame("Transaction History");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ViewTransactionHistoryWindow.class.getResource("/zOut/zOutIcon.gif")));
		isFrameCreated = true;
				
		frame.setBounds(850, 90, 405, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new frameWindowListener());
		frame.setResizable(false);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		
		StringBuilder bldr = new StringBuilder();
		try {
			File history = new File(ZOutGUI.getPath() + "/History.zof");
			in = new Scanner(history);
			while (in.hasNextLine()) {
				bldr.append(in.nextLine() + "\n");
			}
			in.close();
		}catch(NullPointerException ex){
			JOptionPane.showMessageDialog(frame, "Can't Locate History.zof Make Sure It Is In \nthe History FolderOr Delete the Folder\n and Restart The Program", "Can't Located History.zof", JOptionPane.ERROR_MESSAGE);	
		}catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frame,"Cannot Locate History.zof", "Cannot Locate History.zof", JOptionPane.ERROR_MESSAGE);
		}
		in.close();
		if (bldr.length() == 0) {
			textArea.setText("No Transactions Yet");
			textArea.setCaretPosition(0);
		} else{ 
			textArea.setText(bldr.toString());
		}
		

		scrollPane = new JScrollPane(textArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setWheelScrollingEnabled(true);
		frame.getContentPane().add(scrollPane);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		searchMenu = new JMenu("Search");
		menuBar.add(searchMenu);
		
		searchMenuItem = new JMenuItem("Search...");
		searchMenuItem.addActionListener(new SearchMenuItemActionListener());
		searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		searchMenu.add(searchMenuItem);
		
		frame.setVisible(true);		
	}
	/**
	 * Invoked when the user presses the X to close the window. Closes the view transaction history window and also closes
	 * the search window if it was opened. if it wasnt then nothing happens. the 
	 */
	public static void closeTransHistoryWindow(){
		try{
		searchFrame.dispose();
		isSearchCreated = false;
		}catch(NullPointerException ex){
		}
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
				closeTransHistoryWindow();
		}
	}
	/**
	 * ActionListener for when the search menu item is pressed, or invoked via the accelerator(ctrl-f)
	 * if the search frame has already  been created then it is closed and a new one opened up.
	 * This is done in case the user presses the search menu item button multiple times so there
	 * aren't multiple search windows open. 
	 * the search frame that is created allows the user to enter a string and it is highlighted in the 
	 * text area. the user can search via case sensitive or not and there are 2 buttons that allow
	 * the user to go to the next or previous location of the string they are searching for.
	 * @author Harris
	 */
	private class SearchMenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {		
			if (isSearchCreated) {
				try {
					searchFrame.dispose();
					isSearchCreated = false;
				} catch (NullPointerException ex) {
				}
			}
			count = 0;
			searchFrame = new JFrame("Search");
			isSearchCreated = true;
			searchFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(ViewTransactionHistoryWindow.class.getResource("/zOut/zOutIcon.gif")));

			searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			searchFrame.setBounds(600, 200, 230, 85);
			searchFrame.setResizable(false);
			searchFrame.getContentPane().setLayout(null);
			searchFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent arg0) {
					textArea.getHighlighter().removeAllHighlights();
					textArea.setSelectedTextColor(null);
					count = 0;
				}
			});
			entry = new JTextField();
			entry.setBounds(8, 2, 150, 25);

			entry.addActionListener(new entryFieldListener());
			searchFrame.getContentPane().add(entry);

			previousWordButton = new JButton();

			previousWordButton.setIcon(new ImageIcon(ZOutGUI.class.getResource("/zOut/upB.png")));
			previousWordButton.setBounds(190, 3, 20, 23);
			previousWordButton.setToolTipText("Previous Word");
			previousWordButton.addActionListener(new PreviousWordBtnListener());
			searchFrame.getContentPane().add(previousWordButton);

			nextWordButton = new JButton();
			nextWordButton.setIcon(new ImageIcon(ZOutGUI.class.getResource(("/zOut/downB.png"))));
			nextWordButton.setBounds(163, 3, 20, 23);
			nextWordButton.setToolTipText("Next Word");
			nextWordButton.addActionListener(new NextWordBtnListener());
			searchFrame.getContentPane().add(nextWordButton);

			caseSen = new JCheckBox("Case Sensitive");
			caseSen.setBounds(25, 29, 150, 23);
			caseSen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					entry.setText(null);
					textArea.getHighlighter().removeAllHighlights();
					entry.requestFocus();
					count = 0;
					textArea.setSelectionStart(0);
					textArea.setSelectionEnd(0);
				}
			});

			searchFrame.getContentPane().add(caseSen);

			entry.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					count = 0;
					if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
						textArea.setSelectedTextColor(null);
						textArea.getHighlighter().removeAllHighlights();
					} else {
						textArea.setSelectedTextColor(null);
						highlight(textArea, entry.getText());
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					count = 0;
					if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
						textArea.setSelectedTextColor(null);
						textArea.getHighlighter().removeAllHighlights();
						//textArea.setSelectionStart(0);
						//textArea.setSelectionEnd(0);
						highlight(textArea, entry.getText());
					} else {
						textArea.setSelectedTextColor(null);
						highlight(textArea, entry.getText());
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
					count = 0;
					if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
						textArea.setSelectedTextColor(null);
						textArea.getHighlighter().removeAllHighlights();
					} else {
						textArea.setSelectedTextColor(null);
						highlight(textArea, entry.getText());
					}
				}
			});

			searchFrame.setVisible(true);			
		}		
	}
	/**
	 * button listener for the button with the arrow pointing down in the search frame. when invoked selects
	 * the next occurrence of the string that is being searched for, highlighting it in cyan. 
	 * @author Harris
	 */
	private class NextWordBtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				textArea.setSelectedTextColor(Color.CYAN);
				nextBtnPressed = true;
				if (positions.size() > 0) {
					if (count >= 0) {
						if (count < positions.size()) {
							entry.requestFocus();
							if (prevBtnPressed) {
								count++;
								prevBtnPressed = false;
							}
							textArea.setCaretPosition(positions.get(count));
							textArea.setSelectionStart(positions.get(count));
							textArea.setSelectionEnd(positions.get(count)
									+ searchedFor);
							count++;
						} else if (count >= positions.size()) {
							prevBtnPressed = false;
							count = 0;
							textArea.setCaretPosition(positions.get(count));
							textArea.setSelectionStart(positions.get(count));
							textArea.setSelectionEnd(positions.get(count)
									+ searchedFor);
						}
					}
				}
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(searchFrame,
						"Something Went Wrong", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * button listener for the button with the arrow pointing up in the search frame. when invoked selects
	 * the previous occurrence of the string that is being searched for, highlighting it in cyan. 
	 * @author Harris
	 */
	private class PreviousWordBtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				textArea.setSelectedTextColor(Color.CYAN);
				prevBtnPressed = true;
				if (positions.size() > 0) {
					if (count > 0) {
						if (nextBtnPressed) {
							count--;
							nextBtnPressed = false;
						}
						entry.requestFocus();
						count--;
						textArea.setCaretPosition(positions.get(count));
						textArea.setSelectionStart(positions.get(count));
						textArea.setSelectionEnd(positions.get(count)
								+ searchedFor);
					} else if (count == 0) {
						nextBtnPressed = false;
						textArea.setCaretPosition(positions.get(count));
						textArea.setSelectionStart(positions.get(count));
						textArea.setSelectionEnd(positions.get(count)
								+ searchedFor);
						count = positions.size();
					}
				}
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(searchFrame,
						"Something Went Wrong", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	/**
	 * ActionListener for when the enter key is pressed after the desired string is entered.
	 * the occurrences of the string are then highlighted
	 * @author Harris
	 */
	private class entryFieldListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			count = 0;
			highlight(textArea, entry.getText());
		}
	}

	/**
	 * highlights all of the occurrences of the pattern in the textArea. if the case sensitive button is checked
	 * then it highlights all of the patterns in accordance to case sensitive, otherwise it just highlights any occurrence
	 * of the pattern
	 * @param textComp the JTextArea that contains the text to be highlighted
	 * @param pattern the pattern to be searched for and highlighted
	 */
	public void highlight(JTextComponent textComp, String pattern) {
		count = 0;
		textArea.setSelectedTextColor(null);
		textComp.getHighlighter().removeAllHighlights();
		pattern = pattern.trim();
		searchedFor = pattern.length();
		positions.clear();
		String text = "";
		if (pattern.length() > 0) {
			try {
				Highlighter hilite = textComp.getHighlighter();
				Document doc = textComp.getDocument();

				if (caseSen.isSelected()) {
					text = doc.getText(0, doc.getLength());
				} else {
					text = doc.getText(0, doc.getLength()).toLowerCase();
				}
				int pos = 0;
				while ((pos = text.indexOf(pattern, pos)) >= 0) {
					positions.add(pos);
					hilite.addHighlight(pos, pos + pattern.length(),
							new DefaultHighlighter.DefaultHighlightPainter(
									Color.YELLOW));
					pos += pattern.length();
				}
			} catch (BadLocationException e) {
			}
		} else {
			textComp.getHighlighter().removeAllHighlights();
		}
		count = 0;
	}
}