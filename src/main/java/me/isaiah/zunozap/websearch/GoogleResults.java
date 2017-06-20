package me.isaiah.zunozap.websearch;

public class GoogleResults {
    public String title;
    public String url;

    public GoogleResults set(String title, String url) {
        this.title = title;
        this.url = url;

        return this;
    }
}
