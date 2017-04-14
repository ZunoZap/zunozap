package me.isaiah.zunozap.plugin.manager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class PluginLoader {
    private final Pattern[] fileFilters = new Pattern[] {Pattern.compile("\\.jar$"), };
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private final Map<String, PluginClassLoader> loaders = new LinkedHashMap<String, PluginClassLoader>();
    public Pattern[] getPluginFileFilters(){return fileFilters.clone();}

    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (String current : loaders.keySet()) {
                PluginClassLoader loader = loaders.get(current);

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (cachedClass != null) return cachedClass;
            }
        }
        return null;
    }

    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) classes.put(name, clazz);
    }
}