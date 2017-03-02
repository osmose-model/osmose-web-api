package com.github.jhpoelen.fbob;

public class ValueFactoryFishbaseCacheTest extends ValueFactoryFishbaseTestBase {

    ValueFactory createValueFactory() {
        return new ValueFactoryFishbaseCache();
    }

}
