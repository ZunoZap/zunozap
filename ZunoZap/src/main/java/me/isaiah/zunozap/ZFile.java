package me.isaiah.zunozap;

import java.io.File;
import java.io.IOException;

public class ZFile extends File {
    private static final long serialVersionUID = 1L;
    private static File homeDir = new File(System.getProperty("user.home"), "zunozap");

    public ZFile(String child) {
        super(homeDir, child);
        if (!homeDir.exists()) homeDir.mkdirs();
    }

    @Override
    public File getParentFile() {
        return homeDir;
    }

    @Override
    public String getParent() {
        return homeDir.getPath();
    }
    
    public boolean mkIfNotExist(boolean isDir) {
        if (!exists()) {
            if (isDir) return mkdirs();
            
            try {
                return createNewFile();
            } catch (IOException e){ return false; }
        }
        return false;
        
    }
}