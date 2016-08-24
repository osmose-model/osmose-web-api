package com.github.jhpoelen.fbob;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private String name;
    private List<Species> speciesList;
    private FunctionalGroupType type;

    public Group(String name) {
        this(name, FunctionalGroupType.FOCAL);
    }

    public Group(String name, FunctionalGroupType type) {
        this(name, type, new ArrayList<Species>());
    }

    public Group(String name, FunctionalGroupType type, List<Species> speciesList) {
        this.speciesList = speciesList;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Species> getSpeciesList() {
        return speciesList;
    }

    public void setSpeciesList(List<Species> speciesList) {
        this.speciesList = speciesList;
    }

    public FunctionalGroupType getType() {
        return type;
    }

    public void setType(FunctionalGroupType type) {
        this.type = type;
    }


}
