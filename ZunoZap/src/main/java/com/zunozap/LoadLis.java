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

import me.isaiah.downloadmanager.DownloadFrame;

public abstract class LoadLis implements LoadListener {

    @Override public void onDocumentLoadedInFrame(FrameLoadEvent e){}
    @Override public void onDocumentLoadedInMainFrame(LoadEvent e){}
    @Override public void onFailLoadingFrame(FailLoadingEvent e){}
    @Override public void onProvisionalLoadingFrame(ProvisionalLoadingEvent e){}

    @Override public void onStartLoadingFrame(StartLoadingEvent e) {
        String url = e.getValidatedURL();

        if (Options.blockMalware.b) {
            try {
                URL ur = new URL(url);
                if (ZunoAPI.block == null || ZunoAPI.block.isEmpty() || ZunoAPI.block.size() < 1) return;
    
                if (ZunoAPI.block.contains(ur.toURI().getHost())) {
                    e.getBrowser().stop();
                    e.getBrowser().loadURL("http://zunozap.com/pages/blocked.html?" + url);
                }
            } catch (IOException | URISyntaxException e1) {}
        }

        if (ZunoAPI.isUrlDownload(url)) new DownloadFrame(url);
    }

}