package fr.ird.osmose.web.api;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public final class TSVUtil {


    public static TsvParser beginParsingTSV(InputStream input) throws IOException {
        TsvParser parser = createParser();
        parser.beginParsing(input, "UTF-8");
        return parser;
    }

    private static TsvParser createParser() {
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setMaxCharsPerColumn(1024 * 1024);
        settings.setHeaderExtractionEnabled(true);
        return new TsvParser(settings);
    }

    public static List<String> valuesOfFirstColumnInTSV(InputStream input) {
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        TsvParser tsvParser = new TsvParser(settings);

        List<String[]> names = tsvParser
                .parseAll(input);

        List<String> tables = names
                .stream()
                .filter(values -> values != null && values.length > 0)
                .map(values -> values[0])
                .collect(Collectors.toList());
        return tables;
    }
}
