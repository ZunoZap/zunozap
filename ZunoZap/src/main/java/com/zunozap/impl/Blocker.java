package com.zunozap.impl;

import com.javazilla.chromium.events.ResourceRequestEvent;
import com.zunozap.Settings;

public class Blocker {

    public static void check(ResourceRequestEvent ev, String url) {
        if (url.contains("trackingserver") || url.contains("ad_url")) {
            ev.cancel();
            return;
        }

        if (Settings.dataSaver > 1) {
            String[] block = {"google.com/log", "adservice", "facebook.com/brandlift", "w55c.net", "zdbb.net",
                    "tags.bluekai.com", "imrworldwide.com", "sync-tm.everesttech", "cdn.krxd", "google-analytics", "bkrtx.com",
                    "trc.taboola", "googletag", "evidon.com", "scorecardresearch.com", "rlcdn.com", "ads.pubmatic",
                    "youtube.com/ptracking", "r3---sn-xo5-poql.google"};
            if (url.contains("weserv.nl")) return;

            if (ev.isCanceled())
                if (ev.getResourceType().contains("FONT") || ev.getResourceType().contains("SCRIPT"))
                    Settings.increaseDSAM(3);

            if (url.contains("i.ytimg.com") && (url.contains("hq720") || url.contains("hqdefault"))) {
                String sd = (Math.random() > 0.4) ? "mqdefault" : "default";
                ev.setUrl(url.replace("hq720", sd).replace("hqdefault", sd)); // Make YouTube use 320x180 thumb instead of 1280x720
                ev.setUrl("https://images.weserv.nl/?url=" + ev.getUrl() + "&w=320&h=180&we");
                Settings.increaseDSAM(25 + (int)(Math.random()+1)); // conservative estimate 
            }

            if (ev.getResourceType().contains("RT_IMAGE") && !ev.getUrl().contains("weserv.nl") && Settings.compress > 0) {
                String ur = ("https://images.weserv.nl/?url=" + ev.getUrl());
                if (Settings.compress == 1 && Settings.compress == 2)
                    ur += "&output=webp&w=1600&h=900&we";
                if (Settings.compress >= 3) ur += "&output=webp&w=192&h=192&we";
                if (Settings.compress >= 2) ur += "&filt=duotone&start=595959&stop=white";

                ev.setUrl(ur);
                Settings.increaseDSAM(38 * Settings.compress);
            }

            if (url.contains("i.ytimg.com") && url.contains("an_webp")) {
                ev.cancel(); // YouTube Auto-play Preview
                Settings.increaseDSAM(40);
            }

            if (url.contains("geoip") || url.contains("consensu.org")) ev.cancel();
            if (url.contains("jquery.min.js")) {
                ev.cancel();
                Settings.increaseDSAM(12);
            }

            if (url.contains("youtube.com/embed") && url.contains("origin=https%3A%2F%2Fwww.youtube")) ev.cancel();
            for (String s : block) 
                if (url.contains(s)) {
                    Settings.increaseDSAM(2);
                    ev.cancel();
                    break;
                }
        }
        if (Settings.dataSaver > 2) {
            String[] block = {"ggpht.com"};
            for (String s : block) if (url.contains(s)) ev.cancel();
        }
    }

}