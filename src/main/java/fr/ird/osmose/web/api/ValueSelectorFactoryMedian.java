package fr.ird.osmose.web.api;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueSelectorFactoryMedian implements ValueSelectorFactory {

    private final static List<String> DEFAULT_TABLE_COLUMNS_FOR_MEDIAN_SELECTION = Arrays.asList(
            "species.LongevityWild",
            "popgrowth.K",
            "popgrowth.Loo",
            "popgrowth.to",
            "poplw.a",
            "poplw.b",
            "maturity.Lm",
            "maturity.tm",
            "poplw.LengthMin",
            "estimate.Troph",
            "popll.LengthMin",
            "popll.LengthMax",
            "popqb.Mortality",
            "maturity.Lm",
            "maturity.tm"
    );
    private final List<String> namesForMedianSelection;

    public ValueSelectorFactoryMedian() {
        this(DEFAULT_TABLE_COLUMNS_FOR_MEDIAN_SELECTION);
    }

    public ValueSelectorFactoryMedian(List<String> tableColumnsForMedianSelection) {
        this.namesForMedianSelection = Collections.unmodifiableList(tableColumnsForMedianSelection);
    }

    @Override
    public ValueSelector valueSelectorFor(String name) {
        ValueSelector valueSelector = null;
        if (namesForMedianSelection.contains(name)) {
            valueSelector = new ValueSelector() {
                List<String> list = new ArrayList<>();

                @Override
                public void accept(String value) {
                    if (NumberUtils.isNumber(value)) {
                        list.add(value);
                    }
                }

                @Override
                public String select() {
                    double[] values = new double[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        values[i] = Double.parseDouble(list.get(i));
                    }
                    return list.isEmpty() ? null : String.valueOf(new Median().evaluate(values));
                }
            };
        } else {
            valueSelector = new ValueSelector() {
                String lastValue = null;

                @Override
                public void accept(String value) {
                    this.lastValue = value;
                }

                @Override
                public String select() {
                    return lastValue;
                }
            };
        }
        return valueSelector;
    }
}
