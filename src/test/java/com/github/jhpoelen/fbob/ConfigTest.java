package com.github.jhpoelen.fbob;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ConfigTest {

    @Test
    public void archive() {
        assertThat(new Config().configArchive(), is(notNullValue()));
    }
}
