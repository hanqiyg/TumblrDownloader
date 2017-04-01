package com.icesoft.tumblr.downloader.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.htmlparser.util.ParserException;

import com.icesoft.tumblr.downloader.configure.Settings;
import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.managers.QueryManager;
import com.icesoft.tumblr.downloader.panel.interfaces.IUpdatable;
import com.icesoft.tumblr.downloader.service.PostService;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.icesoft.tumblr.downloader.service.UrlService;
import com.icesoft.tumblr.downloader.tablemodel.LikesPostModel;
import com.icesoft.tumblr.downloader.workers.AllLikedQueryWorker;
import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.model.VideoInfo;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

public class LikesPanel extends JPanel implements IUpdatable{
	//private static Logger logger = Logger.getLogger(LikesPanel.class);  
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
	
	public LikesPanel() {
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
				for (Iterator<Post> it = PostService.getInstance().getPosts().iterator(); it.hasNext();){
					Post post = it.next();
					if(post instanceof VideoPost){
						VideoPost v = (VideoPost) post;
						List<Video> videos = v.getVideos();
						for(Video vi:videos){
							String embed = vi.getEmbedCode();
							try {								
								VideoInfo info = UrlService.getVideoInfoFromEmbed(embed);
								String url = info.getURL();
								String poster = info.getPosterURL();
								String saveLocation = Settings.getInstance().getSaveLocation();
								String id = TumblrServices.getInstance().getBlogId(v);
								String name = TumblrServices.getInstance().getBlogName(v);
								DownloadManager.getInstance().addTask(new DownloadTask(url, saveLocation + File.separator + name + File.separator + id));
								DownloadManager.getInstance().addTask(new DownloadTask(poster, saveLocation + File.separator + name + File.separator + id));
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
							//DownloadManager.getInstance().addImageTask(new ImageInfo(url,TumblrServices.getInstance().getBlogId(photoPost),TumblrServices.getInstance().getBlogName(photoPost)));
							String saveLocation = Settings.getInstance().getSaveLocation();
							String id = TumblrServices.getInstance().getBlogId(photoPost);
							String name = TumblrServices.getInstance().getBlogName(photoPost);
							DownloadManager.getInstance().addTask(new DownloadTask(url, saveLocation + File.separator + name + File.separator + id));
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
		
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setViewportView(table);

		add(scrollPane, gbc_scrollPane);
		

	}

	@Override
	public void update() {
		if(model!= null){
			model.fireTableDataChanged();
		}
		if(QueryManager.getInstance().getWorker() != null && QueryManager.getInstance().getWorker() instanceof AllLikedQueryWorker){
			AllLikedQueryWorker likedWorker = (AllLikedQueryWorker) QueryManager.getInstance().getWorker();
			progressBar.setMaximum(likedWorker.getLikedCount());
			progressBar.setMinimum(0);
			progressBar.setValue(likedWorker.getQueryCount());
			progressBar.setString(likedWorker.getQueryCount() + " / " + likedWorker.getLikedCount());
			progressBar.setStringPainted(true);
			progressBar.invalidate();
		}
	}	
}
