package com.corsair.server.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 调用类方法
 * 
 * @author Administrator
 *
 */
public class CallFounctions {
	/**
	 * 调用类方法
	 * 
	 * @param ClassName
	 * @param FunctionName
	 * @param ParmsXML
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings({ "unchecked", "null", "rawtypes" })
	public String CallFunction(String ClassName, String FunctionName,
			String ParmsXML) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		java.lang.Class clazz = null;
		Method cMethod = clazz.getMethod(FunctionName,
				new java.lang.Class[] { ParmsXML.getClass() });
		cMethod.invoke(null, new Object[] { ParmsXML });
		return "";
	}
}
