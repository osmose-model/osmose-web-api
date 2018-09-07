package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.ird.osmose.web.api.OsmosePropertyName.PREDATION_EFFICIENCY_CRITICAL;
import static fr.ird.osmose.web.api.OsmosePropertyName.PREDATION_INGESTION_RATE_MAX;

class ValueFactoryCalculated implements ValueFactory {
    private static final Logger LOG = Logger.getLogger(ValueFactoryCalculated.class.getName());
    public static final String FISHBASE_POPQB_MAINT_QB = "popqb.MaintQB";

    private final Map<String, ValueFactory> factoryMap;

    public ValueFactoryCalculated(ValueFactory valueFactory) {
        this.factoryMap = new HashMap<String, ValueFactory>() {
            {
                put("predation.efficiency.critical.sp", calculateCriticalPredationEfficiency());
                put("species.relativefecundity.sp", calculateRelativeFecundity(valueFactory));
                put("species.egg.weight.sp", calculateEggWeight(valueFactory));
                put("species.sexratio.sp", calculateSexRatio(valueFactory));
                put("species.vonbertalanffy.threshold.age.sp", estimateAmax(valueFactory));
            }

            private ValueFactory calculateCriticalPredationEfficiency() {
                return (name, group) -> {
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
                };
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

    private ValueFactory calculateRelativeFecundity(ValueFactory valueFactory) {
        return (name, group) -> {
            String value = null;
            String relFecundityMean = valueFactory.groupValueFor("fecundity.RelFecundityMean", group);
            String spawningCycles = valueFactory.groupValueFor("fecundity.SpawningCycles", group);
            if (NumberUtils.isParsable(relFecundityMean) && NumberUtils.isParsable(spawningCycles)) {
                value = String.format("%.2f", Float.parseFloat(spawningCycles) * Float.parseFloat(relFecundityMean));
            }
            return value;
        };
    }

    private ValueFactory calculateEggWeight(ValueFactory valueFactory) {
        return (name, group) -> {
            String value = null;
            String eggDiameter = valueFactory.groupValueFor("species.egg.size.sp", group);
            if (NumberUtils.isParsable(eggDiameter)) {
                float radius = Float.parseFloat(eggDiameter) / 2;
                value = String.format("%.8f", 1.025 * 4 / 3 * Math.PI * Math.pow(radius, 3));
            }
            return value;
        };
    }

    private ValueFactory calculateSexRatio(ValueFactory valueFactory) {
        return (name, group) -> {
            String value = null;
            String spawningSexRatioMid = valueFactory.groupValueFor("spawning.SexRatiomid", group);
            if (NumberUtils.isParsable(spawningSexRatioMid)) {
                float sexRatio = Float.parseFloat(spawningSexRatioMid) / 100;
                value = String.format("%.2f", sexRatio);
            }
            return value;
        };
    }

    // see https://github.com/jhpoelen/fb-osmose-bridge/issues/140
    private ValueFactory estimateAmax(ValueFactory valueFactory) {
        return (name, group) -> String.format("%.3f", calculateAmax(valueFactory, group));
    }

    private double calculateAmax(ValueFactory valueFactory, Group group) {
        String stringForTo = valueFactory.groupValueFor("species.t0.sp", group);
        String stringForLoo = valueFactory.groupValueFor("species.lInf.sp", group);
        String stringForLengthMin = valueFactory.groupValueFor("poplw.LengthMin", group);
        String stringForK = valueFactory.groupValueFor("species.K.sp", group);
        if (NumberUtils.isNumber(stringForTo)
                && NumberUtils.isNumber(stringForLoo)
                && NumberUtils.isNumber(stringForLengthMin)
                && NumberUtils.isNumber(stringForK)
                && calcAmax(stringForTo, stringForLoo, stringForLengthMin, stringForK) >= 0
                ) {
            return calcAmax(stringForTo, stringForLoo, stringForLengthMin, stringForK);
        } else {
            String stringForLongevityWild = valueFactory.groupValueFor("species.lifespan.sp", group);
            String stringForAgeMin = valueFactory.groupValueFor("estimate.AgeMin", group);
            String stringForAgeMax = valueFactory.groupValueFor("estimate.AgeMax", group);
            if (NumberUtils.isNumber(stringForLongevityWild)
                    && NumberUtils.isNumber(stringForAgeMin)
                    && NumberUtils.isNumber(stringForAgeMax)
                    && Float.parseFloat(stringForLongevityWild) < 2.0) {
                float ageMin = Float.parseFloat(stringForAgeMin);
                float ageMinAdjusted = ageMin < 0 ? 0.01f : ageMin;
                return Float.parseFloat(stringForLongevityWild) * ageMinAdjusted / Float.parseFloat(stringForAgeMax);
            } else if (NumberUtils.isNumber(stringForLongevityWild)) {
                float v = Float.parseFloat(stringForLongevityWild);
                return v / 10.0;
            } else {
                return 1.0;
            }
        }
    }

    private double calcAmax(String stringForTo, String stringForLoo, String stringForLengthMin, String stringForK) {
        float to = Float.parseFloat(stringForTo);
        float Loo = Float.parseFloat(stringForLoo);
        float lengthMin = Float.parseFloat(stringForLengthMin);
        float K = Float.parseFloat(stringForK);
        return to + (Math.log(Loo) - Math.log(Loo - lengthMin)) / K;
    }

    @Override
        public String groupValueFor (String name, Group group){
            return factoryMap.containsKey(name)
                    ? factoryMap.get(name).groupValueFor(name, group)
                    : null;
        }

    private String getMsg(String name) {
        return "failed to calculate [" + name + "]";
    }

}
