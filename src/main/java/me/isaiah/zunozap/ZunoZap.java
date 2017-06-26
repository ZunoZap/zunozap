package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.manager.PluginManager;

public class ZunoZap extends ZunoAPI {
    public static final String v = "0.3.1";
    public static final File homeDir = new File(System.getProperty("user.home"), "zunozap");
    private static final File localStorage = new File(homeDir, "localstorage");
    private static final File dataDir = new File(homeDir, "webEngine");
    private final MenuBar menuBar = new MenuBar();
    private Menu menuFile = new Menu("File");
    private Menu book = new Menu("Bookmarks");
    private Bookmarks bm = new Bookmarks();
    private static TabPane tb;
    private final static PluginManager p = new PluginManager();

    /**
     * Launch
     * @throws IOException 
     */ 
    public static void main(String[] args) throws IOException {
        launch(ZunoZap.class, args);
    }
    
    @Override
    public void stop() {
        try {
            OptionMenu.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProgramInfo getProgramInfo() {
        return new ProgramInfo("ZunoZap", v);
    }

    @Override
    public void start(Stage stage, Scene scene, Group root, BorderPane borderPane) {
        tb = new TabPane();
        System.setProperty(name + ".version", version);

        if (!homeDir.exists()) homeDir.mkdir();
        if (!localStorage.exists()) localStorage.mkdir();
        if (!dataDir.exists()) dataDir.mkdir();

        /*Add ZunoZap Logo*/stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
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
        menuBar.getMenus().addAll(menuFile,book);
        scene.getStylesheets().add("style.css");

        p.loadPlugins();
        if (allowPluginEvents())
            for (PluginBase pl : p.plugins)
                pl.onLoad(stage, scene, tb);
    }

    /**
     * What happens when you click the 'New Tab' button.
     */
    @SuppressWarnings("static-access") 
    public final void createTab(boolean isStartTab) {
        tabnum++;

        /*====Create Tab====*/
          final Tab tab = new Tab("Loading");
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
              @Override public void handle(ActionEvent event){loadSite(urlField.getText(), webEngine);}  
          };

          final EventHandler<ActionEvent> backAction = new EventHandler<ActionEvent>() {  
              @Override public void handle(ActionEvent event){history(webEngine, "back");}  
          };

          final EventHandler<ActionEvent> forwardAction = new EventHandler<ActionEvent>() {
              @Override public void handle(ActionEvent event){history(webEngine, "forward");}  
          };
          urlChangeLis(webEngine, urlField, tab);
        /*======Bookmarks======*/
          for (final String hi : bm.registered) {
              final MenuItem mark = new MenuItem(hi);
              mark.setOnAction(new EventHandler<ActionEvent>() {
                  @Override public void handle(ActionEvent t){
                      //TODO: way to add & remove bookmarks in browser
                      webEngine.load(bm.map.get(hi).toString());
                  }
              });
              book.getItems().add(mark);
            // bm.registered.remove(hi);
          }

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
        webEngine.setUserAgent(webEngine.getUserAgent() + " ZunoZap/0.1.0 Chrome/53.0.2785.148");

        if (isStartTab) {
            String startText = "<h1><img src='https://zunozap.github.io/images/flash.png' width='150px' height='150px'><br><b>ZunoZap</b></h1><h3>Build: "+version+"</h3><br /> \n\t\t\t To start browsing, click on + (New Tab) sign."; 
            tab.setText("Start");
            try {
                webEngine.load(getClass().getClassLoader().getResource("startpage.html").toURI().toString());
            } catch (URISyntaxException e) {
                webEngine.loadContent(startText);
            }
        } else {
            if (!useDuck) loadSite("https://www.google.com", webEngine);
            else
                loadSite("https://start.duckduckgo.com/", webEngine);
        }
        tab.setContent(vBox);

        if (allowPluginEvents())
            for (PluginBase pl : p.plugins)
                pl.onTabCreate(tab);

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    /**
     * When URL is changed make shure everything gets updated to new url.
     */
    public final void urlChangeLis(final WebEngine webEngine, final TextField urlField, final Tab tab) {
        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == null) {
                    urlField.setText(newValue);
                    if (allowPluginEvents()) {
                        for (PluginBase plug : p.plugins) {
                            try {
                                plug.onURLChange(webEngine, urlField, null, new URL(newValue));
                            } catch (MalformedURLException e) {
                                System.out.println(e);
                                System.err.println("Cant pass onURLChange event to plugin: " + plug.getPluginInfo().name
                                        + " v" + plug.getPluginInfo().version);
                            }
                        }
                    }
                    return;
                }
                boolean httpsredirect = false;
                if (newValue.contains("file://")) {
                    urlField.setText(newValue);
                    return;
                } else {
                    if (oldValue.startsWith("http")) {
                        try {
                            httpsredirect = isHTTPSRedirect(new URL(oldValue), new URL(newValue));
                        } catch (MalformedURLException e) {
                            httpsredirect = true;
                            e.printStackTrace();
                        }
                    }
                }

                urlField.setText(newValue);

                if (httpsredirect) {
                    return; // Redirect from HTTP version of site
                }

                if (allowPluginEvents()) {
                    if (!(newValue.replaceAll("[ . ]", "").equalsIgnoreCase(newValue) || newValue.startsWith("http"))) {
                        for (PluginBase plug : p.plugins) {
                            try {
                                plug.onURLChange(webEngine, urlField, new URL(oldValue), new URL(newValue));
                            } catch (MalformedURLException e) {
                                System.out.println(e);
                                System.err.println("Cant pass onURLChange event to plugin: " + plug.getPluginInfo().name
                                        + " v" + plug.getPluginInfo().version);
                            }
                        }
                    }
                }
            }
        });
        
        // JS alert() function handler
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>(){
            @Override
            public void handle(WebEvent<String> popupText) {
                boolean badPopup = false;
                if (popupText.toString().toLowerCase().contains("virus")) {
                    // Warn user if website trys to create an popup trying to get you two download an virus
                    badPopup = true;
                    JOptionPane.showMessageDialog(null, "The site you are visting has tryed to create an popup with the word 'virus' in it, Please be carefull on this site", "ZunoZap AntiPopupVirus", JOptionPane.WARNING_MESSAGE);
               }
                if (allowPluginEvents()) for (PluginBase plug : p.plugins)
                    plug.onPopup(badPopup);
                JOptionPane.showMessageDialog(null, popupText.getData(), "JS Popup", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        webEngine.titleProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> o, String oV, String nV){ tab.setText(nV); }
        });
    }

    private boolean isHTTPSRedirect(URL oldu, URL newu) {
        if (oldu.getProtocol().equalsIgnoreCase(newu.getProtocol())) return false;
        if (oldu.getProtocol().equalsIgnoreCase("https")) return false;

        if (newu.toString().replaceFirst(newu.getProtocol(), "").substring(3)
                .equalsIgnoreCase(oldu.toString().replaceFirst(oldu.getProtocol(), "").substring(3))) {
            return true;
        }
        return false;
    }

    /**
     * Add the items to the File menu dropdown list.
     */
    public final void regMenuItems() {
        MenuItem downloadPage = new MenuItem("Download Page for offline browsing");
        downloadPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                JOptionPane.showMessageDialog(null, "Not programed yet", "ZunoZap", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        MenuItem aboutPage = new MenuItem("About ZunoZap v"+version);
        aboutPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t){
                Tab aboutTab = new Tab("About");
                WebView WV = new WebView();
                setUserAgent(WV.getEngine());
                WV.getEngine().loadContent(String.format(aboutPageHTML(), WV.getEngine().getUserAgent(), WV.getEngine().isJavaScriptEnabled(), "ZunoZap/zunozap/master/LICENCE", "GNU General Public License v3") + getPluginNames());
                aboutTab.setContent(WV);
                tb.getTabs().add(tb.getTabs().size() - 1, aboutTab);
                tb.getSelectionModel().select(aboutTab);
            }
        });
        
        final MenuItem settingButton = new MenuItem("Settings");
        settingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t){new OptionMenu();}
        });
        
        MenuItem updateItem = new MenuItem("Check for Update");
        updateItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t){
                JOptionPane.showMessageDialog(null, Updater.browser(version, name), "ZunoZap Update",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuFile.getItems().addAll(downloadPage,aboutPage,updateItem,settingButton);
    }

    public static final String getPluginNames() {
        int size = p.plugins.size();
        return size != 0 ? "Plugins ["+ size +"]:"
               +String.valueOf(p.pluginNames).replace("[", "").replace("]", "") : "No Installed Plugins.";
    }

    @Override
    public final boolean allowPluginEvents() {
        return (p.plugins.size() != 0) && (super.allowPluginEvents());
    }
    
    @Deprecated
    protected boolean isOfficalZunoZap() {
        try {
            Class.forName("me.isaiah.zunozap.ZunoZap");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}