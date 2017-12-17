package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.geometry.Side;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.isaiah.zunozap.UniversalEngine.Engine;
import me.isaiah.zunozap.plugin.PluginBase;

@Info(name="ZunoZap", version="0.3.7", engine = UniversalEngine.Engine.WEBKIT)
public class ZunoZapWebView extends ZunoAPI {
    public static final File home = new File(System.getProperty("user.home"), "zunozap");
    private MenuBar menuBar = null;
    protected final static Menu menuFile = new Menu("File"), menuBook = new Menu("Bookmarks");
    private static TabPane tb;
    private static StyleManager sm;
    private static Reader bmread;

    public static void main(String[] args) throws IOException {
        setInstance(new ZunoZapWebView());
        File s = new File(home, "settings.txt");
        if (!s.exists()) {
            home.mkdir();
            s.createNewFile();
            OptionMenu.save(false);
            firstRun = true;
        }
        launch(ZunoZapWebView.class, args);

        printGCSavedRam();
        getInstance().deleteFolders(temp);
        try { t.cancel(); } catch (NullPointerException ingore) {}
    }

    @Override
    public void start(Stage stage, Scene scene, StackPane root, BorderPane border) throws Exception {
        OptionMenu.init();
        tb = new TabPane();
        menuBar = new MenuBar();
        bmread = new Reader(menuBook);
        bmread.refresh();

        mkDirs(home, saveDir, temp, cssDir);

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);

        /// Setup tabs
        Tab newtab = new Tab(" + ");
        newtab.setClosable(false);
        tb.getTabs().add(newtab);
        createTab(true);

        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(false); });

        border.setCenter(tb);
        border.setTop(menuBar);
        border.autosize();

        WebView dummy = new WebView();
        setUserAgent(dummy.getEngine());
        regMenuItems(bmread, menuFile, menuBook, aboutPageHTML("Java WebView", dummy.getEngine().getUserAgent(), "ZunoZap/zunozap/master/LICENCE", "LGPLv3", "N/A"), tb, Engine.WEBKIT);
        menuBar.getMenus().addAll(menuFile, menuBook);
        sm = new StyleManager(cssDir, scene);
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        p.loadPlugins();
        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onLoad(stage, scene, tb);
    }

    @Override
    @SuppressWarnings("static-access") 
    public final void createTab(boolean isStartTab, String url) {
        tabnum++;

        // Create Tab
        final Tab tab = new Tab("Loading...");
        tab.setTooltip(new Tooltip("Tab " + tabnum));
        tab.setId("tab-"+tabnum);

        // init variables
        final Button back = new Button("<"), forward = new Button(">"), goBtn = new Button("Go"), bkmark = new Button("Bookmark");

        WebView web = new WebView();
        WebEngine engine = web.getEngine();
        TextField urlField = new TextField("http://");
        HBox hBox = new HBox(back, forward, urlField, goBtn, bkmark);
        VBox vBox = new VBox(hBox, web);
        UniversalEngine e = new UniversalEngine(web);
        Universal u = new Universal(e);

        urlChangeLis(engine, urlField, tab);

        goBtn.setOnAction((v) -> loadSite(urlField.getText(), e));
        urlField.setOnAction((v) -> loadSite(urlField.getText(), e));

        back.setOnAction((v) -> history(engine, EHistory.BACK));
        forward.setOnAction((v) -> history(engine, EHistory.FORWARD));

        bkmark.setOnAction((v) -> u.bookmarkAction(bmread, ((t) -> createTab(false, engine.getLocation())), bkmark, menuBook));

        // Setting Styles
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        hBox.setHgrow(urlField, Priority.ALWAYS);
        vBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        engine.setUserDataDirectory(dataDir);
        setUserAgent(engine);
        engine.javaScriptEnabledProperty().set(EOption.javascript.b);

        if (isStartTab) engine.load("https://zunozap.github.io/pages/startpage.html");
        else loadSite(url, e);

        tab.setContent(vBox);

        tab.setOnCloseRequest((a) -> {
            ((WebView) ((VBox) ((Tab) a.getSource()).getContent()).getChildren().get(1)).getEngine().loadContent("Closing");
        });

        if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

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

        engine.locationProperty().addListener((o,oU,nU) -> ZunoZapWebView.this.changed(engine, urlField, tab, oU, nU));

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

    @SuppressWarnings("deprecation")
    public void changed(final WebEngine engine, final TextField field, final Tab tab, String old, String newUrl) {
        if (old == null) {
            field.setText(newUrl);
            if (allowPluginEvents()) for (PluginBase plug : p.plugins) {
                try {
                    plug.onURLChange(engine, field, null, new URL(newUrl));
                } catch (MalformedURLException e) {
                    System.err.println("Cant pass url change to plugin " + plug.getPluginInfo().name + " " + plug.getPluginInfo().version + " [" + e.getMessage() + "]");
                }
            }
            return;
        }

        if (newUrl.toLowerCase().contains("zunozap.github.io/addons/themes/")) {
            downloadAddon(newUrl, true, cssDir, sm);
            return;
        }
        if (newUrl.toLowerCase().contains("zunozap.github.io/addons/plugins/")) {
            downloadAddon(newUrl, false, plDir, sm);
            return;
        }

        if (!check(engine, newUrl)) return;

        engine.javaScriptEnabledProperty().set(EOption.javascript.b);

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
                        System.err.println("Cant pass onURLChange event to plugin: " + pl.getPluginInfo().name + " " + pl.getPluginInfo().version);
                    }
                });
            }
        }

        if (EOption.offlineStorage.b) new Thread(() -> downloadPage(saveDir, temp, engine.getLocation(), true)).start();
    }

    @Override
    void onTabClosed(Object source) {
    }
}