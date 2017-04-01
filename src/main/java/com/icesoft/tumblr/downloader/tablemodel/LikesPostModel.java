package com.icesoft.tumblr.downloader.tablemodel;

import javax.swing.table.AbstractTableModel;

import com.icesoft.tumblr.downloader.service.PostService;

public class LikesPostModel extends AbstractTableModel {
	private static final long serialVersionUID = 7484649692951930033L;
	Object name[] = {"#","Type","Url","NoteCount","Status"};
	@Override
	public int getColumnCount() {
		return name.length;
	}
	@Override
	public int getRowCount() {
		return PostService.getInstance().getPosts().size();
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
	public Object getValueAt(int row, int col) {
		switch(col){
			case 0 : return row;
			case 1 : return PostService.getInstance().getPosts().get(row).getType();
			case 2 : return PostService.getInstance().getPosts().get(row).getPostUrl();
			case 3 : return PostService.getInstance().getPosts().get(row).getNoteCount();
		}
		return null;
	}
}
