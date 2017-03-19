package me.isaiah.zunozap.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.isaiah.zunozap.ZunoZap;

public class PluginManager {
    public ArrayList<PluginBase> plugins = new ArrayList<PluginBase>();
    public ArrayList<String> pluginNames = new ArrayList<String>();
    public PluginClassLoader classLoader;

    public void loadPlugins() {
        try {
            loadPlugs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws Exception
     * @author Isaiah - I FINALY GOT IT WORKING!
     */
    public void loadPlugs() throws Exception {
        System.out.println("Loading Plugins ...");
        File pluginFolder = new File(ZunoZap.homeDir, "plugins");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        for (File f : pluginFolder.listFiles()) {
            if ((!f.isDirectory()) && (f.getName().endsWith(".jar"))) {
                Properties p = new Properties();

                JarFile jar = new JarFile(f);
                JarEntry entry = jar.getJarEntry("plugin.txt");
                InputStream stream = jar.getInputStream(entry);
                p.load(stream);
                jar.close();
                stream.close();

                classLoader = new PluginClassLoader(new PluginLoader(), getClass().getClassLoader(), p.getProperty("mainClass"), f);

                PluginBase plugin = classLoader.plugin;
                PluginInfo info = plugin.getPluginInfo();

                if (info == null) {
                    info = new PluginInfo();
                    info.name = f.getName().replace(".jar", "");
                }

                plugins.add(plugin);
                pluginNames.add(f.getName().replace(".jar", ""));
            }
        }
        System.out.println(String.format("Found %s plugins: %s", pluginNames.size(), pluginNames));
    }
}