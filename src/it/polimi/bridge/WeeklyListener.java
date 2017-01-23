package it.polimi.bridge;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.json.simple.JSONObject;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import it.polimi.gui.EntryPoint;

/**
 * Weekly Listener
 *  
 * Listeners implement the UpdateListener 
 * interface and act on EventBean instances 
 *  
 * @author Francesco Tria 
 * @class WeeklyListener
 * 
 */ 
public class WeeklyListener implements UpdateListener {
	
	HashMap<String, HashMap<String, JSONObject> > weekMap = new HashMap<String, HashMap<String, JSONObject> >();
	private JTree tree;
	SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
	SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
	@SuppressWarnings("unchecked")
	@Override
	public void update(EventBean[] newData, EventBean[] arg1) {
		// TODO Auto-generated method stub
		EventBean event = newData[0];
		HashMap<String, String> resultmap = (HashMap<String, String>) event.getUnderlying();
		System.out.println(resultmap);
		String year = yearFormat.format(resultmap.get("date"));
		String month = monthFormat.format(resultmap.get("date"));
		
		if (weekMap.get((String)resultmap.get("deviceId")) != null) {
			HashMap<String, JSONObject> valueMap = weekMap.get((String)resultmap.get("deviceId"));
			JSONObject currentYear = valueMap.get((String) year);
			JSONObject currentMonth = (JSONObject) (( currentYear.get((String) month) != null ) ? currentYear.get((String) month) : new JSONObject()) ;
			currentMonth.put(resultmap.get("weekOfMonth"), resultmap.get("powerConsumptionValue"));
			currentYear.put((String) month, currentMonth);
			valueMap.put((String) year, currentYear);
			weekMap.put((String)resultmap.get("deviceId"), valueMap);
			updateGUI();
			
			return;
		}
		// First event 
		HashMap<String, JSONObject> valueMap = new HashMap<String, JSONObject> ();
		JSONObject currentMonth = new JSONObject();
		JSONObject currentWeek = new JSONObject();
		currentWeek.put(resultmap.get("weekOfMonth"), resultmap.get("powerConsumptionValue"));
		currentMonth.put((String) month, currentWeek);
		valueMap.put(year, currentMonth);
		weekMap.put((String)resultmap.get("deviceId"), valueMap);
		updateGUI();		
	} 
	
	 private void updateGUI () {		
		 // Update GUI with SwingUtilities.invokeLater       	  	
		 SwingUtilities.invokeLater(new Runnable() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public void run() {
					System.out.println("--------- WEEKLY CONSUMPTION VALUES ---------");
					 EntryPoint.panelWeekly.removeAll();
					 DefaultMutableTreeNode root1 = new DefaultMutableTreeNode("Weekly Consumption...");
					 DefaultTreeModel model = new DefaultTreeModel(root1);
					 DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
					 Set<?> set = weekMap.entrySet();
					 Iterator<?> iterator = set.iterator();
					 while (iterator.hasNext()) {
						 Map.Entry mentry = (Map.Entry)iterator.next();		
						 String key = (String) mentry.getKey(); // device ID
						 DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("Device id : "+(String) mentry.getKey(), true);
						 root.add(deviceNode);
						 
						 System.out.println("DeviceId: "+key);
						 HashMap<String, HashMap<String, JSONObject>>  yearJson = (HashMap<String, HashMap<String, JSONObject>>) mentry.getValue(); // key: YEAR, Value: "month" : { "weekofMonth" : powerConsumptionValue }
						 Set<?> yearSet = yearJson.entrySet();
						 Iterator<?> yearIterator = yearSet.iterator();
						 while (yearIterator.hasNext()) {
							 Map.Entry entry = (Map.Entry) yearIterator.next();
							 String year = (String) entry.getKey(); // year
							 System.out.println("  Year: "+year);
							 DefaultMutableTreeNode yearNode = new DefaultMutableTreeNode(year);
							 model.insertNodeInto(yearNode, deviceNode, deviceNode.getChildCount());
							 HashMap<String, JSONObject>  months = (HashMap<String, JSONObject>) entry.getValue();				 
							 Set<?> monthSet = months.entrySet();
							 Iterator<?> monthIterator = monthSet.iterator();
							 while (monthIterator.hasNext()) {
								 Map.Entry monthEntry = (Map.Entry) monthIterator.next();
								 String month = (String) monthEntry.getKey(); // month
								 System.out.println("     Month: "+month);
								 DefaultMutableTreeNode monthNode = new DefaultMutableTreeNode(month);
								 model.insertNodeInto(monthNode, yearNode, yearNode.getChildCount());
								 JSONObject monthAndPower = (JSONObject) monthEntry.getValue(); // weekofMonth and power value
								 for (Object values : monthAndPower.keySet()) {
								        //based on you key types
								        String keyStr = (String)values;
								        Object keyvalue = monthAndPower.get(keyStr);
								        model.insertNodeInto(new DefaultMutableTreeNode(keyStr+"° week: "+String.format("%.2f", keyvalue)+" kwh"), monthNode, monthNode.getChildCount());
										System.out.println("       - "+keyStr+"° week "+String.format("%.2f", keyvalue)+" kwh");
								 }
							 }					 	
						 }			
					 }					 	
					 tree = new JTree(root);
					 expandAllNodes(tree, 0, tree.getRowCount());
					 model.reload(root);
					 JScrollPane sp = new JScrollPane(tree);
					 EntryPoint.panelWeekly.add(BorderLayout.CENTER, sp);
					 EntryPoint.panelWeekly.revalidate();
					 EntryPoint.panelWeekly.repaint();
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
