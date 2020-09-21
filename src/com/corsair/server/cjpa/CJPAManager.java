package com.corsair.server.cjpa;

import java.lang.reflect.Method;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CodeXML;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.Tcode_xml;

/**
 * 不要了
 * 
 * @author Administrator
 *
 */
public class CJPAManager {

	// public String wfMain(String xml) throws Exception {
	// try {
	// Tcode_xml cx = CodeXML.DecCodeRequest(xml);
	// int code = cx.code;
	// Document document = DocumentHelper.parseText(cx.xml);
	// Element employees = document.getRootElement();
	// String jpaclass = employees.element("jpaclass").getText();
	// String xmldata = employees.element("xmldata").getText();
	// CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
	// String rst = "";
	// switch (code) {
	// case ConstsSw.code_of_JPAWFFindTemp: {
	// rst = jpa._findWfTem(xmldata);
	// break;
	// }
	// case ConstsSw.code_of_JPAWFCreateByTemp: {
	// rst = jpa._createWFByTemp(xmldata);
	// break;
	// }
	// case ConstsSw.code_of_JPAWFSubmit: {
	// rst = jpa._submitWF(xmldata);
	// break;
	// }
	// case ConstsSw.code_of_JPAWFReject: {
	// rst = jpa._rejectWF(xmldata);
	// break;
	// }
	// case ConstsSw.code_of_JPAWFBreak: {
	// rst = jpa._breakWF(xmldata);
	// break;
	// }
	// case ConstsSw.code_of_JPAWFTransfer: {
	// rst = jpa._transferWF(xmldata);
	// break;
	// }
	//
	// default: {
	// throw new Exception("流程主函数未定义编码");
	// }
	//
	// }
	// return CodeXML.AddCodeStr(ConstsSw.code_of_JPAWFMain, rst);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// throw new Exception(e.getLocalizedMessage());
	// }
	// }
	//
	// public String callAction(String xml) throws Exception {
	// Document document = DocumentHelper.parseText(xml);
	// Element employees = document.getRootElement();
	// String action = employees.element("action").getText();
	// int actiontype = Integer.valueOf(employees.element("actiontype").getText());
	// String entclass = employees.element("entclass").getText();
	// String jpadata = employees.element("jpadata").getText();
	// String xmlparm = employees.element("xmlparm").getText();
	// CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(entclass);
	// jpa.fromxml(jpadata);
	// if (ConstsSw.getAppParmBoolean("Debug_Mode")) {
	// // String fname = "c:\\" + entclass + ".xml";
	// // jpa.toxmlfile(fname);
	// // System.out.println("调试模式输出JPA XML对象：" + fname);
	// }
	// try {
	// String result = null;
	// if (actiontype == 0) {
	// result = callvoidAction(jpa, entclass, action, xmlparm);
	// } else { // if (actiontype == 1)
	// result = callStrAction(jpa, entclass, action, xmlparm);
	// }
	// return CodeXML.AddCodeStr(ConstsSw.code_of_Call_JPAAction, result);
	// } catch (Exception e) {
	// throw e;
	// }
	// }
	//
	// private String callvoidAction(CJPA jpa, String entclass, String action, String xmlparm) throws Exception {
	// Method method;
	// try {
	// Class<?> CJPAcd = Class.forName(entclass);
	// if ((xmlparm == null) || xmlparm.isEmpty())
	// method = CJPAcd.getMethod(action);
	// else
	// method = CJPAcd.getMethod(action, new Class[] { String.class });
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new Exception("类<" + entclass + ">没找到或其方法<" + action + ">没找到");
	// }
	// if ((xmlparm == null) || xmlparm.isEmpty())
	// method.invoke(jpa);
	// else
	// method.invoke(jpa, xmlparm);
	// return Boolean.valueOf(true).toString();
	// }
	//
	// private String callStrAction(CJPA jpa, String entclass, String action, String xmlparm) throws Exception {
	// Method method;
	// try {
	//
	// Class<?> CJPAcd = Class.forName(entclass);
	// if ((xmlparm == null) || xmlparm.isEmpty())
	// method = CJPAcd.getMethod(action);
	// else
	// method = CJPAcd.getMethod(action, new Class[] { String.class });
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new Exception("类<" + entclass + ">没找到或其方法<" + action + ">没找到");
	// }
	// Object rsobject;
	// if ((xmlparm == null) || xmlparm.isEmpty())
	// rsobject = method.invoke(jpa);
	// else
	// rsobject = method.invoke(jpa, xmlparm);
	// return rsobject.toString();
	// }
	//
	// public String fetchLineDatasByOwner(String xml) throws Exception {
	// Document document = DocumentHelper.parseText(xml);
	// Element employees = document.getRootElement();
	// String lineclass = employees.element("linejpaclass").getText();
	// String entclass = employees.element("entclass").getText();
	// String jpadata = employees.element("jpadata").getText();
	// CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(entclass);
	// jpa.fromxml(jpadata);
	// CJPALineData<CJPABase> cline = jpa.lineDataByEntyClasname(lineclass);
	// if (cline == null) {
	// throw new Exception("fetchLineDatasByOwner方法中，根据<" + lineclass + ">未能找到明细数据对象");
	// }
	// cline.setLazy(false);
	// cline.loadDataByOwner(true, true);
	//
	// document = DocumentHelper.createDocument();
	// Element root = document.addElement("linedata");// 创建根节点
	// cline.toxmlnode(root);
	// String result = document.asXML();
	//
	// return CodeXML.AddCodeStr(ConstsSw.code_of_JPALinesByOwner, result);
	// }
	//
	// public String fetchLineDatasBySQL(String xml) throws Exception {
	// Document document = DocumentHelper.parseText(xml);
	// Element employees = document.getRootElement();
	// String sqlstr = employees.element("sqlstr").getText();
	// String lineclass = employees.element("linejpaclass").getText();
	// String poolname = employees.element("poolname").getText();
	// CJPALineData<CJPABase> line = CjpaUtil.newJPALinedatas(lineclass);
	//
	// if ((poolname == null) || poolname.isEmpty()) {
	// line.setPool(DBPools.poolByName(poolname));
	// } else {
	// line.setPool(DBPools.defaultPool());
	// }
	//
	// if (line.getPool() == null)
	// throw new Exception("<" + poolname + ">的数据库连接池未找到!");
	// line.findDataBySQL(sqlstr, true, true);
	// document = DocumentHelper.createDocument();
	// Element root = document.addElement("linedata");// 创建根节点
	// line.toxmlnode(root);
	// String result = document.asXML();
	// // System.out.println(result);
	// return CodeXML.AddCodeStr(ConstsSw.code_of_JPALinesBySQL, result);
	// }
	//
	// public String initDPAPorpertyByJPA(String xmlstr) throws Exception {
	// Document document = DocumentHelper.parseText(xmlstr);
	// Element employees = document.getRootElement();
	// String entclass = employees.element("entclass").getText();
	// CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(entclass);
	// return jpa.toxml();
	// }

}
