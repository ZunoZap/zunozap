package me.isaiah.zunozap;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
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

public abstract class ZunoAPI extends Application {
    private String name;
    private static String version;
    public static boolean blockPluginEvents = false;
    public static boolean createPluginDataFolders = true;
    public static boolean useDuck = true;
    public static boolean offlineStorage = false;
    public static boolean forceHTTPS = false;
    public static boolean JS = true;
    public static File stylesheet = null;
    public static String styleName = "None";
    private static double totalRamGCsaved = 0;
    protected static Timer t;
    public int tabnum = 0;
    private boolean shouldGC = true;
    private boolean enableGC = true;
    private static ZunoAPI instance;

    /**
     * @deprecated use {@link #getInfo()}
     */
    @Deprecated
    public String getName() {
        return name;
    }

    /**
     * @deprecated use {@link #getInfo()}
     */
    @Deprecated
    public String getVersion() {
        return version;
    }

    public static ZunoAPI getInstance() {
        return instance;
    }

    protected static void setInstance(ZunoAPI instance) {
        ZunoAPI.instance = instance;
    }

    public final String aboutPageHTML() {
        return "<header><h1>About ZunoZap</h1></header>"
                +"<body>"
                + "    %s is a web browser made with the Java web engine,</p><br>"
                +"    Version: "+getVersion()+"<br>"
                +"    UserAgent: %s<br>"
                +"    JavaScript Enabled: %s<br>"
                +"    Licence: <a href='https://raw.githubusercontent.com/%s'>%s</a>"
                +"    <hr>"
                +"</body>";
    }

    public static void setUserAgent(WebEngine e) {
        if (!e.getUserAgent().contains("ZunoZap"))
            e.setUserAgent(e.getUserAgent() + " ZunoZap/" + version + " Chrome/60.0.3112");
        else System.err.println("Useragent has already been set!");
    }

    @Deprecated
    public final static void setStyle(String s, Button... bts) {
        for (Button b : bts) b.setStyle(s);
    }

    public final static void history(WebEngine e, String go) {
        e.executeScript("history." + go + "();");
    }

    public static final void loadSite(String url, WebEngine e) {
        if (url.startsWith("zunozap:")) {
            if (url.substring(8).startsWith("update")) e.loadContent(Updater.browser(version, url));
            else if (url.substring(8).startsWith("home")) e.load("https://zunozap.github.io/");
            else if (url.substring(8).startsWith("start")) e.load("https://zunozap.github.io/pages/startpage.html");

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
            File html = new File(temp, w.getLocation().replaceAll("[ : / . ? ]", "-").trim() + ".html");

            String toWrite = "<!--" + w.getLocation() + "--> " + getUrlSource(w.getLocation().trim());
            Files.write(Paths.get(html.toURI()), toWrite.getBytes());
            System.out.println("Downloaded " + w.getLocation().trim());
            if (html.length() > 2) {
                File hsdp = new File(new File(dp, w.getLocation().replaceAll("[ : / . ? ]", "-").trim()), 
                        w.getLocation().replaceAll("[ : / . ? ]", "-").trim() + ".html");
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
            AttributeSet attributes = iterator.getAttributes();
            String imgSrc = (String) attributes.getAttribute(HTML.Attribute.SRC);
            if (imgSrc != null && (imgSrc.endsWith(".jpg") || (imgSrc.endsWith(".jpeg")) || imgSrc.endsWith(".svg")
                    || (imgSrc.endsWith(".png")) || (imgSrc.endsWith(".ico")) || (imgSrc.endsWith(".bmp")))) {
                downloadImage(site, imgSrc, folder);
            } else System.out.println("Did not download: " + imgSrc);
        }
        
        for (HTMLDocument.Iterator iterator = htmlDoc.getIterator(HTML.Tag.LINK); iterator.isValid(); iterator.next()) {
            AttributeSet attributes = iterator.getAttributes();
            String src = (String) attributes.getAttribute(HTML.Attribute.HREF);
            if (src != null) {
                System.out.println("Downloading: " + src);
                downloadAsset(site, src, folder);
            } else System.out.println("Source is null! " + src);
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
            try {
                image = ImageIO.read(imageUrl);
            } catch (FileNotFoundException | IIOException e) {
                String withoutHost = site.replace(imageUrl.getProtocol() + "://" + imageUrl.getHost(), "").substring(1);
                URL fixedUrl = new URL(imageUrl.getProtocol() + "://" + imageUrl.getHost() + withoutHost.substring(withoutHost.indexOf("/")));
                try {
                    image = ImageIO.read(fixedUrl);
                } catch (IIOException ingore) {}
            }
            if (image != null) {
                File file = new File(folder, imgSrc.replace("//", ""));
                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                if (!file.exists()) file.createNewFile();
                ImageIO.write(image, imgSrc.substring(imgSrc.lastIndexOf(".") + 1), file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
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

    @Deprecated
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
            case JS:
                JS = b;
            default:
                break;
        }
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

        if (isValidProgram()) {
            name = getInfo().name();
            version = getInfo().version();
            enableGC = getInfo().enableGC();
        } else System.out.println("This program is not valid.");

        start(stage, scene, root, borderPane);

        stage.setTitle(name + " v" + version);
        stage.setScene(scene);
        stage.show();

        System.out.println("[GC]: Starting garbage collecter...");
        if (enableGC) startGC();
    }

    public boolean isValidProgram() {
        return instance.getClass().isAnnotationPresent(Info.class);
    }

    public Info getInfo() {
        if (instance.getClass().isAnnotationPresent(Info.class)) {
            Annotation annotation = instance.getClass().getAnnotation(Info.class);
            return (Info) annotation;
        }
        return null;
    }

    private String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private void startGC() {
        System.out.println("[GC]: GC is set to run every 10 sec (9.9)");
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override public void run() {
                long l = Runtime.getRuntime().freeMemory();
                String sl = formatSize(l);
                double e = Double.valueOf(sl.substring(0, (sl.length() - 3)));
                if (e > 140 && e <= 600 && shouldGC) {
                    System.gc();
                    String l2 = formatSize(Runtime.getRuntime().freeMemory() - l);
                    System.out.println("[GC]: Saved " + l2 + " of RAM.");
                    double a = Double.valueOf(l2.substring(0, (l2.length() - 3)));
                    if (l2.endsWith("MB")) totalRamGCsaved += a;
                    else if (l2.endsWith("GB")) totalRamGCsaved += ((long) a * 1024);

                    if (l2.startsWith("-")) shouldGC = false;
                } else {
                    System.out.println("[GC]: RAM is normal level.");
                    shouldGC = true;
                }
            }
        }, 3000, 9900);
    }

    public static double GCSavedInMB() {
        return totalRamGCsaved;
    }

    public abstract void start(Stage stage, Scene scene, StackPane root, BorderPane borderPane) throws Exception;
}