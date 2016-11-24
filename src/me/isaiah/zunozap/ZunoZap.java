package me.isaiah.zunozap;

import javax.swing.JOptionPane;

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
import javafx.stage.Stage;;

public final class ZunoZap extends ZunoApp implements ZunoUtils {

	public String version = "0.1";
	public String versionTag = "SNAPSHOT";
	public final String fullVersion = version+"-"+versionTag;
	private static final ClassLoader loader = ZunoZap.class.getClassLoader();
	private String logo = "https://zunozap.github.io/images/flash.png";
	
	/*Setup Buttons*/
	  final static Button backButton = new Button("<"); 
      final static Button forwardButton = new Button(">");
      final Button goButton = new Button("Go");
	
	int tabnum = 0;
	
	private final MenuBar menuBar = new MenuBar();
	private final Menu menuFile = new Menu("File");
	final static TabPane tb = new TabPane();
	final Tab stab = new Tab("Start");
	 
	 /**
	  * The start of ZunoZap
	  * 
	  * @author Isaiah Patton
	  * */
    @Override
    public void start(Stage stage) {
    	System.setProperty("zunozap.version", fullVersion);
    	System.setProperty("zunoapi.version", "Zuno"+version+".0_API");
    	
    	System.out.println("Starting ZunoZap v"+fullVersion+" (API v"+System.getProperty("zunoapi.version")+")");
    	
    	/*Add ZunoZap Logo*/stage.getIcons().add(new Image(loader.getResourceAsStream("zunozaplogo.gif")));
    	Group root = new Group();
    	BorderPane borderPane = new BorderPane();
        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);
        /*Start Tab*/createTab(true);
        
        /*======New-Tab-Button=====*/
          final Tab newtab = new Tab(" + ");
          newtab.setClosable(false);
          tb.getTabs().addAll(newtab);
        /*=========================*/
        
        tb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {  
                @Override
                public void changed(ObservableValue<? extends Tab> observable, Tab oldSelectedTab, Tab newSelectedTab) {
                	if (newSelectedTab == newtab) {
                	    createTab(false);
                	}
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
        
        stage.setTitle("ZunoZap v"+fullVersion);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * New Tab. 
     */
    @SuppressWarnings("static-access") 
    public final void createTab(boolean isStartTab) {
        tabnum++;

        /*======Create Tab======*/
          final Tab tab = new Tab();  
          tab.setText("Loading...");
          tab.setTooltip(new Tooltip("Tab #"+tabnum));
          tab.setId("tab-"+tabnum);
        /*======================*/
        
          
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
        /*=======================*/
        
        /*======Setting Styles======*/
          urlField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border: solid; -fx-border-color: teal; -fx-border-radius: 5px;");
          urlField.setMaxWidth(400);
          hBox.setStyle("-fx-background-color: orange;");
          hBox.setHgrow(urlField, Priority.ALWAYS);
          vBox.setVgrow(webView, Priority.ALWAYS);
          vBox.autosize();
        /*==========================*/
        
        if (isStartTab) {
            webEngine.loadContent("<html><h1><img src='"+logo+"' width='150px' height='150px'><br><b>ZunoZap</b></h1><h3>Build: "+version+"</h3><br /> \n\t\t\t To start browsing, click on + (New Tab) sign.");
            tab.setText("Start");
        } else {
            loadSite("www.google.com", webEngine);
        }
        tab.setContent(vBox);
        
        final ObservableList<Tab> tabs = tb.getTabs();
        
        if (isStartTab) {
            tabs.add(tabs.size(), tab);
        } else {
            tabs.add(tabs.size() - 1, tab);
        }
        tb.getSelectionModel().select(tab);
    }
    

    /**
     * When URL is changed make shure everything gets updated to new url.
     */
    public final static void urlChangeLis(WebEngine webEngine, final TextField urlField, final Tab tab) {
    	webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {  
                 urlField.setText(newValue);
            }  
         });  
    	
    	// JS alert() function handler
    	webEngine.setOnAlert(new EventHandler<WebEvent<String>>(){
			@Override
			public void handle(WebEvent<String> popupText) {
				if (popupText.toString().toLowerCase().contains("virus")) {
					// Warn user if website trys to create an popup trying to get you two download an virus
					JOptionPane.showMessageDialog(null, "The site you are visting has tryed to create an popup with the word 'virus' in it, Please be carefull on this site", "ZunoZap AntiPopupVirus", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, popupText.getData(), "JS Popup", JOptionPane.INFORMATION_MESSAGE);
				}
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
    	    public void handle(ActionEvent t) { JOptionPane.showMessageDialog(null, "Not programed yet", "ZunoZap", JOptionPane.INFORMATION_MESSAGE);}
    	});
    	
    	MenuItem aboutPage = new MenuItem("About ZunoZap v"+version);
    	aboutPage.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent t) {
    	    	Tab aboutTab =new Tab("About");
                String about = "<html>"
                        +"<header> <h1>About ZunoZap</h1></header>"
                        +"<body>"
                        +"    ZunoZap is a web browser made with the Java WebView,</p><br>"
                        +"    Version: "+System.getProperty("zunozap.version")+"<br>"
                        +"    UserAgent: "+ "ZunoZap/1.0 QupZilla/2.0.1 Mozilla/5.0 JavaFX/8.0" +"<br>"
                        +"    Java Enabled: true<br>"
                        +"    JavaScript Enabled: true"
                        +"</body>"
                        +"</html>";
    			WebView WV = new WebView();
                WV.getEngine().loadContent(about);
                aboutTab.setContent(WV);
                tb.getTabs().add(tb.getTabs().size() - 1, aboutTab);
                tb.getSelectionModel().select(aboutTab);
    	    }
    	});
    	
    	MenuItem settingButton = new MenuItem("Settings");
        settingButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { 
                showSettingMenu();
            }
        });
    	
    	menuFile.getItems().addAll(downloadPage,aboutPage,settingButton);
    }
    
    
    /**
     * Setting Menu!
     */
    private final static void showSettingMenu() {
        new OptionMenu();
    }
    
    
    /* Might not what to edit below this line if you don't know what your doing! */
    /*Set Style  */ public final static void setStyle(String fxcss, Button...buttons) {ZunoAPI.setStyle(fxcss, buttons);}
    /*Version    */ @Override public String getVersion() {return version;}
    /*History    */ private final static void history(WebEngine we, String go) { ZunoAPI.history(we, go); }
    /*Load Page  */ public final static void loadSite(String url, WebEngine WE) { ZunoAPI.loadSite(url, WE); }
    /*Launch     */ 
     public static void main(String[] args) {
         launch(args); 
     }

    public final static void getOptionMenuAction(int Action, boolean b) {        
        //Tav bar display setting
        if (Action == ZCheckButton.displayTabBar) {
            tb.setVisible(b);
        }
        
        //Back button display setting.
        if (Action == ZCheckButton.displayBackButton) {
            backButton.setVisible(b);
        }
        
        //Forward button display setting.
        if (Action == ZCheckButton.displayForwardButton) {
            forwardButton.setVisible(b);
        }
    }
}