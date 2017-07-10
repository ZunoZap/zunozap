package me.isaiah.zunozap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Updater {
    public static String browser(String a, String b) {
        String line = "error";
        try {
            java.net.URLConnection c = new URL(
                    "https://raw.githubusercontent.com/ZunoZap/zunozap/master/LATEST-RELEASE.md").openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
            line = in.readLine().trim();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching update infomation: " + e;
        }

        if (line.equalsIgnoreCase(a))
            return "You are running the latest version";
        if (a.toLowerCase().endsWith("-snapshot"))
            return "Your using a snapshot build of " + b + "\nSnapshot builds might contain bugs!";

        return b + " is outdated!\nIt is recommended that you update to the latest version\n";
    }
}