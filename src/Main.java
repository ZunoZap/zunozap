import javafx.application.Application;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.*;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import java.awt.Font;
import java.io.FileWriter;
import java.util.*;
import java.io.IOException;
import java.io.*;
import java.io.BufferedWriter;
import java.net.URL;
import java.net.URLConnection;

import javafx.scene.shape.Rectangle;
import java.awt.Color;
import javafx.scene.control.TabPane;

import javafx.scene.Node;
import javafx.scene.control.Button;

//import io.github.zunozap.zunoapi.ZunoAPI;

//import javax.swing.JButton;

public class Main extends Application {
  public WebView ZunoZap;
  private WebEngine ZunoZapEngine;
  final static TextField addressBar = new TextField();
  private Button sourcebutton = new Button("Download Page Source");
  private Button aboutbutton = new Button("About ZunoZap");
  private static File DPfolder = new File(System.getProperty("user.home") + File.separator + "ZunoZap" + File.separator + "Saved Pages");

  public void About() {
      String aboutpage = Main.class.getResource("about.html").toExternalForm();
      ZunoZap.getEngine().load(aboutpage);
  }
  //public void startPage() {} //TODO
  //public boolean EditPage(String on) {}
  
  //setOnAction(EventHandler<ActionEvent> value)
  
  private HBox toolBar;
  private HBox topbar;
    private static String[] imageFiles = new String[]{
        "blog.png",
        "documentation.png",
        "help.png",
    };
    private static String[] captions = new String[]{
        "Blogs",
        "Documentation",
        "Help",
    };
    private static String[] urls = new String[]{
        "http://isaiahpatton.blogspot.com",
        "http://zunozap.github.io",
        "http://zunozap.github.io",
    };
    final ImageView selectedImage = new ImageView();
    final Hyperlink[] hpls = new Hyperlink[captions.length];
    final Image[] images = new Image[imageFiles.length];
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final Button showPrevDoc = new Button("Toggle Previous Docs");
    final WebView smallView = new WebView();
    final ComboBox comboBox = new ComboBox();
    private boolean needDocumentationButton = false;
    private String version = "v0.0.3";
    private File folder;
    
  public void start(Stage stage) {
    stage.setTitle("ZunoZap " + version);
    
    File folder = new File(System.getProperty("user.home") + File.separator + "ZunoZap" + File.separator);
        File programsettings = new File(folder + "settings.txt");

            if (!folder.exists()) {
                folder.mkdir();
            }

            if (!DPfolder.exists()) {
                DPfolder.mkdir();
            }
        
        try {
            if (!programsettings.exists()) {
                System.out.println("Creating " + folder + "settings.txt");
                programsettings.createNewFile();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    
    addressBar.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            if (addressBar.getText().contains("http://") || addressBar.getText().contains("https://") || addressBar.getText().contains("file://") || addressBar.getText().contains("zunozap:")) {
                if (addressBar.getText().contains("zunozap:start") || addressBar.getText().contains("zunozap://start")) {
                    loadPage("http://zunozap.github.io");
                } else if (addressBar.getText().contains("zunozap:") && addressBar.getText().contains("about")) {
                    loadPage(Main.class.getResource("about.html").toExternalForm());
                } else {
                    loadPage(addressBar.getText());
                    String newtext = addressBar.getText();
                    newtext = newtext.replace("http://", "");
                    addressBar.setText(newtext);
                }
            } else {
                loadPage("http://" + addressBar.getText());
            }
        }
    });
    
    //apply the styles
        //getStyleClass().add("browser"); 

        for (int i = 0; i < captions.length; i++) {
            // create hyperlinks
            Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
            Image image = images[i] =
                    new Image(getClass().getResourceAsStream(imageFiles[i]));
            hpl.setGraphic(new ImageView(image));
            final String url = urls[i];
            final boolean addButton = (hpl.getText().equals("Documentation"));

            // process event 
            hpl.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    needDocumentationButton = addButton;
                    loadPage(url);
                    addressBar.setText(url.replace("http://", ""));
                }
            });
        }
        
        //sourcebutton.addActionListener(ActionListener) {
        sourcebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //try {
                    //System.out.println(getUrlSource(addressBar.getText()));
                    DownloadPage(addressBar.getText());
                //} catch (IOException io) {
                    //System.out.println("I'm sorry there as been an error :( " + io);
                //}
            }
        });
        
        aboutbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //try {
                    //System.out.println(getUrlSource(addressBar.getText()));
                    loadPage(Main.class.getResource("about.html").toExternalForm());
                //} catch (IOException io) {
                    //System.out.println("I'm sorry there as been an error :( " + io);
                //}
            }
        });

        comboBox.setPrefWidth(60);

        // create the toolbar
        toolBar = new HBox();
        topbar = new HBox();
        
        topbar.getChildren().add(sourcebutton);
        topbar.getChildren().add(aboutbutton);
        toolBar.setAlignment(Pos.CENTER);
        toolBar.getStyleClass().add("browser-toolbar");
        toolBar.getChildren().add(comboBox);
        toolBar.getChildren().addAll(hpls);

    WebView ZunoZap = new WebView();
    ZunoZapEngine = ZunoZap.getEngine();

    loadPage("http://zunozap.github.io");
    addressBar.setText("zunozap:start");
    /*ZunoZapEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
      @Override public void changed(ObservableValue<? extends Throwable> observableValue, Throwable oldException, Throwable exception) {
        System.out.println("ZunoZap encountered an exception loading a page: " + exception);
      }
    });*/

    VBox root = new VBox();
    root.getChildren().setAll(
        topbar,
        addressBar,
        ZunoZap,
        toolBar
    );
    stage.setScene(new Scene(root));
    stage.show();
  }
  
    private static String getUrlSource(String site) throws IOException {
        URL url;
        if (site.contains("http://") || site.contains("https://")) {
            url = new URL(site);
        } else {
            url = new URL("http://" + site);
        }
        
        if (site == "http://zunozap:start" || site == "zunozap:start") {
            site = "zunozap.github.io";
        }
        
        URLConnection urlc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
        urlc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        a.append(inputLine);
        in.close();

        return a.toString();
    }

    private static void DownloadPage(String site) {        
         try{       
                    File htmlsourcefile = new File(DPfolder + File.separator + addressBar.getText() + ".html");

                    // if file doesnt exists, then create it
                    if (!htmlsourcefile.exists()) {
                        htmlsourcefile.createNewFile();
                    }
                    
                    FileWriter fw = new FileWriter(htmlsourcefile.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write("<!--");
                    bw.write("HTML source for: " + addressBar.getText());
                    bw.write("By ZunoZap Web Browser's Download Page Source");
                    bw.write("-->");
                    bw.write(getUrlSource(addressBar.getText()));
                    bw.close();
                    System.out.println("Downloaded source code for: " + addressBar.getText() + "Find it in your ZunoZap folder!");
                    
        } catch(IOException ioe) {
            System.out.println(ioe);
        }
    }
  
  public static void main(String[] args) {
        launch(args);
    }

    public void loadPage(String url) {
        ZunoZapEngine.load(url);
    }
    
    public void Listener() {
        ZunoZapEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override public void changed(ObservableValue<? extends Throwable> observableValue, Throwable oldException, Throwable exception) {
                System.out.println("ZunoZap encountered an exception loading a page: " + exception);
            }
        });
    }
}