package com.icesoft.tumblr.downloader.tablemodel;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import com.icesoft.tumblr.downloader.datamodel.ProgressObject;


public class ProgressCellRenderer implements TableCellRenderer {
	JProgressBar progress = new JProgressBar();
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		progress.setOpaque(true);
		if (isSelected)
        {
			progress.setBackground(table.getSelectionBackground());
			progress.setForeground(table.getSelectionForeground());
        }
        else
        {
            if (row % 2 == 1) {
            	progress.setBackground(UIManager.getColor("Table.alternateRowColor"));
            } else {
            	progress.setBackground(UIManager.getColor("Table:\"Table.cellRenderer\".background"));
            }
        }
		ProgressObject percents = (ProgressObject) value;
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setValue(percents.intValue());
		progress.setStringPainted(true);
		return progress;
	}
}
