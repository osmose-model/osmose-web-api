package com.github.jhpoelen.fbob;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PropertyMapper {
    public static void doMapping(InputStream mappingInputStream, PropertyMapping mapper) throws IOException {
        final CSVReader reader = new CSVReader(new InputStreamReader(mappingInputStream), ',');
        String[] line;
        while ((line = reader.readNext()) != null) {
            final String tableName = line[0];
            final String columnName = line[1];
            if (line.length > 3 && StringUtils.isNotBlank(columnName)) {
                final String propertyName = line[2];
                final String defaultValue = line[3];
                mapper.forMapping(tableName, columnName, propertyName, defaultValue);
            }
        }
    }
}
