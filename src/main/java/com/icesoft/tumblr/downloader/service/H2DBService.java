package com.icesoft.tumblr.downloader.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.downloader.workers.DownloadTask.STATE;
import com.icesoft.tumblr.state.interfaces.IContext;

public class H2DBService {	
	private static Logger logger = Logger.getLogger(H2DBService.class);  
	private static H2DBService instance = new H2DBService();
	private Connection con;
	
	public static final String DRIVER = "org.h2.Driver";
	public static final String DB_URL = "jdbc:h2:./download;AUTO_SERVER=TRUE";
	public static final String DB_USER = "sa";
	public static final String DB_PASS = "mypass";
	
	public static final String TABLE_NAME = "download";
	
	private H2DBService()
	{
		 try {
			Class.forName(DRIVER);
	        con = DriverManager.getConnection( DB_URL,DB_USER,"");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static H2DBService getInstance(){
		return instance;
	}	
	
	public void createDB()
	{
		try {
	        Statement stmt = con.createStatement();
	        stmt.execute("CREATE TABLE " + TABLE_NAME + " (url VARCHAR(2083) primary key, createtime TIMESTAMP, iscomplete BOOLEAN, "
	        		+ "filename VARCHAR(255),filesize BIGINT, ext VARCHAR(64), savepath VARCHAR(255))");
	        stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void dropDB()
	{
        try {
        	Statement stmt = con.createStatement();
			stmt.execute("DROP TABLE " + TABLE_NAME);
	        stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initTask(String url){
		try 
		{
			Statement stmt = con.createStatement();	        
	        stmt.execute("INSERT INTO " + TABLE_NAME  + " (url,createtime,isComplete,filename) VALUES ('" + url +"','" 
	        		+ new Timestamp(System.currentTimeMillis()) +"','" + false + "','" + null + "')");	        
	        stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
	}
	public List<DownloadTask> loadTask(){
		List<DownloadTask> tasks = new ArrayList<DownloadTask>();
		try 
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
			int i = 0;
	        while(rs.next()){
	        	i++;
	        	System.out.println("load task:[" 	+ i +"] " 
	        			   + "url:"			+ rs.getString("url") 
	        			   + " time:" 		+ rs.getTimestamp("createtime")
	        			   + "iscomplete:" 	+ rs.getBoolean("iscomplete")
	        			   + "filename:" 	+ rs.getString("filename")
	        			   + "filesize"  	+ rs.getLong("filesize")
	        			   + "ext:" 		+ rs.getString("ext")
	        			   + "savepath:" 	+ rs.getString("savepath"));
	        	String url = rs.getString("url");
	        	Timestamp time = rs.getTimestamp("createtime");
	        	boolean iscomplete = rs.getBoolean("iscomplete");
	        	String filename = rs.getString("filename");
	        	long filesize = rs.getLong("filesize");
	        	String ext = rs.getString("ext");
	        	String savepath = rs.getString("savepath");
	        	
	        	DownloadTask task = new DownloadTask(url, filename, filesize, time.getTime(), iscomplete,ext, savepath);
	        	tasks.add(task);
	        }
	        stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tasks;
	}
	public boolean isURLExist(String url)
	{
		boolean isExist = true;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE url = '" + url+"'");
	        isExist = rs.next();
	        stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isExist;
	}
	
	public void updateTask(IContext context)
	{
		String url = context.getURL();
		String filename = context.getFilename();
		String ext  = context.getExt();
		long filesize = context.getRemoteFilesize();
		String savepath = context.getSavePath();
		boolean isComplete = context.getState().equals(STATE.DOWNLOAD_COMPLETE);
		if(url != null && !url.trim().equals("")){
			Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.execute("UPDATE " + TABLE_NAME + " SET iscomplete='" + isComplete + "', "
						+"filename='" + filename + "', filesize='" + filesize +"', ext='" + ext +"', savepath='" + savepath + "'" + " WHERE url='" + url +"'");
   
		        stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void close() throws SQLException{
		con.close();
	}
}
