package com.corsair.cjpa;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * CJPA XML交互实现
 * 
 * @author Administrator
 *
 */
public abstract class CJPAXML extends CJPABase {

	public CJPAXML() throws Exception {
	}

	/**
	 * 写入XML文件
	 * 
	 * @param fname
	 * @throws IOException
	 */
	public void toxmlfile(String fname) throws IOException {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("cjpa");// 创建根节点
		toxmlroot(root);

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		FileOutputStream fos = new FileOutputStream(fname);
		XMLWriter writer = new XMLWriter(fos, format);
		writer.write(document);
		writer.close();
	}

	/**
	 * 生成XML字符串
	 * 
	 * @return
	 */
	public String toxml() {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("cjpa");// 创建根节点
		toxmlroot(root);
		String xmlstr = document.asXML();
		// System.out.println(xmlstr);
		return xmlstr;
	}

	/**
	 * 将数据写入XML元素节点
	 * 
	 * @param emt
	 */
	public void toxmlSimple(Element emt) {
		for (CField cf : cFields) {
			Element e = emt.addElement(cf.getFieldname());
			if (!cf.isEmpty())
				e.setText(cf.getValue());
		}
	}

	/**
	 * 将数据写入XML元素节点（不包含行数据）
	 * 
	 * @param emt
	 */
	public void fromxmlSimple(Element emt) {
		List<Element> es = emt.elements();
		for (Element e : es) {
			String fdname = e.getName();
			CField fd = cfield(fdname);
			if (fd != null)
				fd.setValue(e.getTextTrim());
		}
	}

	/**
	 * 将数据写入XML元素节点
	 * 
	 * @param root
	 */
	public void toxmlroot(Element root) {
		root.addAttribute("tablename", tablename);
		root.addAttribute("jpaclass", getClass().toString());
		root.addAttribute("poolname", pool.pprm.name);
		root.addAttribute("where", SqlWhere);
		root.addAttribute("jpastat", getJpaStat().toString());
		root.addAttribute("tag", String.valueOf(tag));
		Element fields = root.addElement("fields");
		for (CField cf : cFields) {
			Element field = fields.addElement("field");
			field.addAttribute("fieldname", cf.getFieldname());
			field.addAttribute("size", String.valueOf(cf.getSize()));
			field.addAttribute("fieldtype", String.valueOf(cf.getFieldtype()));
			field.addAttribute("caption", cf.getCaption());
			field.addAttribute("dicid", String.valueOf(cf.getDicid()));
			field.addAttribute("codeid", String.valueOf(cf.getCodeid()));
			field.addAttribute("nullable", String.valueOf(cf.isNullable()));
			field.addAttribute("iskey", String.valueOf(cf.isIskey()));
			field.addAttribute("readonly", String.valueOf(cf.isReadonly()));
			field.addAttribute("visible", String.valueOf(cf.isVisible()));
			field.addAttribute("changed", String.valueOf(cf.isChanged()));
			field.addAttribute("value", cf.getValue());
			field.addAttribute("sorindex", String.valueOf(cf.getSorIndex()));
		}
		Element linedatas = root.addElement("linedatas");
		for (CJPALineData<CJPABase> line : cJPALileDatas) {
			Element linedata = linedatas.addElement("linedata");
			line.toxmlnode(linedata);
		}
		/*
		 * Element nodejpas = root.addElement("cjpas"); for (CJPABase cjpa : cJPAs) { Element cjparoot = nodejpas.addElement("cjpa"); ((CJPAXML) cjpa).toxmlroot(cjparoot); }
		 */
	}

	/**
	 * 从XML加载数据
	 * 
	 * @param xml
	 * @throws Exception
	 */
	public void fromxml(String xml) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		clear();
		fromxmlroot(root);
	}

	/**
	 * 从XML节点加载数据
	 * 
	 * @param root
	 * @throws Exception
	 */
	public void fromxmlroot(Element root) throws Exception {
		if (!root.getName().equalsIgnoreCase("CJPA"))
			return;
		// this.tablename = root.attributeValue("tablename");
		this.setJpaStat(CJPAStat.valueOf(root.attributeValue("jpastat").toUpperCase()));

		// System.out.println("this.cJpaStat"+this.cJpaStat);

		this.tag = Integer.valueOf(root.attributeValue("tag"));
		this.SqlWhere = root.attributeValue("where");

		// 载入普通字段值
		@SuppressWarnings("unchecked")
		List<Element> efields = root.element("fields").elements("field");
		for (Element efield : efields) {
			String fdname = efield.attributeValue("fieldname");
			CField field = cfield(fdname);
			if (field == null)
				throw new Exception("JPA载入XML数据没发现字段<" + fdname + ">");
			if (Integer.valueOf(efield.attributeValue("size")) != 0)
				field.setSize(Integer.valueOf(efield.attributeValue("size")));
			// field.setFieldtype(Integer.valueOf(efield.attributeValue("fieldtype")));
			// field.setCaption(efield.attributeValue("caption"));
			if (Integer.valueOf(efield.attributeValue("dicid")) != 0)
				field.setDicid(Integer.valueOf(efield.attributeValue("dicid")));
			if (Integer.valueOf(efield.attributeValue("codeid")) != 0)
				field.setCodeid(Integer.valueOf(efield.attributeValue("codeid")));
			if (!Boolean.valueOf(efield.attributeValue("nullable")))
				field.setNullable(false);// 从严原则
			if (Boolean.valueOf(efield.attributeValue("iskey")))
				field.setIskey(true);
			// field.setReadonly(Boolean.valueOf(efield.attributeValue("readonly")));
			// field.setVisible(Boolean.valueOf(efield.attributeValue("visible")));
			field.setValue(efield.attributeValue("value"));
			field.setChanged(Boolean.valueOf(efield.attributeValue("changed")));
		}

		// 载入明细数据
		@SuppressWarnings("unchecked")
		List<Element> elinedatas = root.element("linedatas").elements("linedata");
		for (Element elinedata : elinedatas) {
			String entyclaname = elinedata.attributeValue("entityclas");
			CJPALineData<CJPABase> clinedata = lineDataByEntyClasname(entyclaname);
			if (clinedata == null)
				continue;
			// throw new Exception("<" + this.getClass().getName() +
			// ">JPA载入明细数据，根据明细类名<" + entyclaname + ">没找到明细数据对象!");
			clinedata.fromxmlnode(elinedata);
		}
	}

	/**
	 * 创建EAI对象
	 * 
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public CJPABase newEaiObjcet(String className) throws Exception {
		Class<?> CJPAcd = Class.forName(className);
		if (!CJPABase.class.isAssignableFrom(CJPAcd)) {
			throw new Exception(className + "必须从 com.corsair.server.cjpa.CJPA继承");
		}
		Class<?> paramTypes[] = {};
		Constructor<?> cst = CJPAcd.getConstructor(paramTypes);
		Object o = cst.newInstance();
		return (CJPABase) (o);
	}
}
