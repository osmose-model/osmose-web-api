package com.github.jhpoelen.fbob;

import org.junit.Test;

import java.util.Arrays;

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

}