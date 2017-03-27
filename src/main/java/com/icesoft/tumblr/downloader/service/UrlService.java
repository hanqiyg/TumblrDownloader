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
                	JsonReader reader = new JsonReader(new StringReader(opt));
                	reader.setLenient(true);
            		reader.beginObject();
            
                	while(reader.hasNext()){
            			String tagName = reader.nextName();
            			if(tagName.equals("hdUrl")){            				
            				vi.hdUrl = getStringToken(reader,vi.hdUrl);
            			}else if(tagName.equals("filmstrip"))
            			{
            		 		reader.beginObject();
            				while(reader.hasNext())
            				{
            					if(reader.nextName().equals("url"))
            					{	
            						vi.hdPosterUrl = getStringToken(reader,vi.hdPosterUrl);              					
            					}else
            					{
            						reader.skipValue();
            					}
            				}
            				reader.endObject();
            			}else{
            				reader.skipValue();
            			}
                	}
                	reader.endObject();
                	reader.close();
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
	public static String getStringToken(JsonReader reader,String s) throws IOException{
			JsonToken nextToken = reader.peek();
			if(JsonToken.STRING.equals(nextToken)){
				s = reader.nextString();
			}else{
				reader.skipValue();
			}
		return s;
	}
}
