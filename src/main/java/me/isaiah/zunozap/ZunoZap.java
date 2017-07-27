package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.isaiah.zunozap.plugin.PluginBase;
import me.isaiah.zunozap.plugin.manager.PluginManager;

public class ZunoZap extends ZunoAPI {
    public static final String v = "0.3.5";
    public static final File homeDir = new File(System.getProperty("user.home"), "zunozap");
    private static final File localStorage = new File(homeDir, "offline-pages");
    private static final File dataDir = new File(homeDir, "webEngine");
    protected static final File temp = new File(homeDir, "temp");
    private static final File stylefolder = new File(homeDir, "styles");
    private static final File pluginfolder = new File(homeDir, "plugins");
    private final MenuBar menuBar = new MenuBar();
    private Menu menuFile = new Menu("File");
    private Menu book = new Menu("Bookmarks");
    private static TabPane tb;
    private static StyleManager sm;
    private final static PluginManager p = new PluginManager();
    public static boolean firstRun = false;

    /**
     * Launch
     * @throws IOException 
     */ 
    public static void main(String[] args) throws IOException {
        if (!new File(homeDir, "settings.txt").exists()) {
            if (!homeDir.exists()) homeDir.mkdir();
            new File(homeDir, "settings.txt").createNewFile(); // Fix error.
            firstRun = true;
        }
        launch(ZunoZap.class, args);
        double total = getTotalRamSavedFromGCinMB();
        if (total > 1048576) {
            System.out.println("[GC]: Total saved RAM: " + Math.floor(((total / 1024) / 1024) * 10 + 0.5) / 10 + " TB");
        } else if (total > 1024) {
            System.out.println("[GC]: Total saved RAM: " + Math.floor((total / 1024) * 10 + 0.5) / 10 + " GB");
        } else {
            System.out.println("[GC]: Total saved RAM: " + Math.floor(total * 10 + 0.5) / 10 + " MB");
        }
        System.exit(0); // exit after closing
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
    public void start(Stage stage, Scene scene, StackPane root, BorderPane borderPane) throws Exception {
        tb = new TabPane();
        System.setProperty(name + ".version", version);
        OptionMenu.init();

        if (!homeDir.exists()) homeDir.mkdir();
        if (!localStorage.exists()) localStorage.mkdir();
        if (!dataDir.exists()) dataDir.mkdir();
        if (!temp.exists()) temp.mkdir();

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("zunozaplogo.gif")));
        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);

        /* New tab button */
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
        if (!stylefolder.exists()) {
            stylefolder.mkdir();
        }
        sm = new StyleManager(stylefolder, scene);
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        p.loadPlugins();
        if (allowPluginEvents())
            for (PluginBase pl : p.plugins)
                pl.onLoad(stage, scene, tb);
    }

    @SuppressWarnings("static-access") 
    public final void createTab(boolean isStartTab) {
        tabnum++;

        /* Create Tab */
          final Tab tab = new Tab("Loading");
          tab.setTooltip(new Tooltip("Tab #"+tabnum));
          tab.setId("tab-"+tabnum);

        /* initialize variables */
        final Button backButton = new Button("<");
        final Button forwardButton = new Button(">");
        final Button goButton = new Button("Go");

        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        final TextField urlField = new TextField("http://");
        final HBox hBox = new HBox(backButton, forwardButton, urlField, goButton);
        final VBox vBox = new VBox(hBox, webView);

        /* Setup Event Handlers */
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

        /* Set Actions */
          goButton.setOnAction(goAction);
          backButton.setOnAction(backAction);
          forwardButton.setOnAction(forwardAction);
          urlField.setOnAction(goAction);

        /* Setting Styles */
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        hBox.setHgrow(urlField, Priority.ALWAYS);
        vBox.setVgrow(webView, Priority.ALWAYS);
        vBox.autosize();
        /* === */

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
     * When URL is changed make sure everything gets updated to new url.
     */
    public final void urlChangeLis(final WebEngine webEngine, final TextField urlField, final Tab tab) {
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @SuppressWarnings("rawtypes")
            @Override
            public void changed(ObservableValue ov, State oldState, State newState) {
                if (newState == Worker.State.FAILED) {
                    File f = new File(localStorage,
                            webEngine.getLocation().replaceAll("[ : / . ]", "-").trim() + ".html");
                    if (f.exists()) {
                        System.out.println(f.getAbsolutePath().substring(0, 3));
                        try {
                            webEngine.load(f.toURI().toURL().toExternalForm());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    System.err.println("Failed to load page. No internet?");
                    return;
                }
            }
        });

        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @SuppressWarnings("static-access")
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

                if (newValue.toLowerCase().contains("zunozap.github.io/addons/themes/")) {
                    showMessage("Press OK to start downloading theme");
                    URL website = null;
                    try {
                        website = new URL(newValue);
                    } catch (MalformedURLException e1) {
                        showMessage("Unable to download theme");
                        e1.printStackTrace();
                        return;
                    }
                    try (InputStream in = website.openStream()) {
                        File f = new File(stylefolder, newValue.substring(newValue.lastIndexOf("/") + 1));
                        Files.copy(in, Paths.get(f.toURI()), StandardCopyOption.REPLACE_EXISTING);
                        sm.b.clear();
                        try {
                            sm.init(stylefolder);
                        } catch (Exception e) {
                            showMessage("Unable to reload style manager.\nRestart is required to enable them.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessage("Unable to download theme.");
                        return;
                    }
                    showMessage("Downloaded theme");
                    return;
                }
                if (newValue.toLowerCase().contains("zunozap.github.io/addons/plugins/")) {
                    showMessage("Press OK to start downloading plugin");
                    URL website = null;
                    try {
                        website = new URL(newValue);
                    } catch (MalformedURLException e1) {
                        showMessage("Unable to download plugin");
                        e1.printStackTrace();
                        return;
                    }
                    try (InputStream in = website.openStream()) {
                        File f = new File(pluginfolder, newValue.substring(newValue.lastIndexOf("/") + 1));
                        Files.copy(in, Paths.get(f.toURI()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessage("Unable to download plugin.");
                        return;
                    }
                    showMessage("Downloaded plugin, restart required to use plugin.");
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

                if (httpsredirect)
                    return; // Redirect from HTTP version of site

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
                
                if (ZunoAPI.offlineStorage) {
                    DownloadPage(localStorage, temp, webEngine);
                }
            }
        });
        
        // JS alert() handler
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>(){
            @Override
            public void handle(WebEvent<String> popupText) {
                boolean badPopup = false;
                if (popupText.toString().toLowerCase().contains("virus")) {
                    badPopup = true;
                    JOptionPane.showMessageDialog(null, "The site you are visting has tryed to create an popup with the word 'virus' in it, Please be carefull on this site", "ZunoZap AntiPopupVirus", JOptionPane.WARNING_MESSAGE);
                }
                if (allowPluginEvents()) for (PluginBase pl : p.plugins)
                    pl.onPopup(badPopup);

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
        MenuItem downloadPage = new MenuItem("Clear all offline data.");
        MenuItem aboutPage = new MenuItem("About ZunoZap v" + version);
        MenuItem settingButton = new MenuItem("Settings");
        MenuItem updateItem = new MenuItem("Check for Update");

        downloadPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                temp.delete();
                localStorage.delete();
            }
        });

        aboutPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t){
                Tab aboutTab = new Tab("About");
                WebView w = new WebView();
                setUserAgent(w.getEngine());
                w.getEngine()
                        .loadContent(String.format(aboutPageHTML(), "ZunoZap", w.getEngine().getUserAgent(),
                                w.getEngine().isJavaScriptEnabled(), "ZunoZap/zunozap/master/LICENCE", "GPLv3")
                                + getPluginNames());
                aboutTab.setContent(w);
                tb.getTabs().add(tb.getTabs().size() - 1, aboutTab);
                tb.getSelectionModel().select(aboutTab);
            }
        });

        settingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t){new OptionMenu();}
        });
        
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

    public static String ExportResource(String resourceName) throws Exception {
        try {
            InputStream stream = ZunoZap.class.getClassLoader().getResourceAsStream(resourceName);
            if (stream == null) throw new Exception("Cannot get file " + resourceName + " from Jar file.");

            copy(resourceName, stream, homeDir.getAbsolutePath() + File.separator + "style.css");
        } catch (Exception e) {
            throw e;
        }

        return homeDir + resourceName;
    }

    public static boolean copy(String name, InputStream source, String destination) {
        System.out.println("Copying -> " + name + "\n\tto -> " + destination);

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;

    }
}