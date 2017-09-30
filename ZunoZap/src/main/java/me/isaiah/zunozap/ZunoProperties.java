package me.isaiah.zunozap;

import java.util.Locale;
import java.util.Properties;

public class ZunoProperties extends Properties {
    private static final long serialVersionUID = 1L;

    public void set(String s, boolean b) {
        super.setProperty(s, String.valueOf(b));
    }

    public boolean get(String s) {
        return String.valueOf(super.get(s)).toLowerCase(Locale.ENGLISH).contains("true");
    }
    
    public String getString(String s) {
        return (String) super.get(s);
    }
}