package com.icesoft.tumblr.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.workers.DownloadTask.STATE;
import com.icesoft.utils.MineType;
import com.icesoft.utils.StringUtils;

public class HttpGetUtils {
	public static DownloadState networkQuery(DownloadContext downloadContext){
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
				return DownloadState.LOCAL_QUERY;
			}else
			{
				downloadContext.setMessage("StatusCode is not fit." + response.getStatusLine().getStatusCode());
				return DownloadState.EXCEPTION;
			}
			
		} catch (IOException e) 
        {
			downloadContext.setMessage("Connection Exception. [" + e.getLocalizedMessage() + "]");
			return DownloadState.EXCEPTION;
		}finally
        {		
			try {
				EntityUtils.consume(response.getEntity());
			} catch (IOException e) {
				downloadContext.setMessage("consume error.[" + e.getLocalizedMessage() +"]");
				return DownloadState.EXCEPTION;
			}
			try 
			{
				response.close();
			} catch (IOException e) {
				downloadContext.setMessage("close response error.");
				return DownloadState.EXCEPTION;
			}
			get.releaseConnection();
		}
	}
	public static DownloadState localQuery(DownloadContext downloadContext)
	{
		File file = new File(downloadContext.getAbsolutePath());
		if(file.exists())
		{
			if(file.length() == downloadContext.getRemoteFilesize()){
				downloadContext.setLocalFilesize(file.length());
				downloadContext.setMessage("download complete.");
				return DownloadState.COMPLETE;
			}
			if(file.length() > downloadContext.getRemoteFilesize()){
				downloadContext.setLocalFilesize(0);
				downloadContext.setMessage("local file error." + "Local:" + file.length() + " Remote:" + downloadContext.getRemoteFilesize());
				return DownloadState.EXCEPTION;
			}
			if(file.length() < downloadContext.getRemoteFilesize()){
				downloadContext.setLocalFilesize(0);
				downloadContext.setMessage("continue download at " + file.length() + "of" + downloadContext.getRemoteFilesize());
				return DownloadState.RESUME;
			}
		}else{
			if(createDirectory(file)){
				downloadContext.setMessage("create parent folder success. " + file.getAbsolutePath());
				return DownloadState.DOWNLOAD;
			}else{
				downloadContext.setMessage("create parent folder error. " + file.getAbsolutePath());
				return DownloadState.EXCEPTION;
			}
		}
		return DownloadState.EXCEPTION;
	}
	public static DownloadState download(DownloadContext downloadContext,boolean resume){
		CloseableHttpClient client = HttpClientConnectionManager.getInstance().getHttpClient();
		HttpGet get = new HttpGet(downloadContext.getURL());
		if(resume){
			get.addHeader("Range", "bytes="+ downloadContext.getLocalFilesize() +"-");
		}
		CloseableHttpResponse response = null;
        try {
			response = client.execute(get);
			if(response.getStatusLine().getStatusCode() >=200 && response.getStatusLine().getStatusCode() < 300){
				downloadContext.setMessage("Downloading.");
				ReadableByteChannel rbc = null;
				FileOutputStream fos = null;
				HttpEntity entity = response.getEntity();
				try {
					rbc = Channels.newChannel(entity.getContent());
					fos = new FileOutputStream(downloadContext.getAbsolutePath(), true);
					long remainingSize = entity.getContentLength();
					long buffer = remainingSize;
					if (remainingSize > 65536) {
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
						if(downloadContext.getRemoteFilesize() < downloadContext.getLocalFilesize()){
							downloadContext.setMessage("Local File Exception.[LOCAL:"+ downloadContext.getLocalFilesize() +" Remote:" + downloadContext.getRemoteFilesize() + "]");	
							return DownloadState.EXCEPTION;
						}
					}
					if(downloadContext.getRemoteFilesize() != downloadContext.getLocalFilesize()){
						if(!downloadContext.isRun()){
							downloadContext.setMessage("User Canceled.");
							return DownloadState.PAUSE;
						}else{
							downloadContext.setMessage("Local File Exception.[LOCAL:"+ downloadContext.getLocalFilesize() +" Remote:" + downloadContext.getRemoteFilesize() + "]");
							return DownloadState.EXCEPTION;
						}
					}else{
						downloadContext.setMessage("Download Complete.");
						return DownloadState.COMPLETE;
					}

				} catch (UnsupportedOperationException e) {
					downloadContext.setMessage("Byte Channel Exception. [" + e.getLocalizedMessage() + "]");
					return DownloadState.EXCEPTION;
				} catch (FileNotFoundException e) {
					downloadContext.setMessage("FileOutputStream Exception. [" + e.getLocalizedMessage() + "]");
					return DownloadState.EXCEPTION;
				} catch (IOException e) {
					downloadContext.setMessage("Write to File Exception. [" + e.getLocalizedMessage() + "]");
					return DownloadState.EXCEPTION;
				}finally{
					EntityUtils.consume(entity);
					if(rbc != null)
					{
						try {
							rbc.close();
						} catch (IOException e) {
							downloadContext.setMessage("Byte Channel close Exception. [" + e.getLocalizedMessage() + "]");
							return DownloadState.EXCEPTION;
						}
					}
					if(fos != null)
					{
						try {
							fos.close();
						} catch (IOException e) {
							downloadContext.setMessage("FileOutputStream close Exception. [" + e.getLocalizedMessage() + "]");
							return DownloadState.EXCEPTION;
						}
					}
				}
			}else{
				downloadContext.setMessage("StatusCode is not fit." + response.getStatusLine().getStatusCode());
				return DownloadState.EXCEPTION;
			}
		} catch (IOException e) {
			downloadContext.setMessage("Connection Exception. [" + e.getLocalizedMessage() + "]");
			return DownloadState.EXCEPTION;
		}finally{			
			try {
				response.close();
			} catch (IOException e) {
				downloadContext.setMessage("close response error.");
				return DownloadState.EXCEPTION;
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
