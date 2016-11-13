package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// New ZunoAPI from ZunoZap 0.1.0+
public class ZunoAPI extends ZunoAPI_DEPRECATED {
	public static String version = System.getProperty("zunoapi.version");
	
	public static String aboutPageHTML() {
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
    /*Set Style  */ public static void setStyle(String fxcss, Button...buttons) {for (Button b : buttons) {b.setStyle(fxcss);}}
    /*History    */ public static void history(WebEngine we, String go) {we.executeScript("history."+go+"();");}
    /*Load Page  */ public static void loadSite(String url, WebEngine WE) {WE.load(url.startsWith("http://") ? url : "http://" + url); }
}

// Old ZunoAPI from ZunoZap 0.0.1 - 0.0.5
@Deprecated
class ZunoAPI_DEPRECATED {
	
	public static String version = "0.0.5_DEPRECATED"; //This is the ZunoAPI version
	public static WebView WebView = new WebView();
	public static WebEngine WebEngine = WebView.getEngine();
	public static double MTH;
	
	//private static File DPfolder = FileManager.DPfolder;
  	private static File DataFolder = FileManager.DataFolder;
  	public static TextField addressBar = new TextField();

  	protected static Button googleButton = new Button("Google");
	protected static Button sourcebutton = new Button("Download Page Source");
  	protected static Button aboutbutton = new Button("About ZunoZap");
  	protected static Button aboutAPIbutton = new Button("About the ZunoAPI");
    protected static Button enableTools = new Button("Enable Styles");
  	protected static Button GOButton = new Button("GO");
  	protected static Button BackButton = new Button("Back");
  	protected static Button ForwardButton = new Button("Forward");
  	
  	
  	static boolean toolsEnabled = false;
  	
	public ZunoAPI_DEPRECATED() {
		if (Main.name != "ZunoZap") {
			System.out.println("[ZunoAPI] This app uses the ZunoAPI v"+version);
		}
		FileManager.loadManager();
		WebEngine.setUserAgent("ZunoZap/1.0 ZunoAPI/1.0 QupZilla/2.0.1 "+WebEngine.getUserAgent());
		WebEngine.setUserDataDirectory(DataFolder);
		
		lis();
		
	}
	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	public static void aboutPage() {
		//String ABH = ZunoAPI_Extra.aboutPageHTML();
		//LogManager.blankDialog("About", ABH);
	}
    
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * @return WebView.getEngine()
	 * */
    public static WebEngine getWebEngine() { return WebEngine; }
    
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * @return WebView
	 * */
    public static WebView getWebView() { return WebView; }
    
    
    
    /**
	 * @author Isaiah Patton
	 * @return WebEngine.load(url);
	 * @since 0.2
	 * */
    public static void loadPage(String url) {
    	if (!url.contains(".")) {
    		//This does not work, it should load google if the url does not have a dot in it
    		WebEngine.load("https://www.google.com/?gws_rd=ssl#q="+ addressBar.getText().replaceAll(" ", "+"));
    	}
    	WebEngine.load(url);
    }
    
    
    /**
     * @author Isaiah Patton
     * @decripion Gets the web sites source code
     * @since 0.2
     * */
    
    public static String getUrlSource(String site) throws IOException {
        URL url;
        if (site.contains("http://") || site.contains("https://")) { url = new URL(site); }
        else if (site.contains("zunozap:")) { url = new URL("http://zunozap.github.io"); }
        else {url = new URL("http://" + site);}
        
        URLConnection urlc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        a.append(inputLine);
        in.close();

        return a.toString();
    }
    
    
    
    
    /**
     * @author Isaiah Patton
     * 
     * @decripion Uses the getURLSource() and puts the url's source in to a HTML file
     * @see {@link DPfolder}
     * @since 0.2
     * */
    public static void DownloadPage(String site, File theDPfolder) {
        try{       
        	File htmlsourcefile = new File(theDPfolder + File.separator + WebEngine.getLocation().replaceAll("[ / . ]", "-") + ".html");
            checkIfExists(htmlsourcefile);
            FileWriter fw = new FileWriter(htmlsourcefile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<!--"); bw.newLine(); bw.write("HTML source for: " + WebEngine.getLocation());
            bw.newLine(); bw.write("By ZunoZap Web Browser's Download Page Source"); bw.newLine();
            bw.write("-->");
            bw.newLine(); bw.write(getUrlSource(WebEngine.getLocation())); bw.close();
            System.out.println("Downloaded source code for: " + site + " Find it in your ZunoZap folder!");       
       } catch(IOException ioe) { System.out.println(ioe); }
   }

    
    
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
    public static void CreateRootVBox(VBox root, WebView browser, HBox... boxes) {
    	root.getChildren().addAll(boxes);
    	root.getChildren().add(browser);
    }
    
    
	/**
	 * @author Isaiah Patton
	 * @since 1.0-DevTest
	 * */
    public static void checkIfExists(File file){if(file.exists()){try{file.createNewFile();}catch(IOException e){}}}
    
    
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * @deprecated
	 * */
    public static void setOnActions(Stage stage, TextField addressBar, Button googleBar, Button GOButton, Button BackButton, Button ForwardButton, Button enableTools) {
    	Main.setOnActions(stage);
    }
    
    
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
    public static void setOnActions(Stage stage) {
    	Main.setOnActions(stage);
    }

    
    
	/**
	 * @author Isaiah Patton
	 * @since 0.5
	 * */
    
	public static void loadStartPage() { getWebEngine().load("http://zunozap.github.io/startpage"); }
	
	/**
	 * @author Isaiah Patton
	 * @since 0.5
	 * */
	public static void loadAPI() { new ZunoAPI(); }
	
	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	
	public static void loadPageFromAddressBar(Stage stage, TextField addressBar) {
    	if (addressBar.getText().contains("http://") || addressBar.getText().contains("https://") || addressBar.getText().contains("file://") || addressBar.getText().contains("zunozap:")) {
            if (addressBar.getText().contains("zunozap:start") || addressBar.getText().contains("zunozap://start")) {
            	loadPage("http://zunozap.github.io");
            } else if (addressBar.getText().contains("zunozap:") && addressBar.getText().contains("about")) {
                aboutPage();
            }
        } else { loadPage("http://" + addressBar.getText()); }
    }

	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	
	public static void lis() {
		WebView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
		    @Override
		    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
		        if (newValue != Worker.State.SUCCEEDED && newValue != Worker.State.FAILED) {
		             return;
		        }
		        if (newValue == Worker.State.FAILED) {
		        	Main.pageChangeError();
		        }
		        
		        Main.addressBar.setText(WebEngine.getLocation());
		    }
		});
	}
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * 
	 * */
	
	@SuppressWarnings("static-access")
	public static void setMargins(HBox box, Insets insets, Node n1, Node n2) {
    	box.setMargin(n1, insets);
    	box.setMargin(n2, insets);
    }
	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	@SuppressWarnings("static-access")
	public static void setMargins(HBox box, Insets insets, Insets insets2, Node n1, Node n2) {
    	box.setMargin(n1, insets);
    	box.setMargin(n2, insets2);
    }
	
	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	@SuppressWarnings("static-access")
	public static void setMargin(HBox box, Insets insets, Node n1) {
    	box.setMargin(n1, insets);
    }
	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	public static void setAlined(HBox box, Pos type) {
		box.setAlignment(type);
        box.autosize();
	}
	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	public static void allAllKids(HBox box, Node... ns) {
		box.getChildren().addAll(ns);
	}
	
	
	/**
	 * @author Isaiah Patton
	 * @since 1.0
	 * */
	public static void addStylesheet() {
		try {
			WebView.getStylesheets().add(FileManager.UASS.toURI().toURL().toString());
			ZunoTools.addStyleCSSFileToButtons(FileManager.UASS.toURI().toURL().toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
	
	
	@Deprecated
	public static void loadPagePGL(Stage stage, String url, TextField addressBar) {
    	ZunoAPI.loadPage(url);
        stage.setTitle("ZunoZap " + Main.version +" - "+ WebEngine.getTitle() + " (" + WebEngine.getLocation() + ")");
	}
    @Deprecated
    public static void DownloadPage(String site, TextField addressBar, File DPfolder) {ZunoAPI.DownloadPage(site, DPfolder);}

    public static void HPLS() {
       	for (int i = 0; i < ImgButtons.captions.length; i++) {
        	Hyperlink hpl = Main.hpls[i] = new Hyperlink(ImgButtons.captions[i]);
        	Image image = Main.images[i] = new Image(Main.loader.getResourceAsStream(ImgButtons.imageFiles[i]));
        	hpl.setGraphic(new ImageView(image)); final String url = ImgButtons.urls[i];
        	
        	hpl.setOnAction(new EventHandler<ActionEvent>() {@Override public void handle(ActionEvent e) {
        		ZunoAPI.loadPage(url); ZunoAPI.addressBar.setText(url); }
        	});
        }
    }
	public static void googlePage(String text) {
		ZunoAPI.loadPage("https://www.google.com/?gws_rd=ssl#q="+ text.replaceAll(" ", "+"));
	}
}

class ZunoTools extends ZunoAPI {
	public static void addStyle(Button t, String e) {
		t.getStylesheets().add(e);
	}
	
	@SuppressWarnings("deprecation")
	public static void addStyleCSSFileToButtons(String e) {
		ZunoTools.addStyle(Main.googleButton, e);
		addStyle(Main.sourcebutton, e);
	  	addStyle(Main.aboutbutton, e);
	  	//addStyle(Main.aboutAPIbutton, e);
	  	addStyle(Main.GOButton, e);
	  	addStyle(Main.BackButton, e);
	  	addStyle(Main.ForwardButton, e);
	  	Main.addressBar.getStylesheets().add(e);
	}
}