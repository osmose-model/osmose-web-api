package com.github.jhpoelen.fbob;

import java.util.List;

public class ValueFactoryFishbaseCacheTest extends ValueFactoryFishbaseTestBase {

    @Override
    ValueFactory createValueFactory(List<Group> groups) {
        ValueFactoryFishbaseCache valueFactoryFishbaseCache = new ValueFactoryFishbaseCache();
        valueFactoryFishbaseCache.setGroups(groups);
        return valueFactoryFishbaseCache;
    }
}
