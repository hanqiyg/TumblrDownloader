package com.icesoft.tumblr.state;

import com.icesoft.tumblr.state.interfaces.IContext;
import com.icesoft.utils.HttpGetUtils;

public enum DownloadState{
	CREATE
	{
		@Override
		public DownloadState execute(IContext context) 
		{
			return WAIT;
		}
	},
	WAIT
	{
		@Override
		public DownloadState execute(IContext context) 
		{
			return NETWORK_QUERY;
		}
	},
	NETWORK_QUERY
	{
		@Override
		public DownloadState execute(IContext context)
		{
			return HttpGetUtils.networkQuery(context);
		}
	},LOCAL_QUERY
	{
		@Override
		public DownloadState execute(IContext context) 
		{
			return HttpGetUtils.localQuery(context);
		}
	},PAUSE
	{
		@Override
		public DownloadState execute(IContext context) 
		{
			return null;
		}
	},RESUME
	{
		@Override
		public DownloadState execute(IContext context) 
		{
			return HttpGetUtils.download(context,true);
		}
	},DOWNLOAD
	{
		@Override
		public DownloadState execute(IContext context) 
		{
			return HttpGetUtils.download(context,false);
		}
	},COMPLETE
	{
		@Override
		public DownloadState execute(IContext context) 
		{
			return null;
		}
	},EXCEPTION
	{
		@Override

		public DownloadState execute(IContext context) 
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
	public abstract DownloadState execute(IContext context);
}
