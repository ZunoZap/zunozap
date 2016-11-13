package me.isaiah.zunozap;

/**
 * @author ZunoZap Devs.
 * @deprecated
 * */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import me.isaiah.zunozap.FileManager;
import me.isaiah.zunozap.ZunoAPI;
import me.isaiah.zunozap.ImgButtons;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;

import org.apache.commons.io.FileUtils;

@Deprecated
/**
 * Replaced by class ZunoZap.
 */
@SuppressWarnings("unused")
public class Main extends Application {
	public static String name = "ZunoZap-old";
	public static String version = "0.0.5-old";
	
	public static WebView ZunoZap = ZunoAPI.WebView;
	public static WebEngine WebEngine = ZunoAPI.WebEngine;
	
	public final static TextField addressBar = ZunoAPI.addressBar;

	protected static Button googleButton = ZunoAPI.googleButton;
	protected static Button sourcebutton = ZunoAPI.sourcebutton;
  	protected static Button aboutbutton = ZunoAPI.aboutbutton;
  	protected static Button GOButton = ZunoAPI.GOButton;
  	protected static Button BackButton = ZunoAPI.BackButton;
  	protected static Button ForwardButton = ZunoAPI.ForwardButton;

  	private static File folder = FileManager.folder;
  	private static File Dfolder = FileManager.Dfolder;
  	private static File DPfolder = FileManager.DPfolder;
  	private static File programsettings = FileManager.programsettings;
  	
  	protected static VBox root = new VBox();
  	
  	public static ClassLoader loader = Main.class.getClassLoader();
  	
  	public static void main(String[] args) { launch(args); }
  	
  	public static void About() {ZunoAPI.aboutPage();}
  
  	protected static HBox topbar = new HBox();
  	protected static HBox TheaddressBar = new HBox();

    final ImageView selectedImage = new ImageView();
    final static Hyperlink[] hpls = new Hyperlink[ImgButtons.captions.length];
    final static Image[] images = new Image[ImgButtons.imageFiles.length];

    public void start(Stage stage) {
    	stage.getIcons().add(new Image("https://zunozap.github.io/images/flash.png"));
    	try {
			ZunoZap.getStylesheets().add(FileManager.UASS.toURI().toURL().toString()); // UASS = User Agent Style Sheet
			ZunoTools.addStyleCSSFileToButtons(FileManager.UASS.toURI().toURL().toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
    	stage.setTitle("ZunoZap " + version);
    	//LogManager.info("[ZunoAPI] The browser's style settings: " + ZunoZap.getStylesheets().toString().replace("file:/", ""));
    	
    	ZunoAPI.loadAPI();
        FileManager.SetupZunoZapFiles();
        setOnActions(stage);

        ZunoAPI_Extra.HPLS();

        addressBar.setMinWidth(500);
        Insets pi = new Insets(0, 5, 0, 5);
        Insets pi2 = new Insets(0, 4, 0, 4);
        
        //START---The Addressbar
        ZunoAPI.setMargins(TheaddressBar, pi2, BackButton, GOButton);
        ZunoAPI.setMargins(TheaddressBar, pi2, pi, ForwardButton, googleButton);
        ZunoAPI.allAllKids(TheaddressBar, BackButton, ForwardButton, addressBar, GOButton, googleButton);
        ZunoAPI.setAlined(TheaddressBar, Pos.CENTER);
        //END---Address bar
        
        
        //START---The Top bar
        ZunoAPI.setMargin(topbar, pi, aboutbutton);
        ZunoAPI.allAllKids(topbar, sourcebutton, aboutbutton);
        ZunoAPI.setAlined(topbar, Pos.CENTER);

    	topbar.getStyleClass().add("browser-toolbar");
        topbar.getChildren().addAll(hpls);
        //END---Top Bar
        
        ZunoAPI.loadStartPage();
        
        ZunoAPI.MTH = topbar.getMaxHeight();
        ZunoAPI.CreateRootVBox(root, ZunoZap, topbar, TheaddressBar);
        stage.setScene(new Scene(root));
        stage.show();
        
    }

	public static void pageChangeError() { 
		// This will be ran when theres an error changing the page (example: user is offline)
		System.out.println("[ZunoAPI] ZunoZap encountered an exception loading a page... Now trying offline data!...");
    	String offlineFile = ZunoAPI.WebEngine.getLocation().replaceAll("", "");
    	offlineFile = offlineFile.replace("http://", "");
    	
    	String thesavepagesfolder = System.getProperty("user.home") + File.separator + Main.name + File.separator + "Saved Pages";
    	thesavepagesfolder = thesavepagesfolder.replace("C:", "");
    	ZunoAPI.WebEngine.load("file://" + thesavepagesfolder + File.separator + offlineFile.replace("/", "") + ".html");
	}
	
    public static void setOnActions(Stage stage) {
        addressBar.setOnAction(new EventHandler<ActionEvent>() { public void handle(ActionEvent event) {
        	ZunoAPI.loadPageFromAddressBar(stage, addressBar);}
        });

        googleButton.setOnAction(new EventHandler<ActionEvent>() { public void handle(ActionEvent event) { 
        	ZunoAPI.googlePage(addressBar.getText());}
        });
        
        
        GOButton.setOnAction(new EventHandler<ActionEvent>() { public void handle(ActionEvent event) {
        	ZunoAPI.loadPageFromAddressBar(stage, addressBar);
        }});
        
        BackButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) { WebEngine.executeScript("history.back()"); }
        });
        
        ForwardButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) { WebEngine.executeScript("history.forward()"); }
        });
        sourcebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) { ZunoAPI.DownloadPage(addressBar.getText(), DPfolder); }});
            
        aboutbutton.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent e) { About(); }});
    }
}