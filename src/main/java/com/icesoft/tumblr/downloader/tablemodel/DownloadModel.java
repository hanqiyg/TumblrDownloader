package com.icesoft.tumblr.downloader.tablemodel;

import java.util.Date;

import javax.swing.table.AbstractTableModel;

import com.icesoft.tumblr.downloader.datamodel.ProgressObject;
import com.icesoft.tumblr.downloader.datamodel.SizeObject;
import com.icesoft.tumblr.downloader.datamodel.SpeedObject;
import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.state.interfaces.IContext;

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
		return DownloadManager.getInstance().getContexts().size();
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
		IContext context = DownloadManager.getInstance().getContexts().get(row);
		switch(ColName.values()[col])
		{
			case MESSAGE:		return context.getMessage();
			case CREATETIME:	return new Date(context.getCreateTime());
			case ID:			return row + 1;
			case NAME:			return context.getAbsolutePath();
			case PROGRESS:		return new ProgressObject(context);
			case RECIVED:		return new SizeObject(context.getLocalFilesize());
			case SPEED:			return new SpeedObject(context.getCurrentSpeed());
			case STATUS:		return context.getState();
			case TOTAL:			return new SizeObject(context.getRemoteFilesize());
			case URL:			return context.getURL();
			default:			return null;
		}		
	}
	public IContext getContexts(Object o){
		if(o instanceof Integer){
			int i = (int) o;
			IContext context = DownloadManager.getInstance().getContexts().get(i);
			return context;
		}
		return null;
	}
}
