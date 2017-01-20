package it.polimi.bridge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.espertech.esper.client.EventBean; 
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.util.DateTime;

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
public class DailyListener_old implements UpdateListener { 
	// First HashMap
	// Primary key: deviceId 
	// Value: new HashMap<String, String>
	// Primary key: date of rilevation 
	// Value: power consumption 
	HashMap<String, HashMap<String, Double>> hmap = new HashMap<String, HashMap<String, Double> >();
	String lastDeviceId = null, lastDate = null;
	Double lastPowerConsumptionValue = null;
    private JTree tree;


	@Override 
	 public void update(EventBean[] newData, EventBean[] oldData) {
		 //EventBean is a wrapper for the serialized events
		 EventBean event = newData[0];
				
	/*	 // First access (each parameter must be null) 
		 if (lastDeviceId == null && lastDate == null && lastPowerConsumptionValue == null) {
			 SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			 lastDeviceId = (String) resultmap.get("deviceId");
			 lastDate =  dateFormatter.format(resultmap.get("date"));
			 lastPowerConsumptionValue = (Double) resultmap.get("powerConsumptionValue");
		 } else if (lastDeviceId.equals(resultmap.get("deviceId")) && lastDate.equals(dateFormat.format(resultmap.get("date")))) {
			 lastPowerConsumptionValue += (Double) resultmap.get("powerConsumptionValue");
			 HashMap<String, Double> valueMap = hmap.get((String)resultmap.get("deviceId"));
			 valueMap.put(dateFormat.format(resultmap.get("date")), lastPowerConsumptionValue);
			 hmap.put((String)resultmap.get("deviceId"), valueMap);
			 updateGUI();
			 
			 return;
		 } else {
			 lastDeviceId = (String) resultmap.get("deviceId");
			 lastDate = dateFormat.format(resultmap.get("date"));
			 lastPowerConsumptionValue = (Double) resultmap.get("powerConsumptionValue");
		 }
		 // first rilevation or device id and date changed
		 HashMap<String, Double> date_power = hmap.get(lastDeviceId) != null ? hmap.get(lastDeviceId) : (new HashMap<String, Double>()) ;
		 date_power.put(lastDate, lastPowerConsumptionValue);
		 hmap.put(lastDeviceId, date_power) ;
		 updateGUI();*/
	 }
	 
	 private void updateGUI () {
		
		 // Update GUI with SwingUtilities.invokeLater       	  	
		 SwingUtilities.invokeLater(new Runnable() {
				@SuppressWarnings("rawtypes")
				public void run() {
		 			 EntryPoint.panelDaily.removeAll();
					 DefaultMutableTreeNode root1 = new DefaultMutableTreeNode("Consumption values");
					 DefaultTreeModel model = new DefaultTreeModel(root1);
					 DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
					 Set<?> set = hmap.entrySet();
					 Iterator<?> iterator = set.iterator();
					 int i = 0;
					 while (iterator.hasNext()) {
						 Map.Entry mentry = (Map.Entry)iterator.next();		
						 String key = (String) mentry.getKey();
						 DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("Device id : "+(String) mentry.getKey(), true);
						 root.add(deviceNode);
						 
						 System.out.println(key+"-----");
						 HashMap<String, Double> date_power = (HashMap<String, Double>) mentry.getValue();
						 
						 Map<String, Double> treeMap = new TreeMap<String, Double>(
					                new Comparator<String>() {

					                    @Override
					                    public int compare(String o1, String o2) {
					                        return o2.compareTo(o1);
					                    }

					                });
						 treeMap.putAll(date_power);
						 for (Map.Entry<String, Double> second_mentry : treeMap.entrySet()) {				
							 String dt = (String) second_mentry.getKey();
							 Double powerConsumption = (Double) second_mentry.getValue();
							 model.insertNodeInto(new DefaultMutableTreeNode(dt + "   " +String.format("%.2f", powerConsumption)+" kwh"), deviceNode, deviceNode.getChildCount());
							// deviceNode.add(new DefaultMutableTreeNode(dt + "   " +String.format("%.2f", powerConsumption)+" kwh"));
							 System.out.println(dt+"   "+powerConsumption);
							 
						 }
						 // root.add(deviceNode);
						
						 System.out.println("----");
						
						 i++;
					 }
					 	
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
		    for(int i=startingIndex;i<rowCount;++i){
		        tree.expandRow(i);
		    }

		    if(tree.getRowCount()!=rowCount){
		        expandAllNodes(tree, rowCount, tree.getRowCount());
		    }
		}
}