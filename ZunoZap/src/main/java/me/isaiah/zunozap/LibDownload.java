package me.isaiah.zunozap;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import me.isaiah.downloadmanager.Download;
import me.isaiah.zunozap.UniversalEngine.Engine;

/**
 * Download JxBrowser libraries & launch
 */
public class LibDownload {
    private static String ver = "6.18";
    private static File lib = new File(ZunoAPI.home, "libs");
    
    public static void main(String[] args) {
        try { main0(args); } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main0(String[] args) throws IOException {
        if (Settings.initMenu()) {
            if (ZunoAPI.en == Engine.WEBKIT) {
                ZunoZapWebView.main(args);
                return;
            }
        }
        lib.mkdirs();
        File file = new File(lib, getJarName().substring(getJarName().lastIndexOf("/")));
        File sfile = new File(lib, "jxbrowser-" + ver + ".jar");

        File fileT = new File(lib, getJarName().substring(getJarName().lastIndexOf("/")).replace(".jar", ".temp"));
        File sfileT = new File(lib, "jxbrowser-" + ver + ".temp");

        if (file.exists() && sfile.exists()) {
            try {
                addURLs(file.toURI().toURL(), sfile.toURI().toURL());
            } catch (IOException e) { ZunoZapWebView.main(args); return; }
            try { ZunoZap.main(args); } catch (IOException e) {}
            return;
        } else if (lib.listFiles() != null && lib.listFiles().length > 0) for (File fi : lib.listFiles()) fi.delete();

        JFrame f = new JFrame("ZunoZap Installer");
        Download smalljar = new Download(new URL("http://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/jxbrowser/" + ver + "/jxbrowser-" + ver + ".jar"), lib);
        Download d = new Download(new URL(getJarName()), lib);
        JProgressBar pb0 = new JProgressBar();
        JProgressBar pb = new JProgressBar();
        JTextField txt = new JTextField("\tDownloading required Chromium libaries");
        JTextField txt2 = new JTextField();
        new Thread(() -> { Timer t = new Timer();t.schedule(new TimerTask() { @Override public void run() {
            pb.setValue((int)d.getProgress());
            pb0.setValue((int)smalljar.getProgress());
            txt2.setText(String.format("\tDownloaded %s out of %s", formatSize(d.getDownloaded() + smalljar.getDownloaded() - 1), formatSize(d.getSize() + smalljar.getSize()))
            + "  |  Status: " + Download.STATUSES[d.getStatus()]);
            if (d.getStatus() == 2) {
                fileT.renameTo(file);
                sfileT.renameTo(sfile);
                try {
                    addURLs(smalljar.getAsFile().toURI().toURL(), d.getAsFile().toURI().toURL());
                } catch (IOException e) { e.printStackTrace(); }
                t.cancel();
                f.dispose();
                try {
                    t.cancel();
                    ZunoZap.main(args);
                } catch (IOException e) { try {
                    ZunoZapWebView.main(args);
                } catch (IOException e1) { e1.printStackTrace(); }}
            }
        }}, 10, 400);}).start();
        JPanel p = new JPanel();
        pb0.setStringPainted(true);
        pb.setStringPainted(true);
        pb0.setString(smalljar.getUrl().substring(smalljar.getUrl().lastIndexOf("/")));
        pb.setString(d.getUrl().substring(d.getUrl().lastIndexOf("/")));
        p.add(txt);
        p.add(txt2);
        p.add(pb);
        p.add(pb0);
        txt.setEditable(false);
        txt2.setEditable(false);
        f.setPreferredSize(new Dimension(500,200));
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
        String os = getOS().name().toLowerCase(Locale.ENGLISH);
        return "http://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/jxbrowser-" + os + "/" + ver + "/jxbrowser-" + os + "-" + ver + ".jar";
    }

    private static void addURLs(URL... u) throws IOException {
        Thread.currentThread().setContextClassLoader(new URLClassLoader(u));
    }

    private static OS getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) return OS.WIN;
        if (os.startsWith("mac")) return OS.MAC;

        return OS.LINUX64;
    }
    private enum OS { WIN, LINUX64, MAC; }
}