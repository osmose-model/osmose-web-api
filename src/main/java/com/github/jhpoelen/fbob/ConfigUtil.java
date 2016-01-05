package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            "output.exploitable.biomass.enabled;true;;\n" +
            "output.biomass.bysize.enabled;false;;\n" +
            "output.biomass.byage.enabled;false;;\n" +
            "output.ssb.enabled;true;;\n" +
            "output.ssb.byage.enabled;true;;\n" +
            "# Abundance;;;\n" +
            "output.abundance.enabled;false;;\n" +
            "output.abundance.bysize.enabled;false;;\n" +
            "output.abundance.byage.enabled;true;;\n" +
            "output.recruitment.enabled;false;;\n" +
            "output.recruits.enabled;false;;\n" +
            "output.recruits.month.enabled;false;;\n" +
            "output.eggproduction.enabled;false;;\n" +
            "output.spawners.abundance.byage.enabled;true;;\n" +
            "# Mortality;;;\n" +
            "output.distrib.byAge.max;10;;\n" +
            "output.mortality.enabled;true;;\n" +
            "output.mortality.redgrouper.enabled;true;;\n" +
            "output.mortality.gaggrouper.enabled;true;;\n" +
            "output.mortality.redsnapper.enabled;true;;\n" +
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
            "output.meanWeight.byAge.enabled;false;;\n" +
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

    public static void generateSeasonalReproductionFor(List<String> groupNames, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-reproduction.csv");
        for (int i = 0; i < groupNames.size(); i++) {
            String reproductionFilename = reproductionFilename(i);
            String paramName = "reproduction.season.file.sp" + i;
            writeLine(os, Arrays.asList(paramName, reproductionFilename), i > 0);
        }

        for (int i = 0; i < groupNames.size(); i++) {
            OutputStream reprodOs = factory.outputStreamFor(reproductionFilename(i));
            writeLine(reprodOs, Arrays.asList("Time (year)", groupNames.get(i)), false);
            for (String yearPart : YEAR_PARTS) {
                writeLine(reprodOs, Arrays.asList(yearPart, "0.0"));
            }
        }
    }

    public static String reproductionFilename(int i) {
        return "reproduction-seasonality-sp" + i + ".csv";
    }

    public static void generateFishingParametersFor(List<String> groupNames, StreamFactory factory) throws IOException {
        generateFishingSeasonalityConfig(groupNames, factory);
        generateFishingSeasonalityTables(groupNames, factory);
    }

    public static void generateFishingSeasonalityTables(List<String> groupNames, StreamFactory factory) throws IOException {
        for (String groupName : groupNames) {
            OutputStream seasonalityOs = factory.outputStreamFor(finishingSeasonalityFilename(groupName));
            writeLine(seasonalityOs, Arrays.asList("Time", "Season"), false);
            for (String yearPart : YEAR_PARTS) {
                writeLine(seasonalityOs, Arrays.asList(yearPart, "0.0"));
            }
        }
    }

    public static void generateFishingSeasonalityConfig(List<String> groupNames, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-fishing.csv");
        writeZerosFor(groupNames, "mortality.fishing.rate.sp", os);
        writeZerosFor(groupNames, "mortality.fishing.recruitment.age.sp", os);
        writeZerosFor(groupNames, "mortality.fishing.recruitment.size.sp", os);
        for (String groupName : groupNames) {
            String paramName = "mortality.fishing.season.distrib.file.sp" + groupNames.indexOf(groupName);
            String fishingSeasonality = finishingSeasonalityFilename(groupName);
            writeLine(os, Arrays.asList(paramName, fishingSeasonality));
        }
    }

    public static String finishingSeasonalityFilename(String groupName) {
        return "fishing/fishing-seasonality-" + groupName + ".csv";
    }

    public static void writeZerosFor(List<String> groupNames, String paramName, OutputStream os) throws IOException {
        for (String groupName : groupNames) {
            writeLine(os, Arrays.asList(paramName + groupNames.indexOf(groupName), "0.0"));
        }
    }

    public static void generateStarvationFor(List<String> groupNames, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-starvation.csv");
        for (int i = 0; i < groupNames.size(); i++) {
            String paramName = "mortality.starvation.rate.max.sp" + i;
            writeLine(os, Arrays.asList(paramName, "0.3"), i > 0);
        }
    }

    public static void generateSpecies(List<String> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-species.csv");
        for (String groupName : groupNames) {
            int i = groupNames.indexOf(groupName);
            writeLine(os, Arrays.asList("species.name.sp" + i, groupName), i > 0);
        }

        writeParamLines(groupNames, "species.egg.size.sp", valueFactory, os);
        writeParamLines(groupNames, "species.egg.weight.sp", valueFactory, os);
        writeParamLines(groupNames, "species.K.sp", valueFactory, os);
        writeParamLines(groupNames, "species.length2weight.allometric.power.sp", valueFactory, os);
        writeParamLines(groupNames, "species.length2weight.condition.factor.sp", valueFactory, os);
        writeParamLines(groupNames, "species.lifespan.sp", valueFactory, os);
        writeParamLines(groupNames, "species.lInf.sp", valueFactory, os);
        writeParamLines(groupNames, "species.maturity.size.sp", valueFactory, os);
        writeParamLines(groupNames, "species.relativefecundity.sp", valueFactory, os);
        writeParamLines(groupNames, "species.sexratio.sp", valueFactory, os);
        writeParamLines(groupNames, "species.t0.sp", valueFactory, os);
        writeParamLines(groupNames, "species.vonbertalanffy.threshold.age.sp", valueFactory, os);
        writeParamLines(groupNames, "species.length2weight.fl.sp", valueFactory, os);
    }

    public static void writeParamLines(List<String> groupNames, String paramPrefix, ValueFactory valueFactory, OutputStream os) throws IOException {
        for (String groupName : groupNames) {
            final String paramName = paramPrefix + groupNames.indexOf(groupName);
            writeLine(os, Arrays.asList(paramName, valueFactory.valueFor(paramPrefix)));
        }
    }

    public static void writeParamLines(List<String> groupNames, String paramPrefix, List<String> paramValues, OutputStream os) throws IOException {
        for (String groupName : groupNames) {
            List<String> values = new ArrayList<String>() {{
                add(paramPrefix + groupNames.indexOf(groupName));
                addAll(paramValues);
            }};
            writeLine(os, values);
        }
    }

    public static void generatePredationFor(List<String> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
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

    public static void generateAllParametersFor(List<String> groupNames, List<String> implicitGroupNames, StreamFactory factory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_all-parameters.csv");
        writeLine(os, Arrays.asList("simulation.time.ndtPerYear", "12"));
        writeLine(os, Arrays.asList("simulation.time.nyear", "134"));
        writeLine(os, Arrays.asList("simulation.restart.file", "null"));
        writeLine(os, Arrays.asList("output.restart.recordfrequency.ndt", "60"));
        writeLine(os, Arrays.asList("output.restart.spinup", "114"));
        writeLine(os, Arrays.asList("simulation.nschool", "20"));
        writeLine(os, Arrays.asList("simulation.ncpu", "8"));
        writeLine(os, Arrays.asList("simulation.nplankton", Integer.toString(implicitGroupNames.size())));
        writeLine(os, Arrays.asList("simulation.nsimulation", "10"));
        writeLine(os, Arrays.asList("simulation.nspecies", Integer.toString(groupNames.size())));
        writeLine(os, Arrays.asList("mortality.algorithm", "stochastic"));
        writeLine(os, Arrays.asList("mortality.subdt", "10"));
        writeLine(os, Arrays.asList("osmose.configuration.output", "osm_param-output.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.movement", "osm_param-movement.csv"));
        writeLine(os, Arrays.asList("osmose.configuration.mpa", "osm_param-mpa.csv"));
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

    public static void generateOutputParamsFor(List<String> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-output.csv");
        IOUtils.copy(IOUtils.toInputStream(OUTPUT_DEFAULTS, "UTF-8"), os);

        writeLine(os, Arrays.asList("output.cutoff.enabled", "true"));
        writeParamLines(groupNames, "output.cutoff.age.sp", valueFactory, os);
        writeLine(os, Arrays.asList("output.diet.stage.structure", "agesize"));
        writeParamLines(groupNames, "output.diet.stage.threshold.sp", Arrays.asList("0.0", "0.0", "0.0"), os);
    }

    public static void generateNaturalMortalityFor(List<String> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-natural-mortality.csv");

        writeLine(os, Arrays.asList("mortality.natural.larva.rate.file", "null"), false);
        writeParamLines(groupNames, "mortality.natural.larva.rate.sp", valueFactory, os);
        writeLine(os, Arrays.asList("mortality.natural.rate.file", "null"));
        writeParamLines(groupNames, "mortality.natural.rate.sp", valueFactory, os);
    }

    public static void generateInitBiomassFor(List<String> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-init-pop.csv");
        writeParamLines(groupNames, "population.seeding.biomass.sp", valueFactory, os);
    }


    public static void generateStatic(StreamFactory factory) throws IOException {
        generateFromTemplate(factory, "osm_param-mpa.csv");
        generateFromTemplate(factory, "osm_param-ltl.csv");
        generateFromTemplate(factory, "osm_param-grid.csv");
        generateFromTemplate(factory, "osm_ltlbiomass.nc");
    }

    public static void generateFromTemplate(StreamFactory factory, String staticTemplate) throws IOException {
        OutputStream os = factory.outputStreamFor(staticTemplate);
        IOUtils.copy(ConfigUtil.class.getResourceAsStream("osmose_config/" + staticTemplate), os);
    }

    public static void generateMaps(List<String> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream maskOs = factory.outputStreamFor("grid-mask.csv");
        IOUtils.copy(ConfigUtil.class.getResourceAsStream("osmose_config/grid-mask.csv"), maskOs);
        generateMovementConfig(groupNames, factory, valueFactory);
        generateMovementMapTemplates(groupNames, factory);
    }

    public static void generateMovementMapTemplates(List<String> groupNames, StreamFactory factory) throws IOException {
        int nMaps = 0;
        for (String groupName : groupNames) {
            OutputStream mapOutputStream = factory.outputStreamFor(getMapName(nMaps, groupName));
            IOUtils.copy(ConfigUtil.class.getResourceAsStream("osmose_config/maps/Amberjacks_1.csv"), mapOutputStream);
            nMaps++;
        }
    }

    public static void generateMovementConfig(List<String> groupNames, StreamFactory factory, ValueFactory valueFactory) throws IOException {
        OutputStream os = factory.outputStreamFor("osm_param-movement.csv");
        writeParamLines(groupNames, "movement.distribution.method.sp", valueFactory, os);
        writeParamLines(groupNames, "movement.randomwalk.range.sp", valueFactory, os);
        int nMaps = 0;
        for (String groupName : groupNames) {
            addMapForGroup(os, nMaps, groupName, getMapName(nMaps, groupName));
            nMaps++;
        }
    }

    public static String getMapName(int nMaps, String groupName) {
        return "maps/" + groupName + nMaps + ".csv";
    }

    public static void addMapForGroup(OutputStream os, int nMaps, String groupName, String mapName) throws IOException {
        String prefix = "movement.map" + nMaps;
        writeLine(os, Arrays.asList(prefix + ".age.max", "2"));
        writeLine(os, Arrays.asList(prefix + ".age.min", "0"));
        writeLine(os, Arrays.asList(prefix + ".file", mapName));
        writeLine(os, Arrays.asList(prefix + ".season", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        writeLine(os, Arrays.asList(prefix + ".species", groupName));
    }

    public static void generateConfigFor(List<String> groupNames, List<String> implicitGroupNames, StreamFactory factory) throws IOException {
        final ValueFactory valueFactory = getValueFactory();

        generateAllParametersFor(groupNames, implicitGroupNames, factory);
        generateFishingParametersFor(groupNames, factory);
        generateInitBiomassFor(groupNames, factory, valueFactory);
        generateMaps(groupNames, factory, valueFactory);
        generateNaturalMortalityFor(groupNames, factory, valueFactory);
        generateOutputParamsFor(groupNames, factory, valueFactory);
        generatePredationFor(groupNames, factory, valueFactory);
        generatePredationAccessibilityFor(groupNames, implicitGroupNames, factory);
        generateSeasonalReproductionFor(groupNames, factory);

        generateSpecies(groupNames, factory, valueFactory);
        generateStarvationFor(groupNames, factory);
        generateStatic(factory);
    }

    public static ValueFactory getValueFactory() {
        return new ValueFactory() {
            Map<String, String> defaults = new HashMap<String, String>() {{
                put("species.egg.size.sp", "0.1");
                put("species.egg.weight.sp", "0.0005386");
                put("species.K.sp", "0.0");
                put("species.length2weight.allometric.power.sp", "0.0");
                put("species.length2weight.condition.factor.sp", "0.0");
                put("species.lifespan.sp", "0");
                put("species.lInf.sp", "0.0");
                put("species.maturity.size.sp", "0.0");
                put("species.relativefecundity.sp", "0");
                put("species.sexratio.sp", "0.0");
                put("species.t0.sp", "0.0");
                put("species.length2weight.fl.sp", "false");
                put("species.vonbertalanffy.threshold.age.sp", "0.0");

                put("predation.accessibility.stage.threshold.sp", "0.0");
                put("predation.efficiency.critical.sp", "0.57");
                put("predation.ingestion.rate.max.sp", "3.5");
                put("predation.predPrey.stage.threshold.sp", "0.0");

                put("movement.distribution.method.sp", "maps");
                put("movement.randomwalk.range.sp", "1");

                put("population.seeding.biomass.sp", "0.0");

                put("mortality.natural.larva.rate.sp", "0.0");
                put("mortality.natural.rate.sp", "0.0");

                put("output.cutoff.age.sp", "0.0");

                put("predation.predPrey.stage.threshold.sp", "0.0");
                put("predation.ingestion.rate.max.sp", "3.5");
                put("predation.efficiency.critical.sp", "0.57");
                put("predation.accessibility.stage.threshold.sp", "0.0");
            }};

            @Override
            public String valueFor(String name) {
                return defaults.get(name);
            }
        };
    }

    public static void generatePredationAccessibilityFor(List<String> groupNames, List<String> implicitGroupNames, StreamFactory factory) throws IOException {
        List<String> columnHeaders = new ArrayList<String>();
        for (String groupName : groupNames) {
            columnHeaders.add(groupName + " < 0.0 year");
            columnHeaders.add(groupName + " > 0.0 year");
        }
        columnHeaders.addAll(implicitGroupNames.stream().collect(Collectors.toList()));

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
