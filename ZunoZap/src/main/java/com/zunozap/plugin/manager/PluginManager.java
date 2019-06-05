package com.zunozap.plugin.manager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarFile;

import com.zunozap.ZFile;
import com.zunozap.api.Plugin;
import com.zunozap.api.PluginInfo;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PluginManager {

    public ArrayList<Plugin> plugins = new ArrayList<>();
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
                    Plugin plugin = classLoader.plugin;
                    PluginInfo i = plugin.getInfo();
                    if (i.minBrowserVersion() > 0.8) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Addons");
                        alert.setHeaderText(null);
                        alert.setContentText("Unable to load addon '" + i.name() + "'.\nRequires upgraded browser!");

                        alert.show();
                        throw new Exception("Requires newer browser");
                    }
                    plugins.add(plugin);
                    names.add(i.name());
                } catch (Exception e) { System.err.println("Couldnt load " + f.getName() + ": " + e.getMessage()); }
            }
        }
    }

}