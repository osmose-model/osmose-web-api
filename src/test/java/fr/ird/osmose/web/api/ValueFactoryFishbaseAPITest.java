package fr.ird.osmose.web.api;

import java.util.List;

public class ValueFactoryFishbaseAPITest extends ValueFactoryFishbaseTestBase {

    @Override
    ValueFactory createValueFactory(List<Group> groups) {
        return new ValueFactoryFishbaseAPI();
    }
}
