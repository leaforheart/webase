package com.inveno.cps.common.baseclass;

import com.inveno.base.BaseModel;
import com.inveno.cps.common.exception.BusinessException;
import com.opensymphony.xwork2.Action;

/**
 * 业务层顶层接口 这里可以全部继承事件接口，不会影响性能，因为如果具体Service类没有重载getEventClasses方法就等于没有实现此接口
 * 
 * 2010-1-12
 * 
 * @author yaoyuan
 * @version 1.0
 */
public interface IBaseService {
	
	String SUCCESS = Action.SUCCESS;
	String INPUT = Action.INPUT;
	String ERROR = Action.ERROR;
	String NONE = Action.NONE;
	String LOGIN = Action.LOGIN;

	/**
	 * 保存一个新对象到数据库
	 * 
	 * 2010-1-12
	 * 
	 * @param obj
	 *            要保存的对象
	 */
	<T extends BaseModel> Object save(Object obj) throws BusinessException;

	/**
	 * 更新对象
	 * 
	 * 2010-1-12
	 * 
	 * @param obj
	 *            要更新的对象
	 * 
	 */
	<T extends BaseModel> Object update(Object obj) throws BusinessException;

	/**
	 * 删除对象
	 * 
	 * @param obj
	 *            要删除的对象
	 */
	void delete(Object obj) throws BusinessException;

	/**
	 * 根据主键ID查找一个对象
	 * 
	 * @param id
	 *            主键ID
	 * @return 返回查找到的对象
	 */
	<T extends BaseModel> Object findById(Object id,Class<? extends BaseModel> pojoClass) throws BusinessException;



}
