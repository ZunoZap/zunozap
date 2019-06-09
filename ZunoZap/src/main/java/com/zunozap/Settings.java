package com.zunozap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zunozap.UniversalEngine.Engine;
import com.zunozap.lang.ChangeLis;
import com.zunozap.lang.Lang;
import com.zunozap.lang.LangManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Settings extends VBox {

    private static File f = new ZFile("settings.dat", false);

    private static HashMap<String, Object> map = new HashMap<>();

    public static String styleName = "None";
    public static String tabPage = "http://start.duckduckgo.com/";
    protected static String searchEn = "http://duckduckgo.com/?q=%s";

    private List<Node> c;
    private static ZFile settings = new ZFile("settings.txt", false);
    public static Scene s;
    public static HashMap<String, File> b = new HashMap<>(), l = new HashMap<>();
    public ArrayList<ChangeLis> l2 = new ArrayList<>();

    public static void set(File folder, Scene sc) { s = sc; }

    public enum Options {
        forceHTTPS(false, "HTTPS"), DIS_PL(false),
        offlineStorage(false, "OFFLINE"), javascript(true), blockMalware(true, "MAL"), COMPACT(true);

        public boolean b, def;
        public String n;

        private Options(boolean d) { this(d, null); }
        private Options(boolean d, String z) { this.b = d; this.def = d; this.n = Lang.from((z == null ? name() : z).toUpperCase()); }
    }
    
    @SuppressWarnings("unchecked")
    public Settings() {
        c = this.getChildren();

        for (Options o : Options.values()) addCheckBox(o); // CHECKBOXES

        // THEME
        comboBox("Theme", a -> changeStyle(((ComboBox<String>) a.getSource()).getValue()),
                styleName, b.keySet().toArray(new String[0]));

        // LANG
        comboBox(Lang.LANG.tl, a -> {
            try {
                LangManager.setLang(Settings.l.get(((ComboBox<String>) a.getSource()).getValue()));
            } catch (IOException e1) { e1.printStackTrace(); }
            save();
        }, LangManager.full, l.keySet().toArray(new String[0]));

        // ENGINE
        comboBox("Web Engine", a -> {
            ZunoAPI.en = ((ComboBox<Engine>) a.getSource()).getValue();
            save();
        }, ZunoAPI.getInstance().getInfo().engine().name(), Engine.values());

        // BAR POS
        comboBox("Tab Postioning", a -> {
            Object value = ((ComboBox<String>) a.getSource()).getValue();
            ZunoAPI.tb.setSide((Side) value);
            save();
        }, ZunoAPI.tb.getSide(), Side.values());

        textField("Home Page", a -> tabPage = a.f.getText(), tabPage);
        textField("Search Engine", a -> searchEn = a.f.getText(), searchEn);
    }

    private final TextField textField(String n, EventHandler<TextFieldEvent> ae, String cur) {
        Label l3 = new Label(n);
        l3.setPadding(new Insets(8,6,2,6));

        TextField f = new TextField(cur);
        f.setPrefSize(265, 15);
        Button b = new Button("Save");
        TextFieldEvent e = new TextFieldEvent(f);
        b.setOnAction(new EventHandler<ActionEvent>() { public void handle(ActionEvent ev) { ae.handle(e); save(); }});
        f.setOnAction(b.getOnAction());
        c.add(l3);
        c.add(new HBox(f, b));
        return f;
    }

    private class TextFieldEvent extends ActionEvent {
        private static final long serialVersionUID = 1L;
        public final TextField f;
        public TextFieldEvent(TextField f) {this.f = f;}
    }

    private final <T> ComboBox<T> comboBox(String n, EventHandler<ActionEvent> ae, T v, T[] l) {
        Label l3 = new Label(n);
        Lang.b(() -> l3.setText(n));
        l3.setPadding(new Insets(8,6,2,6));

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T e : l) list.add(e);
        ComboBox<T> cb = new ComboBox<>(list);
        cb.setValue(v);
        cb.setOnAction(ae);
        c.add(l3);
        c.add(cb);
        return cb;
    }

    private final void addCheckBox(Options o) {
        CheckBox box = new CheckBox(o.name()); // TODO
        Lang.a(() -> box.setText(o.n));
        box.setSelected(o.b);
        box.setPadding(new Insets(4,4,4,4));
        box.setOnAction(a -> {
            o.b = box.isSelected();
            save();
        });
        c.add(box);
    }

    public static void changeStyle(String str) {
        ZunoAPI.stylesheet = b.get(str);
        styleName = str;
        try {
            s.getStylesheets().setAll(b.get(str).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) { e.printStackTrace(); }
        save();
    }

    public static void save() {
        map.put("styleName", styleName);
        map.put("newtab", tabPage);
        map.put("search", searchEn);
        map.put("engine", ZunoAPI.en);
        map.put("lang", LangManager.lang + ".lang");
        map.put("stylefile", ZunoAPI.stylesheet.getAbsolutePath());
        for (Options o : Options.values())
            map.put(o.name(), o.b);
        saveMap();
    }

    public static void setLang(String s) {
        map.put("lang", s);
        Platform.runLater(() -> { for (ChangeLis c : Lang.l2) c.a(); });
    }

    public static void saveMap() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static void load() {
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap<String, Object>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        styleName = (String) map.getOrDefault("styleName", "ZunoZap dsefault");
        tabPage = (String) map.getOrDefault("newtab", "http://start.duckduckgo.com/");
        searchEn = (String) map.getOrDefault("search", "http://duckduckgo.com/?q=%s");
        try {
            LangManager.setLang(new File(ZunoAPI.lang, (String) map.getOrDefault("lang", "en.lang")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ZunoAPI.stylesheet = new File((String) map.getOrDefault("stylefile", new ZFile("style.css", false).getAbsolutePath()));
        String sid = (String) map.get("side");

        ZunoAPI.tb.setSide(null != sid ? Side.valueOf(sid) : Side.TOP);

        try {
            ZunoAPI.en = (Engine) map.get("engine");
        } catch (Exception e) {
            e.printStackTrace();
            ZunoAPI.en = Engine.CHROME;
        }

        for (String s : map.keySet()) {
            Object v = map.get(s);
            if (v instanceof Boolean)
                Options.valueOf(s).b = (boolean) v;
        }
    }

    public static boolean init(File dir) {
        try {
            settings.createNewFile();
            load();
        } catch (IOException e) { return false; }
        
        ZunoAPI.exportResource("style.css", ZunoAPI.home);
        ZFile f = new ZFile("style.css", false);
        b.put("ZunoZap default", f);
        if (styleName == null || styleName.equalsIgnoreCase("none") || ZunoAPI.firstRun) {
            ZunoAPI.stylesheet = f;
            styleName = "ZunoZap default";
        }

        for (File fi : dir.listFiles()) b.put(fi.getName(), fi);

        ZFile temp = new ZFile("blank.css", false);
        temp.deleteOnExit();
        b.put("Java", temp);

        File z = ZunoAPI.exportResource("en.lang", ZunoAPI.lang).toFile();

        try {
            if (ZunoAPI.firstRun || ZunoAPI.lang.listFiles().length == 1) LangManager.setLang(z);
        } catch (IOException e1) { e1.printStackTrace(); }

        for (File fi : ZunoAPI.lang.listFiles()) {
            try {
                l.put(Files.readAllLines(fi.toPath()).get(0).replace("#lang=", ""), fi);
            } catch (IOException e) { e.printStackTrace(); }
        }
        save();
        return true;
    }

}