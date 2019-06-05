package me.isaiah.downloadmanager;

public class DownloadFrame {

    private DownloadManager dm = null;

    public DownloadFrame(String url) {
        if (dm == null) dm = new DownloadManager();

        dm.addDownload(url);
        dm.setVisible(true);
    }

}