package com.corsair.dbpool.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/**
 * JSON处理对象
 * 
 * @author Administrator
 *
 */
public class CJSON {
	public static String getErrJson(String msg) {
		ObjectMapper om = new ObjectMapper();
		ObjectNode ejson = om.createObjectNode();
		ejson.put("errmsg", msg);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			om.writeValue(out, ejson);
			return out.toString("utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "error";
	}

	public static JSONArray List2JSONObj(List<HashMap<String, String>> rows) throws Exception {
		return List2JSONObj(rows, true);
	}

	public static JSONArray List2JSONObj(List<HashMap<String, String>> rows, boolean tolwCase) throws Exception {
		JSONArray rst = new JSONArray();
		for (HashMap<String, String> row : rows) {
			JSONObject oj = HashMap2JsonObj(row, tolwCase);
			rst.add(oj);
		}
		return rst;
	}

	public static JSONObject HashMap2JsonObj(HashMap<String, String> row, boolean tolwCase) throws Exception {
		JSONObject rst = new JSONObject();
		Iterator<Entry<String, String>> iter = row.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = (tolwCase) ? entry.getKey().toLowerCase() : entry.getKey();
			rst.put(key, entry.getValue());
		}
		return rst;
	}

	public static String List2JSON(List<HashMap<String, String>> rows) throws Exception {
		return List2JSON(rows, true);
	}

	public static String List2JSON(List<HashMap<String, String>> rows, boolean tolwCase) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		for (HashMap<String, String> row : rows) {
			jg.writeRawValue(HashMap2Json(row, tolwCase));
		}
		jg.writeEndArray();
		jg.close();
		return baos.toString("utf-8");
	}

	public static String HashMap2Json(HashMap<String, String> row, boolean tolwCase) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		Iterator<Entry<String, String>> iter = row.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = (tolwCase) ? entry.getKey().toLowerCase() : entry.getKey();
			jg.writeStringField(key, entry.getValue());
		}
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	public static HashMap<String, String> Json2HashMap(String json) throws Exception {
		HashMap<String, String> rst = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		Iterator<Entry<String, JsonNode>> entrys = rootNode.getFields();
		while (entrys.hasNext()) {
			Entry<String, JsonNode> entry = entrys.next();
			if (entry.getValue().isObject() || entry.getValue().isPojo() || entry.getValue().isArray())
				rst.put(entry.getKey(), entry.getValue().toString());
			else
				rst.put(entry.getKey(), entry.getValue().asText());
		}
		return rst;
	}

	public static String DatasetRow2JSON(ResultSet rs, ResultSetMetaData rsmd, int columnCount) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		for (int i = 1; i <= columnCount; i++) {
			jg.writeStringField(rsmd.getColumnLabel(i), getvalue(rs, i, rsmd.getColumnType(i)));
		}
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	public static JSONObject DatasetRow2JSON_O(ResultSet rs, ResultSetMetaData rsmd, int columnCount) throws Exception {
		JSONObject rst = new JSONObject();
		for (int i = 1; i <= columnCount; i++) {
			rst.put(rsmd.getColumnLabel(i), getvalue(rs, i, rsmd.getColumnType(i)));
		}
		return rst;
	}

	public static String Dataset2JSON(ResultSet rs) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
		int columnCount = rsmd.getColumnCount();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		while (rs.next()) {
			jg.writeRawValue(DatasetRow2JSON(rs, rsmd, columnCount));
		}
		jg.writeEndArray();
		jg.close();
		return baos.toString("utf-8");
	}

	public static JSONArray Dataset2JSON_O(ResultSet rs) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
		int columnCount = rsmd.getColumnCount();
		JSONArray rst = new JSONArray();
		while (rs.next()) {
			rst.add(DatasetRow2JSON_O(rs, rsmd, columnCount));
		}
		return rst;
	}

	public static JSONArray getMetaData_O(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
		int columnCount = rsmd.getColumnCount();
		JSONArray rst = new JSONArray();
		for (int i = 1; i <= columnCount; i++) {
			JSONObject fo = new JSONObject();
			fo.put("fdname", rsmd.getColumnLabel(i));
			fo.put("datatype", rsmd.getColumnType(i));
			fo.put("precision", rsmd.getPrecision(i));
			fo.put("scale", rsmd.getScale(i));
			rst.add(fo);
		}
		return rst;
	}

	private static String getvalue(final ResultSet rs, int colNum, int type) throws Exception {
		switch (type) {
		case Types.ARRAY:
		case Types.BLOB:
		case Types.CLOB:
		case Types.DISTINCT:
		case Types.LONGVARBINARY:
		case Types.VARBINARY:
		case Types.BINARY:
		case Types.REF:
		case Types.STRUCT:
			return "undefined";
		case Types.DATE:
		case Types.TIMESTAMP: {

			Object value = null;
			try {
				value = rs.getObject(colNum);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println(e.getMessage());
			}
			if (rs.wasNull() || (value == null))
				return (null);
			else {
				String v = value.toString();
				if (v.substring(v.length() - 2, v.length()).endsWith(".0"))
					v = v.substring(0, v.length() - 2);
				return (v);
			}
		}

		default: {
			Object value = rs.getObject(colNum);
			if (rs.wasNull() || (value == null))
				return (null);
			else {
				// System.out.println(type);
				// System.out.println(value.toString());
				return (value.toString());
			}
		}
		}
	}

	public static List<JSONParm> getParms(String parms) throws Exception {
		List<JSONParm> jps = new ArrayList<JSONParm>();
		if ((parms == null) || (parms.isEmpty()))
			return jps;

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(parms);
		// System.out.println(rootNode.size());
		for (int i = 0; i < rootNode.size(); i++) {
			JsonNode node = rootNode.get(i);
			jps.add(new JSONParm(node.path("parmname").asText(), node.path("reloper").asText(), node.path("parmvalue").asText()));
		}
		return jps;
	}

	public static List<HashMap<String, String>> parArrJson(String parms) throws Exception {
		List<HashMap<String, String>> rst = new ArrayList<HashMap<String, String>>();
		if ((parms == null) || (parms.isEmpty()))
			return rst;

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(parms);
		for (int i = 0; i < rootNode.size(); i++) {
			JsonNode node = rootNode.get(i);
			HashMap<String, String> row = new HashMap<String, String>();
			Iterator<Entry<String, JsonNode>> entrys = node.getFields();
			while (entrys.hasNext()) {
				Entry<String, JsonNode> entry = entrys.next();
				String fdname = entry.getKey();
				JsonNode fd = entry.getValue();
				if (!(fd.isArray() || fd.isObject() || fd.isPojo())) {
					row.put(fdname, fd.asText());
				}
			}
			rst.add(row);
		}
		return rst;
	}

}
