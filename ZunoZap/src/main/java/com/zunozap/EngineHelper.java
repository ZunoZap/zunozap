package com.zunozap;

import com.zunozap.Engine.Type;

public class EngineHelper {

    public static Class<?> engine;
    public static Type type;

    public static void setEngine(Type t, Class<?> clazz) {
        engine = clazz;
        type = t;
    }

    public static Engine newBrowser() {
        try {
            return (Engine) engine.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}