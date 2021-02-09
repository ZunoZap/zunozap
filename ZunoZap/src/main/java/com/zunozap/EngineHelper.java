package com.zunozap;

public class EngineHelper {

    public enum EngineType {
        WEBKIT("com.zunozap.impl.WebViewEngine", "WebView"),
        CHROME("com.zunozap.impl.CefEngine", "Chromium 84");

        private String clazz;
        public String display;
        private EngineType(String clazz, String display) {
            this.clazz = clazz;
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public static Class<?> engine;
    public static EngineType type;

    public static void setEngine(EngineType t) {
        try {
            engine = Class.forName(t.clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        type = t;
    }

    @SuppressWarnings("deprecation")
    public static Engine newBrowser(String url) {
        try {
            return (Engine) engine.getConstructor(String.class).newInstance(url);
        } catch (Exception e) {
            e.printStackTrace();
            return new com.zunozap.impl.WebViewEngine(url);
        }
    }

}