package com.inveno.cps.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CloneUtil {
		  public static<T> T clone(T o) {
				   T returnObj=null;
				   try {
				             ByteArrayOutputStream baos = new ByteArrayOutputStream();
				             ObjectOutputStream oos = new ObjectOutputStream(baos);
				             oos.writeObject(o);
				             ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				             ObjectInputStream ois= new ObjectInputStream(bais);
				             returnObj = (T)ois.readObject();
				   } catch (IOException e) {
				        e.printStackTrace();
				   } catch (ClassNotFoundException e) {
				       e.printStackTrace();
				   }
				   return returnObj;
		  }


}
