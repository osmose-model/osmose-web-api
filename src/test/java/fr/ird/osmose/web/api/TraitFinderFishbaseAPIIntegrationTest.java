package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

public class TraitFinderFishbaseAPIIntegrationTest {

    @Test
    public void findMackerelTraits() throws IOException, URISyntaxException {
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        InputStream mappingFile = getClass().getResourceAsStream("fishbase-mapping.csv");
        Map<String, String> expectedTraits = new TreeMap<String, String>() {{
            put("species.lifespan.sp", "14.0");
            put("species.maturity.size.sp", "50.0");
            put("species.K.sp", "0.21");
            put("species.lInf.sp", "124.0");
            put("species.t0.sp", "-2.4");
            put("species.length2weight.condition.factor.sp", "0.0083");
            put("species.length2weight.allometric.power.sp", "2.973");
            put("plankton.TL.plk", "4.42");
            put("plankton.size.min.plk", "55.0");
            put("plankton.size.max.plk", "104.0");
        }};

        assertExpectedTraits(kingMackerel, mappingFile, expectedTraits);
    }

    @Test
    public void findLifeSpanAmberjack() throws IOException, URISyntaxException {
        assertNoLifespanAvailable(1006);
        assertNoLifespanAvailable(1008);
    }

    private void assertNoLifespanAvailable(int specCode) throws URISyntaxException, IOException {
        Taxon amberjack = new Taxon("someSpecies");
        amberjack.setUrl("http://fishbase.org/summary/" + specCode);
        InputStream mappingFile = getClass().getResourceAsStream("fishbase-mapping.csv");
        final Map<String, String> speciesProperties = TraitFinderFishbaseAPI.findTraitsStatic(amberjack, mappingFile);
        assertThat(speciesProperties.get("species.lifespan.sp"), is(nullValue()));
    }

    @Test
    public void findMaxLengthRedGrouper() throws IOException, URISyntaxException {
        Taxon redGrouper = new Taxon("RedGrouper");
        redGrouper.setUrl("http://fishbase.org/summary/17");
        InputStream mappingFile = getClass().getResourceAsStream("fishbase-mapping.csv");
        Map<String, String> expectedTraits = new TreeMap<String, String>() {{
            put("plankton.size.min.plk", "5.0");
            put("plankton.size.max.plk", "90.0");
        }};

        assertExpectedTraits(redGrouper, mappingFile, expectedTraits);
    }

    private void assertExpectedTraits(Taxon kingMackerel, InputStream mappingFile, Map<String, String> expectedTraits) throws URISyntaxException, IOException {
        final Map<String, String> speciesProperties = TraitFinderFishbaseAPI.findTraitsStatic(kingMackerel, mappingFile);
        ArrayList<String> msgs = new ArrayList<>();
        for (String traitName : expectedTraits.keySet()) {
            assertNotNull("expected [" + traitName + "], but found none", speciesProperties.get(traitName));
            if (!StringUtils.equals(speciesProperties.get(traitName), expectedTraits.get(traitName))) {
                String msg = "found [" + traitName + "]: [" + speciesProperties.get(traitName) + "], but expected [" + expectedTraits.get(traitName) + "]";
                msgs.add(msg);
            }
        }

        assertThat(StringUtils.join(msgs, "\n"), msgs.size(), is(0));
    }

    @Test
    public void tableDocs() throws URISyntaxException, IOException {
        Set<String> tables = TraitFinderFishbaseAPI.availableTablesStatic();
        assertThat(tables, hasItem("species"));
        assertThat(tables, hasItem("popqb"));
        assertThat(tables, hasItem("genera"));
    }

    @Test
    public void tablesUsed() throws IOException, URISyntaxException {
        List<String> tables = TraitFinderFishbaseAPI.findUsedTablesStatic();
        assertThat(tables, hasItem("popqb"));
        assertThat(tables, hasItem("species"));
        assertThat(tables, not(hasItem("genera")));
    }


}
