package com.github.jhpoelen.fbob;

import org.junit.Test;

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