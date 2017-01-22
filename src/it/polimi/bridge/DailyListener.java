package it.polimi.bridge;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
 * Daily Listener
 *  
 * Listeners implement the UpdateListener 
 * interface and act on EventBean instances as the next code snippet outlines 
 *  
 * @author Francesco Tria 
 * @class DailyListener
 * 
 */ 
public class DailyListener implements UpdateListener { 
	// First HashMap
	// Primary key: deviceId 
	// Value: new HashMap<String, String>
	// Primary key: date of rilevation 
	// Value: power consumption 
	HashMap<String, HashMap<String, Double>> hmap = new HashMap<String, HashMap<String, Double> >();
    private JTree tree;


	@Override 
	 public void update(EventBean[] newData, EventBean[] oldData) {
		 //EventBean is a wrapper for the serialized events
		 EventBean event = newData[0];
		 @SuppressWarnings("unchecked")
		 HashMap<String, Object> resultmap = (HashMap<String, Object>) event.getUnderlying();
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		 HashMap<String, Double> valueMap = new HashMap<String, Double> ();
		 if (hmap.get((String)resultmap.get("deviceId")) != null)
			 valueMap = hmap.get((String)resultmap.get("deviceId"));
		 
		 valueMap.put(dateFormat.format(resultmap.get("date")), (Double) resultmap.get("powerConsumptionValue"));
		 hmap.put((String)resultmap.get("deviceId"), valueMap);
		 updateGUI();
	 }
	 
	private void updateGUI () {		
		 // Update GUI with SwingUtilities.invokeLater       	  	
		SwingUtilities.invokeLater(new Runnable() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run() {
				System.out.println("--------- DAILY CONSUMPTION VALUES ---------");
				EntryPoint.panelDaily.removeAll();
				DefaultMutableTreeNode root1 = new DefaultMutableTreeNode("Daily Consumption...");
				DefaultTreeModel model = new DefaultTreeModel(root1);
				DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
				Set<?> set = hmap.entrySet();
				Iterator<?> iterator = set.iterator();
				while (iterator.hasNext()) {
					Map.Entry mentry = (Map.Entry)iterator.next();		
					String key = (String) mentry.getKey();
					DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("Device id : "+(String) mentry.getKey(), true);
					root.add(deviceNode);
					
					System.out.println("DeviceId: "+key);
					HashMap<String, Double> date_power = (HashMap<String, Double>) mentry.getValue();				
					Map<String, Double> treeMap = new TreeMap<String, Double>(
							new Comparator<String>() {
								@Override
								public int compare(String o1, String o2) {
									return o2.compareTo(o1);
								}			
							}
					);
					treeMap.putAll(date_power);
					for (Map.Entry<String, Double> second_mentry : treeMap.entrySet()) {				
						String dt = (String) second_mentry.getKey();
						Double powerConsumption = (Double) second_mentry.getValue();
						model.insertNodeInto(new DefaultMutableTreeNode(dt + "   " +String.format("%.2f", powerConsumption)+" kwh"), deviceNode, deviceNode.getChildCount());
						System.out.println("Date: "+dt+"  kwh:  "+powerConsumption);						 
					}	
					
				}
				System.out.println("------------------------------");	 	
				tree = new JTree(root);
				expandAllNodes(tree, 0, tree.getRowCount());
				model.reload(root);
				JScrollPane sp = new JScrollPane(tree);
				EntryPoint.panelDaily.add(BorderLayout.CENTER, sp);
				EntryPoint.panelDaily.revalidate();
				EntryPoint.panelDaily.repaint();
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