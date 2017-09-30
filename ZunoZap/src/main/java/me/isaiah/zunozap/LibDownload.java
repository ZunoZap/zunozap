package me.isaiah.zunozap;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import me.isaiah.downloadmanager.Download;

/**
 * Download JxBrowser libraries for operating system.
 */
public class LibDownload {
    private static String ver = "6.15";
    private static File lib = new File(ZunoAPI.home, "libs");
    public static void main(String[] args) throws MalformedURLException {
        lib.mkdirs();
        JFrame f = new JFrame("ZunoZap Installer");
        File file = new File(lib, getJarName().substring(getJarName().lastIndexOf("/")));
        File sfile = new File(lib, "jxbrowser-" + ver + ".jar");
        
        File fileT = new File(lib, getJarName().substring(getJarName().lastIndexOf("/")).replace(".jar", ".temp"));
        File sfileT = new File(lib, "jxbrowser-" + ver + ".temp");

        if (file.exists() && sfile.exists()) {
            try {
                addURL(sfile.toURI().toURL());
                addURL(file.toURI().toURL());
            } catch (IOException e) { e.printStackTrace(); }
            try {
                f.dispose();
                ZunoZap.main(args);
            } catch (IOException e) { e.printStackTrace(); }
            return;
        }
        Download smalljar = new Download(new URL("http://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/jxbrowser/" + ver + "/jxbrowser-" + ver + ".jar"), lib);
        Download d = new Download(new URL(getJarName()), lib);
        JProgressBar bar0 = new JProgressBar();
        JProgressBar bar = new JProgressBar();
        String s = "\tDownloading required Chromium libaries";
        String a = "\tDownloaded %s out of %s";
        JTextField text = new JTextField(s);
        JTextField text2 = new JTextField();
        new Thread(() -> { Timer t = new Timer();t.schedule(new TimerTask() { @Override public void run() {
            bar.setValue((int)d.getProgress());
            bar0.setValue((int)smalljar.getProgress());
            text2.setText(String.format(a, formatSize(d.getDownloaded() + smalljar.getDownloaded() - 1), formatSize(d.getSize() + smalljar.getSize()))
            + "  |  Status: " + Download.STATUSES[d.getStatus()]);
            if (d.getStatus() == 2) {
                fileT.renameTo(file);
                sfileT.renameTo(sfile);
                try {
                    addURL(smalljar.getAsFile().toURI().toURL());
                    addURL(d.getAsFile().toURI().toURL());
                } catch (IOException e) { e.printStackTrace(); }
                try {
                    t.cancel();
                    f.dispose();
                    ZunoZap.main(args);
                } catch (IOException e) { e.printStackTrace(); }
            }
        }}, 10, 400);}).start();
        JPanel p = new JPanel();
        bar0.setStringPainted(true);
        bar.setStringPainted(true);
        bar0.setString(smalljar.getUrl().substring(smalljar.getUrl().lastIndexOf("/")));
        bar.setString(d.getUrl().substring(d.getUrl().lastIndexOf("/")));
        p.add(text);
        p.add(text2);
        p.add(bar);
        p.add(bar0);
        text.setEditable(false);
        text2.setEditable(false);
        f.setPreferredSize(new Dimension(500,200));
        f.setTitle("ZunoZap Installer");
        f.setContentPane(p);
        f.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        f.pack();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private static String getJarName() {
        switch (getOS()) {
            case WINDOWS:
                return "http://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/jxbrowser-win/" + ver + "/jxbrowser-win-" + ver + ".jar";
            case LINUX:
                return "http://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/jxbrowser-linux64/" + ver + "/jxbrowser-linux64-" + ver + ".jar";
            case MAC:
                return "http://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/jxbrowser-mac/" + ver + "/jxbrowser-mac-" + ver + ".jar";
            default:
                return "http://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/jxbrowser-win/" + ver + "/jxbrowser-win-" + ver + ".jar";
        }
    }

    private static void addURL(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) LibDownload.class.getClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, u);
        } catch (Throwable t) {
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    private static OS getOS() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("windows")) return OS.WINDOWS;
        if (os.toLowerCase().startsWith("mac")) return OS.MAC;

        return OS.LINUX;
    }
    private enum OS { WINDOWS, LINUX, MAC; }
}
