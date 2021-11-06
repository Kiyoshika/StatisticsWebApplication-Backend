package com.zweaver.statistics.lib;

import java.io.Serializable;
import java.nio.file.Files;
import java.util.List;

public class DataSet implements Serializable {
    private List<List<String>> data;

    public DataSet(List<List<String>> data) {
        this.data = data;
    }

    public List<List<String>> getData() {
        return this.data;
    }
}
