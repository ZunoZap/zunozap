package com.javazilla.util;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

public class Download extends Observable implements Runnable {

    private static final int MAX_BUFFER_SIZE = 1024;

    private String url;
    public int size,downloaded,status;
    private File target;

    public Download(String url, File path) {
        this.url = url;
        this.target = path;
        size = -1;
        downloaded = 0;
        status = 0;
        new Thread(this).start();
    }

    @Override
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(); // Open connection to URL.

            connection.setRequestProperty("Range", "bytes=" + downloaded + "-"); // Specify what portion of file to download.
            connection.connect();

            if (connection.getResponseCode() / 100 != 2) stateChanged(status = 4); // Make sure response code is in the 200 range

            int contentLength = connection.getContentLength();
            if (contentLength < 1) stateChanged(status = 4);

            // Set the size for this download if it hasn't been already set.
            if (size == -1)  stateChanged(size = contentLength);

            target.getParentFile().mkdir();
            file = new RandomAccessFile(target.getAbsolutePath(), "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == 0) {
                // Size buffer according to how much of the file is left to download
                byte buffer[]  = size - downloaded > MAX_BUFFER_SIZE ? new byte[MAX_BUFFER_SIZE] : new byte[size - downloaded]; 

                int read = stream.read(buffer); // Read from server into buffer
                if (read == -1) break;

                file.write(buffer, 0, read); // Write buffer to file
                stateChanged(downloaded += read);
            }

            if (status == 0) stateChanged(status = 2); // Download finished
        } catch (Exception e) { stateChanged(status = 4); } finally {
            // Close connection to server.
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