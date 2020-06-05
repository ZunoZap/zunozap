package com.zunozap.launch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.zunozap.Engine.Type;
import com.zunozap.EngineHelper;
import com.zunozap.Settings;
import com.zunozap.ZunoAPI;
import com.zunozap.impl.BrowserImpl;
import com.zunozap.impl.ChromeEngine;
import com.zunozap.impl.WebKitEngine;

import javafx.embed.swing.JFXPanel;
import me.isaiah.downloadmanager.Download;

public class Main {

    // Maven info
    private static String ver = "6.24.3";
    private static String mvn = "https://maven.teamdev.com/repository/products/com/teamdev/jxbrowser/";
    private static int i = 0;

    public static final File lib = new File(new File(System.getProperty("user.home"), "zunozap"), "libs");

    public static void main(String[] args) throws IOException {
        if (os() == OS.LINUX64)
            linuxCheck();

        new JFXPanel(); // initialize toolkit 

        lib.mkdirs();
        File file = new File(lib, getJarName().substring(getJarName().lastIndexOf("/")));
        File sfile = new File(lib, "jxbrowser-" + ver + ".jar");

        if ((file.exists() && sfile.exists()) && ((file.length() + sfile.length()) > 45000000)) {
            if (!Agent.addClassPath(file, sfile)) {
                EngineHelper.setEngine(Type.WEBKIT, WebKitEngine.class);
                BrowserImpl.main(args);
                return;
            }
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
                Agent.addClassPath(smalljar.getAsFile(), d.getAsFile());
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
        Settings.init(new File(lib.getParentFile(), "styles"));

        if (Settings.en == Type.WEBKIT)
             EngineHelper.setEngine(Type.WEBKIT, WebKitEngine.class);
        else EngineHelper.setEngine(Type.CHROME, ChromeEngine.class);

        BrowserImpl.main(args);
    }

    public static String formatSize(long v) {
        if (v < 1024) return (v > 0 ? v : 0) + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private static String getJarName() {
        String os = os().name().toLowerCase(Locale.ENGLISH);
        return mvn + "jxbrowser-" + os + "/" + ver + "/jxbrowser-" + os + "-" + ver + ".jar";
    }

    public static void linuxCheck() {
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "OpenJFX is not installed!\nPlease install openjfx\nDebian/Ubuntu may also require Java 11 for openjfx", "ZunoZap", 0);
        }
    }

    public static OS os() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.indexOf("win") >= 0)
            return System.getProperty("os.arch").indexOf("64") != -1 ? OS.WIN64 : OS.WIN32;
        if (os.indexOf("mac") >= 0) return OS.MAC;

        return OS.LINUX64;
    }

    private enum OS { WIN32, WIN64, LINUX64, MAC; }

}