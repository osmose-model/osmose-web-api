package fr.ird.osmose.web.api;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ValueFactoryProxy implements ValueFactory {

    private final List<ValueFactory> valueFactories;

    public ValueFactoryProxy(List<ValueFactory> valueFactories) {
        this.valueFactories = valueFactories;
    }
    @Override
    public String groupValueFor(String name, Group group) {
        String value = null;
        for (ValueFactory valueFactory : valueFactories) {
            value = valueFactory.groupValueFor(name, group);
            if (StringUtils.isNotBlank(value)) {
                break;
            }
        }
        return value;
    }
}
