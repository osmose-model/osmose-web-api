package fr.ird.osmose.web.api;

import fr.ird.osmose.web.api.domain.Group;

import java.util.List;

public class ValueFactoryFishbaseAPITest extends ValueFactoryFishbaseTestBase {

    @Override
    ValueFactory createValueFactory(List<Group> groups) {
        return new ValueFactoryFishbaseAPI();
    }
}
