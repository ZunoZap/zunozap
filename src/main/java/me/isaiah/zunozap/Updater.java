package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Check for updates.
 */
public class Updater {
    public void plugin() {
        System.out.println("Method \"plugin()\" not supported!");
    }

    public static String browser(String a, String b) {
        switch(browserUpdater(a)) {
            case(0):
            break;
            case(1):
            return b + " is outdated!\nIt is recommended that you update to the latest version\n";
            case(2):
            return "Your using a snapshot build of " + b + "\nSnapshot builds might contain bugs!";
            case(-1):
            return "Error fetching update infomation.";
            default:
            return "_NULL_";
        }
        return "_NULL_";
    }
    
    private static int browserUpdater(String ver) {
        String line = "error";
        try {
            URL url = new URL("https://raw.githubusercontent.com/ZunoZap/zunozap/master/LATEST-RELEASE.md");
            URLConnection urlc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
            line = in.readLine().trim();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        if (line.equalsIgnoreCase(ver))
            return 0; // latest
        if (ver.toLowerCase().endsWith("-snapshot"))
            return 2;

        return 1;
    }
}