package com.github.jhpoelen.fbob;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;
import static org.junit.internal.matchers.StringContains.containsString;

public class ConfigServiceUtilTest {
    private StreamFactory factory;

    @Before
    public void init() {
        factory = new StreamFactoryMemory();
    }

    @Test
    public void functionalParams() {
        new TreeMap<String, String>() {{
            put("mortality.starvation.rate.max.sp", "osm_param-starvation.csv");
            put("species.name.sp", "osm_param-species.csv");
            put("species.egg.size.sp", "osm_param-species.csv");
            put("species.egg.weight.sp", "osm_param-species.csv");
            put("species.K.sp", "osm_param-species.csv");
            put("species.length2weight.allometric.power.sp", "osm_param-species.csv");
            put("species.length2weight.condition.factor.sp", "osm_param-species.csv");
            put("species.lifespan.sp", "osm_param-species.csv");
            put("species.lInf.sp", "osm_param-species.csv");
            put("species.maturity.size.sp", "osm_param-species.csv");
            put("species.relativefecundity.sp", "osm_param-species.csv");
            put("species.sexratio.sp", "osm_param-species.csv");
            put("species.t0.sp", "osm_param-species.csv");
            put("species.vonbertalanffy.threshold.age.sp", "osm_param-species.csv");
            put("species.length2weight.fl.sp", "osm_param-species.csv");

            put("predation.accessibility.stage.threshold.sp", "osm_param-predation.csv");
            put("predation.efficiency.critical.sp", "osm_param-predation.csv");
            put("predation.ingestion.rate.max.sp", "osm_param-predation.csv");
            put("predation.predPrey.sizeRatio.max.sp", "osm_param-predation.csv");
            put("predation.predPrey.sizeRatio.min.sp", "osm_param-predation.csv");
            put("predation.predPrey.stage.threshold.sp", "osm_param-predation.csv");

            put("output.cutoff.age.sp", "osm_param-output.csv");
            put("output.diet.stage.threshold.sp", "osm_param-output.csv");

            put("mortality.natural.larva.rate.sp", "osm_param-natural-mortality");
            put("mortality.natural.rate.sp", "osm_param-natural-mortality");

            put("movement.distribution.method.sp", "osm_param-movement.csv");

            put("population.seeding.biomass.sp", "osm_param-init-pop.csv");

            put("mortality.fishing.rate.sp", "osm_param-fishing.csv");
            put("mortality.fishing.recruitment.age.sp", "osm_param-fishing.csv");
            put("mortality.fishing.recruitment.size.sp", "osm_param-fishing.csv");
            put("mortality.fishing.season.distrib.file.", "osm_param-fishing.csv");

            put("simulation.nspecies", "osm_all-parameters.csv");

        }};
    }


    @Test
    public void species() throws IOException {
        List<Group> groups = Arrays.asList(new Group("groupOne"), new Group("groupTwo"));
        StreamFactoryMemory factory = getTestFactory();

        ConfigUtil.generateSpecies(groups, factory, getTestValueFactory());

        String asExpected = "species.name.sp0;groupOne\nspecies.name.sp1;groupTwo\nspecies.egg.size.sp0;0.1\nspecies.egg.size.sp1;0.1\nspecies.egg.weight.sp0;0.0005386\nspecies.egg.weight.sp1;0.0005386\nspecies.K.sp0;0.0\nspecies.K.sp1;0.0\nspecies.length2weight.allometric.power.sp0;0.0\nspecies.length2weight.allometric.power.sp1;0.0\nspecies.length2weight.condition.factor.sp0;0.0\nspecies.length2weight.condition.factor.sp1;0.0\nspecies.lifespan.sp0;0\nspecies.lifespan.sp1;0\nspecies.lInf.sp0;0.0\nspecies.lInf.sp1;0.0\nspecies.maturity.size.sp0;0.0\nspecies.maturity.size.sp1;0.0\nspecies.relativefecundity.sp0;0\nspecies.relativefecundity.sp1;0\nspecies.sexratio.sp0;0.0\nspecies.sexratio.sp1;0.0\nspecies.t0.sp0;0.0\nspecies.t0.sp1;0.0\nspecies.vonbertalanffy.threshold.age.sp0;0.0\nspecies.vonbertalanffy.threshold.age.sp1;0.0\nspecies.length2weight.fl.sp0;false\nspecies.length2weight.fl.sp1;false";
        assertThat(getTestFactory().stringOutputFor("osm_param-species.csv"), is(asExpected));
    }

    @Test
    public void ltl() throws IOException {
        List<Group> groups = Arrays.asList(new Group("groupOne"), new Group("groupTwo"));
        StreamFactoryMemory factory = getTestFactory();

        ConfigUtil.generateLtlForGroups(groups, factory, getTestValueFactory());

        String asExpected = "plankton.name.plk0;groupOne\n" +
                "plankton.name.plk1;groupTwo\n" +
                "plankton.accessibility2fish.plk0;0.2237\n" +
                "plankton.accessibility2fish.plk1;0.2237\n" +
                "plankton.conversion2tons.plk0;1\n" +
                "plankton.conversion2tons.plk1;1\n" +
                "plankton.size.max.plk0;0.002\n" +
                "plankton.size.max.plk1;0.002\n" +
                "plankton.size.min.plk0;0.0002\n" +
                "plankton.size.min.plk1;0.0002\n" +
                "plankton.TL.plk0;1\n" +
                "plankton.TL.plk1;1";
        assertThat(getTestFactory().stringOutputFor("osm_param-ltl.csv"), is(asExpected));
    }

    public ValueFactory getTestValueFactory() {
        return ConfigUtil.getDefaultValueFactory();
    }

    @Test
    public void movementMapAgeRanges() throws IOException {
        final List<Group> species = Arrays.asList(new Group("speciesOne"), new Group("speciesTwo"));
        ConfigUtil.generateMaps(species, getTestFactory(), getTestValueFactory());

        assertThat(getTestFactory().stringOutputFor("grid-mask.csv"), is(notNullValue()));
        assertThat(getTestFactory().stringOutputFor("osm_param-movement.csv"), is(notNullValue()));
        assertThat(getTestFactory().stringOutputFor("maps/speciesOne0.csv"), is(notNullValue()));
        assertThat(getTestFactory().stringOutputFor("maps/speciesTwo1.csv"), is(notNullValue()));
    }

    @Test
    public void fishingSeasonality() throws IOException {
        List<Group> groupNames = new ArrayList<>();
        groupNames.add(new Group("groupOne"));
        groupNames.add(new Group("groupTwo"));

        ConfigUtil.generateFishingParametersFor(groupNames, factory);

        String expectedFishingParams = "\nmortality.fishing.rate.sp0;0.0\n" +
                "mortality.fishing.rate.sp1;0.0\n" +
                "mortality.fishing.recruitment.age.sp0;0.0\n" +
                "mortality.fishing.recruitment.age.sp1;0.0\n" +
                "mortality.fishing.recruitment.size.sp0;0.0\n" +
                "mortality.fishing.recruitment.size.sp1;0.0\n" +
                "mortality.fishing.season.distrib.file.sp0;fishing/fishing-seasonality-groupOne.csv\n" +
                "mortality.fishing.season.distrib.file.sp1;fishing/fishing-seasonality-groupTwo.csv";

        String actual = (getTestFactory()).stringOutputFor("osm_param-fishing.csv");
        assertEquals(expectedFishingParams, actual);
        assertThat(actual, containsString("fishing/fishing-seasonality-groupOne.csv"));
        String expectedFishingSeasonality = "Time;Season\n" +
                "0.0;0.0\n" +
                "0.083333336;0.0\n" +
                "0.16666667;0.0\n" +
                "0.25;0.0\n" +
                "0.33333334;0.0\n" +
                "0.41666666;0.0\n" +
                "0.5;0.0\n" +
                "0.5833333;0.0\n" +
                "0.6666667;0.0\n" +
                "0.75;0.0\n" +
                "0.8333333;0.0\n" +
                "0.9166667;0.0";
        assertEquals(expectedFishingSeasonality, (getTestFactory()).stringOutputFor("fishing/fishing-seasonality-groupOne.csv"));
        assertEquals(expectedFishingSeasonality, (getTestFactory()).stringOutputFor("fishing/fishing-seasonality-groupTwo.csv"));
    }


    @Test
    public void output() throws IOException {
        List<Group> groupNames = Arrays.asList(new Group("groupNameOne"), new Group("groupNameTwo"));

        ConfigUtil.generateOutputParamsFor(groupNames, factory, getTestValueFactory());

        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), containsString("output.diet.stage.threshold.sp1"));
        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), not(containsString("output.diet.stage.threshold.sp2")));
        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), containsString("output.cutoff.age.sp1"));
        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), not(containsString("output.cutoff.age.sp2")));
    }

    @Test
    public void naturalMortality() throws IOException {
        List<Group> groupNames = Arrays.asList(new Group("groupName1"), new Group("groupName2"));

        ConfigUtil.generateNaturalMortalityFor(groupNames, factory, getTestValueFactory());

        String expected = "mortality.natural.larva.rate.file;null" +
                "\nmortality.natural.larva.rate.sp0;0.0" +
                "\nmortality.natural.larva.rate.sp1;0.0" +
                "\nmortality.natural.rate.file;null" +
                "\nmortality.natural.rate.sp0;0.0" +
                "\nmortality.natural.rate.sp1;0.0";
        assertThat(getTestFactory().stringOutputFor("osm_param-natural-mortality.csv"), is(expected));
    }

    private List<Group> toGroups(List<String> groupNames) {
        return toGroupStream(groupNames).collect(Collectors.toList());
    }

    private Stream<Group> toGroupStream(List<String> groupNames) {
        return groupNames.stream().map(Group::new);
    }

    @Test
    public void predation() throws IOException {
        List<Group> groupNames = toGroups(Arrays.asList("groupNameOne", "groupNameTwo"));
        StreamFactoryMemory factory = getTestFactory();

        ConfigUtil.generatePredationFor(groupNames, factory, getTestValueFactory());
        String expectedPredationParams = "predation.accessibility.file;predation-accessibility.csv" +
                "\npredation.accessibility.stage.structure;age" +
                "\npredation.accessibility.stage.threshold.sp0;0.0" +
                "\npredation.accessibility.stage.threshold.sp1;0.0" +
                "\npredation.efficiency.critical.sp0;0.57" +
                "\npredation.efficiency.critical.sp1;0.57" +
                "\npredation.ingestion.rate.max.sp0;3.5" +
                "\npredation.ingestion.rate.max.sp1;3.5" +
                "\npredation.predPrey.sizeRatio.max.sp0;0.0;0.0" +
                "\npredation.predPrey.sizeRatio.max.sp1;0.0;0.0" +
                "\npredation.predPrey.sizeRatio.min.sp0;0.0;0.0" +
                "\npredation.predPrey.sizeRatio.min.sp1;0.0;0.0" +
                "\npredation.predPrey.stage.structure;size" +
                "\npredation.predPrey.stage.threshold.sp0;0.0" +
                "\npredation.predPrey.stage.threshold.sp1;0.0";
        assertThat(getTestFactory().stringOutputFor("osm_param-predation.csv"), is(expectedPredationParams));
    }

    @Test
    public void predationAccessibility() throws IOException {
        List<String> groupNames = new ArrayList<String>() {{
            add("groupNameOne");
            add("groupNameTwo");
        }};

        List<String> implicitGroupNames = new ArrayList<String>() {
            {
                add("Small_phytoplankton");
                add("Diatoms");
                add("Microzooplankton");
                add("Mesozooplankton");
                add("Meiofauna");
                add("Small_infauna");
                add("Small_mobile_epifauna");
                add("Bivalves");
                add("Echinoderms_and_large_gastropods");
            }
        };

        ConfigUtil.generatePredationAccessibilityFor(toGroups(groupNames), toGroups(implicitGroupNames), factory);
        // including the "implicit" functional groups
        String expectedPredationAccessibility = "v Prey / Predator >;groupNameOne < 0.0 year;groupNameOne > 0.0 year;groupNameTwo < 0.0 year;groupNameTwo > 0.0 year;Small_phytoplankton;Diatoms;Microzooplankton;Mesozooplankton;Meiofauna;Small_infauna;Small_mobile_epifauna;Bivalves;Echinoderms_and_large_gastropods\n" +
                "groupNameOne < 0.0 year;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "groupNameOne > 0.0 year;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "groupNameTwo < 0.0 year;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "groupNameTwo > 0.0 year;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Small_phytoplankton;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Diatoms;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Microzooplankton;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Mesozooplankton;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Meiofauna;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Small_infauna;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Small_mobile_epifauna;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Bivalves;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0\n" +
                "Echinoderms_and_large_gastropods;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0";


        assertEquals(expectedPredationAccessibility, (getTestFactory()).stringOutputFor("predation-accessibility.csv"));
    }

    @Test
    public void csvEscape() {
        assertThat(StringEscapeUtils.escapeCsv("123.2"), is("123.2"));
        assertThat(StringEscapeUtils.escapeCsv("\"hello\" she said"), is("\"\"\"hello\"\" she said\""));
    }

    @Test
    public void starvation() throws IOException {
        List<String> groupNames = Arrays.asList("gOne", "gTwo");
        ConfigUtil.generateStarvationFor(toGroups(groupNames), factory);

        String expectedStarvation = "mortality.starvation.rate.max.sp0;0.3\n" +
                "mortality.starvation.rate.max.sp1;0.3";
        assertThat(getTestFactory().stringOutputFor("osm_param-starvation.csv"), is(expectedStarvation));
    }

    private StreamFactoryMemory getTestFactory() {
        return (StreamFactoryMemory) factory;
    }


    @Test
    public void reproductionSeasonTemplates() throws IOException {
        List<String> groupNames = new ArrayList<String>() {{
            add("groupNameOne");
            add("groupNameTwo");
        }};


        ConfigUtil.generateSeasonalReproductionFor(toGroups(groupNames), factory);

        assertThat((getTestFactory()).stringOutputFor("osm_param-reproduction.csv"), is("reproduction.season.file.sp0;reproduction-seasonality-sp0.csv\nreproduction.season.file.sp1;reproduction-seasonality-sp1.csv"));
        String prefix = "Time (year);";
        String suffix = "\n0.0;0.0\n0.083333336;0.0\n0.16666667;0.0\n0.25;0.0\n0.33333334;0.0\n0.41666666;0.0\n0.5;0.0\n0.5833333;0.0\n0.6666667;0.0\n0.75;0.0\n0.8333333;0.0\n0.9166667;0.0";
        assertEquals((getTestFactory()).stringOutputFor("reproduction-seasonality-sp0.csv"), (prefix + "groupNameOne" + suffix));
        assertEquals((getTestFactory()).stringOutputFor("reproduction-seasonality-sp1.csv"), (prefix + "groupNameTwo" + suffix));
    }

    @Test
    public void generateConfigFor() throws IOException {
        List<String> groupNames = Arrays.asList("speciesA", "speciesB", "speciesC");

        ConfigUtil.generateConfigFor(12, toGroups(groupNames), toGroups(new ArrayList<String>() {
            {
                add("planktonA");
                add("planktonB");
                add("planktonC");
                add("planktonD");
            }
        }), getTestFactory(), ConfigUtil.getDefaultValueFactory());

        assertThat(getTestFactory().streamMap.keySet(), hasItems("osm_param-species.csv", "osm_param-starvation.csv"));
    }

    @Test
    public void generateAllParametersFor() throws IOException {
        List<String> groupNames = Arrays.asList("speciesA", "speciesB", "speciesC");
        List<String> groupNamesBackground = Arrays.asList("planktonA", "planktopB", "planktonC");

        ConfigUtil.generateAllParametersFor(11, toGroups(groupNames), toGroups(groupNamesBackground), getTestFactory());

        assertThat(getTestFactory().stringOutputFor("osm_all-parameters.csv"), is(
                "\nsimulation.time.ndtPerYear;11\n" +
                        "simulation.time.nyear;134\n" +
                        "simulation.restart.file;null\n" +
                        "output.restart.recordfrequency.ndt;60\n" +
                        "output.restart.spinup;114\n" +
                        "simulation.nschool;20\n" +
                        "simulation.ncpu;8\n" +
                        "simulation.nplankton;3\n" +
                        "simulation.nsimulation;10\n" +
                        "simulation.nspecies;3\n" +
                        "mortality.algorithm;stochastic\n" +
                        "mortality.subdt;10\n" +
                        "osmose.configuration.output;osm_param-output.csv\n" +
                        "osmose.configuration.movement;osm_param-movement.csv\n" +
                        "osmose.configuration.mpa;osm_param-mpa.csv\n" +
                        "osmose.configuration.mortality.fishing;osm_param-fishing.csv\n" +
                        "osmose.configuration.mortality.natural;osm_param-natural-mortality.csv\n" +
                        "osmose.configuration.mortality.predation;osm_param-predation.csv\n" +
                        "osmose.configuration.mortality.starvation;osm_param-starvation.csv\n" +
                        "osmose.configuration.reproduction;osm_param-reproduction.csv\n" +
                        "osmose.configuration.species;osm_param-species.csv\n" +
                        "osmose.configuration.plankton;osm_param-ltl.csv\n" +
                        "osmose.configuration.grid;osm_param-grid.csv\n" +
                        "osmose.configuration.initialization;osm_param-init-pop.csv"));
    }

    private class StreamFactoryMemory implements StreamFactory {
        private final Map<String, ByteArrayOutputStream> streamMap = new TreeMap<String, ByteArrayOutputStream>();

        @Override
        public OutputStream outputStreamFor(String name) throws IOException {
            if (streamMap.containsKey(name)) {
                throw new IOException("name: [" + name + "] already exists");
            }
            streamMap.put(name, new ByteArrayOutputStream());
            return streamMap.get(name);
        }

        public String stringOutputFor(String name) throws UnsupportedEncodingException {
            return streamMap.containsKey(name) ? streamMap.get(name).toString("UTF-8") : null;
        }
    }
}
