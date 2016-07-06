import java.awt.*;
import javax.swing.*;
import java.lang.Object;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.Frame;
import javax.swing.JFrame;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Browser extends JFrame {
    
    private TextField field = new TextField();
    private JEditorPane display = new JEditorPane();
    private JScrollPane panee = new JScrollPane(display);
    private JScrollBar sbar = new JScrollBar(Scrollbar.VERTICAL, 0, 1, 0, 255);
    
    public static void main(String args[]) {

        Browser file = new Browser();
        file.frameHandler();
        
        Rectangle r = file.getBounds();
        int windowheight = r.height;
        int windowwidth = r.width;
     
    }
    
    public void frameHandler() {
        String version = "0.0.1";
        String build = "02";
        setTitle("ZunoZap " + ver + "-SNAPSHOT-" + build);
        setSize(1000, 500);
        try{ 
            setIconImage(ImageIO.read(new File("icon.png")));
        } catch (IOException e){
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(true);
        setLayout(null);
        setLocationRelativeTo(null);
        addComponentsToFrame(getContentPane());
        
    }
    
    public void addComponentsToFrame(Container pane) {
        Insets insets = getInsets();
        
        pane.add(field);
        pane.add(panee);
        pane.add(sbar);
        
        Font font = new Font("Menlo", Font.ITALIC, 12);
        
        field.setFont(font);
        
        display.setEditable(false);
        
        field.setBounds(8 - insets.left, 30 - insets.top, 1268, 20);
        panee.setBounds(8 - insets.left, 52 - insets.top, 1000, 1000);
        sbar.setBounds(8 - insets.left, 52 - insets.top, 100, 100);
        
        ActionListenerCalls();
    }
    
    private void ActionListenerCalls() {
        field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url = "http://" + e.getActionCommand();
                loadData(url);
            }
        });
        
        display.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
                    loadData(e.getURL().toString());
                }
            }
        });
    }
    
    private void loadData(String text) {
        try{
            display.setPage(text);
        }catch(Exception e){
            Log("Error :( Can't load page!" + text);
        }
    }
    
    private void Log(String text) {
        System.out.println("[ZunoZap] " + text);
    }
}
