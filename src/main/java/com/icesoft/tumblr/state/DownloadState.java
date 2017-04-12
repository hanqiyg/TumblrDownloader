package com.icesoft.tumblr.state;

import com.icesoft.tumblr.contexts.DownloadContext;
import com.icesoft.utils.HttpGetUtils;

public enum DownloadState{
	WAIT
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return NETWORK_QUERY;
		}
	},
	NETWORK_QUERY
	{
		@Override
		public DownloadState execute(DownloadContext context)
		{
			return HttpGetUtils.networkQuery(context);
		}
	},LOCAL_QUERY
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return HttpGetUtils.localQuery(context);
		}
	},PAUSE
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	},RESUME
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return HttpGetUtils.download(context,true);
		}
	},DOWNLOAD
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return HttpGetUtils.download(context,false);
		}
	},RECREATE
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	},COMPLETE
	{
		@Override
		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	},EXCEPTION
	{
		@Override

		public DownloadState execute(DownloadContext context) 
		{
			return null;
		}
	};
    public static DownloadState valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
	public abstract DownloadState execute(DownloadContext context);
}
