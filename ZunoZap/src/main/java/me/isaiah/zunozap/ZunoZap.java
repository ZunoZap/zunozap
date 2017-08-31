package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import javax.swing.JOptionPane;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.manager.PluginManager;

@Info(name="ZunoZap", version="0.3.6-Dev")
public class ZunoZap extends ZunoAPI {
    public static final File homeDir = new File(System.getProperty("user.home"), "zunozap");
    private static final File saveDir = new ZFile("offline-pages"), dataDir = new ZFile("engine"), cssDir = new ZFile("styles"),
            plDir = new ZFile("plugins"), temp = new ZFile("temp");
    private final MenuBar menuBar = new MenuBar();
    protected final static Menu menuFile = new Menu("File"), menuBook = new Menu("Bookmarks");
    private static TabPane tb;
    private static StyleManager sm;
    private final static PluginManager p = new PluginManager();
    public static boolean firstRun = false;
    public static HashMap<String, String> bm = new HashMap<>();
    private static Reader bmread;

    public static void main(String[] args) throws IOException {
        setInstance(new ZunoZap());
        if (!new File(homeDir, "settings.txt").exists()) {
            if (!homeDir.exists()) homeDir.mkdir();
            new File(homeDir, "settings.txt").createNewFile();
            OptionMenu.save(false);
            firstRun = true;
        }
        launch(ZunoZap.class, args);
        double total = GCSavedInMB();
        if (total > 1024)
            System.out.println("[GC]: Total saved RAM: " + Math.floor((total / 1024) * 10 + 0.5) / 10 + " GB");
        else System.out.println("[GC]: Total saved RAM: " + Math.floor(total * 10 + 0.5) / 10 + " MB");

        if (temp.listFiles().length >= 1) for (File f : temp.listFiles()) f.delete();
        Files.delete(Paths.get(temp.toURI()));
        try {
            t.cancel();
        } catch (NullPointerException ingore) {}
    }

    @Override
    public void stop() {
        try {
            OptionMenu.save(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage, Scene scene, StackPane root, BorderPane border) throws Exception {
        OptionMenu.init();
        tb = new TabPane();
        bmread = new Reader();
        bmread.refresh();

        mkDirIfNotExist(homeDir, saveDir, temp, cssDir);

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);

        /* Setup tabs */
        final Tab newtab = new Tab(" + ");
        newtab.setClosable(false);
        tb.getTabs().addAll(newtab);
        createTab(true);

        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(false); });

        border.setCenter(tb);
        border.setTop(menuBar);
        border.autosize();

        regMenuItems();
        menuBar.getMenus().addAll(menuFile, menuBook);
        sm = new StyleManager(cssDir, scene);
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        p.loadPlugins();
        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onLoad(stage, scene, tb);
    }
    
    private void mkDirIfNotExist(File... fs) {
        for (File f : fs) if (!f.exists()) f.mkdir();
    }

    public final void createTab(boolean isStart) {
        if (!useDuck) createTab(isStart, "https://www.google.com");
        else createTab(isStart, "https://start.duckduckgo.com/");
    }

    @SuppressWarnings("static-access") 
    public final void createTab(boolean isStartTab, String url) {
        tabnum++;

        /* Create Tab */
        final Tab tab = new Tab("Loading...");
        tab.setTooltip(new Tooltip("Tab #"+tabnum));
        tab.setId("tab-"+tabnum);

        /* initialize variables */
        final Button back = new Button("<");
        final Button forward = new Button(">");
        final Button goBtn = new Button("Go");
        final Button bookmark = new Button("Bookmark");

        final WebView web = new WebView();
        final WebEngine engine = web.getEngine();
        final TextField urlField = new TextField("http://");
        final HBox hBox = new HBox(back, forward, urlField, goBtn, bookmark);
        final VBox vBox = new VBox(hBox, web);

        urlChangeLis(engine, urlField, tab);

        goBtn.setOnAction((v) -> { loadSite(urlField.getText(), engine); });
        urlField.setOnAction((v) -> { loadSite(urlField.getText(), engine); });

        back.setOnAction((v) -> { history(engine, "back"); });
        forward.setOnAction((v) -> { history(engine, "forward"); });

        bookmark.setOnAction((v) -> {
            if (!bm.containsKey(engine.getTitle())) {
                bm.put(engine.getTitle(), engine.getLocation());
                try {
                    bmread.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MenuItem item = new MenuItem(engine.getTitle());
                item.setOnAction((t) -> { createTab(false, engine.getLocation()); });
                menuBook.getItems().add(item);
                bookmark.setText("Unbookmark");
            } else {
                bm.remove(engine.getTitle());
                
                try {
                    bmread.refresh();
                    bmread = new Reader();
                    bmread.readd();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bookmark.setText("Bookmark");
            }
        });

        /* Setting Styles */
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        hBox.setHgrow(urlField, Priority.ALWAYS);
        vBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        engine.setUserDataDirectory(dataDir);
        setUserAgent(engine);
        engine.javaScriptEnabledProperty().set(ZunoAPI.JS);

        if (isStartTab) {
            tab.setText("Start");
            engine.load("https://zunozap.github.io/pages/startpage.html");
        } else loadSite(url, engine);

        tab.setContent(vBox);

        tab.setOnCloseRequest(new EventHandler<Event>() {
            @Override
            public void handle(Event e) {
                Tab b = (Tab) e.getSource();
                VBox c = (VBox) b.getContent();
                WebView d = (WebView) c.getChildren().get(1);
                d.getEngine().loadContent("Closing tab");
            }
        });
        
        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    /**
     * When URL is changed make sure everything gets updated to new URL.
     */
    public final void urlChangeLis(final WebEngine engine, final TextField urlField, final Tab tab) {
        engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.FAILED) {
                File f = new File(new File(saveDir, engine.getLocation().replaceAll("[ : / . ]", "-").trim()),
                        engine.getLocation().replaceAll("[ : / . ]", "-").trim() + ".html");
                if (f.exists()) {
                    try {
                        engine.load(f.toURI().toURL().toExternalForm());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                engine.loadContent("Unable to load " + engine.getLocation().trim());
                return;
            }
        });

        engine.locationProperty().addListener((o,oldValue,newValue) -> {
            ZunoZap.this.changed(engine, urlField, tab, oldValue, newValue);
        });

        // JS alert() handler
        engine.setOnAlert((popupText) -> {
            boolean bad = false;
            if (popupText.toString().toLowerCase().contains("virus")) {
                bad = true;
                JOptionPane.showMessageDialog(null, "The site you are visting has tryed to create an popup with the word 'virus' in it, Please be carefull on this site", "ZunoZap AntiPopupVirus", JOptionPane.WARNING_MESSAGE);
            }
            if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onPopup(bad);

            JOptionPane.showMessageDialog(null, popupText.getData(), "JS Popup", JOptionPane.INFORMATION_MESSAGE);
        });
        engine.titleProperty().addListener((ov, o, n) -> tab.setText(n));
    }

    protected boolean isUrlDownload(String s) {
        return (s.endsWith(".exe") || s.endsWith(".jar") || s.endsWith(".zip") || s.endsWith(".rar") || 
                s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith(".gif") || s.endsWith("?zunozapforcedownload"));
    }

    @SuppressWarnings("static-access")
    public void changed(final WebEngine engine, final TextField field, final Tab tab, String old, String newUrl) {
        if (old == null) {
            field.setText(newUrl);
            if (allowPluginEvents()) {
                for (PluginBase plug : p.plugins) {
                    try {
                        plug.onURLChange(engine, field, null, new URL(newUrl));
                    } catch (MalformedURLException e) {
                        System.out.println(e);
                        System.err.println("Cant pass url change to plugin: " + plug.getPluginInfo().name + " v"
                                + plug.getPluginInfo().version);
                    }
                }
            }
            return;
        }

        if (newUrl.toLowerCase().contains("zunozap.github.io/addons/themes/")) {
            showMessage("Press OK to start downloading theme");
            URL website = null;
            try {
                website = new URL(newUrl);
            } catch (MalformedURLException e1) {
                showMessage("Unable to download theme");
                e1.printStackTrace();
                return;
            }
            try (InputStream in = website.openStream()) {
                File f = new File(cssDir, newUrl.substring(newUrl.lastIndexOf("/") + 1));
                Files.copy(in, Paths.get(f.toURI()), StandardCopyOption.REPLACE_EXISTING);
                sm.b.clear();
                try {
                    sm.init(cssDir);
                } catch (Exception e) {
                    showMessage("Unable to reload style manager.\nRestart is required to enable them.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Unable to download theme.");
                return;
            }
            showMessage("Downloaded theme");
            return;
        }
        if (newUrl.toLowerCase().contains("zunozap.github.io/addons/plugins/")) {
            showMessage("Press OK to start downloading plugin");
            URL website = null;
            try {
                website = new URL(newUrl);
            } catch (MalformedURLException e1) {
                showMessage("Unable to download plugin");
                e1.printStackTrace();
                return;
            }
            try (InputStream in = website.openStream()) {
                File f = new File(plDir, newUrl.substring(newUrl.lastIndexOf("/") + 1));
                Files.copy(in, Paths.get(f.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Unable to download plugin.");
                return;
            }
            showMessage("Downloaded plugin, restart required to use plugin.");
            return;
        }

        engine.javaScriptEnabledProperty().set(ZunoAPI.JS);

        if (isUrlDownload(newUrl)) {
            new Download(newUrl.replace("?zunozapforcedownload", ""));
            return;
        }

        boolean httpsredirect = false;
        if (newUrl.contains("file://")) {
            field.setText(newUrl);
            return;
        } else {
            if (old.startsWith("http")) {
                try {
                    httpsredirect = isHTTPSRedirect(new URL(old), new URL(newUrl));
                } catch (MalformedURLException e) {
                    httpsredirect = true;
                    e.printStackTrace();
                }
            }
        }

        field.setText(newUrl);

        if (allowPluginEvents() && !httpsredirect) {
            if (!(newUrl.replaceAll("[ . ]", "").equalsIgnoreCase(newUrl) || newUrl.startsWith("http"))) {
                p.plugins.forEach((pl) -> {
                    try {
                        pl.onURLChange(engine, field, new URL(old), new URL(newUrl));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        System.err.println("Cant pass onURLChange event to plugin: " + pl.getPluginInfo().name
                                + " v" + pl.getPluginInfo().version);
                    }
                });
            }
        }

        if (ZunoAPI.offlineStorage) new Thread(() -> DownloadPage(saveDir, temp, engine)).start();
    }

    private boolean isHTTPSRedirect(URL old, URL newu) {
        if (old.getProtocol().equalsIgnoreCase(newu.getProtocol()) || old.getProtocol().equalsIgnoreCase("https"))
            return false;

        return (newu.toString().replaceFirst(newu.getProtocol(), "").substring(3)
                .equalsIgnoreCase(old.toString().replaceFirst(old.getProtocol(), "").substring(3)));
    }

    public final void regMenuItems() {
        MenuItem clear = new MenuItem("Clear all offline data.");
        MenuItem aboutPage = new MenuItem("About ZunoZap v" + getInfo().version());
        MenuItem settings = new MenuItem("Settings");
        MenuItem update = new MenuItem("Check for Update");

        clear.setOnAction((t) -> {
            if (temp.listFiles().length >= 1) for (File f : temp.listFiles()) f.delete();
            if (saveDir.listFiles().length >= 1) for (File f : saveDir.listFiles()) f.delete();
            try {
                Files.delete(Paths.get(temp.toURI()));
                Files.delete(Paths.get(saveDir.toURI()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        aboutPage.setOnAction((t) -> {
            Tab about = new Tab("About");
            WebView w = new WebView();
            setUserAgent(w.getEngine());
            w.getEngine().javaScriptEnabledProperty().set(ZunoAPI.JS);
            w.getEngine().loadContent(String.format(aboutPageHTML(), "ZunoZap", w.getEngine().getUserAgent(),
                    w.getEngine().javaScriptEnabledProperty().get(), "ZunoZap/zunozap/master/LICENCE", "GPLv3") + getPluginNames());
            about.setContent(w);
            tb.getTabs().add(tb.getTabs().size() - 1, about);
            tb.getSelectionModel().select(about);
        });

        settings.setOnAction((t) -> { new OptionMenu(); });
        update.setOnAction((t) -> { showMessage(Updater.browser(getInfo().version(), "ZunoZap")); });

        bm.forEach((s1, s2) -> {
            MenuItem item = new MenuItem(s1);
            item.setOnAction((t) -> { createTab(false, s2); });
            menuBook.getItems().add(item);
        });
        menuFile.getItems().addAll(clear, aboutPage, update, settings);
    }

    public static final String getPluginNames() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins [" + size + "]:" + String.valueOf(p.pluginNames).replace("[", "").replace("]", "")
                : "No Installed Plugins.";
    }

    @Override
    public final boolean allowPluginEvents() {
        return (p.plugins.size() != 0) && (super.allowPluginEvents());
    }

    public static String ExportResource(String res) throws IOException {
        try (InputStream stream = ZunoZap.class.getClassLoader().getResourceAsStream(res)) {
            if (stream == null) throw new IOException("Cannot get file " + res + " from Jar file.");
            String dest = homeDir.getAbsolutePath() + File.separator + res;

            System.out.println("Copying -> " + res + "\n\tto -> " + dest);
            Files.copy(stream, Paths.get(homeDir.getAbsolutePath() + File.separator + "style.css"), 
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) { throw e; }

        return homeDir.getAbsolutePath() + File.separator + res;
    }
}