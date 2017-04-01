package com.icesoft.tumblr.downloader.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.panel.interfaces.IUpdatable;

public class UIMonitor{
	private static Logger logger = Logger.getLogger(UIMonitor.class);  
	private volatile boolean on = true;	
	private List<IUpdatable> updatables = Collections.synchronizedList(new ArrayList<IUpdatable>());
	private static UIMonitor instance = new UIMonitor();
	private Thread thread;
	private UIMonitor()
	{

	}
	private Thread newThread()
	{
		Thread thread = new Thread(){
			@Override
			public void run() 
			{
				while(on)
				{	
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
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) 
					{
						logger.debug("UIMonitor:" + e.getMessage());
					}
				}
			}
		};
		return thread;
	}
	public static UIMonitor getInstance(){
		return instance;
	}
	
	public void turnOn(){
		this.on = true;
		thread = newThread();
		thread.start();		
	}	

	public void turnOff(){
		this.on = false;
	}
	public void addUpdatable(IUpdatable up){
		updatables.add(up);
	}
}
