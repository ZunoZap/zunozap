package me.isaiah.zunozap;

import com.teamdev.jxbrowser.chromium.FullScreenHandler;

import javafx.application.Platform;
import javafx.stage.Stage;

public class ZFullScreenHandler implements FullScreenHandler {
    private final Stage s;

    public ZFullScreenHandler(Stage s) {
        this.s = s;
    }

    @Override public void onFullScreenEnter() {
        Platform.runLater(() -> {
            ZunoZap.tb.setVisible(false);
            s.setFullScreen(true);
        });
    }

    @Override public void onFullScreenExit() {
        Platform.runLater(() -> {
            ZunoZap.tb.setVisible(true);
            s.setFullScreen(false);
        });
    }
}