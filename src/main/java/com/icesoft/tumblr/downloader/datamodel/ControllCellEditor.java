package com.icesoft.tumblr.downloader.datamodel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.htmlparser.util.ParserException;

import com.icesoft.tumblr.downloader.DownloadManager;
import com.icesoft.tumblr.downloader.Settings;
import com.icesoft.tumblr.downloader.datamodel.LikesPostModel.PostStatus;
import com.icesoft.tumblr.downloader.datamodel.LikesPostModel.STATUS;
import com.icesoft.tumblr.downloader.service.UrlService;
import com.icesoft.tumblr.model.VideoInfo;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

public class ControllCellEditor extends AbstractCellEditor implements TableCellRenderer,TableCellEditor {
	private static final long serialVersionUID = -961439013223710139L;
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
		System.out.println("getTableCellEditorComponent");
		ControllCellEditor.this.fireEditingCanceled();
		System.out.println((value instanceof PostStatus));
		if(value instanceof PostStatus){
			PostStatus ps = (PostStatus) value;
			if(ps != null){
				System.out.println("ps != null");
				switch(ps.getStatus()){
					case DEFAULT:		return getDownloadButton(ps);
					case DOWNLOADING:	return getCancelButton(ps);
					case EXCEPTION:		return getRedownloadButton(ps);
					case FINISH:		return getOpenFolderButton(ps);
					default:			return null;		
				}
			}
		}
		return null;
	}

	private Component getOpenFolderButton(PostStatus ps) {
		// TODO Auto-generated method stub
		return null;
	}

	private Component getRedownloadButton(PostStatus ps) {
		// TODO Auto-generated method stub
		return null;
	}

	private Component getCancelButton(PostStatus ps) {
		// TODO Auto-generated method stub
		return null;
	}

	private Component getDownloadButton(final PostStatus ps) {
		System.out.println("getDownloadButton");
		JButton button = new JButton("Download");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("actionPerformed:"+"getDownloadButton");
				if(ps.getPost() instanceof VideoPost){
					System.out.println("addVideoTask");
					VideoPost v = (VideoPost) ps.getPost();
					String blogname = ps.getPost().getBlogName();
					List<Video> videos = v.getVideos();
					for(Video vi:videos){
						String embed = vi.getEmbedCode();
						System.out.println(embed);
						VideoInfo info;
						try {
							info = UrlService.getVideoInfoFromEmbed(embed);
							DownloadManager.getInstance().addVideoTask(info);
						} catch (ParserException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
					ps.setStatus(STATUS.DOWNLOADING);
				}
				if(ps.getPost() instanceof PhotoPost){
					PhotoPost photoPost = (PhotoPost) ps.getPost();
					List<Photo> photos = photoPost.getPhotos();
					for(Photo photo : photos){
						String url = photo.getOriginalSize().getUrl();
					}
				}
			}			
		});
		return button;
	}

	@Override
	public Object getCellEditorValue() {
		System.out.println("getCellEditorValue");
		return null;
	}
}
