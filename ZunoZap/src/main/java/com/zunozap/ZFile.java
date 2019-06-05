package me.isaiah.zunozap;

import java.io.File;

public class ZFile extends File {

    private static final long serialVersionUID = 1L;
    private static File h = new File(System.getProperty("user.home"), "ZunoZap");
    private boolean isDir;

    public ZFile(String ch) {
        this(ch, true);
    }

    public ZFile(String ch, boolean dir) {
        super(h, ch);
        this.isDir = dir;
        mk();
    }

    @Override public File getParentFile() {
        return h;
    }

    @Override public String getParent() {
        return h.getPath();
    }

    @Override public boolean isDirectory() {
        return isDir;
    }

    public boolean mk() {
        if (isDir) return mkdirs();

        try { return createNewFile(); } catch (java.io.IOException e) { return false; }
    }

}