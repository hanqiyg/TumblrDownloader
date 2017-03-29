package com.icesoft.tumblr.downloader.datamodel;

import javax.swing.table.AbstractTableModel;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.utils.UnitUtils;

public class DownloadModel extends AbstractTableModel {
	private static final long serialVersionUID = 4901965435625204398L;
	Object name[] = {"#","Name","Url","Status","Progress","Speed","Recived Size","Total Size","OP"};

	@Override
	public int getColumnCount() {
		return name.length;
	}

	@Override
	public int getRowCount() {
		return DownloadManager.getInstance().getTasks().size();
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return name[arg0].getClass();
	}

	@Override
	public String getColumnName(int arg0) {
		return name[arg0].toString();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	@Override
	public Object getValueAt(int row, int col) {
		DownloadTask task = DownloadManager.getInstance().getTasks().get(row).getTask();
		switch(col){
			case 0 : return row;
			case 1 : return task.getFile().getAbsolutePath();
			case 2 : return task.getURL();
			case 3 : return task.getState();
			case 4 : return getProgress(task);
			case 5 : return getSpeedString(task.getCurrentSpeed());
			case 6 : return getSizeString(task.getCurrentSize());
			case 7 : return getSizeString(task.getFilesize());
		}
		return null;
	}
	public int getProgress(DownloadTask task){
		long curr = task.getCurrentSize();
		long full = task.getFilesize();
		if(full <= 0){
			return 0;
		}else{
			return (int) (curr * 100 / full);
		}
	}
	public String getSpeedString(float speed){
		int bps = (int) (speed * 1000);
		return UnitUtils.getFormatSpeed(bps);
	}
	public String getSizeString(float size){		
		return UnitUtils.getFormatSize(size);
	}
}
