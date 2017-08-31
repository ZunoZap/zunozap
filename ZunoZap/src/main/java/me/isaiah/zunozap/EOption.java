package me.isaiah.zunozap;

import java.util.HashMap;

@Deprecated
public enum EOption {
    forceHTTPS(1), blockEventCalls(2), createPluginDataFolders(3), useDuckDuckGo(4), offlineStorage(5), JS(6);

    private final int i;
    private final static HashMap<Integer, EOption> map = new HashMap<>();

    private EOption(int v) {this.i = v;}

    public int getId() {return i;}

    public static EOption getById(int id){ return map.get(id); }

    static { for (EOption m : values()) map.put(m.getId(), m); }
}