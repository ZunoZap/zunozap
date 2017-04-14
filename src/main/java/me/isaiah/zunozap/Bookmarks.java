package me.isaiah.zunozap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Coming soon!
 */
public class Bookmarks {
    HashMap<String, URL> map = new HashMap<String, URL>();
    ArrayList<String> names = new ArrayList<String>();

    /**
     * Adds a bookmark.
     */
    public void add(String name, URL url) {
        map.put(name, url);
        names.add(name);
    }
    
    /**
     * Adds a bookmark.
     */
    public void add(String name, String url) {
        try {
            map.put(name, new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        names.add(name);
    }

    /**
     * Removes all bookmarks.
     * Clearing the HashMap.
     */
    public void removeAll() {
        map.clear();
        names.clear();
    }
    
    /**
     * Removes bookmark.
     */
    public void remove(String name) {
        map.remove(name);
        names.remove(name);
    }
    
    public HashMap<String, URL> getMap() {
        return map;
    }
}
