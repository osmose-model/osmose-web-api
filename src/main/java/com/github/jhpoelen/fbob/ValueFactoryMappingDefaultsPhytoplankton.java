package com.github.jhpoelen.fbob;

import org.apache.commons.lang3.StringUtils;

public class ValueFactoryMappingDefaultsPhytoplankton extends ValueFactoryMappingDefault {

    public ValueFactoryMappingDefaultsPhytoplankton() {
        super("fishbase-mapping-phytoplankton.csv");
    }

    @Override
    public String groupValueFor(String name, Group group) {
        boolean isPhytoplanktonBackgroundGroup = GroupType.BACKGROUND == group.getType()
                && StringUtils.equalsIgnoreCase("phytoplankton", group.getName());
        return isPhytoplanktonBackgroundGroup ? super.groupValueFor(name, group) : null;
    }

}
