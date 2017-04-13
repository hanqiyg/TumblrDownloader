package com.icesoft.tumblr.downloader;

import org.junit.Test;

import com.icesoft.tumblr.downloader.service.H2DBService;
import com.icesoft.tumblr.state.DownloadPriority;

public class Start {
	@Test
	public void createDB(){
		H2DBService.getInstance().createDB();
	}

	@Test
	public void dropDB(){
		H2DBService.getInstance().dropDB();
	}
	@Test
	public void loadDB(){
		H2DBService.getInstance().loadTask();
	}
	@Test
	public void testString(){
		int count = 1024 * 1024;
		long begin = System.currentTimeMillis();
		String s = null;
		for(int i=0;i<count;i++){
			s = s + "0";
		}
		long end   = System.currentTimeMillis();
		System.err.println("String+" + " takes " + (end - begin) + "ms");				//294196ms
		
		long begin1 = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<count;i++){
			sb.append("0");
		}
		sb.toString();
		long end1   = System.currentTimeMillis();
		System.err.println("StringBuffer" + " takes " + (end1 - begin1) + "ms");		//12ms
		
		long begin2 = System.currentTimeMillis();
		StringBuilder sbr = new StringBuilder();
		for(int i=0;i<count;i++){
			sbr.append("0");
		}
		sbr.toString();
		long end2   = System.currentTimeMillis();
		System.err.println("StringBuilder" + " takes " + (end2 - begin2) + "ms");		//7ms
	}
	@Test
	public void printPriority()
	{
		for(DownloadPriority p : DownloadPriority.values())
		{
			System.out.println(p.name() + " : " + p.ordinal());
		}
	}
}

