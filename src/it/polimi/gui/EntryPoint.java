package it.polimi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.Font;


import it.polimi.bridge.Parser;

/**
 * GUI Entrypoint
 *  
 * @author Francesco Tria 
 * @class EntryPoint
 * 
 */ 
public class EntryPoint {

	public static JFrame frame;
	private static JPanel panel;
	public static JPanel panelCenter;
	public static JPanel principalPanel;
	public static JPanel panelDaily;
	public static JPanel panelWeekly;
	public static JPanel housePanel;
	public static JPanel switchStatusPanel;
	public static JButton btnStart;
	public static JTree dailyTree;
	public static JTree weeklyTree;
	public static JTree houseTree;
	public static DefaultTreeModel dailyModel;
	public static DefaultMutableTreeNode dailyRoot;
	public static DefaultTreeModel weeklyModel;
	public static DefaultMutableTreeNode weeklyRoot;
	public static DefaultTreeModel houseModel;
	public static DefaultMutableTreeNode houseRoot;
	private boolean isFirstTime;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new EntryPoint();
					EntryPoint.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EntryPoint() {
		initialize();		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		isFirstTime = true;
		frame = new JFrame("BRiDGe Real Time Monitoring");
		ImageIcon img = new ImageIcon(EntryPoint.class.getResource("/images/favicon.png"));
		frame.setIconImage(img.getImage());
		frame.setMinimumSize(new Dimension(1200,710).getSize());
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setPreferredSize(new Dimension(235, 90));
		panel.setMinimumSize(new Dimension(235, 90));
		Parser p = new Parser();
		p.init();
		
		btnStart = new JButton("");
		btnStart.setFocusPainted(false); 
		btnStart.setIcon(new ImageIcon(EntryPoint.class.getResource("/images/start.png")));
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isFirstTime) {
					JOptionPane.showMessageDialog(EntryPoint.frame, "Please, reopen the program...", "BRiDGe", JOptionPane.PLAIN_MESSAGE);
					
					return;
				}
				isFirstTime = false;
				btnStart.setEnabled(false);
				panelCenter.removeAll();
				panelCenter.revalidate();
				panelCenter.repaint();
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridwidth = 1;
				gbc.gridx = 1;
				gbc.gridy = 1;
				gbc.insets = new Insets(5, 5, 5, 5);
				JPanel loader_p = new JPanel();
				JLabel j = new JLabel(new ImageIcon(EntryPoint.class.getResource("/images/ajax-loader.gif")));
				loader_p.add(j);
				panelCenter.add(loader_p, gbc);
				Thread t1 = new Thread(p);
				t1.start();
			
			}
		});
		panel.setLayout(new BorderLayout(0, 0));

		panel.add(btnStart);	
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		// Left panel ( Last usage results )
		panelCenter = new JPanel(new GridBagLayout());
		panelCenter.setBackground(Color.WHITE);	
		panelCenter.setPreferredSize(new Dimension(240, 90));
		panelCenter.setMinimumSize(new Dimension(240, 90));
	 	final JScrollPane scroll = new JScrollPane(panelCenter);
		frame.getContentPane().add(scroll, BorderLayout.WEST);
		
		// Daily panel
		panelDaily = new JPanel(new BorderLayout(0, 0));		
		panelDaily.setPreferredSize(new Dimension(260, 90));
		panelDaily.setMinimumSize(new Dimension(260, 90));
		final JScrollPane scrollDaily = new JScrollPane(panelDaily);
		frame.getContentPane().add(scrollDaily, BorderLayout.EAST);
		// JTree daily results
		DefaultMutableTreeNode root1 = new DefaultMutableTreeNode("Daily Consumption...");
		dailyModel = new DefaultTreeModel(root1);
		dailyRoot = (DefaultMutableTreeNode) dailyModel.getRoot();
		DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("No values...");
		dailyRoot.add(deviceNode);
		dailyTree = new JTree(dailyRoot);
		dailyModel.reload(dailyRoot);
		JScrollPane sp = new JScrollPane(dailyTree);
	 	panelDaily.add(BorderLayout.CENTER, sp);
	 	
	 	// Centered panel
	 	principalPanel = new JPanel();		
	 	principalPanel.setBackground(Color.WHITE);
		principalPanel.setLayout(new BorderLayout(0, 0));
		
		// Weekly panel
		panelWeekly = new JPanel(new BorderLayout(0, 0));
		panelWeekly.setPreferredSize(new Dimension(250, 90));
		panelWeekly.setMinimumSize(new Dimension(250, 90));
		// JTree daily results
		DefaultMutableTreeNode weekRoot = new DefaultMutableTreeNode("Weekly Consumption...");
		weeklyModel = new DefaultTreeModel(weekRoot);
		weeklyRoot = (DefaultMutableTreeNode) weeklyModel.getRoot();
		DefaultMutableTreeNode weekNode = new DefaultMutableTreeNode("No values...");
		weeklyRoot.add(weekNode);
		weeklyTree = new JTree(weeklyRoot);
		weeklyModel.reload(weeklyRoot);
		JScrollPane sp1 = new JScrollPane(weeklyTree);
		panelWeekly.add(BorderLayout.CENTER, sp1);
		principalPanel.add(panelWeekly, BorderLayout.EAST);
		frame.getContentPane().add(principalPanel, BorderLayout.CENTER);
		
		// Overall house consumption
		housePanel = new JPanel(new BorderLayout(0, 0));
		housePanel.setBackground(Color.WHITE);	
		housePanel.setPreferredSize(new Dimension(250, 90));
		housePanel.setMinimumSize(new Dimension(250, 90));
		// house tree
		DefaultMutableTreeNode houseRoot = new DefaultMutableTreeNode("Overall House energy consumption...");
		houseModel = new DefaultTreeModel(houseRoot);
		houseRoot = (DefaultMutableTreeNode) houseModel.getRoot();
		houseRoot.add(weekNode);
		houseTree = new JTree(houseRoot);
		houseModel.reload(houseRoot);
		JScrollPane sp2 = new JScrollPane(houseTree);
		housePanel.add(BorderLayout.CENTER, sp2);
		principalPanel.add(housePanel, BorderLayout.CENTER);
		
		// Switch status panel
		switchStatusPanel = new JPanel(new GridBagLayout());
		switchStatusPanel.setBackground(Color.WHITE);	
		switchStatusPanel.setPreferredSize(new Dimension(250, 90));
		switchStatusPanel.setMinimumSize(new Dimension(250, 90));
		principalPanel.add(switchStatusPanel, BorderLayout.WEST);
	 	
		noContent(panelCenter, "Last usage...");
		noContent(switchStatusPanel, "Switch status...");
	}
	
	public static void noContent(JPanel panel, String message) {	
			JPanel p = new JPanel();
			p.setBackground(Color.WHITE);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.insets = new Insets(5, 5, 5, 5);

			JLabel label = new JLabel(message);
			label.setBackground(Color.WHITE);
			label.setFont(new Font("Segoe Print", Font.BOLD, 18));
			label.setForeground(new Color(102, 153, 204));
			p.add(label);
						
			panel.add(p,gbc);
			panel.revalidate();
			panel.repaint();
	}

}
