package com.github.jhpoelen.fbob;

import java.util.HashMap;
import java.util.Map;

public class ValueFactoryDefault implements ValueFactory {

    private final static Map<String, String> DEFAULTS = new HashMap<String, String>() {{
        put("species.egg.size.sp", "0.1");
        put("species.K.sp", "NA");
        put("species.length2weight.allometric.power.sp", "NA");
        put("species.length2weight.condition.factor.sp", "NA");
        put("species.lifespan.sp", "NA");
        put("species.lInf.sp", "NA");
        put("species.maturity.size.sp", "NA");
        put("species.maturity.age.sp", "NA");
        put("species.relativefecundity.sp", "NA");
        put("species.sexratio.sp", "0.0");
        put("species.t0.sp", "NA");
        put("species.vonbertalanffy.threshold.age.sp", "0.0");

        put("predation.accessibility.stage.threshold.sp", "NA");
        put("predation.efficiency.critical.sp", "0.57");
        put("predation.ingestion.rate.max.sp", "3.5");
        put("predation.predPrey.stage.threshold.sp", "NA");

        put("movement.distribution.method.sp", "maps");
        put("movement.randomwalk.range.sp", "1");

        put("population.seeding.biomass.sp", "0.0");

        put("mortality.natural.larva.rate.sp", "0.0");
        put("mortality.natural.rate.sp", "0.0");

        put("output.cutoff.age.sp", "0.0");

        put("predation.predPrey.stage.threshold.sp", "0.0");
        put("predation.ingestion.rate.max.sp", "3.5");
        put("predation.efficiency.critical.sp", "0.57");
        put("predation.accessibility.stage.threshold.sp", "0.0");

        put("plankton.accessibility2fish.plk", "0.0");
        put("plankton.conversion2tons.plk", "1");
        put("plankton.size.max.plk", "0.002");
        put("plankton.size.min.plk", "0.0002");
        put("plankton.TL.plk", "1");
    }};

    @Override
    public String groupValueFor(String name, Group group) {
        return DEFAULTS.get(name);
    }

}
