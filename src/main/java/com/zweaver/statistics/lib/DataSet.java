package com.zweaver.statistics.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.zweaver.statistics.entity.DataSetEntity;

public class DataSet implements Serializable {
    private List<List<String>> data;
    private List<String> headers;

    /**
     * This will take a list of strings of headers and convert them to the appropriate indices used for filtering.
     * @param headers A (String) list of headers to get the numerical index of
     * @return An (Integer) list of indices for the relevant headers
     */
    private List<Integer> getHeaderIndices(List<String> headers) {
        List<Integer> headerIndices = new ArrayList<Integer>();
        for (String currentHeader : headers) {
            headerIndices.add(this.headers.indexOf(currentHeader));
        }

        return headerIndices;
    }

    public DataSet(List<String> headers, List<List<String>> data) {
        this.headers = headers;
        this.data = data;
    }

    public List<String> getHeaders() {
        return this.headers;
    }

    public List<List<String>> getData() {
        return this.data;
    }

    /**
     * 
     * @param indices List<Integer> of numerical indices of the columns to apply filters on.
     * @param values List<String> of values to compare columns to with the appropriate conditions
     * @param conditions List<String> of conditions: >, >=, <, <=, ==, !=, contains, startswith, endswith
     * @param logicals List<String> of logicals: and, or
     * @return A List<List<String>> subset of the original data set with filters applied.
     */
    public List<List<String>> filter(DataSetEntity dataFilterEntity) {

        List<Integer> indices = getHeaderIndices(dataFilterEntity.getHeaders());
        List<String> values = dataFilterEntity.getValues();
        List<String> conditions = dataFilterEntity.getConditions();
        List<String> logicals = dataFilterEntity.getLogicals();

        FilterPredicate compositePredicate = new FilterPredicate(indices.get(0), values.get(0), conditions.get(0));
        Predicate<List<String>> compPredicate = compositePredicate;
        if (logicals.size() > 0) {
            for (int i = 0; i < logicals.size(); ++i) {
                if (logicals.get(i).equals("and")) {
                    compPredicate = compPredicate.and(new FilterPredicate(indices.get(i+1), values.get(i+1), conditions.get(i+1)));
                }
                else if (logicals.get(i).equals("or")) {
                    compPredicate = compPredicate.or(new FilterPredicate(indices.get(i+1), values.get(i+1), conditions.get(i+1)));
                }
            }
        }

        return this.data.stream()
            .filter(compPredicate)
            .collect(Collectors.toList());
}
}