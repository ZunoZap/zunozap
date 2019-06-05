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

    public HashMap<String, String> bm = ZunoAPI.getInstance().bm;
    private Menu book;

    public Reader(Menu bk) {
        File dat = new ZFile("bookmarks.dat", false);
        this.book = bk;
        bk.getItems().clear();
        try {
            for (String s : Files.readAllLines(Paths.get(dat.toURI()))) {
                if (!s.startsWith("#")) {
                    String key = s.substring(0, s.indexOf("="));
                    String value = s.substring(s.indexOf("=") + 1);
                    if (!bm.containsKey(key)) bm.put(decode(key, 12), decode(value, 12));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void readd() {
        bm.forEach((a, b) -> {
            MenuItem item = new MenuItem(a);
            item.setOnAction(t -> ZunoAPI.getInstance().createTab(false, b));
            book.getItems().add(item);
        });
    }

    public void refresh() throws IOException {
        ZFile dat = new ZFile("bookmarks.dat", false);
        BufferedWriter bw = new BufferedWriter(new FileWriter(dat.getAbsoluteFile()));
        bw.write("# dont edit");
        bw.newLine();

        bm.forEach((s1, s2) -> {
           try {
               bw.write(encode(s1 + "=" + s2, 12));
               bw.newLine();
           } catch (IOException e) { e.printStackTrace(); }
        });
        bw.close();
    }

    public static String decode(String enc, int offset) {
        return encode(enc, 26-offset);
    }

    public static String encode(String enc, int offset) {
        offset = offset % 26 + 26;
        StringBuilder encoded = new StringBuilder();
        for (char i : enc.toCharArray()) {
            if (Character.isLetter(i)) {
                if (Character.isUpperCase(i)) encoded.append((char) ('A' + (i - 'A' + offset) % 26 ));
                else encoded.append((char) ('a' + (i - 'a' + offset) % 26 ));
            } else encoded.append(i);
        }
        return encoded.toString();
    }

}