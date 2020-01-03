package com.zunozap;

import javafx.scene.Node;

/**
 * Collection of methods that are the same between engines
 */
public interface Engine {

    public enum Type { WEBKIT, CHROME, COPPER }
    //public Type e;
    //protected WebView w;
    //private WebEngine en;
    //protected Browser b;
    //private boolean c;

    /*public Engine(WebView w) {
        this.e = Type.WEBKIT;
        this.w = w;
        this.en = w.getEngine();
        this.c = false;
    }

    public Engine(Browser b) {
        this.e = Type.CHROME;
        this.b = b;
        this.c = true;
    }*/

    public Node getComponent();

    public Object getImplEngine();

    public void load(String url);

    public void loadHTML(String html);

    public String getTitle();

    public String getURL();

    public void stop();

    public void js(boolean bo);

    public String getUserAgent();

    public void history(int history);

}