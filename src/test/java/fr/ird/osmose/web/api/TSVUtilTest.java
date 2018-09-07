package fr.ird.osmose.web.api;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;

public class TSVUtilTest {

    @Test
    public void handleDOSCarriageReturns() throws IOException {
        InputStream inputStream = IOUtils.toInputStream(
                "a\r\n" +
                "d\r\n"
        );
        List<String> values = TSVUtil.valuesOfFirstColumnInTSV(inputStream);
        assertThat(values, Is.is(Arrays.asList("a", "d")));
    }

    @Test
    public void handle() throws IOException {
        InputStream inputStream = IOUtils.toInputStream(
                "a\n" +
                "d\n"
        );
        List<String> values = TSVUtil.valuesOfFirstColumnInTSV(inputStream);
        assertThat(values, Is.is(Arrays.asList("a", "d")));
    }
}
