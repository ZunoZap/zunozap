package com.zunozap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import com.javazilla.chromium.BrowserPreferences;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.zunozap.EngineHelper.EngineType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public class Settings {

    public static File HOME = new File(System.getProperty("user.home"), "ZunoZap");
    public static File STYLES = new File(HOME, "styles");
    public static File DAT = new File(HOME, "settings.dat");

    private static HashMap<String, Object> map = new HashMap<>();

    public static String styleName = "None";
    public static String NEW_TAB = "http://google.com";
    public static String SEARCH = "http://duckduckgo.com/?q=%s";
    public static File stylesheet;

    public static Scene s;
    public static HashMap<String, File> b = new HashMap<>(), l = new HashMap<>();
    public ArrayList<Runnable> l2 = new ArrayList<>();
    public static int dataSaver = 1;
    public static int compress = 0;

    public static Text dst = new Text("");
    public static int dsm = 0;
    public static void increaseDSAM(int kb) {
        dsm += kb;
        dst.setText("Estimated data savings: " + (dsm > 1000 ? (((double)((dsm*100)/1024)/100) + " MB") : (dsm + " KB")));
    }

    public static VBox GENERAL = new VBox(), ADVANCED = new VBox(), BKMARK = new VBox(), DS = new VBox();

    public enum Options {
        forceHTTPS(false, "Force HTTPS"), offlineStorage(false, "Cache offline pages"), COMPACT(true, "Compact window (requires restart)"),
        SHOW_BB(true, "Show the Bookmarks bar"), PPS(false, "Use one process per site", ADVANCED), NOGL(false, "Disable Web GL", ADVANCED), 
        DGPU(false, "Disable GPU Process", ADVANCED), RUFFLE(false, "Use Ruffle Flash Player", ADVANCED); // TODO Ruffle

        public boolean b, def;
        public String z;
        public VBox box = GENERAL;

        private Options(boolean d, String s, VBox box) {this(d,s);this.box = box;}
        private Options(boolean d, String z) {
            this.b = d; 
            this.def = d;
            this.z = z;
        }
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    public Settings() {
        GENERAL.setId("si");
        ADVANCED.setId("si");
        BKMARK.setId("si");
        DS.setId("si");
        GENERAL.setPadding(new Insets(0,0,30,0));

        for (Options o : Options.values()) addCheckBox(o.box, o); // CHECKBOXES

        // THEME
        comboBox(GENERAL, "Theme", a -> changeStyle(((ComboBox<String>) a.getSource()).getValue()),
                styleName, b.keySet().toArray(new String[0]));

        // ENGINE
        comboBox(ADVANCED, "Web Engine", a -> {
            if (EngineHelper.type == ((ComboBox<EngineType>) a.getSource()).getValue()) return;
            JFXDialogLayout content = new JFXDialogLayout();
            Text title = new Text("WARNING");
            title.setFont(Font.font(24));
            Text body = new Text("This changes the engine used to display webpages! Only change this if you know what you are doing!");
            body.setFont(Font.font(14));

            Button b = new Button("Change");
            Button c = new Button("Don't change");
            content.heading(title);
            VBox box = new VBox(body);
            box.setSpacing(16);
            content.body(box);
            content.actions(b,c);

            JFXDialog dialoge = new JFXDialog(ZunoZap.root, content, false);
            dialoge.show();
            b.setOnAction(d -> {
                EngineHelper.type = ((ComboBox<EngineType>) a.getSource()).getValue();
                save();
                dialoge.close();
            });
            c.setOnAction(d -> { dialoge.close(); ((ComboBox<EngineType>) a.getSource()).setValue(EngineHelper.type);});
        }, EngineHelper.type, EngineType.values());

        // BAR POS
        comboBox(GENERAL, "Tab Postioning", a -> {
            Object value = ((ComboBox<String>) a.getSource()).getValue();
            ZunoZap.tb.setSide((Side) value);
            save();
        }, ZunoZap.tb.getSide(), Side.values());

        textField(GENERAL, "Home Page", NEW_TAB).setOnAction(a -> { NEW_TAB = ((TextField)a.getSource()).getText(); save(); });
        textField(GENERAL, "Search Engine", SEARCH).setOnAction(a -> { SEARCH = ((TextField)a.getSource()).getText(); save(); });
        refreshBk();

        BrowserPreferences.setJavaScriptEnabled(dataSaver < 3);
        BrowserPreferences.setBlockRemoteFonts(dataSaver > 1);

        datasaver();
        imgCompress();
    }

    @SuppressWarnings("deprecation")
    public void datasaver() {
        Label l = new Label("Data Saver & Privacy Protection");
        l.setPadding(new Insets(8,6,2,0));
        l.setFont(Font.font(18));
        DS.getChildren().add(l);
        Slider slid = new Slider();
        slid.setMin(1);
        slid.setMax(3);
        slid.setMajorTickUnit(1);
        slid.setShowTickLabels(true);
        slid.setValue(dataSaver);
        slid.setLabelFormatter(new StringConverter<Double>() {
            public Double fromString(String arg0) {return null;}
            public String toString(Double d) {
                if (d == 1) return "Basic";
                if (d == 2) return "Advanced";
                if (d == 3) return "Strict";
                else return "error";
            }});
        String[] te = {
            "Off - No Protection",
            "Basic - Only blocks Google Ads & analytics",
            "Advanced = Basic + No Remote Fonts & More trackers blocked",
            "Strict   = Advanced + No Javascript (Most sites will break)"
        };
        Text txt = new Text(te[dataSaver]);
        slid.valueProperty().addListener((obs, oldval, newVal) -> {
        slid.setValue(newVal.intValue()); dataSaver = newVal.intValue(); 
            txt.setText(te[dataSaver]);
            BrowserPreferences.setJavaScriptEnabled(dataSaver < 3);
            BrowserPreferences.setBlockRemoteFonts(dataSaver > 1);
            save();
        });
        slid.setScaleX(1.2);
        slid.setScaleY(1.2);
        slid.setMaxSize(208,400);
        slid.setPadding(new Insets(16));
        DS.getChildren().add(slid);
        DS.getChildren().add(txt);
        DS.getChildren().add(dst);
    }

    public void imgCompress() {
        Label l = new Label("Image Compression");
        l.setPadding(new Insets(40,6,2,0));
        l.setFont(Font.font(18));
        DS.getChildren().add(l);
        Slider slid = new Slider();
        slid.setMin(0);
        slid.setMax(3);
        slid.setMajorTickUnit(1);
        slid.setShowTickLabels(true);
        slid.setValue(compress);
        slid.setLabelFormatter(new StringConverter<Double>() {
            public Double fromString(String arg0) {return null;}
            public String toString(Double d) {
                if (d == 1) return "Basic";
                if (d == 2) return "Advanced";
                if (d == 3) return "Strict";
                else return "Off";
            }});
        String[] te = {
            "Off - Don't compress image",
            "Basic - Lightly compress image",
            "Advanced = Compress image and convert to Black White",
            "Strict   = Compress image, back white, small size."
        };
        Text txt = new Text(te[compress]);
        slid.valueProperty().addListener((obs, oldval, newVal) -> {
        slid.setValue(newVal.intValue()); compress = newVal.intValue(); 
            txt.setText(te[compress]);
            save();
        });
        slid.setScaleX(1.2);
        slid.setScaleY(1.2);
        slid.setMaxSize(208,400);
        slid.setPadding(new Insets(16));
        DS.getChildren().add(slid);
        DS.getChildren().add(txt);
    }

    public void refreshBk() {
        BKMARK.getChildren().clear();
        Label l = new Label("Bookmarks");
        l.setFont(Font.font(18));
        BKMARK.setSpacing(8);
        BKMARK.getChildren().add(l);
        for (String b : Bookmarks.map.keySet()) {
            HBox box = new HBox();
            String title = Bookmarks.map.get(b);
            Label f = new Label(title.length() > 34 ? title.substring(0,34) + ".." : title);
            f.setTooltip(new Tooltip(b));
            Button go = new Button("Visit");
            go.setOnAction(a -> ZunoZap.createTab(b));
            Button rm = new Button("Unbookmark");
            rm.setOnAction(a -> Bookmarks.remove(b));
            go.setMinSize(86,25);
            rm.setMinSize(86,25);
            box.getChildren().add(f);
            f.setMinWidth(300);
            f.setPadding(new Insets(4,48,4,28));
            box.setSpacing(8);
            box.getChildren().add(go);
            box.getChildren().add(rm);
            BKMARK.getChildren().add(box);
        }
    }

    private final TextField textField(VBox box, String n, String cur) {
        Label l3 = new Label(n);
        l3.setFont(Font.font(14));
        l3.setPadding(new Insets(26,6,2,0));

        TextField f = new TextField(cur);
        f.setPrefSize(265, 25);
        f.setPadding(new Insets(4,2,4,16));
        Button b = new Button("Save");
        b.setMinSize(50,25);
        b.setOnAction(ev -> f.getOnAction().handle(new ActionEvent(f,ev.getTarget())));
        box.getChildren().add(l3);
        box.getChildren().add(new HBox(f, b));
        return f;
    }

    private final <T> ComboBox<T> comboBox(VBox box, String n, EventHandler<ActionEvent> ae, T v, T[] l) {
        Label l3 = new Label(n);
        l3.setFont(Font.font(14));
        l3.setPadding(new Insets(16,6,2,0));

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T e : l) list.add(e);
        ComboBox<T> cb = new ComboBox<>(list);
        cb.setValue(v);
        cb.setOnAction(ae);
        cb.setMinWidth(148);
        cb.setPadding(new Insets(0,2,2,16));
        box.getChildren().add(l3);
        box.getChildren().add(cb);
        return cb;
    }

    private final void addCheckBox(VBox vbox, Options o) {
        CheckBox box = new CheckBox();
        box.setPadding(new Insets(6));
        box.setSelected(o.b);
        box.setText(o.z);
        box.setOnAction(a -> {
            if (o == Options.RUFFLE && box.isSelected()) {box.setSelected(false);return;}

            o.b = box.isSelected();
            if (o == Options.SHOW_BB) Bookmarks.refreshBar();
            save();
        });
        vbox.getChildren().add(box);
    }

    public static void changeStyle(String str) {
        stylesheet = b.get(str);
        styleName = str;
        try {
            try {
                for (String s : Files.readAllLines(Paths.get(b.get(str).toURI()))) {
                    if (s.contains("tab-header-background") && s.contains("fx-background-color")) {
                        String col = (s= s.substring(s.indexOf("background-color")+17)).substring(0,s.indexOf(")")+1);
                        Color c = Color.web(col);
                        ZunoZap.dark = (c.getRed() < 0.4 && c.getGreen() < 0.4 && c.getBlue() < 0.4);
                        ZunoZap.refreshControls();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (str.equalsIgnoreCase("Java")) {
                s.getStylesheets().clear();
            } else s.getStylesheets().setAll(b.get("ZunoZap default").toURI().toURL().toExternalForm(),
                    b.get(str).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) { ErrorPage.newTab(e); }
        save();
    }

    public static void save() {
        map.put("styleName", styleName);
        if (stylesheet != null)
            map.put("styleFile", stylesheet.getAbsolutePath());
        map.put("newtab", NEW_TAB);
        map.put("search", SEARCH);
        map.put("engine", EngineHelper.type);
        map.put("enginecl", EngineHelper.engine.getName());
        map.put("datasaver", dataSaver);
        map.put("dsm", dsm);
        map.put("compress", compress);
        for (Options o : Options.values())
            map.put(o.name(), o.b);
        Bookmarks.saveMap();
        saveMap();
    }

    public static void saveMap() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(DAT);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static void load() {
        try {
            FileInputStream fis = new FileInputStream(DAT);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap<String, Object>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        styleName = (String) map.getOrDefault("styleName", "ZunoZap default");
        stylesheet = new File((String) map.getOrDefault("styleFile", "{NUL}"));
        if (stylesheet.getName().equalsIgnoreCase("{NUL}")) stylesheet = null;
        NEW_TAB = (String) map.getOrDefault("newtab", "http://start.duckduckgo.com/");
        SEARCH = (String) map.getOrDefault("search", "http://duckduckgo.com/?q=%s");
        dataSaver = (Integer)map.getOrDefault("datasaver",1);
        dsm = (Integer)map.getOrDefault("dsm",0);
        compress = (Integer)map.getOrDefault("compress", 0);

        String sid = (String) map.get("side");

        ZunoZap.tb.setSide(null != sid ? Side.valueOf(sid) : Side.TOP);

        try {
            EngineHelper.setEngine((EngineType) map.get("engine"));
        } catch (Exception e) {
            e.printStackTrace();
            EngineHelper.setEngine(EngineType.CHROME);
        }

        for (String s : map.keySet()) {
            Object v = map.get(s);
            if (v instanceof Boolean) {
                Options op = null;
                for (Options o : Options.values()) {
                    if (o.name().equals(s)) {
                        op = o;
                        break;
                    }
                }
                if (null != op) op.b = (boolean) v;
            }
        }
    }

    public static boolean init(Scene scene) {
        s = scene;
        try {
            load();
            Bookmarks.load();
            DAT.createNewFile();
        } catch (IOException e) {/*First run*/}

        exportResource("style.css", HOME);
        exportResource("dark.css", STYLES);
        exportResource("old_default.css", STYLES);
        exportResource("night.css", STYLES);

        File f = new File(HOME, "style.css");
        b.put("ZunoZap default", f);
        if (styleName == null || styleName.equalsIgnoreCase("none") || ZunoZap.firstRun) {
            stylesheet = f;
            styleName = "ZunoZap default";
        }
        for (File fi : STYLES.listFiles()) b.put(fi.getName().replace(".css","").replace("_"," "), fi);

        save();
        if (styleName.equalsIgnoreCase("ZunoZap default")) stylesheet = f;
        return true;
    }

    public static Path exportResource(String res, File folder) {
        try (InputStream stream = Settings.class.getClassLoader().getResourceAsStream(res)) {
            if (stream == null) throw new IOException("Null " + res);

            Path p = Paths.get(folder.getAbsolutePath() + File.separator + res);
            Files.copy(stream, p, StandardCopyOption.REPLACE_EXISTING);
            return p;
        } catch (IOException e) { e.printStackTrace(); return null;}
    }

}