package it.polimi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.Font;


import it.polimi.bridge.Parser;


public class EntryPoint {

	public static JFrame frame;
	private static JPanel panel;
	public static JPanel panelCenter;
	public static JPanel panelWeekly;
	public static JPanel panelDaily;
	public static JButton btnStart;
	public static JTree tree;
	public static DefaultTreeModel model;
	public static DefaultMutableTreeNode root;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
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
				btnStart.setEnabled(false);
				panelDaily.removeAll();
				panelDaily.revalidate();
				panelDaily.repaint();
				panelCenter.removeAll();

				panelCenter.revalidate();
				panelCenter.repaint();
				panelWeekly.revalidate();
				panelWeekly.repaint();
				JPanel loader_p = new JPanel();
				JLabel j = new JLabel(new ImageIcon(EntryPoint.class.getResource("/images/ajax-loader.gif")));
				loader_p.add(j);
				panelWeekly.add(loader_p, BorderLayout.CENTER);
			 	panelWeekly.setBackground(Color.WHITE);

				panelWeekly.revalidate();
				panelWeekly.repaint();
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
		panelCenter.setPreferredSize(new Dimension(235, 90));
		panelCenter.setMinimumSize(new Dimension(235, 90));
	 	final JScrollPane scroll = new JScrollPane(panelCenter);
		frame.getContentPane().add(scroll, BorderLayout.WEST);
		
		// Daily panel
		panelDaily = new JPanel(new BorderLayout(0, 0));		
		panelDaily.setPreferredSize(new Dimension(235, 90));
		panelDaily.setMinimumSize(new Dimension(235, 90));
		final JScrollPane scrollDaily = new JScrollPane(panelDaily);
		frame.getContentPane().add(scrollDaily, BorderLayout.EAST);
		// JTree daily results
		DefaultMutableTreeNode root1 = new DefaultMutableTreeNode("Consumption values");
		model = new DefaultTreeModel(root1);
		root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("No values...");
		root.add(deviceNode);
		tree = new JTree(root);
		model.reload(root);
		JScrollPane sp = new JScrollPane(tree);
	 	panelDaily.add(BorderLayout.CENTER, sp);
	 	
	 	// Weekly panel
	 	panelWeekly = new JPanel(new BorderLayout(0, 0));		
	 	panelWeekly.setBackground(Color.WHITE);
	 	panelWeekly.setPreferredSize(new Dimension(235, 90));
	 	panelWeekly.setMinimumSize(new Dimension(235, 90));
		final JScrollPane scrollWeekly = new JScrollPane(panelWeekly);
		frame.getContentPane().add(scrollWeekly, BorderLayout.CENTER);
		
	 	
	 	
		noContent();
	}
	
	public static void noContent() {	
			JPanel p = new JPanel();
			p.setBackground(Color.WHITE);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.insets = new Insets(5, 5, 5, 5);

			JLabel label = new JLabel("Last usage...");
			label.setBackground(Color.WHITE);
			label.setFont(new Font("Segoe Print", Font.BOLD, 18));
			label.setForeground(new Color(102, 153, 204));
			p.add(label);
						
			panelCenter.add(p,gbc);
			panelCenter.revalidate();
			panelCenter.repaint();
	}

}
