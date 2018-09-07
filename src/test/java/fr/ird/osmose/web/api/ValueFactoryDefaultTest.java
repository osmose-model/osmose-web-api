package fr.ird.osmose.web.api;

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
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Collections.singletonList(kingMackerel));
        assertThat(valueFactory.groupValueFor("species.lifespan.sp", group), is(nullValue()));
        assertThat(valueFactory.groupValueFor("species.sexratio.sp", group), is("0.50"));
        assertThat(valueFactory.groupValueFor("nonexisting.trait.sp", group), is(nullValue()));
    }

}
