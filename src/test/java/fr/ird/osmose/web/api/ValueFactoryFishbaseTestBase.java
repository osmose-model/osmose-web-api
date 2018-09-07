package fr.ird.osmose.web.api;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public abstract class ValueFactoryFishbaseTestBase {

    abstract ValueFactory createValueFactory(List<Group> groups);

    @Test
    public void knownTrait() {
        String name = "species.lifespan.sp";
        String name1 = "species.sexratio.sp";
        String name2 = "nonexisting.trait.sp";
        Group group = new Group("someGroupName");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Collections.singletonList(kingMackerel));

        ValueFactory valueFactory = createValueFactory(Collections.singletonList(group));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        assertThat(Double.parseDouble(valueFactory.groupValueFor(name, group)), is(14.0d));
        assertThat(valueFactory.groupValueFor(name1, group), is(nullValue()));
        assertThat(valueFactory.groupValueFor(name2, group), is(nullValue()));
        stopWatch.stop();
        System.err.println("lookup took [" + stopWatch.getTime() + "] ms");
    }


    @Test
    public void knownUnknownTwoSpecies() {
        Group group = new Group("knownUnknown");
        Taxon taxonLifespanUnknown = new Taxon("Species unknownens");
        taxonLifespanUnknown.setUrl("http://fishbase.org/summary/666666");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Arrays.asList(taxonLifespanUnknown, kingMackerel));
        String name = "species.lifespan.sp";
        ValueFactory valueFactory = createValueFactory(Arrays.asList(group));
        assertThat(Double.parseDouble(valueFactory.groupValueFor(name, group)), is(14.0d));
    }

    @Test
    public void knownTraitTwoSpeciesPickFirst() {
        Group group = new Group("knownUnknown");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Arrays.asList(atlanticCod(), kingMackerel));
        String name = "species.lifespan.sp";
        ValueFactory valueFactory = createValueFactory(Arrays.asList(group));

        assertThat(Double.parseDouble(valueFactory.groupValueFor(name, group)), is(25.0d));
    }

    private Taxon atlanticCod() {
        Taxon atlanticCod = new Taxon("Gadus morhua");
        atlanticCod.setUrl("http://fishbase.org/summary/69");
        return atlanticCod;
    }

    @Test
    public void knownTraitTwoSpeciesPickFirstFlipped() {
        Group group = new Group("knownUnknown");
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        group.setTaxa(Arrays.asList(kingMackerel, atlanticCod()));
        String name = "species.lifespan.sp";
        ValueFactory valueFactory = createValueFactory(Arrays.asList(group));
        assertThat(Double.parseDouble(valueFactory.groupValueFor(name, group)), is(14.0d));
    }

}