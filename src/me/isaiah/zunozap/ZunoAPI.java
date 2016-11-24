package me.isaiah.zunozap;

import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;

/**
 * New ZunoAPI from ZunoZap 0.1.0+
 * 
 * @author Isaiah Patton
 * @since ZunoZap v0.1.0
 * 
 * @see {@link me.isaiah.zunozap.old.ZunoAPI}
 */
public class ZunoAPI {
    public static String version = System.getProperty("zunoapi.version");
    
    public final static String aboutPageHTML() {
        return   "<html>"
                +"<header><h1>About ZunoZap</h1></header>"
                +"<body>"
                +"    ZunoZap is a web browser made with the Java WebView,</p><br>"
                +"    Version: "+System.getProperty("zunozap.version")+"<br>"
                +"    UserAgent: "+ "ZunoZap/1.0 QupZilla/2.0.1 Mozilla/5.0 JavaFX/8.0" +"<br>"
                +"    Java Enabled: true<br>"
                +"    JavaScript Enabled: true"
                +"</body>"
                +"</html>";
    }
    /*Set Style  */ public final static void setStyle(String fxcss, Button...buttons) {for (Button b : buttons) {b.setStyle(fxcss);}}
    /*History    */ public final static void history(WebEngine we, String go) {we.executeScript("history."+go+"();");}
    /*Load Page  */ public final static void loadSite(String url, WebEngine WE) {WE.load(url.startsWith("http") ? url : "http://" + url); }
}


/**
 * @author Isaiah Patton
 */
interface ZunoUtils {
    
    /**
     * The version of the program
     */
    public abstract String getVersion();
}
