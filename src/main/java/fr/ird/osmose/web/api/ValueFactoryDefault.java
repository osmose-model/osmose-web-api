package fr.ird.osmose.web.api;

import java.util.HashMap;
import java.util.Map;

public class ValueFactoryDefault implements ValueFactory {

    private final static Map<String, String> DEFAULTS = new HashMap<String, String>() {{
        put("species.egg.size.sp", "0.1");
        put("species.K.sp", null);
        put("species.length2weight.allometric.power.sp", null);
        put("species.length2weight.condition.factor.sp", null);
        put("species.lifespan.sp", null);
        put("species.lInf.sp", null);
        put("species.maturity.size.sp", null);
        put("species.maturity.age.sp", null);
        put("species.relativefecundity.sp", null);
        put("species.sexratio.sp", "0.50");
        put("species.t0.sp", null);
        put("species.vonbertalanffy.threshold.age.sp", "0.0");

        put("predation.accessibility.stage.threshold.sp", null);
        put("predation.efficiency.critical.sp", "0.57");
        put("predation.ingestion.rate.max.sp", "3.5");
        put("predation.predPrey.sizeRatio.max.sp", "3.5");
        put("predation.predPrey.sizeRatio.min.sp", "30.0");
        put("predation.predPrey.stage.threshold.sp", null);

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
        put("plankton.size.max.plk", null);
        put("plankton.size.min.plk", null);
        put("plankton.TL.plk", null);
    }};

    @Override
    public String groupValueFor(String name, Group group) {
        return DEFAULTS.get(name);
    }

}
