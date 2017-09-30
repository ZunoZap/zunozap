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
        ZunoAPI.exportResource("style.css", ZunoAPI.home);
        File f = new File(ZunoAPI.home, "style.css");
        b.put("ZunoZap default", f);
        if (ZunoAPI.styleName.equalsIgnoreCase("none") || ZunoZap_old.firstRun || ZunoZap.firstRun) {
            ZunoAPI.stylesheet = f;
            ZunoAPI.styleName = "ZunoZap default";
        } else OptionMenu.init();

        for (File fi : folder.listFiles()) b.put(fi.getName(), fi);

        File temp = new File(ZunoAPI.home, "temp");
        if (!temp.exists()) temp.mkdir();
        File.createTempFile("blank-style", ".css", temp);
        b.put("Java default", temp);
    }
}
