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

public class Main extends Application {
  private WebEngine ZunoZapEngine;
  final static TextField addressBar = new TextField();

  //public void AboutPage() {} //TODO
  //public void startPage() {} //TODO
  //public void DownloadSource(String url) {}
  //public boolean EditPage(String on) {}
  
  private HBox toolBar;
    private static String[] imageFiles = new String[]{
        "blog.png",
        "documentation.png",
        "help.png",
        "HTML_Logo.png"
    };
    private static String[] captions = new String[]{
        "Blogs",
        "Documentation",
        "Help",
        "We now allow HTML5!"
    };
    private static String[] urls = new String[]{
        "http://isaiahpatton.blogspot.com",
        "http://zunozap.github.io",
        "http://zunozap.github.io",
        "http://zunozap.github.io/"
        //getsource()
    };
    private static String getsource() {
        StringBuilder a;
        a = new StringBuilder();
        try{
            getUrlSource(addressBar.getText());
        } catch (IOException exio) {
            System.out.println(exio);
        }
        return a.toString();
    }
    final ImageView selectedImage = new ImageView();
    final Hyperlink[] hpls = new Hyperlink[captions.length];
    final Image[] images = new Image[imageFiles.length];
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final Button showPrevDoc = new Button("Toggle Previous Docs");
    final WebView smallView = new WebView();
    final ComboBox comboBox = new ComboBox();
    private boolean needDocumentationButton = false;
    private String version = "v0.0.2";
    
    public void Browser() {
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
                    webEngine.load(url);
                    addressBar.setText(url);
                }
            });
        }

        comboBox.setPrefWidth(60);

        // create the toolbar
        toolBar = new HBox();
        toolBar.setAlignment(Pos.CENTER);
        toolBar.getStyleClass().add("browser-toolbar");
        toolBar.getChildren().add(comboBox);
        toolBar.getChildren().addAll(hpls);
        //toolBar.getChildren().add(createSpacer());
    }
  
  public void start(Stage stage) {
    stage.setTitle("ZunoZap " + version);

    
    final Menu menu1 = new Menu("File");
    
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(menu1);
    
    addressBar.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        if (addressBar.getText().contains("http://") || addressBar.getText().contains("https://") || addressBar.getText().contains("file://") || addressBar.getText().contains("zunozap:")) {
            if (addressBar.getText().contains("zunozap:start") || addressBar.getText().contains("zunozap://start")) {
                ZunoZapEngine.load("http://zunozap.github.io");
            } else {
                ZunoZapEngine.load(addressBar.getText());
                String newtext = addressBar.getText();
                newtext = newtext.replace("http://", "");
                addressBar.setText(newtext);
            }
        } else {
            ZunoZapEngine.load("http://" + addressBar.getText());
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
                    ZunoZapEngine.load(url);
                    addressBar.setText(url + "TEST");
                }
            });
        }

        comboBox.setPrefWidth(60);

        // create the toolbar
        toolBar = new HBox();
        toolBar.setAlignment(Pos.CENTER);
        toolBar.getStyleClass().add("browser-toolbar");
        toolBar.getChildren().add(comboBox);
        toolBar.getChildren().addAll(hpls);
        //toolBar.getChildren().add(createSpacer());
    
    

    WebView myBrowser = new WebView();
    ZunoZapEngine = myBrowser.getEngine();
    ZunoZapEngine.load("http://zunozap.github.io");
    addressBar.setText("zunozap:start");
    ZunoZapEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
      @Override public void changed(ObservableValue<? extends Throwable> observableValue, Throwable oldException, Throwable exception) {
        System.out.println("ZunoZapEngine encountered an exception loading a page: " + exception);
      }
    });

    VBox root = new VBox();
    //Browser();
    root.getChildren().setAll(
        addressBar,
        myBrowser,
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
        
        if (url == new URL("http://") || url.toString() == "http://") {
            url = new URL("http://google.com");
        } else {
            //Else not needed
        }
        URLConnection urlc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
        urlc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        a.append(inputLine);
        in.close();

        //a = a.toString();
        return a.toString();
    }
  

  public static void main(String[] args) {
        launch(args);
        
    }
}