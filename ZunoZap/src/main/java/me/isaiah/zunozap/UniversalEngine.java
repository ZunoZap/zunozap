package me.isaiah.zunozap;

import com.teamdev.jxbrowser.chromium.Browser;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Collection of methods that are the same between engines.
 * TODO: Fully add by 0.7.0
 */
public class UniversalEngine {
    public enum Engine { WEBKIT, CHROME }
    public Engine e;
    protected WebView w;
    private WebEngine en;
    protected Browser b;

    public UniversalEngine(WebView w) {
        this.e = Engine.WEBKIT;
        this.w = w;
        this.en = w.getEngine();
    }

    public UniversalEngine(Browser b) {
        this.e = Engine.CHROME;
        this.b = b;
    }

    public void load(String url) {
        if (e == Engine.CHROME) b.loadURL(url); else en.load(url);
    }

    public void loadHTML(String html) {
        if (e == Engine.CHROME) b.loadHTML(html); else en.loadContent(html);
    }

    public String getTitle() {
        return (e == Engine.CHROME ? b.getTitle() : en.getTitle());
    }

    public String getURL() {
        return (e == Engine.CHROME ? b.getURL() : en.getLocation());
    }

    public void stop() {
       if (e == Engine.CHROME) b.stop(); else en.loadContent("forced stop");
    }

    public void js(boolean bo) {
        if (e == Engine.CHROME) b.getPreferences().setJavaScriptEnabled(bo); else en.setJavaScriptEnabled(bo);
    }
}
