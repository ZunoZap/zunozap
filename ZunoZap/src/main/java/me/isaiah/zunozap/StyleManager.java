package me.isaiah.zunozap;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;

import javafx.scene.Scene;

public class StyleManager {
    private static StyleManager sm;
    public final Scene s;
    public static HashMap<String, File> b = new HashMap<>();

    public static void setStyle(String name) {
        try {
            sm.s.getStylesheets().setAll(b.get(name).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    public StyleManager(File folder, Scene sc) {
        s = sc;
        sm = this;
        System.out.println("Enabling StyleManager");
        init(folder);
    }

    public void init(File folder) {
        ZunoAPI.exportResource("style.css", ZunoAPI.home);
        ZFile f = new ZFile("style.css", false);
        b.put("ZunoZap default", f);
        if (ZunoAPI.styleName.equalsIgnoreCase("none") || ZunoZapWebView.firstRun || ZunoZap.firstRun) {
            ZunoAPI.stylesheet = f;
            ZunoAPI.styleName = "ZunoZap default";
        } else OptionMenu.init();

        for (File fi : folder.listFiles()) b.put(fi.getName(), fi);

        ZFile temp = new ZFile("blank.css", false);
        temp.deleteOnExit();
        b.put("Java", temp);
    }
}
