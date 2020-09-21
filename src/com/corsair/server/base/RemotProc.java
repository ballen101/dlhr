package com.corsair.server.base;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.dbpool.util.Logsw;

/**
 * 远程方法
 * 
 * @author Administrator
 *
 */
public class RemotProc {

	// public String remotproc(String XMLStr) throws Exception {
	// Document document = DocumentHelper.parseText(XMLStr);
	// Element employees = document.getRootElement();
	//
	// String ACalssName = employees.element(ConstsSw.xml_class_name).getText();
	// String AFunctionName = employees.element(ConstsSw.xml_function_name).getText();
	// String AXMLStr = employees.element(ConstsSw.xml_inout_parms).getText();
	// // System.out.println("远程调用:"+ACalssName+'-'+AFunctionName+'-'+AXMLStr);
	// Logsw.debug("远程调用:" + ACalssName + '-' + AFunctionName + '-' + AXMLStr);
	// Class<?> classType = Class.forName(ACalssName);
	// Object rpcobject = classType.newInstance();
	// Method method = classType.getMethod(AFunctionName, new Class[] { String.class });
	// Object rsobject = method.invoke(rpcobject, AXMLStr);
	// String result = rsobject.toString();
	//
	// return CodeXML.AddCodeStr(ConstsSw.code_of_ExecRemotProc, result);
	// }
	//
	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// public String romotprocEx(String xmlstr) throws Exception {
	// Document document = DocumentHelper.parseText(xmlstr);
	// Element root = document.getRootElement();
	//
	// String procname = root.element("procname").getText();
	// Element nparms = root.element("parms");
	// HashMap<String, String> parms = new HashMap<String, String>();
	// for (Object o : nparms.elements()) {
	// Element nparm = (Element) o;
	// parms.put(nparm.getName(), nparm.getText());
	// }
	// String[] names = procname.split("\\.");
	// String classname = "";
	// for (int i = 0; i < names.length - 1; i++) {
	// classname = classname + "." + names[i];
	// }
	// if (!classname.isEmpty()) {
	// classname = classname.substring(1, classname.length());
	// }
	// String pname = names[names.length - 1];
	// Class<?> classType = Class.forName(classname);
	// Object rpcobject = classType.newInstance();
	// Method method = classType.getMethod(pname, new Class[] { HashMap.class });
	// Object rsobject = method.invoke(rpcobject, parms);
	//
	// parms.clear();
	// if (rsobject != null)
	// parms = (HashMap<String, String>) rsobject;
	//
	// document = DocumentHelper.createDocument();
	// root = document.addElement("parms");// 创建根节点
	// Iterator<?> iter = parms.entrySet().iterator();
	// while (iter.hasNext()) {
	// Map.Entry entry = (Map.Entry) iter.next();
	// root.addElement(entry.getKey().toString()).setText(entry.getValue().toString());
	// }
	// return CodeXML.AddCodeStr(ConstsSw.code_of_ExecRemotProcEx, document.asXML());
	// }
}
