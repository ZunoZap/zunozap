package com.zunozap.impl;

import com.zunozap.Engine;

import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebKitEngine implements Engine {

    private WebView w;
    private WebEngine e;

    public WebKitEngine() {
        this.w = new WebView();
        this.e = w.getEngine();
    }

    @Override
    public Node getComponent() {
        return w;
    }

    @Override
    public Object getImplEngine() {
        return e;
    }

    @Override
    public void load(String url) {
        e.load(url);
    }

    @Override
    public void loadHTML(String html) {
        e.loadContent(html);
    }

    @Override
    public String getTitle() {
        return e.getTitle();
    }

    @Override
    public String getURL() {
        return e.getLocation();
    }

    @Override
    public void stop() {
        e.loadContent("stop");
    }

    @Override
    public void js(boolean bo) {
        e.setJavaScriptEnabled(bo);
    }

    @Override
    public String getUserAgent() {
        return e.getUserAgent();
    }

    @Override
    public void history(int history) {
        if (history == 0)
            e.executeScript("history.back();");
        else if (history == 1)
            e.executeScript("history.forward();");
    }

}