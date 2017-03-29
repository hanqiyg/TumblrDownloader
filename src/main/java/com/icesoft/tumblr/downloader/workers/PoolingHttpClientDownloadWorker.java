package com.icesoft.tumblr.downloader.workers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.workers.interfaces.IHttpClientDownloadWorker;
import com.icesoft.utils.MineType;
import com.icesoft.utils.StringUtils;

public class PoolingHttpClientDownloadWorker implements IHttpClientDownloadWorker{
	private static Logger logger = Logger.getLogger(PoolingHttpClientDownloadWorker.class); 
	
	private CloseableHttpClient httpClient;
	private HttpGet get;
	private HttpContext context;
	
	private DownloadTask task;
	
	public PoolingHttpClientDownloadWorker(CloseableHttpClient httpClient,DownloadTask task){
		logger.info(Thread.currentThread().getName() + " : create HttpGetVideoWorker instance.");
		this.httpClient = httpClient;
		this.task = task;
		this.get = new HttpGet(task.getURL());
	}
	
	@Override
	public DownloadTask call() throws Exception
	{	
		query();
		download();
		return task;
	}
	public void query(){
		if(
				task.getState().equals(DownloadTask.STATE.QUERY_WAITING)
			||  task.getState().equals(DownloadTask.STATE.QUERY_EXCEPTION)
			||  task.getState().equals(DownloadTask.STATE.QUERY_RUNNING)
		){
			CloseableHttpResponse response = null;
			try {
				logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker query task begin.");
				task.setState(DownloadTask.STATE.QUERY_RUNNING);
				response = httpClient.execute(get,context);
				int code = response.getStatusLine().getStatusCode();
				if(code == 200)
				{
					task.setFilesize(response.getEntity().getContentLength());
					task.setFilename(StringUtils.getFilename(response, task.getURL()));
					task.setExt(MineType.getInstance().getExtensionFromMineType(response.getEntity().getContentType().getValue()));	
					task.setCurrentSize(task.getFile().length());
					if(task.getCurrentSize() > task.getFilesize())
					{
						task.setState(DownloadTask.STATE.DOWNLOAD_EXCEPTION);
						logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + "Download error: invalid file[" + task.getFile().getAbsolutePath() + "].");
						task.setMessage("Download error: invalid file[" + task.getFile().getAbsolutePath() + "].");
						return;
					}
					if(task.getCurrentSize() == task.getFilesize())
					{
						task.setState(DownloadTask.STATE.DOWNLOAD_COMPLETE);
						logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker:" + "File already complete [" +  task.getFile().getAbsolutePath() + "].");
						task.setMessage("File already complete ["  + task.getFile().getAbsolutePath() + "].");
						return;
					}
					task.setState(DownloadTask.STATE.QUERY_COMPLETE);
					return;
				}else
				{
					task.setState(DownloadTask.STATE.QUERY_EXCEPTION);
					logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + "Query error: Resopne code" + code);
					logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + response.toString());
					task.setMessage("Query error: Resopne code" + code);
					response.close();
					return;
				}
			} catch (IOException e) 
			{
				task.setState(DownloadTask.STATE.QUERY_EXCEPTION);
				logger.error(Thread.currentThread().getName() + "Query error: IOException" + e.getMessage());
				task.setMessage("Query error: IOException" + e.getMessage());
				return;
			}finally{
				try {
					if(response != null)
					{
						response.close();
					}
				} catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void download()
	{	FileOutputStream fos = null;
		ReadableByteChannel rbc = null;
		CloseableHttpResponse response = null;
		if(
				task.getState().equals(DownloadTask.STATE.QUERY_COMPLETE)
			||  task.getState().equals(DownloadTask.STATE.DOWNLOAD_WAITING)
			||  task.getState().equals(DownloadTask.STATE.DOWNLOAD_PAUSING)
			||  task.getState().equals(DownloadTask.STATE.DOWNLOAD_RUNNING)
		){
			logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker download task begin.");
			try{
				task.setState(DownloadTask.STATE.DOWNLOAD_RUNNING);
				get.addHeader("Range", "bytes="+task.getFile().length()+"-");
				response = httpClient.execute(get,context);
				rbc = Channels.newChannel(response.getEntity().getContent());
				long remainingSize = response.getEntity().getContentLength();
				long buffer = remainingSize;
				if (remainingSize > 65536) {
					buffer = 1 << 16;
				} 
				if(!createDirectory(task.getFile())){
					logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker download task error " + "Create Directory fails:" + task.getFile().getAbsolutePath());
					task.setMessage("Create Directory fails:" + task.getFile().getAbsolutePath());
					response.close();
					return;
				}
				task.setCurrentSize(task.getFile().length());
				fos = new FileOutputStream(task.getFile().getAbsolutePath(), true);				
				logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker download task started:" + "Continue downloading at " + task.getCurrentSize());
				while (task.isRun() && remainingSize > 0) {
					long begin = System.currentTimeMillis();
					long delta = fos.getChannel().transferFrom(rbc, task.getCurrentSize(), buffer);
					long end = System.currentTimeMillis();
					task.setCurrentSize(task.getCurrentSize() + delta);
					long t = end - begin;
					task.setCurrentSpeed(t,delta);
					task.addTotalTime(t);
					//System.out.println(this.currsize + " bytes received");
					if (delta == 0) {
						break;
					}
				}
				if(task.getCurrentSize() > task.getFilesize())
				{
					task.setState(DownloadTask.STATE.DOWNLOAD_EXCEPTION);
					logger.error(Thread.currentThread().getName() + " : HttpGetVideoWorker exception " + "Download error: invalid file[" + task.getFile().getAbsolutePath() + "].");
					task.setMessage("Download error: invalid file[" + task.getFile().getAbsolutePath() + "].");
					return;
				}
				if(task.getCurrentSize() == task.getFilesize())
				{
					task.setState(DownloadTask.STATE.DOWNLOAD_COMPLETE);
					logger.info(Thread.currentThread().getName() + " : HttpGetVideoWorker:" + "File already complete [" +  task.getFile().getAbsolutePath() + "].");
					task.setMessage("File download complete ["  + task.getFile().getAbsolutePath() + "].");
					return;
				}				
			}catch (IOException e) {
				task.setState(DownloadTask.STATE.DOWNLOAD_EXCEPTION);
				logger.error(Thread.currentThread().getName() + "Query error: IOException" + e.getMessage());
				task.setMessage("Download error: IOException" + e.getMessage());
				return;
			}finally{
				try {
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
						fos.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	@Override
	public DownloadTask getTask() {
		return task;
	}
	@Override
	public void stop() {
		task.setRun(false);
	}
}
