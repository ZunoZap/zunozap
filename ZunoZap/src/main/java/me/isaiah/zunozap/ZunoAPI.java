package me.isaiah.zunozap;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.isaiah.zunozap.UniversalEngine.Engine;
import me.isaiah.zunozap.plugin.manager.PluginManager;

public abstract class ZunoAPI extends Application {
    public static File home = new File(System.getProperty("user.home"), "ZunoZap");
    protected static String version;
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
    protected static Log log = new Log(System.out);
    public static boolean firstRun = false;
    protected static ArrayList<String> block = new ArrayList<>();
    protected static UniversalEngine.Engine en;

    protected static final ZFile saveDir = new ZFile("offline-pages"), dataDir = new ZFile("engine"), cssDir = new ZFile("styles"),
            plDir = new ZFile("plugins"), temp = new ZFile("temp");

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

    public final String aboutPageHTML(String... s) {
        return String.format("<h1>%s <small>%s</small></h1><p>A web browser made with the %s engine<br>"
                + " Useragent: %s <br> Javascript: " + EOption.javascript.b
                + "<br> Licence: <a href='https://raw.githubusercontent.com/%s'>%s</a><hr><br>"
                + "<b>Note: The Chromium engine is provided by JxBrowser by <a href='https://teamdev.com/'>TeamDev</a></b></p>"
                + "ZunoZap Plugins: " + getPluginNames() + "<br>Chromium Plugins: " + s[4],
                getInfo().name(), getInfo().version(), s[0], s[1], s[2], s[3]);
    }

    public static void setUserAgent(WebEngine e) {
        if (!e.getUserAgent().contains("ZunoZap")) e.setUserAgent(e.getUserAgent() + " ZunoZap/" + version + " Chrome/60.0.3112");
        else System.err.println("Useragent has already been set!");
    }

    public final static void history(WebEngine e, EHistory go) {
        e.executeScript("history." + go.toString().toLowerCase() + "();");
    }

    public enum EHistory { BACK,FORWARD; }

    public static final void loadSite(String url, UniversalEngine e) {
        if (url.startsWith("zunozap:")) {
            if (url.substring(8).startsWith("home")) e.load("https://zunozap.github.io/");
            else if (url.substring(8).startsWith("start")) e.load("https://zunozap.github.io/pages/startpage.html");

            return;
        }

        if ((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", "")))) {
            e.load("https://" + (EOption.onTheDuckSide.b ? "duckduckgo.com" : "google.com/search") + "?q=" + url.replace(" ", "%20"));
            return;
        }

        e.load(url.startsWith("http") ? url : "http" + (EOption.forceHTTPS.b ? "s://" : "://") + url);
    }

    public static String getUrlSource(String url) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream(), "UTF-8"))) {
            String line;
            StringBuilder a = new StringBuilder();
            while ((line = in.readLine()) != null) a.append(line);

            return a.toString();
        } catch (IOException e) { return null; }
    }

    public static void downloadPage(File dp, File temp, String loc, boolean all) {
        try {
            String regex = "[ : / . ? ]";
            File html = new File(temp, loc.replaceAll(regex, "-").trim() + ".html");

            String toWrite = getUrlSource(loc.trim());
            Files.write(Paths.get(html.toURI()), toWrite.getBytes());
            log.println("Downloaded " + loc);
            if (html.length() > 2) {
                File hsdp = new File(new File(dp, loc.replaceAll(regex, "-").trim()), loc.replaceAll(regex, "-").trim() + ".html");
                hsdp.getParentFile().mkdirs();
                hsdp.delete();
                if (all) downloadAssetsFromPage(loc.trim(), hsdp.getParentFile());
                if (hsdp.exists()) hsdp.delete();
                Files.move(Paths.get(html.toURI()), Paths.get(hsdp.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } else html.delete();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void downloadAssetsFromPage(String site, File f) {
        new Thread(() -> { (new Thread(() -> { try { downloadAssetsFromPage0(site, f); } catch (IOException e) { e.printStackTrace(); }})).start(); }).start();
    }

    public static void downloadAssetsFromPage0(String site, File folder) throws IOException {
        URL url = new URL(site);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        HTMLDocument doc = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        HTMLEditorKit.ParserCallback callback = doc.getReader(0);
        new ParserDelegator().parse(br, callback, true);

        for (HTMLDocument.Iterator iterator = doc.getIterator(HTML.Tag.IMG); iterator.isValid(); iterator.next()) {
            String src = (String) iterator.getAttributes().getAttribute(HTML.Attribute.SRC);
            if (src != null) downloadImage(site, src, folder);
            else System.err.println("null source");
        }

        for (HTMLDocument.Iterator iterator = doc.getIterator(HTML.Tag.LINK); iterator.isValid(); iterator.next()) {
            String src = (String) iterator.getAttributes().getAttribute(HTML.Attribute.HREF);
            if (src != null) downloadAsset(site, src, folder);
            else System.err.println("null source");
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
            file.getParentFile().mkdirs();
            file.createNewFile();
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

    @Override public void init() {
        System.setOut(log);
        log.println("Loading");
    }

    @Override public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        BorderPane borderPane = new BorderPane();

        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 1200, 600);

        if (isValid()) version = getInfo().version();
        else log.println("Note: This program is not valid.");

        en = getInfo().engine();

        start(stage, scene, root, borderPane);

        stage.setTitle(getInfo().name() + " " + version);
        stage.setScene(scene);
        stage.show();

        log.println("[GC]: Starting GC");
        if (getInfo().enableGC()) startGC();
    }

    @Override public void stop() {
        OptionMenu.save(true);
    }

    public boolean isValid() {
        return instance.getClass().isAnnotationPresent(Info.class);
    }

    public Info getInfo() {
        if (isValid()) return instance.getClass().getAnnotation(Info.class);

        return null;
    }

    public static void exportResource(String res, File folder) {
        try (InputStream stream = ZunoAPI.class.getClassLoader().getResourceAsStream(res)) {
            if (stream == null) throw new IOException("Cannot get file " + res + " from jar.");

            Files.copy(stream, Paths.get(folder.getAbsolutePath() + File.separator + res), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private void startGC() {
        log.println("[GC]: GC is set to run every 9 sec");
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override public void run() {
                long l = Runtime.getRuntime().freeMemory();
                String sl = formatSize(l);
                double e = Double.valueOf(sl.substring(0, (sl.length() - 3)));
                if (e > 140 && e < 601) {
                    if (shouldGC) {
                        System.gc();
                        String l2 = formatSize(Runtime.getRuntime().freeMemory() - l);
                        log.println("[GC] Saved " + l2 + " of RAM.");
                        double a = Double.valueOf(l2.substring(0, (l2.length() - 3)));
                        if (l2.endsWith("MB")) totalRamGCsaved += a;
                        else if (l2.endsWith("GB")) totalRamGCsaved += ((long) a * 1024);

                        if (l2.startsWith("-")) shouldGC = false;
                    } else shouldGC = true;
                } else {
                    if (debug) log.println("[GC] RAM normal");

                    shouldGC = true;
                }
            }
        }, 3000, 8900);
    }

    public static void printGCSavedRam() {
        double t = totalRamGCsaved;

        log.println("[GC]: Total saved " + (t > 1024 ? Math.floor((t / 1024) * 10 + 0.5) / 10 + " GB" 
                : Math.floor(t * 10 + 0.5) / 10 + " MB"));
    }

    public static final String getPluginNames() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins [" + size + "]:" + String.valueOf(p.names).replace("[", "").replace("]", "")
                : "No Installed Plugins.";
    }

    public final boolean allowPluginEvents() {
        return p.plugins.size() > 0 && !EOption.blockEventCalls.b;
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
        say("Downloaded " + (theme ? "theme" : "plugin\nRestart is required to enable."));
    }

    public void mkDirs(File... fs) {
        for (File f : fs) f.mkdir();
    }

    public static String updateCheck() {
        Info i = getInstance().getInfo();
        String line = "error";
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new URL(i.updateURL()).openConnection().getInputStream(), "UTF-8"));
            line = r.readLine().trim();
            r.close();
        } catch (IOException e) {
            return "error " + e;
        }

        if (line.equalsIgnoreCase(i.version())) return "Uptodate";
        if (i.version().toLowerCase().endsWith("dev")) return "Running a snapshot";

        return "You are outdated!";
    }

    public final void createTab(boolean isStart) {
        createTab(isStart, "https://" + (EOption.onTheDuckSide.b ? "start.duckduckgo" : "google") + ".com");
    }

    public abstract void start(Stage stage, Scene scene, StackPane root, BorderPane pane) throws Exception;
    public abstract void createTab(boolean b, String s2);

    public static void bmct(boolean b, String s2) {
        getInstance().createTab(b,s2);
    }
    
    public static void setup(URL url, boolean clear) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));

        String site;
        while ((site = in.readLine()) != null) block.add(site);
        in.close();
    }

    public final void regMenuItems(Reader bmread, Menu file, Menu book, String html, TabPane tb, Engine en) {
        MenuItem clear = new MenuItem("Clear offline data"), about = new MenuItem("About ZunoZap " + getInfo().version());
        MenuItem settings = new MenuItem("Settings"), update = new MenuItem("Check for Update");

        clear.setOnAction((t) -> deleteFolders(temp, saveDir));
        settings.setOnAction((t) -> new OptionMenu());
        update.setOnAction((t) -> say(updateCheck()));

        about.setOnAction((a) -> {
            Tab t = new Tab("About");
            UniversalEngine c = null;
            Object v = null;
            if (en == Engine.CHROME) {
                Browser b = new Browser();
                v = new BrowserView(b);
                b.getPreferences().setJavaScriptEnabled(EOption.javascript.b);
                c = new UniversalEngine(b);
            } else c = new UniversalEngine((WebView) (v = new WebView()));

            c.loadHTML(html);
            t.setContent((Node) v);
            t.setOnCloseRequest((e) -> onTabClosed(e.getSource()));
            tb.getTabs().add(tb.getTabs().size() - 1, t);
            tb.getSelectionModel().select(t);
        });

        bmread.bm.forEach((s1, s2) -> {
            MenuItem it = new MenuItem(s1);
            it.setOnAction((t) -> createTab(false, s2));
            book.getItems().add(it);
        });
        file.getItems().addAll(clear, about, update, settings);
    }

    public final boolean check(WebEngine e, String u) {
        if (!EOption.blockMalware.b) return true;

        try {
            URL url = new URL(u);
            if (ZunoAPI.block == null || ZunoAPI.block.isEmpty() || ZunoAPI.block.size() < 1) return true;

            if (ZunoAPI.block.contains(url.toURI().getHost())) {
                e.getLoadWorker().cancel();
                e.load("https://zunozap.github.io/pages/blocked.html?" + url.toURI().getHost());
                return false;
            }
        } catch (IOException | URISyntaxException e1) {}
        return true;
    }

    abstract void onTabClosed(Object source);
}