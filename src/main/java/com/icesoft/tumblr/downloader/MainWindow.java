package com.icesoft.tumblr.downloader;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.Proxy;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.PropertyConfigurator;

import com.icesoft.tumblr.downloader.dialog.ExitDialog;
import com.icesoft.tumblr.downloader.dialog.ProxyDialog;
import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.monitor.UIMonitor;
import com.icesoft.tumblr.downloader.panel.DownloadPanel;
import com.icesoft.tumblr.downloader.panel.LikesPanel;
import com.icesoft.tumblr.downloader.panel.SettingsPanel;
import com.icesoft.tumblr.downloader.service.H2DBService;
import com.icesoft.tumblr.downloader.service.SettingService;


public class MainWindow {
	static {
		Properties pro = new Properties();
		pro.put("log4j.rootLogger", "debug,stdout,R,A");

		pro.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
		pro.put("log4j.appender.stdout.Target","System.out");
		pro.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.stdout.layout.ConversionPattern", "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n");

		pro.put("log4j.appender.R", "org.apache.log4j.RollingFileAppender");
		pro.put("log4j.appender.R.File", SettingService.getInstance().getPath() + File.separator + "logs" + File.separator + "Info.log");
		pro.put("log4j.appender.R.MaxFileSize", "10000KB");
		pro.put("log4j.appender.R.MaxBackupIndex", "20");
		pro.put("log4j.appender.R.Threshold", "INFO");
		pro.put("log4j.appender.R.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.R.layout.ConversionPattern", "%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");

		pro.put("log4j.appender.A", "org.apache.log4j.RollingFileAppender");
		pro.put("log4j.appender.A.File", SettingService.getInstance().getPath() + File.separator + "logs" + File.separator + "Error.log");
		pro.put("log4j.appender.A.MaxFileSize", "10000KB");
		pro.put("log4j.appender.A.MaxBackupIndex", "20");
		pro.put("log4j.appender.A.Threshold", "ERROR");
		pro.put("log4j.appender.A.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.A.layout.ConversionPattern", "%n[%d{HH:mm:ss}] [%p] %m");

		PropertyConfigurator.configure(pro);
	}
	//private static Logger logger = Logger.getLogger(MainWindow.class);  
	private JFrame frame;	
	
	private LikesPanel likesPanel;
	private SettingsPanel settingsPanel;
	private DownloadPanel downloadPanel;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					window.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					window.frame.addWindowListener(new WindowAdapter()
					{
						public void windowClosing(WindowEvent e){
							Rectangle bounds = window.frame.getBounds();
							SettingService.getInstance().saveWindowSettings(bounds.x, bounds.y, bounds.width, bounds.height);
							ExitDialog pd = new ExitDialog();
							UIMonitor.getInstance().addUpdatable(pd);
							int width = (window.frame.getWidth() / 2) < pd.getMinimumSize().width
									?pd.getMinimumSize().width
									:window.frame.getWidth() / 2;
							int height = (window.frame.getHeight() / 2) < pd.getMinimumSize().height
									?pd.getMinimumSize().height
									:window.frame.getHeight() / 2 ;
							int x = window.frame.getX() + width / 2;
							int y = window.frame.getY() + height / 2;
							pd.setVisible(false);
							pd.setBounds(x, y, width, height);
							pd.setModal(true);
							pd.setVisible(true);
							
						}
					}); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainWindow() 
	{
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		}
		initialize();
		String error = H2DBService.getInstance().init();
		if(error != null){
			Object[] options ={ "Exit"};  
			JOptionPane.showOptionDialog(null, 
					error, "Error",JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]); 
			System.exit(0);
		}
		applyPropertie();
		applyUIMonitor();
		applyProxy();
		DownloadManager.getInstance().loadTasks();
	}
	private void initialize() {
		//services = new TumblrServices();
		frame = new JFrame();		
		frame.setBounds(
					SettingService.getInstance().getWindowX(),
					SettingService.getInstance().getWindowY(),
					SettingService.getInstance().getWindowW(),
					SettingService.getInstance().getWindowH()
				);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		likesPanel = new LikesPanel();
		settingsPanel = new SettingsPanel();
		downloadPanel = new DownloadPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.add("Likes", likesPanel);
		tabbedPane.add("Download", downloadPanel);
		tabbedPane.add("Settings", settingsPanel);
		
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		frame.getContentPane().add(tabbedPane);
		
		//Properties systemProperties = System.getProperties();
		//systemProperties.setProperty("socksProxyHost",Settings.getInstance().getProxySettings().getHost());
		//systemProperties.setProperty("socksProxyPort",String.valueOf(Settings.getInstance().getProxySettings().getPort()));
		
		 //Enable header wire + context logging - Best for Debugging
/*	    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
	    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
	    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
	    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");*/
		
		//Enable full wire(header and content) + context logging
/*	    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
	    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
	    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
	    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");*/
		
		//httpclient Enable just context logging		
	}
	public void applyPropertie()
	{
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
	}
	public void applyUIMonitor()
	{		
		UIMonitor.getInstance().addUpdatable(likesPanel);
		UIMonitor.getInstance().addUpdatable(downloadPanel);
		UIMonitor.getInstance().turnOn();
	}
	public void applyProxy()
	{
		Proxy proxy = SettingService.getInstance().loadProxy();
		SettingService.getInstance().applyProxy(proxy);
	}
}
