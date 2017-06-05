package com.github.jhpoelen.fbob;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ValueFactoryMappingDefaultsPhytoplanktonTest {

    @Test
    public void planktonDefaults() {
        Group groupPhytoplankton = new Group("phytoplankton", GroupType.BACKGROUND,
                Collections.singletonList(new Taxon("some plankton")));
        ValueFactory valueFactory = new ValueFactoryMappingDefaultsPhytoplankton();

        String valueMin = valueFactory.groupValueFor("plankton.size.min.plk",
                groupPhytoplankton);
        String valueMax = valueFactory.groupValueFor("plankton.size.max.plk",
                groupPhytoplankton);
        String trophicLevel = valueFactory.groupValueFor("plankton.TL.plk",
                groupPhytoplankton);

        assertThat(valueMin, Is.is("0.0002"));
        assertThat(valueMax, Is.is("0.02"));
        assertThat(trophicLevel, Is.is("1"));
    }

    @Test
    public void nonPlanktonDefaults() {
        Group groupPhytoplankton = new Group("notPhytoplankton", GroupType.BACKGROUND,
                Collections.singletonList(new Taxon("some plankton")));
        ValueFactory valueFactory = new ValueFactoryMappingDefaultsPhytoplankton();

        String valueMin = valueFactory.groupValueFor("plankton.size.min.plk",
                groupPhytoplankton);
        String valueMax = valueFactory.groupValueFor("plankton.size.max.plk",
                groupPhytoplankton);
        String trophicLevel = valueFactory.groupValueFor("plankton.TL.plk",
                groupPhytoplankton);

        assertThat(valueMin, Is.is(nullValue()));
        assertThat(valueMax, Is.is(nullValue()));
        assertThat(trophicLevel, Is.is(nullValue()));
    }

}