package me.isaiah.zunozap.plugin;

import java.util.ArrayList;

public class PluginInfo {
    public String name = null;
    public String description = "None";
    public String version = "1.0";
    public String minBrowserVersion = "0.1.0";
    public ArrayList<Object> optionalData = new ArrayList<>();
    public PluginBase internal_reference = null;

    public String[] getAllInfo() {
        return new String[] {name, version, description, minBrowserVersion};
    }

    public PluginInfo(){}

    public PluginInfo(String name, String des, String version) {
        this.name = name;
        this.description = des;
        this.version = version;
    }
}