package me.isaiah.zunozap;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
/**
 * @author  Isaiah Patton
 */
public class OptionMenu {
    private static File settings = new File(ZunoZap.homeDir, "settings.txt");
    public static ArrayList<Integer> CBlist = new ArrayList<Integer>();
    private static Properties p = new Properties();

    public static JFrame f;
    public static JPanel panel;
    public OptionMenu() { try {
        createMenu();
    } catch (IOException e) {
        e.printStackTrace();
    } }

    public final static void createMenu() throws IOException {
        f = new JFrame();
        panel = new JPanel();

        if (!settings.exists()){
            settings.createNewFile();
        }

        FileInputStream s = new FileInputStream(settings);
        p.load(s);
        
        addDefault("forceHTTPS", "true");
        addDefault("blockEventCalls", "true");
        
        ZunoAPI.forceHTTPS = String.valueOf(p.get("forceHTTPS")).toLowerCase().contains("true");
        ZunoAPI.blockPluginEvents = String.valueOf(p.get("blockEventCalls")).toLowerCase().contains("true");

        p.store(new FileOutputStream(settings), null);

        addCheckBox("Force HTTPS", ZunoAPI.forceHTTPS);
        addCheckBox("Block event calls", ZunoAPI.blockPluginEvents); //might increase porformance when enabled, but will disable plugins. 
       
        s.close();
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        panel.setSize(5000, 2000);
       
        f.setTitle("ZunoZap Settings");
        f.setPreferredSize(new Dimension(400, 300));
        f.setContentPane(panel);
        f.pack();
        f.setVisible(true);
    }

    private static int i = 1;
    private static void addCheckBox(String text) {
        addCheckBox(text, true);
    }
    
    private static void addCheckBox(String text, boolean b) {
        final JCheckBox cBox = new JCheckBox(text);
        cBox.setSelected(b);
        cBox.setName(String.valueOf(i).toString());
        cBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ZunoZap.getOptionMenuAction(EOption.getByValue(Integer.parseInt(cBox.getName())), cBox.isSelected());
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
       
       p.get("forceHTTPS");
       p.setProperty("forceHTTPS", String.valueOf(ZunoAPI.forceHTTPS));
       p.setProperty("blockEventCalls", String.valueOf(ZunoAPI.blockPluginEvents));

       p.store(new FileOutputStream(settings), null);
       s.close();
   }

   public static void addDefault(String key, String value) {
       if (!p.containsKey(key)) {
           p.setProperty(key, value);
       }
   }
}