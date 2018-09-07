package fr.ird.osmose.web.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TraitFinder {
    Map<String, String> findTraits(Taxon taxon, InputStream fishbaseMapping, List<String> tableNames) throws URISyntaxException, IOException;
    Collection<String> availableTables();

    Collection<String> findUsedTables();
}
