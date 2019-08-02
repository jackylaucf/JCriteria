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
    private QueryCriteria criteria;
    private String selectJpql;
    private String countJpql;
    private Map<String, Object> criteriaValueMap;
    private Query query;

    public JCriteria(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public JCriteria criteria(QueryCriteria criteria) throws NoSuchFieldException, IllegalAccessException {
        this.criteria = criteria;
        PersistenceIO.JPQLWriter writer = new PersistenceIO(criteria).getWriter();
        this.selectJpql = writer.getJPQL();
        this.countJpql = PersistenceIO.JPQLWriter.SELECT + PersistenceIO.JPQLWriter.COUNT + this.selectJpql;
        this.query = entityManager.createQuery(selectJpql);

        return this;
    }

    public JCriteria criteria(QueryCriteria criteria, List<String> conditionNameList) throws NoSuchFieldException, IllegalAccessException {
        this.criteria = criteria;
        this.selectJpql = new PersistenceIO(criteria, conditionNameList).getWriter().getJPQL();
        this.countJpql = PersistenceIO.JPQLWriter.SELECT + PersistenceIO.JPQLWriter.COUNT + this.selectJpql;
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

    public String getSelectJpql(){
        return this.selectJpql;
    }

    public String getCountJpql(){
        return this.countJpql;
    }
}
