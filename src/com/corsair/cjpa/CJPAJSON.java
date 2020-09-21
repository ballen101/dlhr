package com.corsair.cjpa;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JPA JSON交互实现
 * 
 * @author Administrator
 *
 */
public abstract class CJPAJSON extends CJPAXML {

	// private static ObjectMapper mapper = new ObjectMapper();

	public CJPAJSON() throws Exception {
	}

	/**
	 * 构建JSON对象
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject toJsonObj() throws Exception {
		return JSONObject.fromObject(tojson());
	}

	/**
	 * @param extFields扩展字段将被写入到JSON对象
	 * @return
	 * @throws Exception
	 */
	public JSONObject toJsonObj(HashMap<String, String> extFields) throws Exception {
		return JSONObject.fromObject(tojson(extFields));
	}

	/**
	 * 构建JSON字符串
	 * 
	 * @return
	 * @throws Exception
	 */
	public String tojson() throws Exception {
		return writejsonstr(null, null);
	}

	/*
	 * 构建JSON字符串
	 *
	 */
	public String toString() {
		try {
			return writejsonstr(null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param extFields扩展字段将被写入到JSON对象
	 * @return
	 * @throws Exception
	 */
	public String tojson(HashMap<String, String> extFields) throws Exception {
		return writejsonstr(extFields, null);
	}

	/**
	 * @param extFields
	 * 扩展字段将被写入到JSON对象
	 * @param disfields
	 * 略过的字段
	 * @return
	 * @throws Exception
	 */
	public String tojson(HashMap<String, String> extFields, String[] disfields) throws Exception {
		return writejsonstr(extFields, disfields);
	}

	/**
	 * @param extFields
	 * 扩展字段将被写入到JSON对象
	 * @return
	 * @throws Exception
	 */
	private String writejsonstr(HashMap<String, String> extFields, String[] disfields) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		jg.writeStringField("_jpastat", this.getJpaStat().name());
		for (CField cfield : cFields) {
			boolean fd = false;
			if ((disfields != null) && (disfields.length > 0)) {
				for (String fdname : disfields) {
					if (fdname.equalsIgnoreCase(cfield.getCfieldname())) {
						fd = true;
						break;
					}
				}
			}
			if (!fd) {
				String fdname = cfield.getCfieldname();
				if (!cfield.isEmpty())
					jg.writeStringField(fdname.toLowerCase(), cfield.getValue());
			}

		}

		if (extFields != null) {
			Iterator<String> iter = extFields.keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				Object val = extFields.get(key);
				if ((key != null) && (val != null))
					jg.writeStringField(key.toString(), val.toString());
			}
		}

		for (CJPALineData<CJPABase> jpalist : cJPALileDatas) {
			jg.writeFieldName(jpalist.getCfieldname().toLowerCase());
			jg.writeStartArray();
			for (CJPABase jpab : jpalist) {
				CJPAJSON jpa = (CJPAJSON) jpab;
				jg.writeRawValue(jpa.writejsonstr(null, null));
			}
			jg.writeEndArray();
		}
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/**
	 * 构建JSON字符串 不包含关联行
	 * 
	 * @param disfields
	 * @return
	 * @throws Exception
	 */
	public String toJsonSimpleDisFields(String[] disfields) throws Exception {
		return writejsonstrSimple(disfields, true);
	}

	/**
	 * 单表导出JSON
	 * 
	 * @param fields
	 * 导出的字段
	 * @return
	 * @throws Exception
	 */
	public String tojsonsimple(String[] fields) throws Exception {
		return writejsonstrSimple(fields, false);
	}

	/**
	 * @param fields
	 * :为空 导出所有字段，略过 isincld 参数
	 * @param isincld
	 * true :只导出指定指定 false：略过指定字段
	 * @return
	 * @throws Exception
	 */
	private String writejsonstrSimple(String[] fields, boolean isincld) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		for (CField cfield : cFields) {
			boolean fd = false;
			if (fields != null) {
				for (String fdname : fields) {
					if (fdname.equalsIgnoreCase(cfield.getCfieldname())) {
						fd = true;
						break;
					}
				}
			}
			// System.out.println(cfield.getCfieldname() + ":" + fd);
			if (fields != null) {
				jg.writeStringField(cfield.getCfieldname().toLowerCase(), cfield.getValue());
			} else {
				if (isincld) {
					if (fd)
						jg.writeStringField(cfield.getCfieldname().toLowerCase(), cfield.getValue());
				} else if (!fd)
					jg.writeStringField(cfield.getCfieldname().toLowerCase(), cfield.getValue());
			}

		}
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/*
	 * public String tosimplejson() throws Exception { return writejson(); }
	 * private String writejson() throws Exception { ByteArrayOutputStream baos
	 * = new ByteArrayOutputStream(); JsonFactory jf = new JsonFactory();
	 * JsonGenerator jg = jf.createJsonGenerator(baos); jg.writeStartObject();
	 * jg.writeObjectFieldStart(this.getClass().getSimpleName()); for (CField
	 * cfield : cFields) { jg.writeStringField(cfield.getFieldname(),
	 * cfield.getValue()); } for (CJPALineData<CJPABase> jpalist :
	 * cJPALileDatas) {
	 * jg.writeFieldName(jpalist.getEntityClas().getSimpleName() + "s");
	 * jg.writeStartArray(); for (CJPABase jpab : jpalist) { CJPAJSON jpa =
	 * (CJPAJSON) jpab; jg.writeRawValue(jpa.writejson()); } jg.writeEndArray();
	 * } jg.writeEndObject(); jg.close(); return baos.toString("utf-8"); }
	 */

	public CJPABase fromjson(String json) throws Exception {
		String[] fds = {};
		return fromjson(json, fds);
	}

	/**
	 * @param json
	 * @param disfields
	 * 需要略过的字段
	 * @return
	 * @throws Exception
	 */
	public CJPABase fromjsonAndDisFields(String json, String[] disfields) throws Exception {
		List<CField> tempFields = new ArrayList<CField>();
		tempFields.addAll(cFields);
		List<String> fieldlist = new ArrayList<String>();
		Iterator<CField> it = tempFields.iterator();
		while (it.hasNext()) {
			CField field = it.next();
			if (isInArray(disfields, field.getFieldname()))
				it.remove();
			else
				fieldlist.add(field.getFieldname());
		}
		String[] fields = new String[fieldlist.size()];
		fieldlist.toArray(fields);
		return fromjson(json, fields);
	}

	/**
	 * 从JSON字符串加载数据
	 * 
	 * @param json
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public CJPABase fromjson(String json, String[] fields) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(json);
		} catch (Exception e) {
			throw new Exception("json格式错误!");
		}
		// CJPAStat temst = cJpaStat;
		// if (temst != CJPAStat.RSINSERT) {// 如果是新插入数据，则无需从数据库总刷新JPA对象
		fromDB(rootNode);// 状态全被修改为 loadBD，删除状态失效
		// cJpaStat=temst; // 恢复主JPA状态
		// }
		if (getJpaStat() == CJPAStat.RSDEL) {// 如果是删除则无需从JSON载入数据，直接查询数据库
			// 是否需要将所有子JPA设置为待删除状态
			return (CJPABase) this;
		}
		return readjson(rootNode, fields);
	}

	private CJPABase fromDB(JsonNode rootNode) throws Exception {
		// JsonNode clsnode = rootNode.get(this.getClass().getSimpleName());
		String idfdname = getIDFieldName();
		if ((idfdname == null) || idfdname.isEmpty())
			throw new Exception("无法获取ID字段名称");
		JsonNode idnod = rootNode.get(idfdname);
		if (idnod == null) {
			return (CJPABase) this;
		}
		String idvalue = idnod.asText();
		if ((idvalue != null) && (!idvalue.isEmpty()))
			((CJPALoad) this).findByID(idvalue, true);
		return (CJPABase) this;
	}

	/**
	 * 从JSON对象加载数据
	 * 
	 * @param rootNode
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public CJPABase readjson(JsonNode rootNode, String[] fields) throws Exception {
		// JsonNode clsnode = rootNode.get(this.getClass().getSimpleName());
		boolean changed = false;
		String id = this.getid();// readjson前 已经从数据库刷新了数据，故可以直接根据id判断是否新增；
									// 本ID应该是从数据库读出来的
		if ((id == null) || (id.isEmpty())) {
			setJpaStat(CJPAStat.RSINSERT);
		} else if (getJpaStat() != CJPAStat.RSDEL) {
			setJpaStat(CJPAStat.RSUPDATED);
		}

		Iterator<Entry<String, JsonNode>> entrys = rootNode.getFields();
		while (entrys.hasNext()) {
			Entry<String, JsonNode> entry = entrys.next();
			String fdname = entry.getKey();
			JsonNode node = entry.getValue();
			if (node.isArray()) {
				String tbname = fdname.substring(0, fdname.length() - 1);
				// System.out.println("is array " + tbname);
				CJPALineData<CJPABase> ld = lineDataByTableName(tbname);
				if (ld == null)
					continue;
				ld.fromjsonnode(node);
			} else if ((node.isObject() || (node.isPojo()))) {
				// 不管
			} else {
				if ((fields.length > 0) && (!isInArray(fields, fdname)))
					continue;
				CField cfield = this.cfieldNoCase(fdname);
				if (cfield != null) {
					if (node.isNull())
						cfield.setValue(null);
					else
						cfield.setValue(node.asText());
					changed = changed || cfield.isChanged();
				}
				// System.out.println(fdname + ":" + node.asText());
			}
		}
		if ((getJpaStat() != CJPAStat.RSDEL) && (getJpaStat() != CJPAStat.RSINSERT) && changed)
			setJpaStat(CJPAStat.RSUPDATED);
		return (CJPABase) this;
	}

	private boolean isInArray(String[] arr, String value) {
		if ((value == null) || (value.isEmpty()))
			return false;
		for (String s : arr) {
			if (value.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回JSON格式的JPA描述
	 * 
	 * @return
	 * @throws Exception
	 */
	public String toJPAInfoJson() throws Exception {
		return writeInfojsonstr();
	}

	/**
	 * 返回JSON格式的JPA描述
	 * 
	 * @return
	 * @throws Exception
	 */
	public String writeInfojsonstr() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		// jg.writeObjectFieldStart(this.getClass().getSimpleName());
		// jg.writeStringField("cJpaStat", this.getJpaStat().name());
		jg.writeArrayFieldStart("fields");
		for (CField cfield : cFields) {
			jg.writeStartObject();
			jg.writeStringField("fieldname", cfield.getCfieldname().toLowerCase());
			jg.writeStringField("caption", cfield.getCaption());
			jg.writeNumberField("fieldtype", cfield.getFieldtype());
			jg.writeNumberField("size", cfield.getSize());
			jg.writeNumberField("dicid", cfield.getDicid());
			jg.writeNumberField("codeid", cfield.getCodeid());
			jg.writeBooleanField("nullable", cfield.isNullable());
			jg.writeBooleanField("readonly", cfield.isReadonly());
			jg.writeBooleanField("iskey", cfield.isIskey());
			// jg.writeStringField("value", cfield.getValue());
			jg.writeEndObject();
		}
		jg.writeEndArray();
		if (cJPALileDatas.size() > 0) {
			jg.writeArrayFieldStart("lines");
			for (CJPALineData<CJPABase> jpalist : cJPALileDatas) {
				CJPAJSON cjpaline = (CJPAJSON) jpalist.newJPAObjcet();
				jg.writeStartObject();
				jg.writeFieldName(jpalist.getCfieldname());
				jg.writeRawValue(cjpaline.writeInfojsonstr());
				jg.writeEndObject();
			}
			jg.writeEndArray();
		}
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/**
	 * 写入JSON文件
	 * 
	 * @param fname
	 * @throws IOException
	 */
	public void tojsonfile(String fname) throws IOException {
		FileWriter fw = new FileWriter(fname);
		try {
			fw.write(this.tojson());// 无法实现追加可写，存在覆盖现象
			fw.close();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("文件写入错误");
		}
	}

}
