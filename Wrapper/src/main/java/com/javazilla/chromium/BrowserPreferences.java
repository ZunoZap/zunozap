package com.javazilla.chromium;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.cef.CefSettings;

public class BrowserPreferences {

    public static CefSettings settings = new CefSettings();
    private static String brand = "ZunoZap";
    public static boolean showNHgui = true;
    private static boolean blockRemoteFonts = false;
    private static boolean blockRemoteJS = false;
    private static ArrayList<String> switches = new ArrayList<>();

    public static void initDefault() {
        if (null == BrowserPreferences.settings.cache_path) {
            BrowserPreferences.settings.cache_path = new File(NativeHelper.lib.getParentFile(), "cache").getAbsolutePath();
        }
        settings.persist_session_cookies = true;
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_INFO;
        settings.user_agent = getUserAgent();
        System.out.println("BrowserPreferences init");
    }

    public static void addSwitch(String... toAdd) {
        switches.addAll(Arrays.asList(toAdd));
    }

    public static String[] getSwitches() {
        return switches.toArray(new String[switches.size()]);
    }

    public static String getBrand() {
        return brand;
    }

    public static void setBrand(String str) {
        brand = str;
    }

    /**
     * Blocks all resources with the type "RT_FONT_RESOURCE"
     */
    public static void setBlockRemoteFonts(boolean b) {
        blockRemoteFonts = b;
    }

    public static boolean shouldBlockRemoteFonts() {
        return blockRemoteFonts;
    }

    public static String getChromiumDir() {
        return NativeHelper.fold.getAbsolutePath();
    }

    public static String getDefaultChromiumDir() {
        return NativeHelper.fold.getAbsolutePath();
    }

    public static void setUserAgent(String userAgent) {
        settings.user_agent = userAgent;
    }

    public static String getUserAgent() {
        if (null == settings.user_agent) {
            // As of 2021, Google has blocked sign-in for CEF browsers
            // Now requiring us to report we are Firefox instead of Chrome.

            String spoof = NativeHelper.getPlatform().VER.split("chromium-")[1].split("[.]")[0] + ".0";
            int spoof_gecko = 20100101;

            String ua = "Mozilla/5.0 (%s) Gecko/" + spoof_gecko + " Firefox/%s";
            String os = System.getProperty("os.name");
            if (os.startsWith("Win")) os = "Windows NT 10.0"; 
            String arch = System.getProperty("os.arch");
            if (os.indexOf("Win") != -1) os = os + "; Win64; x64";
            else if (os.indexOf("Mac") != -1) os = os + "; Mac";
            else os = "X11; Linux x86_64" + arch;
            os += "; rv:84.0";

            return String.format(ua, os, spoof);
        }
        return settings.user_agent;
    }

    @Deprecated
    public static boolean isJavaScriptEnabled() {
        return !blockRemoteJS;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void setJavaScriptEnabled(boolean b) {
        BrowserPreferences.blockRemoteJS = !b;
    }

}