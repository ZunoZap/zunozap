package me.isaiah.zunozap;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserException;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.DownloadHandler;
import com.teamdev.jxbrowser.chromium.DownloadItem;
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
import me.isaiah.zunozap.UniversalEngine.Engine;
import me.isaiah.zunozap.plugin.PluginBase;

@Info(name="ZunoZap", version="0.5.0", enableGC=false, engine = UniversalEngine.Engine.CHROME)
public class ZunoZap extends ZunoAPI {
    public static final File home = new File(System.getProperty("user.home"), "zunozap");
    protected static MenuBar menuBar;
    protected final static Menu menuFile = new Menu("File"), menuBook = new Menu("Bookmarks");
    protected static TabPane tb;
    private static StyleManager sm;
    private static Reader bmread;
    private Stage stage;
    private static JFrame loading = new JFrame("ZunoZap is loading");
    private Random r = new Random();

    @Override
    public void init() {
        if (Environment.isMac()) BrowserCore.initialize();
        super.init();
    }

    public static void main(String[] args) throws IOException {
        setInstance(new ZunoZap());
        File settings = new File(home, "settings.txt");
        if (!settings.exists()) {
            home.mkdir();
            settings.createNewFile();
            OptionMenu.save(false);
            firstRun = true;
        }

        loading.setSize(new java.awt.Dimension(200, 100));
        loading.setVisible(true);

        try {
            setup(new URL("https://raw.githubusercontent.com/ZunoZap/Blacklist/master/list.dat"), true);
        } catch (Exception e) {}

        launch(ZunoZap.class, args);

        printGCSavedRam();
        getInstance().deleteFolders(temp);
        log.println("Shutting down Chromium...");
        BrowserCore.shutdown();
        try { t.cancel(); } catch (NullPointerException ingore) {}
    }

    @Override
    public void start(Stage stage, Scene scene, StackPane root, BorderPane border) throws Exception {
        menuBar = new MenuBar();
        tb = new TabPane();
        bmread = new Reader(menuBook);
        bmread.refresh();
        this.stage = stage;

        mkDirs(home, saveDir, temp, cssDir);

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
        tb.setPrefSize(1365, 768);

        // Setup tabs
        Tab newtab = new Tab(" + ");
        newtab.setClosable(false);
        newtab.setId("createtab");
        tb.getTabs().add(newtab);

        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(false); });

        border.setCenter(tb);
        border.setTop(menuBar);
        border.autosize();

        for (int i = 0; i < 10; i++) deleteDir(new ZFile("engine" + i));

        Browser b = null;
        try {
            b = new Browser();
        } catch (BrowserException e) {
            ZFile f = new ZFile("engine" + r.nextInt(10));
            f.mk();
            f.deleteOnExit();
            b = new Browser(new BrowserContext(new BrowserContextParams(f.getAbsolutePath())));
        }

        createTab(true);
        b.setUserAgent(b.getUserAgent() + " ZunoZap/" + version);
        regMenuItems(bmread, menuFile, menuBook, aboutPageHTML("Java WebView", b.getUserAgent(), "ZunoZap/zunozap/master/LICENCE", "LGPLv3", getJxPluginNames(b)), tb, Engine.CHROME);
        menuBar.getMenus().addAll(menuFile, menuBook);
        sm = new StyleManager(cssDir, scene);
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        BrowserPreferences.setChromiumDir(dataDir.getAbsolutePath());
        BrowserPreferences.setUserAgent("ZunoZap/" + getInfo().version());
        b.dispose(false);
        p.loadPlugins();
        getHooks().onStart(stage, scene, tb);
        loading.setVisible(false);
        loading.dispose();
    }

    private void deleteDir(File f) {
        if (f.isDirectory() && f.list().length > 0) for (File z : f.listFiles()) if (z.isDirectory()) deleteDir(z); else z.delete();
        f.delete();
    }

    @Override
    public final void createTab(boolean isStartTab, String url) {
        Browser b = null;
        try {
            b = new Browser();
        } catch (BrowserException e) {
            ZFile f = new ZFile("engine" + r.nextInt(10));
            f.mk();
            f.deleteOnExit();
            b = new Browser(new BrowserContext(new BrowserContextParams(f.getAbsolutePath())));
        }
        createTab(isStartTab, url, true, b, new BrowserView(b));
    }

    public final void createTab(boolean isStartTab, String url, boolean load, Browser b, BrowserView web) {
        tabnum++;

        final Tab tab = new Tab("Loading");
        tab.setTooltip(new Tooltip("Tab " + tabnum));
        tab.setId("tab-"+tabnum);

        final Button back = new Button("<"), forward = new Button(">"), goBtn = new Button("Go"), bkmark = new Button("Bookmark");

        TextField urlField = new TextField("http://");
        HBox hBox = new HBox(back, forward, urlField, goBtn, bkmark);
        VBox vBox = new VBox(hBox, web);
        UniversalEngine e = new UniversalEngine(b);
        Universal u = new Universal(e);

        urlChangeLis(b, urlField, tab, bkmark);

        goBtn.setOnAction((v) -> loadSite(urlField.getText(), e));
        urlField.setOnAction((v) -> loadSite(urlField.getText(), e));

        back.setOnAction((v) -> b.goBack());
        forward.setOnAction((v) -> b.goForward());

        bkmark.setOnAction((v) -> u.bookmarkAction(bmread, ((t) -> createTab(false, b.getURL())), bkmark, menuBook));

        String title = (b.getTitle() != null ? b.getTitle() : b.getURL());
        if (bmread.bm.containsKey(title)) bkmark.setText("Unbookmark");

        // Setting Styles
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        HBox.setHgrow(urlField, Priority.ALWAYS);
        VBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        b.getPreferences().setJavaScriptEnabled(EOption.javascript.b);

        if (load)
            if (isStartTab) b.loadURL("https://zunozap.github.io/pages/startpage.html"); else loadSite(url, e);

        b.setFullScreenHandler(new ZFullScreenHandler(stage));

        tab.setContent(vBox);
        tab.setOnCloseRequest((a) -> onTabClosed(a.getSource()));

        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();

        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    @Override
    void onTabClosed(Object s) {
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
                    @Override public void insertBrowser(Browser b, Rectangle r) {
                        createTab(false, b.getURL());
                        b.dispose();
                    }};
            }
        });

        engine.addLoadListener(new LoadLis() {
            @Override public void onFinishLoadingFrame(FinishLoadingEvent e) {
                String url = e.getBrowser().getURL();
                Platform.runLater(() -> {
                    tab.setText(engine.getTitle());
                    String title = (engine.getTitle() != null ? engine.getTitle() : engine.getURL());
                    if (bmread.bm.containsKey(title)) bkmark.setText("Unbookmark");
                    ZunoZap.this.changed(engine, urlField, tab, urlField.getText(), url, bkmark);
                });
                File s = new File(saveDir, url.replaceAll("[ : / . ? ]", "-"));
                s.mkdir();
                if (EOption.offlineStorage.b && !url.contains("mail")) new Thread(() -> {
                    ZunoAPI.downloadPage(saveDir, temp, url, false);
                    engine.saveWebPage(url.replaceAll("[ : / . ? ]", "-"), s.getPath(), SavePageType.COMPLETE_HTML);
                }).start();
            }
        });
    }

    public void changed(final Browser engine, final TextField field, final Tab tab, String old, String newUrl, final Button bkmark) {
        if (old == null && (newUrl.contains("zunozap.github.io/pages/startpage") || newUrl.contains("start.duckduckgo.com"))) return;

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

        engine.getPreferences().setJavaScriptEnabled(EOption.javascript.b);
        engine.getPreferences().setPluginsEnabled(!EOption.blockEventCalls.b);

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

    public static final String getJxPluginNames(Browser b) {
        ArrayList<String> names = new ArrayList<>();
        int size = b.getPluginManager().getPluginsInfo().size();
        for (PluginInfo info : b.getPluginManager().getPluginsInfo()) {
            String s = info.getName() + " " + info.getVersion();
            if (!names.contains(s)) names.add(s);
            else size--;
        }
        return "<br>" + (size != 0 ? "Chromium plugins (" + size + "): " + names.toString().substring(1).replace("]", "")
                : "No installed Chromium plugins.");
    }
}