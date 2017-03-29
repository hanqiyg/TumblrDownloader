package com.icesoft.tumblr.downloader.managers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.configure.Settings;
import com.icesoft.tumblr.downloader.monitor.IdleConnectionMonitor;

public class HttpClientConnectionManager 
{
	private static Logger logger = Logger.getLogger(HttpClientConnectionManager.class);  
	private PoolingHttpClientConnectionManager connManager;
	private IdleConnectionMonitor monitor;
	private CloseableHttpClient client;

	private static HttpClientConnectionManager instance = new HttpClientConnectionManager();
	
	private HttpClientConnectionManager(){
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() 
		{
		    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
		        // Honor 'keep-alive' header
		        HeaderElementIterator it = new BasicHeaderElementIterator(
		                response.headerIterator(HTTP.CONN_KEEP_ALIVE));
		        while (it.hasNext())
		        {
		            HeaderElement he = it.nextElement();
		            String param = he.getName();
		            String value = he.getValue();
		            if (value != null && param.equalsIgnoreCase("timeout")) 
		            {
		                try 
		                {
		                    return Long.parseLong(value) * 1000;
		                } catch(NumberFormatException ignore) {}
		            }
		        }
		        return 50 * 1000;
		    }
		};
		HttpHost proxy = new HttpHost(
				Settings.getInstance().getProxySettings().getHost(),
				Settings.getInstance().getProxySettings().getPort()						
				);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		connManager = new PoolingHttpClientConnectionManager();
		connManager.setDefaultMaxPerRoute(5);
		connManager.setMaxTotal(Settings.getInstance().getHttpClientCount());
		client = HttpClients.custom()
				  .setConnectionManager(connManager)
				  .setRoutePlanner(routePlanner)
				  .setKeepAliveStrategy(myStrategy)
				  .build();
		monitor = new IdleConnectionMonitor(this);
		monitor.start();		
	}
	public static HttpClientConnectionManager getInstance(){
		return instance;
	}

	public CloseableHttpClient getHttpClient()
	{
		return client;
	}
	public void closeIdleConnections(int time, TimeUnit unit) {
		connManager.closeIdleConnections(time, unit);
	}
	public void closeExpiredConnections() {
		connManager.closeExpiredConnections();		
	}
	public void shutdown() throws IOException{
		monitor.shutdown();
		connManager.close();
		client.close();
	}
	public PoolStats getStats(){
		return connManager.getTotalStats();
	}
}
