package me.isaiah.zunozap;

import java.util.HashMap;

public enum EOption {
    forceHTTPS, blockEventCalls, createPluginDataFolders, useDuck, offlineStorage, JS;

    private final static HashMap<Integer, EOption> map = new HashMap<>();
    public boolean b;

    private EOption() {this.b = false;}

    public static EOption getById(int id){ return map.get(id - 1); }

    static { for (EOption m : values()) map.put(m.ordinal(), m); }
}