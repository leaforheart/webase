package com.inveno.cps.common.hibernate.resulttranformer;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.property.ChainedPropertyAccessor;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.PropertyAccessorFactory;
import org.hibernate.property.Setter;
import org.hibernate.transform.ResultTransformer;

import com.inveno.base.BaseModel;

/**
 * 自定义的数据库字库转换成POJO
 * 
 * 2011-4-12
 * 
 * @author yaoyuan
 */
public class ColumnToBean implements ResultTransformer {
	private static final long serialVersionUID = 1L;
	private final Class<? extends BaseModel> resultClass;
	private Setter[] setters;
	private PropertyAccessor propertyAccessor;
	
	public ColumnToBean(Class<? extends BaseModel> resultClass) {
		if(resultClass==null) throw new IllegalArgumentException("resultClass cannot be null");
		this.resultClass = resultClass;
		propertyAccessor = new ChainedPropertyAccessor(new PropertyAccessor[] { PropertyAccessorFactory.getPropertyAccessor(resultClass,null), PropertyAccessorFactory.getPropertyAccessor("field")}); 		
	}

	public Object transformTuple(Object[] tuple, String[] aliases) {
		Object result;
		
		try {
			if(setters==null) {
				setters = new Setter[aliases.length];
				for (int i = 0; i < aliases.length; i++) {
					String alias = aliases[i];
					if(alias != null) {
						setters[i] = getSetterByColumnName(alias);
					}
				}
			}
			result = resultClass.newInstance();
			
			for (int i = 0; i < aliases.length; i++) {
				if(setters[i]!=null) {
					if(tuple[i] instanceof Integer)
						setters[i].set(result, String.valueOf(tuple[i]), null);
					else if (tuple[i] instanceof Byte)
						setters[i].set(result, String.valueOf(tuple[i]), null);
					//else if (tuple[i] instanceof BigDecimal)
					//	setters[i].set(result, ((BigDecimal)tuple[i]).doubleValue(), null);
					else
						setters[i].set(result, tuple[i], null);
				}
			}
		} catch (InstantiationException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		} catch (IllegalAccessException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		}
		
		return result;
	}

	private Setter getSetterByColumnName(String alias) {
		Field[] fields = resultClass.getDeclaredFields();
		if(fields==null || fields.length==0){
			throw new RuntimeException("实体"+resultClass.getName()+"不含任何属性");
		}
		String proName = alias.replaceAll("_", "").toLowerCase();
		for (Field field : fields) {
			if(field.getName().toLowerCase().equals(proName)){
				return propertyAccessor.getSetter(resultClass, field.getName());
			}
		}
		return null;
		//throw new RuntimeException("找不到数据库字段 ："+ alias + " 对应的POJO属性或其getter方法，比如数据库字段为USER_ID或USERID，那么JAVA属性应为userId");
	}

	@SuppressWarnings("unchecked")
	public List transformList(List collection) {
		return collection;
	}

}
