package com.icesoft.tumblr.downloader.ui.utils;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.icesoft.tumblr.downloader.configure.Constants;
import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.tablemodel.DownloadModel;
import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;
import com.icesoft.utils.ClipboardUtils;

public class MenuUtils {
	public static JPopupMenu getDownloadPanelRightButtonMenu(List<IContext> contexts,DownloadModel model) {
		JPopupMenu right = new JPopupMenu("Options");
		
		JMenuItem download = new JMenuItem("Download");
		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Iterator<IContext> iter = contexts.iterator();
				while(iter.hasNext())
				{
					IContext context = iter.next();
					if(context != null)
					{
						DownloadManager.getInstance().startSingleTask(context);
					}
				}
				model.fireTableDataChanged();
			} 
		});
		right.add(download);

		JMenuItem stop = new JMenuItem("Pause");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Iterator<IContext> iter = contexts.iterator();
				while(iter.hasNext())
				{
					IContext context = iter.next();
					if(context != null)
					{
						DownloadManager.getInstance().stopSingleTask(context);
					}
				}
				model.fireTableDataChanged();
			} 
		});
		right.add(stop);
		
		JMenuItem open = new JMenuItem("Open Folder");  
		open.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
				Iterator<IContext> iter = contexts.iterator();
				while(iter.hasNext())
				{
					IContext context = iter.next();
	            	if (Desktop.isDesktopSupported()) {
	            	    try {
	            	    	File parent = new File(context.getAbsolutePath()).getParentFile();
	            	    	if(parent.exists())
	            	    	{
	            	    		Desktop.getDesktop().open(parent);
	            	    	}					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	}
	            	break;
            	}
				model.fireTableDataChanged();
            }
        }); 
		right.add(open);
		
		JMenuItem remove = new JMenuItem("Remove Task");  
		remove.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
				Iterator<IContext> iter = contexts.iterator();
				while(iter.hasNext())
				{
					IContext context = iter.next();
					DownloadManager.getInstance().removeTask(context, false);
            	}
				model.fireTableDataChanged();
            }
        }); 
		right.add(remove);
		
		JMenuItem removeWithFile = new JMenuItem("Remove Task with data");  
		removeWithFile.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
				Iterator<IContext> iter = contexts.iterator();
				while(iter.hasNext())
				{
					IContext context = iter.next();
					DownloadManager.getInstance().removeTask(context, true);
            	}
				model.fireTableDataChanged();
            }
        }); 
		right.add(removeWithFile);
		
		JMenuItem reDownload = new JMenuItem("Re-download");  
		reDownload.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
				Iterator<IContext> iter = contexts.iterator();
				while(iter.hasNext())
				{
					IContext context = iter.next();
					DownloadManager.getInstance().removeTask(context, true);
					DownloadManager.getInstance().addNewTask(context.getURL(),context.getSavePath(),context.getBlogId(),context.getBlogName());
            	}
				model.fireTableDataChanged();
            }
        }); 
		right.add(reDownload);
		
		JMenuItem copyURL = new JMenuItem("copyURL");  
		copyURL.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {
            	System.out.println("copy");
            	StringBuffer sb = new StringBuffer();
				Iterator<IContext> iter = contexts.iterator();
				while(iter.hasNext())
				{
					IContext context = iter.next();
					sb.append(context.getURL());
					sb.append(Constants.ENTER);
            	}
				System.out.println(sb.toString());
				ClipboardUtils.setSystemClipboard(sb.toString());
            }
        }); 
		right.add(copyURL);
		
		JMenu priority = new JMenu("priority");  
    	for(DownloadPriority p : DownloadPriority.values())
		{
    		JMenuItem sub = new JMenuItem(p.name());
			sub.addActionListener(new ActionListener() 
			{  
	            public void actionPerformed(ActionEvent evt) { 
					Iterator<IContext> iter = contexts.iterator();
					while(iter.hasNext())
					{
						IContext context = iter.next();
						if(!context.isRun())
						{
							DownloadManager.getInstance().removeContextFromQueue(context);
			            	context.setPriority(p);
			            	context.setState(DownloadState.CREATE);
			            	DownloadManager.getInstance().startSingleTask(context);
						}
					}
					model.fireTableDataChanged();
	            }
	        });
			priority.add(sub);
		}
		right.add(priority);
		return right;
	}
}
