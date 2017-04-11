package com.icesoft.tumblr.state;

import com.icesoft.tumblr.contexts.DownloadContext;
import com.icesoft.utils.HttpGetUtils;

public enum DownloadState{
	WAIT("WAIT")
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return NETWORK_QUERY;
		}
	},
	NETWORK_QUERY("NETWORK_QUERY")
	{
		@Override
		public DownloadState execute(DownloadContext context)
		{
			return HttpGetUtils.networkQuery(context);
		}
	},LOCAL_QUERY("LOCAL_QUERY")
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return HttpGetUtils.localQuery(context);
		}
	},PAUSE("PAUSE")
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	},RESUME("RESUME")
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return HttpGetUtils.download(context,true);
		}
	},DOWNLOAD("DOWNLOAD")
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return HttpGetUtils.download(context,false);
		}
	},RECREATE("RECREATE")
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	},COMPLETE("COMPLETE")
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	},EXCEPTION("EXCEPTION")
	{
		@Override

		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	};
	private String nickname;
	private DownloadState(String nickname)
	{
		this.nickname = nickname;
	}
	public String getNickname()
	{
		return this.nickname;
	}
	public abstract DownloadState execute(DownloadContext context);
}
