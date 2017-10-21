package me.isaiah.zunozap;

import java.awt.Rectangle;

import javax.swing.JFrame;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class ZPopupContainer implements PopupContainer {
    @Override public void insertBrowser(Browser b, Rectangle r) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(2);
        f.add(new BrowserView(b));
        f.setName("Popup");
        f.pack();
        f.setVisible(true);
    }
}