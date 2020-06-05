package com.zunozap;

import static com.zunozap.ZunoAPI.tb;

import com.teamdev.jxbrowser.chromium.FullScreenHandler;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ZFullScreenHandler implements FullScreenHandler {

    private final Stage s;
    public ZFullScreenHandler(Stage s) { this.s = s; }

    @Override public void onFullScreenEnter() { change(true); }
    @Override public void onFullScreenExit() { change(false); }

    public void change(boolean b) {
        Platform.runLater(() -> {
            tb.setVisible(!b);
            StackPane header = (StackPane) tb.lookup(".tab-header-area");
            if(header != null) header.setPrefHeight(b ? 0 : -1);
            header.setVisible(!b);

            tb.lookup(".tab-header-background").setVisible(!b);
            tb.lookup(".control-buttons-tab").setVisible(!b);
            Tab tab = tb.getSelectionModel().getSelectedItem();
            VBox vbox = (VBox) tab.getContent();
            vbox.autosize();
            HBox hbox = (HBox) vbox.getChildren().get(0);
            hbox.setVisible(!b);

            s.setFullScreen(b);
            tab.getContent().autosize();
        });
    }

}