package me.isaiah.zunozap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import com.sun.javafx.application.LauncherImpl;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.PluginManager;

public final class ZunoZap extends ZunoAPI {
    private final static Class<? extends ZunoAPI> c = ZunoZap.class;

    protected static final String name    = "ZunoZap";
    protected static final String version = "0.1-SNAPSHOT";
    protected static final Image logo = new Image(c.getClassLoader().getResourceAsStream("flash.png"));
    public static final File homeDir = new File(System.getProperty("user.home"), "zunozap");
    private static final File localStorage = new File(homeDir, "localstorage");
    private static final File dataDir = new File(homeDir, "webEngine");
	
    private final MenuBar menuBar = new MenuBar();
    private final Menu menuFile = new Menu("File");
    private final static TabPane tb = new TabPane();
	private final static PluginManager p = new PluginManager();
    
	 /**
	  * The start of ZunoZap
	  * 
	  * @author Isaiah Patton
	  */
    @Override
    public void start(Stage stage) {
    	System.setProperty(name + ".version", version);

    	if (!homeDir.exists()) homeDir.mkdir();
    	if (!localStorage.exists()) localStorage.mkdir();
    	if (!dataDir.exists()) dataDir.mkdir();

    	/*Add ZunoZap Logo*/stage.getIcons().add(new Image(c.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
    	Group root = new Group();
    	BorderPane borderPane = new BorderPane();
        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);
        
        /*====New-Tab-Button====*/
          final Tab newtab = new Tab(" + ");
          newtab.setClosable(false);
          tb.getTabs().addAll(newtab);
        
        /*Start Tab*/createTab(true);
          
        tb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {  
            @Override
            public void changed(ObservableValue<? extends Tab> a, Tab b, Tab c) {
                if (c == newtab) createTab(false);
            }
        });

        borderPane.setCenter(tb);
        borderPane.setTop(menuBar);
        borderPane.autosize();

        regMenuItems();
        menuBar.getMenus().addAll(menuFile);
        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add("style.css");

        stage.setTitle("ZunoZap v"+version);
        stage.setScene(scene);
        
        p.loadPlugins();
        for (PluginBase plug : p.plugins) {
            plug.onLoad(stage, scene, tb);
        }
        stage.show();
    }

    /**
     * What happens when you click the 'New Tab' button.
     */
    @SuppressWarnings("static-access") 
    public final void createTab(boolean isStartTab) {
        tabnum++;

        /*====Create Tab====*/
          final Tab tab = new Tab("Loading...");
          tab.setTooltip(new Tooltip("Tab #"+tabnum));
          tab.setId("tab-"+tabnum);

        /*Setup Buttons*/
          final Button backButton = new Button("<"); 
          final Button forwardButton = new Button(">");
          final Button goButton = new Button("Go");

        /*Setup WebView*/  final WebView webView = new WebView();  
        /*Setup WebEngine*/final WebEngine webEngine = webView.getEngine();  
        /*Setup urlField*/ final TextField urlField = new TextField("http://");
        /*Setup hBox*/     final HBox hBox = new HBox(backButton, forwardButton, urlField, goButton);
        /*Setup vBox*/     final VBox vBox = new VBox(hBox, webView);

        /*======Setup Event Handlers======*/
          final EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {  
              @Override public void handle(ActionEvent event) {loadSite(urlField.getText(), webEngine);}  
          };

          final EventHandler<ActionEvent> backAction = new EventHandler<ActionEvent>() {  
              @Override public void handle(ActionEvent event) {history(webEngine, "back");}  
          };

          final EventHandler<ActionEvent> forwardAction = new EventHandler<ActionEvent>() {
              @Override public void handle(ActionEvent event) {history(webEngine, "forward");}  
          };
          urlChangeLis(webEngine, urlField, tab);
        /*================================*/

        /*======Set Actions======*/
          goButton.setOnAction(goAction);
          backButton.setOnAction(backAction);
          forwardButton.setOnAction(forwardAction);
          urlField.setOnAction(goAction);

        /*======Setting Styles======*/
          urlField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border: solid; -fx-border-color: teal; -fx-border-radius: 5px;");
          urlField.setMaxWidth(400);
          hBox.setStyle("-fx-background-color: orange;");
          hBox.setHgrow(urlField, Priority.ALWAYS);
          vBox.setVgrow(webView, Priority.ALWAYS);
          vBox.autosize();
        /*==========================*/

        webEngine.setUserDataDirectory(dataDir);
        if (isStartTab) {
            webEngine.loadContent("<html><h1><img src='https://zunozap.github.io/images/flash.png' width='150px' height='150px'><br><b>ZunoZap</b></h1><h3>Build: "+version+"</h3><br /> \n\t\t\t To start browsing, click on + (New Tab) sign.");
            tab.setText("Start");
        } else {
            loadSite("https://www.google.com", webEngine);
        }
        tab.setContent(vBox);

        if (p.plugins.size() != 0) {
            for (PluginBase plug : p.plugins) plug.onTabCreate(tab);
        }

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
        
    }

    /**
     * When URL is changed make shure everything gets updated to new url.
     */
    public final static void urlChangeLis(final WebEngine webEngine, final TextField urlField, final Tab tab) {
    	webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {  
                 urlField.setText(newValue);
                 for (PluginBase plug : p.plugins) {
                     try {
                        plug.onURLChange(webEngine, urlField, new URL(oldValue), new URL(newValue));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        System.err.println("Cant pass onURLChange event to plugins.");
                    }
                 }
                 //TODO DownloadPage(localStorage, webEngine);
            }
         });  
    	
    	// JS alert() function handler
    	webEngine.setOnAlert(new EventHandler<WebEvent<String>>(){
			@Override
			public void handle(WebEvent<String> popupText) {
				if (popupText.toString().toLowerCase().contains("virus")) {
					// Warn user if website trys to create an popup trying to get you two download an virus
					JOptionPane.showMessageDialog(null, "The site you are visting has tryed to create an popup with the word 'virus' in it, Please be carefull on this site", "ZunoZap AntiPopupVirus", JOptionPane.WARNING_MESSAGE);
				}

				JOptionPane.showMessageDialog(null, popupText.getData(), "JS Popup", JOptionPane.INFORMATION_MESSAGE);
			}
    	});
    	webEngine.titleProperty().addListener(new ChangeListener<String>() {
    		@Override public void changed(ObservableValue<? extends String> o, String oV, String nV){ tab.setText(nV); }
    	});
    }
    
    
    /**
     * Add the items to the File menu dropdown list
     */
    public final void regMenuItems() {
    	MenuItem downloadPage = new MenuItem("Download Page for offline browsing");
    	downloadPage.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent t) {
    	        JOptionPane.showMessageDialog(null, "Not programed yet", "ZunoZap", JOptionPane.INFORMATION_MESSAGE);
    	    }
    	});

    	MenuItem aboutPage = new MenuItem("About ZunoZap v"+version);
    	aboutPage.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent t) {
    	    	Tab aboutTab = new Tab("About");
    			WebView WV = new WebView();
                WV.getEngine().loadContent(
                        aboutPageHTML() + getPlugins());
                aboutTab.setContent(WV);
                tb.getTabs().add(tb.getTabs().size() - 1, aboutTab);
                tb.getSelectionModel().select(aboutTab);
    	    }
    	});
    	
    	final MenuItem settingButton = new MenuItem("Settings");
        settingButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                JOptionPane.showMessageDialog(null, "The setting menu is still a W.I.P", "ZunoZap", JOptionPane.INFORMATION_MESSAGE);
                new OptionMenu();
            }
        });
    	
    	menuFile.getItems().addAll(downloadPage,aboutPage,settingButton);
    }

    /**
     * Launch
     */ 
    public static void main(String[] args) {
        LauncherImpl.launchApplication(ZunoZap.class, args);
    }

    public final static void getOptionMenuAction(int Action, boolean b) {
        //TODO: Impove porformance & finish.
        
        if (Action == ZCheckButton.displayTabBar) {
            tb.setVisible(b);
        }

        if (Action == ZCheckButton.forceHTTPS) {
            forceHTTPS = b;
        }
    }

    public static final String getPlugins() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins ["+ size +"]:"
               +String.valueOf(p.pluginNames).replace("[", "").replace("]", "") : "No Installed Plugins.";
    }
}