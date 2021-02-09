// Copyright (C) 2020 - under MIT
package com.javazilla.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import sun.misc.Unsafe;

public class ClassLoadingUtil {

    public static void appendToURLClassPath(URL url) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        try {
            Field f = cl.getClass().getDeclaredField("ucp");
            setAccessible(f);
            Object o = f.get(cl);
            Method m = o.getClass().getMethod("addURL", URL.class);
            setAccessible(m);
            m.invoke(o, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getField(Class<?> klass,String name, boolean superF) {
        while (superF && klass.getSuperclass() != Object.class) klass = klass.getSuperclass();

        try {
            Field f = klass.getDeclaredField(name);
            f.setAccessible(true);
            return superF ? f : f.get(null);
        } catch (Exception ex) {return null;}
    }

    // Very cut down version of nqzero's Permit Reflect
    // see https://github.com/nqzero/permit-reflect
    public static final Unsafe uu = (Unsafe) getField(Unsafe.class, "theUnsafe", false);
    public static void setAccessible(AccessibleObject accessor) throws RuntimeException {
        if (offset == -99) offset = uu.objectFieldOffset((Field)getField(Fake.class,"override",true));
        uu.putBoolean(accessor, offset, true);
    }
    public class Fake {boolean override;}
    public static long offset = -99;

}