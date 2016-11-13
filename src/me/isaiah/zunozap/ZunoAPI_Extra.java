package me.isaiah.zunozap;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

@Deprecated
public class ZunoAPI_Extra extends ZunoAPI {
	private static WebEngine WebEngine = ZunoAPI.getWebEngine();
	
	public static String aboutPageHTML() {
		String ABH = ""
				+"<html>"
				+"<header>"
				+"    <CENTER>"
				+"        <h1>About ZunoZap</h1>"
				+"    </CENTER>"
				+"</header>"
				+"<body>"
				+"    <CENTER>"
				+"        ZunoZap is a web browser made with the Java WebView,</p><br>"
				+"        Version: "+System.getProperty("zunozap.version")+"<br>"
				+"        UserAgent: "+ "ZunoAPI/1.0 QupZilla/2.0.1 Mozilla/5.0 JavaFX/8.0" +"<br>"
				+"		  Java Enabled: true<br>"
				+"		  JavaScript Enabled: true"
				+"    </CENTER>"
				+"</p>"				
				+"<header>"
				+"    <CENTER>"
				+"        <hr><h1>About the ZunoAPI</h1>"
				+"    </CENTER>"
				+"</header>"
				+"<body>"
				+"    <CENTER>"
				+"        ZunoAPI is a java api that controls most of the ZunoZap stuff</p>"
				+"        Version: "+System.getProperty("zunoapi.version")+"<br>"
				+"		  Extra: ZunoAPI_Extra.class<br>"
				+"    </CENTER>"
				+"</body>"
				+"</html>";
		return ABH;
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
