package com.zunozap.launch;

import com.zunozap.ZunoZap;

public class Main {

    public static void main(String[] args) {
        FxGetter.test();
        try {
            ZunoZap.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}