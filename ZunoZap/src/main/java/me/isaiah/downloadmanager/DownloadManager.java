package me.isaiah.downloadmanager;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class DownloadManager extends JFrame implements Observer {

    private static final long serialVersionUID = 1L;

    private JTextField addTextField;
    private DownloadsTableModel tableModel;
    private JTable table;

    private JButton pause, resume, cancel, clear;
    private Download selected;
    private boolean clearing;
    private Timer t;

    public DownloadManager() {
       setTitle("Download Manager");
       setSize(640, 480);

       addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e){ setVisible(false); }});

       // Set up add panel
       JPanel addPanel = new JPanel();
       addTextField = new JTextField(30);

       // Set up Downloads table
       tableModel = new DownloadsTableModel();
       table = new JTable(tableModel);
       table.getSelectionModel().addListSelectionListener(l -> tableSelectionChanged());

       table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row at a time to be selected

       // Set up ProgressBar as renderer for progress column
       ProgressRenderer renderer = new ProgressRenderer(0, 100);
       renderer.setStringPainted(true);
       table.setDefaultRenderer(JProgressBar.class, renderer);

       table.setRowHeight((int) renderer.getPreferredSize().getHeight());

       JPanel downloadsPanel = new JPanel();
       downloadsPanel.setBorder(BorderFactory.createTitledBorder("Download Manager"));
       downloadsPanel.setLayout(new BorderLayout());
       downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

       // Set up s panel.
       JPanel sPanel = new JPanel();
       pause = new JButton("Pause");
       resume = new JButton("Resume");
       cancel = new JButton("Cancel");
       clear = new JButton("Clear");
       JButton exit = new JButton("Exit");

       pause.addActionListener(l -> action(1));
       pause.setEnabled(false);
       sPanel.add(pause);

       resume.addActionListener(l -> action(0));
       resume.setEnabled(false);
       sPanel.add(resume);

       cancel.addActionListener(l -> action(3));
       cancel.setEnabled(false);
       sPanel.add(cancel);

       clear.addActionListener(l -> actionClear());
       clear.setEnabled(false);
       sPanel.add(clear);

       exit.addActionListener(l -> setVisible(false));
       sPanel.add(exit);

       // Add panels to display.
       getContentPane().setLayout(new BorderLayout());
       getContentPane().add(addPanel, BorderLayout.NORTH);
       getContentPane().add(downloadsPanel, BorderLayout.CENTER);
       getContentPane().add(sPanel, BorderLayout.SOUTH);

       t = new Timer();
       t.schedule(new TimerTask() { @Override public void run() {
           if (!isVisible()) {
               if (tableModel.getDownloads().size() <= 0) {
                   dispose();
                   t.cancel();
               } else {
                   boolean b = false;
                   for (Download down : tableModel.getDownloads()) if (down.getStatus() >= 2) b = true;

                   if (b) {
                       dispose();
                       t.cancel();
                   }
               }
        }}}, 100, 10000);
    }

    public void addDownload(String s) {
        String url = s;
        if (!s.startsWith("http")) url = "http://" + s;
        URL verified = verifyUrl(url);
        if (verified != null) {
           tableModel.addDownload(new Download(verified));
           addTextField.setText(""); // reset
        } else JOptionPane.showMessageDialog(this, "Invalid Download URL", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private URL verifyUrl(String url) {
       if (!url.toLowerCase().startsWith("http")) return null;

       URL verified = null;
       try {
          verified = new URL(url);
       } catch (Exception e) { return null; }

       if (verified.getFile().length() < 2)  return null; // Make sure URL specifies a file.

       return verified;
    }

    // Called when table row selection changes.
    private void tableSelectionChanged() {
        // Unregister from receiving notifications from the last selected download
        if (selected != null) selected.deleteObserver(DownloadManager.this);

        /* If not in the middle of clearing a download, set the selected download and register to
          receive notifications from it. */
        if (!clearing) {
            selected = tableModel.getDownload(table.getSelectedRow());
            selected.addObserver(DownloadManager.this);
            updates();
        }
    }

    private void action(int s) {
        selected.stat(s);
        updates();
    }

    private void actionClear() {
       clearing = true;
       tableModel.clearDownload(table.getSelectedRow());
       clearing = false;
       selected = null;
       updates();
    }

    private void updates() {
       if (selected != null) {
           int s = selected.getStatus();
           setsEnabled(s == 0, (s == 1 || s == 4), s <= 1, s >= 2);
       } else setsEnabled(false, false, false, false); // No download is selected in table.
    }

    private void setsEnabled(boolean a, boolean b, boolean c, boolean d) {
        pause.setEnabled(a);
        resume.setEnabled(b);
        cancel.setEnabled(c);
        clear.setEnabled(d);
    }

    @Override
    public void update(Observable o, Object arg) {
       if (selected != null && selected.equals(o)) updates();
    }

    public static void main(String[] args) {
       run(new DownloadManager());
    }

    public static void run(DownloadManager instance) {
        instance.setVisible(true);
    }

}