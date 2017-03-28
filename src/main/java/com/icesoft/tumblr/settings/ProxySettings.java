package com.icesoft.tumblr.settings;

public class ProxySettings {
	public enum Type{
		DIRECT,SOCKS,HTTP
	}
	private Type type;
	private String host;
	private int port;
	
	public ProxySettings(Type type,String host,int port){
		this.type = type;
		this.host = host;
		this.port = port;
	}
	public ProxySettings(String type,String host,int port){
		this.type = Type.valueOf(type);
		this.host = host;
		this.port = port;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}	
}
