package com.inveno.cps.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtil {
	
	public static String getProperty(String key) {
		return getProperty("cps.properties",key);
	}
	
	public static String getProperty(String path,String key) {
		String prePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
		path = prePath +path;
		Properties pro = new Properties();
		URL url = null;
		File file = null;
		try {
			if(path.indexOf("file")>=0) {
				url = new URL(path);
				file = new File(url.getFile());
			}else {
				file = new File(path);
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		InputStream input;
		try {
			if(path.indexOf("rsrc")>=0) {
				input = PropertiesUtil.class.getClassLoader().getResourceAsStream(new URL(path).getFile());
			}else {
				input = new FileInputStream(file);
			}
			pro.load(input);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		
		String value = pro.getProperty(key).trim();
		pro.clear();
		
		if(value==null) {
			value = "";
		}
		
		return value;
	}

}
