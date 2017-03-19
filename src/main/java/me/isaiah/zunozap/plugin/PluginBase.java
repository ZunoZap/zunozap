package me.isaiah.zunozap.plugin;

import java.net.URL;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

public abstract class PluginBase {
    public PluginInfo getPluginInfo() {
        return null;  
    }

    /**
     * Called when the browser is created. 
     * 
     * @param tabBar 
     * @param scene 
     * @param stage 
     */
    public void onLoad(Stage stage, Scene scene, TabPane tabBar){/**/}

    /**
     * Called when a new tab is created.
     * 
     * @param tab
     */
    public void onTabCreate(Tab tab){/**/}

    /**
     * Called when the page URL changes
     * 
     * @param webEngine
     * @param urlField
     * @param oldURL
     * @param newURL
     */
    public void onURLChange(WebEngine webEngine, TextField urlField, URL oldURL, URL newURL){/**/}
}
