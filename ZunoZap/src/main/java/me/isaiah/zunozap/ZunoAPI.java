package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

//import com.teamdev.jxbrowser.chromium.Browser;
//import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.isaiah.zunozap.Settings.Options;
import me.isaiah.zunozap.UniversalEngine.Engine;
import me.isaiah.zunozap.lang.Lang;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.manager.PluginManager;

public abstract class ZunoAPI extends Application {

    public static File home = new File(System.getProperty("user.home"), "zunozap");
    protected static String version;
    public static File stylesheet = null;
    public static String styleName = "None";
    public static double totalRamGCsaved = 0;
    protected final static PluginManager p = new PluginManager();
    protected static Timer t;
    public int tabnum = 0;
    private boolean shouldGC = true;
    private static ZunoAPI inst;
    public HashMap<String, String> bm = new HashMap<>();
    protected static Log log = new Log(System.out);
    public static boolean firstRun = false;
    protected static ArrayList<String> block = new ArrayList<>();
    public static UniversalEngine.Engine en;
    protected static TabPane tb;
    protected static MenuBar menuBar;
    protected final static Menu menuFile = new Menu("\u2630"), menuBook = new Menu("\uD83D\uDCDA");

    protected static String tabPage = "http://start.duckduckgo.com/";
    protected static String searchEn = "http://duckduckgo.com/?q=%s";

    protected static final ZFile saves = new ZFile("offline-pages"), data = new ZFile("engine"), cssDir = new ZFile("styles"),
            plDir = new ZFile("plugins"), temp = new ZFile("temp"), lang = new ZFile("lang");

    public static ZunoAPI getInstance() {
        return inst;
    }

    protected static void setInstance(ZunoAPI inst) {
        ZunoAPI.inst = inst;
        ZunoAPI.home = new File(System.getProperty("user.home"), inst.getInfo().name());
        ZunoAPI.stylesheet = new File(home, "style.css");
    }

    @Override public void init() throws IOException {
        System.setOut(log);
        log.info("Loading");
        File s = new File(home, "settings.txt");
        if (!s.exists()) {
            home.mkdir();
            s.createNewFile();
            Settings.save(false);
            firstRun = true;
        }
    }

    @Override public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        BorderPane borderPane = new BorderPane();

        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 1200, 600);

        if (isValid()) version = getInfo().version(); else log.err("Program not valid.");

        en = getInfo().engine();

        Settings.initCss(cssDir);
        Settings.initMenu();
        Settings.initLang();

        try {
            setup(new URL("https://raw.githubusercontent.com/ZunoZap/Blacklist/master/list.dat"), true);
        } catch (Exception e) {}

        mkDirs(home, saves, temp, cssDir);

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.png")));
        start(stage, scene, root, borderPane);

        stage.setTitle(getInfo().name() + " " + version);
        stage.setScene(scene);
        stage.show();

        log.info("Starting GC");
        if (getInfo().enableGC()) startGC();
    }

    @Override public void stop() {
        Settings.save(true);

        printGCSavedRam();
        deleteDirs(temp);

        try { t.cancel(); } catch (NullPointerException ingore) {}
        Platform.exit();
    }

    public final String aboutPageHTML(String... s) {
        return String.format("<h1>ZunoZap <small>%s</small></h1><p>A web browser made with the WebKit & Chromium engines<br>"
                + " Useragent: %s <br> Javascript: " + Options.javascript.b
                + "<br> Licence: <a href='https://gnu.org/licenses/lgpl-3.0.txt'>LGPLv3</a><hr><br>"
                + "<b>Note: The Chromium engine is provided by JxBrowser by <a href='https://teamdev.com/'>TeamDev</a></b></p>"
                + "ZunoZap Plugins: " + getPluginNames() + "<br>Chromium Plugins: " + s[1],
                getInfo().version(), s[0]);
    }

    public static void setUserAgent(WebEngine e) {
        if (!e.getUserAgent().contains("ZunoZap")) 
            e.setUserAgent(e.getUserAgent() + " ZunoZap/" + version + " Firefox/58.0 Chrome/64.0.3112");
        else log.err("Useragent has already been set!");
    }

    public final static void history(WebEngine e, String go) {
        e.executeScript("history." + go + "();");
    }

    public static final void loadSite(String url, UniversalEngine e) {
        if (url.startsWith("zunozap:")) {
            if (url.substring(8).startsWith("home")) e.load("http://www.zunozap.com/");
            else if (url.substring(8).startsWith("start")) e.load(tabPage);

            return;
        }

        if ((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", "")))) {
            e.load(String.format(searchEn, url.replace(" ", "%20")));
            return;
        }

        e.load(url.startsWith("http") ? url : "http" + (Options.forceHTTPS.b ? "s://" : "://") + url);
    }

    public static String getUrlSource(String url) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream(), "UTF-8"))) {
            String l;
            StringBuilder a = new StringBuilder();
            while ((l = in.readLine()) != null) a.append(l);

            return a.toString();
        } catch (IOException e) { return null; }
    }

    public static void downloadPage(File dp, File temp, String loc, boolean all) {
        try {
            String regex = "[ : / . ? ]";
            File html = new File(temp, loc.replaceAll(regex, "-").trim() + ".html");

            try {
                Files.write(Paths.get(html.toURI()), getUrlSource(loc.trim()).getBytes());
            } catch (NullPointerException e) {}
            log.info("Downloaded " + loc);
            if (html.length() > 5) {
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
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(site).openConnection().getInputStream()));

        HTMLDocument doc = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        HTMLEditorKit.ParserCallback callback = doc.getReader(0);
        new ParserDelegator().parse(br, callback, true);

        for (HTMLDocument.Iterator iterator = doc.getIterator(HTML.Tag.IMG); iterator.isValid(); iterator.next()) {
            String src = (String) iterator.getAttributes().getAttribute(HTML.Attribute.SRC);
            if (src != null) downloadAsset(site, src, folder); else log.err("null source");
        }

        for (HTMLDocument.Iterator iterator = doc.getIterator(HTML.Tag.LINK); iterator.isValid(); iterator.next()) {
            String src = (String) iterator.getAttributes().getAttribute(HTML.Attribute.HREF);
            if (src != null) downloadAsset(site, src, folder); else log.err("null source");
        }
    }

    private static void downloadAsset(String url, String src, File folder) {
        try {
            File file = new File(folder, src);
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.write(Paths.get(file.toURI()), getUrlSource(src.startsWith("http") ? src : url + src).getBytes());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void deleteDirs(File... fs) {
        for (File fo : fs) {
            if (fo.listFiles().length > 0) for (File f : fo.listFiles()) if (f.isDirectory()) deleteDirs(f); else f.delete();
            fo.delete();
        }
    }

    public void say(String msg) {
        say(msg, 1);
    }

    public void say(String msg, int type) {
        JOptionPane.showMessageDialog(null, /*tl(*/msg/*)*/, getInfo().name(), type); // TODO: Translate
    }

    public boolean isValid() {
        return inst != null && inst.getClass().isAnnotationPresent(Info.class);
    }

    public Info getInfo() {
        return isValid() ? inst.getClass().getAnnotation(Info.class) : null;
    }

    public static Path exportResource(String res, File folder) {
        try (InputStream stream = ZunoAPI.class.getClassLoader().getResourceAsStream(res)) {
            if (stream == null) throw new IOException("Null " + res + " from jar");

            Path p = Paths.get(folder.getAbsolutePath() + File.separator + res);
            Files.copy(stream, p, StandardCopyOption.REPLACE_EXISTING);
            return p;
        } catch (IOException e) { e.printStackTrace(); return null;}
    }

    private String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private void startGC() {
        log.println("GC: 6min delay");
        t = new Timer();
        t.schedule(new TimerTask() { @Override public void run() {
            long l = Runtime.getRuntime().freeMemory();
            String sl = formatSize(l);
            double e = Double.valueOf(sl.substring(0, (sl.length() - 3)));
            if (e > 140 && e < 601 && shouldGC) {
                System.gc();
                String l2 = formatSize(Runtime.getRuntime().freeMemory() - l);
                log.info("GC: Saved " + l2);
                double a = Double.valueOf(l2.substring(0, (l2.length() - 3)));
                if (l2.endsWith("MB")) totalRamGCsaved += a;
                else if (l2.endsWith("GB")) totalRamGCsaved += ((long) a * 1024);

                if (l2.startsWith("-")) shouldGC = false;
            } else shouldGC = true;
        }}, 60000, 10000);
    }

    public static void printGCSavedRam() {
        double t = totalRamGCsaved;

        log.info("GC: Total saved " + (t > 1024 ? Math.floor((t / 1024) * 10 + 0.5) / 10 + " GB" 
                : Math.floor(t * 10 + 0.5) / 10 + " MB"));
    }

    public static final String getPluginNames() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins (" + size + "): " + String.valueOf(p.names) : Lang.NO_PL.tl;
    }

    public final boolean allowPluginEvents() {
        return p.plugins.size() > 0 && !Options.blockEventCalls.b;
    }

    public boolean isHTTPSRedirect(URL old, URL u) {
        return !old.getProtocol().equalsIgnoreCase(u.getProtocol()) || !old.getProtocol().equalsIgnoreCase("https") ||
                u.toString().replaceFirst(u.getProtocol(), "").substring(3).equalsIgnoreCase(old.toString().replaceFirst(old.getProtocol(), "").substring(3));
    }

    public static boolean isUrlDownload(String s) {
        String[] z = {".exe", ".jar", ".zip", ".rar", ".png", ".jpg", ".gif", "?zunozapforcedownload"};
        for (String x : z) if (s.endsWith(x)) return true;

        return false;
    }

    public void downloadAddon(String url, boolean theme, File dir) {
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
                Settings.b.clear();
                try { Settings.initCss(dir); } catch (Exception e) { say("Unable to reload style manager"); }
            }
        } catch (IOException e) {
            e.printStackTrace();
            say("Unable to download addon: " + e.getMessage());
            return;
        }
        say("Downloaded " + (theme ? "theme" : "plugin\nRestart required to enable"));
    }

    public void mkDirs(File... fs) {
        for (File f : fs) f.mkdir();
    }

    public static String updateCheck() {
        Info i = getInstance().getInfo();

        try {
            return getUrlSource(i.updateURL()).equalsIgnoreCase(i.version()) ? "Uptodate" : "You are outdated!";
        } catch (Exception e) { return "error " + e; }
    }

    public final void createTab(boolean isStart) {
        createTab(isStart, tabPage);
    }

    public static void setup(URL url, boolean clear) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));

        String site;
        while ((site = in.readLine()) != null) block.add(site);
        in.close();
    }

    public final void regMenuItems(Reader bmread, Menu file, Menu book, String html, TabPane tb, Engine e) {
        file.getItems().clear();
        MenuItem clear = new MenuItem(Lang.CLEAR_OFFLNE.tl), about = new MenuItem("About ZunoZap");
        MenuItem settings = new MenuItem(Lang.SETT.tl), update = new MenuItem(Lang.UPDATE_CHECK.tl);

        Lang.a(() -> {
            clear.setText(Lang.CLEAR_OFFLNE.tl);
            settings.setText(Lang.SETT.tl);
            update.setText(Lang.UPDATE_CHECK.tl);
        });

        clear.setOnAction(t -> deleteDirs(temp, saves));
        settings.setOnAction(a -> {
            Tab t = new Tab("Settings");
            VBox s = new Settings();
            s.setPadding(new Insets(22, 10, 5, 35));

            VBox si = new VBox(11);
            si.setPadding(new Insets(15, 10, 10, 10));

            Label l = new Label("ZunoZap");
            l.setFont(Font.font(24));

            Button se = new Button("Settings");
            se.setDisable(true);
            se.setPadding(new Insets(5, 5, 5, 5));
            Button ab = new Button("About");

            si.setBackground(new Background(new BackgroundFill(Color.web("#f7f7f7"), null, null)));
            si.getChildren().addAll(l, se, ab);

            t.setContent(new HBox(si, s));
            tb.getTabs().add(tb.getTabs().size() - 1, t);
            tb.getSelectionModel().select(t);
        });
        update.setOnAction(t -> say(updateCheck()));

        about.setOnAction(a -> {
            Tab t = new Tab(Lang.ABOUT.tl);
            UniversalEngine c = null;
            Node v = null;
            if (e == Engine.CHROME) {
                com.teamdev.jxbrowser.chromium.Browser b = new com.teamdev.jxbrowser.chromium.Browser();
                v = new com.teamdev.jxbrowser.chromium.javafx.BrowserView(b);
                b.getPreferences().setJavaScriptEnabled(Options.javascript.b);
                c = new UniversalEngine(b);
            } else c = new UniversalEngine((WebView) (v = new WebView()));

            c.loadHTML(html);
            t.setContent(v);
            t.setOnCloseRequest(r -> onTabClosed(r.getSource()));
            tb.getTabs().add(tb.getTabs().size() - 1, t);
            tb.getSelectionModel().select(t);
        });

        bmread.bm.forEach((s1, s2) -> {
            MenuItem it = new MenuItem(s1);
            it.setOnAction(t -> createTab(false, s2));
            book.getItems().add(it);
        });
        file.getItems().addAll(clear, about, update, settings);
    }

    public final boolean check(WebEngine e, String u) {
        if (!Options.blockMalware.b) return true;

        try {
            URL url = new URL(u);
            if (ZunoAPI.block == null || ZunoAPI.block.isEmpty() || ZunoAPI.block.size() < 1) return true;

            if (ZunoAPI.block.contains(url.toURI().getHost())) {
                e.getLoadWorker().cancel();
                e.load("http://zunozap.com/pages/blocked.html?" + url.toURI().getHost());
                return false;
            }
        } catch (IOException | URISyntaxException e1) {}
        return true;
    }

    public void changed(final UniversalEngine engine, final TextField field, final Tab tab, String old, String url, final Button bkmark, Reader r) {
        if (old == null && (url.contains("zunozap.com/pages/startpage") || url.contains("start.duckduckgo.com"))) return;

        if (old == null || old.isEmpty()) {
            field.setText(url);
            hookonUrlChange(engine, field, null, url);
            return;
        }

        if (url.toLowerCase().contains("zunozap.com/addons")) {
            boolean t = url.toLowerCase().contains("zunozap.com/addons/themes/");
            boolean p = url.toLowerCase().contains("zunozap.com/addons/plugins/");
            if (p || t) {
                downloadAddon(url, t, (t ? cssDir : plDir));
                return;
            }
        }

        engine.js(Options.javascript.b);
        if (engine.e == Engine.CHROME) engine.b.getPreferences().setPluginsEnabled(!Options.blockEventCalls.b);

        if (isUrlDownload(url)) {
            new Download(url);
            return;
        }

        boolean httpsredirect = false;
        if (url.contains("file://")) {
            field.setText(url);
            return;
        } else if (old.startsWith("http")) {
            try {
                httpsredirect = isHTTPSRedirect(new URL(old), new URL(url));
            } catch (MalformedURLException e) { httpsredirect = true; }
        }

        field.setText(url);

        String title = (engine.getTitle() != null ? engine.getTitle() : engine.getURL());
        if (r.bm.containsKey(title)) Lang.b(() -> bkmark.setText("\u2605"));

        if (!httpsredirect || url.startsWith("http")) hookonUrlChange(engine, field, old, url);

        if (Options.offlineStorage.b && engine.e == Engine.WEBKIT) new Thread(() -> downloadPage(saves, temp, engine.getURL(), true)).start();
    }

    public void bookmarkAction(UniversalEngine e, Reader bmread, EventHandler<ActionEvent> value, Button b, Menu m) {
        String title = e.getTitle() != null ? e.getTitle() : e.getURL();
        if (!bmread.bm.containsKey(title)) {
            bmread.bm.put(e.getTitle(), e.getURL());
            try {
                bmread.refresh();
            } catch (IOException ex) { ex.printStackTrace(); }
            MenuItem it = new MenuItem(title);
            it.setOnAction(value);
            m.getItems().add(it);
            b.setText("\u2605");
        } else {
            bmread.bm.remove(e.getTitle());

            try {
                bmread.refresh();
                bmread = new Reader(m);
                bmread.readd();
            } catch (IOException ex) { ex.printStackTrace(); }
            b.setText("\u2606");
        }
    }

    public void hookonStart(Stage st, Scene sc, TabPane tb) {
        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onLoad(st, sc, tb);
    }

    public void hookonUrlChange(UniversalEngine engine, TextField field, String old, String url) {
        if (!allowPluginEvents()) return;

        for (PluginBase pl : p.plugins) {
            try {
                pl.onURLChange(engine, field, (old != null ? new URL(old) : null), new URL(url));
            } catch (MalformedURLException e) {
                log.err(e.getMessage() + ": Cant pass url change to " + pl.getPluginInfo().getAllInfo());
            }
        }
    }

    public abstract void start(Stage stage, Scene scene, StackPane root, BorderPane pane) throws Exception;
    public abstract void createTab(boolean b, String s2);
    protected abstract void onTabClosed(Object source);

}