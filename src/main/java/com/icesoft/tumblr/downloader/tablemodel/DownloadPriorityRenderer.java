package com.icesoft.tumblr.downloader.tablemodel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.icesoft.tumblr.state.DownloadPriority;

public class DownloadPriorityRenderer implements TableCellRenderer {
	private JLabel label = new JLabel();
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		DownloadPriority priority = (DownloadPriority) value;
		if(value != null && value instanceof DownloadPriority)
		{
			label.setOpaque(true);
			label.setText(priority.name());
			switch(priority)
			{
			case CRITICAL:	label.setBackground(Color.RED);
				break;
			case HIGH:		label.setBackground(Color.ORANGE);
				break;
			case LOW:		label.setBackground(Color.GRAY);
				break;
			case MEDIUM:	label.setBackground(Color.GREEN);
				break;
			case NORMAL:	label.setBackground(Color.WHITE);
				break;
			default:		label.setBackground(Color.BLACK);
				break;
			
			}
			return label;
		}
		return null;
	}
}
