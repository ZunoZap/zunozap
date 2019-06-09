package com.zunozap.extra;

import static com.zunozap.Log.err;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.SavePageType;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.zunozap.LoadLis;
import com.zunozap.Settings.Options;
import com.zunozap.UniversalEngine;
import com.zunozap.ZFile;
import com.zunozap.ZunoAPI;
import com.zunozap.api.Plugin;
import com.zunozap.api.PluginInfo;

import javafx.concurrent.Worker;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @TODO Move into a seperate plugin from the browser
 */
@PluginInfo(name = "OfflinePageDownloader", version = "1.0")
public class OfflineDownloader extends Plugin {

    private ZFile saves = new ZFile("offline-pages"), temp = new ZFile("temp");

    @Override
    public void onURLChange(UniversalEngine e, TextField field, Object o, URL n) {
        Object x = e.getImplEngine();
        if (!Options.offlineStorage.b)
            return;

        if (x instanceof WebView) {
            WebEngine en = ((WebView)x).getEngine();
            en.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
                if (newState == Worker.State.FAILED) {
                    File f = new File(new File(saves, en.getLocation().replaceAll("[ : / . ]", "-").trim()),
                            en.getLocation().replaceAll("[ : / . ]", "-").trim() + ".html");
                    if (f.exists()) {
                        try {
                            en.load(f.toURI().toURL().toExternalForm());
                        } catch (MalformedURLException ex) { ex.printStackTrace(); }
                        return;
                    }
                    en.loadContent("Unable to load " + en.getLocation().trim());
                    return;
                }
            });
            new Thread(() -> downloadPage(saves, temp, e.getURL(), true)).start();
        }

        if (x instanceof Browser) {
            Browser b = (Browser) x;
            b.addLoadListener(new LoadLis() {
                @Override public void onFinishLoadingFrame(FinishLoadingEvent e) {
                    String url = e.getBrowser().getURL();

                    if (Options.offlineStorage.b && !url.contains("mail")) new Thread(() -> {
                        File s = new File(saves, url.replaceAll("[ : / . ? ]", "-"));
                        s.mkdir();
                        downloadPage(saves, temp, url, false);
                        b.saveWebPage(url.replaceAll("[ : / . ? ]", "-"), s.getPath(), SavePageType.COMPLETE_HTML);
                    }).start();
                }
            });
        }
    }

    public static void downloadPage(File dp, File temp, String loc, boolean all) {
        try {
            String regex = "[ : / . ? ]";
            File html = new File(temp, loc.replaceAll(regex, "-").trim() + ".html");

            try {
                Files.write(Paths.get(html.toURI()), ZunoAPI.getUrlSource(loc.trim()).getBytes());
            } catch (NullPointerException e) {}
            out("Downloaded " + loc);
            if (html.length() > 5) {
                File hsdp = new File(new File(dp, loc.replaceAll(regex, "-").trim()), loc.replaceAll(regex, "-").trim() + ".html");
                hsdp.getParentFile().mkdirs();
                hsdp.delete();
                if (all) downloadAssetsFromPage(loc.trim(), hsdp.getParentFile());
                if (hsdp.exists()) hsdp.delete();
                Files.move(Paths.get(html.toURI()), Paths.get(hsdp.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } else html.delete();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void downloadAssetsFromPage(String site, File f) {
        new Thread(() -> { try { downloadAssetsFromPage0(site, f); } catch (IOException e) { e.printStackTrace(); }}).start();
    }

    public static void downloadAssetsFromPage0(String site, File folder) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(site).openConnection().getInputStream()));

        HTMLDocument doc = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        HTMLEditorKit.ParserCallback callback = doc.getReader(0);
        new ParserDelegator().parse(br, callback, true);

        for (HTMLDocument.Iterator iterator = doc.getIterator(HTML.Tag.IMG); iterator.isValid(); iterator.next()) {
            String src = (String) iterator.getAttributes().getAttribute(HTML.Attribute.SRC);
            if (src != null) downloadAsset(site, src, folder); else err.log("null source");
        }

        for (HTMLDocument.Iterator iterator = doc.getIterator(HTML.Tag.LINK); iterator.isValid(); iterator.next()) {
            String src = (String) iterator.getAttributes().getAttribute(HTML.Attribute.HREF);
            if (src != null) downloadAsset(site, src, folder); else err.log("null source");
        }
    }

    private static void downloadAsset(String url, String src, File folder) {
        try {
            File file = new File(folder, src);
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.write(Paths.get(file.toURI()), ZunoAPI.getUrlSource(src.startsWith("http") ? src : url + src).getBytes());
        } catch (IOException e) { e.printStackTrace(); }
    }

}