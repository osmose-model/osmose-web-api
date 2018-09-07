package fr.ird.osmose.web.api;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ValueSelectorFactoryMedianTest {

    @Test
    public void selectMedian() {
        ValueSelector valueSelector = new ValueSelectorFactoryMedian(Arrays.asList("mickey"))
                .valueSelectorFor("mickey");
        valueSelector.accept("1");
        valueSelector.accept("2");
        valueSelector.accept("3");

        assertThat(valueSelector.select(), is("2.0"));
    }

    @Test
    public void selectLast() {
        ValueSelector valueSelector = new ValueSelectorFactoryMedian(Arrays.asList("mickey"))
                .valueSelectorFor("not.mickey");
        valueSelector.accept("1");
        valueSelector.accept("2");
        valueSelector.accept("3");

        assertThat(valueSelector.select(), is("3"));

    }

    @Test
    public void selectMedianWithNull() {
        ValueSelector valueSelector = new ValueSelectorFactoryMedian(Arrays.asList("mickey"))
                .valueSelectorFor("mickey");
        valueSelector.accept(null);
        assertThat(valueSelector.select(), is(nullValue()));
    }

}