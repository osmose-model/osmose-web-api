package com.github.jhpoelen.fbob;

public class Taxon {
    private String url;
    private String name;
    private String rank;

    public Taxon(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
