package me.isaiah.zunozap;

import me.isaiah.downloadmanager.DownloadManager;

public class Download {
    private DownloadManager instance = null;
    public Download(String url) {
        if (instance == null) instance = new DownloadManager();

        instance.addDownload(url);
        instance.setVisible(true);
    }
}