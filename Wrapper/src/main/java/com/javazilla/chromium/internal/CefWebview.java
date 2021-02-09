package com.javazilla.chromium.internal;

import java.awt.BorderLayout;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefDownloadHandler;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.cef.network.CefRequest.ResourceType;

import com.javazilla.chromium.Browser;
import com.javazilla.chromium.BrowserPreferences;
import com.javazilla.chromium.NativeHelper;
import com.javazilla.chromium.events.FinishLoadingEvent;
import com.javazilla.chromium.events.ResourceRequestEvent;
import com.javazilla.chromium.events.StartLoadingEvent;
import com.javazilla.chromium.events.TitleEvent;
import com.javazilla.chromium.listeners.BrowserListener;
import com.javazilla.chromium.listeners.LoadListener;
import com.javazilla.chromium.listeners.ResourceRequestListener;
import com.javazilla.chromium.listeners.TitleListener;
import com.jogamp.opengl.awt.GLJPanel;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class CefWebview {

    public Parent root;
    public CefBrowser browser;
    private Browser jx;

    public static CefSettings settings;
    public static CefApp app;
    public static CefClient client;
    public static HashMap<CefBrowser, Browser> browsers = new HashMap<>();
    public static List<GLJPanel> uis = new ArrayList<>();
    private String url;

    public static List<String> ADBLOCK = new ArrayList<>();

    public CefWebview(Browser jx, String url) {
        this.jx = jx;
        this.url = url;
    }

    static {
        ADBLOCK.add("doubleclick.net");
        ADBLOCK.add("pagead2.googlesyndication.com");
        ADBLOCK.add("p.adsymptotic.com");
        ADBLOCK.add("pixel.advertising.com");
        ADBLOCK.add("s.amazon-adsystem.com");
        ADBLOCK.add("adblockanalytics.com");
        ADBLOCK.add("adgrowmedia.com");
    }

    public static void setupNatives() {
        try {
            String libPath = System.getProperty("java.library.path");
            if (!libPath.contains(NativeHelper.CEF_PATH))
                addLibraryDir(NativeHelper.CEF_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CefBrowser getCefBrowser() {
        return browser;
    }

    public void createCef() {
        if (client == null) {
            BrowserPreferences.initDefault();
            if (null == app)
                app = CefApp.getInstance(BrowserPreferences.getSwitches(), BrowserPreferences.settings);
            client = app.createClient();
        }

        browser = client.createBrowser(url, true, false);
        browsers.put(browser, jx);

        client.addRequestHandler(new ReqHandler());
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onTitleChange(CefBrowser browser, String title) {
                Browser b = browsers.get(browser);
                b.title = title;
                TitleEvent event = new TitleEvent(b, title);
                b.getListeners(TitleListener.class).forEach(listener -> ((TitleListener) listener).onTitleChange(event));
            }
        });
        client.addDownloadHandler(new CefDownloadHandler() {

            @Override
            public void onBeforeDownload(CefBrowser b, CefDownloadItem di, String name, CefBeforeDownloadCallback cb) {
                cb.Continue(name, true);
            }

            @Override
            public void onDownloadUpdated(CefBrowser b, CefDownloadItem di, CefDownloadItemCallback cb) {
            }
            
        });

        client.addLoadHandler(new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
            }

            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType type) {
                Browser b = browsers.get(browser);
                long parentId = frame.getParent() == null ? browser.getMainFrame().getIdentifier() : frame.getParent().getIdentifier();
                StartLoadingEvent event = new StartLoadingEvent(b, frame.getIdentifier(), parentId, frame.getParent() == null, frame.getURL(), false);
                for (BrowserListener l : b.getListeners(LoadListener.class))
                    ((LoadListener) l).onStartLoadingFrame(event);
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                Browser b = browsers.get(browser);
                FinishLoadingEvent event = new FinishLoadingEvent(b, frame.getIdentifier(), frame.getParent() == null, frame.getURL());
                for (BrowserListener l : b.getListeners(LoadListener.class))
                    ((LoadListener) l).onFinishLoadingFrame(event);
            }

            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                // TODO FailLoadingEvent
            }
        });
    }

    public static void disposeCef() {
        app.dispose();
    }

    public CefSettings getSettings() {
        return settings;
    }

    public boolean sc = false;
    public void initNode(AnchorPane cefPane) {
        setupNatives();

        SwingNode swingNode = new SwingNode();
        GLJPanel browserUi = (GLJPanel) browser.getUIComponent();
        uis.add(browserUi);
        cefPane.getChildren().add(swingNode);
        AnchorPane.setTopAnchor(swingNode, 0d);
        AnchorPane.setBottomAnchor(swingNode, 0d);
        AnchorPane.setRightAnchor(swingNode, 0d);
        AnchorPane.setLeftAnchor(swingNode, 0d);
        swingNode.setContent(browserUi);
    }

    public void initSwing(JPanel panel) {
        setupNatives();

        GLJPanel browserUi = (GLJPanel) browser.getUIComponent();
        uis.add(browserUi);
        panel.add(browserUi, BorderLayout.CENTER);
    }

    public class ReqHandler extends CefRequestHandlerAdapter {
        @Override
        public CefResourceRequestHandler getResourceRequestHandler(CefBrowser br, CefFrame fr,
                CefRequest req, boolean isNav, boolean isDwnload, String init, BoolRef disDefHandle) {
            return new AdBlockRequestHandler();
        }
    }

    private String getHost(String s, boolean base) {
        if (!s.startsWith("http")) return s;

        String h = s.substring(s.indexOf("//")+2);
        h = h.substring(0, h.indexOf("/"));
        if (base)
            while (h.indexOf('.') != h.lastIndexOf('.'))
                h = h.substring(h.indexOf('.')+1);
        return h;
    }

    public class AdBlockRequestHandler extends CefResourceRequestHandlerAdapter {
        @SuppressWarnings("deprecation")
        @Override
        public boolean onBeforeResourceLoad(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest request) {
            String host = getHost(request.getURL(), false);
            String host2 = getHost(request.getURL(), true);

            if (BrowserPreferences.shouldBlockRemoteFonts() && 
                    (request.getResourceType() == ResourceType.RT_FONT_RESOURCE || request.getURL().endsWith(".woff2"))) {
                request.setURL("https://adblock_block-sdjdjfbskdfb-fonnt.com/");
                Browser b = browsers.get(browser);
                ResourceRequestEvent ev = new ResourceRequestEvent(b, request);
                for (BrowserListener l : b.getListeners(ResourceRequestListener.class)) {
                    ((ResourceRequestListener) l).onBeforeResourceLoad(ev);
                }
                return false;
            }

            if (!BrowserPreferences.isJavaScriptEnabled() && request.getResourceType() == ResourceType.RT_SCRIPT) {
                if (!(host2.equalsIgnoreCase("youtube.com") && request.getURL().contains("jsbin")))
                    request.setURL("https://adblock_block-sdjdjfbskdfb-js.com/");
            }
            for (String ad : ADBLOCK) {
                if (host.startsWith(ad) || host.contains("doubleclick.net") || host2.startsWith(ad)) {
                    // Set to invalid URL to stop request
                    request.setURL("https://adblock_block-sdjdjfbskdfb.com/");
                    return false;
                }
            }
            Browser b = browsers.get(browser);
            ResourceRequestEvent ev = new ResourceRequestEvent(b, request);
            for (BrowserListener l : b.getListeners(ResourceRequestListener.class))
                ((ResourceRequestListener) l).onBeforeResourceLoad(ev);
            return false;
        }
    }

    private static void addLibraryDir(String libraryPath) throws Exception {
        Field userPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        userPathsField.setAccessible(true);
        String[] paths = (String[]) userPathsField.get(null);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            if (libraryPath.equals(paths[i])) continue;
            sb.append(paths[i]).append(File.pathSeparatorChar);
        }
        sb.append(libraryPath);
        System.setProperty("java.library.path", sb.toString());
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }

}