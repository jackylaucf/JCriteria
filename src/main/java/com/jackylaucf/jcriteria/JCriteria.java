package com.jackylaucf.jcriteria;

import com.jackylaucf.jcriteria.annotation.Criteria;
import com.jackylaucf.jcriteria.annotation.TargetEntity;
import com.jackylaucf.jcriteria.criteria.Logic;
import com.jackylaucf.jcriteria.criteria.PagingCriteria;
import com.jackylaucf.jcriteria.criteria.QueryCriteria;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JCriteria {

    private EntityManager entityManager;
    private QueryCriteria criteria;
    private StringBuilder stringBuilder;
    private String selectJpql;
    private String countJpql;
    private Query query;
    private Map<String, Object> criteriaValueMap;

    public JCriteria(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public JCriteria criteria(QueryCriteria criteria) {
        this.criteria = criteria;
        this.selectJpql = getSelectJPQL();
        this.countJpql = getCountJPQL();
        this.query = entityManager.createQuery(selectJpql);
        return this;
    }

    public JCriteria paging(PagingCriteria pagingCriteria) {
        query.setFirstResult(pagingCriteria.getPageNumber() * pagingCriteria.getPageSize());
        query.setMaxResults(pagingCriteria.getPageSize());
        return this;
    }

    public <T> List<T> getResultList(Supplier<T> resultTypeSupplier) throws InvocationTargetException, IllegalAccessException {
        List<T> resultList = new ArrayList<>();
        for (Object o : query.getResultList()) {
            T element = resultTypeSupplier.get();
            BeanUtils.copyProperties(o, element);
            resultList.add(element);
        }
        return resultList;
    }

    public Integer getCount() {
        return (Integer) entityManager.createQuery(countJpql).getSingleResult();
    }

    public <T> PageResult<T> getPageResult(Supplier<T> resultTypeSupplier) throws InvocationTargetException, IllegalAccessException {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setResult(getResultList(resultTypeSupplier));
        pageResult.setCount(getCount());
        pageResult.setFirst(query.getFirstResult());
        pageResult.setSize(query.getMaxResults());
        return pageResult;
    }

    private String getSelectJPQL() {
        stringBuilder = new StringBuilder();
        criteriaValueMap = new HashMap<>();
        writeJPQLEntity();
        return stringBuilder.toString();
    }

    private String getCountJPQL() {
        return JPQLKeyword.SELECT + JPQLKeyword.COUNT + selectJpql;
    }

    private void writeJPQLEntity() {
        stringBuilder.append(JPQLKeyword.FROM);
        TargetEntity targetEntity = criteria.getClass().getAnnotation(TargetEntity.class);
        if (targetEntity == null) {
            throw new IllegalArgumentException();
        }
        else {
            stringBuilder.append(targetEntity.value().getSimpleName()).append(JPQLKeyword.SPACE).append(JPQLKeyword.ALIAS);
        }
    }
}
