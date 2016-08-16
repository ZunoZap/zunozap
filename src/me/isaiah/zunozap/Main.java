package me.isaiah.zunozap;

import java.io.File;
import java.net.URL;

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
import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

@SuppressWarnings("unused")
public class Main extends Application {
	public WebView ZunoZap;
	private WebEngine ZunoZapEngine;
	private String version = "v0.0.5";
	final static TextField addressBar = new TextField();
	private Button googleBar = new Button("Google");
	private Button sourcebutton = new Button("Download Page Source");
  	private Button aboutbutton = new Button("About ZunoZap");
  	private Button GOButton = new Button("GO");
  	private Button BackButton = new Button("Back");
  	private Button ForwardButton = new Button("Forward");
  	private static File DPfolder = new File(System.getProperty("user.home") + File.separator + "ZunoZap" + File.separator + "Saved Pages");
  	private File folder = new File(System.getProperty("user.home") + File.separator + "ZunoZap" + File.separator);
  	public ClassLoader loader = Main.class.getClassLoader();
  	
  	public static void main(String[] args) {
	  	ZunoAPI.loadAPI();
	  	launch(args);
    }
  	
  	
  	public void About() {
  		String aboutpage = loader.getResource("about.html").toExternalForm();
  		loadPage(aboutpage);
  	}
  
  	private HBox topbar = new HBox();
    private HBox TheaddressBar = new HBox();

    final ImageView selectedImage = new ImageView();
    final Hyperlink[] hpls = new Hyperlink[ImgButtons.captions.length];
    final Image[] images = new Image[ImgButtons.imageFiles.length];
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final Button showPrevDoc = new Button("Toggle Previous Docs");
    final WebView smallView = new WebView();
    @SuppressWarnings("rawtypes")
	final ComboBox comboBox = new ComboBox();
    private boolean needDocumentationButton = false;
    
    @SuppressWarnings("static-access")
    public void start(Stage stage) {
    	stage.setTitle("ZunoZap " + version);
        File programsettings = new File(folder + "settings.txt");

            ZunoAPI.SetupZunoZapFiles(folder, DPfolder, programsettings);
    
            addressBar.setOnAction(new EventHandler<ActionEvent>() {
            	public void handle(ActionEvent event) {
            		loadPageFromAddressBar(stage);
            	}
            });
    
            googleBar.setOnAction(new EventHandler<ActionEvent>() {
            	public void handle(ActionEvent event) {
            		loadPage("https://www.google.com/?gws_rd=ssl#q="+ addressBar.getText().replaceAll(" ", "+"));
            	}
            });
            
            
            GOButton.setOnAction(new EventHandler<ActionEvent>() {
            	public void handle(ActionEvent event) {
            		loadPageFromAddressBar(stage);
            	}
            });
            
            BackButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ZunoZapEngine.executeScript("history.back()");
				}
            });
            
            ForwardButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ZunoZapEngine.executeScript("history.forward()");
				}
            });
    
            //apply the styles
            //getStyleClass().add("browser"); 

            for (int i = 0; i < ImgButtons.captions.length; i++) {
            	Hyperlink hpl = hpls[i] = new Hyperlink(ImgButtons.captions[i]);
            	Image image = images[i] =
            			new Image(loader.getResourceAsStream(ImgButtons.imageFiles[i]));
            	hpl.setGraphic(new ImageView(image));
            	final String url = ImgButtons.urls[i];
            	hpl.setOnAction(new EventHandler<ActionEvent>() {
            		@Override
            		public void handle(ActionEvent e) {
            			loadPage(url);
            			addressBar.setText(url);
            		}
            	});
            }
            


        sourcebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                    DownloadPage(addressBar.getText());
            }
        });
        
        aboutbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                    About();
            }
        });

        addressBar.setMinWidth(500);
        Insets pi = new Insets(0, 5, 0, 5);
        Insets pi2 = new Insets(0, 4, 0, 4);
        TheaddressBar.autosize();
        TheaddressBar.setMargin(GOButton, pi);
        TheaddressBar.setMargin(BackButton, pi2);
        TheaddressBar.setMargin(ForwardButton, pi2);
        TheaddressBar.setMargin(googleBar, pi);
        TheaddressBar.getChildren().add(BackButton);
        TheaddressBar.getChildren().add(ForwardButton);
        TheaddressBar.getChildren().add(addressBar);
        TheaddressBar.getChildren().add(GOButton);
        TheaddressBar.getChildren().add(googleBar);
        TheaddressBar.setAlignment(Pos.CENTER);
        TheaddressBar.autosize();
        
        topbar.setMargin(aboutbutton, pi);
        topbar.getChildren().add(sourcebutton);
        topbar.getChildren().add(aboutbutton);
        
        topbar.setAlignment(Pos.CENTER);
    	topbar.getStyleClass().add("browser-toolbar");
        topbar.getChildren().addAll(hpls);

        WebView ZunoZap = new WebView();
        ZunoZapEngine = ZunoZap.getEngine();

        ZunoAPI.loadStartPage(ZunoZapEngine);
        Listener();

        VBox root = new VBox();
        root.getChildren().setAll(
        		topbar,
        		TheaddressBar,
        		ZunoZap
        );
        stage.setScene(new Scene(root));
        stage.show();
    }
  
    @Deprecated
    private static void DownloadPage(String site) {
    	ZunoAPI.DownloadPage(site, addressBar, DPfolder); //Downloading the page has been moved to the ZunoAPI
    }

    public void loadPage(String url) {
        ZunoZapEngine.load(url);
        addressBar.setText(url);
        //Document doc = ZunoZapEngine.getDocument();
        //Element el = doc.getElementById("title");
        //System.out.println(ZunoZapEngine.getDocument().getDocumentElement());
    }
    
    public void loadPageFromAddressBar(Stage stage) {
    	if (addressBar.getText().contains("http://") || addressBar.getText().contains("https://") || addressBar.getText().contains("file://") || addressBar.getText().contains("zunozap:")) {
            if (addressBar.getText().contains("zunozap:start") || addressBar.getText().contains("zunozap://start")) {
            	loadPagePGL(stage, "http://zunozap.github.io");
            } else if (addressBar.getText().contains("zunozap:") && addressBar.getText().contains("about")) {
                loadPagePGL(stage, Main.class.getResource("about.html").toExternalForm());
            } else {
            	loadPagePGL(stage, addressBar.getText());
                //stage.setTitle("ZunoZap " + version +" - "+ addressBar.getText());
                addressBar.setText(addressBar.getText().replace("http://", ""));
            }
        } else {
        	loadPagePGL(stage, "http://" + addressBar.getText());
            stage.setTitle("ZunoZap " + version +" - "+ addressBar.getText());
        }
    }
    
    private void loadPagePGL(Stage stage, String url) {
    	loadPage(url);
    	//Document doc = ZunoZapEngine.getDocument();
    	//Element el = doc.getElementById("title");

        stage.setTitle("ZunoZap " + version +" - "+ addressBar.getText());
    }
    
    public void Listener() {
        ZunoZapEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override public void changed(ObservableValue<? extends Throwable> observableValue, Throwable oldException, Throwable exception) {
                System.out.println("ZunoZap encountered an exception loading a page: " + exception);
            }
        });
    }
    public void test(HyperlinkEvent hyperlinkEvent) {
    	    HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
    	    final URL url = hyperlinkEvent.getURL();
    	    if (type == HyperlinkEvent.EventType.ENTERED) {
    	      System.out.println("URL: " + url);
    	    } else if (type == HyperlinkEvent.EventType.ACTIVATED) {
    	      System.out.println("Activated");
    	}
    	    ZunoZapEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
				@Override
				public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue,
						Throwable newValue) {
					addressBar.setText(url.toString());
					
				}
            });
    }
}