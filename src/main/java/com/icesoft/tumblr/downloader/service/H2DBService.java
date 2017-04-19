package com.icesoft.tumblr.downloader.service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
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
import com.icesoft.tumblr.downloader.configure.TumblrToken;
import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class H2DBService {	
	private static Logger logger = Logger.getLogger(H2DBService.class);  
	private static H2DBService instance = new H2DBService();
	private Connection con;
	
	public static final String DRIVER = "org.h2.Driver";
	public static final String DB_URL = "jdbc:h2:";
	public static final String DB_NAME = "./tumblrdownloader";
	public static final String DB_USER = "sa";
	public static final String DB_PASS = "mypass";
	
	public static final String DOWNLOAD = "download";
	public static final String SETTINGS = "settings";
	public static final String SETTINGS_ID = "tumblrdownloader";
	
	
	private H2DBService(){init();}
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
/*				case 42102 : {
								logger.debug("Create Table [" + SETTINGS + "]");
								createSettingsTable();
								initSettingsTable();
				}break;*/
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
	        		+ "blogId VARCHAR(255),blogName VARCHAR(255),filename VARCHAR(255),filesize BIGINT, ext VARCHAR(64), savepath VARCHAR(255),totalTime BIGINT, priority INT)");
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
		        			 	+ " url:"		+ rs.getString("url") 
		        			   + " blogId:"		+ rs.getString("blogId") 
		        			   + " blogName:"	+ rs.getString("blogName") 
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
		 		    String blogId = rs.getString("blogId");
		 		    String blogName = rs.getString("blogName");
		        	Timestamp time = rs.getTimestamp("createtime");
		        	int state = rs.getInt("state");
		        	String filename = rs.getString("filename");
		        	long filesize = rs.getLong("filesize");
		        	String ext = rs.getString("ext");
		        	String savepath = rs.getString("savepath");
		        	long totalTime = rs.getLong("totalTime");
		        	int priority = rs.getInt("priority");
		        	DownloadContext task = new DownloadContext(url,blogId,blogName,filename, filesize, time.getTime(), state,ext, savepath,totalTime,priority);
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
		    String blogId = context.getBlogId();
 		    String blogName = context.getBlogName();
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
							+ " blogId='" + blogId + "'," 
							+ " blogName='" + blogName + "'," 
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
	         		+"proxyPort INT,"
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
	public void initSettingsTable() {
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
	        		+"'" + SETTINGS_ID +"',"
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
	         		+"'" + "" +"',"
	         		+"'" + 0 +"',"
	         		+"'" + "" +"',"
	         		+"'" + "" +"',"
	         		+"'" + "" +"',"
	         		+"'" + "" +"'"
	         		+")");   
	        stmt.close();
		} catch (SQLException e) {
			logger.debug("stmt execute." + e.getLocalizedMessage());
		} 
	}
	
	public Proxy loadProxy() {
		Statement stmt = null;
		try 
		{
			if(con != null)
			{
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT proxyType, proxyHost, proxyPort FROM " + SETTINGS + " WHERE id='" + SETTINGS_ID +"'");
		        if(rs.next())
		        {
			        int proxyType = rs.getInt("proxyType");
			        String proxyHost = rs.getString("proxyHost");
			        int proxyPort = rs.getInt("proxyPort"); 
			        Proxy proxy = null;
			        if(proxyType >0 && proxyType < Proxy.Type.values().length){
			        	proxy = new Proxy(Proxy.Type.values()[proxyType],new InetSocketAddress(proxyHost,proxyPort));
			        }else{
			        	proxy = null;
			        }
				    return proxy;
			        
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
								logger.debug("Create Table [" + SETTINGS + "]");
								createSettingsTable();	
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
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + SETTINGS + " WHERE id='" + SETTINGS_ID +"'");
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
		        
		        Proxy proxy = null;
		        if(proxyType >0 && proxyType < Proxy.Type.values().length){
		        	proxy = new Proxy(Proxy.Type.values()[proxyType],new InetSocketAddress(proxyHost,proxyPort));
		        }else{
		        	proxy = null;
		        }
		        
				String consumerKey = rs.getString("consumerKey");
				String consumerSecret =  rs.getString("consumerSecret");
				String oauthToken =  rs.getString("oauthToken");
				String oauthTokenSecret =  rs.getString("oauthTokenSecret");
				TumblrToken token = new TumblrToken(consumerKey,consumerSecret,oauthToken,oauthTokenSecret);
				Config config = new Config(basePath, 
		        		windowX, windowY, windowW, windowH, 
		        		workerCount, clientCount, 
		        		connectTimeout, readTimeout, 
		        		proxy,token);
		        return config;      
			}
		} catch (SQLException e) 
		{			
			switch(e.getErrorCode() )
			{
				case 90020 : {
								logger.debug("DB locked by another application. [" + DB_URL + "]");
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
	public TumblrToken loadToken() {
		Statement stmt = null;
		try 
		{
			if(con != null)
			{
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT consumerKey, consumerSecret, oauthToken,oauthTokenSecret FROM " + SETTINGS + " WHERE id='" + SETTINGS_ID +"'");
		        if(rs.next())
		        {
			        String consumerKey 		= rs.getString("consumerKey");
			        String consumerSecret 	= rs.getString("consumerSecret");
			        String oauthToken 		= rs.getString("oauthToken"); 
			        String oauthTokenSecret = rs.getString("oauthTokenSecret"); 
			        return new TumblrToken(consumerKey,consumerSecret,oauthToken,oauthTokenSecret);
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
								logger.debug("Create Table [" + SETTINGS + "]");
								createSettingsTable();	
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
	public String loadPath() {
		Statement stmt = null;
		try 
		{
			if(con != null)
			{
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT basePath FROM " + SETTINGS + " WHERE id='" + SETTINGS_ID +"'");
		        if(rs.next())
		        {
			        String basePath = rs.getString("basePath");
			        return basePath;
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
								logger.debug("Create Table [" + SETTINGS + "]");
								createSettingsTable();	
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
	public void update(String UPDATE){
		logger.debug("UPDATE BY [" + UPDATE + "]");
		Statement stmt = null;		
		try 
		{
			stmt = con.createStatement();
			stmt.execute(UPDATE);
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
	public static final String UPDATE_WINDOW_SETTINGS 	= "UPDATE " + SETTINGS + " SET  windowX='%d', windowY='%d', windowW='%d', windowH='%d' WHERE id='" + SETTINGS_ID +"'";
	public static final String UPDATE_TOKEN_SETTINGS 	= "UPDATE " + SETTINGS + " SET  consumerKey='%s', consumerSecret='%s', oauthToken='%s', oauthTokenSecret='%s' WHERE id='" + SETTINGS_ID +"'";
	public static final String UPDATE_PATH_SETTINGS 	= "UPDATE " + SETTINGS + " SET  basePath='%s' WHERE id='" + SETTINGS_ID +"'";
	public static final String UPDATE_PROXY_SETTINGS 	= "UPDATE " + SETTINGS + " SET  proxyType='%d', proxyHost='%s', proxyPort='%d' WHERE id='" + SETTINGS_ID +"'";
	public void updateWindowSettings(int x,int y,int w,int h) {
		update(String.format(UPDATE_WINDOW_SETTINGS, x,y,w,h));
	}
	public void updateToken(TumblrToken token) {
		if(token != null)
		{
			update(String.format(UPDATE_TOKEN_SETTINGS, token.getConsumer_key(),token.getConsumer_secret(),token.getOauth_token(),token.getOauth_token_secret()));
		}
	}
	public void updatePath(String path) {
		if(path != null && !path.trim().isEmpty())
		{
			update(String.format(UPDATE_PATH_SETTINGS, path));
		}
	}
	public void updateProxy(Proxy proxy) {
		if(proxy != null)
		{
			update(String.format(UPDATE_PROXY_SETTINGS, proxy.type().ordinal(),((InetSocketAddress)proxy.address()).getHostName(),((InetSocketAddress)proxy.address()).getPort()));
		}
		else
		{
			update(String.format(UPDATE_PROXY_SETTINGS, 0,"",0));
		}
	}
}
