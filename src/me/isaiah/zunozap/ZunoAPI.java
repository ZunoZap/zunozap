package me.isaiah.zunozap;

/**
 * @author ZunoZap Devs
 */


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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class ZunoAPI {

	public static String version = "1.0"; //This is the ZunoAPI version
	public static WebView WebView = new WebView();
	public static WebEngine WebEngine = WebView.getEngine();
	public static double MTH;
	
	private static File DPfolder = FileManager.DPfolder;
  	private static File DataFolder = FileManager.DataFolder;
  	public static TextField addressBar = new TextField();

  	static boolean toolsEnabled = false;
  	
	public ZunoAPI() {
		System.out.println("[ZunoAPI] This app uses the ZunoAPI!");
		FileManager.loadManager();
		WebEngine.setUserAgent("ZunoZap/1.0 ZunoAPI/1.0 QupZilla/2.0.1 "+WebEngine.getUserAgent());
		WebEngine.setUserDataDirectory(DataFolder);
		
		lis();
	}
	
	public static void aboutPage() {
		String ABH = ""
				+"<html>"
				+"<header>"
				+"    <CENTER>"
				+"        <h1>About ZunoZap</h1>"
				+"    </CENTER>"
				+"</header>"

				+"<body>"
				+"    <CENTER>"
				+"        ZunoZap is a web browser made with the Java WebView,</p>"
				+"        Version: "+Main.version+"<br>"
				+"        UserAgent: "+ "ZunoAPI/1.0 QupZilla/2.0.1 Mozilla/5.0 JavaFX/8.0" +"<br>"
				+"		  JavaScript Enabled: "+ WebEngine.isJavaScriptEnabled()
				+"    </CENTER>"
				+"</body></p>"
				
				+"<header>"
				+"    <CENTER>"
				+"        <hr><br><h1>About the ZunoAPI</h1>"
				+"    </CENTER>"
				+"</header>"

				+"<body>"
				+"    <CENTER>"
				+"        ZunoAPI is a java api that controls most of the ZunoZap stuff</p>"
				+"        Version: "+version+"<br>"
				+"    </CENTER>"
				+"</body>"
				+"</html>";
		WebEngine.loadContent(ABH);
		
	}
    
    public static WebEngine getWebEngine() { return WebEngine; }
    public static WebView getWebView() { return WebView; }
    
    /**
	 * @author Isaiah Patton
	 * @return WebEngine.load(url);
	 * */
    public static void loadPage(String url) {
    	if (!url.contains(".")) {
    		//FIXME This does not work, it should load google if the url does not have a dot in it
    		WebEngine.load("https://www.google.com/?gws_rd=ssl#q="+ addressBar.getText().replaceAll(" ", "+"));
    	}
    	WebEngine.load(url);
    }
    
    /**
     * @author Isaiah Patton
     * @INFO Gets the web sites source code
     * 
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
    
    @Deprecated
    public static void DownloadPage(String site, TextField addressBar, File DPfolder) {DownloadPage(site);}

    public static void DownloadPage(String site) {DownloadPage(site, DPfolder);}
    
    
    /**
     * @author Isaiah Patton
     * 
     * @decripion Uses the getURLSource() and puts the url's source in to a HTML file
     * @see {@link DPfolder}
     * */
    public static void DownloadPage(String site, File DPfolder) {
        try{       
        	File htmlsourcefile = new File(DPfolder + File.separator + WebEngine.getLocation().replaceAll("[ / . ]", "-") + ".html");
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

    public static void CreateRootVBox(VBox root, WebView browser, HBox... boxes) {
    	root.getChildren().addAll(boxes);
    	root.getChildren().add(browser);
    }
    
    public static void checkIfExists(File file){if(file.exists()){try{file.createNewFile();}catch(IOException e){}}}
    
    public static void setOnActions(Stage stage, TextField addressBar, Button googleBar, Button GOButton, Button BackButton, Button ForwardButton, Button enableTools) {
        addressBar.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent event) { loadPageFromAddressBar(stage, addressBar); }
        });

        googleBar.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent event) {
        		loadPage("https://www.google.com/?gws_rd=ssl#q="+ addressBar.getText().replaceAll(" ", "+"));
        	}
        });
        
        
        GOButton.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent event) { loadPageFromAddressBar(stage, addressBar); }
        });
        
        BackButton.setOnAction(new EventHandler<ActionEvent>() {@Override
			public void handle(ActionEvent event) {
				WebEngine.executeScript("history.back()");
			}
        });
        
        ForwardButton.setOnAction(new EventHandler<ActionEvent>() {@Override
			public void handle(ActionEvent event) {
				WebEngine.executeScript("history.forward()");
			}
        });
        
        enableTools.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				
				if (toolsEnabled == true) {
					Main.root.getChildren().setAll(Main.root, Main.ZunoZap, Main.topbar, Main.TheaddressBar);
					toolsEnabled = false;
				} else {
					Main.root.getChildren().setAll(Main.root, Main.ZunoZap, Main.TheaddressBar);
					toolsEnabled = true;
				}
			}
        });
    }

	public static void loadStartPage() {
		getWebEngine().load("http://zunozap.github.io/startpage");
	}
	public static void loadAPI() { new ZunoAPI(); }
	public static void loadPageFromAddressBar(Stage stage, TextField addressBar) {
    	if (addressBar.getText().contains("http://") || addressBar.getText().contains("https://") || addressBar.getText().contains("file://") || addressBar.getText().contains("zunozap:")) {
            if (addressBar.getText().contains("zunozap:start") || addressBar.getText().contains("zunozap://start")) {
            	loadPagePGL(stage, "http://zunozap.github.io", addressBar);
            } else if (addressBar.getText().contains("zunozap:") && addressBar.getText().contains("about")) {
                loadPagePGL(stage, Main.class.getResource("about.html").toExternalForm(), addressBar);
            }
        } else {
        	loadPagePGL(stage, "http://" + addressBar.getText(), addressBar);
        }
    }
	@SuppressWarnings("unused")
	private static void googlePage(String text) {
		loadPage("https://www.google.com/?gws_rd=ssl#q="+ text.replaceAll(" ", "+"));
	}

	public static void loadPagePGL(Stage stage, String url, TextField addressBar) {
    	loadPage(url);
        stage.setTitle("ZunoZap " + Main.version +" - "+ WebEngine.getTitle() + " (" + WebEngine.getLocation() + ")");
	}
	public static void lis() {
		WebView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
		    @Override
		    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
		        if (newValue != Worker.State.SUCCEEDED && newValue != Worker.State.FAILED) {
		             return;
		        }
		        if (newValue == Worker.State.FAILED) {
		        	System.out.println("[ZunoAPI] ZunoZap encountered an exception loading a page... Now trying offline data!...");
		        	String offlineFile = WebEngine.getLocation().replaceAll("", "");
		        	offlineFile = offlineFile.replace("http://", "");
		        	
		        	String thesavepagesfolder = System.getProperty("user.home") + File.separator + Main.name + File.separator + "Saved Pages";
		        	thesavepagesfolder = thesavepagesfolder.replace("C:", "");
		        	WebEngine.load("file://" + thesavepagesfolder + File.separator + offlineFile.replace("/", "") + ".html");
		        	// file:///Users/Vons/ZunoZap/Saved%20Pages/test.html
		        	System.out.println(offlineFile);
		        }
		        
		        Main.addressBar.setText(WebEngine.getLocation());
		    }
		});
		
		/*WebEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override public void changed(ObservableValue<? extends Throwable> observableValue, Throwable oldException, Throwable exception) {
                System.out.println("ZunoZap encountered an exception loading a page: " + exception);
            }
        });*/
	}
	
	@SuppressWarnings("static-access")
	public static void setMargins(HBox box, Insets insets, Node n1, Node n2) {
    	box.setMargin(n1, insets);
    	box.setMargin(n2, insets);
    }
	@SuppressWarnings("static-access")
	public static void setMargins(HBox box, Insets insets, Insets insets2, Node n1, Node n2) {
    	box.setMargin(n1, insets);
    	box.setMargin(n2, insets2);
    }
	@SuppressWarnings("static-access")
	public static void setMargin(HBox box, Insets insets, Node n1) {
    	box.setMargin(n1, insets);
    }
	public static void setAlined(HBox box, Pos type) {
		box.setAlignment(type);
        box.autosize();
	}
	public static void allAllKids(HBox box, Node... ns) {
		box.getChildren().addAll(ns);
	}
	public static void addStylesheet() {
		try {
			WebView.getStylesheets().add(FileManager.UASS.toURI().toURL().toString());
			ZunoTools.addStyleCSSFileToButtons(FileManager.UASS.toURI().toURL().toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
}