package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ValueFactorySexRatioConstraints implements ValueFactory {

    final private ValueFactory valueFactory;
    private final ValueFactory valueDefaults;

    public ValueFactorySexRatioConstraints(ValueFactory valueFactory, ValueFactory valueDefaults) {
        this.valueFactory = valueFactory;
        this.valueDefaults = valueDefaults;
    }

    @Override
    public String groupValueFor(String name, Group group) {
        // see https://github.com/jhpoelen/fb-osmose-bridge/issues/139
        String value = valueFactory.groupValueFor(name, group);
        if (StringUtils.equals("species.sexratio.sp", name)) {
            if (NumberUtils.isNumber(value)) {
                double x = Double.parseDouble(value);
                if (x < 0.1 || x > 0.9) {
                    value = valueDefaults.groupValueFor(name, group);
                }
            }
        }
        return value;
    }

    private String replaceNoValueWithExplicitNA(String value) {
        return StringUtils.isBlank(value) ? "NA" : value;
    }

}
