package me.isaiah.zunozap;

import java.util.HashMap;

@Deprecated
public class Bookmarks {
    public static void add(String name, String url) {
        ZunoZap.bm.put(name, url);
    }

    public static void remove(String name) {
        ZunoZap.bm.remove(name);
    }

    public static void clear() {
        ZunoZap.bm.clear();
    }

    public static HashMap<String, String> getMap() {
        return ZunoZap.bm;
    }
}
