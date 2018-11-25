package me.isaiah.zunozap;

import com.teamdev.jxbrowser.chromium.FullScreenHandler;

import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ZFullScreenHandler implements FullScreenHandler {

    private final Stage s;
    public ZFullScreenHandler(Stage s) { this.s = s; }

    @Override public void onFullScreenEnter() { change(true); }
    @Override public void onFullScreenExit() { change(false); }

    public void change(boolean b) {
        Platform.runLater(() -> {
            ZunoZap.tb.setVisible(!b);
            StackPane header = (StackPane) ZunoZap.tb.lookup(".tab-header-area");
            if(header != null) header.setPrefHeight(b ? 0 : -1);
            header.setVisible(!b);
            TabPane pane = (TabPane) ZunoZap.tb.lookup(".tab-pane");
            pane.setVisible(!b);
            ZunoZap.tb.lookup(".tab-header-background").setVisible(false);
            ZunoZap.menuBar.setDisable(b);
            ZunoZap.tb.setDisable(b);
            s.setFullScreen(b);
        });
    }

}