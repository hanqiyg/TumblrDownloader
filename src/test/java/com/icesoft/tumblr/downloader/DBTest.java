package com.icesoft.tumblr.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class DBTest {
	private Connection con;
	
	public static final String DRIVER = "org.h2.Driver";
	public static final String DB_URL = "jdbc:h2:";
	public static final String DB_NAME = "./tumblrdownloader";
	public static final String DB_USER = "sa";
	public static final String DB_PASS = "mypass";
	
	public static final String SETTINGS_ID = "tumblrdownloader";

	private static final String[] type = {"TABLE"};
	private static final String DOWNLOAD = "DOWNLOAD";
	private static final String SCHEMA = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s'";
	private static final String[] DB_TABLE_DOWNLOAD_COLUMN_NAMES = {
		"URL",
		"CREATETIME",
		"STATE",
		"BLOGID",
		"BLOGNAME",
		"FILENAME",
		"FILESIZE",
		"EXT",
		"SAVEPATH",
		"TOTALTIME",
		"PRIORITY"
	};
	private static final String[] DB_TABLE_SETTINGS_COLUMN_NAMES = {
			"ID",
			"NAME",
			"VALUE",
			"BASEPATH",
			"CONNECTTIMEOUT",
			"READTIMEOUT",
			"WORKERCOUNT",
			"CLIENTCOUNT",
			"WINDOWX",
			"WINDOWY",
			"WINDOWW",
			"WINDOWH",
			"PROXYTYPE",
			"PROXYHOST",
			"PROXYPORT",
			"CONSUMERKEY",
			"CONSUMERSECRET",
			"OAUTHTOKEN",
			"OAUTHTOKENSECRET"
	};
	@Test
	public void test() throws ClassNotFoundException, SQLException
	{
		
		Class.forName(DRIVER);
	    con = DriverManager.getConnection( DB_URL + DB_NAME ,DB_USER,"");
	    DatabaseMetaData  md = con.getMetaData();
	    ResultSet rs = md.getTables(null, null, "%",null);
	    while (rs.next()) {
	    	  System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3)+ "\t\t" + rs.getString(4));
	    }	    
	}
	@Test
	public void SCHEMA() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, IOException
	{
		String t = "SETTINGS";
		getSchemaTable(t);
		byte[] bytes = getSchemaMd5(t);
		String md5 = getMD5Checksum(bytes);
		System.out.println(md5);
	}
	public void getSchemaTable(String tableName) throws ClassNotFoundException, SQLException
	{		
		Class.forName(DRIVER);
		con = DriverManager.getConnection( DB_URL + DB_NAME ,DB_USER,"");
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(String.format(SCHEMA, tableName));
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		for(int i = 1;i<count+1;i++){
			System.out.print(String.format("%25s", rsmd.getColumnLabel(i)));
		}
		System.out.println();
		
		while(rs.next()){
			for(int i = 1;i<count+1;i++){
				System.out.print(String.format("%25s", rs.getString(i)));
			}
			System.out.println();
		}
	    stmt.close();
	}
	public byte[] getSchemaMd5(String tableName) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, IOException
	{		
		Class.forName(DRIVER);
		con = DriverManager.getConnection( DB_URL + DB_NAME ,DB_USER,"");
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(String.format(SCHEMA, tableName));
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		MessageDigest complete = MessageDigest.getInstance("MD5");
		while(rs.next()){
			for(int i = 1;i<count+1;i++){
				byte[] b = rs.getString(i).getBytes();
				complete.update(b);
			}
		}		
	    stmt.close();
	    return complete.digest();
	}
	public static String getMD5Checksum(byte[] bytes){
		String result = "";
		for (int i=0; i < bytes.length; i++) {
			result +=
	        Integer.toString( ( bytes[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
}
