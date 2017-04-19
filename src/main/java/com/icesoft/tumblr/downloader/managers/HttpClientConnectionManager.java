package com.icesoft.tumblr.downloader.managers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.monitor.IdleConnectionMonitor;
import com.icesoft.tumblr.downloader.service.SettingService;

public class HttpClientConnectionManager 
{
	private static final int TIMEOUT = 50 * 1000;
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
		        return TIMEOUT;
		    }
		};
	   RequestConfig requestConfig = RequestConfig.custom()
	            .setConnectionRequestTimeout(SettingService.getInstance().getConnectionTimeout())
	            .setSocketTimeout(SettingService.getInstance().readTimeout())
	            .setConnectionRequestTimeout(SettingService.getInstance().readTimeout())
	            .build();
		connManager = new PoolingHttpClientConnectionManager();
		connManager.setDefaultMaxPerRoute(5);
		connManager.setMaxTotal(SettingService.getInstance().getHttpClientCount());
		client = HttpClientBuilder
				.create()
				.setConnectionManager(connManager)
				.useSystemProperties()				
				.setDefaultRequestConfig(requestConfig)				  
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
	public void shutdown(){
		monitor.shutdown();
		connManager.close();
		try {
			client.close();
		} catch (IOException e) {
			logger.debug("close CloseableHttpClient exception.");
		}
	}
	public PoolStats getStats(){
		return connManager.getTotalStats();
	}
}
