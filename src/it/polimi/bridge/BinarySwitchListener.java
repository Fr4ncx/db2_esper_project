package it.polimi.bridge;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class BinarySwitchListener implements UpdateListener {

	@Override
	public void update(EventBean[] newData, EventBean[] arg1) {
		// TODO Auto-generated method stub
		 EventBean event = newData[0];
		 System.out.println("binaryyy"+event.getUnderlying());
	}

}
