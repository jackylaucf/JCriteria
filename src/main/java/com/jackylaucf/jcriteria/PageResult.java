package com.jackylaucf.jcriteria;

import java.util.List;

public class PageResult<T> {

    private List<T> result;
    private Long count;
    private Integer first;
    private Integer size;

    public PageResult() {}

    public PageResult(List<T> result, Long count, Integer first, Integer size) {
        this.result = result;
        this.count = count;
        this.first = first;
        this.size = size;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
