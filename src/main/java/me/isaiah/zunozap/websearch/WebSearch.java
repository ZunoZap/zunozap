package me.isaiah.zunozap.websearch;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebSearch {
    public ArrayList<GoogleResults> a = new ArrayList<GoogleResults>();
    public ArrayList<GoogleAds> b = new ArrayList<GoogleAds>();

    public void main(String[] args) throws IOException {
        System.out.println("STARTING SEARCH...");

        Document d = Jsoup.connect("http://www.google.com/search?q=" + URLEncoder.encode(args[0], "UTF-8"))
                .userAgent("ZunoZap/0.1.0").get();

        int maxi = 0;
        for (Element link : d.select(".g>.r>a")) {
            String title = link.text();
            String url = link.absUrl("href");
            url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

            if (!url.startsWith("http")) {
                b.add(b.size(), new GoogleAds(title, url));
                continue; // Ads/news/etc.
            }

            String[] split = ("Result: " + title + " URL: " + url).split(" URL: ");
            int i = split[0].length();
            if (i > maxi) maxi = i;

            a.add(a.size(), new GoogleResults().set(title, url));
        }
    }

    public static String repeat(String str, int t) {
        String sp = "";
        for (; 0 < t; t--)
            sp = sp + str;
        return sp;
    }
}