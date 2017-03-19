package me.isaiah.zunozap.plugin;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import me.isaiah.zunozap.OptionMenu;

public abstract class PluginBase {
    public PluginBase() {}
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
     */
    public void onURLChange(WebEngine webEngine, TextField urlField, URL oldURL, URL newURL){/**/}

    /**
     * Called when a popup pops up.
     * @param badPopup - true if the popup setoff the build-in antivirus alarm, false outherwise.
     */
    public void onPopup(boolean badPopup) {/**/}
}
