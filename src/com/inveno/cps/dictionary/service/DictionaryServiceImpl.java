package com.inveno.cps.dictionary.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.inveno.base.Pagin;
import com.inveno.cps.common.baseclass.AbstractBaseService;
import com.inveno.cps.common.util.Constants;
import com.inveno.cps.dictionary.dao.DictionaryDao;
import com.inveno.cps.dictionary.model.Dd;
import com.inveno.cps.dictionary.thrift.Dictionary;
import com.inveno.cps.dictionary.thrift.DictionaryService;
import com.inveno.util.JsonUtil;
import com.inveno.util.StringUtil;

public class DictionaryServiceImpl extends AbstractBaseService implements DictionaryService.Iface {
	
	private Logger log = Logger.getLogger(DictionaryServiceImpl.class);
	
	private DictionaryDao dictionaryDao;
	
	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public DictionaryDao getDictionaryDao() {
		return dictionaryDao;
	}

	public void setDictionaryDao(DictionaryDao dictionaryDao) {
		this.dictionaryDao = dictionaryDao;
	}
	/**
	 * 查询所有字典列表
	 */
	@Override
	public Map<String, String> queDictionary(Map<String, String> queryMap) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			String parentId = queryMap.get("parentId");
			String memo = queryMap.get("memo");
			String page = queryMap.get("page");
			String rows = queryMap.get("rows");
			
			Dd dd = new Dd();
			dd.setParentId(parentId);
			dd.setMemo(memo);
			
			
			Pagin pagin = getPagin(page,rows);
			String[] columNames = { "id", "parentId", "childType", "code","typeName", "properties", "status", "memo", "createTime","lastUpdTime" };
			DetachedCriteria criteria = null;
			criteria = setConditions(pagin, columNames, Dd.class, "o");
			// 组装查询条件
			this.assembleConditions(criteria, dd, "o");
			// 根据创建日期升序
			pagin.getDefaultOrders(true).add(Order.desc("o.parentId"));
			// 使用系统统一权限
			dictionaryDao.findByPaginN(pagin);
			
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromPagin(pagin));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	
	private Pagin getPagin(String page,String rows) {
		Pagin pagin = new Pagin();
		if(StringUtil.isNotEmpty(page)) {
			pagin.setToPage(Integer.parseInt(page));
		}
		if(StringUtil.isNotEmpty(rows)) {
			pagin.setPageSize(Integer.parseInt(rows));
		}
		return pagin;
	} 
	
	private void assembleConditions(DetachedCriteria detachedCriteria,
			Dd dd, final String alias) {
		String tempStr = "";// 定义临时字符串变量
		// 类型Id查询条件
		tempStr = dd.getParentId();
		if (StringUtil.isNotEmpty(tempStr)) {
			detachedCriteria.add(Restrictions.eq(alias + ".parentId",tempStr.trim()).ignoreCase());
		}
		
		// 类型名称模糊条件查询
		tempStr = dd.getMemo();
		if(StringUtil.isNotEmpty(tempStr)){
			detachedCriteria.add(Restrictions.like(alias + ".memo", tempStr, MatchMode.ANYWHERE).ignoreCase());
		}
	}

	@Override
	public Map<String, String> addDictionary(Dictionary dictionary) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			Dd dd = new Dd();
			String parentId = dictionary.getParentId();
			String memo = dictionary.getMemo();
			if(StringUtil.isEmpty(memo)) {
				map.put(Constants.RETURN_CODE, "-1");//父类型名称不能为空
				return map;
			}
			if(StringUtil.isEmpty(parentId)) {
				parentId = getParentId();
			} 
			dd.setParentId(parentId);
			dd.setMemo(memo);
			
			dd.setCode(dictionary.getCode());
			
			if(StringUtil.isEmpty(dictionary.getChildType())) {
				map.put(Constants.RETURN_CODE, "-2");//子类型不能为空
				return map;
			}
			if(childTypeExist(parentId,dictionary.getChildType())) {
				map.put(Constants.RETURN_CODE, "-3");//子类型不能重复
				return map;
			}
			if(StringUtil.isEmpty(dictionary.getTypeName())) {
				map.put(Constants.RETURN_CODE, "-4");//子类型名称不能为空
				return map;		
			}
			if(dictionary.getType()!=1||dictionary.getType()!=2) {
				map.put(Constants.RETURN_CODE, "-5");//类型操作属性错误
				return map;		
			}
			dd.setChildType(dictionary.getChildType());
			dd.setTypeName(dictionary.getTypeName());
			dd.setType(dictionary.getType());
			dd.setCreateTime(new Date());
			dd.setLastUpdTime(new Date());
			
			dictionaryDao.save(dd);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	
	private boolean childTypeExist(String parentId,String childType) {
		boolean flag = false;
		List<String> parameters = new ArrayList<String>();
		parameters.add(parentId);
		parameters.add(childType);
		List<Dd> list = dictionaryDao.findByHql("from Dd where parentId=? and childType=?", parameters);
		if(list.size()>0) {
			flag = true;
		}
		return flag;
	}
	
	private String getParentId() {
		String strSql = "select max(parent_id) from dd_table";
		List<Object> listObj = dictionaryDao.findBySql(strSql, null);
		Object[] tempObj = (Object[]) listObj.get(0);
		int i = Integer.parseInt(tempObj[0].toString()) + 1;
		String s = String.valueOf(i);
		return s;
	}

	@Override
	public Map<String, String> updDictionary(Dictionary dictionary) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			String id = dictionary.getId();
			if(StringUtil.isEmpty(id)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			Dd dd = dictionaryDao.findById(id, Dd.class);
			String parentId = dictionary.getParentId();
			if(StringUtil.isEmpty(parentId)) {
				map.put(Constants.RETURN_CODE, "-2");
				return map;
			}
			String memo = dictionary.getMemo();
			String code = dictionary.getCode();
			String typeName = dictionary.getTypeName();
			int type = dictionary.getType();
			
			if(memo!=null&&!memo.equals(dd.getMemo())) {
				List<String> parameters = new ArrayList<String>();
				parameters.add(memo);
				parameters.add(parentId);
				dictionaryDao.excuteSql("update dd_table set memo=? where parent_id=?", parameters);
				dd.setMemo(memo);
			}
			
			if(StringUtil.isEmpty(code)) {
				dd.setCode(code);
			}
			
			if(StringUtil.isEmpty(typeName)) {
				dd.setTypeName(typeName);
			}
			
			if(type==1||type==2) {
				dd.setType(type);
			}
			
			dictionaryDao.update(dd);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}

	@Override
	public Map<String, String> getDictionary(String id) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			Dd dd = dictionaryDao.findById(id, Dd.class);
			
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrByConfigFromObj(dd));
			
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}

	@Override
	public Map<String, String> queParentType() {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			List<Object> list = dictionaryDao.findByHql("select distinct parentId,memo from Dd", null);
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(list));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}

	@Override
	public Map<String, String> getParentType(String parentId) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			if(StringUtil.isEmpty(parentId)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			List<String> parameters = new ArrayList<String>();
			parameters.add(parentId);
			List<Object> list = dictionaryDao.findByHql("from Dd where parentId=?", parameters);
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(list));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}

	@Override
	public void ping() {
		
	}

}
