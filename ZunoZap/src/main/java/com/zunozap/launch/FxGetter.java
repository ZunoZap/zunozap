package com.zunozap.launch;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

import com.javazilla.util.ClassLoadingUtil;

public class FxGetter {

    public static void test() {
        try {
            Class.forName("javafx.application.Application");
            return;
        } catch (ClassNotFoundException e) {}
        System.out.println("JavaFX not found, attempting to download OpenJFX 8...");
        try {
            File f = new File("C:\\Users\\patt8139\\Desktop\\jrs\\lib");
            for (File fi : f.listFiles())
                ClassLoadingUtil.appendToURLClassPath(fi.toURI().toURL());
            String javaLibraryPath = System.getProperty("java.library.path");
            File javaExeFile = new File(javaLibraryPath.substring(0, javaLibraryPath.indexOf(';')));
            addLibraryDir(javaExeFile.getParent() + File.separator + "jre" + File.separator + "bin" + File.separator);

            URLClassLoader cll = new URLClassLoader(new URL[] {new File(f, "jfx-natives-win.jar").toURI().toURL()});
            Class<?> cl = cll.loadClass("com.javazilla.fx.FxNatives");
            cl.getDeclaredMethod("main", String[].class).invoke(null, (Object)null);
            cll.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addLibraryDir(String libraryPath) throws Exception {
        Field userPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        userPathsField.setAccessible(true);
        String[] paths = (String[]) userPathsField.get(null);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            if (libraryPath.equals(paths[i])) continue;
            sb.append(paths[i]).append(File.pathSeparatorChar);
        }
        sb.append(libraryPath);
        System.setProperty("java.library.path", sb.toString());
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }

}
