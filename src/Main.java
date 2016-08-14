import java.io.File;
import java.io.IOException;

import io.github.zunozap.zunoapi.ZunoAPI;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.geometry.Insets;

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
    @SuppressWarnings("rawtypes")
	final ComboBox comboBox = new ComboBox();
    @SuppressWarnings("unused")
	private boolean needDocumentationButton = false;
    private String version = "v0.0.3";
    @SuppressWarnings("unused")
	private File folder;
    
  @SuppressWarnings("static-access")
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
            //final boolean addButton = (hpl.getText().equals("Documentation"));

            // process event 
            hpl.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    //needDocumentationButton = addButton;
                    loadPage(url);
                    addressBar.setText(url.replace("http://", ""));
                }
            });
        }
        
        //sourcebutton.addActionListener(ActionListener) {
        sourcebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                    DownloadPage(addressBar.getText());
            }
        });
        
        aboutbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                    loadPage(Main.class.getResource("about.html").toExternalForm());
            }
        });

        comboBox.setPrefWidth(10);

        // create the toolbar
        //toolBar = new HBox();
        topbar = new HBox();
        
        Insets pi = new Insets(0, 10, 0, 10);
        topbar.setMargin(aboutbutton, pi);
        topbar.getChildren().add(sourcebutton);
        topbar.getChildren().add(aboutbutton);
        
        topbar.setAlignment(Pos.CENTER);
    	topbar.getStyleClass().add("browser-toolbar");
        topbar.getChildren().add(comboBox);
        topbar.getChildren().addAll(hpls);

        boolean enableBottomBar = false;
        if (enableBottomBar == true) {
        	toolBar.setAlignment(Pos.CENTER);
        	toolBar.getStyleClass().add("browser-toolbar");
        	toolBar.getChildren().add(comboBox);
        	toolBar.getChildren().addAll(hpls);
        }

    WebView ZunoZap = new WebView();
    ZunoZapEngine = ZunoZap.getEngine();

    ZunoAPI.loadStartPage(ZunoZapEngine);
    //loadPage("http://zunozap.github.io");
    addressBar.setText("zunozap:start");
    ZunoZapEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
      @Override public void changed(ObservableValue<? extends Throwable> observableValue, Throwable oldException, Throwable exception) {
        System.out.println("ZunoZap encountered an exception loading a page: " + exception); //TODO Load the page from the saved pages folder
      }
    });

    VBox root = new VBox();
    root.getChildren().setAll(
        topbar,
        addressBar,
        ZunoZap
    );
    stage.setScene(new Scene(root));
    stage.show();
  }
  
  
    @SuppressWarnings("unused")
	@Deprecated
	private static String getUrlSource(String site) throws IOException {
		return ZunoAPI.getUrlSource(site); //Moved to the ZunoAPI
	}

    private static void DownloadPage(String site) {
    	ZunoAPI.DownloadPage(site, addressBar, DPfolder); //Downloading the page has been moved to the ZunoAPI
    }
  
    public static void main(String[] args) {
	  	@SuppressWarnings("unused")
		ZunoAPI api = new ZunoAPI();
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