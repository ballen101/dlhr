package com.corsair.server.base;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.dbpool.util.Logsw;

/**过时
 * @author Administrator
 *
 */
public class CodeXML {

//	public boolean DecCodeReuqest(Tcode_xml cx) {// 分解CODE
//		try {
//			Document document = DocumentHelper.parseText(cx.xml);
//			Element employees = document.getRootElement();
//			String str = employees.element(ConstsSw.xml_code).getText();
//			cx.code = Integer.valueOf(str);
//			cx.xml = employees.element(ConstsSw.xml_xmlstr).getText();
//			return true;
//		} catch (DocumentException e) {
//			Logsw.error(e);
//			return false;
//		}
//	}
//
//	public static Tcode_xml DecCodeRequest(String xmlstr) {
//		Tcode_xml rst = new Tcode_xml();
//		try {
//			Document document = DocumentHelper.parseText(xmlstr);
//			Element employees = document.getRootElement();
//			String str = employees.element(ConstsSw.xml_code).getText();
//			rst.code = Integer.valueOf(str);
//			rst.xml = employees.element(ConstsSw.xml_xmlstr).getText();
//			return rst;
//		} catch (DocumentException e) {
//			Logsw.error(e);
//			return null;
//		}
//	}
//
//	public static String AddCodeStr(int code, String str) {
//		Document document = DocumentHelper.createDocument();
//		Element root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//		Element codenode = root.addElement(ConstsSw.xml_code);
//		codenode.setText(String.valueOf(code));
//		Element strnode = root.addElement(ConstsSw.xml_xmlstr);
//		strnode.setText(String.valueOf(str));
//		return document.asXML();
//	}
//
//	public boolean AddCodeReuqest(Tcode_xml cx) {// 添加CODE
//		Document document = DocumentHelper.createDocument();
//		Element root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//		Element ageElm = root.addElement(ConstsSw.xml_code);
//		ageElm.setText(String.valueOf(cx.code));
//		ageElm = root.addElement(ConstsSw.xml_xmlstr);
//		ageElm.setText(String.valueOf(cx.xml));
//		cx.xml = document.asXML();
//		return true;
//	}
//
//	public static String CreateErrXml(int ErrCode, String ErrMsg) {
//		Document document = DocumentHelper.createDocument();
//		Element root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//		Element ageElm = root.addElement("ERRORCODE");
//		ageElm.setText(String.valueOf(ErrCode));
//		ageElm = root.addElement("ERRORMSG");
//		ageElm.setText(ErrMsg);
//
//		String xmlstr = document.asXML();
//
//		document = null;
//		document = DocumentHelper.createDocument();
//		root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//		ageElm = root.addElement(ConstsSw.xml_code);
//		ageElm.setText(String.valueOf(ConstsSw.code_of_exception));
//		ageElm = root.addElement(ConstsSw.xml_xmlstr);
//		ageElm.setText(xmlstr);
//
//		return document.asXML();
//	}
}
