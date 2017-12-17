package me.isaiah.zunozap;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class Universal {
    private UniversalEngine e;
    public Universal(UniversalEngine e) {
        this.e = e;
    }

    public void bookmarkAction(Reader bmread, EventHandler<ActionEvent> value, Button b, Menu m) {
        String title = (e.getTitle() != null ? e.getTitle() : e.getURL());
        if (!bmread.bm.containsKey(title)) {
            bmread.bm.put(e.getTitle(), e.getURL());
            try {
                bmread.refresh();
            } catch (IOException ex) { ex.printStackTrace(); }
            MenuItem it = new MenuItem(title);
            it.setOnAction(value);
            m.getItems().add(it);
            b.setText("Unbookmark");
        } else {
            bmread.bm.remove(e.getTitle());

            try {
                bmread.refresh();
                bmread = new Reader(m);
                bmread.readd();
            } catch (IOException ex) { ex.printStackTrace(); }
            b.setText("Bookmark");
        }
    }
}
