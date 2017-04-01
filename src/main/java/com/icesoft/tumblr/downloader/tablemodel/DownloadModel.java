package com.icesoft.tumblr.downloader.tablemodel;

import java.util.Date;

import javax.swing.table.AbstractTableModel;

import com.icesoft.tumblr.downloader.datamodel.ProgressObject;
import com.icesoft.tumblr.downloader.datamodel.SizeObject;
import com.icesoft.tumblr.downloader.datamodel.SpeedObject;
import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.workers.DownloadTask;

public class DownloadModel extends AbstractTableModel {
	private static final long serialVersionUID = 4901965435625204398L;
	Object name[] = {"#","Name","Url","Status","Progress","Speed","Recived Size","Total Size"};

	public enum ColName{
		ID(0,"#",Integer.class),
		NAME(1,"Name",String.class),
		URL(2,"URL",String.class),
		STATUS(3,"Status",DownloadTask.STATE.class),
		PROGRESS(4,"Progress",ProgressObject.class),
		SPEED(5,"Speed",SpeedObject.class),
		CREATETIME(6,"Create Time",Date.class),
		RECIVED(7,"Recived Size",SizeObject.class),
		TOTAL(8,"Total Size",SizeObject.class),
		MESSAGE(9,"Message",String.class);
		
		private int index;
		private String text;
		private Class<?> clazz;
		
		private ColName(int index,String text,Class<?> clazz){
			this.index = index;
			this.text = text;
			this.clazz = clazz;
		}
		public String toString(){
			return text;
		}
		public int intValue(){
			return index;
		}
		public Class<?> toClass(){
			return clazz;
		}
		public static ColName valueOf(int col) {
			for(ColName c : ColName.values()){
				if(c.index == col){
					return c;
				}
			}
			return null;
		}
	}
	@Override
	public int getColumnCount() {
		return ColName.values().length;
	}

	@Override
	public int getRowCount() {
		return DownloadManager.getInstance().getTasks().size();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return ColName.values()[col].toClass();
	}

	@Override
	public String getColumnName(int col) {
		return ColName.values()[col].toString();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	@Override
	public Object getValueAt(int row, int col) {		
		DownloadTask task = DownloadManager.getInstance().getTasks().get(row);
		switch(ColName.values()[col])
		{
			case MESSAGE:		return task.getMessage();
			case CREATETIME:	return new Date(task.getCreateTime());
			case ID:			return row + 1;
			case NAME:			return task.getFile().getAbsolutePath();
			case PROGRESS:		return new ProgressObject(task);
			case RECIVED:		return new SizeObject(task.getLocalFilesize());
			case SPEED:			return new SpeedObject(task.getCurrentSpeed());
			case STATUS:		return task.getState();
			case TOTAL:			return new SizeObject(task.getRemoteFilesize());
			case URL:			return task.getURL();
			default:			return null;
		}		
	}
	public DownloadTask getTask(Object o){
		if(o instanceof Integer){
			int i = (int) o;
			DownloadTask task = DownloadManager.getInstance().getTasks().get(i);
			return task;
		}
		return null;
	}
}
