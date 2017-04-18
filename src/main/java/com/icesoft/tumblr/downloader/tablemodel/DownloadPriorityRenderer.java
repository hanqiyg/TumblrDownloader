package com.icesoft.tumblr.downloader.tablemodel;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import com.icesoft.tumblr.downloader.configure.Constants;
import com.icesoft.tumblr.state.DownloadPriority;

public class DownloadPriorityRenderer implements TableCellRenderer {
	private JLabel label = new JLabel("<html>");
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		label.setOpaque(true);
        if (isSelected)
        {
        	label.setBackground(table.getSelectionBackground());
        }
        else
        {
            if (row % 2 == 1) {
                label.setBackground(UIManager.getColor("Table.alternateRowColor"));
            } else {
                label.setBackground(UIManager.getColor("Table:\"Table.cellRenderer\".background"));
            }
        }
		DownloadPriority priority = (DownloadPriority) value;
		if(value != null && value instanceof DownloadPriority)
		{
			switch(priority)
			{
			case CRITICAL:	label.setText(String.format(Constants.HTML_COLORED_TEXT, "red", priority.name()));
				break;
			case HIGH:		label.setText(String.format(Constants.HTML_COLORED_TEXT, "orange", priority.name()));
				break;
			case LOW:		label.setText(String.format(Constants.HTML_COLORED_TEXT, "gray", priority.name()));
				break;
			case MEDIUM:	label.setText(String.format(Constants.HTML_COLORED_TEXT, "green", priority.name()));
				break;
			case NORMAL:	label.setText(String.format(Constants.HTML_COLORED_TEXT, "black", priority.name()));
				break;
			default:		label.setText(String.format(Constants.HTML_COLORED_TEXT, "pink", priority.name()));
				break;
			
			}
			return label;
		}
		return null;
	}
}
