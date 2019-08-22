package com.jackylaucf.jcriteria;

import com.jackylaucf.jcriteria.annotation.Criteria;
import com.jackylaucf.jcriteria.annotation.IgnoreCase;
import com.jackylaucf.jcriteria.annotation.TargetEntity;
import com.jackylaucf.jcriteria.criteria.CriteriaType;
import com.jackylaucf.jcriteria.criteria.Direction;
import com.jackylaucf.jcriteria.criteria.Logic;
import com.jackylaucf.jcriteria.criteria.QueryCriteria;

import java.lang.reflect.Field;
import java.util.*;

class PersistenceIO {

    private QueryCriteria criteria;
    private List<String> conditionNameList;

    class JPQLWriter{
        static final String SPACE = " ";
        static final String ALIAS = "r";
        static final String DOT = ".";
        static final String SELECT = " SELECT ";
        static final String FROM = " FROM ";
        static final String WHERE = " WHERE ";
        static final String ORDER_BY = " ORDER BY ";
        static final String ASC = " ASC ";
        static final String DESC = " DESC ";
        static final String COUNT = "COUNT(" + ALIAS + ")";
        static final String LOWER = "LOWER";
        static final String WILDCARD = "%";
        static final String OPEN_BRACKET = "(";
        static final String CLOSE_BRACKET = ")";
        static final String NAMED_PARAMETER = ":";
        static final String CONCAT = "CONCAT";
        static final String COMMA = ",";
        static final String QUOTE = "'";

        private StringBuilder stringBuilder;
        private Map<String, Object> criteriaValueMap;
        private boolean globalIgnoreCase;

        private JPQLWriter() {
            stringBuilder = new StringBuilder();
            criteriaValueMap = new HashMap<>();
            globalIgnoreCase = criteria.getClass().isAnnotationPresent(IgnoreCase.class);
        }

        String getJPQL(){
            return stringBuilder.toString();
        }

        Map<String, Object> getWriterValueMap(){
            return this.criteriaValueMap;
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

        private void writeWhereStatement() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
            List<Field> fields = new ArrayList<>();
            if (conditionNameList == null){
                for(Field f : criteria.getClass().getDeclaredFields()){
                    if(f.isAnnotationPresent(Criteria.class)){
                        fields.add(f);
                    }
                }
            }
            else {
                for (String fieldName : conditionNameList) {
                    Field f = criteria.getClass().getDeclaredField(fieldName);
                    if(!f.isAnnotationPresent(Criteria.class)){
                        throw new ClassNotFoundException("Missing @Criteria Annotation for Field:" +f.getName());
                    }
                    fields.add(f);
                }
            }
            for (int i = 0, j = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                f.setAccessible(true);
                Object o = f.get(criteria);
                if (o != null) {
                    criteriaValueMap.put(f.getName(), o);
                    prepareStatement(f, j);
                    j++;
                }
            }
        }

        private void prepareStatement(Field field, int index) {
            Criteria criteria = field.getDeclaredAnnotation(Criteria.class);
            if (index == 0) {
                stringBuilder.append(JPQLWriter.WHERE);
            }
            else {
                stringBuilder.append(criteria.externalConjunction().jpql());
            }
            boolean isIgnoreCase = criteria.criteriaType().equals(CriteriaType.TEXT) && (globalIgnoreCase || field.isAnnotationPresent(IgnoreCase.class));
            if (criteria.mapTo().length > 1) {
                stringBuilder.append(OPEN_BRACKET);
                for (int i = 0; i < criteria.mapTo().length; i++) {
                    if(isIgnoreCase){
                        stringBuilder.append(LOWER).append(OPEN_BRACKET).append(ALIAS).append(DOT).append(criteria.mapTo()[i]).append(CLOSE_BRACKET);
                    }else{
                        stringBuilder.append(ALIAS).append(DOT).append(criteria.mapTo()[i]);
                    }
                    stringBuilder.append(criteria.logic().jpql());
                    if (criteria.logic().equals(Logic.LIKE) || criteria.logic().equals(Logic.START_WITH) || criteria.logic().equals(Logic.END_WITH)) {
                        handleLikeStatement(criteria, field, isIgnoreCase);
                    }
                    else {
                        if(isIgnoreCase) {
                            stringBuilder.append(LOWER).append(OPEN_BRACKET).append(NAMED_PARAMETER).append(field.getName()).append(CLOSE_BRACKET);
                        }else{
                            stringBuilder.append(NAMED_PARAMETER).append(field.getName());
                        }
                    }
                    if (i != criteria.mapTo().length - 1) {
                        stringBuilder.append(criteria.internalConjunction().jpql());
                    }
                }
                stringBuilder.append(CLOSE_BRACKET);
            }
            else if (criteria.mapTo().length == 1) {
                if (isIgnoreCase) {
					stringBuilder.append(LOWER).append(OPEN_BRACKET).append(ALIAS).append(DOT).append(criteria.mapTo()[0]).append(CLOSE_BRACKET);
				}
				else {
					stringBuilder.append(ALIAS).append(DOT).append(criteria.mapTo()[0]);
				}
				stringBuilder.append(criteria.logic().jpql());
                if (criteria.logic().equals(Logic.LIKE) || criteria.logic().equals(Logic.START_WITH) || criteria.logic().equals(Logic.END_WITH)) {
                    handleLikeStatement(criteria, field, isIgnoreCase);
                }
                else {
                    if(isIgnoreCase){
                        stringBuilder.append(LOWER).append(OPEN_BRACKET).append(NAMED_PARAMETER).append(field.getName()).append(CLOSE_BRACKET);
                    }else{
                        stringBuilder.append(NAMED_PARAMETER).append(field.getName());
                    }
                }
            }
        }

        private void handleLikeStatement(Criteria criteria, Field field, boolean isIgnoreCase) {
            if(isIgnoreCase){
                stringBuilder.append(LOWER).append(OPEN_BRACKET);
            }
            stringBuilder.append(CONCAT).append(OPEN_BRACKET);
            if (criteria.logic().equals(Logic.LIKE) || criteria.logic().equals(Logic.END_WITH)) {
                stringBuilder.append(QUOTE).append(WILDCARD).append(QUOTE).append(COMMA);
            }
            stringBuilder.append(NAMED_PARAMETER).append(field.getName());
            if (criteria.logic().equals(Logic.LIKE) || criteria.logic().equals(Logic.START_WITH)) {
                stringBuilder.append(COMMA).append(QUOTE).append(WILDCARD).append(QUOTE);
            }
            stringBuilder.append(CLOSE_BRACKET);
            if(isIgnoreCase){
                stringBuilder.append(CLOSE_BRACKET);
            }
        }
    }

    PersistenceIO(QueryCriteria criteria){
        this.criteria = criteria;
    }

    PersistenceIO(QueryCriteria criteria, List<String> conditionNameList){
        this.criteria = criteria;
        this.conditionNameList = conditionNameList;
    }

    JPQLWriter getWriter() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        JPQLWriter writer = new JPQLWriter();
        writer.writeFromStatement();
        writer.writeWhereStatement();
        return writer;
    }

    static String orderBy(String jpql, String orderByField, Direction sortDirection) {
        if (sortDirection.equals(Direction.DSC)) {
            return jpql + JPQLWriter.ORDER_BY + JPQLWriter.ALIAS + JPQLWriter.DOT + orderByField + JPQLWriter.DESC;
        }
        else {
            return jpql + JPQLWriter.ORDER_BY + JPQLWriter.ALIAS + JPQLWriter.DOT + orderByField + JPQLWriter.ASC;
        }
    }
}

