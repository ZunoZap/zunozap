package com.zunozap;

import static com.javazilla.chromium.BrowserPreferences.addSwitch;
import static com.zunozap.Settings.ADVANCED;
import static com.zunozap.Settings.BKMARK;
import static com.zunozap.Settings.DS;
import static com.zunozap.Settings.GENERAL;

import java.io.IOException;
import java.util.HashMap;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.zunozap.EngineHelper.EngineType;
import com.zunozap.Settings.Options;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ZunoZap extends Application {

    public static final String NAME = "ZunoZap";
    public static final String VERSION = "10.1";

    public static TabPane tb;
    public static boolean firstRun;
    public static Settings s;

    public static void main(String[] args) throws IOException {
        launch(ZunoZap.class, args);

        Platform.exit();
    }

    @Override
    public void init() throws IOException {
        if (!Settings.DAT.exists()) {
            Settings.DAT.getParentFile().mkdirs();
            Settings.DAT.createNewFile();
            Settings.save();
            firstRun = true;
        }
    }

    public static StackPane root;
    public static HBox controls;
    public static Stage stage;
    public static Pane layout;

    @Override
    public void start(Stage stage) throws Exception {
        tb = new TabPane();
        tb.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        ZunoZap.stage = stage;

        root = new StackPane();
        BorderPane border = new BorderPane();

        root.getChildren().add(border);
        if (Options.COMPACT.b) root.setBorder(new Border(new BorderStroke(Color.web("rgb(64,62,60)"), 
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        Scene scene = new Scene(root, 900, 500);
        Bookmarks.load();

        stage.getIcons().add(new Image(ZunoZap.class.getClassLoader().getResourceAsStream("assets/logo.png")));

        layout = new AnchorPane();
        Settings.init(scene);
        if (null != Settings.stylesheet)
            Settings.changeStyle(Settings.styleName);

        if (EngineHelper.type == EngineType.CHROME) {
            if (Options.PPS.b) addSwitch("--process-per-site");
            if (Options.NOGL.b) addSwitch("--disable-webgl");
            if (Options.DGPU.b) addSwitch("--disable-gpu");
        }

        controls = Options.COMPACT.b ? setupWindowControls(stage) : new HBox();
        controls.setId("wc");

        layout.getChildren().addAll(tb, controls);
        AnchorPane.setTopAnchor(controls, 0.0);
        AnchorPane.setRightAnchor(controls, 0.0);
        AnchorPane.setTopAnchor(tb, 0.0);
        AnchorPane.setRightAnchor(tb, 0.0);
        AnchorPane.setLeftAnchor(tb, 0.0);
        AnchorPane.setBottomAnchor(tb, 0.0);

        layout.setBorder(Border.EMPTY);
        border.setCenter(layout);
        border.autosize();

        Tab newtab = new Tab(" + ");
        newtab.setClosable(false);
        newtab.setId("createtab");
        tb.getTabs().add(newtab);
        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab(Settings.NEW_TAB); });

        createTab(Settings.NEW_TAB);

        stage.setScene(scene);
        if (Options.COMPACT.b) ResizeHelper.addResizeListener(stage);
        stage.setTitle(NAME + " " + VERSION);
        s = new Settings();
        Platform.setImplicitExit(true);
        stage.show();
    }

    public static void refreshControls() {
        if (!layout.getChildren().contains(controls))
            return;
        layout.getChildren().remove(controls);
        controls = Options.COMPACT.b ? setupWindowControls(stage) : new HBox();
        controls.setId("wc");
        layout.getChildren().add(controls);
        AnchorPane.setTopAnchor(controls, 0.0);
        AnchorPane.setRightAnchor(controls, 0.0);
        AnchorPane.setTopAnchor(tb, 0.0);
        AnchorPane.setRightAnchor(tb, 0.0);
        AnchorPane.setLeftAnchor(tb, 0.0);
        AnchorPane.setBottomAnchor(tb, 0.0);
    }

    @Override
    public void stop(){
        try {
            if (EngineHelper.type == EngineType.CHROME)
                com.javazilla.chromium.Browser.shutdown();
        } catch (Exception e) {}
        Platform.exit();
        System.exit(0);
    }

    public static HBox setupWindowControls(Stage sta1ge) {
        if (!stage.isShowing())
            stage.initStyle(StageStyle.UNDECORATED);

        Button close = new Button("", getImage("close.png", -1));
        close.setOnAction(a -> {
            Settings.save();
            try {
                if (EngineHelper.type == EngineType.CHROME)
                    com.javazilla.chromium.Browser.shutdown();
            } catch (Exception e) {}
            stage.close();
            Platform.exit();
            System.exit(0);
        });

        Button max = new Button("", getImage("max.png", -1));
        max.setOnAction(a -> stage.setMaximized(!stage.isMaximized()));

        Button min = new Button("", getImage("min.png", -1));
        min.setId("xbtn");
        min.setOnMouseClicked(a -> stage.setIconified(true));

        Button[] btns = {min, max, close};
        for (Button btn : btns) {
            btn.setId("xbtn");
            btn.setPadding(Insets.EMPTY);
        }
        return new HBox(min, max, close);
    }

    public static final HashMap<VBox, Engine> tabs = new HashMap<>();
    private static JFXDialogLayout content = new JFXDialogLayout();

    public static boolean dark = false;

    private static ImageView getImage(Object path, int scale) {
        if (path instanceof Image) {
            ImageView i2 = new ImageView((Image)path);
            if (scale != -1) {
                i2.setFitWidth(scale);
                i2.setFitHeight(scale);
            }
            return i2;
        }
        String ps = (String)path;
        Image i = ps.startsWith("http") ? new Image(ps) : new Image(ZunoZap.class.getClassLoader().getResourceAsStream("assets/" + ps));

        PixelReader pixelReader = i.getPixelReader();
        WritableImage wImage = new WritableImage((int)i.getWidth(), (int)i.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();
        for(int readY=0;readY<i.getHeight();readY++){
            for(int readX=0; readX<i.getWidth();readX++){
                Color color = pixelReader.getColor(readX,readY);
                color = color.getRed()+color.getBlue()+color.getGreen() == 3 ? Color.TRANSPARENT : (dark ? color.brighter() : color); 
                pixelWriter.setColor(readX,readY,color);
            }
        }
        ImageView i2 = new ImageView(wImage);
        if (scale != -1) {
            i2.setFitWidth(scale);
            i2.setFitHeight(scale);
        }
        return i2;
    }

    private static Image I_BACK = new Image(ZunoZap.class.getClassLoader().getResourceAsStream("assets/back.png"));
    private static Image I_BKMA = new Image(ZunoZap.class.getClassLoader().getResourceAsStream("assets/bookmark.png"));
    private static Insets SETT_I = new Insets(22,10,80,48);

    public static final void createTab(String url) {
        int tabnum = tb.getTabs().size() + 1;

        final Tab tab = new Tab("     Loading     ");
        tab.setTooltip(new Tooltip("Tab " + tabnum));
        tab.setId("tab-"+tabnum);

        final Button back = new Button("", getImage(I_BACK, 18)), forward = new Button(">"), 
                bkmark = new Button("", getImage(I_BKMA, 18));
        back.setBackground(Background.EMPTY);
        bkmark.setBackground(Background.EMPTY);

        Engine e = EngineHelper.newBrowser(url);
        TextField field = new TextField("http://");

        Button sett = new Button("", getImage("setting.png", 18));
        sett.setOnAction(a -> {
            Tab t = new Tab(pad("Settings"));
            GENERAL.setPadding(SETT_I);
            GENERAL.setBorder(Border.EMPTY);
            ADVANCED.setPadding(SETT_I);
            BKMARK.setPadding(SETT_I);
            DS.setPadding(SETT_I);

            VBox si = new VBox(11);
            si.setPadding(new Insets(15,10,10,10));

            Label l = new Label(NAME);
            l.setFont(Font.font(28));

            Button se = new Button("General");
            se.setOnAction(b -> t.setContent(new SBox(si, new ScrollPane(GENERAL))));
            se.setPadding(new Insets(5, 5, 5, 5));

            Button ds = new Button("Privacy / Data Saving");
            ds.setOnAction(b -> t.setContent(new SBox(si, new ScrollPane(Settings.DS))));
            ds.setPadding(new Insets(5, 5, 5, 5));

            Button ad = new Button("Advanced");
            ad.setOnAction(b -> t.setContent(new SBox(si, new ScrollPane(ADVANCED))));
            ad.setPadding(new Insets(5, 5, 5, 5));

            Button bk = new Button("Bookmarks");
            bk.setOnAction(b -> {t.setContent(new SBox(si, new ScrollPane(BKMARK))); s.refreshBk();});
            bk.setPadding(new Insets(5, 5, 5, 5));

            Button ab = new Button("About");
            ab.setOnAction(b -> {
                Text title = new Text(NAME + " Browser");
                title.setTranslateX(158);
                title.setTranslateY(-56);
                title.setFont(Font.font("System", FontWeight.SEMI_BOLD, 42));
                String fx = System.getProperty("javafx.ver", System.getProperty("javafx.runtime.version").split("[(]")[0]);
                Text body = new Text("Version " + VERSION + "\nOpenJFX build: " + fx + "\nJava version: " +
                        System.getProperty("java.version") + "\n\nMade possible by the OpenJFX & Chromium projects");
                body.setFont(Font.font(12));
                body.setTranslateX(164);
                body.setTranslateY(24);

                ImageView iv = getImage("https://zunozap.javazilla.com/assets/zunozap-logo-x256.png",144);
                content.heading(iv, title, body);
                content.setPadding(new Insets(24,120,0,4));

                JFXDialog dialoge = new JFXDialog(root, content);
                dialoge.show();
            });
            si.setBackground(new Background(new BackgroundFill(Color.web("#eeeeee"), null, null)));
            si.getChildren().addAll(l, se, bk, ds, ad, ab);
            Button[] btns = {se, bk, ds, ad, ab};
            for (Button bu : btns) {
                bu.setMinWidth(128);
                bu.setMinHeight(38);
            }
            si.setId("si");
            t.setContent(new SBox(si, new ScrollPane(GENERAL)));
            tb.getTabs().add(tb.getTabs().size() - 1, t);
            tb.getSelectionModel().select(t);
        });

        BorderPane hBox = new BorderPane();
        hBox.setLeft(back);
        hBox.setRight(new HBox(bkmark, sett));
        Button pro = new Button("");
        pro.setBackground(Background.EMPTY);
        sett.setBackground(Background.EMPTY);
        HBox mid = new HBox(pro,field);
        mid.maxWidthProperty().bind(field.maxWidthProperty());
        pro.maxHeightProperty().bind(field.heightProperty());
        hBox.setCenter(mid);
        hBox.setPadding(new Insets(4,15,4,15));

        VBox vBox = new VBox(hBox, e.getComponent());

        tabs.put(vBox, e);
        Bookmarks.refreshBar();
        addHandlers(e, field, tab, bkmark, pro);

        field.setOnAction(v -> e.load(field.getText()));
        back.setOnAction(v -> e.history(0));
        forward.setOnAction(v -> e.history(1));
        bkmark.setOnAction(v -> {
            if (Bookmarks.map.containsKey(e.getURL()))
                Bookmarks.remove(e.getURL());
            else Bookmarks.add(e.getTitle(), e.getURL());
        });

        // Setting Styles
        field.setId("urlfield");
        field.setMaxWidth(600);
        hBox.setId("urlbar");
        HBox.setHgrow(field, Priority.ALWAYS);
        VBox.setVgrow(e.getComponent(), Priority.ALWAYS);

        hBox.getChildren().remove(forward);

        tab.setContent(vBox);
        tab.setOnCloseRequest(a -> {
            tabs.remove(vBox);
            e.stop();
        });

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
        Settings.save();
    }

    public static class SBox extends HBox {
        public SBox(Node...nodes) {
            super(nodes);
            ScrollPane sp = (ScrollPane) nodes[1];
            sp.setBorder(Border.EMPTY);
            HBox.setHgrow(sp.getContent(), Priority.ALWAYS);
            sp.setHbarPolicy(ScrollBarPolicy.NEVER);
            ((VBox)sp.getContent()).minWidthProperty().bind(sp.widthProperty());
            ((VBox)sp.getContent()).minHeightProperty().bind(sp.heightProperty());
            HBox.setHgrow(nodes[1], Priority.ALWAYS);
        }
    }

    public static void changed(final Engine engine, final TextField field, final Tab tab, String old, String url, Button bkmark, Button pro) {
        if (old == null && url.contains(Settings.NEW_TAB)) return;
        if (old == null || old.isEmpty()) {
            field.setText(url);
            return;
        }
        field.setText(url);
    }

    public static void changeTitle(Engine u, TextField urlField, Tab tab, Button bkmark, Button pro) {
        Platform.runLater(() -> {
            pro.setText(u.getURL().startsWith("https") ? "" : "\uD83D\uDD13");
            pro.setTextFill(Color.DARKRED);
            urlField.setText(u.getURL().replace("https://","").replace("http://","").replaceFirst("www.",""));
            pro.setTooltip(new Tooltip(u.getURL().startsWith("https") ? "This site uses secure HTTPS" : "This site is unsecure!"));
            String n = u.getTitle();
            tab.setText((n == null) ? u.getURL() : ((n.length() > 30) ? n.substring(0,30) : (n.length() < 20 ? pad(n) : n)));
        });
    }

    public static final void addHandlers(Engine u, final TextField urlField, final Tab tab, final Button bkmark, Button pro) {
        u.addHandlers(urlField, tab, bkmark, pro);
    }

    public static String pad(String s) {
        return "           " + s + "           ";
    }

}