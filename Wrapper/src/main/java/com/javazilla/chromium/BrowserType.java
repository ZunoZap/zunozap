package com.javazilla.chromium;

public enum BrowserType {

    HEAVYWEIGHT,
    LIGHTWEIGHT;

    public static BrowserType getDefault() {
        return LIGHTWEIGHT;
    }

}