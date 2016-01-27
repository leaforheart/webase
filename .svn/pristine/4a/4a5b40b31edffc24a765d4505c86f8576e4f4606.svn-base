package com.inveno.cps.common.hibernate.resulttranformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Ognl;

import org.hibernate.HibernateException;
import org.hibernate.transform.ResultTransformer;

import com.inveno.util.StringUtil;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

/**
 * 支持属性为自定义对象的结果集转换的部份属性查询
 * 2009-3-30
 * @author yaoyuan
 */
@SuppressWarnings({"serial","unchecked"})
public class AliasToBean implements ResultTransformer {

	private static final OgnlUtil ognlUntil = new OgnlUtil();
	private static final Map<String,Boolean> context = new HashMap<String,Boolean>(1);
	static{
		context.put(ReflectionContextState.CREATE_NULL_OBJECTS, true);
	}
	
	/** POJO的class */
	private final Class resultClass;
//	/** 本次查询所用到的POJO中相应属性的对应SET方法集合 */
//	private Setter[] setters;
//	/** 属性访问器 */
//	private PropertyAccessor propertyAccessor;
//	/** POJO中的关联对象的属性名集合 */
//	private Map<String,Object> childPros;
//	/** POJO中的关联对象的属性名集合 */
//	private Map<String,Object> newChildPros;
//	/** POJO中的关联对象的属性值集合 */
//	private Map<Integer,Setter> tupleIndex;
	
	public AliasToBean(Class pojoClass) {
		if(pojoClass==null) throw new IllegalArgumentException("resultClass cannot be null");
		this.resultClass = pojoClass;
//		propertyAccessor = new ChainedPropertyAccessor(new PropertyAccessor[] { PropertyAccessorFactory.getPropertyAccessor(resultClass,null), PropertyAccessorFactory.getPropertyAccessor("field")}); 		
	}

	public List transformList(List collection) {
		return collection;
	}

	/**
	 * 结果集转换
	 * 2009-4-7
	 * @author yaoyuan
	 * @param tuple 属性值集合
	 * @param aliases 属性名集合
	 * @return 单个POJO实例--查询结果
	 */
	public Object transformTuple(Object[] tuple, String[] aliases) {
		try {
			Object root = resultClass.newInstance();
			for (int i = 0; i < aliases.length; i++) {
				if(StringUtil.isNotEmpty(aliases[i]))
				{
					Ognl.setValue(ognlUntil.compile(aliases[i]), context, root, tuple[i]);
				}
			}
			return root;
		} catch (Exception e) {
			throw new HibernateException(e.getMessage(),e);
		}
		/*
		Object result;
		
		try {
			if(setters==null) {//第一次装配POJO时，此集合为空，通过下面的步骤将其填充上
				childPros = new HashMap<String,Object>(3);
				tupleIndex = new HashMap<Integer, Setter>(3);
				setters = new Setter[aliases.length];
				for (int i = 0; i < aliases.length; i++) {
					String alias = aliases[i];//对每个属性找到对应的SET方法
					if(alias != null) {
						if(!alias.contains(".")){//如时没有点号的，直接调用其原来的方法可以找到SET方法
							setters[i] = propertyAccessor.getSetter(resultClass, alias);
						}else{//有点号的，通过下面的途径找到SET方法
							//找到点号前的部份对应的SET方法，
							setters[i] = propertyAccessor.getSetter(resultClass, alias.split("\\.")[0]);
							Object newInstance = null;
							//如果此集合原来已有这个对象
							if(childPros.containsKey(setters[i].getMethodName())){
								//找出这个对象
								newInstance = childPros.get(setters[i].getMethodName());
								//在这个对象里面找到点号后面部份对应的SET方法
								Setter setter = propertyAccessor.getSetter(newInstance.getClass(), alias.split("\\.")[1]);
								//先把这个方法记起来，下面要用到（封装查询结果集的下一条记录时要用到）
								tupleIndex.put(i,setter);
								//调用这个法，把值放入到对象中
								setter.set(newInstance, tuple[i], null);
								//把TUPLE集合的对应的值换成这个对象，下面setters[i]被调用时使用
								tuple[i] = newInstance;
							}else{//如果原来没有这个对象
								//创建这个法对应的参数的类型的实例
								Class clazz = setters[i].getMethod().getParameterTypes()[0];
								newInstance = clazz.newInstance();
								//把实例放到集合中
								childPros.put(setters[i].getMethodName(), newInstance);
								//在这个对象里面找到点号后面部份对应的SET方法
								Setter setter = propertyAccessor.getSetter(clazz, alias.split("\\.")[1]);
								//先把这个方法记起来，下面要用到（封装查询结果集的下一条记录时要用到）
								tupleIndex.put(i,setter);
								//调用这个法，把值放入到对象中
								setter.set(newInstance, tuple[i], null);
								//把TUPLE集合的对应的值换成这个对象，下面setters[i]被调用时使用
								tuple[i] = newInstance;
							}
						}
					}
				}
			}else if(childPros!=null && childPros.size()>0){//如果setters不空（封装查询结果集的下一条记录）
				if(newChildPros==null){
					newChildPros = new HashMap<String, Object>(childPros.size());
				}else{
					newChildPros.clear();
				}
				for (int index : tupleIndex.keySet()) {
					if(newChildPros.containsKey(setters[index].getMethodName())){//如果包含对应的对象属性，则调用对应的方法给这个对象的相应属性赋值
						tupleIndex.get(index).set(newChildPros.get(setters[index].getMethodName()), tuple[index], null);
					}else{
						//否则把这个对象放入集合，再执行和IF一样的逻辑
						newChildPros.put(setters[index].getMethodName(), childPros.get(setters[index].getMethodName()).getClass().newInstance());
						tupleIndex.get(index).set(newChildPros.get(setters[index].getMethodName()), tuple[index], null);
					}
					//把tuple集合的值换成此对象（原值只是这个对象中一个属性）
					tuple[index] = newChildPros.get(setters[index].getMethodName());
				}
			}
			//创建POJO对象
			result = resultClass.newInstance();
			//对每个属性调用SET方法
			for (int i = 0; i < aliases.length; i++) {
				if(setters[i]!=null) {
					setters[i].set(result, tuple[i], null);
				}
			}
		} catch (InstantiationException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		} catch (IllegalAccessException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		}
		//返回对象
		return result;*/
	}

}
