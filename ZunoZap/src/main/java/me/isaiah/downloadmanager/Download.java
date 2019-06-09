package me.isaiah.downloadmanager;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

public class Download extends Observable implements Runnable {

    private static final int MAX_BUFFER_SIZE = 1024;

    public static final String STATUSES[] = {"Downloading", "Paused", "Complete", "Cancelled", "Error"}; // status names
    public static final int DOWNLOADING = 0, PAUSED = 1, COMPLETE = 2, CANCELLED = 3, ERROR = 4; // status codes

    private URL url;
    private int size,downloaded,status;
    private File folder;

    public Download(URL url) {
        this(url, new File(System.getProperty("user.home"), "Downloads"));
    }

    public Download(URL url, File path) {
        this.url = url;
        this.folder = path;
        size = -1;
        downloaded = 0;
        status = 0;

        download(); // Begin
    }

    public File getAsFile() {
        return new File(folder, getFileName(url));
    }

    public String getUrl() {
        return url.toString();
    }

    public int getSize() {
        return size;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    public int getStatus() {
        return status;
    }

    public void stat(int s) {
        stateChanged(status = s);
    }

    // Start or resume downloading.
    private void download() {
        new Thread(this).start();
    }

    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    @Override
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Open connection to URL.

            connection.setRequestProperty("Range", "bytes=" + downloaded + "-"); // Specify what portion of file to download.

            connection.connect(); // Connect to server.

            if (connection.getResponseCode() / 100 != 2) stat(4); // Make sure response code is in the 200 range

            int contentLength = connection.getContentLength();
            if (contentLength < 1) stat(4);

            // Set the size for this download if it hasn't been already set.
            if (size == -1)
                stateChanged(size = contentLength);

            folder.mkdir();
            file = new RandomAccessFile(folder.getAbsolutePath() + File.separator + getFileName(url), "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == DOWNLOADING) {
                // Size buffer according to how much of the file is left to download
                byte buffer[]  = size - downloaded > MAX_BUFFER_SIZE ? new byte[MAX_BUFFER_SIZE] : new byte[size - downloaded]; 

                int read = stream.read(buffer); // Read from server into buffer
                if (read == -1) break;

                file.write(buffer, 0, read); // Write buffer to file
                stateChanged(downloaded += read);
            }

            // Change status to complete if this point was reached because downloading has finished
            if (status == 0)
                stateChanged(status = 2);
        } catch (Exception e) { stat(4); } finally {
            // Close file & connection to server.
            if (file != null) try { file.close(); } catch (Exception e) {}
            if (stream != null) try { stream.close(); } catch (Exception e) {}
        }
    }

    // Notify observers that this download's status has changed
    private Object stateChanged(Object o) {
        setChanged();
        notifyObservers();
        return o;
    }

}