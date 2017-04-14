package com.icesoft.tumblr.downloader.tablemodel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.icesoft.tumblr.state.DownloadState;

public class DownloadStateRenderer implements TableCellRenderer {
	private JLabel label = new JLabel();
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if(value != null && value instanceof DownloadState)
		{
			DownloadState state = (DownloadState) value;
			label.setOpaque(true);
			label.setText(state.name());
			switch(state)
			{
				case COMPLETE:		label.setBackground(Color.BLUE);
					break;
				case DOWNLOAD:		label.setBackground(Color.GREEN);
					break;
				case EXCEPTION:		label.setBackground(Color.RED);
					break;
				case LOCAL_QUERY:	label.setBackground(Color.GREEN);
					break;
				case NETWORK_QUERY:	label.setBackground(Color.GREEN);
					break;
				case PAUSE:			label.setBackground(Color.GRAY);
					break;
				case RESUME:		label.setBackground(Color.GREEN);
					break;
				case WAIT:			label.setBackground(Color.WHITE);
					break;
				default:			label.setBackground(Color.BLACK);
					break;

			}
			return label;
		}else{
			System.out.println("DownloadState Null");
		}
		return null;
	}
}
