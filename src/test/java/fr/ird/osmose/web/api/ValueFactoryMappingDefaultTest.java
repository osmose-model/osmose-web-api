package fr.ird.osmose.web.api;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValueFactoryMappingDefaultTest {

    @Test
    public void knownTrait() {
        ValueFactory valueFactory = new ValueFactoryMappingDefault("test-mapping.csv");
        Group group = new Group("someGroupName");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Collections.singletonList(kingMackerel));
        assertThat(valueFactory.groupValueFor("trait1", group), is("0"));
        assertThat(valueFactory.groupValueFor("trait2", group), is("0.5"));
        assertThat(valueFactory.groupValueFor("non.existence.trait1", group), is(nullValue()));
        assertThat(valueFactory.groupValueFor("trait3", group), is(nullValue()));
        assertThat(valueFactory.groupValueFor("trait3 ", group), is(nullValue()));
    }

}
