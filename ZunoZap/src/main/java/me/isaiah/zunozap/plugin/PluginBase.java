package me.isaiah.zunozap.plugin;

import java.io.File;
import java.net.URL;

import com.teamdev.jxbrowser.chromium.Browser;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import me.isaiah.zunozap.ZFile;

public abstract class PluginBase {
    public final File dataFolder = new File(new ZFile("plugins"), getPluginInfo().name);

    /**
     * Info about your plug-in
     */
    public PluginInfo getPluginInfo() {
        return null;
    }

    /**
     * Called when the browser is created. 
     * 
     * @param tabBar - Tab bar.
     * @param scene - {@link javafx.scene.Scene}
     * @param stage {@link javafx.stage.Stage}
     */
    public void onLoad(Stage stage, Scene scene, TabPane tabBar){/**/}

    /**
     * Called when a new tab is created.
     * 
     * @param tab - The tab.
     */
    public void onTabCreate(Tab tab){/**/}

    /**
     * Called when the page URL changes
     * 
     * @param webEngine - Java 8's WebEngine.
     * @param urlField - the address field.
     * @param oldURL - URL changing from.
     * @param newURL - URL changing to.
     * 
     * @deprecated ZunoZap 0.3.6+ uses the Chromium engine instead of JavaFX
     */
    @Deprecated
    public void onURLChange(WebEngine webEngine, TextField urlField, URL oldURL, URL newURL){/**/}

    /**
     * Called when a pop-up pops up.
     * 
     * @param badPopup
     *            - true if the pop-up set off the build-in anti-virus alarm,
     *            false otherwise.
     */
    public void onPopup(boolean badPopup){/**/}

    /**
     * Called when the page URL changes
     * 
     * @param engine - Chromium engine.
     * @param urlField - the address field.
     * @param oldURL - URL changing from.
     * @param newURL - URL changing to.
     */
    public void onURLChange(Browser engine, TextField field, Object oldURL, URL newURL) {/**/}
}
