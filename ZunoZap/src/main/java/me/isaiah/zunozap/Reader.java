package me.isaiah.zunozap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.control.MenuItem;

public class Reader {
    public Reader() throws IOException {
        File dat = new File(ZunoZap.homeDir, "bookmarks.dat");
        if (!dat.exists()) dat.createNewFile();
        ZunoZap.menuBook.getItems().clear();
        for (String s : Files.readAllLines(Paths.get(dat.toURI()))) {
            if (!s.startsWith("#")) {
                String key = s.substring(0, s.indexOf("="));
                String value = s.substring(s.indexOf("=") + 1);
                if (!ZunoZap.bm.containsKey(key)) ZunoZap.bm.put(decode(key, 12), decode(value, 12));
            }
        }
    }
    
    public void readd() {
        ZunoZap.bm.forEach((s1, s2) -> {
            MenuItem item = new MenuItem(s1);
            item.setOnAction((t) -> { ((ZunoZap) ZunoZap.getInstance()).createTab(false, s2); });
            ZunoZap.menuBook.getItems().add(item);
        });
    }
    
    public void refresh() throws IOException {
        File dat = new File(ZunoZap.homeDir, "bookmarks.dat");
        if (!dat.exists()) dat.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(dat.getAbsoluteFile()));
        bw.write("# Bookmark storage data. DO NOT EDIT!");
        bw.newLine();

        ZunoZap.bm.forEach((s1, s2) -> {
           try {
               bw.write(encode(s1 + "=" + s2, 12));
               bw.newLine();
           } catch (IOException e) {
               e.printStackTrace();
           }
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
                if (Character.isUpperCase(i)) {
                    encoded.append((char) ('A' + (i - 'A' + offset) % 26 ));
                } else encoded.append((char) ('a' + (i - 'a' + offset) % 26 ));
            } else encoded.append(i);
        }
        return encoded.toString();
    }
}
