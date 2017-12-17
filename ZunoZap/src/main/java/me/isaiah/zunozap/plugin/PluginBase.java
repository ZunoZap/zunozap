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
     * Info about plug-in
     */
    public PluginInfo getPluginInfo() { return null; }

    /**
     * Called when the browser is created. 
     * 
     * @param tabBar - Tab bar.
     * @param scene - JavaFX Scene
     * @param stage - JavaFX Stage
     */
    public void onLoad(Stage stage, Scene scene, TabPane tabBar){/**/}

    /**
     * Called when a new tab is created.
     * 
     * @param tab - The tab.
     */
    public void onTabCreate(Tab tab){/**/}

    /**
     * @deprecated ZunoZap now uses the Chromium engine.
     * @see #onURLChange(Browser, TextField, Object, URL)
     */
    @Deprecated
    public void onURLChange(WebEngine engine, TextField field, URL old, URL newURL){/**/}

    /**
     * Called when a pop-up pops up.
     * 
     * @param bad - Contains the word "virus"
     */
    public void onPopup(boolean bad){/**/}

    /**
     * Called when the page URL changes
     * 
     * @param e - engine.
     * @param urlField - the address field.
     * @param old - old URL.
     * @param newURL - new URL.
     */
    public void onURLChange(Browser e, TextField field, Object old, URL newURL) {/**/}
}
