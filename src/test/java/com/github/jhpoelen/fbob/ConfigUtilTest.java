package com.github.jhpoelen.fbob;

import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigUtilTest {

    @Test
    public void depthMinMax() {
        assertNull(ConfigUtil.parseDepthMinMax(null, null));
        assertNull(ConfigUtil.parseDepthMinMax("NA", ""));
        assertThat(ConfigUtil.parseDepthMinMax("12.1", "1.1"), Is.is(Pair.of(12.1d, 1.1)));
    }

}