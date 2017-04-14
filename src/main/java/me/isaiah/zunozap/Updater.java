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
        //TODO: Plugin updater.
        System.out.println("Method \"plugin()\" not supported!");
    }

    public static String browser(String a) {
        switch(browserUpdater(a)) {
            case(0):
                break;
            case(1):
                ZunoAPI.isOutdated = true;
                return ZunoAPI.name + " is outdated!\nIt is recommended that you update to the latest version\n" + ZunoAPI.name + " will still continue to work if you dont update.";
            case(2):
                return "Your using a snapshot build of " + ZunoAPI.name + "\nSnapshot builds might contain bugs!\nPlease report bugs at:\nhttps://github.com/ZunoZap/zunozap/issues/";
            case(-1):
                return "Error fetching update infomation.";
            default:
                return "_NULL_";
        }
        return "_NULL_";
    }
    
    private static int browserUpdater(String version) {
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

        if (line.equalsIgnoreCase(version)) return 0; //Latest build
        if (version.toLowerCase().endsWith("-snapshot")) return 2; //Dev build.

        return 1;
    }
}