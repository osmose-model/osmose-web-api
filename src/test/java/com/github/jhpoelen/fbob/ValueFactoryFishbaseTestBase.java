package com.github.jhpoelen.fbob;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public abstract class ValueFactoryFishbaseTestBase {

    abstract ValueFactory createValueFactory();

    @Test
    public void knownTrait() {
        ValueFactory valueFactory = createValueFactory();
        Group group = new Group("someGroupName");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Collections.singletonList(kingMackerel));
        assertThat(Double.parseDouble(valueFactory.groupValueFor("species.lifespan.sp", group)), is(14.0d));
        assertThat(valueFactory.groupValueFor("species.sexratio.sp", group), is(nullValue()));
        assertThat(valueFactory.groupValueFor("nonexisting.trait.sp", group), is(nullValue()));
    }


    @Test
    public void knownUnknownTwoSpecies() {
        ValueFactory valueFactory = createValueFactory();
        Group group = new Group("knownUnknown");
        Taxon taxonLifespanUnknown = new Taxon("Seriola dumerili");
        taxonLifespanUnknown.setUrl("http://fishbase.org/summary/1005");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        Taxon taxonLifespanKnown = kingMackerel;
        group.setTaxa(Arrays.asList(taxonLifespanUnknown, taxonLifespanKnown));
        assertThat(Double.parseDouble(valueFactory.groupValueFor("species.lifespan.sp", group)), is(14.0d));
    }

    @Test
    public void knownTraitTwoSpeciesPickFirst() {
        ValueFactory valueFactory = createValueFactory();
        Group group = new Group("knownUnknown");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Arrays.asList(atlanticCod(), kingMackerel));
        assertThat(Double.parseDouble(valueFactory.groupValueFor("species.lifespan.sp", group)), is(25.0d));
    }

    private Taxon atlanticCod() {
        Taxon atlanticCod = new Taxon("Gadus morhua");
        atlanticCod.setUrl("http://fishbase.org/summary/69");
        return atlanticCod;
    }

    @Test
    public void knownTraitTwoSpeciesPickFirstFlipped() {
        ValueFactory valueFactory = createValueFactory();
        Group group = new Group("knownUnknown");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Arrays.asList(kingMackerel, atlanticCod()));
        assertThat(Double.parseDouble(valueFactory.groupValueFor("species.lifespan.sp", group)), is(14.0d));
    }

}