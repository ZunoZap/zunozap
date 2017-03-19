package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.sun.javafx.application.LauncherImpl;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import me.isaiah.zunozap.plugin.PluginManager;

/**
 * New ZunoAPI from ZunoZap 0.1.0+
 * 
 * @author Isaiah Patton
 * @since ZunoZap 0.1
 */
public abstract class ZunoAPI extends Application {
    public static String name;
    public static String version;
    
    public static String getVersion() { return version; }
    public final static String aboutPageHTML() {
        return "<header> <h1>About ZunoZap</h1></header>"
                +"<body>"
                +"    ZunoZap is a web browser made with the Java WebView,</p><br>"
                +"    Version: "+System.getProperty("zunozap.version")+"<br>"
                +"    UserAgent: "+ "ZunoZap/0.1 Mozilla/5.0 JavaFX/8.0" +"<br>"
                +"    Java Enabled: true<br>"
                +"    JavaScript Enabled: true<br>"
                +"    Licence: <a href='https://raw.githubusercontent.com/ZunoZap/zunozap/master/LICENSE'>GNU General Public License v3</a>"
                +"    <hr>"
                +"</body>";
    }

    /*Set Style*/ public final static void setStyle(String css, Button...btns) {for (Button b : btns){b.setStyle(css);}}
    /*History  */ public final static void history(WebEngine e, String go) {e.executeScript("history."+go+"();");}
    /*Load Page*/
    public final static void loadSite(String url, WebEngine e) {
        if (forceHTTPS) {
            e.load(url.startsWith("http") ? url : "https://" + url);
        } else {
            e.load(url.startsWith("http") ? url : "http://" + url);
        } 
    }
    public static boolean forceHTTPS = false;
    public int tabnum = 0;

    /**
     * @return HTML code of website. 
     */
    public static String getUrlSource(String site) throws IOException {
        URL url;

        if (site.startsWith("http")) url = new URL(site);
        else url = new URL("http://" + site);
        
        URLConnection urlc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null) a.append(inputLine);
        in.close();

        return a.toString();
    }

    public static void DownloadPage(File theDPfolder, WebEngine we) {
        try{       
            File htmlsourcefile = new File(theDPfolder + File.separator + we.getLocation().replaceAll("[ : / . ]", "-") + ".html");

            if(!htmlsourcefile.exists()){
                try{
                    htmlsourcefile.createNewFile();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }

            URL url = new URL(we.getLocation());

            FileWriter fw = new FileWriter(htmlsourcefile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<!-- HTML source for: " + url.getPath().trim() + "-->");
            bw.newLine();

            URLConnection urlc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                bw.write(inputLine.trim());
                bw.newLine();
            }
            in.close();
            bw.close();
            System.out.println("Downloaded source code for: " + we.getLocation() + " Find it in your ZunoZap folder!");       
       } catch(IOException e) { System.out.println(e); }
   }
}