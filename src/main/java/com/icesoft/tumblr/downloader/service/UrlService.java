package com.icesoft.tumblr.downloader.service;

import java.io.IOException;
import java.io.StringReader;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.NodeClassFilter;
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
        parser.setNodeFactory(p);
		NodeFilter videoFilter = new NodeClassFilter(VideoTag.class);
		NodeList nodes = parser.extractAllNodesThatMatch(videoFilter);
        Node eachNode = null;
		if(nodes != null){
			for (int i = 0; i < nodes.size(); i++) 
            {
                eachNode = (Node)nodes.elementAt(i);
                if(eachNode!= null && eachNode instanceof VideoTag){
                	VideoTag t = (VideoTag) eachNode;            		
                	if(t.getPoster()!= null)
                	{
                    	vi.posterUrl = t.getPoster();
                	}

                	String opt = t.getOption();
                	//System.out.println(opt);
                	
/*                	JsonReader jsonReader = new JsonReader(new StringReader(opt));
                    while(jsonReader.hasNext()){
                        JsonToken nextToken = jsonReader.peek();
                        System.out.println(nextToken);
                        if(JsonToken.BEGIN_OBJECT.equals(nextToken)){
                            jsonReader.beginObject();
                        } else if(JsonToken.NAME.equals(nextToken)){
                            String name  =  jsonReader.nextName();
                            System.out.println(name);
                        } else if(JsonToken.STRING.equals(nextToken)){
                            String value =  jsonReader.nextString();
                            System.out.println(value);
                        } else if(JsonToken.NUMBER.equals(nextToken)){
                            long value =  jsonReader.nextLong();
                            System.out.println(value);
                        }else if(JsonToken.BOOLEAN.equals(nextToken)){
                        	 boolean value =  jsonReader.nextBoolean();
                             System.out.println(value);
                        }else if(JsonToken.END_OBJECT.equals(nextToken)){                        	
                        	jsonReader.endObject();
                        }else if(JsonToken.BEGIN_ARRAY.equals(nextToken)){                        	
                        	jsonReader.beginArray();
                        }else if(JsonToken.END_ARRAY.equals(nextToken)){                        	
                        	jsonReader.endArray();
                        }else{
                        	System.out.println(nextToken);
                        	jsonReader.skipValue();
                        }
                    }
                    jsonReader.close();*/
                	
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
            }
		}

		Parser parser2 = new Parser(embed);	
        PrototypicalNodeFactory p2 = new PrototypicalNodeFactory();  
        p2.registerTag(new SourceTag());   
        parser2.setNodeFactory(p2);
		NodeFilter sourceFilter = new NodeClassFilter(SourceTag.class);  
    	NodeList vss = parser2.extractAllNodesThatMatch(sourceFilter);
    	
    	if(vss != null && vss.size() > 0)
    	{
        	for(int j=0;j<vss.size();j++)
        	{
        		Node n = vss.elementAt(j);
        		if(n instanceof SourceTag)
        		{
        			SourceTag st = (SourceTag) n;
        			if(st.getSrc() != null){
            			vi.baseUrl = st.getSrc();
        			}
        			if(st.getType() != null){
        				vi.mineType = st.getType();
        			}
        		}else
        		{
        			//System.out.println("not instanceof SourceTag");
        		}
        	}
    	}else
    	{
    		//System.out.println("vss null");
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
