package me.isaiah.zunozap.lang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import me.isaiah.zunozap.Settings;

public class LangManager {

    public static String lang, full;

    public static void setLang(File f) throws IOException {
        List<String> l = Files.readAllLines(f.toPath());
        for (String s : l) {
            if (s.startsWith("#")) continue;
            String[] sp = s.split("=");
            try {
                Lang.valueOf(sp[0].trim()).tl = sp[1].trim();
            } catch (Exception e) { System.out.println("Unable to get translation " + e.getMessage());}
        }
        lang = f.getName().replace(".lang", "");
        full = l.get(0).replace("#lang=", "");
        Settings.setLang(f.getName());
    }

}