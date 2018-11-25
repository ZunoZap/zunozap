package me.isaiah.zunozap;

public class ZunoProperties extends java.util.Properties {

    private static final long serialVersionUID = 1L;

    public void set(String s, boolean b) {
        setProperty(s, String.valueOf(b));
    }

    public boolean get(String s) {
        return Boolean.valueOf(String.valueOf(super.get(s)));
    }

    public String getStr(String s) { return (String) super.get(s); }

}