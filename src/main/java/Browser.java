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
import java.net.InetAddress;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.io.*;
import java.io.BufferedWriter;
import java.net.URL;
import java.net.URLConnection;

public class Browser extends JFrame {

    public String version  = "0.0.1";    /*   Big update = Change this by 1   */
    public String build    = "03";       /*   Small update = Change this by 1 */
    private String type    = "DEVBUILD"; /*   DEVBUILD, SNAPSHOT, RELEASE     */ 
    public String fullversion = version + "-" + type + "-" + build;
    
    private TextField field = new TextField();
    private JEditorPane display = new JEditorPane();
    private JScrollPane panee = new JScrollPane(display);
    private JScrollBar sbar = new JScrollBar(Scrollbar.VERTICAL, 0, 1, 0, 255);
    private JButton backButton, forwardButton;
    private JMenuBar themenuBar = new JMenuBar();
    
    public static void main(String args[]) {

        Browser file = new Browser();
        file.frameHandler();
        
        Rectangle r = file.getBounds();
        int windowheight = r.height;
        int windowwidth = r.width;

        String folder = System.getProperty("user.home") + "\\ZunoZap\\";
        File programfolder = new File(folder);
        File programsettings = new File(folder + "settings.txt");
        try {
            if (!programfolder.exists()) {
                LogS("Creating C:\\ZunoZap\\");
                //programfolder.createNewFile();
                programsettings.createNewFile();
            }
            if (!programsettings.exists()) {
                LogS("Creating C:\\ZunoZap\\settings.txt");
                programsettings.createNewFile();
            }
        } catch (IOException e) {
            LogS(e.toString());
        }
        LogS("Starting Browser.class");
        file.startPage();
    }
    
    public void startPage() {
        try{
            runStartPage();
        }catch(Exception e){
            Log("Error :( Can't load start page!");
            display.setText("Error :( Can't load page: http://zunozap.github.io/");
        }
    }
    
    public void frameHandler() {
        setTitle("ZunoZap " + fullversion);
        setSize(1000, 700);
        try{ 
            setIconImage(ImageIO.read(new File("icon.png")));
        } catch (IOException e){
            LogS(e.toString());
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
        
        JPanel buttonPanel = new JPanel();
        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutPage();
            }
        });
        buttonPanel.add(aboutButton);
        
        
        
        //Imported MenuBar from my never released browser MiniBrowser.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu closeTab = new JMenu("Close");
        
        
        // Set up file menu.
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileAboutMenuItem = new JMenuItem("About",
                KeyEvent.VK_X);
        fileAboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //actionExit();
                AboutPage();
            }
        });
        closeTab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        fileMenu.add(fileAboutMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(closeTab);
        setJMenuBar(menuBar);
        
        pane.add(field);
        pane.add(panee);
        pane.add(sbar);
        //pane.add(menuBar);
        //pane.add(buttonPanel);
        //pane.add(fileMenu);
        
        Font font = new Font("Menlo", Font.ITALIC, 12);
        
        field.setFont(font);
        
        display.setEditable(false);
        
        field.setBounds(8 - insets.left, 30 - insets.top, 568, 20);
        panee.setBounds(17 - insets.left, 52 - insets.top, 990, 1000);
        sbar.setBounds(6 - insets.left, 52 - insets.top, 12, 1000);
        
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
                    field.setText(e.getURL().toString());
                }
            }
        });
    }
    
    private void loadData(String text) {
        try{
            /*InetAddress giriip = java.net.InetAddress.getByName(text);
            String ip = giriip.getHostAddress();*/
            String ip = "[IP-HIDDEN]";
            display.setPage(text);
            Log("Connected to: " + ip + "(" + text + ")");
        }catch(Exception e){
            Log("Error :( Can't load page!" + text);
            display.setText("Error :( Can't load page: " + text);
        }
    }
    
    private void Log(String text) {
        System.out.println("[ZunoZap] " + text);
    }
    private static void LogS(String text) {
        //Static version of Log();
        System.out.println("[ZunoZap] " + text);
    }
    private String getUrlSource(String site) throws IOException {
        URL url = new URL(site);
        URLConnection urlc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
        urlc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        a.append(inputLine);
        in.close();

        return a.toString();
    }
    public void actionExit() {
        System.exit(0);
    }
    
    public void runStartPage() throws IOException {
        display.setPage("https://zunozap.github.io/");
    }
    public void writetext(String text) {
        display.setText(text);
    }
    public void AboutPage() {
        try{
            display.setPage("https://zunozap.github.io/");
        } catch(IOException e) {
            Log(e.toString());
        }
        String at;
        at = "<html><CENTER><h1>About ZunoZap</h1>";
        at = at + "<p>Version: " + fullversion + "</p>";
        at = at + "<p>Build: " + build + "</p>";
        at = at + "<p>Folder: " + System.getProperty("user.home") + "\\ZunoZap\\" + "</p></html>";
        field.setText("zunozap:about");
        display.setText(at);
    }
}