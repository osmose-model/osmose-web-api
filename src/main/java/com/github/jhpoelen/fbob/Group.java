package com.github.jhpoelen.fbob;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private String name;
    private List<Species> species;
    private GroupType type;

    public Group(String name) {
        this(name, GroupType.FOCAL);
    }

    public Group(String name, GroupType type) {
        this(name, type, new ArrayList<Species>());
    }

    public Group(String name, GroupType type, List<Species> species) {
        this.species = species;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Species> getSpecies() {
        return species;
    }

    public void setSpecies(List<Species> species) {
        this.species = species;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }


}
