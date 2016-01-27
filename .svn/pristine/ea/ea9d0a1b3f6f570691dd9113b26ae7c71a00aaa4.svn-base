package com.inveno.cps.common.baseclass;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.inveno.base.BaseModel;
import com.inveno.base.Pagin;
import com.inveno.cps.common.exception.BusinessException;
import com.inveno.cps.common.util.SysContext;
import com.inveno.util.DetachedCriteriaUtil;



/**
 * CRM实现顶层接口的抽象类
 * 
 * @author yaoyuan
 * @version 1.0 date 2008-04-27
 */
public abstract class AbstractBaseService implements IBaseService {

	public static final Logger logger = SysContext.getLogger();

	public void delete(Object obj) throws BusinessException {
		SysContext.anyDao.delete(obj);
	}

	public <T extends BaseModel> Object findById(Object id,Class<? extends BaseModel> pojoClass) throws BusinessException {
		return SysContext.anyDao.findById(id,pojoClass);
	}

	public <T extends BaseModel> Object save(Object obj) throws BusinessException {
		return SysContext.anyDao.save(obj);
	}

	public <T extends BaseModel> Object update(Object obj) throws BusinessException {
		return SysContext.anyDao.update(obj);
	}


	
	/**
	 * 该方法提供DetachedCriteria对查询字段的封装(适用于单表查询)， 2008-9-29
	 * 
	 * @author yaoyuan
	 * @param columnNames
	 *            字符串数组，以数据的形式接收要查询的字段属性，如String[] colum={"属性1","属性2","属性3"};
	 * @param pojoClass
	 *            实体类的clazz,如Mobile.class;
	 * @param aials
	 *            为要查询的POJO对象指定一个别名
	 * @return DetachedCriteria 的一个对象，如果需要查询条件，在些对象后追加查询条件。
	 */
	protected DetachedCriteria getDetachedCriteriaByColumn(String[] columnNames, Class<? extends BaseModel> pojoClass, String alias) {
		return getDetachedCriteriaByColumn(columnNames, pojoClass, alias, false);
	}
	
	/**
	 * 该方法提供DetachedCriteria对查询字段的封装(用于多表查询，避免查出重复记录) Dec 3, 2008
	 * 
	 * @author yaoyuan
	 * @param columName
	 *            字符串数组，以数据的形式接收要查询的字段属性，如String[] colum={"属性1","属性2","属性3"};
	 * @param clazz
	 *            实体类的clazz,如Mobile.class;
	 * @param alias
	 *            为要查询的POJO对象指定一个别名
	 * @param forJoinTable 是否多表连接查询
	 * @return
	 */
	protected DetachedCriteria getDetachedCriteriaByColumn(String[] columnNames,
			Class<? extends BaseModel> clazz, String alias,boolean forJoinTable) {
		DetachedCriteria criteria = DetachedCriteria.forClass(clazz, alias);
		DetachedCriteriaUtil.selectColumn(criteria, columnNames, clazz, forJoinTable);
		return criteria;
	}


	
	
	
	/**
	 * 把查询条件等放入PAGIN对象，DAO中进行组合、查询
	 * @author yaoyuan
	 * 2009-8-11
	 * @param pagin
	 * @param columnName
	 * @param pojoClass
	 * @param alias
	 * @return
	 */
	protected DetachedCriteria setConditions(Pagin pagin , String[] columnName,
			Class<? extends BaseModel> pojoClass, String alias){
		return setConditions(pagin, columnName, pojoClass, alias, false);
	}
	
	/**
	 * 把查询条件等放入PAGIN对象，DAO中进行组合、查询
	 * @author yaoyuan
	 * 2009-8-11
	 * @param pagin
	 * @param columnName
	 * @param pojoClass
	 * @param alias
	 * @param joinTable 是否联表查询
	 * @return
	 */
	protected DetachedCriteria setConditions(Pagin pagin , String[] columnName,
			Class<? extends BaseModel> pojoClass, String alias,boolean joinTable){
		DetachedCriteria criteria = DetachedCriteria.forClass(pojoClass,alias);
		pagin.setDetachedCriteria(criteria);
		pagin.setSelectedColumns(columnName);
		pagin.setPojoClass(pojoClass);
		pagin.setAlias(alias);
		pagin.setJoinTable(joinTable);
		return criteria;
	}
	
	
	


	public final <T extends BaseModel> List<T> findByIds(List<String> ids,Class<T> clazz,String[] clumns){
		DetachedCriteria criteria = getDetachedCriteriaByColumn(clumns, clazz, "_model");
		criteria.add(Restrictions.in("_model.id", ids));
		return SysContext.anyDao.findByDetachedCriteria(criteria);
	}

	public boolean isHaveMarketId(String marketId) {
		
		
		return false;
	}
	
	
}
