package com.javazilla.chromium.view;

import com.javazilla.chromium.Browser;

import javafx.scene.layout.AnchorPane;

public class JfxBrowserView extends AnchorPane {

    private Browser browser;

    public JfxBrowserView(Browser b) {
        this.browser = b;
        b.getCef().initNode(this);
    }

    public Browser getBrowser() {
        return browser;
    }

}