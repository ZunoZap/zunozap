package com.zunozap;

import static com.zunozap.Log.err;
import static com.zunozap.Log.out;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.DownloadHandler;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.zunozap.Engine.Type;
import com.zunozap.Settings.Options;
import com.zunozap.api.Plugin;
import com.zunozap.impl.BrowserImpl;
import com.zunozap.impl.ChromeEngine;
import com.zunozap.impl.WebKitEngine;
import com.zunozap.lang.Lang;
import com.zunozap.launch.Main;
import com.zunozap.launch.Main.OS;
import com.zunozap.plugin.manager.PluginManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import me.isaiah.downloadmanager.DownloadManager;

public abstract class ZunoZap extends Application {

    public static final String NAME = "ZunoZap";
    public static final String VERSION = "20.5";
    public final String UPDATE_URL = "https://raw.githubusercontent.com/ZunoZap/zunozap/master/LATEST-RELEASE.md";

    public static File home = new File(System.getProperty("user.home"), "zunozap");
    public static File stylesheet = null;

    public static double totalRamGCsaved = 0;

    protected final static PluginManager p = new PluginManager();
    protected static Timer t;

    public static boolean firstRun = false; 

    private static ZunoZap inst;
    protected static final ArrayList<String> block = new ArrayList<>();
    protected static TabPane tb;
    protected static MenuBar menuBar;
    protected final static Menu menuFile = new Menu("\u2630"), menuBook = new Menu("\uD83D\uDCDA");

    protected Stage stage;
    public static Reader bmread;
    public static JWindow l = new JWindow();

    protected static final ZFile data = new ZFile("engine"), cssDir = new ZFile("styles"),
            plDir = new ZFile("plugins"), lang = new ZFile("lang");

    @Deprecated
    public static ZunoZap getInstance() {
        return inst;
    }

    @Deprecated
    protected static void setInstance(ZunoZap inst) {
        ZunoZap.inst = inst;
        ZunoZap.stylesheet = new File(home, "style.css");
    }

    public static void main(String[] args) throws IOException {
        JLabel z = new JLabel("ZUNOZAP");
        z.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 56));
        z.setForeground(java.awt.Color.WHITE);
        z.setBorder(new EmptyBorder(15,30,15,30));
        l.setBackground(new java.awt.Color(0,0,0,200));
        l.setContentPane(z);
        l.setVisible(true);
        l.pack();
        l.setLocationRelativeTo(null);

        launch(ZunoZap.class, args);

        out("Shutting down BrowserCore");
        if (Settings.en == Type.CHROME)
            com.teamdev.jxbrowser.chromium.BrowserCore.shutdown();
        Platform.exit();
    }

    @Override
    public void init() throws IOException {
        System.setOut(out);
        System.setErr(err);
        out("Loading");
        setInstance(this);
        if (Main.os() == OS.MAC) com.teamdev.jxbrowser.chromium.BrowserCore.initialize();

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
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/ZunoZap/Blacklist/master/list.dat").openConnection().getInputStream()));
            String site;
            while ((site = in.readLine()) != null) block.add(site);
            in.close();
        } catch (Exception e) {}

        home.mkdirs();
        cssDir.mkdirs();

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

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.png")));

        for (int i = 0; i < 10; i++) deleteDirs(new ZFile("engine" + i));

        createTab(Settings.tabPage);
        regMenuItems(menuFile, menuBook, tb, EngineHelper.type);
        menuBar.getMenus().addAll(menuFile, menuBook);

        Settings.set(cssDir, scene);
        Settings.init(cssDir);
        Settings.changeStyle("ZunoZap default");
        Settings.save();
        scene.getStylesheets().add(ZunoZap.stylesheet.toURI().toURL().toExternalForm());

        if (Settings.en == Type.CHROME)
            com.teamdev.jxbrowser.chromium.BrowserPreferences.setChromiumDir(data.getAbsolutePath());

        p.loadPlugins();
        for (Plugin pl : p.plugins) pl.onLoad(stage, scene, tb);
        l.setVisible(false);
        l.dispose();

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

    public final String getJxPluginNames(Engine e) {
        if (!(e instanceof ChromeEngine))
            return "N/A";
        com.teamdev.jxbrowser.chromium.Browser b = (com.teamdev.jxbrowser.chromium.Browser)e.getImplEngine();

        String names = "";
        for (com.teamdev.jxbrowser.chromium.PluginInfo i : b.getPluginManager().getPluginsInfo())
            names += i.getName() + " " + i.getVersion() + ", ";
        return (b.getPluginManager().getPluginsInfo().size() != 0 ? names : "No Chromium plugins");
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

    public static final String getPluginNames() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins (" + size + "): " + String.valueOf(p.names) : Lang.NO_PL.tl;
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
                try { Settings.init(dir); } catch (Exception e){say("Unable to reload style manager");}
            }
        } catch (IOException e) {
            e.printStackTrace();
            say("Unable to download addon: " + e.getMessage());
            return;
        }
        say("Downloaded " + (theme ? "theme" : "plugin\nRestart browser to enable"));
    }

    public String getUpdated() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(UPDATE_URL).openConnection().getInputStream(), "UTF-8"))) {
            return in.readLine().equalsIgnoreCase(VERSION) ? "up to date" : "outdated";
        } catch (IOException e){return null;}
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

    public void changed(final Engine engine, final TextField field, final Tab tab, String old, String url, final Button bkmark, Reader r) {
        if (old == null && url.contains(Settings.tabPage)) return;

        if (old == null || old.isEmpty()) {
            field.setText(url);
            for (Plugin pl : p.plugins)
                pl.onURLChange(engine, field, old, url);
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

        if (!httpsredirect || url.startsWith("http")) 
            for (Plugin pl : p.plugins)
                pl.onURLChange(engine, field, old, url);
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

    public final void createTab(String url) {
        int tabnum = tb.getTabs().size() + 1;

        final Tab tab = new Tab(Lang.LOAD.tl);
        tab.setTooltip(new Tooltip("Tab " + tabnum));
        tab.setId("tab-"+tabnum);

        final Button back = new Button("<"), forward = new Button(">"), goBtn = new Button(Lang.GO.tl), bkmark = new Button("\u2606");
        Lang.a(() -> goBtn.setText(Lang.GO.tl));

        Engine e = EngineHelper.newBrowser();
        TextField field = new TextField("http://");
        HBox hBox = new HBox(back, forward, field, goBtn, bkmark);
        VBox vBox = new VBox(hBox, e.getComponent());

        addHandlers(e, field, tab, bkmark);

        goBtn.setOnAction(v -> e.load(field.getText()));
        field.setOnAction(v -> e.load(field.getText()));

        back.setOnAction(v -> e.history(0));
        forward.setOnAction(v -> e.history(1));

        bkmark.setOnAction(v -> bookmarkAction(e, bmread, (t -> createTab(e.getURL())), bkmark, menuBook));

        String title = (e.getTitle() != null ? e.getTitle() : e.getURL());
        if (null == title && bmread.bm.containsKey(title)) bkmark.setText("\u2605");

        // Setting Styles
        field.setId("urlfield");
        field.setMaxWidth(400);
        hBox.setId("urlbar");
        HBox.setHgrow(field, Priority.ALWAYS);
        VBox.setVgrow(e.getComponent(), Priority.ALWAYS);
        vBox.autosize();

        e.js(Options.javascript.b);
        if (e instanceof ChromeEngine)
            ((Browser)e.getImplEngine()).setFullScreenHandler(new ZFullScreenHandler(stage));

        e.load(url);

        tab.setContent(vBox);
        tab.setOnCloseRequest(a -> e.stop());

        for (Plugin pl : p.plugins) pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();

        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    public final void addHandlers(Engine u, final TextField urlField, final Tab tab, final Button bkmark) {
        if (u instanceof ChromeEngine) {
            DownloadHandler dh = new DownloadHandler() {
                @Override public boolean allowDownload(DownloadItem i) { return !isUrlDownload(i.getDestinationFile().getName()); }
            };

            PopupHandler ph = new PopupHandler() {
                @Override public PopupContainer handlePopup(PopupParams pp) {
                    return new PopupContainer() { @Override public void insertBrowser(Browser b, Rectangle r) {
                        createTab(b.getURL());
                        b.dispose();
                    }};
                }
            };

            Browser b = (Browser)u.getImplEngine();
            b.setDownloadHandler(dh);
            b.setPopupHandler(ph);
    
            b.addLoadListener(new LoadLis(true) {
                @Override public void onFinishLoadingFrame(FinishLoadingEvent e) {
                    String url = e.getBrowser().getURL();
                    Platform.runLater(() -> {
                        tab.setText(b.getTitle());
                        if (bmread.bm.containsKey(b.getTitle() != null ? b.getTitle() : url)) 
                            bkmark.setText("\u2605");
                        changed(u, urlField, tab, urlField.getText(), url, bkmark, bmread);
                    });
                }
            });
        } else if (u instanceof WebKitEngine) {
            WebEngine en = (WebEngine)u.getImplEngine();
            en.locationProperty().addListener((o,oU,nU) -> changed(u, urlField, tab, oU, nU, bkmark, bmread));

            en.setOnAlert(popupText -> {
                for (Plugin pl : p.plugins) pl.onPopup(popupText.getData());

                Alert alert = new Alert(AlertType.NONE);
                alert.setTitle("JS Popup");
                alert.setContentText(popupText.getData());
                alert.show();
            });
            en.titleProperty().addListener((ov, o, n) -> tab.setText(n));
        }
    }

}