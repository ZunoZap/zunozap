package com.zunozap.plugin.manager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarFile;

import com.zunozap.Settings;
import com.zunozap.ZFile;
import com.zunozap.api.Plugin;
import com.zunozap.api.PluginInfo;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PluginManager {

    public ArrayList<Plugin> plugins = new ArrayList<>();
    public ArrayList<String> names = new ArrayList<>();
    public PluginClassLoader classLoader;
    public double API_VERSION = 0.8;

    public void loadPlugins() {
        if (Settings.Options.DIS_PL.b)
            return;
        ZFile fold = new ZFile("plugins");
        fold.mkdir();
        for (File f : fold.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                Properties p = new Properties();
                try (JarFile jar = new JarFile(f)) {
                    InputStream stream = jar.getInputStream(jar.getJarEntry("plugin.txt"));
                    p.load(stream);
                    stream.close();
                    loadPlugin((classLoader = new PluginClassLoader(new PluginLoader(), getClass().getClassLoader(),
                            p.getProperty("mainClass"), f)).plugin);
                } catch (Exception e) { System.err.println("Could not load " + f.getName() + ": " + e.getMessage()); }
            }
        }
    }

    public boolean loadPlugin(Plugin p) throws Exception {
        PluginInfo i = p.getInfo();
        if (i.minBrowserVersion() > API_VERSION) {
            Alert alert = new Alert(AlertType.ERROR, "Unable to load addon '" + i.name() + "'.\nRequires upgraded browser!");
            alert.setTitle("Addons");
            alert.setHeaderText(null);
            alert.show();
            return false;
        }
        plugins.add(p);
        names.add(i.name());
        return true;
    }

}