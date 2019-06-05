package me.isaiah.zunozap.plugin.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.isaiah.zunozap.plugin.PluginBase;

final class PluginClassLoader extends URLClassLoader {

    private final PluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<>();
    public PluginBase plugin;

    PluginClassLoader(final PluginLoader loader, final ClassLoader parent, final String name, final File file) throws Exception {
        super(new URL[] {file.toURI().toURL()}, parent);

        this.loader = loader;
        try {
            Class<? extends PluginBase> clazz;
            try {
                clazz = Class.forName(name, true, this).asSubclass(PluginBase.class);
            } catch (ClassCastException | ClassNotFoundException e) { 
                throw new Exception("Class " + name + " does not exist or not subclass of PluginBase", e); }

            plugin = clazz.newInstance();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("me.isaiah.zunozap")) throw new ClassNotFoundException(name);

        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) result = loader.getClassByName(name);

            if (result == null) {
                result = super.findClass(name);

                if (result != null) loader.setClass(name, result);
            }

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }

}