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
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class ZunoZap extends ZunoApp implements ZunoUtils {

	public String version = "0.1.0";
	public String versionTag = "SNAPSHOT";
	public String fullVersion = version+"-"+versionTag;
	private static ClassLoader loader = ZunoZap.class.getClassLoader();
	private String buttonCSS = "-fx-background-color: transparent; -fx-border: solid; -fx-border-color: teal; -fx-border-radius: 3px;";
	
	int tabnum = 0;
	
	private MenuBar menuBar = new MenuBar();
	private Menu menuFile = new Menu("File");
	TabPane tb = new TabPane();
	Tab stab = new Tab("Start");
	 
	 /**
	  * The start of ZunoZap
	  * 
	  * @author Isaiah Patton
	  * */
    @Override
    public void start(Stage stage) {
    	System.setProperty("zunozap.version", fullVersion);
    	System.setProperty("zunoapi.version", version+"-API");
    	
    	/*Add ZunoZap Logo*/stage.getIcons().add(new Image(loader.getResourceAsStream("zunozaplogo.gif")));
    	Group root = new Group();
    	BorderPane borderPane = new BorderPane();
        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);
        /*Start Tab*/Createfirsttab(tb);
        
        /*======New-Tab-Button=====*/
          final Tab newtab = new Tab();  
          newtab.setText(" + ");  
          newtab.setClosable(false);
          tb.getTabs().addAll(newtab);
        /*=========================*/
        
        tb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {  
                @SuppressWarnings("static-access")
				@Override
                public void changed(ObservableValue<? extends Tab> observable, Tab oldSelectedTab, Tab newSelectedTab) { 
                	if (newSelectedTab == newtab) {
                        tabnum++;

                        Tab tab = new Tab();  
                        tab.setText("Tab #"+tabnum);
                        tab.setId("tab-"+tabnum);
                        
                        /*Setup WebView*/WebView webView = new WebView();  
                        /*Setup WebEngine*/final WebEngine webEngine = webView.getEngine();  
                        /*Setup urlField*/final TextField urlField = new TextField("http://");
                          
                        /*======Setup Event Handlers======*/
                          EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {  
                              @Override public void handle(ActionEvent event) {loadSite(urlField.getText(), webEngine);}  
                          };
                          
                          EventHandler<ActionEvent> backAction = new EventHandler<ActionEvent>() {  
                              @Override public void handle(ActionEvent event) {history(webEngine, "back");}  
                          };
                         
                          EventHandler<ActionEvent> forwardAction = new EventHandler<ActionEvent>() {
                              @Override public void handle(ActionEvent event) {history(webEngine, "forward");}  
                          };
                        /*================================*/
                        

                        /*The URL change event*/urlChangeLis(webEngine, urlField, tab);
                          
                        urlField.setOnAction(goAction);
                        urlField.setMaxWidth(400);

                        /*======Create Buttons======*/
                          Button goButton = new Button("Go");
                          Button backButton = new Button("<"); 
                          Button forwardButton = new Button(">");
                        /*==========================*/


                        /*======Set Button Actions======*/
                          goButton.setOnAction(goAction);
                          backButton.setOnAction(backAction);
                          forwardButton.setOnAction(forwardAction);
                        /*==============================*/


                        HBox hBox = new HBox(backButton, forwardButton, urlField, goButton);
                        final VBox vBox = new VBox(hBox, webView);

                        /*======Setting Styles======*/
                          setStyle(getButtonCSS(), goButton,backButton,forwardButton);
                          urlField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border: solid; -fx-border-color: teal; -fx-border-radius: 5px;");
                          hBox.setStyle("-fx-background-color: orange;");
                          hBox.setHgrow(urlField, Priority.ALWAYS);
                          vBox.setVgrow(webView, Priority.ALWAYS);
                        /*==========================*/
                        
                        loadSite("www.google.com", webEngine);
                        tab.setContent(vBox);
                          
                        final ObservableList<Tab> tabs = tb.getTabs();
                          
                        tabs.add(tabs.size() - 1, tab);
                        tb.getSelectionModel().select(tab);
                     }
                }
           });

        borderPane.setCenter(tb);
        borderPane.setTop(menuBar);
        
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
     * The start tab
     */
    private Tab Createfirsttab(TabPane tb) {
		String about = "<html><h1><b>ZunoZap</b></h1><h3>Build: "+version+"</h3><br /> \n\t\t\t To start browsing, click on + (New Tab) sign.";
		WebView WV = new WebView();
		WV.getEngine().loadContent(about);
        stab.setContent(WV);
        tb.getTabs().add(stab);
        tb.getSelectionModel().select(stab);
        return stab;
    }


    /**
     * When URL is changed make shure the URL bar and Tooltip are updated
     */
    public void urlChangeLis(WebEngine webEngine, TextField urlField, Tab tab) {
    	webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {  
                 urlField.setText(newValue); tab.setTooltip(new Tooltip(newValue));
            }  
       });  
    }
    
    
    /**
     * Add the items to the File menu dropdown list
     */
    public void regMenuItems() {
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
    	menuFile.getItems().addAll(downloadPage,aboutPage);
    }
    
    /* Might not what to edit below this line if you don't know what your doing! */
    /*Set Style  */ public void setStyle(String fxcss, Button...buttons) {ZunoAPI.setStyle(fxcss, buttons);}
    /*Version    */ @Override public String getVersion() {return version;}
    /*Button CSS */ @Override public String getButtonCSS() {return buttonCSS;}
    /*History    */ private void history(WebEngine we, String go) { ZunoAPI.history(we, go); }
    /*Load Page  */ public void loadSite(String url, WebEngine WE) { ZunoAPI.loadSite(url, WE); }
    /*Launch     */ public static void main(String[] args) { launch(args); }
}