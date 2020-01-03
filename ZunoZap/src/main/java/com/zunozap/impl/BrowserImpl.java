package com.zunozap.impl;

import static com.zunozap.Log.out;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.DownloadHandler;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.zunozap.Engine;
import com.zunozap.Engine.Type;
import com.zunozap.EngineHelper;
import com.zunozap.LoadLis;
import com.zunozap.Settings;
import com.zunozap.Settings.Options;
import com.zunozap.ZFile;
import com.zunozap.ZFullScreenHandler;
import com.zunozap.ZunoAPI;
import com.zunozap.api.Plugin;
import com.zunozap.lang.Lang;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

public class BrowserImpl extends ZunoAPI {

    private static JWindow l = new JWindow();

    private final DownloadHandler dh = new DownloadHandler() {
        @Override public boolean allowDownload(DownloadItem i) { return !isUrlDownload(i.getDestinationFile().getName()); }
    };

    private final PopupHandler ph = new PopupHandler() {
        @Override public PopupContainer handlePopup(PopupParams pp) {
            return new PopupContainer() { @Override public void insertBrowser(Browser b, Rectangle r) {
                createTab(b.getURL());
                b.dispose();
            }};
        }
    };

    @Override
    public void init() throws IOException {
        setInstance(this);

        if (Environment.isMac()) BrowserCore.initialize();
        super.init();
    }

    public static void main(String[] args) throws IOException {
        JLabel z = new JLabel("ZUNOZAP");
        z.setFont(new Font("Dialog", Font.BOLD, 56));
        z.setForeground(Color.WHITE);
        z.setBorder(new EmptyBorder(15,30,15,30));
        l.setBackground(new Color(0,0,0,200));
        l.setContentPane(z);
        l.setVisible(true);
        l.pack();
        l.setLocationRelativeTo(null);

        launch(BrowserImpl.class, args);

        out("Shutting down BrowserCore");
        BrowserCore.shutdown();
        System.exit(0);
    }

    @Override
    public void start(Stage stage, Scene scene, StackPane root, BorderPane border) throws Exception {
        for (int i = 0; i < 10; i++) deleteDirs(new ZFile("engine" + i));

        createTab(Settings.tabPage);
        regMenuItems(menuFile, menuBook, tb, EngineHelper.type);
        menuBar.getMenus().addAll(menuFile, menuBook);

        Settings.set(cssDir, scene);
        Settings.init(cssDir);
        Settings.changeStyle("ZunoZap default");
        Settings.save();
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        if (Settings.en == Type.CHROME) {
            BrowserPreferences.setChromiumDir(data.getAbsolutePath());
            BrowserPreferences.setUserAgent(BrowserPreferences.getUserAgent() + " ZunoZap/" + VERSION);
        }

        p.loadPlugins();
        hookonStart(stage, scene, tb);
        l.setVisible(false);
        l.dispose();
    }

    @Override
    public final void createTab(String url) {
        createTab(url, true);
    }

    public final void createTab(String url, boolean load) {
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

        urlChangeLis(e, field, tab, bkmark);

        goBtn.setOnAction(v -> loadSite(field.getText(), e));
        field.setOnAction(v -> loadSite(field.getText(), e));

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

        if (load)
            loadSite(url, e);

        tab.setContent(vBox);
        tab.setOnCloseRequest(a -> e.stop());

        if (allowPluginEvents()) for (Plugin pl : p.plugins) pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();

        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    public final void urlChangeLis(Engine u, final TextField urlField, final Tab tab, final Button bkmark) {
        if (u instanceof ChromeEngine) {
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
                if (allowPluginEvents()) for (Plugin pl : p.plugins) pl.onPopup(popupText.getData());

                Alert alert = new Alert(AlertType.NONE);
                alert.setTitle("JS Popup");
                alert.setContentText(popupText.getData());
                alert.show();
            });
            en.titleProperty().addListener((ov, o, n) -> tab.setText(n));
        }
    }

}