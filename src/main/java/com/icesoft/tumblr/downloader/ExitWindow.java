package com.icesoft.tumblr.downloader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.managers.QueryManager;
import com.icesoft.tumblr.downloader.monitor.UIMonitor;
import com.icesoft.tumblr.downloader.service.H2DBService;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Date;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;

public class ExitWindow extends JDialog {
	private static final long serialVersionUID = 4031211110971171392L;
	private static Logger logger = Logger.getLogger(ExitWindow.class);  
	private final JPanel contentPanel = new JPanel();
	private JLabel lblContext;
	private Thread exitThread;
	
	private static final String WARNING = "Waiting for Task close normally.\n\r Force close will cause problem.";
	/**
	 * Create the dialog.
	 */
	public ExitWindow(int x,int y,int w,int h) {
		setBounds(x, y, w, h);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_contentPanel = new GridBagConstraints();
		gbc_contentPanel.fill = GridBagConstraints.BOTH;
		gbc_contentPanel.insets = new Insets(5, 5, 5, 5);
		gbc_contentPanel.gridx = 0;
		gbc_contentPanel.gridy = 0;
		getContentPane().add(contentPanel, gbc_contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWeights = new double[]{1.0};
		gbl_contentPanel.rowWeights = new double[]{1.0, 1.0};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblExit = new JLabel("Exit");
			GridBagConstraints gbc_lblExit = new GridBagConstraints();
			gbc_lblExit.insets = new Insets(0, 0, 5, 0);
			gbc_lblExit.fill = GridBagConstraints.VERTICAL;
			gbc_lblExit.gridx = 0;
			gbc_lblExit.gridy = 0;
			contentPanel.add(lblExit, gbc_lblExit);
		}
		{
			lblContext = new JLabel(WARNING);
			GridBagConstraints gbc_lblContext = new GridBagConstraints();
			gbc_lblContext.fill = GridBagConstraints.VERTICAL;
			gbc_lblContext.insets = new Insets(0, 0, 5, 0);
			gbc_lblContext.gridx = 0;
			gbc_lblContext.gridy = 1;
			contentPanel.add(lblContext, gbc_lblContext);
		}
		{
			JPanel buttonPane = new JPanel();
			GridBagConstraints gbc_buttonPane = new GridBagConstraints();
			gbc_buttonPane.anchor = GridBagConstraints.NORTH;
			gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonPane.gridx = 0;
			gbc_buttonPane.gridy = 1;
			getContentPane().add(buttonPane, gbc_buttonPane);
			{
				JButton btnExit = new JButton("Force Close");
				btnExit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				{
					JButton btnNormalCloseAnd = new JButton("Normal Close And Wait");
					btnNormalCloseAnd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(exitThread != null && !exitThread.isAlive())
							{
								exitThread.start();
							}
							btnNormalCloseAnd.setEnabled(false);
						}
					});
					buttonPane.add(btnNormalCloseAnd);
				}
				btnExit.setActionCommand("OK");
				buttonPane.add(btnExit);
				getRootPane().setDefaultButton(btnExit);
			}
		}
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
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
	}
}
