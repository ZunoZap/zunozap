package com.zunozap.lang;

import java.util.ArrayList;

public enum Lang {

    HTTPS, DIS_PL, OFFLINE, MAL, LOAD, GO, NO_PL, CLEAR_OFFLNE, SETT, UPDATE_CHECK, ABOUT, LANG, COMPACT;

    public String tl;
    public static ArrayList<ChangeLis> l2 = new ArrayList<>();

    public static final void a(ChangeLis a) { l2.add(a); }

    public static final void b(ChangeLis a) {
        a(a);
        a.a();
    }

}