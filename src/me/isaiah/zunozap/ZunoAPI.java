package me.isaiah.zunozap;

import javafx.scene.control.TextField;
import javafx.scene.web.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ZunoAPI {
    /*
    How to use:
        Create the Browser:
            ZunoAPI.createBrowser();
        Load a page: 
            ZunoAPI.loadPage("http://yoursite.com");
        Get a page's source code:
            ZunoAPI.getUrlSource("http://yoursite.com");
    */
    

    public ZunoAPI() {
        System.out.println("[ZunoZap] This program uses the ZunoAPI (ZunoZap API)");
    }
    
    public String getAPIVersion() {
    	return "ZunoAPI 0.0.5";
    }
    
    public static ZunoAPI getAPI() {
		return null;
    }

    public static void SetupZunoZapFiles(File folder, File DPfolder, File programsettings){
    	if (!folder.exists()) {
            folder.mkdir();
        }

        if (!DPfolder.exists()) {
            DPfolder.mkdir();
        }
    
        try {
        	if (!programsettings.exists()) {
        		System.out.println("Creating " + folder + "settings.txt");
            	programsettings.createNewFile();
        	}
        } catch (IOException e) {
        	System.out.println(e.toString());
        }
    }
    
    

	// Getting an "this can not be used in non-static content" error? then put NonStatic at the end, ex: API.createBrowserNonStatic();
    
    public static WebEngine ZunoZapEngine;

    public static void createBrowser() {
        WebView ZunoZap = new WebView();
        ZunoZapEngine = ZunoZap.getEngine();
    }
    
    public static void createBrowserNonStatic() {
        WebView ZunoZap = new WebView();
        ZunoZapEngine = ZunoZap.getEngine();
    }

    public static void loadPage(String url) {
        ZunoZapEngine.load(url);
        System.out.println(ZunoZapEngine.getDocument().toString());
    }
    
    public void loadPageNonStatic(String url) {
        ZunoZapEngine.load(url);
    }
    
    public static String getUrlSource(String site) throws IOException {
        URL url;
        if (site.contains("http://") || site.contains("https://")) {
            url = new URL(site);
        } else if (site.contains("zunozap:")) {
        	url = new URL("http://zunozap.github.io");
        } else {
            url = new URL("http://" + site);
        }
        
        URLConnection urlc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
        urlc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        a.append(inputLine);
        in.close();

        return a.toString();
    }
    public static void DownloadPage(String site, TextField addressBar, File DPfolder) {        
        try{       
        	File htmlsourcefile = new File(DPfolder + File.separator + addressBar.getText().replaceAll("[ / . ]", "-") + ".html");

            if (!htmlsourcefile.exists()) {
            	htmlsourcefile.createNewFile();
            }
            FileWriter fw = new FileWriter(htmlsourcefile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<!--");
            bw.newLine();
            bw.write("HTML source for: " + addressBar.getText());
            bw.newLine();
            bw.write("By ZunoZap Web Browser's Download Page Source");
            bw.newLine();
            bw.write("-->");
            bw.newLine();
            bw.write(getUrlSource(addressBar.getText()));
            bw.close();
            System.out.println("Downloaded source code for: " + addressBar.getText() + " Find it in your ZunoZap folder!");       
       } catch(IOException ioe) {
           System.out.println(ioe);
       }
   }
    public static void loadStartPage(WebEngine ZunoZapEngine) {
    	ZunoZapEngine.load("http://zunozap.github.io");
    }
    public static void loadAPI() {
	  	@SuppressWarnings("unused")
		ZunoAPI api = new ZunoAPI();
    }
}