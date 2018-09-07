package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringUtils;

public class ValueFactoryMappingDefaultsForGroup extends ValueFactoryMappingDefault {

    private final Group groupSelected;

    ValueFactoryMappingDefaultsForGroup(String mappingResource, Group groupSelector) {
        super(mappingResource);
        groupSelected = groupSelector;
    }

    @Override
    public String groupValueFor(String name, Group group) {
        boolean isPhytoplanktonBackgroundGroup = groupSelected.getType() == group.getType()
                && StringUtils.equalsIgnoreCase(groupSelected.getName(), group.getName());
        return isPhytoplanktonBackgroundGroup ? super.groupValueFor(name, group) : null;
    }

}
