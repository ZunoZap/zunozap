package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

/**
 * New ZunoAPI from ZunoZap 0.1.0+
 *  Extend this class then call {@link #launch()} to launch your program.
 * 
 * @author Isaiah Patton
 * @since ZunoZap 0.1
 */
public abstract class ZunoAPI extends Application {
    public String name;
    public static String version;
    public boolean isOutdated = false;
    public static boolean blockPluginEvents = false;
    public static boolean createPluginDataFolders = true;
    public static boolean useDuck = true; // DuckDuckGO vs Google

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public final String aboutPageHTML() {
        return "<header> <h1>About ZunoZap</h1></header>"
                +"<body>"
                +"    ZunoZap is a web browser made with the Java WebView,</p><br>"
                +"    Version: "+getVersion()+"<br>"
                +"    UserAgent: %s<br>"
                +"    Java Enabled: true<br>"
                +"    JavaScript Enabled: %s<br>"
                +"    Licence: <a href='https://raw.githubusercontent.com/%s'>%s</a>"
                +"    <hr>"
                +"</body>";
    }

    public static void setUserAgent(WebEngine e) {
        if (!e.getUserAgent().contains("ZunoZap")) e.setUserAgent(e.getUserAgent() + " ZunoZap/0.1");

        System.out.println(e.getUserAgent());
    }

    /**
     * Set the style of multiple {@link javafx.scene.control.Button Button}s at one time.
     */
    public final static void setStyle(String css, Button...btns) {for (Button b : btns){b.setStyle(css);}}
    /**
     * Change the browser history.
     */
    public final static void history(WebEngine e, String go) {e.executeScript("history."+go+"();");}
    /*Load Page*/
    public static final void loadSite(String url, WebEngine e) {
        if (url.startsWith("zunozap:")) {
            if (url.substring(8).startsWith("update"))
                e.loadContent(Updater.browser(version, url));
            else if (url.substring(8).startsWith("home"))
                e.load("https://zunozap.github.io/");

            return;
        }

        if ((url.replaceAll("[ . ]", "").equalsIgnoreCase(url.replaceAll(" ", "")))) {
            if (!useDuck) e.load("https://google.com/search?q=" + url.replace(" ", "%20"));
            else e.load("https://duckduckgo.com/?q=" + url.replace(" ", "%20")); 

            return;
        }

        if (forceHTTPS) e.load(url.startsWith("http") ? url : "https://" + url);
        else e.load(url.startsWith("http") ? url : "http://" + url);
    }
    public static boolean forceHTTPS = false;
    public int tabnum = 0;

    public static String getUrlSource(String url) throws IOException {
        URLConnection urlc = new URL(url).openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null) a.append(inputLine);
        in.close();
        return a.toString();
    }

    public static void DownloadPage(File dp, WebEngine w) {
        try{       
            File htmlsrc = new File(
                    dp + File.separator + w.getLocation().replaceAll("[ : / . ]", "-").trim() + ".html");
            if (!htmlsrc.exists())
                htmlsrc.createNewFile();

            FileWriter fw = new FileWriter(htmlsrc.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<!--" + w.getLocation().trim() + "-->");
            bw.newLine();

            URLConnection urlc = new URL(w.getLocation()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                bw.write(inputLine.trim());
                bw.newLine();
            }
            in.close();
            bw.close();
            System.out.println("Downloaded " + w.getLocation().trim());
       } catch(IOException e) { System.out.println(e); }
   }

    public boolean allowPluginEvents() {
        return !blockPluginEvents;
    }

    public void showMessage(String message) {
        showMessage(message, 1);
    }

    public void showMessage(String message, int type) {
        JOptionPane.showMessageDialog(null, message, name, type);
    }

    public final static void getOptionMenuAction(EOption eOption, boolean b) {
        switch (eOption) {
            case blockEventCalls:
                blockPluginEvents = b;
                break;
            case forceHTTPS:
                forceHTTPS = b;
                break;
            case createPluginDataFolders:
                createPluginDataFolders = b;
                break;
            case useDuckDuckGo:
                useDuck = b;
            default:
                break;
        }
    }

    @Override
    public void init() {
        System.out.println("[ZunoAPI] Launching wrapped application...");
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        BorderPane borderPane = new BorderPane();

        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 1200, 600);

        name = getProgramInfo().name;
        version = getProgramInfo().version;

        start(stage, scene, root, borderPane);

        stage.setTitle(name + " v" + version);
        stage.setScene(scene);
        stage.show();
    }

    public abstract ProgramInfo getProgramInfo();
    public abstract void start(Stage stage, Scene scene, Group root, BorderPane borderPane);
}