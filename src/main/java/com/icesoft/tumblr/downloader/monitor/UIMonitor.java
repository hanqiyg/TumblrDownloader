package com.icesoft.tumblr.downloader.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.panel.interfaces.IUpdatable;

public class UIMonitor extends Thread{
	private static Logger logger = Logger.getLogger(UIMonitor.class);  
	private boolean on = true;	
	List<IUpdatable> updatables = Collections.synchronizedList(new ArrayList<IUpdatable>());
	
	@Override
	public void run() 
	{
		while(on)
		{	
			try {

				if(updatables != null && updatables.size() > 0)
				{					
					for(IUpdatable up : updatables)
					{
						if(up != null)
						{
							up.update();
						}else
						{
							logger.debug("UIMonitor:" + "Moniting obj is null.");
						}
					}
				}else
				{
					logger.debug("UIMonitor:" + "Moniting list is null or empty.");
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) 
			{
				logger.debug("UIMonitor:" + e.getMessage());
			}
		}
	}
	public void turnOff(){
		this.on = false;
	}
	public void addUpdatable(IUpdatable up){
		updatables.add(up);
	}
}
