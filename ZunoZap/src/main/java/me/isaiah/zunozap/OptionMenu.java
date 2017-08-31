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

/***
 * The setting manager for ZunoZap.
 * 
 * @Deprecated Planed to replace with a more modern Setting page.
 */
public class OptionMenu implements ActionListener {
    private static File settings = new File(ZunoZap.homeDir, "settings.txt");
    public ArrayList<Integer> CBlist = new ArrayList<>();
    private static ZunoProperties p = new ZunoProperties();
    private int i = 1;
    private JButton odf = new JButton("Open data folder");
    private JButton jbtn = new JButton("Apply settings");
    public static JFrame f;
    public static JPanel panel;
    public OptionMenu() {
        try {
            createMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final void init() throws IOException {
        if (!settings.exists()) settings.createNewFile();
        ZunoProperties p = new ZunoProperties();
        FileInputStream s = new FileInputStream(settings);
        p.load(s);
        addDefault("forceHTTPS", "false");
        addDefault("blockEventCalls", "false");
        addDefault("createPluginDataFolders", "true");
        addDefault("onTheDuckSide", "true");
        addDefault("offlineStorage", "false");
        addDefault("javascript", "true");

        ZunoAPI.forceHTTPS = p.getBoolean("forceHTTPS");
        ZunoAPI.blockPluginEvents = p.getBoolean("blockEventCalls");
        ZunoAPI.createPluginDataFolders = p.getBoolean("createPluginDataFolders");
        ZunoAPI.useDuck = String.valueOf(p.get("onTheDuckSide")).toLowerCase().contains("true");
        ZunoAPI.offlineStorage = String.valueOf(p.get("offlineStorage")).toLowerCase().contains("true");
        ZunoAPI.JS = String.valueOf(p.get("javascript")).toLowerCase().contains("true");

        ZunoAPI.styleName = String.valueOf(p.get("style"));
        ZunoAPI.stylesheet = new File(String.valueOf(p.get("stylefile")));

        s.close();
    }

    public final void createMenu() throws IOException {
        f = new JFrame();
        panel = new JPanel();

        if (!settings.exists()) settings.createNewFile();

        FileInputStream s = new FileInputStream(settings);
        p.load(s);

        addDefault("forceHTTPS", "false");
        addDefault("blockEventCalls", "false");
        addDefault("createPluginDataFolders", "true");
        addDefault("onTheDuckSide", "true");
        addDefault("offlineStorage", "false");
        addDefault("javascript", "true");

        ZunoAPI.forceHTTPS = String.valueOf(p.get("forceHTTPS")).toLowerCase().contains("true");
        ZunoAPI.blockPluginEvents = String.valueOf(p.get("blockEventCalls")).toLowerCase().contains("true");
        ZunoAPI.createPluginDataFolders = String.valueOf(p.get("createPluginDataFolders")).toLowerCase().contains("true");
        ZunoAPI.useDuck = String.valueOf(p.get("onTheDuckSide")).toLowerCase().contains("true");
        ZunoAPI.offlineStorage = String.valueOf(p.get("offlineStorage")).toLowerCase().contains("true");
        ZunoAPI.JS = String.valueOf(p.get("javascript")).toLowerCase().contains("true");

        p.store(new FileOutputStream(settings), "ZunoZap Settings");

        i = 1; // Reset.
        addCheckBox("Force HTTPS", ZunoAPI.forceHTTPS);
        addCheckBox("Block event calls", ZunoAPI.blockPluginEvents);
        addCheckBox("Create plugin folders", ZunoAPI.createPluginDataFolders);
        addCheckBox("Use DuckDuckGo", ZunoAPI.useDuck);
        addCheckBox("Download websites for offline browsing", ZunoAPI.offlineStorage);
        addCheckBox("Javascript", ZunoAPI.JS);

        jbtn.setEnabled(true);

        jbtn.addActionListener((a) -> {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        odf.setEnabled(true);
        odf.addActionListener((a) -> {
            try {
                Desktop.getDesktop().open(ZunoZap.homeDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        JTextField text = new JTextField();
        text.setEditable(false);
        text.setText("Style:");
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
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        f.setLayout(layout);
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        f.pack();
        f.setVisible(true);
    }

    @SuppressWarnings("deprecation")
    private void addCheckBox(String text, boolean b) {
        final int it = i;
        final JCheckBox box = new JCheckBox(text);
        box.setSelected(b);
        box.setName(String.valueOf(i).toString());
        box.addActionListener((a) -> {
            ZunoAPI.getOptionMenuAction(EOption.getById(it), box.isSelected());
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
       CBlist.add(i);
       panel.add(box);
       i++;
    }

    @Deprecated
    public static void save() throws IOException {
        save(true);
    }

    public static void save(boolean all) throws IOException {
        if (!settings.exists()) settings.createNewFile();
        ZunoProperties p = new ZunoProperties();
        FileInputStream s = new FileInputStream(settings);
        p.load(s);
        p.setProperty("forceHTTPS", ZunoAPI.forceHTTPS);
        p.setProperty("blockEventCalls", ZunoAPI.blockPluginEvents);
        p.setProperty("createPluginDataFolders", ZunoAPI.createPluginDataFolders);
        p.setProperty("onTheDuckSide", ZunoAPI.useDuck);
        p.setProperty("offlineStorage", ZunoAPI.offlineStorage);
        p.setProperty("javascript", ZunoAPI.JS);
        if (all) {
            p.setProperty("style", ZunoAPI.styleName);
            p.setProperty("stylefile", ZunoAPI.stylesheet.getAbsolutePath());
        }
        p.store(new FileOutputStream(settings), null);
        s.close();
    }
    
    protected static void addDefault(String key, String value) {
       if (!p.containsKey(key)) p.setProperty(key, value); 
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> cb = (JComboBox<String>) e.getSource();
        String name = (String) cb.getSelectedItem();
        ZunoAPI.stylesheet = StyleManager.staticGetStyles().get(name);
        ZunoAPI.styleName = name;
        StyleManager.staticGetScene().getStylesheets().clear();
        try {
            StyleManager.staticGetScene().getStylesheets().add(StyleManager.b.get(name).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        System.out.println("[StyleManager]: style changed to " + name);
        try {
            save();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}