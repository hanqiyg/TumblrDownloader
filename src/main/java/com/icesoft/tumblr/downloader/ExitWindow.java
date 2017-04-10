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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ExitWindow extends JDialog {
	private static final long serialVersionUID = 4031211110971171392L;
	private static Logger logger = Logger.getLogger(ExitWindow.class);  
	private final JPanel contentPanel = new JPanel();
	private JLabel lblContext;
	private JProgressBar progressBar;

	/**
	 * Create the dialog.
	 */
	public ExitWindow() {
		setBounds(100, 100, 450, 300);
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
		gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, 1.0};
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
			lblContext = new JLabel("context");
			GridBagConstraints gbc_lblContext = new GridBagConstraints();
			gbc_lblContext.fill = GridBagConstraints.VERTICAL;
			gbc_lblContext.insets = new Insets(0, 0, 5, 0);
			gbc_lblContext.gridx = 0;
			gbc_lblContext.gridy = 1;
			contentPanel.add(lblContext, gbc_lblContext);
		}
		{
			progressBar = new JProgressBar();
			GridBagConstraints gbc_progressBar = new GridBagConstraints();
			gbc_progressBar.insets = new Insets(0, 0, 5, 0);
			gbc_progressBar.gridx = 0;
			gbc_progressBar.gridy = 2;
			contentPanel.add(progressBar, gbc_progressBar);
		}
		{
			JPanel buttonPane = new JPanel();
			GridBagConstraints gbc_buttonPane = new GridBagConstraints();
			gbc_buttonPane.anchor = GridBagConstraints.NORTH;
			gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonPane.gridx = 0;
			gbc_buttonPane.gridy = 1;
			getContentPane().add(buttonPane, gbc_buttonPane);
			buttonPane.setLayout(new GridLayout(0, 2, 0, 0));
			{
				JButton btnExit = new JButton("Exit");
				btnExit.setActionCommand("OK");
				buttonPane.add(btnExit);
				getRootPane().setDefaultButton(btnExit);
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
		Thread exitThread = new Thread(){
			@Override
			public void run(){ 
				progressBar.setMaximum(6);
				progressBar.setMinimum(0);
				
				setMessage("Shutdown:" + "UIMonitor" + new Date().toString());
				UIMonitor.getInstance().turnOff();
				progressBar.setValue(1);
				setMessage("Shutdown:" + "QueryManager" + new Date().toString());
				QueryManager.getInstance().stopQuery();	
				progressBar.setValue(2);
				setMessage("Shutdown:" + "DownloadManager" + new Date().toString());
				DownloadManager.getInstance().stopAll();
				progressBar.setValue(3);
				setMessage("Shutdown:" + "HttpClientConnectionManager" + new Date().toString());
				try {
					HttpClientConnectionManager.getInstance().shutdown();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				progressBar.setValue(4);
				setMessage("Shutdown:" + "H2DBService" + new Date().toString());
				try {
					H2DBService.getInstance().close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				progressBar.setValue(5);
				setMessage("saveSettings:" + "setWindowSettings" + new Date().toString());
	/*			Rectangle bounds = window.frame.getBounds();
				Settings.getInstance().setWindowSettings(bounds.x, bounds.y, bounds.width, bounds.height);*/
				logger.info("exit.");
				progressBar.setValue(6);
				System.exit(0);
			}
		};
		exitThread.start();
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	public void setMessage(String msg){
		logger.debug(msg);
		lblContext.setText(msg);
	}

}
