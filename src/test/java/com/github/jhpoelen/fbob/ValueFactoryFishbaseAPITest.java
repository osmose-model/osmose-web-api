package com.github.jhpoelen.fbob;

public class ValueFactoryFishbaseAPITest extends ValueFactoryFishbaseTestBase {

    ValueFactory createValueFactory() {
        return new ValueFactoryFishbaseAPI();
    }

}
