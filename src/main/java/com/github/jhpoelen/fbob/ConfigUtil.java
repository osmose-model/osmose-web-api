package com.github.jhpoelen.fbob;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import ucar.ma2.InvalidRangeException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtil {
    public static final List<String> YEAR_PARTS = Arrays.asList("0.0", "0.083333336", "0.16666667", "0.25", "0.33333334", "0.41666666", "0.5", "0.5833333", "0.6666667", "0.75", "0.8333333", "0.9166667");

    public static final String OUTPUT_DEFAULTS = "output.start.year;0;;\n" +
        "output.file.prefix;osm;;\n" +
        "output.dir.path;output;;\n" +
        "output.recordfrequency.ndt;12;;\n" +
        ";;;\n" +
        "# CSV separator (COMA, SEMICOLON, EQUALS, COLON, TAB);;;\n" +
        "output.csv.separator;COMA;;\n" +
        ";;;\n" +
        "# Save restart file;;;\n" +
        "output.restart.enabled;false;;\n" +
        "output.restart.recordfrequency.ndt;60;;\n" +
        "output.restart.spinup;114;;\n" +
        ";;;\n" +
        "# Biomass;;;\n" +
        "output.biomass.enabled;true;;\n" +
        "output.biomass.bysize.enabled;false;;\n" +
        "output.biomass.byage.enabled;false;;\n" +
        "# Abundance;;;\n" +
        "output.abundance.enabled;false;;\n" +
        "output.abundance.bysize.enabled;false;;\n" +
        "output.abundance.byage.enabled;true;;\n" +
        "# Mortality;;;\n" +
        "output.mortality.enabled;true;;\n" +
        "output.mortality.perSpecies.byAge.enabled;true;;\n" +
        "output.mortality.perSpecies.bySize.enabled;false;;\n" +
        "# Yield;;;\n" +
        "output.yield.biomass.enabled;true;;\n" +
        "output.yield.abundance.enabled;false;;\n" +
        "output.yieldN.bySize.enabled;false;;\n" +
        "output.yield.bySize.enabled;false;;\n" +
        "output.yieldN.byAge.enabled;false;;\n" +
        "output.yield.byAge.enabled;false;;\n" +
        "# Size;;;\n" +
        "output.size.enabled;true ;;\n" +
        "output.size.catch.enabled;true ;;\n" +
        "output.meanSize.byAge.enabled;false;;\n" +
        "# Weight;;;\n" +
        "# TL;;;\n" +
        "output.TL.enabled;true;;\n" +
        "output.TL.catch.enabled;true;;\n" +
        "output.biomass.byTL.enabled;true;;\n" +
        "output.meanTL.bySize.enabled;false;;\n" +
        "output.meanTL.byAge.enabled;false;;\n" +
        "# Predation;;;\n" +
        "output.diet.composition.enabled;true;;\n" +
        "output.diet.composition.byAge.enabled;false;;\n" +
        "output.diet.composition.bySize.enabled;false;;\n" +
        "output.diet.pressure.enabled;true;;\n" +
        "output.diet.pressure.byAge.enabled;false;;\n" +
        "output.diet.pressure.bySize.enabled;false;;\n" +
        "# Spatial;;;\n" +
        "output.spatial.enabled;false;;\n" +
        "output.spatial.ltl.enabled;false;;\n" +
        ";;;\n" +
        "# Advanced parameters;;;\n" +
        "# Whether to include step 0 of the simulation in the outputs;;;\n" +
        "output.step0.include;false;;\n" +
        "# Cutoff for biomass, abundance, mean size and mean trophic level outputs;;;\n" +
        "# Size distribution (centimetre) ;;;\n" +
        "output.distrib.bySize.min;0;;\n" +
        "output.distrib.bySize.max;205;;\n" +
        "output.distrib.bySize.incr;10;;";

    public static void writeLine(OutputStream os, List<String> values, boolean leadingNewline) throws IOException {
        List<String> escapedValues = new ArrayList<String>();
        for (String value : values) {
            escapedValues.add(StringEscapeUtils.escapeCsv(value));
        }
        String row = StringUtils.join(escapedValues, ";");
        String line = leadingNewline ? ("\n" + row) : row;
        IOUtils.copy(IOUtils.toInputStream(line, "UTF-8"), os);
    }

    public static void writeLine(OutputStream os, List<String> values) throws IOException {
        writeLine(os, values, true);
    }

    public static void generateSeasonalReproductionFor(List<Group> groups, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-reproduction.csv");
        for (int i = 0; i < groups.size(); i++) {
            String reproductionFilename = reproductionFilename(i);
            String paramName = "reproduction.season.file.sp" + i;
            writeLine(os, Arrays.asList(paramName, reproductionFilename), i > 0);
        }

        for (int i = 0; i < groups.size(); i++) {
            OutputStream reprodOs = factory.outputStreamFor(reproductionFilename(i));
            writeLine(reprodOs, Arrays.asList("Time (year)", groups.get(i).getName()), false);
            for (String yearPart : YEAR_PARTS) {
                writeLine(reprodOs, Arrays.asList(yearPart, "0.0"));
            }
        }
    }

    public static String reproductionFilename(int i) {
        return "reproduction-seasonality-sp" + i + ".csv";
    }

    public static void generateFishingParametersFor(List<Group> groupNames, StreamFactory factory) throws IOException {
        generateFishingSeasonalityConfig(groupNames, factory);
        generateFishingSeasonalityTables(groupNames, factory);
    }

    public static void generateFishingSeasonalityTables(List<Group> groups, StreamFactory factory) throws IOException {
        for (Group group : groups) {
            OutputStream seasonalityOs = factory.outputStreamFor(finishingSeasonalityFilename(group));
            writeLine(seasonalityOs, Arrays.asList("Time", "Season"), false);
            for (String yearPart : YEAR_PARTS) {
                writeLine(seasonalityOs, Arrays.asList(yearPart, ""));
            }
        }
    }

    public static void generateFishingSeasonalityConfig(List<Group> groups, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-fishing.csv");
        writeZerosFor(groups, "mortality.fishing.rate.sp", os);
        writeZerosFor(groups, "mortality.fishing.recruitment.age.sp", os);
        writeZerosFor(groups, "mortality.fishing.recruitment.size.sp", os);
        for (Group group : groups) {
            String paramName = "mortality.fishing.season.distrib.file.sp" + groups.indexOf(group);
            String fishingSeasonality = finishingSeasonalityFilename(group);
            writeLine(os, Arrays.asList(paramName, fishingSeasonality));
        }
    }

    public static String finishingSeasonalityFilename(Group groupName) {
        return "fishing/fishing-seasonality-" + groupName.getName() + ".csv";
    }

    public static void writeZerosFor(List<Group> groupNames, String paramName, OutputStream os) throws IOException {
        for (Group groupName : groupNames) {
            writeLine(os, Arrays.asList(paramName + groupNames.indexOf(groupName), "0.0"));
        }
    }

    public static void generateStarvationFor(List<Group> groupNames, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-starvation.csv");
        for (int i = 0; i < groupNames.size(); i++) {
            String paramName = "mortality.starvation.rate.max.sp" + i;
            writeLine(os, Arrays.asList(paramName, "0.3"), i > 0);
        }
    }

    public static void generateSpecies(List<Group> groups, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-species.csv");
        for (Group groupName : groups) {
            int i = groups.indexOf(groupName);
            writeLine(os, Arrays.asList("species.name.sp" + i, groupName.getName()), i > 0);
        }

        writeParamLines(groups, "species.egg.size.sp", valueFactory, os);
        writeParamLines(groups, "species.egg.weight.sp", valueFactory, os);
        writeParamLines(groups, "species.K.sp", valueFactory, os);
        writeParamLines(groups, "species.length2weight.allometric.power.sp", valueFactory, os);
        writeParamLines(groups, "species.length2weight.condition.factor.sp", valueFactory, os);
        writeParamLines(groups, "species.lifespan.sp", valueFactory, os);
        writeParamLines(groups, "species.lInf.sp", valueFactory, os);
        writeParamLines(groups, "species.maturity.size.sp", valueFactory, os);
        writeParamLines(groups, "species.maturity.age.sp", valueFactory, os);
        writeParamLines(groups, "species.relativefecundity.sp", valueFactory, os);
        writeParamLines(groups, "species.sexratio.sp", valueFactory, os);
        writeParamLines(groups, "species.t0.sp", valueFactory, os);
        writeParamLines(groups, "species.vonbertalanffy.threshold.age.sp", valueFactory, os);
    }

    public static void generateLtlForGroups(List<Group> groupsBackground, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-ltl.csv");
        for (Group groupName : groupsBackground) {
            int i = groupsBackground.indexOf(groupName);
            writeLine(os, Arrays.asList("plankton.name.plk" + i, groupName.getName()), i > 0);
        }

        writeParamLines(groupsBackground, "plankton.accessibility2fish.plk", valueFactory, os);
        writeParamLines(groupsBackground, "plankton.conversion2tons.plk", valueFactory, os);
        writeParamLines(groupsBackground, "plankton.size.max.plk", valueFactory, os);
        writeParamLines(groupsBackground, "plankton.size.min.plk", valueFactory, os);
        writeParamLines(groupsBackground, "plankton.TL.plk", valueFactory, os);
    }

    public static void writeParamLines(List<Group> groups, String paramPrefix, ValueFactory valueFactory, OutputStream os) throws IOException {
        for (Group group : groups) {
            final String paramName = paramPrefix + groups.indexOf(group);
            writeLine(os, Arrays.asList(paramName, valueFactory.groupValueFor(paramPrefix, group)));
        }
    }

    public static void writeParamLines(List<Group> groupNames, String paramPrefix, List<String> paramValues, OutputStream os) throws IOException {
        for (Group group : groupNames) {
            List<String> values = new ArrayList<String>() {{
                add(paramPrefix + groupNames.indexOf(group));
                addAll(paramValues);
            }};
            writeLine(os, values);
        }
    }

    public static void generatePredationFor(List<Group> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-predation.csv");
        writeLine(os, Arrays.asList("predation.accessibility.file", "predation-accessibility.csv"), false);
        writeLine(os, Arrays.asList("predation.accessibility.stage.structure", "age"));
        writeParamLines(groupNames, "predation.accessibility.stage.threshold.sp", valueFactory, os);
        writeParamLines(groupNames, "predation.efficiency.critical.sp", valueFactory, os);
        writeParamLines(groupNames, "predation.ingestion.rate.max.sp", valueFactory, os);
        writeParamLines(groupNames, "predation.predPrey.sizeRatio.max.sp", Arrays.asList("0.0", "0.0"), os);
        writeParamLines(groupNames, "predation.predPrey.sizeRatio.min.sp", Arrays.asList("0.0", "0.0"), os);
        writeLine(os, Arrays.asList("predation.predPrey.stage.structure", "size"));
        writeParamLines(groupNames, "predation.predPrey.stage.threshold.sp", valueFactory, os);
    }

    public static void generateAllParametersFor(Integer timeStepsPerYear, List<Group> groupFocal, List<Group> groupsBackground, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_all-parameters.csv");
        writeLine(os, Arrays.asList("simulation.time.ndtPerYear", timeStepsPerYear.toString()));
        writeLine(os, Arrays.asList("simulation.time.nyear", "134"));
        writeLine(os, Arrays.asList("simulation.restart.file", "null"));
        writeLine(os, Arrays.asList("output.restart.recordfrequency.ndt", "60"));
        writeLine(os, Arrays.asList("output.restart.spinup", "114"));
        writeLine(os, Arrays.asList("simulation.nschool", "20"));
        writeLine(os, Arrays.asList("simulation.ncpu", "8"));
        writeLine(os, Arrays.asList("simulation.nplankton", Integer.toString(groupsBackground.size())));
        writeLine(os, Arrays.asList("simulation.nsimulation", "10"));
        writeLine(os, Arrays.asList("simulation.nspecies", Integer.toString(groupFocal.size())));
        writeLine(os, Arrays.asList("mortality.algorithm", "stochastic"));
        writeLine(os, Arrays.asList("mortality.subdt", "10"));
        writeLine(os, Arrays.asList("osmose.configuration.output", "osm_param-output.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.movement", "osm_param-movement.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.mortality.fishing", "osm_param-fishing.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.mortality.natural", "osm_param-natural-mortality.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.mortality.predation", "osm_param-predation.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.mortality.starvation", "osm_param-starvation.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.reproduction", "osm_param-reproduction.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.species", "osm_param-species.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.plankton", "osm_param-ltl.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.grid", "osm_param-grid.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.initialization", "osm_param-init-pop.csv"));

    }

    public static void generateOutputParamsFor(List<Group> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-output.csv");
        IOUtils.copy(IOUtils.toInputStream(OUTPUT_DEFAULTS, "UTF-8"), os);

        writeLine(os, Arrays.asList("output.cutoff.enabled", "true"));
        writeParamLines(groupNames, "output.cutoff.age.sp", valueFactory, os);

        writeLine(os, Arrays.asList("output.distrib.byAge.min", "0"));
        writeLine(os, Arrays.asList("output.distrib.byAge.max", "10"));
        writeLine(os, Arrays.asList("output.distrib.byAge.incr", "1"));

        writeLine(os, Arrays.asList("output.diet.stage.structure", "age"));
        writeParamLines(groupNames, "output.diet.stage.threshold.sp", Arrays.asList("0", "1", "2"), os);
    }

    public static void generateNaturalMortalityFor(List<Group> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-natural-mortality.csv");

        writeLine(os, Arrays.asList("mortality.natural.larva.rate.file", "null"), false);
        writeParamLines(groupNames, "mortality.natural.larva.rate.sp", valueFactory, os);
        writeLine(os, Arrays.asList("mortality.natural.rate.file", "null"));
        writeParamLines(groupNames, "mortality.natural.rate.sp", valueFactory, os);
    }

    public static void generateInitBiomassFor(List<Group> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-init-pop.csv");
        writeParamLines(groupNames, "population.seeding.biomass.sp", valueFactory, os);
    }


    public static void generateStatic(StreamFactory factory) throws IOException {
        generateFromTemplate(factory, "osm_param-grid.csv");
    }

    public static void generateFromTemplate(StreamFactory factory, String staticTemplate) throws IOException {
        OutputStream os = factory.outputStreamFor(staticTemplate);
        IOUtils.copy(ConfigUtil.class.getResourceAsStream("osmose_config/" + staticTemplate), os);
    }

    public static void generateMaps(List<Group> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream maskOs = factory.outputStreamFor("grid-mask.csv");
        IOUtils.copy(ConfigUtil.class.getResourceAsStream("osmose_config/grid-mask.csv"), maskOs);
        generateMovementConfig(groupNames, factory, valueFactory);
        generateMovementMapTemplates(groupNames, factory);
    }

    public static void generateMovementMapTemplates(List<Group> groups, StreamFactory factory) throws IOException {
        int nMaps = 0;
        for (Group group : groups) {
            OutputStream mapOutputStream = factory.outputStreamFor(getMapName(nMaps, group));
            IOUtils.copy(ConfigUtil.class.getResourceAsStream("osmose_config/maps/Amberjacks_1.csv"), mapOutputStream);
            nMaps++;
        }
    }

    public static void generateMovementConfig(List<Group> groups, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-movement.csv");
        writeParamLines(groups, "movement.distribution.method.sp", valueFactory, os);
        writeParamLines(groups, "movement.randomwalk.range.sp", valueFactory, os);
        int nMaps = 0;
        for (Group group : groups) {
            addMapForGroup(os, nMaps, group, getMapName(nMaps, group));
            nMaps++;
        }
    }

    public static String getMapName(int nMaps, Group group) {
        return "maps/" + group.getName() + "_" + nMaps + ".csv";
    }

    public static void addMapForGroup(OutputStream os, int nMaps, Group group, String mapName) throws IOException {
        String prefix = "movement.map" + nMaps;
        writeLine(os, Arrays.asList(prefix + ".age.max", "2"));
        writeLine(os, Arrays.asList(prefix + ".age.min", "0"));
        writeLine(os, Arrays.asList(prefix + ".file", mapName));
        writeLine(os, Arrays.asList(prefix + ".season", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        writeLine(os, Arrays.asList(prefix + ".species", group.getName()));
    }

    public static void generateConfigFor(Config config, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        generateConfigFor(config.getTimeStepsPerYear(),
            config.getGroups()
                .stream()
                .filter(group -> group.getType() == GroupType.FOCAL)
                .collect(Collectors.toList()),
            config.getGroups()
                .stream()
                .filter(group -> group.getType() == GroupType.BACKGROUND)
                .collect(Collectors.toList()),
            factory, valueFactory);
    }

    public static void generateConfigFor(Integer timeStepsPerYear, List<Group> groupsFocal, List<Group> groupsBackground, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        generateAllParametersFor(timeStepsPerYear, groupsFocal, groupsBackground, factory);
        generateFishingParametersFor(groupsFocal, factory);
        generateInitBiomassFor(groupsFocal, factory, valueFactory);
        generateMaps(groupsFocal, factory, valueFactory);
        generateNaturalMortalityFor(groupsFocal, factory, valueFactory);
        generateOutputParamsFor(groupsFocal, factory, valueFactory);
        generatePredationFor(groupsFocal, factory, valueFactory);
        generatePredationAccessibilityFor(groupsFocal, groupsBackground, factory);
        generateSeasonalReproductionFor(groupsFocal, factory);

        generateSpecies(groupsFocal, factory, valueFactory);
        generateStarvationFor(groupsFocal, factory);

        generateLtlForGroups(groupsBackground, factory, valueFactory);
        generateLtlBiomassForGroups(groupsBackground, factory, valueFactory);
        generateStatic(factory);
    }

    private static void generateLtlBiomassForGroups(List<Group> groupsBackground, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        final String resourceName = "osm_ltlbiomass.nc";
        OutputStream os = factory.outputStreamFor(resourceName);
        try {
            final File ltlbiomass = File.createTempFile("ltlbiomass", ".nc");
            ltlbiomass.deleteOnExit();
            LtlBiomassUtil.generateLtlBiomassNC(ltlbiomass, groupsBackground.size());
            IOUtils.copy(FileUtils.openInputStream(ltlbiomass), os);

        } catch (InvalidRangeException e) {
            throw new IOException("failed to generate [" + resourceName + "]", e);
        }

    }

    public static ValueFactory getProxyValueFactory(List<ValueFactory> valueFactories) {
        return new ValueFactoryProxy(valueFactories);
    }

    public static void generatePredationAccessibilityFor(List<Group> focalGroups, List<Group> backgroundGroups, StreamFactory factory) throws IOException {
        List<String> columnHeaders = new ArrayList<String>();
        for (Group group : focalGroups) {
            columnHeaders.add(group.getName() + " < 0.0 year");
            columnHeaders.add(group.getName() + " > 0.0 year");
        }
        columnHeaders.addAll(backgroundGroups.stream().map(Group::getName).collect(Collectors.toList()));

        OutputStream outputStream = factory.outputStreamFor("predation-accessibility.csv");
        writeLine(outputStream, new ArrayList<String>() {{
            add("v Prey / Predator >");
            addAll(columnHeaders);
        }}, false);

        for (String header : columnHeaders) {
            List<String> row = new ArrayList<String>();
            row.add(header);
            for (int i = 0; i < columnHeaders.size(); i++) {
                row.add("0.0");
            }
            writeLine(outputStream, row);
        }
    }
}
