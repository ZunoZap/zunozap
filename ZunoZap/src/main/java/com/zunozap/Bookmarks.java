package com.zunozap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.zunozap.Settings.Options;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Bookmarks {

    private static File f = new File(Settings.HOME, "bookmarks.dat");
    public static HashMap<String, String> map = new HashMap<>();

    public static void add(String title, String url) {
        map.put(url, title);
        refreshBar();
        ZunoZap.s.refreshBk();
        saveMap();
    }

    public static void remove(String url) {
        map.remove(url);
        refreshBar();
        ZunoZap.s.refreshBk();
        saveMap();
    }

    public static void refreshBar() {
        for (VBox box : ZunoZap.tabs.keySet()) {
            if (box.getChildren().size() > 2)
                box.getChildren().remove(1); // remove old bar
            if (!Options.SHOW_BB.b || map.size() <= 0)
                continue;

            HBox bb = new HBox();
            bb.setId("bkb");
            Bookmarks.map.forEach((burl,title) -> {
                Button b = new Button(title.length() > 18 ? title.substring(0,18) + ".." : title);
                b.setId("bk");
                b.setOnAction(ac -> ZunoZap.tabs.get(box).load(burl));
                bb.getChildren().add(b);
            });
            bb.setId("urlbar");
            box.getChildren().add(1, bb);
        }
    }

    public static void saveMap() {
        FileOutputStream fos;
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static void load() {
        try {
            f.createNewFile();
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap<String, String>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}