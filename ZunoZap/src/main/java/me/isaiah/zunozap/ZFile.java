package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;

public class ZFile extends File {
    private static final long serialVersionUID = 1L;
    private static File homeDir = new File(System.getProperty("user.home"), "ZunoZap");
    private boolean isDir;

    public ZFile(String child) {
        super(homeDir, child);
        this.isDir = true;
        mkIfNotExist();
    }

    public ZFile(String child, boolean isDir) {
        super(homeDir, child);
        this.isDir = isDir;
        mkIfNotExist();
    }

    @Override
    public File getParentFile() {
        mkIfNotExist();
        return homeDir;
    }

    @Override
    public String getParent() {
        mkIfNotExist();
        return homeDir.getPath();
    }

    @Override
    public boolean isDirectory() {
        return isDir;
    }

    public boolean mkIfNotExist() {
        if (!exists()) {
            if (isDir) return mkdirs();

            try { return createNewFile(); } catch (IOException e) { return false; }
        }
        return false;
    }
}