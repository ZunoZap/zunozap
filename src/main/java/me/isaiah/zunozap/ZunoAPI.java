package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class ZunoAPI extends Application {
    public String name;
    public static String version;
    public static boolean blockPluginEvents = false;
    public static boolean createPluginDataFolders = true;
    public static boolean useDuck = true;
    public static boolean offlineStorage = false;
    public static File stylesheet = null;
    public static String styleName = "None";
    private static double totalRamGCsaved = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public final String aboutPageHTML() {
        return "<header><h1>About ZunoZap</h1></header>"
                +"<body>"
                + "    %s is a web browser made with the Java WebView,</p><br>"
                +"    Version: "+getVersion()+"<br>"
                +"    UserAgent: %s<br>"
                +"    Java Enabled: true<br>"
                +"    JavaScript Enabled: %s<br>"
                +"    Licence: <a href='https://raw.githubusercontent.com/%s'>%s</a>"
                +"    <hr>"
                +"</body>";
    }

    public static void setUserAgent(WebEngine e) {
        if (!e.getUserAgent().contains("ZunoZap"))
            e.setUserAgent(e.getUserAgent() + " ZunoZap/0.1.0 Chrome/53.0.2785.148");
    }

    /**
     * Set the style of multiple {@link javafx.scene.control.Button Button}s at one time.
     */
    public final static void setStyle(String css, Button...btns) {for (Button b : btns){b.setStyle(css);}}
    /**
     * Change the browser history.
     */
    public final static void history(WebEngine e, String go) {e.executeScript("history."+go+"();");}
    /*Load Page*/
    public static final void loadSite(String url, WebEngine e) {
        if (url.startsWith("zunozap:")) {
            if (url.substring(8).startsWith("update"))
                e.loadContent(Updater.browser(version, url));
            else if (url.substring(8).startsWith("home")) e.load("https://zunozap.github.io/");

            return;
        }

        if ((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", "")))) {
            if (!useDuck) e.load("https://google.com/search?q=" + url.replace(" ", "%20"));
            else e.load("https://duckduckgo.com/?q=" + url.replace(" ", "%20")); 

            return;
        }

        if (forceHTTPS) e.load(url.startsWith("http") ? url : "https://" + url);
        else e.load(url.startsWith("http") ? url : "http://" + url);
    }
    public static boolean forceHTTPS = false;
    public int tabnum = 0;

    public static String getUrlSource(String url) {
        try {
            URLConnection urlc = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder a = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                a.append(inputLine);
            in.close();
        return a.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static void DownloadPage(File dp, File temp, WebEngine w) {
        try{       
            URLConnection urlc = new URL(w.getLocation()).openConnection();
            File htmlsrc = new File(temp, w.getLocation().replaceAll("[ : / . ]", "-").trim() + ".html");
            if (!htmlsrc.exists()) {
                htmlsrc.createNewFile();
            }

            FileWriter fw = new FileWriter(htmlsrc.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            System.out.println(w.getLocation().trim());
            bw.write("<!--" + w.getLocation().trim() + "-->");
            bw.newLine();

            // URLConnection urlc = new URL(w.getLocation()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                bw.write(inputLine.trim());
                bw.newLine();
            }
            in.close();
            bw.close();
            System.out.println("Downloaded " + w.getLocation().trim());
            if (htmlsrc.length() > 1) {
                File hsdp = new File(dp, w.getLocation().replaceAll("[ : / . ]", "-").trim() + ".html");
                Files.move(Paths.get(htmlsrc.toURI()), Paths.get(hsdp.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } else {
                htmlsrc.delete();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
   }

    public boolean allowPluginEvents() {
        return !blockPluginEvents;
    }

    public void showMessage(String message) {
        showMessage(message, 1);
    }

    public void showMessage(String message, int type) {
        JOptionPane.showMessageDialog(null, message, name, type);
    }

    public final static void getOptionMenuAction(EOption eOption, boolean b) {
        switch (eOption) {
            case blockEventCalls:
                blockPluginEvents = b;
                break;
            case forceHTTPS:
                forceHTTPS = b;
                break;
            case createPluginDataFolders:
                createPluginDataFolders = b;
                break;
            case useDuckDuckGo:
                useDuck = b;
            case offlineStorage:
                offlineStorage = b;
            default:
                break;
        }
    }

    @Override
    public void init() {
        System.out.println("[ZunoAPI] Launching application...");
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        BorderPane borderPane = new BorderPane();

        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 1200, 600);

        name = getProgramInfo().name;
        version = getProgramInfo().version;

        start(stage, scene, root, borderPane);

        stage.setTitle(name + " v" + version);
        stage.setScene(scene);
        stage.show();

        System.out.println("[GC]: Starting garbage collecter...");
        startGCTimer();
    }

    public abstract ProgramInfo getProgramInfo();

    public abstract void start(Stage stage, Scene scene, StackPane root, BorderPane borderPane) throws Exception;

    private String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    public void startGCTimer() {
        System.out.println("[GC]: GC is set to run every 10 sec.");
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        long l = Runtime.getRuntime().freeMemory();
                        String sl = formatSize(l);
                        double e = Double.valueOf(sl.substring(0, (sl.length() - 3)));
                        System.out.println(e);
                        if (e > 150 && e < 600) {
                            // Calling System.gc(); improves ram usage
                            // Running Paper-IO.com with out GC
                            // zunozap can use over 1GB
                            // With GC zunozap uses around the normal amount
                            System.gc();
                            String l2 = formatSize(Runtime.getRuntime().freeMemory() - l);
                            System.out.println("[GC]: Saved " + l2 + " of RAM.");
                            if (l2.endsWith("MB")) {
                                double a = Double.valueOf(l2.substring(0, (l2.length() - 3)));
                                totalRamGCsaved = totalRamGCsaved + a;
                            } else if (l2.endsWith("GB")) {
                                double a = Double.valueOf(l2.substring(0, (l2.length() - 3)));
                                totalRamGCsaved = totalRamGCsaved + ((long) a * 1024);
                            }
                        } else {
                            System.out.println("[GC]: RAM is normal level.");
                        }
                    }
                }, 0, (long) /* Duration.minutes(1).toMillis() */Duration.seconds(10).toMillis());
    }

    public static double getTotalRamSavedFromGCinMB() {
        return totalRamGCsaved;
    }
}