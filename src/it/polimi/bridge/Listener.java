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
import com.espertech.esper.client.EventBean; 
import com.espertech.esper.client.UpdateListener;
import it.polimi.gui.EntryPoint; 

/**
 * Device listener (last usage for each appliance) 
 *  
 * Listeners are invoked by the engine in response to one or more events that change a statement's result set. Listeners implement the UpdateListener 
 * interface and act on EventBean instances as the next code snippet outlines 
 * 
 * @class Listener 
 * @author Francesco Tria 
 * 
 */ 
public class Listener implements UpdateListener { 
	
	 HashMap<String, String> hmap = new HashMap<String, String>();
	 
	 @SuppressWarnings("unchecked")
	 @Override 
	 public void update(EventBean[] newData, EventBean[] oldData) {
		 //EventBean is a wrapper for the serialized events
		 EventBean event = newData[0];
		 HashMap<String, String> resultmap = (HashMap<String, String>) event.getUnderlying();
		 hmap.put(resultmap.get("deviceId"), resultmap.get("dateString")) ;	 
		 
		 // Update GUI with SwingUtilities.invokeLater       	  	
		 SwingUtilities.invokeLater(new Runnable() {
				@SuppressWarnings("rawtypes")
				public void run() {
					Set<?> set = hmap.entrySet();
					Iterator<?> iterator = set.iterator();
					int  i = 0;	        		     
					JPanel panelCenter = EntryPoint.panelCenter;
					panelCenter.removeAll();
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
						
						JLabel lblNewLabel = new JLabel((String) mentry.getValue());
						lblNewLabel.setFont(new Font("Trebuchet MS", Font.BOLD | Font.ITALIC, 12));
						lblNewLabel.setForeground(new Color(102, 153, 153));
						p.add(lblNewLabel);
						
						panelCenter.add(p, gbc);		
						i++;				
					}
					// refresh the panel with the current content
					panelCenter.revalidate();
					panelCenter.repaint();
				}
		 });
		    	              
		 System.out.println("-----------------------LAST DATE FOR EACH APPLIANCE------------------------------------");
		 System.out.println(hmap);
		 System.out.println("---------------------------------------------------------------------------------------");
    }
}