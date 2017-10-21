package me.isaiah.zunozap;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class OptionMenu implements ActionListener {
    private static ZFile settings = new ZFile("settings.txt", false);
    public ArrayList<Integer> CBlist = new ArrayList<>();
    private static ZunoProperties p = new ZunoProperties();
    private int i = 1;
    private JButton odf = new JButton("Open data folder");
    private JButton aps = new JButton("Apply settings");
    public static JFrame f;
    public static JPanel panel;

    public OptionMenu() {
        try { createMenu(); } catch (IOException e) { e.printStackTrace(); }
    }

    public static final void init() throws IOException {
        settings.createNewFile();
        ZunoProperties p = new ZunoProperties();
        FileInputStream s = new FileInputStream(settings);
        p.load(s);
        addDefaults();

        EOption.forceHTTPS.b = p.get("forceHTTPS");
        EOption.blockEventCalls.b = p.get("blockEventCalls");
        EOption.createPluginDataFolders.b = p.get("createPluginDataFolders");
        EOption.useDuck.b = p.get("onTheDuckSide");
        EOption.offlineStorage.b = p.get("offlineStorage");
        EOption.JS.b = p.get("javascript");

        ZunoAPI.styleName = String.valueOf(p.getStr("style"));
        ZunoAPI.stylesheet = new File(String.valueOf(p.getStr("stylefile")));

        s.close();
    }

    public final void createMenu() throws IOException {
        f = new JFrame();
        panel = new JPanel();

        settings.createNewFile();

        FileInputStream s = new FileInputStream(settings);
        p.load(s);

        addDefaults();

        EOption.forceHTTPS.b = p.get("forceHTTPS");
        EOption.blockEventCalls.b = p.get("blockEventCalls");
        EOption.createPluginDataFolders.b = p.get("createPluginDataFolders");
        EOption.useDuck.b = p.get("onTheDuckSide");
        EOption.offlineStorage.b = p.get("offlineStorage");
        EOption.JS.b = p.get("javascript");

        p.store(new FileOutputStream(settings), "settings");

        i = 1; // Reset.
        addCheckBox("Force HTTPS", EOption.forceHTTPS.b);
        addCheckBox("Block event calls", EOption.blockEventCalls.b);
        addCheckBox("Create plugin folders", EOption.createPluginDataFolders.b);
        addCheckBox("Use DuckDuckGo", EOption.useDuck.b);
        addCheckBox("Download websites for offline browsing", EOption.offlineStorage.b);
        addCheckBox("Javascript", EOption.JS.b);

        aps.setEnabled(true);
        aps.addActionListener((a) -> { try { save(); } catch (IOException e) { e.printStackTrace(); }});

        odf.setEnabled(true);
        odf.addActionListener((a) -> {
            try { Desktop.getDesktop().open(ZunoAPI.home); } catch (IOException e) { e.printStackTrace(); }
        });
        JTextField t = new JTextField("Style:");
        t.setEditable(false);
        panel.setBorder(new EmptyBorder(2, 10, 2, 2));
        t.setBorder(new EmptyBorder(0, 0, 0, 0));
        t.setMargin(new Insets(20, 0, 0, 0));
        t.setMaximumSize(new Dimension(50, 25));
        JComboBox<Object> style = new JComboBox<>(StyleManager.b.keySet().toArray());
        style.setSelectedItem(ZunoAPI.styleName);
        style.setMaximumSize(new Dimension(150, 20));
        style.addActionListener(this);

        JTextField t2 = new JTextField();
        t2.setEditable(false);
        t2.setBorder(new EmptyBorder(0, 0, 0, 0));
        t2.setMaximumSize(new Dimension(10, 20));
        panel.add(t);
        panel.add(style);
        panel.add(t2);
        panel.add(odf);
        panel.add(aps);

        s.close();
        f.setDefaultCloseOperation(2);
        panel.setSize(5000, 2000);

        f.setTitle("ZunoZap Settings");
        f.setPreferredSize(new Dimension(400, 300));
        f.setContentPane(panel);
        f.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent w) {
                try { save(); } catch (IOException e) { e.printStackTrace(); }
            }
        });
        f.pack();
        f.setVisible(true);
    }

    private void addCheckBox(String text, boolean b) {
        final int it = i;
        JCheckBox box = new JCheckBox(text);
        box.setSelected(b);
        box.setName(String.valueOf(i));
        box.addActionListener((a) -> {
            EOption.getById(it).b = box.isSelected();
            try { save(); } catch (IOException e) { e.printStackTrace(); }
        });
        CBlist.add(i);
        panel.add(box);
        i++;
    }

    @Deprecated public static void save() throws IOException { save(true); }

    public static void save(boolean all) throws IOException {
        settings.createNewFile();
        ZunoProperties p = new ZunoProperties();
        FileInputStream s = new FileInputStream(settings);
        p.load(s);
        p.set("forceHTTPS", EOption.forceHTTPS.b);
        p.set("blockEventCalls", EOption.blockEventCalls.b);
        p.set("createPluginDataFolders", EOption.createPluginDataFolders.b);
        p.set("onTheDuckSide", EOption.useDuck.b);
        p.set("offlineStorage", EOption.offlineStorage.b);
        p.set("javascript", EOption.JS.b);
        if (all) {
            p.setProperty("style", ZunoAPI.styleName);
            p.setProperty("stylefile", ZunoAPI.stylesheet.getAbsolutePath());
        }
        p.store(new FileOutputStream(settings), null);
        s.close();
    }

    protected static void addDefault(String key, boolean b) {
        if (!p.containsKey(key)) p.set(key, b); 
    }

    private static void addDefaults() {
        addDefault("forceHTTPS", false);
        addDefault("blockEventCalls", false);
        addDefault("createPluginDataFolders", true);
        addDefault("onTheDuckSide", true);
        addDefault("offlineStorage", false);
        addDefault("javascript", true);
    }

    @SuppressWarnings("unchecked")
    @Override public void actionPerformed(ActionEvent a) {
        String name = (String) ((JComboBox<String>) a.getSource()).getSelectedItem();
        ZunoAPI.stylesheet = StyleManager.b.get(name);
        ZunoAPI.styleName = name;
        StyleManager.staticGetScene().getStylesheets().clear();
        try {
            StyleManager.staticGetScene().getStylesheets().add(StyleManager.b.get(name).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) { e.printStackTrace(); }

        System.out.println("[StyleManager]: Style changed to " + name);
        try { save(); } catch (IOException e) { e.printStackTrace(); }
    }
}