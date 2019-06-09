package me.isaiah.downloadmanager;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class ProgressRenderer extends JProgressBar implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    public ProgressRenderer(int min, int max) { super(min, max); }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setValue((int) ((Float) value).floatValue());
        return this;
    }

}