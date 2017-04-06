package com.icesoft.tumblr.downloader.workers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.workers.DownloadTask.STATE;
import com.icesoft.utils.MineType;
import com.icesoft.utils.StringUtils;

public class PoolingHttpGetDownloadWorker implements Callable<Void>{
	private static Logger logger = Logger.getLogger(PoolingHttpGetDownloadWorker.class); 
	
	private DownloadTask task;	
	public PoolingHttpGetDownloadWorker(DownloadTask task){
		this.task = task;
	}
	public DownloadTask getTask(){
		return this.task;
	}
	@Override
	public Void call() throws Exception {
		Query();
		fileCheck();
		download();
		return null;
	}

	private void fileCheck() {
		if(!task.isRun()){
			message("User canceled.",STATE.PAUSE);
			return;
		}
		if(task.getFile().exists()){
			if(task.getFile().length() > task.getRemoteFilesize())
			{
				task.setRun(false);
				message("Local File Exception.[LOCAL:"+task.getFile().length() +" Remote:" + task.getRemoteFilesize() + "]" ,
						STATE.QUERY_EXCEPTION);
			}else if(task.getFile().length() == task.getRemoteFilesize())
			{
				task.setRun(false);
				message("Downlad already complete." ,
						STATE.DOWNLOAD_COMPLETE);
			}else
			{
				task.setLocalFilesize(task.getFile().length());	
			}
		}else{
			if(!createDirectory(task.getFile())){
				task.setRun(false);
				message("Create Folder fail." ,
						STATE.QUERY_EXCEPTION);
			}
		}
	}
	private void Query(){
		if(!task.isRun()){
			message("User canceled.",STATE.PAUSE);
			return;
		}
		message("Query init.",STATE.QUERY_RUNNING);
		CloseableHttpClient client = HttpClientConnectionManager.getInstance().getHttpClient();
		HttpGet get = new HttpGet(task.getURL());
		CloseableHttpResponse response = null;
        try {
			response = client.execute(get);
			if(response.getStatusLine().getStatusCode() >=200 && response.getStatusLine().getStatusCode() < 300){
				task.setRemoteFilesize(response.getEntity().getContentLength());
				task.setFilename(StringUtils.getFilename(response, task.getURL()));
				task.setExt(MineType.getInstance().getExtensionFromMineType(response.getEntity().getContentType().getValue()));	
				task.setLocalFilesize(task.getFile().length());
			}else{
				message("StatusCode is not fit." + response.getStatusLine().getStatusCode(),STATE.QUERY_EXCEPTION);
			}
			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			message("Connection Exception. [" + e.getLocalizedMessage() + "]",STATE.DOWNLOAD_EXCEPTION);
		}finally{			
			try {
				response.close();
			} catch (IOException e) {
				message("close response error.",null);
			}
			get.releaseConnection();
		}
	}
	
	private void download(){
		if(!task.isRun()){
			message("User canceled.",STATE.PAUSE);
			return;
		}
		message("Download init.",STATE.DOWNLOAD_WAITING);
		CloseableHttpClient client = HttpClientConnectionManager.getInstance().getHttpClient();
		HttpGet get = new HttpGet(task.getURL());
		if(get.containsHeader("Range"))
		{
			get.removeHeaders("Range");
		}
		get.addHeader("Range", "bytes="+ task.getLocalFilesize() +"-");
		CloseableHttpResponse response = null;
        try {
			response = client.execute(get);
			if(response.getStatusLine().getStatusCode() >=200 && response.getStatusLine().getStatusCode() < 300){
				writeToFile(task.getFile().getAbsolutePath(),response.getEntity());
			}else{
				message("StatusCode is not fit." + response.getStatusLine().getStatusCode(),STATE.DOWNLOAD_EXCEPTION);
			}
			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			message("Connection Exception. [" + e.getLocalizedMessage() + "]",STATE.DOWNLOAD_EXCEPTION);
		}finally{			
			try {
				response.close();
			} catch (IOException e) {
				message("close response error.",null);
			}
			get.releaseConnection();
		}
	}
	public void writeToFile(String file,HttpEntity entity){
		//logger.debug(task.toString());
		if(!task.isRun()){
			message("User canceled.",STATE.PAUSE);
			return;
		}

		message("Downloading.",STATE.DOWNLOAD_RUNNING);
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		try {
			rbc = Channels.newChannel(entity.getContent());
			task.setLocalFilesize(task.getFile().length());
			fos = new FileOutputStream(file, true);
			long remainingSize = entity.getContentLength();
			long buffer = remainingSize;
			if (remainingSize > 65536) {
				buffer = 1 << 16;
			}
			while (remainingSize > 0 && task.isRun()) 
			{
				long begin = System.currentTimeMillis();
				long delta;
				delta = fos.getChannel().transferFrom(rbc, task.getLocalFilesize(), buffer);
				remainingSize -= delta;	
				long end = System.currentTimeMillis();
				task.setLocalFilesize(task.getLocalFilesize() + delta);
				long t = end - begin;
				task.setCurrentSpeed(t,delta);
				task.addTotalTime(t);
				if (delta == 0) 
				{
					break;
				}
				if(task.getRemoteFilesize() < task.getLocalFilesize()){
					message("Local File Exception.[LOCAL:"+task.getFile().length() +" Remote:" + task.getRemoteFilesize() + "]" ,
							STATE.DOWNLOAD_EXCEPTION);	
					break;
				}
			}
			if(task.getRemoteFilesize() != task.getLocalFilesize()){
				if(!task.isRun()){
					message("User Canceled." ,
							STATE.PAUSE);	
				}else{
					message("Local File Exception.[LOCAL:"+task.getFile().length() +" Remote:" + task.getRemoteFilesize() + "]" ,
							STATE.DOWNLOAD_EXCEPTION);	
				}
			}else{
				message("Download Complete." ,
						STATE.DOWNLOAD_COMPLETE);
			}
			EntityUtils.consume(entity);
		} catch (UnsupportedOperationException e) {
			message("Byte Channel Exception. [" + e.getLocalizedMessage() + "]",STATE.DOWNLOAD_EXCEPTION);		
		} catch (FileNotFoundException e) {
			message("FileOutputStream Exception. [" + e.getLocalizedMessage() + "]",STATE.DOWNLOAD_EXCEPTION);
		} catch (IOException e) {
			message("Write to File Exception. [" + e.getLocalizedMessage() + "]",STATE.DOWNLOAD_EXCEPTION);
			e.printStackTrace();
		}finally{
			if(rbc != null)
			{
				try {
					rbc.close();
				} catch (IOException e) {
					message("Byte Channel close Exception. [" + e.getLocalizedMessage() + "]",null);
				}
			}
			if(fos != null)
			{
				try {
					fos.close();
				} catch (IOException e) {
					message("FileOutputStream close Exception. [" + e.getLocalizedMessage() + "]",null);
				}
			}
		}
	}
	private boolean createDirectory(File file) {
		if(!file.getParentFile().exists()){
			return file.getParentFile().mkdirs();
		}
		return true;
	}
	public void message(String msg,DownloadTask.STATE state){
		if(state != null){
			task.setState(state);
			switch(state){
			case DOWNLOAD_COMPLETE:	{logger.info(msg);}
				break;
			case DOWNLOAD_EXCEPTION:{logger.error(msg);}
				break;
			case DOWNLOAD_PAUSING:	{logger.info(msg);}
				break;
			case DOWNLOAD_RUNNING:	{logger.info(msg);}
				break;
			case DOWNLOAD_WAITING:	{logger.info(msg);}
				break;
			case QUERY_COMPLETE:	{logger.info(msg);}
				break;
			case QUERY_EXCEPTION:	{logger.error(msg);}
				break;
			case QUERY_RUNNING:		{logger.info(msg);}
				break;
			case QUERY_WAITING:		{logger.info(msg);}
				break;
			default:				{logger.info(msg);}
				break;		
			}	
		}else{
			logger.debug(msg);
		}	
		task.setMessage(msg);
	}
}
