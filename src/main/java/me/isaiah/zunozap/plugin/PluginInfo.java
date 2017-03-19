package me.isaiah.zunozap.plugin;

public class PluginInfo {       
    public String name = null;
    public String description = "No Description";
    public String version = "1.0";

    public String[] getAllInfo() {
        return new String[] {name, version, description};
    }
}
