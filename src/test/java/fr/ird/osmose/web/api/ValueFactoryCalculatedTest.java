package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ValueFactoryCalculatedTest {

    @Test
    public void predationEfficiencyCritical() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            if (name.equals("popqb.MaintQB")) {
                return "1.0";
            } else if (name.equals("predation.ingestion.rate.max.sp")) {
                return "2.0";
            }
            return null;
        });

        String value = valueFactory.groupValueFor("predation.efficiency.critical.sp", null);
        assertThat(value, is("0.50"));
    }

    @Test
    public void predationEfficiencyCriticalMaintQBGreaterThanIngestionRateMax() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            if (name.equals("popqb.MaintQB")) {
                return "2.0";
            } else if (name.equals("predation.ingestion.rate.max.sp")) {
                return "1.0";
            }
            return null;
        });

        String value = valueFactory.groupValueFor("predation.efficiency.critical.sp", null);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void predationEfficiencyCriticalMaintQBMissing() {
        ValueFactoryCalculated valueFactoryCalc = new ValueFactoryCalculated((name, group) -> {
            if (name.equals("popqb.MaintQB")) {
                return null;
            } else if (name.equals("predation.ingestion.rate.max.sp")) {
                return "0.1";
            }
            return null;
        });

        ValueFactory valueFactory = new ValueFactoryProxy(
                Arrays.asList(
                        valueFactoryCalc,
                        new ValueFactoryDefault()
                )
        );

        String value = valueFactory.groupValueFor("predation.efficiency.critical.sp", null);
        assertThat(value, is("0.57"));
    }

    @Test
    public void predationEfficiencyCriticalAllMissing() {
        ValueFactory valueFactory = new ValueFactoryProxy(
                Arrays.asList(
                        new ValueFactoryCalculated((name, group) -> null),
                        new ValueFactoryDefault()
                )
        );

        String value = valueFactory.groupValueFor("predation.efficiency.critical.sp", null);
        assertThat(value, is("0.57"));
    }

    @Test
    public void eggWeight() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            if (name.equals("species.egg.size.sp")) {
                return "0.1";
            }
            return null;
        });

        String value = valueFactory.groupValueFor("species.egg.weight.sp", null);
        assertThat(value, is("0.00053669"));
    }

    @Test
    public void predationEfficiencyCriticalDivideByZero() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            if (name.equals("popqb.MaintQB")) {
                return "1.0";
            } else if (name.equals("predation.ingestion.rate.max.sp")) {
                return "0";
            }
            return null;
        });

        String value = valueFactory.groupValueFor("predation.efficiency.critical.sp", null);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void predationEfficiencyCriticalMissing() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> null);

        String value = valueFactory.groupValueFor("predation.efficiency.critical.sp", null);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void relativeFecundityNoValue() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> null);

        String value = valueFactory.groupValueFor("species.relativefecundity.sp", null);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void relativeFecundityNAValue() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> "NA");

        String value = valueFactory.groupValueFor("species.relativefecundity.sp", null);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void relativeSexRatio() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated(
                (name, group) -> StringUtils.equals(name, "spawning.SexRatiomid") ? "43" : null);

        String value = valueFactory.groupValueFor("species.sexratio.sp", null);
        assertThat(value, is("0.43"));
    }

    @Test
    public void relativeFecundityCalculated() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            if (name.equals("fecundity.SpawningCycles")) {
                return "1.0";
            } else if (name.equals("fecundity.RelFecundityMean")) {
                return "2";
            }
            return null;
        });

        String value = valueFactory.groupValueFor("species.relativefecundity.sp", null);
        assertThat(value, is("2.00"));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase1() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            Map<String, String> values = new TreeMap<String, String>() {{
                put("poplw.LengthMin", "1.0");
                put("species.t0.sp", "1.0");
                put("species.lInf.sp", "2.0");
                put("species.K.sp", "0.2");
            }};
            return values.get(name);
        });

        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        double expected = 1.0 + (Math.log(2.0) - Math.log(2.0 - 1.0)) / 0.2;
        assertThat(actual, is(String.format("%.3f", expected)));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase2() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            Map<String, String> values = new TreeMap<String, String>() {{
                put("species.LongevityWild", "2.1");
            }};
            return values.get(name);
        });

        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        assertThat(actual, is("1.000"));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase3() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            Map<String, String> values = new TreeMap<String, String>() {{
                put("species.lifespan.sp", "1.9");
                put("estimate.AgeMin", "2");
                put("estimate.AgeMax", "5");
            }};
            return values.get(name);
        });

        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        assertThat(actual, is(String.format("%.3f", 1.9 * 2.0 / 5.0)));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase3NegativeAgeMin() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            Map<String, String> values = new TreeMap<String, String>() {{
                put("species.lifespan.sp", "1.9");
                put("estimate.AgeMin", "-2");
                put("estimate.AgeMax", "5");
            }};
            return values.get(name);
        });

        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        assertThat(actual, is(String.format("%.3f", 1.9 * 0.01 / 5.0)));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase3LifespanGreaterThan2() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            Map<String, String> values = new TreeMap<String, String>() {{
                put("species.lifespan.sp", "10");
                put("estimate.AgeMin", "-2");
                put("estimate.AgeMax", "5");
            }};
            return values.get(name);
        });

        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        assertThat(actual, is(String.format("%.3f", 10.0 / 10.0)));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase4() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> null);

        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        assertThat(actual, is("1.000"));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase5() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            Map<String, String> values = new TreeMap<String, String>() {{
                put("species.lifespan.sp", "1.9");
            }};
            return values.get(name);
        });

        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        assertThat(actual, is(String.format("%.3f", 1.9 / 10.0)));
    }

    @Test
    public void relativeFecundityCalculatedAmexCase6() {
        ValueFactoryCalculated valueFactory = new ValueFactoryCalculated((name, group) -> {
            Map<String, String> values = new TreeMap<String, String>() {{
                put("poplw.LengthMin", "0.02");
                put("species.t0.sp", "0.0");
                put("species.lInf.sp", "0.03");
                put("species.K.sp", "-0.002");
                put("species.lifespan.sp", "10");
            }};
            return values.get(name);
        });


        String actual = valueFactory.groupValueFor("species.vonbertalanffy.threshold.age.sp", null);
        assertThat(actual, is(String.format("%.3f", 10 / 10.0)));
    }

}