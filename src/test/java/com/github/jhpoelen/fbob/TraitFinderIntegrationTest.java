package com.github.jhpoelen.fbob;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;

public class TraitFinderIntegrationTest {

    @Test
    public void findLifeSpanDynamic() throws IOException, URISyntaxException {
        Taxon kingMackerel = new Taxon("ScomberomorusCavalla");
        kingMackerel.setUrl("http://fishbase.org/summary/120");
        InputStream mappingFile = getClass().getResourceAsStream("fishbase-mapping.csv");
        Map<String, String> expectedTraits = new TreeMap<String, String>() {{
            put("species.lifespan.sp", "14.0");
            put("species.K.sp", "0.21");
            put("species.lInf.sp", "124.0");
            put("species.t0.sp", "-2.4");
            put("species.length2weight.condition.factor.sp", "0.0083");
            put("species.length2weight.allometric.power.sp", "2.973");
            put("species.maturity.size.sp", "0.0");
            put("species.maturity.age.sp", "0.0");
            // https://fishbase.ropensci.org/spawning?SpecCode=120 has SexRatiomid = null
            put("species.sexratio.sp", "0.0");
            put("predation.ingestion.rate.max.osmose.sp", "0.0");
            put("predation.predPrey.stage.threshold.sp", "0.0");
            // see https://github.com/jhpoelen/fb-osmose-bridge/issues/75
            put("predation.accessibility.stage.threshold.sp", "");
            put("plankton.TL.plk", "4.42");
            put("plankton.size.min.plk", "55.0");
            put("plankton.size.max.plk", "104.0");
        }};

        final Map<String, String> speciesProperties = TraitFinder.findTraits(kingMackerel, mappingFile);
        ArrayList<String> msgs = new ArrayList<>();
        for (String traitName : expectedTraits.keySet()) {
            assertNotNull(speciesProperties.get(traitName));
            if (!StringUtils.equals(speciesProperties.get(traitName), expectedTraits.get(traitName))) {
                String msg = "found [" + traitName + "]: [" + speciesProperties.get(traitName) + "], but expected [" + expectedTraits.get(traitName) + "]";
                msgs.add(msg);
            }
        }

        assertThat(StringUtils.join(msgs, "\n"), msgs.size(), is(0));
    }

}
