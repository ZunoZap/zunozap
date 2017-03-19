package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Check for updates.
 * @author Isaiah Patton
 */
public class Updater {
    public void plugin(String pluginName) {
        //TODO: Plugin updater.
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
            line = latestBrowserVersion();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        if (line.replace("-SNAPSHOT", "").equalsIgnoreCase(version)) {
            return 0; //Latest build
        }
        
        if (version.toLowerCase().endsWith("-snapshot")) {
            return 2; //Dev build.
        }

        /*line = line.toLowerCase().replace("-snapshot", ""); Not right if a version number is skipped.
        return (Integer.valueOf(line.replaceAll("[ . ]", "")) - Integer.valueOf(version.replaceAll("[ . ]", "")));*/
        return 1;
    }
    
    private static String latestBrowserVersion() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/ZunoZap/zunozap/master/pom.xml");

        URLConnection urlc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
        String inputLine;
        String line = "error";
        int i = 0;
        while ((inputLine = in.readLine()) != null) {
            if (i == 6) {
                line = inputLine.trim().substring(9).replace("</version>", "");
                in.close();
                return line;
            }
            i++;
        }
        return "error";
    }
}
