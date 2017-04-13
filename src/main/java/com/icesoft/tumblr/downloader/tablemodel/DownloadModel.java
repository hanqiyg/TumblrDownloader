package com.icesoft.tumblr.downloader.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import com.icesoft.tumblr.downloader.datamodel.ProgressObject;
import com.icesoft.tumblr.downloader.datamodel.SizeObject;
import com.icesoft.tumblr.downloader.datamodel.SpeedObject;
import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadModel extends AbstractTableModel {
	private static final long serialVersionUID = 4901965435625204398L;
	
	public enum ColName{
		ID(0,"#",Integer.class),
		NAME(1,"Name",String.class),
		URL(2,"URL",String.class),
		PRIORITY(3,"Priority",DownloadPriority.class),
		STATUS(4,"Status",DownloadState.class),
		PROGRESS(5,"Progress",ProgressObject.class),
		SPEED(6,"Speed",SpeedObject.class),
		CREATETIME(7,"Create Time",Date.class),
		RECIVED(8,"Recived Size",SizeObject.class),
		TOTAL(9,"Total Size",SizeObject.class),
		MESSAGE(10,"Message",String.class);

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
		IContext context = getContext(row);
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
			case PRIORITY:		return context.getPriority();
			default:			return null;
		}		
	}
	public IContext getContext(Object o){
		if(o instanceof Integer){
			int i = (int) o;
			if(i >=0 && i < DownloadManager.getInstance().getContexts().size())
			{
				return DownloadManager.getInstance().getContexts().get(i);
			}
		}
		return null;
	}
	public List<IContext> getContexts(int[] selections)
	{
		List<IContext> list = new ArrayList<IContext>();
		for(int i : selections){
			if(i >=0 && i < DownloadManager.getInstance().getContexts().size())
			{
				list.add(DownloadManager.getInstance().getContexts().get(i));
			}
		}
		return list;
	}
}
