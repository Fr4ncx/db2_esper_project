package it.polimi.bridge;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import it.polimi.gui.EntryPoint;


public class BinarySwitchListener implements UpdateListener {
	
	HashMap<String, JSONObject> switchMap = new HashMap<String, JSONObject>();
	@SuppressWarnings("unchecked")
	@Override
	public void update(EventBean[] newData, EventBean[] arg1) {		
		EventBean event = newData[0];
		HashMap<String, String> resultmap = (HashMap<String, String>) event.getUnderlying();
		JSONObject json = new JSONObject();
		json.put("date", resultmap.get("date"));
		json.put("status", resultmap.get("status"));
		switchMap.put(resultmap.get("deviceId"), json) ;	 
		// Update GUI with SwingUtilities.invokeLater       	  	
		SwingUtilities.invokeLater(new Runnable() {
			@SuppressWarnings("rawtypes")
			public void run() {
				Set<?> set = switchMap.entrySet();
				Iterator<?> iterator = set.iterator();
				int  i = 0;	        		     
				JPanel switchStatusPanel = EntryPoint.switchStatusPanel;
				switchStatusPanel.removeAll();
				while (iterator.hasNext()) {
					Map.Entry mentry = (Map.Entry)iterator.next();			
					JPanel p = new JPanel();
					GridBagConstraints gbc = new GridBagConstraints();
					gbc.gridwidth = 1;
					gbc.gridx = 1;
					gbc.gridy = i;
					gbc.insets = new Insets(5, 5, 5, 5);
					JLabel label = new JLabel((String) mentry.getKey());
					label.setFont(new Font("Segoe Print", Font.BOLD, 18));
					label.setForeground(new Color(102, 153, 204));
					p.add(label);
					JSONObject mentryValue = new JSONObject((JSONObject)mentry.getValue());		
					JLabel lblNewLabel = new JLabel(((Boolean)mentryValue.get("status") ? "ON" : "OFF"));
					lblNewLabel.setFont(new Font("Trebuchet MS", Font.BOLD | Font.ITALIC, 12));
					lblNewLabel.setForeground(new Color(102, 153, 153));
					p.add(lblNewLabel);				
					switchStatusPanel.add(p, gbc);		
					i++;				
				}
				// refresh the panel with the current content
				switchStatusPanel.revalidate();
				switchStatusPanel.repaint();
			}
		});	 
	}
}
