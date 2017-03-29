package com.icesoft.tumblr.downloader.datamodel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.icesoft.tumblr.downloader.service.UrlService;
import com.icesoft.tumblr.model.ImageInfo;
import com.icesoft.tumblr.model.VideoInfo;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

public class ControllCellEditor extends AbstractCellEditor implements TableCellRenderer,TableCellEditor {
	private static final long serialVersionUID = -961439013223710139L;
	private static Logger logger = Logger.getLogger(ControllCellEditor.class);  
	JButton btn_download;
	
	
	public ControllCellEditor(){
		btn_download = new JButton("Download");
	}
	
	@Override
	public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected, boolean hasFocus, 
			final int row, int column) {		
		return btn_download;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int row, final int column) {
		ControllCellEditor.this.fireEditingCanceled();
		if(value != null && value instanceof Post){
			Post post = (Post) value;
			return getDownloadButton(post);
		}
		return null;
	}


	private Component getDownloadButton(Post post) {
		JButton button = new JButton("Download");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(post instanceof VideoPost){
					VideoPost v = (VideoPost) post;
					List<Video> videos = v.getVideos();
					for(Video vi:videos){
						String embed = vi.getEmbedCode();
						VideoInfo info;
						try {
							info = UrlService.getVideoInfoFromEmbed(embed);
							info.blogName = TumblrServices.getInstance().getBlogName(v);
							info.id = TumblrServices.getInstance().getBlogId(v);
							DownloadManager.getInstance().addVideoTask(info);
						} catch (ParserException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				if(post instanceof PhotoPost){
					PhotoPost photoPost = (PhotoPost) post;
					downloadImage(photoPost);
				}
			}		
		});
		return button;
	}
	public void printPost(PhotoPost photoPost) {
		StringBuffer sb = new StringBuffer();
		sb.append("getAuthorId:" 	+ photoPost.getAuthorId());sb.append("\n\r");
		sb.append("getBlogName:" 	+ photoPost.getBlogName());sb.append("\n\r");
		sb.append("getCaption:"  	+ photoPost.getCaption());sb.append("\n\r");
		sb.append("getFormat:" 		+ photoPost.getFormat());sb.append("\n\r");
		sb.append("getPostUrl:" 	+ photoPost.getPostUrl());sb.append("\n\r");
		sb.append("getRebloggedFromName:" 	+ photoPost.getRebloggedFromName());sb.append("\n\r");
		sb.append("getReblogKey:" 	+ photoPost.getReblogKey());sb.append("\n\r");
		sb.append("getShortUrl:" 	+ photoPost.getShortUrl());sb.append("\n\r");
		sb.append("getSourceTitle:" + photoPost.getSourceTitle());sb.append("\n\r");
		sb.append("getSourceUrl:" 	+ photoPost.getSourceUrl());sb.append("\n\r");
		sb.append("getState:" 		+ photoPost.getState());sb.append("\n\r");
		sb.append("getType:" 		+ photoPost.getType());sb.append("\n\r");
		sb.append("getId:" 			+ photoPost.getId());sb.append("\n\r");		
		sb.append("getRebloggedFromId:" 	+ photoPost.getRebloggedFromId());sb.append("\n\r");	
		System.out.println(sb.toString());
	}	
	@Override
	public Object getCellEditorValue() {
		return null;
	}
	public void downloadImage(PhotoPost p){
		List<Photo> photos = p.getPhotos();
		for(Photo photo : photos){
			String url = photo.getOriginalSize().getUrl();						
			DownloadManager.getInstance().addImageTask(new ImageInfo(url,TumblrServices.getInstance().getBlogId(p),TumblrServices.getInstance().getBlogName(p)));			
		}
	}
}
