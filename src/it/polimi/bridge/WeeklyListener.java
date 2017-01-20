package it.polimi.bridge;

import java.util.HashMap;

import javax.swing.JTree;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class WeeklyListener implements UpdateListener {
	
	HashMap<String, HashMap<String, HashMap<String, String>>> hmap = new HashMap<String, HashMap<String, HashMap<String, String>> >();
	String lastDeviceId = null, lastDate = null;
	Double lastPowerConsumptionValue = null;
    private JTree tree;
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		
	} 

}
