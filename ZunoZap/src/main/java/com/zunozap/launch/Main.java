package com.zunozap.launch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.zunozap.Settings;
import com.zunozap.UniversalEngine.Engine;
import com.zunozap.ZunoAPI;
import com.zunozap.impl.ZunoZapChrome;
import com.zunozap.impl.ZunoZapWebView;

import javafx.embed.swing.JFXPanel;
import me.isaiah.downloadmanager.Download;

public class Main {

    // Info for chromium
    private static String ver = "6.23.1";
    private static String mvn = "https://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/";
    private static int i = 0;

    private static File lib = new File(new File(System.getProperty("user.home"), "zunozap"), "libs");

    public static void main(String[] args) {
        try { main0(args); } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main0(String[] args) throws IOException {
        new JFXPanel(); // initialize toolkit 

        lib.mkdirs();
        File file = new File(lib, getJarName().substring(getJarName().lastIndexOf("/")));
        File sfile = new File(lib, "jxbrowser-" + ver + ".jar");

        if ((file.exists() && sfile.exists()) && ((file.length() + sfile.length()) > 45000000)) {
            try {
                addURLs(file.toURI().toURL(), sfile.toURI().toURL());
            } catch (IOException e) { ZunoZapWebView.main(args); return; }
            try { start(args); } catch (IOException e) { e.printStackTrace(); }
            return;
        } else if (lib.listFiles() != null && lib.listFiles().length > 0) for (File fi : lib.listFiles()) fi.delete();

        Download smalljar = new Download(new URL(mvn + "jxbrowser/" + ver + "/jxbrowser-" + ver + ".jar"), lib);
        Download d = new Download(new URL(getJarName()), lib);

        JProgressBar pb = new JProgressBar(0, 150);
        JFrame f = new JFrame("ZunoZap");
        JLabel z = new JLabel("ZUNOZAP") {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                int k = (int)(getWidth()*(pb.getValue()/150.0f));
                g.setFont(new Font("Dialog", 0, 12));
                g.setColor(Color.ORANGE);
                g.fillRect(0, getHeight() - 5, k, 5);
                g.setColor(Color.WHITE);
                String str = formatSize(d.getDownloaded() + smalljar.getDownloaded() - 1) + 
                        " / " + formatSize(d.getSize() + smalljar.getSize()) + " [Status=" + Download.STATUSES[d.getStatus()] + "]";
                g.drawString("Updating libraries, Please wait.", (getWidth() / 2) - (3 * 32), 24);
                g.drawString(str, (getWidth() / 2) - (3 * str.length()), getHeight() - 15);
            }
        };
        z.setFont(new Font("Dialog", Font.BOLD, 56));
        z.setForeground(Color.WHITE);
        z.setBorder(new EmptyBorder(30,65,30,65));
        f.setUndecorated(true);
        f.setBackground(new Color(0,0,0,235));

        new Thread(() -> { Timer t = new Timer();t.schedule(new TimerTask() { @Override public void run() {
            pb.setValue((int) (d.getProgress() + (smalljar.getProgress() / 2)));
            z.repaint();

            if (d.getStatus() == 2 && i == 0) {
                i = 1;
                try {
                    addURLs(smalljar.getAsFile().toURI().toURL(), d.getAsFile().toURI().toURL());
                } catch (IOException e) { e.printStackTrace(); }
                t.cancel();
                f.dispose();
                try {
                    start(args);
                } catch (IOException e) { e.printStackTrace(); }
            }
        }}, 20, 500);}).start();

        f.setContentPane(z);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private static void start(String[] args) throws IOException {
        ZunoAPI.initTabPane();
        if (Settings.init(new File(new File(System.getProperty("user.home"), "zunozap"), "styles")) && ZunoAPI.en == Engine.WEBKIT) {
            ZunoZapWebView.main(args);
        } else ZunoZapChrome.main(args);
    }

    public static String formatSize(long v) {
        if (v < 1024) return (v > 0 ? v : 0) + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private static String getJarName() {
        String os = getOS().name().toLowerCase(Locale.ENGLISH);
        return mvn + "jxbrowser-" + os + "/" + ver + "/jxbrowser-" + os + "-" + ver + ".jar";
    }

    @Deprecated
    private static void addURLs(URL... u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) Main.class.getClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            for (URL ur : u) method.invoke(sysloader, (Object)ur);
        } catch (Throwable t) { throw new IOException("Could not add URL to system classloader"); }
    }

    private static OS getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows"))
            return System.getProperty("os.arch").indexOf("64") != -1 ? OS.WIN64 : OS.WIN32;
        if (os.startsWith("mac")) return OS.MAC;

        return OS.LINUX64;
    }

    private enum OS { WIN32, WIN64, LINUX64, MAC; }

}