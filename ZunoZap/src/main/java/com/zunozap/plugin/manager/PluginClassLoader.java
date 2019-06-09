package com.zunozap.plugin.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.zunozap.api.Plugin;

final class PluginClassLoader extends URLClassLoader {

    private final PluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<>();
    public Plugin plugin;
    
    static {
        ClassLoader.registerAsParallelCapable();
    }

    PluginClassLoader(final PluginLoader loader, final ClassLoader parent, final String name, final File file) throws Exception {
        super(new URL[] {file.toURI().toURL()}, parent);

        this.loader = loader;
        try {
            Class<? extends Plugin> clazz;
            try {
                clazz = Class.forName(name, true, this).asSubclass(Plugin.class);
            } catch (ClassCastException | ClassNotFoundException e) { 
                throw new Exception("Class " + name + " does not exist or not subclass of Plugin", e); }

            plugin = clazz.newInstance();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }
    
    protected Class<?> findClass(String name, boolean c) throws ClassNotFoundException {
        try {
            return findClass0(name, true);
        } catch (Exception | NoClassDefFoundError e) { return null; }
    }

    Class<?> findClass0(String name, boolean checkGlobal) throws Exception {
        if (name.startsWith("com.zunozap")) throw new ClassNotFoundException("Locked name: " + name);

        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) result = loader.getClassByName(name);

            if (result == null)
                if ((result = super.findClass(name)) != null) loader.setClass(name, result);

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }

}