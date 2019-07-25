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

    private static DownloadManager dm = null;
    public static void addToManager(String url) {
        if (dm == null) dm = new DownloadManager();

        dm.addDownload(url);
        dm.setVisible(true);
    }

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

        JPanel sPanel = new JPanel();
        (pause = (JButton) sPanel.add(new JButton("Pause"))).addActionListener(l -> action(1));
        (resume = (JButton) sPanel.add(new JButton("Resume"))).addActionListener(l -> action(0));
        (cancel = (JButton) sPanel.add(new JButton("Cancel"))).addActionListener(l -> action(3));
        (clear = (JButton) sPanel.add(new JButton("Clear"))).addActionListener(l -> actionClear());
        ((JButton) sPanel.add(new JButton("Exit"))).addActionListener(l -> setVisible(false));

        setsEnabled(false, false, false, false);

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
                    for (Download down : tableModel.getDownloads()) if (down.getStatus() >= 2) b = true; else {b = false; break;}

                    if (b) {
                        dispose();
                        t.cancel();
                    }
                }
        }}}, 100, 10000);
    }

    public void addDownload(String s) {
        URL verified = verifyUrl(s.startsWith("http") ? s : "http://" + s);
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

        if (verified.getFile().length() < 2) return null; // Make sure URL is a file

        return verified;
    }

    private void tableSelectionChanged() {
        if (selected != null) selected.deleteObserver(DownloadManager.this); // Unregister from the last selected

        // If not in the middle of clearing a download, set the selected and register to receive notifications
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
        } else setsEnabled(false, false, false, false); // No download is selected
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

}