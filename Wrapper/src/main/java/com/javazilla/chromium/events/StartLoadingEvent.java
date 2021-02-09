package com.javazilla.chromium.events;

import com.javazilla.chromium.Browser;

public class StartLoadingEvent extends LoadEvent {

    private long frameId;
    private long parentId;
    private boolean isFrameMain;
    private String url;
    private boolean isError;
    private boolean sameDoc;

    public StartLoadingEvent(Browser browser, long frameId, long parentFrameId, boolean mainFrame, String validatedURL, boolean errorPage) {
        this(browser, frameId, parentFrameId, mainFrame, validatedURL, errorPage, false);
    }

    public StartLoadingEvent(Browser browser, long frameId, long parentFrameId, boolean mainFrame, String validatedURL, boolean errorPage, boolean sameDocument) {
        super(browser);

        this.frameId = frameId;
        this.parentId = parentFrameId;
        this.isFrameMain = mainFrame;
        this.url = validatedURL;
        this.isError = errorPage;
        this.sameDoc = sameDocument;
    }

    public long getFrameId() {
        return frameId;
    }

    public long getParentFrameId() {
        return parentId;
    }

    public boolean isMainFrame() {
        return isFrameMain;
    }

    public String getValidatedURL() {
        return url;
    }

    public boolean isErrorPage() {
        return isError;
    }

    public boolean isSameDocument() {
        return sameDoc;
    }

}