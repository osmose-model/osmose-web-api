package fr.ird.osmose.web.api;

import org.junit.Test;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LtlBiomassUtilTest {

    @Test
    public void compareGeneratedWithProvidedNc() throws IOException, URISyntaxException, InvalidRangeException {
        final URL ncTemplate = getClass().getResource("osmose_config/osm_ltlbiomass.nc");
        assertNotNull(ncTemplate);
        NetcdfFile provided = openNCFile(new File(ncTemplate.toURI()));

        final File osmose = File.createTempFile("osmose", ".nc");
        osmose.deleteOnExit();
        LtlBiomassUtil.generateLtlBiomassNC(osmose, 9);

        final NetcdfFile generated = openNCFile(osmose);

        assertThat(generated.readArrays(Collections.singletonList(provided.findVariable("ltl_biomass"))).get(0).getShape(), is(provided.readArrays(Collections.singletonList(generated.findVariable("ltl_biomass"))).get(0).getShape()));

        assertSimilarFile(provided, generated);

        provided.close();
        generated.close();
    }

    @Test
    public void dynamicGroupsGenerated() throws IOException, URISyntaxException, InvalidRangeException {
        final File osmose = File.createTempFile("osmose", ".nc");
        osmose.deleteOnExit();
        LtlBiomassUtil.generateLtlBiomassNC(osmose, 3);

        final NetcdfFile generated = openNCFile(osmose);

        final List<Array> ltlBiomass = generated.readArrays(Collections.singletonList(generated.findVariable("ltl_biomass")));
        assertThat(ltlBiomass.get(0).getShape(), is(new int[]{12, 3, 33, 39}));

        generated.close();
    }

    @Test(expected = IOException.class)
    public void noDynamicGroupsGenerated() throws IOException, URISyntaxException, InvalidRangeException {
        final File osmose = File.createTempFile("osmose", ".nc");
        osmose.deleteOnExit();
        LtlBiomassUtil.generateLtlBiomassNC(osmose, 0);
    }

    public void assertSimilarFile(NetcdfFile provided, NetcdfFile generated) throws IOException {
        assertThat(generated.getDimensions(), is(provided.getDimensions()));
        assertThat(generated.getVariables(), is(provided.getVariables()));

        assertSameArray(generated, provided, "latitude");
        assertSameArray(generated, provided, "longitude");
        assertSameArray(generated, provided, "time");
    }

    public void assertSameArray(NetcdfFile ncGen, NetcdfFile ncProvided, String variableName) throws IOException {
        final List<Array> generated = ncGen.readArrays(Collections.singletonList(ncGen.findVariable(variableName)));
        final List<Array> provided = ncProvided.readArrays(Collections.singletonList(ncProvided.findVariable(variableName)));
        assertThat(generated.size(), is(provided.size()));
        for (int i = 0; i < generated.size(); i++) {
            final long arraySize = generated.get(i).getSize();
            assertThat(arraySize, is(provided.get(i).getSize()));
            for (int j = 0; j < arraySize; j++) {
                assertThat(generated.get(i).getFloat(j), is(provided.get(i).getFloat(j)));
            }
        }
    }

    public NetcdfFile openNCFile(File file) throws IOException, URISyntaxException {
        return ucar.nc2.dataset.NetcdfDataset.openFile(file.getAbsolutePath(), null);
    }

}
