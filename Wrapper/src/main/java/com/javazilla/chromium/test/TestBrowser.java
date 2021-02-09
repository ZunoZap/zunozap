package com.javazilla.chromium.test;

import java.io.IOException;

import com.javazilla.chromium.Browser;
import com.javazilla.chromium.listeners.TitleListener;
import com.javazilla.chromium.view.JfxBrowserView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestBrowser extends Application {

    public static TabPane tb;

    public static void main(String[] args) throws IOException {
        launch(TestBrowser.class, args);

        Browser.shutdown();
        System.exit(0);
    }

    @Override
    public void init() throws IOException {
    }

    @Override
    public void start(Stage stage) throws Exception {
        tb = new TabPane();

        StackPane root = new StackPane();
        BorderPane border = new BorderPane();

        root.getChildren().add(border);
        Scene scene = new Scene(root, 900, 500);

        border.setCenter(tb);
        border.autosize();

        Tab newtab = new Tab("+");
        newtab.setClosable(false);
        tb.getTabs().add(newtab);
        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(); });

        createTab();

        System.gc();
        stage.setScene(scene);
        stage.setTitle("Test Browser");
        stage.show();
    }

    public static final void createTab() {
        Tab tab = new Tab("");

        Browser e = new Browser("https://whatismybrowser.com");
        JfxBrowserView view = new JfxBrowserView(e);
        TextField field = new TextField("https://");

        VBox vBox = new VBox(field, view);

        addHandlers(view, field, tab);

        field.setOnAction(v -> e.loadURL(field.getText()));
        VBox.setVgrow(view, Priority.ALWAYS);

        tab.setContent(vBox);
        tab.setOnCloseRequest(a -> e.stop());

        tb.getTabs().add(tab);
        tb.getSelectionModel().select(tab);
    }

    public static final void addHandlers(JfxBrowserView b, final TextField field, final Tab tab) { 
        Browser br = b.getBrowser();
        b.getBrowser().addListener((TitleListener)ev -> {
            Platform.runLater(() -> {
                field.setText(br.getURL().replace("https://",""));
                String n = ev.getTitle();
                tab.setText((n == null) ? br.getURL() : ((n.length() > 30) ? n.substring(0,30) : (n.length() < 20 ? n : n)));
            });
        });
    }

}