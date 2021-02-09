package com.javazilla.chromium.events;

import org.cef.network.CefRequest;

import com.javazilla.chromium.Browser;

public class ResourceRequestEvent extends BrowserEvent {

    private CefRequest request;

    public ResourceRequestEvent(Browser browser, CefRequest request) {
        super(browser);
        this.request = request;
    }

    public void setUrl(String url) {
        request.setURL(url);
    }

    public String getReferrer() {
        return request.getReferrerURL();
    }

    public String getResourceType() {
        return request.getResourceType().name();
    }

    public CefRequest getCefRequest() {
        return request;
    }

    public String getUrl() {
        return request.getURL();
    }

    public boolean isCanceled() {
        return request.getURL().contains("adblock_block-sdjd");
    }

    public void cancel() {
        request.setURL("https://adblock_block-sdjdjfbskdfb.com/");
    }

}