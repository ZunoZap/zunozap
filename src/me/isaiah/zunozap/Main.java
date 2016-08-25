package me.isaiah.zunozap;

/**
 * @author ZunoZap Devs.
 * 
 * */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import me.isaiah.zunozap.FileManager;
import me.isaiah.zunozap.ZunoAPI;
import me.isaiah.zunozap.ImgButtons;
import me.isaiah.zunozap.HBox;

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
import javafx.geometry.Insets;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("unused")
public class Main extends Application {
	public static String name = "ZunoZap";
	public static String version = "v1.0-DevTestBuild";
	
	public static WebView ZunoZap = ZunoAPI.WebView;
	public WebEngine ZunoZapEngine = ZunoAPI.WebEngine;
	
	public final static TextField addressBar = ZunoAPI.addressBar;

	protected static Button googleBar = new Button("Google");
	protected static Button sourcebutton = new Button("Download Page Source");
  	protected static Button aboutbutton = new Button("About ZunoZap");
  	protected static Button aboutAPIbutton = new Button("About the ZunoAPI");
    protected static Button enableTools = new Button("Enable Styles");
  	protected static Button GOButton = new Button("GO");
  	protected static Button BackButton = new Button("Back");
  	protected static Button ForwardButton = new Button("Forward");

  	private static File folder = FileManager.folder;
  	private static File Dfolder = FileManager.Dfolder;
  	private static File DPfolder = FileManager.DPfolder;
  	private static File programsettings = FileManager.programsettings;
  	protected static VBox root = new VBox();
  	
  	public ClassLoader loader = Main.class.getClassLoader();
  	
  	public static void main(String[] args) { launch(args); }
  	
  	public void About() {ZunoAPI.aboutPage();}
  
  	protected static HBox topbar = new HBox();
  	protected static HBox TheaddressBar = new HBox();

    final ImageView selectedImage = new ImageView();
    final Hyperlink[] hpls = new Hyperlink[ImgButtons.captions.length];
    final Image[] images = new Image[ImgButtons.imageFiles.length];

    private static boolean SS = true;
    public void start(Stage stage) {
    	try {
			ZunoZap.getStylesheets().add(FileManager.UASS.toURI().toURL().toString()); // UASS = User Agent Style Sheet
			ZunoTools.addStyleCSSFileToButtons(FileManager.UASS.toURI().toURL().toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
    	stage.setTitle("ZunoZap " + version);
    	System.out.println("[ZunoAPI] The browser's style settings: " + ZunoZap.getStylesheets().toString().replace("file:/", ""));
    	
    	ZunoAPI.loadAPI();

        FileManager.SetupZunoZapFiles(folder, DPfolder, programsettings, Dfolder);
        ZunoAPI.setOnActions(stage, addressBar, googleBar, GOButton, BackButton, ForwardButton, enableTools);

           	for (int i = 0; i < ImgButtons.captions.length; i++) {
            	Hyperlink hpl = hpls[i] = new Hyperlink(ImgButtons.captions[i]);
            	Image image = images[i] = new Image(loader.getResourceAsStream(ImgButtons.imageFiles[i]));
            	hpl.setGraphic(new ImageView(image)); final String url = ImgButtons.urls[i];
            	
            	hpl.setOnAction(new EventHandler<ActionEvent>() {@Override public void handle(ActionEvent e) {
            		ZunoAPI.loadPage(url); addressBar.setText(url); }
            	});
            }
            


        sourcebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { ZunoAPI.DownloadPage(addressBar.getText()); }});
        
        aboutbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) { About(); }});
        addressBar.setMinWidth(500);
        Insets pi = new Insets(0, 5, 0, 5);
        Insets pi2 = new Insets(0, 4, 0, 4);
        
        //The Addressbar
        ZunoAPI.setMargins(TheaddressBar, pi2, BackButton, GOButton);
        ZunoAPI.setMargins(TheaddressBar, pi2, pi, ForwardButton, googleBar);
        ZunoAPI.allAllKids(TheaddressBar, BackButton, ForwardButton, addressBar, GOButton, googleBar);
        ZunoAPI.setAlined(TheaddressBar, Pos.CENTER);
        
        //The Top bar
        ZunoAPI.setMargin(topbar, pi, aboutbutton);
        ZunoAPI.allAllKids(topbar, sourcebutton, aboutbutton);
        ZunoAPI.setAlined(topbar, Pos.CENTER);

    	topbar.getStyleClass().add("browser-toolbar");
        topbar.getChildren().addAll(hpls);   
        
        ZunoAPI.loadStartPage();

        ZunoAPI.MTH = topbar.getMaxHeight();
        ZunoAPI.CreateRootVBox(root, ZunoZap, topbar, TheaddressBar);
        stage.setScene(new Scene(root));
        stage.show();
    }
}