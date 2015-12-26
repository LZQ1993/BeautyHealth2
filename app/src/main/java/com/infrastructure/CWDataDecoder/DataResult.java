package com.infrastructure.CWDataDecoder;

import java.util.List;

public class DataResult {
    public String resultcode;
    public String currentpage;
    public String total;
    public String rowsofapage;
    public List<Object> result;

    public String getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(String currentpage) {
        this.currentpage = currentpage;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getRowsofapage() {
        return rowsofapage;
    }

    public void setRowsofapage(String rowsofapage) {
        this.rowsofapage = rowsofapage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Object> getResult() {
        return result;
    }

    public void setResult(List<Object> result) {
        this.result = result;
    }
}
