package com.zweaver.statistics.lib;

import java.util.List;
import java.util.function.Predicate;

public class FilterPredicate implements Predicate<List<String>> {
    private int index;
    private String value;
    private String condition;

    public FilterPredicate(int index, String value, String condition) {
        this.index = index;
        this.value = value;
        this.condition = condition;
    }

    @Override
    public boolean test(List<String> data) throws NumberFormatException {
        switch (condition) {
            case "==":
                // this will work for both string and numerical types
                return data.get(index).equals(value);
            case "!=":
                return !data.get(index).equals(value);
            case ">":
                return Double.parseDouble(data.get(index)) > Double.parseDouble(value);
            case ">=":
                return Double.parseDouble(data.get(index)) >= Double.parseDouble(value);
            case "<":
                return Double.parseDouble(data.get(index)) < Double.parseDouble(value);
            case "<=":
                return Double.parseDouble(data.get(index)) <= Double.parseDouble(value);
            case "contains":
                return data.get(index).contains(value);
            case "!contains":
                return !data.get(index).contains(value);
            case "startswith":
                return data.get(index).startsWith(value);
            case "!startswith":
                return !data.get(index).startsWith(value);
            case "endswith":
                return data.get(index).endsWith(value);
            case "!endswith":
                return !data.get(index).endsWith(value);
            default:
                return false;
        }
    }
}
