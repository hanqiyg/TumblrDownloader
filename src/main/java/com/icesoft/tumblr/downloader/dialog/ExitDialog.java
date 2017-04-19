package com.icesoft.tumblr.downloader.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.http.pool.PoolStats;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.managers.QueryManager;
import com.icesoft.tumblr.downloader.monitor.UIMonitor;
import com.icesoft.tumblr.downloader.panel.interfaces.IUpdatable;
import com.icesoft.tumblr.downloader.service.H2DBService;
import javax.swing.JProgressBar;


public class ExitDialog extends JDialog implements IUpdatable
{
	private static final long serialVersionUID = 7898637757439796777L;
	private static Logger logger = Logger.getLogger(ExitDialog.class);  
	private JButton btnNormal,btnForce,btnCancel;
	private JProgressBar pbHttpClients,pbThreads;
	private static final String TITLE = "Exit";
	public ExitDialog() {
		this.setTitle(TITLE);
		this.setMinimumSize(new Dimension(450,260));
		TitledBorder titled = BorderFactory.createTitledBorder
				(
					null
					,TITLE
					,TitledBorder.CENTER
					,TitledBorder.TOP
				);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel plExit = new JPanel();
		plExit.setBorder(titled);
		getContentPane().add(plExit);
		GridBagConstraints gbc_plPathSettings = new GridBagConstraints();
		gbc_plPathSettings.fill = GridBagConstraints.BOTH;
		gbc_plPathSettings.insets = new Insets(5, 5, 5, 5);
		gbc_plPathSettings.gridx = 0;
		gbc_plPathSettings.gridy = 0;
				GridBagLayout gbl_plExit = new GridBagLayout();
				gbl_plExit.columnWidths = new int[] {};
				gbl_plExit.columnWeights = new double[]{1.0};
				gbl_plExit.rowWeights = new double[]{1.0, 0.0, 1.0};
				plExit.setLayout(gbl_plExit);
		
				JPanel plContext = new JPanel();
				GridBagConstraints gbc_plContext = new GridBagConstraints();
				gbc_plContext.insets = new Insets(5, 5, 5, 0);
				gbc_plContext.fill = GridBagConstraints.HORIZONTAL;
				gbc_plContext.gridy = 0;
				gbc_plContext.gridx = 0;

				plExit.add(plContext, gbc_plContext);
				GridBagLayout gbl_plContext = new GridBagLayout();
				gbl_plContext.columnWeights = new double[]{1.0};
				gbl_plContext.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0};
				plContext.setLayout(gbl_plContext);
				
				JLabel label1 = new JLabel("1.Normal Exit : It will take a few seconds to shutdown all componets.");
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.anchor = GridBagConstraints.WEST;
				gbc_label.insets = new Insets(0, 0, 5, 0);
				gbc_label.gridx = 0;
				gbc_label.gridy = 1;
				plContext.add(label1, gbc_label);
				
				JLabel label2 = new JLabel("2.Force Exit : Shutdown immediately, it may cause future problems.");
				GridBagConstraints gbc_label_1 = new GridBagConstraints();
				gbc_label_1.anchor = GridBagConstraints.WEST;
				gbc_label_1.insets = new Insets(0, 0, 5, 0);
				gbc_label_1.gridx = 0;
				gbc_label_1.gridy = 2;
				plContext.add(label2, gbc_label_1);
				
				JLabel label3 = new JLabel("3.Cancel Exit : Go back, and exit later.");
				GridBagConstraints gbc_label_2 = new GridBagConstraints();
				gbc_label_2.anchor = GridBagConstraints.WEST;
				gbc_label_2.gridx = 0;
				gbc_label_2.gridy = 3;
				plContext.add(label3, gbc_label_2);
					
				JPanel plControl = new JPanel();
				GridBagConstraints gbc_plControl = new GridBagConstraints();
				gbc_plControl.anchor = GridBagConstraints.SOUTH;
				gbc_plControl.insets = new Insets(5, 5, 5, 5);
				gbc_plControl.fill = GridBagConstraints.HORIZONTAL;
				gbc_plControl.gridx = 0;
				gbc_plControl.gridy = 2;
				plExit.add(plControl, gbc_plControl);

				btnNormal = new JButton("1.Normal Exit");
				btnNormal.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						Thread exitThread = new Thread(){
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
						btnNormal.setEnabled(false);
					}
				});
				plControl.add(btnNormal);
				
				btnForce = new JButton("2.Force Exit");
				btnForce.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				plControl.add(btnForce);
				
				btnCancel = new JButton("3.Cancel");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ExitDialog.this.dispose();
					}
				});
				plControl.add(btnCancel);
				
				JPanel panel = new JPanel();
				GridBagConstraints gbc_panel = new GridBagConstraints();
				gbc_panel.insets = new Insets(5, 5, 5, 5);
				gbc_panel.fill = GridBagConstraints.HORIZONTAL;
				gbc_panel.gridx = 0;
				gbc_panel.gridy = 1;
				plExit.add(panel, gbc_panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWeights = new double[]{0.0, 1.0};
				gbl_panel.rowWeights = new double[]{0.0, 0.0};
				panel.setLayout(gbl_panel);
				
				JLabel lblHttpClients = new JLabel("HttpClient:");
				GridBagConstraints gbc_lblHttpClients = new GridBagConstraints();
				gbc_lblHttpClients.anchor = GridBagConstraints.EAST;
				gbc_lblHttpClients.insets = new Insets(5, 5, 5, 5);
				gbc_lblHttpClients.gridx = 0;
				gbc_lblHttpClients.gridy = 0;
				panel.add(lblHttpClients, gbc_lblHttpClients);
				
				JLabel lblThreads = new JLabel("Threads:");
				GridBagConstraints gbc_lblThreads = new GridBagConstraints();
				gbc_lblThreads.anchor = GridBagConstraints.EAST;
				gbc_lblThreads.insets = new Insets(5, 5, 5, 5);
				gbc_lblThreads.gridx = 0;
				gbc_lblThreads.gridy = 1;
				panel.add(lblThreads, gbc_lblThreads);
				
				pbHttpClients = new JProgressBar();
				GridBagConstraints gbc_pbHttpClients = new GridBagConstraints();
				gbc_pbHttpClients.fill = GridBagConstraints.HORIZONTAL;
				gbc_pbHttpClients.insets = new Insets(5, 5, 5, 5);
				gbc_pbHttpClients.gridx = 1;
				gbc_pbHttpClients.gridy = 0;
				panel.add(pbHttpClients, gbc_pbHttpClients);
				
				pbThreads = new JProgressBar();
				GridBagConstraints gbc_pbThreads = new GridBagConstraints();
				gbc_pbThreads.insets = new Insets(5, 5, 5, 5);
				gbc_pbThreads.fill = GridBagConstraints.HORIZONTAL;
				gbc_pbThreads.gridx = 1;
				gbc_pbThreads.gridy = 1;
				panel.add(pbThreads, gbc_pbThreads);
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
