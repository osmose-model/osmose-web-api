package fr.ird.osmose.web.api;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ValueFactoryMappingDefaultsForGroupTest {

    @Test
    public void planktonDefaults() {
        Group groupPhytoplankton = new Group("phytoplankton", GroupType.BACKGROUND,
                Collections.singletonList(new Taxon("some plankton")));
        ValueFactory valueFactory = new ValueFactoryMappingDefaultsForGroup("fishbase-mapping-group-test.csv", new Group("phytoplankton", GroupType.BACKGROUND));

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
    public void planktonNonDefaults() {
        Group groupSelected = new Group("zooplankton", GroupType.BACKGROUND,
                Collections.singletonList(new Taxon("some plankton")));
        ValueFactory valueFactory = new ValueFactoryMappingDefaultsForGroup("fishbase-mapping-group-test.csv", groupSelected);

        String valueMin = valueFactory.groupValueFor("plankton.size.min.plk",
                groupSelected);
        String valueMax = valueFactory.groupValueFor("plankton.size.max.plk",
                groupSelected);
        String trophicLevel = valueFactory.groupValueFor("plankton.TL.plk",
                groupSelected);

        assertThat(valueMin, Is.is("0.0002"));
        assertThat(valueMax, Is.is("0.02"));
        assertThat(trophicLevel, Is.is("1"));
    }

    @Test
    public void nonPlanktonDefaults() {
        Group groupPhytoplankton = new Group("notPhytoplankton", GroupType.BACKGROUND,
                Collections.singletonList(new Taxon("some plankton")));
        ValueFactory valueFactory = new ValueFactoryMappingDefaultsForGroup("fishbase-mapping-group-test.csv", new Group("phytoplankton", GroupType.BACKGROUND));

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