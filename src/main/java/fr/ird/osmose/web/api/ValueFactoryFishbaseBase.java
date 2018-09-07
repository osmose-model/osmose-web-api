package fr.ird.osmose.web.api;

import java.io.InputStream;

public class ValueFactoryFishbaseBase {
    static final String FISHBASE_MAPPING_CSV = "fishbase-mapping.csv";

    private final String mappingResource;

    public ValueFactoryFishbaseBase() {
        this(FISHBASE_MAPPING_CSV);
    }

    public ValueFactoryFishbaseBase(String mappingResource) {
        this.mappingResource = mappingResource;
    }

    protected InputStream getMappingInputStream() {
        return getClass().getResourceAsStream(mappingResource);
    }
}
