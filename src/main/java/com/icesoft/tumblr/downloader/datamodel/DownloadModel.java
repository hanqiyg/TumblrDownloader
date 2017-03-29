package com.icesoft.tumblr.downloader.datamodel;

import javax.swing.table.AbstractTableModel;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
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
		DownloadManager manager = DownloadManager.getInstance();
		switch(col){
			case 0 : return row;
			case 1 : return manager.getTasks().get(row).getFilename();
			case 2 : return manager.getTasks().get(row).getUrl();
			case 3 : return manager.getTasks().get(row).getState();
			case 4 : return manager.getTasks().get(row).getProgress();
			case 5 : return getSpeedString(manager.getTasks().get(row).getSpeed());
			case 6 : return getSizeString(manager.getTasks().get(row).getCurrent());
			case 7 : return getSizeString(manager.getTasks().get(row).getFilesize());
		}
		return null;
	}
	public String getSpeedString(float speed){
		int bps = (int) (speed * 1000);
		return UnitUtils.getFormatSpeed(bps);
	}
	public String getSizeString(float size){		
		return UnitUtils.getFormatSize(size);
	}
}
