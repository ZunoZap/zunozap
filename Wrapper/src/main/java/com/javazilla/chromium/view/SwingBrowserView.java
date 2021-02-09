package com.javazilla.chromium.view;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import com.javazilla.chromium.Browser;

public class SwingBrowserView extends JPanel {

    private static final long serialVersionUID = 1L;
    private Browser browser;

    public SwingBrowserView(Browser b) {
        this.browser = b;
        this.setLayout(new BorderLayout());
        b.getCef().initSwing(this);
    }

    public Browser getBrowser() {
        return browser;
    }

}