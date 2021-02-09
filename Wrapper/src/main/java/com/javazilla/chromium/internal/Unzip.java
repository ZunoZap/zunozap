package com.javazilla.chromium.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzip {

    private File src, dest;

    private static ZipFile zf;

    public Unzip(File src, File dest) {
        this.src = src;
        this.dest = dest;
    }

    /**
     * Unzips file
     */
    public void execute() throws IOException {
        zf = new ZipFile(src);
        Enumeration<? extends ZipEntry> enumr = zf.entries();
        while (enumr.hasMoreElements())
            saveEntry(enumr.nextElement());

        zf.close();
    }

    /**
     * Save entry in the target
     */
    public void saveEntry(final ZipEntry target) throws IOException {
        try {
            File file = new File(dest.getAbsolutePath(), target.getName());
            file.delete();

            if (target.isDirectory())
                file.mkdirs();
            else {
                InputStream is = zf.getInputStream(target);
                BufferedInputStream bis = new BufferedInputStream(is);
                File dir = new File(file.getParent());
                dir.mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int c;
                while ((c = bis.read()) != -1)
                    bos.write((byte) c);

                bos.close();
                fos.close();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

}