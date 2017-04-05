package com.icesoft.tumblr.downloader;

import org.junit.Test;

import com.icesoft.tumblr.downloader.service.H2DBService;

public class Start {
	@Test
	public void createDB(){
		H2DBService.getInstance().createDB();
	}
	@Test
	public void insertDB(){
		H2DBService.getInstance().initTask("http://www.google.com/");
	}
	@Test
	public void isExistDB(){
		int count = 1000;
		long start = System.currentTimeMillis();
		for(int i=0; i<count;i++)
		{
			H2DBService.getInstance().isURLExist("http://www.google.com/");
		}
		long end = System.currentTimeMillis();
		System.out.println("Query for " + count + " times takes " + (end - start) + "ms.");
	}
	@Test
	public void dropDB(){
		H2DBService.getInstance().dropDB();
	}
	@Test
	public void loadDB(){
		H2DBService.getInstance().loadTask();
	}
}

