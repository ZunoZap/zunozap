package com.zunozap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.zunozap.Settings.Options;

import me.isaiah.downloadmanager.DownloadManager;

public abstract class LoadLis implements LoadListener {
    
    private boolean m;

    public LoadLis() { this.m = false; }
    public LoadLis(boolean m) { this.m = m; }

    @Override public void onDocumentLoadedInFrame(FrameLoadEvent e){}
    @Override public void onDocumentLoadedInMainFrame(LoadEvent e){}
    @Override public void onFailLoadingFrame(FailLoadingEvent e){}
    @Override public void onProvisionalLoadingFrame(ProvisionalLoadingEvent e){}

    @Override public void onStartLoadingFrame(StartLoadingEvent e) {
        if (!m) return;
        String url = e.getValidatedURL();

        try {
            URL ur = new URL(url);
            System.out.println(ur.toURI().getHost());
            System.out.println(ur.toURI().getHost());
            if (ZunoZap.block == null || ZunoZap.block.isEmpty() || ZunoZap.block.size() < 1) return;
    
            if (ZunoZap.block.contains(ur.toURI().getHost())) {
                e.getBrowser().stop();
                e.getBrowser().loadURL("http://zunozap.com/pages/blocked.html?" + url);
            }
        } catch (IOException | URISyntaxException e1) {}

        if (ZunoZap.isUrlDownload(url)) DownloadManager.addToManager(url);
    }

}