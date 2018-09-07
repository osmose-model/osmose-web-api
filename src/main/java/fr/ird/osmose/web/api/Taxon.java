package fr.ird.osmose.web.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Taxon {
    private String url;
    private String name;
    private String selectionCriteria;

    public Taxon() {}

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

    public String getSelectionCriteria() {
        return selectionCriteria;
    }

    public void setSelectionCriteria(String selectionCriteria) {
        this.selectionCriteria = selectionCriteria;
    }
}
