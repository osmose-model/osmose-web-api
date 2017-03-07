package com.github.jhpoelen.fbob;

import org.apache.commons.lang3.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

class ValueFactoryCalculated implements ValueFactory {
    private static final Logger LOG = Logger.getLogger(ValueFactoryCalculated.class.getName());

    private final ValueFactory valueFactory;

    public ValueFactoryCalculated(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
    }

    @Override
    public String groupValueFor(String name, Group group) {
        String calculatedPropertyName = "predation.efficiency.critical.sp";
        try {
            if (StringUtils.equals(name, calculatedPropertyName)) {
                String ingestionRateMax = "predation.ingestion.rate.max.sp";
                String ingestionRate = valueFactory.groupValueFor(ingestionRateMax, group);
                String maintQB = valueFactory.groupValueFor("popqb.MaintQB", group);
                if (StringUtils.isNotBlank(ingestionRate) && StringUtils.isNotBlank(maintQB)) {
                    float ingestionRateParsed = Float.parseFloat(ingestionRate);
                    if (ingestionRateParsed == 0) {
                        LOG.warning(getMsg(calculatedPropertyName) + ": tried to divide by zero value for [" + ingestionRateMax + "]");
                    } else {
                        return String.format("%.2f", Float.parseFloat(maintQB) / ingestionRateParsed);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            LOG.log(Level.WARNING, getMsg(calculatedPropertyName), ex);
        }

        return null;
    }

    private String getMsg(String cs2) {
        return "failed to calculate [" + cs2 + "]";
    }


}
