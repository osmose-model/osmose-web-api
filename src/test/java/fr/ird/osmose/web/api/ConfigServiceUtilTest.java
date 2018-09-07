package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public void speciesDefaultsOnly() throws IOException {
        List<Group> groups = Arrays.asList(new Group("groupOne"), new Group("groupTwo"));
        StreamFactoryMemory factory = getTestFactory();

        ConfigUtil.generateSpecies(groups, factory, new ValueFactoryNA(getTestValueFactory()));

        String asExpected = "species.name.sp0;groupOne" +
                "\nspecies.name.sp1;groupTwo" +
                "\nspecies.egg.size.sp0;0.1" +
                "\nspecies.egg.size.sp1;0.1" +
                "\nspecies.egg.weight.sp0;NA" +
                "\nspecies.egg.weight.sp1;NA" +
                "\nspecies.K.sp0;NA\nspecies.K.sp1;NA" +
                "\nspecies.length2weight.allometric.power.sp0;NA" +
                "\nspecies.length2weight.allometric.power.sp1;NA" +
                "\nspecies.length2weight.condition.factor.sp0;NA" +
                "\nspecies.length2weight.condition.factor.sp1;NA" +
                "\nspecies.lifespan.sp0;NA\nspecies.lifespan.sp1;NA" +
                "\nspecies.lInf.sp0;NA\nspecies.lInf.sp1;NA" +
                "\nspecies.maturity.size.sp0;NA" +
                "\nspecies.maturity.size.sp1;NA" +
                "\nspecies.maturity.age.sp0;NA" +
                "\nspecies.maturity.age.sp1;NA" +
                "\nspecies.relativefecundity.sp0;NA" +
                "\nspecies.relativefecundity.sp1;NA" +
                "\nspecies.sexratio.sp0;0.50" +
                "\nspecies.sexratio.sp1;0.50" +
                "\nspecies.t0.sp0;NA" +
                "\nspecies.t0.sp1;NA" +
                "\nspecies.vonbertalanffy.threshold.age.sp0;0.0" +
                "\nspecies.vonbertalanffy.threshold.age.sp1;0.0";
        assertThat(getTestFactory().stringOutputFor("osm_param-species.csv"), is(asExpected));
    }

    @Test
    public void speciesWithNAValues() throws IOException {
        List<Group> groups = Arrays.asList(new Group("groupOne"), new Group("groupTwo"));
        StreamFactoryMemory factory = getTestFactory();

        ValueFactory valueFactoryNull = (name, group) -> null;

        ConfigUtil.generateSpecies(groups, factory, new ValueFactoryNA(valueFactoryNull));

        String asExpected = "species.name.sp0;groupOne\n" +
                "species.name.sp1;groupTwo\n" +
                "species.egg.size.sp0;NA\n" +
                "species.egg.size.sp1;NA\n" +
                "species.egg.weight.sp0;NA\n" +
                "species.egg.weight.sp1;NA\n" +
                "species.K.sp0;NA\n" +
                "species.K.sp1;NA\n" +
                "species.length2weight.allometric.power.sp0;NA\n" +
                "species.length2weight.allometric.power.sp1;NA\n" +
                "species.length2weight.condition.factor.sp0;NA\n" +
                "species.length2weight.condition.factor.sp1;NA\n" +
                "species.lifespan.sp0;NA\n" +
                "species.lifespan.sp1;NA\n" +
                "species.lInf.sp0;NA\n" +
                "species.lInf.sp1;NA\n" +
                "species.maturity.size.sp0;NA\n" +
                "species.maturity.size.sp1;NA\n" +
                "species.maturity.age.sp0;NA\n" +
                "species.maturity.age.sp1;NA\n" +
                "species.relativefecundity.sp0;NA\n" +
                "species.relativefecundity.sp1;NA\n" +
                "species.sexratio.sp0;NA\n" +
                "species.sexratio.sp1;NA\n" +
                "species.t0.sp0;NA\n" +
                "species.t0.sp1;NA\n" +
                "species.vonbertalanffy.threshold.age.sp0;NA\n" +
                "species.vonbertalanffy.threshold.age.sp1;NA";
        assertThat(getTestFactory().stringOutputFor("osm_param-species.csv"), is(asExpected));
    }

    @Test
    public void speciesWithSexRatioValues() throws IOException {
        List<Group> groups = Arrays.asList(new Group("groupOne"), new Group("groupTwo"));
        StreamFactoryMemory factory = getTestFactory();

        ValueFactory valueFactoryNull = (name, group) -> StringUtils.equals(name, "species.sexratio.sp") ? "0.0" : null;

        ConfigUtil.generateSpecies(groups, factory, new ValueFactorySexRatioConstraints(valueFactoryNull, getTestValueFactory()));

        String actual = getTestFactory().stringOutputFor("osm_param-species.csv");
        assertThat(actual, containsString("species.sexratio.sp0;0.50\n"));
        assertThat(actual, containsString("species.sexratio.sp1;0.50\n"));
    }

    @Test
    public void ltl() throws IOException {
        List<Group> groups = Arrays.asList(new Group("groupOne"), new Group("groupTwo"));
        StreamFactoryMemory factory = getTestFactory();

        ConfigUtil.generateLtlForGroups(groups, factory, new ValueFactoryNA(getTestValueFactory()));

        String asExpected = "plankton.name.plk0;groupOne\n" +
                "plankton.name.plk1;groupTwo\n" +
                "plankton.accessibility2fish.plk0;0.0\n" +
                "plankton.accessibility2fish.plk1;0.0\n" +
                "plankton.conversion2tons.plk0;1\n" +
                "plankton.conversion2tons.plk1;1\n" +
                "plankton.size.max.plk0;NA\n" +
                "plankton.size.max.plk1;NA\n" +
                "plankton.size.min.plk0;NA\n" +
                "plankton.size.min.plk1;NA\n" +
                "plankton.TL.plk0;NA\n" +
                "plankton.TL.plk1;NA";
        assertThat(getTestFactory().stringOutputFor("osm_param-ltl.csv"), is(asExpected));
    }

    public ValueFactory getTestValueFactory() {
        return new ValueFactoryDefault();
    }

    @Test
    public void movementMapAgeRanges() throws IOException {
        final List<Group> species = Arrays.asList(new Group("speciesOne"), new Group("speciesTwo"));
        ConfigUtil.generateMaps(species, getTestFactory(), getTestValueFactory());

        assertThat(getTestFactory().stringOutputFor("grid-mask.csv"), is(notNullValue()));
        assertThat(getTestFactory().stringOutputFor("osm_param-movement.csv"), is(notNullValue()));
        assertThat(getTestFactory().stringOutputFor("maps/speciesOne_1.csv"), is(notNullValue()));
        assertThat(getTestFactory().stringOutputFor("maps/speciesTwo_1.csv"), is(notNullValue()));
    }

    @Test
    public void fishingSeasonality() throws IOException {
        List<Group> groupNames = new ArrayList<>();
        groupNames.add(new Group("groupOne"));
        groupNames.add(new Group("groupTwo"));

        ConfigUtil.generateFishingParametersFor(groupNames, factory, 4);

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
                "0.000;\n" +
                "0.250;\n" +
                "0.500;\n" +
                "0.750;";

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
        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), containsString("output.distrib.byAge.min"));
        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), containsString("output.distrib.byAge.max"));
        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), containsString("output.distrib.byAge.incr"));
        assertThat(getTestFactory().stringOutputFor("osm_param-output.csv"), not(containsString("output.meanWeight.byAge.enabled")));
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
        return toGroups(groupNames, GroupType.FOCAL);
    }

    private List<Group> toGroups(List<String> groupNames, GroupType groupType) {
        return toGroupStream(groupNames, groupType).collect(Collectors.toList());
    }

    private Stream<Group> toGroupStream(List<String> groupNames, GroupType type) {
        return groupNames.stream().map(name -> new Group(name, type));
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
                "\npredation.predPrey.sizeRatio.max.sp0;3.5;3.5" +
                "\npredation.predPrey.sizeRatio.max.sp1;3.5;3.5" +
                "\npredation.predPrey.sizeRatio.min.sp0;30.0;30.0" +
                "\npredation.predPrey.sizeRatio.min.sp1;30.0;30.0" +
                "\npredation.predPrey.stage.structure;size" +
                "\npredation.predPrey.stage.threshold.sp0;0.0" +
                "\npredation.predPrey.stage.threshold.sp1;0.0";
        assertThat(getTestFactory().stringOutputFor("osm_param-predation.csv"), is(expectedPredationParams));
    }

    @Test
    public void predationAccessibility() throws IOException {
        List<String> groupsFocal = new ArrayList<String>() {{
            add("groupB");
            add("groupA");
            add("groupC");
        }};

        List<String> groupsBackground = new ArrayList<String>() {{
            add("phytoplankton");
            add("zooplankton");
            add("groupD");
            add("groupE");
        }};

        final Set<String> names = new HashSet<String>();
        ValueFactory valueFactory = (name, group) -> {
            names.add(name);
            String value = "";
            if (StringUtils.equalsIgnoreCase("predation.accessibility.stage.threshold.sp", name)) {
                if (StringUtils.equalsIgnoreCase("groupA", group.getName())) {
                    value = "1.1";
                } else {
                    value = "2.1";
                }
            }
            if (StringUtils.equalsIgnoreCase("ecology.Benthic", name)) {
                value = Arrays.asList("groupA", "groupE").contains(group.getName()) ? "0" : "-1";
            }
            return value;
        };

        ConfigUtil.generatePredationAccessibilityFor(toGroups(groupsFocal), toGroups(groupsBackground, GroupType.BACKGROUND), factory, valueFactory);
        String expectedPredationAccessibility = "v Prey / Predator >;groupB < 2.1 year;groupB > 2.1 year;groupA < 1.1 year;groupA > 1.1 year;groupC < 2.1 year;groupC > 2.1 year\n" +
                "groupB < 2.1 year;0.80;0.80;0.40;0.40;0.80;0.80\n" +
                "groupB > 2.1 year;0.80;0.80;0.40;0.40;0.80;0.80\n" +
                "groupA < 1.1 year;0.40;0.40;0.80;0.80;0.40;0.40\n" +
                "groupA > 1.1 year;0.40;0.40;0.80;0.80;0.40;0.40\n" +
                "groupC < 2.1 year;0.80;0.80;0.40;0.40;0.80;0.80\n" +
                "groupC > 2.1 year;0.80;0.80;0.40;0.40;0.80;0.80\n" +
                "phytoplankton;1.00;1.00;1.00;1.00;1.00;1.00\n" +
                "zooplankton;1.00;1.00;1.00;1.00;1.00;1.00\n" +
                "groupD;0.80;0.80;0.40;0.40;0.80;0.80\n" +
                "groupE;0.40;0.40;0.80;0.80;0.40;0.40";

        assertEquals(expectedPredationAccessibility, (getTestFactory()).stringOutputFor("predation-accessibility.csv"));

        assertThat(names, hasItems("ecology.Benthic"));
        assertThat(names, not(hasItems("ecology.Demersal", "ecology.Pelagic")));
        assertThat(names, not(hasItems("estimate.DepthMin", "estimate.DepthMax")));
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

        ValueFactory valueFactory = (name, group) -> "0.0";

        ConfigUtil.generateSeasonalReproductionFor(toGroups(groupNames), factory, valueFactory, 12);

        assertThat((getTestFactory()).stringOutputFor("osm_param-reproduction.csv"), is("reproduction.season.file.sp0;reproduction-seasonality-sp0.csv\nreproduction.season.file.sp1;reproduction-seasonality-sp1.csv"));
        String prefix = "Time (year);";
        String suffix = "\n0.000;0.083\n" +
                "0.083;0.083\n" +
                "0.167;0.083\n" +
                "0.250;0.083\n" +
                "0.333;0.083\n" +
                "0.417;0.083\n" +
                "0.500;0.083\n" +
                "0.583;0.083\n" +
                "0.667;0.083\n" +
                "0.750;0.083\n" +
                "0.833;0.083\n" +
                "0.917;0.083";
        assertEquals(prefix + "groupNameOne" + suffix, getTestFactory().stringOutputFor("reproduction-seasonality-sp0.csv"));
        assertEquals((prefix + "groupNameTwo" + suffix), getTestFactory().stringOutputFor("reproduction-seasonality-sp1.csv"));
    }

    @Test
    public void reproductionSeasonTemplatesSome() throws IOException {
        List<String> groupNames = new ArrayList<String>() {{
            add("groupNameOne");
            add("groupNameTwo");
        }};

        ValueFactory valueFactory = (name, group) -> "spawning.Aug".equals(name) ? "111" : "0";

        ConfigUtil.generateSeasonalReproductionFor(toGroups(groupNames), factory, valueFactory, 12);

        assertThat((getTestFactory()).stringOutputFor("osm_param-reproduction.csv"), is("reproduction.season.file.sp0;reproduction-seasonality-sp0.csv\nreproduction.season.file.sp1;reproduction-seasonality-sp1.csv"));
        String prefix = "Time (year);";
        String suffix = "\n0.000;0.000\n" +
                "0.083;0.000\n" +
                "0.167;0.000\n" +
                "0.250;0.000\n" +
                "0.333;0.000\n" +
                "0.417;0.000\n" +
                "0.500;0.000\n" +
                "0.583;1.000\n" +
                "0.667;0.000\n" +
                "0.750;0.000\n" +
                "0.833;0.000\n" +
                "0.917;0.000";
        assertEquals(prefix + "groupNameOne" + suffix, getTestFactory().stringOutputFor("reproduction-seasonality-sp0.csv"));
        assertEquals(prefix + "groupNameTwo" + suffix, getTestFactory().stringOutputFor("reproduction-seasonality-sp1.csv"));
    }

    @Test
    public void reproductionSeasonTemplates10StepsAYear() throws IOException {
        List<String> groupNames = new ArrayList<String>() {{
            add("groupNameOne");
            add("groupNameTwo");
        }};

        ValueFactory valueFactory = (name, group) -> "spawning.Jun".equals(name) ? "2.0" : "1.0";

        ConfigUtil.generateSeasonalReproductionFor(toGroups(groupNames), factory, valueFactory, 2);

        assertThat((getTestFactory()).stringOutputFor("osm_param-reproduction.csv"), is("reproduction.season.file.sp0;reproduction-seasonality-sp0.csv\nreproduction.season.file.sp1;reproduction-seasonality-sp1.csv"));
        String prefix = "Time (year);";
        String suffix = "\n0.000;0.667\n0.500;0.333";
        assertEquals(prefix + "groupNameOne" + suffix, getTestFactory().stringOutputFor("reproduction-seasonality-sp0.csv"));
        assertEquals(prefix + "groupNameTwo" + suffix, getTestFactory().stringOutputFor("reproduction-seasonality-sp1.csv"));
    }

    @Test
    public void functionalGroupList() throws IOException {
        Taxon taxonX = new Taxon("donald duck");
        taxonX.setUrl("http://example.com/donald");
        Group groupA = new Group("groupA", GroupType.FOCAL, Collections.singletonList(taxonX));

        Taxon taxonY = new Taxon("mickey mouse");
        taxonY.setUrl("http://example.com/mickey");

        Taxon taxonZ = new Taxon("minni mouse");
        taxonZ.setUrl("http://example.com/minni");
        taxonZ.setSelectionCriteria("implicit");
        Group groupB = new Group("groupB", GroupType.BACKGROUND, Arrays.asList(taxonY, taxonZ));

        ConfigUtil.generateFunctionGroupList(Arrays.asList(groupA, groupB), factory);

        String functionalGroups = (getTestFactory()).stringOutputFor("functional_groups.csv");

        String[] lines = functionalGroups.split("\n");
        assertThat(lines[0], is("functional group name,functional group type,species name,species url"));
        assertThat(lines[1], is("groupA,focal_functional_group,donald duck,http://example.com/donald"));
        assertThat(lines[2], is("groupB,biotic_resource,mickey mouse,http://example.com/mickey"));

        assertThat(lines.length, is(3));
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
        }), getTestFactory(), new ValueFactoryDefault());

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
