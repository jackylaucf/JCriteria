package com.jackylaucf.jcriteria.criteria;

public enum Logic {
    GT(" > "),               // Greater than
    LT(" < "),               // Less than
    GE(" >= "),              // Greater than or equal to
    LE(" <= "),              // Less than or equal to
    EQ(" = "),               // Equal to
    NE(" <> "),              // Not equal to
    IN(" IN "),
    LIKE(" LIKE "),
    START_WITH(" LIKE "),
    END_WITH(" LIKE ");

    String jpql;

    Logic(String jpql){
        this.jpql = jpql;
    }

    public String jpql(){
        return this.jpql;
    }
}
