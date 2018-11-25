package me.isaiah.zunozap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.isaiah.zunozap.UniversalEngine.Engine;
import me.isaiah.zunozap.lang.ChangeLis;
import me.isaiah.zunozap.lang.Lang;
import me.isaiah.zunozap.lang.LangManager;

public class Settings extends VBox {

    private List<Node> c;
    private static ZFile settings = new ZFile("settings.txt", false);
    private static ZunoProperties p = new ZunoProperties();
    public static Scene s;
    public static HashMap<String, File> b = new HashMap<>(), l = new HashMap<>();
    public ArrayList<ChangeLis> l2 = new ArrayList<>();

    public static void set(File folder, Scene sc) { s = sc; }

    public enum Options {
        forceHTTPS("HTTPS", false), blockEventCalls("DIS_PL", false),
        offlineStorage("OFFLINE", false), javascript(true), blockMalware("MAL", true);

        private final static HashMap<Integer, Options> map = new HashMap<>();
        public boolean b, def;
        public String n, z;

        private Options(boolean d) { this.b = d; this.def = d; this.n = toString(); }
        private Options(String n, boolean d) { this.b = d; this.def = d; this.n = n; this.z = n;}

        public static Options getById(int id){ return map.get(id - 1); }
        public static void z() {
            for (Options o : Options.values()) try { o.n = Lang.valueOf(o.z).tl; } catch (Exception e) {}
        }

        static { for (Options m : values()) map.put(m.ordinal(), m); Lang.a(() -> z()); }
    }

    @SuppressWarnings("unchecked")
    public Settings() {
        c = this.getChildren();

        for (Options o : Options.values()) addCheckBox(o, o.def); // CHECKBOXES

        // THEME
        comboBox("Theme", a -> changeStyle(((ComboBox<String>) a.getSource()).getValue()),
                ZunoAPI.styleName, b.keySet().toArray(new String[0]));

        // LANG
        comboBox(Lang.LANG.tl, a -> {
            try {
                LangManager.setLang(Settings.l.get(((ComboBox<String>) a.getSource()).getValue()));
            } catch (IOException e1) { e1.printStackTrace(); }
            save(true);
        }, LangManager.full, l.keySet().toArray(new String[0]));

        // ENGINE
        comboBox("Web Engine", a -> {
            ZunoAPI.en = Engine.valueOf(((ComboBox<String>) a.getSource()).getValue());
            save(true);
        }, ZunoAPI.getInstance().getInfo().engine().name(), Engine.values());

        textField("Home Page", a -> ZunoAPI.tabPage = a.f.getText(), ZunoAPI.tabPage);
        textField("Search Engine", a -> ZunoAPI.searchEn = a.f.getText(), ZunoAPI.searchEn);
    }

    public static final boolean initMenu() {
        try {
            settings.createNewFile();
            ZunoProperties p = new ZunoProperties();
            FileInputStream s = new FileInputStream(settings);
            p.load(s);
            addDefaults();

            for (Options e : Options.values()) e.b = p.get(e.toString());

            ZunoAPI.styleName = String.valueOf(p.getStr("style"));
            ZunoAPI.stylesheet = new File(String.valueOf(p.getStr("stylefile")));
            if (p.containsKey("lang")) LangManager.setLang(new File(ZunoAPI.lang, p.getStr("lang")));
            if (p.containsKey("tabpage")) ZunoAPI.tabPage = p.getStr("tabpage");
            if (p.containsKey("search")) ZunoAPI.searchEn = p.getStr("search");
            try {
                ZunoAPI.en = Engine.valueOf(p.getStr("engine"));
            } catch (Exception e) {
                e.printStackTrace();
                ZunoAPI.en = Engine.CHROME;
            }

            s.close();
            return true;
        } catch (IOException e) { return false; }
    }

    public static void initCss(File fold) {
        ZunoAPI.exportResource("style.css", ZunoAPI.home);
        ZFile f = new ZFile("style.css", false);
        b.put("ZunoZap default", f);
        if (ZunoAPI.styleName.equalsIgnoreCase("none") || ZunoZapWebView.firstRun || ZunoZap.firstRun) {
            ZunoAPI.stylesheet = f;
            ZunoAPI.styleName = "ZunoZap default";
        } else initMenu();

        for (File fi : fold.listFiles()) b.put(fi.getName(), fi);

        ZFile temp = new ZFile("blank.css", false);
        temp.deleteOnExit();
        b.put("Java", temp);
    }

    public static void initLang() throws IOException {
        File f = ZunoAPI.exportResource("en.lang", ZunoAPI.lang).toFile();

        if (ZunoAPI.firstRun || ZunoAPI.lang.listFiles().length == 1) LangManager.setLang(f);
        else initMenu();

        Options.z();

        for (File fi : ZunoAPI.lang.listFiles()) l.put(Files.readAllLines(fi.toPath()).get(0).replace("#lang=", ""), fi);
    }

    public static void setLang(String s) {
        p.setProperty("lang", s);
        Platform.runLater(new Runnable() { @Override public void run() {
            for (ChangeLis c : Lang.l2) c.a();
        }});
    }

    private final TextField textField(String n, EventHandler<TextFieldEvent> ae, String cur) {
        Label l3 = new Label(n);
        l3.setPadding(new Insets(8,6,2,6));

        TextField f = new TextField(cur);
        f.setPrefSize(265, 15);
        Button b = new Button("Save");
        TextFieldEvent e = new TextFieldEvent(f);
        b.setOnAction(new EventHandler<ActionEvent>() { public void handle(ActionEvent ev) { ae.handle(e); save(true); }});
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

    private final void addCheckBox(Options o, boolean b) {
        CheckBox box = new CheckBox(o.n);
        Lang.a(() -> box.setText(o.n));
        box.setSelected(b);
        box.setPadding(new Insets(4,4,4,4));
        box.setOnAction(a -> {
            o.b = box.isSelected();
            save(true);
        });
        c.add(box);
    }

    public static void changeStyle(String str) {
        ZunoAPI.stylesheet = Settings.b.get(str);
        ZunoAPI.styleName = str;
        try {
            s.getStylesheets().setAll(b.get(str).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) { e.printStackTrace(); }
        save(true);
    }

    public static boolean save(boolean all) {
        try {
            settings.createNewFile();
            FileInputStream s = new FileInputStream(settings);
            p.load(s);

            for (Options e : Options.values()) p.set(e.toString(), e.b);

            if (all) {
                p.setProperty("style", ZunoAPI.styleName);
                p.setProperty("stylefile", ZunoAPI.stylesheet.getAbsolutePath());
                p.setProperty("engine", ZunoAPI.en.name());
                p.setProperty("search", ZunoAPI.searchEn);
                p.setProperty("tabpage", ZunoAPI.tabPage);
                p.setProperty("lang", LangManager.lang + ".lang");
            }
            p.store(new FileOutputStream(settings), null);
            s.close();
            return true;
        } catch (IOException e) { return false; }
    }

    private static void addDefaults() {
        for (Options e : Options.values()) if (!p.containsKey(e.name())) p.set(e.name(), e.def);
    }

}