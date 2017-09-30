package me.isaiah.zunozap;

import java.util.HashMap;

public enum EOption {
    forceHTTPS, blockEventCalls, createPluginDataFolders, useDuck, offlineStorage, JS;

    private final static HashMap<Integer, EOption> map = new HashMap<>();
    private boolean b;

    private EOption() {this.b = false;}

    public int getId() {return ordinal() + 1;}

    public boolean get() {return b;}
    public void set(boolean b) {this.b = b;};

    public static EOption getById(int id){ return map.get(id); }

    static { for (EOption m : values()) map.put(m.getId(), m); }
}