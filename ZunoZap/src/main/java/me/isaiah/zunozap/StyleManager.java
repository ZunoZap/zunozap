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

    public static HashMap<String, File> staticGetStyles() {
        return b;
    }

    public StyleManager() {
    }

    public StyleManager(File folder) {
        System.out.println("Starting StyleManager...");
        try {
            init(folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-access")
    public StyleManager(File folder, Scene scene) {
        this.scene = scene;
        System.out.println("Starting StyleManager...");
        try {
            init(folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(File folder) throws Exception {
        ZunoZap.ExportResource("style.css");
        File f = new File(ZunoZap.homeDir, "style.css");
        b.put("ZunoZap default", f);
        if (ZunoAPI.styleName.equalsIgnoreCase("none") || ZunoZap.firstRun) {
            ZunoAPI.stylesheet = f;
            ZunoAPI.styleName = "ZunoZap default";
        } else OptionMenu.init();

        for (File fi : folder.listFiles()) b.put(fi.getName(), fi);

        File temp = new File(ZunoZap.temp, "blank.css");
        if (!temp.exists()) temp.createNewFile();
        b.put("Java default", temp);
    }
}
