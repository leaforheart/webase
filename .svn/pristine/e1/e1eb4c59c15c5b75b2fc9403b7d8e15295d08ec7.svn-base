package com.inveno.cps.common.baseclass;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.inveno.base.BaseModel;
import com.inveno.base.Pagin;
import com.inveno.cps.common.exception.BusinessException;
import com.inveno.cps.common.hibernate.resulttranformer.ColumnToBean;
import com.inveno.cps.common.util.SysContext;
import com.inveno.util.CollectionUtils;
import com.inveno.util.DetachedCriteriaUtil;
import com.inveno.util.PainUtils;
import com.inveno.util.StringUtil;

/***
 * 业务层DAO底层类
 * 此类集合了所有与数据库交互的增删改查操作，在下一层具体项目的dao只要继承了此类，则可使用此类中方法，即可实现对数据库的增删改查
 * Xjun 2014-8-7 edit
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBaseDAO extends HibernateDaoSupport implements IBaseDAO {
	
	protected static final Logger log = SysContext.getLogger();

	/**
	 * 保存一个实体对象 
	 * @param object
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T extends BaseModel> T save(Object object) {
		this.getHibernateTemplate().save(object);
		return (T) object;
	}

	/**
	 * 删除一个实体对象
	 * @param object
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	
	public void delete(Object object) {
		getHibernateTemplate().delete(object);
	}

	/**
	 * 更新一个实体对象
	 * @param object
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T extends BaseModel> T update(Object object) {
		this.getHibernateTemplate().update(object);
		return (T) object;
	}
	
	/**
	 * 新增或更新一个实体对象
	 * @param object
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T extends BaseModel> T saveOrUpdate(Object object) {
		this.getHibernateTemplate().saveOrUpdate(object);
		return (T) object;
	}

	/**
	 * 通过实体对象的主键id查找实体对象，但返回的实体对象是子类重写getPojoClass()方法时自定义返回的对象
	 * @param id
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T extends BaseModel> T findById(Object id) {
		return (T) findById(id, getPojoClass());
	}

	/**
	 * 通过实体对象的主键id查找实体对象，返回的实体对象参数传过来的实体class
	 * @param id,pojoClass
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T extends BaseModel> T findById(Object id,Class<? extends BaseModel> pojoClass) {
		try {
			return (T) getHibernateTemplate().get(pojoClass, (Serializable) id);
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new BusinessException("findById出异常.",e);
		}
		
	}

	/**
	 * 合并更新实体对象,更新前会先判断此对象与数据库的是否一致，如果一致则只做select操作，不一致才执行更新操作
	 * @param object
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public Object merge(Object object) {
		return getHibernateTemplate().merge(object);
	}

	/**
	 * 强制把缓存中的数据同步到数据库
	 * @param object
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public void flush() {
		getHibernateSession().flush();
	}

	/**
	 * 把hibernate的一级缓存数据清除
	 * @param object
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public void clearSession() {
		getHibernateSession().clear();
	}

	/**
	 * 根据detachedCriteria封装的查询条件查询对象list
	 * @param criteria
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T> List<T> findByDetachedCriteria(DetachedCriteria criteria) {
		return (List<T>) this.getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * 根据detachedCriteria封装的查询条件,并可根据begin和pageSize查询某部份对象list
	 * @param criteria
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T> List<T> findByDetachedCriteria(DetachedCriteria criteria,int begin, int pageSize) {
		return (List<T>) this.getHibernateTemplate().findByCriteria(criteria,begin, pageSize);
	}


	/**
	 * 根据pagin参数获取查询对象list的总条数
	 * @param criteria
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public int getRowCount(Pagin pagin) {
		if (pagin.getDetachedCriteria() == null) {
			throw new BusinessException("detachedCriteria对象为空");
		}
		String idName = pagin.getColumForCount();
		if (StringUtil.isEmpty(idName)) {
			idName = "id";
		}
		if (pagin.isCountWithDisctinct()) {// 是否使用distinct
			pagin.getDetachedCriteria().setProjection(
					Projections.projectionList().add(
							Projections.countDistinct(idName)));
		} else {
			pagin.getDetachedCriteria()
					.setProjection(
							Projections.projectionList().add(
									Projections.count(idName)));
		}

		List list = findByDetachedCriteria(pagin.getDetachedCriteria(), 0, 1);
		if (list == null || list.size() == 0){
			return 0;
		}
		return Integer.valueOf(list.get(0).toString());
	}
	
	
	/**
	 * 根据pagin参数封装查询条件
	 * @param criteria
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	private void appendPermissionCondition(Pagin pagin){
		this.selectColumn(pagin.getDetachedCriteria(), pagin.getSelectedColumns(),pagin.getPojoClass(), pagin.getAlias(),pagin);
	}

	/**
	 * 此方法主要供继承的子类重写，返回子类所需要具体的哪个实体对象
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	protected Class<? extends BaseModel> getPojoClass() {
		return null;
	}

	/**
	 * 此方法通过传入的HQL语句、查询参数、查询对象、分页参数，查询指定对象list
	 * @return
	 * Xjun 2014-08-07 edit
	 * 用户组装的HQL语句：格式为：select a.属性名1 as 属性名1，a.属性名2 as 属性名2 from pojo a where a.属性名＝:key
	 * @return
	 */
	public <T extends BaseModel> List<T> findObjectByHql(String queryString,
			List<Object> conditions, Class<T> pojoClass, Pagin pagin) {

		Query query = null;
		int i = 0, j = 0;
		if (conditions != null) {
			i = conditions.size();
		}
		// 查询总行数
		if (pagin.isRecountTotalRow() || pagin.getTotalRows() <= 0) {
			int fromIndex = queryString.toLowerCase().indexOf("from");
			StringBuilder hql = new StringBuilder("select count(");
			if (StringUtil.isNotEmpty(pagin.getAlias())) {
				hql.append(pagin.getAlias()).append(".");
			}
			hql.append(pagin.getColumForCount()).append(") ").append(queryString.substring(fromIndex));
			query = getSession().createQuery(hql.toString());

			if (i > 0) {
				// 绑定查询条件
				for (Object object : conditions) {
					query.setParameter(j++, object);
				}
				j = 0;
			}
			List result = query.list();
			if (!result.isEmpty()) {
				pagin.setTotalRows(Integer.valueOf(result.get(0).toString()));
			}

		}

		// 查询结果集
		query = this.getSession().createQuery(queryString);
		if (i > 0) {
			// 绑定查询条件
			for (Object object : conditions) {
				query.setParameter(j++, object);
			}
		}
		query.setFirstResult(pagin.getFromRow());
		query.setMaxResults(pagin.getPageSize());
		query.setResultTransformer(Transformers.aliasToBean(pojoClass));
		pagin.setObjectList(query.list());

		return (List<T>) pagin.getObjectList();
	}

	/**
	 * 此方法通过传入的HQL语句、查询参数、查询对象,查询指定对象list
	 * @return
	 * Xjun 2014-08-07 edit
	 * @return
	 */
	public <T extends BaseModel> List<T> findObjectBySql(String queryString,
			List<?> conditions, Class<T> pojoClass) {
		Query query = null;
		int i = 0, j = 0;
		if (conditions != null) {
			i = conditions.size();
		}
		// 查询结果集
		query = this.getSession().createSQLQuery(queryString);
		if (i > 0) {
			// 绑定查询条件
			j = 0;
			for (Object object : conditions) {
				query.setParameter(j++, object);
			}
		}
		query.setResultTransformer(new ColumnToBean(pojoClass));
		return (List<T>) query.list();
	}
	
	
	/**
	 * 此方法通过传入的HQL语句、查询参数、查询对象,查询出抽象对象list放存pain的objectList中
	 * Xjun 2014-08-07 edit
	 * @return
	 */
	public void  findObjectBySql(String queryString, List<?> conditions, Pagin pagin) {
		
		Query query = null;
		int i = 0, j = 0;
		if (conditions != null) {
			i = conditions.size();
		}
		 //查询总行数
		if(pagin.isRecountTotalRow() || pagin.getTotalRows()<=0){
			StringBuilder sql = new StringBuilder(50);
			if(!pagin.isReport()){//不是报表查询
				int fromIndex = queryString.toLowerCase().indexOf("from");
				sql = new StringBuilder("select count(");
				if(StringUtil.isNotEmpty(pagin.getAlias())){
					sql.append(pagin.getAlias()).append(".");
				}
				sql.append(pagin.getColumForCount()).append(")").append(queryString.substring(fromIndex));
			}else{//是报表查询
				sql.append("select count( * ) from ( "+queryString+" ) as v");
			}
			
			query = getSession().createSQLQuery(sql.toString());
			if(i>0){
				 //绑定查询条件
				for (Object object : conditions) {
					 query.setParameter(j++, object);
				}
			}
			pagin.setTotalRows(Integer.valueOf(query.list().get(0).toString()));
		 }
		 

		// 查询结果集
		query = this.getSession().createSQLQuery(queryString);
		if (i > 0) {
			// 绑定查询条件
			j = 0;
			for (Object object : conditions) {
				query.setParameter(j++, object);
			}
		}
		query.setFirstResult(pagin.getFromRow());
		query.setMaxResults(pagin.getPageSize());
		query.setResultTransformer(new ColumnToBean(pagin.getPojoClass()));
		pagin.setObjectList(query.list());
	}
	
	/**
	 * 查询动态列分页数据
	 * @author wangzhiwen
	 * @date 2012-6-13
	 * @param queryString 查询SQL，已民组装完成的SQL
	 * @param pain 需要page
	 * @return List<Object[]> 数据对象数组
	 */
	
	public List<Object[]> findListObjectsBySql(String queryString,Pagin pagin){
		//查询SQL数据的总行数
		Query query = null;
		if(pagin.isRecountTotalRow() || pagin.getTotalRows()<=0){
			StringBuilder sql = new StringBuilder(50);
			
			sql.append("select count( * ) from ( "+queryString+" ) as v");
			
			
			query = getSession().createSQLQuery(sql.toString());
			pagin.setTotalRows(Integer.valueOf(query.list().get(0).toString()));
		 }
		//为SQL后面添加查询从多少行开始，查询多少行的条件
		queryString = queryString + " LIMIT " + pagin.getFromRow() + " , " + pagin.getPageSize();
		List<Object[]> list =  findBySql(queryString,null);//返回查询结果
		return list;
	}

	
	/**
	 * 标准分页查询数据 
	 * @param pain
	 * @return
	 * Xjun 2014-08-07 edit
	 */
	public <T> List<T> findByPaginN(Pagin pagin) {
		if (pagin.getDetachedCriteria() == null) {
			throw new RuntimeException("查询时pagin中的detachedCriteria对象为空");
		}
		
		try {
			PainUtils.initDefaultOrders(pagin, null);
			//组装分字段、权限查询功能
			appendPermissionCondition(pagin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List list = findByDetachedCriteria(pagin.getDetachedCriteria(), pagin
				.getFromRow(), pagin.getPageSize());
		pagin.setObjectList(list);
		
		if (pagin.isRecountTotalRow() || pagin.getTotalRows() <= 0) {
			pagin.setTotalRows(getRowCount(pagin));
		}
		
		return (List<T>) pagin.getObjectList();
	}

	/**
	 * 往DetachedCriteria对象中封闭要查询的字段
	 * Xjun 2014-08-07 edit
	 * @param columName  字符串数组，以数据的形式接收要查询的字段属性，如String[] colum={"属性1","属性2","属性3"};
	 * @param pojoClass  实体类的Class,如Mobile.class;
	 * @param pagin
	 * @param aials      为要查询的POJO对象指定一个别名
	 * @return DetachedCriteria 对象
	 */
	
	private void selectColumn(DetachedCriteria criteria, String[] columName,
			Class<? extends BaseModel> pojoClass, String alias, Pagin pagin) {
		if (pagin.getPojoClass() == null) {
			pagin.setPojoClass(pojoClass);
		}
		if (pagin.getAlias() == null) {
			pagin.setAlias(alias);
		}

		DetachedCriteriaUtil.selectColumn(criteria, columName, pojoClass, pagin
				.isJoinTable());
	}

	/**
	 * 根据SQL查询数据
	 * Xjun   2014-08-07 edit
	 * @param sql 查询SQL字符串
	 * @param parameters 查询条件集合
	 */
	public <T> List<T> findBySql(String sql, List<?> parameters) {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = getConnection().prepareStatement(sql);
			if (parameters != null && parameters.size() > 0) {
				stmt = getConnection().prepareStatement(sql);
				int size = parameters.size();
				for (int i = 0; i < size; i++) {
					stmt.setObject(i + 1, parameters.get(i));
				}
			}
			rs = stmt.executeQuery();
			if (rs != null) {
				List<Object[]> list = new ArrayList<Object[]>();
				int columnCount = rs.getMetaData().getColumnCount();
				while (rs.next()) {
					Object[] obj = new Object[columnCount];
					for (int j = 0; j < columnCount; j++) {
						obj[j] = rs.getObject(j + 1);
					}
					list.add(obj);
				}
				return (List<T>) list;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return new ArrayList<T>(0);
	}

	/**
	 * 根据HQL查询数据
	 * Xjun   2014-08-07 edit
	 * @param sql 查询SQL字符串
	 * @param parameters 查询条件集合
	 */
	public <T> List<T> findByHql(String hql, List<?> parameters) {
		if (parameters != null) {
			return (List<T>) getHibernateTemplate().find(hql,
					parameters.toArray());
		} else {
			return (List<T>) getHibernateTemplate().find(hql, null);
		}

	}

	/**
	 * 执行指定SQL语句
	 * Xjun   2014-08-07 edit
	 * @param sql
	 * @param parameter sql语句where参数
	 * return
	 */
	public void excuteSql(String sql, List<?> parameters) {
		SQLQuery query = this.getSession().createSQLQuery(sql);
		// 创建Query查询对象
		if (parameters != null) {
			// 绑定查询条件
			int size = parameters.size();
			for (int i = 0; i < size; i++) {
				query.setParameter(i, parameters.get(i));
			}
		}
		query.executeUpdate();
	}
	
	/**
	 * 执行指定HQL语句
	 * Xjun   2014-08-07 edit
	 * @param hql
	 * @param parameter hql语句where参数
	 * return
	 */
	public void excuteHql(String hql, List<?> parameter) {
		if (parameter == null) {
			this.getHibernateTemplate().bulkUpdate(hql);
		} else {
			this.getHibernateTemplate().bulkUpdate(hql, parameter.toArray());
		}
	}

	/**
	 * 批量删除对象集合
	 * Xjun   2014-08-07 edit
	 * @param entities  被删除的对象集合
	 */
	public void deleteAll(Collection<? extends BaseModel> entities) {
		this.getHibernateTemplate().deleteAll(entities);
	}

	/**
	 * 根据数据的主键id数组，批量删除数据库数据
	 * Xjun   2014-08-07 edit
	 * @param entities  被删除的对象集合
	 */
	public int delete(Class<? extends BaseModel> pojoClass, String[] ids) {
		return delete(pojoClass, "id", ids);
	}

	/**
	 * 批量删除对象属性是某个范围值的数据，如userName in("张三","李四","王五")
	 * Xjun   2014-08-07 edit
	 * @param entities  被删除的对象集合
	 */
	public int delete(Class<? extends BaseModel> pojoClass,
			String propertyName, String[] propertyValues) {
		if (CollectionUtils.isNotEmpty(propertyValues)) {
			String hql = "DELETE " + pojoClass.getName()
					+ " as THIS_0 WHERE THIS_0." + propertyName + " in ("
					+ StringUtil.getPlaceHoldersForIn(propertyValues.length)
					+ ")";
			return getHibernateTemplate().bulkUpdate(hql, propertyValues);
		}
		return 0;
	}

	private Session getHibernateSession() {
		return super.getSession();
	}

	/**
	 * 获取hibernate数据库连接对象
	 * Xjun   2014-08-07 edit
	 */
	@SuppressWarnings("deprecation")
	public Connection getConnection() {
		return getHibernateSession().connection();
	}

}
