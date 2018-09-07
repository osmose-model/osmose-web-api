package fr.ird.osmose.web.api;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

public class TraitFinderFishbaseAPITest {

    @Test
    public void buildQuery() throws URISyntaxException {
        assertKingMackerel(TraitFinderFishbaseAPI.queryForNameOnly("ScomberomorusCavalla"));
    }

    @Test
    public void buildQuery2() throws URISyntaxException {
        assertKingMackerel(TraitFinderFishbaseAPI.queryForNameOnly("Scomberomorus cavalla"));
    }

    @Test
    public void buildQueryByUrl() throws URISyntaxException {
        final Taxon taxon = new Taxon("scomberomorus  cavalla");
        taxon.setUrl("http://fishbase.org/summary/120");
        assertThat(TraitFinderFishbaseAPI.queryTable(taxon, "/species"), is(new URI("https://fishbase.ropensci.org/species?SpecCode=120&limit=5000")));
    }

    @Test
    public void buildQueryByUrl2() throws URISyntaxException {
        final Taxon taxon = new Taxon("donald duck");
        taxon.setUrl("http://sealifebase.org/summary/120");
        assertThat(TraitFinderFishbaseAPI.queryTable(taxon, "/species"), is(new URI("https://fishbase.ropensci.org/sealifebase/species?SpecCode=120&limit=5000")));
    }

    public void assertKingMackerel(String query) throws URISyntaxException {
        String expectedUrl = "Genus=Scomberomorus&Species=cavalla";
        assertThat(query, is(expectedUrl));
        URI uri = TraitFinderFishbaseAPI.uriForTableQuery("/species", query);
        assertThat(uri.toString(), is("https://fishbase.ropensci.org/species?Genus=Scomberomorus&Species=cavalla"));
    }


    @Test
    public void findLifeSpanStatic() throws IOException, URISyntaxException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final String jsonString = IOUtils.toString(getClass().getResourceAsStream("ScomberomorusCavalla.json"), "UTF-8");
        TreeMap<String, String> resultMap = new TreeMap<String, String>() {{
            put("species", jsonString);
        }};
        speciesProperties.putAll(TraitFinderFishbaseAPI.mapProperties(resultMap, getClass().getResourceAsStream("fishbase-mapping.csv")));
        assertThat(speciesProperties.get("species.lifespan.sp"), is("14.0"));
    }

    @Test
    public void tablesNeeded() throws IOException, URISyntaxException {
        String someMapping = getSomeMapping();
        List<String> tables = TraitFinderFishbaseAPI.findUsedTablesStatic(IOUtils.toInputStream(someMapping), new TreeSet<>());
        assertThat(tables, hasItem("popgrowth"));
        assertThat(tables, hasItem("species"));
    }

    @Test
    public void tablesNeededWithAvailableName() throws IOException, URISyntaxException {
        List<String> tables = findUsedTables(Arrays.asList("species.lifespan.sp"));
        assertThat(tables, hasItem("popgrowth"));
        assertThat(tables, not(hasItem("species")));
    }

    @Test
    public void tablesNeededWithAvailableNames() throws IOException, URISyntaxException {
        List<String> availableNames = Arrays.asList("species.lifespan.sp", "species.K.sp");
        List<String> tables = findUsedTables(availableNames);
        assertThat(tables, hasItem("popgrowth"));
        assertThat(tables, not(hasItem("species")));
    }

    @Test
    public void tablesNeededWithAvailableNames2() throws IOException, URISyntaxException {
        List<String> availableNames = Arrays.asList("species.lifespan.sp", "species.K.sp", "species.lInf.sp");
        List<String> tables = findUsedTables(availableNames);
        assertThat(tables, not(hasItem("popgrowth")));
        assertThat(tables, not(hasItem("species")));
    }

    private List<String> findUsedTables(List<String> availableNames) throws IOException {
        String someMapping = getSomeMapping();
        return TraitFinderFishbaseAPI.findUsedTablesStatic(IOUtils.toInputStream(someMapping), new TreeSet<>(availableNames));
    }

    private String getSomeMapping() {
        return "FishBase table,FishBase column(s),OSMOSE property,Default value,Notes from Arnaud\n" +
            "species,LongevityWild,species.lifespan.sp,10,Longevity. This is a parameter that can directly be obtained from FishBase/SeaLifeBase\n" +
            "popgrowth,K,species.K.sp,0.0,Instantaneous growth rate at small size. This is a parameter that can directly be obtained from FishBase/SeaLifeBase\n" +
            "popgrowth,Loo,species.lInf.sp,0.0,Maximum size. This is a parameter that can directly be obtained from FishBase/SeaLifeBase";
    }


}
