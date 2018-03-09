package com.github.jhpoelen.fbob;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(String.class)
public enum GroupType {
    @XmlEnumValue("biotic_resource") biotic_resource,
    @XmlEnumValue("focal_functional_group") focal_functional_group
}
