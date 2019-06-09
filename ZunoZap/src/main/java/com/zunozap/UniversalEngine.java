package com.zunozap;

import com.teamdev.jxbrowser.chromium.Browser;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Collection of methods that are the same between engines
 */
public class UniversalEngine {

    public enum Engine { WEBKIT, CHROME }
    public Engine e;
    protected WebView w;
    private WebEngine en;
    protected Browser b;
    private boolean c;

    public UniversalEngine(WebView w) {
        this.e = Engine.WEBKIT;
        this.w = w;
        this.en = w.getEngine();
        this.c = false;
    }

    public UniversalEngine(Browser b) {
        this.e = Engine.CHROME;
        this.b = b;
        this.c = true;
    }

    public Object getImplEngine() {
        return c ? b : w;
    }

    public void load(String url) {
        if (c) b.loadURL(url); else en.load(url);
    }

    public void loadHTML(String html) {
        if (c) b.loadHTML(html); else en.loadContent(html);
    }

    public String getTitle() {
        return c ? b.getTitle() : en.getTitle();
    }

    public String getURL() {
        return c ? b.getURL() : en.getLocation();
    }

    public void stop() {
       if (c) b.stop(); else en.loadContent("force stop");
    }

    public void js(boolean bo) {
        if (c) b.getPreferences().setJavaScriptEnabled(bo); else en.setJavaScriptEnabled(bo);
    }

}