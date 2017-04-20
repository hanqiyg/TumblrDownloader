package com.icesoft.tumblr.downloader.configure;
public class TumblrToken {
	private String consumer_key;
	private String consumer_secret;
	private String oauth_token;
	private String oauth_token_secret;
	
	public TumblrToken(String consumer_key,String consumer_secret,String oauth_token,String oauth_token_secret){
		this.consumer_key = consumer_key;
		this.consumer_secret = consumer_secret;
		this.oauth_token = oauth_token;
		this.oauth_token_secret = oauth_token_secret;
	}
	public String getConsumer_key() {
		return consumer_key;
	}
	public void setConsumer_key(String consumer_key) {
		this.consumer_key = consumer_key;
	}
	public String getConsumer_secret() {
		return consumer_secret;
	}
	public void setConsumer_secret(String consumer_secret) {
		this.consumer_secret = consumer_secret;
	}
	public String getOauth_token() {
		return oauth_token;
	}
	public void setOauth_token(String oauth_token) {
		this.oauth_token = oauth_token;
	}
	public String getOauth_token_secret() {
		return oauth_token_secret;
	}
	public void setOauth_token_secret(String oauth_token_secret) {
		this.oauth_token_secret = oauth_token_secret;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj != null 
			&& obj instanceof TumblrToken
			&& ((TumblrToken)obj).getConsumer_key()			.equals(this.getConsumer_key())
			&& ((TumblrToken)obj).getConsumer_secret()		.equals(this.getConsumer_secret())
			&& ((TumblrToken)obj).getOauth_token()			.equals(this.getOauth_token())
			&& ((TumblrToken)obj).getOauth_token_secret()	.equals(this.getOauth_token_secret()))
		{
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return 	"      consumer_key : [" 	+ this.consumer_key 		+ "]" + Constants.ENTER
			+	"   consumer_secret : [" 	+ this.consumer_secret 		+ "]" + Constants.ENTER
			+	"       oauth_token : ["	+ this.oauth_token 			+ "]" + Constants.ENTER
			+ 	"oauth_token_secret : ["	+ this.oauth_token_secret 	+ "]" + Constants.ENTER;
	}
}
