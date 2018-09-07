package fr.ird.osmose.web.api;

import java.util.Collections;

public final class TestGroups {
    public static Group getGroupWithBlueCrabOnly() {
        Group group = new Group("medianGroup");
        Taxon blueCrab = new Taxon("Callinectes sapidus");
        blueCrab.setUrl("http://sealifebase.org/summary/26794");
        group.setTaxa(Collections.singletonList(blueCrab));
        return group;
    }
}
