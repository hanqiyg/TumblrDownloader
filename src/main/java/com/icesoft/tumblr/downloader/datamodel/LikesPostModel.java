package com.icesoft.tumblr.downloader.datamodel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import com.tumblr.jumblr.types.Post;

public class LikesPostModel extends AbstractTableModel {
	private static final long serialVersionUID = 7484649692951930033L;
	List<PostStatus> posts = new ArrayList<PostStatus>();
	
	Object name[] = {"#","Type","Url","NoteCount","Status","OP"};

	public void addPost(Post p){
		posts.add(new PostStatus(p));
	}

	@Override
	public int getColumnCount() {
		return name.length;
	}

	@Override
	public int getRowCount() {
		return posts.size();
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
		if(col == 5){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public Object getValueAt(int row, int col) {
		switch(col){
			case 0 : return row;
			case 1 : return posts.get(row).getPost().getType();
			case 2 : return posts.get(row).getPost().getPostUrl();
			case 3 : return posts.get(row).getPost().getNoteCount();
			case 4 : return posts.get(row).getStatus();
			case 5 : return posts.get(row);
		}
		return null;
	}

	public void clear() {
		posts = new ArrayList<PostStatus>();
	}
	
	public class PostStatus {
		private Post post;
		private STATUS status;

		
		public PostStatus(Post post){
			this.post = post;
			this.status = STATUS.DEFAULT;
		}
		
		public Post getPost() {
			return post;
		}
		public void setPost(Post post) {
			this.post = post;
		}
		public STATUS getStatus() {
			return status;
		}
		public void setStatus(STATUS status) {
			this.status = status;
			LikesPostModel.this.fireTableDataChanged();
		}
	}
	public enum STATUS{
		DEFAULT,DOWNLOADING,FINISH,EXCEPTION
	}
	public List<PostStatus> getAll(){
		return posts;		
	}
}
