package me.isaiah.zunozap;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.CertificateErrorParams;
import com.teamdev.jxbrowser.chromium.DownloadHandler;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import com.teamdev.jxbrowser.chromium.LoadHandler;
import com.teamdev.jxbrowser.chromium.LoadParams;
import com.teamdev.jxbrowser.chromium.PluginInfo;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.SavePageType;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.PluginBase;

@Info(name="ZunoZap", version="0.4.0")
public class ZunoZap extends ZunoAPI {
    public static final File home = new File(System.getProperty("user.home"), "zunozap");
    private static final ZFile saveDir = new ZFile("offline-pages"), dataDir = new ZFile("engine"), cssDir = new ZFile("styles"),
            plDir = new ZFile("plugins"), temp = new ZFile("temp");
    private static MenuBar menuBar;
    protected final static Menu menuFile = new Menu("File"), menuBook = new Menu("Bookmarks");
    protected static TabPane tb;
    private static StyleManager sm;
    public static boolean firstRun = false;
    private static Reader bmread;
    private Stage stage;

    @Override
    public void init() {
        if (Environment.isMac()) BrowserCore.initialize(); // On Mac OS X Chromium engine must be initialized in non-UI thread.
        super.init();
    }

    public static void main(String[] args) throws IOException {
        setInstance(new ZunoZap());
        File settings = new File(home, "settings.txt");
        if (!settings.exists()) {
            if (!home.exists()) home.mkdir();
            settings.createNewFile();
            OptionMenu.save(false);
            firstRun = true;
        }
        launch(ZunoZap.class, args);

        printGCSavedRam();
        getInstance().deleteFolders(temp);
        System.out.println("Shutting down Chromium...");
        BrowserCore.shutdown();
        try { t.cancel(); } catch (NullPointerException ingore) {}
        System.exit(0);
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
        menuBar = new MenuBar();
        tb = new TabPane();
        bmread = new Reader(menuBook);
        bmread.refresh();
        this.stage = stage;

        mkDirIfNotExist(home, saveDir, temp, cssDir);

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
        tb.setPrefSize(1365, 768);

        /* Setup tabs */
        Tab newtab = new Tab(" + ");
        newtab.setClosable(false);
        tb.getTabs().add(newtab);
        createTab(true);

        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(false); });

        border.setCenter(tb);
        border.setTop(menuBar);
        border.autosize();

        regMenuItems();
        menuBar.getMenus().addAll(menuFile, menuBook);
        sm = new StyleManager(cssDir, scene);
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());
        BrowserPreferences.setChromiumDir(dataDir.getAbsolutePath());

        p.loadPlugins();
        getHooks().onStart(stage, scene, tb);
    }

    public final void createTab(boolean isStart) {
        if (EOption.useDuck.get()) createTab(isStart, "https://start.duckduckgo.com/");
        else createTab(isStart, "https://google.com/");
    }

    @Override
    public final void createTab(boolean isStartTab, String url) {
        Browser b = new Browser();
        createTab(isStartTab, url, true, b, new BrowserView(b));
    }

    public final void createTab(boolean isStartTab, String url, boolean load, Browser engine, BrowserView web) {
        tabnum++;

        final Tab tab = new Tab("Loading...");
        tab.setTooltip(new Tooltip("Tab #"+tabnum));
        tab.setId("tab-"+tabnum);

        final Button back = new Button("<"), forward = new Button(">"), goBtn = new Button("Go"), bkmark = new Button("Bookmark");

        TextField urlField = new TextField("http://");
        HBox hBox = new HBox(back, forward, urlField, goBtn, bkmark);
        VBox vBox = new VBox(hBox, web);

        urlChangeLis(engine, urlField, tab, bkmark);

        goBtn.setOnAction((v) -> { loadSite(urlField.getText(), engine); });
        urlField.setOnAction((v) -> { loadSite(urlField.getText(), engine); });

        back.setOnAction((v) -> { engine.goBack(); });
        forward.setOnAction((v) -> { engine.goForward(); });

        bkmark.setOnAction((v) -> {
            String title = (engine.getTitle() != null ? engine.getTitle() : engine.getURL());
            if (!bmread.bm.containsKey(title)) {
                bmread.bm.put(engine.getTitle(), engine.getURL());
                try {
                    bmread.refresh();
                } catch (IOException e) { e.printStackTrace(); }
                MenuItem it = new MenuItem(title);
                it.setOnAction((t) -> { createTab(false, engine.getURL()); });
                menuBook.getItems().add(it);
                bkmark.setText("Unbookmark");
            } else {
                bmread.bm.remove(engine.getTitle());

                try {
                    bmread.refresh();
                    bmread = new Reader(menuBook);
                    bmread.readd();
                } catch (IOException e) { e.printStackTrace(); }
                bkmark.setText("Bookmark");
            }
        });

        String title = (engine.getTitle() != null ? engine.getTitle() : engine.getURL());
        if (bmread.bm.containsKey(title)) bkmark.setText("Unbookmark");

        /* Setting Styles */
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        HBox.setHgrow(urlField, Priority.ALWAYS);
        VBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        engine.getPreferences().setJavaScriptEnabled(EOption.JS.get());
        engine.setUserAgent(engine.getUserAgent() + " ZunoZap/" + getInfo().version());

        if (load) {
            if (isStartTab) engine.loadURL("https://zunozap.github.io/pages/startpage.html");
            else loadSite(url, engine);
        }

        engine.setFullScreenHandler(new ZFullScreenHandler(stage));

        tab.setContent(vBox);
        tab.setOnCloseRequest((e) -> { onTabClosed(e.getSource()); });

        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    private void onTabClosed(Object s) {
        try {
            ((BrowserView) ((VBox) ((Tab) s).getContent()).getChildren().get(1)).getBrowser().dispose();
        } catch (Exception e) { ((BrowserView) ((Tab) s).getContent()).getBrowser().dispose(); }
    }

    public final void urlChangeLis(final Browser engine, final TextField urlField, final Tab tab, final Button bkmark) {
        engine.setDownloadHandler(new DownloadHandler() {
            @Override public boolean allowDownload(DownloadItem i) { return !isUrlDownload(i.getURL()); }
        });

        engine.setPopupHandler(new PopupHandler() {
            @Override public PopupContainer handlePopup(PopupParams pp) {
                if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onPopup(pp.getTargetName().toLowerCase().contains("virus"));
                return new PopupContainer() {
                    @Override public void insertBrowser(Browser b, Rectangle arg1) {
                        createTab(false, b.getURL());
                        b.dispose();
                    }};
            }
        });
        engine.setLoadHandler(new LoadHandler() {
            @Override public boolean onCertificateError(CertificateErrorParams cp) { return false; }

            @Override
            public boolean onLoad(LoadParams lp) {
                ZunoZap.this.changed(engine, urlField, tab, urlField.getText(), lp.getURL(), bkmark);
                return false;
            }
        });
        engine.addLoadListener(new LoadLis() {

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
                Platform.runLater(() -> {
                    tab.setText(engine.getTitle());
                    String title = (engine.getTitle() != null ? engine.getTitle() : engine.getURL());
                    if (bmread.bm.containsKey(title)) bkmark.setText("Unbookmark");
                });
                if (EOption.offlineStorage.get()) new Thread(() -> engine.saveWebPage(engine.getURL(), saveDir.getPath(), SavePageType.COMPLETE_HTML)).start();
            }
            
        });
    }

    public void changed(final Browser engine, final TextField field, final Tab tab, String old, String newUrl, final Button bkmark) {
        if (old == null && (newUrl.contains("zunozap.github.io/pages/startpage.html") || newUrl.contains("start.duckduckgo.com"))) return;

        if (old == null || old.isEmpty()) {
            field.setText(newUrl);
            getHooks().onUrlChange(engine, field, null, newUrl);
            return;
        }

        if (newUrl.toLowerCase().contains("zunozap.github.io/addons/")) {
            if (newUrl.toLowerCase().contains("zunozap.github.io/addons/themes/")) {
                downloadAddon(newUrl, true, cssDir, sm);
                return;
            }
            if (newUrl.toLowerCase().contains("zunozap.github.io/addons/plugins/")) {
                downloadAddon(newUrl, false, plDir, sm);
                return;
            }
        }

        engine.getPreferences().setJavaScriptEnabled(EOption.JS.get());
        engine.getPreferences().setPluginsEnabled(!EOption.blockEventCalls.get());

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
                } catch (MalformedURLException e) { httpsredirect = true; }
            }
        }

        field.setText(newUrl);

        String title = (engine.getTitle() != null ? engine.getTitle() : engine.getURL());
        if (bmread.bm.containsKey(title)) bkmark.setText("Unbookmark");

        if (!httpsredirect && !(newUrl.replaceAll("[ . ]", "").equalsIgnoreCase(newUrl) || newUrl.startsWith("http"))) getHooks().onUrlChange(engine, field, old, newUrl);
    }

    public final void regMenuItems() {
        MenuItem clear = new MenuItem("Clear offline data"), about = new MenuItem("About ZunoZap " + getInfo().version());
        MenuItem settings = new MenuItem("Settings"), update = new MenuItem("Check for Update");

        clear.setOnAction((t) -> { deleteFolders(temp,saveDir); });
        settings.setOnAction((t) -> { new OptionMenu(); });
        update.setOnAction((t) -> { say(updateCheck());});

        about.setOnAction((a) -> {
            Tab t = new Tab("About");
            Browser b = new Browser();
            BrowserView v = new BrowserView(b);
            b.setUserAgent(b.getUserAgent() + " ZunoZap/" + getInfo().version());
            b.getPreferences().setJavaScriptEnabled(EOption.JS.get());
            b.loadHTML(String.format(aboutPageHTML(), "ZunoZap", b.getUserAgent(), b.getPreferences().isJavaScriptEnabled(), "ZunoZap/zunozap/master/LICENCE", "LGPLv3") + getPluginNames() 
            + getJxPluginNames(b) + "<br><b>Note:</b> ZunoZap uses the Chromium engine provided by <a href='https://www.teamdev.com/jxbrowser'>JxBrowser</a> by <a href='https://www.teamdev.com/'>TeamDev Ltd.</a>");
            t.setContent(v);
            t.setOnCloseRequest((e) -> { onTabClosed(e.getSource()); });
            tb.getTabs().add(tb.getTabs().size() - 1, t);
            tb.getSelectionModel().select(t);
        });

        bmread.bm.forEach((s1, s2) -> {
            MenuItem it = new MenuItem(s1);
            it.setOnAction((t) -> { createTab(false, s2); });
            menuBook.getItems().add(it);
        });
        menuFile.getItems().addAll(clear, about, update, settings);
    }

    public final void loadSite(String url, Browser e) {
        if (url.startsWith("zunozap:")) {
            if (url.substring(8).startsWith("update")) e.loadURL(updateCheck());
            else if (url.substring(8).startsWith("home")) e.loadURL("https://zunozap.github.io/");
            else if (url.substring(8).startsWith("start")) e.loadURL("https://zunozap.github.io/pages/startpage.html");

            return;
        }

        if ((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", "")))) {
            if (!EOption.useDuck.get()) e.loadURL("https://google.com/search?q=" + url.replace(" ", "%20"));
            else e.loadURL("https://duckduckgo.com/?q=" + url.replace(" ", "%20")); 

            return;
        }

        if (EOption.forceHTTPS.get()) e.loadURL(url.startsWith("http") ? url : "https://" + url);
        else e.loadURL(url.startsWith("http") ? url : "http://" + url);
    }

    public static final String getJxPluginNames(Browser b) {
        ArrayList<String> names = new ArrayList<>();
        int size = b.getPluginManager().getPluginsInfo().size();
        for (PluginInfo info : b.getPluginManager().getPluginsInfo()) {
            if (!names.contains(info.getName() + " " + info.getVersion()))
                names.add(info.getName() + " " + info.getVersion());
            else size--;
        }
        return size != 0 ? "<br>Chromium plugins (" + size + "): " + String.valueOf(names).replace("[", "").replace("]", "")
                : "<br>No installed Chromium plugins.";
    }
}