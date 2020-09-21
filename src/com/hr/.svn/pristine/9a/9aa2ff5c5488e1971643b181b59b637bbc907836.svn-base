package com.hr.util.hrmail;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dom4j.Element;

public class SOAPUtil {

	/**
	 * 获取POJO某个字段SOAP值
	 * 
	 * @param pojo
	 * @param fdname
	 * @return
	 */
	public static String getFieldXMLStr(Object pojo, String fdname) {
		String value = null;
		try {
			Field field = pojo.getClass().getDeclaredField(fdname);
			if (field != null) {
				PropertyDescriptor pd = new PropertyDescriptor(fdname, pojo.getClass());
				Method getMethod = pd.getReadMethod();
				if (getMethod != null) {
					Object o = getMethod.invoke(pojo);
					if (o != null)
						value = o.toString();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ((value == null) || value.isEmpty()) {
			return "<eip:" + fdname + " xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>";
		} else {
			return "<eip:" + fdname + ">" + value + "</eip:Address>";
		}
	}

	public static void putField2XML(Object pojo, Element em) {
		try {
			Field[] fields = pojo.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field != null) {
					String value = null;
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), pojo.getClass());
					Method getMethod = pd.getReadMethod();
					if (getMethod != null) {
						Object o = getMethod.invoke(pojo);
						if (o != null) {
							value = o.toString();
						}
					}
					if ((value == null) || value.isEmpty()) {
						// rst = rst + "<eip:" + field.getName() + " xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>";
					} else {
						em.addElement("eip:" + field.getName()).setText(value);
						// rst = rst + "<eip:" + field.getName() + ">" + value + "</eip:" + field.getName() + ">";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取POJO所有字段SOAP值
	 * 
	 * @param pojo
	 * @return
	 */
	public static String getFieldXMLStr(Object pojo) {
		String rst = "";
		try {
			Field[] fields = pojo.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field != null) {
					String value = null;
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), pojo.getClass());
					Method getMethod = pd.getReadMethod();
					if (getMethod != null) {
						Object o = getMethod.invoke(pojo);
						if (o != null) {
							value = o.toString();
						}
					}
					if ((value == null) || value.isEmpty()) {
						// rst = rst + "<eip:" + field.getName() + " xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>";
					} else {
						rst = rst + "<eip:" + field.getName() + ">" + value + "</eip:" + field.getName() + ">";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rst;
	}

}
