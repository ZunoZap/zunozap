package com.zunozap.lang;

import static com.zunozap.Log.err;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.zunozap.Settings;

public class LangManager {

    public static String lang, full;

    public static void setLang(File f) throws IOException {
        List<String> l = Files.readAllLines(f.toPath());
        for (String s : l) {
            String[] sp = s.split("=");
            if (s.startsWith("#") || sp[0].trim().length() < 1) continue;
            try {
                Lang.valueOf(sp[0].trim()).tl = sp[1].trim();
            } catch (Exception e) { err("Unable to get translation " + e.getMessage()); }
        }
        lang = f.getName().replace(".lang", "");
        full = l.get(0).replace("#lang=", "");
        Settings.setLang(f.getName());
    }

}