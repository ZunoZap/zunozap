package com.javazilla.chromium.listeners;

import com.javazilla.chromium.events.FailLoadingEvent;
import com.javazilla.chromium.events.FinishLoadingEvent;
import com.javazilla.chromium.events.FrameLoadEvent;
import com.javazilla.chromium.events.LoadEvent;
import com.javazilla.chromium.events.StartLoadingEvent;

public interface LoadListener extends BrowserListener {

    public void onStartLoadingFrame(StartLoadingEvent e);

    public void onFinishLoadingFrame(FinishLoadingEvent e);

    public void onFailLoadingFrame(FailLoadingEvent e);

    public void onDocumentLoadedInFrame(FrameLoadEvent e);

    public void onDocumentLoadedInMainFrame(LoadEvent e);

    @Override
    public default Class<?> getType() {
        return LoadListener.class;
    }

}