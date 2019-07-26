package com.jackylaucf.jcriteria.criteria;

public enum Relationship {
    AND(" and "),
    OR(" or ");

    String jpql;

    Relationship(String jpql){
        this.jpql = jpql;
    }

    public String jpql(){
        return this.jpql;
    }
}
