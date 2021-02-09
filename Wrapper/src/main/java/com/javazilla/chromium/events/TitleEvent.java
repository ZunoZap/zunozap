package com.javazilla.chromium.events;

import com.javazilla.chromium.Browser;

public final class TitleEvent extends BrowserEvent {

    public String title;

    public TitleEvent(Browser browser, String title) {
        super(browser);
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

}