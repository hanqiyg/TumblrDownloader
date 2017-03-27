package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;

import com.icesoft.tumblr.downloader.DownloadManager;
import com.icesoft.tumblr.downloader.QueryManager;
import com.icesoft.tumblr.downloader.Settings;
import com.icesoft.tumblr.downloader.datamodel.ControllCellEditor;
import com.icesoft.tumblr.downloader.datamodel.LikesPostModel;
import com.icesoft.tumblr.downloader.service.PostService;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.icesoft.tumblr.downloader.service.UrlService;
import com.icesoft.tumblr.model.ImageInfo;
import com.icesoft.tumblr.model.VideoInfo;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.awt.event.ActionEvent;

public class LikesPanel extends JPanel implements IRefreshable{
	private static Logger logger = Logger.getLogger(LikesPanel.class);  
	private static final long serialVersionUID = 4111940040655069650L;
	private JTable table;
	private JProgressBar progressBar;

	private JLabel lblProgess;
	private JPanel panel;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnPause;
	private JButton btnResume;
	private JButton btnAddAll;
	
	private LikesPostModel model;
	
	public LikesPanel(Settings settings,TumblrServices services) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.insets = new Insets(5, 5, 5, 5);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QueryManager.getInstance().ExecLikedQurey();
			}
		});
		panel.add(btnStart);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QueryManager.getInstance().pauseQuery();
			}
		});
		panel.add(btnPause);
		
		btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QueryManager.getInstance().resumeQuery();
			}
		});
		panel.add(btnResume);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QueryManager.getInstance().stopQuery();
			}
		});
		panel.add(btnStop);
		btnAddAll = new JButton("DownloadAll");
		btnAddAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LikesPostModel model = (LikesPostModel) table.getModel();
				for(Post post : PostService.getInstance().getPosts()){
					if(post instanceof VideoPost){
						VideoPost v = (VideoPost) post;
						List<Video> videos = v.getVideos();
						for(Video vi:videos){
							String embed = vi.getEmbedCode();
							System.out.println(embed);
							VideoInfo info;
							try {
								info = UrlService.getVideoInfoFromEmbed(embed);
								info.blogName = TumblrServices.getInstance().getBlogName(v);
								info.id = TumblrServices.getInstance().getBlogId(v);
								DownloadManager.getInstance().addVideoTask(info);
							} catch (ParserException | IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					if(post instanceof PhotoPost){
						PhotoPost photoPost = (PhotoPost) post;
						for(Photo photo : photoPost.getPhotos()){
							String url = photo.getOriginalSize().getUrl();								
							DownloadManager.getInstance().addImageTask(new ImageInfo(url,TumblrServices.getInstance().getBlogId(photoPost),TumblrServices.getInstance().getBlogName(photoPost)));
						}
					}
				}
				
			}
		});
		panel.add(btnAddAll);
		
		lblProgess = new JLabel("");
		GridBagConstraints gbc_lblProgess = new GridBagConstraints();
		gbc_lblProgess.anchor = GridBagConstraints.WEST;
		gbc_lblProgess.insets = new Insets(0, 0, 5, 0);
		gbc_lblProgess.gridx = 0;
		gbc_lblProgess.gridy = 1;
		add(lblProgess, gbc_lblProgess);
		
		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setValue(0);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.insets = new Insets(5, 5, 5, 5);
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 2;
		add(progressBar, gbc_progressBar);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		
		table = new JTable();
		model = new LikesPostModel();
		table.setModel(model);
		
		ControllCellEditor editor = new ControllCellEditor();
		table.getColumn("OP").setCellEditor(editor);
		table.getColumn("OP").setCellRenderer(editor);

		
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setViewportView(table);

		add(scrollPane, gbc_scrollPane);
		

	}

	@Override
	public void refresh() {
		if(model!= null){
			model.fireTableDataChanged();
		}	
	}	
}
