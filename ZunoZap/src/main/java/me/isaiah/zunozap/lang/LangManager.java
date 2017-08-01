package me.isaiah.zunozap.lang;

import java.util.Locale;

// TODO: finish
public class LangManager {
    public Locale sysdefault = Locale.getDefault();
    public Locale current = Locale.getDefault();

    public LangManager() {
        System.out.println("[LangManager] Starting LangManager v1.0");
    }

    public void change(Locale lang) {
        this.current = lang;
        download(lang);
    }
    
    private void download(Locale lang) {
    }
}
