package me.isaiah.zunozap;

import java.io.File;
import java.util.HashMap;

import javafx.scene.Scene;

public class StyleManager {
    private static Scene scene;
    public static HashMap<String, File> b = new HashMap<>();

    public static Scene staticGetScene() {
        return scene;
    }

    public StyleManager() {}

    public StyleManager(File folder) {
        System.out.println("Starting StyleManager...");
        try { init(folder); } catch (Exception e) { e.printStackTrace(); }
    }

    public StyleManager(File folder, Scene sc) {
        StyleManager.scene = sc;
        System.out.println("Starting StyleManager...");
        try { init(folder); } catch (Exception e) { e.printStackTrace(); }
    }

    public void init(File folder) throws Exception {
        ZunoAPI.exportResource("style.css", ZunoAPI.home);
        ZFile f = new ZFile("style.css", false);
        b.put("ZunoZap default", f);
        if (ZunoAPI.styleName.equalsIgnoreCase("none") || ZunoZap_old.firstRun || ZunoZap.firstRun) {
            ZunoAPI.stylesheet = f;
            ZunoAPI.styleName = "ZunoZap default";
        } else OptionMenu.init();

        for (File fi : folder.listFiles()) b.put(fi.getName(), fi);

        ZFile temp = new ZFile("temp");
        File.createTempFile("blank-style", ".css", temp);
        b.put("Java", temp);
    }
}
