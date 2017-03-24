package com.icesoft.tumblr.downloader;

import java.io.IOException;
import java.util.Properties;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.junit.Test;

import com.icesoft.htmlparser.tags.SourceTag;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.icesoft.tumblr.downloader.service.UrlService;
import com.icesoft.tumblr.model.VideoInfo;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import junit.framework.TestCase;

public class UrlServiceTest extends TestCase {
	public static final String embed = "<video  id='embed-58d4c985c555c901518477' class='crt-video crt-skin-default' width='250' height='250' poster='https://31.media.tumblr.com/tumblr_nsp5bzCk2U1upugcq_frame1.jpg' preload='none' muted data-crt-video data-crt-options='{\"autoheight\":null,\"duration\":60,\"hdUrl\":false,\"filmstrip\":{\"url\":\"https://31.media.tumblr.com/previews/tumblr_nsp5bzCk2U1upugcq_filmstrip.jpg\",\"width\":\"200\",\"height\":\"200\"}}' >"
			+ "<source src='https://caobile.tumblr.com/video_file/t:igK72bq7btAh-4IqriDnuQ/158740312842/tumblr_nsp5bzCk2U1upugcq' type='video/mp4'>"
			+ "</video>";
	
	@Test(expected = IOException.class) 
	public void testSaveEmbedToFile() throws IOException, ParserException{
		
		Properties systemProperties = System.getProperties();
		systemProperties.setProperty("socksProxyHost",Settings.proxy_socket_address	);
		systemProperties.setProperty("socksProxyPort",Settings.proxy_socket_port	);
		int error = 0;
		long likes = TumblrServices.getInstance().getLikesCount();
		for(int i=0;i<likes;i++){
			Post p = TumblrServices.getInstance().getLikeById(i);
			if(p instanceof VideoPost){
				VideoPost vp = (VideoPost) p;
				for(Video v : vp.getVideos()){
					VideoInfo vi = UrlService.getVideoInfoFromEmbed(v.getEmbedCode());
					if((vi.baseUrl == null || vi.baseUrl.equals("")) && (vi.hdUrl == null || vi.hdUrl.equals(""))){
						error++;
						System.out.println(vi.toString());
						System.out.println(v.getEmbedCode());
					}else{
						System.out.println(vi.toString());
					}
				}
			}
		}
		System.out.println("Errors:" + error);
	}

	public void testEmbedToLocation() throws ParserException, IOException{
		VideoInfo vi = UrlService.getVideoInfoFromEmbed(embed);
		System.out.println(embed);
		System.out.println(vi.toString());
	}
}
