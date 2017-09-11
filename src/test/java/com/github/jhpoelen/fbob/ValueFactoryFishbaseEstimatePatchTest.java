package com.github.jhpoelen.fbob;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ValueFactoryFishbaseEstimatePatchTest {

    @Test
    public void predPreyRatio() {
        Group group = new Group("someGroupName");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Collections.singletonList(kingMackerel));

        ValueFactoryFishbaseCache factory = new ValueFactoryFishbaseCache();
        factory.setCacheVersion("v0.2.1-patch");
        factory.setGroups(Collections.singletonList(group));

        assertThat(factory.groupValueFor("predation.predPrey.sizeRatio.max.sp", group), Is.is(not(nullValue())));
        assertThat(factory.groupValueFor("predation.predPrey.sizeRatio.min.sp", group), Is.is(not(nullValue())));

        ValueFactoryFishbaseCache factoryUnpatched = new ValueFactoryFishbaseCache();
        factoryUnpatched.setGroups(Collections.singletonList(group));

        assertThat(factoryUnpatched.groupValueFor("predation.predPrey.sizeRatio.max.sp", group), Is.is(nullValue()));
        assertThat(factoryUnpatched.groupValueFor("predation.predPrey.sizeRatio.min.sp", group), Is.is(nullValue()));
    }
}
