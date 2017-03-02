package com.github.jhpoelen.fbob;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.github.jhpoelen.fbob.TraitFinder.findUsedTables;

public class ValueFactoryFishbase implements ValueFactory {
    private final String mappingResource;

    private Map<String, Map<String, String>> groupTraitMap = new HashMap<>();

    public ValueFactoryFishbase() {
        this("fishbase-mapping.csv");
    }
    public ValueFactoryFishbase(String mappingResource) {
        this.mappingResource = mappingResource;
    }

    @Override
    public String groupValueFor(String name, Group group) {
        Map<String, String> valuesForGroup = groupTraitMap.get(group.getName());
        if (valuesForGroup == null) {
            try {
                List<Taxon> taxa = group.getTaxa().size() == 0 ? Collections.singletonList(new Taxon(group.getName())) : group.getTaxa();
                Set<String> availableTables = TraitFinder.availableTables();
                valuesForGroup = new HashMap<>();
                for (Taxon taxon : taxa) {
                    Set<String> availableNames = valuesForGroup.keySet();
                    List<String> tables = findUsedTables(TraitFinder.class.getResourceAsStream(mappingResource), availableNames);
                    tables.retainAll(availableTables);

                    final Map<String, String> traitsForGroup = TraitFinder.findTraits(taxon,
                        getClass().getResourceAsStream(mappingResource),
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

