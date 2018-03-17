package me.isaiah.zunozap;

import java.net.MalformedURLException;
import java.net.URL;

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.manager.PluginManager;

public class Hooks {
    private PluginManager p;
    public Hooks(PluginManager p) { this.p = p; }

    public void onStart(Stage st, Scene sc, TabPane tb) {
        if (a()) for (PluginBase pl : p.plugins) pl.onLoad(st, sc, tb);
    }

    public void onUrlChange(UniversalEngine engine, TextField field, String old, String url) {
        System.out.println("debug");
        if (a()) {
            for (PluginBase pl : p.plugins) {
            try {
                pl.onURLChange(engine, field, (old != null ? new URL(old) : null), new URL(url));
            } catch (MalformedURLException e) {
                System.err.println("Cant pass url change to plugin " + pl.getPluginInfo().name + " " + pl.getPluginInfo().version + " [" + e.getMessage() + "]");
            }
        }}
    }

    private boolean a() {
        return ZunoAPI.getInstance().allowPluginEvents();
    }
}