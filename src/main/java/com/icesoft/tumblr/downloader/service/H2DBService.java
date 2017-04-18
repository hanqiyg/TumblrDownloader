package com.icesoft.tumblr.downloader.service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.contexts.DownloadContext;
import com.icesoft.tumblr.downloader.configure.Config;
import com.icesoft.tumblr.downloader.configure.Settings;
import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class H2DBService {	
	private static Logger logger = Logger.getLogger(H2DBService.class);  
	private static H2DBService instance = new H2DBService();
	private Connection con;
	
	public static final String DRIVER = "org.h2.Driver";
	public static final String DB_URL = "jdbc:h2:";
	public static final String DB_NAME = "./download";
	public static final String DB_USER = "sa";
	public static final String DB_PASS = "mypass";
	
	public static final String DOWNLOAD = "download";
	public static final String SETTINGS = "settings";
	
	
	private H2DBService(){}
	public String init()
	{
		 try {
			Class.forName(DRIVER);
	        con = DriverManager.getConnection( DB_URL + DB_NAME ,DB_USER,"");
		} catch (ClassNotFoundException e){
			logger.debug("H2DB driver is not found.[" + DRIVER + "]");
		} catch (SQLException e) {
			switch(e.getErrorCode() )
			{
				case 90020 : {
								logger.debug("DB locked by another application. [" + DB_NAME + "]");
								return "DB locked by another application. [" + DB_NAME + "]";
							}					
				default:	{
								logger.debug("connect execute." + e.getLocalizedMessage());
								return "connect execute." + e.getLocalizedMessage();
							}
			}
		}
		return null;
	}
	public static H2DBService getInstance(){
		return instance;
	}

	public void createDownloadTable()
	{
		try {
	        Statement stmt = con.createStatement();
	        stmt.execute("CREATE TABLE " + DOWNLOAD + " (url VARCHAR(2083) primary key, createtime TIMESTAMP, state INT, "
	        		+ "filename VARCHAR(255),filesize BIGINT, ext VARCHAR(64), savepath VARCHAR(255),totalTime BIGINT, priority INT)");
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		}
	}
	public void dropDownloadTable()
	{
        try {
        	Statement stmt = con.createStatement();
			stmt.execute("DROP TABLE " + DOWNLOAD);
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		}
	}
	public List<IContext> loadTask(){
		List<IContext> tasks = new ArrayList<IContext>();
		try 
		{
			if(con != null)
			{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + DOWNLOAD);
				int i = 0;
		        while(rs.next()){
		        	i++;
		        	System.out.println("load task:[" 	+ i +"] " 
		        			   + " url:"			+ rs.getString("url") 
		        			   + " time:" 		+ rs.getTimestamp("createtime")
		        			   + " state:" 		+ rs.getInt("state")
		        			   + " filename:" 	+ rs.getString("filename")
		        			   + " filesize"  	+ rs.getLong("filesize")
		        			   + " ext:" 		+ rs.getString("ext")
		        			   + " savepath:" 	+ rs.getString("savepath")
		        			   + " totalTime:" 	+ rs.getLong("totalTime")
		        			   + " priority:" 	+ rs.getInt("priority")
		        			   );
		        	String url = rs.getString("url");
		        	Timestamp time = rs.getTimestamp("createtime");
		        	int state = rs.getInt("state");
		        	String filename = rs.getString("filename");
		        	long filesize = rs.getLong("filesize");
		        	String ext = rs.getString("ext");
		        	String savepath = rs.getString("savepath");
		        	long totalTime = rs.getLong("totalTime");
		        	int priority = rs.getInt("priority");
		        	DownloadContext task = new DownloadContext(url, filename, filesize, time.getTime(), state,ext, savepath,totalTime,priority);
		        	tasks.add(task);
		        }
		        stmt.close();
			}
		} catch (SQLException e) 
		{			
			switch(e.getErrorCode() )
			{
				case 90020 : {
								logger.debug("DB locked by another application. [" + DB_URL + "]");
							}break;					
				case 42102 : {
								logger.debug("Create Table [" + DOWNLOAD + "]");
								createDownloadTable();	
							}break;
				default:	{
								logger.debug("stmt execute." + e.getLocalizedMessage());
							}break;
			}
		}		
		return tasks;
	}
	public void updateContexts(List<IContext> contexts)
	{				
		Statement stmt = null;
		Iterator<IContext> it = contexts.iterator();
		while(it.hasNext())
		{
			IContext context = it.next();
			String url = context.getURL();
			String filename = context.getFilename();
			String ext  = context.getExt();
			long filesize = context.getRemoteFilesize();
			String savepath = context.getSavePath();
			int state = context.getState().ordinal();
			long totalTime = context.getTotalTime();
			int priority = context.getPriority().ordinal();
			if(url != null && !url.trim().equals("")){
				try {
					stmt = con.createStatement();
					stmt.execute("UPDATE " + DOWNLOAD + " SET state='" + state + "',"
							+ " filename='" + filename + "'," 
							+ " filesize='" + filesize +"'," 
							+ " ext='" + ext +"', " 
							+ " savepath='" + savepath + "'," 
							+ " totalTime='" + totalTime + "',"
							+ " priority='"  + priority  + "' "
							+ " WHERE url='" + url +"'");

				} catch (SQLException e) {
					logger.debug("stmt execute." + e.getLocalizedMessage());
				}
			}
		}
		if(stmt != null)
		{
		    try {
				stmt.close();
			} catch (SQLException e) {
				logger.debug("stmt close." + e.getLocalizedMessage());
			}
		}
	}
	
	public void updateContext(IContext context)
	{
		String url = context.getURL();
		String filename = context.getFilename();
		String ext  = context.getExt();
		long filesize = context.getRemoteFilesize();
		String savepath = context.getSavePath();
		int state = context.getState().ordinal();
		long totalTime = context.getTotalTime();
		int priority = context.getPriority().ordinal();
		if(url != null && !url.trim().equals("")){
			Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.execute("UPDATE " + DOWNLOAD + " SET state='" + state + "', "
						+ " filename='" + filename + "'," 
						+ " filesize='" + filesize +"'," 
						+ " ext='" + ext +"'," 
						+ " savepath='" + savepath + "'," 
						+ " totalTime='" + totalTime + "',"
						+ " priority='"  + priority  + "',"
						+ " WHERE url='" + url +"'");
   		        stmt.close();
			} catch (SQLException e) {
				logger.debug("stmt close." + e.getLocalizedMessage());
			}
		}
	}
	public void close(){
		try {
			con.close();
		} catch (SQLException e) {
			logger.debug("connection close." + e.getLocalizedMessage());
		}
	}
	public void initTask(IContext context) {
		try 
		{
			Statement stmt = con.createStatement();	        
	        stmt.execute("INSERT INTO " + DOWNLOAD  + " (url,createtime,state,filename,priority) VALUES ('" + context.getURL() +"','" 
	        		+ new Timestamp(System.currentTimeMillis()) +"','" + DownloadState.WAIT.ordinal() + "','" + null + "','"  
	        		+ DownloadPriority.NORMAL.ordinal() + "')");	        
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		} 
	}
	public void deleteAll(){
		try 
		{
			Statement stmt = con.createStatement();	        
	        stmt.execute("DELETE FROM " + DOWNLOAD);        
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		} 
	}
	public void delete(IContext context) {
		try 
		{
			Statement stmt = con.createStatement();	        
	        stmt.execute("DELETE FROM " + DOWNLOAD  + " WHERE url='" + context.getURL() +"'");        
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		} 
	}
	public void createSettingsTable()
	{
		try {
	        Statement stmt = con.createStatement();
	        stmt.execute("CREATE TABLE " + SETTINGS + " ( "
	        		+"id VARCHAR(255),"
	        		+"basePath VARCHAR(255),"
	        		+"connectTimeout BIGINT,"
	        		+"readTimeout BIGINT,"
	        		+"workerCount INT,"
	         		+"clientCount INT,"
	         		+"windowX INT,"
	         		+"windowY INT,"
	         		+"windowW INT,"
	         		+"windowH INT,"
	         		+"proxyType INT,"
	         		+"proxyHost VARCHAR(255),"
	         		+"proxyPort INT"
	        		+"consumerKey VARCHAR(255),"
	         		+"consumerSecret VARCHAR(255),"
	         		+"oauthToken VARCHAR(255),"
	         		+"oauthTokenSecret VARCHAR(255)"
	        		+ ")");
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		}
	}
	public void initeSettings(Config config) {
		try 
		{
			Statement stmt = con.createStatement();	        
	        stmt.execute("INSERT INTO " + SETTINGS  + " ( "
	        		+"id,"
	        		+"basePath,"
	        		+"connectTimeout,"
	        		+"readTimeout,"
	        		+"workerCount,"
	         		+"clientCount,"
	         		+"windowX,"
	         		+"windowY,"
	         		+"windowW,"
	         		+"windowH,"
	         		+"proxyType,"
	         		+"proxyHost,"
	         		+"proxyPort,"
	         		+"consumerKey,"
	         		+"consumerSecret,"
	         		+"oauthToken,"
	         		+"oauthTokenSecret,"
	        		+ ") values ("
	        		+"'" + "tumblrdownloader" +"',"
	         		+"'" + "./" +"',"
	         		+"'" + 10000 +"',"
	         		+"'" + 10000 +"',"
	         		+"'" + 5 +"',"
	         		+"'" + 5 +"',"
	         		+"'" + 100 +"',"
	         		+"'" + 100 +"',"
	         		+"'" + 600 +"',"
	         		+"'" + 400 +"',"
	         		+"'" + 0 +"',"
	         		+"'" + null +"',"
	         		+"'" + 0 +"',"
	         		+"'" + null +"',"
	         		+"'" + null +"',"
	         		+"'" + null +"',"
	         		+"'" + null +"'"
	         		+")");   
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		} 
	}
	public void updateSettings(Config config)
	{		
		Statement stmt = null;		
		try 
		{
			stmt = con.createStatement();
			stmt.execute("UPDATE " + SETTINGS + " SET " 
					+ " basePath='" 		+ config.basePath 			+ "',"
					+ " connectTimeout='" 	+ config.connectTimeout 	+ "'," 
					+ " readTimeout='" 		+ config.readTimeout 		+ "'," 
					+ " workerCount='" 		+ config.workerCount 		+ "', " 
					+ " clientCount='" 		+ config.clientCount 		+ "'," 
					+ " windowX='" 			+ config.windowX 			+ "',"
					+ " windowY='" 			+ config.windowY 			+ "',"
					+ " windowW='" 			+ config.windowW 			+ "',"
					+ " windowH='" 			+ config.windowH 			+ "',"
					+ " proxyType='" 		+ config.proxy==null? "0" : config.proxy.type().ordinal()	+ "',"
					+ " proxyHost='" 		+ config.proxy==null? null : ((InetSocketAddress)config.proxy.address()).getHostName()	+ "',"
					+ " proxyPort='" 		+ config.proxy==null? "0" : ((InetSocketAddress)config.proxy.address()).getPort()	+ "'"
					+ " consumerKey='" 		+ config.consumerKey 		+ "',"
					+ " consumerSecret='" 	+ config.consumerSecret 	+ "',"
					+ " oauthToken='" 		+ config.oauthToken 		+ "',"
					+ " oauthTokenSecret='" + config.oauthTokenSecret 	+ "',"
					+ " WHERE id='" + "tumblrdownloader" +"'");
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		} finally{
			if(stmt != null)
			{
			    try {
					stmt.close();
				} catch (SQLException e) {
					logger.debug("stmt close." + e.getLocalizedMessage());
				}
			}
		}
	}
	public void savePath() {
		
	}
	public String loadPath() {
		// TODO Auto-generated method stub
		return null;
	}
	public Proxy loadProxy() {
		Statement stmt = null;
		try 
		{
			if(con != null)
			{
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT proxyType, proxyHost, proxyPort FROM " + SETTINGS + " WHERE id='" + "tumblrdownloader" +"'");
		        if(rs.next())
		        {
			        int proxyType = rs.getInt("proxyType");
			        String proxyHost = rs.getString("proxyHost");
			        int proxyPort = rs.getInt("proxyPort"); 
			        return new Proxy(Proxy.Type.values()[proxyType],new InetSocketAddress(proxyHost,proxyPort));
		        }
			}
		} catch (SQLException e) 
		{			
			switch(e.getErrorCode() )
			{
				case 90020 : {
								logger.debug("DB locked by another application. [" + DB_URL + "]");
							}break;					
				case 42102 : {
								logger.debug("Create Table [" + DOWNLOAD + "]");
								createDownloadTable();	
							}break;
				default:	{
								logger.debug("stmt execute." + e.getLocalizedMessage());
							}break;
			}
		}finally{
			if(stmt != null){
		        try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	public Config loadSettings() {
		Statement stmt = null;
		try 
		{
			if(con != null)
			{
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + SETTINGS + " WHERE id='" + "tumblrdownloader" +"'");
		        if(rs.next())
		        {
					String basePath = rs.getString("basePath");
					int connectTimeout = rs.getInt("connectTimeout");
					int readTimeout = rs.getInt("readTimeout");
					int workerCount = rs.getInt("workerCount");
					int clientCount = rs.getInt("clientCount");
					int windowX = rs.getInt("windowX");
					int windowY = rs.getInt("windowY");
					int windowW = rs.getInt("windowW");
					int windowH = rs.getInt("windowH");
			        int proxyType = rs.getInt("proxyType");
			        String proxyHost = rs.getString("proxyHost");
			        int proxyPort = rs.getInt("proxyPort"); 
			        Proxy proxy = new Proxy(Proxy.Type.values()[proxyType],new InetSocketAddress(proxyHost,proxyPort));
					String consumerKey = rs.getString("consumerKey");
					String consumerSecret =  rs.getString("consumerSecret");
					String oauthToken =  rs.getString("oauthToken");
					String oauthTokenSecret =  rs.getString("oauthTokenSecret");
			        return new Config(basePath, 
			        		windowX, windowY, windowW, windowH, 
			        		workerCount, clientCount, 
			        		connectTimeout, readTimeout, 
			        		proxy,consumerKey,consumerSecret,oauthToken,oauthTokenSecret);
		        }
			}
		} catch (SQLException e) 
		{			
			switch(e.getErrorCode() )
			{
				case 90020 : {
								logger.debug("DB locked by another application. [" + DB_URL + "]");
							}break;					
				case 42102 : {
								logger.debug("Create Table [" + DOWNLOAD + "]");
								createDownloadTable();	
							}break;
				default:	{
								logger.debug("stmt execute." + e.getLocalizedMessage());
							}break;
			}
		}finally{
			if(stmt != null){
		        try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
