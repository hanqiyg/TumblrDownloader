package com.icesoft.tumblr.downloader.tablemodel;

import javax.swing.RowFilter;

import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadActiveFilter  extends RowFilter<DownloadModel, Object>{

	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends DownloadModel, ? extends Object> entry) {
		DownloadModel model = entry.getModel();						
		IContext context = model.getContext(entry.getIdentifier());
		if(context != null && context.isRun()){
			return true;
		}
		return false;
	}

}
