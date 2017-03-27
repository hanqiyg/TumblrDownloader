package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.icesoft.tumblr.downloader.MainWindow;

public class ResourceStatusPanel extends JPanel implements IRefreshable{
	private static final long serialVersionUID = 1186855036022070205L;
	private JProgressBar pbMemory;
	public ResourceStatusPanel() {
		setLayout(new GridLayout(1, 1, 5, 5));
		
		JPanel plMemory = new JPanel();
		add(plMemory);
		plMemory.setLayout(new GridLayout(2, 1, 5, 5));
		
		JLabel lblMemoryUsage = new JLabel("Memory Usage");
		plMemory.add(lblMemoryUsage);
		
		pbMemory = new JProgressBar();
		plMemory.add(pbMemory);
	}
	
	public void updateMemory(){
		int totalMemory = (int) (Runtime.getRuntime().totalMemory()/(1024*1024));
		int freeMemory = (int) (Runtime.getRuntime().freeMemory()/(1024*1024));
		
		pbMemory.setMaximum(totalMemory);
		pbMemory.setMinimum(0);
		pbMemory.setValue(freeMemory);
		pbMemory.setString(freeMemory + " MB / " + totalMemory + "MB");
		pbMemory.setStringPainted(true);
	}

	@Override
	public void refresh() {
		updateMemory();	
	}
}
