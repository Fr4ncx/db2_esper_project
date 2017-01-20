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
		cepConfig.addEventType("StockTick", Tick.class.getName());		
		cepConfig.addEventType("SwitchEvent", BinarySwitch.class.getName());	
		// We setup the engine
		cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
		cepRT = cep.getEPRuntime();
			
		EPServiceProvider cep1 = EPServiceProviderManager.getProvider("myCEPEngine2", cepConfig);
		cepRT2 = cep1.getEPRuntime();
		EPAdministrator cepAdm1 = cep1.getEPAdministrator();
		
		cepAdm1.createEPL("insert into Switch "+
				"select  b.deviceId, b.status, b.syncDate from "+
				"pattern [every a = SwitchEvent -> b=SwitchEvent(deviceId=a.deviceId, status!=a.status)  ] ");
		EPStatement testStatement = cepAdm1.createEPL("select  * from Switch ");
		
		
		
		EPAdministrator cepAdm = cep.getEPAdministrator();
		//  Last time each appliance was used
		EPStatement usageStatement = cepAdm.createEPL("select  max(syncDate) as date, dateString , deviceId from " +
				"StockTick() group by deviceId ");
		// 	Daily energy consumption for each appliance in kWh
		/*cepAdm.createEPL("insert into DailyConsumption " +
				"select (t1.ts*1000).toDate() as date, sum( ((t0.value_int + t1.value_int ) / 2 ) * ( t1.ts - t0.ts ) / 3600) as powerConsumptionValue , t0.deviceId as deviceId from StockTick().win:length(2) "+
				"as t0 join StockTick().win:length(2)  as t1 on t1.deviceId = t0.deviceId "+          
				"where  t1.id != t0.id and t1.ts > t0.ts and t1.syncDate.getMonthOfYear() = t0.syncDate.getMonthOfYear() and t1.syncDate.getDayOfMonth() = t0.syncDate.getDayOfMonth() and t1.syncDate.getYear() = t0.syncDate.getYear() "+
				"group by t0.deviceId");*/
		cepAdm.createEPL("insert into DailyConsumption " +
				"select (a.ts*1000).toDate() as date, a.value_int,b.value_int,  (((a.value_int + b.value_int ) / 2 ) * ( b.ts - a.ts ) / 3600) as p, sum( ((a.value_int + b.value_int ) / 2 ) * ( b.ts - a.ts ) / 3600) as powerConsumptionValue, a.deviceId as deviceId from "+
				"pattern [every a = StockTick -> b=StockTick(deviceId=a.deviceId, id != a.id , ts > a.ts , a.syncDate.getMonthOfYear() = syncDate.getMonthOfYear() , a.syncDate.getDayOfMonth() = syncDate.getDayOfMonth() , a.syncDate.getYear() = syncDate.getYear())  ] "+
				"group by a.deviceId, a.syncDate.getMonthOfYear(), a.syncDate.getDayOfMonth(),  a.syncDate.getYear()");
		
	 
		EPStatement dailyStatement = cepAdm.createEPL("select * from " +
	  			"DailyConsumption()");
	  	
	  	cepAdm.createEPL("insert into WeeklyConsumption " +
				"select (t1.ts*1000).toDate() as date, sum( ((t0.value_int + t1.value_int ) / 2 ) * ( t1.ts - t0.ts ) / 3600) as powerConsumptionValue , t0.deviceId as deviceId from StockTick().win:length(2) "+
				"as t0 join StockTick().win:length(2)  as t1 on t1.deviceId = t0.deviceId "+          
				"where  t1.id != t0.id and t1.ts > t0.ts and t1.syncDate.getMonthOfYear() = t0.syncDate.getMonthOfYear() and t1.syncDate.getWeekYear() = t0.syncDate.getWeekYear() and t1.syncDate.getYear() = t0.syncDate.getYear() "+
				"group by t0.deviceId");
	  	EPStatement weeklyStatement = cepAdm.createEPL("select * from " +
	  			"WeeklyConsumption()");

	  	// Listeners
	  	usageStatement.addListener(new Listener());
	  	dailyStatement.addListener(new DailyListener());
	  	weeklyStatement.addListener(new WeeklyListener());
	  	testStatement.addListener(new BinarySwitchListener());
	 }
 
	/**
	 * @param null
	 * run function
	 */
	 public void run() {
		 JSONParser parser = new JSONParser();
		 log.info("Parsing started...");
		 try {
			 Object obj = parser.parse(new FileReader("C:/Users/fr4nc/Desktop/db2/test.json"));     
			 JSONArray jsonObject = (JSONArray) obj;  
			 Iterator<?> i = jsonObject.iterator();
			 while (i.hasNext()) {
				 JSONObject slide = (JSONObject) i.next();   
				 JSONObject message = (JSONObject) slide.get("message");
				 JSONObject device = (JSONObject) message.get("device");				 
				 if (message != null && device != null && device.get("type") != null &&  message.get("event_type") != null && device.get("type").equals("PowerMeter") && message.get("event_type").equals("ValueChangedEvent")) {
					 Tick tick = new Tick(slide);
					 // Send event to the EPR Run time
					 cepRT.sendEvent(tick);    
					 try {
						 Thread.sleep(1); // 1 ms
					 } catch (InterruptedException ex) {
						 System.out.println(ex);
					 }
				 } else if (message != null && device != null && device.get("type") != null && device.get("type").equals("BinarySwitch") &&  message.get("event_type") != null && message.get("event_type").equals("ValueChangedEvent") ) {
					 BinarySwitch plug = new BinarySwitch(slide);
					 cepRT2.sendEvent(plug);
			
				 }
			 }
			 cep.destroy();
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