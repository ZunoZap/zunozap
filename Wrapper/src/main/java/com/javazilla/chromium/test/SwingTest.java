package com.javazilla.chromium.test;

import java.awt.BorderLayout;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.javazilla.chromium.Browser;
import com.javazilla.chromium.NativeHelper;
import com.javazilla.chromium.view.SwingBrowserView;

public class SwingTest {

    public static void main(String[] args) throws MalformedURLException {
        JFrame f = new JFrame("Swing Test");
        JPanel p = new JPanel(new BorderLayout());

        NativeHelper.setupNatives(NativeHelper.getPlatform());
        NativeHelper.blockTillDownloaded();

        Browser b = new Browser("http://whatismybrowser.com/");
        SwingBrowserView v = new SwingBrowserView(b);

        p.add(v);
        f.setDefaultCloseOperation(3);
        f.setContentPane(p);
        f.setSize(800,400);
        f.setVisible(true);
    }

}
