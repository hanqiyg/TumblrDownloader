package com.icesoft.tumblr.state;

public enum DownloadState{
	CREATE("CREATE") 
	{
		@Override
		DownloadState execute(DownloadContext context) 
		{			
			return WAIT;
		}
	},
	WAIT("WAIT")
	{
		@Override
		DownloadState execute(DownloadContext context) 
		{
			return NETWORK_QUERY;
		}
	},
	NETWORK_QUERY("NETWORK_QUERY")
	{
		@Override
		DownloadState execute(DownloadContext context)
		{
			return HttpGetUtils.networkQuery(context);
		}
	},LOCAL_QUERY("LOCAL_QUERY")
	{
		@Override
		DownloadState execute(DownloadContext context) {
			return HttpGetUtils.localQuery(context);
		}
	},PAUSE("PAUSE")
	{
		@Override
		DownloadState execute(DownloadContext context) {
			return WAIT;
		}
	},RESUME("RESUME")
	{
		@Override
		DownloadState execute(DownloadContext context) {
			return HttpGetUtils.download(context,true);
		}
	},DOWNLOAD("DOWNLOAD")
	{
		@Override
		DownloadState execute(DownloadContext context) {
			return HttpGetUtils.download(context,false);
		}
	},RECREATE("RECREATE")
	{
		@Override
		DownloadState execute(DownloadContext context) {
			return null;
		}
	},COMPLETE("COMPLETE")
	{
		@Override
		DownloadState execute(DownloadContext context) {
			return null;
		}
	},EXCEPTION("EXCEPTION")
	{
		@Override
		DownloadState execute(DownloadContext context) {
			return null;
		}
	};
	private String nickname;
	private DownloadState(String nickname){
		this.nickname = nickname;
	}
	public String getNickname(){
		return this.nickname;
	}
	abstract DownloadState execute(DownloadContext context);
}
