package com.icesoft.tumblr.downloader.datamodel;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ProgressCellRenderer implements TableCellRenderer {
	JProgressBar progress = new JProgressBar();
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		int percents = (int) value;
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setValue(percents);
		progress.setStringPainted(true);
		return progress;
	}
}
