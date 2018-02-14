package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import me.isaiah.zunozap.Settings.Options;
import me.isaiah.zunozap.UniversalEngine.Engine;
import me.isaiah.zunozap.plugin.PluginBase;

@Info(name="ZunoZap", version="0.5.2", engine = UniversalEngine.Engine.WEBKIT)
public class ZunoZapWebView extends ZunoAPI {
    public static final File home = new File(System.getProperty("user.home"), "zunozap");
    private static Reader bmread;

    public static void main(String[] args) throws IOException {
        setInstance(new ZunoZapWebView());
        File s = new File(home, "settings.txt");
        if (!s.exists()) {
            home.mkdir();
            s.createNewFile();
            Settings.save(false);
            firstRun = true;
        }
        launch(ZunoZapWebView.class, args);
    }

    @Override
    public void start(Stage stage, Scene scene, StackPane root, BorderPane border) throws Exception {
        tb = new TabPane();
        menuBar = new MenuBar();
        bmread = new Reader(menuBook);
        bmread.refresh();

        mkDirs(home, saves, temp, cssDir);

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
        Settings.set(cssDir, scene);
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

        final Button back = new Button("<"), forward = new Button(">"), goBtn = new Button("Go"), bkmark = new Button("Bookmark");

        WebView web = new WebView();
        WebEngine engine = web.getEngine();
        TextField urlField = new TextField("http://");
        HBox hBox = new HBox(back, forward, urlField, goBtn, bkmark);
        VBox vBox = new VBox(hBox, web);
        UniversalEngine e = new UniversalEngine(web);

        urlChangeLis(e, web, engine, urlField, tab, bkmark);

        goBtn.setOnAction((v) -> loadSite(urlField.getText(), e));
        urlField.setOnAction((v) -> loadSite(urlField.getText(), e));

        back.setOnAction((v) -> history(engine, EHistory.BACK));
        forward.setOnAction((v) -> history(engine, EHistory.FORWARD));

        bkmark.setOnAction((v) -> bookmarkAction(e, bmread, ((t) -> createTab(false, engine.getLocation())), bkmark, menuBook));

        // Setting Styles
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        hBox.setHgrow(urlField, Priority.ALWAYS);
        vBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        engine.setUserDataDirectory(saves);
        setUserAgent(engine);
        engine.javaScriptEnabledProperty().set(Options.javascript.b);

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

    public final void urlChangeLis(UniversalEngine u, WebView web, final WebEngine en, final TextField urlField, final Tab tab, Button bkmark) {
        en.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.FAILED) {
                File f = new File(new File(saves, en.getLocation().replaceAll("[ : / . ]", "-").trim()),
                        en.getLocation().replaceAll("[ : / . ]", "-").trim() + ".html");
                if (f.exists()) {
                    try {
                        en.load(f.toURI().toURL().toExternalForm());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                en.loadContent("Unable to load " + en.getLocation().trim());
                return;
            }
        });

        en.locationProperty().addListener((o,oU,nU) -> ZunoZapWebView.this.changed(u, urlField, tab, oU, nU, bkmark, bmread));

        en.setOnAlert((popupText) -> {
            boolean bad = false;
            if (popupText.toString().toLowerCase().contains("virus")) {
                bad = true;
                JOptionPane.showMessageDialog(null, "The site you are visting has tryed to create an popup with the word 'virus' in it, Please be carefull on this site", "ZunoZap AntiPopupVirus", JOptionPane.WARNING_MESSAGE);
            }
            if (allowPluginEvents()) for (PluginBase pl : p.plugins) pl.onPopup(bad);

            JOptionPane.showMessageDialog(null, popupText.getData(), "JS Popup", JOptionPane.INFORMATION_MESSAGE);
        });
        en.titleProperty().addListener((ov, o, n) -> tab.setText(n));
    }

    @Override
    protected void onTabClosed(Object s) {
    }
}