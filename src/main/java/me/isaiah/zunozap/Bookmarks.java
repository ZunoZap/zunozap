package me.isaiah.zunozap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Bookmarks {
    public HashMap<String, URL> map = new HashMap<String, URL>();
    public ArrayList<String> names = new ArrayList<String>();
    public ArrayList<String> registered = new ArrayList<String>();

    /**
     * Adds a bookmark.
     */
    public void add(String name, URL url){
        if (!names.contains(name)){
            map.put(name, url);
            names.add(name);
            registered.add(name);
        }
    }

    /**
     * Adds a bookmark.
     */
    public void add(String name, String url){
        try {
            map.put(name, new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        names.add(name);
        registered.add(name);
    }

    /**
     * Removes all bookmarks.
     */
    public void removeAll(){
        map.clear();
        names.clear();
        registered.clear();
    }

    /**
     * Removes bookmark.
     */
    public void remove(String name){
        if (names.contains(name)){
            map.remove(name);
            names.remove(name);
            registered.remove(name);
        } else {
            System.err.println("The bookmark " + name + " does not exist.");
        }
    }

    public HashMap<String, URL> getMap(){
        return map;
    }
    
    public ArrayList<String> get() {
        return new ArrayList<String>(registered);
    }
}
