package com.icesoft.tumblr.downloader.tablemodel;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import com.icesoft.tumblr.downloader.configure.Constants;
import com.icesoft.tumblr.state.DownloadState;

public class DownloadStateRenderer implements TableCellRenderer {
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
		if(value != null && value instanceof DownloadState)
		{
			DownloadState state = (DownloadState) value;
			switch(state)
			{
				case COMPLETE:		label.setText(String.format(Constants.HTML_COLORED_TEXT,"green",state.name()));
					break;
				case DOWNLOAD:		label.setText(String.format(Constants.HTML_COLORED_TEXT,"green",state.name()));
					break;
				case EXCEPTION:		label.setText(String.format(Constants.HTML_COLORED_TEXT,"red",state.name()));
					break;
				case LOCAL_QUERY:	label.setText(String.format(Constants.HTML_COLORED_TEXT,"green",state.name()));
					break;
				case NETWORK_QUERY:	label.setText(String.format(Constants.HTML_COLORED_TEXT,"green",state.name()));
					break;
				case PAUSE:			label.setText(String.format(Constants.HTML_COLORED_TEXT,"gray",state.name()));
					break;
				case RESUME:		label.setText(String.format(Constants.HTML_COLORED_TEXT,"green",state.name()));
					break;
				case WAIT:			label.setText(String.format(Constants.HTML_COLORED_TEXT,"black",state.name()));
					break;
				default:			label.setText(String.format(Constants.HTML_COLORED_TEXT,"pink",state.name()));
					break;

			}
			return label;
		}else{
			System.out.println("DownloadState Null");
		}
		return null;
	}
}
