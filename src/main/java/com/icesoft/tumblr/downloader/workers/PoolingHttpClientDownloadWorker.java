package com.icesoft.tumblr.downloader.workers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.exceptions.HttpClientExecuteException;
import com.icesoft.tumblr.downloader.exceptions.TaskInterruptedException;
import com.icesoft.utils.MineType;
import com.icesoft.utils.StringUtils;

public class PoolingHttpClientDownloadWorker implements Callable<Void>{
	private static Logger logger = Logger.getLogger(PoolingHttpClientDownloadWorker.class); 
	private CloseableHttpClient httpClient;
	private HttpGet get;
	private HttpContext context;
	
	private DownloadTask task;	
	
	private CloseableHttpResponse response = null;
	private ReadableByteChannel rbc = null;
	private FileOutputStream fos = null;	
	private RandomAccessFile file = null;
	private ByteBuffer source = null,target = null;
	
	public PoolingHttpClientDownloadWorker(CloseableHttpClient httpClient,DownloadTask task){
		logger.info(" create HttpGetVideoWorker instance.");
		this.httpClient = httpClient;
		this.task = task;
		this.get = new HttpGet(task.getURL());
	}
	
	@Override
	public Void call() throws Exception
	{	
		init();
		return null;
	}
	public void message(String msg,DownloadTask.STATE state){
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
		task.setMessage(msg);
	}
	public void init() throws TaskInterruptedException, HttpClientExecuteException{
		message("Query begin",DownloadTask.STATE.QUERY_RUNNING);
		if(get.containsHeader("Range"))
		{
			get.removeHeaders("Range");
		}
		try {
			response = httpClient.execute(get,context);
		} catch (IOException e) {
			throw new HttpClientExecuteException();
		}
		if(response.getStatusLine().getStatusCode() == 200)
		{
			task.setRemoteFilesize(response.getEntity().getContentLength());
			task.setFilename(StringUtils.getFilename(response, task.getURL()));
			task.setExt(MineType.getInstance().getExtensionFromMineType(response.getEntity().getContentType().getValue()));	
			task.setLocalFilesize(task.getFile().length());
			
			if(task.getRemoteFilesize() < task.getLocalFilesize()){
				message("Query Exception.[File size bigger than response context size]",DownloadTask.STATE.QUERY_EXCEPTION);
				close(response,null,null);
				return;
			}
			if(task.getRemoteFilesize() == task.getLocalFilesize()){
				if(checkTail())
				{
					message("Query complete.[File download complete.]",DownloadTask.STATE.DOWNLOAD_COMPLETE);
					close(response,null,null);
					return;
				}else
				{
					message("Query Exception.[File tail not match.]",DownloadTask.STATE.QUERY_EXCEPTION);
					close(response,null,null);
					return;
				}		
			}
			if(task.getRemoteFilesize() > task.getLocalFilesize())
			{
				if(task.getLocalFilesize() > 0)
				{
					if(checkTail())
					{
						close(response,null,null);
						message("Query success.[File tail match, continue to download.]",DownloadTask.STATE.QUERY_COMPLETE);
						download(false);
						return;
					}else
					{
						close(response,null,null);
						message("Query Exception.[File tail not match.]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
						return;
					}
				}else
				{
					close(response,null,null);
					message("Query success.[download from begining.]",DownloadTask.STATE.QUERY_COMPLETE);
					download(true);
					return;
				}
			}
		}else{
			close(response,null,null);
			message("Query Exception. [Status code is not 200]" + response.getStatusLine().getStatusCode(),DownloadTask.STATE.QUERY_EXCEPTION);
			return;
		}
	}
	
	private void download(boolean isFromBeginning) throws TaskInterruptedException{
		message("Download begin",DownloadTask.STATE.DOWNLOAD_RUNNING);
		if(get.containsHeader("Range"))
		{
			get.removeHeaders("Range");
		}
		task.setLocalFilesize(task.getFile().length());
		if(!isFromBeginning)
		{
			get.removeHeaders("Range");
			get.addHeader("Range", "bytes="+ task.getLocalFilesize() +"-");
		}else
		{	
			if(!createDirectory(task.getFile()))
			{
				message("Download Exception. [ create file fail. ]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
				close(response,rbc,fos);	
				return;
			}
		}
		try {
			response = httpClient.execute(get,context);
			int code = response.getStatusLine().getStatusCode();
			if(code != 200 && code != 206)
			{
				close(response,null,null);
				message("Download Exception. [Status code is not 200 or 206]" + response.getStatusLine().getStatusCode(),DownloadTask.STATE.DOWNLOAD_EXCEPTION);
				return;
			}
		} catch (ClientProtocolException e) {
			message("Download Exception. [" + e.getMessage() + "]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);	
			return;
		} catch (IOException e) {
			message("Download Exception. [" + e.getMessage() + "]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);	
			return;
		}
		try {
			rbc = Channels.newChannel(response.getEntity().getContent());
		} catch (UnsupportedOperationException e) {
			message("Download Exception. [" + e.getMessage() + "]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);	
			return;
		} catch (IOException e) 
		{
			message("Download Exception. [" + e.getMessage() + "]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);	
			return;
		}
		try {
			fos = new FileOutputStream(task.getFile().getAbsolutePath(), true);
		} catch (FileNotFoundException e) 
		{		
			message("Download Exception. [" + e.getMessage() + "]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);	
			return;
		}
		long remainingSize = response.getEntity().getContentLength();
		long buffer = remainingSize;
		if (remainingSize > 65536) {
			buffer = 1 << 16;
		}
		try 
		{
			while (remainingSize > 0) 
			{
				if(Thread.currentThread().isInterrupted()){
					throw new TaskInterruptedException();
				}
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
				//logger.debug("write:" + delta);
			}
		} catch (IOException e) {
			message("Download Exception. [" + e.getMessage() + "]",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);
			return;
		}
		
		if(task.getFile().length() == task.getRemoteFilesize()){
			message("Download Complete.",DownloadTask.STATE.DOWNLOAD_COMPLETE);
			close(response,rbc,fos);
			return;
		}
		if(task.getFile().length() > task.getRemoteFilesize()){
			message("Download Exception. local file size is bigger than remote.",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);
			return;
		}
		if(task.getFile().length() < task.getRemoteFilesize()){
			message("Download Exception. local file size is smaller than remote.",DownloadTask.STATE.DOWNLOAD_EXCEPTION);
			close(response,rbc,fos);
			return;
		}
		close(response,rbc,fos);
	}
	private void shutdown() throws IOException{
		if(response != null)
		{
			response.close();
		}
		if(rbc != null)
		{
			rbc.close();
		}
		if(fos != null)
		{
			fos.getChannel().force(true);
			fos.close();
		}
		if(file != null)
		{
			file.close();
		}
		if(source != null)
		{
			source = null;
		}
		if(target != null)
		{
			target = null;
		}
	}
	private void close(	CloseableHttpResponse response,
						ReadableByteChannel rbc,
						FileOutputStream fos
						)
	{	try 
		{		
			if(fos != null)
			{
				fos.getChannel().force(true);
				fos.close();
			}
			if(rbc != null)
			{
			rbc.close();
			}
			if(response != null)
			{
				response.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private boolean checkTail() throws TaskInterruptedException {
		int tailLength = (int) (task.getRemoteFilesize()<<3);
		if(tailLength > 1024){
			tailLength = 1024;
		}else if(tailLength < 100){
			tailLength = 100;
		}
		try
		{	
			if(get.containsHeader("Range"))
			{
				get.removeHeaders("Range");
			}
			get.addHeader("Range", "bytes="+ (task.getFile().length() - tailLength) +"-");
			file = new RandomAccessFile(task.getFile().getAbsolutePath(), "rw");
			FileChannel inChannel = file.getChannel();
			source = ByteBuffer.allocate(tailLength);
			int sourceBuffered = 0;
			while(sourceBuffered < tailLength){
				if(Thread.currentThread().isInterrupted()){
					throw new TaskInterruptedException();
				}
				int count = inChannel.read(source);
				sourceBuffered += count;
			}
			target = ByteBuffer.allocate(tailLength);		
			response = httpClient.execute(get,context);
			int code = response.getStatusLine().getStatusCode();
			if(code != 200 && code != 206)
			{
				message("Tail check Exception. [Status code is not 200 or 206]" + response.getStatusLine().getStatusCode(),DownloadTask.STATE.QUERY_EXCEPTION);
				TailCost(source, target, file, response, rbc);
				return false;
			}
			rbc = Channels.newChannel(response.getEntity().getContent());
			int targetBuffered = 0;
			while (targetBuffered < tailLength) {
				if(Thread.currentThread().isInterrupted()){
					throw new TaskInterruptedException();
				}
				int delta = rbc.read(target);
				targetBuffered +=delta;
			}
			if(source.equals(target)){
				return true;
			}else{
				return false;
			}
		}catch(IOException e){
			
			message("Tail Check Exception. [" + e.getMessage() + "]",DownloadTask.STATE.QUERY_EXCEPTION);
			return false;
		}finally{
			TailCost(source, target, file, response, rbc);
		}
	}
	private void TailCost(ByteBuffer source,ByteBuffer target,RandomAccessFile file,
	CloseableHttpResponse response,
	ReadableByteChannel rbc){
		source = null;
		target = null;
		try {
			if(response != null)
			{	
				response.close();	
				response = null;
			}
			if(rbc != null)
			{
				rbc.close();
				rbc = null;
			}
			if(file != null){
				file.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private boolean createDirectory(File file) {
		if(!file.getParentFile().exists()){
			return file.getParentFile().mkdirs();
		}
		return true;
	}
}
