package com.jackylaucf.jcriteria;

import com.jackylaucf.jcriteria.criteria.PagingCriteria;
import com.jackylaucf.jcriteria.criteria.QueryCriteria;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JCriteria {

    private EntityManager entityManager;
    private String selectJpql;
    private String countJpql;
    private Query query;
    private Map<String, Object> criteriaValueMap;

    public JCriteria(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public JCriteria criteria(QueryCriteria criteria) throws NoSuchFieldException, IllegalAccessException {
        PersistenceIO.JPQLWriter writer = new PersistenceIO(criteria).getWriter();
        this.selectJpql = writer.getJPQL();
        this.countJpql = PersistenceIO.JPQLWriter.SELECT + PersistenceIO.JPQLWriter.COUNT + this.selectJpql;
        this.criteriaValueMap = writer.getWriterValueMap();
        this.query = entityManager.createQuery(selectJpql);
        for (Map.Entry<String, Object> keyValuePair : writer.getWriterValueMap().entrySet()) {
            query.setParameter(keyValuePair.getKey(), keyValuePair.getValue());
        }
        return this;
    }

    public JCriteria criteria(QueryCriteria criteria, List<String> conditionNameList) throws NoSuchFieldException, IllegalAccessException {
        PersistenceIO.JPQLWriter writer = new PersistenceIO(criteria, conditionNameList).getWriter();
        this.selectJpql = writer.getJPQL();
        this.countJpql = PersistenceIO.JPQLWriter.SELECT + PersistenceIO.JPQLWriter.COUNT + this.selectJpql;
        this.criteriaValueMap = writer.getWriterValueMap();
        this.query = entityManager.createQuery(selectJpql);
        for (Map.Entry<String, Object> keyValuePair : criteriaValueMap.entrySet()) {
            query.setParameter(keyValuePair.getKey(), keyValuePair.getValue());
        }
        return this;
    }

    public JCriteria paging(PagingCriteria pagingCriteria) {
        if (pagingCriteria.getSortProperty() != null) {
            this.selectJpql = PersistenceIO.orderBy(selectJpql, pagingCriteria.getSortProperty(), pagingCriteria.getDirection());
            this.query = entityManager.createQuery(selectJpql);
            for (Map.Entry<String, Object> keyValuePair : criteriaValueMap.entrySet()) {
                query.setParameter(keyValuePair.getKey(), keyValuePair.getValue());
            }
        }
        this.query.setFirstResult(pagingCriteria.getPageNumber() * pagingCriteria.getPageSize());
        this.query.setMaxResults(pagingCriteria.getPageSize());
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

    public long getCount() {
        Query query = entityManager.createQuery(countJpql);
        for (Map.Entry<String, Object> keyValuePair : criteriaValueMap.entrySet()) {
            query.setParameter(keyValuePair.getKey(), keyValuePair.getValue());
        }
        return (long) query.getSingleResult();
    }

    public <T> PageResult<T> getPageResult(Supplier<T> resultTypeSupplier) throws InvocationTargetException, IllegalAccessException {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setResult(getResultList(resultTypeSupplier));
        pageResult.setCount(getCount());
        pageResult.setFirst(query.getFirstResult());
        pageResult.setSize(query.getMaxResults());
        return pageResult;
    }

    public String getSelectJpql(){
        return this.selectJpql;
    }

    public String getCountJpql(){
        return this.countJpql;
    }
}
