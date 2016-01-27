package com.inveno.cps.common.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

/**
 * 文件上传工具类
 * @author XYL
 *
 */
public class FileUploadUtil {
	public static Document getDoc(String xmlPath) {
		DOMReader reader = new DOMReader();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		org.w3c.dom.Document document = null;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(xmlPath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document doc = reader.read(document);
		return doc;
	}
	
	public static String getUuidName(String filedataFileName) {
		String[] partName = filedataFileName.split("\\.");
		int maxLen = partName.length - 1;
		String fileNameTemp = partName[maxLen];
		return System.nanoTime() + "." + fileNameTemp;
	}
}
