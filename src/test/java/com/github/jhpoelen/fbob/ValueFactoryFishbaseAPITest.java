package com.github.jhpoelen.fbob;

import java.util.List;

public class ValueFactoryFishbaseAPITest extends ValueFactoryFishbaseTestBase {

    @Override
    ValueFactory createValueFactory(List<Group> groups) {
        return new ValueFactoryFishbaseAPI();
    }
}
