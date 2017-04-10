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
	public void dropDB(){
		H2DBService.getInstance().dropDB();
	}
	@Test
	public void loadDB(){
		H2DBService.getInstance().loadTask();
	}
}

