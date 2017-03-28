package com.icesoft.tumblr.downloader.workers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.log4j.Logger;

import com.icesoft.utils.MineType;
import com.icesoft.utils.StringUtils;

public class HttpGetVideoWorker implements IHttpGetWorker{
	private static Logger logger = Logger.getLogger(HttpGetVideoWorker.class);  

	private String 	url;
	private STATE 	state;
	private String 	filename;
	private String 	ext;
	private String 	filepath;
	private long   	filesize = 0;
	private long   	currsize = 0;
	private boolean stop = false;
	
	private float speed = 0;
	private String 	message;

	private HttpClient client;
	private HttpGet get;
	private File file;
	
	public HttpGetVideoWorker(String url,String filepath){
		logger.info(Thread.currentThread().getName() + " : create HttpGetVideoWorker instance.");
		this.url = url;
		this.filepath = filepath;
		this.state = STATE.WAIT;
	}
	public void init()
	{
		logger.info(Thread.currentThread().getName() + " : init HttpGetVideoWorker instance.");
			//client = newClient(30*1000);
			//client = buildAllowallClient();
			//client = HttpClients.createDefault();

			HttpHost proxy = new HttpHost("127.0.0.1", 1080);
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			client = HttpClients.custom()
			        .setRoutePlanner(routePlanner)
			        .build();
			get = new HttpGet(url);
	}
/*	public static CloseableHttpClient newClient(int timeout) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException 
	{
		HttpClientBuilder builder = HttpClients.custom();
		builder.useSystemProperties();
		builder.disableAutomaticRetries();
		
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() 
		{
			@Override
			public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws java.security.cert.CertificateException {
				return true;
			}
	    }).build();
		builder.setSSLContext(sslContext);
		builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		configBuilder.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
		configBuilder.setSocketTimeout(timeout);
		configBuilder.setConnectTimeout(timeout);
		configBuilder.setConnectionRequestTimeout(timeout);
		builder.setDefaultRequestConfig(configBuilder.build());

		builder.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Consts.ISO_8859_1).build());

		return builder.build();
	}*/
	private static CloseableHttpClient buildAllowallClient() {
	    String[] protocols =  getSSLPrototocolsFromSystemProperties();
	    //SSLContext sslcontext = SSLContexts.createDefault();
	    SSLContext sslcontext;
	    try {
	        sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
	    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
	        throw new RuntimeException(e);
	    }
	    // Allow TLSv1 protocol only
	    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, protocols, null, new NoopHostnameVerifier());
	    CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

	    return httpclient;
	}
	
	private static String[] getSSLPrototocolsFromSystemProperties() 
	{
		 String protocols = System.getProperty("jdk.tls.client.protocols");
        if (protocols == null)
            protocols = System.getProperty("https.protocols");
        if (protocols != null) {
            String[] protocolsArray = protocols.split(",");
            return protocolsArray;
        }
        return null;
	}
	public boolean query(){
		logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker query task begin.");
		try {
			this.state = STATE.QUERY;
			HttpResponse response = client.execute(get);
			int code = response.getStatusLine().getStatusCode();
			if(code == 200){
				filesize = response.getEntity().getContentLength();
				filename = StringUtils.getFilename(response, url);
				ext = MineType.getInstance().getExtensionFromMineType(response.getEntity().getContentType().getValue());				
				this.state = STATE.DOWNLOAD;
				this.currsize = getCurrFileSize();
				if(this.currsize > this.filesize){
					this.state = STATE.EXCEPTION;
					logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + "Download error: invalid file[" + getFilename() + "].");
					setMessage("Download error: invalid file[" + getFilename() + "].");
					return false;
				}
				if(this.currsize == this.filesize){
					this.state = STATE.COMPLETE;
					logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker:" + "File already complete [" + getFilename() + "].");
					setMessage("File already complete [" + getFilename() + "].");
					return false;
				}
				return true;
			}else{
				this.state = STATE.EXCEPTION;
				logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + "Query error: Resopne code" + code);
				logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + response.toString());
				setMessage("Query error: Resopne code" + code);
				return false;
			}
		}catch (IOException e) {
			this.state = STATE.EXCEPTION;
			logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + "Query error:" + e.getMessage());
			setMessage("Query error:" + e.getMessage());
		}
		return false;
	}
	public void download(){
		logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker download task begin.");
		try{
			this.state = STATE.DOWNLOAD;
			get.addHeader("Range", "bytes="+this.currsize+"-");
			HttpResponse response = client.execute(get);
			ReadableByteChannel rbc = Channels.newChannel(response.getEntity().getContent());
			long remainingSize = response.getEntity().getContentLength();
			long buffer = remainingSize;
			if (remainingSize > 65536) {
				buffer = 1 << 16;
			} 
			if(!createDirectory(getFilename())){
				logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker download task error " + "Create Directory fails:" + getFilename());
				setMessage("Create Directory fails:" + getFilename());
				return;
			}
			FileOutputStream fos = new FileOutputStream(getFilename(), true);				
			logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker download task started:" + "Continue downloading at " + this.currsize);
			while (!stop && remainingSize > 0) {
				long begin = System.currentTimeMillis();
				long delta = fos.getChannel().transferFrom(rbc, this.currsize, buffer);
				this.currsize += delta;
				long end = System.currentTimeMillis();
				long t = end - begin;
				setSpeed(t,delta);
				
				//System.out.println(this.currsize + " bytes received");
				if (delta == 0) {
					break;
				}
			}
			if (this.currsize == this.filesize) {
				logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker download task " + "File is complete");
				setMessage("File is complete");
				this.state = STATE.COMPLETE;
				rbc.close();
				fos.close();
			}else{
				fos.close();
				logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker download task error " + "Download error: incomplete.");
				setMessage("Download error: incomplete.");
			}
		}catch (IOException e) {
			this.state = STATE.EXCEPTION;
			logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker download error " + e.getMessage());
			setMessage("Download error:" + e.getMessage());
			return;
		}
	}
	private void setSpeed(long time, long delta) {
		if(time > 0){
			this.speed = delta/time;
		}else{
			this.speed = 0;
		}
	}
	@Override
	public float getSpeed(){
		return speed;
	}
	@Override
	public Void call() throws Exception {
		init();
		if(!query()){
			return null;
		}
		download();
		return null;
	}
	private long getCurrFileSize() {
		if(file == null){
			file = new File(getFilename());
		}
		return file.length();
	}
	private boolean createDirectory(String filename) {
		File file = new File(filename);
		if(!file.getParentFile().exists()){
			return file.getParentFile().mkdirs();
		}
		return true;
	}

	@Override
	public int getProgress() {
		if(filesize == 0){
			return 0;
		}else{
			//System.out.println(this.currsize + "/" + this.filesize + "=" + (this.currsize * 100 / this.filesize) + "(" + (int) (this.currsize * 100 / this.filesize) + ")");
			return (int) (this.currsize * 100 / this.filesize);
		}
	}
	@Override
	public String getUrl() {
		return this.url;
	}
	@Override
	public long getCurrent() {
		return this.currsize;
	}
	@Override
	public long getFilesize() {
		return this.filesize;
	}
	@Override
	public String getFilename() {
		return filepath + File.separator + filename + "." + ext;
	}
	@Override
	public STATE getState() {
		return state;
	}
	@Override
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String msg){
		//System.out.println(msg);
		this.message = msg;
	}
	@Override
	public void stop() {
		stop = true;
	}
}
