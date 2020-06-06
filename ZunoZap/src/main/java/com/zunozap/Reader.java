package com.zunozap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class Reader {

    public HashMap<String, String> bm;
    private Menu book;

    public Reader(Menu bk) {
        File dat = new ZFile("bookmarks.dat", false);
        this.book = bk;
        this.bm = new HashMap<>();
        bk.getItems().clear();
        try {
            for (String s : Files.readAllLines(Paths.get(dat.toURI()))) {
                if (!s.startsWith("#") && s.trim().length() > 0) {
                    String key = s.substring(0, s.indexOf("="));
                    String value = s.substring(s.indexOf("=") + 1);
                    if (!bm.containsKey(key)) bm.put(key, value);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void readd() {
        bm.forEach((a, b) -> {
            MenuItem item = new MenuItem(a);
            item.setOnAction(t -> ZunoZap.getInstance().createTab(b));
            book.getItems().add(item);
        });
    }

    public void save() throws IOException {
        ZFile dat = new ZFile("bookmarks.dat", false);
        BufferedWriter bw = new BufferedWriter(new FileWriter(dat.getAbsoluteFile()));
        bw.newLine();

        bm.forEach((s1, s2) -> {
           try {
               bw.write(s1.replaceAll("[^a-zA-Z ]", "") + "=" + s2);
               bw.newLine();
           } catch (IOException e) { e.printStackTrace(); }
        });
        bw.close();
    }

}