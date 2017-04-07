package com.icesoft.tumblr.downloader.tablemodel;

import javax.swing.RowFilter;

import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.downloader.workers.DownloadTask.STATE;
import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadTaskStateFilter extends RowFilter<DownloadModel, Object>{
	private STATE[] states;
	public DownloadTaskStateFilter(STATE... states){
		this.states = states;
	}
	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends DownloadModel, ? extends Object> entry) {
		DownloadModel model = entry.getModel();						
		IContext task = model.getContexts(entry.getIdentifier());
		if(task != null && task.getState() != null){
			for(STATE s : states){
				if(s.equals(task.getState())){
					return true;
				}
			}
		}
		return false;
	}
}
