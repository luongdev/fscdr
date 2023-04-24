package com.metechvn.freeswitchcdr.dtos;

import java.util.ArrayList;
import java.util.List;

public class PagedResult {

    private List<Object> data = new ArrayList<>();
    private long totalRecords;
    private long totalPages;

    public static PagedResult of(List<?> data, long totalRecords, long totalPages) {
        var res = new PagedResult();
        res.data.addAll(data);
        res.totalPages = totalPages;
        res.totalRecords = totalRecords;

        return res;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
}
