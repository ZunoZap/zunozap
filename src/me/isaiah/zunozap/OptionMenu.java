package me.isaiah.zunozap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.lang.ExceptionInInitializerError;
/**
 * @author  Administrator
 * @created November 23, 2016. By Jvider v1.8.1.
 */
@SuppressWarnings("serial") 
public class OptionMenu extends JFrame {

    static OptionMenu theTest;
    public static ArrayList<Integer> CBlist = new ArrayList<Integer>();
    
    JPanel panel;
    JLabel lbLabel1;
    JLabel lbLabel2;
    JCheckBox cbBox5;
    JCheckBox cbBox6;
    
    static GridBagLayout gbPanel0 = new GridBagLayout();
    static GridBagConstraints gbcPanel0 = new GridBagConstraints();

   public static void main(String args[]) {
       try {
           UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
       } catch (ClassNotFoundException e) {/**/}
         catch (InstantiationException e) {/**/}
         catch (IllegalAccessException e) {/**/}
         catch (UnsupportedLookAndFeelException e) {/**/}
       try {
           theTest = new OptionMenu();
       } catch (ExceptionInInitializerError e) {
           System.out.println("Error!");
       }
   } 

   public OptionMenu() {
       super();

       panel = new JPanel();
       panel.setLayout(gbPanel0);
   
       addCheckBox("Display Tab bar", panel);
       addCheckBox("Display Back button", panel);
       addCheckBox("Display forward button", panel);
       //addCheckBox("Test", panel);
       
       setDefaultCloseOperation(DISPOSE_ON_CLOSE);
       panel.setSize(5000, 2000);
       
       setTitle("ZunoZap Settings");
       setContentPane(panel);
       pack();
       setVisible(true);
   }
   
   private static void addCheckBox(String text, JPanel panel) {
       panel.add(createCheckBox(text));
   }
   private static int i = 2;
   private static JCheckBox createCheckBox(String text) {
       JCheckBox cBox = new JCheckBox(text);
       cBox.setSelected(true);
       cBox.setName(String.valueOf(i).toString());
       cBox.addActionListener(new ActionListener(){
           
        @Override
        public void actionPerformed(ActionEvent e) {
            ZunoZap.getOptionMenuAction(Integer.parseInt(cBox.getName()), cBox.isSelected());
        }});
       
       CBlist.add(i);
       
       gbcPanel0.gridx = 0;
       gbcPanel0.gridy = i;
       gbcPanel0.gridwidth = 1;
       gbcPanel0.gridheight = 1;
       gbcPanel0.fill = GridBagConstraints.BOTH;
       gbcPanel0.weightx = 1;
       gbcPanel0.weighty = 0;
       gbcPanel0.anchor = GridBagConstraints.NORTH;
       gbPanel0.setConstraints(cBox, gbcPanel0);
       i++;
       
       return cBox;
   }
}

final class ZCheckButton {
    public static int none = 1;
    public static int displayTabBar = OptionMenu.CBlist.get(0);
    public static int displayBackButton = 3;
    public static int displayForwardButton = 4;
}
