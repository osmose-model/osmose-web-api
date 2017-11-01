package com.github.jhpoelen.fbob;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ValueFactorySexRatioConstraintsTest {

    @Test
    public void sexRatioLessThanMinimum() {
        ValueFactory contraints = new ValueFactorySexRatioConstraints((name, group) -> "0.01");
        assertThat(contraints.groupValueFor("species.sexratio.sp", null), Is.is(nullValue()));
    }

    @Test
    public void sexRatioNormal() {
        ValueFactory contraints = new ValueFactorySexRatioConstraints((name, group) -> "0.45");
        assertThat(contraints.groupValueFor("species.sexratio.sp", null), Is.is("0.45"));
    }

    @Test
    public void sexRatioMissing() {
        ValueFactory contraints = new ValueFactorySexRatioConstraints((name, group) -> null);
        assertThat(contraints.groupValueFor("species.sexratio.sp", null), Is.is(nullValue()));
    }

    @Test
    public void sexRatioTooBig() {
        ValueFactory contraints = new ValueFactorySexRatioConstraints((name, group) -> "0.99");
        assertThat(contraints.groupValueFor("species.sexratio.sp", null), Is.is(nullValue()));
    }

}