package me.isaiah.zunozap;

import java.util.HashMap;

public enum EOption {
    forceHTTPS(1), blockEventCalls(2), createPluginDataFolders(3), useDuckDuckGo(4), offlineStorage(5);

    private final int i;
    private final static HashMap<Integer, EOption> map = new HashMap<>();

    private EOption(int v) {
        this.i = v;
    }
    public int getValue(){return i;}

    public static EOption getById(int value) {
        return map.get(value);
    }

    static {
        for (EOption m : values())
            map.put(m.getValue(), m);
    }
}