package net.fmchan.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.apache.log4j.Logger;

import net.fmchan.job.CheckUpdateJob;
import net.fmchan.util.ConfigUtil;
import net.fmchan.util.FtpUtil;

public class Selector extends JPanel {

	private static final long serialVersionUID = -8210785038282182406L;
	final static Logger logger = Logger.getLogger(Selector.class);

	static JFrame frame;
	JLabel result;
	String currentPattern;
	static JComboBox<String> patternList;

	@SuppressWarnings("serial")
	public Selector() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		String[] patternExamples = ConfigUtil.get().getStringArray("queue");

		currentPattern = patternExamples[0];

		// Set up the UI for selecting a pattern.
		JLabel patternLabel1 = new JLabel("Select a queue:");

		patternList = new JComboBox<String>(patternExamples);
		// patternList.setEditable(true);
		// patternList.addActionListener(this);

		// Create the UI for displaying result.
		JLabel resultLabel = new JLabel("Selected:", JLabel.LEADING); // == LEFT
		result = new JLabel(" ");
		result.setForeground(Color.black);

		// Lay out everything.
		JPanel patternPanel = new JPanel();
		patternPanel
				.setLayout(new BoxLayout(patternPanel, BoxLayout.PAGE_AXIS));
		patternPanel.add(patternLabel1);
		patternList.setAlignmentX(Component.LEFT_ALIGNMENT);
		patternPanel.add(patternList);

		JPanel resultPanel = new JPanel(new GridLayout(0, 1));
		resultPanel.add(resultLabel);
		resultPanel.add(result);

		patternPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		resultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		add(patternPanel);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(resultPanel);

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JButton b = new JButton(new AbstractAction("Select") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newSelection = (String) patternList.getSelectedItem();
				currentPattern = newSelection;
				logger.info("gui select queue:" + currentPattern);
				result.setText(currentPattern);
				CheckUpdateJob.setBackupQueue(currentPattern);
			}
		});
		add(b);

		add(Box.createRigidArea(new Dimension(0, 10)));

		JButton s = new JButton(new AbstractAction(" Stop  ") {
			@Override
			public void actionPerformed(ActionEvent e) {
				result.setText("");
				CheckUpdateJob.setBackupQueue("");
			}
		});
		add(s);

		// reformat();
	} // constructor

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Queue Selector");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FtpUtil.closeConnection();
			}
		});

		// Create and set up the content pane.
		JComponent newContentPane = new Selector();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void start() {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}