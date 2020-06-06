package com.zunozap;

import com.zunozap.Settings.Options;

import javafx.scene.Node;

/**
 * Collection of methods that are the same between engines
 */
public interface Engine {

    public enum Type { WEBKIT, CHROME, COPPER }

    public Node getComponent();

    public Object getImplEngine();

    public default void load(String url) {
        if (url.startsWith("<html>"))
            loadHTML(url);
        else load((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", ""))) ? String.format(Settings.searchEn, url.replace(" ", "%20")) :
            url.startsWith("http") ? url : "http" + (Options.forceHTTPS.b ? "s://" : "://") + url);
    }

    public void loadRaw(String url);

    public void loadHTML(String html);

    public String getTitle();

    public String getURL();

    public void stop();

    public void js(boolean bo);

    public String getUserAgent();

    public void history(int history);

}