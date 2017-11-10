package com.github.jhpoelen.fbob;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.jhpoelen.fbob.OsmosePropertyName.PREDATION_EFFICIENCY_CRITICAL;
import static com.github.jhpoelen.fbob.OsmosePropertyName.PREDATION_INGESTION_RATE_MAX;

class ValueFactoryCalculated implements ValueFactory {
    private static final Logger LOG = Logger.getLogger(ValueFactoryCalculated.class.getName());
    public static final String FISHBASE_POPQB_MAINT_QB = "popqb.MaintQB";

    private final Map<String, ValueFactory> factoryMap;

    public ValueFactoryCalculated(ValueFactory valueFactory) {
        this.factoryMap = new HashMap<String, ValueFactory>() {
            {
                put("predation.efficiency.critical.sp", (name, group) -> {
                    String value = null;
                    try {
                        String ingestionRate = valueFactory.groupValueFor(PREDATION_INGESTION_RATE_MAX, group);
                        String maintQBString = valueFactory.groupValueFor(FISHBASE_POPQB_MAINT_QB, group);
                        if (StringUtils.isNotBlank(ingestionRate) && StringUtils.isNotBlank(maintQBString)) {
                            float ingestionRateParsed = Float.parseFloat(ingestionRate);
                            float maintQB = Float.parseFloat(maintQBString);
                            if (isValidPredationEfficiency(ingestionRateParsed, maintQB)) {
                                value = String.format("%.2f", maintQB / ingestionRateParsed);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        LOG.log(Level.WARNING, getMsg(PREDATION_EFFICIENCY_CRITICAL), ex);
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
                put("species.egg.weight.sp", (name, group) -> {
                    String value = null;
                    String eggDiameter = valueFactory.groupValueFor("species.egg.size.sp", group);
                    if (NumberUtils.isParsable(eggDiameter)) {
                        float radius = Float.parseFloat(eggDiameter) / 2;
                        value = String.format("%.8f", 1.025 * 4 / 3 * Math.PI * Math.pow(radius, 3));
                    }
                    return value;
                });
                put("species.sexratio.sp", (name, group) -> {
                    String value = null;
                    String spawningSexRatioMid = valueFactory.groupValueFor("spawning.SexRatiomid", group);
                    if (NumberUtils.isParsable(spawningSexRatioMid)) {
                        float sexRatio = Float.parseFloat(spawningSexRatioMid) / 100;
                        value = String.format("%.2f", sexRatio);
                    }
                    return value;
                });
                put("species.vonbertalanffy.threshold.age.sp", (name, group) -> {
                    String floatFormat = "%.3f";
                    valueFactory.groupValueFor("poplw.LengthMin", group);
                    String stringForTo = valueFactory.groupValueFor("popgrowth.to", group);
                    String stringForLoo = valueFactory.groupValueFor("popgrowth.Loo", group);
                    String stringForLengthMin = valueFactory.groupValueFor("popgrowth.LengthMin", group);
                    String stringForK = valueFactory.groupValueFor("popgrowth.K", group);
                    if (NumberUtils.isNumber(stringForTo)
                            && NumberUtils.isNumber(stringForLoo)
                            && NumberUtils.isNumber(stringForLengthMin)
                            && NumberUtils.isNumber(stringForK)
                            ) {
                        float to = Float.parseFloat(stringForTo);
                        float Loo = Float.parseFloat(stringForLoo);
                        float lengthMin = Float.parseFloat(stringForLengthMin);
                        float K = Float.parseFloat(stringForK);
                        return String.format(floatFormat, to + (Math.log(Loo) - Math.log(Loo - lengthMin)) / K);
                    } else {
                        String stringForLongevityWild = valueFactory.groupValueFor("species.LongevityWild", group);
                        String stringForAgeMin = valueFactory.groupValueFor("estimate.AgeMin", group);
                        String stringForAgeMax = valueFactory.groupValueFor("estimate.AgeMax", group);
                        if (NumberUtils.isNumber(stringForLongevityWild)
                                && NumberUtils.isNumber(stringForAgeMin)
                                && NumberUtils.isNumber(stringForAgeMax)) {
                            float ageMin = Float.parseFloat(stringForAgeMin);
                            float ageMinAdjusted = ageMin < 0 ? 0.01f : ageMin;
                            return String.format(floatFormat, Float.parseFloat(stringForLongevityWild) * ageMinAdjusted / Float.parseFloat(stringForAgeMax));
                        } else if (NumberUtils.isNumber(stringForLongevityWild)) {
                            float v = Float.parseFloat(stringForLongevityWild);
                            double value = v > 2.0 ? 1.0 : v / 2.0;
                            return String.format(floatFormat, value);
                        } else {
                            return String.format(floatFormat, 1.0);
                        }
                    }
                });
            }

            private boolean isValidPredationEfficiency(float ingestionRateParsed, float maintQB) {
                boolean valid = false;
                if (ingestionRateParsed == 0) {
                    LOG.warning(getMsg(PREDATION_EFFICIENCY_CRITICAL) + ": tried to divide by zero value for [" + PREDATION_INGESTION_RATE_MAX + "]");
                } else {
                    if (maintQB >= ingestionRateParsed) {
                        LOG.warning(getMsg(PREDATION_EFFICIENCY_CRITICAL) + ": [" + PREDATION_INGESTION_RATE_MAX + ":" + ingestionRateParsed + "] may not be smaller than [" + FISHBASE_POPQB_MAINT_QB + ":" + maintQB + "].");
                    } else {
                        valid = true;
                    }
                }
                return valid;
            }
        };
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
