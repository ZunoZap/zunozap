package com.zunozap.launch;

import static com.zunozap.Log.out;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class unzipes a zip file.
 * @author skabore
 */
public class Unzip {

    /**
     * File to unzip.
     */
    private File src;

    /**
     * Location of unzipped file.
     */
    private File dest;

    /**
     * ZipFile attribute.
     */
    private static ZipFile zf;

    /**
     * End Of File to unzip.
     */
    private static final int EOF = -1;

    public Unzip(File src, File dest) {
        this.src = src;
        this.dest = dest;
    }

    /**
     * This method unzips file.
     */
    public void execute() throws IOException {
        zf = new ZipFile(src);
        Enumeration<? extends ZipEntry> enumr = zf.entries();
        while (enumr.hasMoreElements())
            saveEntry(enumr.nextElement());

        zf.close();
    }

    /**
     * Save entry in the target.
     * @param target the target
     * @throws IOException the IOException
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
                while ((c = bis.read()) != EOF)
                    bos.write((byte) c);

                bos.close();
                fos.close();
            }
        } catch (IOException e) { out(e.getMessage()); }
    }

}