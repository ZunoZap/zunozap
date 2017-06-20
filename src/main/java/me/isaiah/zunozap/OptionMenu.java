package me.isaiah.zunozap;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * @author Isaiah Patton
 */
public class OptionMenu {
    private static File settings = new File(ZunoZap.homeDir, "settings.txt");
    public ArrayList<Integer> CBlist = new ArrayList<Integer>();
    private static Properties p = new Properties();
    private int i = 1;
    private JButton jbtn = new JButton("Apply");

    public static JFrame f;
    public static JPanel panel;
    public OptionMenu() {
        try {
            createMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void createMenu() throws IOException {
        f = new JFrame();
        panel = new JPanel();

        if (!settings.exists()){
            settings.createNewFile();
        }

        FileInputStream s = new FileInputStream(settings);
        p.load(s);
        
        addDefault("forceHTTPS", "false");
        addDefault("blockEventCalls", "false");
        addDefault("createPluginDataFolders", "true");
        addDefault("onTheDuckSide", "true");
        
        ZunoAPI.forceHTTPS = String.valueOf(p.get("forceHTTPS")).toLowerCase().contains("true");
        ZunoAPI.blockPluginEvents = String.valueOf(p.get("blockEventCalls")).toLowerCase().contains("true");
        ZunoAPI.createPluginDataFolders = String.valueOf(p.get("createPluginDataFolders")).toLowerCase().contains("true");
        ZunoAPI.useDuck = String.valueOf(p.get("onTheDuckSide")).toLowerCase().contains("true");
        
        p.store(new FileOutputStream(settings), null);

        i = 1; //Reset.
        addCheckBox("Force HTTPS", ZunoAPI.forceHTTPS);
        addCheckBox("Block event calls", ZunoAPI.blockPluginEvents); //might increase porformance when enabled, but will disable plugins.
        addCheckBox("Create plugin folders", ZunoAPI.createPluginDataFolders);
        addCheckBox("Use DuckDuckGo", ZunoAPI.useDuck);

        jbtn.setEnabled(false);
        jbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    save();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(jbtn);

        s.close();
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

    private void addCheckBox(String text, boolean b) {
        final int it = i;
        final JCheckBox cBox = new JCheckBox(text);
        cBox.setSelected(b);
        cBox.setName(String.valueOf(i).toString());
        cBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ZunoAPI.getOptionMenuAction(EOption.getById(it), cBox.isSelected());
            }
        });
       CBlist.add(i);
       panel.add(cBox);
       i++;
   }
    
    public static void save() throws IOException {
       Properties p = new Properties();
       FileInputStream s = new FileInputStream(settings);
       p.load(s);
       
       p.setProperty("forceHTTPS", String.valueOf(ZunoAPI.forceHTTPS));
       p.setProperty("blockEventCalls", String.valueOf(ZunoAPI.blockPluginEvents));
       p.setProperty("createPluginDataFolders", String.valueOf(ZunoAPI.createPluginDataFolders));
       p.setProperty("onTheDuckSide", String.valueOf(ZunoAPI.useDuck));
       
       p.store(new FileOutputStream(settings), null);
       s.close();
   }

   protected static void addDefault(String key, String value){
       if (!p.containsKey(key)) p.setProperty(key, value);
   }
}