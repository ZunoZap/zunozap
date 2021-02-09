package com.javazilla.chromium.events;

import com.javazilla.chromium.Browser;

public abstract class BrowserEvent {

    private Browser browser;

    public BrowserEvent(Browser browser) {
        this.browser = browser;
    }

    public Browser getBrowser() {
        return browser;
    }

}
