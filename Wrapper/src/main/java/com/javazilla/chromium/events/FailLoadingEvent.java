package com.javazilla.chromium.events;

import org.cef.handler.CefLoadHandler;

import com.javazilla.chromium.Browser;

public class FailLoadingEvent extends LoadEvent {

    private final long frameId;
    private final boolean isFrameMain;
    private final String url;
    private final CefLoadHandler.ErrorCode error;
    private final String errorDes;

    public FailLoadingEvent(Browser browser, long frameId, boolean mainFrame, String validatedURL, CefLoadHandler.ErrorCode errorCode, String errorDescription) {
        super(browser);

        this.frameId = frameId;
        this.isFrameMain = mainFrame;
        this.url = validatedURL;
        this.error = errorCode;
        this.errorDes = errorDescription;
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

    public CefLoadHandler.ErrorCode getErrorCode() {
        return error;
    }

    public String getErrorDescription() {
        return errorDes;
    }

}