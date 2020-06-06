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
import java.util.List;

import com.zunozap.Engine.Type;
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
    public static Type en;

    private List<Node> c;
    public static Scene s;
    public static HashMap<String, File> b = new HashMap<>(), l = new HashMap<>();
    public ArrayList<Runnable> l2 = new ArrayList<>();

    public static void set(File folder, Scene sc) { s = sc; }

    public enum Options {
        forceHTTPS(false, "HTTPS"), DIS_PL(false), offlineStorage(false, "OFFLINE"), javascript(true), COMPACT(true);

        public boolean b, def;
        public Lang n;

        private Options(boolean d) { this(d, null); }
        private Options(boolean d, String z) {
            this.b = d; 
            this.def = d; 
            this.n = Lang.get((z == null ? name() : z).toUpperCase()); 
        }
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
            en = ((ComboBox<Engine.Type>) a.getSource()).getValue();
            save();
        }, EngineHelper.type.name(), Engine.Type.values());

        // BAR POS
        comboBox("Tab Postioning", a -> {
            Object value = ((ComboBox<String>) a.getSource()).getValue();
            ZunoZap.tb.setSide((Side) value);
            save();
        }, ZunoZap.tb.getSide(), Side.values());

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
        CheckBox box = new CheckBox(o.n == null ? o.name() : o.n.tl);
        Lang.a(() -> box.setText(o.n == null ? o.name() : o.n.tl));
        box.setSelected(o.b);
        box.setPadding(new Insets(4,4,4,4));
        box.setOnAction(a -> {
            o.b = box.isSelected();
            save();
        });
        c.add(box);
    }

    public static void changeStyle(String str) {
        ZunoZap.stylesheet = b.get(str);
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
        map.put("engine", en);
        map.put("lang", LangManager.lang + ".lang");
        map.put("stylefile", ZunoZap.stylesheet.getAbsolutePath());
        for (Options o : Options.values())
            map.put(o.name(), o.b);
        saveMap();
    }

    public static void setLang(String s) {
        map.put("lang", s);
        Platform.runLater(() -> { for (Runnable c : Lang.l2) c.run(); });
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
            LangManager.setLang(new File(ZunoZap.lang, (String) map.getOrDefault("lang", "en.lang")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ZunoZap.stylesheet = new File((String) map.getOrDefault("stylefile", new ZFile("style.css", false).getAbsolutePath()));
        String sid = (String) map.get("side");

        ZunoZap.tb.setSide(null != sid ? Side.valueOf(sid) : Side.TOP);

        try {
            en = (Type) map.get("engine");
        } catch (Exception e) {
            e.printStackTrace();
            en = Type.CHROME;
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

    public static boolean init(File dir) {
        try {
            f.createNewFile();
            load();
        } catch (IOException e) { return false; }

        exportResource("style.css", ZunoZap.home);
        ZFile f = new ZFile("style.css", false);
        b.put("ZunoZap default", f);
        if (styleName == null || styleName.equalsIgnoreCase("none") || ZunoZap.firstRun) {
            ZunoZap.stylesheet = f;
            styleName = "ZunoZap default";
        }

        for (File fi : dir.listFiles()) b.put(fi.getName(), fi);

        ZFile temp = new ZFile("blank.css", false);
        temp.deleteOnExit();
        b.put("Java", temp);

        File z = exportResource("en.lang", ZunoZap.lang).toFile();

        try {
            if (ZunoZap.firstRun || ZunoZap.lang.listFiles().length == 1) LangManager.setLang(z);
        } catch (IOException e1) { e1.printStackTrace(); }

        for (File fi : ZunoZap.lang.listFiles()) {
            try {
                l.put(Files.readAllLines(fi.toPath()).get(0).replace("#lang=", ""), fi);
            } catch (IOException e) { e.printStackTrace(); }
        }
        save();
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