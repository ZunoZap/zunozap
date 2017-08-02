package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import javax.swing.JOptionPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
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

public class ZunoZap extends ZunoAPI {
    public static final String v = "0.3.6-dev";
    public static final File homeDir = new File(System.getProperty("user.home"), "zunozap");
    private static final File offlineData = new File(homeDir, "offline-pages"),
            dataDir = new File(homeDir, "webEngine"), stylefolder = new File(homeDir, "styles"),
            pluginfolder = new File(homeDir, "plugins"), temp = new File(homeDir, "temp");
    private final MenuBar menuBar = new MenuBar();
    protected final static Menu menuFile = new Menu("File"), menuBook = new Menu("Bookmarks");
    private static TabPane tb;
    private static StyleManager sm;
    private final static PluginManager p = new PluginManager();
    public static boolean firstRun = false;
    public static HashMap<String, String> bm = new HashMap<>();
    private static Reader bmread;
    private static ZunoZap instance = new ZunoZap();
    
    protected static ZunoZap getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
        if (!new File(homeDir, "settings.txt").exists()) {
            if (!homeDir.exists()) homeDir.mkdir();
            new File(homeDir, "settings.txt").createNewFile(); // Fix error
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
        t.cancel(); // Stop GC after exit.
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
    public ProgramInfo getProgramInfo() {
        return new ProgramInfo("ZunoZap", v);
    }

    @Override
    public void start(Stage stage, Scene scene, StackPane root, BorderPane borderPane) throws Exception {
        OptionMenu.init();
        tb = new TabPane();
        bmread = new Reader();
        bmread.refresh();

        if (!homeDir.exists()) homeDir.mkdir();
        if (!offlineData.exists()) offlineData.mkdir();
        if (!dataDir.exists()) dataDir.mkdir();
        if (!temp.exists()) temp.mkdir();
        if (!stylefolder.exists()) stylefolder.mkdir();

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);

        /* New tab button */
          final Tab newtab = new Tab(" + ");
          newtab.setClosable(false);
          tb.getTabs().addAll(newtab);
        /*Start Tab*/createTab(true);

        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(false); });

        borderPane.setCenter(tb);
        borderPane.setTop(menuBar);
        borderPane.autosize();

        regMenuItems();
        menuBar.getMenus().addAll(menuFile, menuBook);
        sm = new StyleManager(stylefolder, scene);
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        p.loadPlugins();
        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onLoad(stage, scene, tb);
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
        final Button goButton = new Button("Go");
        final Button bookmark = new Button("Bookmark");

        final WebView web = new WebView();
        final WebEngine webEngine = web.getEngine();
        final TextField urlField = new TextField("http://");
        final HBox hBox = new HBox(back, forward, urlField, goButton, bookmark);
        final VBox vBox = new VBox(hBox, web);

        urlChangeLis(webEngine, urlField, tab);

        goButton.setOnAction((v) -> { loadSite(urlField.getText(), webEngine); });
        urlField.setOnAction((v) -> { loadSite(urlField.getText(), webEngine); });

        back.setOnAction((v) -> { history(webEngine, "back"); });
        forward.setOnAction((v) -> { history(webEngine, "forward"); });

        bookmark.setOnAction((v) -> {
            if (!bm.containsKey(webEngine.getTitle())) {
                bm.put(webEngine.getTitle(), webEngine.getLocation());
                try {
                    bmread.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MenuItem item = new MenuItem(webEngine.getTitle());
                item.setOnAction((t) -> { createTab(false, webEngine.getLocation()); });
                menuBook.getItems().add(item);
            } else {
                System.out.println("Removing...");
                bm.remove(webEngine.getTitle());
                
                try {
                    bmread.refresh();
                    bmread = new Reader();
                    bmread.readd();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /* Setting Styles */
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        hBox.setHgrow(urlField, Priority.ALWAYS);
        vBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        webEngine.setUserDataDirectory(dataDir);
        webEngine.setUserAgent(webEngine.getUserAgent() + " ZunoZap/" + v + " Chrome/60.0.3112");
        webEngine.javaScriptEnabledProperty().set(ZunoAPI.JS);

        if (isStartTab) {
            tab.setText("Start");
            try {
                webEngine.load(getClass().getClassLoader().getResource("startpage.html").toURI().toString());
            } catch (URISyntaxException e) {
                webEngine.loadContent("<h1><img src='https://zunozap.github.io/images/flash.png' width='150px' height='150px'><br><b>ZunoZap</b></h1><h3>v"+v+"</h3><br /> \n\t To start browsing, click on + (New Tab) sign.");
            }
        } else loadSite(url, webEngine);

        tab.setContent(vBox);

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
                File f = new File(offlineData,
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

        engine.locationProperty().addListener(new ChangeListener<String>() {
            @SuppressWarnings("static-access")
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == null) {
                    urlField.setText(newValue);
                    if (allowPluginEvents()) {
                        for (PluginBase plug : p.plugins) {
                            try {
                                plug.onURLChange(engine, urlField, null, new URL(newValue));
                            } catch (MalformedURLException e) {
                                System.out.println(e);
                                System.err.println("Cant pass url change to plugin: " + plug.getPluginInfo().name + " v"
                                        + plug.getPluginInfo().version);
                            }
                        }
                    }
                    return;
                }

                if (newValue.toLowerCase().contains("zunozap.github.io/addons/themes/")) {
                    showMessage("Press OK to start downloading theme");
                    URL website = null;
                    try {
                        website = new URL(newValue);
                    } catch (MalformedURLException e1) {
                        showMessage("Unable to download theme");
                        e1.printStackTrace();
                        return;
                    }
                    try (InputStream in = website.openStream()) {
                        File f = new File(stylefolder, newValue.substring(newValue.lastIndexOf("/") + 1));
                        Files.copy(in, Paths.get(f.toURI()), StandardCopyOption.REPLACE_EXISTING);
                        sm.b.clear();
                        try {
                            sm.init(stylefolder);
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
                if (newValue.toLowerCase().contains("zunozap.github.io/addons/plugins/")) {
                    showMessage("Press OK to start downloading plugin");
                    URL website = null;
                    try {
                        website = new URL(newValue);
                    } catch (MalformedURLException e1) {
                        showMessage("Unable to download plugin");
                        e1.printStackTrace();
                        return;
                    }
                    try (InputStream in = website.openStream()) {
                        File f = new File(pluginfolder, newValue.substring(newValue.lastIndexOf("/") + 1));
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

                if (isUrlDownload(newValue)) {
                    new Download(newValue);
                    return;
                }

                boolean httpsredirect = false;
                if (newValue.contains("file://")) {
                    urlField.setText(newValue);
                    return;
                } else {
                    if (oldValue.startsWith("http")) {
                        try {
                            httpsredirect = isHTTPSRedirect(new URL(oldValue), new URL(newValue));
                        } catch (MalformedURLException e) {
                            httpsredirect = true;
                            e.printStackTrace();
                        }
                    }
                }

                urlField.setText(newValue);

                if (httpsredirect) return; // HTTPS redirect

                if (allowPluginEvents()) {
                    if (!(newValue.replaceAll("[ . ]", "").equalsIgnoreCase(newValue) || newValue.startsWith("http"))) {
                        p.plugins.forEach((pl) -> {
                            try {
                                pl.onURLChange(engine, urlField, new URL(oldValue), new URL(newValue));
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                System.err.println("Cant pass onURLChange event to plugin: " + pl.getPluginInfo().name
                                        + " v" + pl.getPluginInfo().version);
                            }
                        });
                    }
                }

                if (ZunoAPI.offlineStorage) new Thread(() -> DownloadPage(offlineData, temp, engine)).start();
            }
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
        return (s.endsWith(".exe") || s.endsWith(".jar") || s.endsWith(".zip") || s.endsWith(".rar"));
    }

    private boolean isHTTPSRedirect(URL oldu, URL newu) {
        if (oldu.getProtocol().equalsIgnoreCase(newu.getProtocol()) || oldu.getProtocol().equalsIgnoreCase("https"))
            return false;

        return (newu.toString().replaceFirst(newu.getProtocol(), "").substring(3)
                .equalsIgnoreCase(oldu.toString().replaceFirst(oldu.getProtocol(), "").substring(3)));
    }

    public final void regMenuItems() {
        MenuItem clear = new MenuItem("Clear all offline data.");
        MenuItem aboutPage = new MenuItem("About ZunoZap v" + v);
        MenuItem settings = new MenuItem("Settings");
        MenuItem update = new MenuItem("Check for Update");

        clear.setOnAction((t) -> {
            if (temp.listFiles().length >= 1) for (File f : temp.listFiles()) f.delete();
            if (offlineData.listFiles().length >= 1) for (File f : offlineData.listFiles()) f.delete();
            try {
                Files.delete(Paths.get(temp.toURI()));
                Files.delete(Paths.get(offlineData.toURI()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        aboutPage.setOnAction((t) -> {
            Tab about = new Tab("About");
            WebView w = new WebView();
            setUserAgent(w.getEngine());
            w.getEngine().javaScriptEnabledProperty().set(!ZunoAPI.JS);
            w.getEngine().loadContent(String.format(aboutPageHTML(), "ZunoZap", w.getEngine().getUserAgent(),
                    w.getEngine().javaScriptEnabledProperty().get(), "ZunoZap/zunozap/master/LICENCE", "GPLv3") + getPluginNames());
            about.setContent(w);
            tb.getTabs().add(tb.getTabs().size() - 1, about);
            tb.getSelectionModel().select(about);
        });

        settings.setOnAction((t) -> { new OptionMenu(); });
        update.setOnAction((t) -> { showMessage(Updater.browser(v, name)); });

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