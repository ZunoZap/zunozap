package com.zunozap.impl;

import java.util.Random;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserException;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import com.zunozap.Engine;
import com.zunozap.ZFile;

import javafx.scene.Node;

public class ChromeEngine implements Engine {

    private static Random r = new Random();

    private Browser b;
    private BrowserView v;

    public ChromeEngine() {
        try {
            this.b = new Browser();
        } catch (BrowserException e) {
            ZFile f = new ZFile("engine" + r.nextInt(10));
            f.mk();
            f.deleteOnExit();
            b = new Browser(new BrowserContext(new BrowserContextParams(f.getAbsolutePath())));
        }
        v = new BrowserView(b);
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
    public void load(String url) {
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
    public void js(boolean bo) {
        b.getPreferences().setJavaScriptEnabled(bo);
    }

    @Override
    public String getUserAgent() {
        return b.getUserAgent();
    }

    @Override
    public void history(int history) {
        if (history == 0)
            b.goBack();
        else if (history == 1)
            b.goForward();
    }

}
