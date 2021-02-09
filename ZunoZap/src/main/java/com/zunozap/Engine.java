package com.zunozap;

import com.zunozap.Settings.Options;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

/**
 * Collection of methods that are the same between engines
 */
public interface Engine {

    public Node getComponent();

    public Object getImplEngine();

    public default void load(String url) {
        if (url.startsWith("<html>"))
            loadHTML(url);
        else loadRaw((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", ""))) ? String.format(Settings.SEARCH, url.replace(" ", "%20")) :
            url.startsWith("http") ? url : "http" + (Options.forceHTTPS.b ? "s://" : "://") + url);
    }

    public void loadRaw(String url);

    public void loadHTML(String html);

    public String getTitle();

    public String getURL();

    public void stop();

    public String getUserAgent();

    public void history(int history);

    public void addHandlers(TextField urlField, Tab tab, Button bkmark, Button pro);

}