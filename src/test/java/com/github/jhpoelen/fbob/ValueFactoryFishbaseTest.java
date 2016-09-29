package com.github.jhpoelen.fbob;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValueFactoryFishbaseTest {

    @Test
    public void knownTrait() {
        ValueFactory valueFactory = new ValueFactoryFishbase();
        Group group = new Group("someGroupName");
        group.setTaxa(Collections.singletonList(TestUtil.kingMackerel()));
        assertThat(valueFactory.groupValueFor("species.lifespan.sp", group), is("14.0"));
        assertThat(valueFactory.groupValueFor("species.sexratio.sp", group), is(nullValue()));
        assertThat(valueFactory.groupValueFor("nonexisting.trait.sp", group), is(nullValue()));
    }

    @Test
    public void knownUnknownTwoSpecies() {
        ValueFactory valueFactory = new ValueFactoryFishbase();
        Group group = new Group("knownUnknown");
        Taxon taxonLifespanUnknown = new Taxon("Seriola dumerili");
        taxonLifespanUnknown.setUrl("http://fishbase.org/summary/1005");
        Taxon taxonLifespanKnown = TestUtil.kingMackerel();
        group.setTaxa(Arrays.asList(taxonLifespanUnknown, taxonLifespanKnown));
        assertThat(valueFactory.groupValueFor("species.lifespan.sp", group), is("14.0"));
    }

    @Test
    public void knownTraitTwoSpeciesPickFirst() {
        ValueFactory valueFactory = new ValueFactoryFishbase();
        Group group = new Group("knownUnknown");
        group.setTaxa(Arrays.asList(atlanticCod(), TestUtil.kingMackerel()));
        assertThat(valueFactory.groupValueFor("species.lifespan.sp", group), is("25.0"));
    }

    private Taxon atlanticCod() {
        Taxon atlanticCod = new Taxon("Gadus morhua");
        atlanticCod.setUrl("http://fishbase.org/summary/69");
        return atlanticCod;
    }

    @Test
    public void knownTraitTwoSpeciesPickFirstFlipped() {
        ValueFactory valueFactory = new ValueFactoryFishbase();
        Group group = new Group("knownUnknown");
        group.setTaxa(Arrays.asList(TestUtil.kingMackerel(), atlanticCod()));
        assertThat(valueFactory.groupValueFor("species.lifespan.sp", group), is("14.0"));
    }

}
