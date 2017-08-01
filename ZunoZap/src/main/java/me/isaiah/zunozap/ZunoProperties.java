package me.isaiah.zunozap;

import java.util.Properties;

public class ZunoProperties extends Properties {
    private static final long serialVersionUID = 1L;
    
    public void setProperty(String s, boolean b) {
        super.setProperty(s, String.valueOf(b));
    }
    
    public boolean getBoolean(String s) {
        return String.valueOf(super.get(s)).contains("true");
    }

}
