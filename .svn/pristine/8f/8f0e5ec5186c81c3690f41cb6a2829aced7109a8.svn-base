package com.inveno.cps.common.baseclass;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.inveno.base.BaseModel;
import com.inveno.base.Pagin;

/**
 * dao层顶层接口
 * Xjun 2014-8-7 edit
 */

public interface IBaseDAO {

	<T extends BaseModel> T save(Object object);

	<T extends BaseModel> T update(Object object);
	
    <T extends BaseModel> T saveOrUpdate(Object object);
	
	void delete(Object object);

	Object merge(Object object);

	<T extends BaseModel> T findById(Object id);
	
	<T extends BaseModel> T findById(Object id,Class<? extends BaseModel> pojoClass);

	public void flush();

	public void clearSession();

	public <T> List<T> findByDetachedCriteria(DetachedCriteria criteria);

	public <T> List<T> findByDetachedCriteria(DetachedCriteria criteria,
			int begin, int pageSize);

	public <T extends BaseModel> List<T> findObjectBySql(String queryString,
			List<?> conditions, Class<T> pojoClass);

	public <T extends BaseModel> List<T> findObjectByHql(String queryString,
			List<Object> conditions, Class<T> pojoClass, Pagin pagin);

	public <T> List<T> findBySql(String sql, List<?> parameters);

	public <T> List<T> findByHql(String hql, List<?> parameters);

	public void excuteSql(String sql, List<?> parameters);

	public void excuteHql(String hql, List<?> parameters);
	
	public void deleteAll(Collection<? extends BaseModel> entities);

	public Connection getConnection();

	public int delete(Class<? extends BaseModel> pojoClass, String[] ids);

	public int delete(Class<? extends BaseModel> pojoClass,
			String propertyName, String[] propertyValues);

	public int getRowCount(Pagin pagin);
	
	void findObjectBySql(String queryString, List<?> conditions, Pagin pagin);
	
	List<Object[]> findListObjectsBySql(String queryString,Pagin pagin);

	public <T> List<T> findByPaginN(Pagin pagin);

}
