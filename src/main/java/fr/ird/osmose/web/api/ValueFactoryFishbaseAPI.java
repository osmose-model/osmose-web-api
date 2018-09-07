package fr.ird.osmose.web.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.ird.osmose.web.api.TraitFinderFishbaseAPI.findUsedTablesStatic;

public class ValueFactoryFishbaseAPI extends ValueFactoryFishbaseBase implements ValueFactory {

    private Map<String, Map<String, String>> groupTraitMap = new HashMap<>();

    @Override
    public String groupValueFor(String name, Group group) {
        TraitFinder traitFinder = new TraitFinderFishbaseAPI();
        Map<String, String> valuesForGroup = groupTraitMap.get(group.getName());
        if (valuesForGroup == null) {
            try {
                List<Taxon> taxa = group.getTaxa().size() == 0 ? Collections.singletonList(new Taxon(group.getName())) : group.getTaxa();
                Collection<String> availableTables = traitFinder.availableTables();
                valuesForGroup = new HashMap<>();
                for (Taxon taxon : taxa) {
                    Set<String> availableNames = valuesForGroup.keySet();
                    List<String> tables = findUsedTablesStatic(getMappingInputStream(), availableNames);
                    tables.retainAll(availableTables);

                    final Map<String, String> traitsForGroup = traitFinder.findTraits(taxon,
                        getMappingInputStream(),
                        tables);

                    for (Map.Entry<String, String> trait : traitsForGroup.entrySet()) {
                        valuesForGroup.putIfAbsent(trait.getKey(), trait.getValue());
                    }

                }
                groupTraitMap.put(group.getName(), valuesForGroup);
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException("failed to retrieve traits for [" + group.getName() + "]", e);
            }
        }
        return valuesForGroup.get(name);
    }

}

