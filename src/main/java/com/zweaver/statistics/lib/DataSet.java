package com.zweaver.statistics.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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
            headerIndices.add(getHeaders().indexOf(currentHeader));
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

    public void setCell(List<Integer> cellIndices, String value) {
        this.data.get(cellIndices.get(0)).set(cellIndices.get(1), value);
    }

    public void setHeader(int headerIndex, String value) {
        this.headers.set(headerIndex, value);
    }

    /**
     * 
     * @param indices List<Integer> of numerical indices of the columns to apply filters on.
     * @param values List<String> of values to compare columns to with the appropriate conditions
     * @param conditions List<String> of conditions: >, >=, <, <=, ==, !=, contains, startswith, endswith
     * @param logicals List<String> of logicals: and, or
     * @return A DataSet object which is a subset of the original data set with filters applied.
     */
    public DataSet filter(DataSetEntity dataFilterEntity) {

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

        List<List<String>> filteredData = getData().stream()
            .filter(compPredicate)
            .collect(Collectors.toList());

        return new DataSet(getHeaders(), filteredData);
    }

    /**
     * Method to select specific columns from a data set. Essentially the opposite of drop().
     * 
     * @param desiredColumns A list of strings indicating the columns the user wants to keep in the data set.
     * @return A DataSet object that is a subset of the original with the selected columns.
     */
    public DataSet select(List<String> desiredColumns) {
        List<Integer> desiredColumnsIndices = getHeaderIndices(desiredColumns);
        List<String> subsetHeaders = desiredColumns;
        List<List<String>> subset = new ArrayList<List<String>>();

        // copy data
        for (int row = 0; row < getData().size(); ++row) {
            subset.add(new ArrayList<String>());
            for (int col = 0; col < desiredColumnsIndices.size(); ++col) {
                subset.get(row).add(getData().get(row).get(desiredColumnsIndices.get(col)));
            }
        }

        return new DataSet(subsetHeaders, subset);
    }

    /**
     * Method to drop specific columns from a data set. Essentially the opposite of select().
     * 
     * @param dropColumns A list of strings indicating the columns the user wants to drop from the data set.
     * @return A DataSet object that is a subset of the original without the dropped columns.
     */
    public DataSet drop(List<String> dropColumns) {
        List<Integer> dropColumnIndices = getHeaderIndices(dropColumns);
        // instead of copying the same code as above, 
        // we can get the inverse of dropColumnIndices and call select()
        
        List<Integer> allIndices = getHeaderIndices(getHeaders());
        allIndices.removeAll(dropColumnIndices);

        // convert indices back into names to call select()
        List<String> desiredColumns = new ArrayList<String>();
        for (int idx = 0; idx < allIndices.size(); ++idx) {
            desiredColumns.add(getHeaders().get(allIndices.get(idx)));
        }
        
        return select(desiredColumns);
    }

}