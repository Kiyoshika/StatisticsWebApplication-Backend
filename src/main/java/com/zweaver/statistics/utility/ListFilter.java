package com.zweaver.statistics.utility;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListFilter {
    public static List<List<String>> filter2DList(List<List<String>> fileContents, List<Integer> indices, List<String> values, List<String> conds) {

            // example of arguments
            /*List<Integer> indices = new ArrayList<Integer>(){{ add(0); add(2); }};
            List<String> values = new ArrayList<String>(){{ add("a"); add("f"); }};
            List<String> conds = new ArrayList<String>(){{ add("or"); }};*/

            ListFilterPredicate compositePredicate = new ListFilterPredicate(indices.get(0), values.get(0));
            Predicate<List<String>> compPredicate = compositePredicate;
            if (conds.size() > 0) {
                for (int i = 0; i < conds.size(); ++i) {
                    if (conds.get(i).equals("and")) {
                        compPredicate = compPredicate.and(new ListFilterPredicate(indices.get(i+1), values.get(i+1)));
                    }
                    else if (conds.get(i).equals("or")) {
                        compPredicate = compPredicate.or(new ListFilterPredicate(indices.get(i+1), values.get(i+1)));
                    }
                }
            }

            return fileContents.stream()
                .filter(compPredicate)
                .collect(Collectors.toList());
    }
}
