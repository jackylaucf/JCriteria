package com.jackylaucf.jcriteria;

import com.jackylaucf.jcriteria.annotation.Criteria;
import com.jackylaucf.jcriteria.annotation.TargetEntity;
import com.jackylaucf.jcriteria.criteria.Logic;
import com.jackylaucf.jcriteria.criteria.QueryCriteria;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class JCriteria {

    private static final String ENTITY_ALIAS = "s";
    private static final String ENTITY_ALIAS_PROPERTY = "s.";

    private QueryCriteria criteria;
    private Pageable pageable;
    private StringBuilder stringBuilder;
    private Map<String, Object> criteriaContainer;


    public JCriteria(QueryCriteria criteria){
        this.criteria = criteria;
        this.stringBuilder = new StringBuilder();
    }

    public JCriteria(QueryCriteria criteria, Pageable pageable){
        this.criteria = criteria;
        this.pageable = pageable;
        this.stringBuilder = new StringBuilder();
    }

    public Query getQuery(EntityManager entityManager) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        stringBuilder.append("from").append(" ").append(getTargetEntity()).append(" ").append(ENTITY_ALIAS).append(" ");
        stringBuilder.append("where").append(" ");
        inspectCriteria();
        Query query = entityManager.createQuery(stringBuilder.toString());
        setParameter(query);
        setPageable(query);
        return query;
    }

    private String getTargetEntity(){
        TargetEntity targetEntity = criteria.getClass().getAnnotation(TargetEntity.class);
        if (targetEntity == null) {
            throw new IllegalArgumentException();
        }
        else {
            return targetEntity.entityClass().getSimpleName();
        }
    }

    private void inspectCriteria() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        boolean firstCriterion = true;
        for(Field field : criteria.getClass().getDeclaredFields()){
            Object targetField = PropertyUtils.getProperty(criteria, field.getName());
            if(targetField==null){
                break;
            }else{
                Criteria criterion = field.getAnnotation(Criteria.class);
                if(!firstCriterion){
                    stringBuilder.append(criterion.externalConjunction().jpql()).append(" ");
                }
                if(criterion.mapTo().length>1){
                    for(String entityField : criterion.mapTo()){
                        stringBuilder.append("(");
                        stringBuilder.append(ENTITY_ALIAS_PROPERTY).append(entityField).append(criterion.logic().jpql());
                    }
                }
                //stringBuilder.append(ENTITY_ALIAS_PROPERTY)
                firstCriterion = false;
            }
        }
    }

    private void resolveCondition(String fieldName, Logic logic){

    }

    private void setParameter(Query query){

    }

    private void setPageable(Query query){

    }

}
