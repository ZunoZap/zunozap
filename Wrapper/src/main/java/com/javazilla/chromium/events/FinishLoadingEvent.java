package com.javazilla.chromium.events;

import com.javazilla.chromium.Browser;

public final class FinishLoadingEvent extends LoadEvent {

    private long frameId;
    private boolean isFrameMain;
    private String url;

    public FinishLoadingEvent(Browser browser, long frameId, boolean mainFrame, String validatedURL) {
        super(browser);

        this.frameId = frameId;
        this.isFrameMain = mainFrame;
        this.url = validatedURL;
    }

    public long getFrameId() {
        return frameId;
    }

    public boolean isMainFrame() {
        return isFrameMain;
    }

    public String getValidatedURL() {
        return url;
    }

}