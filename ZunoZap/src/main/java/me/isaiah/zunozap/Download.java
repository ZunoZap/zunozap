package me.isaiah.zunozap;

import me.isaiah.downloadmanager.DownloadManager;

public class Download {
    private DownloadManager dm = null;
    public Download(String url) {
        if (dm == null) dm = new DownloadManager();

        dm.addDownload(url);
        dm.setVisible(true);
    }
}