package me.isaiah.zunozap;

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
    @Override public void onStartLoadingFrame(StartLoadingEvent e){}
}