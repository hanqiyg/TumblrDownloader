package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import javax.swing.JTable;

import com.icesoft.tumblr.downloader.MainWindow;
import com.icesoft.tumblr.downloader.Settings;
import com.icesoft.tumblr.downloader.datamodel.DownloadModel;
import com.icesoft.tumblr.downloader.datamodel.ProgressCellRenderer;
import com.icesoft.tumblr.downloader.service.TumblrServices;

import javax.swing.JScrollPane;
import java.awt.GridLayout;

public class DownloadPanel extends JPanel {
	private static final long serialVersionUID = 4111940040655069650L;
	private JTable table;
	
	public DownloadPanel(Settings settings,TumblrServices services) {
		JScrollPane scrollPane = new JScrollPane();
		table = new JTable();
		final DownloadModel model = new DownloadModel();
		table.setModel(model);
		table.getColumn("Progress").setCellRenderer(new ProgressCellRenderer());
		setLayout(new GridLayout(1, 1, 0, 0));
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setViewportView(table);
		add(scrollPane);
		Thread t = new Thread(){
			@Override
			public void run() {
				while(MainWindow.refresh){
					try {
						if(model!= null){
							model.fireTableDataChanged();
						}				
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}			
		};
		t.start();
	}	
}
