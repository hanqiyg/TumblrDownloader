package com.icesoft.tumblr.downloader.service;

import java.io.IOException;
import java.io.StringReader;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.icesoft.htmlparser.tags.SourceTag;
import com.icesoft.htmlparser.tags.VideoTag;
import com.icesoft.tumblr.model.VideoInfo;

public class UrlService 
{
	private static Logger logger = Logger.getLogger(UrlService.class);  
	public static VideoInfo getVideoInfoFromEmbed(String embed) throws ParserException, IOException
	{
		VideoInfo vi = new VideoInfo();
		Parser parser = new Parser(embed);	
        PrototypicalNodeFactory p=new PrototypicalNodeFactory();  
        p.registerTag(new VideoTag()); 
        p.registerTag(new SourceTag());   
        parser.setNodeFactory(p);
		NodeFilter videoFilter = new NodeClassFilter(VideoTag.class);
		NodeFilter sourceFilter = new NodeClassFilter(SourceTag.class);  
		OrFilter or = new OrFilter(videoFilter,sourceFilter);
		NodeList nodes = parser.extractAllNodesThatMatch(or);
        Node eachNode = null;
		if(nodes != null && nodes.size() > 0)
		{
			for (int i = 0; i < nodes.size(); i++) 
            {
                eachNode = (Node)nodes.elementAt(i);
                if(eachNode!= null && eachNode instanceof VideoTag)
                {
                	VideoTag t = (VideoTag) eachNode;            		
                	if(t.getPoster()!= null)
                	{
                    	vi.posterUrl = t.getPoster();
                	}
                	String opt = t.getOption();      	
                	findVideoInfoFromEmbed(opt,vi);
                }
                if(eachNode!= null && eachNode instanceof SourceTag)
                {
        			SourceTag st = (SourceTag) eachNode;
        			if(st.getSrc() != null){
            			vi.baseUrl = st.getSrc();
        			}
        			if(st.getType() != null){
        				vi.mineType = st.getType();
        			}
                }
            }
		}else{
			logger.error("getVideoInfoFromEmbed() can not find any VideoTag / SourceTag From " + embed); 
		}
		return vi;	
	}
	public static String getStringToken(JsonReader reader,String s) throws IOException
	{
		JsonToken nextToken = reader.peek();
		if(JsonToken.STRING.equals(nextToken)){
			s = reader.nextString();
		}else{
			reader.skipValue();
		}
		return s;
	}
	public static void findVideoInfoFromEmbed(String embed,VideoInfo info) throws IOException
	{	
		JsonReader jsonReader = new JsonReader(new StringReader(embed));
		jsonReader.setLenient(true);
        while(jsonReader.hasNext()){
            JsonToken nextToken = jsonReader.peek();
            if(JsonToken.BEGIN_OBJECT.equals(nextToken)){
                jsonReader.beginObject();
            } else if(JsonToken.NAME.equals(nextToken)){
                String name  =  jsonReader.nextName();
                if(name.equals("hdUrl")){
                	JsonToken token = jsonReader.peek();
                	if(JsonToken.STRING.equals(token))
                	{
                		info.hdUrl = jsonReader.nextString();
                	}else
                	{
                		info.hdUrl = null;
                		jsonReader.skipValue();
                	}
                }else if(name.equals("filmstrip")){
                	JsonToken token = jsonReader.peek();
                	if(JsonToken.STRING.equals(token))
                	{
                		String strip = jsonReader.nextString();
                		findPosterFromFileStrip(strip, info);
                	}else
                	{
                		jsonReader.skipValue();
                	}
                }else{
                	jsonReader.skipValue();
                }           
            }else if(JsonToken.END_OBJECT.equals(nextToken)){                        	
            	jsonReader.endObject();
            }else if(JsonToken.BEGIN_ARRAY.equals(nextToken)){                        	
            	jsonReader.beginArray();
            }else if(JsonToken.END_ARRAY.equals(nextToken)){                        	
            	jsonReader.endArray();
            }else{
            	jsonReader.skipValue();
            }
        }
		jsonReader.close();
	}
	private static void findPosterFromFileStrip(String strip,VideoInfo info) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(strip));
		jsonReader.setLenient(true);
        while(jsonReader.hasNext())
        {
            JsonToken nextToken = jsonReader.peek();
            if(JsonToken.BEGIN_OBJECT.equals(nextToken))
            {
                jsonReader.beginObject();
            } else if(JsonToken.NAME.equals(nextToken))
            {
                String name  =  jsonReader.nextName();
                if(name.equals("url")){
                	JsonToken token = jsonReader.peek();
                	if(JsonToken.STRING.equals(token))
                	{
                		info.hdPosterUrl = jsonReader.nextString();
                	}else
                	{
                		info.hdPosterUrl = null;
                		jsonReader.skipValue();
                	}                
                }else{
                	jsonReader.skipValue();
                }           
            }else if(JsonToken.END_OBJECT.equals(nextToken))
            {                        	
            	jsonReader.endObject();
            }else if(JsonToken.BEGIN_ARRAY.equals(nextToken))
            {                        	
            	jsonReader.beginArray();
            }else if(JsonToken.END_ARRAY.equals(nextToken))
            {                        	
            	jsonReader.endArray();
            }else{
            	jsonReader.skipValue();
            }
        }
        jsonReader.close();
	}
}
