package com.icesoft.tumblr.downloader;

import org.junit.Test;

import com.icesoft.tumblr.downloader.configure.Config;
import com.icesoft.tumblr.downloader.service.H2DBService;
import com.icesoft.tumblr.state.DownloadPriority;

public class Start {
	@Test
	public void printSettingsDB(){
		H2DBService i = H2DBService.getInstance();
		System.err.println("H2DBService:" 	+ i==null?"H2DBService null":i);
		Config c = H2DBService.getInstance().loadSettings();
		System.err.println("Config:" 		+ c==null?"Config null":c);
		String s = c.toString();
		System.err.println("toString:" 		+ s==null?"toString null":s);
		
	}
	public void splite(String s){
		String splite = "\\.";
		String[] args = s.split(splite);
		StringBuffer sb = new StringBuffer();
		sb.append(s +" splite by [" + splite + "] into [" + args.length + "]={");
		for(String a : args){
			sb.append(a + " ");
		}
		sb.append("}");
		System.out.println(sb.toString());
	}
	@Test
	public void testSplite(){
		String[] s = {"tumblr_nu86mgbLsN1uatgt2_frame1.jpg","tumblr_oau21zjnWS1vbslqx_smart1.jpg"};
		for(String a : s){
			System.out.println(a.contains("\\."));
			splite(a);
		}		
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
	@Test
	public void deleteDB()
	{
		H2DBService.getInstance().deleteAll();
	}
}

