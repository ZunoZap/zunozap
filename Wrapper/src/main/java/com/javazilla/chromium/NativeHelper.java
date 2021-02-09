package com.javazilla.chromium;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.javazilla.chromium.internal.Unzip;
import com.javazilla.util.ClassLoadingUtil;
import com.javazilla.util.Download;

public class NativeHelper {

    public static String CEF_PATH;

    public enum NativePlatform {
        LINUX32 ("84.3.8+gc8a556f",  "84.0.4147.105"),
        LINUX64 ("84.3.8+gc8a556f",  "84.0.4147.105"),
        WIN32   ("84.3.8+gc8a556f",  "84.0.4147.105"),
        WIN64   ("84.3.8+gc8a556f",  "84.0.4147.105"),
        //WIN64 ("87.1.12+g03f9336", "87.0.4280.88"),
        MACOSX64("84.3.8+gc8a556f",  "84.0.4147.105");

        public String VER;
        public String GHD;
        private NativePlatform(String a, String b) {
            VER = "v1.0.10-" + a + "+chromium-" + b;
            GHD = "https://github.com/jcefbuild/jcefbuild/releases/download/" + VER;
        }
    }

    public static NativePlatform getPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0)
            return System.getProperty("os.arch").indexOf("64") != -1 ? NativePlatform.WIN64 : NativePlatform.WIN32;
        if (os.indexOf("mac") >= 0) return NativePlatform.MACOSX64;
        return System.getProperty("os.arch").indexOf("64") != -1 ? NativePlatform.LINUX64 : NativePlatform.LINUX32;
    }

    public static final File lib = new File(new File(System.getProperty("java.io.tmpdir"), "zunozap"), "cef");
    public static File fold;
    public static boolean loaded;
    private static int i = 0;

    private static JProgressBar pb;
    private static JFrame f;
    private static JLabel z;
    private static Download d;

    public static Download getDownload() {
        return d;
    }

    public static void setupNatives(NativePlatform platform) {
        String url = platform.GHD + "/" + platform.name().toLowerCase() + ".zip";
        String arch = System.getProperty("sun.arch.data.model");
        if (arch.equals("32") && platform.name().contains("64")) {
            System.out.println("Can not load 64-bit natives on 32-bit JVM");
            System.exit(1);
        }

        lib.mkdirs();

        File file = new File(lib, platform.name().toLowerCase() + platform.VER + ".zip");
        fold = new File(lib, platform.name().toLowerCase() + platform.VER);

        if (fold.exists()) {
            setNativePath(fold, platform);
            i = 1;
            return;
        } else if (lib.listFiles() != null && lib.listFiles().length > 0) for (File fi : lib.listFiles()) delete(fi);

        d = new Download(url, file);

        boolean showGui = BrowserPreferences.showNHgui;
        if (showGui) {
            pb = new JProgressBar(0, 101);
            f = new JFrame("ZunoZap");
            z = new JLabel(BrowserPreferences.getBrand()) {
                private static final long serialVersionUID = 1L;
    
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    int k = (int)(getWidth()*(pb.getValue()/100.0f));
                    g.setFont(new Font("Dialog", 0, 12));
                    g.setColor(Color.ORANGE);
                    g.fillRect(0, getHeight() - 5, k, 5);
                    g.setColor(Color.WHITE);
                    String str = formatSize(d.downloaded - 1) + 
                            " / " + formatSize(d.size) + " [Status=" + d.status + "]";
                    g.drawString("Updating libraries, Please wait.", (getWidth() / 2) - (3 * 32), 24);
                    g.drawString(str, (getWidth() / 2) - (3 * str.length()), getHeight() - 15);
                    pb.setValue((int)(((float)d.downloaded / (float)d.size) * 100));
                    repaint();
                }
            };
            z.setFont(new Font("Dialog", Font.BOLD, 56));
            z.setForeground(Color.WHITE);
            z.setBorder(new EmptyBorder(30,65,30,65));
            f.setUndecorated(true);
            f.setBackground(new Color(0,0,0,235));
            f.setContentPane(z);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setVisible(true);
        }

        new Thread(() -> { 
            Timer t = new Timer();
            t.schedule(new TimerTask() { 
                @Override public void run() {
                    if (showGui) {
                        pb.setValue((int)((float) d.downloaded / d.size) * 100);
                        z.repaint();
                    }
        
                    if (d.status == 2 && i == 0) {
                        Unzip unzip = new Unzip(file, fold);
                        try {
                            unzip.execute();
                        } catch (IOException e) {e.printStackTrace();}
                        setNativePath(fold, platform);
        
                        i = 1;
                        t.cancel();
                        if (showGui) f.dispose();
                    }
        }}, 20, 500);}).start();
    }

    private static void setNativePath(File f, NativePlatform plat) {
        System.out.println("SET N");
        File bin = new File(new File(f, "java-cef-build-bin"), "bin");
        File lib = new File(bin, "lib");
        File natives = new File(lib, plat.name().toLowerCase());
        System.out.println(natives);
        if (natives.exists()) CEF_PATH = natives.getAbsolutePath();
        try {
            System.out.println(CEF_PATH);
            addLibraryDir(CEF_PATH);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (File file : bin.listFiles()) {
            if (!file.getName().endsWith(".jar")) continue;
            try {
                ClassLoadingUtil.appendToURLClassPath(file.toURI().toURL());
            } catch (MalformedURLException e) {e.printStackTrace();}
        }
        loaded = true;
    }

    private static void addLibraryDir(String libraryPath) throws Exception {
        Field userPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        userPathsField.setAccessible(true);
        String[] paths = (String[]) userPathsField.get(null);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            if (libraryPath.equals(paths[i])) continue;
            sb.append(paths[i]).append(File.pathSeparatorChar);
        }
        sb.append(libraryPath);
        System.setProperty("java.library.path", sb.toString());
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }

    private static void delete(File f) {
        if (null != f.listFiles())
        for (File fi : f.listFiles())
            if (fi.isDirectory()) delete(fi); else fi.delete();
        f.delete();
    }

    public static void blockTillDownloaded() {
        while (i == 0)
            try {Thread.sleep(1000);} catch (InterruptedException e){}
    }

    private static String formatSize(long v) {
        if (v < 1024) return (v > 0 ? v : 0) + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

}