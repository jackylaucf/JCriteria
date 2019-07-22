package com.jackylaucf.jcriteria.criteria;

public class PagingCriteria {

    private Integer pageSize;
    private Integer pageNumber;
    private Direction direction;
    private String sortProperty;

    public PagingCriteria(Integer pageSize, Integer pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public PagingCriteria(Integer pageSize, Integer pageNumber, Direction direction, String sortProperty) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.direction = direction;
        this.sortProperty = sortProperty;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }
}
