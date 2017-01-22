package it.polimi.bridge;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.espertech.esper.client.EventBean; 
import com.espertech.esper.client.UpdateListener;

import it.polimi.gui.EntryPoint;

/**
 * OverallHouseListener
 *  
 * Listeners implement the UpdateListener 
 * interface and act on EventBean instances as the next code snippet outlines 
 *  
 * @author Francesco Tria 
 * @class OverallHouseListener
 * 
 */ 
public class OverallHouseListener implements UpdateListener { 
	// First HashMap
	// Primary key: deviceId 
	// Value: new HashMap<String, String>
	// Primary key: date of rilevation 
	// Value: power consumption 
	HashMap<String, Double> houseMap = new HashMap<String, Double>();
    private JTree tree;


	@Override 
	 public void update(EventBean[] newData, EventBean[] oldData) {
		 //EventBean is a wrapper for the serialized events
		 EventBean event = newData[0];
		 @SuppressWarnings("unchecked")
		 HashMap<String, Object> resultmap = (HashMap<String, Object>) event.getUnderlying();
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		 houseMap.put(dateFormat.format(resultmap.get("date")), (Double) resultmap.get("powerConsumptionValue"));
		 updateGUI();
	 }
	 
	 private void updateGUI () {
		
		 // Update GUI with SwingUtilities.invokeLater       	  	
		 SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					System.out.println("--------- OVERALL HOUSE CONSUMPTION VALUES ---------");
		 			 EntryPoint.housePanel.removeAll();
					 DefaultMutableTreeNode root1 = new DefaultMutableTreeNode("Overall House Consumption...");
					 DefaultTreeModel model = new DefaultTreeModel(root1);
					 DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
					 Map<String, Double> treeMap = new TreeMap<String, Double>(
							 new Comparator<String>() {
								 @Override
								 public int compare(String o1, String o2) {
									 return o2.compareTo(o1);
								 }	
							 }
					 );
					 treeMap.putAll(houseMap);
					 for (Map.Entry<String, Double> mentry : treeMap.entrySet()) {	
						 DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode((String) mentry.getKey()+" "+String.format("%.2f", mentry.getValue())+" kwh", true);
						 root.add(deviceNode);
						 System.out.println("Date: "+(String) mentry.getKey()+"  kwh:  "+String.format("%.2f", mentry.getValue()));						 
					 }					 	
					 tree = new JTree(root);
					 expandAllNodes(tree, 0, tree.getRowCount());
					 model.reload(root);
					 JScrollPane sp = new JScrollPane(tree);
					 EntryPoint.housePanel.add(BorderLayout.CENTER, sp);
					 EntryPoint.housePanel.revalidate();
					 EntryPoint.housePanel.repaint();
				}
		 });
 	}
				
	 private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
		 for(int i = startingIndex; i < rowCount; ++i) {
			 tree.expandRow(i);
		 }
		 if(tree.getRowCount() != rowCount)
			 expandAllNodes(tree, rowCount, tree.getRowCount());
	}
}