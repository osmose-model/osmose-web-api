package com.github.jhpoelen.fbob;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ValueFactoryMappingDefault implements ValueFactory {

    private final String mappingResource;
    private Map<String, String> defaults;

    public ValueFactoryMappingDefault() {
        this("fishbase-mapping.csv");
    }

    public ValueFactoryMappingDefault(String mappingResource) {
        this.mappingResource = mappingResource;
    }

    @Override
    public String groupValueFor(String name, Group group) {
        if (defaults == null) {
            populateDefaults(name, group);
        }
        return defaults.get(name);
    }

    private void populateDefaults(String name, Group group) {
        defaults = new TreeMap<>();
        PropertyMapping defaultMapping = (tableName, columnName, mappedName, defaultValue) -> defaults.put(mappedName, defaultValue);
        try {
            TraitFinder.doMapping(getClass().getResourceAsStream(mappingResource), defaultMapping);
        } catch (IOException e) {
            System.err.println("failed to find default for [" + name + "] in group [" + group.getName());
            e.printStackTrace(System.err);
        }
    }

}
