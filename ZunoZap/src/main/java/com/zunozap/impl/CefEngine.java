package com.zunozap.impl;

import com.javazilla.chromium.Browser;
import com.javazilla.chromium.BrowserPreferences;
import com.javazilla.chromium.listeners.ResourceRequestListener;
import com.javazilla.chromium.listeners.TitleListener;
import com.javazilla.chromium.view.JfxBrowserView;
import com.zunozap.Engine;
import com.zunozap.ZunoZap;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class CefEngine implements Engine {

    private Browser b;
    private JfxBrowserView v;

    public CefEngine(String url) {
        //BrowserPreferences.setUserAgent(BrowserPreferences.getUserAgent() + " ZunoZap/" + ZunoZap.VERSION);
        System.out.println(BrowserPreferences.getUserAgent());
        this.b = new Browser(url);
        v = new JfxBrowserView(b);
    }

    @Override
    public Node getComponent() {
        return v;
    }

    @Override
    public Object getImplEngine() {
        return b;
    }

    @Override
    public void loadRaw(String url) {
        b.loadURL(url);
    }

    @Override
    public void loadHTML(String html) {
        b.loadHTML(html);
    }

    @Override
    public String getTitle() {
        return b.getTitle();
    }

    @Override
    public String getURL() {
        return b.getURL();
    }

    @Override
    public void stop() {
        b.stop();
    }

    @Override
    public String getUserAgent() {
        return BrowserPreferences.getUserAgent();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void history(int history) {
        if (history == 0) b.goBack();
        else if (history == 1) b.goForward();
    }

    @Override
    public void addHandlers(TextField urlField, Tab tab, Button bkmark, Button pro) {
        b.addListener((TitleListener)ev -> ZunoZap.changeTitle(CefEngine.this, urlField, tab, bkmark, pro));
        b.addListener((ResourceRequestListener)ev -> {
            Blocker.check(ev, ev.getUrl());
        });
    }

}
