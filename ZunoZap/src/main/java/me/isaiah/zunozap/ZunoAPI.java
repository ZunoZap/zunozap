package me.isaiah.zunozap;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.manager.PluginManager;

public abstract class ZunoAPI extends Application {
    public static File home = new File(System.getProperty("user.home"), "ZunoZap");
    private static String version;
    public static File stylesheet = null;
    public static String styleName = "None";
    public static double totalRamGCsaved = 0;
    protected final static PluginManager p = new PluginManager();
    protected static Timer t;
    public int tabnum = 0;
    private boolean shouldGC = true;
    private static ZunoAPI instance;
    private boolean debug = false;
    public HashMap<String, String> bm = new HashMap<>();
    private Hooks hooks = new Hooks(p);

    public static ZunoAPI getInstance() {
        return instance;
    }

    public Hooks getHooks() {
        return hooks;
    }

    protected static void setInstance(ZunoAPI instance) {
        ZunoAPI.instance = instance;
        ZunoAPI.home = new File(System.getProperty("user.home"), instance.getInfo().name());
        ZunoAPI.stylesheet = new File(home, "style.css");
    }

    public final String aboutPageHTML() {
        return "<header><h1>About ZunoZap</h1></header><body><p>%s is a web browser made with the Java web engine</p>"
                + " Version: " + version + "<br> UserAgent: %s<br> JavaScript Enabled: %s<br>"
                + " Licence: <a href='https://raw.githubusercontent.com/%s'>%s</a><hr></body>";
    }

    public static void setUserAgent(WebEngine e) {
        if (!e.getUserAgent().contains("ZunoZap"))
            e.setUserAgent(e.getUserAgent() + " ZunoZap/" + version + " Chrome/60.0.3112");
        else System.err.println("Useragent has already been set!");
    }

    @Deprecated public final static void setStyle(String s, Button... bts) {
        for (Button b : bts) b.setStyle(s);
    }

    public final static void history(WebEngine e, EHistory go) {
        e.executeScript("history." + go.toString().toLowerCase() + "();");
    }

    public enum EHistory { BACK,FORWARD; }

    public static final void loadSite(String url, WebEngine e) {
        if (url.startsWith("zunozap:")) {
            if (url.substring(8).startsWith("update")) e.loadContent(updateCheck());
            else if (url.substring(8).startsWith("home")) e.load("https://zunozap.github.io/");
            else if (url.substring(8).startsWith("start")) e.load("https://zunozap.github.io/pages/startpage.html");

            return;
        }

        if ((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", "")))) {
            if (!EOption.useDuck.get()) e.load("https://google.com/search?q=" + url.replace(" ", "%20"));
            else e.load("https://duckduckgo.com/?q=" + url.replace(" ", "%20")); 

            return;
        }

        if (EOption.forceHTTPS.get()) e.load(url.startsWith("http") ? url : "https://" + url);
        else e.load(url.startsWith("http") ? url : "http://" + url);
    }

    public static String getUrlSource(String url) {
        try {
            URLConnection urlc = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
            String line;
            StringBuilder a = new StringBuilder();
            while ((line = in.readLine()) != null) a.append(line);
            in.close();
            return a.toString();
        } catch (IOException e){return null;}
    }

    public static void DownloadPage(File dp, File temp, WebEngine w) {
        try {
            String regex = "[ : / . ? ]";
            File html = new File(temp, w.getLocation().replaceAll(regex, "-").trim() + ".html");

            String toWrite = getUrlSource(w.getLocation().trim());
            Files.write(Paths.get(html.toURI()), toWrite.getBytes());
            System.out.println("Downloaded " + w.getLocation());
            if (html.length() > 2) {
                File hsdp = new File(new File(dp, w.getLocation().replaceAll(regex, "-").trim()), w.getLocation().replaceAll(regex, "-").trim() + ".html");
                if (!hsdp.getParentFile().exists()) hsdp.getParentFile().mkdirs();
                hsdp.delete();
                downloadAssetsFromPage(w.getLocation().trim(), hsdp.getParentFile());
                if (hsdp.exists()) hsdp.delete();
                Files.move(Paths.get(html.toURI()), Paths.get(hsdp.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } else html.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadAssetsFromPage(String site, File folder) {
        runTask(() -> {
                try {
                    downloadAssetsFromPage0(site,folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    public static void runTask(Runnable run) {
        (new Thread(() -> { (new Thread(run)).start(); })).start();
    }

    public static void downloadAssetsFromPage0(String site, File folder) throws IOException {
        URL url;
        url = new URL(site);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();
        HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
        parser.parse(br, callback, true);

        for (HTMLDocument.Iterator iterator = htmlDoc.getIterator(HTML.Tag.IMG); iterator.isValid(); iterator.next()) {
            String imgSrc = (String) iterator.getAttributes().getAttribute(HTML.Attribute.SRC);
            if (imgSrc != null) downloadImage(site, imgSrc, folder);
            else System.out.println("Did not download: " + imgSrc);
        }
        
        for (HTMLDocument.Iterator iterator = htmlDoc.getIterator(HTML.Tag.LINK); iterator.isValid(); iterator.next()) {
            String src = (String) iterator.getAttributes().getAttribute(HTML.Attribute.HREF);
            if (src != null) downloadAsset(site, src, folder);
            else System.out.println("Source is null! " + src);
        }
    }

    private static void downloadImage(String url, String imgSrc, File folder) {
        BufferedImage image = null;
        try {
            String site = url;
            if (!(imgSrc.startsWith("http"))) site = site + imgSrc;
            else site = imgSrc;

            if (site.length() > 8 && site.substring(8).contains("//")) site = site.substring(0,8) + site.substring(8).replace("//","/");

            URL imageUrl = new URL(site);
            try { image = ImageIO.read(imageUrl); } catch (FileNotFoundException | IIOException e) {
                String withoutHost = site.replace(imageUrl.getProtocol() + "://" + imageUrl.getHost(), "").substring(1);
                URL fixedUrl = new URL(imageUrl.getProtocol() + "://" + imageUrl.getHost() + withoutHost.substring(withoutHost.indexOf("/")));
                try { image = ImageIO.read(fixedUrl); } catch (IIOException ingore) {}
            }
            if (image != null) {
                File file = new File(folder, imgSrc.replace("//", ""));
                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                if (!file.exists()) file.createNewFile();
                ImageIO.write(image, imgSrc.substring(imgSrc.lastIndexOf(".") + 1), file);
            }
            image.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void downloadAsset(String url, String src, File folder) {
        try {
            String site = url;
            if (!(src.startsWith("http"))) site = site + src;
            else site = src;

            File file = new File(folder, src);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();
            Files.write(Paths.get(file.toURI()), getUrlSource(url).getBytes());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void deleteFolders(File... fs) {
        for (File folder : fs) {
            if (folder.listFiles().length > 0) for (File f : folder.listFiles()) if (f.isDirectory()) deleteFolders(f); else f.delete();
            folder.delete();
        }
    }

    public void say(String msg) {
        say(msg, 1);
    }

    public void say(String msg, int type) {
        JOptionPane.showMessageDialog(null, msg, getInfo().name(), type);
    }

    @Override
    public void init() {
        System.out.println("[ZunoAPI] Loading...");
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        BorderPane borderPane = new BorderPane();

        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 1200, 600);

        if (isValid()) version = getInfo().version();
        else System.out.println("Note: This program is not valid.");

        start(stage, scene, root, borderPane);

        stage.setTitle(getInfo().name() + " " + version);
        stage.setScene(scene);
        stage.show();

        System.out.println("[GC]: Starting garbage collecter...");
        if (getInfo().enableGC()) startGC();
    }

    public boolean isValid() {
        return instance.getClass().isAnnotationPresent(Info.class);
    }

    public Info getInfo() {
        if (isValid()) return instance.getClass().getAnnotation(Info.class);

        return null;
    }

    public static void exportResource(String res, File folder) throws IOException {
        try (InputStream stream = ZunoAPI.class.getClassLoader().getResourceAsStream(res)) {
            if (stream == null) throw new IOException("Cannot get file " + res + " from Jar file.");

            System.out.println("Exporting resource " + res + " to folder " + folder.getAbsolutePath());
            Files.copy(stream, Paths.get(folder.getAbsolutePath() + File.separator + res), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) { throw e; }
    }

    private String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private void startGC() {
        System.out.println("[GC]: GC is set to run every 9 sec (8.9)");
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override public void run() {
                long l = Runtime.getRuntime().freeMemory();
                String sl = formatSize(l);
                double e = Double.valueOf(sl.substring(0, (sl.length() - 3)));
                if (e > 140 && e <= 600) {
                    if (shouldGC) {
                        System.gc();
                        String l2 = formatSize(Runtime.getRuntime().freeMemory() - l);
                        System.out.println("[GC]: Saved " + l2 + " of RAM.");
                        double a = Double.valueOf(l2.substring(0, (l2.length() - 3)));
                        if (l2.endsWith("MB")) totalRamGCsaved += a;
                        else if (l2.endsWith("GB")) totalRamGCsaved += ((long) a * 1024);

                        if (l2.startsWith("-")) shouldGC = false;
                    } else shouldGC = true;
                } else {
                    if (debug) System.out.println("[GC]: RAM is normal.");

                    shouldGC = true;
                }
            }
        }, 3000, 8900);
    }

    public static void printGCSavedRam() {
        double total = totalRamGCsaved;
        if (total > 1024) System.out.println("[GC]: Total saved RAM: " + Math.floor((total / 1024) * 10 + 0.5) / 10 + " GB");
        else System.out.println("[GC]: Total saved RAM: " + Math.floor(total * 10 + 0.5) / 10 + " MB");
    }

    public static final String getPluginNames() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins [" + size + "]:" + String.valueOf(p.pluginNames).replace("[", "").replace("]", "")
                : "No Installed Plugins.";
    }

    public final boolean allowPluginEvents() {
        return p.plugins.size() != 0 && !EOption.blockEventCalls.get();
    }

    public boolean isHTTPSRedirect(URL old, URL newu) {
        return !old.getProtocol().equalsIgnoreCase(newu.getProtocol()) || !old.getProtocol().equalsIgnoreCase("https") ||
                newu.toString().replaceFirst(newu.getProtocol(), "").substring(3).equalsIgnoreCase(old.toString().replaceFirst(old.getProtocol(), "").substring(3));
    }

    public boolean isUrlDownload(String s) {
        return (s.endsWith(".exe") || s.endsWith(".jar") || s.endsWith(".zip") || s.endsWith(".rar") || 
                s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith(".gif") || s.endsWith("?zunozapforcedownload"));
    }
    
    public void downloadAddon(String url, boolean theme, File dir, StyleManager sm) {
        say("Press OK to start downloading addon");
        URL website = null;
        try {
            website = new URL(url);
        } catch (MalformedURLException e) {
            say("Unable to download addon");
            e.printStackTrace();
            return;
        }
        try (InputStream in = website.openStream()) {
            File f = new File(dir, url.substring(url.lastIndexOf("/") + 1));
            Files.copy(in, Paths.get(f.toURI()), StandardCopyOption.REPLACE_EXISTING);
            if (theme) {
                StyleManager.b.clear();
                try { sm.init(dir); } catch (Exception e) { say("Unable to reload style manager."); }
            }
        } catch (IOException e) {
            e.printStackTrace();
            say("Unable to download addon. " + e.getMessage());
            return;
        }
        if (theme) say("Downloaded theme");
        else say("Downloaded plugin\nRestart is required to enable plugin.");

        return;
    }

    public void mkDirIfNotExist(File... fs) {
        for (File f : fs) if (!f.exists()) f.mkdir();
    }

    public static String updateCheck() {
        Info i = getInstance().getInfo();
        String line = "error";
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new URL(i.updateURL()).openConnection().getInputStream(), "UTF-8"));
            line = r.readLine().trim();
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching update infomation: " + e;
        }

        if (line.equalsIgnoreCase(i.version())) return "You are running the latest version";
        if (i.version().toLowerCase().endsWith("dev")) return "Your using a snapshot build of " + i.name();

        return i.name() + " is outdated!\nIt is recommended that you update to the latest version\n";
    }

    public abstract void start(Stage stage, Scene scene, StackPane root, BorderPane pane) throws Exception;

    public abstract void createTab(boolean b, String s2);
    
    public static void bmct(boolean b, String s2) {
        getInstance().createTab(b,s2);
    }
}