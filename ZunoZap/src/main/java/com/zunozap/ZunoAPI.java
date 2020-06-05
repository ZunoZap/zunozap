package com.zunozap;

import static com.zunozap.Log.err;
import static com.zunozap.Log.out;

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
import java.util.Timer;

import javax.swing.JOptionPane;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PluginInfo;
import com.zunozap.Engine.Type;
import com.zunozap.Settings.Options;
import com.zunozap.api.Plugin;
import com.zunozap.impl.ChromeEngine;
import com.zunozap.lang.Lang;
import com.zunozap.launch.Main;
import com.zunozap.plugin.manager.PluginManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.stage.Stage;
import me.isaiah.downloadmanager.DownloadManager;

public abstract class ZunoAPI extends Application {

    public final String NAME = "ZunoZap";
    public final String VERSION = "20.5";
    public final String UPDATE_URL = "https://raw.githubusercontent.com/ZunoZap/zunozap/master/LATEST-RELEASE.md";

    public static File home = new File(System.getProperty("user.home"), "zunozap");
    public static File stylesheet = null;

    public static double totalRamGCsaved = 0;

    protected final static PluginManager p = new PluginManager();
    protected static Timer t;

    public static boolean firstRun = false; 

    private static ZunoAPI inst;
    protected static final ArrayList<String> block = new ArrayList<>();
    protected static TabPane tb;
    protected static MenuBar menuBar;
    protected final static Menu menuFile = new Menu("\u2630"), menuBook = new Menu("\uD83D\uDCDA");

    protected Stage stage;
    public static Reader bmread;

    protected static final ZFile data = new ZFile("engine"), cssDir = new ZFile("styles"),
            plDir = new ZFile("plugins"), lang = new ZFile("lang");

    public static ZunoAPI getInstance() {
        return inst;
    }

    protected static void setInstance(ZunoAPI inst) {
        ZunoAPI.inst = inst;
        ZunoAPI.stylesheet = new File(home, "style.css");
    }

    @Override
    public void init() throws IOException {
        System.setOut(out);
        System.setErr(err);
        out("Loading");
        File s = new File(home, "settings.dat");
        if (!s.exists()) {
            home.mkdir();
            s.createNewFile();
            Settings.save();
            firstRun = true;
        }
    }

    public static void initTabPane() {
        if (null == tb) tb = new TabPane();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        StackPane root = new StackPane();
        BorderPane border = new BorderPane();

        root.getChildren().add(border);
        Scene scene = new Scene(root, 1200, 700);

        Settings.en = EngineHelper.type;
        menuBar = new MenuBar();

        Settings.init(cssDir);

        try {
            setup(new URL("https://raw.githubusercontent.com/ZunoZap/Blacklist/master/list.dat"), true);
        } catch (Exception e) {}

        mkDirs(home, cssDir);

        if (Options.COMPACT.b) {
            Tab m = new Tab();
            m.setClosable(false);
            menuBar.setBackground(null);
            m.setGraphic(menuBar);
            m.setId("createtab");
            tb.getTabs().add(m);
            tb.setRotateGraphic(true);
        } else border.setTop(menuBar);

        bmread = new Reader(menuBook);

        tb.setPrefSize(1365, 768);
        Tab newtab = new Tab(" + ");
        newtab.setClosable(false);
        newtab.setId("createtab");
        tb.getTabs().add(newtab);

        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(Settings.tabPage); });

        border.setCenter(tb);
        border.autosize();

        stage.getIcons().add(new Image(ZunoAPI.class.getClassLoader().getResourceAsStream("zunozaplogo.png")));
        start(stage, scene, root, border);

        stage.setTitle(NAME + " " + VERSION);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        Settings.save();

        try { t.cancel(); } catch (NullPointerException ingore){}
        Platform.exit();
    }

    public final String aboutPageHTML(Engine e) {
        return "<title>About ZunoZap</title><h1 style='margin:0;padding:0;'>ZunoZap</h1>" + VERSION + " (on " + Main.os() + ")<br>ZunoZap is " + getUpdated()
                + "<p>A web browser made with the WebKit & Chromium engines"
                + "<br>Useragent: " + e.getUserAgent()
                + "<br><br>Released under <a href='https://gnu.org/licenses/lgpl-3.0.txt'>LGPLv3</a><hr><br>"
                + "Note: The Chromium engine is provided by JxBrowser by <a href='https://teamdev.com/'>TeamDev</a></p>"
                + "ZunoZap Plugins: " + getPluginNames() + "<br>Chromium Plugins: " + getJxPluginNames(e);
    }

    public final void loadSite(String url, Engine e) {
        if (url.startsWith("zunozap:")) {
            if ((url = url.substring(8)).startsWith("home")) e.load("http://www.zunozap.com/");
            if (url.startsWith("start")) e.load(Settings.tabPage);
            if (url.startsWith("about")) e.loadHTML(aboutPageHTML(e));

            return;
        }

        if ((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", "")))) {
            e.load(String.format(Settings.searchEn, url.replace(" ", "%20")));
            return;
        }

        e.load(url.startsWith("http") ? url : "http" + (Options.forceHTTPS.b ? "s://" : "://") + url);
    }

    public final String getJxPluginNames(Engine e) {
        if (!(e instanceof ChromeEngine))
            return "N/a";
        Browser b = (Browser)e.getImplEngine();

        ArrayList<String> names = new ArrayList<>();
        int size = b.getPluginManager().getPluginsInfo().size();
        for (PluginInfo i : b.getPluginManager().getPluginsInfo()) {
            String s = i.getName() + " " + i.getVersion();
            if (!names.contains(s)) names.add(s);
            else size--;
        }
        return (size != 0 ? "(" + size + "): " + names.toString().substring(1).replace("]", "") : "No Chromium plugins");
    }

    public static String getUrlSource(String url) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream(), "UTF-8"))) {
            String l, s = "";
            while ((l = in.readLine()) != null) s += l + "\n";
            return s;
        } catch (IOException e) { return null; }
    }

    public void deleteDirs(File... fs) {
        for (File fo : fs) {
            if (fo.listFiles().length > 0) for (File f : fo.listFiles()) if (f.isDirectory()) deleteDirs(f); else f.delete();
            fo.delete();
        }
    }

    public void say(Object...obj) {
        JOptionPane.showMessageDialog(null, obj[0], "ZunoZap", obj.length < 2 ? 1 : Integer.parseInt(String.valueOf(obj[1])));
    }

    public static Path exportResource(String res, File folder) {
        try (InputStream stream = ZunoAPI.class.getClassLoader().getResourceAsStream(res)) {
            if (stream == null) throw new IOException("Null " + res + " from jar");

            Path p = Paths.get(folder.getAbsolutePath() + File.separator + res);
            Files.copy(stream, p, StandardCopyOption.REPLACE_EXISTING);
            return p;
        } catch (IOException e) { e.printStackTrace(); return null;}
    }

    public static final String getPluginNames() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins (" + size + "): " + String.valueOf(p.names) : Lang.NO_PL.tl;
    }

    public final boolean allowPluginEvents() {
        return p.plugins.size() > 0 && !Options.DIS_PL.b;
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
                try { Settings.init(dir); } catch (Exception e) { say("Unable to reload style manager"); }
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

    public String getUpdated() {
        try {
            return getUrlSource(UPDATE_URL).split("\n")[0].equalsIgnoreCase(VERSION) ? "up to date" : "outdated!";
        } catch (Exception e) { return "UPDATE_INFO_ERROR " + e; }
    }

    public static void setup(URL url, boolean clear) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));

        String site;
        while ((site = in.readLine()) != null) block.add(site);
        in.close();
    }

    public final void regMenuItems(Menu file, Menu book, TabPane tb, Type e) {
        file.getItems().clear();
        MenuItem about = new MenuItem("About ZunoZap");
        MenuItem settings = new MenuItem(Lang.SETT.tl);

        Lang.a(() -> settings.setText(Lang.SETT.tl));

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
            ab.setOnAction(about.getOnAction());

            si.setBackground(new Background(new BackgroundFill(Color.web("#f7f7f7"), null, null)));
            si.getChildren().addAll(l, se, ab);

            t.setContent(new HBox(si, s));
            tb.getTabs().add(tb.getTabs().size() - 1, t);
            tb.getSelectionModel().select(t);
        });

        about.setOnAction(a -> createTab("zunozap:about"));

        bmread.bm.forEach((s1, s2) -> {
            MenuItem it = new MenuItem(s1);
            it.setOnAction(t -> createTab(s2));
            book.getItems().add(it);
        });
        file.getItems().addAll(settings, about);
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

    public void changed(final Engine engine, final TextField field, final Tab tab, String old, String url, final Button bkmark, Reader r) {
        if (old == null && (url.contains("zunozap.com/pages/startpage") || url.contains("start.duckduckgo.com"))) return;

        if (old == null || old.isEmpty()) {
            field.setText(url);
            hookonUrlChange(engine, field, null, url);
            return;
        }

        if (url.toLowerCase().contains("zunozap.com/addons")) {
            boolean t = url.toLowerCase().contains("/themes/");
            boolean p = url.toLowerCase().contains("/plugins/");
            if (p || t) {
                downloadAddon(url, t, (t ? cssDir : plDir));
                return;
            }
        }

        engine.js(Options.javascript.b);
        if (engine instanceof ChromeEngine) ((Browser)engine.getImplEngine()).getPreferences().setPluginsEnabled(!Options.DIS_PL.b);

        if (isUrlDownload(url)) {
            DownloadManager.addToManager(url);
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

        String title = (engine.getTitle() != null ? engine.getTitle() : engine.getURL()).replaceAll("[^a-zA-Z ]", "");
        if (r.bm.containsKey(title)) Lang.b(() -> bkmark.setText("\u2605"));

        if (!httpsredirect || url.startsWith("http")) hookonUrlChange(engine, field, old, url);
    }

    public void bookmarkAction(Engine e, Reader bmread, EventHandler<ActionEvent> value, Button b, Menu m) {
        String title = (e.getTitle() != null ? e.getTitle() : e.getURL()).replaceAll("[^a-zA-Z ]", "");
        if (!bmread.bm.containsKey(title)) {
            bmread.bm.put(e.getTitle(), e.getURL());
            try {
                bmread.save();
            } catch (IOException ex) { ex.printStackTrace(); }
            MenuItem it = new MenuItem(title);
            it.setOnAction(value);
            m.getItems().add(it);
            b.setText("\u2605");
        } else {
            bmread.bm.remove(e.getTitle());

            try {
                bmread.save();
                bmread = new Reader(m);
                bmread.readd();
            } catch (IOException ex) { ex.printStackTrace(); }
            b.setText("\u2606");
        }
    }

    public void hookonStart(Stage st, Scene sc, TabPane tb) {
        if (allowPluginEvents()) for (Plugin pl : p.plugins) pl.onLoad(st, sc, tb);
    }

    public void hookonUrlChange(Engine engine, TextField field, String old, String url) {
        if (!allowPluginEvents()) return;

        for (Plugin pl : p.plugins) {
            try {
                pl.onURLChange(engine, field, (old != null ? new URL(old) : null), new URL(url));
            } catch (MalformedURLException e) {
                err(e.getMessage() + ": Cant pass url change to " + pl.getInfo().name());
            }
        }
    }

    public abstract void start(Stage stage, Scene scene, StackPane root, BorderPane pane) throws Exception;
    public abstract void createTab(String s2);

}