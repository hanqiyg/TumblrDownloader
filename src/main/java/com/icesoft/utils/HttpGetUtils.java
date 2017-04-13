package com.icesoft.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.contexts.DownloadContext;
import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.state.DownloadState;

public class HttpGetUtils 
{
	private static Logger logger = Logger.getLogger(DownloadState.class);  
	public static DownloadState networkQuery(DownloadContext downloadContext)
	{
		CloseableHttpClient client = HttpClientConnectionManager.getInstance().getHttpClient();
		HttpGet get = new HttpGet(downloadContext.getURL());
		CloseableHttpResponse response = null;
        try {
			response = client.execute(get);
			if(response.getStatusLine().getStatusCode() >=200 && response.getStatusLine().getStatusCode() < 300)
			{
				downloadContext.setRemoteFilesize(response.getEntity().getContentLength());
				downloadContext.setFilename(StringUtils.getFilename(response, downloadContext.getURL()));
				downloadContext.setExt(MineType.getInstance().getExtensionFromMineType(response.getEntity().getContentType().getValue()));
		        if(!downloadContext.isRun()){
					logger.debug("return DownloadState.PAUSE");
		        	return DownloadState.PAUSE;
		        }
				logger.debug("return DownloadState.LOCAL_QUERY");
				return DownloadState.LOCAL_QUERY;
			}
			else
			{
				downloadContext.setMessage("StatusCode is not fit." + response.getStatusLine().getStatusCode());
				logger.debug("return DownloadState.EXCEPTION");
				return DownloadState.EXCEPTION;
			}
		} 
        catch (IOException e) 
        {
			downloadContext.setMessage("Connection Exception. [" + e.getLocalizedMessage() + "]");
			logger.debug("return DownloadState.EXCEPTION");
			return DownloadState.EXCEPTION;
		}finally
        {		
			if(response != null)
			{
				try 
				{
					response.close();
				} 
				catch (IOException e) 
				{
					downloadContext.setMessage("close response error.");
					logger.debug("return DownloadState.EXCEPTION");
					return DownloadState.EXCEPTION;
				}
			}
			get.releaseConnection();
		}
	}
	public static DownloadState localQuery(DownloadContext downloadContext)
	{
		File file = new File(downloadContext.getAbsolutePath());
		if(file.exists())
		{
			if(file.length() == downloadContext.getRemoteFilesize())
			{
				downloadContext.setLocalFilesize(file.length());
				downloadContext.setMessage("download complete.");
		        if(!downloadContext.isRun()){
					logger.debug("return DownloadState.PAUSE");
		        	return DownloadState.PAUSE;
		        }
				logger.debug("return DownloadState.COMPLETE");
				return DownloadState.COMPLETE;
			}
			if(file.length() > downloadContext.getRemoteFilesize())
			{
				downloadContext.setLocalFilesize(0);
				downloadContext.setMessage("local file error." + "Local:" + file.length() + " Remote:" + downloadContext.getRemoteFilesize());
				logger.debug("return DownloadState.EXCEPTION");
				return DownloadState.EXCEPTION;
			}
			if(file.length() < downloadContext.getRemoteFilesize())
			{
				downloadContext.setLocalFilesize(0);
				downloadContext.setMessage("continue download at " + file.length() + "of" + downloadContext.getRemoteFilesize());
		        if(!downloadContext.isRun()){
					logger.debug("return DownloadState.PAUSE");
		        	return DownloadState.PAUSE;
		        }
				logger.debug("return DownloadState.RESUME");
				return DownloadState.RESUME;
			}
		}
		else
		{
			if(createDirectory(file))
			{
				downloadContext.setMessage("create parent folder success. " + file.getAbsolutePath());
				logger.debug("return DownloadState.DOWNLOAD");
				return DownloadState.DOWNLOAD;
			}
			else
			{
				downloadContext.setMessage("create parent folder error. " + file.getAbsolutePath());
				logger.debug("return DownloadState.EXCEPTION");
				return DownloadState.EXCEPTION;
			}
		}
		return DownloadState.EXCEPTION;
	}
	public static DownloadState download(DownloadContext downloadContext,boolean resume)
	{
		CloseableHttpClient client = HttpClientConnectionManager.getInstance().getHttpClient();
		HttpGet get = new HttpGet(downloadContext.getURL());
		if(resume)
		{
			get.addHeader("Range", "bytes="+ downloadContext.getLocalFilesize() +"-");
		}
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		CloseableHttpResponse response = null;
        try {
			response = client.execute(get);
			if(response.getStatusLine().getStatusCode() >=200 && response.getStatusLine().getStatusCode() < 300)
			{
				downloadContext.setMessage("Downloading.");
				HttpEntity entity = response.getEntity();
				rbc = Channels.newChannel(entity.getContent());
				fos = new FileOutputStream(downloadContext.getAbsolutePath(), true);
				long remainingSize = entity.getContentLength();
				long buffer = remainingSize;
				if (remainingSize > 65536) 
				{
					buffer = 1 << 16;
				}
				while (remainingSize > 0 && downloadContext.isRun()) 
				{
					long begin = System.currentTimeMillis();
					long delta;
					delta = fos.getChannel().transferFrom(rbc, downloadContext.getLocalFilesize(), buffer);
					remainingSize -= delta;	
					long end = System.currentTimeMillis();
					downloadContext.setLocalFilesize(downloadContext.getLocalFilesize() + delta);
					long t = end - begin;
					downloadContext.setCurrentSpeed(t,delta);
					downloadContext.addTotalTime(t);
					if (delta == 0) 
					{
						break;
					}
					if(downloadContext.getRemoteFilesize() < downloadContext.getLocalFilesize())
					{
						downloadContext.setMessage("Local File Exception.[LOCAL:"+ UnitUtils.getFormatSize(downloadContext.getLocalFilesize()) 
								+" Remote:" + UnitUtils.getFormatSize(downloadContext.getRemoteFilesize()) + "]");
						logger.debug("return DownloadState.EXCEPTION");
						return DownloadState.EXCEPTION;
					}
				}
				if(downloadContext.getRemoteFilesize() != downloadContext.getLocalFilesize())
				{
					if(!downloadContext.isRun())
					{
						downloadContext.setMessage("User Canceled.");
						logger.debug("return DownloadState.PAUSE");
						return DownloadState.PAUSE;
					}
					else
					{
						downloadContext.setMessage("Local File Exception.[LOCAL:"+ UnitUtils.getFormatSize(downloadContext.getLocalFilesize()) 
							+" Remote:" + UnitUtils.getFormatSize(downloadContext.getRemoteFilesize()) + "]");
						logger.debug("return DownloadState.EXCEPTION");
						return DownloadState.EXCEPTION;
					}
				}
				else
				{
					downloadContext.setMessage("Download Complete.");
					logger.debug("return DownloadState.COMPLETE");
					return DownloadState.COMPLETE;
				}				
			}
			else
			{
				downloadContext.setMessage("StatusCode is not fit." + response.getStatusLine().getStatusCode());
				logger.debug("return DownloadState.EXCEPTION");
				return DownloadState.EXCEPTION;
			}
		} 
        catch (ClientProtocolException e) 
        {
			downloadContext.setMessage("StatusCode is not fit." + e.getLocalizedMessage());
			return DownloadState.EXCEPTION;
		} 
        catch (UnsupportedOperationException e) 
        {
			downloadContext.setMessage("StatusCode is not fit." + e.getLocalizedMessage());
			return DownloadState.EXCEPTION;
		} 
        catch (FileNotFoundException e) 
        {
			downloadContext.setMessage("StatusCode is not fit." + e.getLocalizedMessage());
			return DownloadState.EXCEPTION;
		} 
        catch (IOException e) 
        {
			downloadContext.setMessage("StatusCode is not fit." + e.getLocalizedMessage());
			return DownloadState.EXCEPTION;
		}
		finally
		{
			if(response != null)
			{
				try {
					response.close();
				} catch (IOException e) {
					downloadContext.setMessage("close response error.");
					logger.debug("return DownloadState.EXCEPTION");
					return DownloadState.EXCEPTION;
				}
			}
			if(rbc != null && rbc.isOpen())
			{
				try {	
					rbc.close();
				} catch (IOException e) {
					downloadContext.setMessage("Byte Channel close Exception. [" + e.getLocalizedMessage() + "]");
				}
			}
			if(fos != null )
			{
				try {
					fos.getChannel().force(true);
					fos.close();
				} catch (IOException e) {
					downloadContext.setMessage("FileOutputStream close Exception. [" + e.getLocalizedMessage() + "]");
				}
			}
			get.releaseConnection();
		}
	}
	private static boolean createDirectory(File file) {
		if(!file.getParentFile().exists()){
			return file.getParentFile().mkdirs();
		}
		return true;
	}
}
