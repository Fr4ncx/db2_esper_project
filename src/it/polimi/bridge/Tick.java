package it.polimi.bridge;

import java.text.SimpleDateFormat;
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

public class Tick {
	JSONObject message;
	String id;
	Long seq;
	String deviceId;
	String value;
	String weekOfMonth;
	Float value_int;
	Long ts;
    String house_id, message_type, dateString;
    Date syncDate;
   
    public Tick(JSONObject obj) {
    	SimpleDateFormat weekOfMonth = new SimpleDateFormat("W");

    	JSONObject _id = (JSONObject) obj.get("_id");
    	this.id = (String) _id.get("$oid");
    	JSONObject message = (JSONObject) obj.get("message");
    	this.message = message;
    	this.house_id = (String) obj.get("house_id");
    	this.seq = (Long) obj.get("seq");
    	this.ts = (Long) message.get("uts");
    	this.syncDate = new Date(this.ts * 1000);
    	this.weekOfMonth = weekOfMonth.format(this.syncDate);
    	this.dateString = syncDate.toString();
    	this.message_type = (String) obj.get("message_type");
    	this.value = String.valueOf(message.get("value"));
    	if (this.value.length() == 0)
    		this.value = "0";
    	else {
    		Number s = (Number)(message.get("value"));
    		this.value_int = s.floatValue();
    		
    	}
    			
    	// Device object
    	JSONObject device = (JSONObject) message.get("device");
    	if (device != null )
    		this.deviceId = Long.toString((long) device.get("id"));
    	else 
    		this.deviceId = "0";
    }
    
    public String getId() { return id; }
    public JSONObject getMessage() { return message; }
    public Long getSeq() { return seq; }
    public String getDeviceId() { return deviceId; }
    public Long getTs () { return ts; }
    public Date getSyncDate() { return syncDate; }
    public String getValue () { return value; }
    public Float getValue_int () { return value_int; }
    public String getWeekOfMonth() { return weekOfMonth; };
    public String getDateString () { return dateString; }
    @Override
    public String toString() {
        return message.toJSONString()+""+syncDate.toString();
    }
}