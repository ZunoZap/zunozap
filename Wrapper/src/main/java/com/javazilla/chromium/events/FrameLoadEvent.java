package com.javazilla.chromium.events;

import com.javazilla.chromium.Browser;

public final class FrameLoadEvent extends BrowserEvent {

    private long frameId;
    private boolean isFrameMain;

    public FrameLoadEvent(Browser browser, long frameId, boolean isMainFrame) {
        super(browser);
        this.frameId = frameId;
        this.isFrameMain = isMainFrame;
    }

    public long getFrameId() {
        return frameId;
    }

    public boolean isMainFrame() {
        return isFrameMain;
    }

}