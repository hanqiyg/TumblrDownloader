package com.icesoft.tumblr.downloader.tablemodel;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.icesoft.utils.UnitUtils;


public class TotalTimeRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 5454043637924028126L;

    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
        if( value instanceof Long) {
            value = UnitUtils.getDays((long)value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
	}
}
