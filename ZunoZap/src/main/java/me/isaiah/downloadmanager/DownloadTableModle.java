package me.isaiah.downloadmanager;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;
 
// This class manages the download table's data.
class DownloadsTableModel extends AbstractTableModel implements Observer {
    private static final long serialVersionUID = 1L;

    private static final String[] columnNames = {"URL", "Size", "Progress", "Status"};  // Names for the table's columns.
    private static final Class<?>[] columnClasses = {String.class, String.class, JProgressBar.class, String.class}; // Classes for each column's values.

    private ArrayList<Download> downloadList = new ArrayList<>(); // The table's list of downloads.

    public void addDownload(Download download) {
        download.addObserver(this); // Register to be notified when the download changes.
         
        downloadList.add(download);

        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public Download getDownload(int row) {
        return downloadList.get(row);
    }

    public ArrayList<Download> getDownloads() {
        return downloadList;
    }

    // Remove a download from the list.
    public void clearDownload(int row) {
        downloadList.remove(row);

        fireTableRowsDeleted(row, row);
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return columnClasses[col];
    }

    @Override
    public int getRowCount() {
        return downloadList.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Download download = downloadList.get(row);
        switch (col) {
            case 0: // URL
                return download.getUrl();
            case 1: // Size
                int size = download.getSize();
                return (size == -1) ? "" : formatSize(size);
            case 2: // Progress
                return new Float(download.getProgress());
            case 3: // Status
                return Download.STATUSES[download.getStatus()];
        }
        return "";
    }

    @Override
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);
    }

    private String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }
}