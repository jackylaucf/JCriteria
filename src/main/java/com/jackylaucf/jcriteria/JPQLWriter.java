package com.jackylaucf.jcriteria;

import com.jackylaucf.jcriteria.annotation.Criteria;
import com.jackylaucf.jcriteria.annotation.TargetEntity;
import com.jackylaucf.jcriteria.criteria.QueryCriteria;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class JPQLWriter {
    private static final String SPACE = " ";
    private static final String ALIAS = "r";
    private static final String DOT = ".";
    private static final String SELECT = " SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String COUNT = "COUNT(" + ALIAS + ")";
    private static final String WILDCARD = "%";

    private StringBuilder stringBuilder;
    private QueryCriteria criteria;
    private List<String> conditionNameList;

    JPQLWriter(QueryCriteria criteria){
        stringBuilder = new StringBuilder();
        this.criteria = criteria;
    }

    JPQLWriter(QueryCriteria criteria, List<String> conditionNameList){
        stringBuilder = new StringBuilder();
        this.criteria = criteria;
        this.conditionNameList = conditionNameList;
    }

    String writeSelectJpql() throws NoSuchFieldException, IllegalAccessException {
        writeFromStatement();
        writeWhereStatement();
        return stringBuilder.toString();
    }

    private void writeFromStatement(){
        stringBuilder.append(JPQLWriter.FROM);
        TargetEntity targetEntity = criteria.getClass().getAnnotation(TargetEntity.class);
        if (targetEntity == null) {
            throw new IllegalArgumentException();
        }
        else {
            stringBuilder.append(targetEntity.value().getSimpleName()).append(JPQLWriter.SPACE).append(JPQLWriter.ALIAS);
        }
    }

    private void writeWhereStatement() throws NoSuchFieldException, IllegalAccessException {
        List<Field> fields = new ArrayList<>();
        if(conditionNameList==null){
            fields = Arrays.asList(criteria.getClass().getDeclaredFields());
        }else{
            for (String fieldName : conditionNameList) {
                fields.add(criteria.getClass().getDeclaredField(fieldName));
            }
        }
        for(int i=0,j=0; i<fields.size(); i++){
            Field f = fields.get(i);
            f.setAccessible(true);
            Object o = f.get(criteria);
            if(o!=null){
                resolveCriteria(f, o, j);
                j++;
            }
        }
    }

    private void resolveCriteria(Field field, Object value, int index){

    }

    static String writeSelectCountStatement(){
        return (SELECT + COUNT).trim();
    }
}

