package fr.ird.osmose.web.api;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;



@XmlRootElement
public class Group {

    private String name;
    private List<Taxon> taxa;
    private GroupType type;

    public Group() {}

    public Group(String name) {
        this(name, GroupType.FOCAL);
    }

    public Group(String name, GroupType type) {
        this(name, type, new ArrayList<>());
    }

    public Group(String name, GroupType type, List<Taxon> taxa) {
        this.taxa = taxa;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Taxon> getTaxa() {
        return taxa;
    }

    public void setTaxa(List<Taxon> taxa) {
        this.taxa = taxa;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }


}
