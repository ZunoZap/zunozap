package com.zunozap.launch;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class Agent {

    private static Instrumentation inst = null;
    public static void agentmain(final String a, final Instrumentation inst) { Agent.inst = inst; }

    public static boolean addClassPath(File...ur) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        try {
            if (!(cl instanceof URLClassLoader)) {
                for (File u : ur)
                    inst.appendToSystemClassLoaderSearch(new JarFile(u));
                return true;
            }

            Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            m.setAccessible(true);
            for (File u : ur) m.invoke(cl, (Object)u.toURI().toURL());
            return true;
        } catch (Throwable e) { e.printStackTrace(); return false; }
    }

}