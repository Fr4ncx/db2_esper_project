package it.polimi.bridge;

import com.espertech.esper.client.*;
import com.espertech.esper.client.Configuration; 
import com.espertech.esper.client.EPServiceProvider; 
import com.espertech.esper.client.EPServiceProviderManager; 
import com.espertech.esper.client.EPStatement;
import it.polimi.gui.EntryPoint;
import java.io.FileReader;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @namespace it.polimi.bridge
 * @author Francesco Tria 
 * @class Parser
 */ 
public class Parser implements Runnable { 
	
	private static final Log log = LogFactory.getLog(Parser.class);
	private  EPRuntime cepRT;
	private EPRuntime cepRT2;
	public static EPServiceProvider cep;
 
	 // Init method
	public void init() {
		// log4j configuration
		BasicConfigurator.configure();
		//The Configuration is meant only as an initialization-time object.
		Configuration cepConfig = new Configuration();
		// We register Ticks as objects the engine will have to handle
		cepConfig.addEventType("StockEvent", PowerMeter.class.getName());		
		cepConfig.addEventType("SwitchEvent", BinarySwitch.class.getName());	
		// We setup the engine
		cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
		cepRT = cep.getEPRuntime();
			
		EPServiceProvider cep1 = EPServiceProviderManager.getProvider("myCEPEngine2", cepConfig);
		cepRT2 = cep1.getEPRuntime();
		EPAdministrator cepAdm1 = cep1.getEPAdministrator();
				
		
		EPAdministrator cepAdm = cep.getEPAdministrator();
		//  Last time each appliance was used
		EPStatement usageStatement = cepAdm.createEPL("select  max(syncDate) as date, dateString , deviceId from " +
				"StockEvent() group by deviceId");
		// 	Daily energy consumption for each appliance in kWh
		cepAdm.createEPL("insert into DailyConsumption " +
				"select (a.ts * 1000).toDate() as date, sum(a.value_int * 0.001 * ( b.ts - a.ts ) / 3600) as powerConsumptionValue, a.deviceId as deviceId from "+
				"pattern [every a = StockEvent -> b=StockEvent(deviceId=a.deviceId, id != a.id , ts > a.ts , a.syncDate.getMonthOfYear() = syncDate.getMonthOfYear() ,"+
				"a.syncDate.getDayOfMonth() = syncDate.getDayOfMonth() , a.syncDate.getYear() = syncDate.getYear()) ] "+
				"group by a.deviceId, a.syncDate.getMonthOfYear(), a.syncDate.getDayOfMonth(),  a.syncDate.getYear() output every 10 seconds");
		 
		EPStatement dailyStatement = cepAdm.createEPL("select * from " +
	  			"DailyConsumption()");
	  	
		// Weekly energy consumption for each appliance in kWh
	  	cepAdm.createEPL("insert into WeeklyConsumption " +
	  			"select (a.ts * 1000).toDate() as date, sum(a.value_int * 0.001 * ( b.ts - a.ts ) / 3600) as powerConsumptionValue, a.deviceId as deviceId, a.weekOfMonth as weekOfMonth from "+
				"pattern [every a = StockEvent -> b=StockEvent(deviceId=a.deviceId, id != a.id , ts > a.ts , a.weekOfMonth = weekOfMonth , a.syncDate.getYear() = syncDate.getYear()) ] "+
				"group by a.deviceId, a.weekOfMonth,  a.syncDate.getYear() output every 10 seconds");
	  	
	  	EPStatement weeklyStatement = cepAdm.createEPL("select powerConsumptionValue, sum(powerConsumptionValue), deviceId, date, weekOfMonth from " +
	  			"WeeklyConsumption() group by  deviceId, weekOfMonth,  date.getYear()");
	  	
	  	//	Daily Overall house energy consumption in kWh
		cepAdm.createEPL("insert into HouseConsumption " +
				"select (a.ts * 1000).toDate() as date, sum(a.value_int * 0.001 * ( b.ts - a.ts ) / 3600) as powerConsumptionValue from "+
				"pattern [every a = StockEvent -> b=StockEvent(id != a.id , ts > a.ts , a.syncDate.getMonthOfYear() = syncDate.getMonthOfYear() ,"+
				"a.syncDate.getDayOfMonth() = syncDate.getDayOfMonth() , a.syncDate.getYear() = syncDate.getYear())] "+
				"group by a.syncDate.getMonthOfYear(), a.syncDate.getDayOfMonth(),  a.syncDate.getYear() output every 10 seconds");
			 
		EPStatement houseStatement = cepAdm.createEPL("select * from HouseConsumption() ");
	  	
	  	// List of the appliances in use at the moment
		cepAdm1.createEPL("insert into Switch "+
				"select  b.deviceId as deviceId, b.status as status, b.syncDate as date from "+
				"pattern [every a = SwitchEvent -> b=SwitchEvent(deviceId=a.deviceId, status!=a.status)  ] ");
		EPStatement switchStatement = cepAdm1.createEPL("select  * from Switch ");
		
		
	  	// Listeners
	  	usageStatement.addListener(new UsageListener());
	  	dailyStatement.addListener(new DailyListener());
	  	weeklyStatement.addListener(new WeeklyListener());
	  	houseStatement.addListener(new OverallHouseListener());
	  	switchStatement.addListener(new BinarySwitchListener());
	 }
 
	/**
	 * @param null
	 * run function
	 */
	 public void run() {
		 JSONParser parser = new JSONParser();
		 log.info("Parsing started...");
		 try {
			 Object obj = parser.parse(new FileReader("C:/Users/fr4nc/Desktop/db2/data.json"));     
			 JSONArray jsonObject = (JSONArray) obj;  
			 Iterator<?> i = jsonObject.iterator();
			 while (i.hasNext()) {
				 JSONObject slide = (JSONObject) i.next();   
				 JSONObject message = (JSONObject) slide.get("message");
				 JSONObject device = (JSONObject) message.get("device");				 
				 if (message != null && device != null && device.get("type") != null &&  message.get("event_type") != null && device.get("type").equals("PowerMeter") && message.get("event_type").equals("ValueChangedEvent")) {
					 PowerMeter tick = new PowerMeter(slide);
					 // Send event to the EPR Run time
					 cepRT.sendEvent(tick);    
					 try {
						 Thread.sleep(10); // 10 ms
					 } catch (InterruptedException ex) {
						 System.out.println(ex);
					 }
				 } else if (message != null && device != null && device.get("type") != null && device.get("type").equals("BinarySwitch") &&  message.get("event_type") != null && message.get("event_type").equals("ValueChangedEvent") ) {
					 // Only event with type "BinarySwitch" with no failed status
					 BinarySwitch plug = new BinarySwitch(slide);
					 cepRT2.sendEvent(plug);
				 }
			 }
			// cep.destroy();
			 try {
				 Thread.sleep(2000);
				 EntryPoint.btnStart.setEnabled(true);				 
				 JOptionPane.showMessageDialog(EntryPoint.frame, "Parsing finished ...", "BRiDGe", JOptionPane.PLAIN_MESSAGE);
				 log.info("Parsing finished ...");
			 } catch (InterruptedException ex) {
				 System.out.println(ex);
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 } 
}