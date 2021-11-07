package com.zweaver.statistics.entity;

import java.util.List;

// note: this entity is not meant to be stored in a data base, hence no @Entity annotation
// its purpose is to be used when fetching filter requirements from the client to pass to DataSet class
public class DataSetEntity {
    private List<String> headers;
    private List<String> values;
    private List<String> conditions;
    private List<String> logicals;


    public DataSetEntity(List<String> headers, List<String> values, List<String> conditions, List<String> logicals) {
        this.headers = headers;
        this.values = values;
        this.conditions = conditions;
        this.logicals = logicals;
    }


    public List<String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<String> getLogicals() {
        return this.logicals;
    }

    public void setLogicals(List<String> logicals) {
        this.logicals = logicals;
    }

}
