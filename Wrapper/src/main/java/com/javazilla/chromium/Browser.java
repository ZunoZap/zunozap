package com.javazilla.chromium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cef.browser.CefBrowser;

import com.javazilla.chromium.internal.CefWebview;
import com.javazilla.chromium.listeners.BrowserListener;
import com.jogamp.opengl.awt.GLJPanel;

@SuppressWarnings("unchecked")
public class Browser {

    private BrowserType type;
    private CefWebview cef;

    @SuppressWarnings("rawtypes")
    private HashMap<Class<?>, List> listeners;

    public String title;

    public Browser(BrowserType type, String url) {
        this.type = type;
        this.listeners = new HashMap<>();

        if (!NativeHelper.loaded) {
            NativeHelper.setupNatives(NativeHelper.getPlatform());
            NativeHelper.blockTillDownloaded();
        }
        CefWebview.setupNatives();

        this.cef = new CefWebview(this, url);
        cef.createCef();
    }

    public Browser() {
        this(BrowserType.LIGHTWEIGHT, "about:blank");
    }

    public Browser(String url) {
        this(BrowserType.LIGHTWEIGHT, url);
    }

    public static void shutdown() {
        for (GLJPanel n : CefWebview.uis) {
            n.removeAll();
            n = null;
        }
        for (CefBrowser b : CefWebview.browsers.keySet()) {
            b.stopLoad();
            b.close(true);
            CefWebview.client.doClose(b);
        }
        CefWebview.client.dispose();
        CefWebview.app.dispose();
        CefWebview.app = null;
    }

    public static boolean isInitialized() {
        return null != CefWebview.app;
    }

    public CefWebview getCef() {
        return cef;
    }

    public BrowserType getType() {
        return type;
    }

    public void loadURL(String url) {
        cef.getCefBrowser().loadURL(url);
    }

    public void loadHTML(String html) {
        cef.getCefBrowser().loadURL(DataUri_create("text/html", html));
    }

    private static String DataUri_create(String mimeType, String contents) {
        return "data:" + mimeType + ";base64," + java.util.Base64.getEncoder().encodeToString(contents.getBytes());
    }

    public String getTitle() {
        return null == title ? getURL() : title;
    }

    public String getURL() {
        return cef.getCefBrowser().getURL();
    }

    /**
     * @deprecated To be replaced with history manager
     */
    @Deprecated
    public void goBack() {
        cef.getCefBrowser().goBack();
    }

    /**
     * @deprecated To be replaced with history manager
     */
    @Deprecated
    public void goForward() {
        cef.getCefBrowser().goForward();
    }

    /**
     * @deprecated To be replaced with history manager
     */
    @Deprecated
    public boolean canGoBack() {
        return cef.getCefBrowser().canGoBack();
    }

    /**
     * @deprecated To be replaced with history manager
     */
    @Deprecated
    public boolean canGoForward() {
        return cef.getCefBrowser().canGoForward();
    }

    public boolean isLoading() {
        return cef.getCefBrowser().isLoading();
    }

    public void stop() {
        cef.getCefBrowser().stopLoad();
    }

    public <T> List<? extends BrowserListener> getListeners(T type) {
        List<? extends BrowserListener> list  = this.listeners.getOrDefault(type, new ArrayList<>());
        return list;
    }

    public <T> void addListener(BrowserListener listener) {
        List<BrowserListener> list = this.listeners.getOrDefault(listener.getType(), new ArrayList<>());
        if (!list.contains(listener)) {
            list.add(listener);
            this.listeners.put(listener.getType(), list);
        }
    }

    public void removeListener(Class<? extends BrowserListener> type, BrowserListener listener) {
        List<BrowserListener> list  = this.listeners.getOrDefault(type, new ArrayList<>());
        if (list.contains(listener)) {
            list.remove(listener);
            this.listeners.put(type, list);
        }
    }

}