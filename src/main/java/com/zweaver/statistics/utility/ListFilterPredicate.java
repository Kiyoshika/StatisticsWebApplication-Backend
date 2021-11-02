package com.zweaver.statistics.utility;

import java.util.List;
import java.util.function.Predicate;

public class ListFilterPredicate implements Predicate<List<String>> {
    private int index;
    private String value;

    public ListFilterPredicate(int index, String value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public boolean test(List<String> data) {
        return data.get(index).equals(value);
    }
}
