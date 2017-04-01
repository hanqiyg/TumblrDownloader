package com.icesoft.tumblr.downloader.tablemodel;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class DateCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 5454043637924028126L;
	private SimpleDateFormat f = new SimpleDateFormat("MM/dd/yy-HH:mm:ss");

    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
        if( value instanceof Date) {
            value = f.format(value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
	}
}
