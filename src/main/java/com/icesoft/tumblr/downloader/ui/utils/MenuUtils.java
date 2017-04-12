package com.icesoft.tumblr.downloader.ui.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.tablemodel.DownloadModel;
import com.icesoft.tumblr.state.interfaces.IContext;

public class MenuUtils {
	public static JPopupMenu getDownloadPanelRightButtonMenu(Map<Integer, IContext> contexts,DownloadModel model) {
		JPopupMenu right = new JPopupMenu("Options");
		
		JMenuItem download = new JMenuItem("Download");
		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Iterator<Entry<Integer, IContext>> iter = contexts.entrySet().iterator();
				while(iter.hasNext()){
					Entry<Integer, IContext> entry = iter.next();
					int key = entry.getKey();
					IContext context = entry.getValue();
					if(context != null)
					{
						DownloadManager.getInstance().downloadResumeSingleTask(context);
						model.fireTableRowsUpdated(key, key);
					}
				}
			} 
		});
		right.add(download);

		JMenuItem stop = new JMenuItem("Stop");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Iterator<Entry<Integer, IContext>> iter = contexts.entrySet().iterator();
				while(iter.hasNext()){
					Entry<Integer, IContext> entry = iter.next();
					int key = entry.getKey();
					IContext context = entry.getValue();
					if(context != null)
					{
						DownloadManager.getInstance().stop(context);
						model.fireTableRowsUpdated(key, key);
					}
				}
			} 
		});
		right.add(stop);
		return right;
	}
}
