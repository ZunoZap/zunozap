package me.isaiah.zunozap;

import java.net.MalformedURLException;
import java.net.URL;

import com.teamdev.jxbrowser.chromium.Browser;

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.manager.PluginManager;

public class Hooks {
    private PluginManager p;
    public Hooks(PluginManager p) {
        this.p = p;
    }

    public void onStart(Stage stage, Scene scene, TabPane tb) {
        if (a()) for (PluginBase pl : p.plugins) pl.onLoad(stage, scene, tb);
    }

    public void onUrlChange(Browser b, TextField field, String old, String newURL) {
        if (a()) for (PluginBase pl : p.plugins) {
            try {
                pl.onURLChange(b, field, (old != null ? new URL(old) : null), new URL(newURL));
            } catch (MalformedURLException e) {
                System.err.println("Cant pass url change to plugin " + pl.getPluginInfo().name + " " + pl.getPluginInfo().version + " [" + e.getMessage() + "]");
            }
        }
    }

    private boolean a() {
        return ZunoAPI.getInstance().allowPluginEvents();
    }
}
