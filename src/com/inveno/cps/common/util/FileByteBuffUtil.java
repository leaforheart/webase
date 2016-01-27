package com.inveno.cps.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
/**
 * 文件远程传输工具类
 * @author XYL
 *
 */
public class FileByteBuffUtil {
	
	private FileByteBuffUtil(){}
	
	public static ByteBuffer fileToByteBuffer(File file,int capacity) {
		ByteBuffer buf = ByteBuffer.allocate(capacity);
		FileInputStream fis = null;
		FileChannel inChannel = null;
		try {
			fis = new FileInputStream(file);
			inChannel = fis.getChannel();
			inChannel.read(buf);
			buf.flip();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inChannel!=null) {
				try {
					inChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buf;
	}
	
	public static void byteBufferToFile(ByteBuffer buf,String dir,String fileName) throws Exception {
		String filePath="";
		if(dir.lastIndexOf(File.separator)+1!=dir.length()) {
			filePath = dir + File.separator+fileName;
		}else {
			filePath = dir + fileName;
		}
		File dirFile = new File(dir);
		if(!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File file = new File(filePath);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		FileChannel outChannel = null;
		try {
			fos = new FileOutputStream(file);
			outChannel = fos.getChannel();
			outChannel.write(buf);
		} catch (Exception e) {
			throw new Exception(e.getMessage()+":"+dir+fileName+"文件写入失败");
		} finally {
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outChannel!=null) {
				try {
					outChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getFileContents(String path) throws Exception {
		String content = null;
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
		    StringBuilder stringBuilder = new StringBuilder();
		    String ls = System.getProperty("line.separator");
			while( ( line = reader.readLine() ) != null ) {
	            stringBuilder.append( line );
	            stringBuilder.append( ls );
	        }
			content = stringBuilder.toString();
		} catch (Exception e) {
			throw new Exception("getFileContents"+e.getMessage());
		} finally {
			if(reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return content;
	}
	
	public static int deleteFile(String path) {
		File file = new File(path);
		
		if(!file.exists()) {
			return 0;
		}else if(file.isFile()) {
			file.delete();
			return 1;
		}else {
			int i = getDelCount(file,0);
			file.delete();
			return i;
		}
	}
	
	private static int getDelCount(File file,int count) {
		File[] subFiles = file.listFiles();
		int i = count;
		for(File subFile:subFiles) {
			if(subFile.isFile()) {
				i++;
				subFile.delete();
			}else {
				i = getDelCount(subFile,i);
				subFile.delete();
			}
		}
		return i;
	}
}
