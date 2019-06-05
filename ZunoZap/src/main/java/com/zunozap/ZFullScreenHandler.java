package com.zunozap;

import static com.zunozap.ZunoAPI.tb;

import com.teamdev.jxbrowser.chromium.FullScreenHandler;
import com.zunozap.impl.ZunoZapChrome;

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
            tb.setVisible(!b);
            StackPane header = (StackPane) ZunoZapChrome.tb.lookup(".tab-header-area");
            if(header != null) header.setPrefHeight(b ? 0 : -1);
            header.setVisible(!b);
            TabPane pane = (TabPane) tb.lookup(".tab-pane");
            pane.setVisible(!b);
            tb.lookup(".tab-header-background").setVisible(false);
            ZunoZapChrome.menuBar.setDisable(b);
            tb.setDisable(b);
            s.setFullScreen(b);
        });
    }

}