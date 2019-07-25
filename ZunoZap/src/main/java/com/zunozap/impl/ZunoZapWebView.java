package com.zunozap.impl;

import com.zunozap.Info;
import com.zunozap.Settings;
import com.zunozap.Settings.Options;
import com.zunozap.UniversalEngine;
import com.zunozap.UniversalEngine.Engine;
import com.zunozap.ZunoAPI;
import com.zunozap.api.Plugin;
import com.zunozap.lang.Lang;

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
import javafx.scene.web.WebView;
import javafx.stage.Stage;

@Info(engine = Engine.WEBKIT)
public class ZunoZapWebView extends ZunoAPI {

    public static void main(String[] args) {
        setInstance(new ZunoZapWebView());
        launch(ZunoZapWebView.class, args);
    }

    @Override
    public void start(Stage stage, Scene scene, StackPane root, BorderPane border) throws Exception {
        WebView dummy = new WebView();
        setUserAgent(dummy.getEngine());
        regMenuItems(menuFile, menuBook, aboutPageHTML(dummy.getEngine().getUserAgent(), "N/A"), tb, Engine.WEBKIT);
        menuBar.getMenus().addAll(menuFile, menuBook);
        Settings.set(cssDir, scene);
        Settings.init(cssDir);
        Settings.changeStyle("ZunoZap default");
        Settings.save();
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        p.loadPlugins();
        if (allowPluginEvents()) for (Plugin pl : p.plugins) pl.onLoad(stage, scene, tb);
    }

    @Override
    @SuppressWarnings("static-access") 
    public final void createTab(String url) {
        int tabnum = tb.getTabs().size() + 1;

        // Create Tab
        final Tab tab = new Tab(Lang.LOAD.tl);
        tab.setTooltip(new Tooltip("Tab " + tabnum));
        tab.setId("tab-"+tabnum);

        final Button back = new Button("<"), forward = new Button(">"), goBtn = new Button("Go"), bkmark = new Button("\u2606");
        Lang.b(() -> goBtn.setText(Lang.GO.tl));

        WebView web = new WebView();
        WebEngine engine = web.getEngine();
        TextField urlField = new TextField("http://");
        HBox hBox = new HBox(back, forward, urlField, goBtn, bkmark);
        VBox vBox = new VBox(hBox, web);
        UniversalEngine e = new UniversalEngine(web);

        urlChangeLis(e, web, engine, urlField, tab, bkmark);

        goBtn.setOnAction(v -> loadSite(urlField.getText(), e));
        urlField.setOnAction(v -> loadSite(urlField.getText(), e));

        back.setOnAction(v -> history(engine, "back"));
        forward.setOnAction(v -> history(engine, "forward"));

        bkmark.setOnAction(v -> bookmarkAction(e, bmread, (t -> createTab(engine.getLocation())), bkmark, menuBook));

        // Setting Styles
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        hBox.setHgrow(urlField, Priority.ALWAYS);
        vBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        engine.setUserDataDirectory(data);
        setUserAgent(engine);
        engine.javaScriptEnabledProperty().set(Options.javascript.b);

        loadSite(url, e);

        tab.setContent(vBox);

        tab.setOnCloseRequest(a -> onTabClosed(a.getSource()));

        if (allowPluginEvents()) for (Plugin pl : p.plugins) pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    @SuppressWarnings("deprecation")
    public final void urlChangeLis(UniversalEngine u, WebView web, final WebEngine en, final TextField urlField, final Tab tab, Button bkmark) {

        en.locationProperty().addListener((o,oU,nU) -> ZunoZapWebView.this.changed(u, urlField, tab, oU, nU, bkmark, bmread));

        en.setOnAlert(popupText -> {
            if (allowPluginEvents()) for (Plugin pl : p.plugins) pl.onPopup(popupText.getData());

            Alert alert = new Alert(AlertType.NONE);
            alert.setTitle("JS Popup");
            alert.setContentText(popupText.getData());
            alert.show();
        });
        en.titleProperty().addListener((ov, o, n) -> tab.setText(n));
    }

    @Override
    protected void onTabClosed(Object s) {
        ((WebView) ((VBox) ((Tab) s).getContent()).getChildren().get(1)).getEngine().loadContent("Closing");
    }

}