package com.github.jhpoelen.fbob;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValueFactoryFishbaseCacheTest extends ValueFactoryFishbaseTestBase {

    @Override
    ValueFactory createValueFactory(List<Group> groups) {
        ValueFactoryFishbaseCache valueFactoryFishbaseCache = new ValueFactoryFishbaseCache();
        valueFactoryFishbaseCache.setGroups(groups);
        return valueFactoryFishbaseCache;
    }

    @Test
    public void knownTraitValue() {
        Group group = new Group("medianGroup");
        Taxon redSnapper = new Taxon("Lutjanus campechanus");
        redSnapper.setUrl("http://fishbase.org/summary/1423");
        group.setTaxa(Arrays.asList(redSnapper));
        String name = "species.lInf.sp";
        ValueFactory valueFactory = createValueFactory(Arrays.asList(group));
        assertThat(Double.parseDouble(valueFactory.groupValueFor(name, group)), is(93.55d));
    }

}
