package com.icesoft.tumblr.downloader.workers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.icesoft.utils.StringUtils;

public class HttpGetImageWorker implements IHttpGetWorker{
	private static Logger logger = Logger.getLogger(HttpGetImageWorker.class);  
	private String 	url;
	private STATE 	state;
	private String 	filename;
	private String 	filepath;
	private long   	filesize = 0;
	private long   	currsize = 0;
	private boolean stop = false;
	
	private float speed = 0;
	private String 	message;

	private HttpClient client;
	private HttpGet get;
	private File file;
	
	public HttpGetImageWorker(String url,String filepath){
		logger.info(Thread.currentThread().getName() + " : create HttpGetImageWorker instance.");
		this.url = url;
		this.filepath = filepath;
		this.state = STATE.WAIT;
	}
	public void init(){
		logger.info(Thread.currentThread().getName() + " : init HttpGetImageWorker instance.");
		client = HttpClients.createDefault();
		get = new HttpGet(url);
		
	}
	public boolean query(){
		logger.info(Thread.currentThread().getName() + " : HttpGetImageWorker query task begin.");
		try {
			this.state = STATE.QUERY;
			HttpResponse response = client.execute(get);
			int code = response.getStatusLine().getStatusCode();
			if(code == 200){
				filesize = response.getEntity().getContentLength();
				filename = StringUtils.getFilename(response, url);
				this.state = STATE.DOWNLOAD;
				this.currsize = getCurrFileSize();
				if(this.currsize > this.filesize){
					this.state = STATE.EXCEPTION;
					logger.error(Thread.currentThread().getName() + " : HttpGetImageWorker exception " + "Download error: invalid file[" + getFilename() + "].");
					setMessage("Download error: invalid file[" + getFilename() + "].");
					return false;
				}
				if(this.currsize == this.filesize){
					this.state = STATE.COMPLETE;
					logger.info(Thread.currentThread().getName() + " : HttpGetImageWorker:" + "File already complete [" + getFilename() + "].");
					setMessage("File already complete [" + getFilename() + "].");
					return false;
				}
				return true;
			}else{
				this.state = STATE.EXCEPTION;
				logger.error(Thread.currentThread().getName() + " : HttpGetImageWorker exception " + "Query error: Resopne code" + code);
				logger.error(Thread.currentThread().getName() + " : HttpGetImageWorker exception " + response.toString());
				setMessage("Query error: Resopne code" + code);
				return false;
			}
		}catch (IOException e) {
			this.state = STATE.EXCEPTION;
			logger.error(Thread.currentThread().getName() + " : HttpGetImageWorker exception " + "Query error:" + e.getMessage());
			setMessage("Query error:" + e.getMessage());
		}
		return false;
	}
	public void download(){
		logger.info(Thread.currentThread().getName() + " : HttpGetImageWorker download task begin.");
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
				logger.error(Thread.currentThread().getName() + " : HttpGetImageWorker download task error " + "Create Directory fails:" + getFilename());
				setMessage("Create Directory fails:" + getFilename());
				return;
			}
			FileOutputStream fos = new FileOutputStream(getFilename(), true);				
			logger.info(Thread.currentThread().getName() + " : HttpGetImageWorker download task started:" + "Continue downloading at " + this.currsize);
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
				logger.info(Thread.currentThread().getName() + " : HttpGetImageWorker download task " + "File is complete");
				setMessage("File is complete");
				this.state = STATE.COMPLETE;
				rbc.close();
				fos.close();
			}else{
				fos.close();
				logger.error(Thread.currentThread().getName() + " : HttpGetImageWorker download task error " + "Download error: incomplete.");
				setMessage("Download error: incomplete.");
			}
		}catch (IOException e) {
			this.state = STATE.EXCEPTION;
			logger.error(Thread.currentThread().getName() + " : HttpGetImageWorker download error " + e.getMessage());
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
		return filepath + File.separator + filename;
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
		this.message = msg;
	}
	@Override
	public void stop() {
		stop = true;
	}
}