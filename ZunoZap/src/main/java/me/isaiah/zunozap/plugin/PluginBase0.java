package me.isaiah.zunozap.plugin;

import java.io.File;
import java.net.URL;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEvent;
import javafx.stage.Stage;
import me.isaiah.zunozap.UniversalEngine;
import me.isaiah.zunozap.ZFile;

public abstract class PluginBase {

    public final File dataFolder = new File(new ZFile("plugins"), getPluginInfo().name);

    /**
     * Plugin's info
     */
    public abstract PluginInfo getPluginInfo();

    /**
     * On ZunoZap startup
     */
    public void onLoad(Stage stage, Scene scene, TabPane tabBar){/**/}

    /**
     * Called when a new tab is created.
     */
    public void onTabCreate(Tab tab){/**/}

    @Deprecated
    public void onPopup(boolean bad){/**/}

    /**
     * Called when a JS pop-up pops up
     */
    public void onPopup(WebEvent<String> popupText) { onPopup(false); } // if not overrided in plugin call old method

    /**
     * Called when the page URL changes
     */
    public void onURLChange(UniversalEngine e, TextField field, Object oldURL, URL newURL) {/**/}

}