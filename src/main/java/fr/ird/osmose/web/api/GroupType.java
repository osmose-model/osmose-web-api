package fr.ird.osmose.web.api;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(String.class)
public enum GroupType {
    @XmlEnumValue(GroupTypeConstants.API_BIOTIC_RESOURCE_LABEL)
    BACKGROUND(GroupTypeConstants.OSMOSE_BIOTIC_RESOURCE_LABEL),
    @XmlEnumValue(GroupTypeConstants.API_FOCAL_FUNCTIONAL_GROUP_LABEL)
    FOCAL(GroupTypeConstants.OSMOSE_FOCAL_FUNCTIONAL_GROUP_LABEL);

    private final String label;

    GroupType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
