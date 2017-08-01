package me.isaiah.zunozap.plugin.manager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.isaiah.zunozap.ZunoZap;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.PluginInfo;

public class PluginManager {
    public ArrayList<PluginBase> plugins = new ArrayList<>();
    public ArrayList<String> pluginNames = new ArrayList<>();
    public PluginClassLoader classLoader;

    public void loadPlugins() {
        System.out.println("Loading Plugins...");
        File pluginFolder = new File(ZunoZap.homeDir, "plugins");
        if (!pluginFolder.exists()) pluginFolder.mkdir();
        for (File f : pluginFolder.listFiles()) {
            if ((!f.isDirectory()) && (f.getName().endsWith(".jar"))) {
                Properties p = new Properties();
                try (JarFile jar = new JarFile(f)) {
                    JarEntry entry = jar.getJarEntry("plugin.txt");
                    InputStream stream = jar.getInputStream(entry);
                    p.load(stream);
                    stream.close();
                    classLoader = new PluginClassLoader(new PluginLoader(), getClass().getClassLoader(), p.getProperty("mainClass"), f);
                    PluginBase plugin = classLoader.plugin;
                    PluginInfo info = plugin.getPluginInfo();

                    if (info == null) {
                        info = new PluginInfo();
                        info.name = f.getName().replace(".jar", "");
                    }
                    info.internal_reference = plugin;
                    plugins.add(plugin);
                    pluginNames.add(info.name);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Cant load plugin from jar " + f.getName());
                }
            }
        }
        System.out.println("Found " + pluginNames.size() + " plugins: " + pluginNames.toString());
    }
}