package me.isaiah.zunozap;

import java.util.HashMap;

public enum EOption {
    forceHTTPS(1),
    blockEventCalls(2),
    createPluginDataFolders(3),
    useDuckDuckGo(4);

    private final int i;
    private final static HashMap<Integer, EOption> BY_ID = new HashMap<Integer, EOption>();

    private EOption(int value){
        this.i = value;
    }

    public int getValue(){
        return i;
    }

    public static EOption getByValue(int value){
        return BY_ID.get(value);
    }

    static {
        for (EOption mode : values()) BY_ID.put(mode.getValue(), mode);
    }
}