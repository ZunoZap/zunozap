package me.isaiah.zunozap;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.isaiah.zunozap.UniversalEngine.Engine;

public class OptionMenu {
    private static ZFile settings = new ZFile("settings.txt", false);
    private static ZunoProperties p = new ZunoProperties();
    private int i = 1;
    private JButton odf = new JButton("Open data folder");
    private JButton aps = new JButton("Apply settings");
    public static JFrame f;
    public static JPanel panel;

    public OptionMenu() {
        try { createMenu(); } catch (IOException e) { e.printStackTrace(); }
    }

    public static final boolean init() {
        try {
            settings.createNewFile();
            ZunoProperties p = new ZunoProperties();
            FileInputStream s = new FileInputStream(settings);
            p.load(s);
            addDefaults();

            for (EOption e : EOption.values()) e.b = p.get(e.toString());

            ZunoAPI.styleName = String.valueOf(p.getStr("style"));
            ZunoAPI.stylesheet = new File(String.valueOf(p.getStr("stylefile")));
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

    @SuppressWarnings("unchecked")
    public final void createMenu() throws IOException {
        f = new JFrame();
        panel = new JPanel();

        settings.createNewFile();

        FileInputStream s = new FileInputStream(settings);
        p.load(s);

        addDefaults();

        for (EOption e : EOption.values()) e.b = p.get(e.toString());

        p.store(new FileOutputStream(settings), "config");

        i = 1; // Reset
        for (EOption e : EOption.values()) addCheckBox(e.n, e.b);

        aps.setEnabled(true);
        aps.addActionListener((a) -> save());

        odf.setEnabled(true);
        odf.addActionListener((a) -> { try { Desktop.getDesktop().open(ZunoAPI.home); } catch (IOException e) {}});

        JTextField t = new JTextField("Style:");
        t.setEditable(false);
        panel.setBorder(new EmptyBorder(2, 10, 2, 2));
        t.setBorder(new EmptyBorder(0, 0, 0, 0));
        t.setMargin(new Insets(20, 0, 0, 0));
        t.setMaximumSize(new Dimension(50, 25));

        JTextField e = new JTextField("Engine:");
        e.setEditable(false);
        panel.setBorder(new EmptyBorder(2, 10, 2, 2));
        e.setBorder(new EmptyBorder(0, 0, 0, 0));
        e.setMargin(new Insets(20, 0, 0, 0));
        e.setMaximumSize(new Dimension(50, 25));

        JComboBox<Object> style = new JComboBox<>(StyleManager.b.keySet().toArray());
        style.setSelectedItem(ZunoAPI.styleName);
        style.setMaximumSize(new Dimension(150, 20));
        style.addActionListener((a) -> {
            String name = (String) ((JComboBox<String>) a.getSource()).getSelectedItem();
            ZunoAPI.stylesheet = StyleManager.b.get(name);
            ZunoAPI.styleName = name;
            StyleManager.setStyle(name);

            System.out.println("New Style: " + name);
            save();
        });

        JComboBox<Object> en = new JComboBox<>(UniversalEngine.Engine.values());
        en.setSelectedItem(ZunoAPI.en);
        en.setMaximumSize(new Dimension(150, 20));
        en.addActionListener((a) -> {
            ZunoAPI.en = (Engine) ((JComboBox<String>) a.getSource()).getSelectedItem(); 
            save(true);
        });

        JTextField p = new JTextField();
        p.setEditable(false);
        p.setBorder(new EmptyBorder(0, 0, 0, 0));
        p.setMaximumSize(new Dimension(10, 20));

        Component[] cs = {t, style, e, en, p, odf, aps};
        for (Component c : cs) panel.add(c);
        
        s.close();
        f.setDefaultCloseOperation(2);
        panel.setSize(5500, 2500);

        f.setTitle("ZunoZap Settings");
        f.setPreferredSize(new Dimension(400, 300));
        f.setContentPane(panel);
        f.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent w) { save(); }
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
            save();
        });
        panel.add(box);
        i++;
    }

    @Deprecated public static void save() { save(true); }

    public static boolean save(boolean all) {
        try {
            settings.createNewFile();
            ZunoProperties p = new ZunoProperties();
            FileInputStream s = new FileInputStream(settings);
            p.load(s);

            for (EOption e : EOption.values()) p.set(e.toString(), e.b);

            if (all) {
                p.setProperty("style", ZunoAPI.styleName);
                p.setProperty("stylefile", ZunoAPI.stylesheet.getAbsolutePath());
                p.setProperty("engine", ZunoAPI.en.name());
            }
            p.store(new FileOutputStream(settings), null);
            s.close();
            return true;
        } catch (IOException e) { return false; }
    }

    protected static void addDefault(String key, boolean b) {
        if (!p.containsKey(key)) p.set(key, b); 
    }

    private static void addDefaults() {
        for (EOption e : EOption.values()) addDefault(e.name(), e.def);
    }
}