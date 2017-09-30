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
    private JButton jbtn = new JButton("Apply settings");
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

        EOption.forceHTTPS.set(p.get("forceHTTPS"));
        EOption.blockEventCalls.set(p.get("blockEventCalls"));
        EOption.createPluginDataFolders.set(p.get("createPluginDataFolders"));
        EOption.useDuck.set(p.get("onTheDuckSide"));
        EOption.offlineStorage.set(p.get("offlineStorage"));
        EOption.JS.set(p.get("javascript"));

        ZunoAPI.styleName = String.valueOf(p.getString("style"));
        ZunoAPI.stylesheet = new File(String.valueOf(p.getString("stylefile")));

        s.close();
    }

    public final void createMenu() throws IOException {
        f = new JFrame();
        panel = new JPanel();

        settings.createNewFile();

        FileInputStream s = new FileInputStream(settings);
        p.load(s);

        addDefaults();

        EOption.forceHTTPS.set(p.get("forceHTTPS"));
        EOption.blockEventCalls.set(p.get("blockEventCalls"));
        EOption.createPluginDataFolders.set(p.get("createPluginDataFolders"));
        EOption.useDuck.set(p.get("onTheDuckSide"));
        EOption.offlineStorage.set(p.get("offlineStorage"));
        EOption.JS.set(p.get("javascript"));

        p.store(new FileOutputStream(settings), "ZunoZap Settings");

        i = 1; // Reset.
        addCheckBox("Force HTTPS", EOption.forceHTTPS.get());
        addCheckBox("Block event calls", EOption.blockEventCalls.get());
        addCheckBox("Create plugin folders", EOption.createPluginDataFolders.get());
        addCheckBox("Use DuckDuckGo", EOption.useDuck.get());
        addCheckBox("Download websites for offline browsing", EOption.offlineStorage.get());
        addCheckBox("Javascript", EOption.JS.get());

        jbtn.setEnabled(true);
        jbtn.addActionListener((a) -> { try { save(); } catch (IOException e) { e.printStackTrace(); }});

        odf.setEnabled(true);
        odf.addActionListener((a) -> {
            try {
                Desktop.getDesktop().open(ZunoAPI.home);
            } catch (IOException e) { e.printStackTrace(); }
        });
        JTextField text = new JTextField("Style:");
        text.setEditable(false);
        panel.setBorder(new EmptyBorder(2, 10, 2, 2));
        text.setBorder(new EmptyBorder(0, 0, 0, 0));
        text.setMargin(new Insets(20, 0, 0, 0));
        text.setMaximumSize(new Dimension(50, 25));
        JComboBox<Object> style = new JComboBox<>(StyleManager.staticGetStyles().keySet().toArray());
        style.setSelectedItem(ZunoAPI.styleName);
        style.setMaximumSize(new Dimension(150, 20));
        style.addActionListener(this);

        JTextField text2 = new JTextField();
        text2.setEditable(false);
        text2.setBorder(new EmptyBorder(0, 0, 0, 0));
        text2.setMaximumSize(new Dimension(10, 20));
        panel.add(text);
        panel.add(style);
        panel.add(text2);
        panel.add(odf);
        panel.add(jbtn);

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
        final JCheckBox box = new JCheckBox(text);
        box.setSelected(b);
        box.setName(String.valueOf(i));
        box.addActionListener((a) -> {
            EOption.getById(it).set(box.isSelected());
            try { save(); } catch (IOException e) { e.printStackTrace(); }
        });
       CBlist.add(i);
       panel.add(box);
       i++;
    }

    @Deprecated
    public static void save() throws IOException { save(true); }

    public static void save(boolean all) throws IOException {
        settings.createNewFile();
        ZunoProperties p = new ZunoProperties();
        FileInputStream s = new FileInputStream(settings);
        p.load(s);
        p.set("forceHTTPS", EOption.forceHTTPS.get());
        p.set("blockEventCalls", EOption.blockEventCalls.get());
        p.set("createPluginDataFolders", EOption.createPluginDataFolders.get());
        p.set("onTheDuckSide", EOption.useDuck.get());
        p.set("offlineStorage", EOption.offlineStorage.get());
        p.set("javascript", EOption.JS.get());
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
    @Override public void actionPerformed(ActionEvent e) {
        String name = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
        ZunoAPI.stylesheet = StyleManager.staticGetStyles().get(name);
        ZunoAPI.styleName = name;
        StyleManager.staticGetScene().getStylesheets().clear();
        try {
            StyleManager.staticGetScene().getStylesheets().add(StyleManager.b.get(name).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e1) { e1.printStackTrace(); }
        System.out.println("[StyleManager]: style changed to " + name);
        try { save(); } catch (IOException e1) { e1.printStackTrace(); }
    }
}