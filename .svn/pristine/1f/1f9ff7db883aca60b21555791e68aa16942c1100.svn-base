package com.inveno.cps.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;
/**
 * 数据校验工具 20151103，防止恶意注入
 * @author XYL
 *
 */
public class CheckUtil {
	
	private CheckUtil() {}
	
	private static CheckUtil cu = new CheckUtil();
	
	public static Map<String,String> check(String xmlPath,String modelName,Map<String,String> data,Map<String,String> data1) {
		return cu.cycleCheck(xmlPath, modelName, data, data1);
	}
	
	/**
	 * xiaoyilin
	 * @param data 用户可能输入的数据集合
	 * @param data1 用户不可能输入的数据集合
	 * @return
	 */
	private Map<String,String> cycleCheck(String xmlPath,String modelName,Map<String,String> data,Map<String,String> data1) {
		Map<String,String> result = new HashMap<String,String>();
		
		//1.对客户端可能传入的数据进行逐个校验
		if(!MapUtils.isEmpty(data)) {
			for(String key:data.keySet()) {
				String value = data.get(key);
				result = getSingleCheckResult(xmlPath,modelName,key,value);
				if(!"N".equals(result.get("rCode"))&&!"C".equals(result.get("rCode"))) {
					return result;
				}
				//检测sql注入和脚本注入
				Map<String,String> map = checkInject(key,value);
				if("InjectError".equals(map.get("rCode"))) {
					return map;
				}
			}
		}
		
		
		//2.对客户端不可能传入的数据进行是否为初始值的检查，防止恶意注入
		if(!MapUtils.isEmpty(data1)) {
			for(String key:data1.keySet()) {
				String value = data1.get(key);
				if(!"null".equals(value)&&!"0".equals(value)&&!"false".equals(value)&&value!=null) {
					result.put("rCode", "InjectError");
					result.put("des", key+":注入攻击，客户端不可输入此数据");
					return result;
				}
			}
		}
	
		//3.校验成功
		result.put("rCode", "C");
		result.put("des", "校验成功");
		return result;
	}
	
	private Map<String,String> checkInject(String key,String value) {
		Map<String,String> map = new HashMap<String,String>();
		
		//SQL注入检测，首先检测 (' -- ;),然后检测sql关键字，如果两者都存在，极有可能是恶意注入，屏蔽之。 
		Pattern r1 = Pattern.compile("(')|select|update|and|or|delete|insert|trancate|substr|declare|exec|count|into|drop|execute|union|alter|create",Pattern.CASE_INSENSITIVE);
		Pattern r2 = Pattern.compile("(')|(--)|(;)");
		
		boolean flag1 = r1.matcher(value).find();
		boolean flag2 = r2.matcher(value).find();
		
		if(flag1&&flag2) {
			map.put("rCode", "InjectError");
			map.put("des", key+":SQL注入攻击,检查该字段是否包含sql关键字或特殊符号(' -- ;)");
			return map;
		}
		
		//js脚本注入检测，如果存在（< >）对，就极有可能是恶意注入，屏蔽之。
		Pattern r3 = Pattern.compile("<|%3c|%3C");
		Pattern r4 = Pattern.compile(">|%3e|%3E");
		
		boolean flag3 = r3.matcher(value).find();
		boolean flag4 = r4.matcher(value).find();
		
		if(flag3||flag4) {
			map.put("rCode", "InjectError");
			map.put("des", key+":JS注入攻击,检查该字段是否包含（<>）");
			return map;
		}
		map.put("rCode", "C");
		return map;
	}
	
	private Map<String,Object> fieldMap = new HashMap<String,Object>();
	
	private Map<String,String> getSingleCheckResult(String xmlPath,String modelName,String fieldName,String value) {
		Map<String,String> map = new HashMap<String,String>();
		Element field = (Element) fieldMap.get(xmlPath+"_"+modelName+"_"+fieldName);
		if(field==null) {
			field = getField(xmlPath,modelName,fieldName);
		}
		if(field==null) {
			map.put("rCode", "N");
			map.put("des", "没找到相关校验项");
			return map;
		}else {
			fieldMap.put(xmlPath+"_"+modelName+"_"+fieldName, field);
		}
		
		Attribute minA = field.attribute("minLength");
		Attribute maxA = field.attribute("maxLength");
		String minS = "0";
		if(minA!=null) {
			minS = minA.getValue();
		}
		String maxS = "0";
		if(maxA!=null) {
			maxS = maxA.getValue();
		}
		int min = 0;
		if(Pattern.compile("^[1-9]\\d*$").matcher(minS).matches()) {
			min = Integer.parseInt(minS);
		}
		int max = 0;
		if(Pattern.compile("^[1-9]\\d*$").matcher(maxS).matches()) {
			max = Integer.parseInt(maxS);
		}
		
		Attribute isEssentialA = field.attribute("isEssential");
		String isEssential = "n";
		if(isEssentialA!=null) {
			isEssential = isEssentialA.getValue();
			if(!"n".equals(isEssential)&&!"y".equals(isEssential)) {
				isEssential = "n";
			}
		}
		
		Attribute regA = field.attribute("reg");
		String reg = "";
		if(regA!=null) {
			reg = regA.getValue();
		}
		
		Attribute rCodeA = field.attribute("rCode");
		Attribute desA = field.attribute("des");
		String rCode = "E-1";//如果没有指定错误码，就用E-1,最好自己指定。
		if(rCodeA!=null) {
			rCode = rCodeA.getValue();
		}
		String des = fieldName + "格式错误";
		if(desA!=null) {
			des = desA.getValue();
		}
		map.put("rCode", rCode);
		map.put("des", des);
		
		boolean isEmpty = StringUtils.isEmpty(value);
		if(isEmpty&&"n".equals(isEssential)) {
			map.put("rCode", "C");
			map.put("des", "校验成功");
			return map;
		}else if(isEmpty&&"y".equals(isEssential)) {
			map.put("des", des+":不可为空");
			return map;
		}
		
		int len = value.length();
		if((len<min||len>max)&&!(min==0&&max==0)) {
			map.put("des", des+":长度不符合要求");
			return map;
		}
		
		if("".equals(reg.trim())) {
			map.put("rCode", "C");
			map.put("des", "校验成功");
			return map;
		}
		Pattern r = Pattern.compile(reg);
		boolean flag = r.matcher(value).matches();
		
		if(!flag) {
			return map;
		}
		
		map.put("rCode", "C");
		map.put("des", "校验成功");
		return map;
	} 
	
	private Map<String,Object> docMap = new HashMap<String,Object>();
	
	@SuppressWarnings("unchecked")
	private Element getField(String xmlPath,String modelName,String fieldName) {
		//首先从Map中取配置文件对象
		Document doc = (Document) docMap.get("xmlPath");
		//如果为空，就从xml中load
		if(doc==null) {
			doc = getDocument(xmlPath);
		}
		//如果load之后还是为空，就不再校验，直接返回
		if(doc==null) {
			return null;
		}else {
			docMap.put(xmlPath, doc);
		}
		Element root = doc.getRootElement();
		Iterator<Element> iterator1 = root.elementIterator();
		Element model = null;
		while(iterator1.hasNext()) {
			Element temp = iterator1.next();
			if(modelName.equals(temp.attribute("name").getValue())) {
				model = temp;
				break;
			}
		}
		
		Iterator<Element> iterator2 = null;
		Element field = null;
		if(model==null) {
			return null;
		}
		iterator2 = model.elementIterator();
		while(iterator2.hasNext()) {
			Element temp = iterator2.next();
			if(fieldName.equals(temp.attribute("name").getValue())) {
				field = temp;
				break;
			}
		}

		return field;
	}
	
	private Document getDocument(String xmlPath) {
		DOMReader reader = new DOMReader();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		org.w3c.dom.Document document = null;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(xmlPath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Document doc = reader.read(document);
		return doc;
	}
}
