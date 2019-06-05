package me.isaiah.zunozap.plugin.manager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarFile;

import me.isaiah.zunozap.ZFile;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.PluginInfo;

public class PluginManager {

    public ArrayList<PluginBase> plugins = new ArrayList<>();
    public ArrayList<String> names = new ArrayList<>();
    public PluginClassLoader classLoader;

    public void loadPlugins() {
        ZFile fold = new ZFile("plugins");
        fold.mkdir();
        for (File f : fold.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                Properties p = new Properties();
                try (JarFile jar = new JarFile(f)) {
                    InputStream stream = jar.getInputStream(jar.getJarEntry("plugin.txt"));
                    p.load(stream);
                    stream.close();
                    classLoader = new PluginClassLoader(new PluginLoader(), getClass().getClassLoader(), p.getProperty("mainClass"), f);
                    PluginBase plugin = classLoader.plugin;
                    PluginInfo info = plugin.getPluginInfo();
                    info.internal_reference = plugin;
                    plugins.add(plugin);
                    names.add(info.name);
                } catch (Exception e) { System.err.println(f.getName() + " is not a plugin: " + e.getMessage()); }
            }
        }
    }

}