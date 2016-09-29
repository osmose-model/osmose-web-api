package com.github.jhpoelen.fbob;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValueFactoryDefaultTest {

    @Test
    public void knownTrait() {
        ValueFactory valueFactory = new ValueFactoryDefault();
        Group group = new Group("someGroupName");
        group.setTaxa(Collections.singletonList(TestUtil.kingMackerel()));
        assertThat(valueFactory.groupValueFor("species.lifespan.sp", group), is("0"));
        assertThat(valueFactory.groupValueFor("species.sexratio.sp", group), is("0.0"));
        assertThat(valueFactory.groupValueFor("nonexisting.trait.sp", group), is(nullValue()));
    }

}
