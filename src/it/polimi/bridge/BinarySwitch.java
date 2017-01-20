package it.polimi.bridge;

import java.util.Date;
import org.json.simple.JSONObject;


/**
 * Tick event class
 *    
 * A sample Java class that represents a device event is shown below. A simple plain-old Java class that provides getter-methods for access to 
 * properties 
 *  
 * @author Francesco Tria
 * @class Tick
 */ 

public class BinarySwitch {
	String id,  deviceId, value;
	Date syncDate;
	Boolean status;
   
    public BinarySwitch(JSONObject obj) {
    	JSONObject _id = (JSONObject) obj.get("_id");
    	this.id = (String) _id.get("$oid");
    	JSONObject message = (JSONObject) obj.get("message");

    	Long ts = (Long) message.get("uts");
    	this.syncDate = new Date(ts * 1000);
    	this.value = String.valueOf(message.get("value"));
    	
    	
    	
    	if (message.get("value").getClass().getSimpleName().equals("Long")) {
    		Long s =  (Long) message.get("value");
    		this.status = ( s == 255 ) ? true : false;
    	}else
    		this.status = (Boolean) message.get("value");
    			
    	// Device object
    	JSONObject device = (JSONObject) message.get("device");
    	if (device != null )
    		this.deviceId = Long.toString((long) device.get("id"));
    
    }
    
    public String getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public Date getSyncDate() { return syncDate; }
    public String getValue () { return value; }
    public Boolean getStatus () { return status; }
    @Override
    public String toString() {
        return deviceId+""+syncDate.toString();
    }
}