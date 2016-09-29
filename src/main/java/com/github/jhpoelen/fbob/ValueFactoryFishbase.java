package com.github.jhpoelen.fbob;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jorrit on 9/28/16.
 */
public class ValueFactoryFishbase implements ValueFactory {
    private Map<String, Map<String, String>> groupTraitMap = new HashMap<>();

    @Override
    public String groupValueFor(String name, Group group) {
        Map<String, String> valuesForGroup = groupTraitMap.get(group.getName());
        if (valuesForGroup == null) {
            try {
                List<Taxon> taxa = group.getTaxa().size() == 0 ? Collections.singletonList(new Taxon(group.getName())) : group.getTaxa();
                valuesForGroup = new HashMap<>();
                for (Taxon taxon : taxa) {
                    final Map<String, String> traitsForGroup = TraitFinder.findTraits(taxon, getClass().getResourceAsStream("fishbase-mapping.csv"));
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

