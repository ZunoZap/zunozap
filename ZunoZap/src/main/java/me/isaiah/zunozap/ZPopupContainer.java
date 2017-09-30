package me.isaiah.zunozap;

import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class ZPopupContainer implements PopupContainer {
    @Override
    public void insertBrowser(Browser arg0, Rectangle arg1) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.add(new BrowserView(arg0));
        f.setName("Popup");
        f.pack();
        f.setVisible(true);
    }
}