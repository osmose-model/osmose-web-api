package fr.ird.osmose.web.api;

import fr.ird.osmose.web.api.domain.Group;

interface ValueFactory {
    String groupValueFor(String name, Group group);
}
