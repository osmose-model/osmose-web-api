package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringUtils;

public class ValueFactoryNA implements ValueFactory {

    final private ValueFactory valueFactory;

    public ValueFactoryNA(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
    }

    @Override
    public String groupValueFor(String name, Group group) {
        String value = valueFactory.groupValueFor(name, group);
        return replaceNoValueWithExplicitNA(value);
    }

    private String replaceNoValueWithExplicitNA(String value) {
        return StringUtils.isBlank(value) ? "NA" : value;
    }

}
