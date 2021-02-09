package com.zunozap;

public class ErrorPage {

    public static void newTab(Exception ex) {
        ZunoZap.createTab(get(ex));
        ex.printStackTrace();
    }

    public static String get(Exception ex) {
        String t = ex.getMessage() + "<br>";
        for (StackTraceElement e : ex.getStackTrace()) {
            if (e.toString().contains("zunozap")) {
                t += "at " + e.toString() + "<br>";
                break;
            }
        }
        return "<html><title>Error</title><link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\"><center><div style=\"display:inline-block;\"><img src=\"https://avatars1.githubusercontent.com/u/20327341\" width=\"60px\" style=\"padding:0;margin:0;display:inline-block;\">"
                + "<h1 style=\"display:inline-block;vertical-align:middle;\">ZunoZap</h1></div><p>We're sorry, There was an error while loading this page</p><br><br><p style=\"font-size:9px;\">" + t + "</p></center></html>";
    }

}