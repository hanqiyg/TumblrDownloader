package com.icesoft.tumblr.downloader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.apache.http.pool.PoolStats;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.managers.QueryManager;
import com.icesoft.tumblr.downloader.monitor.UIMonitor;
import com.icesoft.tumblr.downloader.panel.interfaces.IUpdatable;
import com.icesoft.tumblr.downloader.service.H2DBService;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ExitWindow extends JDialog  implements IUpdatable{
	private static final long serialVersionUID = 4031211110971171392L;
	private static Logger logger = Logger.getLogger(ExitWindow.class);  
	private Thread exitThread;
	private JProgressBar pbThreads;
	private JProgressBar pbHttpClients;
	/**
	 * Create the dialog.
	 */
	public ExitWindow(int x,int y,int w,int h) {
		setBounds(x, y, w, h);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		{
			JPanel plMessage = new JPanel();
			GridBagConstraints gbc_plMessage = new GridBagConstraints();
			gbc_plMessage.insets = new Insets(5, 20, 5, 20);
			gbc_plMessage.fill = GridBagConstraints.HORIZONTAL;
			gbc_plMessage.gridx = 0;
			gbc_plMessage.gridy = 0;
			getContentPane().add(plMessage, gbc_plMessage);
			GridBagLayout gbl_plMessage = new GridBagLayout();
			gbl_plMessage.columnWidths = new int[]{366, 0};
			gbl_plMessage.rowHeights = new int[]{15, 0, 0, 0, 0};
			gbl_plMessage.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_plMessage.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
			plMessage.setLayout(gbl_plMessage);
			{
				JLabel lbTitle = new JLabel("Exit");
				GridBagConstraints gbc_lbTitle = new GridBagConstraints();
				gbc_lbTitle.insets = new Insets(0, 0, 5, 0);
				gbc_lbTitle.gridx = 0;
				gbc_lbTitle.gridy = 0;
				plMessage.add(lbTitle, gbc_lbTitle);
			}
			{
				JLabel lbNormalExit = new JLabel("1.Normal Exit : It will take a few seconds to shutdown all componets.");
				GridBagConstraints gbc_lbNormalExit = new GridBagConstraints();
				gbc_lbNormalExit.fill = GridBagConstraints.HORIZONTAL;
				gbc_lbNormalExit.insets = new Insets(0, 0, 5, 0);
				gbc_lbNormalExit.gridx = 0;
				gbc_lbNormalExit.gridy = 1;
				plMessage.add(lbNormalExit, gbc_lbNormalExit);
			}
			{
				JLabel lbForceExit = new JLabel("2.Force Exit : Shutdown immediately, it may cause future problems.");
				GridBagConstraints gbc_lbForceExit = new GridBagConstraints();
				gbc_lbForceExit.fill = GridBagConstraints.HORIZONTAL;
				gbc_lbForceExit.insets = new Insets(0, 0, 5, 0);
				gbc_lbForceExit.gridx = 0;
				gbc_lbForceExit.gridy = 2;
				plMessage.add(lbForceExit, gbc_lbForceExit);
			}
			{
				JLabel lbCancel = new JLabel("3.Cancel Exit : Go back, and exit later.");
				GridBagConstraints gbc_lbCancel = new GridBagConstraints();
				gbc_lbCancel.fill = GridBagConstraints.HORIZONTAL;
				gbc_lbCancel.gridx = 0;
				gbc_lbCancel.gridy = 3;
				plMessage.add(lbCancel, gbc_lbCancel);
			}
		}
		{
			JPanel plProgress = new JPanel();
			GridBagConstraints gbc_plProgress = new GridBagConstraints();
			gbc_plProgress.insets = new Insets(5, 20, 5, 20);
			gbc_plProgress.fill = GridBagConstraints.BOTH;
			gbc_plProgress.gridx = 0;
			gbc_plProgress.gridy = 1;
			getContentPane().add(plProgress, gbc_plProgress);
				GridBagLayout gbl_plProgress = new GridBagLayout();
				gbl_plProgress.rowHeights = new int[] {0};
				gbl_plProgress.columnWidths = new int[] {0};
				gbl_plProgress.columnWeights = new double[]{0.0, 1.0};
				gbl_plProgress.rowWeights = new double[]{0.0, 0.0};
				plProgress.setLayout(gbl_plProgress);
				
				JLabel lblHttpclients = new JLabel("HttpClient:");
				GridBagConstraints gbc_lblHttpclients = new GridBagConstraints();
				gbc_lblHttpclients.anchor = GridBagConstraints.EAST;
				gbc_lblHttpclients.gridy = 0;
				gbc_lblHttpclients.gridx = 0;
				plProgress.add(lblHttpclients, gbc_lblHttpclients);
				
				pbHttpClients = new JProgressBar();
				GridBagConstraints gbc_pbHttpClients = new GridBagConstraints();
				gbc_pbHttpClients.gridy = 0;
				gbc_pbHttpClients.fill = GridBagConstraints.HORIZONTAL;
				gbc_pbHttpClients.gridx = 1;
				plProgress.add(pbHttpClients, gbc_pbHttpClients);
				
				JLabel lblThreads = new JLabel("Threads:");
				GridBagConstraints gbc_lblThreads = new GridBagConstraints();
				gbc_lblThreads.anchor = GridBagConstraints.EAST;
				gbc_lblThreads.ipady = 1;
				gbc_lblThreads.gridy = 1;
				gbc_lblThreads.gridx = 0;
				plProgress.add(lblThreads, gbc_lblThreads);
				
				pbThreads = new JProgressBar();
				GridBagConstraints gbc_pbThreads = new GridBagConstraints();
				gbc_pbThreads.gridy = 1;
				gbc_pbThreads.gridx = 1;
				gbc_pbThreads.fill = GridBagConstraints.HORIZONTAL;
				plProgress.add(pbThreads, gbc_pbThreads);
		}
		{
			JPanel plButton = new JPanel();
			GridBagConstraints gbc_plButton = new GridBagConstraints();
			gbc_plButton.anchor = GridBagConstraints.SOUTH;
			gbc_plButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_plButton.gridx = 0;
			gbc_plButton.gridy = 2;
			getContentPane().add(plButton, gbc_plButton);
			{
				JButton btnNormalExit = new JButton("1.Normal Exit");
				btnNormalExit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						exitThread = new Thread(){
							@Override
							public void run(){
								long begin = System.currentTimeMillis();
								logger.debug("Shutdown:" + "QueryManager@[" + new Date().toString()+"]");
								QueryManager.getInstance().stopQuery();	
								logger.debug("Shutdown:" + "DownloadManager@[" + new Date().toString()+"]");
								DownloadManager.getInstance().terminate();
								logger.debug("Shutdown:" + "HttpClientConnectionManager@[" + new Date().toString()+"]");
								HttpClientConnectionManager.getInstance().shutdown();	
								logger.debug("Shutdown:" + "H2DBService@[" + new Date().toString()+"]");
								H2DBService.getInstance().close();
								logger.debug("Shutdown:" + "UIMonitor@[" + new Date().toString()+"]");
								UIMonitor.getInstance().turnOff();
								logger.debug("exit.@[" + new Date().toString()+"]");
								long end = System.currentTimeMillis();
								logger.debug("exit.@[" + (end - begin) +"ms]");
								System.exit(0);
							}
						};
						exitThread.start();
						btnNormalExit.setEnabled(false);
					}
				});
				plButton.add(btnNormalExit);
			}
			{
				JButton btnForceExit = new JButton("2.Force Exit");
				btnForceExit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				plButton.add(btnForceExit);
			}
			{
				JButton btnCancel = new JButton("3.Cancel");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ExitWindow.this.dispose();
					}
				});
				plButton.add(btnCancel);
			}
		}
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	}
	public void loadStats(){
		PoolStats s = HttpClientConnectionManager.getInstance().getStats();		
		if(s != null){
			pbHttpClients.setMaximum(s.getMax());
			pbHttpClients.setMinimum(0);
			pbHttpClients.setValue(s.getLeased());
			pbHttpClients.setString(s.getLeased() + " / " + s.getMax());
			pbHttpClients.setStringPainted(true);
		}else{
			pbHttpClients.setMaximum(1);
			pbHttpClients.setMinimum(0);
			pbHttpClients.setValue(1);
			pbHttpClients.setString("");
			pbHttpClients.setStringPainted(true);
		}
	}
	public  void loadThreadsInfo(){
		ThreadPoolExecutor pool = DownloadManager.getInstance().getPool();		
		pbThreads.setMaximum(pool.getPoolSize());
		pbThreads.setMinimum(0);
		pbThreads.setValue(pool.getActiveCount());
		pbThreads.setString(pool.getActiveCount() + " / " + pool.getPoolSize());
		pbThreads.setStringPainted(true);
	}
	@Override
	public void update() {
		loadStats();
		loadThreadsInfo();
	}
}
