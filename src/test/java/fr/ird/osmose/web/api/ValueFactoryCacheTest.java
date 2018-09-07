package fr.ird.osmose.web.api;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValueFactoryCacheTest extends ValueFactoryFishbaseTestBase {

    @Override
    ValueFactory createValueFactory(List<Group> groups) {
        ValueFactoryCache valueFactoryCache = new ValueFactoryCache();
        valueFactoryCache.setGroups(groups);
        return valueFactoryCache;
    }

    @Test
    public void knownTraitValue() {
        Group group = new Group("medianGroup");
        Taxon redSnapper = new Taxon("Lutjanus campechanus");
        redSnapper.setUrl("http://fishbase.org/summary/1423");
        group.setTaxa(Collections.singletonList(redSnapper));
        String name = "species.lInf.sp";
        ValueFactory valueFactory = createValueFactory(Collections.singletonList(group));
        assertThat(Double.parseDouble(valueFactory.groupValueFor(name, group)), is(93.55d));
    }

    @Test
    public void knownTraitValue2() {
        Group group = TestGroups.getGroupWithBlueCrabOnly();

        ValueFactoryCache valueFactory = new ValueFactoryCache(ValueFactoryCache.Database.sealifebase, "v0.2.1");
        valueFactory.setGroups(Collections.singletonList(group));

        String name = "species.lInf.sp";
        assertThat(Double.parseDouble(valueFactory.groupValueFor(name, group)), is(19.61d));
    }

}
