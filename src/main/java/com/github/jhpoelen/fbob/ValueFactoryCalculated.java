package com.github.jhpoelen.fbob;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class ValueFactoryCalculated implements ValueFactory {
    private static final Logger LOG = Logger.getLogger(ValueFactoryCalculated.class.getName());

    private final ValueFactory valueFactory;
    private final Map<String, ValueFactory> factoryMap;

    public ValueFactoryCalculated(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
        this.factoryMap = new HashMap<String, ValueFactory>() {{
            put("predation.efficiency.critical.sp", (name, group) -> {
                String calculatedPropertyName = "predation.efficiency.critical.sp";
                String value = null;
                try {
                    String ingestionRateMax = "predation.ingestion.rate.max.sp";
                    String ingestionRate = valueFactory.groupValueFor(ingestionRateMax, group);
                    String maintQB = valueFactory.groupValueFor("popqb.MaintQB", group);
                    if (StringUtils.isNotBlank(ingestionRate) && StringUtils.isNotBlank(maintQB)) {
                        float ingestionRateParsed = Float.parseFloat(ingestionRate);
                        if (ingestionRateParsed == 0) {
                            LOG.warning(getMsg(calculatedPropertyName) + ": tried to divide by zero value for [" + ingestionRateMax + "]");
                        } else {
                            value = String.format("%.2f", Float.parseFloat(maintQB) / ingestionRateParsed);
                        }
                    }
                } catch (NumberFormatException ex) {
                    LOG.log(Level.WARNING, getMsg(calculatedPropertyName), ex);
                }
                return value;
            });
            put("species.relativefecundity.sp", (name, group) -> {
                String value = null;
                String relFecundityMean = valueFactory.groupValueFor("fecundity.RelFecundityMean", group);
                String spawningCycles = valueFactory.groupValueFor("fecundity.SpawningCycles", group);
                if (NumberUtils.isParsable(relFecundityMean) && NumberUtils.isParsable(spawningCycles)) {
                    value = String.format("%.2f", Float.parseFloat(spawningCycles) * Float.parseFloat(relFecundityMean));
                }
                return value;
            });
        }};
    }

    @Override
    public String groupValueFor(String name, Group group) {
        return factoryMap.containsKey(name)
            ? factoryMap.get(name).groupValueFor(name, group)
            : null;
    }

    private String getMsg(String name) {
        return "failed to calculate [" + name + "]";
    }

}
