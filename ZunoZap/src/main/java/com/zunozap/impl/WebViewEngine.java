package com.zunozap.impl;

import com.zunozap.Engine;
import com.zunozap.ZunoZap;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

@Deprecated
public class WebViewEngine implements Engine {

    private WebView w;
    private WebEngine e;

    public WebViewEngine(String url) {
        this.w = new WebView();
        this.e = w.getEngine();
        this.load(url);
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
    public void loadRaw(String url) {
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
    public String getUserAgent() {
        return e.getUserAgent();
    }

    @Override
    public void history(int history) {
        if (history == 0) e.executeScript("history.back();");
        if (history == 1) e.executeScript("history.forward();");
    }

    @Override
    public void addHandlers(TextField field, Tab tab, Button bm, Button s) {
        e.locationProperty().addListener((o,oU,nU) -> ZunoZap.changed(WebViewEngine.this, field, tab, oU, nU, bm,s));
        e.titleProperty().addListener((ov, o, n) -> ZunoZap.changeTitle(WebViewEngine.this, field, tab, bm,s));
    }

}