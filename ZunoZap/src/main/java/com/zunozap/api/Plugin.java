package com.zunozap.api;

import java.io.File;

import com.zunozap.Engine;
import com.zunozap.ZFile;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public abstract class Plugin {

    public final File folder = new File(new ZFile("plugins"), getInfo().name());

    public PluginInfo getInfo() {
        return getClass().getAnnotation(PluginInfo.class);
    }

    public void onTabCreate(Tab tab){}

    public void onURLChange(Engine e, TextField field, String oldURL, String newURL) {}

    public void onLoad(Stage stage, Scene scene, TabPane tabBar){}

    @Deprecated
    public void onPopup(boolean bad){/**/}

    @Deprecated
    public void onPopup(String popupText) { onPopup(false); }

}