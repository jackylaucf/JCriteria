package com.jackylaucf.jcriteria.annotation;

import com.jackylaucf.jcriteria.criteria.CriteriaType;
import com.jackylaucf.jcriteria.criteria.Relationship;
import com.jackylaucf.jcriteria.criteria.Logic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Criteria {
    String[] mapTo();
    CriteriaType criteriaType();
    Logic logic();
    Relationship internalConjunction() default Relationship.OR;
    Relationship externalConjunction() default Relationship.AND;
}
