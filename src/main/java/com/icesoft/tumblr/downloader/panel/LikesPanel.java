package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.htmlparser.util.ParserException;

import com.icesoft.tumblr.downloader.DownloadManager;
import com.icesoft.tumblr.downloader.Settings;
import com.icesoft.tumblr.downloader.datamodel.ControllCellEditor;
import com.icesoft.tumblr.downloader.datamodel.LikesPostModel;
import com.icesoft.tumblr.downloader.datamodel.LikesPostModel.PostStatus;
import com.icesoft.tumblr.downloader.datamodel.LikesPostModel.STATUS;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.icesoft.tumblr.downloader.service.UrlService;
import com.icesoft.tumblr.model.VideoInfo;
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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.event.ActionEvent;

public class LikesPanel extends JPanel {
	private static final long serialVersionUID = 4111940040655069650L;
	private JTable table;
	private JProgressBar progressBar;
	
	private Thread likesThread;
	private int likes;
	
	private int index;
	
	private boolean load = false;
	private JLabel lblProgess;
	private JPanel panel;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnPause;
	private JButton btnResume;
	private JButton btnAddAll;
	
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
				likes = TumblrServices.getInstance().getLikesCount();
				index = 0;
				load = true;
				initThread();
				LikesPostModel model = (LikesPostModel) table.getModel();
				model.clear();
				model.fireTableDataChanged();
				likesThread.start();
			}
		});
		panel.add(btnStart);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("btnPause");
				load = false;
			}
		});
		panel.add(btnPause);
		
		btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("btnResume");
				load = true;
				initThread();
				likesThread.start();
			}
		});
		panel.add(btnResume);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Stop");
				load = false;
				likesThread = null;
			}
		});
		panel.add(btnStop);
		btnAddAll = new JButton("DownloadAll");
		btnAddAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LikesPostModel model = (LikesPostModel) table.getModel();
				for(PostStatus post : model.getAll()){
					if(post.getPost() instanceof VideoPost){
						System.out.println("addVideoTask");
						VideoPost v = (VideoPost) post.getPost();
						String blogname = post.getPost().getBlogName();
						List<Video> videos = v.getVideos();
						for(Video vi:videos){
							String embed = vi.getEmbedCode();
							System.out.println(embed);
							VideoInfo info;
							try {
								info = UrlService.getVideoInfoFromEmbed(embed);
								DownloadManager.getInstance().addVideoTask(info.baseUrl, Settings.save_location + File.separator + blogname);
							} catch (ParserException | IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						post.setStatus(STATUS.DOWNLOADING);
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
		table.setModel(new LikesPostModel());
		
		ControllCellEditor editor = new ControllCellEditor();
		table.getColumn("OP").setCellEditor(editor);
		table.getColumn("OP").setCellRenderer(editor);

		
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setViewportView(table);

		add(scrollPane, gbc_scrollPane);
		

	}
	public void initThread(){		
		if(likes > 0){
			progressBar.setMaximum(likes);
			progressBar.setMinimum(0);
			progressBar.setStringPainted(true);
			
			likesThread = new Thread(){
				@Override
				public void run() {
					while(load){
						if(index >= 0 && index<likes){
							long begin = System.currentTimeMillis();
							Post p = TumblrServices.getInstance().getLikeById(index);
							if(p != null){
								LikesPostModel model = (LikesPostModel) table.getModel();
								model.addPost(p);
								model.fireTableDataChanged();
							}	
							LikesPanel.this.progressBar.setValue(index);
							long end = System.currentTimeMillis();
							long time = end - begin;
							long seconds = time * (likes - index) /1000;
							lblProgess.setText(index + "/" + likes + "@" + seconds + "seconds left.");
							index++;
						}else{
							break;
						}
					}				
				}			
			};
		}
	}
}
