package me.isaiah.zunozap;

import java.util.HashMap;

public enum EOption {
    forceHTTPS("Force HTTPS", false), blockEventCalls("Block plugin events", false), 
    createPluginDataFolders("Create plugin folders", true), onTheDuckSide("Use DuckDuckGO", true), 
    offlineStorage("Store web pages for offline browsing", false), javascript(true),
    blockMalware("Block Malware sites", true);

    private final static HashMap<Integer, EOption> map = new HashMap<>();
    public boolean b;
    public boolean def;
    public String n;

    private EOption(boolean d) { this.b = d; this.def = d; this.n = toString(); }
    private EOption(String n, boolean d) { this.b = d; this.def = d; this.n = n; }

    public static EOption getById(int id){ return map.get(id - 1); }

    static { for (EOption m : values()) map.put(m.ordinal(), m); }
}