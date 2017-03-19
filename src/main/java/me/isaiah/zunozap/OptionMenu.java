package me.isaiah.zunozap;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
/**
 * @author  Isaiah Patton
 */
public class OptionMenu {
    public static ArrayList<Integer> CBlist = new ArrayList<Integer>();

    public static JFrame f;
    public static JPanel panel;
    public static JLabel lbLabel1;
    public static JLabel lbLabel2;
    public static JCheckBox cbBox5;
    public static JCheckBox cbBox6;
    
    static GridBagLayout gbPanel0 = new GridBagLayout();
    static GridBagConstraints gbcPanel0 = new GridBagConstraints();

    public OptionMenu() { createMenu(); }

    public final static void createMenu() {
        f = new JFrame();
        panel = new JPanel();

        addCheckBox("Display Tab bar");
        addCheckBox("Force HTTPS", false);
       
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        panel.setSize(5000, 2000);
       
        f.setTitle("ZunoZap Settings");
        f.setPreferredSize(new Dimension(400, 300));
        f.setContentPane(panel);
        f.pack();
        f.setVisible(true);
    }

    private static int i = 2;
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
                ZunoZap.getOptionMenuAction(Integer.parseInt(cBox.getName()), cBox.isSelected());
            }
        });

       CBlist.add(i);
       panel.add(cBox);
       i++;
   }
}

final class ZCheckButton {
    public static int none = 0;
    public static int displayTabBar = OptionMenu.CBlist.get(0);
    public static int forceHTTPS = 3;
}
