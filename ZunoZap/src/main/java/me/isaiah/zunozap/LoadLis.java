package me.isaiah.zunozap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

public abstract class LoadLis implements LoadListener {
    @Override public void onDocumentLoadedInFrame(FrameLoadEvent e){}
    @Override public void onDocumentLoadedInMainFrame(LoadEvent e){}
    @Override public void onFailLoadingFrame(FailLoadingEvent e){}
    @Override public void onProvisionalLoadingFrame(ProvisionalLoadingEvent e){}

    @Override public void onStartLoadingFrame(StartLoadingEvent e) {
        if (!EOption.blockMalware.b) return;

        try {
            URL url = new URL(e.getBrowser().getURL());
            if (ZunoAPI.block == null || ZunoAPI.block.isEmpty() || ZunoAPI.block.size() < 1) return;

            if (ZunoAPI.block.contains(url.toURI().getHost())) {
                e.getBrowser().stop();
                e.getBrowser().loadURL("https://zunozap.github.io/pages/blocked.html?" + url.toURI().getHost());
            }
        } catch (IOException | URISyntaxException e1) {}
    }
}