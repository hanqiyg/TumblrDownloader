package com.icesoft.tumblr.downloader.tablemodel;

import javax.swing.RowFilter;

import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadTaskStateFilter extends RowFilter<DownloadModel, Object>{
	private DownloadState[] states;
	public DownloadTaskStateFilter(DownloadState... states){
		this.states = states;
	}
	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends DownloadModel, ? extends Object> entry) {
		DownloadModel model = entry.getModel();						
		IContext task = model.getContext(entry.getIdentifier());
		if(task != null && task.getState() != null){
			for(DownloadState s : states){
				if(s.equals(task.getState())){
					return true;
				}
			}
		}
		return false;
	}	
}
